package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import java.io.Serializable;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum KontrollerAktivitetskravAvklaring implements Serializable {

    I_AKTIVITET,
    IKKE_I_AKTIVITET_IKKE_DOKUMENTERT,
    IKKE_I_AKTIVITET_DOKUMENTERT;

    @JsonProperty
    public String getKode() {
        return this.name();
    }

    @JsonCreator
    public static KontrollerAktivitetskravAvklaring fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        return Stream.of(values()).filter(v -> v.getKode().equals(kode)).findFirst().orElseThrow();
    }
}
