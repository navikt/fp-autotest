package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tilretteleggingsdato {

    protected LocalDate fom;
    protected TilretteleggingType type;
    protected BigDecimal stillingsprosent;
    protected SvpTilretteleggingFomKilde kilde = SvpTilretteleggingFomKilde.SØKNAD;


    public Tilretteleggingsdato(LocalDate fom, TilretteleggingType type, BigDecimal stillingsprosent,
                                SvpTilretteleggingFomKilde kilde) {
        this.fom = fom;
        this.type = type;
        this.stillingsprosent = stillingsprosent;
    }

    public enum SvpTilretteleggingFomKilde {
        ENDRET_AV_SAKSBEHANDLER,
        REGISTRERT_AV_SAKSBEHANDLER,
        TIDLIGERE_VEDTAK,
        SØKNAD
    }
}
