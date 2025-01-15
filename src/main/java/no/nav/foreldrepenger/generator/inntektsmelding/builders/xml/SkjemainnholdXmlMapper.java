package no.nav.foreldrepenger.generator.inntektsmelding.builders.xml;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.inntektsmelding.xml.kodeliste._20210216.NaturalytelseKodeliste;
import no.nav.inntektsmelding.xml.kodeliste._20210216.YtelseKodeliste;
import no.nav.inntektsmelding.xml.kodeliste._20210216.ÅrsakInnsendingKodeliste;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Arbeidsgiver;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ArbeidsgiverPrivat;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Avsendersystem;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Kontaktinformasjon;
import no.seres.xsd.nav.inntektsmelding_m._20181211.NaturalytelseDetaljer;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.OpphoerAvNaturalytelseListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Skjemainnhold;

class SkjemainnholdXmlMapper {

    private SkjemainnholdXmlMapper() {
        // skjul ctor
    }

    public static Skjemainnhold map(Inntektsmelding im) {
        Objects.requireNonNull(im.arbeidsgiver(), "Inntektsmelding må ha arbeidsgiver eller arbeidsgiverPrivat");
        Objects.requireNonNull(im.ytelseType(), "Ytelse kan ikke være null");
        Objects.requireNonNull(im.arbeidstakerFnr(), "arbeidstakerFNR kan ikke være null");

        var objectFactory = new ObjectFactory();
        var skjemainnhold = new Skjemainnhold();

        skjemainnhold.setAarsakTilInnsending(ÅrsakInnsendingKodeliste.NY.value());
        skjemainnhold.setAvsendersystem(createAvsendersystem(objectFactory, Optional.ofNullable(im.avsender().system()).orElse("FS22"),
                Optional.ofNullable(im.avsender().versjon()).orElse("1.0")));
        skjemainnhold.setYtelse(mapTilYtelseKode(im.ytelseType()).value());
        skjemainnhold.setArbeidstakerFnr(im.arbeidstakerFnr());

        var ag = im.arbeidsgiver();
        if (ag.erPrivatArbeidsgiver()) {
            skjemainnhold.setArbeidsgiverPrivat(objectFactory.createSkjemainnholdArbeidsgiverPrivat(
                    createArbeidsgiverPrivat(ag.arbeidsgiverIdentifikator(), ag.kontaktnummer(), ag.navn())));
        } else {
            skjemainnhold.setArbeidsgiver(objectFactory.createSkjemainnholdArbeidsgiver(
                    createArbeidsgiver(ag.arbeidsgiverIdentifikator(), ag.kontaktnummer(), ag.navn())));
        }

        skjemainnhold.setStartdatoForeldrepengeperiode(
                objectFactory.createSkjemainnholdStartdatoForeldrepengeperiode(im.arbeidsforhold().foersteFravaarsdag()));

        if (im.opphoerAvNaturalytelse() != null && !im.opphoerAvNaturalytelse().isEmpty()) {
            skjemainnhold.setOpphoerAvNaturalytelseListe(objectFactory.createSkjemainnholdOpphoerAvNaturalytelseListe(
                    mapOpphoerAvNaturalytelse(objectFactory, im.opphoerAvNaturalytelse())));
        }

        var arbeidsforhold = ArbeidsforholdXmlMapper.map(im.arbeidsforhold(), objectFactory);
        skjemainnhold.setArbeidsforhold(objectFactory.createSkjemainnholdArbeidsforhold(arbeidsforhold));

        if (im.refusjon() != null) {
            var refusjon = RefusjonXmlMapper.map(im.refusjon(), objectFactory);
            skjemainnhold.setRefusjon(objectFactory.createSkjemainnholdRefusjon(refusjon));
        }

        return skjemainnhold;
    }

    private static Avsendersystem createAvsendersystem(ObjectFactory objectFactory, String avsenderSystem, String systemVersjon) {
        var as = new Avsendersystem();
        as.setSystemnavn(avsenderSystem);
        as.setSystemversjon(systemVersjon);
        as.setInnsendingstidspunkt(objectFactory.createAvsendersystemInnsendingstidspunkt(LocalDateTime.now()));
        return as;
    }

    private static Arbeidsgiver createArbeidsgiver(String virksomhetsnummer,
                                                   String kontaktinformasjonTLF,
                                                   String kontaktinformasjonNavn) {
        var ag = new Arbeidsgiver();
        ag.setVirksomhetsnummer(virksomhetsnummer);
        var kontaktinformasjon = new Kontaktinformasjon();
        kontaktinformasjon.setTelefonnummer(kontaktinformasjonTLF);
        kontaktinformasjon.setKontaktinformasjonNavn(kontaktinformasjonNavn);
        ag.setKontaktinformasjon(kontaktinformasjon);
        return ag;
    }

