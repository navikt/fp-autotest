package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class Arbeidsgivere {

    private final List<Arbeidsgiver> alleArbeidsgivere;

    public Arbeidsgivere(List<Arbeidsgiver> alleArbeidsgivere) {
        this.alleArbeidsgivere = alleArbeidsgivere;
    }

    public List<Arbeidsgiver> toList() {
        return alleArbeidsgivere;
    }

    public Arbeidsgiver arbeidsgiver(ArbeidsgiverIdentifikator arbeidsgiverIdentifikator) {
        return alleArbeidsgivere.stream()
                .filter(a -> a.arbeidsgiverIdentifikator().equals(arbeidsgiverIdentifikator))
                .findFirst()
                .orElseThrow();
    }

    public PersonArbeidsgiver arbeidsgiver(Fødselsnummer fødselsnummer) {
        return alleArbeidsgivere.stream()
                .filter(PersonArbeidsgiver.class::isInstance)
                .map(PersonArbeidsgiver.class::cast)
                .filter(a -> a.fnrArbeidsgiver().equals(fødselsnummer))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Scenario har ikke private arbeidsforhold"));
    }

    public void sendDefaultInntektsmeldingerFP(Saksnummer saksnummer, LocalDate startdatoForeldrepenger) {
        alleArbeidsgivere.forEach(arbeidsgiver ->
                arbeidsgiver.sendInntektsmeldingerFP(saksnummer, startdatoForeldrepenger));
    }

    public void sendDefaultInnteksmeldingerSVP(Saksnummer saksnummer) {
        alleArbeidsgivere.forEach(arbeidsgiver ->
                arbeidsgiver.sendInntektsmeldingerSVP(saksnummer));
    }
}
