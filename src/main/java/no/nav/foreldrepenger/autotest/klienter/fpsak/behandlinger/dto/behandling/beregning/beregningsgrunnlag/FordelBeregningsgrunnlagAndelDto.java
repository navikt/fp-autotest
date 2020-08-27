package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FordelBeregningsgrunnlagAndelDto extends FaktaOmBeregningAndelDto {

    private BigDecimal fordelingForrigeBehandlingPrAar;
    private BigDecimal refusjonskravPrAar = BigDecimal.ZERO;
    private BigDecimal fordeltPrAar;
    private BigDecimal belopFraInntektsmeldingPrAar;
    private BigDecimal fastsattForrigePrAar;
    private BigDecimal refusjonskravFraInntektsmeldingPrAar;
    private boolean nyttArbeidsforhold;
    private Kode arbeidsforholdType;

    public Kode getArbeidsforholdType() {
        return arbeidsforholdType;
    }

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

    public BigDecimal getFastsattForrigePrAar() {
        return fastsattForrigePrAar;
    }

    public BigDecimal getRefusjonskravFraInntektsmeldingPrAar() {
        return refusjonskravFraInntektsmeldingPrAar;
    }

    public boolean isNyttArbeidsforhold() {
        return nyttArbeidsforhold;
    }

    public void setFordelingForrigeBehandlingPrAar(BigDecimal fordelingForrigeBehandlingPrAar) {
        this.fordelingForrigeBehandlingPrAar = fordelingForrigeBehandlingPrAar;
    }

    public void setRefusjonskravPrAar(BigDecimal refusjonskravPrAar) {
        this.refusjonskravPrAar = refusjonskravPrAar;
    }

    public void setFordeltPrAar(BigDecimal fordeltPrAar) {
        this.fordeltPrAar = fordeltPrAar;
    }

    public void setBelopFraInntektsmeldingPrAar(BigDecimal belopFraInntektsmeldingPrAar) {
        this.belopFraInntektsmeldingPrAar = belopFraInntektsmeldingPrAar;
    }

    public void setFastsattForrigePrAar(BigDecimal fastsattForrigePrAar) {
        this.fastsattForrigePrAar = fastsattForrigePrAar;
    }

    public void setRefusjonskravFraInntektsmeldingPrAar(BigDecimal refusjonskravFraInntektsmeldingPrAar) {
        this.refusjonskravFraInntektsmeldingPrAar = refusjonskravFraInntektsmeldingPrAar;
    }

    public void setNyttArbeidsforhold(boolean nyttArbeidsforhold) {
        this.nyttArbeidsforhold = nyttArbeidsforhold;
    }

    public void setArbeidsforholdType(Kode arbeidsforholdType) {
        this.arbeidsforholdType = arbeidsforholdType;
    }
}
