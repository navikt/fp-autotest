package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.tilrettelegging.TilretteleggingType;

public class SvpTilretteleggingDato {
    private LocalDate fom;
    private TilretteleggingType type;
    private BigDecimal stillingsprosent;

    public SvpTilretteleggingDato() {
        // nix
    }

    SvpTilretteleggingDato(LocalDate fom, TilretteleggingType type, BigDecimal stillingsprosent) {
        this.fom = fom;
        this.type = type;
        this.stillingsprosent = stillingsprosent;
    }

    public LocalDate getFom() {
        return fom;
    }

    public TilretteleggingType getType() {
        return type;
    }

    public BigDecimal getStillingsprosent() {
        return stillingsprosent;
    }

    public SvpTilretteleggingDato setFom(LocalDate fom) {
        this.fom = fom;
        return this;
    }

    public SvpTilretteleggingDato setType(TilretteleggingType type) {
        this.type = type;
        return this;
    }

    public SvpTilretteleggingDato setStillingsprosent(BigDecimal stillingsprosent) {
        this.stillingsprosent = stillingsprosent;
        return this;
    }
}
