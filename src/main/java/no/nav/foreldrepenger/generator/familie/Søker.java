package no.nav.foreldrepenger.generator.familie;

import static no.nav.foreldrepenger.autotest.util.StreamUtils.distinctByKeys;
import static no.nav.foreldrepenger.generator.familie.Aareg.arbeidsforholdFrilans;
import static no.nav.foreldrepenger.generator.familie.Sigrun.hentNæringsinntekt;
import static no.nav.foreldrepenger.generator.familie.Sigrun.startdato;
import static no.nav.foreldrepenger.vtp.kontrakter.person.Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD;
import static no.nav.vedtak.klient.http.CommonHttpHeaders.HEADER_NAV_CONSUMER_ID;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.autotest.aktoerer.innsyn.Innsyn;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.soknad.kontrakt.SøkerDto;
import no.nav.foreldrepenger.soknad.kontrakt.SøknadDto;
import no.nav.foreldrepenger.soknad.kontrakt.builder.EndringssøknadBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.builder.SøknadBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.ettersendelse.YtelseType;
import no.nav.foreldrepenger.soknad.kontrakt.vedlegg.DokumentTypeId;
import no.nav.foreldrepenger.soknad.kontrakt.vedlegg.Dokumenterer;
import no.nav.foreldrepenger.soknad.kontrakt.vedlegg.InnsendingType;
import no.nav.foreldrepenger.soknad.kontrakt.vedlegg.VedleggDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.AaregDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.InntektYtelseModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.OrganisasjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.PersonDto;
import no.nav.vedtak.log.mdc.MDCOperations;

public abstract class Søker {

    private static final Logger LOG = LoggerFactory.getLogger(Søker.class);

    private final Ident ident;
    private final Ident identAnnenpart;
    private final PersonDto personDto;
    private final Innsender innsender;

    private Innsyn innsyn;
    private Saksnummer saksnummer = null;
    private SøknadDto førstegangssøknad = null;

    Søker(Ident ident, Ident identAnnenpart, PersonDto personDto, Innsender innsender) {
        this.ident = ident;
        this.identAnnenpart = identAnnenpart;
        this.personDto = personDto;
        this.innsender = innsender;
    }

    public SøknadDto førstegangssøknad() {
        return førstegangssøknad;
    }

    public Fødselsnummer fødselsnummer() {
        return ident.fødselsnummer();
    }

    public AktørId aktørId() {
        return ident.aktørId();
    }

    public Ident ident() {
        return ident;
    }

    public Innsyn innsyn() {
        if (innsyn == null) {
            innsyn = new Innsyn(ident.fødselsnummer());
        }
        return innsyn;
    }

    public Arbeidsforhold arbeidsforhold() {
        guardFlereArbeidsgivere();
        return arbeidsforholdene().getFirst();
    }

    public List<Arbeidsforhold> arbeidsforholdene() {
        if (personDto.inntektytelse() == null) {
            return List.of();
        }
        return Aareg.arbeidsforholdene(personDto.inntektytelse().aareg());
    }

    public Arbeidsforhold arbeidsforhold(String orgnummer) {
        return arbeidsforholdene().stream()
                .filter(arbeidsforhold -> orgnummer.equals(arbeidsforhold.arbeidsgiverIdentifikasjon()))
                .findFirst()
                .orElseThrow();
    }

    private List<Arbeidsforhold> arbeidsforholdene(AktørId identifikator) {
        return Aareg.arbeidsforholdene(personDto.inntektytelse().aareg(), identifikator);
    }

    private List<Arbeidsforhold> arbeidsforholdene(Orgnummer identifikator) {
        return Aareg.arbeidsforholdene(personDto.inntektytelse().aareg(), identifikator);
    }

    public Arbeidsgiver arbeidsgiver(String orgnummer) {
        return arbeidsgivere().toList().stream()
                .filter(a -> orgnummer.equals(a.arbeidsgiverIdentifikator()))
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
        arbeidsforholdene().stream()
                .filter(a -> ORDINÆRT_ARBEIDSFORHOLD.equals(a.arbeidsforholdstype()))
                .filter(distinctByKeys(Arbeidsforhold::arbeidsgiverIdentifikasjon))
                .forEach(a -> leggTilArbeidsforhold(arbeidsgivere, a));

        return new Arbeidsgivere(arbeidsgivere);
    }

    private boolean leggTilArbeidsforhold(ArrayList<Arbeidsgiver> arbeidsgivere, Arbeidsforhold arbeidsforhold) {
        if (arbeidsforhold.arbeidsgiverIdentifikasjon().length() == 9) {
            var orgnummer = new Orgnummer(arbeidsforhold.arbeidsgiverIdentifikasjon());
            return arbeidsgivere.add(new Virksomhet(orgnummer,
                    new Arbeidstaker(ident, månedsinntekt(orgnummer)),
                    arbeidsforholdene(orgnummer),
                    innsender));
        }
        var identArbeidsgiver = Ident.fra(arbeidsforhold.arbeidsgiverIdentifikasjon());
        return arbeidsgivere.add(new PersonArbeidsgiver(identArbeidsgiver,
                new Arbeidstaker(ident, månedsinntekt(identArbeidsgiver.fødselsnummer())),
                arbeidsforholdene(identArbeidsgiver.aktørId()),
                innsender));
    }

