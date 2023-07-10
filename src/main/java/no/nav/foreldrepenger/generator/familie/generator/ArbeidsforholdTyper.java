//package no.nav.foreldrepenger.generator.familie.generator;
//
//import static no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner.PRIVAT_ARBEIDSGIVER;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto;
//import no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsforholdDto;
//import no.nav.foreldrepenger.vtp.kontrakter.v2.Arbeidsforholdstype;
//import no.nav.foreldrepenger.vtp.kontrakter.v2.Arbeidsgiver;
//import no.nav.foreldrepenger.vtp.kontrakter.v2.OrganisasjonDto;
//import no.nav.foreldrepenger.vtp.kontrakter.v2.PrivatArbeidsgiver;
//
//public class ArbeidsforholdTyper {
//
//    private final TestOrganisasjoner testOrganisasjoner = new TestOrganisasjoner();
//
//    public ArbeidsforholdTyper() {
//    }
//
//    // TODO: Litt for mye teleskop. Bruker Builder..
//    public ArbeidsforholdDto enkeltArbeidsforhold(LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
//        return enkeltArbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), fom, null, 100, arbeidsavtaler);
//    }
//
//    public ArbeidsforholdDto enkeltArbeidsforhold(LocalDate fom, Integer stillingsprosent, ArbeidsavtaleDto... arbeidsavtaler) {
//        return enkeltArbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), fom, null, stillingsprosent, arbeidsavtaler);
//    }
//
//    public ArbeidsforholdDto enkeltArbeidsforhold(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
//        return enkeltArbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), fom, tom, arbeidsavtaler);
//    }
//
//    public  ArbeidsforholdDto enkeltArbeidsforhold(LocalDate fom, LocalDate tom, Integer stillingsprosent, ArbeidsavtaleDto... arbeidsavtaler) {
//        return enkeltArbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), fom, tom, stillingsprosent, arbeidsavtaler);
//    }
//
//    public ArbeidsforholdDto enkeltArbeidsforhold(OrganisasjonDto organisasjonDto, String arbeidsforholdId, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
//        return enkeltArbeidsforhold(organisasjonDto, arbeidsforholdId, fom, null, 100, arbeidsavtaler);
//    }
//
//
//    public ArbeidsforholdDto enkeltArbeidsforhold(OrganisasjonDto organisasjonDto, String arbeidsforholdId, LocalDate fom, Integer stillingsprosent, ArbeidsavtaleDto... arbeidsavtaler) {
//        return enkeltArbeidsforhold(organisasjonDto, arbeidsforholdId, fom, null, stillingsprosent, arbeidsavtaler);
//    }
//
//    public ArbeidsforholdDto enkeltArbeidsforhold(OrganisasjonDto organisasjonDto, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
//        return enkeltArbeidsforhold(organisasjonDto, arbeidsforholdId, fom, tom, 100, arbeidsavtaler);
//    }
//
//    public ArbeidsforholdDto enkeltArbeidsforhold(OrganisasjonDto organisasjonDto, String arbeidsforholdId, LocalDate fom,
//                                                         LocalDate tom, Integer stillingsprosent, ArbeidsavtaleDto... arbeidsavtaler) {
//        return ArbeidsforholdDto.builder()
//                .organisasjon(organisasjonDto)
//                .arbeidsforholdId(arbeidsforholdId)
//                .ansettelsesperiodeFom(fom)
//                .ansettelsesperiodeTom(tom)
//                .arbeidsforholdstype(Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD)
//                .arbeidsavtaler(List.of(arbeidsavtaler).isEmpty() ? List.of(defaultArbeidsavtale(fom, tom, stillingsprosent)) : List.of(arbeidsavtaler))
//                .build();
//    }
//
//    public ArbeidsforholdDto enkeltArbeidsforhold(Arbeidsgiver arbeidsgiver, String arbeidsforholdId, LocalDate fom,
//                                                  LocalDate tom, Integer stillingsprosent, ArbeidsavtaleDto... arbeidsavtaler) {
//        return ArbeidsforholdDto.builder()
//                .arbeidsgiver(arbeidsgiver)
//                .arbeidsforholdId(arbeidsforholdId)
//                .ansettelsesperiodeFom(fom)
//                .ansettelsesperiodeTom(tom)
//                .arbeidsforholdstype(Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD)
//                .arbeidsavtaler(List.of(arbeidsavtaler).isEmpty() ? List.of(defaultArbeidsavtale(fom, tom, stillingsprosent)) : List.of(arbeidsavtaler))
//                .build();
//    }
//
//
//
//    /*
//     * FRILANS
//     */
//    public ArbeidsforholdDto frilansArbeidsforhold(LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
//        return frilansArbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), fom, null, arbeidsavtaler);
//    }
//
//    public ArbeidsforholdDto frilansArbeidsforhold(LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
//        return frilansArbeidsforhold(testOrganisasjoner.tilfeldigOrg(), testOrganisasjoner.arbeidsforholdId(), fom, tom, arbeidsavtaler);
//    }
//
//    public ArbeidsforholdDto frilansArbeidsforhold(OrganisasjonDto organisasjon, String arbeidsforholdId, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
//        return frilansArbeidsforhold(organisasjon, arbeidsforholdId, fom, null, arbeidsavtaler);
//    }
//
//    public ArbeidsforholdDto frilansArbeidsforhold(OrganisasjonDto organisasjon, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
//        return ArbeidsforholdDto.builder()
//                .organisasjon(organisasjon)
//                .arbeidsforholdId(arbeidsforholdId)
//                .ansettelsesperiodeFom(fom)
//                .ansettelsesperiodeTom(tom)
//                .arbeidsforholdstype(Arbeidsforholdstype.FRILANSER_OPPDRAGSTAKER_MED_MER)
//                .arbeidsavtaler(List.of(arbeidsavtaler).isEmpty() ? List.of(defaultArbeidsavtale(fom, tom, 100)) : List.of(arbeidsavtaler))
//                .build();
//    }
//
//
//    // TODO
//
//    /*
//     * Arbeidsforhold med privat arbeidsgiver
//     */
//    public ArbeidsforholdDto arbeidsforholdPrivatperson(LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
//        return arbeidsforholdPrivatperson(PRIVAT_ARBEIDSGIVER, testOrganisasjoner.arbeidsforholdId(), fom, null, 100, arbeidsavtaler);
//    }
//
//    public ArbeidsforholdDto arbeidsforholdPrivatperson(PrivatArbeidsgiver privatArbeidsgiver, String arbeidsforholdId, LocalDate fom, ArbeidsavtaleDto... arbeidsavtaler) {
//        return arbeidsforholdPrivatperson(privatArbeidsgiver, arbeidsforholdId, fom, null, 100, arbeidsavtaler);
//    }
//
//    public ArbeidsforholdDto arbeidsforholdPrivatperson(PrivatArbeidsgiver privatArbeidsgiver, String arbeidsforholdId, LocalDate fom, Integer stillingsprosent,
//                                                               ArbeidsavtaleDto... arbeidsavtaler) {
//        return arbeidsforholdPrivatperson(privatArbeidsgiver, arbeidsforholdId, fom, null, stillingsprosent, arbeidsavtaler);
//    }
//
//    public ArbeidsforholdDto arbeidsforholdPrivatperson(PrivatArbeidsgiver privatArbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom, ArbeidsavtaleDto... arbeidsavtaler) {
//        return arbeidsforholdPrivatperson(privatArbeidsgiver, arbeidsforholdId, fom, tom, 100, arbeidsavtaler);
//    }
//
//    public ArbeidsforholdDto arbeidsforholdPrivatperson(PrivatArbeidsgiver privatArbeidsgiver, String arbeidsforholdId, LocalDate fom, LocalDate tom,
//                                                               Integer stillingsprosent,
//                                                               ArbeidsavtaleDto... arbeidsavtaler) {
//        return ArbeidsforholdDto.builder()
//                .privatarbeidsgiver(privatArbeidsgiver)
//                .arbeidsforholdId(arbeidsforholdId)
//                .ansettelsesperiodeFom(fom)
//                .ansettelsesperiodeTom(tom)
//                .arbeidsforholdstype(Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD)
//                .arbeidsavtaler(List.of(arbeidsavtaler).isEmpty() ? List.of(defaultArbeidsavtale(fom, tom, stillingsprosent)) : List.of(arbeidsavtaler))
//                .build();
//    }
//
//    private ArbeidsavtaleDto defaultArbeidsavtale(LocalDate fom, LocalDate tom, Integer stillingsprosent) {
//        return ArbeidsavtaleDto.arbeidsavtale(fom, tom)
//                .stillingsprosent(stillingsprosent)
//                .build();
//    }
//}
