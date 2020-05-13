package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

public class Kravgrunnlag {

    protected KravgrunnlagDetaljert kravGrunnlag;

    public Kravgrunnlag(Long saksnummer, String ident, int behandlingId, String ytelseType, String kravStatusKode) {
        this.kravGrunnlag = new KravgrunnlagDetaljert(saksnummer,ident,String.valueOf(behandlingId),ytelseType,kravStatusKode);
    }

    public void leggTilGeneriskPeriode() {
        this.kravGrunnlag.leggTilPeriode();
    }
    public void leggTilGeneriskPeriode(String ytelseType) {
        if (ytelseType.equals("ES")) {
            this.kravGrunnlag.leggTilPeriodeForEngangsstonad();
        }
        else leggTilGeneriskPeriode();
    }
}
