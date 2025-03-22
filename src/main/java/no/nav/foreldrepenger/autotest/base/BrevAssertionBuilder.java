package no.nav.foreldrepenger.autotest.base;

import java.util.LinkedHashSet;
import java.util.Set;

public class BrevAssertionBuilder {

    private Set<String> BREV_ASSERTIONS;

    private BrevAssertionBuilder() {
        BREV_ASSERTIONS = new LinkedHashSet<>();
    }

    public static BrevAssertionBuilder ny() {
        return new BrevAssertionBuilder();
    }

    public BrevAssertionBuilder medAleneomsorg() {
        BREV_ASSERTIONS.add("Du har aleneomsorgen for barnet og får derfor hele foreldrepengeperioden.");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmInnvilget100ProsentForeldrepenger() {
        BREV_ASSERTIONS.add("Nav har innvilget søknaden din om 100 prosent foreldrepenger");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmInnvilget80ProsentForeldrepenger() {
        BREV_ASSERTIONS.add("Nav har innvilget søknaden din om 80 prosent foreldrepenger");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmInnvilgetEndringAvForeldrepenger() {
        BREV_ASSERTIONS.add("Nav har endret foreldrepengeperioden din");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmInnvilgetAnnuleringAvForeldrepenger() {
        BREV_ASSERTIONS.add("Nav har endret foreldrepengene dine");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmInnvilgetEnagangsstønad() {
        BREV_ASSERTIONS.add("Nav har innvilget søknaden din om engangsstønad");
        return this;
    }

    public BrevAssertionBuilder medKapittelDuHarRettTilKlage() {
        BREV_ASSERTIONS.add("Du har rett til å klage");
        return this;
    }

    public BrevAssertionBuilder medKapittelDuHarRettTilInnsyn() {
        BREV_ASSERTIONS.add("Du har rett til innsyn");
        return this;
    }

    public BrevAssertionBuilder medKapittelHarDuSpørsmål() {
        BREV_ASSERTIONS.add("Har du spørsmål?");
        return this;
    }

    public BrevAssertionBuilder medVennligHilsen() {
        BREV_ASSERTIONS.add("Med vennlig hilsen");
        return this;
    }

    public BrevAssertionBuilder medUnderksriftNFP() {
        BREV_ASSERTIONS.add("Nav Familie- og pensjonsytelser");
        return this;
    }

    public BrevAssertionBuilder medTekstOmAutomatiskVedtakUtenUndferskrift() {
        BREV_ASSERTIONS.add("Vedtaket har blitt automatisk saksbehandlet av vårt fagsystem. Vedtaksbrevet er derfor ikke underskrevet av saksbehandler.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmOpplysningerFraEnArbeidsgiver() {
        BREV_ASSERTIONS.add("Arbeidsgiveren din har gitt oss disse opplysningene.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmOpplysningerFraFlereArbeidsgivere() {
        BREV_ASSERTIONS.add("Arbeidsgiverne dine har gitt oss disse opplysningene.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmVedtaketEtterFolketrygdloven() {
        BREV_ASSERTIONS.add("Vedtaket er gjort etter folketrygdloven §");
        return this;
    }

    public BrevAssertionBuilder medTekstOmBeregningEtterFolketrygdloven() {
        BREV_ASSERTIONS.add("Beregningen er gjort etter folketrygdloven §");
        return this;
    }

    public BrevAssertionBuilder medTekstOmInntektBruktIBeregningen() {
        BREV_ASSERTIONS.add("Inntekt vi har brukt i beregningen");
        return this;
    }

    public BrevAssertionBuilder medParagraf_14_17() {
        BREV_ASSERTIONS.add("14-17");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_16() {
        BREV_ASSERTIONS.add("14-16");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_15() {
        BREV_ASSERTIONS.add("14-15");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_14() {
        BREV_ASSERTIONS.add("14-14");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_13() {
        BREV_ASSERTIONS.add("14-13");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_12() {
        BREV_ASSERTIONS.add("14-12");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_11() {
        BREV_ASSERTIONS.add("14-11");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_10() {
        BREV_ASSERTIONS.add("14-10");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_9() {
        BREV_ASSERTIONS.add("14-9");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_8() {
        BREV_ASSERTIONS.add("14-8");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_7() {
        BREV_ASSERTIONS.add("14-7");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_6() {
        BREV_ASSERTIONS.add("14-6");
        return this;
    }

    public BrevAssertionBuilder medParagraf_8_30() {
        BREV_ASSERTIONS.add("8-30");
        return this;
    }

    public BrevAssertionBuilder medParagraf_8_35() {
        BREV_ASSERTIONS.add("8-35");
        return this;
    }

    public BrevAssertionBuilder medKapittelDuMåMeldeOmEndringer() {
        BREV_ASSERTIONS.add("Du må melde fra om endringer");
        return this;
    }

    public BrevAssertionBuilder medKapittelDetteHarViInnvilget() {
        BREV_ASSERTIONS.add("Dette har vi innvilget");
        return this;
    }

    public BrevAssertionBuilder medKapittelDetteHarViAvslått() {
        BREV_ASSERTIONS.add("Dette har vi avslått");
        return this;
    }

    public BrevAssertionBuilder medKapittelDuHarFlereAgbeidsgivere() {
        BREV_ASSERTIONS.add("Du har flere arbeidsgivere");
        return this;
    }

    public BrevAssertionBuilder medTekstOmDuMåSøkeNyForeldrepengerperiodePå() {
        BREV_ASSERTIONS.add("Du må søke om ny foreldrepengeperiode på ");
        return this;
    }

    public BrevAssertionBuilder medTekstOmSøkeSenestDagenFørNyPeriodeEllerBarnetFyllerTreÅr() {
        BREV_ASSERTIONS.add("senest dagen før ny foreldrepengeperiode for nytt barn starter, eller senest dagen før barnet fyller tre år.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmDenAndreForelderenIkkeHarRettDerforFårDuAlt() {
        BREV_ASSERTIONS.add("Den andre forelderen har ikke rett til foreldrepenger. Derfor får du hele foreldrepengeperioden.");
        return this;
    }

    public BrevAssertionBuilder medEgenndefinertAssertion(String assertion) {
        BREV_ASSERTIONS.add(assertion);
        return this;
    }

    public Set<String> build() {
        return BREV_ASSERTIONS;
    }
}
