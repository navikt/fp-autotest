package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Beregningsgrunnlag {

    private LocalDate skjaeringstidspunktBeregning;
    private LocalDate skjæringstidspunkt;
    private List<AktivitetStatus> aktivitetStatus;
    private List<BeregningsgrunnlagPeriodeDto> beregningsgrunnlagPeriode;
    private SammenligningsgrunnlagDto sammenligningsgrunnlag;
    private List<SammenligningsgrunnlagDto> sammenligningsgrunnlagPrStatus;
    private Double halvG;
    private FaktaOmBeregningDto faktaOmBeregning;
    private List<BeregningsgrunnlagPrStatusOgAndelDto> andelerMedGraderingUtenBG;
    private FaktaOmFordelingDto faktaOmFordeling;
    private String ledetekstAvkortet;
    private String ledetekstBrutto;
    private String ledetekstRedusert;

    public int antallAktivitetStatus() {
        return aktivitetStatus.size();
    }

    public AktivitetStatus getAktivitetStatus(int index) {
        return aktivitetStatus.get(index);
    }

    public int antallBeregningsgrunnlagPeriodeDto() {
        return beregningsgrunnlagPeriode.size();
    }

    public BeregningsgrunnlagPeriodeDto getBeregningsgrunnlagPeriode(int index) {
        return beregningsgrunnlagPeriode.get(index);
    }

    public BeregningsgrunnlagPeriodeDto getBeregningsgrunnlagPeriode(LocalDate fom) {
        return beregningsgrunnlagPeriode.stream().filter(p -> p.getBeregningsgrunnlagPeriodeFom().equals(fom))
                .findFirst().orElseThrow();
    }

    public List<BeregningsgrunnlagPeriodeDto> getBeregningsgrunnlagPerioder() {
        return beregningsgrunnlagPeriode;
    }

    public LocalDate getSkjaeringstidspunktBeregning() {
        return skjaeringstidspunktBeregning;
    }

    public FaktaOmBeregningDto getFaktaOmBeregning() {
        return faktaOmBeregning;
    }

    public FaktaOmFordelingDto getFaktaOmFordeling() {
        return faktaOmFordeling;
    }

    public Double getHalvG() {
        return halvG;
    }
}
