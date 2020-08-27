package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@BekreftelseKode(kode = "5042")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettBruttoBeregningsgrunnlagSNBekreftelse extends AksjonspunktBekreftelse {

    private Integer bruttoBeregningsgrunnlag;

    public FastsettBruttoBeregningsgrunnlagSNBekreftelse() {
        super();
    }

    public Integer getBruttoBeregningsgrunnlag() {
        return bruttoBeregningsgrunnlag;
    }

    public FastsettBruttoBeregningsgrunnlagSNBekreftelse setBruttoBeregningsgrunnlag(Integer bruttoBeregningsgrunnlag) {
        this.bruttoBeregningsgrunnlag = bruttoBeregningsgrunnlag;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsettBruttoBeregningsgrunnlagSNBekreftelse that = (FastsettBruttoBeregningsgrunnlagSNBekreftelse) o;
        return Objects.equals(bruttoBeregningsgrunnlag, that.bruttoBeregningsgrunnlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bruttoBeregningsgrunnlag);
    }

    @Override
    public String toString() {
        return "FastsettBruttoBeregningsgrunnlagSNBekreftelse{" +
                "bruttoBeregningsgrunnlag=" + bruttoBeregningsgrunnlag +
                ", kode='" + kode + '\'' +
                ", begrunnelse='" + begrunnelse + '\'' +
                '}';
    }
}
