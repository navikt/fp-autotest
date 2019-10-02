package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FordelBeregningsgrunnlagPeriodeDto {

    protected LocalDate fom;
    protected LocalDate tom;
    protected List<FordelBeregningsgrunnlagAndelDto> fordelBeregningsgrunnlagAndeler = new ArrayList<>();
    protected boolean harPeriodeAarsakGraderingEllerRefusjon = false;
    protected boolean skalKunneEndreRefusjon = false;

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public List<FordelBeregningsgrunnlagAndelDto> getFordelBeregningsgrunnlagAndeler() {
        return fordelBeregningsgrunnlagAndeler;
    }

    public boolean isHarPeriodeAarsakGraderingEllerRefusjon() {
        return harPeriodeAarsakGraderingEllerRefusjon;
    }

    public boolean isSkalKunneEndreRefusjon() {
        return skalKunneEndreRefusjon;
    }
}
