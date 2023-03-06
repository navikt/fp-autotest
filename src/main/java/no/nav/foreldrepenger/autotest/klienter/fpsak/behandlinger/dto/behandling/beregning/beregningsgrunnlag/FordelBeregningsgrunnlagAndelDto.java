package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OpptjeningAktivitetType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FordelBeregningsgrunnlagAndelDto extends FaktaOmBeregningAndelDto {

    private BigDecimal fordelingForrigeBehandlingPrAar;
    private BigDecimal refusjonskravPrAar;
    private BigDecimal fordeltPrAar;
    private BigDecimal belopFraInntektsmeldingPrAar;
    private BigDecimal refusjonskravFraInntektsmeldingPrAar;
    private boolean nyttArbeidsforhold;
    private OpptjeningAktivitetType arbeidsforholdType;

    public BigDecimal getFordelingForrigeBehandlingPrAar() {
        return fordelingForrigeBehandlingPrAar;
    }

    public BigDecimal getRefusjonskravPrAar() {
        return refusjonskravPrAar;
    }

    public BigDecimal getFordeltPrAar() {
        return fordeltPrAar;
    }

    public BigDecimal getBelopFraInntektsmeldingPrAar() {
        return belopFraInntektsmeldingPrAar;
    }

    public BigDecimal getRefusjonskravFraInntektsmeldingPrAar() {
        return refusjonskravFraInntektsmeldingPrAar;
    }

    public boolean isNyttArbeidsforhold() {
        return nyttArbeidsforhold;
    }

    public OpptjeningAktivitetType getArbeidsforholdType() {
        return arbeidsforholdType;
    }
}
