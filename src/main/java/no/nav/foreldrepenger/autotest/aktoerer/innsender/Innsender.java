package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import static no.nav.foreldrepenger.autotest.util.log.LoggFormater.leggTilCallIdForFnr;
import static no.nav.foreldrepenger.autotest.util.log.LoggFormater.leggTilCallIdforSaksnummer;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingerJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.MottakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.JournalforingJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.oauth2.AzureAdJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.pdl.PdlLeesahJerseyKlient;
import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.autotest.søknad.modell.Søknad;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
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
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Variantformat;

public class Innsender extends Aktoer {

    private final FagsakJerseyKlient fagsakKlient;
    private final BehandlingerJerseyKlient behandlingerKlient;
    private final HistorikkJerseyKlient historikkKlient;

    private final MottakJerseyKlient mottakKlient;

    private final AzureAdJerseyKlient oauth2Klient;
    private final JournalforingJerseyKlient journalpostKlient;
    private final PdlLeesahJerseyKlient pdlLeesahKlient;

    public Innsender(Rolle rolle) {
        super(rolle);
        fagsakKlient = new FagsakJerseyKlient(cookieRequestFilter);
        behandlingerKlient = new BehandlingerJerseyKlient(cookieRequestFilter);
        historikkKlient = new HistorikkJerseyKlient(cookieRequestFilter);

        mottakKlient = new MottakJerseyKlient();

        oauth2Klient = new AzureAdJerseyKlient();
        journalpostKlient = new JournalforingJerseyKlient();
        pdlLeesahKlient = new PdlLeesahJerseyKlient();
    }

    public void sendInnInnteksmeldingFpfordel(Fødselsnummer fnr, Long saksnummer, InntektsmeldingBuilder... inntektsmelding) {
        sendInnInnteksmeldingFpfordel(List.of(inntektsmelding), fnr, saksnummer);
    }

    @Step("Sender inn IM for bruker {fnr}")
    public void sendInnInnteksmeldingFpfordel(List<InntektsmeldingBuilder> inntektsmeldinger, Fødselsnummer fnr, Long saksnummer) {
        var antallGamleInntekstmeldinger = hentAntallHistorikkInnslagAvTypenVedleggMottatt(saksnummer);
        journalførInnteksmeldinger(inntektsmeldinger, fnr);
        ventTilInntekstmeldingErMottatt(fnr, saksnummer, inntektsmeldinger.size(), antallGamleInntekstmeldinger);
    }

    private void journalførInnteksmeldinger(List<InntektsmeldingBuilder> inntektsmeldinger, Fødselsnummer fnr) {
        for (InntektsmeldingBuilder inntektsmelding : inntektsmeldinger) {
            LOG.info("Sender inn IM for søker: {}", fnr);
            var xml = inntektsmelding.createInntektesmeldingXML();
            var journalpostModell = lagJournalpost(fnr, "Inntektsmelding", xml,
                    "ALTINN", null, DokumenttypeId.INNTEKTSMELDING);
            journalpostKlient.journalførR(journalpostModell);
        }
    }

    @Step("[{søknad.søker.søknadsRolle}]: Sender inn søknad: {fnr}")
    public Long sendInnSøknad(Fødselsnummer fnr, Søknad søknad) {
        var callId = leggTilCallIdForFnr(fnr);
        LOG.info("Sender inn søknadd for bruker {}", fnr);
        var token = oauth2Klient.hentAccessTokenForBruker(fnr);
        AllureHelper.tilJsonOgPubliserIAllureRapport(søknad);
        var kvittering = mottakKlient.sendSøknad(token, søknad);
        assertTrue(kvittering.erVellykket(), "Innsending av søknad til fpsoknad-mottak feilet!");
        var saksnummer = ventTilFagsakOgBehandlingErOpprettet(fnr);
        leggTilCallIdforSaksnummer(callId, saksnummer);
        LOG.info("Innsending av søknad er vellykket!");
        return saksnummer;
    }

    public Long sendInnPapirsøknad(Fødselsnummer fnr, DokumenttypeId dokumenttypeId) {
        var callId = leggTilCallIdForFnr(fnr);
        LOG.info("Sender inn papirsøknadd for bruker {}", fnr);
        var journalpostModell = lagJournalpost(fnr, dokumenttypeId.getTermnavn(), null,
                "SKAN_IM", "skanIkkeUnik.pdf", dokumenttypeId);
        journalpostKlient.journalførR(journalpostModell);
        var saksnummer = ventTilFagsakOgBehandlingErOpprettet(fnr);
        LOG.info("Innsending av papirsøknad er vellykket!");
        leggTilCallIdforSaksnummer(callId, saksnummer);
        return saksnummer;
    }

