package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerHarGyldigPeriodeBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarLopendeVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SøknadErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.LocalDate;
import java.util.List;

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

        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        AvklarLopendeVedtakBekreftelse avklarLopendeVedtakBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarLopendeVedtakBekreftelse.class);
        avklarLopendeVedtakBekreftelse.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(avklarLopendeVedtakBekreftelse);

        AvklarBrukerHarGyldigPeriodeBekreftelse avklarBrukerHarGyldigPeriodeBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class);
        avklarBrukerHarGyldigPeriodeBekreftelse.setVurdering(hentKodeverk().MedlemskapManuellVurderingType.getKode("MEDLEM"));
        saksbehandler.bekreftAksjonspunkt(avklarBrukerHarGyldigPeriodeBekreftelse);

        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
            .leggTilFaktaOmBeregningTilfeller("FASTSETT_BG_KUN_YTELSE")
            .leggTilAndelerYtelse(10000.0, new Kode("", "ARBEIDSTAKER", ""));//TODO hent kode
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_OM_SØKER_HAR_MOTTATT_STØTTE))
            .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN))
            .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_GYLDIG_MEDLEMSKAPSPERIODE));
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

        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR, 1, fødselsdato);
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

        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario, startDatoForeldrepenger);
        fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario, saksnummer);

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
