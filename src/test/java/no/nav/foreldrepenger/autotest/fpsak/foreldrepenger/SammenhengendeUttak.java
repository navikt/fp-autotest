package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder.VURDER_FEILUTBETALING_KODE;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEndringMaler.lagEndringssøknad;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.utsettelsesperiode;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.KonsekvensForYtelsen;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakresultatUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedNegativSimulering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderUttakDokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler;
import no.nav.foreldrepenger.generator.soknad.maler.UttakMaler;
import no.nav.foreldrepenger.kontrakter.fpsoknad.BrukerRolle;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.UtsettelsesÅrsak;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("fpsak")
@Tag("foreldrepenger")
class SammenhengendeUttak extends VerdikjedeTestBase {

    @Test
    @DisplayName("Utsettelse av forskjellige årsaker")
    @Description("Mor søker fødsel med mange utsettelseperioder. Hensikten er å sjekke at alle årsaker fungerer." +
            "Kun arbeid (og ferie) skal oppgis i IM. Verifiserer på 0 trekkdager for perioder med utsettelserList. " +
            "Kun perioder som krever dokumentasjon skal bli manuelt behandlet i fakta om uttak. Ingen AP i uttak.")
    void utsettelse_med_avvik() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.of(2017, 1, 1))
                                .build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.of(2018, 1, 1))
                                .build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.of(2019, 11, 1))
                .build();
        var mor = familie.mor();
        var fødsel = familie.barn().fødselsdato();
        var fpStartdato = fødsel.minusWeeks(3);
        var utsettelseInstitusjsoppholdBarn = utsettelsesperiode(UtsettelsesÅrsak.INSTITUSJONSOPPHOLD_BARNET, fødsel.plusWeeks(6), fødsel.plusWeeks(9).minusDays(1));
        var utsettelseInstitusjsoppholdSøker = utsettelsesperiode(UtsettelsesÅrsak.INSTITUSJONSOPPHOLD_SØKER, fødsel.plusWeeks(9), fødsel.plusWeeks(12).minusDays(1));
        var utsettelseSykdom = utsettelsesperiode(UtsettelsesÅrsak.SYKDOM, fødsel.plusWeeks(12), fødsel.plusWeeks(15).minusDays(1));
        var utsettelseArbeid = utsettelsesperiode(UtsettelsesÅrsak.ARBEID, fødsel.plusWeeks(15), fødsel.plusWeeks(16).minusDays(1));
        var utsettelseHvØvelse = utsettelsesperiode(UtsettelsesÅrsak.HV_OVELSE, fødsel.plusWeeks(16), fødsel.plusWeeks(17).minusDays(1));
        var utsettelseNavTiltak = utsettelsesperiode(UtsettelsesÅrsak.NAV_TILTAK, fødsel.plusWeeks(17), fødsel.plusWeeks(18).minusDays(1));
        var fordelingMor = fordeling(
                uttaksperiode(KontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødsel.minusDays(1)),
                uttaksperiode(KontoType.MØDREKVOTE, fødsel, fødsel.plusWeeks(6).minusDays(1)),
                utsettelseInstitusjsoppholdBarn,
                utsettelseInstitusjsoppholdSøker,
                utsettelseSykdom,
                utsettelseArbeid,
                utsettelseHvØvelse,
                utsettelseNavTiltak,
                uttaksperiode(KontoType.FELLESPERIODE, fødsel.plusWeeks(18), fødsel.plusWeeks(21).minusDays(1))
        );
        var søknad = SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel(fødsel, BrukerRolle.MOR)
                .medUttaksplan(fordelingMor)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fpStartdato.minusWeeks(3));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato);
        inntektsmelding.medUtsettelse(Inntektsmelding.UtsettelseÅrsak.ARBEID, fødsel.plusWeeks(15),
                fødsel.plusWeeks(18).minusDays(1));
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);
        var vurderUttakDokumentasjonBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse())
                .godkjenn(utsettelseInstitusjsoppholdBarn)
                .godkjenn(utsettelseInstitusjsoppholdSøker)
                .godkjenn(utsettelseSykdom)
                .godkjenn(utsettelseHvØvelse)
                .godkjenn(utsettelseNavTiltak);
        saksbehandler.bekreftAksjonspunkt(vurderUttakDokumentasjonBekreftelse);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse())
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentUttaksperioder())
                .as("Uttaksperioder")
                .hasSize(9);
        assertThat(beslutter.valgtBehandling.hentUttaksperiode(2).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Antall trekkdager for aktivitet i uttaksperiode")
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(beslutter.valgtBehandling.hentUttaksperiode(3).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Antall trekkdager for aktivitet i uttaksperiode")
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(beslutter.valgtBehandling.hentUttaksperiode(4).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Antall trekkdager for aktivitet i uttaksperiode")
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(beslutter.valgtBehandling.hentUttaksperiode(5).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Antall trekkdager for aktivitet i uttaksperiode")
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(beslutter.valgtBehandling.hentUttaksperiode(6).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Antall trekkdager for aktivitet i uttaksperiode")
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(beslutter.valgtBehandling.hentUttaksperiode(7).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Antall trekkdager for aktivitet i uttaksperiode")
                .isEqualByComparingTo(BigDecimal.ZERO);
    }


    @Test
    @DisplayName("Endringssøknad med utsettelserList")
    @Description("Førstegangsbehandling til positivt vedtak. Endringssøknad med utsettelserList fra bruker. Vedtak fortsatt løpende.")
    void endringssøknadMedUtsettelse() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.of(2017, 1, 1))
                                .build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.of(2018, 1, 1))
                                .build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.of(2019, 11, 1))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var opprinneligFordeling = UttakMaler.fordelingMorHappyCaseLong(fødselsdato);
        var søknad = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medUttaksplan(opprinneligFordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fpStartdato.minusWeeks(3));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker())
                .as("Antall uttaksperioder")
                .hasSize(4);

        var utsettelseFom = fødselsdato.plusWeeks(16);
        var fordelingUtsettelseEndring = fordeling(
                utsettelsesperiode(UtsettelsesÅrsak.ARBEID, utsettelseFom, utsettelseFom.plusWeeks(2).minusDays(1)),
                uttaksperiode(KontoType.FELLESPERIODE, utsettelseFom.plusWeeks(2), utsettelseFom.plusWeeks(16).minusDays(1)));
        var endretSøknad = lagEndringssøknad(søknad.build(), saksnummer, fordelingUtsettelseEndring)
                .medMottattdato(utsettelseFom.minusWeeks(3));
        var saksnummerE = mor.søk(endretSøknad);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        if (saksbehandler.harAksjonspunkt(VURDER_FEILUTBETALING_KODE)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(new VurderTilbakekrevingVedNegativSimulering())
                    .avventSamordningIngenTilbakekreving();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
            saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
            saksbehandler.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();
        } else {
            saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        }

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.konsekvenserForYtelsen())
                .as("Konsekvenser for ytelse")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);

        // Verifisering av uttak
        var UttaksPerioderForSøker = saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker();
        assertThat(UttaksPerioderForSøker)
                .as("Antall uttaksperioder for søker")
                .hasSize(7);
        for (UttakResultatPeriode periode : UttaksPerioderForSøker) {
            assertThat(periode.getAktiviteter())
                    .as("Antall aktiviteter for hver uttaksperiode")
                    .hasSize(1);
        }
        assertThat(UttaksPerioderForSøker.get(4).getUtsettelseType())
                .as("Uttaksutsettelesårsak")
                .isEqualTo(UttakresultatUtsettelseÅrsak.ARBEID);

        // Verifisering tilkjent ytelse
        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        assertThat(tilkjentYtelsePerioder.getPerioder())
                .as("Antall perioder i tilkjent ytelse")
                .hasSize(7);
        assertThat(tilkjentYtelsePerioder.getPerioder().get(0).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isPositive();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(1).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isPositive();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isPositive();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isPositive();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(4).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(5).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isPositive();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(6).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isPositive();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();
    }

    @Test
    @DisplayName("Mor endringssøknad med aksjonspunkt i uttak")
    @Description("Mor endringssøknad med aksjonspunkt i uttak. Søker for lang mødrekvote for å få aksjonspunkt." +
            "Saksbehandler avslår. Mor har også arbeid med arbeidsforholdId i inntektsmelding")
    void endringssøknad_med_aksjonspunkt_i_uttak() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.of(2017, 1, 1))
                                .build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.of(2018, 1, 1))
                                .build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.of(2019, 11, 1))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = fordeling(
                uttaksperiode(KontoType.FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        var søknad = SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fpStartdato.minusWeeks(3));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var fordelingEndring = fordeling(
                uttaksperiode(KontoType.MØDREKVOTE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(30).minusDays(1))
        );
        var søknadE = lagEndringssøknad(søknad.build(), saksnummer, fordelingEndring)
                .medMottattdato(fødselsdato.plusWeeks(5));
        var saksnummerE = mor.søk(søknadE);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new FastsettUttaksperioderManueltBekreftelse())
                .avslåManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        // Behandle totrinnskontroll
        beslutter.hentFagsak(saksnummerE);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.konsekvenserForYtelsen())
                .as("Konsekvenser for ytelse")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);

        var UttaksPerioderForSøker = saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker();
        assertThat(UttaksPerioderForSøker)
                .as("Antall uttaksperioder")
                .hasSize(4);
        assertThat(UttaksPerioderForSøker.get(0).getPeriodeResultatType())
                .as("Perioderesultatstype for periode 0")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(UttaksPerioderForSøker.get(1).getPeriodeResultatType())
                .as("Perioderesultatstype for periode 1")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(UttaksPerioderForSøker.get(2).getPeriodeResultatType())
                .as("Perioderesultatstype for periode 2")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(UttaksPerioderForSøker.get(3).getPeriodeResultatType())
                .as("Perioderesultatstype for periode 3")
                .isEqualTo(PeriodeResultatType.AVSLÅTT);
    }

    @Test
    @DisplayName("Utsettelser og gradering fra førstegangsbehandling skal ikke gå til manuell behandling")
    @Description("Utsettelser og gradering fra førstegangsbehandling skal ikke gå til manuell behandling hvis innenfor søknadsfrist." +
            "Førstegangsbehandling avslutter med utsettelserList. Søker sender inn endringssøknad hvor en tar ut 1 uke etter utsettelsen.")
    void utsettelser_og_gradering_fra_førstegangsbehandling_skal_ikke_gå_til_manuell_behandling_ved_endringssøknad() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.of(2017, 1, 1))
                                .build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.of(2018, 1, 1))
                                .build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.of(2019, 11, 1))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var arbeidsgiver = mor.arbeidsgiver();
        var arbeidsgiverIdentifikasjon = arbeidsgiver.arbeidsgiverIdentifikator();
        var fordeling = fordeling(
                uttaksperiode(KontoType.FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)),
                graderingsperiodeArbeidstaker(KontoType.MØDREKVOTE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(12).minusDays(1),
                        arbeidsgiverIdentifikasjon, 50),
                utsettelsesperiode(UtsettelsesÅrsak.ARBEID, fødselsdato.plusWeeks(12), fødselsdato.plusWeeks(15))
        );

        var søknad = SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fødselsdato);
        var saksnummer = mor.søk(søknad);
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .isEmpty();

        var utsettelseStart = fødselsdato.plusWeeks(15);
        var fordelingEndringssøknad = fordeling(
                uttaksperiode(KontoType.FELLESPERIODE, utsettelseStart, utsettelseStart.plusWeeks(1).minusDays(1))
        );
        var søknadE = lagEndringssøknad(søknad.build(), saksnummer, fordelingEndringssøknad)
                .medMottattdato(utsettelseStart.minusWeeks(3));
        mor.søk(søknadE);

        saksbehandler.ventPåOgVelgRevurderingBehandling(RE_ENDRING_FRA_BRUKER);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .isEmpty();

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering(RE_OPPLYSNINGER_OM_FORDELING);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .isEmpty();
    }
}



