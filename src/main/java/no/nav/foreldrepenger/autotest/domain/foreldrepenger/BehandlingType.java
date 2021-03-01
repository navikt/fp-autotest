package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BehandlingType implements Kode {

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

    @JsonCreator
    public static BehandlingType fraKode(String kode) {
        return Arrays.stream(BehandlingType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet behandlingtype %s.", kode));
    }

    @Override
    public String getKode() {
        return kode;
    }
}
