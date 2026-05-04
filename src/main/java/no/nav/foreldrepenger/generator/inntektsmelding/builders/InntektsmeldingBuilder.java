package no.nav.foreldrepenger.generator.inntektsmelding.builders;

import no.nav.foreldrepenger.autotest.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InntektsmeldingBuilder {

    private String arbeidstakerFnr;
    private Inntektsmelding.YtelseType ytelseType;
    private Inntektsmelding.Arbeidsgiver arbeidsgiver;
    // Arbeidsforhold
    private BigDecimal beregnetInntekt;
    private LocalDate førsteFraværsdag; // erstatter forendrepengerStartdato + førsteFraværsdag)
    private List<Inntektsmelding.Utsettelse> utsettelserList;
    private String arbeidsforholdId; // skal fases ut på sikt med ny løsning
    // Refusjon
    private BigDecimal refusjonBeløpPrMnd;
    private List<Inntektsmelding.EndringRefusjon> refusjonEndringList;
    private LocalDate refusjonOpphørdato;
    private List<Inntektsmelding.OpphørAvNaturalytelse> opphørAvNaturalytelseList;
    // AvsenderSystem
    private String avsendersystem;
    private String systemVersjon;

    private InntektsmeldingBuilder() {
        opphørAvNaturalytelseList = new ArrayList<>();
        refusjonEndringList = new ArrayList<>();
        utsettelserList = new ArrayList<>();
    }

    public static InntektsmeldingBuilder builder() {
        return new InntektsmeldingBuilder();
    }

    public InntektsmeldingBuilder medArbeidsgiver(String virksomhetsnummer, String kontaktinformasjonTelefon) {
        this.arbeidsgiver = new Inntektsmelding.Arbeidsgiver(virksomhetsnummer, kontaktinformasjonTelefon, "Corpolarsen", false);
        return this;
    }

    public InntektsmeldingBuilder medArbeidsgiverPrivat(String arbeidsgiverFnr, String kontaktinformasjonTelefon) {
        this.arbeidsgiver = new Inntektsmelding.Arbeidsgiver(arbeidsgiverFnr, kontaktinformasjonTelefon, "Privatelarsen", true);
        return this;
    }

    public InntektsmeldingBuilder medOpphørAvNaturalytelseListe(BigDecimal belopPrMnd,
                                                                LocalDate fom,
                                                                Inntektsmelding.NaturalytelseType naturalytelseType) {
        this.opphørAvNaturalytelseList.add(new Inntektsmelding.OpphørAvNaturalytelse(naturalytelseType, belopPrMnd, fom, null));
        return this;
    }

    // Om tom er satt så vil bortfallt naturalytelse tilkomme fra dagen etter tom.
    public InntektsmeldingBuilder medOpphørAvNaturalytelseListe(BigDecimal beløpPrMnd,
                                                                LocalDate fom,
                                                                LocalDate tom,
                                                                Inntektsmelding.NaturalytelseType naturalytelseType) {
        this.opphørAvNaturalytelseList.add(new Inntektsmelding.OpphørAvNaturalytelse(naturalytelseType, beløpPrMnd, fom, tom));
        return this;
    }

    public InntektsmeldingBuilder medArbeidstakerFnr(String arbeidstakerFnr) {
        this.arbeidstakerFnr = arbeidstakerFnr;
        return this;
    }

    public InntektsmeldingBuilder medYtelse(Inntektsmelding.YtelseType ytelseType) {
        this.ytelseType = ytelseType;
        return this;
    }

    public InntektsmeldingBuilder medAvsendersystem(String avsenderSystem, String systemVersjon) {
        this.avsendersystem = avsenderSystem;
        this.systemVersjon = systemVersjon;
        return this;
    }

    public InntektsmeldingBuilder medFørsteFraværsdag(LocalDate førsteFraværsdag) {
        this.førsteFraværsdag = førsteFraværsdag;
        return this;
    }

    public InntektsmeldingBuilder medRefusjonBeløpPerMnd(int refusjonBeløpPerMnd) {
        return medRefusjonBeløpPerMnd(BigDecimal.valueOf(refusjonBeløpPerMnd));
    }

    public InntektsmeldingBuilder medRefusjonBeløpPerMnd(Prosent prosentAvBeregnetInntekt) {
        return medRefusjonBeløpPerMnd(this.beregnetInntekt.multiply(BigDecimal.valueOf(prosentAvBeregnetInntekt.prosent() / 100.0)));
    }

    public InntektsmeldingBuilder medRefusjonBeløpPerMnd(BigDecimal refusjonsBelopPerMnd) {
        this.refusjonBeløpPrMnd = refusjonsBelopPerMnd;
        return this;
    }

    public InntektsmeldingBuilder medEndringIRefusjonslist(Map<LocalDate, BigDecimal> endringRefusjonMap) {
        this.refusjonEndringList.addAll(endringRefusjonMap.entrySet()
                .stream()
                .map(entry -> new Inntektsmelding.EndringRefusjon(entry.getKey(), entry.getValue()))
                .toList());
        return this;
    }

    public InntektsmeldingBuilder medRefusjonsOpphordato(LocalDate refusjonOpphørdato) {
        this.refusjonOpphørdato = refusjonOpphørdato;
        return this;
    }

    public InntektsmeldingBuilder medBeregnetInntekt(int beregnetInntektBeløp) {
        return medBeregnetInntekt(BigDecimal.valueOf(beregnetInntektBeløp));
    }

    public InntektsmeldingBuilder medBeregnetInntekt(BigDecimal beregnetInntektBeløp) {
        this.beregnetInntekt = beregnetInntektBeløp;
        return this;
    }

    public InntektsmeldingBuilder medBeregnetInntekt(Prosent prosentIForholdTilRegistrertInntekt) {
        this.beregnetInntekt = this.beregnetInntekt.multiply(
                BigDecimal.valueOf(prosentIForholdTilRegistrertInntekt.prosent() / 100.0));
        return this;
    }

    public InntektsmeldingBuilder medUtsettelse(Inntektsmelding.UtsettelseÅrsak utsettelseÅrsak,
                                                LocalDate periodeFom,
                                                LocalDate periodeTom) {
        this.utsettelserList.add(new Inntektsmelding.Utsettelse(periodeFom, periodeTom, utsettelseÅrsak));
        return this;
    }

    public InntektsmeldingBuilder medArbeidsforholdId(String arbeidsforholdId) {
        this.arbeidsforholdId = arbeidsforholdId;
        return this;
    }

    public Inntektsmelding build() {
        Objects.requireNonNull(this.ytelseType, "YtelseType kan ikke være null.");
        Objects.requireNonNull(this.arbeidstakerFnr, "ArbeidstakerFnr kan ikke være null.");
        Objects.requireNonNull(this.arbeidsgiver, "Arbeidsgiver kan ikke være null.");
        Objects.requireNonNull(this.beregnetInntekt, "Beregnet inntekt kan ikke være null.");
        if (Inntektsmelding.YtelseType.FORELDREPENGER.equals(this.ytelseType)) {
            // Foreløppig trenger ikke å være satt for gamle Altinn SVP IM.
            Objects.requireNonNull(this.førsteFraværsdag, "FørsteFraværsdag kan ikke være null.");
        }

        var arbeidsforhold = new Inntektsmelding.Arbeidsforhold(this.beregnetInntekt, this.førsteFraværsdag, this.utsettelserList, this.arbeidsforholdId);

        var avsenderSystem = new Inntektsmelding.AvsenderSysten(this.avsendersystem, this.systemVersjon);

        Inntektsmelding.Refusjon refusjon = null;
        if (this.refusjonBeløpPrMnd != null
                || CollectionUtils.isNotEmpty(this.refusjonEndringList)
                || this.refusjonOpphørdato != null) {
            Objects.requireNonNull(refusjonBeløpPrMnd, "Refusjon beloep pr mnd kan ikke være null.");
            refusjon = new Inntektsmelding.Refusjon(this.refusjonBeløpPrMnd, this.refusjonEndringList, this.refusjonOpphørdato);
        }

        return new Inntektsmelding(this.ytelseType, this.arbeidstakerFnr, this.arbeidsgiver, arbeidsforhold, refusjon,
                this.opphørAvNaturalytelseList, avsenderSystem);
    }
}
