package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettMaanedsinntektFL {

    private int maanedsinntekt;

    public FastsettMaanedsinntektFL(@JsonProperty("maanedsinntekt") int maanedsinntekt) {
        this.maanedsinntekt = maanedsinntekt;
    }

    public int getMaanedsinntekt() {
        return maanedsinntekt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsettMaanedsinntektFL that = (FastsettMaanedsinntektFL) o;
        return maanedsinntekt == that.maanedsinntekt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maanedsinntekt);
    }

    @Override
    public String toString() {
        return "FastsettMaanedsinntektFL{" +
                "maanedsinntekt=" + maanedsinntekt +
                '}';
    }
}
