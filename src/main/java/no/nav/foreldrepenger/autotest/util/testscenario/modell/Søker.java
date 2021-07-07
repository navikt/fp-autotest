package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import static no.nav.foreldrepenger.autotest.util.testscenario.modell.Aareg.arbeidsforholdFrilans;
import static no.nav.foreldrepenger.autotest.util.testscenario.modell.Sigrun.hentNæringsinntekt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.autotest.søknad.builder.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadForeldrepengerErketyper;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.autotest.søknad.modell.Søknad;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.InntektYtelseModell;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;

public abstract class Søker {

    private final Fødselsnummer fødselsnummer;
    private final BrukerRolle brukerRolle;
    private final Relasjoner relasjoner;
    private final InntektYtelseModell inntektYtelseModell;

    Søker(Fødselsnummer fødselsnummer, BrukerRolle brukerRolle, Relasjoner relasjoner, InntektYtelseModell inntektYtelseModell) {
        this.fødselsnummer = fødselsnummer;
        this.brukerRolle = brukerRolle;
        this.relasjoner = relasjoner;
        this.inntektYtelseModell = inntektYtelseModell;
    }

    public Fødselsnummer fødselsnummer() {
        return fødselsnummer;
    }

    public Arbeidsforhold arbeidsforhold() {
        guardFlereArbeidsgivere();
        return arbeidsforholdene().get(0);
    }

    public List<Arbeidsforhold> arbeidsforholdene() {
        return Aareg.arbeidsforholdene(inntektYtelseModell.arbeidsforholdModell());
    }

    private List<Arbeidsforhold> arbeidsforholdene(Orgnummer orgnummer) {
        return Aareg.arbeidsforholdene(inntektYtelseModell.arbeidsforholdModell(), orgnummer);
    }

    public Arbeidsgiver arbeidsgiver() {
        guardFlereArbeidsgivere();
        return arbeidsgivere().getArbeidsgivere().stream()
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Ingen arbeidsgivere funnet for søker"));
    }

    public Arbeidsgivere arbeidsgivere(){
        var arbeidsgivere = new ArrayList<Arbeidsgiver>();
        inntektYtelseModell.arbeidsforholdModell().arbeidsforhold().stream()
                .map(a -> new Orgnummer(a.arbeidsgiverOrgnr()))
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .forEach(orgnr -> arbeidsgivere.add(new Arbeidsgiver(orgnr,
                        new Arbeidstaker(fødselsnummer, månedsinntekt(orgnr)), arbeidsforholdene(orgnr))));
        return new Arbeidsgivere(arbeidsgivere);
    }

    public Arbeidsgivere arbeidsgivere(Orgnummer orgnummer){
        return new Arbeidsgivere(arbeidsgivere().getArbeidsgivere().stream()
               .filter(a -> orgnummer.equals(a.orgnummer()))
               .collect(Collectors.toList()));
    }

    public LocalDate FrilansAnnsettelsesFom() {
        return arbeidsforholdFrilans(inntektYtelseModell.arbeidsforholdModell()).get(0)
                .ansettelsesperiodeFom();
    }

    public int månedsinntekt() {
        guardFlereArbeidsgivere();
        return Inntektskomponenten.månedsinntekt(inntektYtelseModell.inntektskomponentModell());
    }

    public int månedsinntekt(Orgnummer orgnummer) {
        return Inntektskomponenten.månedsinntekt(inntektYtelseModell.inntektskomponentModell(), orgnummer);
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
                .filter(p -> orgnummer.equals(p.orgnummer()))
                .filter(p -> arbeidsforholdId.equals(p.arbeidsforholdId()))
                .map(Arbeidsforhold::stillingsprosent)
                .findFirst()
                .orElse(0);
    }

    public double næringsinntekt(int beregnFraOgMedÅr) {
        return hentNæringsinntekt(inntektYtelseModell.sigrunModell(), beregnFraOgMedÅr);
    }

    public ForeldrepengerBuilder lagSøknad(SøknadYtelse ytelse, SøknadHendelse hendelse, LocalDate familiehendelse) {

        if(ytelse.equals(SøknadYtelse.FORELDREPENGER)) {
            return switch (hendelse) {
                case FØDSEL -> SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel(familiehendelse, brukerRolle)
                        .medAnnenForelder(new NorskForelder(relasjoner.annenforeldre().relatertPersonsIdent(), ""));
                case ADOPSJON -> SøknadForeldrepengerErketyper.lagSøknadForeldrepengerAdopsjon(familiehendelse, brukerRolle, false)
                        .medAnnenForelder(new NorskForelder(relasjoner.annenforeldre().relatertPersonsIdent(), ""));
                case TERMIN -> SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin(familiehendelse, brukerRolle)
                        .medAnnenForelder(new NorskForelder(relasjoner.annenforeldre().relatertPersonsIdent(), ""));
            };
        }
        return null;
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
