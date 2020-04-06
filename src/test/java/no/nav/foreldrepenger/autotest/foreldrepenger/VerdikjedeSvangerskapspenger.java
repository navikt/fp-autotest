package no.nav.foreldrepenger.autotest.foreldrepenger;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.autotest.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.erketyper.TilretteleggingsErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.Arbeidsforhold;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingSvangerskapspengerErketyper.lagSvangerskapspengerInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadSvangerskapspengerErketype.lagSvangerskapspengerSøknad;

@Execution(ExecutionMode.CONCURRENT)
@Tag("verdikjedeSVP")
public class VerdikjedeSvangerskapspenger extends ForeldrepengerTestBase {

    @Test
    @DisplayName("1: Mor søker fullt uttak med inntekt under 6G")
    @Description("Mor søker ingen tilrettelegging for en 100% stilling med inntekt over 6G.")
    public void morSøkerIngenTilretteleggingInntektOver6GTest() throws Exception {
        var testscenario = opprettTestscenario("500");
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();

        var orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var tilrettelegginsprosent = 0;
        LocalDate termindato = LocalDate.now().plusMonths(3);
        var tilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                termindato.minusMonths(3),
                termindato.minusMonths(3),
                ArbeidsforholdErketyper.virksomhet(orgNr)
        );

        var søknad = lagSvangerskapspengerSøknad(
                søkerAktørId,
                SøkersRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                søkerAktørId,
                søkerFnr,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);


