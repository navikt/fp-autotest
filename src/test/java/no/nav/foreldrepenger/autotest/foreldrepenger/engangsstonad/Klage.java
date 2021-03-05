package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadFødsel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.VurderÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravKa;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravNfp;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse.VurderingAvKlageNfpBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse.VurderingAvKlageNkBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("fpsak")
@Tag("engangsstonad")
public class Klage extends FpsakTestBase {

    @Test
    @DisplayName("Behandle klage via NFP - medhold")
    @Description("Behandle klage via NFP - vurdert til medhold")
    public void klageMedholdNFP() {
        // Opprette førstegangssøknad engangsstønad
        TestscenarioDto testscenario = opprettTestscenario("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.personopplysninger().fødselsdato());

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage NFP
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftMedholdGunst("PROSESSUELL_FEIL")
                .fritekstBrev("Fritektst til brev fra klagebehandler.")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        verifiserLikhet(klagebehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.KLAGE_MEDHOLD);
        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.KLAGE_MEDHOLD);

    }

    @Test
    @DisplayName("Behandle klage via NFP - påklaget vedtak opphevet")
    @Description("Behandle klage via NFP - stadfestet af NFP og opphevet av KA")
    public void klageOppheveAvKA() {
        TestscenarioDto testscenario = opprettTestscenario("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.personopplysninger().fødselsdato());

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        verifiserLikhet(klagebehandler.valgtBehandling.behandlingsresultat.getType(),
                BehandlingResultatType.KLAGE_YTELSESVEDTAK_STADFESTET);

        // KA - klage kommer rett til KA uten totrinnsbehanling. Kan fortsette med samme klagebehandler.
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);

        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse.bekreftOpphevet("NYE_OPPLYSNINGER")
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        verifiserLikhet(klagebehandler.valgtBehandling.behandlingsresultat.getType(),
                BehandlingResultatType.KLAGE_YTELSESVEDTAK_OPPHEVET);

        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);
        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();

        klagebehandler.fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling();

    }

    @Test
    @DisplayName("Behandle klage via KA - påklaget vedtak omgjort/medhold")
    @Description("Behandle klage via KA - stadfestet af NFP og medhold av KA")
    public void klageOmgjortAvKA() {
        TestscenarioDto testscenario = opprettTestscenario("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.personopplysninger().fødselsdato());

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        verifiserLikhet(klagebehandler.valgtBehandling.behandlingsresultat.getType(),
                BehandlingResultatType.KLAGE_YTELSESVEDTAK_STADFESTET);

        // KA - klage kommer rett til KA uten totrinnsbehanling. Kan fortsette med samme klagebehandler.
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);

        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse
                .bekreftMedholdGunst("NYE_OPPLYSNINGER")
                .fritekstBrev("Brev");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();

        klagebehandler.fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling();
    }

    @Test
    @DisplayName("Behandle klage via KA - avslag")
    @Description("Behandle klage via KA - stadfestet af NFP og medhold av KA")
    public void klageAvslaattAvKA() {
        TestscenarioDto testscenario = opprettTestscenario("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.personopplysninger().fødselsdato());

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        verifiserLikhet(klagebehandler.valgtBehandling.behandlingsresultat.getType(),
                BehandlingResultatType.KLAGE_YTELSESVEDTAK_STADFESTET);

        // Behandle klage - KA
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa
                .klageErIkkeKonkret()
                .setBegrunnelse("Begrunnelse formkrav");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();
        klagebehandler.fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling();
        verifiserLikhet(klagebehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.KLAGE_AVVIST);
    }

    @Test
    @DisplayName("Behandle klage via NFP - avvist av beslutter")
    @Description("Behandle klage via NFP - medhold av NFP avvist av beslutter send tilbake til NFP vurdert til delvist gunst")
    public void avvistAvBelutterNFP() {
        TestscenarioDto testscenario = opprettTestscenario("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.personopplysninger().fødselsdato());

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        String fritekstbrev1 = "Fritekst brev nfp.";
        String begrunnelse1 = "Fordi.";

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftMedholdGunst("NYE_OPPLYSNINGER")
                .fritekstBrev(fritekstbrev1)
                .setBegrunnelse(begrunnelse1);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        verifiserLikhet(klagebehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.KLAGE_MEDHOLD,
                "Behandlingsresultat");
        verifiserKlageVurderingOmgjoer(klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP()
                .getKlageVurderingOmgjoer().kode, "GUNST_MEDHOLD_I_KLAGE");

        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();

        FatterVedtakBekreftelse fatterVedtakBekreftelse = beslutter
                .hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse
                .avvisAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP), VurderÅrsak.FEIL_REGEL)
                .setBegrunnelse("Avvist av beslutter");
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();
        verifiserFritekst(
                klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getFritekstTilBrev(),
                fritekstbrev1);
        verifiserFritekst(
                klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getBegrunnelse(),
                begrunnelse1);
        String fritekstbrev2 = "Fritekst brev nr 2 .";
        String begrunnelse2 = "Fordi.";
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse1 = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse1
                .bekreftMedholdDelvisGunst("NYE_OPPLYSNINGER")
                .fritekstBrev(fritekstbrev2)
                .setBegrunnelse(begrunnelse2);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse1);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.KLAGE_MEDHOLD);
        verifiserFritekst(
                beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getFritekstTilBrev(),
                fritekstbrev2);
        verifiserFritekst(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getBegrunnelse(),
                begrunnelse2);
        verifiserKlageVurderingOmgjoer(
                beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getKlageVurderingOmgjoer().kode,
                "DELVIS_MEDHOLD_I_KLAGE");
    }

    @Step("Oppretter førstegangsvedtak")
    private void opprettForstegangssoknadVedtak(long saksnummer) {
        // Opprette førstegangssøknad engangsstønad
        saksbehandler.hentFagsak(saksnummer);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);

        saksbehandler.ventTilAvsluttetBehandling();
    }

    private void verifiserFritekst(String verdiFaktisk, String verdiForventet) {
        verifiserLikhet(verdiFaktisk, verdiForventet, "Fritekst");
    }

    private void verifiserKlageVurderingOmgjoer(String verdiFaktisk, String verdiForventet) {
        verifiserLikhet(verdiFaktisk, verdiForventet, "KlageVurderingOmgjoer");
    }
}