    public Arbeidsgivere arbeidsgivere(String orgnummer){
        return new Arbeidsgivere(arbeidsgivere().toList().stream()
               .filter(a -> orgnummer.equals(a.arbeidsgiverIdentifikator()))
               .toList());
    }

    public LocalDate frilansAnnsettelsesFom() {
        return arbeidsforholdFrilans(personDto.inntektytelse().aareg()).getFirst()
                .ansettelsesperiodeFom();
    }

    public int månedsinntekt() {
        guardFlereArbeidsgivere();
        return Inntektskomponenten.månedsinntekt(personDto.inntektytelse().inntektskomponent());
    }

    public int månedsinntekt(String identifikator) {
        return Inntektskomponenten.månedsinntekt(personDto.inntektytelse().inntektskomponent(), identifikator);
    }

    public int månedsinntekt(Orgnummer orgnummer) {
        return Inntektskomponenten.månedsinntekt(personDto.inntektytelse().inntektskomponent(), orgnummer);
    }

    public int månedsinntekt(Fødselsnummer fnrArbeidsgiver) {
        return Inntektskomponenten.månedsinntekt(personDto.inntektytelse().inntektskomponent(), fnrArbeidsgiver);
    }

    public int månedsinntekt(Orgnummer orgnummer, ArbeidsforholdId arbeidsforholdId) {
        var stillingsproentAF = stillingsprosent(orgnummer, arbeidsforholdId);
        var stillingsprosentSamlet = arbeidsforholdene().stream()
                .map(Arbeidsforhold::stillingsprosent)
                .mapToInt(Integer::intValue).sum();

        return månedsinntekt(orgnummer.value()) * stillingsproentAF / stillingsprosentSamlet;
    }

    public Integer stillingsprosent(Orgnummer orgnummer, ArbeidsforholdId arbeidsforholdId) {
        return arbeidsforholdene().stream()
                .filter(p -> orgnummer.value().equals(p.arbeidsgiverIdentifikasjon()))
                .filter(p -> arbeidsforholdId.equals(p.arbeidsforholdId()))
                .map(Arbeidsforhold::stillingsprosent)
                .findFirst()
                .orElse(0);
    }

    public double næringsinntekt() {
        return hentNæringsinntekt(personDto.inntektytelse().sigrun(), LocalDate.now().getYear() - 1);
    }

    public LocalDate næringStartdato() {
        var årstall = startdato(personDto.inntektytelse().sigrun());
        return LocalDate.now().withYear(årstall);
    }

    public Saksnummer søk(SøknadBuilder søknadBuilder) {
        var søknad = søknadBuilder
                .medSøkerinfo(new SøkerDto(ident.fødselsnummer(), new SøkerDto.Navn("Fornavnet", "Mellomnavnet", "Etternavnet hardkodet"), registrerteArbeidsforhold()))
                .build();
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn søknad for {} ...", ident);
        this.førstegangssøknad = søknad;
        this.saksnummer = innsender.sendInnSøknad(søknad, ident.aktørId(), ident.fødselsnummer(),
                identAnnenpart != null ? identAnnenpart.aktørId() : null, null);
        LOG.debug("Søknad sendt inn og behandling opprettet på {}", this.saksnummer.value());
        return this.saksnummer;
    }

    public Saksnummer søk(SøknadBuilder søknadBuilder, Saksnummer saksnummer) {
        var søknad = søknadBuilder
                .medSøkerinfo(new SøkerDto(ident.fødselsnummer(), new SøkerDto.Navn("Fornavnet", "Mellomnavnet", "Etternavnet hardkodet"), registrerteArbeidsforhold()))
                .build();
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn søknad for {} med saksnummer {} ...", ident, saksnummer.value());
        this.førstegangssøknad = søknad;
        this.saksnummer = innsender.sendInnSøknad(søknad, ident.aktørId(), ident.fødselsnummer(),
                identAnnenpart != null ? identAnnenpart.aktørId() : null, saksnummer);
        LOG.debug("Søknad sendt inn og behandling opprettet på fagsak {}", saksnummer.value());
        return this.saksnummer;
    }

    public Saksnummer søk(EndringssøknadBuilder søknadBuilder) {
        var søknad = søknadBuilder
                .medSøkerinfo(new SøkerDto(ident.fødselsnummer(), new SøkerDto.Navn("Fornavnet", "Mellomnavnet", "Etternavnet hardkodet"), registrerteArbeidsforhold()))
                .build();
        genererUniktNavConsumerIdForDokument();
        this.saksnummer = søknad.saksnummer();
        LOG.info("Sender inn endringssøknadsøknad for {} med saksnummer {} ...", ident, this.saksnummer.value());
        innsender.sendInnSøknad(søknad, ident.aktørId(), ident.fødselsnummer(),
                identAnnenpart != null ? identAnnenpart.aktørId() : null, saksnummer);
        LOG.debug("Endringssøknad sendt inn og fagsak {} er oppdatert", saksnummer.value());
        return saksnummer;
    }

