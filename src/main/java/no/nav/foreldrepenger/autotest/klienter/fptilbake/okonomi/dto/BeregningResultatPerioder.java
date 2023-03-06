package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeregningResultatPerioder {
    protected int renteBeløp;
    protected int skattBeløp;
    protected int tilbakekrevingBeløp;
    protected int tilbakekrevingBeløpEtterSkatt;
    protected int tilbakekrevingBeløpUtenRenter;

    public BeregningResultatPerioder(){

    }

    public int getRenteBeløp() {
        return renteBeløp;
    }

    public void addRenteBeløp(int renteBeløp) {
        this.renteBeløp = this.renteBeløp+renteBeløp;
    }

    public int getSkattBeløp() {
        return skattBeløp;
    }

    public void addSkattBeløp(int skattBeløp) {
        this.skattBeløp = this.skattBeløp+skattBeløp;
    }

    public int getTilbakekrevingBeløp() {
        return tilbakekrevingBeløp;
    }

    public void addTilbakekrevingBeløp(int tilbakekrevingBeløp) {
        this.tilbakekrevingBeløp = this.tilbakekrevingBeløp+tilbakekrevingBeløp;
    }

    public int getTilbakekrevingBeløpEtterSkatt() {
        return tilbakekrevingBeløpEtterSkatt;
    }

    public void addTilbakekrevingBeløpEtterSkatt(int tilbakekrevingBeløpEtterSkatt) {
        this.tilbakekrevingBeløpEtterSkatt = this.tilbakekrevingBeløpEtterSkatt+tilbakekrevingBeløpEtterSkatt;
    }

    public int getTilbakekrevingBeløpUtenRenter() {
        return tilbakekrevingBeløpUtenRenter;
    }

    public void addTilbakekrevingBeløpUtenRenter(int tilbakekrevingBeløpUtenRenter) {
        this.tilbakekrevingBeløpUtenRenter = this.tilbakekrevingBeløpUtenRenter+tilbakekrevingBeløpUtenRenter;
    }
}
