package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

public class Kravgrunnlag {

    protected KravgrunnlagDetaljert kravGrunnlag;

    public Kravgrunnlag(Long saksnummer, String ident, String behandlingId, String ytelseType, String kravStatusKode){
        this.kravGrunnlag = new KravgrunnlagDetaljert(saksnummer,ident,behandlingId,ytelseType,kravStatusKode);
        this.kravGrunnlag.leggTilPeriode();
    }
}
