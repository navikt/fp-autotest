package no.nav.foreldrepenger.generator.familie;

import static no.nav.foreldrepenger.autotest.util.StreamUtils.distinctByKeys;
import static no.nav.foreldrepenger.generator.familie.Aareg.arbeidsforholdFrilans;
import static no.nav.foreldrepenger.generator.familie.Sigrun.hentNæringsinntekt;
import static no.nav.foreldrepenger.generator.familie.Sigrun.startdato;
import static no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD;
import static no.nav.vedtak.klient.http.CommonHttpHeaders.HEADER_NAV_CONSUMER_ID;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.NotSupportedException;
import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.autotest.aktoerer.innsyn.Innsyn;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.DokumentType;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.SøknadDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.endringssøknad.EndringssøknadDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.endringssøknad.EndringssøknadForeldrepengerDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.ettersendelse.YtelseType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.VedleggDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.VedleggInnsendingType;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.InntektYtelseModell;
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
    private SøknadDto førstegangssøknad = null;
    private no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.SøknadDto førstegangssøknadV2 = null;

    Søker(Fødselsnummer fødselsnummer, AktørId aktørId, AktørId aktørIdAnnenpart, InntektYtelseModell inntektYtelseModell, Innsender innsender) {
        this.fødselsnummer = fødselsnummer;
        this.aktørId = aktørId;
        this.aktørIdAnnenpart = aktørIdAnnenpart;
        this.inntektYtelseModell = inntektYtelseModell;
        this.innsender = innsender;
    }

    public SøknadDto førstegangssøknad() {
        return førstegangssøknad;
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

    public Arbeidsforhold arbeidsforhold(String orgnummer) {
        return arbeidsforholdene().stream()
                .filter(arbeidsforhold -> orgnummer.equals(arbeidsforhold.arbeidsgiverIdentifikasjon().value()))
                .findFirst()
                .orElseThrow();
    }
    private List<Arbeidsforhold> arbeidsforholdene(ArbeidsgiverIdentifikator arbeidsgiverIdentifikator) {
        return Aareg.arbeidsforholdene(inntektYtelseModell.arbeidsforholdModell(), arbeidsgiverIdentifikator);
    }

    public Arbeidsgiver arbeidsgiver(String orgnummer) {
        return arbeidsgivere().toList().stream()
                .filter(a -> orgnummer.equals(a.arbeidsgiverIdentifikator().value()))
                .findFirst()
                .orElseThrow();
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

    private boolean leggTilArbeidsforhold(ArrayList<Arbeidsgiver> arbeidsgivere, no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold arbeidsforhold) {
        var arbeidsgiverIdentifikator = getArbeidsgiverIdentifikator(arbeidsforhold);
        if (arbeidsgiverIdentifikator instanceof Orgnummer orgnummer) {
            return arbeidsgivere.add(new Virksomhet(arbeidsgiverIdentifikator,
                    new Arbeidstaker(fødselsnummer, aktørId, månedsinntekt(orgnummer)),
                    arbeidsforholdene(arbeidsgiverIdentifikator),
                    innsender));
        } else if (arbeidsgiverIdentifikator instanceof AktørId) {
            var personarbeidsgiver = arbeidsforhold.personArbeidsgiver();
            var fnrArbeidsgiver = new Fødselsnummer(personarbeidsgiver.getIdent());
            var aktørIdArbeidsgiver = new AktørId(personarbeidsgiver.getAktørIdent());
            return arbeidsgivere.add(new PersonArbeidsgiver(arbeidsgiverIdentifikator,
                    new Arbeidstaker(fødselsnummer, aktørId, månedsinntekt(fnrArbeidsgiver)),
                    arbeidsforholdene(aktørIdArbeidsgiver),
                    innsender, fnrArbeidsgiver));
        }
        return false;
    }

    private ArbeidsgiverIdentifikator getArbeidsgiverIdentifikator(no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold arbeidsforhold) {
        if (arbeidsforhold.arbeidsgiverOrgnr() != null) {
            return new Orgnummer(arbeidsforhold.arbeidsgiverOrgnr());
        }
        return new AktørId(arbeidsforhold.personArbeidsgiver().getAktørIdent());

    }

    public Arbeidsgivere arbeidsgivere(String orgnummer){
        return new Arbeidsgivere(arbeidsgivere().toList().stream()
               .filter(a -> orgnummer.equals(a.arbeidsgiverIdentifikator().value()))
               .toList());
    }

    public LocalDate frilansAnnsettelsesFom() {
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

    public double næringsinntekt() {
        return hentNæringsinntekt(inntektYtelseModell.sigrunModell(), LocalDate.now().getYear() - 1);
    }

    public LocalDate næringStartdato() {
        var årstall = startdato(inntektYtelseModell.sigrunModell());
        return LocalDate.now().withYear(årstall);
    }

    public Søknad lagSøknad() {
        throw new NotSupportedException();
    }

    public Saksnummer søk(SøknadDto søknad) {
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn søknad for {} ...", fødselsnummer.value());
        this.førstegangssøknad = søknad;
        this.saksnummer = innsender.sendInnSøknad(søknad, aktørId, fødselsnummer, aktørIdAnnenpart, null);
        LOG.info("Søknad sendt inn og behandling opprettet på {}", this.saksnummer.value());
        return this.saksnummer;
    }

    public Saksnummer søk(no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.SøknadDto søknad) {
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn søknad for {} ...", fødselsnummer.value());
        this.førstegangssøknadV2 = søknad;
        this.saksnummer = innsender.sendInnSøknad(søknad, aktørId, fødselsnummer, aktørIdAnnenpart, null);
        LOG.info("Søknad sendt inn og behandling opprettet på {}", this.saksnummer.value());
        return this.saksnummer;
    }

    public Saksnummer søk(SøknadDto søknad, Saksnummer saksnummer) {
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn søknad for {} med saksnummer {} ...", fødselsnummer.value(), saksnummer.value());
        this.førstegangssøknad = søknad;
        this.saksnummer = innsender.sendInnSøknad(søknad, aktørId, fødselsnummer, aktørIdAnnenpart, saksnummer);
        LOG.info("Søknad sendt inn og behandling opprettet på fagsak {}", saksnummer.value());
        return this.saksnummer;
    }

    public Saksnummer søk(no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.SøknadDto søknad, Saksnummer saksnummer) {
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn søknad for {} med saksnummer {} ...", fødselsnummer.value(), saksnummer.value());
        this.førstegangssøknadV2 = søknad;
        this.saksnummer = innsender.sendInnSøknad(søknad, aktørId, fødselsnummer, aktørIdAnnenpart, saksnummer);
        LOG.info("Søknad sendt inn og behandling opprettet på fagsak {}", saksnummer.value());
        return this.saksnummer;
    }

    public Saksnummer søk(EndringssøknadDto søknad) {
        genererUniktNavConsumerIdForDokument();
        this.saksnummer = ((EndringssøknadForeldrepengerDto) søknad).saksnummer();
        LOG.info("Sender inn endringssøknadsøknad for {} med saksnummer {} ...", fødselsnummer.value(), this.saksnummer);
        innsender.sendInnSøknad(søknad, aktørId, fødselsnummer, aktørIdAnnenpart, saksnummer);
        LOG.info("Endringssøknad sendt inn og fagsak {} er oppdatert", saksnummer.value());
        return saksnummer;
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

    public void ettersendVedlegg(Fødselsnummer fnr, YtelseType ytelseType, DokumentType skjemanummer, VedleggDto.Dokumenterer dokumenterer) {
        LOG.info("Ettersender vedlegg {} for {} med saksnummer {} ...", skjemanummer, fnr.value(), this.saksnummer.value());
        var vedlegg = new VedleggDto(UUID.randomUUID(), skjemanummer, VedleggInnsendingType.LASTET_OPP, null, dokumenterer);
        innsender.ettersendVedlegg(fnr, saksnummer, ytelseType, vedlegg);
        LOG.info("Vedlegg er ettersendt.");
    }

    // Brukes bare i tilfelle hvor en ønsker å sende IM uten registret arbeidsforhold i Aareg!
    // TODO: Gjøres på en annen måte?
    public void sendIMBasertPåInntekskomponenten(InntektsmeldingBuilder inntektsmelding) {
        genererUniktNavConsumerIdForDokument();
        innsender.sendInnInntektsmelding(inntektsmelding.build(), aktørId, fødselsnummer, this.saksnummer);
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
        MDCOperations.putToMDC(HEADER_NAV_CONSUMER_ID, UUID.randomUUID().toString());
    }
}
