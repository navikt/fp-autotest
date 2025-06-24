package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.graderingsperiodeSN;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OpptjeningAktivitetType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AvklarAktiviteterBekreftelse;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Prosent;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OpptjeningAktivitetType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FordelBeregningsgrunnlagBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Prosent;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.OpptjeningMaler;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.GrunnlagDto;

@Tag("fpsak")
class BeregningVerdikjede extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold og tre bortfalte naturalytelser på forskjellige tidspunkt")
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
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato);

        // Legger til naturalytelser som opphører
        var førsteYtelse = lagBortfaltNaturalytelse(685, fpStartdato.plusDays(10));
        var andreYtelse = lagBortfaltNaturalytelse(998, fpStartdato.plusDays(40));
        var tredjeYtelse = lagBortfaltNaturalytelse(754, fpStartdato.plusDays(60));

        inntektsmelding
                .medOpphørAvNaturalytelseListe(førsteYtelse.beløpPrMnd, førsteYtelse.fom, Inntektsmelding.NaturalytelseType.ELEKTRISK_KOMMUNIKASJON)
                .medOpphørAvNaturalytelseListe(andreYtelse.beløpPrMnd, andreYtelse.fom,
                        Inntektsmelding.NaturalytelseType.FRI_TRANSPORT)
                .medOpphørAvNaturalytelseListe(tredjeYtelse.beløpPrMnd, tredjeYtelse.fom,
                        Inntektsmelding.NaturalytelseType.KOST_DAGER);

        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        debugLoggBehandling(saksbehandler.valgtBehandling);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // Verifiser at beregning er gjort riktig
        var startdatoer = Arrays.asList(fpStartdato, førsteYtelse.fom, andreYtelse.fom, tredjeYtelse.fom);
        var beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserBGPerioder(startdatoer, beregningsgrunnlag);
        var inntektPrÅr = mor.månedsinntekt() * 12;
        var orgNr = arbeidsgiver.arbeidsgiverIdentifikator();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr, 0, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1),
                lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr, førsteYtelse.beløpPrÅr.doubleValue(), 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(2), lagBGAndel(orgNr, inntektPrÅr,
                inntektPrÅr, førsteYtelse.beløpPrÅr.add(andreYtelse.beløpPrÅr).doubleValue(), 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(3),
                lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr,
                        førsteYtelse.beløpPrÅr.add(andreYtelse.beløpPrÅr).add(tredjeYtelse.beløpPrÅr).doubleValue(),
                        0));
    }

    @Test
    @DisplayName("Mor søker fødsel med full AAP og et arbeidsforhold som tilkommer etter skjæringstidspunktet")
    void morSøkerFødselMedFullAAPOgArbeidsforhold() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arena(ArenaSakerDto.YtelseTema.AAP, LocalDate.now().minusMonths(12), LocalDate.now().minusWeeks(12), 10_000)
                                .arbeidsforholdUtenInntekt(LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(12))
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
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var inntektPerMåned = 30_000;
        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmeldingForTilkommendeArbeidsforhold = arbeidsgiver.lagInntektsmeldingTilkommendeArbeidsforholdEtterFPstartdato(fpStartdato)
                .medBeregnetInntekt(inntektPerMåned)
                .medRefusjonBeløpPerMnd(inntektPerMåned);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmeldingForTilkommendeArbeidsforhold);

        saksbehandler.hentFagsak(saksnummer);

        // FORDEL BEREGNINGSGRUNNLAG //
        BeregningsgrunnlagPrStatusOgAndelDto aapAndel = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0)
                .getBeregningsgrunnlagPrStatusOgAndel()
                .stream().filter(a -> a.getAndelsnr() == 1)
                .findFirst().orElseThrow();
        double totaltBg = aapAndel.getBeregnetPrAar();

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        var beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndelMedFordelt(aapAndel.getAktivitetStatus().getKode(), totaltBg, totaltBg, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1),
                lagBGAndelMedFordelt(aapAndel.getAktivitetStatus().getKode(), totaltBg, 0, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1),
                lagBGAndelMedFordelt(arbeidsgiver.arbeidsgiverIdentifikator(), 0, totaltBg, totaltBg, inntektPerMåned * 12));
    }


    @Test
    @DisplayName("Mor søker fødsel med full AAP og et arbeidsforhold som ikke skal benyttes.")
    void morSøkerFødselMedFullAAPOgArbeidsforholdSomErAktivtPåStp() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arena(ArenaSakerDto.YtelseTema.AAP, LocalDate.now().minusMonths(12), LocalDate.now().plusMonths(2), 10_000)
                                .arbeidsforhold(LocalDate.now().minusMonths(10), LocalDate.now().plusMonths(12))
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
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        var arbeidsgiverIdentifikator = arbeidsgiver.arbeidsgiverIdentifikator();
        var månedsinntekt = mor.månedsinntekt(arbeidsgiverIdentifikator);
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medRefusjonBeløpPerMnd(månedsinntekt);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);

         //AVKLAR AKTIVITETER
        assertThat(saksbehandler.harAksjonspunkt(AksjonspunktKoder.AVKLAR_AKTIVITETER)).isTrue();
                var avklarAktiviteterBekreftelse = saksbehandler
                    .hentAksjonspunktbekreftelse(new AvklarAktiviteterBekreftelse())
                    .setSkalBrukes(true, arbeidsgiverIdentifikator);
            saksbehandler.bekreftAksjonspunkt(avklarAktiviteterBekreftelse);

         // FAKTA BEREGNING //
        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
                    .leggTilMånedsinntektArbeidUnderAAP(10_000)
                .setBegrunnelse("Endret av Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

         // AVVIKSVURDERING  //
        var andelArbeid = getAndelsnr(saksbehandler.valgtBehandling.getBeregningsgrunnlag(), OpptjeningAktivitetType.ARBEID);
        var andelArbeidUnderAAP = getAndelsnr(saksbehandler.valgtBehandling.getBeregningsgrunnlag(), OpptjeningAktivitetType.ARBEID_UNDER_AAP);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(
                new VurderBeregnetInntektsAvvikBekreftelse()).leggTilInntekt(månedsinntekt * 12, andelArbeid).leggTilInntekt(120_000, andelArbeidUnderAAP).setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        var andeler = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPerioder().getFirst().getBeregningsgrunnlagPrStatusOgAndel();

        var aapAndel = andeler.stream().filter(a -> a.getAktivitetStatus().equals(AktivitetStatus.ARBEIDSAVKLARINGSPENGER)).findFirst().orElseThrow();
        assertThat(aapAndel.getBruttoPrAar()).isEqualTo(260_000);
        assertThat(aapAndel.getBeregnetPrAar()).isEqualTo(260_000);

        var arbeidsforholdAndel = andeler.stream().filter(a -> a.getAndelsnr() == andelArbeid).findFirst().orElseThrow();
        assertThat(arbeidsforholdAndel.getBruttoPrAar()).isEqualTo(månedsinntekt * 12);
        assertThat(arbeidsforholdAndel.getBeregnetPrAar()).isEqualTo(månedsinntekt * 12);

        var arbeidUnderAAPAndel = andeler.stream().filter(a -> a.getAndelsnr() == andelArbeidUnderAAP).findFirst().orElseThrow();
        assertThat(arbeidUnderAAPAndel.getBruttoPrAar()).isEqualTo(120_000);
        assertThat(arbeidUnderAAPAndel.getBeregnetPrAar()).isEqualTo(120_000);
    }


    @Test
    @DisplayName("Mor med kun ytelse på skjæringstidspunktet og dagpenger i opptjeningsperioden")
    @Description("Mor med kun ytelse på skjæringstidspunktet og dagpenger i opptjeningsperioden")
    void kun_ytelse_med_vurdering_av_besteberegning() {
        var fødselsdatoBarn = LocalDate.now().minusDays(2);
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arena(ArenaSakerDto.YtelseTema.DAG, LocalDate.now().minusYears(1), LocalDate.now().minusWeeks(12), 10_000)
                                .infotrygd(GrunnlagDto.Ytelse.SP, LocalDate.now().minusMonths(9), LocalDate.now(), GrunnlagDto.Status.AVSLUTTET, fødselsdatoBarn)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(fødselsdatoBarn)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        // FAKTA OM BEREGNING: Vurder besteberegning og fastsett månedsinntekt fra
        // ytelse
        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
                .leggTilAndelerYtelse(10000.0, Inntektskategori.ARBEIDSTAKER)
                .settSkalHaBesteberegningForKunYtelse(true)
                .setBegrunnelse("Endret av Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        // FORESLÅ VEDTAK //
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        // FATTE VEDTAK //
        beslutter.hentFagsak(saksnummer);
        var apFaktaOmBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(List.of(apFaktaOmBeregning));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        var beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndelMedBesteberegning("BA", 120_000));
    }

    @Test
    @DisplayName("SN med gradering og Arbeidsforhold med refusjon over 6G")
    @Description("Mor er SN som søker gradering og har arbeidsgiver som søker refusjon over 6G")
    @Tag("beregning")
    void SN_med_gradering_og_arbeidsforhold_som_søker_refusjon_over_6G() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidMedOpptjeningOver6G()
                                .selvstendigNæringsdrivende(1_000_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusDays(2))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var opptjening = OpptjeningMaler.egenNaeringOpptjening(
                mor.arbeidsforhold().arbeidsgiverIdentifikasjon().value(),
                mor.næringStartdato(),
                LocalDate.now(),
                false,
                30_000,
                false);
        var fordeling = fordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(StønadskontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)),
                graderingsperiodeSN(StønadskontoType.FELLESPERIODE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(10).minusDays(1), 50)
        );
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medSelvstendigNæringsdrivendeInformasjon(opptjening)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        var arbeidsgiverIdentifikator = arbeidsgiver.arbeidsgiverIdentifikator();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medRefusjonBeløpPerMnd(Prosent.valueOf(100));
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.VEDLEGG_MOTTATT);
        debugLoggBehandling(saksbehandler.valgtBehandling);

        // FORDEL BEREGNINGSGRUNNLAG //
        var graderingsperiode = fordeling.uttaksperioder().get(2);
        var fordelBeregningsgrunnlagBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new FordelBeregningsgrunnlagBekreftelse())
                .settFastsattBeløpOgInntektskategoriMedRefusjon(graderingsperiode.fom(), 500_000, 500_000,
                        Inntektskategori.ARBEIDSTAKER, 1)
                .settFastsattBeløpOgInntektskategori(graderingsperiode.fom(), 235_138, Inntektskategori.SELVSTENDIG_NÆRINGSDRIVENDE, 2)
                .settFastsattBeløpOgInntektskategoriMedRefusjon(graderingsperiode.tom().plusDays(1), 720_000, 720_000,
                        Inntektskategori.ARBEIDSTAKER, 1)
                .settFastsattBeløpOgInntektskategori(graderingsperiode.tom().plusDays(1), 0, Inntektskategori.SELVSTENDIG_NÆRINGSDRIVENDE, 2);
        saksbehandler.bekreftAksjonspunkt(fordelBeregningsgrunnlagBekreftelse);

        // FORESLÅ VEDTAK //
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        // FATTE VEDTAK //
        beslutter.hentFagsak(saksnummer);
        var apFordelBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.FORDEL_BEREGNINGSGRUNNLAG);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(List.of(apFordelBeregning));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        var beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndel(arbeidsgiverIdentifikator, 900_000, 900_000, 0, 900_000));
        assertThat(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel().stream()
                .filter(andel -> andel.getAktivitetStatus().equals(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .mapToInt(andel -> ((Double)andel.getDagsats()).intValue()).sum()).isZero();

        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1),
                lagBGAndelMedFordelt(arbeidsgiverIdentifikator, 900_000, 500_000, 0, 500_000));
        assertFordeltSNAndel(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1), 235_138);

        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(2),
                lagBGAndelMedFordelt(arbeidsgiverIdentifikator, 900_000, 720_000, 0, 720_000));
        assertThat(beregningsgrunnlag.getBeregningsgrunnlagPeriode(2).getBeregningsgrunnlagPrStatusOgAndel().stream()
                .filter(andel -> andel.getAktivitetStatus().equals(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .mapToInt(andel -> ((Double)andel.getDagsats()).intValue()).sum()).isZero();
    }

    private void assertFordeltSNAndel(BeregningsgrunnlagPeriodeDto periode, int bruttoPrÅr) {
        var brutto = periode.getBeregningsgrunnlagPrStatusOgAndel().stream()
                .filter(andel -> andel.getAktivitetStatus().equals(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .mapToInt(andel -> ((Double)andel.getBruttoPrAar()).intValue()).sum();
        var fordelt = periode.getBeregningsgrunnlagPrStatusOgAndel().stream()
                .filter(andel -> andel.getAktivitetStatus().equals(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .mapToInt(andel -> ((Double)andel.getFordeltPrAar()).intValue()).sum();
        var dagsats = periode.getBeregningsgrunnlagPrStatusOgAndel().stream()
                .filter(andel -> andel.getAktivitetStatus().equals(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .mapToInt(andel -> ((Double)andel.getDagsats()).intValue()).sum();
        assertThat(brutto).isEqualTo(bruttoPrÅr);
        assertThat(fordelt).isZero();
        assertThat(dagsats).isPositive();
    }

    @Test
    @DisplayName("Mor med for sent refusjonskrav.")
    void morFødselForSentRefusjonskrav() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4), 900_000)
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-002", LocalDate.now().minusMonths(4), LocalDate.now(), 900_000)
                                .build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NAV_BERGEN, "ARB001-001", LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4), 900_000)
                                .arbeidsforhold(TestOrganisasjoner.NAV_BERGEN, "ARB001-002", LocalDate.now().minusMonths(4), LocalDate.now(), 900_000)
                                .build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusYears(3))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = LocalDate.now().minusMonths(4);
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fpStartdato);
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        var arbeidsgiverIdentifikator = arbeidsgiver.arbeidsgiverIdentifikator();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medBeregnetInntekt(Prosent.valueOf(50))
                .medRefusjonBeløpPerMnd(29_000);
        var inntektsmeldingTilkommendeArbeidsforhold = arbeidsgiver.lagInntektsmeldingTilkommendeArbeidsforholdEtterFPstartdato(fpStartdato)
                .medBeregnetInntekt(Prosent.valueOf(50))
                .medRefusjonBeløpPerMnd(Prosent.valueOf(100));

        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmeldingTilkommendeArbeidsforhold);

        saksbehandler.hentFagsak(saksnummer);

        // FAKTA OM FØDSEL
        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderManglendeFodselBekreftelse())
                .bekreftDokumentasjonForeligger(1, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        // FAKTA OM BEREGNING: Vurder gyldighet for refusjonskrav som har kommet for
        // sent
        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
                .leggTilRefusjonGyldighetVurdering(arbeidsgiverIdentifikator, true)
                .setBegrunnelse("Endret av Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderBeregnetInntektsAvvikBekreftelse())
                .leggTilInntekt(360_000, 1);
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        // FORESLÅ VEDTAK //
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        // FATTE VEDTAK //
        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(List.of(
                beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN),
                beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS),
                beslutter.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL)));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        var beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndel(arbeidsgiverIdentifikator, 450_000, 360_000, 0, 348_000));
    }

    @Test
    @DisplayName("ATFL i samme org med lønnsendring")
    void ATFL_samme_org_med_lønnendring_uten_inntektsmelding() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusYears(1), 300_000,
                                        arbeidsavtale(LocalDate.now().minusYears(1))
                                                .sisteLønnsendringsdato(LocalDate.now().minusMonths(2))
                                                .build())
                                .frilans(25, LocalDate.now().minusYears(3), 300_000,
                                        arbeidsavtale(LocalDate.now().minusYears(1))
                                                .sisteLønnsendringsdato(LocalDate.now())
                                                .build())
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now())
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var opptjening = OpptjeningMaler.frilansOpptjening();
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medFrilansInformasjon(opptjening)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        debugLoggBehandling(saksbehandler.valgtBehandling);

        // Gjenoppta autopunkter
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();
        if (saksbehandler.harAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE)) {
            saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE);
            saksbehandler.gjenopptaBehandling();
        }

        // Løs 5085, ikke vent på inntektsmeldinger
        saksbehandler.fortsettUteninntektsmeldinger();

        // FAKTA OM BEREGNING
        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        assertThat(aksjonspunkt.erUbekreftet())
                .as("Aksjonspunktstatus er ubekreftet for VURDER_FAKTA_FOR_ATFL_SN")
                .isTrue();
    }

    @Test
    @DisplayName("Uten inntektsmelding, med lønnsendring de siste 3 månedene")
    @Description("Uten inntektsmelding, med lønnsendring de siste 3 månedene")
    void vurder_mottar_ytelse_vurder_lonnsendring() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusYears(1),
                                        arbeidsavtale(LocalDate.now().minusYears(1))
                                                .sisteLønnsendringsdato(LocalDate.now().minusMonths(2))
                                                .build())
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now())
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        // Gjenoppta autopunkter
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();
        if (saksbehandler.harAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE)) {
            saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE);
            saksbehandler.gjenopptaBehandling();
        }

        // Løs 5085, ikke vent på inntektsmeldinger
        saksbehandler.fortsettUteninntektsmeldinger();

        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        assertThat(aksjonspunkt.erUbekreftet())
                .as("Aksjonspunktstatus er ubekreftet for VURDER_FAKTA_FOR_ATFL_SN")
                .isTrue();
    }

    @Test
    @DisplayName("To arbeidsforhold samme org.")
    void toArbeidsforholdSammeOrgEttStarterEtterStp() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", LocalDate.now().minusMonths(12), LocalDate.now().minusMonths(1), 360_000)
                                .arbeidsforholdUtenInntekt(TestOrganisasjoner.NAV, "ARB001-002", LocalDate.now().minusMonths(12), LocalDate.now())
                                .arbeidsforholdUtenInntekt(TestOrganisasjoner.NAV, "ARB001-003", LocalDate.now().plusWeeks(1), LocalDate.now().plusMonths(2))
                                .arbeidsforhold(TestOrganisasjoner.NAV_STORD, 15, LocalDate.now().minusMonths(12), 300_000)
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
        var arbeidsgiver1 = arbeidsgivere.toList().get(0);
        var inntektsmelding1 = arbeidsgiver1.lagInntektsmeldingFP(fpStartdato)
                .medRefusjonBeløpPerMnd(100);
        arbeidsgiver1.sendInntektsmelding(saksnummer, inntektsmelding1);

        var arbeidsgiver2 = arbeidsgivere.toList().get(1);
        var inntektsmelding2 = arbeidsgiver2.lagInntektsmeldingFP(fpStartdato)
                .medRefusjonBeløpPerMnd(100);
        arbeidsgiver2.sendInntektsmelding(saksnummer, inntektsmelding2);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

    }

    @Test
    @DisplayName("Mor fødsel med frilans som eneste inntekt")
    @Description("Mor fødsel med frilans som eneste inntekt. Oppgir ikke frilans i søknaden")
    void morSøkerFødselMedEttArbeidsforholdOgFrilans_VurderOpptjening_VurderFaktaOmBeregning_AvvikIBeregning() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .frilans(100, LocalDate.now().minusYears(3), LocalDate.now().plusMonths(3), 504_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now())
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.VEDTAK_FATTET);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        // Verifiser Beregningsgrunnlag
        Assertions.assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus())
                .as("Antall aktivitetstatus")
                .isEqualTo(1);
        Assertions.assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0))
                .as("Aktivitetsstatus i beregnignsgrunnlag")
                .isEqualTo(AktivitetStatus.FRILANSER);
        Assertions.assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto())
                .as("Antall beregningsgrunnlagsparioder")
                .isEqualTo(1);
        var andeler = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        Assertions.assertThat(andeler)
                .as("Antall andeler")
                .hasSize(1);
        Assertions.assertThat(andeler.get(0).getAktivitetStatus())
                .as("Aktivitetsstatus")
                .isEqualTo(AktivitetStatus.FRILANSER);
        Assertions.assertThat(andeler.get(0).getDagsats())
                .as("Dagsats")
                .isEqualTo(1938);
        Assertions.assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }


    private void verifiserAndelerIPeriode(BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode,
            BGAndelHelper BGAndelHelper) {
        assertThat(beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndel().stream()
                .noneMatch(a -> matchAndel(BGAndelHelper, a)))
                .isFalse();
        beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndel().forEach(andel -> {
            if (matchAndel(BGAndelHelper, andel)) {
                assertAndeler(andel, BGAndelHelper);
            }
        });
    }

    private boolean matchAndel(BGAndelHelper andelHelper, BeregningsgrunnlagPrStatusOgAndelDto andel) {
        if (andelHelper.andelsnr != 0) {
            return andelHelper.andelsnr == andel.getAndelsnr();
        }
        return andelTilhørerArbeidsgiverMedId(andelHelper, andel)
                || andelTilhørerAktivitetMedStatus(andelHelper, andel);
    }

    private boolean andelTilhørerAktivitetMedStatus(BGAndelHelper bgAndelHelper,
                                                    BeregningsgrunnlagPrStatusOgAndelDto andel) {
        return andel.getAktivitetStatus().getKode().equals(bgAndelHelper.aktivitetstatus);
    }

    private Integer getAndelsnr(Beregningsgrunnlag beregningsgrunnlag, OpptjeningAktivitetType type) {
        return beregningsgrunnlag.getBeregningsgrunnlagPerioder()
                .getFirst()
                .getBeregningsgrunnlagPrStatusOgAndel()
                .stream()
                .filter(a -> a.getArbeidsforhold() != null && Objects.equals(a.getArbeidsforhold().getArbeidsforholdType(), type))
                .map(BeregningsgrunnlagPrStatusOgAndelDto::getAndelsnr)
                .findFirst()
                .orElseThrow();
    }

    private void assertAndeler(BeregningsgrunnlagPrStatusOgAndelDto andel, BGAndelHelper BGAndelHelper) {
        assertThat(andel.getBruttoPrAar()).isEqualTo(BGAndelHelper.bruttoPrÅr);
        assertThat(andel.getBeregnetPrAar()).isEqualTo(BGAndelHelper.beregnetPrÅr);
        assertThat(andel.getBortfaltNaturalytelse()).isEqualTo(BGAndelHelper.naturalytelseBortfaltPrÅr);
        assertThat(andel.getFordeltPrAar()).isEqualTo(BGAndelHelper.fordeltPrÅr);
        if (andel.getArbeidsforhold() != null) {
            assertThat(andel.getArbeidsforhold().getRefusjonPrAar()).isEqualTo(BGAndelHelper.refusjonPrÅr);
        }
    }

    private boolean andelTilhørerArbeidsgiverMedId(BGAndelHelper BGAndelHelper,
            BeregningsgrunnlagPrStatusOgAndelDto andel) {
        return (andel.getArbeidsforhold() != null)
                && Objects.equals(andel.getArbeidsforhold().getArbeidsgiverIdent(), BGAndelHelper.arbeidsgiverId);
    }

    private BGAndelHelper lagBGAndel(ArbeidsgiverIdentifikator orgNr, int beregnetPrÅr, int bruttoPrÅr, double bortfaltNaturalytelseBeløp,
                                     double refusjonskravPrÅr) {
        var andel = new BGAndelHelper();
        andel.arbeidsgiverId = orgNr.value();
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = bruttoPrÅr;
        andel.naturalytelseBortfaltPrÅr = bortfaltNaturalytelseBeløp;
        andel.refusjonPrÅr = refusjonskravPrÅr;
        return andel;
    }

    private BGAndelHelper lagBGAndelForAndelsnr(long andelsnr, double beregnetPrÅr, double bruttoPrÅr, double refusjonPrÅr) {
        var andel = new BGAndelHelper();
        andel.andelsnr = andelsnr;
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = bruttoPrÅr;
        andel.refusjonPrÅr = refusjonPrÅr;
        return andel;
    }

    private BGAndelHelper lagBGAndelMedFordelt(ArbeidsgiverIdentifikator orgNr, double beregnetPrÅr, double bruttoPrÅr, double fordeltPrÅr,
            double refusjonPrÅr) {
        var andel = new BGAndelHelper();
        andel.arbeidsgiverId = orgNr.value();
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = bruttoPrÅr;
        andel.fordeltPrÅr = fordeltPrÅr;
        andel.refusjonPrÅr = refusjonPrÅr;
        return andel;
    }

    private BGAndelHelper lagBGAndelMedFordelt(String aktivitetstatus, double beregnetPrÅr, double bruttoPrÅr,
            double fordeltPrÅr) {
        var andel = new BGAndelHelper();
        andel.aktivitetstatus = aktivitetstatus;
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = bruttoPrÅr;
        andel.fordeltPrÅr = fordeltPrÅr;
        return andel;
    }

    private BGAndelHelper lagBGAndelMedBesteberegning(String aktivitetstatus, int beregnetPrÅr) {
        var andel = new BGAndelHelper();
        andel.aktivitetstatus = aktivitetstatus;
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = beregnetPrÅr;
        return andel;
    }

    private BortfaltnaturalytelseHelper lagBortfaltNaturalytelse(double mndBeløp, LocalDate fom) {
        var nat = new BortfaltnaturalytelseHelper();
        nat.beløpPrMnd = BigDecimal.valueOf(mndBeløp);
        nat.beløpPrÅr = nat.beløpPrMnd.multiply(BigDecimal.valueOf(12));
        nat.fom = fom;
        return nat;

    }

    private void verifiserBGPerioder(List<LocalDate> startdatoer, Beregningsgrunnlag beregningsgrunnlag) {
        for (int i = 0; i < startdatoer.size(); i++) {
            assertThat(startdatoer.get(i))
                    .isEqualTo(beregningsgrunnlag.getBeregningsgrunnlagPeriode(i).getBeregningsgrunnlagPeriodeFom());
        }
    }

    private static class BGAndelHelper {
        public String aktivitetstatus;
        private double bruttoPrÅr;
        private double beregnetPrÅr;
        private double fordeltPrÅr;
        private double refusjonPrÅr;
        private double andelsnr;
        private double naturalytelseBortfaltPrÅr;
        private String arbeidsgiverId;
    }

    private static class BortfaltnaturalytelseHelper {
        private BigDecimal beløpPrMnd;
        private BigDecimal beløpPrÅr;
        private LocalDate fom;
    }

}
