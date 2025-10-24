package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

import no.nav.foreldrepenger.kontrakter.fpsoknad.Saksnummer;

public class Kravgrunnlag {

    protected KravgrunnlagDetaljert kravGrunnlag;

    public Kravgrunnlag(Saksnummer saksnummer, String ident, int behandlingId, String ytelseType, String kravStatusKode) {
        this.kravGrunnlag = new KravgrunnlagDetaljert(saksnummer, ident, String.valueOf(behandlingId), ytelseType,
                kravStatusKode);
    }

    public void leggTilGeneriskPeriode() {
        this.kravGrunnlag.leggTilPeriode();
    }

    public void leggTilPeriodeMedSmåBeløp() {
        this.kravGrunnlag.leggTilPeriodeMedSmåBeløp();
    }

    public void leggTilGeneriskPeriode(String ytelseType) {
        if (ytelseType.equals("ES")) {
            this.kravGrunnlag.leggTilPeriodeForEngangsstonad();
        } else {
            leggTilGeneriskPeriode();
        }
    }
}
