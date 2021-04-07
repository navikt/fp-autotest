package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PeriodeResultatType {
    INNVILGET,
    AVSLÅTT,
    IKKE_FASTSATT,
    MANUELL_BEHANDLING,

    /** @deprecated kan fjernes når beregning har sluttet å bruke. */
    @Deprecated
    GYLDIG_UTSETTELSE,

    /** @deprecated kan fjernes når beregning har sluttet å bruke. */
    @Deprecated
    UGYLDIG_UTSETTELSE,
    ;


    private final String kode;

    PeriodeResultatType() {
        this(null);
    }

    PeriodeResultatType(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    @JsonCreator
    public static PeriodeResultatType fraKode(String kode) {
        return Arrays.stream(PeriodeResultatType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet UttakPeriodeVurderingType " + kode));
    }

    public String getKode() {
        return kode;
    }
}