package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OppholdÅrsak.FEDREKVOTE_ANNEN_FORELDER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OppholdÅrsak.FELLESPERIODE_ANNEN_FORELDER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OppholdÅrsak.MØDREKVOTE_ANNEN_FORELDER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FEDREKVOTE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.oppholdsperiode;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.makeInntektsmeldingFromTestscenario;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.makeInntektsmeldingFromTestscenarioMedIdent;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadEndringErketyper.lagEndringssøknad;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugFritekst;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandlingsliste;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EndringssøknadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.perioder.UttaksperiodeBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.erketyper.RelasjonTilBarnetErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerManueltOpprettetRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerBosattBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrFodselsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.ObjectFactory;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("foreldrepenger")
@Tag("fluoritt")
public class MorOgFarSammen extends ForeldrepengerTestBase {

    private static final Logger logger = LoggerFactory.getLogger(MorOgFarSammen.class);

    @Test
    @DisplayName("Mor og far koblet sak, kant til kant")
    @Description("Mor søker, får AP slik at behandling stopper opp. Far sender søknad og blir satt på vent. Behandler ferdig mor sin søknad (positivt vedtak)." +
            "Behandler far sin søknad (positivt vedtak). Ingen overlapp. Verifiserer at sakene er koblet og at det ikke opprettes revurdering berørt sak.")
    public void morOgFar_fødsel_ettArbeidsforholdHver_kobletsak_kantTilKant() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("82");
        String morIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String farIdent = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpstartdatoMor = fødselsdato.minusWeeks(3);
        LocalDate fpstartdatoFar = fødselsdato.plusWeeks(6);

