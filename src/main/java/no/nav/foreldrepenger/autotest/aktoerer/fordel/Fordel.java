package no.nav.foreldrepenger.autotest.aktoerer.fordel;

import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugSenderInnDokument;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingerJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.FordelJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.JournalpostId;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.JournalpostKnyttning;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.JournalpostMottak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.OpprettSak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.Saksnummer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.JournalforingJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.pdl.PdlLeesahJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.SafJerseyKlient;
import no.nav.foreldrepenger.autotest.util.ControllerHelper;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.vtp.kontrakter.PersonhendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.JournalpostModellGenerator;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.JournalpostModell;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Dokumentkategori;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.v3.Soeknad;

public class Fordel extends Aktoer {

    private static final Logger logger = LoggerFactory.getLogger(Fordel.class);

    /*
     * Klienter
     */
    FordelJerseyKlient fordelKlient;
    BehandlingerJerseyKlient behandlingerKlient;
    FagsakJerseyKlient fagsakKlient;
    HistorikkJerseyKlient historikkKlient;

    // Vtp Klienter
    PdlLeesahJerseyKlient pdlLeesahKlient;
    JournalforingJerseyKlient journalpostKlient;
    SafJerseyKlient safKlient;

    public Fordel() {
        fordelKlient = new FordelJerseyKlient();
        behandlingerKlient = new BehandlingerJerseyKlient();
        journalpostKlient = new JournalforingJerseyKlient();
        safKlient = new SafJerseyKlient();
        fagsakKlient = new FagsakJerseyKlient();
        historikkKlient = new HistorikkJerseyKlient();
        pdlLeesahKlient = new PdlLeesahJerseyKlient();
    }

