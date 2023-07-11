package no.nav.foreldrepenger.generator.familie.generator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.vtp.kontrakter.v2.AdresseDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.GeografiskTilknytningDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.Kjønn;
import no.nav.foreldrepenger.vtp.kontrakter.v2.MedlemskapDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PersonDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PersonstatusDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.Rolle;
import no.nav.foreldrepenger.vtp.kontrakter.v2.SivilstandDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.StatsborgerskapDto;

public class PersonGenerator {

    private static final LocalDate DEFAULT_FØRDSELSDATO_MOR = LocalDate.now().minusYears(32);
    private static final LocalDate DEFAULT_FØDSELSDATO_FAR = LocalDate.now().minusYears(34);
    private static final String DEFAULT_SPRÅK = "NB";

    public static PersonDto.Builder mor() {
        return mor(DEFAULT_FØRDSELSDATO_MOR);
    }
    public static PersonDto.Builder mor(LocalDate fødselsdato) {
        return PersonDto.builder()
                .rolle(Rolle.MOR)
                .kjønn(Kjønn.K)
                .fødselsdato(fødselsdato)
                .språk(DEFAULT_SPRÅK)
                .geografiskTilknytning(GeografiskTilknytningDto.norsk())
                .adresser(norskAdresse())
                .personstatus(bosattFra(fødselsdato))
                .sivilstand(ugift())
                .medlemskap(norskMedlemskap())
                .statsborgerskap(norskStatsborgerskap())
                ;
    }

    public static PersonDto.Builder far() {
        return far(DEFAULT_FØDSELSDATO_FAR);
    }

    public static PersonDto.Builder far(LocalDate fødselsdato) {
        return PersonDto.builder()
                .rolle(Rolle.FAR)
                .kjønn(Kjønn.M)
                .fødselsdato(fødselsdato)
                .språk(DEFAULT_SPRÅK)
                .geografiskTilknytning(GeografiskTilknytningDto.norsk())
                .adresser(norskAdresse())
                .personstatus(bosattFra(fødselsdato))
                .sivilstand(ugift())
                .medlemskap(norskMedlemskap())
                .statsborgerskap(norskStatsborgerskap())
                ;
    }

    public static PersonDto.Builder medmor() {
        return medmor(DEFAULT_FØRDSELSDATO_MOR);
    }

    public static PersonDto.Builder medmor(LocalDate fødselsdato) {
        return PersonDto.builder()
                .rolle(Rolle.MEDMOR)
                .kjønn(Kjønn.K)
                .fødselsdato(fødselsdato)
                .språk(DEFAULT_SPRÅK)
                .geografiskTilknytning(GeografiskTilknytningDto.norsk())
                .adresser(norskAdresse())
                .personstatus(bosattFra(fødselsdato))
                .sivilstand(ugift())
                .medlemskap(norskMedlemskap())
                .statsborgerskap(norskStatsborgerskap())
                ;
    }


    public static List<StatsborgerskapDto> norskStatsborgerskap() {
        return new ArrayList<>(List.of(new StatsborgerskapDto(CountryCode.NO)));
    }

    public static List<MedlemskapDto> norskMedlemskap() {
        return new ArrayList<>(); // Her skal bare utenlandske medlemskap spesifiseres
    }

    public static List<SivilstandDto> gift() {
        return new ArrayList<>(List.of(new SivilstandDto(SivilstandDto.Sivilstander.GIFT, LocalDate.now().minusYears(4), null)));
    }

    public static List<SivilstandDto> ugift() {
        return new ArrayList<>(List.of(new SivilstandDto(SivilstandDto.Sivilstander.UGIF, null, null)));
    }

    public static List<PersonstatusDto> bosattFra(LocalDate fom) {
        return new ArrayList<>(List.of(new PersonstatusDto(PersonstatusDto.Personstatuser.BOSA, fom, null)));
    }

    public static List<AdresseDto> norskAdresse() {
        var adresse = new AdresseDto(
                AdresseDto.AdresseType.BOSTEDSADRESSE,
                CountryCode.NO,
                "000000001",
                LocalDate.now().minusYears(10),
                null
        );

        return new ArrayList<>(List.of(adresse));
    }

    public static List<AdresseDto> utenlandskAdresse(CountryCode land) {
        var adresse = new AdresseDto(
                AdresseDto.AdresseType.BOSTEDSADRESSE,
                CountryCode.NO,
                "000000001",
                LocalDate.now().minusYears(5),
                null
        );

        return new ArrayList<>(List.of(adresse));
    }

}
