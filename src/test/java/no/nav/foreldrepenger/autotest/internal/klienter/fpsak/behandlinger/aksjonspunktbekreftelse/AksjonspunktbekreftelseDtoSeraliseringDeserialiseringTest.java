package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.behandlinger.aksjonspunktbekreftelse;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak.ARBEID;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OppholdÅrsak;
import no.nav.foreldrepenger.autotest.internal.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AvklarAktiviteterBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BeregningsaktivitetLagreDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BesteberegningFødendeKvinneAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BesteberegningFødendeKvinneDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.DagpengeAndelLagtTilBesteberegningDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FaktaOmBeregningLagreDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsatteVerdier;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsatteVerdierDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsatteVerdierForBesteberegningDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettBeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettBeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettBgKunYtelseDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettBrukersAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettBruttoBeregningsgrunnlagSNBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettEndretBeregningsgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettEndretBeregningsgrunnlagAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettEndretBeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettMaanedsinntektUtenInntektsmeldingAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttakKontrollerOpplysningerOmMedlemskap;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FordelBeregningsgrunnlagBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravKa;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravNfp;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.MannAdoptererAleneBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.RedigerbarAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VarselOmRevurderingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderEktefellesBarnBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderOmsorgForBarnBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedFeilutbetalingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedNegativSimulering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderVarigEndringEllerNyoppstartetSNBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderVilkaarForSykdomBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvForeldreansvarAndreLedd;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvForeldreansvarFjerdeLedd;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvInnsynBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvOmsorgsvilkoret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.FaktaOmBeregningTilfelle;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.MottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.AktørId;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagArbeidsforholdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.FordelBeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.opptjening.OpptjeningAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Arbeidsgiver;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class AksjonspunktbekreftelseDtoSeraliseringDeserialiseringTest extends SerializationTestBase {

    private Arbeidsgiver arbeidsgiver = null;
    private UttakResultatPeriode uttakResultatPeriode = null;

    @Test
    void AvklarAktiviteterBekreftelseTest() {
        test(new AvklarAktiviteterBekreftelse(List.of(lagBeregningsaktivitetLagreDto(), lagBeregningsaktivitetLagreDto())));
    }

    @Test
    void BesteberegningFødendeKvinneDtoTest() {
        test(lagBesteberegningFødendeKvinneDto());
    }

    @Test
    void FastsettBruttoBeregningsgrunnlagSNBekreftelseTest() {
        test(new FastsettBruttoBeregningsgrunnlagSNBekreftelse(50000));
    }

    @Test
    void FastsettUttakKontrollerOpplysningerOmMedlemskapTest() {
        var fastsettUttakKontrollerOpplysningerOmMedlemskap = new FastsettUttakKontrollerOpplysningerOmMedlemskap();
        fastsettUttakKontrollerOpplysningerOmMedlemskap.leggTilUttakPeriode(lagUttakResultatPeriode());
        test(fastsettUttakKontrollerOpplysningerOmMedlemskap);
    }

    @Test
    void FastsettUttaksperioderManueltBekreftelseTest() {
        test(new FastsettUttaksperioderManueltBekreftelse(List.of(lagUttakResultatPeriode())));
    }

    @Test
    void FatterVedtakBekreftelseTest() {
        test(new FatterVedtakBekreftelse(List.of(new FatterVedtakBekreftelse.AksjonspunktGodkjenningDto(
                "2015", List.of("aarsak1"), "begrunnelse", true))));
    }

    @Test
    void FordelBeregningsgrunnlagBekreftelseTest() {
        test(new FordelBeregningsgrunnlagBekreftelse(List.of(lagFastsettBeregningsgrunnlagPeriodeDto())));
    }

    @Test
    void ForeslåVedtakBekreftelseTest() {
        test(new ForeslåVedtakBekreftelse(new Kode("4001"), "fritekst", true,
                false));
    }

    @Test
    void KlageFormkravKaTest() {
        KlageFormkravKa klageFormkravKa = new KlageFormkravKa();
        klageFormkravKa.godkjennAlleFormkrav();
        test(klageFormkravKa);
    }

    @Test
    void KlageFormkravNfpTest() {
        KlageFormkravNfp klageFormkravNfp = new KlageFormkravNfp();
        klageFormkravNfp.klageErIkkeKonkret();
        test(klageFormkravNfp);
    }

    @Test
    void MannAdoptererAleneBekreftelseTest() {
        test(new MannAdoptererAleneBekreftelse(true));
    }

    @Test
    void VarselOmRevurderingBekreftelseTest() {
        test(new VarselOmRevurderingBekreftelse("Begrunnelse for varsel", "fritekst /nm", "varsel",
                LocalDate.now(), "ARBEID"));
    }

    @Test
    void VurderBeregnetInntektsAvvikBekreftelseTest() {
        var vurderBeregnetInntektsAvvikBekreftelse = new VurderBeregnetInntektsAvvikBekreftelse();
        vurderBeregnetInntektsAvvikBekreftelse
                .leggTilInntekt(30_000, 1)
                .leggTilInntekt(45_000, 2)
                .leggTilInntektFrilans(20_000);
        test(vurderBeregnetInntektsAvvikBekreftelse);
    }

    @Test
    void VurderEktefellesBarnBekreftelseTest() {
        var vurderEktefellesBarnBekreftelse = new VurderEktefellesBarnBekreftelse();
        vurderEktefellesBarnBekreftelse.bekreftBarnErEktefellesBarn();
        test(vurderEktefellesBarnBekreftelse);
    }

    @Test
    void VurderFaktaOmBeregningBekreftelseTest() {
        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse =
                new VurderFaktaOmBeregningBekreftelse(new FaktaOmBeregningLagreDto());
        vurderFaktaOmBeregningBekreftelse
                .leggTilMaanedsinntektFL(20_000)
                .leggTilMaanedsinntektUtenInntektsmelding(
                        List.of(new FastsettMaanedsinntektUtenInntektsmeldingAndel(1L, 25_000)))
                .leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.VURDER_MOTTAR_YTELSE.toString())
                .leggTilMottarYtelseFrilans(false)
                .leggTilAndelerEndretBg(lagBeregningsgrunnlagPeriodeDto(), lagBeregningsgrunnlagPrStatusOgAndelDto(),
                        new FastsatteVerdier(1, 231, new Kode("ARBEIDSGIVER")))
                .leggTilBesteBeregningAndeler(2000.0, new Kode("ARBEIDSGIVER"))
                .leggTilVurderTidsbegrenset(false)
                .leggTilAndelerYtelse(20_000.0, new Kode("ARBEIDSGIVER"))
                .leggTilVurdertLønnsendring(true)
                .leggTilRefusjonGyldighetVurdering("910909088", false);
        test(vurderFaktaOmBeregningBekreftelse);
    }

    @Test
    void VurderingAvForeldreansvarAndreLeddTest() {
        var vurderingAvForeldreansvarAndreLedd = new VurderingAvForeldreansvarAndreLedd();
        vurderingAvForeldreansvarAndreLedd.bekreftAvvist("4001");
        test(vurderingAvForeldreansvarAndreLedd);
    }

    @Test
    void VurderingAvForeldreansvarFjerdeLeddTest() {
        var vurderingAvForeldreansvarFjerdeLedd = new VurderingAvForeldreansvarFjerdeLedd();
        vurderingAvForeldreansvarFjerdeLedd.bekreftAvvist("4002");
        test(vurderingAvForeldreansvarFjerdeLedd);
    }

    @Test
    void VurderingAvInnsynBekreftelseTest() {
        var vurderingAvInnsynBekreftelse = new VurderingAvInnsynBekreftelse();
        vurderingAvInnsynBekreftelse
                .setMottattDato(LocalDate.now())
                .setInnsynDokumenter(List.of("object1", 2, false))
                .setInnsynResultatType(new Kode("INNSYN_RESULTAT_TYPE", "AVVIST"))
                .skalSetteSakPåVent(true);
        test(vurderingAvInnsynBekreftelse);
    }

    @Test
    void VurderingAvKlageNfpBekreftelseTest() {
        test(new VurderingAvKlageBekreftelse.VurderingAvKlageNfpBekreftelse().bekreftMedholdDelvisGunst("årsak"));
    }

    @Test
    void VurderingAvKlageNkBekreftelseTest() {
        test(new VurderingAvKlageBekreftelse.VurderingAvKlageNkBekreftelse().bekreftOpphevet("2001"));
    }

    @Test
    void VurderingAvOmsorgsvilkoretTest() {
        var vurderingAvOmsorgsvilkoret = new VurderingAvOmsorgsvilkoret();
        vurderingAvOmsorgsvilkoret.bekreftAvvist(new Kode("4033"));
        test(vurderingAvOmsorgsvilkoret);
    }

    @Test
    void VurderManglendeFodselBekreftelseTest() {
        var vurderManglendeFodselBekreftelse = new VurderManglendeFodselBekreftelse();
        vurderManglendeFodselBekreftelse
                .bekreftDokumentasjonForeligger(2, LocalDate.now().minusWeeks(2))
                .bekreftBrukAntallBarnITps();
        test(vurderManglendeFodselBekreftelse);
    }

    @Test
    void VurderOmsorgForBarnBekreftelseTest() {
        var vurderOmsorgForBarnBekreftelse = new VurderOmsorgForBarnBekreftelse();
        vurderOmsorgForBarnBekreftelse.bekreftBrukerHarIkkeOmsorg(LocalDate.now(), LocalDate.now().plusMonths(3));
        test(vurderOmsorgForBarnBekreftelse);
    }

    @Test
    void VurderPerioderOpptjeningBekreftelseTest() {
        test(new VurderPerioderOpptjeningBekreftelse(List.of(lagOpptjeningAktivitet(), lagOpptjeningAktivitet())));
    }

    @Test
    void VurderSoknadsfristBekreftelseTest() {
        VurderSoknadsfristBekreftelse vurderSoknadsfristBekreftelse = new VurderSoknadsfristBekreftelse();
        vurderSoknadsfristBekreftelse.setErVilkarOk(true);
        vurderSoknadsfristBekreftelse.setMottattDato(LocalDate.now());
        vurderSoknadsfristBekreftelse.setOmsorgsovertakelseDato(LocalDate.now().plusWeeks(4));
        test(vurderSoknadsfristBekreftelse);
    }

    @Test
    void VurderSoknadsfristForeldrepengerBekreftelseTest() {
        var vurderSoknadsfristForeldrepengerBekreftelse = new VurderSoknadsfristForeldrepengerBekreftelse();
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(LocalDate.now());
        test(vurderSoknadsfristForeldrepengerBekreftelse);
    }

    @Test
    void VurderTilbakekrevingVedFeilutbetalingBekreftelseTest() {
        var vurderTilbakekrevingVedFeilutbetalingBekreftelse = new VurderTilbakekrevingVedFeilutbetalingBekreftelse();
        vurderTilbakekrevingVedFeilutbetalingBekreftelse.setTilbakekrevFrasøker(false);
        test(vurderTilbakekrevingVedFeilutbetalingBekreftelse);
    }

    @Test
    void VurderTilbakekrevingVedNegativSimuleringTest() {
        var vurderTilbakekrevingVedNegativSimulering = new VurderTilbakekrevingVedNegativSimulering();
        vurderTilbakekrevingVedNegativSimulering.setTilbakekrevingMedVarsel("WARNING!");
        test(vurderTilbakekrevingVedNegativSimulering);
    }

    @Test
    void VurderVarigEndringEllerNyoppstartetSNBekreftelseTest() {
        var vurderVarigEndringEllerNyoppstartetSNBekreftelse = new VurderVarigEndringEllerNyoppstartetSNBekreftelse();
        vurderVarigEndringEllerNyoppstartetSNBekreftelse.setErVarigEndretNaering(true);
        vurderVarigEndringEllerNyoppstartetSNBekreftelse.setBruttoBeregningsgrunnlag(200_000);
        test(vurderVarigEndringEllerNyoppstartetSNBekreftelse);
    }

    @Test
    void VurderVilkaarForSykdomBekreftelseTest() {
        var vurderVilkaarForSykdomBekreftelse = new VurderVilkaarForSykdomBekreftelse();
        vurderVilkaarForSykdomBekreftelse.setErMorForSykVedFodsel(false);
        test(vurderVilkaarForSykdomBekreftelse);
    }

    private OpptjeningAktivitet lagOpptjeningAktivitet() {
        return new OpptjeningAktivitet(new Kode("2001"), "REF-ID-1234","Sopra Steria",
                "ID-1234", "begrunnelse", LocalDate.now(), LocalDate.now(),
                LocalDate.now().plusDays(12), LocalDate.now().plusWeeks(2),false,true,
                false, "910909088");
    }

    private FastsettEndretBeregningsgrunnlag lagFastsettEndretBeregningsgrunnlag() {
        return new FastsettEndretBeregningsgrunnlag(
                List.of(new FastsettEndretBeregningsgrunnlagPeriode(List.of(lagFastsettEndretBeregningsgrunnlagPeriode()),
                    LocalDate.now(),
                    LocalDate.now().plusMonths(1))));
    }

    private FastsettEndretBeregningsgrunnlagAndel lagFastsettEndretBeregningsgrunnlagPeriode() {
        return new FastsettEndretBeregningsgrunnlagAndel(
                new RedigerbarAndel("andel",1L,"910909088","AR-0001",
                        true, false, new Kode("ARBEIDSGIVER"), LocalDate.now(), LocalDate.now().minusDays(1),
                        new Kode("ARBEIDSGIVER")),
                new FastsatteVerdier(123456,123456, new Kode("ARBEIDSGIVER")));
    }

    private MottarYtelse lagMottarYtelse() {
        return new MottarYtelse(false, List.of(
                new ArbeidstakerandelUtenIMMottarYtelse(1L, true),
                new ArbeidstakerandelUtenIMMottarYtelse(2L, false)
        ));
    }

    private FastsettBgKunYtelseDto lagYtelseForedeling() {
        return new FastsettBgKunYtelseDto(
                List.of(new FastsettBrukersAndel(12, 20_000.0,
                        "ARBEIDSTAKER",true, false)),
                true
        );
    }

    private BesteberegningFødendeKvinneDto lagBesteberegningFødendeKvinneDto() {
        var besteberegningFødendeKvinneDto = new BesteberegningFødendeKvinneDto();
        besteberegningFødendeKvinneDto.leggTilBesteberegningAndel(
                new BesteberegningFødendeKvinneAndelDto(20_000,"ARBEIDSTAKER"));
        besteberegningFødendeKvinneDto.setNyDagpengeAndel(
                new DagpengeAndelLagtTilBesteberegningDto(new FastsatteVerdierForBesteberegningDto(20_000.0,
                        "ARBEIDSTAKER")));
        return besteberegningFødendeKvinneDto;
    }

    private BeregningsaktivitetLagreDto lagBeregningsaktivitetLagreDto() {
        return new BeregningsaktivitetLagreDto(new Kode("2004"), LocalDate.now().minusDays(6), LocalDate.now(),
                "910909088", "AR231231432154653","arbeidsforholdRef",
                true);
    }

    private Arbeidsgiver lagArbeidsgiver() {
        if (arbeidsgiver == null) {
            arbeidsgiver = new Arbeidsgiver("Indikator", "NAV", "1234567", true);
        }
        return arbeidsgiver;
    }

    private UttakResultatPeriode lagUttakResultatPeriode() {
        if (uttakResultatPeriode == null) {
            uttakResultatPeriode = new UttakResultatPeriode(LocalDate.now().minusDays(1), LocalDate.now(),
                    List.of(lagUttakResultatPeriodeAktivitet()), new Kode("2001"), "Begrunnelses",
                    new Kode("2001"), new Kode("2432"), new Kode("1234"),false, true,
                    BigDecimal.valueOf(87), true, new Kode("5432"),ARBEID,
                    OppholdÅrsak.FEDREKVOTE_ANNEN_FORELDER, lagUttakResultatPeriodeAktivitet());
        }
        return uttakResultatPeriode;
    }

    private UttakResultatPeriodeAktivitet lagUttakResultatPeriodeAktivitet() {
        return new UttakResultatPeriodeAktivitet(FORELDREPENGER_FØR_FØDSEL, BigDecimal.valueOf(10),
                BigDecimal.valueOf(50), BigDecimal.valueOf(50), new Kode("5003"),
                lagArbeidsgiver(), BigDecimal.valueOf(120), "UAFA-123-21312", true);
    }

    private FastsettBeregningsgrunnlagPeriodeDto lagFastsettBeregningsgrunnlagPeriodeDto() {
        return new FastsettBeregningsgrunnlagPeriodeDto(List.of(lagFastsettBeregningsgrunnlagAndelDto()),
                LocalDate.now(), LocalDate.now().plusMonths(5));
    }

    private FastsettBeregningsgrunnlagAndelDto lagFastsettBeregningsgrunnlagAndelDto() {
        var fastsettBeregningsgrunnlagAndelDto = new FastsettBeregningsgrunnlagAndelDto(
                lagFordelBeregningsgrunnlagAndelDto(),
                lagBeregningsgrunnlagPrStatusOgAndelDto());
        fastsettBeregningsgrunnlagAndelDto.setFastsatteVerdier(lagFastsatteVerdierDto());
        return fastsettBeregningsgrunnlagAndelDto;
    }

    private FastsatteVerdierDto lagFastsatteVerdierDto() {
        return new FastsatteVerdierDto(123456, 200_000, 300_000, 500_000,
                new Kode("ARBEIDSTAKER"), true);
    }

    private BeregningsgrunnlagArbeidsforholdDto lagBeregningsgrunnlagArbeidsforholdDto() {
        var beregningsgrunnlagArbeidsforholdDto = new BeregningsgrunnlagArbeidsforholdDto();
        beregningsgrunnlagArbeidsforholdDto.setArbeidsgiverNavn("BEDRIFT AS");
        beregningsgrunnlagArbeidsforholdDto.setArbeidsgiverId("910909088");
        beregningsgrunnlagArbeidsforholdDto.setStartdato("2016-08-26");
        beregningsgrunnlagArbeidsforholdDto.setOpphoersdato("2016-10-26");
        beregningsgrunnlagArbeidsforholdDto.setArbeidsforholdId("AR-123456789");
        beregningsgrunnlagArbeidsforholdDto.setArbeidsforholdType(new Kode("OPPTJENING_AKTIVITET_TYPE", "ARBEID"));
        beregningsgrunnlagArbeidsforholdDto.setAktørId(new AktørId("123456789"));
        beregningsgrunnlagArbeidsforholdDto.setRefusjonPrAar(0.0);
        return beregningsgrunnlagArbeidsforholdDto;
    }

    private BeregningsgrunnlagPrStatusOgAndelDto lagBeregningsgrunnlagPrStatusOgAndelDto() {
        var beregningsgrunnlagPrStatusOgAndelDto = new BeregningsgrunnlagPrStatusOgAndelDto();
        beregningsgrunnlagPrStatusOgAndelDto.setBeregningsgrunnlagTom(LocalDate.now());
        beregningsgrunnlagPrStatusOgAndelDto.setBeregningsgrunnlagFom(LocalDate.now().plusDays(1));
        beregningsgrunnlagPrStatusOgAndelDto.setAktivitetStatus(new Kode("AKTIVITET_STATUS", "AT"));
        beregningsgrunnlagPrStatusOgAndelDto.setBeregningsperiodeFom(LocalDate.now());
        beregningsgrunnlagPrStatusOgAndelDto.setBeregningsperiodeTom(LocalDate.now().plusDays(1));
        beregningsgrunnlagPrStatusOgAndelDto.setBeregnetPrAar(480000.0);
        beregningsgrunnlagPrStatusOgAndelDto.setOverstyrtPrAar(0.0);
        beregningsgrunnlagPrStatusOgAndelDto.setBruttoPrAar(480000.0);
        beregningsgrunnlagPrStatusOgAndelDto.setAvkortetPrAar(480000.0);
        beregningsgrunnlagPrStatusOgAndelDto.setRedusertPrAar(480000.0);
        beregningsgrunnlagPrStatusOgAndelDto.setFordeltPrAar(0.0);
        beregningsgrunnlagPrStatusOgAndelDto.setErTidsbegrensetArbeidsforhold(false);
        beregningsgrunnlagPrStatusOgAndelDto.setErNyIArbeidslivet(false);
        beregningsgrunnlagPrStatusOgAndelDto.setLonnsendringIBeregningsperioden(false);
        beregningsgrunnlagPrStatusOgAndelDto.setAndelsnr(1L);
        beregningsgrunnlagPrStatusOgAndelDto.setBesteberegningPrAar(0.0);
        beregningsgrunnlagPrStatusOgAndelDto.setInntektskategori(new Kode("INNTEKTSKATEGORI", "ARBEIDSTAKER"));
        beregningsgrunnlagPrStatusOgAndelDto.setArbeidsforhold(lagBeregningsgrunnlagArbeidsforholdDto());
        beregningsgrunnlagPrStatusOgAndelDto.setFastsattAvSaksbehandler(false);
        beregningsgrunnlagPrStatusOgAndelDto.setBortfaltNaturalytelse(0.0);
        beregningsgrunnlagPrStatusOgAndelDto.setDagsats(1846.0);
        return beregningsgrunnlagPrStatusOgAndelDto;
    }

    private BeregningsgrunnlagPeriodeDto lagBeregningsgrunnlagPeriodeDto() {
        var beregningsgrunnlagPeriodeDto = new BeregningsgrunnlagPeriodeDto();
        beregningsgrunnlagPeriodeDto.setBeregningsgrunnlagPeriodeFom(LocalDate.now());
        beregningsgrunnlagPeriodeDto.setBeregningsgrunnlagPeriodeTom(LocalDate.now().plusMonths(1));
        beregningsgrunnlagPeriodeDto.setBeregnetPrAar(480000.0);
        beregningsgrunnlagPeriodeDto.setBruttoPrAar(480000.0);
        beregningsgrunnlagPeriodeDto.setBruttoInkludertBortfaltNaturalytelsePrAar(480000.0);
        beregningsgrunnlagPeriodeDto.setAvkortetPrAar(480000.0);
        beregningsgrunnlagPeriodeDto.setRedusertPrAar(480000.0);
        beregningsgrunnlagPeriodeDto.setPeriodeAarsaker(List.of(new Kode("ÅRSAK")));
        beregningsgrunnlagPeriodeDto.setDagsats(1846);
        beregningsgrunnlagPeriodeDto.setBeregningsgrunnlagPrStatusOgAndel(List.of(lagBeregningsgrunnlagPrStatusOgAndelDto()));
        return beregningsgrunnlagPeriodeDto;
    }

    private FordelBeregningsgrunnlagAndelDto lagFordelBeregningsgrunnlagAndelDto() {
        var fordelBeregningsgrunnlagAndelDto = new FordelBeregningsgrunnlagAndelDto();
        fordelBeregningsgrunnlagAndelDto.setFordelingForrigeBehandlingPrAar(BigDecimal.valueOf(500_000));
        fordelBeregningsgrunnlagAndelDto.setRefusjonskravPrAar(BigDecimal.ZERO);
        fordelBeregningsgrunnlagAndelDto.setFordeltPrAar(BigDecimal.valueOf(200_000));
        fordelBeregningsgrunnlagAndelDto.setBelopFraInntektsmeldingPrAar(BigDecimal.valueOf(250_000));
        fordelBeregningsgrunnlagAndelDto.setFastsattForrigePrAar(BigDecimal.valueOf(300_000));
        fordelBeregningsgrunnlagAndelDto.setRefusjonskravFraInntektsmeldingPrAar(BigDecimal.valueOf(300_000));
        fordelBeregningsgrunnlagAndelDto.setNyttArbeidsforhold(true);
        fordelBeregningsgrunnlagAndelDto.setArbeidsforholdType(new Kode("ARBEIDS_FORHOLD_TYPE","ARBEIDSGIVER"));
        fordelBeregningsgrunnlagAndelDto.setAndelsnr(1L);
        fordelBeregningsgrunnlagAndelDto.setArbeidsforhold(lagBeregningsgrunnlagArbeidsforholdDto());
        fordelBeregningsgrunnlagAndelDto.setInntektskategori(new Kode("ARBEIDSGIVER"));
        fordelBeregningsgrunnlagAndelDto.setAktivitetStatus(new Kode("AKTIVITET_STATUS", "AT", "Arbeidstaker"));
        fordelBeregningsgrunnlagAndelDto.setLagtTilAvSaksbehandler(true);
        fordelBeregningsgrunnlagAndelDto.setFastsattAvSaksbehandler(false);
        fordelBeregningsgrunnlagAndelDto.setAndelIArbeid(List.of(0.0, 20_000.0));
        return fordelBeregningsgrunnlagAndelDto;
    }

}