    public Saksnummer søkPapirsøknadForeldrepenger() {
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn papirsøknad for {} ..", ident);
        this.saksnummer = innsender.sendInnPapirsøknadForeldrepenger(ident.aktørId(), ident.fødselsnummer(),
                identAnnenpart != null ? identAnnenpart.aktørId() : null);
        LOG.debug("Papirsøknad sendt inn og behandling opprettet på {}", saksnummer.value());
        return this.saksnummer;
    }

    public Saksnummer søkPapirsøknadSvangerskapspenger() {
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn papirsøknad for {} ..", ident);
        this.saksnummer = innsender.sendInnPapirsøknadSvangerskapspenger(ident.aktørId(), ident.fødselsnummer(),
                identAnnenpart != null ? identAnnenpart.aktørId() : null);
        LOG.debug("Papirsøknad sendt inn og behandling opprettet på {}", saksnummer.value());
        return this.saksnummer;
    }

    public Saksnummer sendInnPapirsøknadEEndringForeldrepenger() {
        guardTrengerEksisterendeBehandling();
        genererUniktNavConsumerIdForDokument();
        LOG.info("Sender inn endringssøknad på papirsøknad for {} ..", ident);
        this.saksnummer = innsender.sendInnPapirsøknadEEndringForeldrepenger(ident.aktørId(), ident.fødselsnummer(),
                identAnnenpart != null ? identAnnenpart.aktørId() : null, saksnummer);
        LOG.debug("Endringssøknad sendt inn og fagsak {} er oppdatert", saksnummer.value());
        return this.saksnummer;
    }

    public Saksnummer søkPapirsøknadEngangsstønad() {
        genererUniktNavConsumerIdForDokument();
        this.saksnummer = innsender.sendInnPapirsøknadEngangsstønad(ident.aktørId(), ident.fødselsnummer());
        return this.saksnummer;
    }

    public void sendInnKlage() {
        guardTrengerEksisterendeBehandling();
        genererUniktNavConsumerIdForDokument();
        innsender.sendInnKlage(ident.aktørId(), ident.fødselsnummer(), this.saksnummer);
    }

    public void ettersendVedlegg(Fødselsnummer fnr, YtelseType ytelseType, DokumentTypeId skjemanummer, Dokumenterer dokumenterer) {
        LOG.info("Ettersender vedlegg {} for {} med saksnummer {} ...", skjemanummer, fnr.value(), this.saksnummer.value());
        var vedlegg = new VedleggDto(UUID.randomUUID(), skjemanummer, InnsendingType.LASTET_OPP, null, dokumenterer);
        innsender.ettersendVedlegg(fnr, saksnummer, ytelseType, vedlegg);
        LOG.debug("Vedlegg er ettersendt.");
    }

    // Brukes bare i tilfelle hvor en ønsker å sende IM uten registret arbeidsforhold i Aareg!
    public void sendIMBasertPåInntekskomponenten(InntektsmeldingBuilder inntektsmelding, LocalDate startdato) {
        genererUniktNavConsumerIdForDokument();
        innsender.sendInnInntektsmeldingUtenForespørsel(inntektsmelding.build(), startdato, ident.aktørId(), ident.fødselsnummer(), this.saksnummer, false);
    }

    private void guardFlereArbeidsgivere() {
        var antallOrdinæreArbeidsforhold = Optional.ofNullable(personDto.inntektytelse())
                .map(InntektYtelseModellDto::aareg).map(AaregDto::arbeidsforhold).orElseGet(List::of).stream()
                .filter(a -> a.arbeidsforholdstype().equals(ORDINÆRT_ARBEIDSFORHOLD))
                .filter(a -> a.arbeidsgiver() instanceof OrganisasjonDto)
                .map(a -> ((OrganisasjonDto) a.arbeidsgiver()).orgnummer())
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

    private List<SøkerDto.Arbeidsforhold> registrerteArbeidsforhold() {
        return arbeidsforholdene().stream()
                .map(Søker::tilArbeidsforhold)
                .toList();
    }

    private static SøkerDto.Arbeidsforhold tilArbeidsforhold(Arbeidsforhold af) {
        var hardkodetNavn = "ARBEIDSGIVERS NAVN AS";
        return new SøkerDto.Arbeidsforhold(
                hardkodetNavn,
                new Orgnummer(af.arbeidsgiverIdentifikasjon()),
                (double) af.stillingsprosent(),
                af.ansettelsesperiodeFom(),
                af.ansettelsesperiodeTom());
    }
}
