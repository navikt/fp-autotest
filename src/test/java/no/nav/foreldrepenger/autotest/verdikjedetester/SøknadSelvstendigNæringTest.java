package no.nav.foreldrepenger.autotest.verdikjedetester;

import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner.NAV_BERGEN;
import static no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner.NAV_KLAGE_MIDT;
import static no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner.NAV_OSLO;
import static no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner.NAV_STORD;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektGenerator;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.OpptjeningMaler;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.soknad.kontrakt.SøkerDto;
import no.nav.foreldrepenger.soknad.kontrakt.opptjening.NæringDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.FamilierelasjonDto;

@Tag("verdikjede")
@Tag("foreldrepenger")
class SøknadSelvstendigNæringTest extends VerdikjedeTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(SøknadSelvstendigNæringTest.class);

    @Test
    @DisplayName("Sender foreldrepengesøknad med mange registrerte frilansoppdrag")
    @Description("Aareg forelegger 14 frilansoppdrag hos fire oppdragsgivere over omtrent åtte måneder, "
            + "med både avsluttede og pågående oppdrag.")
    void senderSøknadMedFlereFrilansoppdrag() {
        var sisteOppdragFom = LocalDate.now().minusMonths(18);
        var førsteOppdragFom = sisteOppdragFom.minusDays(235);
        var inntektYtelse = InntektGenerator.ny()
                .frilans(NAV_STORD, "frilans-1", 0, førsteOppdragFom, null, null)
                .frilans(NAV_STORD, "frilans-2", 0, førsteOppdragFom.plusDays(23), førsteOppdragFom.plusDays(50), null)
                .frilans(NAV_STORD, "frilans-2", 0, førsteOppdragFom.plusDays(54), null, null)
                .frilans(NAV_STORD, "frilans-3", 0, førsteOppdragFom.plusDays(55), null, null)
                .frilans(NAV_STORD, "frilans-4", 0, førsteOppdragFom.plusDays(84), null, null)
                .frilans(NAV_STORD, "frilans-5", 0, førsteOppdragFom.plusDays(118), førsteOppdragFom.plusDays(141), null)
                .frilans(NAV_STORD, "frilans-5", 0, førsteOppdragFom.plusDays(145), null, null)
                .frilans(NAV_STORD, "frilans-6", 0, førsteOppdragFom.plusDays(146), førsteOppdragFom.plusDays(164), null)
                .frilans(NAV_STORD, "frilans-6", 0, førsteOppdragFom.plusDays(176), null, null)
                .frilans(NAV_OSLO, "frilans-7", 0, førsteOppdragFom.plusDays(176), førsteOppdragFom.plusDays(200), null)
                .frilans(NAV_BERGEN, "frilans-8", 0, førsteOppdragFom.plusDays(182), null, null)
                .frilans(NAV_KLAGE_MIDT, "frilans-9", 0, førsteOppdragFom.plusDays(204), null, null)
                .frilans(NAV_BERGEN, "frilans-10", 0, førsteOppdragFom.plusDays(209), førsteOppdragFom.plusDays(232), null)
                .frilans(NAV_BERGEN, "frilans-10", 0, førsteOppdragFom.plusDays(235), null, null);
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntekt(inntektYtelse.build()).build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build();

        var mor = familie.mor();
        var søknad = lagSøknadForeldrepengerTerminFødsel(familie.barn().fødselsdato(), BrukerRolle.MOR)
                .medFrilansInformasjon(OpptjeningMaler.frilansOpptjening())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));

        var saksnummer = mor.søk(søknad);

        var frilansoppdrag = mor.førstegangssøknad().søkerinfo().frilansoppdrag();
        assertThat(frilansoppdrag).hasSize(14);
        assertThat(frilansoppdrag)
                .filteredOn(oppdrag -> oppdrag.navn().equals("NAV FAMILIE- OG PENSJONSYTELSER STORD"))
                .hasSize(9);
        assertThat(frilansoppdrag)
                .filteredOn(oppdrag -> oppdrag.navn().equals("NAV FAMILIE- OG PENSJONSYTELSER BERGEN"))
                .hasSize(3);
        assertThat(frilansoppdrag)
                .filteredOn(oppdrag -> oppdrag.tom() == null)
                .hasSize(9);
        assertThat(frilansoppdrag)
                .filteredOn(oppdrag -> oppdrag.navn().equals("NAV FAMILIE- OG PENSJONSYTELSER OSLO"))
                .singleElement()
                .extracting(SøkerDto.Frilansoppdrag::tom)
                .isEqualTo(førsteOppdragFom.plusDays(200));
        assertThat(frilansoppdrag.getFirst().fom()).isEqualTo(førsteOppdragFom);
        assertThat(frilansoppdrag.getLast().fom()).isEqualTo(sisteOppdragFom);
        LOG.info("Søknad med mange frilansoppdrag er sendt. Fødselsnummer: {}, saksnummer: {}",
                mor.fødselsnummer().value(), saksnummer.value());
    }

    @Test
    @DisplayName("Sender foreldrepengesøknad med én registrert fiskenæring")
    @Description("Brreg forelegger VTP FISKE, og søkerens samlede svar gjelder den registrerte fiskenæringen.")
    void senderSøknadMedFiskenæring() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny().selvstendigNæringsdrivende(200_000).build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build();

        var mor = familie.mor();
        var oppgittNæring = OpptjeningMaler.registrertEgenNæring(
                "999999999",
                "VTP FISKE",
                NæringDto.Virksomhetstype.FISKE,
                mor.næringStartdato(),
                mor.næringsinntekt(),
                false);
        var søknad = lagSøknadForeldrepengerTerminFødsel(familie.barn().fødselsdato(), BrukerRolle.MOR)
                .medSelvstendigNæringsdrivendeInformasjon(oppgittNæring)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));

        var saksnummer = mor.søk(søknad);

        assertThat(mor.førstegangssøknad().søkerinfo().selvstendigNæring())
                .singleElement()
                .extracting(
                        SøkerDto.SelvstendigNæring::navn,
                        SøkerDto.SelvstendigNæring::næringstype)
                .containsExactly("VTP FISKE", NæringDto.Virksomhetstype.FISKE);
        LOG.info("Søknad med fiskenæring er sendt. Fødselsnummer: {}, saksnummer: {}",
                mor.fødselsnummer().value(), saksnummer.value());
    }

    @Test
    @DisplayName("Sender foreldrepengesøknad med bare ett arbeidsforhold")
    @Description("Aareg forelegger ett ordinært arbeidsforhold og ingen frilansoppdrag eller selvstendige næringer.")
    void senderSøknadMedBareArbeidsgiver() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny()
                                .arbeidsforhold(NAV_OSLO, 100, LocalDate.now().minusYears(1), 480_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build();

        var mor = familie.mor();
        var søknad = lagSøknadForeldrepengerTerminFødsel(familie.barn().fødselsdato(), BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));

        var saksnummer = mor.søk(søknad);

        var søkerinfo = mor.førstegangssøknad().søkerinfo();
        assertThat(søkerinfo.arbeidsforhold())
                .singleElement()
                .extracting(SøkerDto.Arbeidsforhold::navn)
                .isEqualTo("NAV FAMILIE- OG PENSJONSYTELSER OSLO");
        assertThat(søkerinfo.frilansoppdrag()).isEmpty();
        assertThat(søkerinfo.selvstendigNæring()).isEmpty();
        LOG.info("Søknad med bare arbeidsgiver er sendt. Fødselsnummer: {}, saksnummer: {}",
                mor.fødselsnummer().value(), saksnummer.value());
    }

    @Test
    @DisplayName("Sender foreldrepengesøknad med ett arbeidsforhold og ett frilansoppdrag")
    @Description("Aareg forelegger ett ordinært arbeidsforhold og ett avsluttet frilansoppdrag.")
    void senderSøknadMedArbeidsgiverOgFrilansoppdrag() {
        var frilansFom = LocalDate.now().minusMonths(6);
        var frilansTom = LocalDate.now().minusMonths(1);
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny()
                                .arbeidsforhold(NAV_OSLO, 75, LocalDate.now().minusYears(1), 480_000)
                                .frilans(NAV_STORD, "frilans-1", 25, frilansFom, frilansTom, 120_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build();

        var mor = familie.mor();
        var søknad = lagSøknadForeldrepengerTerminFødsel(familie.barn().fødselsdato(), BrukerRolle.MOR)
                .medFrilansInformasjon(OpptjeningMaler.frilansOpptjening())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));

        var saksnummer = mor.søk(søknad);

        var søkerinfo = mor.førstegangssøknad().søkerinfo();
        assertThat(søkerinfo.arbeidsforhold())
                .singleElement()
                .extracting(SøkerDto.Arbeidsforhold::navn)
                .isEqualTo("NAV FAMILIE- OG PENSJONSYTELSER OSLO");
        assertThat(søkerinfo.frilansoppdrag())
                .singleElement()
                .extracting(
                        SøkerDto.Frilansoppdrag::navn,
                        SøkerDto.Frilansoppdrag::fom,
                        SøkerDto.Frilansoppdrag::tom)
                .containsExactly(
                        "NAV FAMILIE- OG PENSJONSYTELSER STORD",
                        frilansFom,
                        frilansTom);
        assertThat(søkerinfo.selvstendigNæring()).isEmpty();
        LOG.info("Søknad med arbeidsgiver og frilansoppdrag er sendt. Fødselsnummer: {}, saksnummer: {}",
                mor.fødselsnummer().value(), saksnummer.value());
    }

    @Test
    @DisplayName("Sender foreldrepengesøknad med flere registrerte næringer av forskjellige typer")
    @Description("Brreg forelegger fiske, jordbruk, dagmamma og annen næring. Inntekten gjelder samlet for virksomhetene, "
            + "og VTP FISKE brukes som teknisk representant fordi dagens DTO og XML krever ett orgnummer.")
    void senderSøknadMedFlereNæringstyper() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny()
                                .selvstendigNæringsdrivende(350_000)
                                .registrertNæring(
                                        "974760673",
                                        "VTP GÅRDSDRIFT",
                                        "ENK",
                                        "Enkeltpersonforetak",
                                        "01.110",
                                        "Dyrking av korn")
                                .registrertNæring(
                                        "889640782",
                                        "VTP DAGMAMMA",
                                        "ENK",
                                        "Enkeltpersonforetak",
                                        "88.910",
                                        "Barnehage")
                                .registrertNæring(
                                        "992257822",
                                        "VTP KONSULENT",
                                        "ENK",
                                        "Enkeltpersonforetak",
                                        "62.010",
                                        "Programmering")
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build();

        var mor = familie.mor();
        var samletNæringsinformasjon = OpptjeningMaler.registrertEgenNæring(
                "999999999",
                "VTP FISKE",
                NæringDto.Virksomhetstype.FISKE,
                mor.næringStartdato(),
                mor.næringsinntekt(),
                false);
        var søknad = lagSøknadForeldrepengerTerminFødsel(familie.barn().fødselsdato(), BrukerRolle.MOR)
                .medSelvstendigNæringsdrivendeInformasjon(samletNæringsinformasjon)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));

        var saksnummer = mor.søk(søknad);

        assertThat(mor.førstegangssøknad().søkerinfo().selvstendigNæring())
                .extracting(SøkerDto.SelvstendigNæring::næringstype)
                .containsExactly(
                        NæringDto.Virksomhetstype.FISKE,
                        NæringDto.Virksomhetstype.JORDBRUK_SKOGBRUK,
                        NæringDto.Virksomhetstype.DAGMAMMA,
                        NæringDto.Virksomhetstype.ANNEN);
        LOG.info("Søknad med flere næringstyper er sendt. Fødselsnummer: {}, saksnummer: {}",
                mor.fødselsnummer().value(), saksnummer.value());
    }
}
