package no.nav.foreldrepenger.autotest.foreldrepengerUtvidet.engangsstonad;

import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadAdopsjon;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

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
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;

@Tag("fpsak")
@Tag("engangsstonad")
class Adopsjon extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker adopsjon - godkjent")
    @Description("Mor søker adopsjon - godkjent happy case")
    void morSøkerAdopsjonGodkjent() {
        var mor = new Familie("55").mor();
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.MOR, LocalDate.now().plusMonths(1), false);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        var vurderEktefellesBarnBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class)
                .bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunktbekreftelserer(avklarFaktaAdopsjonsdokumentasjonBekreftelse, vurderEktefellesBarnBekreftelse);

        foreslåOgFatterVedtak(saksnummer);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker adopsjon - avvist - barn er over 15 år")
    @Description("Mor søker adopsjon - avvist - barn er over 15 år og blir dermed avlått")
    void morSøkerAdopsjonAvvist() {
        var mor = new Familie("55").mor();
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.MOR, LocalDate.now().plusMonths(1), false);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBarnetsAnkomstTilNorgeDato(LocalDate.now())
                .endreFødselsdato(1, LocalDate.now().minusYears(16));
        var vurderEktefellesBarnBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class)
                .bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunktbekreftelserer(avklarFaktaAdopsjonsdokumentasjonBekreftelse, vurderEktefellesBarnBekreftelse);

        foreslåOgFatterVedtak(saksnummer);

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
        var mor = new Familie("55").mor();
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.MOR, LocalDate.now().plusMonths(1), false);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        var vurderEktefellesBarnBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class)
                .bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunktbekreftelserer(avklarFaktaAdopsjonsdokumentasjonBekreftelse, vurderEktefellesBarnBekreftelse);

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
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
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
        var far = new Familie("61").far();
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.FAR, LocalDate.now().plusMonths(1), false);
        var saksnummer = far.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        var vurderEktefellesBarnBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class)
                .bekreftBarnErIkkeEktefellesBarn();
        var mannAdoptererAleneBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(MannAdoptererAleneBekreftelse.class)
                .bekreftMannAdoptererAlene();
        saksbehandler.bekreftAksjonspunktbekreftelserer(avklarFaktaAdopsjonsdokumentasjonBekreftelse,
                vurderEktefellesBarnBekreftelse, mannAdoptererAleneBekreftelse);

        foreslåOgFatterVedtak(saksnummer);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Far søker adopsjon av ektefelles barn")
    @Description("Far søker adopsjon av ektefelles barn fører til avvist behandling")
    void farSøkerAdopsjonAvvist() {
        var far = new Familie("61").far();
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.FAR, LocalDate.now().plusMonths(1), false);
        var saksnummer = far.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var bekreftelse1 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        var bekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class)
                .bekreftBarnErEktefellesBarn();
        var bekreftelse3 = saksbehandler
                .hentAksjonspunktbekreftelse(MannAdoptererAleneBekreftelse.class)
                .bekreftMannAdoptererIkkeAlene();
        saksbehandler.bekreftAksjonspunktbekreftelserer(bekreftelse1, bekreftelse2, bekreftelse3);

        assertThat(saksbehandler.vilkårStatus("FP_VK_4").kode)
                .as("Vilkårstatus for adopsjon")
                .isEqualTo("IKKE_OPPFYLT");
        assertThat(saksbehandler.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak")
                .isEqualTo(Avslagsårsak.EKTEFELLES_SAMBOERS_BARN);

        foreslåOgFatterVedtak(saksnummer);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
    }
}
