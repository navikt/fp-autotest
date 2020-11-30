package no.nav.foreldrepenger.autotest.søknad.erketyper;

import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.autotest.søknad.modell.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver;
import no.nav.foreldrepenger.autotest.søknad.modell.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.autotest.søknad.modell.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet;

public class ArbeidsforholdErketyper {

    public static Virksomhet virksomhet(String orgnummer) {
        return new Virksomhet(orgnummer);
    }

    public static PrivatArbeidsgiver privatArbeidsgiver(String fnr) {
        return new PrivatArbeidsgiver(new Fødselsnummer(fnr));
    }

    public static SelvstendigNæringsdrivende selvstendigNæringsdrivende() {
        return new SelvstendigNæringsdrivende("", "");
    }

}
