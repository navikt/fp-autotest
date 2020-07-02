package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.addPeriode;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.addStønadskontotype;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadEndringErketyper.lagEndringssøknad;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugFritekst;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;

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
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EndringssøknadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerManueltOpprettetRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaStartdatoForForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaUttakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrMedlemskapsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.GraderingPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.UtsettelsePeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Utsettelsesaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Uttaksperiodetyper;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Gradering;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.ObjectFactory;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Utsettelsesperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Virksomhet;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("foreldrepenger")
@Tag("fluoritt")
public class Revurdering extends ForeldrepengerTestBase {

    private static final Logger logger = LoggerFactory.getLogger(Revurdering.class);

    @Test
    @DisplayName("Revurdering opprettet manuelt av saksbehandler.")
    @Description("Førstegangsbehandling til positivt vedtak. Saksbehandler oppretter revurdering manuelt. " +
            "Overstyrer medlemskap. Vedtaket opphører.")
    public void opprettRevurderingManuelt() {

        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                        .getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        saksbehandler.opprettBehandlingRevurdering("RE-MDL");

        overstyrer.erLoggetInnMedRolle(Rolle.OVERSTYRER);
        overstyrer.hentFagsak(saksnummer);
        overstyrer.velgRevurderingBehandling();
        OverstyrMedlemskapsvilkaaret overstyrMedlemskapsvilkaaret = new OverstyrMedlemskapsvilkaaret();
        overstyrMedlemskapsvilkaaret.avvis(hentKodeverk().Avslagsårsak.get("FP_VK_2").getKode("1020" /*
                                                                                                      * Søker er ikke
                                                                                                      * medlem
                                                                                                      */));
        overstyrMedlemskapsvilkaaret.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyrMedlemskapsvilkaaret);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(KontrollerManueltOpprettetRevurdering.class);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        verifiserLikhet(overstyrer.valgtBehandling.behandlingsresultat.toString(), "OPPHØR", "Behandlingsresultat");

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        beslutter.velgRevurderingBehandling();
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.OVERSTYRING_AV_MEDLEMSKAPSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "OPPHØR", "Behandlingsresultat");
        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.getAvslagsarsak().kode, "1020", "Avslagsårsak");
        verifiserLikhet(beslutter.valgtBehandling.status.kode, "AVSLU", "Behandlingsstatus");
        beslutter.ventTilFagsakAvsluttet();
        logger.info("Status på sak: {}", beslutter.valgtFagsak.hentStatus().kode);
    }

    @Test
    @DisplayName("Endringssøknad med ekstra uttaksperiode.")
    @Description("Førstegangsbehandling til positivt vedtak. Søker sender inn endringsøknad. Endring i uttak. Vedtak fortsatt løpende.")
    public void endringssøknad() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");

        // Førstegangssøknad
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                        .getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDLEGG_MOTTATT);
        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.behandlinger.size(), 1, "Antall behandlinger"); // revurdering opprettes ved flere
                                                                                      // arbeidsforhold for nå i
                                                                                      // autotest
        verifiserLikhet(saksbehandler.valgtBehandling.type.kode, "BT-002", "Behandlingstype");
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        debugFritekst("Ferdig med første behandling");

        // Endringssøknad
        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(10).minusDays(1)));
        EndringssøknadBuilder søknadE = lagEndringssøknad(søkerAktørIdent, SøkersRolle.MOR, fordeling,
                saksnummer.toString());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummerE = fordel.sendInnSøknad(søknadE.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.velgRevurderingBehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        verifiser(saksbehandler.valgtBehandling.behandlingsresultat.toString().equals("FORELDREPENGER_ENDRET"),
                "Behandlingsresultat er ikke 'Foreldrepenger er endret'");
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen().get(0).kode,
                "ENDRING_I_UTTAK", "konsekvensForYtelsen");
        saksbehandler.hentFagsak(saksnummerE);
        verifiser(saksbehandler.valgtFagsak.hentStatus().kode.equals("LOP"), "Status på fagsaken er ikke løpende.");

    }

    @Disabled
    @Test
    @DisplayName("Revurdering og ny IM når behandling er hos beslutter.")
    @Description("Førstegangsbehandling til positivt vedtak. Revurdering, og ny IM kommer når behandling er hos beslutter. Vedtak fortsatt løpende.")
    public void nyInntektsmeldingUnderÅpenRevurdering() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                        .getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        verifiserLikhet(saksbehandler.valgtBehandling.status.kode, "AVSLU", "Behandlingsstatus");

        // Sender inn ny IM
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        InntektsmeldingBuilder inntektsmeldingerEndret = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato.plusWeeks(1),
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                        .getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldingerEndret,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgRevurderingBehandling();
        var aksjonspunktBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaStartdatoForForeldrepengerBekreftelse.class)
                .setStartdatoFraSoknad(fpStartdato.plusWeeks(1))
                .setBegrunnelse("Endret startdato for fp.");
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelse);
        verifiser(saksbehandler.harAksjonspunkt("5081"), "Har ikke AP 5081");
        var avklarFaktaUttakBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakFørsteUttakDato.class)
                .delvisGodkjennPeriode(fpStartdato, fpStartdato.plusWeeks(3).minusDays(1), fpStartdato.plusWeeks(1),
                        fpStartdato.plusWeeks(3).minusDays(1),
                        hentKodeverk().UttakPeriodeVurderingType.getKode("PERIODE_OK"));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakBekreftelse);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // Sender inn ny IM når revurdering ligger hos beslutter
        inntektsmeldingerEndret = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato.plusDays(2),
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                        .getArbeidsgiverOrgnr());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnInntektsmelding(
                inntektsmeldingerEndret,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        verifiser(saksbehandler.behandlinger.size() == 2, "Fagsaken har mer enn én revurdering.");
        saksbehandler.velgRevurderingBehandling();
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.BEHANDLINGEN_ER_FLYTTET);
        verifiser(saksbehandler.harHistorikkinnslagForBehandling(HistorikkInnslag.BEHANDLINGEN_ER_FLYTTET),
                "Mangler historikkinnslag om at behandlingen er flyttet.");
        verifiser(saksbehandler.harAksjonspunkt("5045"),
                "Behandling hopper ikke tilbake til 'Avklar startdato for foreldrepengeperioden'.");
        var avklarFaktaStartdatoForForeldrepengerBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaStartdatoForForeldrepengerBekreftelse.class)
                .setStartdatoFraSoknad(fpStartdato.plusDays(2))
                .setBegrunnelse("Endret startdato for fp.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaStartdatoForForeldrepengerBekreftelse2);
        verifiser(saksbehandler.harAksjonspunkt("5081"), "Har ikke AP 5081");
        var avklarFaktaUttakBekreftelse1 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakFørsteUttakDato.class)
                .delvisGodkjennPeriode(fpStartdato, fpStartdato.plusWeeks(3).minusDays(1), fpStartdato.plusDays(2),
                        fpStartdato.plusWeeks(3).minusDays(1),
                        hentKodeverk().UttakPeriodeVurderingType.getKode("PERIODE_OK"));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakBekreftelse1);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        beslutter.velgRevurderingBehandling();
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_FØRSTE_UTTAKSDATO));
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);
        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "FORELDREPENGER_ENDRET",
                "Behandlingsresultat");
        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen().get(0).kode,
                "ENDRING_I_UTTAK", "konsekvensForYtelsen");

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgRevurderingBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.status.kode, "AVSLU", "Behandlingsstatus");
        verifiser(saksbehandler.valgtFagsak.hentStatus().kode.equals("LOP"), "Fagsaken er ikke løpende.");

    }

    @Test
    @DisplayName("Endringssøknad med utsettelse")
    @Description("Førstegangsbehandling til positivt vedtak. Endringssøknad med utsettelse fra bruker. Vedtak fortsatt løpende.")
    public void endringssøknadMedUtsettelse() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        // Opprette perioder mor søker om
