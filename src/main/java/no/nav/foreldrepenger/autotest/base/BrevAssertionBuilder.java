package no.nav.foreldrepenger.autotest.base;

import java.util.LinkedHashSet;
import java.util.Set;

import static no.nav.foreldrepenger.autotest.base.FpsakTestBase.formatKroner;

public class BrevAssertionBuilder {

    private final Set<String> brevAssertions;

    private BrevAssertionBuilder() {
        brevAssertions = new LinkedHashSet<>();
    }

    public static BrevAssertionBuilder ny() {
        return new BrevAssertionBuilder();
    }

    public BrevAssertionBuilder medTekstOmAleneomsorg() {
        brevAssertions.add("Du har aleneomsorgen for barnet og får derfor hele foreldrepengeperioden.");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmInnvilget100ProsentForeldrepenger() {
        brevAssertions.add("Nav har innvilget søknaden din om 100 prosent foreldrepenger");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmInnvilget80ProsentForeldrepenger() {
        brevAssertions.add("Nav har innvilget søknaden din om 80 prosent foreldrepenger");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmInnvilgetEndringAvForeldrepenger() {
        brevAssertions.add("Nav har endret foreldrepengeperioden din");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmInnvilgetAnnuleringAvForeldrepenger() {
        brevAssertions.add("Nav har endret foreldrepengene dine");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmInnvilgetEnagangsstønad() {
        brevAssertions.add("Nav har innvilget søknaden din om engangsstønad");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmViHarBedtOmOpplysningerFraArbeidsgiverenDin() {
        brevAssertions.add("Vi har bedt om opplysninger fra arbeidsgiveren din");
        return this;
    }

    public BrevAssertionBuilder medTekstOmAtViHarBedtArbeidsgiverenOmInntektsmelding() {
        brevAssertions.add("Vi har bedt denne arbeidsgiveren om å sende inntektsmelding");
        return this;
    }

    public BrevAssertionBuilder medTesktOmDuKanSeBortFreDenneOmArbeidsgiverenHarSendt() {
        brevAssertions.add("Hvis arbeidsgiver allerede har sendt inntektsmelding, kan du se bort fra denne henvendelsen.");
        return this;
    }

    public BrevAssertionBuilder medKapittelDuHarRettTilKlage() {
        brevAssertions.add("Du har rett til å klage");
        return this;
    }

    public BrevAssertionBuilder medKapittelDuHarRettTilInnsyn() {
        brevAssertions.add("Du har rett til innsyn");
        return this;
    }

    public BrevAssertionBuilder medKapittelHarDuSpørsmål() {
        brevAssertions.add("Har du spørsmål?");
        return this;
    }

    public BrevAssertionBuilder medVennligHilsen() {
        brevAssertions.add("Med vennlig hilsen");
        return this;
    }

    public BrevAssertionBuilder medUnderksriftNFP() {
        brevAssertions.add("Nav Familie- og pensjonsytelser");
        return this;
    }

    public BrevAssertionBuilder medTekstOmAutomatiskVedtakUtenUndferskrift() {
        brevAssertions.add("Vedtaket har blitt automatisk saksbehandlet av vårt fagsystem. Vedtaksbrevet er derfor ikke underskrevet av saksbehandler.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmOpplysningerFraEnArbeidsgiver() {
        brevAssertions.add("Arbeidsgiveren din har gitt oss disse opplysningene.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmOpplysningerFraFlereArbeidsgivere() {
        brevAssertions.add("Arbeidsgiverne dine har gitt oss disse opplysningene.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmVedtaketEtterFolketrygdloven() {
        brevAssertions.add("Vedtaket er gjort etter folketrygdloven §");
        return this;
    }

    public BrevAssertionBuilder medTekstOmBeregningEtterFolketrygdloven() {
        brevAssertions.add("Beregningen er gjort etter folketrygdloven §");
        return this;
    }

    public BrevAssertionBuilder medTekstOmInntektBruktIBeregningen() {
        brevAssertions.add("Inntekt vi har brukt i beregningen");
        return this;
    }

    public BrevAssertionBuilder medParagraf_14_17() {
        brevAssertions.add("14-17");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_16() {
        brevAssertions.add("14-16");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_15() {
        brevAssertions.add("14-15");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_14() {
        brevAssertions.add("14-14");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_13() {
        brevAssertions.add("14-13");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_12() {
        brevAssertions.add("14-12");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_11() {
        brevAssertions.add("14-11");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_10() {
        brevAssertions.add("14-10");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_9() {
        brevAssertions.add("14-9");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_8() {
        brevAssertions.add("14-8");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_7() {
        brevAssertions.add("14-7");
        return this;
    }
    public BrevAssertionBuilder medParagraf_14_6() {
        brevAssertions.add("14-6");
        return this;
    }

    public BrevAssertionBuilder medParagraf_8_30() {
        brevAssertions.add("8-30");
        return this;
    }

    public BrevAssertionBuilder medParagraf_8_35() {
        brevAssertions.add("8-35");
        return this;
    }

    public BrevAssertionBuilder medParagraf_8_38() {
        brevAssertions.add("8-38");
        return this;
    }

    public BrevAssertionBuilder medParagraf_8_41() {
        brevAssertions.add("8-41");
        return this;
    }

    public BrevAssertionBuilder medParagraf_8_49() {
        brevAssertions.add("8-49");
        return this;
    }

    public BrevAssertionBuilder medKapittelDuMåMeldeOmEndringer() {
        brevAssertions.add("Du må melde fra om endringer");
        return this;
    }

    public BrevAssertionBuilder medKapittelDetteHarViInnvilget() {
        brevAssertions.add("Dette har vi innvilget");
        return this;
    }

    public BrevAssertionBuilder medKapittelDetteHarViAvslått() {
        brevAssertions.add("Dette har vi avslått");
        return this;
    }

    public BrevAssertionBuilder medKapittelDuHarFlereAgbeidsgivere() {
        brevAssertions.add("Du har flere arbeidsgivere");
        return this;
    }

    public BrevAssertionBuilder medTekstOmDuMåSøkeNyForeldrepengerperiodePå() {
        brevAssertions.add("Du må søke om ny foreldrepengeperiode på ");
        return this;
    }

    public BrevAssertionBuilder medTekstOmSøkeSenestDagenFørNyPeriodeEllerBarnetFyllerTreÅr() {
        brevAssertions.add("senest dagen før ny foreldrepengeperiode for nytt barn starter, eller senest dagen før barnet fyller tre år.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmDenAndreForelderenIkkeHarRettDerforFårDuAlt() {
        brevAssertions.add("Den andre forelderen har ikke rett til foreldrepenger. Derfor får du hele foreldrepengeperioden.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmXDagerIgjenAvPeriodenMed(int dagerIgjen) {
        brevAssertions.add("Det er %s dager igjen av perioden med foreldrepenger. Disse dagene må være tatt ut innen barnet fyller tre år eller innen en ny foreldrepengeperiode for et nytt barn starter.".formatted(dagerIgjen));
        return this;
    }

    public BrevAssertionBuilder medTekstOmDuFårXKronerUtbetalt(int beløp) {
        brevAssertions.add("Du får %s kroner per dag før skatt".formatted(formatKroner(beløp)));
        return this;
    }

    public BrevAssertionBuilder medEgenndefinertAssertion(String assertion) {
        brevAssertions.add(assertion);
        return this;
    }

    public Set<String> build() {
        return brevAssertions;
    }
}
