package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BeregningsgrunnlagPeriodeDto {
    private LocalDate beregningsgrunnlagPeriodeFom;
    private LocalDate beregningsgrunnlagPeriodeTom;
    private double beregnetPrAar;
    private double bruttoPrAar;
    private double bruttoInkludertBortfaltNaturalytelsePrAar;
    private double avkortetPrAar;
    private double redusertPrAar;
    private List<Kode> periodeAarsaker;
    private int dagsats;
    private List<BeregningsgrunnlagPrStatusOgAndelDto> beregningsgrunnlagPrStatusOgAndel;


    public LocalDate getBeregningsgrunnlagPeriodeFom() {
        return beregningsgrunnlagPeriodeFom;
    }

    public void setBeregningsgrunnlagPeriodeFom(LocalDate beregningsgrunnlagPeriodeFom) {
        this.beregningsgrunnlagPeriodeFom = beregningsgrunnlagPeriodeFom;
    }

    public LocalDate getBeregningsgrunnlagPeriodeTom() {
        return beregningsgrunnlagPeriodeTom;
    }

    public void setBeregningsgrunnlagPeriodeTom(LocalDate beregningsgrunnlagPeriodeTom) {
        this.beregningsgrunnlagPeriodeTom = beregningsgrunnlagPeriodeTom;
    }

    public double getBeregnetPrAar() {
        return beregnetPrAar;
    }

    public void setBeregnetPrAar(double beregnetPrAar) {
        this.beregnetPrAar = beregnetPrAar;
    }

    public double getBruttoPrAar() {
        return bruttoPrAar;
    }

    public void setBruttoPrAar(double bruttoPrAar) {
        this.bruttoPrAar = bruttoPrAar;
    }

    public double getBruttoInkludertBortfaltNaturalytelsePrAar() {
        return bruttoInkludertBortfaltNaturalytelsePrAar;
    }

    public void setBruttoInkludertBortfaltNaturalytelsePrAar(double bruttoInkludertBortfaltNaturalytelsePrAar) {
        this.bruttoInkludertBortfaltNaturalytelsePrAar = bruttoInkludertBortfaltNaturalytelsePrAar;
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

    public List<Kode> getPeriodeAarsaker() {
        return periodeAarsaker;
    }

    public void setPeriodeAarsaker(List<Kode> periodeAarsaker) {
        this.periodeAarsaker = periodeAarsaker;
    }

    public int getDagsats() {
        return dagsats;
    }

    public void setDagsats(int dagsats) {
        this.dagsats = dagsats;
    }

    public List<BeregningsgrunnlagPrStatusOgAndelDto> getBeregningsgrunnlagPrStatusOgAndel() {
        return beregningsgrunnlagPrStatusOgAndel;
    }

    public void setBeregningsgrunnlagPrStatusOgAndel(List<BeregningsgrunnlagPrStatusOgAndelDto> beregningsgrunnlagPrStatusOgAndel) {
        this.beregningsgrunnlagPrStatusOgAndel = beregningsgrunnlagPrStatusOgAndel;
    }
}
