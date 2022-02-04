package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FagsakStatus {

    OPPRETTET("OPPR"),
    UNDER_BEHANDLING("UBEH"),
    LØPENDE("LOP"),
    AVSLUTTET("AVSLU"),
    ;

    private final String kode;

    FagsakStatus(String kode) {
        this.kode = kode;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FagsakStatus fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        var kode = TempAvledeKode.getVerdi(PeriodeUtfallÅrsak.class, node, "kode");
        return Arrays.stream(FagsakStatus.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet FagsakStatus " + kode));
    }

    public String getKode() {
        return kode;
    }
}


