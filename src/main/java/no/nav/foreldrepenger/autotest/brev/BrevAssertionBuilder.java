package no.nav.foreldrepenger.autotest.brev;

import static no.nav.foreldrepenger.autotest.brev.BrevFormateringUtils.formaterKroner;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import no.nav.foreldrepenger.autotest.base.Paragrafer;

public class BrevAssertionBuilder {

    private final Set<String> brevAssertions;

    private BrevAssertionBuilder() {
        brevAssertions = new LinkedHashSet<>();
    }

    public static BrevAssertionBuilder ny() {
        return new BrevAssertionBuilder();
    }

    public BrevAssertionBuilder medTekstOmAleneomsorg() {
        brevAssertions.add("Du har aleneomsorgen for barnet, og får derfor hele foreldrepengeperioden.");
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

    public BrevAssertionBuilder medOverskriftOmAvslagAvForeldrepenger() {
        brevAssertions.add("Nav har avslått søknaden din om foreldrepenger");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmInnvilgetAnnuleringAvForeldrepenger() {
        brevAssertions.add("Nav har endret foreldrepengene dine");
        return this;
    }

    public BrevAssertionBuilder medOverskriftOmInnvilgettSvangerskapspenger() {
        brevAssertions.add("Nav har innvilget søknaden din om svangerskapspenger");
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

    public BrevAssertionBuilder medTekstOmDuKanSeBortFreDenneOmArbeidsgiverenHarSendt() {
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

    public BrevAssertionBuilder medParagrafer(Paragrafer... paragrafer) {
        Arrays.stream(paragrafer).forEach(this::medParagraf);
        return this;
    }

    public BrevAssertionBuilder medParagraf(Paragrafer paragraf) {
        brevAssertions.add(paragraf.getKode());
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

    public BrevAssertionBuilder medDuTarIkkeUtForeldrepengerFør() {
        brevAssertions.add("Du tar ikke ut foreldrepenger før ");
        return this;
    }

    public BrevAssertionBuilder medTekstKanIkkeBehandleFørSenere() {
        brevAssertions.add("Søknaden din om foreldrepenger");
        brevAssertions.add("kan ikke behandles før");
        brevAssertions.add("fordi foreldrepenger skal beregnes ut fra den inntekten du har når du starter uttaket ditt");
        return this;
    }

    public BrevAssertionBuilder medTekstOmDenAndreForelderenIkkeHarRettDerforFårDuAlt() {
        brevAssertions.add("Den andre forelderen har ikke rett til foreldrepenger. Derfor får du hele foreldrepengeperioden.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmXDagerIgjenAvPeriodenMed(int dagerIgjen) {
        brevAssertions.add("Det er %s dager igjen av perioden med foreldrepenger.".formatted(dagerIgjen));
        medTekstOmDageneMåVæreTattUtFørTreÅrEllerNyttBarn();
        return this;
    }

    public BrevAssertionBuilder medTekstOmDageneMåVæreTattUtFørTreÅrEllerNyttBarn() {
        brevAssertions.add("Disse dagene må være tatt ut innen barnet fyller tre år eller innen en ny foreldrepengeperiode for et nytt barn starter.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmForeldrepengerUtgjørDetSammeSomTidligere() {
        brevAssertions.add("Foreldrepengene utgjør det samme som tidligere. Sjekk utbetalingene dine på");
        return this;
    }

    public BrevAssertionBuilder medTekstOmDuFårXKronerPerDagFørSkatt(int beløp) {
        brevAssertions.add("Du får %s kroner per dag før skatt".formatted(formaterKroner(beløp)));
        return this;
    }

    public BrevAssertionBuilder medTekstOmDuFårIGjennomsnittXKronerIMånedenFørSkatt(int beløp) {
        brevAssertions.add("Du får i gjennomsnitt %s kroner i måneden før skatt.".formatted(formaterKroner(beløp)));
        return this;
    }

    public BrevAssertionBuilder medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer() {
        brevAssertions.add("Foreldrepengene blir utbetalt for alle dager, unntatt lørdag og søndag. Fordi det ikke er like mange dager i hver måned, vil de månedlige utbetalingene dine variere.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmGjennomsnittInntektFraTreSisteMåndene() {
        brevAssertions.add("Dette er gjennomsnittet av inntekten din fra de siste tre månedene. Hvis du nettopp har begynt å arbeide, byttet arbeidsforhold eller lønnen din har endret seg, har vi brukt månedsinntektene etter at endringen skjedde.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmInntektOverSeksGBeløp(int beløp) {
        brevAssertions.add("Foreldrepengene dine er fastsatt til %s kroner i året, som er seks ganger grunnbeløpet i folketrygden. Du tjener mer enn dette, men du får ikke foreldrepenger for den delen av inntekten som overstiger seks ganger grunnbeløpet.".formatted(formaterKroner(beløp)));
        return this;
    }

    public BrevAssertionBuilder medTekstOmAtSvpBlirUtbetaltForAlleDagerOgAtUtbetalingeneKanVariere() {
        brevAssertions.add("Svangerskapspengene blir utbetalt for alle dager, unntatt lørdag og søndag. Fordi det ikke er like mange dager i hver måned, vil de månedlige utbetalingene dine variere.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmPengenePåKontoDen25HverMåned() {
        brevAssertions.add("Pengene er på kontoen din innen den 25. hver måned. Sjekk utbetalingene dine på ");
        return this;
    }

    public BrevAssertionBuilder medTekstOmAtViUtbetalerTilArbeidsgiveren() {
        brevAssertions.add("Vi utbetaler svangerskapspengene til arbeidsgiveren din fordi du får lønn mens du er borte fra jobb.");
        return this;
    }

    public BrevAssertionBuilder medTekstOmAtViUtbetalerTilFlereArbeidsgiverne() {
        brevAssertions.add("Vi utbetaler svangerskapspengene til arbeidsgiverne dine fordi du får lønn mens du er borte fra jobb.");
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
