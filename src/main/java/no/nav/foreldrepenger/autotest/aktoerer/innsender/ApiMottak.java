package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Mottakskanal.SKAN_IM;
import static org.assertj.core.api.Assertions.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.InntektsmeldingKlient;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.dto.SendInntektsmeldingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.FpsoknadKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.pdl.PdlLeesahKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.navno.InntektsmeldingPortalMapper;
import no.nav.foreldrepenger.soknad.kontrakt.EndringssøknadForeldrepengerDto;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.soknad.kontrakt.SøknadDto;
import no.nav.foreldrepenger.soknad.kontrakt.ettersendelse.YtelseType;
import no.nav.foreldrepenger.soknad.kontrakt.vedlegg.VedleggDto;
import no.nav.foreldrepenger.vtp.kontrakter.PersonhendelseDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.DokumentModell;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.DokumentVariantInnhold;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.JournalpostBruker;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.JournalpostModell;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Arkivfiltype;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Arkivtema;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.BrukerType;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumentTilknyttetJournalpost;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Journalposttyper;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Journalstatus;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Mottakskanal;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Variantformat;

public class ApiMottak extends DokumentInnsendingHjelper {

    private final FpsoknadKlient fpsoknadKlient;
    private final PdlLeesahKlient pdlLeesahKlient;

    public ApiMottak(SaksbehandlerRolle saksbehandlerRolle) {
        super(saksbehandlerRolle);
        fpsoknadKlient = new FpsoknadKlient();
        pdlLeesahKlient = new PdlLeesahKlient();
    }

    @Override
    public Saksnummer sendInnInntektsmelding(Inntektsmelding inntektsmelding,
                                             AktørId aktørId,
                                             Fødselsnummer fnr,
                                             Saksnummer saksnummer) {
        var forespørsler = InntektsmeldingKlient.hentInntektsmeldingForespørslerFor(saksnummer);
        if (forespørsler == null || forespørsler.inntektmeldingForespørsler() == null || forespørsler.inntektmeldingForespørsler()
                .isEmpty()) {
            fail("Forventer å motta enn eller flere forespørsel for %s", saksnummer);
            return null;
        }
        var forespørslerFiltrert = forespørsler.inntektmeldingForespørsler()
                .stream()
                .filter(forespørselDto -> forespørselDto.arbeidsgiverident()
                        .ident()
                        .equals(inntektsmelding.arbeidsgiver().arbeidsgiverIdentifikator()))
                .findFirst();

        if (forespørslerFiltrert.isEmpty()) {
            fail("Forventer å finne enn forespørsel for AG: %s på sak: %s", inntektsmelding.arbeidsgiver().arbeidsgiverIdentifikator(),
                    saksnummer);
        }
        var request = InntektsmeldingPortalMapper.map(inntektsmelding, forespørslerFiltrert.orElseThrow());
        return sendInnInntektsmelding(request, fnr, saksnummer);
    }

    @Override
    public Saksnummer sendInnInntektsmelding(List<Inntektsmelding> inntektsmeldinger,
                                             AktørId aktørId,
                                             Fødselsnummer fnr,
                                             Saksnummer saksnummer) {
        if (inntektsmeldinger == null || inntektsmeldinger.isEmpty()) {
            fail("Forventer å motta en inntektsmelding for %s", saksnummer);
            return null;
        } else if (inntektsmeldinger.size() > 1) {
            fail("Forventer at det sendes kun 1 inntektsmelding per arbeidsgiver for %s", saksnummer);
            return null;
        }
        LOG.debug("Sender inn inntektsmelding for sak {} og arbeidsgiver {}", saksnummer, inntektsmeldinger.getFirst().arbeidsgiver().arbeidsgiverIdentifikator());
        return sendInnInntektsmelding(inntektsmeldinger.getFirst(), aktørId, fnr, saksnummer);
    }

