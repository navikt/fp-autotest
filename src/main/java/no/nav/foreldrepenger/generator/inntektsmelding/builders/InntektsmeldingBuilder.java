package no.nav.foreldrepenger.generator.inntektsmelding.builders;

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
    private LocalDate foersteFravaarsdag; // erstatter forendrepengerStartdato + foersteFravaarsdag)
    private List<Inntektsmelding.Utsettelse> utsettelser;
    private String arbeidsforholdId; // skal fases ut på sikt med ny løsning
    // Refusjon
    private BigDecimal refusjonBeloepPrMnd;
    private List<Inntektsmelding.EndringRefusjon> refusjonEndring;
    private LocalDate refusjonOpphoersdato;
    private List<Inntektsmelding.OpphoerAvNaturalytelse> opphoerAvNaturalytelse;
    // AvsenderSystem
    private String avsendersystem;
    private String systemVersjon;

    private InntektsmeldingBuilder() {
        opphoerAvNaturalytelse = new ArrayList<>();
        refusjonEndring = new ArrayList<>();
        utsettelser = new ArrayList<>();
    }

    public static InntektsmeldingBuilder builder() {
        return new InntektsmeldingBuilder();
    }

    public InntektsmeldingBuilder medArbeidsgiver(String virksomhetsnummer, String kontaktinformasjonTLF) {
        this.arbeidsgiver = new Inntektsmelding.Arbeidsgiver(virksomhetsnummer, kontaktinformasjonTLF, "Corpolarsen", false);
        return this;
    }

    public InntektsmeldingBuilder medArbeidsgiverPrivat(String arbeidsgiverFnr, String kontaktinformasjonTLF) {
        this.arbeidsgiver = new Inntektsmelding.Arbeidsgiver(arbeidsgiverFnr, kontaktinformasjonTLF, "Privatelarsen", true);
        return this;
    }

    public InntektsmeldingBuilder medOpphoerAvNaturalytelseListe(BigDecimal belopPrMnd,
                                                                 LocalDate fom,
                                                                 Inntektsmelding.NaturalytelseType naturalytelseType) {
        this.opphoerAvNaturalytelse.add(new Inntektsmelding.OpphoerAvNaturalytelse(naturalytelseType, belopPrMnd, fom, null));
        return this;
    }

    // Om tom er satt så vil bortfallt naturalytelse tilkomme fra dagen etter tom.
    public InntektsmeldingBuilder medOpphoerAvNaturalytelseListe(BigDecimal belopPrMnd,
                                                                 LocalDate fom,
                                                                 LocalDate tom,
                                                                 Inntektsmelding.NaturalytelseType naturalytelseType) {
        this.opphoerAvNaturalytelse.add(new Inntektsmelding.OpphoerAvNaturalytelse(naturalytelseType, belopPrMnd, fom, tom));
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

    public InntektsmeldingBuilder medFørsteFraværsdag(LocalDate foersteFravaersdag) {
        this.foersteFravaarsdag = foersteFravaersdag;
        return this;
    }

    public InntektsmeldingBuilder medRefusjonsBelopPerMnd(int refusjonsBelopPerMnd) {
        return medRefusjonsBelopPerMnd(BigDecimal.valueOf(refusjonsBelopPerMnd));
    }

    public InntektsmeldingBuilder medRefusjonsBelopPerMnd(Prosent prosentAvBeregnetInntekt) {
        return medRefusjonsBelopPerMnd(this.beregnetInntekt.multiply(BigDecimal.valueOf(prosentAvBeregnetInntekt.prosent() / 100.0)));
    }

    public InntektsmeldingBuilder medRefusjonsBelopPerMnd(BigDecimal refusjonsBelopPerMnd) {
        this.refusjonBeloepPrMnd = refusjonsBelopPerMnd;
        return this;
    }

    public InntektsmeldingBuilder medEndringIRefusjonslist(Map<LocalDate, BigDecimal> endringRefusjonMap) {
        this.refusjonEndring.addAll(endringRefusjonMap.entrySet()
                .stream()
                .map(entry -> new Inntektsmelding.EndringRefusjon(entry.getKey(), entry.getValue()))
                .toList());
        return this;
    }

    public InntektsmeldingBuilder medRefusjonsOpphordato(LocalDate refusjonsOpphordato) {
        this.refusjonOpphoersdato = refusjonsOpphordato;
        return this;
    }

    public InntektsmeldingBuilder medBeregnetInntekt(int beregnetInntektBelop) {
        return medBeregnetInntekt(BigDecimal.valueOf(beregnetInntektBelop));
    }

    public InntektsmeldingBuilder medBeregnetInntekt(BigDecimal beregnetInntektBelop) {
        this.beregnetInntekt = beregnetInntektBelop;
        return this;
    }

    public InntektsmeldingBuilder medBeregnetInntekt(Prosent prosentIForholdTilRegistrertInntekt) {
        this.beregnetInntekt = this.beregnetInntekt.multiply(
                BigDecimal.valueOf(prosentIForholdTilRegistrertInntekt.prosent() / 100.0));
        return this;
    }

    public InntektsmeldingBuilder medUtsettelse(Inntektsmelding.UtsettelseÅrsak utsettelseAarsak,
                                                LocalDate periodeFom,
                                                LocalDate periodeTom) {
        this.utsettelser.add(new Inntektsmelding.Utsettelse(periodeFom, periodeTom, utsettelseAarsak));
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
            Objects.requireNonNull(this.foersteFravaarsdag, "FørsteFraværsdag kan ikke være null.");
        }

        var arbeidsforhold = new Inntektsmelding.Arbeidsforhold(this.beregnetInntekt, this.foersteFravaarsdag, this.utsettelser, this.arbeidsforholdId);

        var avsenderSystem = new Inntektsmelding.AvsenderSysten(this.avsendersystem, this.systemVersjon);

        Inntektsmelding.Refusjon refusjon = null;
        if (this.refusjonBeloepPrMnd != null
                || (this.refusjonEndring != null && !this.refusjonEndring.isEmpty())
                || this.refusjonOpphoersdato != null) {
            Objects.requireNonNull(refusjonBeloepPrMnd, "Refusjon beloep pr mnd kan ikke være null.");
            refusjon = new Inntektsmelding.Refusjon(this.refusjonBeloepPrMnd, this.refusjonEndring, this.refusjonOpphoersdato);
        }

        return new Inntektsmelding(this.ytelseType, this.arbeidstakerFnr, this.arbeidsgiver, arbeidsforhold, refusjon,
                this.opphoerAvNaturalytelse, avsenderSystem);
    }
}
