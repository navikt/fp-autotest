package no.nav.foreldrepenger.autotest.verdikjedetester;

import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingSvangerskapspengerErketyper.lagSvangerskapspengerInntektsmelding;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.søknad.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadSvangerskapspengerErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.TilretteleggingsErketyper;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;


@Tag("verdikjede")
class VerdikjedeSvangerskapspenger extends ForeldrepengerTestBase {

    @Test
    @DisplayName("1: Mor søker fullt uttak med inntekt under 6G")
    @Description("Mor søker ingen tilrettelegging for en 100% stilling med inntekt over 6G.")
    void morSøkerIngenTilretteleggingInntektOver6GTest() {
        var familie = new Familie("501");
        var mor = familie.mor();
        var søkerFnr = mor.fødselsnummer();
        var tilrettelegginsprosent = 0;
        var termindato = LocalDate.now().plusMonths(3);
        var arbeidsforholdMor = mor.arbeidsforhold();
        var orgnummer = arbeidsforholdMor.orgnummer();
        var tilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(orgnummer));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        var saksnummer = mor.søk(søknad.build());

        var månedsinntekt = mor.månedsinntekt();
        var inntektsmedling = lagSvangerskapspengerInntektsmelding(søkerFnr, månedsinntekt, orgnummer);
        arbeidsforholdMor.arbeidsgiver().sendInntektsmeldinger(saksnummer, inntektsmedling);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo("INNVILGET");

