package no.nav.foreldrepenger.autotest.verdikjedetester;

import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.OpptjeningErketyper.medEgenNaeringOpptjening;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerAdopsjon;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper.overføringsperiode;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.UtsettelsesÅrsak.ARBEID;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettBruttoBeregningsgrunnlagSNBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsetteUttakKontrollerOpplysningerOmDødDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerAktivitetskravBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderRefusjonBeregningsgrunnlagBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedNegativSimulering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderVarigEndringEllerNyoppstartetSNBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAdopsjonsdokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAleneomsorgBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAnnenForeldreHarRett;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaUttakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrUttaksperioder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.VilkarTypeKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatMedUttaksplan;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriodeAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.DekningsgradDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.autotest.søknad.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.RelasjonTilBarnErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadForeldrepengerErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.Overføringsårsak;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.autotest.util.localdate.Virkedager;
import no.nav.foreldrepenger.vtp.kontrakter.DødfødselhendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.DødshendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.FødselshendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

@Execution(ExecutionMode.CONCURRENT)
@Tag("verdikjede")
public class VerdikjedeForeldrepenger extends ForeldrepengerTestBase {

    @Test
    @DisplayName("1: Mor automatisk førstegangssøknad termin (med fødselshendelse), aleneomsorg og avvik i beregning.")
    @Description("Mor førstegangssøknad før fødsel på termin. Mor har aleneomsorg og enerett. Sender inn IM med over " +
                "25% avvik med delvis refusjon. Etter behandlingen er ferdigbehandlet mottas en fødselshendelse.")
    public void testcase_mor_fødsel() {
        var testscenario = opprettTestscenario("501");
        var identSøker = testscenario.personopplysninger().søkerIdent();
        var termindato = LocalDate.now().plusWeeks(1);
        var fpStartdato = termindato.minusWeeks(3);

        // BYGGER OG SENDER SØKNAD TIL MOTTAK!
        var fordeling = FordelingErketyper.generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, termindato.minusDays(1)),
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FORELDREPENGER, termindato, termindato.plusWeeks(15).minusDays(1)),
                UttaksperioderErketyper.utsettelsesperiode(UtsettelsesÅrsak.ARBEID, termindato.plusWeeks(15),
                        termindato.plusWeeks(20).minusDays(1)),
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FORELDREPENGER, termindato.plusWeeks(20), termindato.plusWeeks(36).minusDays(1)));

        var søknad = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medFordeling(fordeling)
                .medRettigheter(RettigheterErketyper.harAleneOmsorgOgEnerett())
                .medAnnenForelder(lagNorskAnnenforeldre(testscenario.personopplysninger().annenpartIdent()))
                .medMottatdato(termindato.minusWeeks(5));
        var saksnummer = selvbetjening.sendInnSøknad(identSøker, søknad.build());


        // BYGGER OG SENDER IM TIL JOURNALFØRING OG TRIGGER JOURNALFØRINGHENDEELSE!
        var månedsinntekt = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0)
                .beløp();
        var orgNummer = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var søkerFnr = testscenario.personopplysninger().søkerIdent();
        var avvikendeMånedsinntekt = månedsinntekt * 1.3;
        var inntektsmeldinger = lagInntektsmelding((int) avvikendeMånedsinntekt, søkerFnr, fpStartdato, orgNummer)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntekt * 0.6));

        inntektsmelding.sendInnInnteksmeldingFpfordel(inntektsmeldinger,
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(månedsinntekt * 12, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        var avklarFaktaAleneomsorgBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAleneomsorgBekreftelse.class)
                .bekreftBrukerHarAleneomsorg()
                .setBegrunnelse("Bekreftelse sendt fra Autotest.");
        saksbehandler.bekreftAksjonspunktbekreftelserer(avklarFaktaAleneomsorgBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        Saldoer saldoer = saksbehandler.valgtBehandling.getSaldoer();
        verifiserLikhet(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo(), 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp (dvs = 0)!");
        verifiserLikhet(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER).getSaldo(),  75,
                "Forventer at saldoen for stønadskonton FORELDREPENGER er 75 dager!");
        List<Integer> beregnetDagsats = regnUtForventetDagsatsForPeriode(List.of(månedsinntekt), List.of(100), List.of(false));
        verifiser(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0)
                        .getDagsats() == beregnetDagsats.get(0),
                "Forventer at dagsatsen blir justert ut i fra årsinntekten og utbeatlinsggrad, og IKKE 6G fordi inntekten er under 6G!");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(60),
                "Forventer at halve summen utbetales til søker og halve summen til arbeisdgiver pga 60% refusjon!");

        // Fødselshendelse
        var fødselshendelseDto = new FødselshendelseDto("OPPRETTET", null, søkerFnr,
                null, null, termindato.minusWeeks(1));
        fordel.opprettHendelsePåKafka(fødselshendelseDto);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        verifiser(saksbehandler.valgtBehandling.getBehandlingÅrsaker().get(0).getBehandlingArsakType().kode
                        .equalsIgnoreCase("RE-HENDELSE-FØDSEL"),
                "Foventer at revurderingen har årsakskode RE-HENDELSE-FØDSEL.");

        var avklarFaktaAleneomsorgBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAleneomsorgBekreftelse.class)
                .bekreftBrukerHarAleneomsorg()
                .setBegrunnelse("Bekreftelse sendt fra Autotest.");
        saksbehandler.bekreftAksjonspunktbekreftelserer(avklarFaktaAleneomsorgBekreftelse2);

        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "FORELDREPENGER_ENDRET");

        // Verifiser riktig justering av kontoer og uttak.
        saldoer = saksbehandler.valgtBehandling.getSaldoer();
        verifiserLikhet(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo(), 5,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL har 5 dager igjen!");
        verifiserLikhet(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER).getSaldo(),  70,
                "Forventer at saldoen for stønadskonton FORELDREPENGER er 70 dager!");
    }

    @Test
    @DisplayName("2: Mor selvstendig næringsdrivende, varig endring. Søker dør etter behandlingen er ferdigbehandlet.")
    @Description("Mor er selvstendig næringsdrivende og har ferdiglignet inntekt i mange år. Oppgir en næringsinntekt" +
            "som avviker med mer enn 25% fra de tre siste ferdiglignede årene. Søker dør etter behandlingen er " +
            "ferdigbehandlet.")
    public void morSelvstendigNæringsdrivendeTest() {
        var testscenario = opprettTestscenario("510");
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var identSøker = testscenario.personopplysninger().søkerIdent();
        var gjennomsnittFraTreSisteÅreneISigrun = hentNæringsinntektFraSigrun(testscenario, 2018,false);
        var næringsnntekt = BigDecimal.valueOf(gjennomsnittFraTreSisteÅreneISigrun * 1.80).toBigInteger(); // >25% avvik
        var opptjening = OpptjeningErketyper.medEgenNaeringOpptjening(false, næringsnntekt, true);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(testscenario.personopplysninger().annenpartIdent()))
                .medOpptjening(opptjening)
                .medMottatdato(fødselsdato.plusWeeks(2));
        var saksnummer = selvbetjening.sendInnSøknad(identSøker, søknad.build());

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        var vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class)
                .godkjennAllOpptjening()
                .setBegrunnelse("Opptjening godkjent av Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // Verifiser at aksjonspunkt 5042 ikke blir oprettet uten varig endring
        var vurderVarigEndringEllerNyoppstartetSNBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderVarigEndringEllerNyoppstartetSNBekreftelse.class)
                .setErVarigEndretNaering(false)
                .setBegrunnelse("Ingen endring");
        saksbehandler.bekreftAksjonspunkt(vurderVarigEndringEllerNyoppstartetSNBekreftelse);
        var runtimeException = assertThrows(RuntimeException.class, () ->
                saksbehandler.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_SELVSTENDIG_NÆRINGSDRIVENDE));
        verifiser(runtimeException.getMessage().equalsIgnoreCase("Fant ikke aksjonspunkt med kode 5042"),
                "Har uventet aksjonspunkt: 5042");

        var vurderVarigEndringEllerNyoppstartetSNBekreftelse1 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderVarigEndringEllerNyoppstartetSNBekreftelse.class)
                .setErVarigEndretNaering(true)
                .setBegrunnelse("Vurder varig endring for selvstendig næringsdrivende begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderVarigEndringEllerNyoppstartetSNBekreftelse1);
        var fastsettBruttoBeregningsgrunnlagSNBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettBruttoBeregningsgrunnlagSNBekreftelse.class)
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

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        verifiser(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp!");
        verifiser(saldoer.getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton MØDREKVOTE er brukt opp!");
        verifiser(saldoer.getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FELLESPERIODE er brukt opp!");

        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");

        var dødshendelseDto = new DødshendelseDto("OPPRETTET", null, identSøker,
                LocalDate.now().minusDays(1));
        fordel.opprettHendelsePåKafka(dødshendelseDto);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        verifiser(saksbehandler.valgtBehandling.getBehandlingÅrsaker().get(0).getBehandlingArsakType().kode
                        .equalsIgnoreCase("RE-HENDELSE-DØD-F"),
                "Foventer at revurderingen har årsakskode RE-HENDELSE-DØD-F");

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class)
                .avslåManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(FastsetteUttakKontrollerOpplysningerOmDødDto.class);

        if (saksbehandler.harAksjonspunkt("5084")) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.
                    hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
            vurderTilbakekrevingVedNegativSimulering.setTilbakekrevingIgnorer();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        }

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true);

        verifiser(saksbehandler.hentAvslåtteUttaksperioder().size() == 3,
                "Forventer at det er 3 avslåtte uttaksperioder");

        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        verifiserLikhet(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats(),0,
                "Siden perioden er avslått pga død, forventes det 0 i dagsats.");
        verifiserLikhet(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats(),0,
                "Siden perioden er avslått pga død, forventes det 0 i dagsats.");
        verifiserLikhet(tilkjentYtelsePerioder.getPerioder().get(4).getDagsats(),0,
                "Siden perioden er avslått pga død, forventes det 0 i dagsats.");
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
        var termindato = LocalDate.now().plusWeeks(6);
        var fpStartdatoMor = termindato.minusWeeks(3);
        var fpMottatDato = termindato.minusWeeks(6);
        var fordelingDtoMor = new FordelingDto();
        var foreldrepengerFørFødsel = new PermisjonPeriodeDto(Stønadskonto.FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor,
                termindato.minusDays(1));
        var mødrekvote = new PermisjonPeriodeDto(Stønadskonto.MØDREKVOTE, termindato,
                termindato.plusWeeks(20).minusDays(1));
        fordelingDtoMor.permisjonsPerioder.add(foreldrepengerFørFødsel);
        fordelingDtoMor.permisjonsPerioder.add(mødrekvote);
        var papirSoknadForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(PapirSoknadForeldrepengerBekreftelse.class)
                .morSøkerTermin(fordelingDtoMor, termindato, fpMottatDato, DekningsgradDto.AATI);
        saksbehandler.bekreftAksjonspunkt(papirSoknadForeldrepengerBekreftelse);

        var avklarArbeidsforholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        var avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class)
                .setUtstedtdato(termindato.minusWeeks(10))
                .setBegrunnelse("Begrunnelse fra autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        verifiser(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"),
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som " +
                        "er forut for permisjonen på skjæringstidspunktet!");

        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilAndelerYtelse(10000.0, saksbehandler.kodeverk.Inntektskategori.getKode("ARBEIDSTAKER"))
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class)
                .avslåManuellePerioderMedPeriodeResultatÅrsak(
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
    public void farSøkerForeldrepengerTest() {
        TestscenarioDto testscenario = opprettTestscenario("560");

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(23);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(testscenario,
                fødselsdato, fpStartdatoMor, fpStartdatoFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus("IKKE_HOY");
        saksbehandler.ventTilAvsluttetBehandling();

        /*
         * FAR: Søker med to arbeidsforhold i samme virksomhet, orgn.nr, men med ulik
         * arbeidsforholdID. Søker utsettelse arbeid og deretter resten av felles
         * perioden og hele fedrekvoten med gradert uttak. Sender inn 2 IM med ulik
         * arbeidsforholdID og refusjon på begge.
         */
        var identFar = testscenario.personopplysninger().søkerIdent();
        var orgNummerFar = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
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
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                        .medAnnenForelder(lagNorskAnnenforeldre(testscenario.personopplysninger().annenpartAktørIdent()))
                        .medFordeling(fordelingFar);
        var saksnummerFar = selvbetjening.sendInnSøknad(identFar, søknadFar.build());

        var månedsinntektFar1 = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp();
        var arbeidsforholdIdFar1 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsforholdId();
        InntektsmeldingBuilder inntektsmeldingFar1 = lagInntektsmelding(månedsinntektFar1, identFar, fpStartdatoFar, orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar1)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntektFar1));
        var månedsinntektFar2 = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(1).beløp();
        var arbeidsforholdIdFar2 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1).arbeidsforholdId();
        InntektsmeldingBuilder inntektsmeldingFar2 = lagInntektsmelding(månedsinntektFar2, identFar, fpStartdatoFar, orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntektFar2));
        inntektsmelding.sendInnInnteksmeldingFpfordel(List.of(inntektsmeldingFar1, inntektsmeldingFar2),
                identFar,
                saksnummerFar);

        saksbehandler.hentFagsak(saksnummerFar);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerAktivitetskravBekreftelse.class);

        /*
         * Fellesperioden skal splittes slik at første periode på 8 uker blir avslått og
         * en andre perioden (av splitten) skal stjele dager fra fedrekvoten. Deretter
         * skal fedrekvoten reduseres med 8 uker. (trenger også en split).
         * Bruker overstyrer ettersom uttaket er automatisk innvilget
         */
        overstyrer.erLoggetInnMedRolle(Aktoer.Rolle.OVERSTYRER);
        overstyrer.hentFagsak(saksnummerFar);
        overstyrer.velgSisteBehandling();
        var overstyringUttak = new OverstyrUttaksperioder();
        overstyringUttak.oppdaterMedDataFraBehandling(overstyrer.valgtFagsak, overstyrer.valgtBehandling);
        overstyringUttak.splitPeriode(
                fpStartdatoFar.plusWeeks(3),
                fpStartdatoFar.plusWeeks(19).minusDays(1),
                fpStartdatoFar.plusWeeks(11).minusDays(1));
        overstyringUttak.avslåPeriode(
                fpStartdatoFar.plusWeeks(3),
                fpStartdatoFar.plusWeeks(11).minusDays(1),
                new Kode("IKKE_OPPFYLT_AARSAK", "4050",
                        "§14-13 første ledd bokstav a: Aktivitetskravet arbeid ikke oppfylt"),
                true);
        overstyringUttak.innvilgPeriode(
                fpStartdatoFar.plusWeeks(11),
                fpStartdatoFar.plusWeeks(19).minusDays(1),
                new Kode("INNVILGET_AARSAK", "2031", "§14-12, jf. §14-16: Gradering av kvote/overført kvote"),
                Stønadskonto.FEDREKVOTE);
        overstyringUttak.splitPeriode(
                fpStartdatoFar.plusWeeks(19),
                fpStartdatoFar.plusWeeks(49).minusDays(1),
                fpStartdatoFar.plusWeeks(41).minusDays(1));
        overstyringUttak.innvilgPeriode(
                fpStartdatoFar.plusWeeks(19),
                fpStartdatoFar.plusWeeks(41).minusDays(1),
                new Kode("INNVILGET_AARSAK", "2031", "§14-12, jf. §14-16: Gradering av kvote/overført kvote"));
        overstyringUttak.avslåPeriode(
                fpStartdatoFar.plusWeeks(41),
                fpStartdatoFar.plusWeeks(49).minusDays(1),
                new Kode("IKKE_OPPFYLT_AARSAK", "4002", "§14-9: Ikke stønadsdager igjen på stønadskonto"));
        overstyringUttak.setBegrunnelse("Begrunnelse fra Autotest.");
        overstyrer.overstyr(overstyringUttak);

        saksbehandler.velgSisteBehandling();
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        Saldoer saldoer = saksbehandler.valgtBehandling.getSaldoer();
        verifiser(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp!");
        verifiser(saldoer.getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton MØDREKVOTE er brukt opp!");
        verifiser(saldoer.getStonadskontoer().get(Stønadskonto.FEDREKVOTE).getSaldo() == 0,
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
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(18);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(testscenario,
                fødselsdato, fpStartdatoMor, fpStartdatoFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus("IKKE_HOY");
        saksbehandler.ventTilAvsluttetBehandling();

        /*
         * FAR: Søker som FL. Har frilansinntekt frem til, men ikke inklusiv,
         * skjæringstidspunktet. Søker noe av fellesperioden og deretter hele
         * fedrekvoten
         */
        var identFar = testscenario.personopplysninger().søkerIdent();
        var fordelingFar = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(4).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(4), fpStartdatoFar.plusWeeks(19).minusDays(1)));
        var frilansFom = testscenario.scenariodataDto().inntektskomponentModell().frilansarbeidsforholdperioder()
                .get(0).frilansFom();
        var opptjeningFar = OpptjeningErketyper.medFrilansOpptjening(frilansFom, fpStartdatoFar.minusDays(1));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medAnnenForelder(lagNorskAnnenforeldre(testscenario.personopplysninger().annenpartIdent()))
                .medFordeling(fordelingFar)
                .medOpptjening(opptjeningFar);
        var saksnummerFar = selvbetjening.sendInnSøknad(identFar, søknadFar.build());

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

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerAktivitetskravBekreftelse.class);

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
        var identFar = testscenario.personopplysninger().søkerIdent();
        var identMor = testscenario.personopplysninger().annenpartIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var orgNummerFar1 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var stillingsprosent1 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsavtaler().get(0).stillingsprosent();
        var fpStartdatoFar = Virkedager.helgejustertTilMandag(fødselsdato.plusWeeks(6));
        var fordelingFar = generiskFordeling(
                graderingsperiodeArbeidstaker(FORELDREPENGER,
                        fpStartdatoFar,
                        fpStartdatoFar.plusWeeks(100).minusDays(1),
                        orgNummerFar1,
                        stillingsprosent1));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                        .medRettigheter(RettigheterErketyper.harIkkeAleneomsorgOgAnnenpartIkkeRett())
                        .medFordeling(fordelingFar)
                        .medAnnenForelder(lagNorskAnnenforeldre(identMor));
        var saksnummerFar = selvbetjening.sendInnSøknad(identFar, søknadFar.build());

        var månedsinntektFar1 = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0)
                .beløp();
        var arbeidsforholdIdFar1 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsforholdId();
        InntektsmeldingBuilder inntektsmeldingFar1 = lagInntektsmelding(månedsinntektFar1, identFar, fpStartdatoFar, orgNummerFar1)
                .medArbeidsforholdId(arbeidsforholdIdFar1)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntektFar1));
        var månedsinntektFar2 = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(1)
                .beløp();
        var orgNummerFar2 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1)
                .arbeidsgiverOrgnr();
        var arbeidsforholdIdFar2 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1)
                .arbeidsforholdId();
        var opphørsDatoForRefusjon = fpStartdatoFar.plusMonths(2).minusDays(1);
        InntektsmeldingBuilder inntektsmeldingFar2 = lagInntektsmelding(månedsinntektFar2, identFar, fpStartdatoFar, orgNummerFar2)
                .medArbeidsforholdId(arbeidsforholdIdFar2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntektFar2))
                .medRefusjonsOpphordato(opphørsDatoForRefusjon);

        inntektsmelding.sendInnInnteksmeldingFpfordel(List.of(inntektsmeldingFar1, inntektsmeldingFar2),
                identFar,
                saksnummerFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();

        AvklarArbeidsforholdBekreftelse avklarArbeidsforholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        var ansettelsesperiodeFom = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(2)
                .ansettelsesperiodeFom();
        var tomGyldighetsperiode = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1)
                .ansettelsesperiodeFom();
        avklarArbeidsforholdBekreftelse.bekreftArbeidsforholdErIkkeAktivt(
                "991779493",
                ansettelsesperiodeFom,
                tomGyldighetsperiode.minusDays(1),
                "Arbeidsforholdet skulle vært avsluttet");
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        var avklarFaktaAnnenForeldreHarRett = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAnnenForeldreHarRett.class)
                .setAnnenforelderHarRett(false)
                .setBegrunnelse("Bare far har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerAktivitetskravBekreftelse.class);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);

        /* VERIFISERINGER */
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(Stønadskonto.FORELDREPENGER).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER er 0 dager!");

        var beregningsresultatPerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger().getPerioder();
        verifiser(beregningsresultatPerioder.size() == 3, "Forventer 3 forskjelige beregningsresultatsperioder!");
        verifiser(beregningsresultatPerioder.get(0).getTom().isEqual(opphørsDatoForRefusjon),
                "Forventer at lengden på første peridoe har tom dato som matcher tom dato angitt i IM#2");
        verifiser(beregningsresultatPerioder.get(1).getTom().isEqual(fpStartdatoFar.plusWeeks(40).minusDays(1)),
                "Forventer den andre periden har en varighet på 40 uker.");

        List<BeregningsresultatPeriodeAndel> andelerForAT1 = saksbehandler
                .hentBeregningsresultatPerioderMedAndelIArbeidsforhold(orgNummerFar1);
        List<BeregningsresultatPeriodeAndel> andelerForAT2 = saksbehandler
                .hentBeregningsresultatPerioderMedAndelIArbeidsforhold(orgNummerFar2);
        List<Integer> forventetDagsatsForFørstePeriode = regnUtForventetDagsatsForPeriode(
                List.of(månedsinntektFar1, månedsinntektFar2),
                List.of(40, 100), List.of(true, true));
        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().get(0)
                        .getDagsats() == sumOfList(forventetDagsatsForFørstePeriode),
                "Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel");
        verifiser(andelerForAT1.get(0).getTilSoker().equals(forventetDagsatsForFørstePeriode.get(0)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");
        verifiser(andelerForAT2.get(0).getRefusjon().equals(forventetDagsatsForFørstePeriode.get(1)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til arbeidsgiver");

        List<Integer> forventetDagsatsForAndrePeriode = regnUtForventetDagsatsForPeriode(
                List.of(månedsinntektFar1, månedsinntektFar2),
                List.of(40, 100), List.of(true, false));
        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().get(1)
                        .getDagsats() == sumOfList(forventetDagsatsForAndrePeriode),
                "Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel");
        verifiser(andelerForAT1.get(1).getTilSoker().equals(forventetDagsatsForAndrePeriode.get(0)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");
        verifiser(andelerForAT2.get(1).getTilSoker().equals(forventetDagsatsForAndrePeriode.get(1)),
                "Forventer at dagsatsen matchen den kalkulerte og alt går til søker");

        List<Integer> forventetDagsatsForTredjePeriode = regnUtForventetDagsatsForPeriode(
                List.of(månedsinntektFar1, månedsinntektFar2),
                List.of(40, 0), List.of(true, false));
        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().get(2)
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
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFarOrdinær = fødselsdato.plusWeeks(23);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(testscenario,
                fødselsdato, fpStartdatoMor, fpStartdatoFarOrdinær);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus("IKKE_HOY");
        saksbehandler.ventTilAvsluttetBehandling();

        /*
         * FAR: Søker overføring av mødrekvoten fordi mor er syk innenfor de 6 første
         * uker av mødrekvoten.
         */
        var identFar = testscenario.personopplysninger().søkerIdent();
        var fpStartdatoFarEndret = fødselsdato.plusWeeks(4);
        var fordelingFar = generiskFordeling(
                overføringsperiode(Overføringsårsak.SYKDOM_ANNEN_FORELDER, MØDREKVOTE, fpStartdatoFarEndret,
                        fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fpStartdatoFarOrdinær, fpStartdatoFarOrdinær.plusWeeks(15).minusDays(1)));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(lagNorskAnnenforeldre(testscenario.personopplysninger().annenpartAktørIdent()))
                .medMottatdato(fødselsdato.plusWeeks(6));
        var saksnummerFar = selvbetjening.sendInnSøknad(identFar, søknadFar.build());

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
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        // Løser AP 5084 negativ simulering! Oppretter tilbakekreving og sjekk at den er opprette. Ikke løs det.
        if (forventerNegativSimuleringForBehandling(fpStartdatoFarEndret)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler
                    .hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
            vurderTilbakekrevingVedNegativSimulering.setTilbakekrevingUtenVarsel();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        }


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
        verifiser(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats() == 0,
                "Siden perioden er avslått, forventes det 0 i dagsats.");
        verifiser(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats() == 0,
                "Siden perioden er avslått, forventes det 0 i dagsats.");

        if (forventerNegativSimuleringForBehandling(fpStartdatoFarEndret)) {
            tbksaksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
            tbksaksbehandler.hentSisteBehandling(saksnummerMor);
            tbksaksbehandler.ventTilBehandlingErPåVent();
            verifiser(tbksaksbehandler.valgtBehandling.venteArsakKode.equals("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG"),
                    "Behandling har feil vent årsak.");
        }
    }

    // Hvis perioden som overføres er IKKE i samme måned som dagens dato ELLER
    // Hvis perioden som overføres er i samme måned som dagens dato OG dagens dato er ETTER utbetalingsdagen
    // (20. i alle måneder) så skal det resultere i negativ simulering.
    private Boolean forventerNegativSimuleringForBehandling(LocalDate fpStartdatoFarEndret) {
        return fpStartdatoFarEndret.getMonth() != LocalDate.now().getMonth() ||
                (fpStartdatoFarEndret.getMonth() == LocalDate.now().getMonth() && LocalDate.now().getDayOfMonth() >= 20);
    }

    @Test
    @DisplayName("8: Mor har tvillinger og søker om hele utvidelsen.")
    @Description("Mor føder tvillinger og søker om hele mødrekvoten og fellesperioden, inkludert utvidelse. Far søker " +
            "samtidig uttak av fellesperioden fra da mor starter utvidelsen av fellesperioden. Søker deretter samtidig " +
            "av fedrekvoten, frem til mor er ferdig med fellesperioden, og deretter søker resten av fedrekvoten.")
    public void MorSøkerFor2BarnHvorHunFårBerørtSakPgaFar() {
        TestscenarioDto testscenario = opprettTestscenario("512");

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var identMor = testscenario.personopplysninger().søkerIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(31);
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fpStartdatoFar.minusDays(1)),
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(17).minusDays(1), true, false));
        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordelingMor)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.fødsel(2, fødselsdato))
                .medAnnenForelder(lagNorskAnnenforeldre(testscenario.personopplysninger().annenpartIdent()))
                .medMottatdato(fpStartdatoMor.minusWeeks(3));
        var saksnummerMor = selvbetjening.sendInnSøknad(identMor, søknadMor.build());

        var månedsinntektMor = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0)
                .beløp();
        var orgNummerMor = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var inntektsmeldingMor = lagInntektsmelding(
                månedsinntektMor,
                identMor,
                fpStartdatoMor,
                orgNummerMor);
        inntektsmelding.sendInnInnteksmeldingFpfordel(inntektsmeldingMor,
                identMor,
                saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        Saldoer saldoerFørstgangsbehandling = saksbehandler.valgtBehandling.getSaldoer();
        verifiser(saldoerFørstgangsbehandling.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp!");
        verifiser(saldoerFørstgangsbehandling.getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton MØDREKVOTE er brukt opp!");
        verifiser(saldoerFørstgangsbehandling.getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FELLESPERIODE er brukt opp!");
        verifiser(saldoerFørstgangsbehandling.getStonadskontoer().get(Stønadskonto.FLERBARNSDAGER).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FLERBARNSDAGER er brukt opp!");

        /*
         * FAR: Søker samtidig uttak med flerbansdager. Søker deretter hele fedrekvoten,
         * også samtidig uttak.
         */
        var identFar = testscenario.personopplysninger().annenpartIdent();
        var gjennomsnittFraTreSisteÅreneISigrun = hentNæringsinntektFraSigrun(testscenario, 2018,true);
        var opptjeningFar = medEgenNaeringOpptjening(
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
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordelingFar)
                .medOpptjening(opptjeningFar)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.fødsel(2, fødselsdato))
                .medAnnenForelder(lagNorskAnnenforeldre(identMor));
        var saksnummerFar = selvbetjening.sendInnSøknad(identFar, søknadFar.build());

        var månedsinntektFar = testscenario.scenariodataAnnenpartDto().inntektskomponentModell()
                .inntektsperioder().get(0).beløp();
        var orgNummerFar = testscenario.scenariodataAnnenpartDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var arbeidsforholdIdFar = testscenario.scenariodataAnnenpartDto().arbeidsforholdModell().arbeidsforhold()
                .get(0).arbeidsforholdId();
        var inntektsmeldingFar = lagInntektsmelding(
                månedsinntektFar,
                identFar,
                fpStartdatoFar,
                orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar);
        inntektsmelding.sendInnInnteksmeldingFpfordel(inntektsmeldingFar,
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
        saksbehandler.ventPåOgVelgRevurderingBehandling();
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
        verifiser(saldoerBerørtSak.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp!");
        verifiser(saldoerBerørtSak.getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton MØDREKVOTE er brukt opp!");
        verifiser(saldoerBerørtSak.getStonadskontoer().get(Stønadskonto.FEDREKVOTE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FEDREKVOTE er brukt opp!");
        verifiser(saldoerBerørtSak.getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FELLESPERIODE er brukt opp!");
        verifiser(saldoerBerørtSak.getStonadskontoer().get(Stønadskonto.FLERBARNSDAGER).getSaldo() == 0,
                "Forventer at saldoen for stønadskonton FLERBARNSDAGER er brukt opp!");

        verifiser(saksbehandler.hentAvslåtteUttaksperioder().size() == 1,
                "Forventer at det er 1 avslåtte uttaksperioder");
        verifiser(saksbehandler.valgtBehandling.hentUttaksperiode(6).getPeriodeResultatÅrsak().kodeverk
                        .equalsIgnoreCase("IKKE_OPPFYLT_AARSAK"),
                "Perioden burde være avslått fordi det er ingen stønadsdager igjen på stønadskontoen.");
        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().get(6).getDagsats() == 0,
                "Siden perioden er avslått, forventes det 0 i dagsats i tilkjent ytelse");
    }

    @Test
    @DisplayName("9: Mor søker med dagpenger som grunnlag, besteberegnes automatisk")
    @Description("Mor søker med dagpenger som grunnlag. Kvalifiserer til automatisk besteberegning." +
            "Beregning etter etter §14-7, 3. ledd gir høyere inntekt enn beregning etter §14-7, 1. ledd")
    public void MorSøkerMedDagpengerTest() {
        var testscenario = opprettTestscenario("521");
        var identMor = testscenario.personopplysninger().søkerIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(31).minusDays(1)));

        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(lagNorskAnnenforeldre(testscenario.personopplysninger().annenpartIdent()));
        var saksnummerMor = selvbetjening.sendInnSøknad(identMor, søknadMor.build());

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);

        verifiser(saksbehandler.sjekkOmDetErOpptjeningFremTilSkjæringstidspunktet("DAGPENGER"),
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype DAGPENGER med " +
                        "opptjening frem til skjæringstidspunktet for opptjening.");
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        verifiser(saksbehandler.harHistorikkinnslagForBehandling(HistorikkInnslag.BREV_BESTILT),
                "Brev er bestillt i førstegangsbehandling");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");
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
        var identFar = testscenario.personopplysninger().søkerIdent();
        var omsorgsovertakelsedatoe = LocalDate.now().minusMonths(4);
        var fpStartdatoFar = omsorgsovertakelsedatoe;
        var fpSluttdatoFar = fpStartdatoFar.plusWeeks(46).minusDays(1);
        var fordelingFar = generiskFordeling(
                uttaksperiode(FORELDREPENGER, fpStartdatoFar, fpSluttdatoFar));
        var søknadFar = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, BrukerRolle.FAR, false)
                .medFordeling(fordelingFar)
                .medAnnenForelder(new UkjentForelder())
                .medMottatdato(fpStartdatoFar.minusWeeks(3));
        var saksnummerFar = selvbetjening.sendInnSøknad(identFar, søknadFar.build());

        var månedsinntektFar = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0)
                .beløp();
        var orgNummerFar = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var arbeidsforholdIdFar = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsforholdId();
        var inntektsmeldingFar = lagInntektsmelding(
                månedsinntektFar,
                identFar,
                fpStartdatoFar,
                orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar);
        inntektsmelding.sendInnInnteksmeldingFpfordel(inntektsmeldingFar,
                identFar,
                saksnummerFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBegrunnelse("Adopsjon behandlet av Autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);

        var avklarFaktaAleneomsorgBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAleneomsorgBekreftelse.class)
                .bekreftBrukerHarAleneomsorg()
                .setBegrunnelse("Bekreftelse sendt fra Autotest.");
        saksbehandler.bekreftAksjonspunktbekreftelserer(avklarFaktaAleneomsorgBekreftelse);

        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        verifiser(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().size() == 1,
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
        inntektsmelding.sendInnInnteksmeldingFpfordel(inntektsmeldingEndringFar,
                identFar,
                saksnummerFar);

        // Revurdering / Berørt sak til far
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .leggTilRefusjonGyldighetVurdering(orgNummerFar, false)
                .setBegrunnelse("Refusjonskrav er sendt inn for sent og skal ikke tas med i beregning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        var vurderRefusjonBeregningsgrunnlagBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderRefusjonBeregningsgrunnlagBekreftelse.class);
        vurderRefusjonBeregningsgrunnlagBekreftelse
                .setFastsattRefusjonFomForAllePerioder(LocalDate.now().minusMonths(3))
                .setBegrunnelse("Fordi autotest sier det!");
        saksbehandler.bekreftAksjonspunkt(vurderRefusjonBeregningsgrunnlagBekreftelse);

        var vurderTilbakekrevingVedNegativSimulering = saksbehandler
                .hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
        vurderTilbakekrevingVedNegativSimulering.setTilbakekrevingUtenVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);

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
        inntektsmelding.sendInnInnteksmeldingFpfordel(inntektsmeldingEndringFar2,
                identFar,
                saksnummerFar);

        saksbehandler.hentFagsak(saksnummerFar);
        verifiser(saksbehandler.behandlinger.size() == 2, "Fagsaken har mer enn én revurdering.");
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.BEHANDLINGEN_ER_FLYTTET);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse2
                .leggTilRefusjonGyldighetVurdering(orgNummerFar, false)
                .setBegrunnelse("Refusjonskrav er sendt inn for sent og skal ikke tas med i beregning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse2);

        var vurderRefusjonBeregningsgrunnlagBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderRefusjonBeregningsgrunnlagBekreftelse.class);
        vurderRefusjonBeregningsgrunnlagBekreftelse2
                .setFastsattRefusjonFomForAllePerioder(LocalDate.now().minusMonths(3))
                .setBegrunnelse("Fordi autotest sier det!");
        saksbehandler.bekreftAksjonspunkt(vurderRefusjonBeregningsgrunnlagBekreftelse2);

        var vurderTilbakekrevingVedNegativSimulering2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
        vurderTilbakekrevingVedNegativSimulering2.setTilbakekrevingUtenVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering2);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, true);

        var perioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        if (LocalDate.now().getDayOfMonth() == 1) {
            verifiser(perioder.size() == 2,
                    "Foventer at den berørte saken har 2 tilkjent ytelse perioder, faktisk antall var: " + perioder.size());
            verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(0), 0),
                    "Forventer at hele summen utbetales til søker i første periode, og derfor ingenting til arbeidsgiver!");
            verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(1), 100),
                    "Forventer at hele summen utbetales til AG i andre periode, og derfor ingenting til søker!");

        } else {
            verifiser(perioder.size() == 3,
                    "Foventer at den berørte saken har 3 tilkjent ytelse perioder, faktisk antall var: " + perioder.size());
            verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(0), 0),
                    "Forventer at hele summen utbetales til søker i første periode, og derfor ingenting til arbeidsgiver!");
            verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(1), 0),
                    "Forventer at hele summen utbetales til AG i andre periode, og derfor ingenting til søker!");
            verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(2), 100),
                    "Forventer at hele summen utbetales til AG i tredje periode, og derfor ingenting til søker!");
        }
    }

    @Test
    @DisplayName("11: Far søker adopsjon hvor han søker hele fedrekvoten og fellesperiode, og får berørt sak pga mor")
    @Description("Far søker adopsjon hvor han søker hele fedrekvoten og fellesperioden. Mor søker noe av mødrekvoten midt " +
            "i fars periode med fullt uttak. Deretter søker mor 9 uker av fellesperioden med samtidig uttak. Far får " +
            "berørt sak hvor han får avkortet fellesperidoen på slutten og redusert perioder hvor mor søker samtidig uttak")
    public void FarSøkerAdopsjonOgMorMødrekvoteMidtIFarsOgDeretterSamtidigUttakAvFellesperidoe() {
        TestscenarioDto testscenario = opprettTestscenario("563");

        /* FAR */
        var identFar = testscenario.personopplysninger().søkerIdent();
        var identMor = testscenario.personopplysninger().annenpartIdent();
        var omsorgsovertakelsedatoe = LocalDate.now().minusWeeks(4);
        var fpStartdatoFar = omsorgsovertakelsedatoe;
        var fellesperiodeStartFar = fpStartdatoFar.plusWeeks(15);
        var fellesperiodeSluttFar = fellesperiodeStartFar.plusWeeks(16).minusDays(1);
        var fordelingFar = generiskFordeling(
                uttaksperiode(FEDREKVOTE, fpStartdatoFar, fellesperiodeStartFar.minusDays(1)),
                uttaksperiode(FELLESPERIODE, fellesperiodeStartFar, fellesperiodeSluttFar));
        var søknadFar = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, BrukerRolle.FAR, false)
                .medFordeling(fordelingFar)
                .medAnnenForelder(lagNorskAnnenforeldre(identMor));
        var saksnummerFar = selvbetjening.sendInnSøknad(identFar, søknadFar.build());

        var månedsinntektFar = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0)
                .beløp();
        var orgNummerFar = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var arbeidsforholdIdFar = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsforholdId();
        var inntektsmeldingFar = lagInntektsmelding(
                månedsinntektFar,
                identFar,
                fpStartdatoFar,
                orgNummerFar)
                .medArbeidsforholdId(arbeidsforholdIdFar);
        inntektsmelding.sendInnInnteksmeldingFpfordel(inntektsmeldingFar,
                identFar,
                saksnummerFar);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBegrunnelse("Adopsjon behandlet av Autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);

        var avklarFaktaAnnenForeldreHarRett = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAnnenForeldreHarRett.class)
                .setAnnenforelderHarRett(true)
                .setBegrunnelse("Både far og mor har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerAktivitetskravBekreftelse.class);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().size(), 2,
                "Forventer at det er to perioder i tilkjent ytelse. En for fedrekvote og en for fellesperioden");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");

        /* MOR */
        var fpStartdatoMor = fpStartdatoFar.plusWeeks(7);
        var fellesperiodeStartMor = fpStartdatoMor.plusWeeks(4);
        var fellesperiodeSluttMor = fellesperiodeStartMor.plusWeeks(9).minusDays(1);
        var fordelingMor = generiskFordeling(
                uttaksperiode(MØDREKVOTE, fpStartdatoMor, fellesperiodeStartMor.minusDays(1), false, false),
                uttaksperiode(FELLESPERIODE, fellesperiodeStartMor, fellesperiodeSluttMor, false, true, 40));
        var søknadMor = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, BrukerRolle.MOR, false)
                .medFordeling(fordelingMor)
                .medAnnenForelder(lagNorskAnnenforeldre(identFar));
        var saksnummerMor = selvbetjening.sendInnSøknad(identMor, søknadMor.build());

        var månedsinntektMor = testscenario.scenariodataAnnenpartDto().inntektskomponentModell()
                .inntektsperioder().get(0).beløp();
        var orgNummerMor = testscenario.scenariodataAnnenpartDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var arbeidsforholdIdMor = testscenario.scenariodataAnnenpartDto().arbeidsforholdModell().arbeidsforhold()
                .get(0).arbeidsforholdId();
        var inntektsmeldingMor = lagInntektsmelding(
                månedsinntektMor,
                identMor,
                fpStartdatoMor,
                orgNummerMor)
                .medArbeidsforholdId(arbeidsforholdIdMor);
        inntektsmelding.sendInnInnteksmeldingFpfordel(inntektsmeldingMor,
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
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        List<UttakResultatPeriode> avslåttePerioder = saksbehandler.hentAvslåtteUttaksperioder();
        verifiserLikhet(avslåttePerioder.size(), 1,
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

        var fomSistePeriode = fastsettUttaksperioderManueltBekreftelseMor.getPerioder()
                .stream().sorted(Comparator.comparing(UttakResultatPeriode::getFom))
                .collect(Collectors.toList())
                .get(fastsettUttaksperioderManueltBekreftelseMor.getPerioder().size() - 1).getFom();
        fastsettUttaksperioderManueltBekreftelseMor.innvilgPeriode(
                fellesperiodeSluttMor.plusDays(1),
                fomSistePeriode.minusDays(1),
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        var saldoer = saksbehandler
                .hentSaldoerGittUttaksperioder(fastsettUttaksperioderManueltBekreftelseMor.getPerioder());
        var disponibleFellesdager = saldoer.getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo();
        var sisteDagMedFellesperiode = Virkedager.plusVirkedager(fomSistePeriode.plusDays(1), Math.abs(disponibleFellesdager));
        fastsettUttaksperioderManueltBekreftelseMor.splitPeriode(
                fomSistePeriode,
                fellesperiodeSluttFar, sisteDagMedFellesperiode);
        fastsettUttaksperioderManueltBekreftelseMor.innvilgPeriode(
                fomSistePeriode,
                sisteDagMedFellesperiode,
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
        fastsettUttaksperioderManueltBekreftelseMor.avslåPeriode(
                sisteDagMedFellesperiode.plusDays(1),
                fellesperiodeSluttFar,
                new Kode("IKKE_OPPFYLT_AARSAK", "4002", "§14-9: Ikke stønadsdager igjen på stønadskonto"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelseMor);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, true);

        beslutter.ventTilFagsakLøpende();

        // verifisering i uttak
        verifiserLikhet(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo(), 0,
                "Forventer at saldoen for stønadskonton FELLESPERIODE er brukt opp (dvs = 0)!");
        verifiserLikhet(saksbehandler.hentAvslåtteUttaksperioder().size(), 2,
                "Forventer at det er 2 avslåtte uttaksperioder");

        // verifisering i tilkjent ytelse
        BeregningsresultatMedUttaksplan tilkjentYtelsePerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger();
        verifiserLikhet(tilkjentYtelsePerioder.getPerioder().get(1).getDagsats(), 0,
                "Siden perioden er avslått, forventes det 0 i dagsats.");
        verifiserLikhet(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats(),
                (int) Math.round(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats() * 0.6),
                "Forventer at dagsatsen blir redusert fra 100% til 60% for 3 periode i tilkjent ytelse.");
        verifiserLikhet(tilkjentYtelsePerioder.getPerioder().get(6).getDagsats(), 0,
                "Siden perioden er avslått, forventes det 0 i dagsats.");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");
    }

    @Test
    @DisplayName("12: Mor søker fødsel og mottar sykepenger som er under 1/2 G")
    @Description("12: Mor søker fødsel og mottar sykepenger som er under 1/2 G. Har ingen inntektskilder. " +
            "Hun har for lite inntekt og har dermed ikke rett til foreldrepenger – beregning avvist søknadden.")
    public void morSøkerFødselMottarForLite() {
        var testscenario = opprettTestscenario("70");
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(testscenario.personopplysninger().annenpartIdent()));
        var saksnummer = selvbetjening.sendInnSøknad(søkerIdent, søknad.build());

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


    @Test
    @DisplayName("13: Mor søker på termin og får innvilget, men etter termin mottas det en dødfødselshendelse")
    @Description("13: Mør søker på termin og blir automatisk behanldet (innvilget). En uke etter terminen mottas det" +
            "en dødfødselshendelse hvor mor får avslag etter det 6 uken av mødrekvoten.")
    public void morSøkerTerminFårInnvilgetOgSåKommerDetEnDødfødselEtterTermin() {
        var testscenario = opprettTestscenario("55");
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var termindato = LocalDate.now().minusWeeks(2);
        var fpStartdatoMor = termindato.minusWeeks(3);

        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(testscenario.personopplysninger().annenpartIdent()))
                .medMottatdato(termindato.minusMonths(2));
        var saksnummer = selvbetjening.sendInnSøknad(søkerIdent, søknad.build());

        var månedsinntektMor = testscenario.scenariodataDto().inntektskomponentModell()
                .inntektsperioder().get(0).beløp();
        var orgNummerMor = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var inntektsmeldingMor = lagInntektsmelding(
                månedsinntektMor,
                søkerIdent,
                fpStartdatoMor,
                orgNummerMor);
        inntektsmelding.sendInnInnteksmeldingFpfordel(inntektsmeldingMor,
                søkerIdent,
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        verifiserLikhet(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo(), 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp!");
        verifiserLikhet(saldoer.getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo(), 0,
                "Forventer at saldoen for stønadskonton MØDREKVOTE er brukt opp!");
        verifiserLikhet(saldoer.getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo(), 0,
                "Forventer at saldoen for stønadskonton FELLESPERIODE er brukt opp!");

        var differanseFødselTermin = 7;
        var dødfødselshendelseDto = new DødfødselhendelseDto("OPPRETTET", null, søkerIdent,
                termindato.plusDays(differanseFødselTermin));
        fordel.opprettHendelsePåKafka(dødfødselshendelseDto);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        verifiser(saksbehandler.valgtBehandling.getBehandlingÅrsaker().get(0).getBehandlingArsakType().kode
                        .equalsIgnoreCase("RE-HENDELSE-DØDFØD"),
                "Foventer at revurderingen har årsakskode RE_HENDELSE_DØDFØDSEL.");

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.avslåManuellePerioderMedPeriodeResultatÅrsak(
                new Kode("IKKE_OPPFYLT_AARSAK", "4072", "§14-9 sjuende ledd: Barnet er dødt"));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(FastsetteUttakKontrollerOpplysningerOmDødDto.class);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true);

        var saldoerRevurdering = saksbehandler.valgtBehandling.getSaldoer();
        verifiserLikhet(saldoerRevurdering.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo(), 0,
                "Forventer at saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL er brukt opp!");
        verifiserLikhet(saldoerRevurdering.getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo(), 45,
                "Forventer at saldoen for stønadskonton MØDREKVOTE er brukt opp!");
        verifiserLikhet(saldoerRevurdering.getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo(), 75,
                "Forventer at saldoen for stønadskonton FELLESPERIODE er brukt opp!");


        var uttakResultatPerioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        var uttaksperiode_0 = uttakResultatPerioder.get(0);
        var uttaksperiode_1 = uttakResultatPerioder.get(1);
        verifiser(uttaksperiode_0.getPeriodeResultatType().kode.equalsIgnoreCase("INNVILGET"),
                "Forventer at første periode er innvilget");
        verifiser(uttaksperiode_0.getPeriodeType().kode.equalsIgnoreCase(FELLESPERIODE.name()),
                "Forventer at første periode er FELLESPERIODE pga dødfødsel etter termin.");
        verifiserLikhet(uttaksperiode_0.getFom(), uttaksperiode_1.getFom().minusDays(differanseFødselTermin),
                "Verifiserer at antall dager etter termin fylles med fellesperioden.");


        verifiser(uttaksperiode_1.getPeriodeResultatType().kode.equalsIgnoreCase("INNVILGET"),
                "Forventer at andre periode er innvilget");
        verifiser(uttaksperiode_1.getPeriodeType().kode.equalsIgnoreCase(FORELDREPENGER_FØR_FØDSEL.name()),
                "Forventer at første periode er FORELDREPENGER_FØR_FØDSEL pga dødfødsel etter termin.");
        verifiser(uttaksperiode_1.getAktiviteter().get(0).getTrekkdagerDesimaler().compareTo(BigDecimal.valueOf(3 * 5)) == 0,
        "Verifiser at søker tar ut hele FORELDREPENGER_FØR_FØDSEL kvoten.");

        var uttaksperiode_2 = uttakResultatPerioder.get(2);
        verifiser(uttaksperiode_2.getPeriodeResultatType().kode.equalsIgnoreCase("INNVILGET"),
                "Forventer at tredje periode er innvilget");
        verifiser(uttaksperiode_2.getPeriodeType().kode.equalsIgnoreCase(MØDREKVOTE.name()),
                "Forventer at første periode er MØDREKVTOEN pga dødfødsel etter termin.");
        verifiser(uttaksperiode_2.getAktiviteter().get(0).getTrekkdagerDesimaler().compareTo(BigDecimal.valueOf(6 * 5)) == 0,
                "Forventer at det tas ut 6 uker av den gjenværende delen av stønadsperioden.");

        verifiserLikhet(saksbehandler.hentAvslåtteUttaksperioder().size(), 2,
                "Forventer at det er 2 avslåtte uttaksperioder pga dødfødsel");
        verifiser(uttakResultatPerioder.get(3).getPeriodeResultatÅrsak().kodeverk.equalsIgnoreCase("IKKE_OPPFYLT_AARSAK"),
                "Perioden burde være avslått fordi det er mottatt dødfødselshendelse");
        verifiser(uttakResultatPerioder.get(4).getPeriodeResultatÅrsak().kodeverk.equalsIgnoreCase("IKKE_OPPFYLT_AARSAK"),
                "Perioden burde være avslått fordi det er mottatt dødfødselshendelse");
    }


    private Long sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(TestscenarioDto testscenario,
            LocalDate fødselsdato,
            LocalDate fpStartdatoMor,
            LocalDate fpStartdatoFar) {
        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var identMor = testscenario.personopplysninger().annenpartIdent();
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fpStartdatoFar.minusDays(1)));
        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(testscenario.personopplysninger().søkerIdent()))
                .medFordeling(fordelingMor)
                .medMottatdato(fpStartdatoMor.minusWeeks(4));
        var saksnummerMor = selvbetjening.sendInnSøknad(identMor, søknadMor.build());

        var månedsinntektMor = testscenario.scenariodataAnnenpartDto().inntektskomponentModell()
                .inntektsperioder().get(0).beløp();
        var orgNummerMor = testscenario.scenariodataAnnenpartDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var inntektsmeldingMor = lagInntektsmelding(
                månedsinntektMor,
                identMor,
                fpStartdatoMor,
                orgNummerMor);
        inntektsmelding.sendInnInnteksmeldingFpfordel(inntektsmeldingMor,
                identMor,
                saksnummerMor);

        return saksnummerMor;
    }

    private NorskForelder lagNorskAnnenforeldre(String indent) {
        return new NorskForelder(new Fødselsnummer(indent), "");
    }

}
