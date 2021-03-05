package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Venteårsak {

    AAP_DP_ENESTE_AKTIVITET_SVP,
    ANKE_OVERSENDT_TIL_TRYGDERETTEN,
    ANKE_VENTER_PAA_MERKNADER_FRA_BRUKER,
    AVV_DOK,
    AVV_FODSEL,
    AVV_RESPONS_REVURDERING,
    DELVIS_TILRETTELEGGING_OG_REFUSJON_SVP,
    FLERE_ARBEIDSFORHOLD_SAMME_ORG_SVP,
    FOR_TIDLIG_SOKNAD,
    GRADERING_FLERE_ARBEIDSFORHOLD,
    REFUSJON_3_MÅNEDER,
    SCANN,
    ULIKE_STARTDATOER_SVP,
    UTV_FRIST,
    VENT_DØDFØDSEL_80P_DEKNINGSGRAD,
    VENT_FEIL_ENDRINGSSØKNAD,
    VENT_GRADERING_UTEN_BEREGNINGSGRUNNLAG,
    VENT_INFOTRYGD,
    VENT_INNTEKT_RAPPORTERINGSFRIST,
    VENT_MILITÆR_BG_UNDER_3G,
    VENT_OPDT_INNTEKTSMELDING,
    VENT_OPPTJENING_OPPLYSNINGER,
    VENT_PÅ_NY_INNTEKTSMELDING_MED_GYLDIG_ARB_ID,
    VENT_PÅ_SISTE_AAP_ELLER_DP_MELDEKORT,
    VENT_REGISTERINNHENTING,
    VENT_SØKNAD_SENDT_INFORMASJONSBREV,
    VENT_TIDLIGERE_BEHANDLING,
    VENT_ÅPEN_BEHANDLING,
    OPPD_ÅPEN_BEH,
    VENT_DEKGRAD_REGEL,
    VENT_ØKONOMI,
    VENTELØNN_ELLER_MILITÆR_MED_FLERE_AKTIVITETER,
    VENT_BEREGNING_TILBAKE_I_TID,
    AAP_DP_SISTE_10_MND_SVP,
    FL_SN_IKKE_STOTTET_FOR_SVP,
    VENT_MANGLENDE_ARBEIDSFORHOLD,
    VENT_MANGLENDE_SYKEMELDING,
    ;

    private final String kode;

    Venteårsak() {
        this(null);
    }

    Venteårsak(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }


    @JsonCreator
    public static Venteårsak fraKode(String kode) {
        return Arrays.stream(Venteårsak.values())
                .filter(value -> value.name().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet Venteårsak " + kode));
    }

    public String getKode() {
        return kode;
    }
}
