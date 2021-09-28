package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforholdstype;

public class Arbeidsforhold {

    private final Orgnummer orgnummer;
    private final ArbeidsforholdId arbeidsforholdId;
    private final LocalDate ansettelsesperiodeFom;
    private final LocalDate ansettelsesperiodeTom;
    private final Arbeidsforholdstype arbeidsforholdType;
    private final int stillingsprosent; // TODO Skriv stillingsprosent til egen klasse!


    Arbeidsforhold(Orgnummer orgnummer, ArbeidsforholdId arbeidsforholdId, LocalDate ansettelsesperiodeFom,
                   LocalDate ansettelsesperiodeTom, Arbeidsforholdstype arbeidsforholdType, int stillingsprosent) {
        this.orgnummer = orgnummer;
        this.arbeidsforholdId = arbeidsforholdId;
        this.arbeidsforholdType = arbeidsforholdType;
        this.stillingsprosent = stillingsprosent;
        this.ansettelsesperiodeFom = ansettelsesperiodeFom;
        this.ansettelsesperiodeTom = ansettelsesperiodeTom;
    }

    public Orgnummer orgnummer() {
        return orgnummer;
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

    public Arbeidsgiver arbeidsgiver() {
        return null;
    }
}
