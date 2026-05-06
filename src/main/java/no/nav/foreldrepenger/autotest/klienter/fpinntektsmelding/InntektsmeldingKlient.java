package no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBruker;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedMaskinportenToken;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.api.InntektsmeldingRequest;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.arbeidsgiverportal.SendInntektsmeldingDto;
import tools.jackson.core.type.TypeReference;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.arbeidsgiverportal.AktørIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.arbeidsgiverportal.ArbeidsgiverDto;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.arbeidsgiverportal.YtelseType;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;

public class InntektsmeldingKlient {

    private static final String FORESPØRSEL_UUID = "/forvaltning/api/foresporsel/list";
    private static final String FORESPØRSEL_OPPRETT = "/api/imdialog/send-inntektsmelding";
    private static final String API_HENT_FORESPØRSLER = "/v1/forespoersel/forespoersler";
    private static final String API_SEND_INNTEKTSMELDING = "/v1/inntektsmelding/send-inn";

    private static final String FPINNTEKTSMELDING_APP = "fpinntektsmelding";

    private InntektsmeldingKlient() {
        // skjul ctor
    }

    public static ListForespørslerResponse hentInntektsmeldingForespørslerFor(Saksnummer saksnummer) {
        var request = requestMedInnloggetSaksbehandler(SaksbehandlerRolle.DRIFTER, FPINNTEKTSMELDING_APP).uri(
                fromUri(BaseUriProvider.FPINNTEKTSMELDING_BASE).path(FORESPØRSEL_UUID + "/" + saksnummer.value()).build()).GET();
        return send(request.build(), ListForespørslerResponse.class);
    }

    public static List<ForespørselDto> søkEtterForespørslerFraApi(String orgnr, String fnr) {
        var filterRequest = new ForespørselFilter(orgnr, fnr);
        var request = requestMedMaskinportenToken(orgnr).uri(
                fromUri(BaseUriProvider.FPINNTEKTSMELDINGAPI_BASE).path(API_HENT_FORESPØRSLER).build()).POST(HttpRequest.BodyPublishers.ofString(toJson(filterRequest)));

        return send(request.build(), new TypeReference<List<ForespørselDto>>() {});
    }

    public static void sendInntektsmelding(SendInntektsmeldingDto sendInntektsmeldingDto, Fødselsnummer fnr) {
        var request = requestMedInnloggetBruker(fnr).uri(
                        fromUri(BaseUriProvider.FPINNTEKTSMELDING_BASE).path(FORESPØRSEL_OPPRETT).build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(sendInntektsmeldingDto)));
        send(request.build());
    }

    public static void sendInntektsmeldingTilApi(InntektsmeldingRequest sendInntektsmeldingDto, String orgnr, Fødselsnummer fnr) {
        var request = requestMedMaskinportenToken(orgnr).uri(
                fromUri(BaseUriProvider.FPINNTEKTSMELDINGAPI_BASE).path(API_SEND_INNTEKTSMELDING).build())
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

    public record ForespørselDto(UUID forespoerselId, String orgnr, String fnr, LocalDate startdato,
                                 LocalDate inntektsdato, StatusDto status, YtelseTypeDto ytelseType, LocalDateTime opprettetTid) {
        public enum StatusDto {
            AKTIV,
            BESVART,
            FORKASTET
        }
        public enum YtelseTypeDto {
            FORELDREPENGER,
            SVANGERSKAPSPENGER
        }
    }
    public record ForespørselFilter(@NotNull @Pattern(regexp = "^\\d{9}$") String orgnr,
                                    @Pattern(regexp = "^\\d{11}$") String fnr) {}

}
