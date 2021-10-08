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
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.dto.Kvittering;
import no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.JournalforingJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.oauth2.AzureAdJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.pdl.PdlLeesahJerseyKlient;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
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
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Variantformat;

public class SøknadMottak extends Aktoer implements Innsender {

    private final FagsakJerseyKlient fagsakKlient;
    private final BehandlingerJerseyKlient behandlingerKlient;
    private final HistorikkJerseyKlient historikkKlient;

    private final MottakJerseyKlient mottakKlient;

    private final AzureAdJerseyKlient oauth2Klient;
    private final JournalforingJerseyKlient journalpostKlient;
    private final PdlLeesahJerseyKlient pdlLeesahKlient;

    public SøknadMottak(Rolle rolle) {
        super(rolle);
        fagsakKlient = new FagsakJerseyKlient(cookieRequestFilter);
        behandlingerKlient = new BehandlingerJerseyKlient(cookieRequestFilter);
        historikkKlient = new HistorikkJerseyKlient(cookieRequestFilter);

        mottakKlient = new MottakJerseyKlient();

        oauth2Klient = new AzureAdJerseyKlient();
        journalpostKlient = new JournalforingJerseyKlient();
        pdlLeesahKlient = new PdlLeesahJerseyKlient();
    }

    @Override
    public long sendInnInntektsmelding(InntektsmeldingBuilder inntektsmeldingBuilder, AktørId aktørId, Fødselsnummer fnr, Long saksnummer) {
        // aktørId ignoreres ettersom det trengs bare i xmlen
        return sendInnInntektsmelding(List.of(inntektsmeldingBuilder), fnr, saksnummer);
    }

    public long sendInnInntektsmelding(InntektsmeldingBuilder inntektsmeldingBuilder, Fødselsnummer fnr, Long saksnummer) {
        return sendInnInntektsmelding(List.of(inntektsmeldingBuilder), fnr, saksnummer);
    }

    @Override
    public long sendInnInntektsmelding(List<InntektsmeldingBuilder> inntektsmeldingBuilder, AktørId aktørId, Fødselsnummer fnr, Long saksnummer) {
        return sendInnInntektsmelding(inntektsmeldingBuilder, fnr, saksnummer);
    }
    @Step("Sender inn IM for bruker {fnr}")
    public long sendInnInntektsmelding(List<InntektsmeldingBuilder> inntektsmeldinger, Fødselsnummer fnr, Long saksnummer) {
        var antallGamleInntekstmeldinger = hentAntallHistorikkInnslagAvTypenVedleggMottatt(saksnummer);
        journalførInnteksmeldinger(inntektsmeldinger, fnr);
        return ventTilInntekstmeldingErMottatt(fnr, saksnummer, inntektsmeldinger.size(), antallGamleInntekstmeldinger);
    }

    private void journalførInnteksmeldinger(List<InntektsmeldingBuilder> inntektsmeldinger, Fødselsnummer fnr) {
        for (InntektsmeldingBuilder inntektsmelding : inntektsmeldinger) {
            LOG.info("Sender inn IM for søker: {}", fnr.getFnr());
            var xml = inntektsmelding.createInntektesmeldingXML();
            var journalpostModell = lagJournalpost(fnr, "Inntektsmelding", xml,
                    "ALTINN", null, DokumenttypeId.INNTEKTSMELDING);
            journalpostKlient.journalførR(journalpostModell);
        }
    }

