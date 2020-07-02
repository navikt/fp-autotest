package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@BekreftelseKode(kode = "5039")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderVarigEndringEllerNyoppstartetSNBekreftelse extends AksjonspunktBekreftelse {

    private boolean erVarigEndretNaering;
    private Integer bruttoBeregningsgrunnlag;

    public VurderVarigEndringEllerNyoppstartetSNBekreftelse() {
        super();
    }

    public boolean isErVarigEndretNaering() {
        return erVarigEndretNaering;
    }

    public Integer getBruttoBeregningsgrunnlag() {
        return bruttoBeregningsgrunnlag;
    }

    public VurderVarigEndringEllerNyoppstartetSNBekreftelse setErVarigEndretNaering(boolean erVarigEndretNaering) {
        this.erVarigEndretNaering = erVarigEndretNaering;
        return this;
    }

    public VurderVarigEndringEllerNyoppstartetSNBekreftelse setBruttoBeregningsgrunnlag(int bruttoBeregningsgrunnlag) {
        this.bruttoBeregningsgrunnlag = bruttoBeregningsgrunnlag;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderVarigEndringEllerNyoppstartetSNBekreftelse that = (VurderVarigEndringEllerNyoppstartetSNBekreftelse) o;
        return erVarigEndretNaering == that.erVarigEndretNaering &&
                Objects.equals(bruttoBeregningsgrunnlag, that.bruttoBeregningsgrunnlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(erVarigEndretNaering, bruttoBeregningsgrunnlag);
    }

    @Override
    public String toString() {
        return "VurderVarigEndringEllerNyoppstartetSNBekreftelse{" +
                "erVarigEndretNaering=" + erVarigEndretNaering +
                ", bruttoBeregningsgrunnlag=" + bruttoBeregningsgrunnlag +
                "} " + super.toString();
    }
}
