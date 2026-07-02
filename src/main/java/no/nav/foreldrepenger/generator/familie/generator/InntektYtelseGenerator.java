package no.nav.foreldrepenger.generator.familie.generator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.vtp.kontrakter.person.v2.ArbeidsavtaleDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.ArbeidsgiverDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.InntektsperiodeDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.PermisjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.SkatteopplysningDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.Arbeidsforholdstype;

public class InntektYtelseGenerator {

    private final List<ArbeidsforholdDto> arbeidsforhold = new ArrayList<>();
    private final List<InntektsperiodeDto> inntekt = new ArrayList<>();
    private final List<SkatteopplysningDto> skatteopplysninger = new ArrayList<>();
    private final TestOrganisasjoner testOrganisasjoner = new TestOrganisasjoner();
    private static final int DEFAULT_ÅRSLØNN = 600_000;
    private static final int DEFAULT_STILLINGSPROSENT = 100;

    public static InntektYtelseGenerator ny() {
        return new InntektYtelseGenerator();
    }

    public InntektYtelseGenerator arbeidMedOpptjeningOver6G() {
        return arbeidsforhold(LocalDate.now().minusYears(3), 900_000);
    }

    public InntektYtelseGenerator arbeidMedOpptjeningUnder6G() {
        return arbeidsforhold(LocalDate.now().minusYears(3), 480_000);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforholdUtenInntekt(testOrganisasjoner.tilfeldigOrg(), fom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforholdUtenInntekt(testOrganisasjoner.tilfeldigOrg(), fom, tom, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(ArbeidsgiverDto arbeidsgiver, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforholdUtenInntekt(arbeidsgiver, fom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(ArbeidsgiverDto arbeidsgiver, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), fom, tom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforholdUtenInntekt(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, fom, tom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(DEFAULT_STILLINGSPROSENT, fom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(stillingsprosent, fom, null, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(DEFAULT_STILLINGSPROSENT, fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(DEFAULT_STILLINGSPROSENT, fom, null, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(DEFAULT_STILLINGSPROSENT, fom, tom, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(Integer stillingsprosent, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(stillingsprosent, fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, tom, årslønn, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), fom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), fom, null, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, fom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, DEFAULT_STILLINGSPROSENT, fom, tom, årslønn, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, null, årslønn, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, tom, årslønn, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, stillingsprosent, fom, null, årslønn, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(LocalDate fom, Integer årslønn, List<PermisjonDto> permisjoner) {
        return arbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), DEFAULT_STILLINGSPROSENT, fom, null, årslønn, permisjoner);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return arbeidsforhold(arbeidsgiver, arbeidsforholdId, stillingsprosent, fom, tom, årslønn, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator arbeidsforhold(ArbeidsgiverDto arbeidsgiver,
                                                 String arbeidsforholdId,
                                                 Integer stillingsprosent,
                                                 LocalDate fom,
                                                 LocalDate tom,
                                                 Integer årslønn,
                                                 List<PermisjonDto> permisjoner,
                                                 ArbeidsavtaleDto... arbeidsavtaler) {
        var arbeidsforholdDto = ArbeidsforholdDto.builder()
                .arbeidsgiver(arbeidsgiver)
                .arbeidsforholdId(arbeidsforholdId)
                .ansettelsesperiodeFom(fom)
                .ansettelsesperiodeTom(tom)
                .arbeidsforholdstype(Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD)
                .arbeidsavtaler(List.of(arbeidsavtaler).isEmpty() ? List.of(defaultArbeidsavtale(fom, tom, stillingsprosent)) : List.of(arbeidsavtaler))
                .permisjoner(permisjoner)
                .build();
        return arbeidsforhold(arbeidsforholdDto, årslønn);
    }

    public InntektYtelseGenerator frilans(LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(fom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(Integer stillingsprosent, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(fom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(fom, null, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(DEFAULT_STILLINGSPROSENT, fom, tom, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(Integer stillingsprosent, LocalDate fom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(stillingsprosent, fom, null, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), stillingsprosent, fom, tom, årslønn, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(arbeidsgiver, arbeidsforholdId, fom, null, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
        return frilans(arbeidsgiver, arbeidsforholdId, DEFAULT_STILLINGSPROSENT, fom, tom, DEFAULT_ÅRSLØNN, arbeidsavtaler);
    }

    public InntektYtelseGenerator frilans(ArbeidsgiverDto arbeidsgiver, String arbeidsforholdId, Integer stillingsprosent, LocalDate fom, LocalDate tom, Integer årslønn, ArbeidsavtaleDto... arbeidsavtaler) {
        var frilansArbeidsforhold = ArbeidsforholdDto.builder()
                .arbeidsgiver(arbeidsgiver)
                .arbeidsforholdId(arbeidsforholdId)
                .ansettelsesperiodeFom(fom)
                .ansettelsesperiodeTom(tom)
                .arbeidsforholdstype(Arbeidsforholdstype.FRILANSER_OPPDRAGSTAKER_MED_MER)
                .arbeidsavtaler(List.of(arbeidsavtaler).isEmpty() ? List.of(defaultArbeidsavtale(fom, tom, stillingsprosent)) : List.of(arbeidsavtaler))
                .build();
        return arbeidsforhold(frilansArbeidsforhold, årslønn);
    }

    private ArbeidsavtaleDto defaultArbeidsavtale(LocalDate fom, LocalDate tom, Integer stillingsprosent) {
        return ArbeidsavtaleDto.arbeidsavtale(fom, tom)
                .stillingsprosent(stillingsprosent)
                .build();
    }

    private InntektYtelseGenerator arbeidsforhold(ArbeidsforholdDto arbeidsforholdDto, Integer årslønn) {
        arbeidsforhold.add(arbeidsforholdDto);
        if (årslønn != null) {
            return inntektsperiode(arbeidsforholdDto, årslønn / 12);
        }
        return this;
    }

    public InntektYtelseGenerator inntektsperiode(ArbeidsforholdDto fraArbeidsforhold, Integer beløpPerMnd) {
        var inntektsperiode = new InntektsperiodeDto(
                fraArbeidsforhold.arbeidsgiver(),
                fraArbeidsforhold.ansettelsesperiodeFom(),
                fraArbeidsforhold.ansettelsesperiodeTom() != null ? fraArbeidsforhold.ansettelsesperiodeTom() : LocalDate.now(),
                beløpPerMnd,
                InntektsperiodeDto.YtelseType.FASTLØNN,
                InntektsperiodeDto.FordelType.KONTANTYTELSE
        );
        return inntektsperiode(inntektsperiode);
    }

    public InntektYtelseGenerator inntektsperiode(no.nav.foreldrepenger.vtp.kontrakter.person.v2.OrganisasjonDto organisasjon, LocalDate fom, LocalDate tom, Integer beløp) {
        var inntektsperiode = new InntektsperiodeDto(
                organisasjon,
                fom,
                tom,
                beløp,
                InntektsperiodeDto.YtelseType.FASTLØNN,
                InntektsperiodeDto.FordelType.KONTANTYTELSE
        );
        return inntektsperiode(inntektsperiode);
    }

    public InntektYtelseGenerator inntektsperiode(InntektsperiodeDto inntektsperiode) {
        inntekt.add(inntektsperiode);
        return this;
    }

    public InntektYtelseGenerator selvstendigNæringsdrivende(Integer gjennomsnittligNæringsinntekt) {
        var now = LocalDate.now().minusYears(1);
        for (int i = 0; i < 5; i++) {
            skatteopplysninger.add(new SkatteopplysningDto(now.getYear(), gjennomsnittligNæringsinntekt));
            now = now.minusYears(1);
        }
        return this;
    }

    public InntektYtelseBundle build() {
        return new InntektYtelseBundle(arbeidsforhold, inntekt, skatteopplysninger);
    }
}