    @Override
    public long sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, Long saksnummer) {
        return sendInnSøknad(fnr, søknad);
    }

    @Override
    public long sendInnSøknad(Endringssøknad søknad, AktørId aktørId, Fødselsnummer fnr, Long saksnummer) {
        return sendInnSøknad(fnr, søknad);
    }

    @Step("[{søknad.søker.søknadsRolle}]: Sender inn søknad: {fnr}")
    private Long sendInnSøknad(Fødselsnummer fnr, Søknad søknad) {
        var callId = leggTilCallIdForFnr(fnr);
        LOG.info("Sender inn søknadd for bruker {}", fnr.getFnr());
        var token = oauth2Klient.hentAccessTokenForBruker(fnr);
        AllureHelper.tilJsonOgPubliserIAllureRapport(søknad);
        // TODO: Bruk Kvittering i felles istedenfor hardkodet her.
        Kvittering kvittering;
        if (søknad instanceof Endringssøknad endringssøknad) {
            kvittering = mottakKlient.sendSøknad(token, endringssøknad);
        } else {
            kvittering = mottakKlient.sendSøknad(token, søknad);
        }
        assertTrue(kvittering != null && kvittering.erVellykket(), "Innsending av søknad til fpsoknad-mottak feilet!");
        var saksnummer = ventTilFagsakOgBehandlingErOpprettet(fnr);
        leggTilCallIdforSaksnummer(callId, saksnummer);
        LOG.info("Innsending av søknad er vellykket!");
        return saksnummer;
    }

    @Override
    public long sendInnPapirsøknadForeldrepenger(AktørId aktørId, Fødselsnummer fnr) {
        return sendInnPapirsøknad(fnr, DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL, null);
    }

    @Override
    public long sendInnPapirsøknadEEndringForeldrepenger(AktørId aktørId, Fødselsnummer fnr, Long saksnummer) {
        return sendInnPapirsøknad(fnr, DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL, saksnummer);
    }

    @Override
    public long sendInnPapirsøknadEngangsstønad(AktørId aktørId, Fødselsnummer fnr) {
        return sendInnPapirsøknad(fnr, DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL, null);
    }

    private Long sendInnPapirsøknad(Fødselsnummer fnr, DokumenttypeId dokumenttypeId, Long saksnummer) {
        var callId = leggTilCallIdForFnr(fnr);
        LOG.info("Sender inn papirsøknadd for bruker {}", fnr);
        var journalpostModell = lagJournalpost(fnr, dokumenttypeId.getTermnavn(), null,
                "SKAN_IM", "skanIkkeUnik.pdf", dokumenttypeId);
        if (saksnummer != null) {
            journalpostModell.setSakId(saksnummer.toString());
        }
        journalpostKlient.journalførR(journalpostModell);
        saksnummer = ventTilFagsakOgBehandlingErOpprettet(fnr);
        LOG.info("Innsending av papirsøknad er vellykket!");
        leggTilCallIdforSaksnummer(callId, saksnummer);
        return saksnummer;
    }

    @Override
    public void sendInnKlage(AktørId aktørId, Fødselsnummer fnr, Long saksnummer) {
        sendInnKlage(fnr);
    }

    public void sendInnKlage(Fødselsnummer fnr) {
        leggTilCallIdForFnr(fnr);
        var journalpostModell = lagJournalpost(fnr, DokumenttypeId.KLAGE_DOKUMENT.getTermnavn(), null,
                "SKAN_IM", null, DokumenttypeId.KLAGE_DOKUMENT);
        journalpostKlient.journalførR(journalpostModell);
    }

    private JournalpostModell lagJournalpost(Fødselsnummer fnr, String tittel, String innhold, String mottakskanal,
                                             String eksternReferanseId, DokumenttypeId dokumenttypeId) {
        var journalpostModell = new JournalpostModell();
        journalpostModell.setTittel(tittel);
        journalpostModell.setJournalStatus(Journalstatus.MIDLERTIDIG_JOURNALFØRT);
        journalpostModell.setMottattDato(LocalDateTime.now());
        journalpostModell.setMottakskanal(mottakskanal);
        journalpostModell.setArkivtema(Arkivtema.FOR);
        journalpostModell.setAvsenderFnr(fnr.getFnr());
        journalpostModell.setEksternReferanseId(eksternReferanseId);
        journalpostModell.setSakId("");
        journalpostModell.setBruker(new JournalpostBruker(fnr.getFnr(), BrukerType.FNR));
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

    private Integer hentAntallHistorikkInnslagAvTypenVedleggMottatt(Long saksnummer) {
        if (saksnummer == null) {
            return 0;
        }
        return (int) historikkKlient.hentHistorikk(saksnummer).stream()
                .filter(h -> HistorikkinnslagType.VEDLEGG_MOTTATT.equals(h.type()))
                .count();
    }

    private long ventTilInntekstmeldingErMottatt(Fødselsnummer fnr, Long saksnummer,
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
            return saksnummer;
        } else {
            return ventTilFagsakOgBehandlingErOpprettet(fnr);
        }
    }

    private Long ventTilFagsakOgBehandlingErOpprettet(Fødselsnummer fnr) {
        Vent.til(() -> !fagsakKlient.søk(fnr).isEmpty(), 30,
                "Fagsak for bruker " + fnr.getFnr() + " har ikke blitt opprettet!");
        var saksnummer = fagsakKlient.søk(fnr).get(0).saksnummer();

        Vent.til(() -> {
            var behandlinger = behandlingerKlient.alle(saksnummer);
            var behandlingStartet = historikkKlient.hentHistorikk(saksnummer).stream()
                    .anyMatch(h -> HistorikkinnslagType.BEH_STARTET.equals(h.type()));
            return !behandlinger.isEmpty() && behandlingStartet;
        }, 30, "Ingen behandlinger er opprettet på saksnummer " + saksnummer);

        return saksnummer;
    }

    /*
     * Opretter en personhendelse
     */
    public void opprettHendelsePåKafka(PersonhendelseDto personhendelseDto) {
        pdlLeesahKlient.opprettHendelse(personhendelseDto);
    }
}
