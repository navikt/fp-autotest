package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import static no.nav.foreldrepenger.autotest.util.log.LoggFormater.leggTilCallIdForFnr;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Mottakskanal.ALTINN;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Mottakskanal.SKAN_IM;

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
import no.nav.foreldrepenger.autotest.klienter.vtp.pdl.PdlLeesahJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.tokenx.TokenXHenterKlient;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
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

public class SøknadMottak extends Aktoer implements Innsender {

    private final FagsakJerseyKlient fagsakKlient;
    private final BehandlingerJerseyKlient behandlingerKlient;
    private final HistorikkJerseyKlient historikkKlient;

    private final MottakJerseyKlient mottakKlient;

    private final TokenXHenterKlient tokenXHenterKlient;
    private final JournalforingJerseyKlient journalpostKlient;
    private final PdlLeesahJerseyKlient pdlLeesahKlient;

    public SøknadMottak(Rolle rolle) {
        super(rolle);
        fagsakKlient = new FagsakJerseyKlient(cookieRequestFilter);
        behandlingerKlient = new BehandlingerJerseyKlient(cookieRequestFilter);
        historikkKlient = new HistorikkJerseyKlient(cookieRequestFilter);

        mottakKlient = new MottakJerseyKlient();

        tokenXHenterKlient = new TokenXHenterKlient();
        journalpostKlient = new JournalforingJerseyKlient();
        pdlLeesahKlient = new PdlLeesahJerseyKlient();
    }

    @Override
    public Saksnummer sendInnInntektsmelding(InntektsmeldingBuilder inntektsmeldingBuilder, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        // aktørId ignoreres ettersom det trengs bare i xmlen
        return sendInnInntektsmelding(List.of(inntektsmeldingBuilder), fnr, saksnummer);
    }

    public Saksnummer sendInnInntektsmelding(InntektsmeldingBuilder inntektsmeldingBuilder, Fødselsnummer fnr, Saksnummer saksnummer) {
        return sendInnInntektsmelding(List.of(inntektsmeldingBuilder), fnr, saksnummer);
    }

    @Override
    public Saksnummer sendInnInntektsmelding(List<InntektsmeldingBuilder> inntektsmeldingBuilder, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        return sendInnInntektsmelding(inntektsmeldingBuilder, fnr, saksnummer);
    }
    @Step("Sender inn IM for bruker {fnr}")
    public Saksnummer sendInnInntektsmelding(List<InntektsmeldingBuilder> inntektsmeldinger, Fødselsnummer fnr, Saksnummer saksnummer) {
        var antallGamleInntekstmeldinger = hentAntallHistorikkInnslagAvTypenVedleggMottatt(saksnummer);
        journalførInnteksmeldinger(inntektsmeldinger, fnr);
        return ventTilInntekstmeldingErMottatt(fnr, saksnummer, inntektsmeldinger.size(), antallGamleInntekstmeldinger);
    }

    private void journalførInnteksmeldinger(List<InntektsmeldingBuilder> inntektsmeldinger, Fødselsnummer fnr) {
        for (InntektsmeldingBuilder inntektsmelding : inntektsmeldinger) {
            LOG.info("Sender inn IM for søker: {}", fnr.value());
            var xml = inntektsmelding.createInntektesmeldingXML();
            var journalpostModell = lagJournalpost(fnr, "Inntektsmelding", xml,
                    ALTINN, null, DokumenttypeId.INNTEKTSMELDING);
            journalpostKlient.journalførR(journalpostModell);
            LOG.info("IM sendt inn!");
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
        var token = tokenXHenterKlient.hentAccessTokenForBruker(fnr);
        AllureHelper.tilJsonOgPubliserIAllureRapport(søknad);
        if (søknad instanceof Endringssøknad endringssøknad) {
            mottakKlient.sendSøknad(token, endringssøknad);
        } else {
            mottakKlient.sendSøknad(token, søknad);
        }
        return ventTilFagsakOgBehandlingErOpprettet(fnr);
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
        journalpostKlient.journalførR(journalpostModell);
        return ventTilFagsakOgBehandlingErOpprettet(fnr);
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

    private Integer hentAntallHistorikkInnslagAvTypenVedleggMottatt(Saksnummer saksnummer) {
        if (saksnummer == null) {
            return 0;
        }
        return (int) historikkKlient.hentHistorikk(saksnummer).stream()
                .filter(h -> HistorikkinnslagType.VEDLEGG_MOTTATT.equals(h.type()))
                .count();
    }

    private Saksnummer ventTilInntekstmeldingErMottatt(Fødselsnummer fnr, Saksnummer saksnummer,
                                                 Integer antallNyeInntektsmeldinger,
                                                 Integer antallGamleInntekstmeldinger) {
        if (saksnummer != null) {
            var forventetAntallInnteksmeldinger = antallGamleInntekstmeldinger + antallNyeInntektsmeldinger;
            var antallIM = new AtomicReference<>(antallGamleInntekstmeldinger);
            Vent.til(() -> {
                antallIM.set(hentAntallHistorikkInnslagAvTypenVedleggMottatt(saksnummer));
                return antallIM.get() == forventetAntallInnteksmeldinger;
            }, 60, "Forventet at det ble mottatt " + antallNyeInntektsmeldinger +
                    " ny(e) innteksmeldinge(r), men det ble mottatt " + (antallIM.get() - antallGamleInntekstmeldinger) +
                    " på saksnummer " + saksnummer);
            return saksnummer;
        } else {
            return ventTilFagsakOgBehandlingErOpprettet(fnr);
        }
    }

    private Saksnummer ventTilFagsakOgBehandlingErOpprettet(Fødselsnummer fnr) {
        Vent.til(() -> !fagsakKlient.søk(fnr).isEmpty(), 30,
                "Fagsak for bruker " + fnr.value() + " har ikke blitt opprettet!");
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
