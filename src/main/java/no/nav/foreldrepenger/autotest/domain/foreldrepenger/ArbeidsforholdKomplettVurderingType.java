package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ArbeidsforholdKomplettVurderingType {

    KONTAKT_ARBEIDSGIVER_VED_MANGLENDE_INNTEKTSMELDING("KONTAKT_ARBEIDSGIVER_VED_MANGLENDE_INNTEKTSMELDING"),
    FORTSETT_UTEN_INNTEKTSMELDING("FORTSETT_UTEN_INNTEKTSMELDING"),

    KONTAKT_ARBEIDSGIVER_VED_MANGLENDE_ARBEIDSFORHOLD("KONTAKT_ARBEIDSGIVER_VED_MANGLENDE_ARBEIDSFORHOLD"),
    IKKE_OPPRETT_BASERT_PÅ_INNTEKTSMELDING("IKKE_OPPRETT_BASERT_PÅ_INNTEKTSMELDING"),
    OPPRETT_BASERT_PÅ_INNTEKTSMELDING("OPPRETT_BASERT_PÅ_INNTEKTSMELDING"),

    MANUELT_OPPRETTET_AV_SAKSBEHANDLER("MANUELT_OPPRETTET_AV_SAKSBEHANDLER"),
    FJERN_FRA_BEHANDLINGEN("FJERN_FRA_BEHANDLINGEN"),

    UDEFINERT("-"),
    ;
    public static final String KODEVERK = "ARBEIDSFORHOLD_KOMPLETT_VURDERING_TYPE";

    private final String kode;

    ArbeidsforholdKomplettVurderingType() {
        this(null);
    }

    ArbeidsforholdKomplettVurderingType(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    @JsonCreator
    public static ArbeidsforholdKomplettVurderingType fraKode(String kode) {
        return Arrays.stream(ArbeidsforholdKomplettVurderingType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet ArbeidsforholdKomplettVurderingType " + kode));
    }

    public String getKode() {
        return kode;
    }
}
