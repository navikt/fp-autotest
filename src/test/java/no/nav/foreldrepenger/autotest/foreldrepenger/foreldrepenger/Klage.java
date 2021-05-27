package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Kode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelseUtenTotrinn;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravKa;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravNfp;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse.VurderingAvKlageNfpBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse.VurderingAvKlageNkBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("fpsak")
@Tag("foreldrepenger")
@Tag("fluoritt")
class Klage extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Klage med Medhold Ugunst NFP")
    @Description("Sender inn klage på førstegangsbehandling. Bekrefter medhold i Ugunst hos NFP. Beslutter og avslutter.")
    void klageMedholUgunstNFP() {
        // opprette førstegangsbehandling til vedtak
        var testscenario = opprettTestscenario("50");
        var saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        var sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.hentFagsak(sakId);

        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        klagebehandler.ventPåOgVelgKlageBehandling();

        var førstegangsbehandling = klagebehandler.førstegangsbehandling();
        var klageFormkravNfp = klagebehandler
                .hentAksjonspunktbekreftelse(KlageFormkravNfp.class)
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        var fritekstBrev = "Fritektst til brev fra NFP.";
        var begrunnelse = "Begrunnelse NFP.";
        var vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class)
                .bekreftMedholdUGunst("ULIK_VURDERING")
                .fritekstBrev(fritekstBrev)
                .setBegrunnelse(begrunnelse);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        // Mellomlager og tilbakestiller
        assertThat(klagebehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP).getStatus().kode)
                .as("Status for aksjonspunkt MANUELL_VURDERING_AV_KLAGE_NFP")
                .isEqualTo("UTFO");
        klagebehandler.mellomlagreKlage();
        assertThat(klagebehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP).getStatus().kode)
                .as("Status for aksjonspunkt MANUELL_VURDERING_AV_KLAGE_NFP")
                .isEqualTo("UTFO");

        var vurderingAvKlageNfpBekreftelse1 = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class)
                .bekreftMedholdUGunst("ULIK_VURDERING")
                .fritekstBrev(fritekstBrev)
                .setBegrunnelse(begrunnelse);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse1);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getKlageVurderingOmgjoer().kode)
                .as("Klagevurderingomgjør fra NFP")
                .isEqualTo("UGUNST_MEDHOLD_I_KLAGE");
        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_MEDHOLD);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getBegrunnelse())
                .as("Begrunnelse klagevurdering NFP")
                .isEqualTo(begrunnelse);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getFritekstTilBrev())
                .as("Fritekst til brev for klagevurdering NFP")
                .isEqualTo(fritekstBrev);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getKlageMedholdArsak().kode)
                .as("Årsak til klagevburdering fra NFP")
                .isEqualTo("ULIK_VURDERING");
    }

    @Test
    @DisplayName("Klage med hjemsende av KA")
    @Description("Sender inn klage på førstegangsbehandling. NFP sender videre til KA. KA bekrefter hjemsende. Beslutter og avslutter.")
    void hjemsendeKA() {
        // opprette førstegangsbehandling til vedtak
        var testscenario = opprettTestscenario("50");
        var saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        var sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventPåOgVelgKlageBehandling();

        var førstegangsbehandling = klagebehandler.førstegangsbehandling();
        var klageFormkravNfp = klagebehandler
                .hentAksjonspunktbekreftelse(KlageFormkravNfp.class)
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla");
        debugLoggBehandling(klagebehandler.valgtBehandling);
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        var vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class)
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        assertThat(klagebehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_YTELSESVEDTAK_STADFESTET);

        // KA
        var klageFormkravKa = klagebehandler
                .hentAksjonspunktbekreftelse(KlageFormkravKa.class)
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);
        var vurderingAvKlageNkBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class)
                .bekreftHjemsende()
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);
        klagebehandler.ventTilAvsluttetBehandling();

        assertThat(klagebehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.HJEMSENDE_UTEN_OPPHEVE);
        assertThat(klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNK().getKlageVurdering().kode)
                .as("Klagevurderingsresultat fra NK")
                .isEqualTo("HJEMSENDE_UTEN_Å_OPPHEVE");
    }

    @Test
    @DisplayName("Klage med stadfestet av KA")
    @Description("Sender inn klage på førstegangsbehandling. NFP sender videre til KA. KA bekrefter stadfestet. Beslutter og avslutter.")
    void stadfesteKA() {
        // opprette førstegangsbehandling til vedtak
        var testscenario = opprettTestscenario("50");
        var saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        var sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();

        var førstegangsbehandling = klagebehandler.førstegangsbehandling();
        var klageFormkravNfp = klagebehandler
                .hentAksjonspunktbekreftelse(KlageFormkravNfp.class)
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("Begrunnelse NFP.");
        debugLoggBehandling(klagebehandler.valgtBehandling);
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        var vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class)
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        assertThat(klagebehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_YTELSESVEDTAK_STADFESTET);

        // KA
        var klageFormkravKa = klagebehandler
                .hentAksjonspunktbekreftelse(KlageFormkravKa.class)
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);
        var vurderingAvKlageNkBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class)
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(bekreftelse);

        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelseUtenTotrinn.class);
        klagebehandler.ventTilAvsluttetBehandling();

        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();
        assertThat(klagebehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_YTELSESVEDTAK_STADFESTET);
        assertThat(klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNK().getKlageVurdering().kode)
                .as("Klagevurderingsresultat NK")
                .isEqualTo("STADFESTE_YTELSESVEDTAK");
        assertThat(klagebehandler.valgtBehandling.status)
                .as("Behandlingsstatus")
                .isEqualTo(BehandlingStatus.AVSLUTTET);
    }

    @Test
    @DisplayName("Klage med Medhold Delvis Gunst KA")
    @Description("Sender inn klage på førstegangsbehandling. NFP sender videre til KA. KA bekrefter medhold med delvis gunst. Beslutter og avslutter.")
    void medholdDelvisGunstKA() {
        // opprette førstegangsbehandling til vedtak
        var testscenario = opprettTestscenario("50");
        var saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        var sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();

        var førstegangsbehandling = klagebehandler.førstegangsbehandling();
        var klageFormkravNfp = klagebehandler
                .hentAksjonspunktbekreftelse(KlageFormkravNfp.class)
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        var vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class)
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        assertThat(klagebehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_YTELSESVEDTAK_STADFESTET);

        // KA
        var klageFormkravKa = klagebehandler
                .hentAksjonspunktbekreftelse(KlageFormkravKa.class)
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);
        var vurderingAvKlageNkBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class)
                .bekreftMedholdDelvisGunst("ULIK_VURDERING")
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(bekreftelse);

        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelseUtenTotrinn.class);
        klagebehandler.ventTilAvsluttetBehandling();

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_MEDHOLD);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNK().getKlageVurderingOmgjoer().kode)
                .as("Vurdering omgjør til klagervurderingsresultat NK")
                .isEqualTo("DELVIS_MEDHOLD_I_KLAGE");
        assertThat(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNK().getKlageMedholdArsak().kode)
                .as("KlageMedholdÅrsak til klagevurdering NK")
                .isEqualTo("ULIK_VURDERING");
    }

    @Test
    @DisplayName("Klage avvist i formkrav av NFP")
    @Description("Sender inn klage på førstegangsbehandling. NFP avslår formkrav (ikke konkret). Beslutter og avslutter.")
    void avvisFormkravNFP() {
        // opprette førstegangsbehandling til vedtak
        var testscenario = opprettTestscenario("50");
        var saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        var sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();

        var førstegangsbehandling = klagebehandler.førstegangsbehandling();
        var klageFormkravNfp = klagebehandler
                .hentAksjonspunktbekreftelse(KlageFormkravNfp.class)
                .klageErIkkeKonkret()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_AVVIST);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().getKlageFormkravResultatNFP().getAvvistArsaker())
                .as("Årsak for avvisning")
                .contains(new Kode("IKKE_KONKRET"));
    }

    @Step("Klage: oppretter førstegangsbehandling")
    private long opprettForstegangsbehandling(TestscenarioDto testscenario) {
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);

        return saksnummer;
    }
}
