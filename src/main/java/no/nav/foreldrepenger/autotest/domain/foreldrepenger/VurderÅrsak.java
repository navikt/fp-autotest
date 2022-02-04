package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum VurderÅrsak {
    FEIL_FAKTA,
    FEIL_LOV,
    FEIL_REGEL,
    ANNET,
    ;

    private final String kode;

    VurderÅrsak() {
        this(null);
    }

    VurderÅrsak(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VurderÅrsak fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        var kode = TempAvledeKode.getVerdi(PeriodeUtfallÅrsak.class, node, "kode");
        return Arrays.stream(VurderÅrsak.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet VurderÅrsak " + kode));
    }

    public String getKode() {
        return kode;
    }
}

