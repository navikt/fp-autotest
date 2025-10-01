package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadAdopsjon;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderOmsorgsovertakelseVilkårAksjonspunktDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.VilkarTypeKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.AdopsjonDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.EngangsstønadDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.SøknadDto;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("fpsak")
@Tag("engangsstonad")
class Adopsjon extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker adopsjon - godkjent")
    @Description("Mor søker adopsjon - godkjent happy case")
    void morSøkerAdopsjonGodkjent() {
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
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1);
        var søknad = lagEngangstønadAdopsjon(omsorgsovertakelsedato, false);
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        var vurderOmsorgsovertakelse = saksbehandler.hentAksjonspunktbekreftelse(new VurderOmsorgsovertakelseVilkårAksjonspunktDto())
                .oppfylt(OmsorgsovertakelseVilkårType.ES_ADOPSJONSVILKÅRET, omsorgsovertakelsedato, false,
                        lagFødselsdatoer(søknad.build()));
        saksbehandler.bekreftAksjonspunkt(vurderOmsorgsovertakelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());
        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker adopsjon - avvist - barn er over 15 år")
    @Description("Mor søker adopsjon - avvist - barn er over 15 år og blir dermed avlått")
    void morSøkerAdopsjonAvvist() {
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
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1);
        var søknad = lagEngangstønadAdopsjon(omsorgsovertakelsedato, false);
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        var vurderOmsorgsovertakelse = saksbehandler.hentAksjonspunktbekreftelse(new VurderOmsorgsovertakelseVilkårAksjonspunktDto())
                .ikkeOppfylt(Avslagsårsak.BARN_OVER_15_ÅR, OmsorgsovertakelseVilkårType.ES_ADOPSJONSVILKÅRET, omsorgsovertakelsedato,
                        false, LocalDate.now().minusYears(16));
        saksbehandler.bekreftAksjonspunkt(vurderOmsorgsovertakelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());
        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        assertThat(beslutter.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak")
                .isEqualTo(Avslagsårsak.BARN_OVER_15_ÅR);
    }

    @Test
    @DisplayName("Mor søker adopsjon med overstyrt vilkår")
    @Description("Mor søker adopsjon med overstyrt vilkår som tar behandlingen fra innvilget til avslått")
    void morSøkerAdopsjonOverstyrt() {
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
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1);
        var søknad = lagEngangstønadAdopsjon(omsorgsovertakelsedato, false);
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        var vurderOmsorgsovertakelse = saksbehandler.hentAksjonspunktbekreftelse(new VurderOmsorgsovertakelseVilkårAksjonspunktDto())
                .oppfylt(OmsorgsovertakelseVilkårType.ES_ADOPSJONSVILKÅRET, omsorgsovertakelsedato, false,
                        lagFødselsdatoer(søknad.build()));
        saksbehandler.bekreftAksjonspunkt(vurderOmsorgsovertakelse);

        overstyrer.hentFagsak(saksnummer);
        var revurderOmsorgsovertakelse = overstyrer.hentAksjonspunktbekreftelse(new VurderOmsorgsovertakelseVilkårAksjonspunktDto())
                .ikkeOppfylt(Avslagsårsak.BARN_OVER_15_ÅR, OmsorgsovertakelseVilkårType.ES_ADOPSJONSVILKÅRET, omsorgsovertakelsedato,
                        false, LocalDate.now().minusYears(16));
        overstyrer.bekreftAksjonspunkt(revurderOmsorgsovertakelse);

        assertThat(overstyrer.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        overstyrer.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        assertThat(beslutter.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak")
                .isEqualTo(Avslagsårsak.BARN_OVER_15_ÅR);
    }

    @Test
    @DisplayName("Far søker adopsjon - godkjent")
    @Description("Far søker adopsjon - godkjent happy case")
    void farSøkerAdopsjonGodkjent() {
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arena(ArenaSakerDto.YtelseTema.AAP, LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2), 10_000)
                                .build())
                        .build())
                .forelder(mor().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var far = familie.far();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1);
        var søknad = lagEngangstønadAdopsjon(omsorgsovertakelsedato, false);
        var saksnummer = far.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        var vurderOmsorgsovertakelse = saksbehandler.hentAksjonspunktbekreftelse(new VurderOmsorgsovertakelseVilkårAksjonspunktDto())
                .oppfylt(OmsorgsovertakelseVilkårType.ES_ADOPSJONSVILKÅRET, omsorgsovertakelsedato, false,
                        lagFødselsdatoer(søknad.build()));
        saksbehandler.bekreftAksjonspunkt(vurderOmsorgsovertakelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Far søker adopsjon av ektefelles barn")
    @Description("Far søker adopsjon av ektefelles barn fører til avvist behandling")
    void farSøkerAdopsjonAvvist() {
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arena(ArenaSakerDto.YtelseTema.AAP, LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2), 10_000)
                                .build())
                        .build())
                .forelder(mor().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var far = familie.far();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1);
        var søknad = lagEngangstønadAdopsjon(omsorgsovertakelsedato, false);
        var saksnummer = far.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        var vurderOmsorgsovertakelse = saksbehandler.hentAksjonspunktbekreftelse(new VurderOmsorgsovertakelseVilkårAksjonspunktDto())
                .ikkeOppfylt(Avslagsårsak.EKTEFELLES_SAMBOERS_BARN, OmsorgsovertakelseVilkårType.ES_ADOPSJONSVILKÅRET,
                        omsorgsovertakelsedato, true, lagFødselsdatoer(søknad.build()));
        saksbehandler.bekreftAksjonspunkt(vurderOmsorgsovertakelse);

        assertThat(saksbehandler.vilkårStatus(VilkarTypeKoder.OMSORGSOVERTAKELSEVILKÅRET))
                .as("Vilkårstatus for adopsjon")
                .isEqualTo("IKKE_OPPFYLT");
        assertThat(saksbehandler.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak")
                .isEqualTo(Avslagsårsak.EKTEFELLES_SAMBOERS_BARN);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);

        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkt(
                beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_OMSORGSOVERTAKELSEVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
    }

    static LocalDate[] lagFødselsdatoer(SøknadDto engangsstønadDto) {
        var adopsjonDto = ((EngangsstønadDto) engangsstønadDto).barn();
        return ((AdopsjonDto)adopsjonDto).fødselsdatoer().toArray(LocalDate[]::new);
    }
}
