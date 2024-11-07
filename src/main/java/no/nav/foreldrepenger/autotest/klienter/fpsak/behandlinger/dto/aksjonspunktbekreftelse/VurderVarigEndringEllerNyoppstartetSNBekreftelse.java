package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

public class VurderVarigEndringEllerNyoppstartetSNBekreftelse extends AksjonspunktBekreftelse {

    protected boolean erVarigEndretNaering;
    protected Integer bruttoBeregningsgrunnlag = null;

    public VurderVarigEndringEllerNyoppstartetSNBekreftelse setErVarigEndretNaering(boolean erVarigEndretNaering) {
        this.erVarigEndretNaering = erVarigEndretNaering;
        return this;
    }

    public VurderVarigEndringEllerNyoppstartetSNBekreftelse setBruttoBeregningsgrunnlag(int bruttoBeregningsgrunnlag) {
        this.bruttoBeregningsgrunnlag = bruttoBeregningsgrunnlag;
        return this;
    }

    public boolean hentErVarigEndretNaering() {
        return erVarigEndretNaering;
    }

    @Override
    public String aksjonspunktKode() {
        return "5039";
    }
}
