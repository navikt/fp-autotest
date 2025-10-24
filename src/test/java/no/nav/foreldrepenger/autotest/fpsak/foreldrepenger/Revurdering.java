package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugFritekst;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEndringMaler.lagEndringssøknad;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordelingEndringssøknadGradering;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.KonsekvensForYtelsen;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedNegativSimulering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrMedlemskapsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.FagsakStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.kontrakter.fpsoknad.BrukerRolle;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("fpsak")
@Tag("foreldrepenger")
class Revurdering extends VerdikjedeTestBase {

    @Test
    @DisplayName("Revurdering opprettet manuelt av saksbehandler.")
    @Description("Førstegangsbehandling til positivt vedtak. Saksbehandler oppretter revurdering manuelt. " +
            "Overstyrer medlemskap. Vedtaket opphører.")
    void opprettRevurderingManuelt() {
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
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_MEDLEMSKAP);

        overstyrer.hentFagsak(saksnummer);
        overstyrer.ventPåOgVelgRevurderingBehandling();
        var overstyrMedlemskapsvilkaaret = new OverstyrMedlemskapsvilkaaret()
                .avvis(Avslagsårsak.SØKER_ER_IKKE_MEDLEM)
                .setBegrunnelse("avvist");
        overstyrer.overstyr(overstyrMedlemskapsvilkaaret);

        var vurderTilbakekrevingVedNegativSimulering = overstyrer.hentAksjonspunktbekreftelse(new VurderTilbakekrevingVedNegativSimulering())
                .tilbakekrevingMedVarsel();
        overstyrer.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        overstyrer.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse())
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(bekreftelse);
        beslutter.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();

        assertThat(beslutter.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.OPPHØR);
        assertThat(beslutter.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak")
                .isEqualTo(Avslagsårsak.SØKER_ER_IKKE_MEDLEM);
        assertThat(beslutter.valgtBehandling.status)
                .as("Behandlingsstatus")
                .isEqualTo(BehandlingStatus.AVSLUTTET);
    }

    @Test
    @DisplayName("Endringssøknad med ekstra uttaksperiode.")
    @Description("Førstegangsbehandling til positivt vedtak. Søker sender inn endringsøknad. Endring i uttak. Vedtak fortsatt løpende.")
    void endringssøknad() {
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
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.VEDLEGG_MOTTATT);
        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.behandlinger)
                .as("Antall behandlinger")
                .hasSize(1);
        assertThat(saksbehandler.valgtBehandling.type)
                .as("Behandlingstype")
                .isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        debugFritekst("Ferdig med første behandling");

        // Endringssøknad
        var fordeling = fordeling(
                uttaksperiode(KontoType.FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(10).minusDays(1))
        );
        var søknadE = lagEndringssøknad(søknad.build(), saksnummer, fordeling);
        var saksnummerE = mor.søk(søknadE);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.konsekvenserForYtelsen())
                .as("konsekvensForYtelsen")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);

        saksbehandler.hentFagsak(saksnummerE);
        assertThat(saksbehandler.valgtFagsak.status())
                .as("Fagsak status")
                .isEqualTo(FagsakStatus.LØPENDE);
    }

    @Test
    @DisplayName("Endringssøknad med gradering")
    @Description("Førstegangsbehandling til positivt vedtak. Endringssøknad med gradering fra bruker. Vedtak fortsatt løpende.")
    void endringssøknadMedGradering() {
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
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker())
                .as("Antall uttaksperioder")
                .hasSize(4);

        // Endringssøknad
        var graderingFom = fødselsdato.plusWeeks(20);
        var graderingTom = fødselsdato.plusWeeks(23).minusDays(1);
        var arbeidsgiveridentifikator = arbeidsgiver.arbeidsgiverIdentifikator();
        var fordelingGradering = fordelingEndringssøknadGradering(KontoType.FELLESPERIODE, graderingFom, graderingTom,
                arbeidsgiveridentifikator, 40);
        var endretSøknad = lagEndringssøknad(søknad.build(), saksnummer, fordelingGradering);
        var saksnummerE = mor.søk(endretSøknad);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtFagsak.status())
                .as("Fagsak status")
                .isEqualTo(FagsakStatus.LØPENDE);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.konsekvenserForYtelsen())
                .as("Konsekvenser for ytelse")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker())
                .as("Antall uttaksperioder")
                .hasSize(5);
        for (UttakResultatPeriode periode : saksbehandler.valgtBehandling.getUttakResultatPerioder()
                .getPerioderSøker()) {
            assertThat(periode.getAktiviteter())
                    .as("Aktiviteter i uttaksperiode")
                    .hasSize(1);
        }
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker().get(4).getGraderingInnvilget())
                .as("Uttaksperiode 4 er en graderingsperiode")
                .isTrue();
    }

    @Test
    @DisplayName("Ikke få avslåg på innvilget perioder pga søknadsfrist")
    @Description("Ikke få avslåg på innvilget perioder pga søknadsfrist.")
    void ikke_avslag_pa_innvilget_perioder_pga_søknadsfrist_i_revurdering() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(25))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = fordeling(
                uttaksperiode(KontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(13).minusDays(1))
        );
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fødselsdato.plusWeeks(9));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Sender endringssøknad for å gi fagsaken en ny søknad mottatt dato
        var fordelingEndringssøknad = fordeling(
                uttaksperiode(KontoType.FELLESPERIODE, fødselsdato.plusWeeks(13), fødselsdato.plusWeeks(14).minusDays(1))
        );
        var søknadE = lagEndringssøknad(søknad.build(), saksnummer, fordelingEndringssøknad)
                .medMottattdato(fødselsdato.plusWeeks(10));
        mor.søk(søknadE);

        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.velgSisteBehandling();
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentUttaksperioder())
                .as("Forventer at alle uttaksperioder er innvilget")
                .allMatch(p -> p.getPeriodeResultatType().equals(PeriodeResultatType.INNVILGET));
    }

    @Test
    @DisplayName("Fortsatt få avslag på avslåtte perioder pga søknadsfrist i neste revurdering")
    @Description("Fortsatt få avslag på avslåtte perioder pga søknadsfrist i neste revurdering. Bruker papirsøknad for å kunne sette mottatt dato tilbake i tid")
    void fortsatt_tape_avslåtte_perioder_pga_søknadsfrist_i_revurdering() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(25))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = fordeling(
                uttaksperiode(KontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(13).minusDays(1))
        );
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fødselsdato.plusWeeks(18));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);

        var vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderSoknadsfristForeldrepengerBekreftelse())
                .bekreftHarIkkeGyldigGrunn();
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(bekreftelse);

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .hasSizeGreaterThan(1);

        var fordelingEndringssøknad = fordeling(
                uttaksperiode(KontoType.FELLESPERIODE, fødselsdato.plusWeeks(13), fødselsdato.plusWeeks(12).plusWeeks(2))
        );
        var søknadE = lagEndringssøknad(søknad.build(), saksnummer, fordelingEndringssøknad)
                .medMottattdato(fødselsdato.plusWeeks(10));
        mor.søk(søknadE);

        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .hasSizeGreaterThan(1);
    }
}



