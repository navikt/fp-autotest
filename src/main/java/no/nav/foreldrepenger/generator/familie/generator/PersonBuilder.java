package no.nav.foreldrepenger.generator.familie.generator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.vtp.kontrakter.person.PersonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.arbeidsforhold.ArbeidsavtaleDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.arbeidsforhold.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.arbeidsforhold.Arbeidsforholdstype;
import no.nav.foreldrepenger.vtp.kontrakter.person.arbeidsforhold.ArbeidsgiverDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.arbeidsforhold.OrganisasjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.arbeidsforhold.PermisjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.inntekt.InntektsperiodeDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.PersonopplysningerDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.skatt.SkatteopplysningDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.ytelse.YtelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.ytelse.YtelseType;

/**
 * Sentral builder for å bygge en komplett PersonDto med personopplysninger,
 * arbeidsforhold, inntekt, ytelser og skatteopplysninger.
 *
 * <p>Eksempel:
 * <pre>
 * PersonBuilder.mor()
 *     .arbeidMedOpptjeningOver6G()
 *     .build();
 *
 * PersonBuilder.far()
 *     .personopplysninger(p -> p.medSpråk(Språk.NN))
 *     .arbeidsforhold(LocalDate.now().minusYears(2), 500_000)
 *     .build();
 * </pre>
 */
public class PersonBuilder {

    private PersonopplysningerDto personopplysningerDto;
    private final List<ArbeidsforholdDto> arbeidsforholdListe = new ArrayList<>();
    private final List<InntektsperiodeDto> inntektsperioder = new ArrayList<>();
    private final List<YtelseDto> ytelser = new ArrayList<>();
    private final List<SkatteopplysningDto> skatteopplysninger = new ArrayList<>();

    private final TestOrganisasjoner testOrganisasjoner = new TestOrganisasjoner();
    private static final int DEFAULT_ÅRSLØNN = 600_000;
    private static final int DEFAULT_STILLINGSPROSENT = 100;

    public static PersonBuilder mor() {
        var personBuilder = new PersonBuilder();
        personBuilder.personopplysningerDto = PersonopplysningMaler.mor();
        return personBuilder;
    }

    public static PersonBuilder far() {
        var personBuilder = new PersonBuilder();
        personBuilder.personopplysningerDto = PersonopplysningMaler.far();
        return personBuilder;
    }

    public PersonBuilder forelder(PersonopplysningerDto personopplysninger) {
        this.personopplysningerDto = personopplysninger;
        return this;
    }



    // ==================== Convenience-metoder (arbeid + inntekt) ====================
    public PersonBuilder arbeidMedOpptjeningOver6G() {
        return arbeidsforhold(LocalDate.now().minusYears(3), 900_000);
    }

    public PersonBuilder arbeidMedOpptjeningUnder6G() {
        return arbeidsforhold(LocalDate.now().minusYears(3), 480_000);
    }


    // ==================== Arbeidsforhold uten inntekt ====================

