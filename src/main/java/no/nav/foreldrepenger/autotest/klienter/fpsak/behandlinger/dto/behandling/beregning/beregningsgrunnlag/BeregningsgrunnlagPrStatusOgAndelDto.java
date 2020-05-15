package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeregningsgrunnlagPrStatusOgAndelDto {

    protected LocalDate beregningsgrunnlagTom;
    protected LocalDate beregningsgrunnlagFom;
    protected Kode aktivitetStatus;
    protected LocalDate beregningsperiodeFom;
    protected LocalDate beregningsperiodeTom;
    protected double beregnetPrAar;
    protected double overstyrtPrAar;
    protected double bruttoPrAar;
    protected double avkortetPrAar;
    protected double redusertPrAar;
    protected double fordeltPrAar;
    protected boolean erTidsbegrensetArbeidsforhold;
    protected boolean erNyIArbeidslivet;
    protected boolean lonnsendringIBeregningsperioden;
    protected int andelsnr;
    protected double besteberegningPrAar;
    protected Kode inntektskategori;
    protected BeregningsgrunnlagArbeidsforholdDto arbeidsforhold;
    protected boolean fastsattAvSaksbehandler;
    protected double bortfaltNaturalytelse;
    protected double dagsats;

    public Kode getAktivitetStatus() {
        return aktivitetStatus;
    }

    public BeregningsgrunnlagArbeidsforholdDto getArbeidsforhold() {
        return arbeidsforhold;
    }

    public int getAndelsnr() {
        return andelsnr;
    }

    public double getBeregnetPrAar() {
        return beregnetPrAar;
    }

    public double getBortfaltNaturalytelse() {
        return bortfaltNaturalytelse;
    }

    public double getBruttoPrAar() {
        return bruttoPrAar;
    }

    public double getFordeltPrAar() {
        return fordeltPrAar;
    }

    public LocalDate getBeregningsperiodeFom() {
        return beregningsperiodeFom;
    }

    public LocalDate getBeregningsperiodeTom() {
        return beregningsperiodeTom;
    }

    public LocalDate getBeregningsgrunnlagTom() {
        return beregningsgrunnlagTom;
    }

    public LocalDate getBeregningsgrunnlagFom() {
        return beregningsgrunnlagFom;
    }

    public double getDagsats() {
        return dagsats;
    }

    public double getRedusertPrAar() {
        return redusertPrAar;
    }
}
