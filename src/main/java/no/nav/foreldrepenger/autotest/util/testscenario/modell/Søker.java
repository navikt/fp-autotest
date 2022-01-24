package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import static no.nav.foreldrepenger.autotest.util.testscenario.modell.Aareg.arbeidsforholdFrilans;
import static no.nav.foreldrepenger.autotest.util.testscenario.modell.Sigrun.hentNæringsinntekt;
import static no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.InntektYtelseModell;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;

public abstract class Søker {

    private final Fødselsnummer fødselsnummer;
    private final AktørId aktørId;
    private final InntektYtelseModell inntektYtelseModell;
    private final Innsender innsender;

    private Long saksnummer = null;

    Søker(Fødselsnummer fødselsnummer, AktørId aktørId, InntektYtelseModell inntektYtelseModell, Innsender innsender) {
        this.fødselsnummer = fødselsnummer;
        this.aktørId = aktørId;
        this.inntektYtelseModell = inntektYtelseModell;
        this.innsender = innsender;
    }

    public Fødselsnummer fødselsnummer() {
        return fødselsnummer;
    }

    public AktørId aktørId() {
        return aktørId;
    }

    public Arbeidsforhold arbeidsforhold() {
        guardFlereArbeidsgivere();
        return arbeidsforholdene().get(0);
    }

    public List<Arbeidsforhold> arbeidsforholdene() {
        return Aareg.arbeidsforholdene(inntektYtelseModell.arbeidsforholdModell());
    }

    private List<Arbeidsforhold> arbeidsforholdene(ArbeidsgiverIdentifikator arbeidsgiverIdentifikator) {
        return Aareg.arbeidsforholdene(inntektYtelseModell.arbeidsforholdModell(), arbeidsgiverIdentifikator);
    }

    public Arbeidsgiver arbeidsgiver() {
        guardFlereArbeidsgivere();
        return arbeidsgivere().getArbeidsgivere().stream()
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Ingen arbeidsgivere funnet for søker i AAREG"));
    }

    public Arbeidsgivere arbeidsgivere(){
        var arbeidsgivere = new ArrayList<Arbeidsgiver>();
        inntektYtelseModell.arbeidsforholdModell().arbeidsforhold().stream()
                .filter(a -> ORDINÆRT_ARBEIDSFORHOLD.equals(a.arbeidsforholdstype()))
                .filter(distinctByKeys(no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold::arbeidsgiverOrgnr, no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold::arbeidsgiverAktorId))
                .forEach(a -> leggTilArbeidsforhold(arbeidsgivere, a));

        return new Arbeidsgivere(arbeidsgivere);
    }

    private boolean leggTilArbeidsforhold(ArrayList<Arbeidsgiver> arbeidsgivere, no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold a) {
        var arbeidsgiverIdentifikator = getArbeidsgiverIdentifikator(a);
        if (arbeidsgiverIdentifikator instanceof Orgnummer orgnummer) {
            return arbeidsgivere.add(new Virksomhet(
                    arbeidsgiverIdentifikator,
                    new Arbeidstaker(fødselsnummer, aktørId, månedsinntekt(orgnummer)),
                    arbeidsforholdene(arbeidsgiverIdentifikator),
                    innsender));
        } else if(arbeidsgiverIdentifikator instanceof AktørId id) {
            // Trenger fnr for person arbeidsgiver pga aktørid ikke kan brukes for IM
            var fnrArbeidsgiver = Fødselsnummer.valueOf(inntektYtelseModell.inntektskomponentModell().inntektsperioder().stream()
                    .filter(p -> p.arbeidsgiver() != null && p.arbeidsgiver().getAktørIdent().equalsIgnoreCase(id.value()))
                    .max(Comparator.nullsFirst(Comparator.comparing(Inntektsperiode::tom)))
                    .map(p -> p.arbeidsgiver().getIdent())
                    .orElseThrow(() -> new IllegalArgumentException("Fant ikke ident på aktørid!")));
            return arbeidsgivere.add(new PersonArbeidsgiver(
                    arbeidsgiverIdentifikator,
                    new Arbeidstaker(fødselsnummer, id, månedsinntekt(fnrArbeidsgiver)),
                    arbeidsforholdene(arbeidsgiverIdentifikator),
                    innsender,
                    fnrArbeidsgiver));
        }
        return false;
    }

    private ArbeidsgiverIdentifikator getArbeidsgiverIdentifikator(no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold arbeidsforhold) {
        if (arbeidsforhold.arbeidsgiverOrgnr() != null) {
            return Orgnummer.valueOf(arbeidsforhold.arbeidsgiverOrgnr());
        } else {
            return AktørId.valueOf(arbeidsforhold.arbeidsgiverAktorId());
        }
    }

    public Arbeidsgivere arbeidsgivere(Orgnummer orgnummer){
        return new Arbeidsgivere(arbeidsgivere().getArbeidsgivere().stream()
               .filter(a -> orgnummer.equals(a.arbeidsgiverIdentifikator()))
               .toList());
    }

