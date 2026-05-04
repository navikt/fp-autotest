package no.nav.foreldrepenger.generator.inntektsmelding.builders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public record Inntektsmelding(@NotNull YtelseType ytelseType,
                              @NotNull String arbeidstakerFnr,
                              @NotNull Arbeidsgiver arbeidsgiver,
                              @NotNull Arbeidsforhold arbeidsforhold,
                              Refusjon refusjon,
                              List<OpphørAvNaturalytelse> opphørAvNaturalytelseList,
                              AvsenderSysten avsender) {

    public enum YtelseType {
        FORELDREPENGER,
        SVANGERSKAPSPENGER
    }

    public record Arbeidsgiver(@NotNull String arbeidsgiverIdentifikator,
                               String kontaktnummer,
                               String navn,
                               boolean erPrivatArbeidsgiver) {
    }

    public record Arbeidsforhold(@NotNull BigDecimal beregnetInntekt,
                                 @NotNull LocalDate førsteFraværsdag,
                                 // erstatter forendrepengerStartdato + førsteFraværsdag)
                                 List<Utsettelse> utsettelserList,
                                 String arbeidsforholdId) {
    }

    public record Utsettelse(LocalDate fom,
                             LocalDate tom,
                             UtsettelseÅrsak årsak) {
    }

    public enum UtsettelseÅrsak {
        LOVBESTEMT_FERIE,
        ARBEID
    }

    public record Refusjon(BigDecimal refusjonBeløpPrMnd,
                           List<EndringRefusjon> refusjonEndringList,
                           LocalDate refusjonOpphørsdato) {

    }


    public record EndringRefusjon(LocalDate fom,
                                  BigDecimal beloepPrMnd) {
    }

    public record OpphørAvNaturalytelse(@NotNull NaturalytelseType natyralYtelseType,
                                        @NotNull BigDecimal beloepPrMnd,
                                        @NotNull LocalDate fom,
                                        LocalDate tom) {
    }

    public enum NaturalytelseType {
        ELEKTRISK_KOMMUNIKASJON,
        AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS,
        LOSJI,
        KOST_DOEGN,
        BESØKSREISER_HJEMMET_ANNET,
        KOSTBESPARELSE_I_HJEMMET,
        RENTEFORDEL_LÅN,
        BIL,
        KOST_DAGER,
        BOLIG,
        SKATTEPLIKTIG_DEL_FORSIKRINGER,
        FRI_TRANSPORT,
        OPSJONER,
        TILSKUDD_BARNEHAGEPLASS,
        ANNET,
        BEDRIFTSBARNEHAGEPLASS,
        YRKEBIL_TJENESTLIGBEHOV_KILOMETER,
        YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS,
        INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING,
    }

    public record AvsenderSysten(String system,
                                 String versjon) {
    }

}
