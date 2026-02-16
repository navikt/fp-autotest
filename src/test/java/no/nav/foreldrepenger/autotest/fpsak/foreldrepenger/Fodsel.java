package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
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
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.MØDREKVOTE;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakresultatUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAleneomsorgBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAnnenForeldreHarRett;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.SjekkManglendeFødselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSøknadEndringForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSøknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatMedUttaksplan;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Prosent;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.OpptjeningMaler;
import no.nav.foreldrepenger.generator.soknad.maler.UttaksperiodeType;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.MorsAktivitet;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.soknad.kontrakt.builder.BarnBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PrivatArbeidsgiver;
import no.nav.foreldrepenger.vtp.kontrakter.v2.SivilstandDto;

@Tag("fpsak")
@Tag("foreldrepenger")
class Fodsel extends VerdikjedeTestBase {

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
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medFrilansInformasjon(OpptjeningMaler.frilansOpptjening())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var overstyrtInntekt = 500_000;
        var overstyrtFrilanserInntekt = 500_000;
        var refusjon = BigDecimal.valueOf(overstyrtInntekt / 12);
        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medBeregnetInntekt(Prosent.valueOf(50))
                .medRefusjonBeløpPerMnd(refusjon);
        ventPåInntektsmeldingForespørsel(saksnummer);
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
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var inntektPrMåned = 50_000;
        var overstyrtInntekt = inntektPrMåned * 12;
        var refusjon = BigDecimal.valueOf(inntektPrMåned);
        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmeldinger = arbeidsgiver.lagInntektsmeldingerFP(fpStartdato, true);
        inntektsmeldinger.getFirst()
                .medBeregnetInntekt(BigDecimal.valueOf(inntektPrMåned - 20_000))
                .medRefusjonBeløpPerMnd(refusjon);

        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmeldinger.getFirst());

        saksbehandler.hentFagsak(saksnummer);
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
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold og avvik i beregning")
    void morSøkerFødselMedEttArbeidsforhold_AvvikIBeregning() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(8))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var inntektPrMåned = 15_000;
        var refusjon = BigDecimal.valueOf(inntektPrMåned);
        var overstyrtInntekt = inntektPrMåned * 12;
        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medBeregnetInntekt(15_000)
                .medRefusjonBeløpPerMnd(refusjon);
        ventPåInntektsmeldingForespørsel(saksnummer);
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
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingerFP(fpStartdato, true).getFirst();

        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        debugLoggBehandling(saksbehandler.valgtBehandling);

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
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold")
    void morSøkerFødselMedEttArbeidsforhold() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(8))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
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
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgivere = mor.arbeidsgivere();
        ventPåInntektsmeldingForespørsel(saksnummer);
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
                .build();
        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();
        var fordeling = fordeling(
                uttaksperiode(KontoType.FEDREKVOTE, fødselsdato, fødselsdato.plusWeeks(2).minusDays(1), UttaksperiodeType.SAMTIDIGUTTAK),
                uttaksperiode(KontoType.FEDREKVOTE, fødselsdato.plusWeeks(30), fødselsdato.plusWeeks(43).minusDays(1)),
                uttaksperiode(KontoType.FELLESPERIODE, fødselsdato.plusWeeks(43), fødselsdato.plusWeeks(45).minusDays(1), MorsAktivitet.ARBEID)
        );
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = far.søk(søknad);
        var arbeidsgiver = far.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fødselsdato);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAnnenForeldreHarRett = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAnnenForeldreHarRett())
                .setAnnenforelderHarRett(true)
                .setBegrunnelse("Mor har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var stonadskontoer = saksbehandler.valgtBehandling.getSaldoer().stønadskonti();
        assertThat(stonadskontoer.get(SaldoVisningStønadskontoType.FEDREKVOTE).saldo())
                .as("Saldoen for stønadskonton FORELDREPENGER")
                .isZero();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Forventer ingen avslåtte peridoer")
                .isEmpty();
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
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
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medBeregnetInntekt(BigDecimal.valueOf(mor.månedsinntekt()))
                .medArbeidsforholdId(null);

        ventPåInntektsmeldingForespørsel(saksnummer);
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
                .build();
        var mor = familie.mor();
        var saksnummer = mor.søkPapirsøknadForeldrepenger();

        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);

        saksbehandler.hentFagsak(saksnummer);
        var aksjonspunktBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new PapirSøknadForeldrepengerBekreftelse());
        var fordeling = new FordelingDto();
        var fpff = new PermisjonPeriodeDto(KontoType.FORELDREPENGER_FØR_FØDSEL,
                fpStartdato, fødselsdato.minusDays(1));
        var mødrekvote = new PermisjonPeriodeDto(KontoType.MØDREKVOTE,
                fødselsdato, fødselsdato.plusWeeks(10));
        fordeling.permisjonsPerioder.add(fpff);
        fordeling.permisjonsPerioder.add(mødrekvote);
        aksjonspunktBekreftelse.morSøkerFødsel(fordeling, fødselsdato, fpff.periodeFom.minusWeeks(3));
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelse);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // verifiserer uttak
        var perioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(perioder).hasSize(3);
        verifiserUttaksperiode(perioder.get(0), KontoType.FORELDREPENGER_FØR_FØDSEL, 1);
        verifiserUttaksperiode(perioder.get(1), KontoType.MØDREKVOTE, 1);
        verifiserUttaksperiode(perioder.get(2), KontoType.MØDREKVOTE, 1);

        // Endringssøknad
        mor.sendInnPapirsøknadEEndringForeldrepenger();
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var aksjonspunktBekreftelseEndringssøknad = saksbehandler
                .hentAksjonspunktbekreftelse(new PapirSøknadEndringForeldrepengerBekreftelse());
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
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.gjenopptaBehandling();
        saksbehandler.fortsettUteninntektsmeldinger();
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
        var overstyrtInntekt = 250_000;
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
    @DisplayName("Far søker fødsel med aleneomsorg som bekreftes at han har (mor forsvunnet)")
    void farSøkerFødselAleneomsorgMenErGiftOgBorMedAnnenpart() {
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(mor().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build();
        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medUttaksplan(fordelingFarAleneomsorg(fødselsdato))
                .medAnnenForelder(AnnenforelderMaler.norskAleneomsorg(familie.mor()));
        var saksnummer = far.søk(søknad);
        var arbeidsgiver = far.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fødselsdato);

        // Saksbehandler må avklare aleneomsorgen fordi far er gift og bor på sammested med ektefelle
        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAleneomsorgBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAleneomsorgBekreftelse())
                .bekreftBrukerHarAleneomsorg()
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAleneomsorgBekreftelse);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // verifiserer uttak
        var uttaksperioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttaksperioder).hasSize(2);
        var foreldrepengerFørste6Ukene = uttaksperioder.get(0);
        assertThat(foreldrepengerFørste6Ukene.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getKontoType()).isEqualTo(FORELDREPENGER);
        var foreldrepengerEtterUke6 = uttaksperioder.get(1);
        assertThat(foreldrepengerEtterUke6.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getKontoType()).isEqualTo(FORELDREPENGER);

        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti())
                .as("Stonadskontoer i Saldo")
                .hasSize(1);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti().get(SaldoVisningStønadskontoType.FORELDREPENGER))
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
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
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
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var arbeidsgivere = mor.arbeidsgivere();
        var gradertArbeidsgiverIdentifikator = arbeidsgivere.toList().get(0).arbeidsgiverIdentifikator();
        var graderingFom = fødselsdato.plusWeeks(10).plusDays(1);
        var graderingTom = fødselsdato.plusWeeks(12);
        var arbeidstidsprosent = BigDecimal.TEN;
        var fordeling = fordeling(
                uttaksperiode(KontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)),
                graderingsperiodeArbeidstaker(KontoType.FELLESPERIODE,graderingFom, graderingTom, gradertArbeidsgiverIdentifikator, arbeidstidsprosent.intValue()));
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgivere.sendDefaultInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        debugLoggBehandling(saksbehandler.valgtBehandling);

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
                        arbeidsforholdFraScenario.get(0).arbeidsgiverIdentifikasjon(),
                        arbeidsforholdFraScenario.get(1).arbeidsgiverIdentifikasjon());
            }
        }
        var fpff = uttaksperioder.get(0);
        assertThat(fpff.getAktiviteter().get(0).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(fpff.getAktiviteter().get(0).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(fpff.getAktiviteter().get(0).getKontoType()).isEqualTo(FORELDREPENGER_FØR_FØDSEL);
        assertThat(fpff.getAktiviteter().get(1).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(fpff.getAktiviteter().get(1).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(fpff.getAktiviteter().get(1).getKontoType()).isEqualTo(FORELDREPENGER_FØR_FØDSEL);
        var mødrekvoteFørste6Ukene = uttaksperioder.get(1);
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(0).getKontoType()).isEqualTo(MØDREKVOTE);
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(1).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(1).getTrekkdagerDesimaler())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(1).getKontoType()).isEqualTo(MØDREKVOTE);
        var mødrekvoteEtterUke6 = uttaksperioder.get(2);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(0).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(0).getKontoType()).isEqualTo(MØDREKVOTE);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(1).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(1).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(1).getKontoType()).isEqualTo(MØDREKVOTE);
        var gradering = uttaksperioder.get(3);
        assertThat(gradering.getGraderingAvslagÅrsak()).isEqualTo("-");
        assertThat(gradering.getGraderingInnvilget()).isTrue();
        assertThat(gradering.getGradertArbeidsprosent()).isEqualTo(arbeidstidsprosent);
        assertThat(gradering.getAktiviteter().get(0).getKontoType()).isEqualTo(FELLESPERIODE);
        assertThat(gradering.getAktiviteter().get(1).getKontoType()).isEqualTo(FELLESPERIODE);
        UttakResultatPeriodeAktivitet gradertAktivitet = finnAktivitetForArbeidsgiver(gradering, gradertArbeidsgiverIdentifikator);
        assertThat(gradertAktivitet.getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(gradertAktivitet.getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100).subtract(arbeidstidsprosent));
        assertThat(gradertAktivitet.getProsentArbeid()).isEqualTo(arbeidstidsprosent);

        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti())
                .as("Antall stønadskonter i saldo")
                .hasSize(4);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldo igjen på FORELDREPENGER_FØR_FØDSEL")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo())
                .as("Saldo igjen på FELLESPERIODE")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo())
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
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medUttaksplan(fordelingMorAleneomsorgHappyCase(fødselsdato))
                .medAnnenForelder(AnnenforelderMaler.ukjentForelder());
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
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
        assertThat(fpff.getAktiviteter().get(0).getKontoType()).isEqualTo(FORELDREPENGER_FØR_FØDSEL);
        var foreldrepengerFørste6Ukene = uttaksperioder.get(1);
        assertThat(foreldrepengerFørste6Ukene.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getKontoType()).isEqualTo(FORELDREPENGER);
        var foreldrepengerEtterUke6 = uttaksperioder.get(2);
        assertThat(foreldrepengerFørste6Ukene.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isEqualByComparingTo(BigDecimal.valueOf(200));
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getKontoType()).isEqualTo(FORELDREPENGER);
        // Periode søkt mer enn 49 uker er avslått automatisk
        var periodeMerEnn49Uker = uttaksperioder.get(3);
        assertThat(periodeMerEnn49Uker.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.AVSLÅTT);
        assertThat(periodeMerEnn49Uker.getPeriodeResultatÅrsak()).isEqualTo(PeriodeResultatÅrsak.IKKE_STØNADSDAGER_IGJEN);
        assertThat(periodeMerEnn49Uker.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(0));
        assertThat(periodeMerEnn49Uker.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(periodeMerEnn49Uker.getAktiviteter().get(0).getKontoType()).isEqualTo(FORELDREPENGER);

        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti())
                .as("Antall stønadskonter i saldo")
                .hasSize(2);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldo igjen på FORELDREPENGER_FØR_FØDSEL")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stønadskonti().get(SaldoVisningStønadskontoType.FORELDREPENGER).saldo())
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
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medBarn(BarnBuilder.fødsel(2, fødselsdato).build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);

        var sjekkManglendeFødsel = saksbehandler.hentAksjonspunktbekreftelse(new SjekkManglendeFødselBekreftelse())
                .bekreftBarnErFødt(2, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(sjekkManglendeFødsel);

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
                .build();
        var mor = familie.mor();
        var fødselsdato = LocalDate.now().minusDays(5);
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medBarn(BarnBuilder.fødsel(2, fødselsdato).build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandling er satt på vent (Har ventet til 2. uke)")
                .isTrue();
        logger.debug("{}", saksbehandler.valgtBehandling.fristBehandlingPåVent);
        logger.debug("{}", fødselsdato.plusWeeks(2));
        assertThat(saksbehandler.valgtBehandling.fristBehandlingPåVent)
                .as("Frist behandling på vent")
                .isEqualTo(fødselsdato.plusWeeks(1).plusDays(1));
    }

    private UttakResultatPeriodeAktivitet finnAktivitetForArbeidsgiver(UttakResultatPeriode uttakResultatPeriode, String identifikator) {
        return uttakResultatPeriode.getAktiviteter().stream()
                .filter(a -> a.getArbeidsgiverReferanse().equalsIgnoreCase(identifikator)).findFirst().orElseThrow();
    }

    // TODO må ta inn fordeling som blir laget i søknad for å kunne verifisere rikitg på hva som er i VL!
    //  Denne verifisering kan bare brukes hvis fordeling = fordelingMorHappyCaseLong
    @Step("Verifiserer utttaksperioder")
    private void verifiserUttak(int antallAktiviteter, List<UttakResultatPeriode> perioder) {
        assertThat(perioder).hasSize(4);
        verifiserUttaksperiode(perioder.get(0), KontoType.FORELDREPENGER_FØR_FØDSEL, antallAktiviteter);
        verifiserUttaksperiode(perioder.get(1), KontoType.MØDREKVOTE, antallAktiviteter);
        verifiserUttaksperiode(perioder.get(2), KontoType.MØDREKVOTE, antallAktiviteter);
        verifiserUttaksperiode(perioder.get(3), KontoType.FELLESPERIODE, antallAktiviteter);
    }

    private void verifiserUttaksperiode(UttakResultatPeriode uttakResultatPeriode, KontoType stønadskonto,
            int antallAktiviteter) {
        assertThat(uttakResultatPeriode.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttakResultatPeriode.getUtsettelseType()).isEqualTo(UttakresultatUtsettelseÅrsak.UDEFINERT);
        assertThat(uttakResultatPeriode.getAktiviteter()).hasSize(antallAktiviteter);
        for (UttakResultatPeriodeAktivitet aktivitet : uttakResultatPeriode.getAktiviteter()) {
            assertThat(aktivitet.getKontoType()).isEqualTo(stønadskonto);
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
