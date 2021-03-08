package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

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

    @JsonCreator
    public static AndelKilde fraKode(String kode) {
        return Arrays.stream(AndelKilde.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet behandlingresultattype " + kode));
    }

    public String getKode() {
        return kode;
    }
}
