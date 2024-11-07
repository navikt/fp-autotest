package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadFødsel;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;
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
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("foreldrepenger")
class Soknadsfrist extends FpsakTestBase {

    @Test
    @DisplayName("Behandle søknadsfrist og sent tilbake")
    @Description("Behandle søknadsfrist og sent tilbake på grunn av søknadsfrist. Manglende fødsel.")
    void behandleSøknadsfristOgSentTilbakePåGrunnAvSøknadsfrist() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusYears(4),
                                        arbeidsavtale(LocalDate.now().minusYears(4), LocalDate.now().minusDays(60)).build(),
                                        arbeidsavtale(LocalDate.now().minusDays(59)).stillingsprosent(50).build()
                                )
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var fødselsdato = LocalDate.now().minusMonths(7);
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderManglendeFodselBekreftelse())
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(7));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        var vurderSoknadsfristBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderSoknadsfristBekreftelse())
                .bekreftVilkårErOk();
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);

        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse())
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
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusYears(4),
                                        arbeidsavtale(LocalDate.now().minusYears(4), LocalDate.now().minusDays(60)).build(),
                                        arbeidsavtale(LocalDate.now().minusDays(59)).stillingsprosent(50).build()
                                )
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = LocalDate.now().minusMonths(7);
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderManglendeFodselBekreftelse())
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(7));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new VurderSoknadsfristBekreftelse());
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var vedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse())
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
