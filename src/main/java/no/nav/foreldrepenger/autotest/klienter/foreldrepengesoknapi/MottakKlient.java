package no.nav.foreldrepenger.autotest.klienter.foreldrepengesoknapi;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.MultipartBodyPublisher;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.Innsending;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.SøknadDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.endringssøknad.EndringssøknadDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.engangsstønad.EngangsstønadDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.foreldrepenger.ForeldrepengesøknadDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.svangerskapspenger.SvangerskapspengesøknadDto;

import java.net.http.HttpRequest;
import java.time.Duration;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBruker;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

public class MottakKlient {

    private static final String API_SEND_PATH = "/rest/soknad";
    private static final String API_ENDRING_PATH = API_SEND_PATH + "/endre";

    public Kvittering sendSøknad(Fødselsnummer fnr, SøknadDto søknad) {
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FORELDREPENGESOKNAD_API_BASE)
                        .path(API_SEND_PATH)
                        .build())
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(toJson(søknad)));
        return send(request.build(), Kvittering.class);
    }

    public Kvittering sendSøknad(Fødselsnummer fnr, no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.SøknadDto søknad) {
        var path = switch (søknad) {
            case EngangsstønadDto ignored -> API_SEND_PATH + "/engangsstonad";
            case SvangerskapspengesøknadDto ignored -> API_SEND_PATH + "/svangerskapspenger";
            default -> throw new IllegalStateException("Unexpected value: " + søknad);
        };
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FORELDREPENGESOKNAD_API_BASE)
                        .path(path)
                        .build())
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(toJson(søknad)));
        return send(request.build(), Kvittering.class);
    }

    public Kvittering sendSøknad(Fødselsnummer fnr, EndringssøknadDto søknad) {
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FORELDREPENGESOKNAD_API_BASE)
                        .path(API_ENDRING_PATH)
                        .build())
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(toJson(søknad)));
        return send(request.build(), Kvittering.class);
    }

    public void mellomlagreVedlegg(Fødselsnummer fnr, Innsending søknad) {
        for (var vedlegg : søknad.vedlegg()) {
            if (vedlegg.innsendingsType().equals(InnsendingsType.LASTET_OPP)) {
                var multipartBody = new MultipartBodyPublisher();
                multipartBody.addFile("vedlegg", "dummy.pdf");
                var request = requestMedInnloggetBruker(fnr)
                        .uri(fromUri(BaseUriProvider.FORELDREPENGESOKNAD_API_BASE)
                                .path(String.format("/rest/storage/%s/vedlegg", tilFagsakYtelseType(søknad)))
                                .queryParam("uuid", vedlegg.uuid())
                                .build())
                        .setHeader("Content-Type", multipartBody.getContentType())
                        .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody.build()))
                        .build();
                send(request);
            }
        }
    }

    private static String tilFagsakYtelseType(Innsending innsending) {
        if (innsending instanceof no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.SøknadDto søknadDto) {
            if (søknadDto instanceof ForeldrepengesøknadDto) return "FORELDREPENGER";
            if (søknadDto instanceof SvangerskapspengesøknadDto) return "SVANGERSKAPSPENGER";
            if (søknadDto instanceof EngangsstønadDto) return "ENGANGSSTONAD";
        }
        if (innsending instanceof no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.endringssøknad.EndringssøknadDto endringssøknadDto) {
            return "FORELDREPENGER";
        }
        return "FORELDREPENGER";
    }

}
