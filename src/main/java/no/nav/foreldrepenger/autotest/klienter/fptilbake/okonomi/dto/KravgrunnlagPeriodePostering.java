package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

import java.math.BigDecimal;

public class KravgrunnlagPeriodePostering {

    protected String klasseKode;
    protected String klasseType;
    protected BigDecimal opprUtbetBelop;
    protected BigDecimal nyBelop;
    protected BigDecimal tilbakekrevesBelop;
    protected BigDecimal uinnkrevdBelop;
    protected BigDecimal skattProsent;

    public KravgrunnlagPeriodePostering(String klasseKode, String klasseType, BigDecimal opprUtbetBelop, BigDecimal nyBelop, BigDecimal tilbakekrevesBelop, BigDecimal uinnkrevdBelop, BigDecimal skattProsent) {
        this.klasseKode = klasseKode;
        this.klasseType = klasseType;
        this.opprUtbetBelop = opprUtbetBelop;
        this.nyBelop = nyBelop;
        this.tilbakekrevesBelop = tilbakekrevesBelop;
        this.uinnkrevdBelop = uinnkrevdBelop;
        this.skattProsent = skattProsent;
    }
}
