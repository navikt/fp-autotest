package no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendInntektsmeldingDto(@NotNull @Valid UUID foresporselUuid,
                                     @NotNull @Valid AktørIdDto aktorId,
                                     @NotNull @Valid YtelseType ytelse,
                                     @NotNull @Valid ArbeidsgiverDto arbeidsgiverIdent,
                                     @NotNull @Valid KontaktpersonRequestDto kontaktperson,
                                     @NotNull LocalDate startdato,
                                     @NotNull @Min(0) @Max(Integer.MAX_VALUE) @Digits(integer = 20, fraction = 2) BigDecimal inntekt,
                                     @NotNull List<@Valid Refusjon> refusjon,
                                     @NotNull List<@Valid BortfaltNaturalytelseRequestDto> bortfaltNaturalytelsePerioder,
                                     @NotNull List<@Valid EndringsårsakerRequestDto> endringAvInntektÅrsaker) {

    public record Refusjon(@NotNull LocalDate fom,
                           @NotNull @Min(0) @Max(Integer.MAX_VALUE) @Digits(integer = 20, fraction = 2) BigDecimal beløp) {
    }


    public record BortfaltNaturalytelseRequestDto(@NotNull LocalDate fom,
                                                  LocalDate tom,
                                                  @NotNull NaturalytelsetypeDto naturalytelsetype,
                                                  @NotNull @Min(0) @Max(Integer.MAX_VALUE) @Digits(integer = 20, fraction = 2) BigDecimal beløp) {
    }

    public record EndringsårsakerRequestDto(@NotNull @Valid EndringsårsakDto årsak,
                                            LocalDate fom,
                                            LocalDate tom,
                                            LocalDate bleKjentFom) {
    }

    public record KontaktpersonRequestDto(@Size(max = 100) @NotNull String navn,
                                          @NotNull @Size(max = 100) String telefonnummer) {
    }

}

