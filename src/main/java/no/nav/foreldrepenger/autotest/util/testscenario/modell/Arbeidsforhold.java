package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforholdstype;

public class Arbeidsforhold {

    private final ArbeidsgiverIdentifikator arbeidsgiverIdentifikasjon;
    private final ArbeidsforholdId arbeidsforholdId;
    private final LocalDate ansettelsesperiodeFom;
    private final LocalDate ansettelsesperiodeTom;
    private final Arbeidsforholdstype arbeidsforholdType;
    private final int stillingsprosent; // TODO Skriv stillingsprosent til egen klasse!


    Arbeidsforhold(ArbeidsgiverIdentifikator arbeidsgiverIdentifikasjon, ArbeidsforholdId arbeidsforholdId,
                   LocalDate ansettelsesperiodeFom, LocalDate ansettelsesperiodeTom,
                   Arbeidsforholdstype arbeidsforholdType, int stillingsprosent) {
        this.arbeidsgiverIdentifikasjon = arbeidsgiverIdentifikasjon;
        this.arbeidsforholdId = arbeidsforholdId;
        this.arbeidsforholdType = arbeidsforholdType;
        this.stillingsprosent = stillingsprosent;
        this.ansettelsesperiodeFom = ansettelsesperiodeFom;
        this.ansettelsesperiodeTom = ansettelsesperiodeTom;
    }

    public ArbeidsgiverIdentifikator arbeidsgiverIdentifikasjon() {
        return arbeidsgiverIdentifikasjon;
    }

    public ArbeidsforholdId arbeidsforholdId() {
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

    public Virksomhet arbeidsgiver() {
        return null;
    }
}
