package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadFødsel;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.VurderÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Execution(ExecutionMode.CONCURRENT)
@Tag("foreldrepenger")
class Soknadsfrist extends FpsakTestBase {

    @Test
    @DisplayName("Behandle søknadsfrist og sent tilbake")
    @Description("Behandle søknadsfrist og sent tilbake på grunn av søknadsfrist")
    void behandleSøknadsfristOgSentTilbakePåGrunnAvSøknadsfrist() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        String aktørID = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(7);
        EngangstønadBuilder søknad = lagEngangstønadFødsel(aktørID, SøkersRolle.MOR, fødselsdato);

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

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
        assertThat(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL).getStatus().kode)
                .as("Aksjonspunktstatus for SJEKK_MANGLENDE_FØDSEL")
                .isEqualTo("UTFO");
        assertThat(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET).getStatus().kode)
                .as("Aksjonspunktstatus for MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET")
                .isEqualTo("OPPR");
    }

    @Test
    @DisplayName("Behandle søknadsfrist og sent tilbake på grunn av fødsel")
    @Description("Behandle søknadsfrist og sent tilbake på grunn av fødsel - tester tilbakesending")
    void behandleSøknadsfristOgSentTilbakePåGrunnAvFodsel() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        String aktørID = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(7);
        EngangstønadBuilder søknad = lagEngangstønadFødsel(aktørID, SøkersRolle.MOR, fødselsdato);

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

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
        assertThat(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL).getStatus().kode)
                .as("Aksjonspunktstatus for SJEKK_MANGLENDE_FØDSEL")
                .isEqualTo("OPPR");

        var harSøknadsfristAP = saksbehandler.valgtBehandling.getAksjonspunkter().stream()
                .anyMatch(ap -> ap.getDefinisjon().kode
                        .equals(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET));
        assertThat(harSøknadsfristAP)
                .as("Uforventet aksjonspunkt MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET")
                .isFalse();
    }

}