    @Override
    public Saksnummer sendInnInntektsmeldingUtenForespørsel(Inntektsmelding inntektsmelding,
                                                        LocalDate startDato,
                                                        AktørId aktørId,
                                                        Fødselsnummer fødselsnummer,
                                                        Saksnummer saksnummer,
                                                        boolean registrertIAareg) {
        var request = InntektsmeldingPortalMapper.mapUtenForespørsel(inntektsmelding, aktørId, startDato, registrertIAareg);
        return sendInnInntektsmelding(request, fødselsnummer, saksnummer);
    }



    @Step("Sender inn IM for bruker {fnr}")
    private Saksnummer sendInnInntektsmelding(SendInntektsmeldingDto request, Fødselsnummer fnr, Saksnummer saksnummer) {
        var antallGamleInntekstmeldinger = antallInntektsmeldingerMottattPåSak(saksnummer);
        InntektsmeldingKlient.sendInntektsmelding(request, fnr);
        return ventTilAlleInntekstmeldingeneErMottatt(fnr, saksnummer, 1, antallGamleInntekstmeldinger);
    }

    @Override
    public Saksnummer sendInnSøknad(SøknadDto søknad,
                                    AktørId aktørId,
                                    Fødselsnummer fnr,
                                    AktørId aktørIdAnnenpart,
                                    Saksnummer saksnummer) {
        return sendInnSøknad(fnr, søknad);
    }

    @Override
    public Saksnummer sendInnSøknad(EndringssøknadForeldrepengerDto søknad,
                                    AktørId aktørId,
                                    Fødselsnummer fnr,
                                    AktørId aktørIdAnnenpart,
                                    Saksnummer saksnummer) {
        return sendInnSøknad(fnr, søknad);
    }

    @Step("[Sender inn søknad: {fnr}")
    private Saksnummer sendInnSøknad(Fødselsnummer fnr, SøknadDto søknad) {
        AllureHelper.tilJsonOgPubliserIAllureRapport(søknad);
        var skjæringsTidspunktForNyBehandling = LocalDateTime.now();
        var antallEksistrendeFagsakerPåSøker = antallEksistrendeFagsakerPåSøker(fnr);
        fpsoknadKlient.mellomlagreVedlegg(fnr, søknad);
        fpsoknadKlient.sendSøknad(fnr, søknad);
        return ventTilFagsakOgBehandlingErOpprettet(fnr, skjæringsTidspunktForNyBehandling, antallEksistrendeFagsakerPåSøker);
    }

    @Step("[Sender inn endrignssøknad: {fnr}")
    private Saksnummer sendInnSøknad(Fødselsnummer fnr, EndringssøknadForeldrepengerDto søknad) {
        AllureHelper.tilJsonOgPubliserIAllureRapport(søknad);
        var skjæringsTidspunktForNyBehandling = LocalDateTime.now();
        var antallEksistrendeFagsakerPåSøker = antallEksistrendeFagsakerPåSøker(fnr);
        fpsoknadKlient.mellomlagreVedlegg(fnr, søknad);
        fpsoknadKlient.sendSøknad(fnr, søknad);
        return ventTilFagsakOgBehandlingErOpprettet(fnr, skjæringsTidspunktForNyBehandling, antallEksistrendeFagsakerPåSøker);
    }

    @Override
    public Saksnummer sendInnPapirsøknadForeldrepenger(AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart) {
        return sendInnPapirsøknad(fnr, DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL, null);
    }

    @Override
    public Saksnummer sendInnPapirsøknadSvangerskapspenger(AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart) {
        return sendInnPapirsøknad(fnr, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER, null);
    }

    @Override
    public Saksnummer sendInnPapirsøknadEEndringForeldrepenger(AktørId aktørId,
                                                               Fødselsnummer fnr,
                                                               AktørId aktørIdAnnenpart,
                                                               Saksnummer saksnummer) {
        return sendInnPapirsøknad(fnr, DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL, saksnummer);
    }

    @Override
    public Saksnummer sendInnPapirsøknadEngangsstønad(AktørId aktørId, Fødselsnummer fnr) {
        return sendInnPapirsøknad(fnr, DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL, null);
    }

