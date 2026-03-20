package no.nav.foreldrepenger.generator.familie.generator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.AdresseDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.AdresserDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.GeografiskTilknytningDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.Kjønn;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.MedlemskapDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.PersonopplysningerDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.PersonstatusDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.Rolle;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.SivilstandDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.Språk;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.StatsborgerskapDto;

public class PersonopplysningMaler {

    private static final LocalDate DEFAULT_FØDSELSDATO_MOR = LocalDate.now().minusYears(32);
    private static final LocalDate DEFAULT_FØDSELSDATO_FAR = LocalDate.now().minusYears(34);
    private static final Språk DEFAULT_SPRÅK = Språk.NB;

    private PersonopplysningMaler() {
        // Statisk implementasjon
    }

    public static PersonopplysningerDto mor() {
        return mor(DEFAULT_FØDSELSDATO_MOR);
    }

    public static PersonopplysningerDto mor(LocalDate fødselsdato) {
        return personopplysninger(Rolle.MOR, Kjønn.K, fødselsdato);
    }

    public static PersonopplysningerDto far() {
        return far(DEFAULT_FØDSELSDATO_FAR);
    }

    public static PersonopplysningerDto far(LocalDate fødselsdato) {
        return personopplysninger(Rolle.FAR, Kjønn.M, fødselsdato);
    }

    public static PersonopplysningerDto medmor() {
        return medmor(DEFAULT_FØDSELSDATO_MOR);
    }

    public static PersonopplysningerDto medmor(LocalDate fødselsdato) {
        return personopplysninger(Rolle.MEDMOR, Kjønn.K, fødselsdato);
    }

    static PersonopplysningerDto personopplysninger(Rolle rolle, Kjønn kjønn, LocalDate fødselsdato) {
        return PersonopplysningerDto.builder()
                .medRolle(rolle)
                .medKjønn(kjønn)
                .medFødselsdato(fødselsdato)
                .medSpråk(DEFAULT_SPRÅK)
                .medGeografiskTilknytning(GeografiskTilknytningDto.norsk())
                .medAdresser(norskAdresser())
                .medPersonstatus(bosattFra(fødselsdato))
                .medSivilstand(ugift())
                .medMedlemskap(norskMedlemskap())
                .medStatsborgerskap(norskStatsborgerskap())
                .build();
    }

    public static List<StatsborgerskapDto> norskStatsborgerskap() {
        return new ArrayList<>(List.of(new StatsborgerskapDto(CountryCode.NO)));
    }

    public static List<MedlemskapDto> norskMedlemskap() {
        return new ArrayList<>(); // Her skal bare utenlandske medlemskap spesifiseres
    }

    public static List<SivilstandDto> ugift() {
        return new ArrayList<>(List.of(new SivilstandDto(SivilstandDto.Type.UGIFT, null, null)));
    }

    public static List<PersonstatusDto> bosattFra(LocalDate fom) {
        return new ArrayList<>(List.of(new PersonstatusDto(PersonstatusDto.Type.BOSA, fom, null)));
    }

    public static AdresserDto norskAdresser() {
        var adresse = new AdresseDto(
                AdresseDto.AdresseType.BOSTEDSADRESSE,
                CountryCode.NO,
                "000000001",
                LocalDate.now().minusYears(10),
                null
        );
        return new AdresserDto(new ArrayList<>(List.of(adresse)), null);
    }

    public static AdresserDto utenlandskAdresser(CountryCode land) {
        var adresse = new AdresseDto(
                AdresseDto.AdresseType.POSTADRESSE,
                land,
                null,
                LocalDate.now().minusYears(1),
                null
        );
        return new AdresserDto(new ArrayList<>(List.of(adresse)), null);
    }

}
