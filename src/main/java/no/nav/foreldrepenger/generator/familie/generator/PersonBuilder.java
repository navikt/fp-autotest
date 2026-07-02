package no.nav.foreldrepenger.generator.familie.generator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.vtp.kontrakter.person.Adressebeskyttelse;
import no.nav.foreldrepenger.vtp.kontrakter.person.Kjønn;
import no.nav.foreldrepenger.vtp.kontrakter.person.Rolle;
import no.nav.foreldrepenger.vtp.kontrakter.person.Språk;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.AdresseDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.FamilierelasjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.GeografiskTilknytningDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.InntektsperiodeDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.MedlemskapDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.PersonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.PersonopplysningerDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.PersonstatusDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.SivilstandDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.SkatteopplysningDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.StatsborgerskapDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.YtelseDto;

/**
 * Erstatter v1s bruk av kontrakter.PersonDto.Builder direkte. Wrapper rundt v2s
 * PersonopplysningerDto.Builder + de fire flate listene (arbeidsforhold/inntekt/ytelser/skatteopplysninger)
 * som v2.PersonDto krever separat. Gir samme fluent API som testene bruker i dag.
 */
public class PersonBuilder {

    private final PersonopplysningerDto.Builder personopplysninger = PersonopplysningerDto.builder();
    private List<ArbeidsforholdDto> arbeidsforhold = new ArrayList<>();
    private List<InntektsperiodeDto> inntekt = new ArrayList<>();
    private final List<YtelseDto> ytelser = new ArrayList<>();
    private List<SkatteopplysningDto> skatteopplysninger = new ArrayList<>();

    PersonBuilder() {
    }

    public static PersonBuilder ny() {
        return new PersonBuilder();
    }

    public PersonBuilder uuid(UUID uuid) {
        personopplysninger.uuid(uuid);
        return this;
    }

    public UUID uuid() {
        return personopplysninger.build().uuid();
    }

    public PersonBuilder rolle(Rolle rolle) {
        personopplysninger.rolle(rolle);
        return this;
    }

    public PersonBuilder kjønn(Kjønn kjønn) {
        personopplysninger.kjønn(kjønn);
        return this;
    }

    public PersonBuilder fødselsdato(LocalDate fødselsdato) {
        personopplysninger.fødselsdato(fødselsdato);
        return this;
    }

    public PersonBuilder dødsdato(LocalDate dødsdato) {
        personopplysninger.dødsdato(dødsdato);
        return this;
    }

    public PersonBuilder språk(Språk språk) {
        personopplysninger.språk(språk);
        return this;
    }

    public PersonBuilder geografiskTilknytning(GeografiskTilknytningDto geografiskTilknytning) {
        personopplysninger.geografiskTilknytning(geografiskTilknytning);
        return this;
    }

    public PersonBuilder familierelasjoner(List<FamilierelasjonDto> familierelasjoner) {
        personopplysninger.familierelasjoner(familierelasjoner);
        return this;
    }

    public PersonBuilder statsborgerskap(List<StatsborgerskapDto> statsborgerskap) {
        personopplysninger.statsborgerskap(statsborgerskap);
        return this;
    }

    public PersonBuilder sivilstand(List<SivilstandDto> sivilstand) {
        personopplysninger.sivilstand(sivilstand);
        return this;
    }

    public PersonBuilder personstatus(List<PersonstatusDto> personstatus) {
        personopplysninger.personstatus(personstatus);
        return this;
    }

    public PersonBuilder medlemskap(List<MedlemskapDto> medlemskap) {
        personopplysninger.medlemskap(medlemskap);
        return this;
    }

    public PersonBuilder adresser(List<AdresseDto> adresser) {
        personopplysninger.adresser(adresser);
        return this;
    }

    public PersonBuilder addressebeskyttelse(Adressebeskyttelse adressebeskyttelse) {
        personopplysninger.adressebeskyttelse(adressebeskyttelse);
        return this;
    }

    public PersonBuilder erSkjermet(boolean erSkjermet) {
        personopplysninger.erSkjermet(erSkjermet);
        return this;
    }

    public PersonBuilder inntektytelse(InntektYtelseBundle bundle) {
        this.arbeidsforhold = new ArrayList<>(bundle.arbeidsforhold());
        this.inntekt = new ArrayList<>(bundle.inntekt());
        this.skatteopplysninger = new ArrayList<>(bundle.skatteopplysninger());
        return this;
    }

    public PersonBuilder ytelse(YtelseDto.YtelseType type, LocalDate fom, LocalDate tom, Integer dagsats, Integer utbetalingsgrad) {
        ytelser.add(new YtelseDto(type, fom, tom, dagsats, utbetalingsgrad, null));
        return this;
    }

    public PersonBuilder ytelse(YtelseDto.YtelseType type, LocalDate fom, LocalDate tom) {
        return ytelse(type, fom, tom, null, null);
    }

    public PersonDto build() {
        return PersonDto.builder()
                .personopplysninger(personopplysninger.build())
                .arbeidsforhold(arbeidsforhold)
                .inntekt(inntekt)
                .ytelser(ytelser)
                .skatteopplysninger(skatteopplysninger)
                .build();
    }
}