    private static ArbeidsgiverPrivat createArbeidsgiverPrivat(String arbeidsgiverFnr,
                                                               String kontaktinformasjonTLF,
                                                               String kontaktinformasjonNavn) {
        var ag = new ArbeidsgiverPrivat();
        ag.setArbeidsgiverFnr(arbeidsgiverFnr);
        var kontaktinformasjon = new Kontaktinformasjon();
        kontaktinformasjon.setTelefonnummer(kontaktinformasjonTLF);
        kontaktinformasjon.setKontaktinformasjonNavn(kontaktinformasjonNavn);
        ag.setKontaktinformasjon(kontaktinformasjon);
        return ag;
    }

    private static NaturalytelseDetaljer createNaturalytelseDetaljer(ObjectFactory of,
                                                                     BigDecimal belopPrMnd,
                                                                     LocalDate fom,
                                                                     NaturalytelseKodeliste kodelisteNaturalytelse) {
        var naturalytelseDetaljer = of.createNaturalytelseDetaljer();
        naturalytelseDetaljer.setBeloepPrMnd(of.createNaturalytelseDetaljerBeloepPrMnd(belopPrMnd));
        naturalytelseDetaljer.setFom(of.createNaturalytelseDetaljerFom(fom));
        naturalytelseDetaljer.setNaturalytelseType(of.createNaturalytelseDetaljerNaturalytelseType(kodelisteNaturalytelse.value()));

        return naturalytelseDetaljer;

    }

    private static YtelseKodeliste mapTilYtelseKode(Inntektsmelding.@NotNull YtelseType ytelseType) {
        return switch (ytelseType) {
            case FORELDREPENGER -> YtelseKodeliste.FORELDREPENGER;
            case SVANGERSKAPSPENGER -> YtelseKodeliste.SVANGERSKAPSPENGER;
        };
    }

    private static OpphoerAvNaturalytelseListe mapOpphoerAvNaturalytelse(ObjectFactory of,
                                                                         List<Inntektsmelding.OpphoerAvNaturalytelse> opphoerAvNaturalytelse) {
        var opphoerAvNaturalytelseListe = new OpphoerAvNaturalytelseListe();
        opphoerAvNaturalytelse.forEach(opphoer -> opphoerAvNaturalytelseListe.getOpphoerAvNaturalytelse()
                .add(createNaturalytelseDetaljer(of, opphoer.beloepPrMnd(), opphoer.fom(),
                        mapTilKodelisete(opphoer.natyralYtelseType()))));
        return opphoerAvNaturalytelseListe;
    }

    private static NaturalytelseKodeliste mapTilKodelisete(Inntektsmelding.NaturalytelseType naturalytelseType) {
        return switch (naturalytelseType) {
            case BIL -> NaturalytelseKodeliste.BIL;
            case KOST_DAGER -> NaturalytelseKodeliste.KOST_DAGER;
            case FRI_TRANSPORT -> NaturalytelseKodeliste.FRI_TRANSPORT;
            case ANNET -> NaturalytelseKodeliste.ANNET;
            case BOLIG -> NaturalytelseKodeliste.BOLIG;
            case LOSJI -> NaturalytelseKodeliste.LOSJI;
            case OPSJONER -> NaturalytelseKodeliste.OPSJONER;
            case ELEKTRISK_KOMMUNIKASJON -> NaturalytelseKodeliste.ELEKTRONISK_KOMMUNIKASJON;
            case KOST_DOEGN -> NaturalytelseKodeliste.KOST_DOEGN;
            case BEDRIFTSBARNEHAGEPLASS -> NaturalytelseKodeliste.BEDRIFTSBARNEHAGEPLASS;
            case RENTEFORDEL_LÅN -> NaturalytelseKodeliste.RENTEFORDEL_LAAN;
            case TILSKUDD_BARNEHAGEPLASS -> NaturalytelseKodeliste.TILSKUDD_BARNEHAGEPLASS;
            case KOSTBESPARELSE_I_HJEMMET -> NaturalytelseKodeliste.KOSTBESPARELSE_I_HJEMMET;
            case BESØKSREISER_HJEMMET_ANNET -> NaturalytelseKodeliste.BESOEKSREISER_HJEMMET_ANNET;
            case SKATTEPLIKTIG_DEL_FORSIKRINGER -> NaturalytelseKodeliste.SKATTEPLIKTIG_DEL_FORSIKRINGER;
            case YRKEBIL_TJENESTLIGBEHOV_KILOMETER -> NaturalytelseKodeliste.YRKEBIL_TJENESTLIGBEHOV_KILOMETER;
            case YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS -> NaturalytelseKodeliste.YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS;
            case AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS -> NaturalytelseKodeliste.AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS;
            case INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING -> NaturalytelseKodeliste.INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING;
        };
    }

}