        var inntektBeløp = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummer = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var inntektsmedling = lagSvangerskapspengerInntektsmelding(
                søkerFnr,
                inntektBeløp,
                orgNummer);
        fordel.sendInnInntektsmelding(
                inntektsmedling,
                søkerAktørId,
                søkerFnr,
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_FØDSEL_OG_TILRETTELEGGING);
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);


        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.BEKREFT_SVANGERSKAPSPENGER_VILKÅR);
        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår =
                saksbehandler.hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårFatterVedtakMedDefaultGodkjenningOgVenterTilAvsluttetBehandling(saksnummer);

        int beregnetDagsats = regnUtForventetDagsats(inntektBeløp, tilrettelegginsprosent);
        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats() == beregnetDagsats,
                "Forventer at dagsatsen beregnes ut i fra årsinntekten og 100% utbetalingsgrad!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPart(0),
                "Foventer at hele utbetalte dagsatsen går til søker!");

    }


    @Test
    @DisplayName("2: Mor søker gradert uttak med inntekt over 6G")
    @Description("Mor søker delvis tilrettelegging for en 100% stilling hvor hun har inntekt over 6G.")
    public void morSøkerDelvisTilretteleggingMedInntektOver6GTest() throws Exception {
        var testscenario = opprettTestscenario("502");
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();

        var orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var tilrettelegginsprosent = 40;
        LocalDate termindato = LocalDate.now().plusMonths(3);
        var tilrettelegging = TilretteleggingsErketyper.delvisTilrettelegging(
                termindato.minusMonths(3),
                termindato.minusMonths(3),
                ArbeidsforholdErketyper.virksomhet(orgNr),
                BigDecimal.valueOf(tilrettelegginsprosent)
        );

        var søknad = lagSvangerskapspengerSøknad(
                søkerAktørId,
                SøkersRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                søkerAktørId,
                søkerFnr,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);


        var inntektBeløp = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummer = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var inntektsmedling = lagSvangerskapspengerInntektsmelding(
                søkerFnr,
                inntektBeløp,
                orgNummer);
        fordel.sendInnInntektsmelding(
                inntektsmedling,
                søkerAktørId,
                søkerFnr,
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_FØDSEL_OG_TILRETTELEGGING);
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);


        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.BEKREFT_SVANGERSKAPSPENGER_VILKÅR);
        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår =
                saksbehandler.hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårFatterVedtakMedDefaultGodkjenningOgVenterTilAvsluttetBehandling(saksnummer);

        int beregnetDagsats = regnUtForventetDagsats(inntektBeløp, tilrettelegginsprosent);
        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats() == beregnetDagsats,
                "Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPart(0),
                "Foventer at hele utbetalte dagsatsen går til søker!");

    }

    @Test
    @DisplayName("3: Mor søk fullt uttak for ett av to arbeidsforhold i samme virksomhet")
    @Description("Mor søker ingen tilrettelegging for ett av to arbeidsforhold i samme virksomhet. Arbeidsgiver leverer to" +
                "inntektsmeldinger med forskjellig arbeidsforholdID, med ulik lønn.")
    public void morSøkerFulltUttakForEttAvToArbeidsforholdTest() throws Exception {
        var testscenario = opprettTestscenario("503");
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();

        var orgNr1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0;
        var tilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                termindato.minusMonths(3),
                termindato.minusMonths(3),
                ArbeidsforholdErketyper.virksomhet(orgNr1)
        );

        var søknad = lagSvangerskapspengerSøknad(
                søkerAktørId,
                SøkersRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                søkerAktørId,
                søkerFnr,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        var inntektBeløp1 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummer1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var arbeidsforholdId1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId();
        var inntektsmedling1 = lagSvangerskapspengerInntektsmelding(
                søkerFnr,
                inntektBeløp1,
                orgNummer1)
                .medArbeidsforholdId(arbeidsforholdId1);
        var inntektBeløp2 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(1).getBeløp();
        var orgNummer2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr();
        var arbeidsforholdId2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsforholdId();
        var inntektsmedling2 = lagSvangerskapspengerInntektsmelding(
                søkerFnr,
                inntektBeløp2,
                orgNummer2)
                .medArbeidsforholdId(arbeidsforholdId2);
        fordel.sendInnInntektsmeldinger(
                List.of(inntektsmedling1, inntektsmedling2),
                søkerAktørId,
                søkerFnr,
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_FØDSEL_OG_TILRETTELEGGING);
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setSkalBrukesTilFalsePåArbeidsfoholdResteTrue(arbeidsforholdId2);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.BEKREFT_SVANGERSKAPSPENGER_VILKÅR);
        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår =
                saksbehandler.hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår.godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårFatterVedtakMedDefaultGodkjenningOgVenterTilAvsluttetBehandling(saksnummer);

        double årsinntekt = Double.valueOf(inntektBeløp1) * 12;
        double utbetalingProsentFaktor = (double) (100 - tilrettelegginsprosent) /100;
        int beregnetDagsats = regnUtForventetDagsats(inntektBeløp1, tilrettelegginsprosent);
        BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0);
        verifiser(beregningsgrunnlagPeriode.getRedusertPrAar() == årsinntekt * utbetalingProsentFaktor,
                "Forventer at redusertPrAar er det samme som årsinntekten for den gjeldende arbeidsforhold x utbetalingsgrad");
        verifiser(beregningsgrunnlagPeriode.getDagsats() == beregnetDagsats,
                "Forventer at dagsatsen bare beregnes ut i fra årsinntekten til det ene arbeidsforholdet og dens utbetalingsgrad!");

        var internArbeidforholdId = hentInternArbeidsforholdIdVedHjelpAvEkstern(avklarFaktaFødselOgTilrettelegging, arbeidsforholdId1);
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiver(internArbeidforholdId, 0),
                "Foventer at hele den utbetalte dagsatsen går til søker!");
    }


    @Test
    @DisplayName("4: Mor kombinert AT/SN søker i to omganger")
    @Description("Mor søker i første omgang bare for AT, hvor AG ønsker full refusjon av innekt over 6G." +
                "To måneder senere sender mor inn ny søknad for SN")
    public void morSøkerFørstForATOgSenereForSNTest() throws Exception {
        var testscenario = opprettTestscenario("511");
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();
        var orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0;
        var tilrettelegging1 = TilretteleggingsErketyper.ingenTilrettelegging(
                termindato.minusMonths(5),
                termindato.minusMonths(5),
                ArbeidsforholdErketyper.virksomhet(orgNr)
        );
        var søknad1 = lagSvangerskapspengerSøknad(
                søkerAktørId,
                SøkersRolle.MOR,
                termindato,
                List.of(tilrettelegging1));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummer1 = fordel.sendInnSøknad(
                søknad1.build(),
                søkerAktørId,
                søkerFnr,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        var inntektBeløp = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummer = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var arbeidsforholdId = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId();
        var inntektsmedling = lagSvangerskapspengerInntektsmelding(
                søkerFnr,
                inntektBeløp,
                orgNummer)
                .medArbeidsforholdId(arbeidsforholdId)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektBeløp));
        fordel.sendInnInntektsmeldinger(
                List.of(inntektsmedling),
                søkerAktørId,
                søkerFnr,
                saksnummer1);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer1);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_FØDSEL_OG_TILRETTELEGGING);
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.BEKREFT_SVANGERSKAPSPENGER_VILKÅR);
        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår =
                saksbehandler.hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår.godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårFatterVedtakMedDefaultGodkjenningOgVenterTilAvsluttetBehandling(saksnummer1);

        saksbehandler.ventTilAvsluttetBehandling();

        int beregnetDagsats = regnUtForventetDagsats(inntektBeløp, tilrettelegginsprosent);
        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats() == beregnetDagsats,
                "Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt!");
        var internArbeidforholdId = hentInternArbeidsforholdIdVedHjelpAvEkstern(avklarFaktaFødselOgTilrettelegging, arbeidsforholdId);
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiver(internArbeidforholdId, 100),
                "Foventer at hele den utbetalte dagsatsen går til arbeidsgiver siden de ønsker full refusjon!");



        /* SØKNAD 2 */
        var gjennomsnittFraTreSisteÅreneISigrun = (1_000_000 * 3) / 3; // TODO: HARDCODET! Bør hentes fra sigrun i scenario (gjennomsnittet at de tre siste årene)
        var opptjening = OpptjeningErketyper.medEgenNaeringOpptjening(
                false,
                BigDecimal.valueOf(gjennomsnittFraTreSisteÅreneISigrun).toBigInteger(),
                false);
        var tilrettelegging2 = TilretteleggingsErketyper.ingenTilrettelegging(
                termindato.minusMonths(3),
                termindato.minusMonths(3),
                ArbeidsforholdErketyper.selvstendigNæringsdrivende()
        );
        var søknad2 = lagSvangerskapspengerSøknad(
                søkerAktørId,
                SøkersRolle.MOR,
                termindato,
                List.of(tilrettelegging2))
                .medSpesiellOpptjening(opptjening);
        var saksnummer2 = fordel.sendInnSøknad(
                søknad2.build(),
                søkerAktørId,
                søkerFnr,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER,
                saksnummer1);

        saksbehandler.hentFagsak(saksnummer2);
        saksbehandler.ventTilSakHarRevurdering();
        saksbehandler.velgRevurderingBehandling();

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_FØDSEL_OG_TILRETTELEGGING);
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging2 =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging2.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging2);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.BEKREFT_SVANGERSKAPSPENGER_VILKÅR);
        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår2 =
                saksbehandler.hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår2.godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår2);

        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FORESLÅ_VEDTAK);
        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer2);
        beslutter.velgRevurderingBehandling(); // UNIKT FOR DENNE!
        beslutter.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FATTER_VEDTAK);
        FatterVedtakBekreftelse bekreftelse2 = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse2.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse2);

        saksbehandler.ventTilAvsluttetBehandling();

        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiver(internArbeidforholdId, 100),
                "Foventer at hele den utbetalte dagsatsen går til arbeidsgiver siden de ønsker full refusjon!");
    }

    @Test
    @DisplayName("5: Mor har flere AG og søker fullt uttak for begge AFene")
    @Description("Mor søker inten tilrettelegging for begge arbeidsforholdene. Begge arbeidsgiverene ønsker 100% reufsjon." +
                "Inntekten i disse to arbeidsforholdne er samlet over 6G hvor fordelingen er 2/3 og 1/3 av inntekten.")
    public void morSøkerIngenTilretteleggingForToArbeidsforholdFullRefusjonTest() throws Exception {
        var testscenario = opprettTestscenario("504");
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();

        var orgNr1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var orgNr2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr();
        LocalDate termindato = LocalDate.now().plusMonths(3);
        var tilrettelegginsprosent = 0;
        var tilrettelegging1 = TilretteleggingsErketyper.ingenTilrettelegging(
                termindato.minusMonths(3),
                termindato.minusMonths(3),
                ArbeidsforholdErketyper.virksomhet(orgNr1));
        var tilrettelegging2 = TilretteleggingsErketyper.ingenTilrettelegging(
                termindato.minusMonths(3),
                termindato.minusMonths(3),
                ArbeidsforholdErketyper.virksomhet(orgNr2));

        var søknad = lagSvangerskapspengerSøknad(
                søkerAktørId,
                SøkersRolle.MOR,
                termindato,
                List.of(tilrettelegging1, tilrettelegging2));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                søkerAktørId,
                søkerFnr,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        var inntektPerMånedForAF1 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummer1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var arbeidsforholdId1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId();
        var inntektsmedling1 = lagSvangerskapspengerInntektsmelding(
                søkerFnr,
                inntektPerMånedForAF1,
                orgNummer1)
                .medArbeidsforholdId(arbeidsforholdId1)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektPerMånedForAF1));
        var inntektPerMånedForAF2 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(1).getBeløp();
        var orgNummer2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr();
        var arbeidsforholdId2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsforholdId();
        var inntektsmedling2 = lagSvangerskapspengerInntektsmelding(
                søkerFnr,
                inntektPerMånedForAF2,
                orgNummer2)
                .medArbeidsforholdId(arbeidsforholdId2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektPerMånedForAF1));
        fordel.sendInnInntektsmeldinger(
                List.of(inntektsmedling1, inntektsmedling2),
                søkerAktørId,
                søkerFnr,
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_FØDSEL_OG_TILRETTELEGGING);
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.BEKREFT_SVANGERSKAPSPENGER_VILKÅR);
        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår =
                saksbehandler.hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårFatterVedtakMedDefaultGodkjenningOgVenterTilAvsluttetBehandling(saksnummer);

        int beregnetDagsats = regnUtForventetDagsats(inntektPerMånedForAF1 + inntektPerMånedForAF2, tilrettelegginsprosent);
        BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0);
        verifiser(beregningsgrunnlagPeriode.getDagsats() == beregnetDagsats,
                "Forventer at dagsatsen blir justert ut i fra 6G og utbeatlinsggrad, og IKKE arbeidstakers årsinntekt!");

        double månedsinntekt = Double.valueOf(inntektPerMånedForAF1 + inntektPerMånedForAF2);
        double prosentTilArbeidsgiver1 = (Double.valueOf(inntektPerMånedForAF1) / månedsinntekt) * 100;
        var internArbeidforholdId1 = hentInternArbeidsforholdIdVedHjelpAvEkstern(avklarFaktaFødselOgTilrettelegging, arbeidsforholdId1);
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiver(internArbeidforholdId1, prosentTilArbeidsgiver1),
                "Foventer at hele den utbetalte dagsatsen går til søker!");

        double prosentTilArbeidforhold2 = 100 - prosentTilArbeidsgiver1;
        var internArbeidsgiver2 = hentInternArbeidsforholdIdVedHjelpAvEkstern(avklarFaktaFødselOgTilrettelegging, arbeidsforholdId2);
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiver(internArbeidsgiver2, prosentTilArbeidforhold2),
                "Foventer at hele den utbetalte dagsatsen går til søker!");
    }


    private void foreslårFatterVedtakMedDefaultGodkjenningOgVenterTilAvsluttetBehandling(long saksnummer) throws Exception {
        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FORESLÅ_VEDTAK);
        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        beslutter.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FATTER_VEDTAK);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        saksbehandler.ventTilAvsluttetBehandling();
    }


    private Integer regnUtForventetDagsats(Integer inntektBeløp, Integer tilrettelegginsprosent) {
        double årsinntekt = Double.valueOf(inntektBeløp) * 12;
        double seksG = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getHalvG() * 2 * 6;
        double utbetalingProsentFaktor = (double) (100 - tilrettelegginsprosent) /100;
        if ( årsinntekt > seksG ) {
            årsinntekt = seksG;
        }
        return ((int) Math.round(årsinntekt * utbetalingProsentFaktor / 260));
    }

    private String hentInternArbeidsforholdIdVedHjelpAvEkstern(AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging, String arbeidsforholdId) {
        return avklarFaktaFødselOgTilrettelegging.getBekreftetSvpArbeidsforholdList().stream()
                .filter(arbeidsforhold -> arbeidsforhold.getEksternArbeidsforholdReferanse().equalsIgnoreCase(arbeidsforholdId))
                .map(Arbeidsforhold::getInternArbeidsforholdReferanse)
                .findFirst()
                .orElseThrow();
    }
}
