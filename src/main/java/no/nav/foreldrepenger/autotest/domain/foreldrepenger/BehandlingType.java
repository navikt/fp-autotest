package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BehandlingType {

    FØRSTEGANGSSØKNAD("BT-002"),
    KLAGE("BT-003"),
    REVURDERING("BT-004"),
    ANKE("BT-008"),
    INNSYN("BT-006"),
    ;

    private final String kode;

    BehandlingType(String kode) {
        this.kode = kode;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static BehandlingType fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        var kode = TempAvledeKode.getVerdi(PeriodeUtfallÅrsak.class, node, "kode");
        return Arrays.stream(BehandlingType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet behandlingtype " + kode));
    }

    public String getKode() {
        return kode;
    }
}
