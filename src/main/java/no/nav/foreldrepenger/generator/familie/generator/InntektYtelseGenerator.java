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
import no.nav.foreldrepenger.vtp.kontrakter.person.skatt.SkatteopplysningDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.ytelse.YtelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.ytelse.YtelseType;

public class InntektYtelseGenerator {

    private final List<ArbeidsforholdDto> arbeidsforholdListe = new ArrayList<>();
    private final List<InntektsperiodeDto> inntektsperioder = new ArrayList<>();
    private final List<YtelseDto> ytelser = new ArrayList<>();
    private final List<SkatteopplysningDto> skatteopplysninger = new ArrayList<>();

    private final TestOrganisasjoner testOrganisasjoner = new TestOrganisasjoner();
    private static final int DEFAULT_ÅRSLØNN = 600_000;
    private static final int DEAFULT_STILLINGSPROSENT = 100;

    public static InntektYtelseGenerator ny() {
        return new InntektYtelseGenerator();
    }

    public InntektYtelseGenerator arbeidMedOpptjeningOver6G() {
        return arbeidsforhold(LocalDate.now().minusYears(3), 900_000);
    }

    public InntektYtelseGenerator arbeidMedOpptjeningUnder6G() {
        return arbeidsforhold(LocalDate.now().minusYears(3), 480_000);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(LocalDate fom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforholdUtenInntekt(testOrganisasjoner.tilfeldigOrg(), fom, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforholdUtenInntekt(testOrganisasjoner.tilfeldigOrg(), fom, tom, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(ArbeidsgiverDto arbeidsgiver, LocalDate fom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforholdUtenInntekt(arbeidsgiver, fom, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(ArbeidsgiverDto arbeidsgiver, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), fom, tom, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, fom, tom, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(DEAFULT_STILLINGSPROSENT, fom, DEFAULT_ÅRSLØNN, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(stillingsprosent, fom, null, årslønn, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(DEAFULT_STILLINGSPROSENT, fom, tom, DEFAULT_ÅRSLØNN, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(DEAFULT_STILLINGSPROSENT, fom, null, årslønn, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(DEAFULT_STILLINGSPROSENT, fom, tom, årslønn, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(Integer stillingsprosent, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(stillingsprosent, fom, tom, DEFAULT_ÅRSLØNN, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, tom, årslønn, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, LocalDate fom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), fom, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), fom, null, årslønn, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, fom, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, fom, tom, DEFAULT_ÅRSLØNN, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, DEAFULT_STILLINGSPROSENT, fom, tom, årslønn, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, null, årslønn, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, tom, årslønn, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, stillingsprosent, fom, null, årslønn, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, Integer årslønn, List<PermisjonDto> PermisjonDtoer) {
        return arbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), DEAFULT_STILLINGSPROSENT, fom, null, årslønn, PermisjonDtoer);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, stillingsprosent, fom, tom, årslønn, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver,
                                                 String arbeidsforholdId,
                                                 Integer stillingsprosent,
                                                 LocalDate fom,
                                                 LocalDate tom,
                                                 Integer årslønn,
                                                 List<PermisjonDto> PermisjonDtoer,
                                                 ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        var arbeidsforholdDto = ArbeidsforholdDto.builder()
                .medArbeidsgiver(arbeidsgiver)
                .medArbeidsforholdId(arbeidsforholdId)
                .medAnsettelsesperiodeFom(fom)
                .medAnsettelsesperiodeTom(tom)
                .medArbeidsforholdstype(Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD)
                .medArbeidsavtaler(List.of(ArbeidsavtaleDtor).isEmpty() ? List.of(defaultArbeidsavtaleDto(fom, tom, stillingsprosent)) : List.of(ArbeidsavtaleDtor))
                .medPermisjoner(PermisjonDtoer != null ? PermisjonDtoer : List.of())
                .build();
        return arbeidsforhold(arbeidsforholdDto, årslønn);
    }

    public InntektYtelseGenerator frilans(LocalDate fom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return frilans(fom, DEFAULT_ÅRSLØNN, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator frilans(Integer stillingsprosent, LocalDate fom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return frilans(fom, DEFAULT_ÅRSLØNN, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator frilans(LocalDate fom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return frilans(fom, null, årslønn, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator frilans(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return frilans(fom, tom, DEFAULT_ÅRSLØNN, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator frilans(LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return frilans(DEAFULT_STILLINGSPROSENT, fom, tom, årslønn, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator frilans(Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return frilans(stillingsprosent, fom, null, årslønn, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator frilans(Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return frilans(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, tom, årslønn, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator frilans(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return frilans(arbeidsgiver, arbeidsforholdId, fom, null, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator frilans(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        return frilans(arbeidsgiver, arbeidsforholdId, DEAFULT_STILLINGSPROSENT, fom, tom, DEFAULT_ÅRSLØNN, ArbeidsavtaleDtor);
    }

    public InntektYtelseGenerator frilans(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... ArbeidsavtaleDtor) {
        var frilansArbeidsforhold = ArbeidsforholdDto.builder()
                .medArbeidsgiver(arbeidsgiver)
                .medArbeidsforholdId(arbeidsforholdId)
                .medAnsettelsesperiodeFom(fom)
                .medAnsettelsesperiodeTom(tom)
                .medArbeidsforholdstype(Arbeidsforholdstype.FRILANSER_OPPDRAGSTAKER_MED_MER)
                .medArbeidsavtaler(List.of(ArbeidsavtaleDtor).isEmpty() ? List.of(defaultArbeidsavtaleDto(fom, tom, stillingsprosent)) : List.of(ArbeidsavtaleDtor))
                .build();
        return arbeidsforhold(frilansArbeidsforhold, årslønn);
    }

    private ArbeidsavtaleDto defaultArbeidsavtaleDto(LocalDate fom, LocalDate tom, Integer stillingsprosent) {
        return new ArbeidsavtaleDto(null, stillingsprosent, null, null, fom, tom);
    }

    private InntektYtelseGenerator arbeidsforhold(ArbeidsforholdDto arbeidsforhold, Integer årslønn) {
        arbeidsforholdListe.add(arbeidsforhold);
        if (årslønn != null) {
            return inntektsperiode(arbeidsforhold, årslønn / 12);
        }
        return this;
    }

    public InntektYtelseGenerator inntektsperiode(ArbeidsforholdDto fraArbeidsforhold, Integer beløpPerMnd) {
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

    public InntektYtelseGenerator inntektsperiode(OrganisasjonDto organisasjon, LocalDate fom, LocalDate tom, Integer beløp) {
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

    public InntektYtelseGenerator inntektsperiode(InntektsperiodeDto inntektsperiode) {
        inntektsperioder.add(inntektsperiode);
        return this;
    }


    public InntektYtelseGenerator ytelse(YtelseType ytelseType, LocalDate fom, LocalDate tom, Integer dagsats, Integer utbetalt) {
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


    public InntektYtelseGenerator selvstendigNæringsdrivende(Integer gjennomsnittligNæringsinntekt) {
        var now = LocalDate.now().minusYears(1);
        for (int i = 0; i < 5; i++) {
            skatteopplysninger.add(new SkatteopplysningDto(now.getYear(), gjennomsnittligNæringsinntekt));
            now = now.minusYears(1);
        }
        return this;
    }

    public InntektYtelseGenerator harUføretrygd() {
        return ytelse(YtelseType.UFØREPENSJON, LocalDate.now().minusYears(1), LocalDate.now(), 0, 0);
    }

    public PersonDto.Builder medPå(PersonDto.Builder personBuilder) {
        return personBuilder
                .medArbeidsforhold(arbeidsforholdListe)
                .medInntekt(inntektsperioder)
                .medYtelser(ytelser)
                .medSkatteopplysninger(skatteopplysninger);
    }

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

    /**
     * Returnerer generatoren selv. Brukes for bakoverkompatibilitet med test-kode
     * som kaller .inntektytelse(generator.build()).
     */
    public InntektYtelseGenerator build() {
        return this;
    }
}