    /*
     * Sender inn søkand og returnerer saksinformasjon
     */
    @Step("Sender inn søknad")
    public long sendInnSøknad(Soeknad søknad, String aktørId, String fnr, DokumenttypeId dokumenttypeId,
            Long saksnummer) {
        String xml = null;
        LocalDate mottattDato;
        if (null != søknad) {
            xml = SøknadBuilder.tilXML(søknad);
            mottattDato = søknad.getMottattDato();
        } else {
            mottattDato = LocalDate.now();
        }

        JournalpostModell journalpostModell = JournalpostModellGenerator
                .lagJournalpostStrukturertDokument(xml == null ? "" : xml, fnr, dokumenttypeId);
        if ((saksnummer != null) && (saksnummer != 0L)) {
            journalpostModell.setSakId(saksnummer.toString());
        }
        String journalpostId = journalpostKlient.journalfør(journalpostModell).journalpostId();

        String behandlingstemaOffisiellKode = finnBehandlingstemaKode(dokumenttypeId);
        String dokumentTypeIdOffisiellKode = dokumenttypeId.getKode();
        debugSenderInnDokument("Foreldrepengesøknad", xml);
        long sakId = sendInnJournalpost(xml, mottattDato, journalpostId, behandlingstemaOffisiellKode,
                dokumentTypeIdOffisiellKode, "SOK", aktørId, saksnummer);
        journalpostModell.setSakId(String.valueOf(sakId));
        logger.info("Sendt inn søknad på sak med saksnummer: {}", sakId);

        Vent.til(() -> {
            List<Behandling> behandlinger = behandlingerKlient.alle(sakId);
            // TODO: Gjøre denne asynkron
            if (behandlinger.size() > 1) {
                sleep(5000);
            }
            return !behandlinger.isEmpty()
                    && (behandlingerKlient.statusAsObject(behandlinger.get(0).uuid) == null);
        }, 60, "Saken hadde ingen behandlinger");

        if (DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD.equals(dokumenttypeId)) {
            // TODO: Vent.til fungerer ikke med endringssøknad. Venter ikke til behandlingen er opprettet
            sleep(5000);
        }

        return sakId;
    }
    private String finnBehandlingstemaKode(DokumenttypeId dokumenttypeId) {
        try {
            return ControllerHelper.translateSøknadDokumenttypeToBehandlingstema(dokumenttypeId).getKode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Sender inn søknad og opretter ny sak
     */

    public long sendInnSøknad(Soeknad søknad, String aktørId, String fnr, DokumenttypeId dokumenttypeId) {
        return sendInnSøknad(søknad, aktørId, fnr, dokumenttypeId, null);
    }

    public long sendInnSøknad(Soeknad søknad, TestscenarioDto scenario, DokumenttypeId dokumenttypeId) {
        return sendInnSøknad(søknad, scenario, dokumenttypeId, null);
    }

    public long sendInnSøknad(Soeknad søknad, TestscenarioDto scenario, DokumenttypeId dokumenttypeId,
            Long saksnummer) {
        String aktørId = scenario.personopplysninger().søkerAktørIdent();
        String fnr = scenario.personopplysninger().søkerIdent();
        return sendInnSøknad(søknad, aktørId, fnr, dokumenttypeId, saksnummer);
    }

    public long sendInnSøknad(Soeknad søknad, TestscenarioDto scenario, DokumenttypeId dokumenttypeId, Long saksnummer,
            boolean annenPart) {
        String aktørId;
        String fnr;
        if (annenPart) {
            aktørId = scenario.personopplysninger().annenpartAktørIdent();
            fnr = scenario.personopplysninger().annenpartIdent();
        } else {
            aktørId = scenario.personopplysninger().søkerAktørIdent();
            fnr = scenario.personopplysninger().søkerIdent();
        }

        return sendInnSøknad(søknad, aktørId, fnr, dokumenttypeId, saksnummer);
    }

    /*
     * Sender inn søknad og returnerer saksinformasjon
     */
    @Step("Sender inn papirsøknad foreldrepenger")
    public long sendInnPapirsøknadForeldrepenger(TestscenarioDto testscenario, boolean erAnnenPart) {
        return sendInnSøknad(null, testscenario, DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL, null, erAnnenPart);
    }

    @Step("Sender inn endringssøknad på papir")
    public long sendInnPapirsøknadEndringForeldrepenger(TestscenarioDto testscenario, Long saksnummer) {
        return sendInnSøknad(null, testscenario, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
    }

    @Step("Sender inn papirsøknad svangersskapspenger")
    public long sendInnPapirsøknadSvangerskapspenger(TestscenarioDto testscenario) {
        return sendInnSøknad(null, testscenario, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);
    }

    /*
     * Sender inn inntektsmelding og returnerer saksnummer
     */
    @Step("Sender inn inntektsmelding")
    public long sendInnInntektsmelding(InntektsmeldingBuilder inntektsmelding, String aktørId, String fnr,
            Long gammeltSaksnummer) {
        String xml = inntektsmelding.createInntektesmeldingXML();
        debugSenderInnDokument("Inntektsmelding", xml);
        String behandlingstemaOffisiellKode = "ab0047";
        String dokumentKategori = Dokumentkategori.IKKE_TOLKBART_SKJEMA.getKode();
        String dokumentTypeIdOffisiellKode = DokumenttypeId.INNTEKTSMELDING.getKode();

        JournalpostModell journalpostModell = JournalpostModellGenerator.lagJournalpostStrukturertDokument(xml, fnr,
                DokumenttypeId.INNTEKTSMELDING);
        String journalpostId = journalpostKlient.journalfør(journalpostModell).journalpostId();

        long nyttSaksnummer = sendInnJournalpost(xml, journalpostId, behandlingstemaOffisiellKode,
                dokumentTypeIdOffisiellKode, dokumentKategori, aktørId, gammeltSaksnummer);
        journalpostModell.setSakId(String.valueOf(nyttSaksnummer));

        // vent til inntektsmelding er mottatt
        if (gammeltSaksnummer != null) {
            final long saksnummerF = gammeltSaksnummer;
            AtomicReference<List<HistorikkInnslag>> historikkRef = new AtomicReference<>();
            Vent.til(() -> {
                historikkRef.set(historikkKlient.hentHistorikk(saksnummerF));
                return historikkRef.get().stream()
                        .anyMatch(h -> HistorikkInnslag.VEDLEGG_MOTTATT.getKode().equals(h.getTypeKode()));
            }, 40, "Saken har ikke mottatt inntektsmeldingen.\nHar historikk: " + historikkRef.get());
        } else {
            Vent.til(() -> fagsakKlient.søk("" + nyttSaksnummer).size() > 0,
                    40, "Opprettet ikke fagsak for inntektsmelding");
        }

        return nyttSaksnummer;
    }

    public long sendInnInntektsmelding(InntektsmeldingBuilder inntektsmelding, TestscenarioDto testscenario,
            Long saksnummer) {
        return sendInnInntektsmelding(inntektsmelding, testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(), saksnummer);
    }

    public Long sendInnInntektsmeldinger(List<InntektsmeldingBuilder> inntektsmeldinger, TestscenarioDto scenario) {
        return sendInnInntektsmeldinger(inntektsmeldinger, scenario, null);
    }

    @Step("Sender inn innteksmeldinger")
    public Long sendInnInntektsmeldinger(List<InntektsmeldingBuilder> inntektsmeldinger, String aktørId, String fnr,
            Long saksnummer) {
        int gammelAntallIM = 0;
        if (saksnummer != null) {
            gammelAntallIM = antallInntektsmeldingerMottatt(saksnummer);
        }
        for (InntektsmeldingBuilder builder : inntektsmeldinger) {
            saksnummer = sendInnInntektsmelding(builder, aktørId, fnr, saksnummer);
            if (inntektsmeldinger.size() > 1) {
                // Innteksmeldinger etter den førte returneres med en gang fordi det allerede finnes et historikkinnslag
                // VEDLEGG_MOTTATT fra den tidligere. Vi må derfor vente litt.
                sleep(4000); // TODO finn en bedre måte å gjøre dette på...
            }
        }
        final int gammelAntallIMF = gammelAntallIM;
        final long saksnummerF = saksnummer;
        Vent.til(() -> (antallInntektsmeldingerMottatt(saksnummerF) - gammelAntallIMF) == inntektsmeldinger.size(),
                20, "har ikke mottat alle inntektsmeldinger. Sak: " + saksnummer);
        return saksnummer;
    }

    @Step("Henter antall innteksmeldingerMottatt for saksnummer [{saksnummer}]")
    private int antallInntektsmeldingerMottatt(long saksnummer) {
        List<HistorikkInnslag> historikk = historikkKlient.hentHistorikk(saksnummer);
        return (int) historikk.stream().filter(h -> HistorikkInnslag.VEDLEGG_MOTTATT.getKode().equals(h.getTypeKode()))
                .count();
    }

    public Long sendInnInntektsmeldinger(List<InntektsmeldingBuilder> inntektsmeldinger, TestscenarioDto testscenario,
            Long saksnummer) {
        return sendInnInntektsmeldinger(inntektsmeldinger, testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(), saksnummer);
    }

    @Step("Sender inn klage for bruker")
    public long sendInnKlage(String xmlstring, TestscenarioDto scenario, Long saksnummer) {
        String aktørId = scenario.personopplysninger().søkerAktørIdent();
        String behandlingstemaOffisiellKode = "ab0047";
        String dokumentKategori = Dokumentkategori.KLAGE_ANKE.getKode();
        String dokumentTypeIdOffisiellKode = DokumenttypeId.KLAGE_DOKUMENT.getKode();

        JournalpostModell journalpostModell = JournalpostModellGenerator.lagJournalpostUstrukturertDokument(
                scenario.personopplysninger().søkerIdent(), DokumenttypeId.KLAGE_DOKUMENT);
        String journalpostId = journalpostKlient.journalfør(journalpostModell).journalpostId();

        long sakId = sendInnJournalpost(xmlstring, journalpostId, behandlingstemaOffisiellKode,
                dokumentTypeIdOffisiellKode, dokumentKategori, aktørId, saksnummer);
        journalpostModell.setSakId(String.valueOf(sakId));
        return sakId;
    }

    /*
     * Sender inn journalpost og returnerer saksnummer
     */
    private Long sendInnJournalpost(String xml, String journalpostId, String behandlingstemaOffisiellKode,
            String dokumentTypeIdOffisiellKode, String dokumentKategori, String aktørId, Long saksnummer) {
        return sendInnJournalpost(xml, LocalDate.now(), journalpostId, behandlingstemaOffisiellKode,
                dokumentTypeIdOffisiellKode, dokumentKategori, aktørId, saksnummer);
    }

    @Step("Sender inn journalpost")
    private Long sendInnJournalpost(String xml, LocalDate mottattDato, String journalpostId,
            String behandlingstemaOffisiellKode, String dokumentTypeIdOffisiellKode,
            String dokumentKategori, String aktørId, Long saksnummer) {

        if ((saksnummer == null) || (saksnummer == 0L)) {
            OpprettSak journalpost = new OpprettSak(journalpostId, behandlingstemaOffisiellKode, aktørId);
            saksnummer = fordelKlient.fagsakOpprett(journalpost).saksnummer();
        }

        journalpostKlient.knyttSakTilJournalpost(journalpostId, saksnummer.toString());
        JournalpostId idDto = new JournalpostId(journalpostId);
        JournalpostKnyttning journalpostKnyttning = new JournalpostKnyttning(new Saksnummer(saksnummer), idDto);
        fordelKlient.fagsakKnyttJournalpost(journalpostKnyttning);

        JournalpostMottak journalpostMottak = new JournalpostMottak("" + saksnummer, journalpostId, mottattDato,
                behandlingstemaOffisiellKode);
        journalpostMottak.setDokumentTypeIdOffisiellKode(dokumentTypeIdOffisiellKode);
        journalpostMottak.setForsendelseId(UUID.randomUUID().toString());
        journalpostMottak.setDokumentKategoriOffisiellKode(dokumentKategori);
        if (dokumentTypeIdOffisiellKode.equalsIgnoreCase(DokumenttypeId.INNTEKTSMELDING.getKode())) {
            journalpostMottak.setEksternReferanseId(journalpostMottak.lagUnikEksternReferanseId());
        }

        if (null != xml) {
            journalpostMottak.setPayloadXml(new String(Base64.getUrlEncoder().withoutPadding().encode(xml.getBytes())));
            journalpostMottak.setPayloadLength(xml.length());
        } else {
            journalpostMottak.setPayloadLength(1);
        }

        fordelKlient.journalpost(journalpostMottak);

        return saksnummer;
    }

    /* SAF */
    public byte[] hentJournalførtDokument(String dokumentId, String variantFormat) {
        return safKlient.hentDokumenter(null, dokumentId, variantFormat);
    }


    /*
     * Opretter en personhendelse
     */
    public void opprettHendelsePåKafka(PersonhendelseDto personhendelseDto) {
        pdlLeesahKlient.opprettHendelse(personhendelseDto);
    }
}