        long saksnummerMor = sendInnSøknadMorMedAksjonspunkt(testscenario, fødselsdato);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, morIdent, fpstartdatoMor, false);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, morAktørId, morIdent, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        verifiserLikhet(saksbehandler.valgtFagsak.hentStatus().kode, "UBEH", "Fagsakstatus sak mor");
        long saksnummerFar = sendInnSøknadFar(testscenario, fødselsdato, fpstartdatoFar);
        saksbehandler.hentFagsak(saksnummerFar);
        debugLoggBehandlingsliste(saksbehandler.behandlinger);
        verifiserLikhet(saksbehandler.valgtFagsak.hentStatus().kode, "UBEH", "Fagsakstatus sak far");
        verifiser(saksbehandler.valgtBehandling.erSattPåVent(), "Behandlingen er ikke på vent.");
        //Behandle ferdig mor sin sak
        saksbehandler.hentFagsak(saksnummerMor);
        debugLoggBehandlingsliste("mors behandlinger", saksbehandler.behandlinger);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.godkjennAllePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);
        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummerMor);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_UTTAKPERIODER));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        beslutter.ventTilFagsakLøpende();
        verifiserLikhet(beslutter.valgtFagsak.hentStatus().kode, "LOP", "Fagsakstatus");

        debugFritekst("Ferdig med behandling mor");
        //Behandle ferdig far sin sak
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farIdent, fpstartdatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farIdent, saksnummerFar);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        verifiser(saksbehandler.sakErKobletTilAnnenpart(), "Saken er ikke koblet til en annen behandling");
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        debugFritekst("Ferdig med behandling far");
        // Verifisere at det ikke er blitt opprettet revurdering berørt sak på mor
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        verifiser(saksbehandler.behandlinger.size() == 1, "Mor har for mange behandlinger.");
    }

    @Test
    @DisplayName("Mor og far koblet sak, kant til kant. Automatisk innvilget utsettelse")
    @Description("Mor søker utsettelse pga arbeid og får automatisk innvilget, far søker kant i kant med mor og automatisk innvilges")
    public void morOgFar_fødsel_ettArbeidsforholdHver_kobletsak_kantTilKant_med_automatisk_utsettelse() {
        var testscenario = opprettTestscenario("140");
        var morIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        var morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var farIdent = testscenario.getPersonopplysninger().getAnnenpartIdent();
        var farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var fpstartdatoMor = fødselsdato.minusWeeks(3);

        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10).minusDays(1)),
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødselsdato.plusWeeks(10), fødselsdato.plusWeeks(11).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(11), fødselsdato.plusWeeks(12).minusDays(1)));
        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, morAktørId, SøkersRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(farAktørId);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        var saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), morAktørId, morIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpstartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, morAktørId, morIdent, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();

        var fordelingFar = generiskFordeling(
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødselsdato.plusWeeks(12), fødselsdato.plusWeeks(13).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fødselsdato.plusWeeks(13), fødselsdato.plusWeeks(15).minusDays(1)));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, farAktørId, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(morAktørId);
        var saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        var inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farIdent, fødselsdato.plusWeeks(12), true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farIdent, saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();
    }

    @Test
    @DisplayName("Far og mor søker fødsel med overlappende uttaksperiode")
    @Description("Mor søker og får innvilget. Far søker med to uker overlapp med mor (stjeling). Far får innvilget. " +
            "Berørt sak opprettet mor. Siste periode blir spittet i to og siste del blir avlsått. Det opprettes ikke" +
            "berørt sak på far.")
    public void farOgMor_fødsel_ettArbeidsforholdHver_overlappendePeriode() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("82");
        String morIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String farIdent = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpstartdatoMor = fødselsdato.minusWeeks(3);
        LocalDate fpStartdatoFar = fødselsdato.plusWeeks(8);
        // MOR
        Fordeling fordelingMor = new Fordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> periodeMor = fordelingMor.getPerioder();
        periodeMor.add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)));
        periodeMor.add(uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10).minusDays(1)));
        ForeldrepengerBuilder søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, morAktørId, SøkersRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(farAktørId);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), morAktørId, morIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, morIdent, fpstartdatoMor, false);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, morAktørId, morIdent, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        verifiserLikhet(saksbehandler.valgtBehandling.status.kode, "AVSLU", "Mors behandling er ikke ferdigbehandlet.");
        debugFritekst("Ferdig med første behandling mor");
        verifiser(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderForSøker().size() == 3, "Antall perioder for mor er ikke 3.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().size() == 4, "Feil antall stønadskontoer.");
        // FAR
        Fordeling fordelingFar = new Fordeling();
        fordelingFar.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> periodeFar = fordelingFar.getPerioder();
        periodeFar.add(uttaksperiode(FEDREKVOTE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(12)));
        ForeldrepengerBuilder søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, farAktørId, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(morAktørId);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farIdent, fpStartdatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farIdent, saksnummerFar);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        verifiserLikhet(saksbehandler.valgtBehandling.status.kode, "AVSLU", "Fars behandling er ikke ferdigbehandlet.");
        debugFritekst("Ferdig med første behandling til far");
        verifiser(saksbehandler.sakErKobletTilAnnenpart(), "Saken er ikke koblet til mor sin behandling");
        verifiser(saksbehandler.valgtBehandling.hentUttaksperioder().size() == 2, "Antall perioder er ikke 2.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().size() == 4, "Feil antall stønadskontoer.");
        // Revurdering berørt sak mor
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgRevurderingBehandling();
        debugFritekst("Revurdering berørt sak opprettet på mor.");
        verifiser(saksbehandler.sakErKobletTilAnnenpart(), "Saken er ikke koblet til en far sin behandling");
        verifiser(saksbehandler.valgtBehandling.hentUttaksperioder().size() == 4, "Feil i splitting av mors perioder.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FEDREKVOTE).getSaldo() > 0, "Feil i stønadsdager fedrekvote.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(MØDREKVOTE).getSaldo() > 0, "Feil i stønadsdager mødrekvote.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FELLESPERIODE).getSaldo() == 80, "Feil i stønadsdager fellesperiode.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo() >= 0, "Feil i stønadsdager FPFF.");
        // verifiser ikke berørt sak far
        saksbehandler.hentFagsak(saksnummerFar);
        verifiser(saksbehandler.behandlinger.size() == 1, "Feil antall behandlinger i fagsak til far.");

    }

    @Disabled
    @Test
    @DisplayName("Mor og far koblet sak med oppholdsperiode i søknad")
    @Description("Mor og far sender inn søknader med oppholdsperiode for den andre parten. Periodene er kant til kant. " +
            "Berørt sak opprettes fordi periodene anses som overlapp. Verifiserer på like trekkdager i siste behandling hos begge.")
    public void morOgFar_berørtSak_oppholdsperioder() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("82");

        String morIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String farIdent = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);
        LocalDate fpStartdatoFar = fødselsdato.plusWeeks(6);

        // Fordeling og søknad mor
        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        perioderMor.add(uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        perioderMor.add(oppholdsperiode(FEDREKVOTE_ANNEN_FORELDER, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(8).minusDays(1)));
        perioderMor.add(uttaksperiode(MØDREKVOTE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(12).minusDays(1)));
        ForeldrepengerBuilder søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, morAktørId, SøkersRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(farAktørId);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdatoMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        debugFritekst("Ferdig med første behandling til mor");

        // Fordeling og søknad far
        Fordeling fordelingFar = new ObjectFactory().createFordeling();
        fordelingFar.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderFar = fordelingFar.getPerioder();
        perioderFar.add(uttaksperiode(FEDREKVOTE, fpStartdatoFar, fpStartdatoFar.plusWeeks(2).minusDays(1)));
        perioderFar.add(oppholdsperiode(MØDREKVOTE_ANNEN_FORELDER, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(12).minusDays(1)));
        perioderFar.add(uttaksperiode(FEDREKVOTE, fødselsdato.plusWeeks(12), fødselsdato.plusWeeks(18).minusDays(1)));

        ForeldrepengerBuilder søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, farAktørId, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(morAktørId);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingerFar = lagInntektsmelding(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                farIdent,
                fpStartdatoFar,
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldingerFar,
                farAktørId,
                farIdent,
                saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgFørstegangsbehandling();
        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        debugFritekst("Ferdig med første behandling til far");

        saksbehandler.hentFagsak(saksnummerMor);
        debugLoggBehandlingsliste("Mors behandlinger", saksbehandler.behandlinger);
        saksbehandler.ventTilFagsakLøpende();
        saksbehandler.velgRevurderingBehandling();
        debugLoggBehandling(saksbehandler.valgtBehandling);
        verifiser(saksbehandler.sakErKobletTilAnnenpart(), "Mor sin sak ikke koblet til far sin sak.");
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.UENDRET_UTFALL);
        verifiser(saksbehandler.behandlinger.size() == 3, "Feil antall behandlinger hos mor");
        Behandling sistebehandling = saksbehandler.behandlinger.get(2);
        saksbehandler.velgBehandling(sistebehandling);
        int morDispMødrekvote = saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(MØDREKVOTE).getSaldo();
        int morDispFedrekvote = saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FEDREKVOTE).getSaldo();
        int morDispFellesperiode = saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FELLESPERIODE).getSaldo();
        int morDispFPFF = saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo();

        saksbehandler.hentFagsak(saksnummerFar);
        verifiser(saksbehandler.behandlinger.size() == 2, "Feil antall behandlinger hos far");
        saksbehandler.velgRevurderingBehandling();
        debugLoggBehandling(saksbehandler.valgtBehandling);
        verifiser(saksbehandler.sakErKobletTilAnnenpart(), "Far sin sak ikke koblet til mor sin sak.");
        int farDispMødrekvote = saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(MØDREKVOTE).getSaldo();
        int farDispFedrekvote = saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FEDREKVOTE).getSaldo();
        int farDispFellesperiode = saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FELLESPERIODE).getSaldo();
        int farDispFPFF = saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo();

        verifiser(morDispMødrekvote == farDispMødrekvote, "Partene har forskjellig saldo for Mødrekvote");
        verifiser(morDispFedrekvote == farDispFedrekvote, "Partene har forskjellig saldo for Fedrekvote");
        verifiser(morDispFellesperiode == farDispFellesperiode, "Partene har forskjellig saldo for Fellesperiode");
        verifiser(morDispFPFF == farDispFPFF, "Partene har forskjellig saldo for Foreldrepenger før fødsel");
    }


    @Test
    @DisplayName("Koblet sak endringssøknad ingen endring")
    @Description("Sender inn søknad mor. Sender inn søknad far uten overlapp. Sender inn endringssøknad mor som er lik " +
            "førstegangsbehandlingen. Verifiserer at det ikke blir berørt sak på far.")
    public void KobletSakIngenEndring() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("84");
        long saksnummerMor = behandleSøknadForMorUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));
        long saksnummerFar = behandleSøknadForFarUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));

        saksbehandler.hentFagsak(saksnummerMor);
        verifiser(!saksbehandler.harRevurderingBehandling(), "Mor har fått revurdering uten endringssøknad eller endring i behandling");

        sendInnEndringssøknadforMor(testscenario, saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);

        saksbehandler.velgRevurderingBehandling();

        VurderSoknadsfristForeldrepengerBekreftelse vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);

        beslutter.hentFagsak(saksnummerMor);
        beslutter.velgRevurderingBehandling();
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRIST_FORELDREPENGER));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        saksbehandler.hentFagsak(saksnummerFar);
        verifiser(!saksbehandler.harRevurderingBehandling(), "Fars behandling fikk revurdering selv uten endringer i mors behandling av endringssøknaden");
    }


    @Test
    @DisplayName("Mor får revurdering fra endringssøknad vedtak opphører")
    @Description("Mor får revurdering fra endringssøknad vedtak opphører - far får revurdering")
    public void BerørtSakOpphør() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("84");
        long saksnummerMor = behandleSøknadForMorUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));
        long saksnummerFar = behandleSøknadForFarUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));

        sendInnEndringssøknadforMor(testscenario, saksnummerMor);

        overstyrer.erLoggetInnMedRolle(Rolle.OVERSTYRER);
        overstyrer.hentFagsak(saksnummerMor);
        overstyrer.velgRevurderingBehandling();

        OverstyrFodselsvilkaaret overstyr = new OverstyrFodselsvilkaaret();
        overstyr.avvis(overstyrer.kodeverk.Avslagsårsak.get("FP_VK_1").getKode("1003" /*Søker er far */));
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        VurderSoknadsfristForeldrepengerBekreftelse vurderSoknadsfristForeldrepengerBekreftelse = overstyrer.hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
        overstyrer.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummerMor);
        beslutter.velgRevurderingBehandling();

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRIST_FORELDREPENGER))
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.OVERSTYRING_AV_FØDSELSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        saksbehandler.hentFagsak(saksnummerFar);
        verifiser(saksbehandler.harRevurderingBehandling(), "Fars behandling fikk ikke revurdering selv med opphørt vedtak i mors behandling av endringssøknaden");
    }

    @Test
    @DisplayName("Mor får revurdering fra endringssøknad endring av uttak")
    @Description("Mor får revurdering fra endringssøknad endring av uttak - fører til revurdering hos far")
    public void BerørtSakEndringAvUttak() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("84");
        long saksnummerMor = behandleSøknadForMorUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));
        long saksnummerFar = behandleSøknadForFarUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));
        sendInnEndringssøknadforMorMedEndretUttak(testscenario, saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);

        saksbehandler.velgRevurderingBehandling();

        var vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);


        logger.debug("Date start: " + LocalDate.now().minusMonths(4).plusWeeks(6).plusDays(1));
        logger.debug("Date start: " + LocalDate.now().minusMonths(4).plusWeeks(10).minusDays(2));

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);

        beslutter.hentFagsak(saksnummerMor);
        beslutter.velgRevurderingBehandling();
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRIST_FORELDREPENGER));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        saksbehandler.hentFagsak(saksnummerFar);
        verifiser(saksbehandler.harRevurderingBehandling(), "Fars behandling fikk ikke revurdering selv uten med endringer i mors behandling av endringssøknaden");
    }

    @Test
    @DisplayName("Koblet sak mor søker etter far og sniker i køen")
    @Description("Far søker. Blir satt på vent pga for tidlig søknad. Mor søker og får innvilget. Oppretter manuell " +
            "revurdering på mor. ")
    public void KobletSakMorSøkerEtterFar() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("84");
        LocalDate fødselsdato = LocalDate.now().minusDays(15);
        behandleSøknadForFarSattPåVent(testscenario, fødselsdato);
        long saksnummerMor = behandleSøknadForMorUregistrert(testscenario, fødselsdato);

        saksbehandler.hentFagsak(saksnummerMor);

        saksbehandler.opprettBehandlingRevurdering("RE-FRDLING");
        saksbehandler.velgRevurderingBehandling();

        saksbehandler.bekreftAksjonspunktbekreftelserer(
                saksbehandler.hentAksjonspunktbekreftelse(KontrollerManueltOpprettetRevurdering.class),
                saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakManueltBekreftelse.class));
        saksbehandler.ventTilAvsluttetBehandling();
    }

    //TODO (sagrada) skriv ferdig testen
    @Disabled
    @Test
    @DisplayName("Koblet sak med flerbarnsdager og samtidig uttak")
    @Description("Mor søker med blabla. Far søker med blabla.")
    public void kobletSakMedFlerbarnsdagerOgSamtidigUttak() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("85");

        String morIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String morAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String farIdent = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String farAktørIdent = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        LocalDate fødsel = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpstartdatoMor = fødsel.minusWeeks(3);

        //Søknad mor
        ForeldrepengerBuilder søknadMor = lagSøknadForeldrepengerFødsel(fødsel, morAktørIdent, SøkersRolle.MOR)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(2, fødsel))
                .medFordeling(fordelingMorSamtidigUttakFlerbarnsdager(fpstartdatoMor, fødsel))
                .medAnnenForelder(farAktørIdent);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingerMor = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpstartdatoMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldingerMor,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        //Søknad far
        Fordeling fordelingFar = fordelingFarSamtidigUttakFlerbarnsdager(fødsel);

        ForeldrepengerBuilder søknadFar = lagSøknadForeldrepengerFødsel(fødsel, farAktørIdent, SøkersRolle.FAR)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(2, fødsel))
                .medFordeling(fordelingFar);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørIdent, farIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingerFar = lagInntektsmelding(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                farIdent,
                fødsel.plusWeeks(2),
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldingerFar,
                farAktørIdent,
                farIdent,
                saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.godkjennAlleManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummerFar);
        FatterVedtakBekreftelse fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_UTTAKPERIODER));
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        // Behandler revurdering berørt sak mor
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgRevurderingBehandling();
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse1 = saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse1.godkjennAlleManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse1);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        beslutter.hentFagsak(saksnummerMor);
        beslutter.velgRevurderingBehandling();
        FatterVedtakBekreftelse fatterVedtakBekreftelse1 = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse1.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_UTTAKPERIODER));
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse1);

        //TODO: legg til valideringer før testen taes i bruk.


    }

    public Fordeling fordelingMorSamtidigUttakFlerbarnsdager(LocalDate fpstartdatoMor, LocalDate fødsel) {
        Fordeling fordelingMor = new Fordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpstartdatoMor, fødsel.minusDays(1)));
        perioderMor.add(uttaksperiode(MØDREKVOTE, fødsel, fødsel.plusWeeks(6).minusDays(1)));
        perioderMor.add(uttaksperiode(MØDREKVOTE, fødsel.plusWeeks(6), fødsel.plusWeeks(9).minusDays(1)));
        perioderMor.add(uttaksperiode(FELLESPERIODE, fødsel.plusWeeks(9), fødsel.plusWeeks(21).minusDays(1)));
        perioderMor.add(uttaksperiode(MØDREKVOTE, fødsel.plusWeeks(21), fødsel.plusWeeks(27).minusDays(1)));
        perioderMor.add(oppholdsperiode(FELLESPERIODE_ANNEN_FORELDER, fødsel.plusWeeks(27), fødsel.plusWeeks(31).minusDays(1)));
        perioderMor.add(uttaksperiode(FELLESPERIODE, fødsel.plusWeeks(31), fødsel.plusWeeks(38).minusDays(1)));
        return fordelingMor;
    }

    public Fordeling fordelingFarSamtidigUttakFlerbarnsdager(LocalDate fødsel) {
        Fordeling fordelingFar = new Fordeling();
        fordelingFar.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> periodeFar = fordelingFar.getPerioder();
        periodeFar.add(new UttaksperiodeBuilder(
                FELLESPERIODE.getKode(),
                fødsel.plusWeeks(2), fødsel.plusWeeks(6).minusDays(1))
                .medFlerbarnsdager()
                .medSamtidigUttak(BigDecimal.valueOf(100))
                .build());
        periodeFar.add(new UttaksperiodeBuilder(
                FEDREKVOTE.getKode(),
                fødsel.plusWeeks(6), fødsel.plusWeeks(9).minusDays(1))
                .medFlerbarnsdager()
                .medSamtidigUttak(BigDecimal.valueOf(100))
                .build());
        periodeFar.add(new UttaksperiodeBuilder(
                FEDREKVOTE.getKode(),
                fødsel.plusWeeks(9), fødsel.plusWeeks(10).minusDays(1))
                .medFlerbarnsdager()
                .build());
        periodeFar.add(new UttaksperiodeBuilder(
                FEDREKVOTE.getKode(),
                fødsel.plusWeeks(10), fødsel.plusWeeks(11).minusDays(1))
                .medSamtidigUttak(BigDecimal.valueOf(50))
                .build());
        periodeFar.add(new UttaksperiodeBuilder(
                FEDREKVOTE.getKode(),
                fødsel.plusWeeks(23), fødsel.plusWeeks(27).minusDays(1))
                .medFlerbarnsdager()
                .medSamtidigUttak(BigDecimal.valueOf(100))
                .build());
        periodeFar.add(new UttaksperiodeBuilder(
                FELLESPERIODE.getKode(),
                fødsel.plusWeeks(27), fødsel.plusWeeks(31).minusDays(1))
                .build());
        periodeFar.add(new UttaksperiodeBuilder(
                FELLESPERIODE.getKode(),
                fødsel.plusWeeks(31), fødsel.plusWeeks(38).minusDays(1))
                .medFlerbarnsdager()
                .medSamtidigUttak(BigDecimal.valueOf(100))
                .build());
        periodeFar.add(uttaksperiode(FEDREKVOTE, fødsel.plusWeeks(38), fødsel.plusWeeks(42).minusDays(1)));
        return fordelingFar;
    }

    //TODO Flytte til nytt funksjonelt nivå
    @Step("Behandle søknad for mor registrert fødsel")
    public long behandleSøknadForMorRegistrertFødsel(TestscenarioDto testscenario) {
        long saksnummer = sendInnSøknadOgInntektMor(testscenario, testscenario.getPersonopplysninger().getFødselsdato());

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        return saksnummer;
    }

    @Step("Behandle søknad for mor uregistrert")
    private long behandleSøknadForMorUregistrert(TestscenarioDto testscenario, LocalDate fødselsdato) {
        long saksnummer = sendInnSøknadOgInntektMor(testscenario, fødselsdato);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonForeligger(1, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        return saksnummer;
    }

    @Step("Behandle søknad for mor uten overlapp")
    private long behandleSøknadForMorUtenOverlapp(TestscenarioDto testscenario, LocalDate fødselsdato) {
        long saksnummer = sendInnSøknadOgInntektMor(testscenario, fødselsdato);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonForeligger(1, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);


        VurderSoknadsfristForeldrepengerBekreftelse vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRIST_FORELDREPENGER))
                .godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        return saksnummer;
    }

    @Step("Behandle søknad for far uten overlapp")
    public long behandleSøknadForFarUtenOverlapp(TestscenarioDto testscenario, LocalDate fødselsdato) {
        long saksnummer = sendInnSøknadOgInntektFar(testscenario, fødselsdato, fødselsdato.plusWeeks(10).plusDays(1));

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        verifiser(saksbehandler.sakErKobletTilAnnenpart(), "Saken er ikke koblet til en annen behandling");

        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarBrukerBosattBekreftelse.class);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        return saksnummer;
    }

    //TODO Blir det virkelig send endringssøknader her??
    @Step("Send inn endringssøknad for mor")
    private void sendInnEndringssøknadforMor(TestscenarioDto testscenario, long saksnummerMor) {
        String søkerAktørid = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(4);
        Fordeling fordeling = FordelingErketyper.fordelingMorHappyCase(fødselsdato);
        String saksnummerString = String.valueOf(saksnummerMor);
        EndringssøknadBuilder søknad = lagEndringssøknad(søkerAktørid, SøkersRolle.MOR, fordeling, saksnummerString);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummerMor);
    }

    @Step("Send inn endringssøknad for mor med endret uttak")
    private void sendInnEndringssøknadforMorMedEndretUttak(TestscenarioDto testscenario, long saksnummerMor) {
        String søkerAktørid = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String annenPartAktørid = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(4);
        Fordeling fordeling = FordelingErketyper.fordelingMorHappyCaseLong(fødselsdato);
        EndringssøknadBuilder søknad = lagEndringssøknad(søkerAktørid, SøkersRolle.MOR, fordeling, String.valueOf(saksnummerMor));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummerMor);
    }

    @Step("Behandle søknad for far satt på vent")
    private long behandleSøknadForFarSattPåVent(TestscenarioDto testscenario, LocalDate fødselsdato) {
        long saksnummer = sendInnSøknadOgInntektFar(testscenario, fødselsdato, fødselsdato.plusWeeks(10).plusDays(1));

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        verifiser(saksbehandler.valgtBehandling.erSattPåVent(), "Behandling ble ikke satt på vent selv om far har søkt for tidlig");

        return saksnummer;
    }

    @Step("Send inn søknad og inntekt mor")
    private long sendInnSøknadOgInntektMor(TestscenarioDto testscenario, LocalDate fødselsdato) {
        long saksnummer = sendInnSøknadMor(testscenario, fødselsdato);
        sendInnInntektsmeldingMor(testscenario, fødselsdato, saksnummer);
        return saksnummer;
    }

    @Step("Send inn søknad mor: fødsel funnet sted mor med far")
    private long sendInnSøknadMor(TestscenarioDto testscenario, LocalDate fødselsdato) {
        String søkerAktørid = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String annenPartAktørid = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørid, SøkersRolle.MOR)
                .medFordeling(FordelingErketyper.fordelingMorHappyCase(fødselsdato))
                .medAnnenForelder(annenPartAktørid);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        return fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
    }

    @Step("Send inn søknad mor med aksjonspunkt")
    private long sendInnSøknadMorMedAksjonspunkt(TestscenarioDto testscenario, LocalDate fødselsdato) {
        String søkerAktørid = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String annenPartAktørid = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)));
        perioder.add(FordelingErketyper.utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørid, SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medAnnenForelder(annenPartAktørid);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        return fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
    }

    @Step("Send inn inntektsmelding mor")
    private long sendInnInntektsmeldingMor(TestscenarioDto testscenario, LocalDate fødselsdato, Long saksnummer) {
        String søkerAktørid = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        List<Integer> inntekter = sorterteInntektsbeløp(testscenario);
        String orgnr = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();

        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(
                inntekter.get(0),
                søkerIdent,
                startDatoForeldrepenger,
                orgnr);
        return fordel.sendInnInntektsmelding(inntektsmelding, søkerAktørid, søkerIdent, saksnummer);
    }

    @Step("Send inn søknad og inntektsmelding far")
    private long sendInnSøknadOgInntektFar(TestscenarioDto testscenario, LocalDate fødselsdato, LocalDate startDatoForeldrepenger) {
        long saksnummer = sendInnSøknadFar(testscenario, fødselsdato, startDatoForeldrepenger);
        sendInnInntektsmeldingFar(testscenario, fødselsdato, startDatoForeldrepenger, saksnummer);
        return saksnummer;
    }

    @Step("Send inn søknad far")
    private long sendInnSøknadFar(TestscenarioDto testscenario, LocalDate fødselsdato, LocalDate startDatoForeldrepenger) {
        String søkerAktørid = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String annenPartAktørid = testscenario.getPersonopplysninger().getSøkerAktørIdent();

        Fordeling fordeling = generiskFordeling(uttaksperiode(FEDREKVOTE, startDatoForeldrepenger, startDatoForeldrepenger.plusWeeks(2)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørid, SøkersRolle.FAR)
                .medFordeling(fordeling)
                .medAnnenForelder(annenPartAktørid);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        return fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
    }

    @Step("Send inn inntektsmelding far")
    private long sendInnInntektsmeldingFar(TestscenarioDto testscenario, LocalDate fødselsdato, LocalDate startDatoForeldrepenger, Long saksnummer) {
        String søkerAktørid = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getAnnenpartIdent();

        List<Integer> inntekter = sorterteInntektsbeløp(testscenario);
        String orgnr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();

        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(
                inntekter.get(0),
                søkerIdent,
                startDatoForeldrepenger,
                orgnr);
        return fordel.sendInnInntektsmelding(inntektsmelding, søkerAktørid, søkerIdent, saksnummer);
    }

}
