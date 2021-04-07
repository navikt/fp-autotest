package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.time.LocalDate;
import java.util.List;

public class Arbeidsgivere {

    private final List<Arbeidsgiver> arbeidsgivere;

    public Arbeidsgivere(List<Arbeidsgiver> arbeidsgivere) {
        this.arbeidsgivere = arbeidsgivere;
    }

    public List<Arbeidsgiver> getArbeidsgivere() {
        return arbeidsgivere;
    }

    public void sendDefualtInnteksmeldingerFP(long saksnummer, LocalDate startdatoForeldrepenger) {
        arbeidsgivere.forEach(arbeidsgiver ->
                arbeidsgiver.sendDefaultInntektsmeldingerFP(saksnummer, startdatoForeldrepenger));
    }

    public void sendDefualtInnteksmeldingerSVP(long saksnummer) {
        arbeidsgivere.forEach(arbeidsgiver ->
                arbeidsgiver.sendDefaultInntektsmeldingerSVP(saksnummer));
    }
}
