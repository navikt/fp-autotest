package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadAdopsjon;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;

import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;

import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Venteårsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VarselOmRevurderingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderEktefellesBarnBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAdopsjonsdokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.common.domain.BrukerRolle;

@Tag("fpsak")
@Tag("engangsstonad")
class Revurdering extends FpsakTestBase {

    @Test
    @DisplayName("Manuelt opprettet revurdering")
    @Description("Manuelt opprettet revurdering etter avsluttet behandling med utsendt varsel")
    void manueltOpprettetRevurderingSendVarsel() {
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
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1L);
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

        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(
                beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_OM_ADOPSJON_GJELDER_EKTEFELLES_BARN));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_FEIL_ELLER_ENDRET_FAKTA);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.harHistorikkinnslagPåBehandling(HistorikkinnslagType.REVURD_OPPR);

        VarselOmRevurderingBekreftelse varselOmRevurderingBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VarselOmRevurderingBekreftelse.class);
        varselOmRevurderingBekreftelse.bekreftSendVarsel(Venteårsak.UTV_FRIST, "Send brev");
        saksbehandler.bekreftAksjonspunkt(varselOmRevurderingBekreftelse);

        assertThat(saksbehandler.hentHistorikkinnslagPåBehandling())
                .as("Historikkinnslag på revurdering")
                .extracting(HistorikkInnslag::type)
                .contains(  HistorikkinnslagType.BREV_BESTILT,
                            HistorikkinnslagType.BEH_VENT);

        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandlingen er ikke satt på vent etter varsel for revurdering")
                .isTrue();
    }
}
