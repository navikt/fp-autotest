package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import static no.nav.foreldrepenger.autotest.util.StreamUtils.distinctByKeys;
import static no.nav.foreldrepenger.autotest.util.testscenario.modell.Aareg.arbeidsforholdFrilans;
import static no.nav.foreldrepenger.autotest.util.testscenario.modell.Sigrun.hentNæringsinntekt;
import static no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD;
import static no.nav.vedtak.log.mdc.MDCOperations.NAV_CONSUMER_ID;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.NotSupportedException;
import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.autotest.aktoerer.innsyn.Innsyn;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.InntektYtelseModell;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;
import no.nav.vedtak.log.mdc.MDCOperations;

public abstract class Søker {

    private static final Logger LOG = LoggerFactory.getLogger(Søker.class);

    private final Fødselsnummer fødselsnummer;
    private final AktørId aktørId;
    private final AktørId aktørIdAnnenpart;
    private final InntektYtelseModell inntektYtelseModell;
    private final Innsender innsender;

    private Innsyn innsyn;
    private Saksnummer saksnummer = null;

    Søker(Fødselsnummer fødselsnummer, AktørId aktørId, AktørId aktørIdAnnenpart, InntektYtelseModell inntektYtelseModell, Innsender innsender) {
        this.fødselsnummer = fødselsnummer;
        this.aktørId = aktørId;
        this.aktørIdAnnenpart = aktørIdAnnenpart;
        this.inntektYtelseModell = inntektYtelseModell;
        this.innsender = innsender;
    }

    public Fødselsnummer fødselsnummer() {
        return fødselsnummer;
    }

    public AktørId aktørId() {
        return aktørId;
    }

    public Innsyn innsyn() {
        if (innsyn == null) {
            innsyn = new Innsyn(fødselsnummer);
        }
        return innsyn;
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
        return arbeidsgivere().toList().stream()
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
            var fnrArbeidsgiver = new Fødselsnummer(inntektYtelseModell.inntektskomponentModell().inntektsperioder().stream()
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
        return new Arbeidsgivere(arbeidsgivere().toList().stream()
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
        throw new NotSupportedException();
    }

    public Saksnummer søk(Søknad søknad) {
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn søknad for {} ...", fødselsnummer.value());
        saksnummer = innsender.sendInnSøknad(søknad, aktørId, fødselsnummer, aktørIdAnnenpart, null);
        LOG.info("Søknad sendt inn og behandling opprettet på {}", this.saksnummer.value());
        return this.saksnummer;
    }



    public Saksnummer søk(Søknad søknad, Saksnummer saksnummer) {
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn søknad for {} med saksnummer {} ...", fødselsnummer.value(), saksnummer.value());
        this.saksnummer = innsender.sendInnSøknad(søknad, aktørId, fødselsnummer, aktørIdAnnenpart, saksnummer);
        LOG.info("Søknad sendt inn og behandling opprettet på fagsak {}", saksnummer.value());
        return this.saksnummer;
    }

    public Saksnummer søk(Endringssøknad søknad) {
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn endringssøknadsøknad for {} med saksnummer {} ...", fødselsnummer.value(), søknad.getSaksnr());

        this.saksnummer = innsender.sendInnSøknad(søknad, aktørId, fødselsnummer, aktørIdAnnenpart, søknad.getSaksnr());
        LOG.info("Endringssøknad sendt inn og fagsak {} er oppdatert", søknad.getSaksnr());
        return søknad.getSaksnr();
    }

    public Saksnummer søkPapirsøknadForeldrepenger() {
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn papirsøknadd for {} ..", fødselsnummer.value());
        this.saksnummer = innsender.sendInnPapirsøknadForeldrepenger(aktørId, fødselsnummer, aktørIdAnnenpart);
        LOG.info("Papirsøknad sendt inn og behandling opprettet på {}", saksnummer.value());
        return this.saksnummer;
    }

    public Saksnummer sendInnPapirsøknadEEndringForeldrepenger() {
        guardTrengerEksisterendeBehandling();
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn endringssøknad på papirsøknadd for {} ..", fødselsnummer.value());
        this.saksnummer = innsender.sendInnPapirsøknadEEndringForeldrepenger(aktørId, fødselsnummer, aktørIdAnnenpart, saksnummer);
        LOG.info("Endringssøknad sendt inn og fagsak {} er oppdatert", saksnummer.value());
        return this.saksnummer;
    }

    public Saksnummer søkPapirsøknadEngangsstønad() {
        genererUniktNavConsumerIdForDokument();
        this.saksnummer = innsender.sendInnPapirsøknadEngangsstønad(aktørId, fødselsnummer);
        return this.saksnummer;
    }

    public void sendInnKlage() {
        guardTrengerEksisterendeBehandling();
        genererUniktNavConsumerIdForDokument();
        innsender.sendInnKlage(aktørId, fødselsnummer, this.saksnummer);
    }

    // Brukes bare i tilfelle hvor en ønsker å sende IM uten registret arbeidsforhold i Aareg!
    // TODO: Gjøres på en annen måte?
    public void sendIMBasertPåInntekskomponenten(InntektsmeldingBuilder inntektsmelding) {
        genererUniktNavConsumerIdForDokument();
        innsender.sendInnInntektsmelding(inntektsmelding, aktørId, fødselsnummer, this.saksnummer);
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

    // Fpfordel stiller krav til at Nav-ConsumerId er unik på tvers av ulike dokumenter!
    private void genererUniktNavConsumerIdForDokument() {
        MDCOperations.putToMDC(NAV_CONSUMER_ID, UUID.randomUUID().toString());
    }
}
