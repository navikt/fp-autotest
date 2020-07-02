package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettMaanedsinntektUtenInntektsmelding {

    private List<FastsettMaanedsinntektUtenInntektsmeldingAndel> andelListe;

    public FastsettMaanedsinntektUtenInntektsmelding(
            @JsonProperty("andelListe") List<FastsettMaanedsinntektUtenInntektsmeldingAndel> andelListe) {
        this.andelListe = andelListe;
    }

    public List<FastsettMaanedsinntektUtenInntektsmeldingAndel> getAndelListe() {
        return andelListe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsettMaanedsinntektUtenInntektsmelding that = (FastsettMaanedsinntektUtenInntektsmelding) o;
        return Objects.equals(andelListe, that.andelListe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(andelListe);
    }

    @Override
    public String toString() {
        return "FastsettMaanedsinntektUtenInntektsmelding{" +
                "andelListe=" + andelListe +
                '}';
    }
}
