package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TilstøtendeYtelseDto {

    protected List<TilstøtendeYtelseAndelDto> tilstøtendeYtelseAndeler;
    protected int dekningsgrad;
    protected String arbeidskategori;
    protected String ytelseType;
    protected double bruttoBG;
    protected boolean skalReduseres;
    protected boolean erBesteberegning;
}
