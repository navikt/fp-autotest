package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum InnsynResultatType implements Kode {

    INNVILGET("INNV"),
    DELVIS_INNVILGET("DELV"),
    AVVIST("AVVIST"),
    ;

    private final String kode;

    InnsynResultatType(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static InnsynResultatType fraKode(String kode) {
        return Arrays.stream(InnsynResultatType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet InnsynResultatType %s.", kode));
    }

    @Override
    public String getKode() {
        return kode;
    }
}
