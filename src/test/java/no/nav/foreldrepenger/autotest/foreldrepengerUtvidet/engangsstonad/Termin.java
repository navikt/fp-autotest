package no.nav.foreldrepenger.autotest.foreldrepengerUtvidet.engangsstonad;

import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadTermin;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Venteårsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrFodselsvilkaaret;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;

@Tag("fpsak")
@Tag("engangsstonad")
class Termin extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker termin - godkjent")
    @Description("Mor søker termin - godkjent happy case")
    void morSøkerTerminGodkjent() {
        var familie = new Familie("55");
        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = lagEngangstønadTermin(BrukerRolle.MOR, termindato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class)
                .setAntallBarn(1)
                .setUtstedtdato(LocalDate.now().minusMonths(1))
                .setTermindato(LocalDate.now().plusMonths(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        foreslåOgFatterVedtak(saksnummer);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

    }

    @Test
    @DisplayName("Mor søker termin overstyrt vilkår")
    @Description("Mor søker termin overstyrt vilkår fødsel fra oppfylt til avvist")
    void morSøkerTerminOvertyrt() {
        var familie = new Familie("55");
        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = lagEngangstønadTermin(BrukerRolle.MOR, termindato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class)
                .setAntallBarn(1)
                .setUtstedtdato(LocalDate.now().minusMonths(1))
                .setTermindato(LocalDate.now().plusMonths(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        overstyrer.hentFagsak(saksnummer);

        var overstyr = new OverstyrFodselsvilkaaret()
                .avvis(Avslagsårsak.SØKER_ER_FAR)
                .setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        assertThat(overstyrer.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        fatterVedtak(saksnummer);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
    }


    @Test
    @DisplayName("Far søker termin")
    @Description("Far søker termin avslått pga søker er far")
    void farSøkerTermin() {
        var familie = new Familie("55");
        var far = familie.far();
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = lagEngangstønadTermin(BrukerRolle.FAR, termindato);
        var saksnummer = far.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class)
                .setAntallBarn(1)
                .setUtstedtdato(LocalDate.now().minusMonths(1))
                .setTermindato(LocalDate.now().plusMonths(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
    }

    @Test
    @DisplayName("Setter behandling på vent og gjennoptar og henlegger")
    @Description("Setter behandling på vent og gjennoptar og henlegger")
    void settBehandlingPåVentOgGjenopptaOgHenlegg() {
        var familie = new Familie("55");
        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = lagEngangstønadTermin(BrukerRolle.MOR, termindato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.settBehandlingPåVent(LocalDate.now(), Venteårsak.AVV_DOK);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandlingen satt på vent")
                .isTrue();

        saksbehandler.gjenopptaBehandling();
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandlingen satt på vent")
                .isFalse();

        saksbehandler.henleggBehandling(BehandlingResultatType.HENLAGT_SØKNAD_TRUKKET);
        assertThat(saksbehandler.valgtBehandling.erHenlagt())
                .as("Behandlingen ble uventet ikke henlagt")
                .isTrue();
        assertThat(saksbehandler.getBehandlingsstatus())
                .as("Behandlingsstatus")
                .isEqualTo(BehandlingStatus.AVSLUTTET);
    }

    @Test
    @DisplayName("Mor søker termin 25 dager etter fødsel")
    @Description("Mor søker termin 25 dager etter fødsel - Får aksjonpunkt om manglende fødsel - godkjent")
    void morSøkerTermin25DagerTilbakeITid() {
        var familie = new Familie("55");
        var mor = familie.mor();
        var termindato = LocalDate.now().minusDays(26);
        var søknad = lagEngangstønadTermin(BrukerRolle.MOR, termindato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(1));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        foreslåOgFatterVedtak(saksnummer);

        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

}
