package no.nav.foreldrepenger.autotest.fptilbake.engangsstonad;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType.TILBAKEKREVING;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadAdopsjon;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadFødsel;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.MedlemskapDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PersonstatusDto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FptilbakeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.MedlemskapManuellVurderingType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderEktefellesBarnBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerBosattBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerHarGyldigPeriodeBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAdopsjonsdokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaVergeBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApFaktaFeilutbetaling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVerge;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVilkårsvurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.FattVedtakTilbakekreving;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;

@Tag("tilbakekreving")
@Tag("fptilbake")
class TilbakekrevingES extends FptilbakeTestBase {

    private static final String ytelseType = "ES";

    @Test
    @DisplayName("1. Førstegangssøknad innvilges. Revurdering opprettes som fører til avslag. Saksbehandler oppretter tilbakekreving.")
    @Description("Vanligste scenario, enkel periode, treffer ikke foreldelse, full tilbakekreving.")
    void opprettTilbakekrevingManuelt() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusYears(4), 1_120_000,
                                        ArbeidsavtaleDto.arbeidsavtale(LocalDate.now().minusYears(4), LocalDate.now().minusDays(60)).build(),
                                        ArbeidsavtaleDto.arbeidsavtale(LocalDate.now().minusDays(59)).stillingsprosent(50).build()
                                )
                        .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();
        var mor = familie.mor();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1).plusWeeks(1);
        var søknad = lagEngangstønadAdopsjon(omsorgsovertakelsedato, false)
                .medMottattdato(LocalDate.now().minusMonths(1));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBarnetsAnkomstTilNorgeDato(omsorgsovertakelsedato);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelse);
        var vurderEktefellesBarnBekreftelse = saksbehandler
                   .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class)
                .bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunkt(vurderEktefellesBarnBekreftelse);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        var bekreftelse = beslutter
                .hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

