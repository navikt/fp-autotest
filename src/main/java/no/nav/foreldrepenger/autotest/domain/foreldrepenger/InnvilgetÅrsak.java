package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum InnvilgetÅrsak implements PeriodeResultatÅrsak {

    FELLESPERIODE_ELLER_FORELDREPENGER("2002", "§14-9: Innvilget fellesperiode/foreldrepenger"),
    KVOTE_ELLER_OVERFØRT_KVOTE("2003", "§14-12: Innvilget uttak av kvote"),
    FORELDREPENGER_KUN_FAR_HAR_RETT("2004", "§14-14, jf. §14-13 : Innvilget foreldrepenger, kun far har rett"),
    FORELDREPENGER_ALENEOMSORG("2005", "§14-15: Innvilget foreldrepenger ved aleneomsorg"),
    INNVILGET_FORELDREPENGER_FØR_FØDSEL("2006", "§14-10: Innvilget foreldrepenger før fødsel"),
    FORELDREPENGER_KUN_MOR_HAR_RETT("2007", "§14-10: Innvilget foreldrepenger, kun mor har rett"),
    UTSETTELSE_GYLDIG_PGA_FERIE("2010", "§14-11 første ledd bokstav a: Gyldig utsettelse pga. ferie"),
    UTSETTELSE_GYLDIG_PGA_100_PROSENT_ARBEID("2011", "§14-11 første ledd bokstav b: Gyldig utsettelse pga. 100% arbeid"),
    UTSETTELSE_GYLDIG_PGA_INNLEGGELSE("2012", "§14-11 første ledd bokstav c: Gyldig utsettelse pga. innleggelse"),
    UTSETTELSE_GYLDIG_PGA_BARN_INNLAGT("2013", "§14-11 første ledd bokstav d: Gyldig utsettelse pga. barn innlagt"),
    UTSETTELSE_GYLDIG_PGA_SYKDOM("2014", "§14-11 første ledd bokstav c: Gyldig utsettelse pga. sykdom"),
    UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT("2015", "§14-11 første ledd bokstav a, jf. §14-14, jf. §14-13: Utsettelse pga. ferie, kun far har rett"),
    UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT("2016", "§14-11 første ledd bokstav b, jf. §14-14, jf. §14-13: Utsettelse pga. 100% arbeid, kun far har rett"),
    UTSETTELSE_GYLDIG_PGA_SYKDOM_KUN_FAR_HAR_RETT("2017", "§14-11 første ledd bokstav c, jf. §14-14, jf. §14-13: Utsettelse pga. sykdom, skade, kun far har rett"),
    UTSETTELSE_GYLDIG_PGA_INNLEGGELSE_KUN_FAR_HAR_RETT("2018", "§14-11 første ledd bokstav c, jf. §14-14, jf. §14-13: Utsettelse pga. egen innleggelse på helseinstiusjon, kun far har rett"),
    UTSETTELSE_GYLDIG_PGA_BARN_INNLAGT_KUN_FAR_HAR_RETT("2019", "§14-11 første ledd bokstav d, jf. §14-14, jf. §14-13: Utsettelse pga. barnets innleggelse på helseinstitusjon, kun far har rett"),
    OVERFØRING_ANNEN_PART_HAR_IKKE_RETT_TIL_FORELDREPENGER("2020", "§14-9 første ledd: Overføring oppfylt, annen part har ikke rett til foreldrepenger"),
    OVERFØRING_ANNEN_PART_SYKDOM_SKADE("2021", "§14-12: Overføring oppfylt, annen part er helt avhengig av hjelp til å ta seg av barnet"),
    OVERFØRING_ANNEN_PART_INNLAGT("2022", "§14-12: Overføring oppfylt, annen part er innlagt i helseinstitusjon"),
    OVERFØRING_SØKER_HAR_ALENEOMSORG_FOR_BARNET("2023", "§14-15 første ledd: Overføring oppfylt, søker har aleneomsorg for barnet"),
    UTSETTELSE_GYLDIG("2024", "§14-11: Gyldig utsettelse"),
    UTSETTELSE_GYLDIG_SEKS_UKER_INNLEGGELSE("2025", "§14-11: Gyldig utsettelse første 6 uker pga. innleggelse"),
    UTSETTELSE_GYLDIG_SEKS_UKER_FRI_BARN_INNLAGT("2026", "§14-11: Gyldig utsettelse første 6 uker pga. barn innlagt"),
    UTSETTELSE_GYLDIG_SEKS_UKER_FRI_SYKDOM("2027", "§14-11: Gyldig utsettelse første 6 uker pga. sykdom"),
    UTSETTELSE_GYLDIG_BFR_AKT_KRAV_OPPFYLT("2028", "§14-14, jf. 14-13: Bare far rett, aktivitetskravet oppfylt"),
    GRADERING_FELLESPERIODE_ELLER_FORELDREPENGER("2030", "§14-9, jf. §14-16: Gradering av fellesperiode/foreldrepenger"),
    GRADERING_KVOTE_ELLER_OVERFØRT_KVOTE("2031", "§14-12, jf. §14-16: Gradering av kvote/overført kvote"),
    GRADERING_ALENEOMSORG("2032", "§14-15, jf. §14-16: Gradering foreldrepenger ved aleneomsorg"),
    GRADERING_FORELDREPENGER_KUN_FAR_HAR_RETT("2033", "§14-14, jf. §14-13, jf. §14-16: Gradering foreldrepenger, kun far har rett"),
    GRADERING_FORELDREPENGER_KUN_MOR_HAR_RETT("2034", "§14-10, jf. §14-16: Gradering foreldrepenger, kun mor har rett"),
    GRADERING_KUN_FAR_HAR_RETT_MOR_UFØR("2035", "§14-14 tredje ledd: Gradering foreldrepenger, kun far har rett og mor er ufør"),
    FORELDREPENGER_KUN_FAR_HAR_RETT_MOR_UFØR("2036", "§14-14 tredje ledd: Innvilget foreldrepenger, kun far har rett og mor er ufør"),
    FORELDREPENGER_FELLESPERIODE_TIL_FAR("2037", "§14-9, jf. §14-13: Innvilget fellesperiode til far"),
    FORELDREPENGER_REDUSERT_GRAD_PGA_SAMTIDIG_UTTAK("2038", "§14-10 sjette ledd: Samtidig uttak"),
    ;

    public static final String KODEVERK = "INNVILGET_AARSAK";

    private final String kode;
    private final String navn;

    InnvilgetÅrsak(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @JsonCreator
    public static InnvilgetÅrsak fraKode(String kode) {
        return Arrays.stream(InnvilgetÅrsak.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet InnvilgetÅrsak " + kode));
    }

    @Override
    public String getKode() {
        return kode;
    }

    @Override
    public String getNavn() {
        return navn;
    }

    @JsonProperty
    @Override
    public String getKodeverk() {
        return KODEVERK;
    }
}
