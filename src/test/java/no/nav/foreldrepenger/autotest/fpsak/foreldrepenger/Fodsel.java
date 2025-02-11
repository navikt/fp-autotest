package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.MØDREKVOTE;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner.NAV_OSLO;
import static no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner.NAV_STORD;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordelingFarAleneomsorg;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordelingMorAleneomsorgHappyCase;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.generator.inntektsmelding.builders.Prosent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakresultatUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAleneomsorgBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAnnenForeldreHarRett;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderUttakDokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadEndringForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatMedUttaksplan;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.UttaksperiodeType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.AnnenforelderBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.BarnBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.SøkerBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.OpptjeningMaler;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PrivatArbeidsgiver;
import no.nav.foreldrepenger.vtp.kontrakter.v2.SivilstandDto;

@Tag("fpsak")
@Tag("foreldrepenger")
class Fodsel extends FpsakTestBase {

    private static final Logger logger = LoggerFactory.getLogger(Fodsel.class);

    @Test
    @DisplayName("Mor fødsel med arbeidsforhold og frilans. Vurderer opptjening og beregning. Finner avvik")
    @Description("Mor søker fødsel med ett arbeidsforhold og frilans. Vurder opptjening. Vurder fakta om beregning. Avvik i beregning")
    void morSøkerFødselMedEttArbeidsforholdOgFrilans_VurderOpptjening_VurderFaktaOmBeregning_AvvikIBeregning() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(NAV_OSLO, 75, LocalDate.now().minusYears(1), 480_000)
                                .frilans(NAV_STORD, "arb", 25, LocalDate.now().minusYears(3), LocalDate.now().minusMonths(1), 120_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now())
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medSøker(new SøkerBuilder(BrukerRolle.MOR).medFrilansInformasjon(OpptjeningMaler.frilansOpptjening()).build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var overstyrtInntekt = 500_000;
        var overstyrtFrilanserInntekt = 500_000;
        var refusjon = BigDecimal.valueOf(overstyrtInntekt / 12);
        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medBeregnetInntekt(Prosent.valueOf(50))
                .medRefusjonBeløpPerMnd(refusjon);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.VEDLEGG_MOTTATT);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        // Verifiser Beregningsgrunnlag
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus())
                .as("Antall aktivitetstatus")
                .isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0))
                .as("Aktivitetsstatus i beregnignsgrunnlag")
                .isEqualTo(AktivitetStatus.KOMBINERT_AT_FL);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto())
                .as("Antall beregningsgrunnlagsparioder")
                .isEqualTo(1);
        var andeler = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        assertThat(andeler)
                .as("Antall andeler")
                .hasSize(2);
        assertThat(andeler.get(0).getAktivitetStatus())
                .as("Aktivitetsstatus")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        assertThat(andeler.get(1).getAktivitetStatus())
                .as("Aktivitetsstatus")
                .isEqualTo(AktivitetStatus.FRILANSER);

        assertThat(saksbehandler.harAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS))
                .as("Har aksjonspunkt FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS")
                .isTrue();

        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderBeregnetInntektsAvvikBekreftelse())
                .leggTilInntektFrilans(overstyrtFrilanserInntekt)
                .leggTilInntekt(overstyrtInntekt, 2)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(2, beslutter.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(beslutter.valgtBehandling.getBeregningResultatForeldrepenger(), true);

    }


    @Test
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold i samme organisasjon og avvik i beregning")
    void morSøkerFødselMedToArbeidsforhold_AvvikIBeregning() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", 40, LocalDate.now().minusYears(2), 490_000)
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-002", 60, LocalDate.now().minusYears(3), null)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusDays(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var inntektPrMåned = 50_000;
        var overstyrtInntekt = inntektPrMåned * 12;
        var refusjon = BigDecimal.valueOf(inntektPrMåned);
        var arbeidsgiver = mor.arbeidsgiver();
        List<InntektsmeldingBuilder> inntektsmeldinger = arbeidsgiver.lagInntektsmeldingerFP(fpStartdato);
        inntektsmeldinger.get(0)
                .medBeregnetInntekt(BigDecimal.valueOf(inntektPrMåned))
                .medRefusjonBeløpPerMnd(refusjon);
        inntektsmeldinger.get(1)
                .medBeregnetInntekt(BigDecimal.valueOf(inntektPrMåned))
                .medRefusjonBeløpPerMnd(refusjon);

        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmeldinger.getFirst());
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmeldinger.get(1));

        saksbehandler.hentFagsak(saksnummer);
        debugLoggBehandling(saksbehandler.valgtBehandling);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderBeregnetInntektsAvvikBekreftelse())
                .leggTilInntekt(overstyrtInntekt, 1)
                .leggTilInntekt(overstyrtInntekt, 2)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(2, beslutter.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(beslutter.valgtBehandling.getBeregningResultatForeldrepenger(), true);

    }

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold og avvik i beregning")
    void morSøkerFødselMedEttArbeidsforhold_AvvikIBeregning() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(8))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var inntektPrMåned = 15_000;
        var refusjon = BigDecimal.valueOf(inntektPrMåned);
        var overstyrtInntekt = inntektPrMåned * 12;
        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medBeregnetInntekt(15_000)
                .medRefusjonBeløpPerMnd(refusjon);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderBeregnetInntektsAvvikBekreftelse())
                .leggTilInntekt(overstyrtInntekt, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(1, beslutter.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(beslutter.valgtBehandling.getBeregningResultatForeldrepenger(), true);
    }

    @Test
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold i samme organisasjon")
    void morSøkerFødselMedToArbeidsforholdISammeOrganisasjon() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", 40, LocalDate.now().minusYears(2), 490_000)
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-002", 60, LocalDate.now().minusYears(3), null)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusDays(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        mor.arbeidsgivere().sendDefaultInntektsmeldingerFP(saksnummer, fpStartdato);

        debugLoggBehandling(saksbehandler.valgtBehandling);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        debugLoggBehandling(saksbehandler.valgtBehandling);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(2, saksbehandler.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger(), false);
    }

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold")
    void morSøkerFødselMedEttArbeidsforhold() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(8))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        debugLoggBehandling(saksbehandler.valgtBehandling);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(1, saksbehandler.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger(), false);
    }

    @Test
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold med inntekt over 6G")
    void morSøkerFødselMedToArbeidsforhold() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidMedOpptjeningUnder6G()
                                .arbeidMedOpptjeningUnder6G()
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusDays(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgivere = mor.arbeidsgivere();
        arbeidsgivere.sendDefaultInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(2, saksbehandler.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger(), false);
    }

    @Test
    @DisplayName("Far søker fødsel med 1 arbeidsforhold")
    void farSøkerFødselMedEttArbeidsforhold() {
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(mor().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();
        var fordeling = fordeling(
                uttaksperiode(FEDREKVOTE, fødselsdato, fødselsdato.plusWeeks(2).minusDays(1), UttaksperiodeType.SAMTIDIGUTTAK),
                uttaksperiode(FEDREKVOTE, fødselsdato.plusWeeks(30), fødselsdato.plusWeeks(43).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(43), fødselsdato.plusWeeks(45).minusDays(1), MorsAktivitet.ARBEID)
        );
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = far.søk(søknad.build());
        var arbeidsgiver = far.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fødselsdato);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAnnenForeldreHarRett = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAnnenForeldreHarRett())
                .setAnnenforelderHarRett(true)
                .setBegrunnelse("Mor har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new VurderUttakDokumentasjonBekreftelse());
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        var stonadskontoer = saksbehandler.valgtBehandling.getSaldoer().stonadskontoer();
        assertThat(stonadskontoer.get(SaldoVisningStønadskontoType.FEDREKVOTE).saldo())
                .as("Saldoen for stønadskonton FORELDREPENGER")
                .isZero();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Forventer ingen avslåtte peridoer")
                .isEmpty();
        beslutter.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold i samme organisasjon med 1 inntektsmelding")
    void morSøkerFødselMedToArbeidsforholdISammeOrganisasjonEnInntektsmelding() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NAV, 50, LocalDate.now().minusYears(2), null)
                                .arbeidsforhold(TestOrganisasjoner.NAV, 50, LocalDate.now().minusYears(3), 540_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusDays(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medBeregnetInntekt(BigDecimal.valueOf(mor.månedsinntekt()))
                .medArbeidsforholdId(null);

        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(1, saksbehandler.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger(), false);

    }

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold, Papirsøkand")
    void morSøkerFødselMedEttArbeidsforhold_papirsøknad() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var saksnummer = mor.søkPapirsøknadForeldrepenger();

        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        var aksjonspunktBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new PapirSoknadForeldrepengerBekreftelse());
        var fordeling = new FordelingDto();
        var fpff = new PermisjonPeriodeDto(FORELDREPENGER_FØR_FØDSEL,
                fpStartdato, fødselsdato.minusDays(1));
        var mødrekvote = new PermisjonPeriodeDto(MØDREKVOTE,
                fødselsdato, fødselsdato.plusWeeks(10));
        fordeling.permisjonsPerioder.add(fpff);
        fordeling.permisjonsPerioder.add(mødrekvote);
        aksjonspunktBekreftelse.morSøkerFødsel(fordeling, fødselsdato, fpff.periodeFom.minusWeeks(3));

        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelse);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // verifiserer uttak
        var perioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(perioder).hasSize(3);
        verifiserUttaksperiode(perioder.get(0), FORELDREPENGER_FØR_FØDSEL, 1);
        verifiserUttaksperiode(perioder.get(1), MØDREKVOTE, 1);
        verifiserUttaksperiode(perioder.get(2), MØDREKVOTE, 1);

        // Endringssøknad
        mor.sendInnPapirsøknadEEndringForeldrepenger();
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var aksjonspunktBekreftelseEndringssøknad = saksbehandler
                .hentAksjonspunktbekreftelse(new PapirSoknadEndringForeldrepengerBekreftelse());
        var fordelingEndringssøknad = new FordelingDto();
        // Legger til fellesperiode på slutten
        var fellesperiode = new PermisjonPeriodeDto(FELLESPERIODE,
                fødselsdato.plusWeeks(10).plusDays(1), fødselsdato.plusWeeks(15));
        fordelingEndringssøknad.permisjonsPerioder.add(fellesperiode);
        aksjonspunktBekreftelseEndringssøknad.setFordeling(fordelingEndringssøknad);
        aksjonspunktBekreftelseEndringssøknad.setAnnenForelderInformert(true);
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelseEndringssøknad);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);

        // verifiserer uttak
        var perioderEtterEndringssøknad = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(perioderEtterEndringssøknad).hasSize(4);
        verifiserUttaksperiode(perioderEtterEndringssøknad.get(0), FORELDREPENGER_FØR_FØDSEL, 1);
        verifiserUttaksperiode(perioderEtterEndringssøknad.get(1), MØDREKVOTE, 1);
        verifiserUttaksperiode(perioderEtterEndringssøknad.get(2), MØDREKVOTE, 1);
        verifiserUttaksperiode(perioderEtterEndringssøknad.get(3), FELLESPERIODE, 1);
    }

    @Test
    @Description("Mor søker fødsel med 2 arbeidsforhold med arbeidsforhold som ikke matcher på ID")
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold med arbeidsforhold som ikke matcher på ID")
    void morSøkerFødselMed2ArbeidsforholdArbeidsforholdIdMatcherIkke() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidMedOpptjeningUnder6G()
                                .arbeidMedOpptjeningUnder6G()
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusDays(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var inntektPerMåned = 20_000;
        var arbeidsgivere = mor.arbeidsgivere();
        var arbeidsgiver1 = arbeidsgivere.toList().get(0);
        var inntektsmelding1 = arbeidsgiver1.lagInntektsmeldingFP(startDatoForeldrepenger)
                .medBeregnetInntekt(inntektPerMåned)
                .medArbeidsforholdId("1");
        arbeidsgiver1.sendInntektsmelding(saksnummer, inntektsmelding1);
        var arbeidsgiver2 = arbeidsgivere.toList().get(1);
        var inntektsmelding2 = arbeidsgiver2.lagInntektsmeldingFP(startDatoForeldrepenger)
                .medBeregnetInntekt(inntektPerMåned)
                .medArbeidsforholdId("9");
        arbeidsgiver2.sendInntektsmelding(saksnummer, inntektsmelding2);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Do nothing
        }


        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandling satt på vent")
                .isTrue();
        debugLoggBehandling(saksbehandler.valgtBehandling);

    }

    @Test
    @Description("Mor søker fødsel med privatperson som arbeidsgiver")
    @DisplayName("Mor søker fødsel med privatperson som arbeidsgiver, avvik i beregning")
    void morSøkerFødselMedPrivatpersonSomArbeidsgiver() {
        var uuidArbeidsgiver = UUID.randomUUID();
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                    .inntektytelse(InntektYtelseGenerator.ny()
                        .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-000", 0, LocalDate.now().minusMonths(12), LocalDate.now().minusMonths(11), 360_000)
                        .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-001", 0, LocalDate.now().minusMonths(10), LocalDate.now().minusMonths(9), 360_000)
                        .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-002", 0, LocalDate.now().minusMonths(7), LocalDate.now().minusMonths(6), 360_000)
                        .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-003", 0, LocalDate.now().minusMonths(4), LocalDate.now().minusMonths(3), 360_000)
                        .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-004", 0, LocalDate.now().minusMonths(3), LocalDate.now().minusMonths(2), 360_000)
                        .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-005", 0, LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1), 360_000)
                        .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-006", 0, LocalDate.now().minusMonths(1), 360_000)
                        .build())
                    .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusDays(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var inntektPerMaaned = 10_000;
        var overstyrtInntekt = 250_000;
        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(startDatoForeldrepenger)
                .medBeregnetInntekt(inntektPerMaaned);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);
        debugLoggBehandling(saksbehandler.valgtBehandling);

        // Bekreft Opptjening
        var vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderPerioderOpptjeningBekreftelse())
                .godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // Verifiser Beregningsgrunnlag
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus())
                .as("Antall aktivitetsstatus i beregningsgrunnlag")
                .isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0))
                .as("Aktivitetstatus")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto())
                .as("Antall beregningsgrunnlagperioder i beregnignsgrunnlag")
                .isEqualTo(1);
        List<BeregningsgrunnlagPrStatusOgAndelDto> andeler = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        assertThat(andeler)
                .as("Andeler")
                .hasSize(1);
        assertThat(andeler.get(0).getAktivitetStatus())
                .as("Aktivitetsstatus i andel")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);

        // Bekreft inntekt i beregning
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderBeregnetInntektsAvvikBekreftelse())
                .leggTilInntekt(overstyrtInntekt, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        // Foreslå vedtak
        var foreslåVedtakBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new ForeslåVedtakBekreftelse());
        saksbehandler.bekreftAksjonspunkt(foreslåVedtakBekreftelse);

        // Totrinnskontroll
        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(1, beslutter.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(beslutter.valgtBehandling.getBeregningResultatForeldrepenger(), false);
    }

    @Test
    @Description("Mor søker fødsel med privatperson som arbeidsgiver med endring i refusjon")
    @DisplayName("Mor søker fødsel med privatperson som arbeidsgiver med endring i refusjon, avvik i beregning")
    void morSøkerFødselMedPrivatpersonSomArbeidsgiverMedEndringIRefusjon() {
        var uuidArbeidsgiver = UUID.randomUUID();
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-000", 0, LocalDate.now().minusMonths(12), LocalDate.now().minusMonths(11), 360_000)
                                .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-001", 0, LocalDate.now().minusMonths(10), LocalDate.now().minusMonths(9), 360_000)
                                .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-002", 0, LocalDate.now().minusMonths(7), LocalDate.now().minusMonths(6), 360_000)
                                .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-003", 0, LocalDate.now().minusMonths(4), LocalDate.now().minusMonths(3), 360_000)
                                .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-004", 0, LocalDate.now().minusMonths(3), LocalDate.now().minusMonths(2), 360_000)
                                .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-005", 0, LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1), 360_000)
                                .arbeidsforhold(new PrivatArbeidsgiver(uuidArbeidsgiver), "ARB001-006", 0, LocalDate.now().minusMonths(1), 360_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusDays(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        // Send inn inntektsmelding
        var overstyrtInntekt = 250_000;
        var inntektPerMaaned = 10_000;
        var refusjon = 25_000;
        var endret_refusjon = 10_000;
        var endringsdato = fødselsdato.plusMonths(1);
        HashMap<LocalDate, BigDecimal> endringRefusjonMap = new HashMap<>();
        endringRefusjonMap.put(endringsdato, BigDecimal.valueOf(endret_refusjon));
        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(startDatoForeldrepenger)
                .medBeregnetInntekt(inntektPerMaaned)
                .medRefusjonBeløpPerMnd(BigDecimal.valueOf(refusjon))
                .medEndringIRefusjonslist(endringRefusjonMap);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);
        debugLoggBehandling(saksbehandler.valgtBehandling);

        // Bekreft Opptjening
        var vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderPerioderOpptjeningBekreftelse())
                .godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // Verifiser Beregningsgrunnlag
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus())
                .as("Antall aktivitetstatuser i beregnignsgrunnlag")
                .isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0))
                .as("Aktivitetsstatus i beregnignsgrunnlag")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto())
                .as("Antall beregningsgrunnlagperioder i beregningsgrunnlag")
                .isEqualTo(1);
        var andelerFørstePeriode = saksbehandler.valgtBehandling
                .getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        assertThat(andelerFørstePeriode)
                .as("Andeler i første periode")
                .hasSize(1);
        assertThat(andelerFørstePeriode.get(0).getAktivitetStatus())
                .as("Aktivitetsstatus i andel i første periode")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);

        // Bekreft inntekt i beregning
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderBeregnetInntektsAvvikBekreftelse())
                .leggTilInntekt(overstyrtInntekt, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.FORESLÅ_VEDTAK);

        // Verifiser Beregningsgrunnlag
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus())
                .as("Antall aktivitetsstatus i beregningsgrunnlag")
                .isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0))
                .as("Aktivitetstatus")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto())
                .as("Antall beregningsgrunnlagperioder i beregnignsgrunnlag")
                .isEqualTo(2);
        andelerFørstePeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        assertThat(andelerFørstePeriode)
                .as("Andeler")
                .hasSize(1);
        assertThat(andelerFørstePeriode.get(0).getAktivitetStatus())
                .as("Aktivitetsstatus i andel")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);

        // Assert refusjon
        var resultatPerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger().getPerioder();
        assertThat(resultatPerioder).hasSize(5);
        var forventetDagsats = BigDecimal.valueOf(overstyrtInntekt).divide(BigDecimal.valueOf(260),
                RoundingMode.HALF_EVEN);
        assertThat(resultatPerioder.get(0).getAndeler().get(0).getRefusjon()).isEqualTo(forventetDagsats.intValue());
        assertThat(resultatPerioder.get(1).getAndeler().get(0).getRefusjon()).isEqualTo(forventetDagsats.intValue());
        var forventetRefusjon = BigDecimal.valueOf(endret_refusjon * 12).divide(BigDecimal.valueOf(260),
                RoundingMode.HALF_EVEN);
        assertThat(resultatPerioder.get(2).getAndeler().get(0).getRefusjon()).isEqualTo(forventetRefusjon.intValue());
        assertThat(resultatPerioder.get(3).getAndeler().get(0).getRefusjon()).isEqualTo(forventetRefusjon.intValue());

        // Foreslå vedtak
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        // Totrinnskontroll
        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(1, beslutter.valgtBehandling.hentUttaksperioder());
    }

    @Test
    @DisplayName("Far søker fødsel med aleneomsorg som bekreftes at han har (mor forsvunnet)")
    void farSøkerFødselAleneomsorgMenErGiftOgBorMedAnnenpart() {
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(mor().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medSøker(new SøkerBuilder(BrukerRolle.FAR).medErAleneOmOmsorg(true).build())
                .medFordeling(fordelingFarAleneomsorg(fødselsdato))
                .medAnnenForelder(AnnenforelderMaler.norskIkkeRett(familie.mor()));
        var saksnummer = far.søk(søknad.build());
        var arbeidsgiver = far.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fødselsdato);

        // Saksbehandler må avklare aleneomsorgen fordi far er gift og bor på sammested med ektefelle
        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAleneomsorgBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAleneomsorgBekreftelse())
                .bekreftBrukerHarAleneomsorg();
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAleneomsorgBekreftelse);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // verifiserer uttak
        var uttaksperioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttaksperioder).hasSize(2);
        var foreldrepengerFørste6Ukene = uttaksperioder.get(0);
        assertThat(foreldrepengerFørste6Ukene.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);
        var foreldrepengerEtterUke6 = uttaksperioder.get(1);
        assertThat(foreldrepengerEtterUke6.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);

        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer())
                .as("Stonadskontoer i Saldo")
                .hasSize(1);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER))
                .isNotNull();
    }

    @Test
    @DisplayName("Mor søker fødsel har stillingsprosent 0")
    @Description("Mor søker fødsel har stillingsprosent 0 som fører til aksjonspunkt for opptjening")
    void morSøkerFødselStillingsprosent0() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(0, LocalDate.now().minusMonths(12), 360_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusDays(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        var vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderPerioderOpptjeningBekreftelse())
                .godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter
                .hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse())
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker gradering. Med to arbeidsforhold. Uten avvikende inntektsmelding")
    @Description("Mor, med to arbeidsforhold, søker gradering. Samsvar med IM.")
    void morSøkerGraderingOgUtsettelseMedToArbeidsforhold_utenAvvikendeInntektsmeldinger() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidMedOpptjeningUnder6G()
                                .arbeidMedOpptjeningUnder6G()
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusDays(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var arbeidsgivere = mor.arbeidsgivere();
        var gradertArbeidsgiverIdentifikator = arbeidsgivere.toList().get(0).arbeidsgiverIdentifikator();
        var graderingFom = fødselsdato.plusWeeks(10).plusDays(1);
        var graderingTom = fødselsdato.plusWeeks(12);
        var arbeidstidsprosent = BigDecimal.TEN;
        var fordeling = fordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)),
                graderingsperiodeArbeidstaker(FELLESPERIODE,graderingFom, graderingTom, gradertArbeidsgiverIdentifikator, arbeidstidsprosent.intValue()));
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        arbeidsgivere.sendDefaultInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        debugLoggBehandling(saksbehandler.valgtBehandling);
        // hackForÅKommeForbiØkonomi(saksnummer);

        // verifiserer uttak
        var uttaksperioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttaksperioder).hasSize(4);
        for (UttakResultatPeriode periode : uttaksperioder) {
            assertThat(periode.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
            assertThat(periode.getPeriodeResultatÅrsak().getKode()).isNotEqualTo(PeriodeResultatÅrsak.UKJENT.getKode());
            assertThat(periode.getAktiviteter()).hasSize(2);
            for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
                assertThat(aktivitet.getArbeidsgiverReferanse()).isNotNull();
                assertThat(aktivitet.getUttakArbeidType()).isEqualTo("ORDINÆRT_ARBEID");
                var arbeidsforholdFraScenario = mor.arbeidsforholdene();
                assertThat(aktivitet.getArbeidsgiverReferanse()).isIn(
                        arbeidsforholdFraScenario.get(0).arbeidsgiverIdentifikasjon().value(),
                        arbeidsforholdFraScenario.get(1).arbeidsgiverIdentifikasjon().value());
            }
        }
        var fpff = uttaksperioder.get(0);
        assertThat(fpff.getAktiviteter().get(0).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(fpff.getAktiviteter().get(0).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(fpff.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER_FØR_FØDSEL);
        assertThat(fpff.getAktiviteter().get(1).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(fpff.getAktiviteter().get(1).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(fpff.getAktiviteter().get(1).getStønadskontoType()).isEqualTo(FORELDREPENGER_FØR_FØDSEL);
        var mødrekvoteFørste6Ukene = uttaksperioder.get(1);
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(MØDREKVOTE);
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(1).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(1).getTrekkdagerDesimaler())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(1).getStønadskontoType()).isEqualTo(MØDREKVOTE);
        var mødrekvoteEtterUke6 = uttaksperioder.get(2);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(0).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(MØDREKVOTE);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(1).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(1).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(1).getStønadskontoType()).isEqualTo(MØDREKVOTE);
        var gradering = uttaksperioder.get(3);
        assertThat(gradering.getGraderingAvslagÅrsak()).isEqualTo("-");
        assertThat(gradering.getGraderingInnvilget()).isTrue();
        assertThat(gradering.getGradertArbeidsprosent()).isEqualTo(arbeidstidsprosent);
        assertThat(gradering.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FELLESPERIODE);
        assertThat(gradering.getAktiviteter().get(1).getStønadskontoType()).isEqualTo(FELLESPERIODE);
        UttakResultatPeriodeAktivitet gradertAktivitet = finnAktivitetForArbeidsgiver(gradering, gradertArbeidsgiverIdentifikator);
        assertThat(gradertAktivitet.getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(gradertAktivitet.getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100).subtract(arbeidstidsprosent));
        assertThat(gradertAktivitet.getProsentArbeid()).isEqualTo(arbeidstidsprosent);

        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer())
                .as("Antall stønadskonter i saldo")
                .hasSize(4);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldo igjen på FORELDREPENGER_FØR_FØDSEL")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo())
                .as("Saldo igjen på FELLESPERIODE")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo())
                .as("Saldo igjen på MØDREKVOTE")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker fødsel med aleneomsorg")
    @Description("Mor søker fødsel aleneomsorg. Annen forelder ikke kjent.")
    void morSøkerFødselAleneomsorgKunEnHarRett() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .sivilstand(List.of(new SivilstandDto(SivilstandDto.Sivilstander.UGIF, null, null)))
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidMedOpptjeningUnder6G()
                                .build())
                        .build())
                .forelder(far().build())
                .barn(LocalDate.now().minusDays(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medSøker(new SøkerBuilder(BrukerRolle.MOR).medErAleneOmOmsorg(true).build())
                .medFordeling(fordelingMorAleneomsorgHappyCase(fødselsdato))
                .medAnnenForelder(AnnenforelderBuilder.ukjentForelder());
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        // saksbehandler.ventTilØkonomioppdragFerdigstilles();

        // verifiserer uttak
        var uttaksperioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttaksperioder).hasSize(4);

        var fpff = uttaksperioder.get(0);
        assertThat(fpff.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(fpff.getAktiviteter().get(0).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(fpff.getAktiviteter().get(0).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(fpff.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER_FØR_FØDSEL);
        var foreldrepengerFørste6Ukene = uttaksperioder.get(1);
        assertThat(foreldrepengerFørste6Ukene.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);
        var foreldrepengerEtterUke6 = uttaksperioder.get(2);
        assertThat(foreldrepengerFørste6Ukene.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isEqualByComparingTo(BigDecimal.valueOf(200));
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);
        // Periode søkt mer enn 49 uker er avslått automatisk
        var periodeMerEnn49Uker = uttaksperioder.get(3);
        assertThat(periodeMerEnn49Uker.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.AVSLÅTT);
        assertThat(periodeMerEnn49Uker.getPeriodeResultatÅrsak()).isEqualTo(PeriodeResultatÅrsak.IKKE_STØNADSDAGER_IGJEN);
        assertThat(periodeMerEnn49Uker.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(0));
        assertThat(periodeMerEnn49Uker.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(periodeMerEnn49Uker.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);

        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer())
                .as("Antall stønadskonter i saldo")
                .hasSize(2);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldo igjen på FORELDREPENGER_FØR_FØDSEL")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER).saldo())
                .as("Saldo igjen på FELLESPERIODE")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker fødsel for 2 barn med 1 barn registrert")
    @Description("Mor søker fødsel for 2 barn med 1 barn registrert. dette fører til aksjonspunkt for bekreftelse av antall barn")
    void morSøker2Barn1Registrert() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medBarn(BarnBuilder.fødsel(2, fødselsdato).build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);

        var fodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderManglendeFodselBekreftelse())
                .bekreftDokumentasjonForeligger(2, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(fodselBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse())
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker uregistrert fødsel før det har gått 1 uke")
    @Description("Mor søker uregistrert fødsel før det har gått 1 uke - skal sette behandling på vent")
    void morSøkerUregistrertEtterFør2Uker() {
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
        var fødselsdato = LocalDate.now().minusDays(5);
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medBarn(BarnBuilder.fødsel(2, fødselsdato).build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandling er satt på vent (Har ventet til 2. uke)")
                .isTrue();
        logger.debug("{}", saksbehandler.valgtBehandling.fristBehandlingPaaVent);
        logger.debug("{}", fødselsdato.plusWeeks(2));
        assertThat(saksbehandler.valgtBehandling.fristBehandlingPaaVent)
                .as("Frist behandling på vent")
                .isEqualTo(fødselsdato.plusWeeks(1).plusDays(1));
    }

    private UttakResultatPeriodeAktivitet finnAktivitetForArbeidsgiver(UttakResultatPeriode uttakResultatPeriode,
            ArbeidsgiverIdentifikator identifikator) {
        return uttakResultatPeriode.getAktiviteter().stream()
                .filter(a -> a.getArbeidsgiverReferanse().equalsIgnoreCase(identifikator.value())).findFirst().orElseThrow();
    }

    // TODO må ta inn fordeling som blir laget i søknad for å kunne verifisere rikitg på hva som er i VL!
    //  Denne verifisering kan bare brukes hvis fordeling = fordelingMorHappyCaseLong
    @Step("Verifiserer utttaksperioder")
    private void verifiserUttak(int antallAktiviteter, List<UttakResultatPeriode> perioder) {
        assertThat(perioder).hasSize(4);
        verifiserUttaksperiode(perioder.get(0), FORELDREPENGER_FØR_FØDSEL, antallAktiviteter);
        verifiserUttaksperiode(perioder.get(1), MØDREKVOTE, antallAktiviteter);
        verifiserUttaksperiode(perioder.get(2), MØDREKVOTE, antallAktiviteter);
        verifiserUttaksperiode(perioder.get(3), FELLESPERIODE, antallAktiviteter);
    }

    private void verifiserUttaksperiode(UttakResultatPeriode uttakResultatPeriode, StønadskontoType stønadskonto,
            int antallAktiviteter) {
        assertThat(uttakResultatPeriode.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttakResultatPeriode.getUtsettelseType()).isEqualTo(UttakresultatUtsettelseÅrsak.UDEFINERT);
        assertThat(uttakResultatPeriode.getAktiviteter()).hasSize(antallAktiviteter);
        for (UttakResultatPeriodeAktivitet aktivitet : uttakResultatPeriode.getAktiviteter()) {
            assertThat(aktivitet.getStønadskontoType()).isEqualTo(stønadskonto);
            assertThat(aktivitet.getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
            assertThat(aktivitet.getUtbetalingsgrad()).isGreaterThan(BigDecimal.ZERO);
        }
    }

    @Step("Verifiserer tilkjent ytelse")
    private void verifiserTilkjentYtelse(BeregningsresultatMedUttaksplan beregningResultatForeldrepenger,
            boolean medFullRefusjon) {
        var perioder = beregningResultatForeldrepenger.getPerioder();
        assertThat(perioder).isNotEmpty();
        for (var periode : perioder) {
            assertThat(periode.getDagsats()).isPositive();
            var andeler = periode.getAndeler();
            for (var andel : andeler) {
                var kode = andel.getAktivitetStatus();
                if (kode.equals(AktivitetStatus.ARBEIDSTAKER)) {
                    if (medFullRefusjon) {
                        assertThat(andel.getTilSoker()).isZero();
                        assertThat(andel.getRefusjon()).isPositive();
                    } else {
                        assertThat(andel.getTilSoker()).isPositive();
                        assertThat(andel.getRefusjon()).isZero();
                    }
                } else if (kode.equals(AktivitetStatus.FRILANSER) || kode.equals(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)) {
                    assertThat(andel.getTilSoker()).isPositive();
                    assertThat(andel.getRefusjon()).isZero();
                }
                assertThat(andel.getUttak().isGradering()).isFalse();
                assertThat(andel.getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
            }
        }
    }
}
