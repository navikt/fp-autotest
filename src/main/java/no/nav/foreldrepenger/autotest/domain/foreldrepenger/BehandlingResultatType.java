package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BehandlingResultatType {

    IKKE_FASTSATT,
    INNVILGET,
    AVSLÅTT,
    OPPHØR,
    HENLAGT_SØKNAD_TRUKKET,
    HENLAGT_FEILOPPRETTET,
    HENLAGT_BRUKER_DØD,
    MERGET_OG_HENLAGT,
    HENLAGT_SØKNAD_MANGLER,
    FORELDREPENGER_ENDRET,
    FORELDREPENGER_SENERE,
    INGEN_ENDRING,
    MANGLER_BEREGNINGSREGLER,

    // Klage
    KLAGE_AVVIST,
    KLAGE_MEDHOLD,
    KLAGE_YTELSESVEDTAK_OPPHEVET,
    KLAGE_YTELSESVEDTAK_STADFESTET,
    KLAGE_TILBAKEKREVING_VEDTAK_STADFESTET, // Brukes av kun Tilbakekreving eller Tilbakekreving Revurdering
    HENLAGT_KLAGE_TRUKKET,
    HJEMSENDE_UTEN_OPPHEVE,

    // Anke
    ANKE_AVVIST,
    ANKE_OMGJOER,
    ANKE_OPPHEVE_OG_HJEMSENDE,
    ANKE_HJEMSENDE_UTEN_OPPHEV,
    ANKE_YTELSESVEDTAK_STADFESTET,

    // Innsyn
    INNSYN_INNVILGET,
    INNSYN_DELVIS_INNVILGET,
    INNSYN_AVVIST,
    HENLAGT_INNSYN_TRUKKET,
    ;

    private final String kode;

    BehandlingResultatType() {
        this(null);
    }

    BehandlingResultatType(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static BehandlingResultatType fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        var kode = TempAvledeKode.getVerdi(PeriodeUtfallÅrsak.class, node, "kode");
        return Arrays.stream(BehandlingResultatType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet behandlingresultattype " + kode));
    }

    public String getKode() {
        return kode;
    }
}
