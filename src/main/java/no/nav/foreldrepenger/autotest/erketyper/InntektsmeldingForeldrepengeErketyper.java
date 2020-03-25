package no.nav.foreldrepenger.autotest.erketyper;

import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenariodataDto;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;
import no.nav.inntektsmelding.xml.kodeliste._20180702.YtelseKodeliste;
import no.nav.inntektsmelding.xml.kodeliste._20180702.ÅrsakInnsendingKodeliste;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InntektsmeldingForeldrepengeErketyper {
    public static List<InntektsmeldingBuilder> makeInntektsmeldingFromTestscenario(TestscenarioDto testscenario, LocalDate startDatoForeldrepenger) {
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        return makeInntektsmeldingFromTestscenarioMedIdent(testscenario, søkerIdent, startDatoForeldrepenger, false);
    }
    @Deprecated
    public static List<InntektsmeldingBuilder> makeInntektsmeldingFromTestscenarioMedIdent(
            TestscenarioDto testscenario,
            String søkerIdent,
            LocalDate startDatoForeldrepenger,
            boolean erAnnenpart) {

        List<Inntektsperiode> inntektsperioder;
        List<Arbeidsforhold> arbeidsforholdEtterStartdatoFP;
        if (erAnnenpart) {
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
    public static List<InntektsmeldingBuilder> makeInntektsmeldingFromtestscenariodata(
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
    public static InntektsmeldingBuilder lagInntektsmelding(Integer beløp, String fnr, LocalDate fpStartdato, String orgNr) {
        return new InntektsmeldingBuilder()
                .medBeregnetInntekt(BigDecimal.valueOf(beløp))
                .medArbeidstakerFNR(fnr)
                .medYtelse(YtelseKodeliste.FORELDREPENGER)
                .medAarsakTilInnsending(ÅrsakInnsendingKodeliste.NY)
                .medStartdatoForeldrepengerperiodenFOM(fpStartdato)
                .medAvsendersystem("FS22", "1.0")
                .medArbeidsgiver(orgNr, "41925090");
    }
    @Deprecated
    public static InntektsmeldingBuilder lagInntektsmeldingBuilderMedGradering(Integer beløp, String fnr, LocalDate fpStartdato, String orgNr, Integer arbeidsprosent, LocalDate graderingFom, LocalDate graderingTom) {
        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(beløp, fnr, fpStartdato, orgNr);
        inntektsmelding.medGradering(BigDecimal.valueOf(arbeidsprosent), graderingFom, graderingTom);
        return inntektsmelding;
    }
    @Deprecated
    public static InntektsmeldingBuilder lagInntektsmeldingBuilderPrivatArbeidsgiver(Integer beløp, String fnr,
                                                                                 LocalDate fpStartdato, String fnrArbeidsgiver) {
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
    public static InntektsmeldingBuilder lagInntektsmeldingBuilderMedEndringIRefusjonPrivatArbeidsgiver(Integer beløp, String fnr, LocalDate fpStartdato, String fnrArbeidsgiver, BigDecimal refusjon, Map<LocalDate, BigDecimal> endringRefusjonMap) {
        InntektsmeldingBuilder inntektsmelding = lagInntektsmeldingBuilderPrivatArbeidsgiver(beløp, fnr, fpStartdato, fnrArbeidsgiver);
        inntektsmelding.medRefusjonsBelopPerMnd(refusjon)
                .medEndringIRefusjonslist(endringRefusjonMap);
        return inntektsmelding;
    }
}
