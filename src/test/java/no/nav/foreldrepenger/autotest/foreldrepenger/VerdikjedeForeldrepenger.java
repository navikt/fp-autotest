package no.nav.foreldrepenger.autotest.foreldrepenger;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.OverføringÅrsak;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.erketyper.RelasjonTilBarnetErketyper;
import no.nav.foreldrepenger.autotest.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravNfp;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerManueltOpprettetRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedFeilutbetalingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderVarigEndringEllerNyoppstartetSNBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse.VurderingAvKlageNfpBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAdopsjonsdokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAleneomsorgBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAnnenForeldreHarRett;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTillegsopplysningerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaUttakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriodeAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.FaktaOmBeregningTilfelle;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.DekningsgradDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.autotest.util.localdate.Virkedager;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FEDREKVOTE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FLERBARNSDAGER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak.ARBEID;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.overføringsperiode;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepenger;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerAdopsjon;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerTermin;
import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
@Tag("verdikjede")
public class VerdikjedeForeldrepenger extends ForeldrepengerTestBase {

    @Test
    @DisplayName("1: Mor automatisk førstegangssøknad fødsel")
    @Description("Mor førstegangssøknad før fdødsel på termin.")
    public void testcase_mor_fødsel() throws Exception {
        var testscenario = opprettTestscenario("501");
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var termindato = LocalDate.now().plusMonths(1);
        var fpStartdatoMor = termindato.minusWeeks(3);

        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, termindato.minusDays(1)),
                uttaksperiode(FORELDREPENGER, termindato, termindato.plusWeeks(15).minusDays(1)),
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, termindato.plusWeeks(15), termindato.plusWeeks(20).minusDays(1)),
                uttaksperiode(FORELDREPENGER, termindato.plusWeeks(20), termindato.plusWeeks(36).minusDays(1)));

        var søknad = lagSøknadForeldrepengerTermin(
                termindato, søkerAktørId, SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medRettigheter(RettigheterErketyper.harAleneOmsorgOgEnerett());

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var inntektBeløp = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummer = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();
        var avvikendeInntekt = inntektBeløp*1.3;
        var inntektsmeldinger = lagInntektsmelding(
                (int)avvikendeInntekt,
                søkerFnr,
                fpStartdatoMor,
                orgNummer)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektBeløp/2));
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        var vurderBeregnetInntektsAvvikBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse
                .leggTilInntekt(inntektBeløp*12, 1L)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.MANUELL_KONTROLL_AV_OM_BRUKER_HAR_ALENEOMSORG);
        var avklarFaktaAleneomsorgBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaAleneomsorgBekreftelse.class);
        avklarFaktaAleneomsorgBekreftelse.bekreftBrukerHarAleneomsorg();
        saksbehandler.bekreftAksjonspunktbekreftelserer(avklarFaktaAleneomsorgBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummer, false);

        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp (dvs = 0)!");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER).getSaldo() == (46 - 31) * 5,
                "Forventer at saldoen for stønadskonton FORELDREPENGER er 75 dager!");
        List<Integer> beregnetDagsats = regnUtForventetDagsatsForPeriode(List.of(inntektBeløp), List.of(100), List.of(false));
        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats() == beregnetDagsats.get(0),
                "Forventer at dagsatsen blir justert ut i fra årsinntekten og utbeatlinsggrad, og IKKE 6G fordi inntekten er under 6G!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(50),
                "Forventer at halve summen utbetales til søker og halve summen til arbeisdgiver pga 50% refusjon!");
    }

    @Test
    @DisplayName("2: Mor selvstendig næringsdrivende, varig endring")
    @Description("Selvstendig næringsdrivende, varig endring")
    public void morSelvstendigNæringsdrivendeTest() throws Exception {
        var testscenario = opprettTestscenario("510");
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();
        var gjennomsnittFraTreSisteÅreneISigrun = (1_000_000 * 3) / 3; // TODO: HARDCODET! Bør hentes fra sigrun i scenario (gjennomsnittet at de tre siste årene)
        BigInteger næringsnntekt = BigDecimal.valueOf(gjennomsnittFraTreSisteÅreneISigrun * 1.30).toBigInteger(); // > 25% avvik
        var opptjening = OpptjeningErketyper.medEgenNaeringOpptjening(false, næringsnntekt, true);
        var søknad = lagSøknadForeldrepengerFødsel(
                fødselsdato, søkerAktørId, SøkersRolle.MOR)
                .medSpesiellOpptjening(opptjening)
                .medMottattDato(fødselsdato.plusWeeks(2));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                søkerAktørId,
                søkerFnr,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING);
        VurderPerioderOpptjeningBekreftelse vurderPerioderOpptjeningBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class);
        vurderPerioderOpptjeningBekreftelse
                .godkjennAllOpptjening()
                .setBegrunnelse("Vurder periode med opptjening begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_VARIG_ENDRET_ELLER_NYOPPSTARTET_NÆRING_SELVSTENDIG_NÆRINGSDRIVENDE);
        VurderVarigEndringEllerNyoppstartetSNBekreftelse vurderVarigEndringEllerNyoppstartetSNBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderVarigEndringEllerNyoppstartetSNBekreftelse.class);
        vurderVarigEndringEllerNyoppstartetSNBekreftelse
                .setErVarigEndretNaering(true)
                .setBruttoBeregningsgrunnlag(næringsnntekt.intValue())
                .setBegrunnelse("Vurder varig endring for selvstendig næringsdrivende begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderVarigEndringEllerNyoppstartetSNBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummer, false);

        Set<Kode> beregningAktivitetStatus = saksbehandler.hentUnikeBeregningAktivitetStatus();
        assertThat(beregningAktivitetStatus.contains(new Kode("AKTIVITET_STATUS", "SN")));
        verifiser(beregningAktivitetStatus.size() == 1, "Forventer bare perioder med aktivitetstatus lik SN");

    }


    @Test
    @DisplayName("3: Mor, sykepenger, kun ytelse, papirsøknad")
    @Description("Mor søker fullt uttak, men søker mer en det hun har rett til.")
    public void morSykepengerKunYtelseTest() throws Exception {
        var testscenario = opprettTestscenario("520");
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnPapirsøknadForeldrepenger(testscenario, false);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.REGISTRER_PAPIRSØKNAD_FORELDREPENGER);
        PapirSoknadForeldrepengerBekreftelse papirSoknadForeldrepengerBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(PapirSoknadForeldrepengerBekreftelse.class);
        var termindato = LocalDate.now().plusWeeks(6);
        var fpStartdatoMor = termindato.minusWeeks(3);
        var fpMottatDato = termindato.minusWeeks(6);
        FordelingDto fordelingDtoMor = new FordelingDto();
        PermisjonPeriodeDto foreldrepengerFørFødsel =
                new PermisjonPeriodeDto(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, termindato.minusDays(1));
        PermisjonPeriodeDto mødrekvote =
                new PermisjonPeriodeDto(MØDREKVOTE, termindato, termindato.plusWeeks(20).minusDays(1));
        fordelingDtoMor.permisjonsPerioder.add(foreldrepengerFørFødsel);
        fordelingDtoMor.permisjonsPerioder.add(mødrekvote);
        papirSoknadForeldrepengerBekreftelse.morSøkerTermin(
                fordelingDtoMor,
                termindato,
                fpMottatDato,
                DekningsgradDto.AATI);
        saksbehandler.bekreftAksjonspunkt(papirSoknadForeldrepengerBekreftelse);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();

        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD);
        AvklarArbeidsforholdBekreftelse avklarArbeidsforholdBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.AVKLAR_TERMINBEKREFTELSE);
        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse
                .setTermindato(termindato)
                .setUtstedtdato(termindato.minusWeeks(10))
                .setAntallBarn(1);
        avklarFaktaTerminBekreftelse.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        // TODO: Assert på riktig grunnlag for opptjening: Sjekk at dette er riktig!
        verifiser(saksbehandler.sjekkOmSykepengerLiggerTilGrunnForOpptjening(),
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som " +
                         "er forut for permisjonen på skjæringstidspunktet!");

        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .leggTilFaktaOmBeregningTilfeller("FASTSETT_BG_KUN_YTELSE")
                .leggTilAndelerYtelse(10000.0, new Kode("", "ARBEIDSTAKER", ""))
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FASTSETT_UTTAKPERIODER);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.avslåAlleManuellePerioderMedPeriodeResultatÅrsak(
                new Kode("IKKE_OPPFYLT_AARSAK", "4002", "§14-9: Ikke stønadsdager igjen på stønadskonto"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerMor, false);

        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");

    }


    @Test
    @DisplayName("4: Far søker resten av fellesperioden og hele fedrekvoten med gradert uttak.")
    @Description("Mor har løpende fagsak med hele mødrekvoten og deler av fellesperioden. Far søker resten av fellesperioden" +
                 "og hele fedrekvoten med gradert uttak. Far har to arbeidsforhold i samme virksomhet, samme org.nr, men ulik" +
                 "arbeidsforholdsID. To inntekstmeldinger sendes inn med refusjon på begge.")
    public void fraSøkerForeldrepengerTest() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("560");

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(23);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(testscenario,
                fødselsdato, fpStartdatoMor, fpStartdatoFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRIST_FORELDREPENGER);
        VurderSoknadsfristForeldrepengerBekreftelse vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(fpStartdatoMor);
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerMor, false);

        /*
         * FAR: Søker med to arbeidsforhold i samme virksomhet, orgn.nr, men med ulik arbeidsforholdID.
         *      Søker utsettelse arbeid og deretter resten av felles perioden og hele fedrekvoten med gradert uttak.
         *      Sender inn 2 IM med ulik arbeidsforholdID og refusjon på begge.
         */
        var identFar = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdFar = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var orgNummerFar = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var fordelingFar = generiskFordeling(
                utsettelsesperiode(ARBEID, fpStartdatoFar, fpStartdatoFar.plusWeeks(3).minusDays(1)),
                graderingsperiodeArbeidstaker(FELLESPERIODE,
                        fpStartdatoFar.plusWeeks(3),
                        fpStartdatoFar.plusWeeks(19).minusDays(1),
                        orgNummerFar,
                        50),
                graderingsperiodeArbeidstaker(FEDREKVOTE,
                        fpStartdatoFar.plusWeeks(19),
                        fpStartdatoFar.plusWeeks(49).minusDays(1),
                        orgNummerFar,
                        50)
        );
        var søknadFar = lagSøknadForeldrepengerFødsel(
                fødselsdato, aktørIdFar, SøkersRolle.FAR)
                .medAnnenForelder(testscenario.getPersonopplysninger().getAnnenPartAktørIdent())
                .medFordeling(fordelingFar);
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var inntektBeløpFar1 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var arbeidsforholdIdFar1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId();
        InntektsmeldingBuilder inntektsmeldingFar1= lagInntektsmelding(
                inntektBeløpFar1,
                identFar,
                fpStartdatoFar,
                orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar1)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektBeløpFar1));
        var inntektBeløpFar2 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(1).getBeløp();
        var arbeidsforholdIdFar2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsforholdId();
        InntektsmeldingBuilder inntektsmeldingFar2= lagInntektsmelding(
                inntektBeløpFar2,
                identFar,
                fpStartdatoFar,
                orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektBeløpFar2));
        fordel.sendInnInntektsmeldinger(
                List.of(inntektsmeldingFar1, inntektsmeldingFar2),
                aktørIdFar,
                identFar,
                saksnummerFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FASTSETT_UTTAKPERIODER);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.avslåAlleManuellePerioderMedPeriodeResultatÅrsak(
                new Kode("IKKE_OPPFYLT_AARSAK", "4050", "§14-13 første ledd bokstav a: Aktivitetskravet arbeid ikke oppfylt"));
        fastsettUttaksperioderManueltBekreftelse.setBegrunnelse("");
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerFar, false);

        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(100),
                "Forventer at hele summen utbetales til arbeidsgiver, og derfor ingenting til søker!");
    }


    @Test
    @DisplayName("5: Far søker fellesperiode og fedrekvote som frilanser.")
    @Description("Mor søker hele mødrekvoten og deler av fellesperiode, happy case. Far søker etter føsdsel og søker noe" +
            "av fellesperioden og hele fedrekvoten. Opplyser at han er frilanser og har frilanserinntekt frem til skærings-" +
            "tidspunktet.")
    public void farSøkerSomFrilanser() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("561");

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(18);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(testscenario,
                fødselsdato, fpStartdatoMor, fpStartdatoFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRIST_FORELDREPENGER);
        VurderSoknadsfristForeldrepengerBekreftelse vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(fpStartdatoMor);
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);
        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerMor, false);

        /*
         * FAR: Søker som FL. Har frilansinntekt frem til, men ikke inklusiv, skjæringstidspunktet.
         *      Søker noe av fellesperioden og deretter hele fedrekvoten
         */
        var identFar = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdFar = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var fordelingFar = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(4).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(4), fpStartdatoFar.plusWeeks(19).minusDays(1))
        );
        var frilansFom = testscenario.getScenariodata().getInntektskomponentModell().getFrilansarbeidsforholdperioder().get(0).getFrilansFom();
        var opptjeningFar = OpptjeningErketyper.medFrilansOpptjening(frilansFom, fpStartdatoFar.minusDays(1));
        var søknadFar = lagSøknadForeldrepengerFødsel(
                fødselsdato, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medSpesiellOpptjening(opptjeningFar);
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);

        verifiser(saksbehandler.sjekkOmDetErFrilansinntektDagenFørSkjæringstidspuktet(),
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype FRILANSER som " +
                        "har frilansinntekt på skjæringstidspunktet!");


        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING);
        VurderPerioderOpptjeningBekreftelse vurderPerioderOpptjeningBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class);
        vurderPerioderOpptjeningBekreftelse.godkjennOpptjening("FRILANS")
                .setBegrunnelse("Godkjenner Aktivitet");
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse.behandleFrilansMottarIkke();
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FASTSETT_UTTAKPERIODER);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.godkjennAlleManuellePerioder(new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerFar, false);

        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");
    }


    @Test
    @DisplayName("6: Far søker foreldrepenger med AF som ikke er avsluttet og mor ikke har rett.")
    @Description("Far søker foreldrepenger med to aktive arbeidsforhold og ett gammelt arbeidsforhold som skulle vært " +
            "avsluttet. Søker en graderingsperiode i en av virksomheten og gjennopptar full deltidsstilling: I dette " +
            "arbeidsforholdet vil AG ha full refusjon i hele perioden. I det andre vil bare ha refusjon i to måneder.")
    public void farSøkerMedToAktiveArbeidsforholdOgEtInaktivtTest() throws Exception {
        var testscenario = opprettTestscenario("570");
        var identFar = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdFar = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var aktørIdMor = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var orgNummerFar1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var stillingsprosent1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsavtaler().get(0).getStillingsprosent();
        var fpStartdatoFar = fødselsdato.plusWeeks(6);
        var fordelingFar = generiskFordeling(
                graderingsperiodeArbeidstaker(FORELDREPENGER,
                        fpStartdatoFar,
                        fpStartdatoFar.plusWeeks(100).minusDays(1),
                        orgNummerFar1,
                        stillingsprosent1)
        );
        var søknadFar = lagSøknadForeldrepengerFødsel(
                fødselsdato, aktørIdFar, SøkersRolle.FAR)
                .medRettigheter(RettigheterErketyper.harIkkeAleneomsorgOgAnnenpartIkkeRett())
                .medFordeling(fordelingFar)
                .medAnnenForelder(aktørIdMor);

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var inntektBeløpFar1 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var arbeidsforholdIdFar1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId();
        InntektsmeldingBuilder inntektsmeldingFar1= lagInntektsmelding(
                inntektBeløpFar1,
                identFar,
                fpStartdatoFar,
                orgNummerFar1)
                .medArbeidsforholdId(arbeidsforholdIdFar1)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektBeløpFar1));
        var inntektBeløpFar2 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(1).getBeløp();
        var orgNummerFar2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr();
        var arbeidsforholdIdFar2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsforholdId();
        var opphørsDatoForRefusjon = fpStartdatoFar.plusMonths(2).minusDays(1);
        InntektsmeldingBuilder inntektsmeldingFar2= lagInntektsmelding(
                inntektBeløpFar2,
                identFar,
                fpStartdatoFar,
                orgNummerFar2)
                .medArbeidsforholdId(arbeidsforholdIdFar2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektBeløpFar2))
                .medRefusjonsOpphordato(opphørsDatoForRefusjon);
        fordel.sendInnInntektsmeldinger(
                List.of(inntektsmeldingFar1, inntektsmeldingFar2),
                aktørIdFar,
                identFar,
                saksnummerFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();

        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD);
        AvklarArbeidsforholdBekreftelse avklarArbeidsforholdBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        var ansettelsesperiodeFom = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(2)
                .getAnsettelsesperiodeFom();
        var tomGyldighetsperiode = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1)
                .getAnsettelsesperiodeFom();
        avklarArbeidsforholdBekreftelse.bekreftArbeidsforholdErOverstyrt(
                "TEST TRANSPORT AS",
                ansettelsesperiodeFom,
                tomGyldighetsperiode.minusDays(1));
        avklarArbeidsforholdBekreftelse.setBegrunnelse("Arbeidsforholdet skulle vært avsluttet");
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.AVKLAR_FAKTA_ANNEN_FORELDER_HAR_RETT_KODE);
        AvklarFaktaAnnenForeldreHarRett avklarFaktaAnnenForeldreHarRett =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaAnnenForeldreHarRett.class);
        avklarFaktaAnnenForeldreHarRett.setAnnenforelderHarRett(false);
        avklarFaktaAnnenForeldreHarRett.setBegrunnelse("Bare far har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FASTSETT_UTTAKPERIODER);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.splitPeriode(
                fpStartdatoFar,
                fpStartdatoFar.plusWeeks(100).minusDays(1),
                fpStartdatoFar.plusWeeks(40).minusDays(1));
        fastsettUttaksperioderManueltBekreftelse.godkjennPeriode(
                fpStartdatoFar,
                fpStartdatoFar.plusWeeks(40).minusDays(1),
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        fastsettUttaksperioderManueltBekreftelse.godkjennAktiviteterOgAvslåResten(
                fpStartdatoFar.plusWeeks(40),
                fpStartdatoFar.plusWeeks(100).minusDays(1),
                List.of(orgNummerFar1));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerFar, false);

        /* VERIFISERINGER */
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER er 0 dager!");

        BeregningsresultatPeriode[] beregningsresultatPerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder();
        verifiser(beregningsresultatPerioder.length == 3, "Forventer 3 forskjelige beregningsresultatsperioder!");
        verifiser(beregningsresultatPerioder[0].getTom().isEqual(opphørsDatoForRefusjon),
                "Forventer at lengden på første peridoe har tom dato som matcher tom dato angitt i IM#2");
        verifiser(beregningsresultatPerioder[1].getTom().isEqual(fpStartdatoFar.plusWeeks(40).minusDays(1)),
                "Forventer den andre periden har en varighet på 40 uker.");

        List<BeregningsresultatPeriodeAndel> andelerForAT1 = saksbehandler.hentBeregningsresultatPeriodeAndelerForArbeidsforhold(orgNummerFar1);
        List<BeregningsresultatPeriodeAndel> andelerForAT2 = saksbehandler.hentBeregningsresultatPeriodeAndelerForArbeidsforhold(orgNummerFar2);
        List<Integer> forventetDagsatsForFørstePeriode = regnUtForventetDagsatsForPeriode(List.of(inntektBeløpFar1, inntektBeløpFar2),
                List.of(40, 100), List.of(true, true));
        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()[0].getDagsats() == sumOfList(forventetDagsatsForFørstePeriode),
                "Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel");
        verifiser(andelerForAT1.get(0).getTilSoker().equals(forventetDagsatsForFørstePeriode.get(0)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");
        verifiser(andelerForAT2.get(0).getRefusjon().equals(forventetDagsatsForFørstePeriode.get(1)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til arbeidsgiver");

        List<Integer> forventetDagsatsForAndrePeriode = regnUtForventetDagsatsForPeriode(List.of(inntektBeløpFar1, inntektBeløpFar2),
                List.of(40, 100), List.of(true, false));
        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()[1].getDagsats() == sumOfList(forventetDagsatsForAndrePeriode),
                "Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel");
        verifiser(andelerForAT1.get(1).getTilSoker().equals(forventetDagsatsForAndrePeriode.get(0)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");
        verifiser(andelerForAT2.get(1).getTilSoker().equals(forventetDagsatsForAndrePeriode.get(1)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");

        List<Integer> forventetDagsatsForTredjePeriode = regnUtForventetDagsatsForPeriode(List.of(inntektBeløpFar1, inntektBeløpFar2),
                List.of(40, 0), List.of(true, false));
        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()[2].getDagsats() == sumOfList(forventetDagsatsForTredjePeriode),
                "Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel");
        verifiser(andelerForAT1.get(2).getTilSoker().equals(forventetDagsatsForTredjePeriode.get(0)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");
        verifiser(andelerForAT2.get(2).getTilSoker().equals(forventetDagsatsForTredjePeriode.get(1)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");
    }


    @Test
    @DisplayName("7: Far ")
    public void FarTestMorSyk() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("562");

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFarOrdinær = fødselsdato.plusWeeks(23);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(testscenario,
                fødselsdato, fpStartdatoMor, fpStartdatoFarOrdinær);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();

        /*
         * FAR: Søker overføring av mødrekvoten og fellesperiode fordi mor er syk innenfor 6 første uker av mødrekvoten.
         */
        var identFar = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdFar = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var fpStartdatoFarEndret = fødselsdato.plusWeeks(4);
        var fordelingFar = generiskFordeling(
                overføringsperiode(OverføringÅrsak.SYKDOM_ANNEN_FORELDER,
                        MØDREKVOTE, fpStartdatoFarEndret, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fpStartdatoFarOrdinær, fpStartdatoFarOrdinær.plusWeeks(15).minusDays(1))
        );
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var søknadFar = lagSøknadForeldrepengerFødsel(
                fødselsdato, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(testscenario.getPersonopplysninger().getAnnenPartAktørIdent())
                .medMottattDato(fødselsdato.plusWeeks(6));
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_FAKTA_UTTAK);
        AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder avklarFaktaUttakPerioder =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class);
        Kode godkjenningskode = saksbehandler.kodeverk.UttakPeriodeVurderingType.getKode("PERIODE_OK");
        avklarFaktaUttakPerioder.godkjennPeriode(
                fpStartdatoFarEndret,
                fødselsdato.plusWeeks(15).minusDays(1),
                godkjenningskode,
                true);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakPerioder);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerFar, false);

        /* Mor: berørt sak */
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        verifiser(saksbehandler.valgtBehandling.hentBehandlingsresultat().equalsIgnoreCase("FORELDREPENGER_ENDRET"),
                "Foreldrepenger skal være endret pga annenpart har overlappende uttak!");

        List<UttakResultatPeriode> avslåttePerioder = saksbehandler.hentAvslåtteUttaksperioder();
        verifiser(avslåttePerioder.size() == 2, "Forventer at det er 2 avslåtte uttaksperioder");
        verifiser(saksbehandler.valgtBehandling.hentUttaksperiode(2).getPeriodeResultatÅrsak().kode.equalsIgnoreCase("4084"),
                "Perioden burde være avslått fordi annenpart har overlappende uttak!");
        verifiser(saksbehandler.valgtBehandling.hentUttaksperiode(3).getPeriodeResultatÅrsak().kode.equalsIgnoreCase("4084"),
                "Perioden burde være avslått fordi annenpart har overlappende uttak!");

        // TODO: Sjekk med simulering! Er ikke støtte for det for øyeblikket. Ukelønn jobber med det.

    }

    @Test
    @DisplayName("8: Mor søker for tvillinger og tar ut hele utvidelsen i fellesperioden.")
    public void MorSøkerFor2BarnHvorHunFårBerørtSakPgaFar() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("512");

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var identMor = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(31);
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fpStartdatoFar.minusDays(1)),
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(17).minusDays(1), true, false)
        );
        var søknadMor = lagSøknadForeldrepenger(
                fødselsdato, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordelingMor)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(2, fødselsdato));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                identMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var inntektBeløpMor = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummerMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var inntektsmeldingMor = lagInntektsmelding(
                inntektBeløpMor,
                identMor,
                fpStartdatoMor,
                orgNummerMor);
        fordel.sendInnInntektsmelding(
                inntektsmeldingMor,
                aktørIdMor,
                identMor,
                saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRIST_FORELDREPENGER);
        VurderSoknadsfristForeldrepengerBekreftelse vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(fpStartdatoMor);
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerMor, false);

        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp!");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(MØDREKVOTE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton MØDREKVOTE er brukt opp!");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FELLESPERIODE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FELLESPERIODE er brukt opp!");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FLERBARNSDAGER).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FLERBARNSDAGER er brukt opp!");

        /*
         * FAR: Søker samtidig uttak med flerbansdager. Søker deretter hele fedrekvoten, også samtidig uttak.
         */
        var identFar = testscenario.getPersonopplysninger().getAnnenpartIdent();
        var aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        var gjennomsnittFraTreSisteÅreneISigrun = (1_000_000 * 3) / 3; // TODO: HARDCODET! Bør hentes fra sigrun i scenario (gjennomsnittet at de tre siste årene)
        var opptjeningFar = OpptjeningErketyper.medEgenNaeringOpptjening(
                LocalDate.now().minusYears(4),
                fpStartdatoFar,
                false,
                BigDecimal.valueOf(gjennomsnittFraTreSisteÅreneISigrun).toBigInteger(),
                false);
        var fordelingFar = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(4).minusDays(1), true, true),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(4), fpStartdatoFar.plusWeeks(17).minusDays(1), false, true),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(17), fpStartdatoFar.plusWeeks(19).minusDays(1))
        );
        var søknadFar = lagSøknadForeldrepenger(
                fødselsdato, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medSpesiellOpptjening(opptjeningFar)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(2, fødselsdato))
                .medAnnenForelder(aktørIdMor);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var inntektBeløpFar = testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummerFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var arbeidsforholdIdFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId();
        var inntektsmeldingFar = lagInntektsmelding(
                inntektBeløpFar,
                identFar,
                fpStartdatoFar,
                orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar);
        fordel.sendInnInntektsmelding(
                inntektsmeldingFar,
                aktørIdFar,
                identFar,
                saksnummerFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();

        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0).kode.equalsIgnoreCase("AT_SN"),
                "Forventer at far får kombinert satus i beregning (da AT og SN)");


        /* Mor: Berørt sak */
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgRevurderingBehandling();
        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FASTSETT_UTTAKPERIODER);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.godkjennPeriode(fpStartdatoFar, fpStartdatoFar.plusWeeks(4).minusDays(1));
        fastsettUttaksperioderManueltBekreftelse.godkjennPeriode(
                fpStartdatoFar,
                fpStartdatoFar.plusWeeks(4).minusDays(1),
                new Kode("INNVILGET_AARSAK", "2038", "§14-10 sjette ledd: Samtidig uttak"),
                true,
                true,
                100);
        fastsettUttaksperioderManueltBekreftelse.splitPeriode(
                fpStartdatoFar.plusWeeks(4),
                fpStartdatoFar.plusWeeks(17).minusDays(1),
                fpStartdatoFar.plusWeeks(13).minusDays(1));
        fastsettUttaksperioderManueltBekreftelse.godkjennPeriode(
                fpStartdatoFar.plusWeeks(4),
                fpStartdatoFar.plusWeeks(13).minusDays(1),
                new Kode("INNVILGET_AARSAK", "2038", "§14-10 sjette ledd: Samtidig uttak"),
                true,
                true,
                100);
        fastsettUttaksperioderManueltBekreftelse.avslåPeriode(
                fpStartdatoFar.plusWeeks(13),
                fpStartdatoFar.plusWeeks(17).minusDays(1),
                new Kode("IKKE_OPPFYLT_AARSAK", "4059",
                        "§14-13 sjette ledd, jf. §14-9 fjerde ledd: Unntak for Aktivitetskravet, flerbarnsfødsel - ikke nok dager")
        );
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerMor, true);
    }

    @Test
    @DisplayName("9: Mor søker med dagpenger som grunnlag, klager, får medhold og revurderes.")
    @Description("Mor søker med dagpenger som grunnlag. Bruker ikke besteberegning. Søker klager på dette. Mor får" +
            "medhold og revurderes. I revurderingen brukes besteberegning og sbh oppgir en verdi høyere månedsinntekt.")
    public void MorSøkerMedDagpengerTest() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("521");
        var identMor = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(31).minusDays(1)));

        var søknadMor = lagSøknadForeldrepengerFødsel(
                fødselsdato, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordelingMor);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                identMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var inntektBeløpMor = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummerMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var inntektsmeldingMor = lagInntektsmelding(
                inntektBeløpMor,
                identMor,
                fpStartdatoMor,
                orgNummerMor);
        fordel.sendInnInntektsmelding(
                inntektsmeldingMor,
                aktørIdMor,
                identMor,
                saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.VURDER_BESTEBEREGNING.kode)
                .leggTilTomBesteBeregningAndeler()
                .setBegrunnelse("Dette ");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerMor, false);

        saksbehandler.refreshBehandling();
        verifiser(saksbehandler.harHistorikkinnslag(HistorikkInnslag.BREV_BESTILT), "Brev er bestillt i førstegangsbehandling");
        BeregningsresultatPeriode[] beregningsresultatPeriodeFørstegangsbehandling = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder();
        verifiser(beregningsresultatPeriodeFørstegangsbehandling.length == 4,
                "Forventer 3 forskjelige beregningsresultatsperioder!");
        verifiser(beregningsresultatPeriodeFørstegangsbehandling[0].getDagsats() == 1_000,
                "Forventer at dagsatsen er satt til dagsatsen for dagpengene.");
        verifiser(beregningsresultatPeriodeFørstegangsbehandling[1].getDagsats() == 1_000,
                "Forventer at dagsatsen er satt til dagsatsen for dagpengene.");
        verifiser(beregningsresultatPeriodeFørstegangsbehandling[2].getDagsats() == 1_000,
                "Forventer at dagsatsen er satt til dagsatsen for dagpengene.");
        verifiser(beregningsresultatPeriodeFørstegangsbehandling[3].getDagsats() == 1_000,
                "Forventer at dagsatsen er satt til dagsatsen for dagpengene.");

        //* KLAGE *//
        fordel.sendInnKlage(null, testscenario, saksnummerMor);

        klagebehandler.erLoggetInnMedRolle(Aktoer.Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(saksnummerMor);
        klagebehandler.ventTilSakHarKlage();
        klagebehandler.velgKlageBehandling();
        klagebehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDERING_AV_FORMKRAV_KLAGE_NFP);
        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("Godkjenner alle formkrav");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        klagebehandler.ventTilAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP);
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse =
                klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftMedholdGunst("PROSESSUELL_FEIL")
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Slik er det bare!");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        klagebehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FORESLÅ_VEDTAK);
        klagebehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummerMor);
        beslutter.velgKlageBehandling();
        beslutter.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FATTER_VEDTAK);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        //* REVURDERING *//
        saksbehandler.opprettBehandlingRevurdering("ETTER_KLAGE");
        saksbehandler.ventTilSakHarRevurdering();
        saksbehandler.velgRevurderingBehandling();

        verifiser(saksbehandler.valgtBehandling.getBehandlingArsaker().get(0).getBehandlingArsakType().kode.equalsIgnoreCase("ETTER_KLAGE"),
                "Foventer at revurderingen har årsakskode ETTER_KLAGE.");

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse2 =
                saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse2
                .leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.VURDER_BESTEBEREGNING.kode)
                .leggTilBesteBeregningAndeler(30_000.0, new Kode("","DAGPENGER", "Dagpenger"))
                .setBegrunnelse("Legger til en beste beregning andel periode som gjør at vi bruker besteberegning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse2);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.KONTROLL_AV_MANUELT_OPPRETTET_REVURDERINGSBEHANDLING);
        KontrollerManueltOpprettetRevurdering kontrollerManueltOpprettetRevurdering =
                saksbehandler.hentAksjonspunktbekreftelse(KontrollerManueltOpprettetRevurdering.class);
        saksbehandler.bekreftAksjonspunkt(kontrollerManueltOpprettetRevurdering);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerMor, true);

        saksbehandler.refreshBehandling();
        BeregningsresultatPeriode[] beregningsresultatPeriodeRevurdering = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder();
        List<Integer> forventetDagsats = regnUtForventetDagsatsForPeriode(List.of(30_000), List.of(100), List.of(false));
        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats() == forventetDagsats.get(0),
                "Forventer at dagsatsen er kalkulert etter beløpet gitt i besteberegning!");
        verifiser(beregningsresultatPeriodeRevurdering.length == 4,
                "Forventer 3 forskjelige beregningsresultatsperioder!");
        verifiser(beregningsresultatPeriodeRevurdering[0].getDagsats() == forventetDagsats.get(0),
                "Forventer at dagsatsen er satt til dagsatsen for dagpengene.");
        verifiser(beregningsresultatPeriodeRevurdering[1].getDagsats() == forventetDagsats.get(0),
                "Forventer at dagsatsen er satt til dagsatsen for dagpengene.");
        verifiser(beregningsresultatPeriodeRevurdering[2].getDagsats() == forventetDagsats.get(0),
                "Forventer at dagsatsen er satt til dagsatsen for dagpengene.");
        verifiser(beregningsresultatPeriodeRevurdering[3].getDagsats() == forventetDagsats.get(0),
                "Forventer at dagsatsen er satt til dagsatsen for dagpengene.");
        verifiser(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen().get(0).kode.equalsIgnoreCase("ENDRING_I_BEREGNING"),
                "Foventer at konsekvens for ytelse er satt til ENDRING_I_BEREGNING.");
        verifiser(saksbehandler.harHistorikkinnslagForBehandling(HistorikkInnslag.BREV_BESTILT, saksbehandler.valgtBehandling.id) == false,
                "Forventer at det ikke sendes et nytt vedtaksbrev i revurderingen");


    }

    @Test
    @DisplayName("10: Far søker adopsjon og får revurdert sak 4 måneder senere på grunn av IM med endring i refusjon.")
    @Description("Far søker adopsjon og får revurdert sak 4 måneder senere på grunn av IM med endring i refusjon. " +
            "AG ber om full refusjon, men kommer for sent til å få alt. AG får refusjon for den inneværende måneden " +
            "og tre måneder tilbake i tid; tiden før dette skal gå til søker.")
    public void FarSøkerAdopsjon() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("563");

        /* FAR */
        var identFar = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdFar = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var omsorgsovertakelsedatoe = LocalDate.now().minusMonths(4);
        var fpStartdatoFar = omsorgsovertakelsedatoe;
        var fellesperiodeStartFar = fpStartdatoFar.plusWeeks(15);
        var fellesperiodeSluttFar = fellesperiodeStartFar.plusWeeks(16).minusDays(1);

        var fordelingFar = generiskFordeling(
                uttaksperiode(FEDREKVOTE, fpStartdatoFar, fellesperiodeStartFar.minusDays(1)),
                uttaksperiode(FELLESPERIODE, fellesperiodeStartFar, fellesperiodeSluttFar)
        );

        var søknadFar = lagSøknadForeldrepengerAdopsjon(
                omsorgsovertakelsedatoe, aktørIdFar, SøkersRolle.FAR, false)
                .medFordeling(fordelingFar);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.ADOPSJONSSOKNAD_FORELDREPENGER);

        var inntektBeløpFar = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummerFar = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var arbeidsforholdIdFar = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId();
        var inntektsmeldingFar = lagInntektsmelding(
                inntektBeløpFar,
                identFar,
                fpStartdatoFar,
                orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar);
        fordel.sendInnInntektsmelding(
                inntektsmeldingFar,
                aktørIdFar,
                identFar,
                saksnummerFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_TILLEGGSOPPLYSNINGER);
        AvklarFaktaTillegsopplysningerBekreftelse avklarFaktaTillegsopplysningerBekreftelseFar
                = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaTillegsopplysningerBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTillegsopplysningerBekreftelseFar);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_ADOPSJONSDOKUMENTAJON);
        AvklarFaktaAdopsjonsdokumentasjonBekreftelse avklarFaktaAdopsjonsdokumentasjonBekreftelseFar =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        avklarFaktaAdopsjonsdokumentasjonBekreftelseFar.setBarnetsAnkomstTilNorgeDato(omsorgsovertakelsedatoe);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);

        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRIST_FORELDREPENGER);
        VurderSoknadsfristForeldrepengerBekreftelse vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(fpStartdatoFar);
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_UTTAKPERIODER);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.godkjennAlleManuellePerioder(
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerFar, false);

        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().length == 2,
                "Forventer at det er to perioder i tilkjent ytelse. En for fedrekvote og en for fellesperioden");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");

        // AG sender inn en IM med endring i refusjon som skal føre til revurdering på far sin sak
        HashMap<LocalDate, BigDecimal> endringRefusjonMap = new HashMap<>();
        endringRefusjonMap.put(fpStartdatoFar, BigDecimal.valueOf(inntektBeløpFar));
        var inntektsmeldingEndringFar = lagInntektsmelding(
                inntektBeløpFar,
                identFar,
                fpStartdatoFar,
                orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektBeløpFar))
                .medEndringIRefusjonslist(endringRefusjonMap);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        fordel.sendInnInntektsmelding(
                inntektsmeldingEndringFar,
                aktørIdFar,
                identFar,
                saksnummerFar);

        // Revurdering / Berørt sak til far
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.ventTilSakHarRevurdering();
        saksbehandler.velgRevurderingBehandling();

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .leggTilFaktaOmBeregningTilfeller("VURDER_REFUSJONSKRAV_SOM_HAR_KOMMET_FOR_SENT")
                .leggTilRefusjonGyldighetVurdering(orgNummerFar, false)
                .setBegrunnelse("Refusjonskrav er sendt inn for sent!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_UTTAKPERIODER);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelseRevurdering =
                saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelseRevurdering.godkjennAlleManuellePerioder(
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelseRevurdering);


        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_TILBAKREVING_AV_FEILUTBETALING);
        VurderTilbakekrevingVedFeilutbetalingBekreftelse vurderTilbakekrevingVedFeilutbetalingBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(VurderTilbakekrevingVedFeilutbetalingBekreftelse.class);
        vurderTilbakekrevingVedFeilutbetalingBekreftelse.setHindreTilbaketrekk(true);
        vurderTilbakekrevingVedFeilutbetalingBekreftelse.setBegrunnelse("AG ber om refusjon for sent til å få alt!");
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedFeilutbetalingBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerFar, true);

        BeregningsresultatPeriode[] resultatPerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder();
        verifiser(resultatPerioder.length == 3,
                "Foventer at den berørte saken har 3 tilkjent ytelse peridoer, og ikke 2 som i førstegangsbehandling!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(resultatPerioder[0], 0),
                "Forventer at hele summen utbetales til søker i første periode, og derfor ingenting til arbeidsgiver!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(resultatPerioder[1], 100),
                "Forventer at hele summen utbetales til AG i andre periode, og derfor ingenting til søker!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(resultatPerioder[2], 100),
                "Forventer at hele summen utbetales til AG i tredje periode, og derfor ingenting til søker!");
    }

    @Test
    @Disabled
    @DisplayName("11: Far søker adopsjon hvor han søker hele fedrekvoten og fellesperiode, og får berørt sak pga mor")
    @Description("Far søker adopsjon hvor han søker hele fedrekvoten og fellesperioden. Mor søker noe av mødrekvoten midt " +
            "midt i fars periode med fullt uttak. Deretter søker more 9 uker av fellesperiode med samtidig uttak. Far får " +
            "berørt sak hvor hanf år avkortet fellesperidoen på slutten og redusert perioder hvor mor søker samtidig uttak")
    public void FarSøkerAdopsjonOgMorMødrekvoteMidtIFarsOgDeretterSamtidigUttakAvFellesperidoe() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("563");

        /* FAR */
        var identFar = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdFar = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var omsorgsovertakelsedatoe = LocalDate.now().minusWeeks(4);
        var fpStartdatoFar = omsorgsovertakelsedatoe;
        var fellesperiodeStartFar = fpStartdatoFar.plusWeeks(15);
        var fellesperiodeSluttFar = fellesperiodeStartFar.plusWeeks(16).minusDays(1);

        var fordelingFar = generiskFordeling(
                uttaksperiode(FEDREKVOTE, fpStartdatoFar, fellesperiodeStartFar.minusDays(1)),
                uttaksperiode(FELLESPERIODE, fellesperiodeStartFar, fellesperiodeSluttFar)
        );

        var søknadFar = lagSøknadForeldrepengerAdopsjon(
                omsorgsovertakelsedatoe, aktørIdFar, SøkersRolle.FAR, false)
                .medFordeling(fordelingFar);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.ADOPSJONSSOKNAD_FORELDREPENGER);

        var inntektBeløpFar = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummerFar = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var arbeidsforholdIdFar = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId();
        var inntektsmeldingFar = lagInntektsmelding(
                inntektBeløpFar,
                identFar,
                fpStartdatoFar,
                orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar);
        fordel.sendInnInntektsmelding(
                inntektsmeldingFar,
                aktørIdFar,
                identFar,
                saksnummerFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_TILLEGGSOPPLYSNINGER);
        AvklarFaktaTillegsopplysningerBekreftelse avklarFaktaTillegsopplysningerBekreftelseFar
                = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaTillegsopplysningerBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTillegsopplysningerBekreftelseFar);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_ADOPSJONSDOKUMENTAJON);
        AvklarFaktaAdopsjonsdokumentasjonBekreftelse avklarFaktaAdopsjonsdokumentasjonBekreftelseFar =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        avklarFaktaAdopsjonsdokumentasjonBekreftelseFar.setBarnetsAnkomstTilNorgeDato(omsorgsovertakelsedatoe);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_UTTAKPERIODER);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.godkjennAlleManuellePerioder(
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerFar, false);

        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().length == 2,
                "Forventer at det er to perioder i tilkjent ytelse. En for fedrekvote og en for fellesperioden");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");


        /* MOR */
        var identMor = testscenario.getPersonopplysninger().getAnnenpartIdent();
        var aktørIdMor = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        var fpStartdatoMor = fpStartdatoFar.plusWeeks(7);
        var fellesperiodeStartMor = fpStartdatoMor.plusWeeks(4);
        var fellesperiodeSluttMor = fellesperiodeStartMor.plusWeeks(9).minusDays(1);

        var fordelingMor = generiskFordeling(
                uttaksperiode(MØDREKVOTE, fpStartdatoMor, fellesperiodeStartMor.minusDays(1), false, false),
                uttaksperiode(FELLESPERIODE, fellesperiodeStartMor, fellesperiodeSluttMor, false, true, 40)
        );

        var søknadMor = lagSøknadForeldrepengerAdopsjon(
                omsorgsovertakelsedatoe, aktørIdMor, SøkersRolle.MOR, false)
                .medFordeling(fordelingMor)
                .medAnnenForelder(aktørIdFar);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                identMor,
                DokumenttypeId.ADOPSJONSSOKNAD_FORELDREPENGER);

        var inntektBeløpMor = testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummerMor = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var arbeidsforholdIdMor = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId();
        var inntektsmeldingMor = lagInntektsmelding(
                inntektBeløpMor,
                identMor,
                fpStartdatoFar.plusWeeks(7),
                orgNummerMor)
                .medArbeidsforholdId(arbeidsforholdIdMor);
        fordel.sendInnInntektsmelding(
                inntektsmeldingMor,
                aktørIdMor,
                identMor,
                saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_TILLEGGSOPPLYSNINGER);
        AvklarFaktaTillegsopplysningerBekreftelse avklarFaktaTillegsopplysningerBekreftelseMor
                = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaTillegsopplysningerBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTillegsopplysningerBekreftelseMor);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_ADOPSJONSDOKUMENTAJON);
        AvklarFaktaAdopsjonsdokumentasjonBekreftelse avklarFaktaAdopsjonsdokumentasjonBekreftelseMor =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        avklarFaktaAdopsjonsdokumentasjonBekreftelseMor.setBarnetsAnkomstTilNorgeDato(omsorgsovertakelsedatoe);
        // TODO: Finn ut hvorfor denne fører til prosesstaks i vrang tilstand.
        //  det vil si "Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect)
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseMor);

        saksbehandler.ventTilAvsluttetBehandling();

        /* FAR: Berørt behandling */
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgRevurderingBehandling();

        List<UttakResultatPeriode> avslåttePerioder = saksbehandler.hentAvslåtteUttaksperioder();
        verifiser(avslåttePerioder.size() == 1,
                "Forventer at det er 1 avslåtte uttaksperioder (automatisk avslått)");
        verifiser(saksbehandler.valgtBehandling.hentUttaksperiode(1).getPeriodeResultatÅrsak().kode.equalsIgnoreCase("4084"),
                "Perioden burde være avslått fordi annenpart tar ut mødrekovte med 100% utbetalingsgrad samtidig!");


        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_UTTAKPERIODER);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelseMor =
                saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelseMor.godkjennPeriode(
                fellesperiodeStartMor,
                fellesperiodeStartFar.minusDays(1),
                new Kode("INNVILGET_AARSAK", "2038", "§14-10 sjette ledd: Samtidig uttak"),
                false,
                true,
                100);
        fastsettUttaksperioderManueltBekreftelseMor.godkjennPeriode(
                fellesperiodeStartFar,
                fellesperiodeSluttMor,
                new Kode("INNVILGET_AARSAK", "2038", "§14-10 sjette ledd: Samtidig uttak"),
                false,
                true,
                60);
        var saldoer = saksbehandler.hentSaldoerGittUttaksperioder(fastsettUttaksperioderManueltBekreftelseMor.getPerioder());
        var disponibleFellesdager = saldoer.getStonadskontoer().get(FELLESPERIODE).getSaldo();
        var sluttenAvFørstePeriode = Virkedager.minusVirkedager(fellesperiodeSluttFar, Math.abs(disponibleFellesdager));
        fastsettUttaksperioderManueltBekreftelseMor.splitPeriode(
                fellesperiodeSluttMor.plusDays(1),
                fellesperiodeSluttFar,
                sluttenAvFørstePeriode);
        fastsettUttaksperioderManueltBekreftelseMor.godkjennPeriode(
                fellesperiodeSluttMor.plusDays(1),
                sluttenAvFørstePeriode,
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        fastsettUttaksperioderManueltBekreftelseMor.avslåPeriode(
                sluttenAvFørstePeriode.plusDays(1),
                fellesperiodeSluttFar,
                new Kode("IKKE_OPPFYLT_AARSAK", "4002", "§14-9: Ikke stønadsdager igjen på stønadskonto"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelseMor);

        foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(saksnummerFar, true);
    }




    private Long sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(TestscenarioDto testscenario,
                                                                                        LocalDate fødselsdato,
                                                                                        LocalDate fpStartdatoMor,
                                                                                        LocalDate fpStartdatoFar) throws Exception {
        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var identMor = testscenario.getPersonopplysninger().getAnnenpartIdent();
        var aktørIdMor = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fpStartdatoFar.minusDays(1))
        );
        var søknadMor = lagSøknadForeldrepengerFødsel(
                fødselsdato, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordelingMor);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                identMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var inntektBeløpMor = testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummerMor = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var inntektsmeldingMor = lagInntektsmelding(
                inntektBeløpMor,
                identMor,
                fpStartdatoMor,
                orgNummerMor);
        fordel.sendInnInntektsmelding(
                inntektsmeldingMor,
                aktørIdMor,
                identMor,
                saksnummerMor);

        return saksnummerMor;
    }
    private void foreslårVedtakFatterVedtakOgVenterTilAvsluttetBehandling(long saksnummer, boolean revurdering) throws Exception {
        saksbehandler.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FORESLÅ_VEDTAK);
        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        if ( beslutter.harRevurderingBehandling() && revurdering ) {
            beslutter.velgRevurderingBehandling();
        }
        beslutter.ventTilAksjonspunktSomKanLøses(AksjonspunktKoder.FATTER_VEDTAK);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }
    private List<Integer> regnUtForventetDagsatsForPeriode(List<Integer> månedsinntekter, List<Integer> utbetalingsgraderForPeriode,
                                                           List<Boolean> refusjonerForPeriode) {
        List<Integer> dagsatser = Arrays.asList(new Integer[månedsinntekter.size()]);
        double årsinntekt = sumOfList(månedsinntekter).doubleValue() * 12;
        if ( refusjonerForPeriode.contains(true) ) {
            double sumAvÅrsinntektTilRefusjon = 0;
            double sumAvÅrsinntektDirekteTilSøker = 0;
            for (int i=0; i < månedsinntekter.size(); i++) {
                if ( refusjonerForPeriode.get(i) ){
                    sumAvÅrsinntektTilRefusjon += månedsinntekter.get(i).doubleValue() * 12;
                } else {
                    sumAvÅrsinntektDirekteTilSøker += månedsinntekter.get(i).doubleValue() * 12;
                }
            }

            double redusertSumAvÅrsinntektTilRefusjon = justerÅrsinntekt(sumAvÅrsinntektTilRefusjon);
            for (int i=0; i < månedsinntekter.size(); i++) {
                if ( refusjonerForPeriode.get(i) ){
                    double fordeling = månedsinntekter.get(i).doubleValue() * 12 / sumAvÅrsinntektTilRefusjon;
                    dagsatser.set(i, (int) Math.round(redusertSumAvÅrsinntektTilRefusjon  / 260 * fordeling * utbetalingsgraderForPeriode.get(i).doubleValue() / 100));
                }
            }

            double resterendeÅrsinntekt = justerÅrsinntekt(årsinntekt);
            resterendeÅrsinntekt -= redusertSumAvÅrsinntektTilRefusjon;
            for (int i=0; i < månedsinntekter.size(); i++) {
                if ( dagsatser.get(i) == null ) {
                    double fordeling = månedsinntekter.get(i).doubleValue() * 12 / sumAvÅrsinntektDirekteTilSøker;
                    dagsatser.set(i, (int) Math.round(resterendeÅrsinntekt / 260 * fordeling * utbetalingsgraderForPeriode.get(i).doubleValue() / 100));
                }
            }
        } else {
            double redusertÅrsinntekt = justerÅrsinntekt(årsinntekt);
            for (int i=0; i < månedsinntekter.size(); i++) {
                if ( dagsatser.get(i) == null ) {
                    double fordeling = månedsinntekter.get(i).doubleValue() * 12 / årsinntekt;
                    dagsatser.set(i, (int) Math.round(redusertÅrsinntekt / 260 * fordeling * utbetalingsgraderForPeriode.get(i).doubleValue() / 100));
                }
            }
        }

        return dagsatser;
    }
    private Double justerÅrsinntekt(Double opprinneligÅrsinntekt) {
        double seksG = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getHalvG() * 2 * 6;
        if ( opprinneligÅrsinntekt > seksG ) {
            return seksG;
        }
        return opprinneligÅrsinntekt;
    }
    private Integer sumOfList(List<Integer> list) {
        int sum = 0;
        for (int i : list) {
            sum += i;
        }
        return sum;
    }
}
