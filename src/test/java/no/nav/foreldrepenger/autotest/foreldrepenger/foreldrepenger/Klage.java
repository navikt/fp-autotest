package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;


import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravKa;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravNfp;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse.VurderingAvKlageNfpBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse.VurderingAvKlageNkBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;

@Tag("fpsak")
@Tag("foreldrepenger")
@Tag("fluoritt")
public class Klage extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Klage med Medhold Ugunst NFP")
    @Description("Sender inn klage på førstegangsbehandling. Bekrefter medhold i Ugunst hos NFP. Beslutter og avslutter.")
    public void klageMedholUgunstNFP() {
        // opprette førstegangsbehandling til vedtak
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        long saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Aktoer.Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);

        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        klagebehandler.ventTilSakHarKlage();
        klagebehandler.velgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp.godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        String fritekstBrev = "Fritektst til brev fra NFP.";
        String begrunnelse = "Begrunnelse NFP.";
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse.bekreftMedholdUGunst("ULIK_VURDERING")
                .fritekstBrev(fritekstBrev)
                .setBegrunnelse(begrunnelse);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        //Mellomlager og tilbakestiller
        verifiserLikhet(klagebehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP).getStatus().kode,
                "UTFO", "Vurdering av klage");
        klagebehandler.mellomlagreOgGjennåpneKlage();
        verifiserLikhet(klagebehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP).getStatus().kode,
                "OPPR", "Vurdering av klage");

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse1 = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse1
                .bekreftMedholdUGunst("ULIK_VURDERING")
                .fritekstBrev(fritekstBrev)
                .setBegrunnelse(begrunnelse);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse1);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.velgKlageBehandling();

        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP));
        beslutter.bekreftAksjonspunktMedDefaultVerdier(FatterVedtakBekreftelse.class);
        verifiserKlageVurderingOmgjoer(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getKlageVurderingOmgjoer(), "UGUNST_MEDHOLD_I_KLAGE");
        verifiserBehandlingsresultat(beslutter.valgtBehandling.behandlingsresultat.toString(), "KLAGE_MEDHOLD");
        verifiserFritekst(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getBegrunnelse(), begrunnelse);
        verifiserFritekst(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getFritekstTilBrev(), fritekstBrev);
        verifiserLikhet(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getKlageMedholdArsak(), "ULIK_VURDERING", "Årsak");
        verifiserBehandlingsstatus(beslutter.valgtBehandling.status.kode, "AVSLU");


    }

    @Test
    @DisplayName("Klage med hjemsende av KA")
    @Description("Sender inn klage på førstegangsbehandling. NFP sender videre til KA. KA bekrefter hjemsende. Beslutter og avslutter.")
    public void hjemsendeKA() {
        // opprette førstegangsbehandling til vedtak
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        long saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Aktoer.Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventTilSakHarKlage();
        klagebehandler.velgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        debugLoggBehandling(klagebehandler.valgtBehandling);
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(), "KLAGE_YTELSESVEDTAK_STADFESTET");

        // KA
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa.godkjennAlleFormkrav()
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);
        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse.bekreftHjemsende()
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);

        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(), "HJEMSENDE_UTEN_OPPHEVE");
        verifiserKlageVurdering(klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNK().getKlageVurdering(), "HJEMSENDE_UTEN_Å_OPPHEVE");
        verifiserBehandlingsstatus(klagebehandler.valgtBehandling.status.kode, "AVSLU");
    }

    @Test
    @DisplayName("Klage med stadfestet av KA")
    @Description("Sender inn klage på førstegangsbehandling. NFP sender videre til KA. KA bekrefter stadfestet. Beslutter og avslutter.")
    public void stadfesteKA() {
        // opprette førstegangsbehandling til vedtak
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        long saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Aktoer.Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventTilSakHarKlage();
        klagebehandler.velgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("Begrunnelse NFP.");
        debugLoggBehandling(klagebehandler.valgtBehandling);
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(), "KLAGE_YTELSESVEDTAK_STADFESTET");

        // KA
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);
        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse.bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.velgKlageBehandling();
        beslutter.bekreftAksjonspunktMedDefaultVerdier(FatterVedtakBekreftelse.class);
        verifiserBehandlingsresultat(beslutter.valgtBehandling.behandlingsresultat.toString(), "KLAGE_YTELSESVEDTAK_STADFESTET");
        verifiserKlageVurdering(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNK().getKlageVurdering(), "STADFESTE_YTELSESVEDTAK");
        verifiserBehandlingsstatus(beslutter.valgtBehandling.status.kode, "AVSLU");
    }

    @Test
    @DisplayName("Klage med Medhold Delvis Gunst KA")
    @Description("Sender inn klage på førstegangsbehandling. NFP sender videre til KA. KA bekrefter medhold med delvis gunst. Beslutter og avslutter.")
    public void medholdDelvisGunstKA() {
        // opprette førstegangsbehandling til vedtak
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        long saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Aktoer.Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventTilSakHarKlage();
        klagebehandler.velgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp.godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse.bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(), "KLAGE_YTELSESVEDTAK_STADFESTET");

        // KA
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa.godkjennAlleFormkrav()
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);
        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse
                .bekreftMedholdDelvisGunst("ULIK_VURDERING")
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.ventTilSakHarKlage();
        beslutter.velgKlageBehandling();
        beslutter.bekreftAksjonspunktMedDefaultVerdier(FatterVedtakBekreftelse.class);

        verifiserBehandlingsresultat(beslutter.valgtBehandling.behandlingsresultat.toString(), "KLAGE_MEDHOLD");
        verifiserKlageVurderingOmgjoer(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNK().getKlageVurderingOmgjoer(), "DELVIS_MEDHOLD_I_KLAGE");
        verifiserLikhet(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNK().getKlageMedholdArsak(), "ULIK_VURDERING");
        verifiserBehandlingsstatus(beslutter.valgtBehandling.status.kode, "AVSLU");
    }

    @Test
    @DisplayName("Klage avvist i formkrav av NFP")
    @Description("Sender inn klage på førstegangsbehandling. NFP avslår formkrav (ikke konkret). Beslutter og avslutter.")
    public void avvisFormkravNFP() {
        // opprette førstegangsbehandling til vedtak
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        long saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Aktoer.Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventTilSakHarKlage();
        klagebehandler.velgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp.klageErIkkeKonkret()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.velgKlageBehandling();
        FatterVedtakBekreftelse fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.setBegrunnelse("Godkjent");
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);
        verifiserBehandlingsresultat(beslutter.valgtBehandling.behandlingsresultat.toString(), "KLAGE_AVVIST");
        verifiserInneholder(beslutter.valgtBehandling.getKlagevurdering().getKlageFormkravResultatNFP().getAvvistArsaker(), new Kode("KLAGE_AVVIST_AARSAK", "IKKE_KONKRET"));
        verifiserBehandlingsstatus(beslutter.valgtBehandling.status.kode, "AVSLU");
    }

    @Step("Klage: oppretter førstegangsbehandling")
    private long opprettForstegangsbehandling(TestscenarioDto testscenario) {
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
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
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);

        return saksnummer;
    }

    private void verifiserFritekst(String verdiFaktisk, String verdiForventet) {
        verifiserLikhet(verdiFaktisk, verdiForventet, "Fritekst");
    }

    private void verifiserKlageVurderingOmgjoer(String verdiFaktisk, String verdiForventet) {
        verifiserLikhet(verdiFaktisk, verdiForventet, "KlageVurderingOmgjoer");
    }

    private void verifiserBehandlingsstatus(String verdiFaktisk, String verdiForventet) {
        verifiserLikhet(verdiFaktisk, verdiForventet, "Behandlingsstatus");
    }

    private void verifiserBehandlingsresultat(String verdiFaktisk, String verdiForventet) {
        verifiserLikhet(verdiFaktisk, verdiForventet, "Behandlingsresultat");
    }

    private void verifiserKlageVurdering(String verdiFaktisk, String verdiForventet) {
        verifiserLikhet(verdiFaktisk, verdiForventet, "KlageVurdering");
    }
}
