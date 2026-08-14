package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;

public class Kravgrunnlag {

    protected KravgrunnlagDetaljert kravGrunnlag;

    public Kravgrunnlag(Saksnummer saksnummer, String ident, int behandlingId, String ytelseType, String kravStatusKode) {
        this.kravGrunnlag = new KravgrunnlagDetaljert(saksnummer, ident, String.valueOf(behandlingId), ytelseType,
                kravStatusKode);
    }

    public void leggTilGeneriskPeriode() {
        this.kravGrunnlag.leggTilPeriode();
    }

    /**
     * Legger til en periode som er eldre enn foreldelsesfristen, slik at fptilbake utleder
     * aksjonspunkt 5003 VURDER_FORELDELSE.
     */
    public void leggTilForeldetPeriode() {
        this.kravGrunnlag.leggTilPeriode(KravgrunnlagDetaljert.PeriodeType.GENERISK,
                KravgrunnlagDetaljert.ANTALL_MÅNEDER_TILBAKE_FORELDET);
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
