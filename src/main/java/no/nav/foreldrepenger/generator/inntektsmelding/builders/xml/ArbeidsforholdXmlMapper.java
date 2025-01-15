package no.nav.foreldrepenger.generator.inntektsmelding.builders.xml;

import java.time.LocalDate;
import java.util.Objects;

import no.nav.foreldrepenger.autotest.util.CollectionUtils;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.inntektsmelding.xml.kodeliste._20210216.ÅrsakUtsettelseKodeliste;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Arbeidsforhold;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Periode;
import no.seres.xsd.nav.inntektsmelding_m._20181211.UtsettelseAvForeldrepenger;

class ArbeidsforholdXmlMapper {

    private ArbeidsforholdXmlMapper() {
        // Skjul ctor
    }

    public static Arbeidsforhold map(Inntektsmelding.Arbeidsforhold imArb, ObjectFactory objectFactory) {
        Objects.requireNonNull(imArb.beregnetInntekt(), "Beregnet inntekt kan ikke være null");

        var arbeidsforhold = objectFactory.createArbeidsforhold();

        var inntekt = objectFactory.createInntekt();
        inntekt.setBeloep(objectFactory.createInntektBeloep(imArb.beregnetInntekt()));
        arbeidsforhold.setBeregnetInntekt(objectFactory.createArbeidsforholdBeregnetInntekt(inntekt));

        arbeidsforhold.setFoersteFravaersdag(objectFactory.createArbeidsforholdFoersteFravaersdag(imArb.førsteFraværsdag()));
        arbeidsforhold.setArbeidsforholdId(objectFactory.createArbeidsforholdArbeidsforholdId(imArb.arbeidsforholdId()));

        if (CollectionUtils.isNotEmpty(imArb.utsettelserList())) {
            var utsettelseAvForeldrepengerListe = objectFactory.createUtsettelseAvForeldrepengerListe();
            utsettelseAvForeldrepengerListe.getUtsettelseAvForeldrepenger()
                    .addAll(imArb.utsettelserList()
                            .stream()
                            .map(utsettelse -> createUtsettelseAvForeldrepenger(objectFactory, utsettelse.årsak(), utsettelse.fom(),
                                    utsettelse.tom()))
                            .toList());
            arbeidsforhold.setUtsettelseAvForeldrepengerListe(
                    objectFactory.createArbeidsforholdUtsettelseAvForeldrepengerListe(utsettelseAvForeldrepengerListe));
        }

        return arbeidsforhold;
    }

    private static UtsettelseAvForeldrepenger createUtsettelseAvForeldrepenger(ObjectFactory objectFactory,
                                                                               Inntektsmelding.UtsettelseÅrsak aarsakTilUtsettelse,
                                                                               LocalDate periodeFom,
                                                                               LocalDate periodeTom) {
        var utsettelseAvForeldrepenger = objectFactory.createUtsettelseAvForeldrepenger();
        utsettelseAvForeldrepenger.setAarsakTilUtsettelse(
                objectFactory.createUtsettelseAvForeldrepengerAarsakTilUtsettelse(mapUtsettelseAarsak(aarsakTilUtsettelse).value()));
        utsettelseAvForeldrepenger.setPeriode(
                objectFactory.createUtsettelseAvForeldrepengerPeriode(createPeriode(objectFactory, periodeFom, periodeTom)));
        return utsettelseAvForeldrepenger;
    }

    private static ÅrsakUtsettelseKodeliste mapUtsettelseAarsak(Inntektsmelding.UtsettelseÅrsak årsak) {
        return switch (årsak) {
            case LOVBESTEMT_FERIE -> ÅrsakUtsettelseKodeliste.LOVBESTEMT_FERIE;
            case ARBEID -> ÅrsakUtsettelseKodeliste.ARBEID;
        };
    }

    private static Periode createPeriode(ObjectFactory objectFactory, LocalDate periodeFom, LocalDate periodeTom) {
        var periode = objectFactory.createPeriode();
        periode.setTom(objectFactory.createPeriodeTom(periodeTom));
        periode.setFom(objectFactory.createPeriodeFom(periodeFom));
        return periode;
    }
}
