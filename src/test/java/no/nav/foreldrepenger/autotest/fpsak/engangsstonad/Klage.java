package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadFødsel;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.VurderÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravKa;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravNfp;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse.VurderingAvKlageNfpBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse.VurderingAvKlageNkBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.common.domain.BrukerRolle;

@Tag("fpsak")
@Tag("engangsstonad")
class Klage extends FpsakTestBase {

    @Test
    @DisplayName("Behandle klage via NFP - medhold")
    @Description("Behandle klage via NFP - vurdert til medhold")
    void klageMedholdNFP() {
        var familie = new Familie("50", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(BrukerRolle.MOR, fødselsdato);
        var saksnummer = mor.søk(søknad.build());

        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage NFP
        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);

        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(klagebehandler.førstegangsbehandling().uuid)
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
        assertThat(klagebehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_MEDHOLD);
        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_MEDHOLD);
    }

    @Test
    @DisplayName("Behandle klage via NFP - påklaget vedtak opphevet")
    @Description("Behandle klage via NFP - stadfestet af NFP og opphevet av KA")
    void klageOppheveAvKA() {
        var familie = new Familie("50", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(BrukerRolle.MOR, fødselsdato);
        var saksnummer = mor.søk(søknad.build());
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);

        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        var førstegangsbehandling = klagebehandler.førstegangsbehandling();
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        assertThat(klagebehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_YTELSESVEDTAK_STADFESTET);

        // KA - klage kommer rett til KA uten totrinnsbehanling. Kan fortsette med samme klagebehandler.
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);

        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse.bekreftOpphevet("NYE_OPPLYSNINGER")
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        assertThat(klagebehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_YTELSESVEDTAK_OPPHEVET);

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);
        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();

        klagebehandler.fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling();

    }

    @Test
    @DisplayName("Behandle klage via KA - påklaget vedtak omgjort/medhold")
    @Description("Behandle klage via KA - stadfestet af NFP og medhold av KA")
    void klageOmgjortAvKA() {
        var familie = new Familie("50", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(BrukerRolle.MOR, fødselsdato);
        var saksnummer = mor.søk(søknad.build());
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);

        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        var førstegangsbehandling = klagebehandler.førstegangsbehandling();
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        assertThat(klagebehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_YTELSESVEDTAK_STADFESTET);

        // KA - klage kommer rett til KA uten totrinnsbehanling. Kan fortsette med samme klagebehandler.
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);

        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse
                .bekreftMedholdGunst("NYE_OPPLYSNINGER")
                .fritekstBrev("Brev");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();

        klagebehandler.fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling();
    }

    @Test
    @DisplayName("Behandle klage via KA - avslag")
    @Description("Behandle klage via KA - stadfestet af NFP og medhold av KA")
    void klageAvslaattAvKA() {
        var familie = new Familie("50", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(BrukerRolle.MOR, fødselsdato);
        var saksnummer = mor.søk(søknad.build());
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        var førstegangsbehandling = klagebehandler.førstegangsbehandling();
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        assertThat(klagebehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_YTELSESVEDTAK_STADFESTET);

        // Behandle klage - KA
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa
                .klageErIkkeKonkret()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("Begrunnelse formkrav");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();
        klagebehandler.fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling();
        assertThat(klagebehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_AVVIST);
    }

    @Test
    @DisplayName("Behandle klage via NFP - avvist av beslutter")
    @Description("Behandle klage via NFP - medhold av NFP avvist av beslutter send tilbake til NFP vurdert til delvist gunst")
    void avvistAvBelutterNFP() {
        var familie = new Familie("50", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(BrukerRolle.MOR, fødselsdato);
        var saksnummer = mor.søk(søknad.build());
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();

        var klageFormkravNfp = klagebehandler
                .hentAksjonspunktbekreftelse(KlageFormkravNfp.class)
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(klagebehandler.førstegangsbehandling().uuid)
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        var fritekstbrev1 = "Fritekst brev nfp.";
        var begrunnelse1 = "Fordi.";
        var vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class)
                .bekreftMedholdGunst("NYE_OPPLYSNINGER")
                .fritekstBrev(fritekstbrev1)
                .setBegrunnelse(begrunnelse1);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        assertThat(klagebehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_MEDHOLD);
        assertThat(klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getKlageVurderingOmgjoer().kode)
                .as("Klagevurderingsresultat")
                .isEqualTo("GUNST_MEDHOLD_I_KLAGE");

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();

        var fatterVedtakBekreftelse = beslutter
                .hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .avvisAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP), VurderÅrsak.FEIL_REGEL)
                .setBegrunnelse("Avvist av beslutter");
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();
        assertThat(klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getFritekstTilBrev())
                .as("Fritekst")
                .isEqualTo(fritekstbrev1);
        assertThat(klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getBegrunnelse())
                .as("Begrunnelse")
                .isEqualTo(begrunnelse1);
        var fritekstbrev2 = "Fritekst brev nr 2 .";
        var begrunnelse2 = "Fordi.";
        var vurderingAvKlageNfpBekreftelse1 = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class)
                .bekreftMedholdDelvisGunst("NYE_OPPLYSNINGER")
                .fritekstBrev(fritekstbrev2)
                .setBegrunnelse(begrunnelse2);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse1);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_MEDHOLD);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getFritekstTilBrev())
                .as("Fritekst")
                .isEqualTo(fritekstbrev2);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getBegrunnelse())
                .as("begrunnelse2")
                .isEqualTo(begrunnelse1);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getKlageVurderingOmgjoer().kode)
                .as("KlageVurderingOmgjoer")
                .isEqualTo("DELVIS_MEDHOLD_I_KLAGE");
    }

    @Step("Oppretter førstegangsvedtak")
    private void opprettForstegangssoknadVedtak(long saksnummer) {
        // Opprette førstegangssøknad engangsstønad
        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        saksbehandler.ventTilAvsluttetBehandling();
    }
}
