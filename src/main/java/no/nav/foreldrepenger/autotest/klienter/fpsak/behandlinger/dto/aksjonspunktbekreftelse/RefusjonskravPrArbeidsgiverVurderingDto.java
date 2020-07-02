package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RefusjonskravPrArbeidsgiverVurderingDto {

    private String arbeidsgiverId;
    private boolean skalUtvideGyldighet;

    public RefusjonskravPrArbeidsgiverVurderingDto(String arbeidsgiverId, boolean skalUtvideGyldighet) {
        this.arbeidsgiverId = arbeidsgiverId;
        this.skalUtvideGyldighet = skalUtvideGyldighet;
    }

    public String getArbeidsgiverId() {
        return arbeidsgiverId;
    }

    public boolean isSkalUtvideGyldighet() {
        return skalUtvideGyldighet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefusjonskravPrArbeidsgiverVurderingDto that = (RefusjonskravPrArbeidsgiverVurderingDto) o;
        return skalUtvideGyldighet == that.skalUtvideGyldighet &&
                Objects.equals(arbeidsgiverId, that.arbeidsgiverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidsgiverId, skalUtvideGyldighet);
    }

    @Override
    public String toString() {
        return "RefusjonskravPrArbeidsgiverVurderingDto{" +
                "arbeidsgiverId='" + arbeidsgiverId + '\'' +
                ", skalUtvideGyldighet=" + skalUtvideGyldighet +
                '}';
    }
}
