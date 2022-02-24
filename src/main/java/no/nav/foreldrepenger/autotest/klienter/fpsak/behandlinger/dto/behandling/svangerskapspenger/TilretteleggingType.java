package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.TempAvledeKode;
import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

public enum TilretteleggingType {

    HEL_TILRETTELEGGING("HEL_TILRETTELEGGING", "Hel tilrettelegging"),
    DELVIS_TILRETTELEGGING("DELVIS_TILRETTELEGGING", "Delvis tilrettelegging"),
    INGEN_TILRETTELEGGING("INGEN_TILRETTELEGGING", "Ingen tilrettelegging"),
    ;

    private static final Map<String, TilretteleggingType> KODER = new LinkedHashMap<>();

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String navn;

    private String kode;

    TilretteleggingType(String kode) {
        this.kode = kode;
    }

    TilretteleggingType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TilretteleggingType fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        var kode = TempAvledeKode.getVerdi(TilretteleggingType.class, node, "kode");
        return Arrays.stream(TilretteleggingType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet TilretteleggingType " + kode));
    }

    public static Map<String, TilretteleggingType> kodeMap() {
        return Collections.unmodifiableMap(KODER);
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() {
        return kode;
    }

}
