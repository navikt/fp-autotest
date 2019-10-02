package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FordelBeregningsgrunnlagAndelDto extends FaktaOmBeregningAndelDto {

    protected BigDecimal fordelingForrigeBehandlingPrAar;
    protected BigDecimal refusjonskravPrAar = BigDecimal.ZERO;
    protected BigDecimal fordeltPrAar;
    protected BigDecimal belopFraInntektsmeldingPrAar;
    protected BigDecimal fastsattForrigePrAar;
    protected BigDecimal refusjonskravFraInntektsmeldingPrAar;
    protected boolean nyttArbeidsforhold;

}
