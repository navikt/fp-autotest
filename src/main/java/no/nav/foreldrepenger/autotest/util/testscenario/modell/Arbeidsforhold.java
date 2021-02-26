package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.time.LocalDate;

import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforholdstype;

public class Arbeidsforhold {

    private final String arbeidsforholdId;
    private final LocalDate ansettelsesperiodeFom;
    private final LocalDate ansettelsesperiodeTom;
    private final Arbeidsforholdstype arbeidsforholdType;
    private final int stillingsprosent;
    private final Arbeidsgiver arbeidsgiver;

    Arbeidsforhold(String arbeidsforholdId, LocalDate ansettelsesperiodeFom, LocalDate ansettelsesperiodeTom,
                   Arbeidsforholdstype arbeidsforholdType, int stillingsprosent, Arbeidsgiver arbeidsgiver) {
        this.arbeidsforholdId = arbeidsforholdId;
        this.arbeidsforholdType = arbeidsforholdType;
        this.stillingsprosent = stillingsprosent;
        this.arbeidsgiver = arbeidsgiver;
        this.ansettelsesperiodeFom = ansettelsesperiodeFom;
        this.ansettelsesperiodeTom = ansettelsesperiodeTom;
    }

    public String arbeidsforholdId() {
        return arbeidsforholdId;
    }

    public LocalDate ansettelsesperiodeFom() {
        return ansettelsesperiodeFom;
    }

    public LocalDate ansettelsesperiodeTom() {
        return ansettelsesperiodeTom;
    }

    public Arbeidsforholdstype arbeidsforholdstype() {
        return arbeidsforholdType;
    }

    public int stillingsprosent() {
        return stillingsprosent;
    }

    public Arbeidsgiver arbeidsgiver() {
        return arbeidsgiver;
    }

    public String orgnummer() {
        return arbeidsgiver.orgnummer();
    }
}
