package no.nav.foreldrepenger.autotest.klienter.fpsoknad;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBruker;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.MultipartBodyPublisher;
import no.nav.foreldrepenger.kontrakter.fpsoknad.EndringssøknadForeldrepengerDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.EngangsstønadDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.ForeldrepengesøknadDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.SvangerskapspengesøknadDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.SøknadDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.vedlegg.VedleggDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.vedlegg.InnsendingType;
import no.nav.foreldrepenger.kontrakter.fpsoknad.ettersendelse.EttersendelseDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.ettersendelse.YtelseType;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Saksnummer;

public class FpsoknadKlient {

    private static final String API_SEND_PATH = "/api/soknad";
    private static final String API_ENDRING_PATH = API_SEND_PATH + "/foreldrepenger/endre";
    private static final String API_ETTERSEND_PATH = API_SEND_PATH + "/ettersend";

    public Kvittering sendSøknad(Fødselsnummer fnr, SøknadDto søknad) {
        var path = switch (søknad) {
            case EngangsstønadDto ignored -> API_SEND_PATH + "/engangsstonad";
            case SvangerskapspengesøknadDto ignored -> API_SEND_PATH + "/svangerskapspenger";
            case ForeldrepengesøknadDto ignored -> API_SEND_PATH + "/foreldrepenger";
            default -> throw new IllegalStateException("Unexpected value: " + søknad);
        };
        var json = toJson(søknad);
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FPSOKNAD)
                        .path(path)
                        .build())
                .timeout(Duration.ofSeconds(20))
                .POST(HttpRequest.BodyPublishers.ofString(json));
        return send(request.build(), Kvittering.class);
    }

    public Kvittering sendSøknad(Fødselsnummer fnr, EndringssøknadForeldrepengerDto søknad) {
        var json = toJson(søknad);
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FPSOKNAD)
                        .path(API_ENDRING_PATH)
                        .build())
                .timeout(Duration.ofSeconds(20))
                .POST(HttpRequest.BodyPublishers.ofString(json));
        return send(request.build(), Kvittering.class);
    }

    public void mellomlagreVedlegg(Fødselsnummer fnr, SøknadDto søknad) {
        mellomlagreVedlegg(fnr, tilYtelseType(søknad), søknad.vedlegg());
    }

    public void mellomlagreVedlegg(Fødselsnummer fnr, YtelseType ytelseType, List<VedleggDto> vedleggene) {
        for (var vedlegg : vedleggene) {
            if (vedlegg.innsendingsType().equals(InnsendingType.LASTET_OPP)) {
                var multipartBody = new MultipartBodyPublisher();
                multipartBody.addFile("vedlegg", "dummy.pdf");
                var request = requestMedInnloggetBruker(fnr)
                        .uri(fromUri(BaseUriProvider.FPSOKNAD)
                                .path(String.format("/api/storage/%s/vedlegg", ytelseType.name()))
                                .queryParam("uuid", vedlegg.uuid())
                                .build())
                        .setHeader("Content-Type", multipartBody.getContentType())
                        .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody.build()))
                        .build();
                send(request);
            }
        }
    }

    private static YtelseType tilYtelseType(SøknadDto innsending) {
        if (innsending instanceof no.nav.foreldrepenger.kontrakter.fpsoknad.SøknadDto søknadDto) {
            if (søknadDto instanceof ForeldrepengesøknadDto) return YtelseType.FORELDREPENGER;
            if (søknadDto instanceof SvangerskapspengesøknadDto) return YtelseType.SVANGERSKAPSPENGER;
            if (søknadDto instanceof EngangsstønadDto) return YtelseType.ENGANGSSTØNAD;
        }
        if (innsending instanceof EndringssøknadForeldrepengerDto) {
            return YtelseType.FORELDREPENGER;
        }
        return YtelseType.FORELDREPENGER;
    }

    public void ettersendVedlegg(Fødselsnummer fnr, Saksnummer saksnummer, List<VedleggDto> vedlegg) {
        var ettersendelseDto = new EttersendelseDto(
                LocalDateTime.now(),
                saksnummer,
                fnr,
                YtelseType.FORELDREPENGER,
                null,
                vedlegg
        );

        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FPSOKNAD)
                        .path(API_ETTERSEND_PATH)
                        .build())
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(toJson(ettersendelseDto)));
        send(request.build());
    }
}
