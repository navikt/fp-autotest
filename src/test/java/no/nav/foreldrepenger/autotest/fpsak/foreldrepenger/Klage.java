package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravNfp;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageNfpBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.generator.familie.Familie;
import no.nav.foreldrepenger.generator.familie.Mor;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.kontrakter.fpsoknad.BrukerRolle;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("fpsak")
@Tag("foreldrepenger")
class Klage extends VerdikjedeTestBase {

    @Test
    @DisplayName("Klage med Medhold Ugunst NFP")
    @Description("Sender inn klage på førstegangsbehandling. Bekrefter medhold i Ugunst hos NFP. Beslutter og avslutter.")
    void klageMedholUgunstNFP() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build();
        var mor = familie.mor();
        var saksnummer = opprettForstegangsbehandlingMor(mor, familie);

        // Motta og behandle klage NFP
        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);

        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        klagebehandler.ventPåOgVelgKlageBehandling();

        var førstegangsbehandling = klagebehandler.førstegangsbehandling();
        var klageFormkravNfp = klagebehandler
                .hentAksjonspunktbekreftelse(new KlageFormkravNfp())
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        var fritekstBrev = "Fritektst til brev fra NFP.";
        var begrunnelse = "Begrunnelse NFP.";
        var vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(new VurderingAvKlageNfpBekreftelse())
                .bekreftMedholdUGunst("ULIK_VURDERING")
                .setBegrunnelse(begrunnelse);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        // Mellomlager og tilbakestiller
        assertThat(klagebehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP).getStatus())
                .as("Status for aksjonspunkt MANUELL_VURDERING_AV_KLAGE_NFP")
                .isEqualTo("UTFO");
        klagebehandler.mellomlagreKlage();
        assertThat(klagebehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP).getStatus())
                .as("Status for aksjonspunkt MANUELL_VURDERING_AV_KLAGE_NFP")
                .isEqualTo("UTFO");

        var vurderingAvKlageNfpBekreftelse1 = klagebehandler
                .hentAksjonspunktbekreftelse(new VurderingAvKlageNfpBekreftelse())
                .bekreftMedholdUGunst("ULIK_VURDERING")
                .fritekstBrev(fritekstBrev)
                .setBegrunnelse(begrunnelse);
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse1);

        klagebehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.getKlagevurdering().klageVurderingResultatNFP().klageVurderingOmgjør())
                .as("Klagevurderingomgjør fra NFP")
                .isEqualTo("UGUNST_MEDHOLD_I_KLAGE");
        assertThat(beslutter.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_OMGJORT_UGUNST);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().klageVurderingResultatNFP().begrunnelse())
                .as("Begrunnelse klagevurdering NFP")
                .isEqualTo(begrunnelse);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().klageVurderingResultatNFP().fritekstTilBrev())
                .as("Fritekst til brev for klagevurdering NFP")
                .isEqualTo(fritekstBrev);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().klageVurderingResultatNFP().klageMedholdÅrsak())
                .as("Årsak til klagevburdering fra NFP")
                .isEqualTo("ULIK_VURDERING");
    }

    @Test
    @DisplayName("Klage avvist i formkrav av NFP")
    @Description("Sender inn klage på førstegangsbehandling. NFP avslår formkrav (ikke konkret). Beslutter og avslutter.")
    void avvisFormkravNFP() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build();
        var mor = familie.mor();
        var saksnummer = opprettForstegangsbehandlingMor(mor, familie);

        // Motta og behandle klage NFP
        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();

        var førstegangsbehandling = klagebehandler.førstegangsbehandling();
        var klageFormkravNfp = klagebehandler
                .hentAksjonspunktbekreftelse(new KlageFormkravNfp())
                .klageErIkkeKonkret()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("blabla");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);
        klagebehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_AVVIST);
        assertThat(beslutter.valgtBehandling.getKlagevurdering().klageFormkravResultatNFP().avvistÅrsaker())
                .as("Årsak for avvisning")
                .contains("IKKE_KONKRET");
    }

    @Step("Klage: oppretter førstegangsbehandling")
    private Saksnummer opprettForstegangsbehandlingMor(Mor mor, Familie familie) {
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);

        return saksnummer;
    }
}
