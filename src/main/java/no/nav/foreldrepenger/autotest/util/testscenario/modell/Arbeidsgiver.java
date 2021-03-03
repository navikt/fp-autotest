package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;

public class Arbeidsgiver {
    private final String orgnummer;
    private final Søker søker;

    Arbeidsgiver(String orgnummer, Søker søker) {
        this.orgnummer = orgnummer;
        this.søker = søker;
    }

    public String orgnummer() {
        return orgnummer;
    }

    public void sendInntektsmeldinger(long saksnummer, InntektsmeldingBuilder... inntektsmelding) {
        new Innsender(Aktoer.Rolle.SAKSBEHANDLER).sendInnInnteksmeldingFpfordel(søker.fødselsnummer(), saksnummer, inntektsmelding);
    }
}