    public PersonBuilder arbeidsforholdUtenInntekt(LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforholdUtenInntekt(testOrganisasjoner.tilfeldigOrg(), fom, null, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforholdUtenInntekt(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforholdUtenInntekt(testOrganisasjoner.tilfeldigOrg(), fom, tom, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforholdUtenInntekt(ArbeidsgiverDto arbeidsgiver, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforholdUtenInntekt(arbeidsgiver, fom, null, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforholdUtenInntekt(ArbeidsgiverDto arbeidsgiver, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), fom, tom, null, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforholdUtenInntekt(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, fom, tom, null, arbeidsavtaler);
    }


    // ==================== Arbeidsforhold (med inntekt) ====================

    public PersonBuilder arbeidsforhold(LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(DEFAULT_STILLINGSPROSENT, fom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(stillingsprosent, fom, null, årslønn, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(DEFAULT_STILLINGSPROSENT, fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(DEFAULT_STILLINGSPROSENT, fom, null, årslønn, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(DEFAULT_STILLINGSPROSENT, fom, tom, årslønn, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(Integer stillingsprosent, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(stillingsprosent, fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, tom, årslønn, null, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(ArbeidsgiverDto arbeidsgiver, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), fom, null, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(ArbeidsgiverDto arbeidsgiver, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), fom, null, årslønn, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, fom, null, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, DEFAULT_STILLINGSPROSENT, fom, tom, årslønn, null, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(ArbeidsgiverDto arbeidsgiver, Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, null, årslønn, null, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(ArbeidsgiverDto arbeidsgiver, Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, tom, årslønn, null, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, stillingsprosent, fom, null, årslønn, null, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(LocalDate fom, Integer årslønn, List<PermisjonDto> permisjoner) {
        return arbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), DEFAULT_STILLINGSPROSENT, fom, null, årslønn, permisjoner);
    }

    public PersonBuilder arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, stillingsprosent, fom, tom, årslønn, null, arbeidsavtaler);
    }

    public PersonBuilder arbeidsforhold(ArbeidsgiverDto arbeidsgiver,
                                        String arbeidsforholdId,
                                        Integer stillingsprosent,
                                        LocalDate fom,
                                        LocalDate tom,
                                        Integer årslønn,
                                        List<PermisjonDto> permisjoner,
                                        ArbeidsavtaleDto... arbeidsavtaler) {
        var arbeidsforholdDto = ArbeidsforholdDto.builder()
                .medArbeidsgiver(arbeidsgiver)
                .medArbeidsforholdId(arbeidsforholdId)
                .medAnsettelsesperiodeFom(fom)
                .medAnsettelsesperiodeTom(tom)
                .medArbeidsforholdstype(Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD)
                .medArbeidsavtaler(List.of(arbeidsavtaler).isEmpty() ? List.of(defaultArbeidsavtaleDto(fom, tom, stillingsprosent)) : List.of(arbeidsavtaler))
                .medPermisjoner(permisjoner != null ? permisjoner : List.of())
                .build();
        return leggTilArbeidsforhold(arbeidsforholdDto, årslønn);
    }


    // ==================== Frilans ====================

    public PersonBuilder frilans(LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(fom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public PersonBuilder frilans(Integer stillingsprosent, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(fom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public PersonBuilder frilans(LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(fom, null, årslønn, arbeidsavtaler);
    }

    public PersonBuilder frilans(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public PersonBuilder frilans(LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(DEFAULT_STILLINGSPROSENT, fom, tom, årslønn, arbeidsavtaler);
    }

    public PersonBuilder frilans(Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(stillingsprosent, fom, null, årslønn, arbeidsavtaler);
    }

    public PersonBuilder frilans(Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, tom, årslønn, arbeidsavtaler);
    }

    public PersonBuilder frilans(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(arbeidsgiver, arbeidsforholdId, fom, null, arbeidsavtaler);
    }

    public PersonBuilder frilans(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(arbeidsgiver, arbeidsforholdId, DEFAULT_STILLINGSPROSENT, fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public PersonBuilder frilans(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        var frilansArbeidsforhold = ArbeidsforholdDto.builder()
                .medArbeidsgiver(arbeidsgiver)
                .medArbeidsforholdId(arbeidsforholdId)
                .medAnsettelsesperiodeFom(fom)
                .medAnsettelsesperiodeTom(tom)
                .medArbeidsforholdstype(Arbeidsforholdstype.FRILANSER_OPPDRAGSTAKER_MED_MER)
                .medArbeidsavtaler(List.of(arbeidsavtaler).isEmpty() ? List.of(defaultArbeidsavtaleDto(fom, tom, stillingsprosent)) : List.of(arbeidsavtaler))
                .build();
        return leggTilArbeidsforhold(frilansArbeidsforhold, årslønn);
    }


    // ==================== Inntektsperioder ====================

    public PersonBuilder inntektsperiode(ArbeidsforholdDto fraArbeidsforhold, Integer beløpPerMnd) {
        var inntektsperiode = InntektsperiodeDto.builder()
                .medArbeidsgiver(fraArbeidsforhold.arbeidsgiver())
                .medFom(fraArbeidsforhold.ansettelsesperiodeFom())
                .medTom(fraArbeidsforhold.ansettelsesperiodeTom() != null ? fraArbeidsforhold.ansettelsesperiodeTom() : LocalDate.now())
                .medBeløp(beløpPerMnd)
                .medYtelseType(InntektsperiodeDto.YtelseType.FASTLØNN)
                .medInntektFordel(InntektsperiodeDto.FordelType.KONTANTYTELSE)
                .build();
        return inntektsperiode(inntektsperiode);
    }

    public PersonBuilder inntektsperiode(OrganisasjonDto organisasjon, LocalDate fom, LocalDate tom, Integer beløp) {
        var inntektsperiode = InntektsperiodeDto.builder()
                .medArbeidsgiver(organisasjon)
                .medFom(fom)
                .medTom(tom)
                .medBeløp(beløp)
                .medYtelseType(InntektsperiodeDto.YtelseType.FASTLØNN)
                .medInntektFordel(InntektsperiodeDto.FordelType.KONTANTYTELSE)
                .build();
        return inntektsperiode(inntektsperiode);
    }

    public PersonBuilder inntektsperiode(InntektsperiodeDto inntektsperiode) {
        inntektsperioder.add(inntektsperiode);
        return this;
    }


    // ==================== Ytelser ====================

    public PersonBuilder ytelse(YtelseType ytelseType, LocalDate fom, LocalDate tom, Integer dagsats, Integer utbetalt) {
        var ytelseDto = YtelseDto.builder()
                .medYtelse(ytelseType)
                .medFra(fom)
                .medTil(tom)
                .medDagsats(dagsats)
                .medUtbetalt(utbetalt)
                .build();
        ytelser.add(ytelseDto);

        // Legg til tilhørende inntektsperiode for ytelsen
        var inntektYtelseType = switch (ytelseType) {
            case ARBEIDSAVKLARINGSPENGER -> InntektsperiodeDto.YtelseType.AAP;
            case DAGPENGER -> InntektsperiodeDto.YtelseType.DAGPENGER;
            case SYKEPENGER -> InntektsperiodeDto.YtelseType.SYKEPENGER;
            case PLEIEPENGER -> InntektsperiodeDto.YtelseType.PLEIEPENGER;
            case OMSORGSPENGER -> InntektsperiodeDto.YtelseType.OMSORGSPENGER;
            case OPPLÆRINGSPENGER -> InntektsperiodeDto.YtelseType.OPPLÆRINGSPENGER;
            case FORELDREPENGER -> InntektsperiodeDto.YtelseType.FORELDREPENGER;
            case SVANGERSKAPSPENGER -> InntektsperiodeDto.YtelseType.SVANGERSKAPSPENGER;
            case UFØREPENSJON -> InntektsperiodeDto.YtelseType.FASTLØNN;
        };
        var inntektsperiode = InntektsperiodeDto.builder()
                .medArbeidsgiver(TestOrganisasjoner.NAV_YTELSE_BETALING)
                .medFom(fom)
                .medTom(tom)
                .medBeløp(utbetalt)
                .medYtelseType(inntektYtelseType)
                .medInntektFordel(InntektsperiodeDto.FordelType.KONTANTYTELSE)
                .build();
        return inntektsperiode(inntektsperiode);
    }

    public PersonBuilder harUføretrygd() {
        return ytelse(YtelseType.UFØREPENSJON, LocalDate.now().minusYears(1), LocalDate.now(), 0, 0);
    }


    // ==================== Skatteopplysninger ====================

    public PersonBuilder selvstendigNæringsdrivende(Integer gjennomsnittligNæringsinntekt) {
        var now = LocalDate.now().minusYears(1);
        for (int i = 0; i < 5; i++) {
            skatteopplysninger.add(new SkatteopplysningDto(now.getYear(), gjennomsnittligNæringsinntekt));
            now = now.minusYears(1);
        }
        return this;
    }


    // ==================== Aksessorer ====================

    public List<ArbeidsforholdDto> arbeidsforholdListe() {
        return arbeidsforholdListe;
    }

    public List<InntektsperiodeDto> inntektsperioder() {
        return inntektsperioder;
    }

    public List<YtelseDto> ytelser() {
        return ytelser;
    }

    public List<SkatteopplysningDto> skatteopplysninger() {
        return skatteopplysninger;
    }


    // ==================== Build ====================

    public PersonDto build() {
        return new PersonDto(personopplysningerDto, arbeidsforholdListe, inntektsperioder, ytelser, skatteopplysninger);
    }


    // ==================== Private hjelpemetoder ====================

    private ArbeidsavtaleDto defaultArbeidsavtaleDto(LocalDate fom, LocalDate tom, Integer stillingsprosent) {
        return new ArbeidsavtaleDto(null, stillingsprosent, null, null, fom, tom);
    }

    private PersonBuilder leggTilArbeidsforhold(ArbeidsforholdDto arbeidsforhold, Integer årslønn) {
        arbeidsforholdListe.add(arbeidsforhold);
        if (årslønn != null) {
            return inntektsperiode(arbeidsforhold, årslønn / 12);
        }
        return this;
    }
}
