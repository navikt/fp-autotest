package no.nav.foreldrepenger.autotest.søknad.erketyper;

import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.autotest.søknad.modell.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver;
import no.nav.foreldrepenger.autotest.søknad.modell.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.autotest.søknad.modell.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Orgnummer;

public final class ArbeidsforholdErketyper {

    private ArbeidsforholdErketyper() {
    }

    public static Virksomhet virksomhet(Orgnummer orgnummer) {
        return new Virksomhet(orgnummer);
    }

    public static PrivatArbeidsgiver privatArbeidsgiver(String fnr) {
        return new PrivatArbeidsgiver(new Fødselsnummer(fnr));
    }

    public static SelvstendigNæringsdrivende selvstendigNæringsdrivende() {
        return new SelvstendigNæringsdrivende("", "");
    }

}
