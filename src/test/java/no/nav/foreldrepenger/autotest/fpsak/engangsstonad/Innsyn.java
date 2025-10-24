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
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.InnsynResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvInnsynBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("fpsak")
@Tag("engangsstonad")
class Innsyn extends VerdikjedeTestBase {

    @Test
    @DisplayName("Behandle innsyn for mor - godkjent")
    @Description("Behandle innsyn for mor - godkjent happy case")
    void behandleInnsynMorGodkjent() {
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
        var søknad = lagEngangstønadFødsel(
                fødselsdato);
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        saksbehandler.oprettBehandlingInnsyn(null);
        saksbehandler.ventPåOgVelgDokumentInnsynBehandling();

        VurderingAvInnsynBekreftelse vurderingAvInnsynBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderingAvInnsynBekreftelse());
        vurderingAvInnsynBekreftelse.setMottattDato(LocalDate.now())
                .setMottattDato(LocalDate.now())
                .setInnsynResultatType(InnsynResultatType.INNVILGET)
                .skalSetteSakPåVent(false)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunkt(vurderingAvInnsynBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNSYN_INNVILGET);
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("Behandle innsyn for mor - avvist")
    @Description("Behandle innsyn for mor - avvist ved vurdering")
    void behandleInnsynMorAvvist() {
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
        var søknad = lagEngangstønadFødsel(
                fødselsdato);
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        saksbehandler.oprettBehandlingInnsyn(null);
        saksbehandler.ventPåOgVelgDokumentInnsynBehandling();

        VurderingAvInnsynBekreftelse vurderingAvInnsynBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderingAvInnsynBekreftelse());
        vurderingAvInnsynBekreftelse.setMottattDato(LocalDate.now())
                .setInnsynResultatType(InnsynResultatType.AVVIST)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunkt(vurderingAvInnsynBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNSYN_AVVIST);
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
    }
}
