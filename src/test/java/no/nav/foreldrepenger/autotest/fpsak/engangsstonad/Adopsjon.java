package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadAdopsjon;
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
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.common.domain.BrukerRolle;

@Tag("fpsak")
@Tag("engangsstonad")
class Adopsjon extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker adopsjon - godkjent")
    @Description("Mor søker adopsjon - godkjent happy case")
    void morSøkerAdopsjonGodkjent() {
        var familie = new Familie("55", fordel);
        var mor = familie.mor();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1);
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.MOR, omsorgsovertakelsedato, false);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaAdopsjonsdokumentasjonBekreftelse bekreftelse1 = saksbehandler.hentAksjonspunktbekreftelse(
                AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        bekreftelse1.setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        VurderEktefellesBarnBekreftelse bekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class);
        bekreftelse2.bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunktbekreftelserer(bekreftelse1, bekreftelse2);

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
        var familie = new Familie("55", fordel);
        var mor = familie.mor();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1);
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.MOR, omsorgsovertakelsedato, false);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaAdopsjonsdokumentasjonBekreftelse bekreftelse1 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        bekreftelse1.setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        bekreftelse1.endreFødselsdato(1, LocalDate.now().minusYears(16));
        VurderEktefellesBarnBekreftelse bekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class);
        bekreftelse2.bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunktbekreftelserer(bekreftelse1, bekreftelse2);

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
        var familie = new Familie("55", fordel);
        var mor = familie.mor();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1);
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.MOR, omsorgsovertakelsedato, false);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaAdopsjonsdokumentasjonBekreftelse bekreftelse1 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        bekreftelse1.setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        VurderEktefellesBarnBekreftelse bekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class);
        bekreftelse2.bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunktbekreftelserer(bekreftelse1, bekreftelse2);

        overstyrer.hentFagsak(saksnummer);

        OverstyrAdopsjonsvilkaaret overstyr = new OverstyrAdopsjonsvilkaaret();
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
        var familie = new Familie("61", fordel);
        var far = familie.far();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1);
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.FAR, omsorgsovertakelsedato, false);
        var saksnummer = far.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaAdopsjonsdokumentasjonBekreftelse bekreftelse1 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        bekreftelse1.setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        VurderEktefellesBarnBekreftelse bekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class);
        bekreftelse2.bekreftBarnErIkkeEktefellesBarn();
        MannAdoptererAleneBekreftelse bekreftelse3 = saksbehandler
                .hentAksjonspunktbekreftelse(MannAdoptererAleneBekreftelse.class);
        bekreftelse3.bekreftMannAdoptererAlene();
        saksbehandler.bekreftAksjonspunktbekreftelserer(bekreftelse1, bekreftelse2, bekreftelse3);

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
        var familie = new Familie("61", fordel);
        var far = familie.far();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1);
        var søknad = lagEngangstønadAdopsjon(BrukerRolle.FAR, omsorgsovertakelsedato, false);
        var saksnummer = far.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaAdopsjonsdokumentasjonBekreftelse bekreftelse1 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        bekreftelse1.setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        VurderEktefellesBarnBekreftelse bekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class);
        bekreftelse2.bekreftBarnErEktefellesBarn();
        MannAdoptererAleneBekreftelse bekreftelse3 = saksbehandler
                .hentAksjonspunktbekreftelse(MannAdoptererAleneBekreftelse.class);
        bekreftelse3.bekreftMannAdoptererIkkeAlene();
        saksbehandler.bekreftAksjonspunktbekreftelserer(bekreftelse1, bekreftelse2, bekreftelse3);

        assertThat(saksbehandler.vilkårStatus("FP_VK_4"))
                .as("Vilkårstatus for adopsjon")
                .isEqualTo("IKKE_OPPFYLT");
        assertThat(saksbehandler.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak")
                .isEqualTo(Avslagsårsak.EKTEFELLES_SAMBOERS_BARN);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(
                beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_OM_ADOPSJON_GJELDER_EKTEFELLES_BARN));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
    }
}