    public void sendInnKlage(Fødselsnummer fnr) {
        leggTilCallIdForFnr(fnr);
        var journalpostModell = lagJournalpost(fnr, DokumenttypeId.KLAGE_DOKUMENT.getTermnavn(), null,
                "SKAN_IM", null, DokumenttypeId.KLAGE_DOKUMENT);
        journalpostKlient.journalførR(journalpostModell);
    }

    private JournalpostModell lagJournalpost(Fødselsnummer fnr, String tittel, String innhold, String mottakskanal,
                                             String eksternReferanseId, DokumenttypeId dokumenttypeId) {
        JournalpostModell journalpostModell = new JournalpostModell();
        journalpostModell.setTittel(tittel);
        journalpostModell.setJournalStatus(Journalstatus.MIDLERTIDIG_JOURNALFØRT);
        journalpostModell.setMottattDato(LocalDateTime.now());
        journalpostModell.setMottakskanal(mottakskanal);
        journalpostModell.setArkivtema(Arkivtema.FOR);
        journalpostModell.setAvsenderFnr(fnr.toString());
        journalpostModell.setEksternReferanseId(eksternReferanseId);
        journalpostModell.setSakId("");
        journalpostModell.setBruker(new JournalpostBruker(fnr.toString(), BrukerType.FNR));
        journalpostModell.setJournalposttype(Journalposttyper.INNGAAENDE_DOKUMENT);
        journalpostModell.getDokumentModellList().add(lagDokumentModell(innhold, dokumenttypeId));
        return journalpostModell;
    }

    private DokumentModell lagDokumentModell(String innhold, DokumenttypeId dokumenttypeId) {
        DokumentModell dokumentModell = new DokumentModell();
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

    private Integer hentAntallHistorikkInnslagAvTypenVedleggMottatt(Long saksnummer) {
        if (saksnummer == null) {
            return 0;
        }
        return (int) historikkKlient.hentHistorikk(saksnummer).stream()
                .filter(h -> HistorikkinnslagType.VEDLEGG_MOTTATT.equals(h.type()))
                .count();
    }

    private void ventTilInntekstmeldingErMottatt(Fødselsnummer fnr, Long saksnummer,
                                                 Integer antallNyeInntektsmeldinger,
                                                 Integer antallGamleInntekstmeldinger) {
        if (saksnummer != null) {
            var forventetAntallInnteksmeldinger = antallGamleInntekstmeldinger + antallNyeInntektsmeldinger;
            var antallIM = new AtomicReference<>(antallGamleInntekstmeldinger);
            Vent.til(() -> {
                antallIM.set(hentAntallHistorikkInnslagAvTypenVedleggMottatt(saksnummer));
                return antallIM.get() == forventetAntallInnteksmeldinger;
            }, 60, String.format("Forventet at det ble mottatt %d ny(e) innteksmeldinge(r), men det ble mottatt %d!" +
                    "på saksnummer %s", antallNyeInntektsmeldinger, antallIM.get() - antallGamleInntekstmeldinger, saksnummer));
        } else {
            ventTilFagsakOgBehandlingErOpprettet(fnr);
        }
    }

    private Long ventTilFagsakOgBehandlingErOpprettet(Fødselsnummer fnr) {
        Vent.til(() -> !fagsakKlient.søk(fnr).isEmpty(), 30,
                "Fagsak for bruker " + fnr + " har ikke blitt opprettet!");
        var saksnummer = fagsakKlient.søk(fnr).get(0).saksnummer();

        Vent.til(() -> {
            var behandlinger = behandlingerKlient.alle(saksnummer);
            var behandlingStartet = historikkKlient.hentHistorikk(saksnummer).stream()
                    .anyMatch(h -> HistorikkinnslagType.BEH_STARTET.equals(h.type()));
            return !behandlinger.isEmpty() && behandlingStartet;
        }, 30, "Ingen behandlinger er opprettet på saksnummer " + saksnummer + "etter 30 skun");

        return saksnummer;
    }

    /*
     * Opretter en personhendelse
     */
    public void opprettHendelsePåKafka(PersonhendelseDto personhendelseDto) {
        pdlLeesahKlient.opprettHendelse(personhendelseDto);
    }
}
