package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelseUtenTotrinn;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravKa;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravNfp;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse.VurderingAvKlageNfpBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse.VurderingAvKlageNkBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("fpsak")
@Tag("foreldrepenger")
@Tag("fluoritt")
public class Klage extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Klage med Medhold Ugunst NFP")
    @Description("Sender inn klage på førstegangsbehandling. Bekrefter medhold i Ugunst hos NFP. Beslutter og avslutter.")
    public void klageMedholUgunstNFP() {
        // opprette førstegangsbehandling til vedtak
        TestscenarioDto testscenario = opprettTestscenario("50");
        long saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Aktoer.Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);

        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp.godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        String fritekstBrev = "Fritektst til brev fra NFP.";
        String begrunnelse = "Begrunnelse NFP.";
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse.bekreftMedholdUGunst("ULIK_VURDERING")
                .fritekstBrev(fritekstBrev)
                .setBegrunnelse(begrunnelse);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        // Mellomlager og tilbakestiller
        verifiserLikhet(klagebehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP).getStatus().kode,
                "UTFO", "Vurdering av klage");
        klagebehandler.mellomlagreKlage();
        verifiserLikhet(klagebehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP).getStatus().kode,
                "UTFO", "Vurdering av klage");

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse1 = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse1
                .bekreftMedholdUGunst("ULIK_VURDERING")
                .fritekstBrev(fritekstBrev)
                .setBegrunnelse(begrunnelse);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse1);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserKlageVurderingOmgjoer(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getKlageVurderingOmgjoer().kode,
                "UGUNST_MEDHOLD_I_KLAGE");
        verifiserBehandlingsresultat(beslutter.valgtBehandling.behandlingsresultat.toString(), "KLAGE_MEDHOLD");
        verifiserFritekst(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getBegrunnelse(),
                begrunnelse);
        verifiserFritekst( beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getFritekstTilBrev(),
                fritekstBrev);
        verifiserLikhet(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNFP().getKlageMedholdArsak().kode,
                "ULIK_VURDERING", "Årsak");

    }

    @Test
    @DisplayName("Klage med hjemsende av KA")
    @Description("Sender inn klage på førstegangsbehandling. NFP sender videre til KA. KA bekrefter hjemsende. Beslutter og avslutter.")
    public void hjemsendeKA() {
        // opprette førstegangsbehandling til vedtak
        TestscenarioDto testscenario = opprettTestscenario("50");
        long saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Aktoer.Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);

        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        debugLoggBehandling(klagebehandler.valgtBehandling);
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(),
                "KLAGE_YTELSESVEDTAK_STADFESTET");

        // KA
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa.godkjennAlleFormkrav()
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);
        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse.bekreftHjemsende()
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);
        klagebehandler.ventTilAvsluttetBehandling();

        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(),
                "HJEMSENDE_UTEN_OPPHEVE");
        verifiserKlageVurdering(klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNK().getKlageVurdering().kode,
                "HJEMSENDE_UTEN_Å_OPPHEVE");
    }

    @Test
    @DisplayName("Klage med stadfestet av KA")
    @Description("Sender inn klage på førstegangsbehandling. NFP sender videre til KA. KA bekrefter stadfestet. Beslutter og avslutter.")
    public void stadfesteKA() {
        // opprette førstegangsbehandling til vedtak
        TestscenarioDto testscenario = opprettTestscenario("50");
        long saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Aktoer.Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setBegrunnelse("Begrunnelse NFP.");
        debugLoggBehandling(klagebehandler.valgtBehandling);
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse
                .bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(),
                "KLAGE_YTELSESVEDTAK_STADFESTET");

        // KA
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa
                .godkjennAlleFormkrav()
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);
        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse.bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
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
        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(),
                "KLAGE_YTELSESVEDTAK_STADFESTET");
        verifiserKlageVurdering(klagebehandler.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNK().getKlageVurdering().kode,
                "STADFESTE_YTELSESVEDTAK");
        verifiserBehandlingsstatus(klagebehandler.valgtBehandling.status.kode, "AVSLU");
    }

    @Test
    @DisplayName("Klage med Medhold Delvis Gunst KA")
    @Description("Sender inn klage på førstegangsbehandling. NFP sender videre til KA. KA bekrefter medhold med delvis gunst. Beslutter og avslutter.")
    public void medholdDelvisGunstKA() {
        // opprette førstegangsbehandling til vedtak
        TestscenarioDto testscenario = opprettTestscenario("50");
        long saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Aktoer.Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp.godkjennAlleFormkrav()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNfpBekreftelse.class);
        vurderingAvKlageNfpBekreftelse.bekreftStadfestet()
                .fritekstBrev("Fritekst brev fra nfp")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);
        verifiserBehandlingsresultat(klagebehandler.valgtBehandling.behandlingsresultat.toString(),
                "KLAGE_YTELSESVEDTAK_STADFESTET");

        // KA
        KlageFormkravKa klageFormkravKa = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravKa.class);
        klageFormkravKa.godkjennAlleFormkrav()
                .setBegrunnelse("blabla begrunnelse");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);
        VurderingAvKlageNkBekreftelse vurderingAvKlageNkBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageNkBekreftelse.class);
        vurderingAvKlageNkBekreftelse
                .bekreftMedholdDelvisGunst("ULIK_VURDERING")
                .fritekstBrev("Fritekst brev fra KA")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(bekreftelse);

        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelseUtenTotrinn.class);
        klagebehandler.ventTilAvsluttetBehandling();

        verifiserBehandlingsresultat(beslutter.valgtBehandling.behandlingsresultat.toString(), "KLAGE_MEDHOLD");
        verifiserKlageVurderingOmgjoer(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNK().getKlageVurderingOmgjoer().kode,
                "DELVIS_MEDHOLD_I_KLAGE");
        verifiserLikhet(beslutter.valgtBehandling.getKlagevurdering().getKlageVurderingResultatNK().getKlageMedholdArsak().kode,
                "ULIK_VURDERING");
    }

    @Test
    @DisplayName("Klage avvist i formkrav av NFP")
    @Description("Sender inn klage på førstegangsbehandling. NFP avslår formkrav (ikke konkret). Beslutter og avslutter.")
    public void avvisFormkravNFP() {
        // opprette førstegangsbehandling til vedtak
        TestscenarioDto testscenario = opprettTestscenario("50");
        long saksnummer = opprettForstegangsbehandling(testscenario);

        // Motta og behandle klage NFP
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long sakId = fordel.sendInnKlage(null, testscenario, saksnummer);
        klagebehandler.erLoggetInnMedRolle(Aktoer.Rolle.KLAGEBEHANDLER);
        klagebehandler.hentFagsak(sakId);
        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(KlageFormkravNfp.class);
        klageFormkravNfp.klageErIkkeKonkret()
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(sakId);
        beslutter.ventPåOgVelgKlageBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        verifiserBehandlingsresultat(beslutter.valgtBehandling.behandlingsresultat.toString(), "KLAGE_AVVIST");
        verifiserInneholder(beslutter.valgtBehandling.getKlagevurdering().getKlageFormkravResultatNFP().getAvvistArsaker(),
                new Kode("KLAGE_AVVIST_AARSAK", "IKKE_KONKRET"));
    }

    @Step("Klage: oppretter førstegangsbehandling")
    private long opprettForstegangsbehandling(TestscenarioDto testscenario) {
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
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

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
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