    private Saksnummer sendInnPapirsøknad(Fødselsnummer fnr, DokumenttypeId dokumenttypeId, Saksnummer saksnummer) {
        var journalpostModell = lagJournalpost(fnr, dokumenttypeId.getTermnavn(), null, SKAN_IM, "skanIkkeUnik.pdf", dokumenttypeId);
        if (saksnummer != null) {
            journalpostModell.setSakId(saksnummer.value());
        }

        var skjæringsTidspunktForNyBehandling = LocalDateTime.now();
        var antallEksistrendeFagsakerPåSøker = antallEksistrendeFagsakerPåSøker(fnr);
        journalpostKlient.journalførR(journalpostModell);
        return ventTilFagsakOgBehandlingErOpprettet(fnr, skjæringsTidspunktForNyBehandling, antallEksistrendeFagsakerPåSøker);
    }

    @Override
    public void sendInnKlage(AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        sendInnKlage(fnr);
    }

    public void ettersendVedlegg(Fødselsnummer fnr, Saksnummer saksnummer, YtelseType ytelseType, VedleggDto vedlegg) {
        var vedleggMottattFraFørPåSak = antallEttersendelserMottattPåSak(saksnummer);
        fpsoknadKlient.mellomlagreVedlegg(fnr, ytelseType, List.of(vedlegg));
        fpsoknadKlient.ettersendVedlegg(fnr, saksnummer, List.of(vedlegg));
        ventTilVedleggErMottatt(saksnummer, vedleggMottattFraFørPåSak);
    }

    public void sendInnKlage(Fødselsnummer fnr) {
        var journalpostModell = lagJournalpost(fnr, DokumenttypeId.KLAGE_DOKUMENT.getTermnavn(), null, SKAN_IM, null,
                DokumenttypeId.KLAGE_DOKUMENT);
        journalpostKlient.journalførR(journalpostModell);
    }

    private JournalpostModell lagJournalpost(Fødselsnummer fnr,
                                             String tittel,
                                             String innhold,
                                             Mottakskanal mottakskanal,
                                             String eksternReferanseId,
                                             DokumenttypeId dokumenttypeId) {
        var journalpostModell = new JournalpostModell();
        journalpostModell.setTittel(tittel);
        journalpostModell.setJournalStatus(Journalstatus.MOTTATT);
        journalpostModell.setMottattDato(LocalDateTime.now());
        journalpostModell.setMottakskanal(mottakskanal);
        journalpostModell.setArkivtema(Arkivtema.FOR);
        journalpostModell.setAvsenderFnr(fnr.value());
        journalpostModell.setEksternReferanseId(eksternReferanseId);
        journalpostModell.setBruker(new JournalpostBruker(fnr.value(), BrukerType.FNR));
        journalpostModell.setJournalposttype(Journalposttyper.INNGAAENDE_DOKUMENT);
        journalpostModell.getDokumentModellList().add(lagDokumentModell(innhold, dokumenttypeId));
        return journalpostModell;
    }

    private DokumentModell lagDokumentModell(String innhold, DokumenttypeId dokumenttypeId) {
        var dokumentModell = new DokumentModell();
        dokumentModell.setInnhold(innhold);
        dokumentModell.setDokumentType(dokumenttypeId);
        dokumentModell.setDokumentTilknyttetJournalpost(DokumentTilknyttetJournalpost.HOVEDDOKUMENT);
        dokumentModell.getDokumentVariantInnholdListe()
                .add(new DokumentVariantInnhold(Arkivfiltype.XML, Variantformat.ORIGINAL,
                        innhold != null ? innhold.getBytes() : new byte[0]));
        dokumentModell.getDokumentVariantInnholdListe()
                .add(new DokumentVariantInnhold(Arkivfiltype.XML, Variantformat.FULLVERSJON,
                        innhold != null ? innhold.getBytes() : new byte[0]));
        dokumentModell.getDokumentVariantInnholdListe()
                .add(new DokumentVariantInnhold(Arkivfiltype.PDF, Variantformat.ARKIV, new byte[0]));
        return dokumentModell;
    }

    /*
     * Opretter en personhendelse
     */
    @Override
    public void sendInnHendelse(PersonhendelseDto personhendelseDto) {
        pdlLeesahKlient.opprettHendelse(personhendelseDto);
    }
}
