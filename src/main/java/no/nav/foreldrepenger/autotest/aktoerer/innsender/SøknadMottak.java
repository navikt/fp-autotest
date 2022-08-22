package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import static no.nav.foreldrepenger.autotest.util.log.LoggFormater.leggTilCallIdForFnr;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Mottakskanal.ALTINN;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Mottakskanal.SKAN_IM;

import java.time.LocalDateTime;
import java.util.List;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.MottakKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.pdl.PdlLeesahKlient;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
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

public class SøknadMottak extends DokumentVenter {

    private final MottakKlient mottakKlient;
    private final PdlLeesahKlient pdlLeesahKlient;

    public SøknadMottak() {
        mottakKlient = new MottakKlient();
        pdlLeesahKlient = new PdlLeesahKlient();
    }

    @Override
    public Saksnummer sendInnInntektsmelding(InntektsmeldingBuilder inntektsmeldingBuilder, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        // aktørId ignoreres ettersom det trengs bare i xmlen
        return sendInnInntektsmelding(List.of(inntektsmeldingBuilder), fnr, saksnummer);
    }

    @Override
    public Saksnummer sendInnInntektsmelding(List<InntektsmeldingBuilder> inntektsmeldingBuilder, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        return sendInnInntektsmelding(inntektsmeldingBuilder, fnr, saksnummer);
    }

    @Step("Sender inn IM for bruker {fnr}")
    public Saksnummer sendInnInntektsmelding(List<InntektsmeldingBuilder> inntektsmeldinger, Fødselsnummer fnr, Saksnummer saksnummer) {
        var antallGamleInntekstmeldinger = antallInntektsmeldingerMottattPåSak(saksnummer);
        journalførInnteksmeldinger(inntektsmeldinger, fnr);
        return ventTilAlleInntekstmeldingeneErMottatt(fnr, saksnummer, inntektsmeldinger.size(), antallGamleInntekstmeldinger);
    }

    private void journalførInnteksmeldinger(List<InntektsmeldingBuilder> inntektsmeldinger, Fødselsnummer fnr) {
        LOG.info("Sender inn {} IM(er) for søker {}...", inntektsmeldinger.size(), fnr.value());
        for (InntektsmeldingBuilder inntektsmelding : inntektsmeldinger) {
            var xml = inntektsmelding.createInntektesmeldingXML();
            var journalpostModell = lagJournalpost(fnr, "Inntektsmelding", xml,
                    ALTINN, null, DokumenttypeId.INNTEKTSMELDING);
            journalpostKlient.journalførR(journalpostModell);
        }
    }

    @Override
    public Saksnummer sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        return sendInnSøknad(fnr, søknad);
    }

    @Override
    public Saksnummer sendInnSøknad(Endringssøknad søknad, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        return sendInnSøknad(fnr, søknad);
    }

    @Step("[{søknad.søker.søknadsRolle}]: Sender inn søknad: {fnr}")
    private Saksnummer sendInnSøknad(Fødselsnummer fnr, Søknad søknad) {
        AllureHelper.tilJsonOgPubliserIAllureRapport(søknad);
        var skjæringsTidspunktForNyBehandling  = LocalDateTime.now();
        var antallEksistrendeFagsakerPåSøker = antallEksistrendeFagsakerPåSøker(fnr);

        if (søknad instanceof Endringssøknad endringssøknad) {
            mottakKlient.sendSøknad(fnr, endringssøknad);
        } else {
            mottakKlient.sendSøknad(fnr, søknad);
        }
        return ventTilFagsakOgBehandlingErOpprettet(fnr, skjæringsTidspunktForNyBehandling, antallEksistrendeFagsakerPåSøker);
    }

    @Override
    public Saksnummer sendInnPapirsøknadForeldrepenger(AktørId aktørId, Fødselsnummer fnr) {
        return sendInnPapirsøknad(fnr, DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL, null);
    }

    @Override
    public Saksnummer sendInnPapirsøknadEEndringForeldrepenger(AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        return sendInnPapirsøknad(fnr, DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL, saksnummer);
    }

    @Override
    public Saksnummer sendInnPapirsøknadEngangsstønad(AktørId aktørId, Fødselsnummer fnr) {
        return sendInnPapirsøknad(fnr, DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL, null);
    }

    private Saksnummer sendInnPapirsøknad(Fødselsnummer fnr, DokumenttypeId dokumenttypeId, Saksnummer saksnummer) {
        var journalpostModell = lagJournalpost(fnr, dokumenttypeId.getTermnavn(), null,
                SKAN_IM, "skanIkkeUnik.pdf", dokumenttypeId);
        if (saksnummer != null) {
            journalpostModell.setSakId(saksnummer.value());
        }

        var skjæringsTidspunktForNyBehandling  = LocalDateTime.now();
        var antallEksistrendeFagsakerPåSøker = antallEksistrendeFagsakerPåSøker(fnr);
        journalpostKlient.journalførR(journalpostModell);
        return ventTilFagsakOgBehandlingErOpprettet(fnr, skjæringsTidspunktForNyBehandling, antallEksistrendeFagsakerPåSøker);
    }

    @Override
    public void sendInnKlage(AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        sendInnKlage(fnr);
    }

    public void sendInnKlage(Fødselsnummer fnr) {
        leggTilCallIdForFnr(fnr);
        var journalpostModell = lagJournalpost(fnr, DokumenttypeId.KLAGE_DOKUMENT.getTermnavn(), null,
                SKAN_IM, null, DokumenttypeId.KLAGE_DOKUMENT);
        journalpostKlient.journalførR(journalpostModell);
    }

    private JournalpostModell lagJournalpost(Fødselsnummer fnr, String tittel, String innhold, Mottakskanal mottakskanal,
                                             String eksternReferanseId, DokumenttypeId dokumenttypeId) {
        var journalpostModell = new JournalpostModell();
        journalpostModell.setTittel(tittel);
        journalpostModell.setJournalStatus(Journalstatus.MIDLERTIDIG_JOURNALFØRT);
        journalpostModell.setMottattDato(LocalDateTime.now());
        journalpostModell.setMottakskanal(mottakskanal);
        journalpostModell.setArkivtema(Arkivtema.FOR);
        journalpostModell.setAvsenderFnr(fnr.value());
        journalpostModell.setEksternReferanseId(eksternReferanseId);
        journalpostModell.setSakId("");
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
        dokumentModell.getDokumentVariantInnholdListe().add(new DokumentVariantInnhold(
                Arkivfiltype.XML, Variantformat.ORIGINAL, innhold != null ? innhold.getBytes() : new byte[0]
        ));
        dokumentModell.getDokumentVariantInnholdListe().add(new DokumentVariantInnhold(
                Arkivfiltype.XML, Variantformat.FULLVERSJON, innhold != null ? innhold.getBytes() : new byte[0]
        ));
        dokumentModell.getDokumentVariantInnholdListe().add(new DokumentVariantInnhold(
                Arkivfiltype.PDF, Variantformat.ARKIV, new byte[0]
        ));
        return dokumentModell;
    }

    /*
     * Opretter en personhendelse
     */
    public void opprettHendelsePåKafka(PersonhendelseDto personhendelseDto) {
        pdlLeesahKlient.opprettHendelse(personhendelseDto);
    }
}