        int beregnetDagsats = regnUtForventetDagsats(månedsinntekt, tilrettelegginsprosent);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats())
                .as("Forventer at dagsatsen beregnes ut i fra årsinntekten og 100% utbetalingsgrad")
                .isEqualTo(beregnetDagsats);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Foventer at hele den utbetalte dagsatsen går til søker!")
                .isTrue();
    }

    @Test
    @DisplayName("2: Mor søker gradert uttak med inntekt over 6G")
    @Description("Mor søker delvis tilrettelegging for en 100% stilling hvor hun har inntekt over 6G.")
    void morSøkerDelvisTilretteleggingMedInntektOver6GTest() {
        var familie = new Familie("502");
        var mor = familie.mor();
        var søkerFnr = mor.fødselsnummer();
        var arbeidsforholdMor = mor.arbeidsforhold();
        var orgnummer = arbeidsforholdMor.orgnummer();
        var tilrettelegginsprosent = 40;
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegging = TilretteleggingsErketyper.delvisTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(orgnummer),
                BigDecimal.valueOf(tilrettelegginsprosent));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        var saksnummer = mor.søk(søknad.build());

        var månedsinntekt = mor.månedsinntekt();
        var inntektsmedling = lagSvangerskapspengerInntektsmelding(
                søkerFnr,
                månedsinntekt,
                orgnummer);
        arbeidsforholdMor.arbeidsgiver().sendInntektsmeldinger(saksnummer, inntektsmedling);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("En begrunnelse fra autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo("INNVILGET");

        var beregnetDagsats = regnUtForventetDagsats(månedsinntekt, tilrettelegginsprosent);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats())
                .as("Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt")
                .isEqualTo(beregnetDagsats);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Foventer at hele den utbetalte dagsatsen går til søker!")
                .isTrue();
    }

    @Test
    @DisplayName("3: Mor søk fullt uttak for ett av to arbeidsforhold i samme virksomhet")
    @Description("Mor søker ingen tilrettelegging for ett av to arbeidsforhold i samme virksomhet. Arbeidsgiver leverer to" +
            "inntektsmeldinger med forskjellig arbeidsforholdID, med ulik lønn.")
    void morSøkerFulltUttakForEttAvToArbeidsforholdTest() {
        var familie = new Familie("503");
        var mor = familie.mor();
        var søkerFnr = mor.fødselsnummer();
        var arbeidsforholdListe = mor.arbeidsforholdListe();
        var arbeidsforhold1 = arbeidsforholdListe.get(0);
        var orgnummer1 = arbeidsforhold1.orgnummer();
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0;
        var tilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(orgnummer1));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        var saksnummer = mor.søk(søknad.build());


        var inntektsperioder = mor.inntektsperioder();
        var månedsinntekt1 = inntektsperioder.get(0).beløp();
        var inntektsmedling1 = lagSvangerskapspengerInntektsmelding(søkerFnr, månedsinntekt1, orgnummer1)
                .medArbeidsforholdId(arbeidsforhold1.arbeidsforholdId());
        arbeidsforhold1.arbeidsgiver().sendInntektsmeldinger(saksnummer, inntektsmedling1);

        var månedsinntekt2 = inntektsperioder.get(1).beløp();
        var arbeidsforhold2 = arbeidsforholdListe.get(1);
        var orgNummer2 = arbeidsforhold2.orgnummer();
        var arbeidsforholdId2 = arbeidsforhold2.arbeidsforholdId();
        var inntektsmedling2 = lagSvangerskapspengerInntektsmelding(søkerFnr, månedsinntekt2, orgNummer2)
                .medArbeidsforholdId(arbeidsforhold2.arbeidsforholdId());
        arbeidsforhold2.arbeidsgiver().sendInntektsmeldinger(saksnummer, inntektsmedling2);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setSkalBrukesTilFalseForArbeidsforhold(arbeidsforholdId2);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår.godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo("INNVILGET");

        var årsinntekt = Double.valueOf(månedsinntekt1) * 12;
        var utbetalingProsentFaktor = (double) (100 - tilrettelegginsprosent) / 100;
        var beregnetDagsats = regnUtForventetDagsats(månedsinntekt1, tilrettelegginsprosent);
        var beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0);
        assertThat(beregningsgrunnlagPeriode.getRedusertPrAar())
                .as("Forventer at redusertPrAar er det samme som årsinntekten for den gjeldende arbeidsforhold x utbetalingsgrad")
                .isEqualTo(årsinntekt * utbetalingProsentFaktor);
        assertThat(beregningsgrunnlagPeriode.getDagsats())
                .as("Forventer at dagsatsen bare beregnes ut i fra årsinntekten til det ene arbeidsforholdet og dens utbetalingsgrad!")
                .isEqualTo(beregnetDagsats);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgnummer1, 0))
                .as("Foventer at hele den utbetalte dagsatsen går til søker!")
                .isTrue();

    }

    @Test
    @DisplayName("4: Mor kombinert AT/SN søker i to omganger")
    @Description("Mor søker i første omgang bare for AF, hvor AG ønsker full refusjon av innekt over 6G." +
            "To måneder senere sender mor inn ny søknad for SN")
    void morSøkerFørstForATOgSenereForSNTest() {
        var familie = new Familie("511");
        var mor = familie.mor();
        var søkerFnr = mor.fødselsnummer();
        var arbeidsforhold = mor.arbeidsforhold();
        var orgNr = arbeidsforhold.orgnummer();
        var næringsinntekt = mor.næringsinntekt(2018);
        var opptjening = OpptjeningErketyper.medEgenNaeringOpptjening(
                false,
                næringsinntekt,
                false);
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0;
        var tilrettelegging1 = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now().minusMonths(2),
                LocalDate.now().minusMonths(2),
                ArbeidsforholdErketyper.virksomhet(orgNr));
        var søknad1 = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging1))
                .medOpptjening(opptjening);
        var saksnummer1 = mor.søk(søknad1.build());


        var månedsinntekt = mor.månedsinntekt();
        var inntektsmelding = lagSvangerskapspengerInntektsmelding(søkerFnr, månedsinntekt, orgNr)
                .medArbeidsforholdId(arbeidsforhold.arbeidsforholdId())
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntekt));
        arbeidsforhold.arbeidsgiver().sendInntektsmeldinger(saksnummer1, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer1);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår.godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer1, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo("INNVILGET");

        var beregnetDagsats = regnUtForventetDagsats(månedsinntekt, tilrettelegginsprosent);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats())
                .as("Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt!")
                .isEqualTo(beregnetDagsats);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgNr, 100))
                .as("Foventer at hele den utbetalte dagsatsen går til arbeidsgiver siden de ønsker full refusjon!")
                .isTrue();

        /* SØKNAD 2 */
        var tilrettelegging2 = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.selvstendigNæringsdrivende());
        var søknad2 = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging2))
                .medOpptjening(opptjening);
        var saksnummer2 = mor.søk(søknad2.build());

        saksbehandler.hentFagsak(saksnummer2);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var avklarFaktaFødselOgTilrettelegging2 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging2.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging2);

        var bekreftSvangerskapspengervilkår2 = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår2.godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår2);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer2, true);

        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgNr, 100))
                .as("Foventer at hele den utbetalte dagsatsen går til arbeidsgiver siden de ønsker full refusjon!")
                .isTrue();
        var beregningsresultatPerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger().getPerioder();
        assertThat(beregningsresultatPerioder.get(0).getAndeler().size())
                .as("Andeler for første periode i tilkjent ytelse")
                .isEqualTo(1);
        assertThat(saksbehandler.sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(beregningsresultatPerioder.get(0), "AT"))
                .as("Forventer aktivitetsstatus for første andel for første periode er AT")
                .isTrue();
        assertThat(beregningsresultatPerioder.get(1).getAndeler().size())
                .as("Andeler for andre periode i tilkjent ytelse")
                .isEqualTo(2);
        assertThat(saksbehandler.sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(beregningsresultatPerioder.get(1), "AT"))
                .as("Forventer aktivitetsstatus for første andel for andre periode er AT")
                .isTrue();
        assertThat(saksbehandler.sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(beregningsresultatPerioder.get(1), "SN"))
                .as("Forventer aktivitetsstatus for andre andel for andre periode er SN")
                .isTrue();


        var beregningsresultatPeriodeAndeler = saksbehandler.hentBeregningsresultatPerioderMedAndelISN();
        assertThat(beregningsresultatPeriodeAndeler.get(0).getRefusjon())
                .as("Dagsats til refusjon")
                .isZero();
        assertThat(beregningsresultatPeriodeAndeler.get(0).getTilSoker())
                .as("Dagsats til søker")
                .isZero();
    }

    @Test
    @DisplayName("5: Mor har flere AG og søker fullt uttak for begge AFene")
    @Description("Mor søker ingen tilrettelegging for begge arbeidsforholdene. Begge arbeidsgiverene ønsker 100% reufsjon." +
            "Inntekten i disse to arbeidsforholdene er samlet over 6G hvor fordelingen er 2/3 og 1/3 av inntekten.")
    void morSøkerIngenTilretteleggingForToArbeidsforholdFullRefusjonTest() {
        var familie = new Familie("504");
        var mor = familie.mor();
        var søkerFnr = mor.fødselsnummer();
        var arbeidsforhold1 = mor.arbeidsforholdListe().get(0);
        var orgnummer1 = arbeidsforhold1.orgnummer();
        var arbeidsforhold2 = mor.arbeidsforholdListe().get(1);
        var orgnummer2 = arbeidsforhold2.orgnummer();
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0;
        var tilrettelegging1 = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(orgnummer1));
        var tilrettelegging2 = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(orgnummer2));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging1, tilrettelegging2));
        var saksnummer = mor.søk(søknad.build());

        var månedsinntekt1 = mor.månedsinntekt(orgnummer1);
        var inntektsmedling1 = lagSvangerskapspengerInntektsmelding(søkerFnr, månedsinntekt1, orgnummer1)
                .medArbeidsforholdId(arbeidsforhold1.arbeidsforholdId())
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntekt1));
        arbeidsforhold1.arbeidsgiver().sendInntektsmeldinger(saksnummer, inntektsmedling1);
        var månedsinntekt2 = mor.månedsinntekt(orgnummer2);
        var inntektsmedling2 = lagSvangerskapspengerInntektsmelding(søkerFnr, månedsinntekt2, orgnummer2)
                .medArbeidsforholdId(arbeidsforhold2.arbeidsforholdId())
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntekt2));
        arbeidsforhold2.arbeidsgiver().sendInntektsmeldinger(saksnummer, inntektsmedling2);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo("INNVILGET");

        var beregnetDagsats = regnUtForventetDagsats(månedsinntekt1 + månedsinntekt2, tilrettelegginsprosent);
        var beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0);
        assertThat(beregningsgrunnlagPeriode.getDagsats())
                .as("Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt!")
                .isEqualTo(beregnetDagsats);

        var månedsinntekt = månedsinntekt1 + månedsinntekt2;
        var prosentTilArbeidsgiver1 = ((double) månedsinntekt1 / månedsinntekt) * 100;
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgnummer1, prosentTilArbeidsgiver1))
                .as("Foventer at hele den utbetalte dagsatsen går til søker!")
                .isTrue();

        var prosentTilArbeidforhold2 = 100 - prosentTilArbeidsgiver1;
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgnummer2, prosentTilArbeidforhold2))
                .as("Foventer at hele den utbetalte dagsatsen går til søker!")
                .isTrue();
    }

    private Integer regnUtForventetDagsats(Integer samletMånedsbeløp, Integer tilrettelegginsprosent) {
        var årsinntekt = Double.valueOf(samletMånedsbeløp) * 12;
        var seksG = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getHalvG() * 2 * 6;
        var utbetalingProsentFaktor = (double) (100 - tilrettelegginsprosent) / 100;
        if (årsinntekt > seksG) {
            årsinntekt = seksG;
        }
        return ((int) Math.round((årsinntekt * utbetalingProsentFaktor) / 260));
    }
}
