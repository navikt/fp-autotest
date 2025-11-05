package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadFødsel;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.VurderÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravNfp;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageNfpBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("fpsak")
@Tag("engangsstonad")
class Klage extends VerdikjedeTestBase {

    @Test
    @DisplayName("Behandle klage via NFP - medhold")
    @Description("Behandle klage via NFP - vurdert til medhold")
    void klageMedholdNFP() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad);

        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage NFP
        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);

        klagebehandler.ventPåOgVelgKlageBehandling();

        KlageFormkravNfp klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(new KlageFormkravNfp());
        klageFormkravNfp
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(klagebehandler.førstegangsbehandling().uuid)
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        VurderingAvKlageNfpBekreftelse vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(new VurderingAvKlageNfpBekreftelse());
        vurderingAvKlageNfpBekreftelse
                .bekreftMedholdGunst("PROSESSUELL_FEIL")
                .fritekstBrev("Fritektst til brev fra klagebehandler.")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        klagebehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());
        assertThat(klagebehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_MEDHOLD);
        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_MEDHOLD);
    }

    @Test
    @DisplayName("Behandle klage via NFP - avvist av beslutter")
    @Description("Behandle klage via NFP - medhold av NFP avvist av beslutter send tilbake til NFP vurdert til delvist gunst")
    void avvistAvBelutterNFP() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad);
        opprettForstegangssoknadVedtak(saksnummer);

        // Motta og behandle klage - NFP
        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();

        var klageFormkravNfp = klagebehandler
                .hentAksjonspunktbekreftelse(new KlageFormkravNfp())
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(klagebehandler.førstegangsbehandling().uuid)
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        var fritekstbrev1 = "Fritekst brev nfp.";
        var begrunnelse1 = "Fordi.";
        var vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(new VurderingAvKlageNfpBekreftelse())
                .bekreftMedholdGunst("NYE_OPPLYSNINGER")
                .fritekstBrev(fritekstbrev1)
                .setBegrunnelse(begrunnelse1);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        klagebehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());
        assertThat(klagebehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_MEDHOLD);
        assertThat(klagebehandler.valgtBehandling.getKlagevurdering().klageVurderingResultatNFP().klageVurderingOmgjoer())
                .as("Klagevurderingsresultat")
                .isEqualTo("GUNST_MEDHOLD_I_KLAGE");

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();

        var fatterVedtakBekreftelse = beslutter
                .hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse())
                .avvisAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP), VurderÅrsak.FEIL_REGEL)
                .setBegrunnelse("Avvist av beslutter");
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();
        assertThat(klagebehandler.valgtBehandling.getKlagevurdering().klageVurderingResultatNFP().fritekstTilBrev())
                .as("Fritekst")
                .isEqualTo(fritekstbrev1);
        assertThat(klagebehandler.valgtBehandling.getKlagevurdering().klageVurderingResultatNFP().begrunnelse())
                .as("Begrunnelse")
                .isEqualTo(begrunnelse1);
        var fritekstbrev2 = "Fritekst brev nr 2 .";
        var begrunnelse2 = "Fordi.";
        var vurderingAvKlageNfpBekreftelse1 = klagebehandler
                .hentAksjonspunktbekreftelse(new VurderingAvKlageNfpBekreftelse())
                .bekreftMedholdDelvisGunst("NYE_OPPLYSNINGER")
                .fritekstBrev(fritekstbrev2)
                .setBegrunnelse(begrunnelse2);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse1);

        klagebehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_DELVIS_MEDHOLD);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().klageVurderingResultatNFP().fritekstTilBrev())
                .as("Fritekst")
                .isEqualTo(fritekstbrev2);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().klageVurderingResultatNFP().begrunnelse())
                .as("begrunnelse2")
                .isEqualTo(begrunnelse1);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().klageVurderingResultatNFP().klageVurderingOmgjoer())
                .as("KlageVurderingOmgjoer")
                .isEqualTo("DELVIS_MEDHOLD_I_KLAGE");
    }

    @Step("Oppretter førstegangsvedtak")
    private void opprettForstegangssoknadVedtak(Saksnummer saksnummer) {
        // Opprette førstegangssøknad engangsstønad
        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
    }
}
