package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettMaanedsinntektUtenInntektsmeldingAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerHarGyldigPeriodeBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarLopendeVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.FaktaOmBeregningTilfelle;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.util.Collections.singletonList;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("foreldrepenger")
public class Ytelser extends ForeldrepengerTestBase {

    /*
     * Mottar foreldrepenger og ikke sykepenger pga problemer / mangler med vtp scenariet. usikker på hva som mangler
     */
    @Test
    @DisplayName("Mor søker fødsel og mottar sykepenger")
    @Description("Mor søker fødsel og mottar sykepenger - opptjening automatisk oppfylt")
    public void morSøkerFødselMottarSykepenger() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("70"); //TODO bruker ytelse foreldrepenger og ikke sykepenger

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String annenpartAktørIdent = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String annenpartIdent = testscenario.getPersonopplysninger().getAnnenpartIdent();

        log.info("Søker: " + søkerIdent);
        log.info("Søker aktør: " + søkerAktørIdent);
        log.info("Annen part: " + annenpartIdent);
        log.info("Annen part aktør: " + annenpartAktørIdent);


        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        log.info("Fødselsdato: " + fødselsdato);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        AvklarLopendeVedtakBekreftelse avklarLopendeVedtakBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarLopendeVedtakBekreftelse.class);
        avklarLopendeVedtakBekreftelse.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(avklarLopendeVedtakBekreftelse);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD);
        AvklarArbeidsforholdBekreftelse arbeidsforholdBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        arbeidsforholdBekreftelse.bekreftArbeidsforholdLagtTilAvSaksbehandler("Test", startDatoForeldrepenger.minusYears(2), startDatoForeldrepenger.plusMonths(2), BigDecimal.ONE);
        saksbehandler.bekreftAksjonspunkt(arbeidsforholdBekreftelse);

        AvklarBrukerHarGyldigPeriodeBekreftelse avklarBrukerHarGyldigPeriodeBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class);
        avklarBrukerHarGyldigPeriodeBekreftelse.setVurdering(hentKodeverk().MedlemskapManuellVurderingType.getKode("MEDLEM"));
        saksbehandler.bekreftAksjonspunkt(avklarBrukerHarGyldigPeriodeBekreftelse);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING);
        VurderPerioderOpptjeningBekreftelse vurderPerioderOpptjeningBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class);
        vurderPerioderOpptjeningBekreftelse
                .godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.VURDER_MOTTAR_YTELSE.kode)
                .leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.FASTSETT_MAANEDSLONN_ARBEIDSTAKER_UTEN_INNTEKTSMELDING.kode)
                .leggTilMottarYtelse(singletonList(new ArbeidstakerandelUtenIMMottarYtelse(1, true)))
                .leggTilMaanedsinntektUtenInntektsmelding(singletonList(new FastsettMaanedsinntektUtenInntektsmeldingAndel(1, 30000)));
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderFaktaOmBeregningBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        VurderBeregnetInntektsAvvikBekreftelse vurderBeregnetInntektsAvvikBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse
                .leggTilInntekt(300_000, 1L)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_OM_SØKER_HAR_MOTTATT_STØTTE))
            .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN))
            .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD))
            .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS))
            .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_GYLDIG_MEDLEMSKAPSPERIODE))
            .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }

    @Test
    @Disabled //TODO (OL): Feiler i pipe og lokalt. Mangler vilkår Beregning (siste assertion). Vilkåret er ikke vurdert.
    @DisplayName("Mor søker fødsel og mottar sykepenger for lite inntekter")
    @Description("Mor søker fødsel og mottar sykepenger for lite inntekter - beregning avvist")
    public void morSøkerFødselMottarForLite() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("70"); //TODO bruker ytelse foreldrepenger og ikke sykepenger

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String annenpartAktørIdent = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String annenpartIdent = testscenario.getPersonopplysninger().getAnnenpartIdent();

        log.info("Søker: " + søkerIdent);
        log.info("Søker aktør: " + søkerAktørIdent);
        log.info("Annen part: " + annenpartIdent);
        log.info("Annen part aktør: " + annenpartAktørIdent);


        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        log.info("Fødselsdato: " + fødselsdato);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.hentAksjonspunktbekreftelse(AvklarLopendeVedtakBekreftelse.class).bekreftGodkjent();
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarLopendeVedtakBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class)
            .setVurdering(hentKodeverk().MedlemskapManuellVurderingType.getKode("MEDLEM"));
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarBrukerHarGyldigPeriodeBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
            .leggTilFaktaOmBeregningTilfeller("FASTSETT_BG_KUN_YTELSE")
            .leggTilAndelerYtelse(4000.0, new Kode("", "ARBEIDSTAKER", ""));//TODO hent kode
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderFaktaOmBeregningBekreftelse.class);

        verifiserLikhet(saksbehandler.vilkårStatus("FP_VK_41").kode, "IKKE_OPPFYLT"); //Beregning
    }

    @Test
    @DisplayName("Mor søker fødsel og mottar sykepenger og inntekter")
    @Description("Mor søker fødsel og mottar sykepenger og inntekter - opptjening automatisk godkjent")
    public void morSøkerFødselMottarSykepengerOgInntekter() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("72"); //TODO bruker ytelse foreldrepenger og ikke sykepenger

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String annenpartAktørIdent = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String annenpartIdent = testscenario.getPersonopplysninger().getAnnenpartIdent();

        log.info("Søker: " + søkerIdent);
        log.info("Søker aktør: " + søkerAktørIdent);
        log.info("Annen part: " + annenpartIdent);
        log.info("Annen part aktør: " + annenpartAktørIdent);


        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        log.info("Fødselsdato: " + fødselsdato);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                startDatoForeldrepenger,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        AvklarLopendeVedtakBekreftelse avklarLopendeVedtakBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarLopendeVedtakBekreftelse.class);
        avklarLopendeVedtakBekreftelse.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(avklarLopendeVedtakBekreftelse);

        AvklarBrukerHarGyldigPeriodeBekreftelse avklarBrukerHarGyldigPeriodeBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class);
        avklarBrukerHarGyldigPeriodeBekreftelse.setVurdering(hentKodeverk().MedlemskapManuellVurderingType.getKode("MEDLEM"));
        saksbehandler.bekreftAksjonspunkt(avklarBrukerHarGyldigPeriodeBekreftelse);

        VurderBeregnetInntektsAvvikBekreftelse vurderBeregnetInntektsAvvikBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse.leggTilInntekt((12*5000), 1L);
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_OM_SØKER_HAR_MOTTATT_STØTTE))
            .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS))
            .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_GYLDIG_MEDLEMSKAPSPERIODE));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }
}
