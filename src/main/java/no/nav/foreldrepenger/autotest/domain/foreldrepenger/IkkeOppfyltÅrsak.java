package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum IkkeOppfyltÅrsak implements PeriodeResultatÅrsak {

    UKJENT("-", "Ikke definert"),
    IKKE_STØNADSDAGER_IGJEN("4002", "§14-9: Ikke stønadsdager igjen på stønadskonto"),
    MOR_HAR_IKKE_OMSORG("4003", "§14-10 fjerde ledd: Mor har ikke omsorg"),
    HULL_MELLOM_FORELDRENES_PERIODER("4005", "§14-10 sjuende ledd: Ikke sammenhengende perioder"),
    _4006("4006", "§14-10 sjuende ledd: Ikke sammenhengende perioder"),
    DEN_ANDRE_PART_SYK_SKADET_IKKE_OPPFYLT("4007", "§14-12 tredje ledd: Den andre part syk/skadet ikke oppfylt"),
    DEN_ANDRE_PART_INNLEGGELSE_IKKE_OPPFYLT("4008", "§14-12 tredje ledd: Den andre part innleggelse ikke oppfylt"),
    FAR_HAR_IKKE_OMSORG("4012", "§14-10 fjerde ledd: Far/medmor har ikke omsorg"),
    MOR_SØKER_FELLESPERIODE_FØR_12_UKER_FØR_TERMIN_FØDSEL("4013", "§14-10 første ledd: Mor søker uttak før 12 uker før termin/fødsel"),
    _4018("4018", "§14-10 andre ledd: Søkt uttak/utsettelse før omsorgsovertakelse"),
    SØKNADSFRIST("4020", "§22-13 tredje ledd: Brudd på søknadsfrist"),
    BARN_OVER_3_ÅR("4022", "§14-10 tredje ledd: Barnet er over 3 år"),
    ARBEIDER_I_UTTAKSPERIODEN_MER_ENN_0_PROSENT("4023", "§14-10 femte ledd: Arbeider i uttaksperioden mer enn 0%"),
    AVSLAG_GRADERING_ARBEIDER_100_PROSENT_ELLER_MER("4025", "§14-16 første ledd: Avslag gradering - arbeid 100% eller mer"),
    UTSETTELSE_FØR_TERMIN_FØDSEL("4030", "§14-9: Avslag utsettelse før termin/fødsel"),
    UTSETTELSE_INNENFOR_DE_FØRSTE_6_UKENE("4031", "§14-9: Ferie/arbeid innenfor de første 6 ukene"),
    FERIE_SELVSTENDIG_NÆRINGSDRIVENDSE_FRILANSER("4032", "§14-11 første ledd bokstav a: Ferie - selvstendig næringsdrivende/frilanser"),
    IKKE_LOVBESTEMT_FERIE("4033", "§14-11 første ledd bokstav a: Ikke lovbestemt ferie"),
    INGEN_STØNADSDAGER_IGJEN("4034", "§14-11, jf §14-9: Avslag utsettelse - ingen stønadsdager igjen"),
    BARE_FAR_RETT_MOR_FYLLES_IKKE_AKTIVITETSKRAVET("4035", "§14-11 første ledd bokstav b, jf. §14-14: Bare far har rett, mor fyller ikke aktivitetskravet"),
    IKKE_HELTIDSARBEID("4037", "§14-11 første ledd bokstav b: Ikke heltidsarbeid"),
    SØKERS_SYKDOM_SKADE_IKKE_OPPFYLT("4038", "§14-11 første ledd bokstav c: Søkers sykdom/skade ikke oppfylt"),
    SØKERS_INNLEGGELSE_IKKE_OPPFYLT("4039", "§14-11 første ledd bokstav c: Søkers innleggelse ikke oppfylt"),
    BARNETS_INNLEGGELSE_IKKE_OPPFYLT("4040", "§14-11 første ledd bokstav d: Barnets innleggelse ikke oppfylt"),
    UTSETTELSE_FERIE_PÅ_BEVEGELIG_HELLIGDAG("4041", "§14-11 første ledd bokstav a: Avslag utsettelse ferie på bevegelig helligdag"),
    AKTIVITETSKRAVET_ARBEID_IKKE_OPPFYLT("4050", "§14-13 første ledd bokstav a: Aktivitetskravet arbeid ikke oppfylt"),
    AKTIVITETSKRAVET_OFFENTLIG_GODKJENT_UTDANNING_IKKE_OPPFYLT("4051", "§14-13 første ledd bokstav b: Aktivitetskravet offentlig godkjent utdanning ikke oppfylt"),
    AKTIVITETSKRAVET_OFFENTLIG_GODKJENT_UTDANNING_I_KOMBINASJON_MED_ARBEID_IKKE_OPPFYLT("4052", "§14-13 første ledd bokstav c: Aktivitetskravet offentlig godkjent utdanning i kombinasjon med arbeid ikke oppfylt"),
    AKTIVITETSKRAVET_MORS_SYKDOM_IKKE_OPPFYLT("4053", "§14-13 første ledd bokstav d: Aktivitetskravet mors sykdom/skade ikke oppfylt"),
    AKTIVITETSKRAVET_MORS_INNLEGGELSE_IKKE_OPPFYLT("4054", "§14-13 første ledd bokstav e: Aktivitetskravet mors innleggelse ikke oppfylt"),
    AKTIVITETSKRAVET_MORS_DELTAKELSE_PÅ_INTRODUKSJONSPROGRAM_IKKE_OPPFYLT("4055", "§14-13 første ledd bokstav f: Aktivitetskravet mors deltakelse på introduksjonsprogram ikke oppfylt"),
    AKTIVITETSKRAVET_MORS_DELTAKELSE_PÅ_KVALIFISERINGSPROGRAM_IKKE_OPPFYLT("4056", "§14-13 første ledd bokstav g: Aktivitetskravet mors deltakelse på kvalifiseringsprogram ikke oppfylt"),
    MORS_MOTTAK_AV_UFØRETRYGD_IKKE_OPPFYLT("4057", "§14-14 tredje ledd: Unntak for aktivitetskravet, mors mottak av uføretrygd ikke oppfylt"),
    STEBARNSADOPSJON_IKKE_NOK_DAGER("4058", "§14-5 tredje ledd: Unntak for Aktivitetskravet, stebarnsadopsjon - ikke nok dager"),
    FLERBARNSFØDSEL_IKKE_NOK_DAGER("4059", "§14-13 sjette ledd, jf. §14-9 fjerde ledd: Unntak for Aktivitetskravet, flerbarnsfødsel - ikke nok dager"),
    SAMTIDIG_UTTAK_IKKE_GYLDIG_KOMBINASJON("4060", "§14-10 sjette ledd: Samtidig uttak - ikke gyldig kombinasjon"),
    UTSETTELSE_FERIE_IKKE_DOKUMENTERT("4061", "§14-11 første ledd bokstav a, jf §21-3: Utsettelse ferie ikke dokumentert"),
    UTSETTELSE_ARBEID_IKKE_DOKUMENTERT("4062", "§14-11 første ledd bokstav b, jf §21-3: Utsettelse arbeid ikke dokumentert"),
    UTSETTELSE_SØKERS_SYKDOM_ELLER_SKADE_IKKE_DOKUMENTERT("4063", "§14-11 første ledd bokstav c og tredje ledd, jf §21-3: Utsettelse søkers sykdom/skade ikke dokumentert"),
    UTSETTELSE_SØKERS_INNLEGGELSE_IKKE_DOKUMENTERT("4064", "§14-11 første ledd bokstav c og tredje ledd, jf §21-3: Utsettelse søkers innleggelse ikke dokumentert"),
    UTSETTELSE_BARNETS_INNLEGGELSE_IKKE_DOKUMENTERT("4065", "§14-11 første ledd bokstav d, jf §21-3: Utsettelse barnets innleggelse - ikke dokumentert"),
    AKTIVITETSKRAVET_ARBEID_IKKE_DOKUMENTERT("4066", "§14-13 første ledd bokstav a, jf §21-3: Aktivitetskrav - arbeid ikke dokumentert"),
    AKTIVITETSKRAVET_UTDANNING_IKKE_DOKUMENTERT("4067", "§14-13 første ledd bokstav b, jf §21-3: Aktivitetskrav – utdanning ikke dokumentert"),
    AKTIVITETSKRAVET_ARBEID_I_KOMB_UTDANNING_IKKE_DOKUMENTERT("4068", "§14-13 første ledd bokstav c, jf §21-3: Aktivitetskrav – arbeid i komb utdanning ikke dokumentert"),
    AKTIVITETSKRAVET_SYKDOM_ELLER_SKADE_IKKE_DOKUMENTERT("4069", "§14-13 første ledd bokstav d og femte ledd, jf §21-3: Aktivitetskrav – sykdom/skade ikke dokumentert"),
    AKTIVITETSKRAVET_INNLEGGELSE_IKKE_DOKUMENTERT("4070", "§14-13 første ledd bokstav e og femte ledd, jf §21-3: Aktivitetskrav – innleggelse ikke dokumentert"),
    SØKER_ER_DØD("4071", "§14-10: Bruker er død"),
    BARNET_ER_DØD("4072", "§14-9 sjuende ledd: Barnet er dødt"),
    MOR_IKKE_RETT_TIL_FORELDREPENGER("4073", "§14-12 første ledd: Ikke rett til kvote fordi mor ikke har rett til foreldrepenger"),
    SYKDOM_SKADE_INNLEGGELSE_IKKE_DOKUMENTERT("4074", "§14-12 tredje ledd, jf §21-3: Avslag overføring kvote pga. sykdom/skade/innleggelse ikke dokumentert"),
    FAR_IKKE_RETT_PÅ_FELLESPERIODE_FORDI_MOR_IKKE_RETT("4075", "§14-9 første ledd: Ikke rett til fellesperiode fordi mor ikke har rett til foreldrepenger"),
    ANNEN_FORELDER_HAR_RETT("4076", "§14-9 femte ledd: Avslag overføring - annen forelder har rett til foreldrepenger"),
    FRATREKK_PLEIEPENGER("4077", "§14-10 a: Innvilget prematuruker, med fratrekk pleiepenger"),
    AVSLAG_GRADERING_PÅ_GRUNN_AV_FOR_SEN_SØKNAD("4080", "§14-16: Ikke gradering pga. for sen søknad"),
    AVSLAG_UTSETTELSE_PGA_FERIE_TILBAKE_I_TID("4081", "§14-11 første ledd bokstav a: Avslag utsettelse pga ferie tilbake i tid"),
    AVSLAG_UTSETTELSE_PGA_ARBEID_TILBAKE_I_TID("4082", "§14-11 første ledd bokstav b: Avslag utsettelse pga arbeid tilbake i tid"),
    DEN_ANDRE_PART_OVERLAPPENDE_UTTAK_IKKE_SØKT_INNVILGET_SAMTIDIG_UTTAK("4084", "§14-10 sjette ledd: Annen part har overlappende uttak, det er ikke søkt/innvilget samtidig uttak"),
    IKKE_SAMTYKKE_MELLOM_PARTENE("4085", "§14-10 sjette ledd: Det er ikke samtykke mellom partene"),
    DEN_ANDRE_PART_HAR_OVERLAPPENDE_UTTAKSPERIODER_SOM_ER_INNVILGET_UTSETTELSE("4086", "§14-10 sjette ledd og §14-11: Annen part har overlappende uttaksperioder som er innvilget utsettelse"),
    OPPHØR_MEDLEMSKAP("4087", "§14-2: Opphør medlemskap"),
    AKTIVITETSKRAVET_INTROPROGRAM_IKKE_DOKUMENTERT("4088", "§14-13 første ledd bokstav f, jf §21-3: Aktivitetskrav – introprogram ikke dokumentert"),
    AKTIVITETSKRAVET_KVP_IKKE_DOKUMENTERT("4089", "§14-13 første ledd bokstav g, jf §21-3: Aktivitetskrav – KVP ikke dokumentert"),
    HULL_MELLOM_SØKNADSPERIODE_ETTER_SISTE_UTTAK("4090", "§14-10 sjuende ledd: Ikke sammenhengende perioder"),
    HULL_MELLOM_SØKNADSPERIOD_ETTER_SISTE_UTSETTELSE("4091", "§14-10 sjuende ledd: Ikke sammenhengende perioder"),
    HAR_IKKE_ALENEOMSORG_FOR_BARNET("4092", "§14-12: Avslag overføring - har ikke aleneomsorg for barnet"),
    AVSLAG_GRADERING_SØKER_ER_IKKE_I_ARBEID("4093", "§14-16: Avslag gradering - søker er ikke i arbeid"),
    AVSLAG_GRADERINGSAVTALE_MANGLER_IKKE_DOKUMENTERT("4094", "§14-16 femte ledd, jf §21-3: Avslag graderingsavtale mangler - ikke dokumentert"),
    MOR_TAR_IKKE_ALLE_UKENE("4095", "§14-10 første ledd: Mor tar ikke alle 3 ukene før termin"),
    FØDSELSVILKÅRET_IKKE_OPPFYLT("4096", "§14-5: Fødselsvilkåret er ikke oppfylt"),
    ADOPSJONSVILKÅRET_IKKE_OPPFYLT("4097", "§14-5: Adopsjonsvilkåret er ikke oppfylt"),
    FORELDREANSVARSVILKÅRET_IKKE_OPPFYLT("4098", "§14-5: Foreldreansvarsvilkåret er ikke oppfylt"),
    OPPTJENINGSVILKÅRET_IKKE_OPPFYLT("4099", "§14-6: Opptjeningsvilkåret er ikke oppfylt"),
    UTTAK_FØR_OMSORGSOVERTAKELSE("4100", "§14-10 andre ledd: Uttak før omsorgsovertakelse"),
    BARE_FAR_RETT_IKKE_SØKT("4102", "§14-14, jf 14-13: Bare far har rett, mangler søknad uttak/aktivitetskrav"),
    MOR_FØRSTE_SEKS_UKER_IKKE_SØKT("4103", "§14-9 sjette ledd: Mangler søknad for første 6 uker etter fødsel"),
    STØNADSPERIODE_NYTT_BARN("4104", "§14-10 tredje ledd: Stønadsperiode for nytt barn"),
    FAR_SØKT_FØR_FØDSEL("4105", "§14-9 sjette ledd: Far/medmor søker uttak før fødsel/omsorg"),
    SØKERS_SYKDOM_SKADE_SEKS_UKER_IKKE_OPPFYLT("4110", "§14-11: Søkers sykdom/skade første 6 uker ikke oppfylt"),
    SØKERS_INNLEGGELSE_SEKS_UKER_IKKE_OPPFYLT("4111", "§14-11: Søkers innleggelse første 6 uker ikke oppfylt"),
    BARNETS_INNLEGGELSE_SEKS_UKER_IKKE_OPPFYLT("4112", "§14-11: Barnets innleggelse første 6 uker ikke oppfylt"),
    SØKERS_SYKDOM_ELLER_SKADE_SEKS_UKER_IKKE_DOKUMENTERT("4115", "§14-11, jf §21-3: Søkers sykdom/skade første 6 uker ikke dokumentert"),
    SØKERS_INNLEGGELSE_SEKS_UKER_IKKE_DOKUMENTERT("4116", "§14-11, jf §21-3: Søkers innleggelse første 6 uker ikke dokumentert"),
    BARNETS_INNLEGGELSE_SEKS_UKER_IKKE_DOKUMENTERT("4117", "§14-11, jf §21-3: Barnets innleggelse første 6 uker ikke dokumentert"),
    ;

    public static final String KODEVERK = "IKKE_OPPFYLT_AARSAK";

    private final String kode;
    private final String navn;

    IkkeOppfyltÅrsak(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @JsonCreator
    public static IkkeOppfyltÅrsak fraKode(String kode) {
        return Arrays.stream(IkkeOppfyltÅrsak.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet ikkeOppfyltÅrsak " + kode));
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
