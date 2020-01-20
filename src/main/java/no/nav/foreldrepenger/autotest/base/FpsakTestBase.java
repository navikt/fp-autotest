package no.nav.foreldrepenger.autotest.base;

import no.nav.foreldrepenger.autotest.aktoerer.fordel.Fordel;
import no.nav.foreldrepenger.autotest.aktoerer.foreldrepenger.Saksbehandler;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.OmsorgsovertakelseÅrsak;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingErketype;
import no.nav.foreldrepenger.autotest.erketyper.MedlemskapErketyper;
import no.nav.foreldrepenger.autotest.erketyper.RelasjonTilBarnetErketyper;
import no.nav.foreldrepenger.autotest.erketyper.SøknadErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kodeverk;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenariodataDto;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;
import no.nav.inntektsmelding.xml.kodeliste._20180702.YtelseKodeliste;
import no.nav.inntektsmelding.xml.kodeliste._20180702.ÅrsakInnsendingKodeliste;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FpsakTestBase extends TestScenarioTestBase {

    /*
     * Aktører
     */
    protected Fordel fordel;
    protected Saksbehandler saksbehandler;
    protected Saksbehandler overstyrer;
    protected Saksbehandler beslutter;
    protected Saksbehandler klagebehandler;

    /*
     * VTP
     */
    protected SøknadErketyper foreldrepengeSøknadErketyper;
    protected InntektsmeldingErketype inntektsmeldingErketype;


    @BeforeEach
    public void setUp() {
        fordel = new Fordel();
        saksbehandler = new Saksbehandler();
        overstyrer = new Saksbehandler();
        beslutter = new Saksbehandler();
        klagebehandler = new Saksbehandler();

        foreldrepengeSøknadErketyper = new SøknadErketyper();
        inntektsmeldingErketype = new InntektsmeldingErketype();

    }

    protected Kodeverk hentKodeverk() {
        if (saksbehandler != null && saksbehandler.kodeverk != null) {
            return saksbehandler.kodeverk;
        }
        return null;
    }

    protected List<InntektsmeldingBuilder> makeInntektsmeldingFromTestscenario(TestscenarioDto testscenario, LocalDate startDatoForeldrepenger) {
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        return makeInntektsmeldingFromTestscenarioMedIdent(testscenario, søkerIdent, startDatoForeldrepenger, false);
    }
    @Deprecated
    protected List<InntektsmeldingBuilder> makeInntektsmeldingFromTestscenarioMedIdent(
            TestscenarioDto testscenario,
            String søkerIdent,
            LocalDate startDatoForeldrepenger,
            boolean erAnnenpart) {

        List<Inntektsperiode> inntektsperioder;
        List<Arbeidsforhold> arbeidsforholdEtterStartdatoFP;
        if (erAnnenpart == true) {
            inntektsperioder = testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioderSplittMånedlig();
            arbeidsforholdEtterStartdatoFP = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold();
        } else {
            inntektsperioder = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioderSplittMånedlig();
            arbeidsforholdEtterStartdatoFP = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold();
        }
        arbeidsforholdEtterStartdatoFP.stream()
                .filter(arbeidsforhold ->
                        arbeidsforhold.getAnsettelsesperiodeTom() == null ||
                                !arbeidsforhold.getAnsettelsesperiodeTom().isBefore(startDatoForeldrepenger))
                .collect(Collectors.toList());
        List<InntektsmeldingBuilder> inntektsmeldinger = new ArrayList<>();
        for (var arbeidsforhold : arbeidsforholdEtterStartdatoFP) {
            String arbeidsgiverOrgnr = arbeidsforhold.getArbeidsgiverOrgnr();
            Inntektsperiode sisteInntektsperiode = inntektsperioder.stream()
                    .filter(inntektsperiode -> inntektsperiode.getOrgnr().equals(arbeidsgiverOrgnr))
                    .max(Comparator.comparing(Inntektsperiode::getTom))
                    .orElseThrow(() -> new IllegalStateException("Utvikler feil: Arbeidsforhold mangler inntektsperiode"));
            Integer beløp = sisteInntektsperiode.getBeløp();
            inntektsmeldinger.add(lagInntektsmelding(beløp, søkerIdent, startDatoForeldrepenger, arbeidsgiverOrgnr));
        }

        return inntektsmeldinger;
    }
    @Deprecated
    protected List<InntektsmeldingBuilder> makeInntektsmeldingFromtestscenariodata(
            TestscenariodataDto testscenariodata,
            String søkerIdent,
            LocalDate startDatoForeldrepenger) {

        List<Inntektsperiode> inntektsperioder;
        List<Arbeidsforhold> arbeidsforholdEtterStartdatoFP;
        inntektsperioder = testscenariodata.getInntektskomponentModell().getInntektsperioderSplittMånedlig();
        arbeidsforholdEtterStartdatoFP = testscenariodata.getArbeidsforholdModell().getArbeidsforhold();

        arbeidsforholdEtterStartdatoFP.stream()
                .filter(arbeidsforhold ->
                        arbeidsforhold.getAnsettelsesperiodeTom() == null ||
                                !arbeidsforhold.getAnsettelsesperiodeTom().isBefore(startDatoForeldrepenger))
                .collect(Collectors.toList());
        List<InntektsmeldingBuilder> inntektsmeldinger = new ArrayList<>();
        for (var arbeidsforhold : arbeidsforholdEtterStartdatoFP) {
            String arbeidsgiverOrgnr = arbeidsforhold.getArbeidsgiverOrgnr();
            Inntektsperiode sisteInntektsperiode = inntektsperioder.stream()
                    .filter(inntektsperiode -> inntektsperiode.getOrgnr().equals(arbeidsgiverOrgnr))
                    .max(Comparator.comparing(Inntektsperiode::getTom))
                    .orElseThrow(() -> new IllegalStateException("Utvikler feil: Arbeidsforhold mangler inntektsperiode"));
            Integer beløp = sisteInntektsperiode.getBeløp();
            inntektsmeldinger.add(lagInntektsmelding(beløp, søkerIdent, startDatoForeldrepenger, arbeidsgiverOrgnr));
        }

        return inntektsmeldinger;
    }

    private EngangstønadBuilder lagEngangstønad(String aktørID, SøkersRolle søkersRolle){
        return new EngangstønadBuilder(aktørID, søkersRolle)
                .medMedlemskap(MedlemskapErketyper.medlemskapNorge());
    }
    public EngangstønadBuilder lagEngangstønadFødsel(String aktørID, SøkersRolle søkersRolle, LocalDate familiehendelse){
        return lagEngangstønad(aktørID, søkersRolle)
                .medSoekersRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(1, familiehendelse))
                .medTilleggsopplysninger("Autogenerert erketypetest" + søkersRolle.toString() + "søker på fødsel");
    }
    public EngangstønadBuilder lagEngangstønadTermin(String aktørID, SøkersRolle søkersRolle, LocalDate familiehendelse){
        return lagEngangstønad(aktørID, søkersRolle)
                .medTilleggsopplysninger("Autogenerert erketypetest" + søkersRolle.toString() + "søker på termin")
                .medSoekersRelasjonTilBarnet(RelasjonTilBarnetErketyper.termin(1, familiehendelse));
    }
    public EngangstønadBuilder lagEngangstønadAdopsjon(String aktørID, SøkersRolle søkersRolle, Boolean ektefellesBarn){
        return lagEngangstønad(aktørID, søkersRolle)
                .medTilleggsopplysninger("Autogenerert erketypetest" + søkersRolle.toString() + "søker på adopsjon")
                .medSoekersRelasjonTilBarnet(RelasjonTilBarnetErketyper.adopsjon(ektefellesBarn));
    }
    public EngangstønadBuilder lagEngangstønadOmsorg(String aktørID, SøkersRolle søkersRolle, OmsorgsovertakelseÅrsak årsak) {
        return lagEngangstønad(aktørID, søkersRolle)
                .medSoekersRelasjonTilBarnet(RelasjonTilBarnetErketyper.omsorgsovertakelse(
                        årsak))
                .medTilleggsopplysninger("Autogenerert erketypetest" + søkersRolle.toString() + "søker på omsorgsovertakelse");

    }


    protected InntektsmeldingBuilder lagInntektsmelding(Integer beløp, String fnr, LocalDate fpStartdato, String orgNr) {
        InntektsmeldingBuilder inntektsmelding = new InntektsmeldingBuilder()
                .medBeregnetInntekt(BigDecimal.valueOf(beløp))
                .medArbeidstakerFNR(fnr)
                .medYtelse(YtelseKodeliste.FORELDREPENGER)
                .medAarsakTilInnsending(ÅrsakInnsendingKodeliste.NY)
                .medStartdatoForeldrepengerperiodenFOM(fpStartdato)
                .medAvsendersystem("FS22", "1.0")
                .medArbeidsgiver(orgNr, "41925090");
        return inntektsmelding;
    }
    @Deprecated
    protected InntektsmeldingBuilder lagInntektsmeldingBuilderMedGradering(Integer beløp, String fnr, LocalDate fpStartdato, String orgNr, Integer arbeidsprosent, LocalDate graderingFom, LocalDate graderingTom) {
        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(beløp, fnr, fpStartdato, orgNr);
        inntektsmelding.medGradering(BigDecimal.valueOf(arbeidsprosent), graderingFom, graderingTom);
        return inntektsmelding;
    }
    @Deprecated
    protected InntektsmeldingBuilder lagInntektsmeldingBuilderPrivatArbeidsgiver(Integer beløp, String fnr, LocalDate fpStartdato, String fnrArbeidsgiver) {
        InntektsmeldingBuilder inntektsmelding = new InntektsmeldingBuilder()
                .medBeregnetInntekt(BigDecimal.valueOf(beløp))
                .medArbeidstakerFNR(fnr)
                .medYtelse(YtelseKodeliste.FORELDREPENGER)
                .medAarsakTilInnsending(ÅrsakInnsendingKodeliste.NY)
                .medStartdatoForeldrepengerperiodenFOM(fpStartdato)
                .medAvsendersystem("FS22", "1.0")
                .medArbeidsgiverPrivat(fnrArbeidsgiver, "41925090");
        return inntektsmelding;
    }
    @Deprecated
    protected InntektsmeldingBuilder lagInntektsmeldingBuilderMedEndringIRefusjonPrivatArbeidsgiver(Integer beløp, String fnr, LocalDate fpStartdato, String fnrArbeidsgiver, BigDecimal refusjon, Map<LocalDate, BigDecimal> endringRefusjonMap) {
        InntektsmeldingBuilder inntektsmelding = lagInntektsmeldingBuilderPrivatArbeidsgiver(beløp, fnr, fpStartdato, fnrArbeidsgiver);
        inntektsmelding.medRefusjonsBelopPerMnd(refusjon)
                .medEndringIRefusjonslist(endringRefusjonMap);
        return inntektsmelding;
    }

}