//        saksbehandler.opprettBehandlingRevurdering(RE_FEIL_ELLER_ENDRET_FAKTA);
//        saksbehandler.ventPåOgVelgRevurderingBehandling(RE_FEIL_ELLER_ENDRET_FAKTA);
//
//        var varselOmRevurderingBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VarselOmRevurderingBekreftelse.class);
//        varselOmRevurderingBekreftelse.bekreftIkkeSendVarsel();
//        saksbehandler.bekreftAksjonspunkt(varselOmRevurderingBekreftelse);
//        var avklarFaktaAdopsjonsdokumentasjonBekreftelseRevurdering = saksbehandler
//                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
//                .setBarnetsAnkomstTilNorgeDato(omsorgsovertakelsedato);
//        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseRevurdering);
//        var vurderEktefellesBarnBekreftelseRevurdering = saksbehandler
//                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class)
//                .bekreftBarnErEktefellesBarn();
//        saksbehandler.bekreftAksjonspunkt(vurderEktefellesBarnBekreftelseRevurdering);
//
//        var vurderTilbakekrevingVedNegativSimulering = saksbehandler
//                .hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
//        vurderTilbakekrevingVedNegativSimulering.setTilbakekrevingUtenVarsel();
//        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
//        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true);
        //Her mangler behandling av Engangsstønad revurderingen!!


        tbksaksbehandler.opprettTilbakekreving(saksnummer, saksbehandler.valgtBehandling.uuid, ytelseType);
        tbksaksbehandler.hentSisteBehandling(saksnummer, TILBAKEKREVING);
        tbksaksbehandler.ventTilBehandlingErPåVent();
        assertThat(tbksaksbehandler.valgtBehandling.venteArsakKode)
                .as("Venteårsak")
                .isEqualTo("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG");
        var kravgrunnlag = new Kravgrunnlag(saksnummer, mor.fødselsnummer().value(),
                saksbehandler.valgtBehandling.id, ytelseType, "NY");
        kravgrunnlag.leggTilGeneriskPeriode(ytelseType);
        tbksaksbehandler.sendNyttKravgrunnlag(kravgrunnlag, saksnummer, saksbehandler.valgtBehandling.id);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(7003);

        var vurderFakta = (ApFaktaFeilutbetaling) tbksaksbehandler.hentAksjonspunktbehandling(7003);
        vurderFakta.addGeneriskVurdering(ytelseType);
        tbksaksbehandler.behandleAksjonspunkt(vurderFakta);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5002);

        var vurderVilkår = (ApVilkårsvurdering) tbksaksbehandler.hentAksjonspunktbehandling(5002);
        vurderVilkår.addGeneriskVurdering();
        tbksaksbehandler.behandleAksjonspunkt(vurderVilkår);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5004);

        tbksaksbehandler.behandleAksjonspunkt(tbksaksbehandler.hentAksjonspunktbehandling(5004));
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5005);

        tbkbeslutter.hentSisteBehandling(saksnummer);
        tbkbeslutter.ventTilBehandlingHarAktivtAksjonspunkt(5005);

        var fattVedtak = (FattVedtakTilbakekreving) tbkbeslutter.hentAksjonspunktbehandling(5005);
        fattVedtak.godkjennAksjonspunkt(5002);
        fattVedtak.godkjennAksjonspunkt(7003);
        fattVedtak.godkjennAksjonspunkt(5004);
        tbkbeslutter.behandleAksjonspunkt(fattVedtak);
        tbkbeslutter.ventTilAvsluttetBehandling();

        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløp())
                .as("Tilbakekrevingsbeløp")
                .isEqualTo(83_140);
    }

    @Test
    @DisplayName("2. Oppretter en tilbakekreving manuelt etter Fpsak-førstegangsbehandling med verge")
    @Description("FPsak med søker under 18, kopierer verge fra FPSAK, fjerner i FPTILBAKE og legger til ny.")
    void tilbakeKrevingMedVerge() {
        var familie = FamilieGenerator.ny()
                .forelder(mor(LocalDate.now().minusYears(17))
                        .personstatus(List.of(new PersonstatusDto(PersonstatusDto.Personstatuser.UTVA, LocalDate.now().minusYears(30), null)))
                        .medlemskap(List.of(new MedlemskapDto(LocalDate.now().minusYears(1), LocalDate.now().plusYears(3), CountryCode.DE, MedlemskapDto.DekningsType.IHT_AVTALE)))
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arena(ArenaSakerDto.YtelseTema.AAP, LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2), 100_00)
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4))
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-002", LocalDate.now().minusMonths(4))
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        var avklarFaktaVergeBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaVergeBekreftelse.class)
                .bekreftSøkerErKontaktperson()
                .bekreftSøkerErIkkeUnderTvungenForvaltning()
                .setVerge(familie.far().fødselsnummer());
        saksbehandler.bekreftAksjonspunkt(avklarFaktaVergeBekreftelse);

        var avklarBrukerHarGyldigPeriodeBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class)
                .setVurdering(MedlemskapManuellVurderingType.MEDLEM,
                        saksbehandler.valgtBehandling.getMedlem().getMedlemskapPerioder());
        saksbehandler.bekreftAksjonspunkt(avklarBrukerHarGyldigPeriodeBekreftelse);
        var bosatt = saksbehandler.hentAksjonspunktbekreftelse(AvklarBrukerBosattBekreftelse.class);
        bosatt.getBekreftedePerioder().forEach(p -> p.setBosattVurdering(true));
        saksbehandler.bekreftAksjonspunkt(bosatt);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        tbksaksbehandler.opprettTilbakekreving(saksnummer, saksbehandler.valgtBehandling.uuid, ytelseType);
        tbksaksbehandler.hentSisteBehandling(saksnummer);
        tbksaksbehandler.ventTilBehandlingErPåVent();
        assertThat(tbksaksbehandler.valgtBehandling.venteArsakKode)
                .as("Venteårsak")
                .isEqualTo("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG");
        assertThat(tbksaksbehandler.valgtBehandling.harVerge)
                .as("Behandling har verge")
                .isTrue();
        var kravgrunnlag = new Kravgrunnlag(saksnummer, mor.fødselsnummer().value(),
                saksbehandler.valgtBehandling.id, ytelseType, "NY");
        kravgrunnlag.leggTilGeneriskPeriode(ytelseType);
        tbksaksbehandler.sendNyttKravgrunnlag(kravgrunnlag, saksnummer, saksbehandler.valgtBehandling.id);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(7003);

        // Sjekk om menyvalget om å fjerne verge fungere
        tbksaksbehandler.fjernVerge();
        assertThat(tbksaksbehandler.valgtBehandling.harVerge)
                .as("Behandling har verge")
                .isFalse();
        tbksaksbehandler.leggTilVerge();

        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5030);
        var vergeFakta = (ApVerge) tbksaksbehandler.hentAksjonspunktbehandling(5030);
        var vergeFamilie = FamilieGenerator.ny()
                .forelder(mor().build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();
        vergeFakta.setVerge(vergeFamilie);
        tbksaksbehandler.behandleAksjonspunkt(vergeFakta);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(7003);
        assertThat(tbksaksbehandler.valgtBehandling.harVerge)
                .as("Behandling har verge")
                .isTrue();

        var vurderFakta = (ApFaktaFeilutbetaling) tbksaksbehandler.hentAksjonspunktbehandling(7003);
        vurderFakta.addGeneriskVurdering(ytelseType);
        tbksaksbehandler.behandleAksjonspunkt(vurderFakta);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5002);

        var vurderVilkår = (ApVilkårsvurdering) tbksaksbehandler.hentAksjonspunktbehandling(5002);
        vurderVilkår.addGeneriskVurdering();
        tbksaksbehandler.behandleAksjonspunkt(vurderVilkår);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5004);

        tbksaksbehandler.behandleAksjonspunkt(tbksaksbehandler.hentAksjonspunktbehandling(5004));
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5005);

        tbkbeslutter.hentSisteBehandling(saksnummer);
        tbkbeslutter.ventTilBehandlingHarAktivtAksjonspunkt(5005);

        var fattVedtak = (FattVedtakTilbakekreving) tbkbeslutter.hentAksjonspunktbehandling(5005);
        fattVedtak.godkjennAksjonspunkt(5002);
        fattVedtak.godkjennAksjonspunkt(7003);
        fattVedtak.godkjennAksjonspunkt(5004);
        tbkbeslutter.behandleAksjonspunkt(fattVedtak);
        tbkbeslutter.ventTilAvsluttetBehandling();
    }
}
