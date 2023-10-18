package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadAdopsjon;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.MannAdoptererAleneBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderEktefellesBarnBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAdopsjonsdokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrAdopsjonsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.common.domain.BrukerRolle;

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
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.MOR, omsorgsovertakelsedato, false);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelse);
        var vurderEktefellesBarnBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class)
                .bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunkt(vurderEktefellesBarnBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
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
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.MOR, omsorgsovertakelsedato, false);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBarnetsAnkomstTilNorgeDato(LocalDate.now())
                .endreFødselsdato(1, LocalDate.now().minusYears(16));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelse);
        var vurderEktefellesBarnBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class)
                .bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunkt(vurderEktefellesBarnBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
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
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.MOR, omsorgsovertakelsedato, false);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelse);
        var vurderEktefellesBarnBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class)
                .bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunkt(vurderEktefellesBarnBekreftelse);

        overstyrer.hentFagsak(saksnummer);

        var overstyr = new OverstyrAdopsjonsvilkaaret();
        overstyr.avvis(Avslagsårsak.BARN_OVER_15_ÅR);
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        assertThat(overstyrer.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
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
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.FAR, omsorgsovertakelsedato, false);
        var saksnummer = far.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelse);
        var vurderEktefellesBarnBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class)
                .bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunkt(vurderEktefellesBarnBekreftelse);
        var mannAdoptererAleneBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(MannAdoptererAleneBekreftelse.class)
                .bekreftMannAdoptererAlene();
        saksbehandler.bekreftAksjonspunkt(mannAdoptererAleneBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
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
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.FAR, omsorgsovertakelsedato, false);
        var saksnummer = far.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelse);
        var vurderEktefellesBarnBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class)
                .bekreftBarnErEktefellesBarn();
        saksbehandler.bekreftAksjonspunkt(vurderEktefellesBarnBekreftelse);
        var mannAdoptererAleneBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(MannAdoptererAleneBekreftelse.class)
                .bekreftMannAdoptererIkkeAlene();
        saksbehandler.bekreftAksjonspunkt(mannAdoptererAleneBekreftelse);

        assertThat(saksbehandler.vilkårStatus("FP_VK_4"))
                .as("Vilkårstatus for adopsjon")
                .isEqualTo("IKKE_OPPFYLT");
        assertThat(saksbehandler.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak")
                .isEqualTo(Avslagsårsak.EKTEFELLES_SAMBOERS_BARN);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(
                beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_OM_ADOPSJON_GJELDER_EKTEFELLES_BARN));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
    }
}
