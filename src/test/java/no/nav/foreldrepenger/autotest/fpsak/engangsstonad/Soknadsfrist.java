package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.generator.soknad.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadFødsel;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.VurderÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.common.domain.BrukerRolle;

@Tag("foreldrepenger")
class Soknadsfrist extends FpsakTestBase {

    @Test
    @DisplayName("Behandle søknadsfrist og sent tilbake")
    @Description("Behandle søknadsfrist og sent tilbake på grunn av søknadsfrist. Manglende fødsel.")
    void behandleSøknadsfristOgSentTilbakePåGrunnAvSøknadsfrist() {
        var familie = new Familie("55", SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = LocalDate.now().minusMonths(7);
        var søknad = lagEngangstønadFødsel(BrukerRolle.MOR, fødselsdato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(7));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        var vurderSoknadsfristBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristBekreftelse.class)
                .bekreftVilkårErOk();
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL))
                .avvisAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET),
                        VurderÅrsak.FEIL_FAKTA);
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL).getStatus())
                .as("Aksjonspunktstatus for SJEKK_MANGLENDE_FØDSEL")
                .isEqualTo("UTFO");
        assertThat(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET).getStatus())
                .as("Aksjonspunktstatus for MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET")
                .isEqualTo("OPPR");
    }

    @Test
    @DisplayName("Behandle søknadsfrist og sent tilbake på grunn av fødsel")
    @Description("Behandle søknadsfrist og sent tilbake på grunn av fødsel - tester tilbakesending")
    void behandleSøknadsfristOgSentTilbakePåGrunnAvFodsel() {
        var familie = new Familie("55", SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = LocalDate.now().minusMonths(7);
        var søknad = lagEngangstønadFødsel(BrukerRolle.MOR, fødselsdato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(7));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderSoknadsfristBekreftelse.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var vedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET))
                .avvisAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL), VurderÅrsak.FEIL_FAKTA);
        beslutter.bekreftAksjonspunkt(vedtakBekreftelse);

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL).getStatus())
                .as("Aksjonspunktstatus for SJEKK_MANGLENDE_FØDSEL")
                .isEqualTo("OPPR");

        var harSøknadsfristAP = saksbehandler.valgtBehandling.getAksjonspunkt().stream()
                .anyMatch(ap -> ap.getDefinisjon()
                        .equals(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET));
        assertThat(harSøknadsfristAP)
                .as("Uforventet aksjonspunkt MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET")
                .isFalse();
    }

}
