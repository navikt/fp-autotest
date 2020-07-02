package no.nav.foreldrepenger.autotest.verdikjedetester;

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
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerAdopsjon;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerTermin;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

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
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettBruttoBeregningsgrunnlagSNBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravNfp;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerManueltOpprettetRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedFeilutbetalingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderVarigEndringEllerNyoppstartetSNBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse.VurderingAvKlageNfpBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAdopsjonsdokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAleneomsorgBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAnnenForeldreHarRett;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaUttakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.VilkarTypeKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatMedUttaksplan;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriodeAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.DekningsgradDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.autotest.util.localdate.Virkedager;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Execution(ExecutionMode.CONCURRENT)
@Tag("verdikjede")
public class VerdikjedeForeldrepenger extends ForeldrepengerTestBase {

    @Test
    @DisplayName("1: Mor automatisk førstegangssøknad termin, aleneomsorg og avvik i beregning")
    @Description("Mor førstegangssøknad før fødsel på termin. Mor har aleneomsorg og enerett. Sender inn IM med over " +
                "25% avvik med delvis refusjon.")
    public void testcase_mor_fødsel() {
        var testscenario = opprettTestscenario("501");
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var termindato = LocalDate.now().plusMonths(1);
        var fpStartdato = termindato.minusWeeks(3);
        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, termindato.minusDays(1)),
                uttaksperiode(FORELDREPENGER, termindato, termindato.plusWeeks(15).minusDays(1)),
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, termindato.plusWeeks(15),
                        termindato.plusWeeks(20).minusDays(1)),
                uttaksperiode(FORELDREPENGER, termindato.plusWeeks(20), termindato.plusWeeks(36).minusDays(1)));
        var søknad = lagSøknadForeldrepengerTermin(termindato, søkerAktørId, SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medRettigheter(RettigheterErketyper.harAleneOmsorgOgEnerett());
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var månedsinntekt = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0)
                .getBeløp();
        var orgNummer = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();
        var avvikendeMånedsinntekt = månedsinntekt * 1.3;
        var inntektsmeldinger = lagInntektsmelding((int) avvikendeMånedsinntekt, søkerFnr, fpStartdato, orgNummer)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntekt * 0.6));
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse
                .leggTilInntekt(månedsinntekt * 12, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        var avklarFaktaAleneomsorgBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAleneomsorgBekreftelse.class);
        avklarFaktaAleneomsorgBekreftelse.bekreftBrukerHarAleneomsorg();
        avklarFaktaAleneomsorgBekreftelse.setBegrunnelse("Bekreftelse sendt fra Autotest.");
        saksbehandler.bekreftAksjonspunktbekreftelserer(avklarFaktaAleneomsorgBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        Saldoer saldoer = saksbehandler.valgtBehandling.getSaldoer();
        verifiser(saldoer.getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp (dvs = 0)!");
        verifiser(saldoer.getStonadskontoer().get(FORELDREPENGER).getSaldo() == 75,
                "Forventer at saldoen for stønadskonton FORELDREPENGER er 75 dager!");
        List<Integer> beregnetDagsats = regnUtForventetDagsatsForPeriode(List.of(månedsinntekt), List.of(100),
                List.of(false));
        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0)
                        .getDagsats() == beregnetDagsats.get(0),
                "Forventer at dagsatsen blir justert ut i fra årsinntekten og utbeatlinsggrad, og IKKE 6G fordi inntekten er under 6G!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(60),
                "Forventer at halve summen utbetales til søker og halve summen til arbeisdgiver pga 60% refusjon!");
    }

    @Test
    @DisplayName("2: Mor selvstendig næringsdrivende, varig endring")
    @Description("Mor er selvstendig næringsdrivende og har ferdiglignet inntekt i mange år. Oppgir en næringsinntekt" +
            "som avviker med mer enn 25% fra de tre siste ferdiglignede årene.")
    public void morSelvstendigNæringsdrivendeTest() {
        var testscenario = opprettTestscenario("510");
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();
        var gjennomsnittFraTreSisteÅreneISigrun = hentNæringsinntektFraSigrun(testscenario, 2018,false);
        BigInteger næringsnntekt = BigDecimal.valueOf(gjennomsnittFraTreSisteÅreneISigrun * 1.80).toBigInteger(); // >25% avvik
        var opptjening = OpptjeningErketyper.medEgenNaeringOpptjening(false, næringsnntekt, true);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørId, SøkersRolle.MOR)
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
        VurderPerioderOpptjeningBekreftelse vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class);
        vurderPerioderOpptjeningBekreftelse.godkjennAllOpptjening();
        vurderPerioderOpptjeningBekreftelse.setBegrunnelse("Opptjening godkjent av Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // Verifiser at aksjonspunkt 5042 ikke blir oprettet uten varig endring
        VurderVarigEndringEllerNyoppstartetSNBekreftelse vurderVarigEndringEllerNyoppstartetSNBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderVarigEndringEllerNyoppstartetSNBekreftelse.class);
        vurderVarigEndringEllerNyoppstartetSNBekreftelse
                .setErVarigEndretNaering(false)
                .setBegrunnelse("Ingen endring");
        saksbehandler.bekreftAksjonspunkt(vurderVarigEndringEllerNyoppstartetSNBekreftelse);
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () ->
                saksbehandler.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_SELVSTENDIG_NÆRINGSDRIVENDE));
        verifiser(runtimeException.getMessage().equalsIgnoreCase("Fant ikke aksjonspunkt med kode 5042"),
                "Har uventet aksjonspunkt: 5042");

        VurderVarigEndringEllerNyoppstartetSNBekreftelse vurderVarigEndringEllerNyoppstartetSNBekreftelse1 =
                saksbehandler.hentAksjonspunktbekreftelse(VurderVarigEndringEllerNyoppstartetSNBekreftelse.class);
        vurderVarigEndringEllerNyoppstartetSNBekreftelse1
                .setErVarigEndretNaering(true)
                .setBegrunnelse("Vurder varig endring for selvstendig næringsdrivende begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderVarigEndringEllerNyoppstartetSNBekreftelse1);
        FastsettBruttoBeregningsgrunnlagSNBekreftelse fastsettBruttoBeregningsgrunnlagSNBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(FastsettBruttoBeregningsgrunnlagSNBekreftelse.class);
        fastsettBruttoBeregningsgrunnlagSNBekreftelse
                .setBruttoBeregningsgrunnlag(næringsnntekt.intValue())
                .setBegrunnelse("Grunnlag begrunnelse");
        saksbehandler.bekreftAksjonspunkt(fastsettBruttoBeregningsgrunnlagSNBekreftelse);

        // verifiser skjæringstidspunkt i følge søknad
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getSkjaeringstidspunktBeregning(),
                fødselsdato.minusWeeks(3));

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        var beregningAktivitetStatus = saksbehandler.hentUnikeBeregningAktivitetStatus();
        verifiser(beregningAktivitetStatus.contains(new Kode("AKTIVITET_STATUS", "SN")),
                "Forventer at søker får utbetaling med status SN!");
        verifiser(beregningAktivitetStatus.size() == 1, "Forventer bare en periode med aktivitetstatus lik SN");

        Saldoer saldoer = saksbehandler.valgtBehandling.getSaldoer();
        verifiser(saldoer.getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp!");
        verifiser(saldoer.getStonadskontoer().get(MØDREKVOTE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton MØDREKVOTE er brukt opp!");
        verifiser(saldoer.getStonadskontoer().get(FELLESPERIODE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FELLESPERIODE er brukt opp!");

        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");
    }

    @Test
    @DisplayName("3: Mor, sykepenger, kun ytelse, papirsøknad")
    @Description("Mor søker fullt uttak, men søker mer enn det hun har rett til.")
    public void morSykepengerKunYtelseTest() {
        var testscenario = opprettTestscenario("520");
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnPapirsøknadForeldrepenger(testscenario, false);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        PapirSoknadForeldrepengerBekreftelse papirSoknadForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(PapirSoknadForeldrepengerBekreftelse.class);
        var termindato = LocalDate.now().plusWeeks(6);
        var fpStartdatoMor = termindato.minusWeeks(3);
        var fpMottatDato = termindato.minusWeeks(6);
        FordelingDto fordelingDtoMor = new FordelingDto();
        PermisjonPeriodeDto foreldrepengerFørFødsel = new PermisjonPeriodeDto(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor,
                termindato.minusDays(1));
        PermisjonPeriodeDto mødrekvote = new PermisjonPeriodeDto(MØDREKVOTE, termindato,
                termindato.plusWeeks(20).minusDays(1));
        fordelingDtoMor.permisjonsPerioder.add(foreldrepengerFørFødsel);
        fordelingDtoMor.permisjonsPerioder.add(mødrekvote);
        papirSoknadForeldrepengerBekreftelse.morSøkerTermin(
                fordelingDtoMor,
                termindato,
                fpMottatDato,
                DekningsgradDto.AATI);
        saksbehandler.bekreftAksjonspunkt(papirSoknadForeldrepengerBekreftelse);

        AvklarArbeidsforholdBekreftelse avklarArbeidsforholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse.setUtstedtdato(termindato.minusWeeks(10));
        avklarFaktaTerminBekreftelse.setBegrunnelse("Begrunnelse fra autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        verifiser(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"),
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som " +
                        "er forut for permisjonen på skjæringstidspunktet!");

        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .leggTilAndelerYtelse(10000.0, saksbehandler.kodeverk.Inntektskategori.getKode("ARBEIDSTAKER"))
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.avslåManuellePerioderMedPeriodeResultatÅrsak(
                new Kode("IKKE_OPPFYLT_AARSAK", "4002", "§14-9: Ikke stønadsdager igjen på stønadskonto"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerMor, false);

        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");

    }

    @Test
    @DisplayName("4: Far søker resten av fellesperioden og hele fedrekvoten med gradert uttak.")
    @Description("Mor har løpende fagsak med hele mødrekvoten og deler av fellesperioden. Far søker resten av fellesperioden" +
            "og hele fedrekvoten med gradert uttak. Far har to arbeidsforhold i samme virksomhet, samme org.nr, men ulik" +
            "arbeidsforholdsID. To inntekstmeldinger sendes inn med refusjon på begge.")
    public void fraSøkerForeldrepengerTest() {
        TestscenarioDto testscenario = opprettTestscenario("560");



        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(23);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(testscenario,
                fødselsdato, fpStartdatoMor, fpStartdatoFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(saksbehandler.valgtBehandling.uuid.toString(), "IKKE_HOY");
        saksbehandler.ventTilAvsluttetBehandling();

        /*
         * FAR: Søker med to arbeidsforhold i samme virksomhet, orgn.nr, men med ulik
         * arbeidsforholdID. Søker utsettelse arbeid og deretter resten av felles
         * perioden og hele fedrekvoten med gradert uttak. Sender inn 2 IM med ulik
         * arbeidsforholdID og refusjon på begge.
         */
        var identFar = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdFar = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var orgNummerFar = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
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
                        50));
        var søknadFar = lagSøknadForeldrepengerFødsel(
                fødselsdato, aktørIdFar, SøkersRolle.FAR)
                        .medAnnenForelder(testscenario.getPersonopplysninger().getAnnenPartAktørIdent())
                        .medFordeling(fordelingFar);
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var månedsinntektFar1 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var arbeidsforholdIdFar1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId();
        InntektsmeldingBuilder inntektsmeldingFar1 = lagInntektsmelding(månedsinntektFar1, identFar, fpStartdatoFar, orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar1)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntektFar1));
        var månedsinntektFar2 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(1).getBeløp();
        var arbeidsforholdIdFar2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsforholdId();
        InntektsmeldingBuilder inntektsmeldingFar2 = lagInntektsmelding(månedsinntektFar2, identFar, fpStartdatoFar, orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntektFar2));
        fordel.sendInnInntektsmeldinger(
                List.of(inntektsmeldingFar1, inntektsmeldingFar2),
                aktørIdFar,
                identFar,
                saksnummerFar);

        saksbehandler.hentFagsak(saksnummerFar);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        /*
         * Fellesperioden skal splittes slik at første periode på 8 uker blir avslått og
         * en andre perioden (av splitten) skal stjele dager fra fedrekvoten. Deretter
         * skal fedrekvoten reduseres med 8 uker. (trenger også en split).
         */
        fastsettUttaksperioderManueltBekreftelse.splitPeriode(
                fpStartdatoFar.plusWeeks(3),
                fpStartdatoFar.plusWeeks(19).minusDays(1),
                fpStartdatoFar.plusWeeks(11).minusDays(1));
        fastsettUttaksperioderManueltBekreftelse.avslåPeriode(
                fpStartdatoFar.plusWeeks(3),
                fpStartdatoFar.plusWeeks(11).minusDays(1),
                new Kode("IKKE_OPPFYLT_AARSAK", "4050",
                        "§14-13 første ledd bokstav a: Aktivitetskravet arbeid ikke oppfylt"));
        fastsettUttaksperioderManueltBekreftelse.innvilgPeriode(
                fpStartdatoFar.plusWeeks(11),
                fpStartdatoFar.plusWeeks(19).minusDays(1),
                new Kode("INNVILGET_AARSAK", "2031", "§14-12, jf. §14-16: Gradering av kvote/overført kvote"),
                FEDREKVOTE);
        fastsettUttaksperioderManueltBekreftelse.splitPeriode(
                fpStartdatoFar.plusWeeks(19),
                fpStartdatoFar.plusWeeks(49).minusDays(1),
                fpStartdatoFar.plusWeeks(41).minusDays(1));
        fastsettUttaksperioderManueltBekreftelse.innvilgPeriode(
                fpStartdatoFar.plusWeeks(19),
                fpStartdatoFar.plusWeeks(41).minusDays(1),
                new Kode("INNVILGET_AARSAK", "2031", "§14-12, jf. §14-16: Gradering av kvote/overført kvote"));
        fastsettUttaksperioderManueltBekreftelse.avslåPeriode(
                fpStartdatoFar.plusWeeks(41),
                fpStartdatoFar.plusWeeks(49).minusDays(1),
                new Kode("IKKE_OPPFYLT_AARSAK", "4002", "§14-9: Ikke stønadsdager igjen på stønadskonto"));
        fastsettUttaksperioderManueltBekreftelse.setBegrunnelse("Begrunnelse fra Autotest.");
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        Saldoer saldoer = saksbehandler.valgtBehandling.getSaldoer();
        verifiser(saldoer.getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp!");
        verifiser(saldoer.getStonadskontoer().get(MØDREKVOTE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton MØDREKVOTE er brukt opp!");
        verifiser(saldoer.getStonadskontoer().get(FEDREKVOTE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FEDREKVOTE er brukt opp!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(100),
                "Forventer at hele summen utbetales til arbeidsgiver, og derfor ingenting til søker!");
    }

    @Test
    @DisplayName("5: Far søker fellesperiode og fedrekvote som frilanser.")
    @Description("Mor søker hele mødrekvoten og deler av fellesperiode, happy case. Far søker etter føsdsel og søker" +
            "noe av fellesperioden og hele fedrekvoten. Opplyser at han er frilanser og har frilanserinntekt frem til" +
            "skjæringstidspunktet.")
    public void farSøkerSomFrilanser() {
        TestscenarioDto testscenario = opprettTestscenario("561");

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(18);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(testscenario,
                fødselsdato, fpStartdatoMor, fpStartdatoFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(saksbehandler.valgtBehandling.uuid.toString(), "IKKE_HOY");
        saksbehandler.ventTilAvsluttetBehandling();

        /*
         * FAR: Søker som FL. Har frilansinntekt frem til, men ikke inklusiv,
         * skjæringstidspunktet. Søker noe av fellesperioden og deretter hele
         * fedrekvoten
         */
        var identFar = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdFar = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var fordelingFar = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(4).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(4), fpStartdatoFar.plusWeeks(19).minusDays(1)));
        var frilansFom = testscenario.getScenariodata().getInntektskomponentModell().getFrilansarbeidsforholdperioder()
                .get(0).getFrilansFom();
        var opptjeningFar = OpptjeningErketyper.medFrilansOpptjening(frilansFom, fpStartdatoFar.minusDays(1));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medSpesiellOpptjening(opptjeningFar);
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.hentFagsak(saksnummerFar);
        VurderPerioderOpptjeningBekreftelse vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class);
        vurderPerioderOpptjeningBekreftelse
                .godkjennOpptjening("FRILANS")
                .setBegrunnelse("Godkjenner Aktivitet");
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        verifiser(saksbehandler.sjekkOmDetErOpptjeningFremTilSkjæringstidspunktet("FRILANS"),
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype FRILANSER som " +
                        "har frilansinntekt på skjæringstidspunktet!");

        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .leggTilMottarYtelseFrilans(false)
                .setBegrunnelse("Begrunnelse fra Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.innvilgManuellePerioder(
                new Kode("INNVILGET", "2037", "§14-9, jf.§14-13: Innvilget fellesperiode til Far"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");
    }

    @Test
    @DisplayName("6: Far søker foreldrepenger med AF som ikke er avsluttet og mor ikke har rett.")
    @Description("Far søker foreldrepenger med to aktive arbeidsforhold og ett gammelt arbeidsforhold som skulle vært " +
            "avsluttet, men er ikke det. Søker en graderingsperiode i en av virksomheten og gjennopptar full " +
            "deltidsstilling: I dette arbeidsforholdet vil AG ha full refusjon i hele perioden. I det andre vil AG " +
            "bare ha refusjon i to måneder.")
    public void farSøkerMedToAktiveArbeidsforholdOgEtInaktivtTest() {
        var testscenario = opprettTestscenario("570");
        var identFar = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdFar = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var aktørIdMor = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var orgNummerFar1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
        var stillingsprosent1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsavtaler().get(0).getStillingsprosent();
        var fpStartdatoFar = fødselsdato.plusWeeks(6);
        var fordelingFar = generiskFordeling(
                graderingsperiodeArbeidstaker(FORELDREPENGER,
                        fpStartdatoFar,
                        fpStartdatoFar.plusWeeks(100).minusDays(1),
                        orgNummerFar1,
                        stillingsprosent1));
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

        var månedsinntektFar1 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0)
                .getBeløp();
        var arbeidsforholdIdFar1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsforholdId();
        InntektsmeldingBuilder inntektsmeldingFar1 = lagInntektsmelding(månedsinntektFar1, identFar, fpStartdatoFar, orgNummerFar1)
                .medArbeidsforholdId(arbeidsforholdIdFar1)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntektFar1));
        var månedsinntektFar2 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(1)
                .getBeløp();
        var orgNummerFar2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1)
                .getArbeidsgiverOrgnr();
        var arbeidsforholdIdFar2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1)
                .getArbeidsforholdId();
        var opphørsDatoForRefusjon = fpStartdatoFar.plusMonths(2).minusDays(1);
        InntektsmeldingBuilder inntektsmeldingFar2 = lagInntektsmelding(månedsinntektFar2, identFar, fpStartdatoFar, orgNummerFar2)
                .medArbeidsforholdId(arbeidsforholdIdFar2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntektFar2))
                .medRefusjonsOpphordato(opphørsDatoForRefusjon);
        fordel.sendInnInntektsmeldinger(
                List.of(inntektsmeldingFar1, inntektsmeldingFar2),
                aktørIdFar,
                identFar,
                saksnummerFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();

        AvklarArbeidsforholdBekreftelse avklarArbeidsforholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        var ansettelsesperiodeFom = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(2)
                .getAnsettelsesperiodeFom();
        var tomGyldighetsperiode = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1)
                .getAnsettelsesperiodeFom();
        avklarArbeidsforholdBekreftelse.bekreftArbeidsforholdErIkkeAktivt(
                "TEST TRANSPORT AS",
                ansettelsesperiodeFom,
                tomGyldighetsperiode.minusDays(1),
                "Arbeidsforholdet skulle vært avsluttet");
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        AvklarFaktaAnnenForeldreHarRett avklarFaktaAnnenForeldreHarRett = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAnnenForeldreHarRett.class);
        avklarFaktaAnnenForeldreHarRett.setAnnenforelderHarRett(false);
        avklarFaktaAnnenForeldreHarRett.setBegrunnelse("Bare far har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.splitPeriode(
                fpStartdatoFar,
                fpStartdatoFar.plusWeeks(100).minusDays(1),
                fpStartdatoFar.plusWeeks(40).minusDays(1));
        fastsettUttaksperioderManueltBekreftelse.innvilgPeriode(
                fpStartdatoFar,
                fpStartdatoFar.plusWeeks(40).minusDays(1),
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        fastsettUttaksperioderManueltBekreftelse.innvilgAktiviteterOgAvslåResten(
                fpStartdatoFar.plusWeeks(40),
                fpStartdatoFar.plusWeeks(100).minusDays(1),
                List.of(orgNummerFar1));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);

        /* VERIFISERINGER */
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER er 0 dager!");

        BeregningsresultatPeriode[] beregningsresultatPerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger().getPerioder();
        verifiser(beregningsresultatPerioder.length == 3, "Forventer 3 forskjelige beregningsresultatsperioder!");
        verifiser(beregningsresultatPerioder[0].getTom().isEqual(opphørsDatoForRefusjon),
                "Forventer at lengden på første peridoe har tom dato som matcher tom dato angitt i IM#2");
        verifiser(beregningsresultatPerioder[1].getTom().isEqual(fpStartdatoFar.plusWeeks(40).minusDays(1)),
                "Forventer den andre periden har en varighet på 40 uker.");

        List<BeregningsresultatPeriodeAndel> andelerForAT1 = saksbehandler
                .hentBeregningsresultatPerioderMedAndelIArbeidsforhold(orgNummerFar1);
        List<BeregningsresultatPeriodeAndel> andelerForAT2 = saksbehandler
                .hentBeregningsresultatPerioderMedAndelIArbeidsforhold(orgNummerFar2);
        List<Integer> forventetDagsatsForFørstePeriode = regnUtForventetDagsatsForPeriode(
                List.of(månedsinntektFar1, månedsinntektFar2),
                List.of(40, 100), List.of(true, true));
        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()[0]
                        .getDagsats() == sumOfList(forventetDagsatsForFørstePeriode),
                "Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel");
        verifiser(andelerForAT1.get(0).getTilSoker().equals(forventetDagsatsForFørstePeriode.get(0)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");
        verifiser(andelerForAT2.get(0).getRefusjon().equals(forventetDagsatsForFørstePeriode.get(1)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til arbeidsgiver");

        List<Integer> forventetDagsatsForAndrePeriode = regnUtForventetDagsatsForPeriode(
                List.of(månedsinntektFar1, månedsinntektFar2),
                List.of(40, 100), List.of(true, false));
        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()[1]
                        .getDagsats() == sumOfList(forventetDagsatsForAndrePeriode),
                "Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel");
        verifiser(andelerForAT1.get(1).getTilSoker().equals(forventetDagsatsForAndrePeriode.get(0)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");
        verifiser(andelerForAT2.get(1).getTilSoker().equals(forventetDagsatsForAndrePeriode.get(1)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");

        List<Integer> forventetDagsatsForTredjePeriode = regnUtForventetDagsatsForPeriode(
                List.of(månedsinntektFar1, månedsinntektFar2),
                List.of(40, 0), List.of(true, false));
        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()[2]
                        .getDagsats() == sumOfList(forventetDagsatsForTredjePeriode),
                "Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel");
        verifiser(andelerForAT1.get(2).getTilSoker().equals(forventetDagsatsForTredjePeriode.get(0)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");
        verifiser(andelerForAT2.get(2).getTilSoker().equals(forventetDagsatsForTredjePeriode.get(1)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");
    }

    @Test
    @DisplayName("7: Far har AAP og søker overføring av gjennværende mødrekvoten fordi mor er syk.")
    @Description("Mor har løpende sak hvor hun har søkt om hele mødrekvoten og deler av fellesperioden. Mor blir syk 4" +
            "uker inn i mødrekvoten og far søker om overføring av resten. Far søker ikke overføring av fellesperioden." +
            "Far får innvilget mødrevkoten og mor sin sak blir berørt og automatisk revurdert.")
    public void FarTestMorSyk() {
        TestscenarioDto testscenario = opprettTestscenario("562");

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFarOrdinær = fødselsdato.plusWeeks(23);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(testscenario,
                fødselsdato, fpStartdatoMor, fpStartdatoFarOrdinær);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(saksbehandler.valgtBehandling.uuid.toString(), "IKKE_HOY");
        saksbehandler.ventTilAvsluttetBehandling();

        /*
         * FAR: Søker overføring av mødrekvoten fordi mor er syk innenfor de 6 første
         * uker av mødrekvoten.
         */
        var identFar = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdFar = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var fpStartdatoFarEndret = fødselsdato.plusWeeks(4);
        var fordelingFar = generiskFordeling(
                overføringsperiode(OverføringÅrsak.SYKDOM_ANNEN_FORELDER, MØDREKVOTE, fpStartdatoFarEndret,
                        fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fpStartdatoFarOrdinær, fpStartdatoFarOrdinær.plusWeeks(15).minusDays(1)));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(testscenario.getPersonopplysninger().getAnnenPartAktørIdent())
                .medMottattDato(fødselsdato.plusWeeks(6));
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.hentFagsak(saksnummerFar);
        AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder avklarFaktaUttakPerioder = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class);
        avklarFaktaUttakPerioder.godkjennPeriode(fpStartdatoFarEndret, fødselsdato.plusWeeks(15).minusDays(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakPerioder);

        var beregningAktivitetStatus = saksbehandler.hentUnikeBeregningAktivitetStatus();
        verifiser(beregningAktivitetStatus.contains(new Kode("AKTIVITET_STATUS", "AAP")),
                "Forventer at beregningsstatusen er APP!");
        verifiser(beregningAktivitetStatus.size() == 1,
                "Forventer bare en periode med aktivitetstatus lik AAP");
        verifiser(
                saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0)
                        .getRedusertPrAar() > 0,
                "Forventer at beregningsgrunnlaget baserer seg på en årsinntekt større enn 0. Søker har bare AAP.");

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        /* Mor: berørt sak */
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        verifiser(saksbehandler.valgtBehandling.hentBehandlingsresultat().equalsIgnoreCase("FORELDREPENGER_ENDRET"),
                "Foreldrepenger skal være endret pga annenpart har overlappende uttak!");

        verifiser(saksbehandler.hentAvslåtteUttaksperioder().size() == 2,
                "Forventer at det er 2 avslåtte uttaksperioder");
        verifiser(saksbehandler.valgtBehandling.hentUttaksperiode(2).getPeriodeResultatÅrsak().kodeverk
                        .equalsIgnoreCase("IKKE_OPPFYLT_AARSAK"),
                "Perioden burde være avslått fordi annenpart har overlappende uttak!");
        verifiser(saksbehandler.valgtBehandling.hentUttaksperiode(3).getPeriodeResultatÅrsak().kodeverk
                        .equalsIgnoreCase("IKKE_OPPFYLT_AARSAK"),
                "Perioden burde være avslått fordi annenpart har overlappende uttak!");

        BeregningsresultatMedUttaksplan tilkjentYtelsePerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger();
        verifiser(tilkjentYtelsePerioder.getPerioder()[2].getDagsats() == 0,
                "Siden perioden er avslått, forventes det 0 i dagsats.");
        verifiser(tilkjentYtelsePerioder.getPerioder()[3].getDagsats() == 0,
                "Siden perioden er avslått, forventes det 0 i dagsats.");

        // TODO: Sjekk med simulering! Er ikke støtte for det for øyeblikket. Trenger å inkludere fpoppdrag.

    }

    @Test
    @DisplayName("8: Mor har tvillinger og søker om hele utvidelsen.")
    @Description("Mor føder tvillinger og søker om hele mødrekvoten og fellesperioden, inkludert utvidelse. Far søker " +
            "samtidig uttak av fellesperioden fra da mor starter utvidelsen av fellesperioden. Søker deretter samtidig " +
            "av fedrekvoten, frem til mor er ferdig med fellesperioden, og deretter søker resten av fedrekvoten.")
    public void MorSøkerFor2BarnHvorHunFårBerørtSakPgaFar() {
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
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(17).minusDays(1), true, false));
        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordelingMor)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(2, fødselsdato))
                .medMottattDato(fpStartdatoMor.minusWeeks(3));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                identMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var månedsinntektMor = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0)
                .getBeløp();
        var orgNummerMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
        var inntektsmeldingMor = lagInntektsmelding(
                månedsinntektMor,
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
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        Saldoer saldoerFørstgangsbehandling = saksbehandler.valgtBehandling.getSaldoer();
        verifiser(saldoerFørstgangsbehandling.getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp!");
        verifiser(saldoerFørstgangsbehandling.getStonadskontoer().get(MØDREKVOTE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton MØDREKVOTE er brukt opp!");
        verifiser(saldoerFørstgangsbehandling.getStonadskontoer().get(FELLESPERIODE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FELLESPERIODE er brukt opp!");
        verifiser(saldoerFørstgangsbehandling.getStonadskontoer().get(FLERBARNSDAGER).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FLERBARNSDAGER er brukt opp!");

        /*
         * FAR: Søker samtidig uttak med flerbansdager. Søker deretter hele fedrekvoten,
         * også samtidig uttak.
         */
        var identFar = testscenario.getPersonopplysninger().getAnnenpartIdent();
        var aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        var gjennomsnittFraTreSisteÅreneISigrun = hentNæringsinntektFraSigrun(testscenario, 2018,true);
        var opptjeningFar = OpptjeningErketyper.medEgenNaeringOpptjening(
                LocalDate.now().minusYears(4),
                fpStartdatoFar,
                false,
                BigDecimal.valueOf(gjennomsnittFraTreSisteÅreneISigrun).toBigInteger(),
                false);
        var fordelingFar = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(4).minusDays(1),
                        true, true),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(4), fpStartdatoFar.plusWeeks(17).minusDays(1),
                        false,true),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(17), fpStartdatoFar.plusWeeks(19).minusDays(1)));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medSpesiellOpptjening(opptjeningFar)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(2, fødselsdato))
                .medAnnenForelder(aktørIdMor);
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var månedsinntektFar = testscenario.getScenariodataAnnenpart().getInntektskomponentModell()
                .getInntektsperioder().get(0).getBeløp();
        var orgNummerFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
        var arbeidsforholdIdFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold()
                .get(0).getArbeidsforholdId();
        var inntektsmeldingFar = lagInntektsmelding(
                månedsinntektFar,
                identFar,
                fpStartdatoFar,
                orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar);
        fordel.sendInnInntektsmelding(
                inntektsmeldingFar,
                aktørIdFar,
                identFar,
                saksnummerFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0).kode
                        .equalsIgnoreCase("AT_SN"),
                "Forventer at far får kombinert satus i beregning (da AT og SN)");

        BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0);
        var dagsats = beregningsgrunnlagPeriode.getDagsats();
        var redusertPrAar = beregningsgrunnlagPeriode.getRedusertPrAar();
        var prosentfaktorAvDagsatsTilAF = beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndel().get(0)
                .getRedusertPrAar() / redusertPrAar;
        var dagsatsTilAF = Math.round(dagsats * prosentfaktorAvDagsatsTilAF);

        List<BeregningsresultatPeriodeAndel> perioderMedAndelIArbeidsforhold = saksbehandler
                .hentBeregningsresultatPerioderMedAndelIArbeidsforhold(orgNummerFar);
        verifiser(perioderMedAndelIArbeidsforhold.get(0).getTilSoker() == dagsatsTilAF,
                "Forventer at dagsatsen for arbeidsforholdet blir beregnet først – rest går til søker for SN");
        verifiser(perioderMedAndelIArbeidsforhold.get(1).getTilSoker() == dagsatsTilAF,
                "Forventer at dagsatsen for arbeidsforholdet blir beregnet først – rest går til søker for SN");
        verifiser(perioderMedAndelIArbeidsforhold.get(2).getTilSoker() == dagsatsTilAF,
                "Forventer at dagsatsen for arbeidsforholdet blir beregnet først – rest går til søker for SN");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(orgNummerFar, 0),
                "Foventer at hele den utbetalte dagsatsen går til søker!");

        List<BeregningsresultatPeriodeAndel> perioderMedAndelISN = saksbehandler
                .hentBeregningsresultatPerioderMedAndelISN();
        verifiser(perioderMedAndelISN.get(0).getTilSoker() == (dagsats - dagsatsTilAF),
                "Forventer at resten av dagsatsen går til søker for SN");
        verifiser(perioderMedAndelISN.get(1).getTilSoker() == (dagsats - dagsatsTilAF),
                "Forventer at resten av dagsatsen går til søker for SN");
        verifiser(perioderMedAndelISN.get(2).getTilSoker() == (dagsats - dagsatsTilAF),
                "Forventer at resten av dagsatsen går til søker for SN");

        /* Mor: Berørt sak */
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgRevurderingBehandling();
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.innvilgPeriode(
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
        fastsettUttaksperioderManueltBekreftelse.innvilgPeriode(
                fpStartdatoFar.plusWeeks(4),
                fpStartdatoFar.plusWeeks(13).minusDays(1),
                new Kode("INNVILGET_AARSAK", "2038", "§14-10 sjette ledd: Samtidig uttak"),
                true,
                true,
                100);
        fastsettUttaksperioderManueltBekreftelse.avslåPeriode(
                fpStartdatoFar.plusWeeks(13),
                fpStartdatoFar.plusWeeks(17).minusDays(1),
                new Kode("IKKE_OPPFYLT_AARSAK", "4002", "§14-9: Ikke stønadsdager igjen på stønadskonto"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerMor, true);

        Saldoer saldoerBerørtSak = saksbehandler.valgtBehandling.getSaldoer();
        verifiser(saldoerBerørtSak.getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp!");
        verifiser(saldoerBerørtSak.getStonadskontoer().get(MØDREKVOTE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton MØDREKVOTE er brukt opp!");
        verifiser(saldoerBerørtSak.getStonadskontoer().get(FEDREKVOTE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FEDREKVOTE er brukt opp!");
        verifiser(saldoerBerørtSak.getStonadskontoer().get(FELLESPERIODE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FELLESPERIODE er brukt opp!");
        verifiser(saldoerBerørtSak.getStonadskontoer().get(FLERBARNSDAGER).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FLERBARNSDAGER er brukt opp!");

        verifiser(saksbehandler.hentAvslåtteUttaksperioder().size() == 1,
                "Forventer at det er 1 avslåtte uttaksperioder");
        verifiser(saksbehandler.valgtBehandling.hentUttaksperiode(6).getPeriodeResultatÅrsak().kodeverk
                        .equalsIgnoreCase("IKKE_OPPFYLT_AARSAK"),
                "Perioden burde være avslått fordi det er ingen stønadsdager igjen på stønadskontoen.");
        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()[6].getDagsats() == 0,
                "Siden perioden er avslått, forventes det 0 i dagsats i tilkjent ytelse");
    }

    @Test
    @DisplayName("9: Mor søker med dagpenger som grunnlag, klager, får medhold og revurderes.")
    @Description("Mor søker med dagpenger som grunnlag. Bruker ikke besteberegning. Søker klager på dette. Mor får" +
            "medhold og revurderes. I revurderingen brukes besteberegning og sbh oppgir en verdi høyere månedsinntekt.")
    public void MorSøkerMedDagpengerTest() {
        TestscenarioDto testscenario = opprettTestscenario("521");
        var identMor = testscenario.getPersonopplysninger().getSøkerIdent();
        var aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(31).minusDays(1)));

        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordelingMor);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                identMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        // TODO: Her får en avventer dokumentasjon hvis en ikke sender inn IM. Her skal
        // det ikke være behov for å sende inn IM.
        var månedsinntektMor = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0)
                .getBeløp();
        var orgNummerMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
        var inntektsmeldingMor = lagInntektsmelding(
                månedsinntektMor,
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

        verifiser(saksbehandler.sjekkOmDetErOpptjeningFremTilSkjæringstidspunktet("DAGPENGER"),
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype DAGPENGER med " +
                        "opptjening frem til skjæringstidspunktet for opptjening.");

        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .fordelEtterBesteBeregningForDagpenger(false)
                .setBegrunnelse("Bruker IKKE besteberegning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerMor, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        verifiser(saksbehandler.harHistorikkinnslagForBehandling(HistorikkInnslag.BREV_BESTILT),
                "Brev er bestillt i førstegangsbehandling");
        BeregningsresultatPeriode[] beregningsresultatPeriodeFørstegangsbehandling = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger().getPerioder();
        verifiser(beregningsresultatPeriodeFørstegangsbehandling.length == 4,
                "Forventer 4 forskjelige beregningsresultatsperioder!");
        verifiser(beregningsresultatPeriodeFørstegangsbehandling[0].getDagsats() == 1_000,
                "Forventer at dagsatsen er satt til dagsatsen for dagpengene.");
        verifiser(beregningsresultatPeriodeFørstegangsbehandling[1].getDagsats() == 1_000,
                "Forventer at dagsatsen er satt til dagsatsen for dagpengene.");
        verifiser(beregningsresultatPeriodeFørstegangsbehandling[2].getDagsats() == 1_000,
                "Forventer at dagsatsen er satt til dagsatsen for dagpengene.");
        verifiser(beregningsresultatPeriodeFørstegangsbehandling[3].getDagsats() == 1_000,
                "Forventer at dagsatsen er satt til dagsatsen for dagpengene.");

        // * KLAGE *//
        fordel.sendInnKlage(null, testscenario, saksnummerMor);

        klagebehandler.erLoggetInnMedRolle(Aktoer.Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(saksnummerMor);
        klagebehandler.velgKlageBehandling();
        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .setPåklagdVedtak(Integer.toString(klagebehandler.valgtBehandling.id - 1))
                .godkjennAlleFormkrav()
                .setBegrunnelse("Godkjenner alle formkrav");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftMedholdGunst("PROSESSUELL_FEIL")
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Slik er det bare!");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummerMor);
        beslutter.velgKlageBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        // * REVURDERING *//
        saksbehandler.opprettBehandlingRevurdering("ETTER_KLAGE");
        saksbehandler.velgRevurderingBehandling();

        verifiser(saksbehandler.valgtBehandling.getBehandlingArsaker().get(0).getBehandlingArsakType().kode
                        .equalsIgnoreCase("ETTER_KLAGE"),
                "Foventer at revurderingen har årsakskode ETTER_KLAGE.");

        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse2
                .fordelEtterBesteBeregningForDagpenger(true)
                .leggTilBesteBeregningAndeler(30_000.0, saksbehandler.kodeverk.Inntektskategori.getKode("DAGPENGER"))
                .setBegrunnelse("Legger til en beste beregning andel periode som gjør at vi bruker besteberegning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse2);

        KontrollerManueltOpprettetRevurdering kontrollerManueltOpprettetRevurdering = saksbehandler
                .hentAksjonspunktbekreftelse(KontrollerManueltOpprettetRevurdering.class);
        saksbehandler.bekreftAksjonspunkt(kontrollerManueltOpprettetRevurdering);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerMor, true);

        BeregningsresultatPeriode[] beregningsresultatPeriodeRevurdering = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger().getPerioder();
        List<Integer> forventetDagsats = regnUtForventetDagsatsForPeriode(List.of(30_000), List.of(100),
                List.of(false));
        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0)
                        .getDagsats() == forventetDagsats.get(0),
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
        verifiser(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen().get(0).kode
                        .equalsIgnoreCase("ENDRING_I_BEREGNING"),
                "Foventer at konsekvens for ytelse er satt til ENDRING_I_BEREGNING.");
        verifiser(!saksbehandler.harHistorikkinnslagForBehandling(HistorikkInnslag.BREV_BESTILT,
                        saksbehandler.valgtBehandling.id),
                "Forventer at det ikke sendes et nytt vedtaksbrev i revurderingen");

    }

    @Test
    @DisplayName("10: Far søker adopsjon og får revurdert sak 4 måneder senere på grunn av IM med endring i refusjon.")
    @Description("Far søker adopsjon og får revurdert sak 4 måneder senere på grunn av IM med endring i refusjon. " +
            "Mens behandlingen er hos beslutter sender AG en ny korrigert IM. Behandlingen rulles tilbake. På den " +
            "siste IM som AG sender ber AG om full refusjon, men kommer for sent til å få alt. AG får refusjon for" +
            "den inneværende måneden og tre måneder tilbake i tid; tiden før dette skal gå til søker.")
    public void FarSøkerAdopsjon() {
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
                uttaksperiode(FELLESPERIODE, fellesperiodeStartFar, fellesperiodeSluttFar));
        var søknadFar = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, aktørIdFar, SøkersRolle.FAR, false)
                .medFordeling(fordelingFar)
                .medMottattDato(fpStartdatoFar.minusWeeks(3));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.ADOPSJONSSOKNAD_FORELDREPENGER);

        var månedsinntektFar = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0)
                .getBeløp();
        var orgNummerFar = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
        var arbeidsforholdIdFar = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsforholdId();
        var inntektsmeldingFar = lagInntektsmelding(
                månedsinntektFar,
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
        AvklarFaktaAdopsjonsdokumentasjonBekreftelse avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        avklarFaktaAdopsjonsdokumentasjonBekreftelseFar.setBegrunnelse("Adopsjon behandlet av Autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);

        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.innvilgManuellePerioder(
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().length == 2,
                "Forventer at det er to perioder i tilkjent ytelse. En for fedrekvote og en for fellesperioden");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");

        // AG sender inn en IM med endring i refusjon som skal føre til revurdering på far sin sak.
        var inntektsmeldingEndringFar = lagInntektsmelding(
                månedsinntektFar,
                identFar,
                fpStartdatoFar,
                orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntektFar/2));
        fordel.sendInnInntektsmelding(
                inntektsmeldingEndringFar,
                aktørIdFar,
                identFar,
                saksnummerFar);

        // Revurdering / Berørt sak til far
        saksbehandler.velgRevurderingBehandling();
        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .leggTilRefusjonGyldighetVurdering(orgNummerFar, false)
                .setBegrunnelse("Refusjonskrav er sendt inn for sent og skal ikke tas med i beregning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelseRevurdering = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelseRevurdering.innvilgManuellePerioder(
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelseRevurdering);

        VurderTilbakekrevingVedFeilutbetalingBekreftelse vurderTilbakekrevingVedFeilutbetalingBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderTilbakekrevingVedFeilutbetalingBekreftelse.class);
        vurderTilbakekrevingVedFeilutbetalingBekreftelse.setTilbakekrevFrasøker(false);
        vurderTilbakekrevingVedFeilutbetalingBekreftelse.setBegrunnelse("AG ber om refusjon for sent til å få alt!");
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedFeilutbetalingBekreftelse);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // AG sender inn ny korrigert IM med endring i refusjon mens behandlingen er hos beslutter. Behandlingen skal
        // rulles tilbake og behandles på nytt fra første AP i revurderingen.
        var inntektsmeldingEndringFar2 = lagInntektsmelding(
                månedsinntektFar,
                identFar,
                fpStartdatoFar,
                orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntektFar));
        fordel.sendInnInntektsmelding(
                inntektsmeldingEndringFar2,
                aktørIdFar,
                identFar,
                saksnummerFar);

        saksbehandler.hentFagsak(saksnummerFar);
        verifiser(saksbehandler.behandlinger.size() == 2, "Fagsaken har mer enn én revurdering.");
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.BEHANDLINGEN_ER_FLYTTET);
        saksbehandler.velgRevurderingBehandling();

        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse2
                .leggTilRefusjonGyldighetVurdering(orgNummerFar, false)
                .setBegrunnelse("Refusjonskrav er sendt inn for sent og skal ikke tas med i beregning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse2);

        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelseRevurdering2 = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelseRevurdering2.innvilgManuellePerioder(
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelseRevurdering2);

        VurderTilbakekrevingVedFeilutbetalingBekreftelse vurderTilbakekrevingVedFeilutbetalingBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderTilbakekrevingVedFeilutbetalingBekreftelse.class);
        vurderTilbakekrevingVedFeilutbetalingBekreftelse2.setTilbakekrevFrasøker(false);
        vurderTilbakekrevingVedFeilutbetalingBekreftelse2.setBegrunnelse("AG ber om refusjon for sent til å få alt!");
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedFeilutbetalingBekreftelse2);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, true);

        BeregningsresultatPeriode[] perioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        verifiser(perioder.length == 3,
                "Foventer at den berørte saken har 3 tilkjent ytelse peridoer, og ikke 2 som i førstegangsbehandling!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder[0], 0),
                "Forventer at hele summen utbetales til søker i første periode, og derfor ingenting til arbeidsgiver!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder[1], 100),
                "Forventer at hele summen utbetales til AG i andre periode, og derfor ingenting til søker!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder[2], 100),
                "Forventer at hele summen utbetales til AG i tredje periode, og derfor ingenting til søker!");
    }

    @Test
    @DisplayName("11: Far søker adopsjon hvor han søker hele fedrekvoten og fellesperiode, og får berørt sak pga mor")
    @Description("Far søker adopsjon hvor han søker hele fedrekvoten og fellesperioden. Mor søker noe av mødrekvoten midt " +
            "i fars periode med fullt uttak. Deretter søker mor 9 uker av fellesperioden med samtidig uttak. Far får " +
            "berørt sak hvor han får avkortet fellesperidoen på slutten og redusert perioder hvor mor søker samtidig uttak")
    public void FarSøkerAdopsjonOgMorMødrekvoteMidtIFarsOgDeretterSamtidigUttakAvFellesperidoe() {
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
                uttaksperiode(FELLESPERIODE, fellesperiodeStartFar, fellesperiodeSluttFar));
        var søknadFar = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, aktørIdFar, SøkersRolle.FAR, false)
                .medFordeling(fordelingFar);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                aktørIdFar,
                identFar,
                DokumenttypeId.ADOPSJONSSOKNAD_FORELDREPENGER);

        var månedsinntektFar = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0)
                .getBeløp();
        var orgNummerFar = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
        var arbeidsforholdIdFar = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsforholdId();
        var inntektsmeldingFar = lagInntektsmelding(
                månedsinntektFar,
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
        AvklarFaktaAdopsjonsdokumentasjonBekreftelse avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        avklarFaktaAdopsjonsdokumentasjonBekreftelseFar.setBegrunnelse("Adopsjon behandlet av Autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);

        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.innvilgManuellePerioder(
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
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
                uttaksperiode(FELLESPERIODE, fellesperiodeStartMor, fellesperiodeSluttMor, false, true, 40));
        var søknadMor = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, aktørIdMor, SøkersRolle.MOR, false)
                .medFordeling(fordelingMor)
                .medAnnenForelder(aktørIdFar);
        var saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                identMor,
                DokumenttypeId.ADOPSJONSSOKNAD_FORELDREPENGER);

        var månedsinntektMor = testscenario.getScenariodataAnnenpart().getInntektskomponentModell()
                .getInntektsperioder().get(0).getBeløp();
        var orgNummerMor = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
        var arbeidsforholdIdMor = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold()
                .get(0).getArbeidsforholdId();
        var inntektsmeldingMor = lagInntektsmelding(
                månedsinntektMor,
                identMor,
                fpStartdatoMor,
                orgNummerMor)
                .medArbeidsforholdId(arbeidsforholdIdMor);
        fordel.sendInnInntektsmelding(
                inntektsmeldingMor,
                aktørIdMor,
                identMor,
                saksnummerMor);

        saksbehandler.hentFagsak(saksnummerMor);
        AvklarFaktaAdopsjonsdokumentasjonBekreftelse avklarFaktaAdopsjonsdokumentasjonBekreftelseMor = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        avklarFaktaAdopsjonsdokumentasjonBekreftelseMor.setBegrunnelse("Adopsjon behandlet av Autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseMor);

        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        /* FAR: Berørt behandling */
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgRevurderingBehandling();

        List<UttakResultatPeriode> avslåttePerioder = saksbehandler.hentAvslåtteUttaksperioder();
        verifiser(avslåttePerioder.size() == 1,
                "Forventer at det er 1 avslåtte uttaksperioder (automatisk avslått)");
        verifiser(saksbehandler.valgtBehandling.hentUttaksperiode(1).getPeriodeResultatÅrsak().kode
                        .equalsIgnoreCase("4084"),
                "Perioden burde være avslått fordi annenpart tar ut mødrekovte med 100% utbetalingsgrad samtidig!");

        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelseMor = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelseMor.innvilgPeriode(
                fellesperiodeStartMor,
                fellesperiodeStartFar.minusDays(1),
                new Kode("INNVILGET_AARSAK", "2038", "§14-10 sjette ledd: Samtidig uttak"),
                false,
                true,
                100);
        fastsettUttaksperioderManueltBekreftelseMor.innvilgPeriode(
                fellesperiodeStartFar,
                fellesperiodeSluttMor,
                new Kode("INNVILGET_AARSAK", "2038", "§14-10 sjette ledd: Samtidig uttak"),
                false,
                true,
                60);
        var saldoer = saksbehandler
                .hentSaldoerGittUttaksperioder(fastsettUttaksperioderManueltBekreftelseMor.getPerioder());
        var disponibleFellesdager = saldoer.getStonadskontoer().get(FELLESPERIODE).getSaldo();
        var sluttenAvFørstePeriode = Virkedager.minusVirkedager(fellesperiodeSluttFar, Math.abs(disponibleFellesdager));
        fastsettUttaksperioderManueltBekreftelseMor.splitPeriode(
                fellesperiodeSluttMor.plusDays(1),
                fellesperiodeSluttFar,
                sluttenAvFørstePeriode);
        fastsettUttaksperioderManueltBekreftelseMor.innvilgPeriode(
                fellesperiodeSluttMor.plusDays(1),
                sluttenAvFørstePeriode,
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        fastsettUttaksperioderManueltBekreftelseMor.avslåPeriode(
                sluttenAvFørstePeriode.plusDays(1),
                fellesperiodeSluttFar,
                new Kode("IKKE_OPPFYLT_AARSAK", "4002", "§14-9: Ikke stønadsdager igjen på stønadskonto"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelseMor);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, true);

        beslutter.ventTilFagsakLøpende();

        // verifisering i uttak
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FELLESPERIODE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FELLESPERIODE er brukt opp (dvs = 0)!");
        verifiser(saksbehandler.hentAvslåtteUttaksperioder().size() == 2,
                "Forventer at det er 2 avslåtte uttaksperioder");

        // verifisering i tilkjent ytelse
        BeregningsresultatMedUttaksplan tilkjentYtelsePerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger();
        verifiser(tilkjentYtelsePerioder.getPerioder()[1].getDagsats() == 0,
                "Siden perioden er avslått, forventes det 0 i dagsats.");
        verifiser(tilkjentYtelsePerioder.getPerioder()[3].getDagsats() == Math
                        .ceil(tilkjentYtelsePerioder.getPerioder()[2].getDagsats() * 0.6),
                "Siden perioden er avslått, forventes det 0 i dagsats.");
        verifiser(tilkjentYtelsePerioder.getPerioder()[5].getDagsats() == 0,
                "Siden perioden er avslått, forventes det 0 i dagsats.");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");
    }

    @Test
    @DisplayName("12: Mor søker fødsel og mottar sykepenger som er under 1/2 G")
    @Description("12: Mor søker fødsel og mottar sykepenger som er under 1/2 G. Har ingen inntektskilder. Hun har " +
            "for lite inntekt og har dermed ikke rett til foreldrepenger – beregning avvist søknadden.")
    public void morSøkerFødselMottarForLite() {
        var testscenario = opprettTestscenario("70");
        var søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                søkerAktørIdent,
                søkerIdent,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        var avklarArbeidsforholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        verifiser(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"),
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som " +
                        "er forut for permisjonen på skjæringstidspunktet!");

        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilAndelerYtelse(4000.0, saksbehandler.kodeverk.Inntektskategori.getKode("ARBEIDSTAKER"));
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);

        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0)
                        .getRedusertPrAar() < saksbehandler.valgtBehandling.getBeregningsgrunnlag().getHalvG(),
                "Forventer at beregningsgrunnlaget baserer seg på et grunnlag som er mindre enn 1/2 G.");
        verifiserLikhet(saksbehandler.vilkårStatus(VilkarTypeKoder.BEREGNINGSGRUNNLAGVILKÅR).kode, "IKKE_OPPFYLT");
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "AVSLÅTT",
                "Forventer at behandlingen er avslått fordi søker ikke har rett på foreldrepenger.");
    }

    private Long sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(TestscenarioDto testscenario,
            LocalDate fødselsdato,
            LocalDate fpStartdatoMor,
            LocalDate fpStartdatoFar) {
        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var identMor = testscenario.getPersonopplysninger().getAnnenpartIdent();
        var aktørIdMor = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fpStartdatoFar.minusDays(1)));
        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordelingMor)
                .medMottattDato(fpStartdatoMor.minusWeeks(4));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                identMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var månedsinntektMor = testscenario.getScenariodataAnnenpart().getInntektskomponentModell()
                .getInntektsperioder().get(0).getBeløp();
        var orgNummerMor = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
        var inntektsmeldingMor = lagInntektsmelding(
                månedsinntektMor,
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

}
