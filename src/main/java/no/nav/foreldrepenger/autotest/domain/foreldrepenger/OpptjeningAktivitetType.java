package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OpptjeningAktivitetType {
    AAP,
    ARBEID,
    DAGPENGER,
    FORELDREPENGER,
    ETTERLØNN_SLUTTPAKKE,
    FRILANS,
    FRISINN,
    MILITÆR_ELLER_SIVILTJENESTE,
    NÆRING,
    OMSORGSPENGER,
    OPPLÆRINGSPENGER,
    PLEIEPENGER,
    SVANGERSKAPSPENGER,
    SYKEPENGER,
    VENTELØNN_VARTPENGER,
    VIDERE_ETTERUTDANNING,
    UTENLANDSK_ARBEIDSFORHOLD,
    UTDANNINGSPERMISJON,
    UDEFINERT("-"),
    ;

    private final String kode;

    OpptjeningAktivitetType() {
        this(null);
    }

    OpptjeningAktivitetType(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static OpptjeningAktivitetType fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        var kode = TempAvledeKode.getVerdi(PeriodeUtfallÅrsak.class, node, "kode");
        return Arrays.stream(OpptjeningAktivitetType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElse(OpptjeningAktivitetType.UDEFINERT);
    }

    public String getKode() {
        return kode;
    }
}