//        Fordeling fordeling = fordelingFørstegangsbehandling(fødselsdato, fpStartdato);
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                        .getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        verifiserLikhet(saksbehandler.valgtBehandling.status.kode, "AVSLU", "Behandlingsstatus");
        verifiser(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderForSøker().size() == 4,
                "Feil antall perioder.");

        // TODO fikse på periodene i søknad
        // Endringssøknad
        LocalDate utsettelseFom = fødselsdato.plusWeeks(16);
        LocalDate utsettelseTom = fødselsdato.plusWeeks(18);
        Fordeling fordelingUtsettelse = fordelingEndringssøknadUtsettelse(utsettelseFom, utsettelseTom);
        EndringssøknadBuilder endretSøknad = lagEndringssøknad(søkerAktørIdent, SøkersRolle.MOR,
                fordelingUtsettelse, saksnummer.toString());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummerE = fordel.sendInnSøknad(endretSøknad.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.velgRevurderingBehandling();
        // TODO bedre validering, funksjonelt så er denne søknaden ikke helt bra!
        verifiserLikhet(saksbehandler.valgtBehandling.status.kode, "AVSLU", "Behandlingsstatus er ikke avsluttet");
        verifiserLikhet(saksbehandler.valgtFagsak.hentStatus().kode, "LOP", "Saken er ikke løpende.");
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.toString(), "FORELDREPENGER_ENDRET");
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen().get(0).kode,
                "ENDRING_I_UTTAK");
        verifiser(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderForSøker().size() == 5,
                "Feil antall perioder.");
        for (UttakResultatPeriode periode : saksbehandler.valgtBehandling.getUttakResultatPerioder()
                .getPerioderForSøker()) {
            verifiser(periode.getAktiviteter().size() == 1, "Periode har mer enn én aktivitet");
        }
        var utsettelseType = saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderForSøker().get(4)
                .getUtsettelseType();
        verifiser(utsettelseType.equals(UttakUtsettelseÅrsak.ARBEID), "Feil i utsettelsetype eller periode.");

    }

    @Test
    @DisplayName("Endringssøknad med gradering")
    @Description("Førstegangsbehandling til positivt vedtak. Endringssøknad med gradering fra bruker. Vedtak fortsatt løpende.")
    public void endringssøknadMedGradering() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String orgnr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();

        Fordeling fordeling = fordelingFørstegangsbehandling(fødselsdato, fpStartdato);
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medFordeling(fordeling);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                        .getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        verifiserLikhet(saksbehandler.valgtBehandling.status.kode, "AVSLU", "Behandlingsstatus");
        verifiser(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderForSøker().size() == 4,
                "Feil antall perioder.");

        // Endringssøknad
        LocalDate graderingFom = fødselsdato.plusWeeks(20);
        LocalDate graderingTom = fødselsdato.plusWeeks(23).minusDays(1);
        Fordeling fordelingGradering = fordelingEndringssøknadGradering(orgnr, graderingFom, graderingTom);
        EndringssøknadBuilder endretSøknad = lagEndringssøknad(søkerAktørIdent, SøkersRolle.MOR,
                fordelingGradering, saksnummer.toString());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummerE = fordel.sendInnSøknad(endretSøknad.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.velgRevurderingBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.status.kode, "AVSLU", "Behandlingsstatus er ikke avsluttet");
        verifiserLikhet(saksbehandler.valgtFagsak.hentStatus().kode, "LOP", "Saken er ikke løpende.");
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.toString(), "FORELDREPENGER_ENDRET");
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen().get(0).kode,
                "ENDRING_I_UTTAK");
        verifiser(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderForSøker().size() == 5,
                "Feil antall perioder.");
        for (UttakResultatPeriode periode : saksbehandler.valgtBehandling.getUttakResultatPerioder()
                .getPerioderForSøker()) {
            verifiser(periode.getAktiviteter().size() == 1, "Periode har mer enn én aktivitet");
        }
        verifiser(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderForSøker().get(4)
                .getGraderingInnvilget(), "Feil graderingsperiode.");

    }

    @Test
    @DisplayName("Mor endringssøknad med aksjonspunkt i uttak")
    @Description("Mor endringssøknad med aksjonspunkt i uttak. Søker utsettelse tilbake i tid for å få aksjonspunkt." +
            "Saksbehandler avslår utsettelsen. Mor har også arbeid med arbeidsforholdId i inntektsmelding")
    public void endringssøknad_med_aksjonspunkt_i_uttak() {
        var testscenario = opprettTestscenario("140");
        var aktørIdSøker = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var fnrSøker = testscenario.getPersonopplysninger().getSøkerIdent();
        var inntekt = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0)
                .getBeløp();
        var orgNrSøker = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søkersRolle = SøkersRolle.MOR;

        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdSøker, SøkersRolle.MOR)
                .medFordeling(fordeling);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        var arbeidsforholdId = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsforholdId();
        var im = lagInntektsmelding(inntekt, fnrSøker, fpStartdato, orgNrSøker)
                .medArbeidsforholdId(arbeidsforholdId);
        fordel.sendInnInntektsmelding(im, aktørIdSøker, fnrSøker, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        var fordelingEndring = generiskFordeling(
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødselsdato.plusWeeks(6),
                        fødselsdato.plusWeeks(10).minusDays(1)));

        var søknadE = lagEndringssøknad(aktørIdSøker, søkersRolle, fordelingEndring, String.valueOf(saksnummer));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        var saksnummerE = fordel.sendInnSøknad(søknadE.build(), aktørIdSøker, fnrSøker,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.velgRevurderingBehandling();

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.avslåManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // Behandle totrinnskontroll
        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        beslutter.velgRevurderingBehandling();
        var fatterVedtak = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtak.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_UTTAKPERIODER));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(fatterVedtak);
    }

    @Test
    @DisplayName("Ikke få avslåg på innvilget perioder pga søknadsfrist")
    @Description("Ikke få avslåg på innvilget perioder pga søknadsfrist. Bruker papirsøknad for å kunne sette mottatt dato tilbake i tid")
    public void ikke_avslag_pa_innvilget_perioder_pga_søknadsfrist_i_revurdering() {
        var testscenario = opprettTestscenario("76");

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnPapirsøknadForeldrepenger(testscenario, false);
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        var im = InntektsmeldingForeldrepengeErketyper
                .makeInntektsmeldingFromTestscenario(testscenario, startDatoForeldrepenger).get(0);
        fordel.sendInnInntektsmelding(im, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        var aksjonspunktBekreftelse = saksbehandler.aksjonspunktBekreftelse(PapirSoknadForeldrepengerBekreftelse.class);
        var fordeling = new FordelingDto();
        var fpff = new PermisjonPeriodeDto(FORELDREPENGER_FØR_FØDSEL, startDatoForeldrepenger,
                fødselsdato.minusDays(1));
        var mødrekvote = new PermisjonPeriodeDto(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(12));
        fordeling.permisjonsPerioder.add(fpff);
        fordeling.permisjonsPerioder.add(mødrekvote);
        aksjonspunktBekreftelse.morSøkerFødsel(fordeling, fødselsdato, fødselsdato.plusWeeks(12));

        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelse);
        saksbehandler.ventTilAvsluttetBehandling();

        // Sender endringssøknad for å gi fagsaken en ny søknad mottatt dato
        var fordelingEndringssøknad = new ObjectFactory().createFordeling();
        fordelingEndringssøknad.setAnnenForelderErInformert(true);
        var perioder = fordelingEndringssøknad.getPerioder();
        perioder.add(
                uttaksperiode(FELLESPERIODE, mødrekvote.periodeTom.plusDays(1), mødrekvote.periodeTom.plusWeeks(2)));
        var søknadE = lagEndringssøknad(testscenario.getPersonopplysninger().getSøkerAktørIdent(), SøkersRolle.MOR,
                fordelingEndringssøknad, String.valueOf(saksnummer));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnSøknad(søknadE.build(), testscenario, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering("RE-FRDLING");
        saksbehandler.velgSisteBehandling();

        var allePerioderInnvilget = saksbehandler.valgtBehandling.hentUttaksperioder().stream()
                .allMatch(p -> p.getPeriodeResultatType().kode.equals("INNVILGET"));
        verifiser(allePerioderInnvilget, "Forventer at alle uttaksperioder er innvilget");
    }

    @Disabled("TFP-2394")
    @Test
    @DisplayName("Fortsatt få avslag på avslåtte perioder pga søknadsfrist i neste revurdering")
    @Description("Fortsatt få avslag på avslåtte perioder pga søknadsfrist i neste revurdering. Bruker papirsøknad for å kunne sette mottatt dato tilbake i tid")
    public void fortsatt_tape_avslåtte_perioder_pga_søknadsfrist_i_revurdering() {
        var testscenario = opprettTestscenario("76");

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnPapirsøknadForeldrepenger(testscenario, false);
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        var im = InntektsmeldingForeldrepengeErketyper
                .makeInntektsmeldingFromTestscenario(testscenario, startDatoForeldrepenger).get(0);
        fordel.sendInnInntektsmelding(im, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        var aksjonspunktBekreftelse = saksbehandler.aksjonspunktBekreftelse(PapirSoknadForeldrepengerBekreftelse.class);
        var fordeling = new FordelingDto();
        var fpff = new PermisjonPeriodeDto(FORELDREPENGER_FØR_FØDSEL, startDatoForeldrepenger,
                fødselsdato.minusDays(1));
        var mødrekvote = new PermisjonPeriodeDto(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(12));
        fordeling.permisjonsPerioder.add(fpff);
        fordeling.permisjonsPerioder.add(mødrekvote);
        aksjonspunktBekreftelse.morSøkerFødsel(fordeling, fødselsdato, fødselsdato.plusWeeks(25));
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelse);

        var vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .aksjonspunktBekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class)
                .bekreftHarIkkeGyldigGrunn();
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(VurderSoknadsfristForeldrepengerBekreftelse.class));
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        saksbehandler.ventTilAvsluttetBehandling();
        verifiser(saksbehandler.hentAvslåtteUttaksperioder().size() > 1, "Forventer avslåtte uttaksperioder");

        var fordelingEndringssøknad = new ObjectFactory().createFordeling();
        fordelingEndringssøknad.setAnnenForelderErInformert(true);
        var perioder = fordelingEndringssøknad.getPerioder();
        perioder.add(
                uttaksperiode(FELLESPERIODE, mødrekvote.periodeTom.plusDays(1), mødrekvote.periodeTom.plusWeeks(2)));
        var søknadE = lagEndringssøknad(testscenario.getPersonopplysninger().getSøkerAktørIdent(), SøkersRolle.MOR,
                fordelingEndringssøknad, String.valueOf(saksnummer));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnSøknad(søknadE.build(), testscenario, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering("RE-FRDLING");
        saksbehandler.velgSisteBehandling();

        verifiser(saksbehandler.hentAvslåtteUttaksperioder().size() > 1, "Forventer avslåtte uttaksperioder");
    }

    @Disabled("TFP-2394")
    @Test
    @DisplayName("Utsettelser og gradering fra førstegangsbehandling skal ikke gå til manuell behandling")
    @Description("Utsettelser og gradering fra førstegangsbehandling skal ikke gå til manuell behandling hvis innenfor søknadsfrist."
            +
            " Bruker papirsøknad for å kunne sette mottatt dato tilbake i tid")
    public void utsettelser_og_gradering_fra_førstegangsbehandling_skal_ikke_gå_til_manuell_behandling_ved_endringssøknad() {
        var testscenario = opprettTestscenario("76");

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnPapirsøknadForeldrepenger(testscenario, false);
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();

        var im = InntektsmeldingForeldrepengeErketyper.makeInntektsmeldingFromTestscenario(testscenario, fødselsdato)
                .get(0);
        fordel.sendInnInntektsmelding(im, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        var aksjonspunktBekreftelse = saksbehandler.aksjonspunktBekreftelse(PapirSoknadForeldrepengerBekreftelse.class);
        var fordeling = new FordelingDto();
        var mødrekvote = new PermisjonPeriodeDto(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1));
        var gradering = new GraderingPeriodeDto(MØDREKVOTE, fødselsdato.plusWeeks(6),
                fødselsdato.plusWeeks(12).minusDays(1), BigDecimal.valueOf(50),
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                        .getArbeidsgiverOrgnr(),
                true, false, false);
        var utsettelse = new UtsettelsePeriodeDto(fødselsdato.plusWeeks(12), fødselsdato.plusWeeks(15),
                SøknadUtsettelseÅrsak.ARBEID);
        fordeling.permisjonsPerioder = List.of(mødrekvote);
        fordeling.graderingPeriode = List.of(gradering);
        fordeling.utsettelsePeriode = List.of(utsettelse);
        aksjonspunktBekreftelse.morSøkerFødsel(fordeling, fødselsdato, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelse);
        saksbehandler.ventTilAvsluttetBehandling();

        verifiser(saksbehandler.hentAvslåtteUttaksperioder().isEmpty(),
                "Forventer at alle uttaksperioder er innvilget");

        var fordelingEndringssøknad = new ObjectFactory().createFordeling();
        fordelingEndringssøknad.setAnnenForelderErInformert(true);
        var perioder = fordelingEndringssøknad.getPerioder();
        perioder.add(
                uttaksperiode(FELLESPERIODE, utsettelse.periodeTom.plusDays(1), utsettelse.periodeTom.plusWeeks(1)));
        var søknadE = lagEndringssøknad(testscenario.getPersonopplysninger().getSøkerAktørIdent(), SøkersRolle.MOR,
                fordelingEndringssøknad, String.valueOf(saksnummer));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnSøknad(søknadE.build(), testscenario, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        verifiser(saksbehandler.hentAvslåtteUttaksperioder().isEmpty(),
                "Forventer at alle uttaksperioder er innvilget");

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering("RE-FRDLING");
        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        verifiser(saksbehandler.hentAvslåtteUttaksperioder().isEmpty(),
                "Forventer at alle uttaksperioder er innvilget");
    }

    @Deprecated
    private Fordeling fordelingFørstegangsbehandling(LocalDate fødselsdato, LocalDate fpStartdato) {
        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)));
        perioder.add(uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)));
        perioder.add(uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(23).minusDays(1)));
        return fordeling;
    }

    private Fordeling fordelingEndringssøknadUtsettelse(LocalDate utsettelseFom, LocalDate utsettelseTom) {
        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        Utsettelsesperiode utsettelsesperiode = new Utsettelsesperiode();
        utsettelsesperiode.setErArbeidstaker(true);
        Uttaksperiodetyper typer = new Uttaksperiodetyper();
        typer.setKode(FELLESPERIODE.getKode());
        utsettelsesperiode.setUtsettelseAv(typer);
        Utsettelsesaarsaker aarsak = new Utsettelsesaarsaker();
        aarsak.setKode(SøknadUtsettelseÅrsak.ARBEID.getKode());
        utsettelsesperiode.setAarsak(aarsak);
        addPeriode(utsettelseFom, utsettelseTom, utsettelsesperiode);
        perioder.add(utsettelsesperiode);
        return fordeling;
    }

    private Fordeling fordelingEndringssøknadGradering(String orgnr, LocalDate graderingFom, LocalDate graderingTom) {
        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        Gradering gradering = new Gradering();
        gradering.setErArbeidstaker(true);
        gradering.setArbeidtidProsent(40);
        gradering.setArbeidsforholdSomSkalGraderes(true);
        Virksomhet virksomhet = new Virksomhet();
        virksomhet.setIdentifikator(orgnr);
        gradering.setArbeidsgiver(virksomhet);
        addStønadskontotype(FELLESPERIODE, gradering);
        addPeriode(graderingFom, graderingTom, gradering);
        perioder.add(gradering);
        return fordeling;
    }

}
