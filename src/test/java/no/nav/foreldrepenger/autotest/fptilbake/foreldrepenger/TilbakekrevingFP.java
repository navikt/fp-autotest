package no.nav.foreldrepenger.autotest.fptilbake.foreldrepenger;

import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.erketyper.FordelingErketyper.fordeling;
import static no.nav.foreldrepenger.generator.soknad.erketyper.SøknadEndringErketyper.lagEndringssøknadFødsel;
import static no.nav.foreldrepenger.generator.soknad.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.erketyper.UttaksperioderErketyper.uttaksperiode;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;

import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;

import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FptilbakeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerRevuderingsbehandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedNegativSimulering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApFaktaFeilutbetaling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVerge;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVilkårsvurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.FattVedtakTilbakekreving;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.generator.familie.Familie;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;

@Tag("tilbakekreving")
@Tag("fptilbake")
class TilbakekrevingFP extends FptilbakeTestBase {

    private static final String ytelseType = "FP";

    @Test
    @DisplayName("1. Oppretter en tilbakekreving manuelt etter Fpsak-førstegangsbehandling og revurdering")
    @Description("Enkel periode, treffer ikke foreldelse, full tilbakekreving.")
    void opprettTilbakekrevingManuelt() {
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
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        lagOgSendInntektsmelding(familie, fpStartdato, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        AllureHelper.debugFritekst("Ferdig med førstegangsbehandling");

        var fordeling = fordeling(
                uttaksperiode(StønadskontoType.FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(10).minusDays(1)));
        var søknadE = lagEndringssøknadFødsel(fødselsdato, BrukerRolle.MOR, fordeling.build(), saksnummer);
        mor.søk(søknadE.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        tbksaksbehandler.opprettTilbakekreving(saksnummer, saksbehandler.valgtBehandling.uuid, ytelseType);
        tbksaksbehandler.hentSisteBehandling(saksnummer);
        tbksaksbehandler.ventTilBehandlingErPåVent();
        assertThat(tbksaksbehandler.valgtBehandling.venteArsakKode)
                .as("Venteårsak")
                .isEqualTo("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG");
        var kravgrunnlag = new Kravgrunnlag(saksnummer, mor.fødselsnummer().value(),
                saksbehandler.valgtBehandling.id, ytelseType, "NY");
        kravgrunnlag.leggTilGeneriskPeriode();
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
    }

    @Test
    @DisplayName("2. Oppretter en tilbakekreving automatisk etter negativ simulering på fpsak revurdering")
    @Description("Vanligste scenario, enkel periode, treffer ikke foreldelse, full tilbakekreving men med registrert advokat som verge/fullmektig")
    void opprettTilbakekrevingAutomatisk() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(12))
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()))
                .medMottattDato(fpStartdato);
        var saksnummer = mor.søk(søknad.build());

        lagOgSendInntektsmelding(familie, fpStartdato, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        AllureHelper.debugFritekst("Ferdig med førstegangsbehandling");

        lagOgSendInntektsmelding(familie, fpStartdato, saksnummer, true);

        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(mor.månedsinntekt() * 6, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        var vurderTilbakekrevingVedNegativSimulering = saksbehandler
                .hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
        vurderTilbakekrevingVedNegativSimulering.setTilbakekrevingMedVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        var kontrollerRevuderingsbehandling = saksbehandler.hentAksjonspunktbekreftelse(KontrollerRevuderingsbehandling.class);
        saksbehandler.bekreftAksjonspunkt(kontrollerRevuderingsbehandling);
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true, true);

        tbksaksbehandler.hentSisteBehandling(saksnummer);
        tbksaksbehandler.ventTilBehandlingErPåVent();
        assertThat(tbksaksbehandler.valgtBehandling.venteArsakKode)
                .as("Venteårsak")
                .isEqualTo("VENT_PÅ_BRUKERTILBAKEMELDING");

        var kravgrunnlag = new Kravgrunnlag(saksnummer, mor.fødselsnummer().value(),
                saksbehandler.valgtBehandling.id, ytelseType, "NY");
        kravgrunnlag.leggTilGeneriskPeriode();
        tbksaksbehandler.sendNyttKravgrunnlag(kravgrunnlag, saksnummer, saksbehandler.valgtBehandling.id);

        tbksaksbehandler.registrerBrukerrespons(true);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(7003);

        tbksaksbehandler.leggTilVerge();
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5030);
        var vergeFakta = (ApVerge) tbksaksbehandler.hentAksjonspunktbehandling(5030);
        vergeFakta.setVerge(familie.far().arbeidsgiver().arbeidsgiverIdentifikator().value());
        tbksaksbehandler.behandleAksjonspunkt(vergeFakta);
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

        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getRenteBeløp())
                .as("Rente i beregningsresultat")
                .isZero();
        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getSkattBeløp())
                .as("Skattebeløp i beregningsresultat")
                .isEqualTo(412);
        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløp())
                .as("Tilbakekrevingsbeløp i beregningsresultat")
                .isEqualTo(1616);
        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløpEtterSkatt())
                .as("Tilbakekrevingsbeløp etter skatt i beregningsresultat")
                .isEqualTo(1204);
        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløpUtenRenter())
                .as("Tilbakekrevingbeløp uten renter i beregningsresultat")
                .isEqualTo(1616);
    }

    @Test
    @DisplayName("3. Oppretter og behandler en tilbakekreving helt-automatisk")
    @Description("Heltautomatisert scenario. Beløp under et halvt rettsgebyr og blir plukket av auto-batch. Batchen kjører ikke i helgene, derfor skip hvis helg.")
    void opprettOgBehandleTilbakekrevingAutomatisk() {
        Assumptions.assumeTrue(!isWeekend(LocalDate.now()), "Batche kjører ikke i helgen.");
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(12))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()))
                .medMottattDato(fpStartdato);
        var saksnummer = mor.søk(søknad.build());

        lagOgSendInntektsmelding(familie, fpStartdato, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        AllureHelper.debugFritekst("Ferdig med førstegangsbehandling");

        lagOgSendInntektsmelding(familie, fpStartdato, saksnummer, true);

        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(mor.månedsinntekt() * 6, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
        vurderTilbakekrevingVedNegativSimulering.setTilbakekrevingUtenVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        var kontrollerRevuderingsbehandling = saksbehandler.hentAksjonspunktbekreftelse(KontrollerRevuderingsbehandling.class);
        saksbehandler.bekreftAksjonspunkt(kontrollerRevuderingsbehandling);
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true, true);

        tbksaksbehandler.hentSisteBehandling(saksnummer, BehandlingType.TILBAKEKREVING);
        tbksaksbehandler.ventTilBehandlingErPåVent();
        assertThat(tbksaksbehandler.valgtBehandling.venteArsakKode)
                .as("Venteårsak")
                .isEqualTo("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG");

        var kravgrunnlag = new Kravgrunnlag(saksnummer, mor.fødselsnummer().value(),
                saksbehandler.valgtBehandling.id, ytelseType, "NY");
        kravgrunnlag.leggTilPeriodeMedSmåBeløp();
        tbksaksbehandler.sendNyttKravgrunnlag(kravgrunnlag, saksnummer, saksbehandler.valgtBehandling.id);

        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(7003);
        tbksaksbehandler.startAutomatiskBehandlingBatchOgVentTilAutoPunktErKjørt(7003); // TODO: Litt hacky.
        tbksaksbehandler.ventTilAvsluttetBehandling();
        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløp())
                .as("Tilbakekrevingsbeløp")
                .isZero();
    }

    private void lagOgSendInntektsmelding(Familie familie, LocalDate fpStartdato, Saksnummer saksnummer) {
        lagOgSendInntektsmelding(familie, fpStartdato, saksnummer, false);
    }
    private void lagOgSendInntektsmelding(Familie familie, LocalDate fpStartdato, Saksnummer saksnummer, Boolean redusert) {
        var arbeidsgiver = familie.mor().arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato);
        if (redusert){
            inntektsmelding.medBeregnetInntekt(ProsentAndel.valueOf(50));
        }
        arbeidsgiver.sendInntektsmeldinger(saksnummer, inntektsmelding);
    }

    public static boolean isWeekend(final LocalDate ld)
    {
        var day = DayOfWeek.of(ld.get(ChronoField.DAY_OF_WEEK));
        return day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY;
    }
}
