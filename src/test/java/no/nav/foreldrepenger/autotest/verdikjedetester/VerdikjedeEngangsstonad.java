package no.nav.foreldrepenger.autotest.verdikjedetester;

import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.neovisionaries.i18n.CountryCode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderMedlemskapsvilkårForutgåendeBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.DokumentTag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.common.innsyn.BehandlingTilstand;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.StatsborgerskapDto;

@Tag("verdikjede")
class VerdikjedeEngangsstonad extends VerdikjedeTestBase {

    @Test
    @DisplayName("1: Mor er tredjelandsborger og søker engangsstønad")
    @Description("Mor er tredjelandsborger med statsborgerskap i USA og har ikke registrert medlemsskap i norsk folketrygd.")
    void MorTredjelandsborgerSøkerEngangsStønadTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().statsborgerskap(List.of(new StatsborgerskapDto(CountryCode.US)))
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = SøknadEngangsstønadMaler.lagEngangstønadTermin(termindato);
        var saksnummer = familie.mor().søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaTerminBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaTerminBekreftelse());
        avklarFaktaTerminBekreftelse.setBegrunnelse("Informasjon er hentet fra søknadden og godkjennes av autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new VurderMedlemskapsvilkårForutgåendeBekreftelse());

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, false, false);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getBeregningResultatEngangsstonad().getBeregnetTilkjentYtelse()).as(
                "Beregnet tilkjent ytelse").isPositive();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        hentBrevOgSjekkAtInnholdetErRiktig(engangsstønadInnvilgetAssertionsBuilder(), familie.mor().fødselsnummer(), DokumentTag.ETTERLYS_INNTEKTSMELDING);
    }

    @Test
    @DisplayName("2: Verifiser innsyn har korrekt data")
    @Description("Verifiserer at innsyn har korrekt data og sammenligner med vedtaket med det saksbehandlerene ser")
    void mor_innsyn_verifsere() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().statsborgerskap(List.of(new StatsborgerskapDto(CountryCode.US)))
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = SøknadEngangsstønadMaler.lagEngangstønadTermin(termindato);
        var mor = familie.mor();
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaTerminBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaTerminBekreftelse());

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
        saksbehandler.bekreftAksjonspunkt(new VurderMedlemskapsvilkårForutgåendeBekreftelse());
        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, false, false);

        var esSakEtterVedtak = mor.innsyn().hentEsSakUtenÅpenBehandling(saksnummer);
        assertThat(esSakEtterVedtak.saksnummer().value()).isEqualTo(saksnummer.value());
        assertThat(esSakEtterVedtak.sakAvsluttet()).isTrue();
        assertThat(esSakEtterVedtak.åpenBehandling()).isNull();
        assertThat(esSakEtterVedtak.gjelderAdopsjon()).isFalse();
        assertThat(esSakEtterVedtak.familiehendelse().termindato()).isEqualTo(termindato);
        assertThat(esSakEtterVedtak.familiehendelse().fødselsdato()).isNull();
        assertThat(esSakEtterVedtak.familiehendelse().antallBarn()).isEqualTo(1);
        assertThat(esSakEtterVedtak.familiehendelse().omsorgsovertakelse()).isNull();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        hentBrevOgSjekkAtInnholdetErRiktig(engangsstønadInnvilgetAssertionsBuilder(), mor.fødselsnummer(), DokumentTag.ETTERLYS_INNTEKTSMELDING);
    }
}
