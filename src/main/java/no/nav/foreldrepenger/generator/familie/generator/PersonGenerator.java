package no.nav.foreldrepenger.generator.familie.generator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.vtp.kontrakter.person.Kjønn;
import no.nav.foreldrepenger.vtp.kontrakter.person.Rolle;
import no.nav.foreldrepenger.vtp.kontrakter.person.Språk;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.AdresseDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.GeografiskTilknytningDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.MedlemskapDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.PersonstatusDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.SivilstandDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.StatsborgerskapDto;

public class PersonGenerator {

    private static final Språk DEFAULT_SPRÅK = Språk.NB;

    private PersonGenerator() {
        // Statisk implementasjon
    }

    public static PersonBuilder mor() {
        return kvinne(Rolle.MOR);
    }

    public static PersonBuilder mor(LocalDate fødselsdato) {
        return kvinne(Rolle.MOR, fødselsdato);
    }

    public static PersonBuilder medmor() {
        return kvinne(Rolle.MEDMOR);
    }

    public static PersonBuilder medmor(LocalDate fødselsdato) {
        return kvinne(Rolle.MEDMOR, fødselsdato);
    }

    public static PersonBuilder far() {
        return mann(Rolle.FAR);
    }

    public static PersonBuilder far(LocalDate fødselsdato) {
        return mann(Rolle.FAR, fødselsdato);
    }

    public static PersonBuilder kvinne(Rolle rolle) {
        return kvinne(rolle, FødselsdatoGenerator.tilfeldig());
    }

    public static PersonBuilder kvinne(Rolle rolle, LocalDate fødselsdato) {
        return PersonBuilder.ny()
                .rolle(rolle)
                .kjønn(Kjønn.K)
                .fødselsdato(fødselsdato)
                .språk(DEFAULT_SPRÅK)
                .geografiskTilknytning(GeografiskTilknytningDto.norsk())
                .adresser(norskAdresse())
                .personstatus(bosattFra(fødselsdato))
                .sivilstand(ugift())
                .medlemskap(norskMedlemskap())
                .statsborgerskap(norskStatsborgerskap());
    }

    public static PersonBuilder mann(Rolle rolle) {
        return mann(rolle, FødselsdatoGenerator.tilfeldig());
    }

    public static PersonBuilder mann(Rolle rolle, LocalDate fødselsdato) {
        return PersonBuilder.ny()
                .rolle(rolle)
                .kjønn(Kjønn.M)
                .fødselsdato(fødselsdato)
                .språk(DEFAULT_SPRÅK)
                .geografiskTilknytning(GeografiskTilknytningDto.norsk())
                .adresser(norskAdresse())
                .personstatus(bosattFra(fødselsdato))
                .sivilstand(ugift())
                .medlemskap(norskMedlemskap())
                .statsborgerskap(norskStatsborgerskap());
    }

    public static List<StatsborgerskapDto> norskStatsborgerskap() {
        return new ArrayList<>(List.of(new StatsborgerskapDto(CountryCode.NO)));
    }

    public static List<MedlemskapDto> norskMedlemskap() {
        return new ArrayList<>(); // Her skal bare utenlandske medlemskap spesifiseres
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
                AdresseDto.AdresseType.POSTADRESSE,
                land,
                null,
                LocalDate.now().minusYears(1),
                null
        );

        return new ArrayList<>(List.of(adresse));
    }

}
