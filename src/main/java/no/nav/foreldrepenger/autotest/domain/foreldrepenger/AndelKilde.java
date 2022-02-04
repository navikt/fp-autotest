package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AndelKilde {
    SAKSBEHANDLER_KOFAKBER,
    SAKSBEHANDLER_FORDELING,
    PROSESS_PERIODISERING,
    PROSESS_OMFORDELING,
    PROSESS_START,
    ;

    private final String kode;

    AndelKilde() {
        this(null);
    }

    AndelKilde(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AndelKilde fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        var kode = TempAvledeKode.getVerdi(PeriodeUtfallÅrsak.class, node, "kode");
        return Arrays.stream(AndelKilde.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet behandlingresultattype " + kode));
    }

    public String getKode() {
        return kode;
    }
}
