package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.BERØRT_BEHANDLING;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_UTSATT_START;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder.VURDER_FEILUTBETALING_KODE;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugFritekst;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEndringMaler.lagEndringssøknad;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.byttPerspektiv;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.utsettelsesperiode;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil.helgejustertTilFredag;
import static no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil.helgejustertTilMandag;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.MorsAktivitet.ARBEID;
import static no.nav.foreldrepenger.vtp.kontrakter.person.v2.ArbeidsavtaleDto.arbeidsavtale;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedNegativSimulering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.SjekkManglendeFødselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderUttakDokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrFodselsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.FagsakStatus;
import no.nav.foreldrepenger.generator.familie.Familie;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.soknad.kontrakt.builder.UttakPeriodeBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Rolle;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UtsettelseÅrsak;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UttakPeriodeDto;
import no.nav.foreldrepenger.soknad.kontrakt.vedlegg.ÅpenPeriodeDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.FamilierelasjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.PermisjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.Permisjonstype;

@Tag("fpsak")
@Tag("foreldrepenger")
class MorOgFarSammen extends VerdikjedeTestBase {

    @Test
    @DisplayName("Mor og far koblet sak, kant til kant")
    @Description("Mor søker, får aksjonspunkt slik at behandling stopper opp. "
            + "Far sender deretter søknad med uttak ifm fødsel og blir satt på vent pga manglende inntektsmelding."
            + "Behandler ferdig mor sin søknad (positivt vedtak). Behandler deretter far sin søknad (positivt og automatisk vedtak). "
            + "Ingen overlapp, med unntak av de to ukene ifm med fødsel. Verifiserer at sakene er koblet og at det ikke opprettes revurdering berørt sak.")
    void morOgFar_fødsel_ettArbeidsforholdHver_kobletsak_kantTilKant() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .forelder(far().inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(8))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpstartdatoMor = fødselsdato.minusWeeks(3);
        var utsettelseMor = utsettelsesperiode(Rolle.MOR, UtsettelseÅrsak.SØKER_SYKDOM, fødselsdato,
                fødselsdato.plusWeeks(6).minusDays(1));
        var samtidigUttakFar = uttaksperiode(Rolle.FAR_MEDMOR, FEDREKVOTE, fødselsdato, fødselsdato.plusWeeks(2).minusDays(1),
                BigDecimal.valueOf(100));
        var uttakFar = uttaksperiode(Rolle.FAR_MEDMOR, FEDREKVOTE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(8).minusDays(1));
        var uttaksplan = List.of(
                uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fpstartdatoMor, fødselsdato.minusDays(1)),
                new UttakPeriodeDto(samtidigUttakFar.fom(), samtidigUttakFar.tom(), utsettelseMor.søker(), samtidigUttakFar.søker(), null),
                new UttakPeriodeDto(helgejustertTilMandag(fødselsdato.plusWeeks(2)), utsettelseMor.tom(), utsettelseMor.søker(), null, null),
                new UttakPeriodeDto(uttakFar.fom(), uttakFar.tom(), null, uttakFar.søker(), null));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR).medPerioder(uttaksplan)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummerMor = mor.søk(søknad);
        ventPåInntektsmeldingForespørsel(saksnummerMor);
        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummerMor, fpstartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.harAksjonspunkt(AksjonspunktKoder.VURDER_UTTAK_DOKUMENTASJON_KODE);
        assertThat(saksbehandler.valgtFagsak.status()).as("Fagsak status").isEqualTo(FagsakStatus.UNDER_BEHANDLING);

        var far = familie.far();
        var søknadFar = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medPerioder(byttPerspektiv(uttaksplan))
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()));
        var saksnummerFar = far.søk(søknadFar);
        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.valgtFagsak.status()).as("Fagsak status").isEqualTo(FagsakStatus.UNDER_BEHANDLING);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent()).as("Behandling er satt på vent").isTrue();

        // Behandle ferdig mor sin sak
        saksbehandler.hentFagsak(saksnummerMor);
        var avklarUttakDok = saksbehandler.hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse()).godkjennSykdom();
        saksbehandler.bekreftAksjonspunkt(avklarUttakDok);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());
        beslutter.hentFagsak(saksnummerMor);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        beslutter.ventTilFagsakLøpende();
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        debugFritekst("Ferdig med behandling mor");

        // Behandle ferdig far sin sak
        var arbeidsgivere = far.arbeidsgivere();
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.sakErKobletTilAnnenpart()).as("Saken er koblet til en annen behandling").isTrue();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        debugFritekst("Ferdig med behandling far");

        // Verifisere at det ikke er blitt opprettet revurdering berørt sak på mor
        saksbehandler.hentFagsak(saksnummerMor);
        assertThat(saksbehandler.behandlinger).as("Antall behandlinger").hasSize(1);
    }

    @Test
    @DisplayName("Mor og far koblet sak, mors endringssøknad sniker")
    @Description("Mor sin endringssøknad sniker i køen, når far sin behandling venter på IM. "
            + "Dette skal ikke føre til at far mister til periode som overlapper med mors førstegangssøknad. "
            + "Mor søker om perioden på nytt får å ta tilbake den tapte perioden.")
    void far_skal_ikke_miste_perioder_til_mor_ved_sniking() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .forelder(far().inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(8))
                .build();
        var mor = familie.mor();
        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();

        // Mor førstegangssøknad
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var periodeFørFødselMor = uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor,
                fødselsdato.minusDays(1));
        var mødrekvoteMor = uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1));
        var fellesPeriodeMorFørstegangssøknad = uttaksperiode(Rolle.MOR, KontoType.FELLESPERIODE, fødselsdato.plusWeeks(6),
                fødselsdato.plusWeeks(7).minusDays(1));
        var fpStartdatoFar = fødselsdato.minusWeeks(2);
        var samtidigUttakFar = uttaksperiode(Rolle.FAR_MEDMOR, FEDREKVOTE, fpStartdatoFar, fødselsdato.minusDays(1), BigDecimal.valueOf(100));
        var fedrekvoteFar = uttaksperiode(Rolle.FAR_MEDMOR, FEDREKVOTE, fellesPeriodeMorFørstegangssøknad.fom(),
                fellesPeriodeMorFørstegangssøknad.fom().plusWeeks(6).minusDays(1));
        var uttaksplan = List.of(
                new UttakPeriodeDto(periodeFørFødselMor.fom(), helgejustertTilFredag(fpStartdatoFar.minusDays(1)),
                        periodeFørFødselMor.søker(), null, null),
                new UttakPeriodeDto(samtidigUttakFar.fom(), samtidigUttakFar.tom(), periodeFørFødselMor.søker(), samtidigUttakFar.søker(), null),
                mødrekvoteMor,
                new UttakPeriodeDto(fellesPeriodeMorFørstegangssøknad.fom(), fellesPeriodeMorFørstegangssøknad.tom(),
                        fellesPeriodeMorFørstegangssøknad.søker(), fedrekvoteFar.søker(), null),
                new UttakPeriodeDto(helgejustertTilMandag(fellesPeriodeMorFørstegangssøknad.tom().plusDays(1)), fedrekvoteFar.tom(),
                        null, fedrekvoteFar.søker(), null));
        var søknadMor = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medPerioder(uttaksplan)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fødselsdato);
        var morSaksnummer = mor.søk(søknadMor);
        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(morSaksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(morSaksnummer, fpStartdatoMor);
        saksbehandler.hentFagsak(morSaksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Far førstegangssøknad
        var søknadFar = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medPerioder(byttPerspektiv(uttaksplan))
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medMottattdato(fødselsdato.plusWeeks(1));
        var farSaksnummer = far.søk(søknadFar);

        saksbehandler.hentFagsak(farSaksnummer);
        saksbehandler.ventPåOgVelgÅpenFørstegangsbehandling();
        saksbehandler.harBehandlingsstatus(BehandlingStatus.UTREDES);

        /*
            Mor utvider fellesperioden sin med 1 uke mens far sin behandling er på vent pga manglende inntektsmelding
            Mor sniker i køen. Far skal ikke miste perioder som overlapper med mor sin førstegangssøknad.
        */

        var uttaksperiodeEndringssøknadMor = uttaksperiode(Rolle.MOR, KontoType.FELLESPERIODE, fødselsdato.plusWeeks(7),
                fødselsdato.plusWeeks(8).minusDays(1));
        var uttaksplanEndring = List.of(
                new UttakPeriodeDto(periodeFørFødselMor.fom(), helgejustertTilFredag(fpStartdatoFar.minusDays(1)),
                        periodeFørFødselMor.søker(), null, null),
                new UttakPeriodeDto(samtidigUttakFar.fom(), samtidigUttakFar.tom(), periodeFørFødselMor.søker(), samtidigUttakFar.søker(), null),
                mødrekvoteMor,
                new UttakPeriodeDto(fellesPeriodeMorFørstegangssøknad.fom(), uttaksperiodeEndringssøknadMor.tom(),
                        fellesPeriodeMorFørstegangssøknad.søker(), fedrekvoteFar.søker(), null),
                new UttakPeriodeDto(helgejustertTilMandag(uttaksperiodeEndringssøknadMor.tom().plusDays(1)), fedrekvoteFar.tom(),
                        null, fedrekvoteFar.søker(), null));
        var endringssøknadMor = lagEndringssøknad(søknadMor.build(), morSaksnummer,
                fordeling(uttaksplanEndring)).medMottattdato(fødselsdato.plusWeeks(2));
        mor.søk(endringssøknadMor);

        saksbehandler.hentFagsak(morSaksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).isEmpty();

        /*
            Arbeidsgiver til far sender IM. Far sin sak ferdigbehandles automatisk.
            Far eller mor har ikke søkt om samtidig uttak og vi vil dermed avslå enten far eller mor sin periode i det tidsrommet.
            1)  Far skal ikke få avslag på periodene som overlapper med mor sin førstegangsbehandling siden far sin søknad ble
                sendt etter mors førstegangsøknad. Det skal opprettes berørt behandling på mor som avslår denne perioden.
            2)  Tilsvarende skal far få avslag på perioden som mor har søkt om i revurderingen sin.
         */
        far.arbeidsgivere().sendDefaultInntektsmeldingerFP(farSaksnummer, fødselsdato.minusWeeks(2));
        saksbehandler.hentFagsak(farSaksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        var avslåttUttaksperiode = saksbehandler.hentAvslåtteUttaksperioder();
        assertThat(avslåttUttaksperiode).hasSize(1);
        assertThat(avslåttUttaksperiode.get(0).getFom()).isEqualTo(uttaksperiodeEndringssøknadMor.fom());
        assertThat(avslåttUttaksperiode.get(0).getTom()).isEqualTo(uttaksperiodeEndringssøknadMor.tom());

        // Mor's uttak
        saksbehandler.hentFagsak(morSaksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling(BERØRT_BEHANDLING);

        if (saksbehandler.harAksjonspunkt(VURDER_FEILUTBETALING_KODE)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(
                    new VurderTilbakekrevingVedNegativSimulering()).avventSamordningIngenTilbakekreving();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
            saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
            saksbehandler.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();
        } else {
            saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        }

        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).hasSize(1);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder().get(0).getFom()).isEqualTo(
                fellesPeriodeMorFørstegangssøknad.fom());
        assertThat(saksbehandler.hentAvslåtteUttaksperioder().get(0).getTom()).isEqualTo(
                fellesPeriodeMorFørstegangssøknad.tom());

        /*
            Mor søker om perioden som ble avslått siden far søkte siste og ingen hadde søkt samtidig uttak.
            Mor vil dermed stjele perioden tilbake fra far. Fører til at begge periodene til far blir avlått.
         */
        var uttaksplanEndring2 = List.of(
                new UttakPeriodeDto(periodeFørFødselMor.fom(), helgejustertTilFredag(fpStartdatoFar.minusDays(1)),
                        periodeFørFødselMor.søker(), null, null),
                new UttakPeriodeDto(samtidigUttakFar.fom(), samtidigUttakFar.tom(), periodeFørFødselMor.søker(), samtidigUttakFar.søker(), null),
                mødrekvoteMor,
                new UttakPeriodeDto(fellesPeriodeMorFørstegangssøknad.fom(), uttaksperiodeEndringssøknadMor.tom(),
                        fellesPeriodeMorFørstegangssøknad.søker(), fedrekvoteFar.søker(), null),
                new UttakPeriodeDto(helgejustertTilMandag(uttaksperiodeEndringssøknadMor.tom().plusDays(1)), fedrekvoteFar.tom(),
                        null, fedrekvoteFar.søker(), null));
        var endringssøknadMor2 = lagEndringssøknad(endringssøknadMor.build(), morSaksnummer,
                fordeling(uttaksplanEndring2)).medMottattdato(fødselsdato.plusWeeks(3));
        mor.søk(endringssøknadMor2);

        saksbehandler.ventPåOgVelgRevurderingBehandling(RE_ENDRING_FRA_BRUKER, 2);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).isEmpty();

        saksbehandler.hentFagsak(farSaksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling(BERØRT_BEHANDLING);
        var avslåttePerioderFarRevurdering = saksbehandler.hentAvslåtteUttaksperioder();
        assertThat(avslåttePerioderFarRevurdering).hasSize(1);
        assertThat(avslåttePerioderFarRevurdering.get(0).getFom()).isEqualTo(fellesPeriodeMorFørstegangssøknad.fom());
    }

    @Test
    @DisplayName("Far og mor søker fødsel med overlappende uttaksperiode")
    @Description("Mor søker og får innvilget. Far søker med to uker overlapp med mor (stjeling) og ifm fødsel. Far får innvilget. "
            + "Berørt sak opprettes på mor. Siste periode blir spittet i to og siste del blir avlsått. Det opprettes ikke"
            + "berørt sak på far.")
    void farOgMor_fødsel_ettArbeidsforholdHver_overlappendePeriode() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .forelder(far().inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(8))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpstartdatoMor = fødselsdato.minusWeeks(3);

        // MOR
        var fpStartdatoFar = fødselsdato;
        var mødrekvoteMor = uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10).minusDays(1));
        var samtidigUttakFar = uttaksperiode(Rolle.FAR_MEDMOR, FEDREKVOTE, fpStartdatoFar, fpStartdatoFar.plusWeeks(2).minusDays(1),
                BigDecimal.valueOf(100));
        var fedrekvoteFar = uttaksperiode(Rolle.FAR_MEDMOR, FEDREKVOTE, fpStartdatoFar.plusWeeks(8), fpStartdatoFar.plusWeeks(12).minusDays(1));
        var uttaksplan = List.of(
                uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fpstartdatoMor, fødselsdato.minusDays(1)),
                new UttakPeriodeDto(samtidigUttakFar.fom(), samtidigUttakFar.tom(), mødrekvoteMor.søker(), samtidigUttakFar.søker(), null),
                new UttakPeriodeDto(helgejustertTilMandag(fpStartdatoFar.plusWeeks(2)), helgejustertTilFredag(fpStartdatoFar.plusWeeks(8).minusDays(1)),
                        mødrekvoteMor.søker(), null, null),
                new UttakPeriodeDto(fedrekvoteFar.fom(), mødrekvoteMor.tom(), mødrekvoteMor.søker(), fedrekvoteFar.søker(), null),
                new UttakPeriodeDto(helgejustertTilMandag(fødselsdato.plusWeeks(10)), fedrekvoteFar.tom(), null, fedrekvoteFar.søker(), null));
        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR).medPerioder(uttaksplan)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fødselsdato.minusWeeks(3));
        var saksnummerMor = mor.søk(søknadMor);

        var arbeidsgivere = mor.arbeidsgivere();
        ventPåInntektsmeldingForespørsel(saksnummerMor);
        arbeidsgivere.sendDefaultInntektsmeldingerFP(saksnummerMor, fpstartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        debugFritekst("Ferdig med første behandling mor");
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker()).as("Antall uttkasperioder for søker")
                .hasSize(3);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti()).as("Antall stønadskontoer i saldo").hasSize(4);

        // FAR
        var far = familie.far();
        var søknadFar = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medPerioder(byttPerspektiv(uttaksplan))
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medMottattdato(fødselsdato);
        var saksnummerFar = far.søk(søknadFar);

        ventPåInntektsmeldingForespørsel(saksnummerFar);
        far.arbeidsgivere().sendDefaultInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        debugFritekst("Ferdig med første behandling til far");
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.sakErKobletTilAnnenpart()).as("Sak er koblet til mor sin behandling").isTrue();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Antall avslåtte uttaksperioder").isEmpty();

        // Revurdering berørt sak mor
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        debugFritekst("Revurdering berørt sak opprettet på mor.");
        assertThat(saksbehandler.sakErKobletTilAnnenpart()).as("Saken er koblet til annenpart").isTrue();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Antall avslåtte uttaksperioder").hasSize(1);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder().get(0).getFom()).isEqualTo(
                helgejustertTilMandag(fpStartdatoFar.plusWeeks(8)));
        assertThat(saksbehandler.valgtBehandling.getSaldoer()
                .stønadskonti()
                .get(Saldoer.SaldoVisningStønadskontoType.FEDREKVOTE)
                .saldo()).as("Saldo fro stønadskonto FEDREKVOTE").isPositive();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti().get(MØDREKVOTE).saldo()).as(
                "Saldo fro stønadskonto MØDREKVOTE").isPositive();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti().get(FELLESPERIODE).saldo()).as(
                "Saldo fro stønadskonto FELLESPERIODE").isEqualTo(80);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti().get(FORELDREPENGER_FØR_FØDSEL).saldo()).as(
                "Saldo fro stønadskonto FORELDREPENGER_FØR_FØDSEL").isNotNegative();

        // FAR: Verifiser at far ikke får berørt behandling
        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.harRevurderingBehandling()).isFalse();
        assertThat(saksbehandler.hentAlleBehandlingerForFagsak(saksnummerFar)).as("Antall behandlinger").hasSize(1);
    }

    @Test
    @DisplayName("Koblet sak endringssøknad ingen endring")
    @Description("Sender inn søknad mor. Sender inn søknad far uten overlapp. Sender inn endringssøknad mor som er lik "
            + "førstegangsbehandlingen. Verifiserer at det ikke blir berørt sak på far.")
    void kobletSakIngenEndring() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntekt(InntektGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4), 900_000)
                        .arbeidsforhold(LocalDate.now().minusMonths(4), 900_000)
                        .build()).build())
                .forelder(far().inntekt(InntektGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4), 900_000)
                        .arbeidsforhold(LocalDate.now().minusMonths(4), 900_000)
                        .build()).build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusYears(3))
                .build();
        var fødselsdato = LocalDate.now().minusMonths(4);
        var fpStartdatoFar = fødselsdato.plusWeeks(10).plusDays(1);
        var uttaksplan = List.of(
                uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)),
                UttakPeriodeBuilder.periode(helgejustertTilMandag(fpStartdatoFar), helgejustertTilFredag(fpStartdatoFar.plusWeeks(2)))
                        .medAnnenPart(UttakPeriodeBuilder.uttak(Rolle.FAR_MEDMOR).medKontoType(FEDREKVOTE).build())
                        .build());
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, fødselsdato, uttaksplan);
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, fødselsdato, fpStartdatoFar, byttPerspektiv(uttaksplan));

        saksbehandler.hentFagsak(saksnummerMor);
        assertThat(saksbehandler.harRevurderingBehandling()).as("Har revurdert behandling").isFalse();

        sendInnEndringssøknadforMor(familie, saksnummerMor, uttaksplan);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INGEN_ENDRING);

        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.harRevurderingBehandling()).as(
                        "Har revurdert behandling (Fars behandling fikk revurdering selv uten endringer i mors behandling av endringssøknaden)")
                .isFalse();
    }

    @Test
    @DisplayName("Koblet sak. Far utsetter alt/gir fra seg alt. Far ny 1gang")
    @Description("Mor sender inn førstegangssøknad som ferdigbehandles før far sender inn søknad uten overlapp. "
            + "Far sin sak ferdigbehandles. Far tar deretter å sender inn en endringssøknad for å gi fra seg alle "
            + "innvilgede periodene.")
    void kobletSakFarUtsetterAlt() {
        var fødselsdato = LocalDate.now().minusMonths(4);
        var farOpprinneligStartdato = fødselsdato.plusWeeks(10).plusDays(1);
        var farUtsattStartDato = fødselsdato.plusWeeks(50).plusDays(1);
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntekt(InntektGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4), 900_000)
                        .arbeidsforhold(LocalDate.now().minusMonths(4), 900_000)
                        .build()).build())
                .forelder(far().inntekt(InntektGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4), 900_000)
                        .arbeidsforhold(LocalDate.now().minusMonths(4), 900_000)
                        .build()).build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusYears(3))
                .build();
        var uttaksplan = List.of(
                uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)),
                UttakPeriodeBuilder.periode(helgejustertTilMandag(farOpprinneligStartdato),
                                helgejustertTilFredag(farOpprinneligStartdato.plusWeeks(2)))
                        .medAnnenPart(UttakPeriodeBuilder.uttak(Rolle.FAR_MEDMOR).medKontoType(FEDREKVOTE).build())
                        .build());
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, fødselsdato, uttaksplan); // fødselsdato-3w -> fødselsdato+10w
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, fødselsdato, farOpprinneligStartdato,
                byttPerspektiv(uttaksplan)); // fødselsdato+10w1d -> fødselsdato+12w1d
        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.harRevurderingBehandling()).as("Har revurdert behandling").isFalse();

        // FAR: Endringssøknad som sier opp innvilget uttak fra start
        var far = familie.far();
        var uttaksplanEndring = List.of(UttakPeriodeBuilder.periode(helgejustertTilMandag(fødselsdato.minusWeeks(3)),
                                helgejustertTilFredag(fødselsdato.minusDays(1)))
                        .medAnnenPart(UttakPeriodeBuilder.uttak(Rolle.MOR).medKontoType(KontoType.FORELDREPENGER_FØR_FØDSEL).build())
                        .build(),
                UttakPeriodeBuilder.periode(helgejustertTilMandag(fødselsdato), helgejustertTilFredag(fødselsdato.plusWeeks(10)))
                        .medAnnenPart(UttakPeriodeBuilder.uttak(Rolle.MOR).medKontoType(KontoType.MØDREKVOTE).build())
                        .build(),
                utsettelsesperiode(Rolle.FAR_MEDMOR, UtsettelseÅrsak.FRI, farOpprinneligStartdato, farOpprinneligStartdato.plusWeeks(2)),
                uttaksperiode(Rolle.FAR_MEDMOR, FEDREKVOTE, farUtsattStartDato, farUtsattStartDato.plusWeeks(10)));
        var søknad = lagEndringssøknad(far.førstegangssøknad(), saksnummerFar, fordeling(uttaksplanEndring));
        far.søk(søknad);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling(RE_UTSATT_START); //Må velge revurdering her siden noen ganger opprettes det en ny førstegangsbehandling som legger seg på vent
        var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(
                new VurderTilbakekrevingVedNegativSimulering()).avventSamordningIngenTilbakekreving();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandling();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_SENERE);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker()
                .stream()
                .map(BehandlingÅrsak::behandlingArsakType)).doesNotContain(BERØRT_BEHANDLING);
    }

    @Test
    @DisplayName("Koblet sak. Far utsetter oppstart rundt fødsel")
    @Description("Mor søker og får innvilget. Far søker og får innvilget rundt fødsel. Far søker utsatt oppstart "
            + "og blir innvilget uten berørt behandling hos mor.")
    void farUtsetterOppstartRundtFødsel() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .forelder(far().inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(8))
                .build();
        var fødselsdato = familie.barn().fødselsdato();

        var far = familie.far();
        var periodeFørFødselMor = uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1));
        var mødrekvoteMor = uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1));
        var fellesperiodeMor = uttaksperiode(Rolle.MOR, KontoType.FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(31).minusDays(1));
        var samtidigUttakFar = uttaksperiode(Rolle.FAR_MEDMOR, FEDREKVOTE, fødselsdato, fødselsdato.plusWeeks(2).minusDays(1),
                BigDecimal.valueOf(100));
        var uttaksplan = List.of(
                periodeFørFødselMor,
                new UttakPeriodeDto(samtidigUttakFar.fom(), samtidigUttakFar.tom(), mødrekvoteMor.søker(), samtidigUttakFar.søker(), null),
                new UttakPeriodeDto(helgejustertTilMandag(fødselsdato.plusWeeks(2)), mødrekvoteMor.tom(), mødrekvoteMor.søker(), null, null),
                fellesperiodeMor);
        var søknadMor = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medPerioder(uttaksplan)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var mor = familie.mor();
        var saksnummerMor = mor.søk(søknadMor);
        ventPåInntektsmeldingForespørsel(saksnummerMor);
        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummerMor, fødselsdato.minusWeeks(3));
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var søknadFar = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medPerioder(byttPerspektiv(uttaksplan))
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()));
        var saksnummerFar = far.søk(søknadFar);
        ventPåInntektsmeldingForespørsel(saksnummerFar);
        far.arbeidsgiver().sendInntektsmeldingerFP(saksnummerFar, fødselsdato);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var utsettelseFar = utsettelsesperiode(Rolle.FAR_MEDMOR, UtsettelseÅrsak.FRI, fødselsdato, fødselsdato.plusWeeks(1).minusDays(1));
        var utsattUttakFar = uttaksperiode(Rolle.FAR_MEDMOR, FEDREKVOTE, fødselsdato.plusWeeks(1), fødselsdato.plusWeeks(3).minusDays(1),
                BigDecimal.valueOf(100));
        var uttaksplanEndring = List.of(
                new UttakPeriodeDto(periodeFørFødselMor.fom(), periodeFørFødselMor.tom(), null, periodeFørFødselMor.søker(), null),
                new UttakPeriodeDto(utsettelseFar.fom(), utsettelseFar.tom(), utsettelseFar.søker(), mødrekvoteMor.søker(), null),
                new UttakPeriodeDto(utsattUttakFar.fom(), utsattUttakFar.tom(), utsattUttakFar.søker(), mødrekvoteMor.søker(), null),
                new UttakPeriodeDto(helgejustertTilMandag(fødselsdato.plusWeeks(3)), mødrekvoteMor.tom(), null, mødrekvoteMor.søker(), null),
                new UttakPeriodeDto(fellesperiodeMor.fom(), fellesperiodeMor.tom(), null, fellesperiodeMor.søker(), null));
        var endringssøknad = lagEndringssøknad(søknadFar.build(), saksnummerFar,
                fordeling(uttaksplanEndring));
        far.søk(endringssøknad);

        saksbehandler.ventPåOgVelgRevurderingBehandling(RE_ENDRING_FRA_BRUKER);
        var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(
                new VurderTilbakekrevingVedNegativSimulering()).avventSamordningIngenTilbakekreving();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        var uttak = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttak).hasSize(1);
        assertThat(uttak.get(0).getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttak.get(0).getAktiviteter().get(0).getKontoType()).isEqualTo(FEDREKVOTE);
        assertThat(uttak.get(0).getAktiviteter().get(0).getUtbetalingsgrad()).isEqualTo(BigDecimal.valueOf(100));

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker()
                .stream()
                .map(BehandlingÅrsak::behandlingArsakType)).doesNotContain(BERØRT_BEHANDLING);
    }

    @Test
    @DisplayName("Koblet sak. Far utsetter fra start med senere uttaksdato")
    @Description("Sender inn søknad mor. Sender inn søknad far uten overlapp. Sender inn endringssøknad far med "
            + "fri utsettelserList og uttaksperioder med start senere. Sender inn IM")
    void kobletSakFarUtsetterStartdato() {
        var fødselsdato = LocalDate.now().minusMonths(4);
        var farOpprinneligStartdato = fødselsdato.plusWeeks(10).plusDays(1);
        var farUtsattStartDato = LocalDate.now().plusWeeks(5).plusDays(2);
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntekt(InntektGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4), 900_000)
                        .arbeidsforhold(LocalDate.now().minusMonths(4), 900_000)
                        .build()).build())
                .forelder(far().inntekt(InntektGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4), 900_000)
                        .arbeidsforhold(LocalDate.now().minusMonths(4), 900_000)
                        .build()).build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusYears(3))
                .build();
        var uttaksplan = List.of(
                uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)),
                UttakPeriodeBuilder.periode(helgejustertTilMandag(farOpprinneligStartdato),
                                helgejustertTilFredag(farOpprinneligStartdato.plusWeeks(2)))
                        .medAnnenPart(UttakPeriodeBuilder.uttak(Rolle.FAR_MEDMOR).medKontoType(FEDREKVOTE).build())
                        .build());
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, fødselsdato, uttaksplan);
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, fødselsdato, farOpprinneligStartdato,
                byttPerspektiv(uttaksplan));

        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.harRevurderingBehandling()).as("Har revurdert behandling").isFalse();

        // Sier opp uttaket men søker om uttak ca 15 uker senere
        var far = familie.far();
        var uttaksplanEndring = List.of(UttakPeriodeBuilder.periode(helgejustertTilMandag(fødselsdato.minusWeeks(3)),
                                helgejustertTilFredag(fødselsdato.minusDays(1)))
                        .medAnnenPart(UttakPeriodeBuilder.uttak(Rolle.MOR).medKontoType(KontoType.FORELDREPENGER_FØR_FØDSEL).build())
                        .build(),
                UttakPeriodeBuilder.periode(helgejustertTilMandag(fødselsdato), helgejustertTilFredag(fødselsdato.plusWeeks(10)))
                        .medAnnenPart(UttakPeriodeBuilder.uttak(Rolle.MOR).medKontoType(KontoType.MØDREKVOTE).build())
                        .build(),
                utsettelsesperiode(Rolle.FAR_MEDMOR, UtsettelseÅrsak.FRI, farOpprinneligStartdato, farOpprinneligStartdato.plusWeeks(2).minusDays(1)),
                uttaksperiode(Rolle.FAR_MEDMOR, KontoType.FEDREKVOTE, farUtsattStartDato, farUtsattStartDato.plusWeeks(2).minusDays(1)));
        var søknad = lagEndringssøknad(far.førstegangssøknad(), saksnummerFar, fordeling(uttaksplanEndring));
        far.søk(søknad);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(
                new VurderTilbakekrevingVedNegativSimulering()).tilbakekrevingMedVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_SENERE);

        saksbehandler.ventPåOgVelgÅpenFørstegangsbehandling();
        saksbehandler.harBehandlingsstatus(BehandlingStatus.UTREDES);

        // Søkt for tidlig - kan behandles om litt over en uke
        assertThat(saksbehandler.valgtBehandling.getAksjonspunkt()
                .stream()
                .map(Aksjonspunkt::getDefinisjon)
                .anyMatch(AksjonspunktKoder.AUTO_VENT_PGA_FOR_TIDLIG_SØKNAD::equals)).isTrue();
    }

    @Test
    @DisplayName("Mor får revurdering fra endringssøknad vedtak opphører")
    @Description("Mor får revurdering fra endringssøknad vedtak opphører - far får revurdering")
    void berørtSakOpphør() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntekt(InntektGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4), 900_000)
                        .arbeidsforhold(LocalDate.now().minusMonths(4), 900_000)
                        .build()).build())
                .forelder(far().inntekt(InntektGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4), 900_000)
                        .arbeidsforhold(LocalDate.now().minusMonths(4), 900_000)
                        .build()).build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusYears(3))
                .build();
        var fødselsdato = LocalDate.now().minusMonths(4);
        var fpStartdatoFar = fødselsdato.plusWeeks(10).plusDays(1);
        var uttaksplan = List.of(
                uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)),
                UttakPeriodeBuilder.periode(helgejustertTilMandag(fpStartdatoFar), helgejustertTilFredag(fpStartdatoFar.plusWeeks(2)))
                        .medAnnenPart(UttakPeriodeBuilder.uttak(Rolle.FAR_MEDMOR).medKontoType(FEDREKVOTE).build())
                        .build());
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, fødselsdato, uttaksplan);
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, fødselsdato, fpStartdatoFar, byttPerspektiv(uttaksplan));

        // Endringssøknad med aksjonspunkt
        var mødrekvoteMor = uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1));
        var fedrekvoteFar = uttaksperiode(Rolle.FAR_MEDMOR, FEDREKVOTE, fpStartdatoFar, fpStartdatoFar.plusWeeks(2));
        var uttaksplanEndring = List.of(
                uttaksperiode(Rolle.MOR, KontoType.FELLESPERIODE, fødselsdato.minusWeeks(4), fødselsdato.minusWeeks(3).minusDays(1)),
                uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                new UttakPeriodeDto(mødrekvoteMor.fom(), helgejustertTilFredag(fedrekvoteFar.fom().minusDays(1)), mødrekvoteMor.søker(), null, null),
                new UttakPeriodeDto(fedrekvoteFar.fom(), fedrekvoteFar.tom(), mødrekvoteMor.søker(), fedrekvoteFar.søker(), null),
                new UttakPeriodeDto(helgejustertTilMandag(fedrekvoteFar.tom().plusDays(1)), mødrekvoteMor.tom(), mødrekvoteMor.søker(), null, null),
                uttaksperiode(Rolle.MOR, KontoType.FELLESPERIODE, fødselsdato.plusWeeks(17), fødselsdato.plusWeeks(30).minusDays(1)));
        var mor = familie.mor();
        var søknad = lagEndringssøknad(mor.førstegangssøknad(), saksnummerMor, fordeling(uttaksplanEndring));
        mor.søk(søknad);

        overstyrer.hentFagsak(saksnummerMor);
        overstyrer.ventPåOgVelgRevurderingBehandling();

        var overstyr = new OverstyrFodselsvilkaaret();
        overstyr.avvis(Avslagsårsak.SØKER_ER_FAR);
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        var vurderSoknadsfristForeldrepengerBekreftelse = overstyrer.hentAksjonspunktbekreftelse(
                new VurderSoknadsfristForeldrepengerBekreftelse()).bekreftHarGyldigGrunn(fødselsdato);
        overstyrer.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        var vurderTilbakekrevingVedNegativSimulering = overstyrer.hentAksjonspunktbekreftelse(
                new VurderTilbakekrevingVedNegativSimulering()).tilbakekrevingUtenVarsel();
        overstyrer.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);

        overstyrer.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummerMor);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(bekreftelse);
        beslutter.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.OPPHØR);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INGEN_ENDRING);
    }


    @Test
    @DisplayName("Koblet sak mor søker etter far og sniker i køen")
    @Description("Far søker. Blir satt på vent pga for tidlig søknad. Mor søker og får innvilget. Oppretter manuell revurdering på mor.")
    void kobletSakMorSøkerEtterFar() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntekt(InntektGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4), 900_000)
                        .arbeidsforhold(LocalDate.now().minusMonths(4), 900_000)
                        .build()).build())
                .forelder(far().inntekt(InntektGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4), 900_000)
                        .arbeidsforhold(LocalDate.now().minusMonths(4), 900_000)
                        .build()).build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusYears(3))
                .build();
        var fødselsdato = LocalDate.now().minusDays(15);
        var fpStartdatoFar = fødselsdato.plusWeeks(10).plusDays(1);
        var uttaksplan = List.of(
                uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)),
                UttakPeriodeBuilder.periode(helgejustertTilMandag(fpStartdatoFar), helgejustertTilFredag(fpStartdatoFar.plusWeeks(2)))
                        .medAnnenPart(UttakPeriodeBuilder.uttak(Rolle.FAR_MEDMOR).medKontoType(FEDREKVOTE).build())
                        .build());
        behandleSøknadForFarSattPåVent(familie, fødselsdato, byttPerspektiv(uttaksplan));
        var saksnummerMor = behandleSøknadForMorUregistrert(familie, fødselsdato, uttaksplan);

        saksbehandler.hentFagsak(saksnummerMor);

        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
    }

    @Test
    @DisplayName("Mor og far søker. Krever dokumentasjon når far søker hvor mor er i 50% stilling.")
    @Description(
            "Mor har foreldrepenger og har 50% stilling i AA-register - som øker til 100% under uttaket med perm som strekker seg forbi mors uttak. "
                    + "Far søker litt fellesperiode/arbeid når a) mor jobber 50% og b) mor jobber 100% men har perm og c) mor jobber 100%. "
                    + "Far skal få aksjonspunkt rundt uttaksdokumentasjon. 2 av 3 perioder skal ha behov for avklaring")
    void kreverDokumentasjonBeggeRett() {
        var fødselsdato = LocalDate.now().minusMonths(2);
        var årslønn = 600_000;
        var tomMor50Prosent = fødselsdato.plusWeeks(10).minusDays(1);
        var morPermisjonTom = fødselsdato.plusWeeks(11).minusDays(1);
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntekt(
                        InntektGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", null, fødselsdato.minusYears(5), null, årslønn,
                                        List.of(new PermisjonDto(100, fødselsdato.minusWeeks(3), morPermisjonTom,
                                                Permisjonstype.PERMISJON_MED_FORELDREPENGER)),
                                        arbeidsavtale(fødselsdato.minusYears(5)).tomGyldighetsperiode(tomMor50Prosent).stillingsprosent(50).build(),
                                        arbeidsavtale(tomMor50Prosent.plusDays(1)).stillingsprosent(100).build())
                                .build()).build())
                .forelder(far().inntekt(InntektGenerator.ny().arbeidMedOpptjeningOver6G().build()).build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(fødselsdato)
                .build();

        // Mor's søknad og behandling
        var mor = familie.mor();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = helgejustertTilMandag(fødselsdato.plusWeeks(7));
        var uttaksplan = List.of(
                uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(7).minusDays(1)),
                UttakPeriodeBuilder.periode(fpStartdatoFar, helgejustertTilFredag(fødselsdato.plusWeeks(20).minusDays(1)))
                        .medAnnenPart(UttakPeriodeBuilder.uttak(Rolle.FAR_MEDMOR)
                                .medKontoType(KontoType.FELLESPERIODE)
                                .medMorsAktivitet(ARBEID)
                                .build())
                        .build());

        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR).medAnnenForelder(
                        AnnenforelderMaler.norskMedRettighetNorge(familie.far())).medPerioder(uttaksplan);
        var saksnummerMor = mor.søk(søknadMor);
        ventPåInntektsmeldingForespørsel(saksnummerMor);
        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummerMor, fpStartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Far's søknad og behandling
        var far = familie.far();
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR).medAnnenForelder(
                        AnnenforelderMaler.norskMedRettighetNorge(familie.mor())).medPerioder(byttPerspektiv(uttaksplan));
        var saksnummerFar = far.søk(søknadFar);
        ventPåInntektsmeldingForespørsel(saksnummerFar);
        far.arbeidsgiver().sendInntektsmeldingerFP(saksnummerFar, fødselsdato.plusWeeks(7));

        // Verifiser at far får aksjonspunkt for uttaksdokumentasjon
        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.harAksjonspunkt(AksjonspunktKoder.VURDER_UTTAK_DOKUMENTASJON_KODE)).as(
                "Aksjonspunkt når mor har 50% stilling og 100% stilling").isTrue();

        // Mor har 50% stilling til og med 9 uker etter fødsel. Fra og med den tiende uke har hun 100% stilling.
        var periode50Prosent = new ÅpenPeriodeDto(fpStartdatoFar, tomMor50Prosent);
        saksbehandler.bekreftAksjonspunkt(saksbehandler.hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse())
                .godkjenn(periode50Prosent, BigDecimal.valueOf(50))
                .godkjenn(new ÅpenPeriodeDto(tomMor50Prosent.plusDays(1), morPermisjonTom))
        );
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummerFar);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        beslutter.ventTilFagsakLøpende();
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var innvilgetUttaksperiode = saksbehandler.hentInnvilgedeUttaksperioder();
        assertThat(innvilgetUttaksperiode).hasSize(3);
    }

    @Step("Behandle søknad for mor uregistrert")
    private Saksnummer behandleSøknadForMorUregistrert(Familie familie, LocalDate fødselsdato, List<UttakPeriodeDto> uttaksplan) {
        var saksnummer = sendInnSøknadOgInntektMor(familie, fødselsdato, uttaksplan);

        saksbehandler.hentFagsak(saksnummer);

        var sjekkManglendeFødsel = saksbehandler.hentAksjonspunktbekreftelse(new SjekkManglendeFødselBekreftelse())
                .bekreftBarnErFødt(1, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(sjekkManglendeFødsel);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());


        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        return saksnummer;
    }

    private Saksnummer behandleSøknadForMorUtenOverlapp(Familie familie, LocalDate fødselsdato, List<UttakPeriodeDto> uttaksplan) {
        var saksnummer = sendInnSøknadOgInntektMor(familie, fødselsdato, uttaksplan);

        saksbehandler.hentFagsak(saksnummer);

        var sjekkManglendeFødsel = saksbehandler.hentAksjonspunktbekreftelse(new SjekkManglendeFødselBekreftelse())
                .bekreftBarnErFødt(1, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(sjekkManglendeFødsel);

        var vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(
                new VurderSoknadsfristForeldrepengerBekreftelse()).bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        return saksnummer;
    }

    private Saksnummer behandleSøknadForFarUtenOverlapp(Familie familie,
                                                        LocalDate fødselsdato,
                                                        LocalDate startdato, List<UttakPeriodeDto> uttaksplan) {
        var saksnummer = sendInnSøknadOgInntektFar(familie, fødselsdato, startdato, uttaksplan);
        behandleFerdigSøknadForFarUtenOverlapp(saksnummer);
        return saksnummer;

    }

    private void behandleFerdigSøknadForFarUtenOverlapp(Saksnummer saksnummer) {
        saksbehandler.hentFagsak(saksnummer);

        assertThat(saksbehandler.sakErKobletTilAnnenpart()).as("Sak koblet til annenpart").isTrue();

        var sjekkManglendeFødsel = saksbehandler.hentAksjonspunktbekreftelse(new SjekkManglendeFødselBekreftelse())
                .bekreftBarnErFødt(1, LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(sjekkManglendeFødsel);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

    }

    private void sendInnEndringssøknadforMor(Familie familie, Saksnummer saksnummerMor, List<UttakPeriodeDto> uttaksplan) {
        var mor = familie.mor();
        var søknad = lagEndringssøknad(mor.førstegangssøknad(), saksnummerMor, uttaksplan);
        mor.søk(søknad);
    }

    private Saksnummer behandleSøknadForFarSattPåVent(Familie familie, LocalDate fødselsdato, List<UttakPeriodeDto> uttaksplan) {
        var saksnummer = sendInnSøknadFar(familie, fødselsdato, uttaksplan);

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent()).as(
                "Behandling satt på vent (Behandling ble ikke satt på vent selv om far har søkt for tidlig)").isTrue();

        return saksnummer;
    }


    private Saksnummer sendInnSøknadOgInntektMor(Familie familie, LocalDate fødselsdato, List<UttakPeriodeDto> uttaksplan) {
        var saksnummer = sendInnSøknadMor(familie, fødselsdato, uttaksplan);
        sendInnInntektsmeldingMor(familie, fødselsdato, saksnummer);
        return saksnummer;
    }

    private Saksnummer sendInnSøknadMor(Familie familie, LocalDate fødselsdato, List<UttakPeriodeDto> uttaksplan) {
        var mor = familie.mor();
        var søknad = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medPerioder(uttaksplan)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        return mor.søk(søknad);
    }

    private void sendInnInntektsmeldingMor(Familie familie, LocalDate fødselsdato, Saksnummer saksnummer) {
        ventPåInntektsmeldingForespørsel(saksnummer);
        familie.mor().arbeidsgivere().sendDefaultInntektsmeldingerFP(saksnummer, fødselsdato.minusWeeks(3));
    }

    private Saksnummer sendInnSøknadOgInntektFar(Familie familie,
                                                 LocalDate fødselsdato,
                                                 LocalDate startDatoForeldrepenger,
                                                 List<UttakPeriodeDto> uttaksplan) {
        var saksnummer = sendInnSøknadFar(familie, fødselsdato, uttaksplan);
        ventPåInntektsmeldingForespørsel(saksnummer);
        familie.far().arbeidsgivere().sendDefaultInntektsmeldingerFP(saksnummer, startDatoForeldrepenger);
        return saksnummer;
    }

    private Saksnummer sendInnSøknadFar(Familie familie, LocalDate fødselsdato, List<UttakPeriodeDto> uttaksplan) {
        var far = familie.far();
        var søknad = SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR).medPerioder(uttaksplan)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()));
        return far.søk(søknad);
    }
}
