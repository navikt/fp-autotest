package no.nav.foreldrepenger.autotest.klienter.inntektsmelding;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBruker;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class InntektsmeldingKlient {

    private static final String FORESPØRSEL_UUID = "/forvaltning/api/foresporsel/list";
    private static final String FORESPØRSEL_OPPRETT = "/api/imdialog/send-inntektsmelding";

    private static final String API_NAME = "fpinntektsmelding";

    public InntektsmeldingKlient() {
    }

    public ListForespørslerResponse hentInntektsmeldingForespørslerFor(Saksnummer saksnummer) {
        var request = requestMedInnloggetSaksbehandler(SaksbehandlerRolle.SAKSBEHANDLER, API_NAME)
                .uri(fromUri(BaseUriProvider.FPINNTEKTSMELDING_BASE)
                        .path(FORESPØRSEL_UUID + "/" + saksnummer.value())
                        .build()).GET();

        return send(request.build(), ListForespørslerResponse.class);
    }

    public void sendInntektsmelding(InntektsmeldingForespørselDto opprettInntektsmeldingDTO, BigDecimal inntekt, Fødselsnummer fnr) {
        var inntektsmeldingRequest = new SendInntektsmeldingRequestDto(
                opprettInntektsmeldingDTO.uuid(),
                opprettInntektsmeldingDTO.aktørid(),
                opprettInntektsmeldingDTO.ytelsetype(),
                opprettInntektsmeldingDTO.arbeidsgiverident(),
                new KontaktpersonDto("Inntektsmelding Innsender", "55555555"),
                opprettInntektsmeldingDTO.startDato(),
                inntekt,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());

        var request = requestMedInnloggetBruker(fnr).uri(
                        fromUri(BaseUriProvider.FPINNTEKTSMELDING_BASE).path(FORESPØRSEL_OPPRETT).build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(inntektsmeldingRequest)));
        send(request.build());
    }

    public record ListForespørslerResponse(List<InntektsmeldingForespørselDto> inntektmeldingForespørsler) {
    }

    public record InntektsmeldingForespørselDto(UUID uuid,
                                                LocalDate skjæringstidspunkt,
                                                String arbeidsgiverident,
                                                String aktørid,
                                                String ytelsetype,
                                                LocalDate startDato) {
    }

    public record SendInntektsmeldingRequestDto(@NotNull @Valid UUID foresporselUuid,
                                                @NotNull @Valid String aktorId,
                                                @NotNull @Valid String ytelse,
                                                @NotNull @Valid String arbeidsgiverIdent,
                                                @NotNull @Valid KontaktpersonDto kontaktperson,
                                                @NotNull LocalDate startdato,
                                                @NotNull @Min(0) @Max(Integer.MAX_VALUE) @Digits(integer = 20, fraction = 2) BigDecimal inntekt,
                                                @NotNull List<@Valid RefusjonDto> refusjon,
                                                @NotNull List<@Valid BortfaltNaturalytelseDto> bortfaltNaturalytelsePerioder,
                                                @NotNull List<@Valid EndringsårsakerDto> endringAvInntektÅrsaker) {
    }

    public record RefusjonDto(@NotNull LocalDate fom,
                              @NotNull @Min(0) @Max(Integer.MAX_VALUE) @Digits(integer = 20, fraction = 2) BigDecimal beløp) {
    }


    public record BortfaltNaturalytelseDto(@NotNull LocalDate fom, LocalDate tom, @NotNull Naturalytelsetype naturalytelsetype,
                                           @NotNull @Min(0) @Max(Integer.MAX_VALUE) @Digits(integer = 20, fraction = 2) BigDecimal beløp) {
    }

    public record EndringsårsakerDto(@NotNull @Valid Endringsårsak årsak, LocalDate fom, LocalDate tom, LocalDate bleKjentFom) {
    }

    public record KontaktpersonDto(@Size(max = 100) @NotNull String navn, @NotNull @Size(max = 100) String telefonnummer) {
    }
}
