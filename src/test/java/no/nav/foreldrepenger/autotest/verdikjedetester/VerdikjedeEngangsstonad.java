package no.nav.foreldrepenger.autotest.verdikjedetester;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarLovligOppholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.util.pdf.Pdf;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.innsyn.BehandlingTilstand;
import no.nav.foreldrepenger.generator.soknad.erketyper.SøknadEngangsstønadErketyper;

@Tag("verdikjede")
class VerdikjedeEngangsstonad extends VerdikjedeTestBase {

    @Test
    @DisplayName("1: Mor er tredjelandsborger og søker engangsstønad")
    @Description("Mor er tredjelandsborger med statsborgerskap i USA og har ikke registrert medlemsskap i norsk folketrygd.")
    void MorTredjelandsborgerSøkerEngangsStønadTest() {
        var familie = new Familie("505");
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = SøknadEngangsstønadErketyper.lagEngangstønadTermin(BrukerRolle.MOR, termindato);
        var saksnummer = familie.mor().søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse.setBegrunnelse("Informasjon er hentet fra søknadden og godkjennes av autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        var avklarLovligOppholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarLovligOppholdBekreftelse.class);
        avklarLovligOppholdBekreftelse.bekreftBrukerHarLovligOpphold();
        saksbehandler.bekreftAksjonspunkt(avklarLovligOppholdBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getBeregningResultatEngangsstonad().getBeregnetTilkjentYtelse())
                .as("Beregnet tilkjent ytelse")
                .isPositive();

        var dokumentId = saksbehandler
                .hentHistorikkinnslagAvType(HistorikkinnslagType.BREV_SENT)
                .dokumentLinks().get(0)
                .dokumentId();
        var pdf = saksbehandler.hentJournalførtDokument(dokumentId, "ARKIV");
        assertThat(Pdf.is_pdf(pdf))
                .as("Sjekker om byte array er av typen PDF")
                .isTrue();
    }

    @Test
    @DisplayName("2: Verifiser innsyn har korrekt data")
    @Description("Verifiserer at innsyn har korrekt data og sammenligner med vedtaket med det saksbehandlerene ser")
    void mor_innsyn_verifsere() {
        var familie = new Familie("505");
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = SøknadEngangsstønadErketyper.lagEngangstønadTermin(BrukerRolle.MOR, termindato);
        var mor = familie.mor();
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);

        var esSak = mor.innsyn().hentEsSakMedÅpenBehandlingTilstand(saksnummer, BehandlingTilstand.UNDER_BEHANDLING);
        assertThat(esSak.saksnummer().value()).isEqualTo(saksnummer.value());
        assertThat(esSak.sakAvsluttet()).isFalse();
        assertThat(esSak.gjelderAdopsjon()).isFalse();
        assertThat(esSak.åpenBehandling().tilstand()).isEqualTo(BehandlingTilstand.UNDER_BEHANDLING);
        assertThat(esSak.familiehendelse().termindato()).isEqualTo(termindato);
        assertThat(esSak.familiehendelse().fødselsdato()).isNull();
        assertThat(esSak.familiehendelse().antallBarn()).isEqualTo(1);
        assertThat(esSak.familiehendelse().omsorgsovertakelse()).isNull();

        avklarFaktaTerminBekreftelse.setBegrunnelse("Informasjon er hentet fra søknadden og godkjennes av autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);
        var avklarLovligOppholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarLovligOppholdBekreftelse.class);
        avklarLovligOppholdBekreftelse.bekreftBrukerHarLovligOpphold();
        saksbehandler.bekreftAksjonspunkt(avklarLovligOppholdBekreftelse);
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);

        var esSakEtterVedtak = mor.innsyn().hentEsSakUtenÅpenBehandling(saksnummer);
        assertThat(esSakEtterVedtak.saksnummer().value()).isEqualTo(saksnummer.value());
        assertThat(esSakEtterVedtak.sakAvsluttet()).isTrue();
        assertThat(esSakEtterVedtak.åpenBehandling()).isNull();
        assertThat(esSakEtterVedtak.gjelderAdopsjon()).isFalse();
        assertThat(esSakEtterVedtak.familiehendelse().termindato()).isEqualTo(termindato);
        assertThat(esSakEtterVedtak.familiehendelse().fødselsdato()).isNull();
        assertThat(esSakEtterVedtak.familiehendelse().antallBarn()).isEqualTo(1);
        assertThat(esSakEtterVedtak.familiehendelse().omsorgsovertakelse()).isNull();

    }
}