    public LocalDate FrilansAnnsettelsesFom() {
        return arbeidsforholdFrilans(inntektYtelseModell.arbeidsforholdModell()).get(0)
                .ansettelsesperiodeFom();
    }

    public int månedsinntekt() {
        guardFlereArbeidsgivere();
        return Inntektskomponenten.månedsinntekt(inntektYtelseModell.inntektskomponentModell());
    }

    public int månedsinntekt(ArbeidsgiverIdentifikator arbeidsgiverIdentifikator) {
        return Inntektskomponenten.månedsinntekt(inntektYtelseModell.inntektskomponentModell(), arbeidsgiverIdentifikator);
    }

    public int månedsinntekt(Fødselsnummer fnrArbeidsgiver) {
        return Inntektskomponenten.månedsinntekt(inntektYtelseModell.inntektskomponentModell(), fnrArbeidsgiver);
    }

    public int månedsinntekt(Orgnummer orgnummer, ArbeidsforholdId arbeidsforholdId) {
        var stillingsproentAF = stillingsprosent(orgnummer, arbeidsforholdId);
        var stillingsprosentSamlet = arbeidsforholdene().stream()
                .map(Arbeidsforhold::stillingsprosent)
                .mapToInt(Integer::intValue).sum();

        return månedsinntekt(orgnummer) * stillingsproentAF / stillingsprosentSamlet;
    }

    public Integer stillingsprosent(Orgnummer orgnummer, ArbeidsforholdId arbeidsforholdId) {
        return arbeidsforholdene().stream()
                .filter(p -> orgnummer.equals(p.arbeidsgiverIdentifikasjon()))
                .filter(p -> arbeidsforholdId.equals(p.arbeidsforholdId()))
                .map(Arbeidsforhold::stillingsprosent)
                .findFirst()
                .orElse(0);
    }

    public double næringsinntekt(int beregnFraOgMedÅr) {
        return hentNæringsinntekt(inntektYtelseModell.sigrunModell(), beregnFraOgMedÅr);
    }

    public Søknad lagSøknad() {
        return null;
    }

    public long søk(Søknad søknad) {
        this.saksnummer = innsender.sendInnSøknad(søknad, aktørId, fødselsnummer, null);
        return this.saksnummer;
    }

    public long søk(Søknad søknad, Long saksnummer) {
        this.saksnummer = innsender.sendInnSøknad(søknad, aktørId, fødselsnummer, saksnummer);
        return this.saksnummer;
    }

    public long søk(Endringssøknad søknad) {
        return innsender.sendInnSøknad(søknad, aktørId, fødselsnummer, saksnummer);
    }

    // Brukes bare i tilfelle hvor en ønsker å sende IM uten registret arbeidsforhold i Aareg!
    // TODO: Gjøres på en annen måte?
    public void sendIMBasertPåInntekskomponenten(InntektsmeldingBuilder inntektsmelding) {
        innsender.sendInnInntektsmelding(inntektsmelding, aktørId, fødselsnummer, this.saksnummer);
    }

    public long søkPapirsøknadForeldrepenger() {
        this.saksnummer = innsender.sendInnPapirsøknadForeldrepenger(aktørId, fødselsnummer);
        return this.saksnummer;
    }

    public long sendInnPapirsøknadEEndringForeldrepenger() {
        guardTrengerEksisterendeBehandling();
        return innsender.sendInnPapirsøknadEEndringForeldrepenger(aktørId, fødselsnummer, this.saksnummer);
    }

    public long søkPapirsøknadEngangsstønad() {
        this.saksnummer = innsender.sendInnPapirsøknadEngangsstønad(aktørId, fødselsnummer);
        return this.saksnummer;
    }

    public void sendInnKlage() {
        guardTrengerEksisterendeBehandling();
        innsender.sendInnKlage(aktørId, fødselsnummer, this.saksnummer);
    }

    private void guardFlereArbeidsgivere() {
        var antallOrdinæreArbeidsforhold = inntektYtelseModell.arbeidsforholdModell().arbeidsforhold().stream()
                .filter(a -> a.arbeidsforholdstype().equals(ORDINÆRT_ARBEIDSFORHOLD))
                .map(no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold::arbeidsgiverOrgnr)
                .distinct()
                .count();
        if (antallOrdinæreArbeidsforhold > 1) {
            throw new UnsupportedOperationException("Det er flere arbeidsgivere. Spesifiser hvilken arbeidsgiver du ønsker månedsinntekt fra.");
        }
    }

    private void guardTrengerEksisterendeBehandling() {
        if (this.saksnummer == null) {
            throw new IllegalStateException("For å sende endringssøknad eller klage så trengs det en eksistrende behandling!");
        }
    }

    private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors)
    {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .toList();

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }

}
