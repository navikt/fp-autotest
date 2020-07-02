package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderLønnsendringDto {

    private Boolean erLønnsendringIBeregningsperioden;

    public VurderLønnsendringDto() {
    }

    public Boolean getErLønnsendringIBeregningsperioden() {
        return erLønnsendringIBeregningsperioden;
    }

    public void setErLønnsendringIBeregningsperioden(Boolean erLønnsendringIBeregningsperioden) {
        this.erLønnsendringIBeregningsperioden = erLønnsendringIBeregningsperioden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderLønnsendringDto that = (VurderLønnsendringDto) o;
        return Objects.equals(erLønnsendringIBeregningsperioden, that.erLønnsendringIBeregningsperioden);
    }

    @Override
    public int hashCode() {
        return Objects.hash(erLønnsendringIBeregningsperioden);
    }

    @Override
    public String toString() {
        return "VurderLønnsendringDto{" +
                "erLønnsendringIBeregningsperioden=" + erLønnsendringIBeregningsperioden +
                '}';
    }
}
