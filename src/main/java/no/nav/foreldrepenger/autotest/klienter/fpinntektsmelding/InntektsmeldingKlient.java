package no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBruker;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedMaskinportenToken;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.AktørIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.ArbeidsgiverDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.SendInntektsmeldingDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.YtelseType;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;

public class InntektsmeldingKlient {

    private static final String FORESPØRSEL_UUID = "/forvaltning/api/foresporsel/list";
    private static final String FORESPØRSEL_OPPRETT = "/api/imdialog/send-inntektsmelding";
    private static final String API_HENT_FORESPØRSLER = "/v1/forespoersel";

    private static final String FPINNTEKTSMELDING_APP = "fpinntektsmelding";
    private static final String FPINNTEKTSMELDING_API_APP = "fpinntektsmeldingapi";

    private InntektsmeldingKlient() {
        // skjul ctor
    }

    public static ListForespørslerResponse hentInntektsmeldingForespørslerFor(Saksnummer saksnummer) {
        var request = requestMedInnloggetSaksbehandler(SaksbehandlerRolle.DRIFTER, FPINNTEKTSMELDING_APP).uri(
                fromUri(BaseUriProvider.FPINNTEKTSMELDING_BASE).path(FORESPØRSEL_UUID + "/" + saksnummer.value()).build()).GET();
        var respons = send(request.build(), ListForespørslerResponse.class);

        var midlertidig = hentInntektsmeldingForespørslerForMaskin(respons.inntektmeldingForespørsler.getFirst().uuid, respons.inntektmeldingForespørsler.getFirst().arbeidsgiverident.ident());
        return respons;
    }

    public static Object hentInntektsmeldingForespørslerForMaskin(UUID uuid,
                                                                 String orgnr) {
        var request = requestMedMaskinportenToken("nav:inntektsmelding/foreldrepenger", orgnr).uri(
                fromUri(BaseUriProvider.FPINNTEKTSMELDINGAPI_BASE).path(API_HENT_FORESPØRSLER + "/" + uuid).build()).GET();

        return send(request.build(), Object.class);
    }

    public static void sendInntektsmelding(SendInntektsmeldingDto sendInntektsmeldingDto, Fødselsnummer fnr) {
        var request = requestMedInnloggetBruker(fnr).uri(
                        fromUri(BaseUriProvider.FPINNTEKTSMELDING_BASE).path(FORESPØRSEL_OPPRETT).build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(sendInntektsmeldingDto)));
        send(request.build());
    }

    public record ListForespørslerResponse(List<InntektsmeldingForespørselDto> inntektmeldingForespørsler) {
    }

    public record InntektsmeldingForespørselDto(UUID uuid,
                                                LocalDate skjæringstidspunkt,
                                                ArbeidsgiverDto arbeidsgiverident,
                                                AktørIdDto aktørid,
                                                YtelseType ytelsetype,
                                                LocalDate startDato) {
    }
}
