package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode = "5039")
public class VurderVarigEndringEllerNyoppstartetSNBekreftelse extends AksjonspunktBekreftelse {

    protected boolean erVarigEndretNaering;
    protected Integer bruttoBeregningsgrunnlag = null;

    public VurderVarigEndringEllerNyoppstartetSNBekreftelse() {
        super();
    }

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

}
