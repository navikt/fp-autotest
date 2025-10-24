package no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBruker;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
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
import no.nav.foreldrepenger.kontrakter.fpsoknad.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Saksnummer;

public class InntektsmeldingKlient {

    private static final String FORESPØRSEL_UUID = "/forvaltning/api/foresporsel/list";
    private static final String FORESPØRSEL_OPPRETT = "/api/imdialog/send-inntektsmelding";

    private static final String API_NAME = "fpinntektsmelding";

    private InntektsmeldingKlient() {
        // skjul ctor
    }

    public static ListForespørslerResponse hentInntektsmeldingForespørslerFor(Saksnummer saksnummer) {
        var request = requestMedInnloggetSaksbehandler(SaksbehandlerRolle.SAKSBEHANDLER, API_NAME).uri(
                fromUri(BaseUriProvider.FPINNTEKTSMELDING_BASE).path(FORESPØRSEL_UUID + "/" + saksnummer.value()).build()).GET();

        return send(request.build(), ListForespørslerResponse.class);
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
