package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import static no.nav.foreldrepenger.autotest.util.testscenario.modell.Aareg.hentAnsettelsesFomForFrilans;
import static no.nav.foreldrepenger.autotest.util.testscenario.modell.Sigrun.hentNæringsinntekt;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.autotest.søknad.modell.Søknad;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.InntektYtelseModell;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;

public abstract class Søker {

    private final String fødselsnummer;
    private final InntektYtelseModell inntektYtelseModell;

    Søker(String fødselsnummer, InntektYtelseModell inntektYtelseModell) {
        this.fødselsnummer = fødselsnummer;
        this.inntektYtelseModell = inntektYtelseModell;
    }

    public String fødselsnummer() {
        return fødselsnummer;
    }

    public int månedsinntekt() {
        guardFlereArbeidsgivere();
        return inntektYtelseModell.inntektskomponentModell().inntektsperioder().stream()
                .max(Comparator.comparing(Inntektsperiode::tom))
                .map(Inntektsperiode::beløp)
                .orElse(0);
    }


    public int månedsinntekt(String orgnummer) {
        return inntektYtelseModell.inntektskomponentModell().inntektsperioder().stream()
                .filter(inntektsperiode -> orgnummer.equalsIgnoreCase(inntektsperiode.orgnr()))
                .max(Comparator.comparing(Inntektsperiode::tom))
                .map(Inntektsperiode::beløp)
                .orElse(0);
    }

    public List<Inntektsperiode> inntektsperioder() {
        return inntektYtelseModell.inntektskomponentModell().inntektsperioder();
    }

    public double næringsinntekt(int beregnFraOgMedÅr) {
        return hentNæringsinntekt(inntektYtelseModell.sigrunModell(), beregnFraOgMedÅr);
    }

    public LocalDate annsettelsesFomFrilans() {
        return hentAnsettelsesFomForFrilans(inntektYtelseModell.arbeidsforholdModell());
    }

    public Arbeidsgiver arbeidsgiver() {
        return arbeidsforhold().arbeidsgiver();
    }

    public Arbeidsforhold arbeidsforhold() {
        guardFlereArbeidsgivere();
        return arbeidsforholdListe().get(0);
    }

    public List<Arbeidsforhold> arbeidsforholdListe() {
        return inntektYtelseModell.arbeidsforholdModell().arbeidsforhold().stream()
                .map(a -> new Arbeidsforhold(a.arbeidsforholdId(), a.ansettelsesperiodeFom(), a.ansettelsesperiodeTom(),
                        a.arbeidsforholdstype(), a.arbeidsavtaler().get(0).stillingsprosent(),
                        new Arbeidsgiver(a.arbeidsgiverOrgnr(), this)))
                .collect(Collectors.toList());
    }

    public long søk(Søknad søknad) {
        return new Innsender(Aktoer.Rolle.SAKSBEHANDLER).sendInnSøknad(fødselsnummer, søknad);
    }

    public long søkPåPapir() {
        return new Innsender(Aktoer.Rolle.SAKSBEHANDLER).sendInnPapirsøknad(fødselsnummer(), DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
    }

    public void sendInnKlage() {
        new Innsender(Aktoer.Rolle.SAKSBEHANDLER).sendInnKlage(fødselsnummer);
    }

    private void guardFlereArbeidsgivere() {
        var antallArbeidsgivere = inntektYtelseModell.inntektskomponentModell().inntektsperioder().stream()
                .map(Inntektsperiode::orgnr)
                .distinct()
                .count();
        if (antallArbeidsgivere > 1) {
            throw new UnsupportedOperationException("Det er flere arbeidsgivere. Spesifiser hvilken arbeidsgiver du ønsker månedsinntekt fra.");
        }
    }
}
