package no.nav.foreldrepenger.autotest.verdikjedetester;

import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingSvangerskapspengerErketyper.lagSvangerskapspengerInntektsmelding;

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
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriodeAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.søknad.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadSvangerskapspengerErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.TilretteleggingsErketyper;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;


@Tag("verdikjede")
public class VerdikjedeSvangerskapspenger extends ForeldrepengerTestBase {

    @Test
    @DisplayName("1: Mor søker fullt uttak med inntekt under 6G")
    @Description("Mor søker ingen tilrettelegging for en 100% stilling med inntekt over 6G.")
    public void morSøkerIngenTilretteleggingInntektOver6GTest() {
        var testscenario = opprettTestscenario("501");
        var søkerFnr = testscenario.personopplysninger().søkerIdent();
        var orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var tilrettelegginsprosent = 0;
        LocalDate termindato = LocalDate.now().plusMonths(3);
        var tilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(orgNr));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        var saksnummer = innsender.sendInnSøknad(søkerFnr, søknad.build());


        var månedsinntekt = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0)
                .beløp();
        var orgNummer = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var inntektsmedling = lagSvangerskapspengerInntektsmelding(
                søkerFnr,
                månedsinntekt,
                orgNummer);
        innsender.sendInnInnteksmeldingFpfordel(inntektsmedling, søkerFnr, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        int beregnetDagsats = regnUtForventetDagsats(månedsinntekt, tilrettelegginsprosent);
        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0)
                        .getDagsats() == beregnetDagsats,
                "Forventer at dagsatsen beregnes ut i fra årsinntekten og 100% utbetalingsgrad!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Foventer at hele den utbetalte dagsatsen går til søker!");

    }

    @Test
    @DisplayName("2: Mor søker gradert uttak med inntekt over 6G")
    @Description("Mor søker delvis tilrettelegging for en 100% stilling hvor hun har inntekt over 6G.")
    public void morSøkerDelvisTilretteleggingMedInntektOver6GTest() {
        var testscenario = opprettTestscenario("502");
        var søkerFnr = testscenario.personopplysninger().søkerIdent();
        var orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var tilrettelegginsprosent = 40;
        LocalDate termindato = LocalDate.now().plusMonths(3);
        var tilrettelegging = TilretteleggingsErketyper.delvisTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(orgNr),
                BigDecimal.valueOf(tilrettelegginsprosent));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        var saksnummer = innsender.sendInnSøknad(søkerFnr, søknad.build());

        var månedsinntekt = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0)
                .beløp();
        var orgNummer = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var inntektsmedling = lagSvangerskapspengerInntektsmelding(
                søkerFnr,
                månedsinntekt,
                orgNummer);
        innsender.sendInnInnteksmeldingFpfordel(inntektsmedling, søkerFnr, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("En begrunnelse fra autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        int beregnetDagsats = regnUtForventetDagsats(månedsinntekt, tilrettelegginsprosent);
        verifiser(
                saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0)
                        .getDagsats() == beregnetDagsats,
                "Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Foventer at hele den utbetalte dagsatsen går til søker!");

    }

    @Test
    @DisplayName("3: Mor søk fullt uttak for ett av to arbeidsforhold i samme virksomhet")
    @Description("Mor søker ingen tilrettelegging for ett av to arbeidsforhold i samme virksomhet. Arbeidsgiver leverer to" +
            "inntektsmeldinger med forskjellig arbeidsforholdID, med ulik lønn.")
    public void morSøkerFulltUttakForEttAvToArbeidsforholdTest() {
        var scenario = opprettTestscenario("503");
        var søkerFnr = scenario.personopplysninger().søkerIdent();
        var orgNr1 = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        LocalDate termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0;
        var tilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(orgNr1));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        var saksnummer = innsender.sendInnSøknad(søkerFnr, søknad.build());

        var månedsinntekt1 = scenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp();
        var orgNummer1 = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr();
        var arbeidsforholdId1 = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsforholdId();
        var inntektsmedling1 = lagSvangerskapspengerInntektsmelding(søkerFnr, månedsinntekt1, orgNummer1)
                .medArbeidsforholdId(arbeidsforholdId1);
        var månedsinntekt2 = scenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(1).beløp();
        var orgNummer2 = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1).arbeidsgiverOrgnr();
        var arbeidsforholdId2 = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1).arbeidsforholdId();
        var inntektsmedling2 = lagSvangerskapspengerInntektsmelding(søkerFnr, månedsinntekt2, orgNummer2)
                .medArbeidsforholdId(arbeidsforholdId2);
        innsender.sendInnInnteksmeldingFpfordel(
                List.of(inntektsmedling1, inntektsmedling2),
                søkerFnr,
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setSkalBrukesTilFalseForArbeidsforhold(arbeidsforholdId2);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår.godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        double årsinntekt = Double.valueOf(månedsinntekt1) * 12;
        double utbetalingProsentFaktor = (double) (100 - tilrettelegginsprosent) / 100;
        int beregnetDagsats = regnUtForventetDagsats(månedsinntekt1, tilrettelegginsprosent);
        BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0);
        verifiser(beregningsgrunnlagPeriode.getRedusertPrAar() == (årsinntekt * utbetalingProsentFaktor),
                "Forventer at redusertPrAar er det samme som årsinntekten for den gjeldende arbeidsforhold x utbetalingsgrad");
        verifiser(beregningsgrunnlagPeriode.getDagsats() == beregnetDagsats,
                "Forventer at dagsatsen bare beregnes ut i fra årsinntekten til det ene arbeidsforholdet og dens utbetalingsgrad!");

        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgNummer1, 0),
                "Foventer at hele den utbetalte dagsatsen går til søker!");
    }

    @Test
    @DisplayName("4: Mor kombinert AT/SN søker i to omganger")
    @Description("Mor søker i første omgang bare for AF, hvor AG ønsker full refusjon av innekt over 6G." +
            "To måneder senere sender mor inn ny søknad for SN")
    public void morSøkerFørstForATOgSenereForSNTest() {
        var scenario = opprettTestscenario("511");
        var søkerFnr = scenario.personopplysninger().søkerIdent();
        var orgNr = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr();

        var gjennomsnittFraTreSisteÅreneISigrun =  hentNæringsinntektFraSigrun(scenario, 2018,false);
        var opptjening = OpptjeningErketyper.medEgenNaeringOpptjening(
                false,
                BigDecimal.valueOf(gjennomsnittFraTreSisteÅreneISigrun).toBigInteger(),
                false);
        LocalDate termindato = LocalDate.now().plusMonths(3);
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
        var saksnummer1 = innsender.sendInnSøknad(søkerFnr, søknad1.build());


        var månedsinntekt = scenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp();
        var orgNummer = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr();
        var arbeidsforholdId = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsforholdId();
        var inntektsmedling = lagSvangerskapspengerInntektsmelding(søkerFnr, månedsinntekt, orgNummer)
                .medArbeidsforholdId(arbeidsforholdId)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntekt));
        innsender.sendInnInnteksmeldingFpfordel(List.of(inntektsmedling), søkerFnr, saksnummer1);

        saksbehandler.hentFagsak(saksnummer1);
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår.godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer1, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        int beregnetDagsats = regnUtForventetDagsats(månedsinntekt, tilrettelegginsprosent);
        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0)
                        .getDagsats() == beregnetDagsats,
                "Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgNummer, 100),
                "Foventer at hele den utbetalte dagsatsen går til arbeidsgiver siden de ønsker full refusjon!");

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
        var saksnummer2 = innsender.sendInnSøknad(søkerFnr, søknad2.build());

        saksbehandler.hentFagsak(saksnummer2);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging2 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging2.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging2);

        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår2 = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår2.godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår2);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer2, true);

        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgNummer, 100),
                "Foventer at hele den utbetalte dagsatsen går til arbeidsgiver siden de ønsker full refusjon!");
        var beregningsresultatPerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger().getPerioder();
        verifiser(beregningsresultatPerioder.get(0).getAndeler().size() == 1,
                "Forventer at det er bare en andel for første periode i tilkjent ytelse.");
        verifiser(saksbehandler.sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(beregningsresultatPerioder.get(0), "AT"),
                "Forventer aktivitetsstatus for første andel for første periode er AT.");

        verifiser(beregningsresultatPerioder.get(1).getAndeler().size() == 2,
                "Forventer at det er bare en andel for første periode i tilkjent ytelse.");
        verifiser(saksbehandler.sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(beregningsresultatPerioder.get(1), "AT"),
                "Forventer aktivitetsstatus for første andel for andre periode er AT.");
        verifiser(saksbehandler.sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(beregningsresultatPerioder.get(1), "SN"),
                "Forventer aktivitetsstatus for andre andel for andre periode er SN.");
        List<BeregningsresultatPeriodeAndel> beregningsresultatPeriodeAndeler = saksbehandler
                .hentBeregningsresultatPerioderMedAndelISN();
        verifiser(beregningsresultatPeriodeAndeler.get(0).getRefusjon() == 0,
                "Hele dagsatsen går til AF og dermed ingenting til SN");
        verifiser(beregningsresultatPeriodeAndeler.get(0).getTilSoker() == 0,
                "Hele dagsatsen går til AF og dermed ingenting til SN");
    }

    @Test
    @DisplayName("5: Mor har flere AG og søker fullt uttak for begge AFene")
    @Description("Mor søker ingen tilrettelegging for begge arbeidsforholdene. Begge arbeidsgiverene ønsker 100% reufsjon." +
            "Inntekten i disse to arbeidsforholdene er samlet over 6G hvor fordelingen er 2/3 og 1/3 av inntekten.")
    public void morSøkerIngenTilretteleggingForToArbeidsforholdFullRefusjonTest() {
        var scenario = opprettTestscenario("504");
        var søkerFnr = scenario.personopplysninger().søkerIdent();
        var orgNr1 = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr();
        var orgNr2 = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1).arbeidsgiverOrgnr();
        LocalDate termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0;
        var tilrettelegging1 = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(orgNr1));
        var tilrettelegging2 = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(orgNr2));
        var søknad = SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad(
                BrukerRolle.MOR,
                termindato,
                List.of(tilrettelegging1, tilrettelegging2));
        var saksnummer = innsender.sendInnSøknad(søkerFnr, søknad.build());

        var månedsinntekt1 = scenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp();
        var orgNummer1 = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr();
        var arbeidsforholdId1 = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsforholdId();
        var inntektsmedling1 = lagSvangerskapspengerInntektsmelding(søkerFnr, månedsinntekt1, orgNummer1)
                .medArbeidsforholdId(arbeidsforholdId1)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntekt1));
        var månedsinntekt2 = scenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(1).beløp();
        var orgNummer2 = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1).arbeidsgiverOrgnr();
        var arbeidsforholdId2 = scenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1).arbeidsforholdId();
        var inntektsmedling2 = lagSvangerskapspengerInntektsmelding(søkerFnr, månedsinntekt2, orgNummer2)
                .medArbeidsforholdId(arbeidsforholdId2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntekt2));
        innsender.sendInnInnteksmeldingFpfordel(
                List.of(inntektsmedling1, inntektsmedling2),
                søkerFnr,
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        int beregnetDagsats = regnUtForventetDagsats(månedsinntekt1 + månedsinntekt2, tilrettelegginsprosent);
        BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0);
        verifiser(beregningsgrunnlagPeriode.getDagsats() == beregnetDagsats,
                "Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt!");

        double månedsinntekt = månedsinntekt1 + månedsinntekt2;
        double prosentTilArbeidsgiver1 = (Double.valueOf(månedsinntekt1) / månedsinntekt) * 100;
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgNummer1,
                        prosentTilArbeidsgiver1),
                "Foventer at hele den utbetalte dagsatsen går til søker!");

        double prosentTilArbeidforhold2 = 100 - prosentTilArbeidsgiver1;
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgNummer2,
                        prosentTilArbeidforhold2),
                "Foventer at hele den utbetalte dagsatsen går til søker!");
    }

    private Integer regnUtForventetDagsats(Integer samletMånedsbeløp, Integer tilrettelegginsprosent) {
        double årsinntekt = Double.valueOf(samletMånedsbeløp) * 12;
        double seksG = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getHalvG() * 2 * 6;
        double utbetalingProsentFaktor = (double) (100 - tilrettelegginsprosent) / 100;
        if (årsinntekt > seksG) {
            årsinntekt = seksG;
        }
        return ((int) Math.round((årsinntekt * utbetalingProsentFaktor) / 260));
    }
}
