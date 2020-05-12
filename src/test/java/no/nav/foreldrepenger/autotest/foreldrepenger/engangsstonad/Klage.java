package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadFødsel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
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
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.getPersonopplysninger().getFødselsdato());

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage NFP
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventTilSakHarKlage();
        klagebehandler.velgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftMedholdGunst("PROSESSUELL_FEIL")
                .fritekstBrev("Fritektst til brev fra klagebehandler.")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);
        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(), "KLAGE_MEDHOLD");
        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.velgKlageBehandling();
        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP));
        beslutter.bekreftAksjonspunktMedDefaultVerdier(FatterVedtakBekreftelse.class);
        verifiserBehandlingsstatus(beslutter.valgtBehandling.status.kode, "AVSLU");

    }

    @Test
    @DisplayName("Behandle klage via NFP - påklaget vedtak opphevet")
    @Description("Behandle klage via NFP - stadfestet af NFP og opphevet av KA")
    public void klageOppheveAvKA() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.getPersonopplysninger().getFødselsdato());

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventTilSakHarKlage();
        klagebehandler.velgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(), "KLAGE_YTELSESVEDTAK_STADFESTET");

        // KA - klage kommer rett til KA uten totrinnsbehanling. Kan fortsette med samme klagebehandler.
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa.godkjennAlleFormkrav()
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);

        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse.bekreftOpphevet("NYE_OPPLYSNINGER")
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);
        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(), "KLAGE_YTELSESVEDTAK_OPPHEVET");

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.velgKlageBehandling();
        FatterVedtakBekreftelse aksjonspunkt = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NK));
        beslutter.bekreftAksjonspunkt(aksjonspunkt);
        klagebehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        klagebehandler.hentFagsak(sakId);
        klagebehandler.velgKlageBehandling();
        AksjonspunktBekreftelse bekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class)
                .bekreftOpphevet("NYE_OPPLYSNINGER")
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(bekreftelse);
        klagebehandler.fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling();

    }

    @Test
    @DisplayName("Behandle klage via KA - påklaget vedtak omgjort/medhold")
    @Description("Behandle klage via KA - stadfestet af NFP og medhold av KA")
    public void klageOmgjortAvKA() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.getPersonopplysninger().getFødselsdato());

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventTilSakHarKlage();
        klagebehandler.velgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(), "KLAGE_YTELSESVEDTAK_STADFESTET");

        // KA - klage kommer rett til KA uten totrinnsbehanling. Kan fortsette med samme klagebehandler.
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);

        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse
            .bekreftMedholdGunst("NYE_OPPLYSNINGER")
            .fritekstBrev("Brev");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);


        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.velgKlageBehandling();
        FatterVedtakBekreftelse fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NK));
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        klagebehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        klagebehandler.hentFagsak(sakId);
        klagebehandler.velgKlageBehandling();

        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse1 = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse1
                .bekreftMedholdGunst("NYE_OPPLYSNINGER")
                .fritekstBrev("Brev");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse1);
        klagebehandler.fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling();
    }

    @Test
    @DisplayName("Behandle klage via KA - avslag")
    @Description("Behandle klage via KA - stadfestet af NFP og medhold av KA")
    public void klageAvslaattAvKA() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.getPersonopplysninger().getFødselsdato());

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventTilSakHarKlage();
        klagebehandler.velgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(), "KLAGE_YTELSESVEDTAK_STADFESTET");

        // Behandle klage - KA
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa
                .klageErIkkeKonkret()
                .setBegrunnelse("Begrunnelse formkrav");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.velgKlageBehandling();
        FatterVedtakBekreftelse fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDERING_AV_FORMKRAV_KLAGE_KA));
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);
        verifiserBehandlingsresultat(beslutter.valgtBehandling.behandlingsresultat.toString(), "KLAGE_AVVIST");
    }

    @Test
    @DisplayName("Behandle klage via NFP - avvist av beslutter")
    @Description("Behandle klage via NFP - medhold av NFP avvist av beslutter send tilbake til NFP vurdert til delvist gunst")
    public void avvistAvBelutterNFP() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.getPersonopplysninger().getFødselsdato());

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventTilSakHarKlage();
        klagebehandler.velgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        String fritekstbrev1 = "Fritekst brev nfp.";
        String begrunnelse1 = "Fordi.";


        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftMedholdGunst("NYE_OPPLYSNINGER")
                .fritekstBrev(fritekstbrev1)
                .setBegrunnelse(begrunnelse1);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);
        verifiserLikhet(klagebehandler.valgtBehandling.behandlingsresultat.toString(), "KLAGE_MEDHOLD", "Behandlingsresultat");
        verifiserKlageVurderingOmgjoer(klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getKlageVurderingOmgjoer(), "GUNST_MEDHOLD_I_KLAGE");

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.ventTilSakHarKlage();
        beslutter.velgKlageBehandling();

        FatterVedtakBekreftelse fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse
                .avvisAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP), beslutter.kodeverk.VurderÅrsak.getKode("FEIL_REGEL"))
                .setBegrunnelse("Avvist av beslutter");
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        klagebehandler.erLoggetInnMedRolle(Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);
        klagebehandler.velgKlageBehandling();
        verifiserFritekst(klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getFritekstTilBrev(), fritekstbrev1);
        verifiserFritekst(klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getBegrunnelse(), begrunnelse1);
        String fritekstbrev2 = "Fritekst brev nr 2 .";
        String begrunnelse2 = "Fordi.";
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse1 = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse1
                .bekreftMedholdDelvisGunst("NYE_OPPLYSNINGER")
                .fritekstBrev(fritekstbrev2)
                .setBegrunnelse(begrunnelse2);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse1);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.ventTilSakHarKlage();
        beslutter.velgKlageBehandling();
        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP));
        beslutter.bekreftAksjonspunktMedDefaultVerdier(FatterVedtakBekreftelse.class);
        verifiserBehandlingsresultat(beslutter.valgtBehandling.behandlingsresultat.toString(), "KLAGE_MEDHOLD");
        verifiserFritekst(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getFritekstTilBrev(), fritekstbrev2);
        verifiserFritekst(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getBegrunnelse(), begrunnelse2);
        verifiserKlageVurderingOmgjoer(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getKlageVurderingOmgjoer(), "DELVIS_MEDHOLD_I_KLAGE");
        verifiserBehandlingsstatus(beslutter.valgtBehandling.status.kode, "AVSLU");
    }

    @Step("Oppretter førstegangsvedtak")
    private void opprettForstegangssoknadVedtak(long saksnummer) {
        // Opprette førstegangssøknad engangsstønad
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        verifiserBehandlingsresultat(saksbehandler.valgtBehandling.behandlingsresultat.toString(), "INNVILGET");

        saksbehandler.ventTilAvsluttetBehandling();
    }

    private void verifiserBehandlingsresultat(String verdiFaktisk, String verdiForventet) {
        verifiserLikhet(verdiFaktisk, verdiForventet, "Behandlingsresultat");
    }

    private void verifiserBehandlingsstatus(String verdiFaktisk, String verdiForventet) {
        verifiserLikhet(verdiFaktisk, verdiForventet, "Behandlingsstatus");
    }

    private void verifiserFritekst(String verdiFaktisk, String verdiForventet) {
        verifiserLikhet(verdiFaktisk, verdiForventet, "Fritekst");
    }

    private void verifiserKlageVurderingOmgjoer(String verdiFaktisk, String verdiForventet) {
        verifiserLikhet(verdiFaktisk, verdiForventet, "KlageVurderingOmgjoer");
    }
}
