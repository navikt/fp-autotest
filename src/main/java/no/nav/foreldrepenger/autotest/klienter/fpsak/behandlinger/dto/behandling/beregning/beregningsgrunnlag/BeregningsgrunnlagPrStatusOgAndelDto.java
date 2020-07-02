package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BeregningsgrunnlagPrStatusOgAndelDto {

    private LocalDate beregningsgrunnlagTom;
    private LocalDate beregningsgrunnlagFom;
    private Kode aktivitetStatus;
    private LocalDate beregningsperiodeFom;
    private LocalDate beregningsperiodeTom;
    private double beregnetPrAar;
    private double overstyrtPrAar;
    private double bruttoPrAar;
    private double avkortetPrAar;
    private double redusertPrAar;
    private double fordeltPrAar;
    private boolean erTidsbegrensetArbeidsforhold;
    private boolean erNyIArbeidslivet;
    private boolean lonnsendringIBeregningsperioden;
    private int andelsnr;
    private double besteberegningPrAar;
    private Kode inntektskategori;
    private BeregningsgrunnlagArbeidsforholdDto arbeidsforhold;
    private boolean fastsattAvSaksbehandler;
    private double bortfaltNaturalytelse;
    private double dagsats;

    public LocalDate getBeregningsgrunnlagTom() {
        return beregningsgrunnlagTom;
    }

    public void setBeregningsgrunnlagTom(LocalDate beregningsgrunnlagTom) {
        this.beregningsgrunnlagTom = beregningsgrunnlagTom;
    }

    public LocalDate getBeregningsgrunnlagFom() {
        return beregningsgrunnlagFom;
    }

    public void setBeregningsgrunnlagFom(LocalDate beregningsgrunnlagFom) {
        this.beregningsgrunnlagFom = beregningsgrunnlagFom;
    }

    public Kode getAktivitetStatus() {
        return aktivitetStatus;
    }

    public void setAktivitetStatus(Kode aktivitetStatus) {
        this.aktivitetStatus = aktivitetStatus;
    }

    public LocalDate getBeregningsperiodeFom() {
        return beregningsperiodeFom;
    }

    public void setBeregningsperiodeFom(LocalDate beregningsperiodeFom) {
        this.beregningsperiodeFom = beregningsperiodeFom;
    }

    public LocalDate getBeregningsperiodeTom() {
        return beregningsperiodeTom;
    }

    public void setBeregningsperiodeTom(LocalDate beregningsperiodeTom) {
        this.beregningsperiodeTom = beregningsperiodeTom;
    }

    public double getBeregnetPrAar() {
        return beregnetPrAar;
    }

    public void setBeregnetPrAar(double beregnetPrAar) {
        this.beregnetPrAar = beregnetPrAar;
    }

    public double getOverstyrtPrAar() {
        return overstyrtPrAar;
    }

    public void setOverstyrtPrAar(double overstyrtPrAar) {
        this.overstyrtPrAar = overstyrtPrAar;
    }

    public double getBruttoPrAar() {
        return bruttoPrAar;
    }

    public void setBruttoPrAar(double bruttoPrAar) {
        this.bruttoPrAar = bruttoPrAar;
    }

    public double getAvkortetPrAar() {
        return avkortetPrAar;
    }

    public void setAvkortetPrAar(double avkortetPrAar) {
        this.avkortetPrAar = avkortetPrAar;
    }

    public double getRedusertPrAar() {
        return redusertPrAar;
    }

    public void setRedusertPrAar(double redusertPrAar) {
        this.redusertPrAar = redusertPrAar;
    }

    public double getFordeltPrAar() {
        return fordeltPrAar;
    }

    public void setFordeltPrAar(double fordeltPrAar) {
        this.fordeltPrAar = fordeltPrAar;
    }

    public boolean isErTidsbegrensetArbeidsforhold() {
        return erTidsbegrensetArbeidsforhold;
    }

    public void setErTidsbegrensetArbeidsforhold(boolean erTidsbegrensetArbeidsforhold) {
        this.erTidsbegrensetArbeidsforhold = erTidsbegrensetArbeidsforhold;
    }

    public boolean isErNyIArbeidslivet() {
        return erNyIArbeidslivet;
    }

    public void setErNyIArbeidslivet(boolean erNyIArbeidslivet) {
        this.erNyIArbeidslivet = erNyIArbeidslivet;
    }

    public boolean isLonnsendringIBeregningsperioden() {
        return lonnsendringIBeregningsperioden;
    }

    public void setLonnsendringIBeregningsperioden(boolean lonnsendringIBeregningsperioden) {
        this.lonnsendringIBeregningsperioden = lonnsendringIBeregningsperioden;
    }

    public int getAndelsnr() {
        return andelsnr;
    }

    public void setAndelsnr(int andelsnr) {
        this.andelsnr = andelsnr;
    }

    public double getBesteberegningPrAar() {
        return besteberegningPrAar;
    }

    public void setBesteberegningPrAar(double besteberegningPrAar) {
        this.besteberegningPrAar = besteberegningPrAar;
    }

    public Kode getInntektskategori() {
        return inntektskategori;
    }

    public void setInntektskategori(Kode inntektskategori) {
        this.inntektskategori = inntektskategori;
    }

    public BeregningsgrunnlagArbeidsforholdDto getArbeidsforhold() {
        return arbeidsforhold;
    }

    public void setArbeidsforhold(BeregningsgrunnlagArbeidsforholdDto arbeidsforhold) {
        this.arbeidsforhold = arbeidsforhold;
    }

    public boolean isFastsattAvSaksbehandler() {
        return fastsattAvSaksbehandler;
    }

    public void setFastsattAvSaksbehandler(boolean fastsattAvSaksbehandler) {
        this.fastsattAvSaksbehandler = fastsattAvSaksbehandler;
    }

    public double getBortfaltNaturalytelse() {
        return bortfaltNaturalytelse;
    }

    public void setBortfaltNaturalytelse(double bortfaltNaturalytelse) {
        this.bortfaltNaturalytelse = bortfaltNaturalytelse;
    }

    public double getDagsats() {
        return dagsats;
    }

    public void setDagsats(double dagsats) {
        this.dagsats = dagsats;
    }
}
