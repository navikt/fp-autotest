package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.DokumentIDFraSøknad.dokumentTypeFraRelasjon;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugSenderInnDokument;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.mockito.Mockito;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
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
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.JournalforingJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.pdl.PdlLeesahJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.SafJerseyKlient;
import no.nav.foreldrepenger.autotest.util.ControllerHelper;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.innsending.mappers.V1SvangerskapspengerDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3EngangsstønadDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.vtp.testmodell.dokument.JournalpostModellGenerator;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.JournalpostModell;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Dokumentkategori;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

public class Fordel extends Aktoer implements Innsender {

    // TODO: Finn bedre plass for denne
    public Oppslag oppslag = Mockito.mock(Oppslag.class);

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


    public Fordel(Rolle rolle) {
        super(rolle);
        fordelKlient = new FordelJerseyKlient(cookieRequestFilter);
        behandlingerKlient = new BehandlingerJerseyKlient(cookieRequestFilter);
        fagsakKlient = new FagsakJerseyKlient(cookieRequestFilter);
        historikkKlient = new HistorikkJerseyKlient(cookieRequestFilter);

        journalpostKlient = new JournalforingJerseyKlient();
        safKlient = new SafJerseyKlient();
        pdlLeesahKlient = new PdlLeesahJerseyKlient();
    }

    /*
     * Sender inn søkand og returnerer saksinformasjon
     */
    @Override
    public long sendInnSøknad(Endringssøknad søknad, AktørId aktørId, Fødselsnummer fnr, Long saksnummer) {
        return sendInnSøknad(søknad, aktørId, fnr, FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
    }

    public long sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, DokumenttypeId dokumenttypeId) {
        return sendInnSøknad(søknad, aktørId, fnr, dokumenttypeId, null);
    }

    @Override
    @Step("Sender inn papirsøknad foreldrepenger")
    public long sendInnPapirsøknadForeldrepenger(AktørId aktørId, Fødselsnummer fnr) {
        return sendInnSøknad(null, aktørId, fnr, SØKNAD_FORELDREPENGER_FØDSEL);
    }

    @Override
    @Step("Sender inn endringssøknad på papir")
    public long sendInnPapirsøknadEEndringForeldrepenger(AktørId aktørId, Fødselsnummer fnr, Long saksnummer) {
        return sendInnSøknad(null, aktørId, fnr, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
    }

    @Override
    @Step("Sender inn papirsøknad engangsstønad")
    public long sendInnPapirsøknadEngangsstønad(AktørId aktørId, Fødselsnummer fnr) {
        return sendInnSøknad(null, aktørId, fnr, SØKNAD_ENGANGSSTØNAD_FØDSEL);
    }

    @Override
    public long sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, Long saksnummer) {
        var dokumenttypeId = dokumentTypeFraRelasjon(søknad);
        return sendInnSøknad(søknad, aktørId, fnr, dokumenttypeId, saksnummer);
    }

    @Step("Sender inn søknad [{dokumenttypeId}]")
    public long sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, DokumenttypeId dokumenttypeId,
                              Long saksnummer) {
        String xml = null;
        LocalDate mottattDato;
        if (null != søknad) {
            xml = tilSøknadXML(søknad, aktørId, dokumenttypeId);
            mottattDato = søknad.getMottattdato();
        } else {
            mottattDato = LocalDate.now();
        }

        JournalpostModell journalpostModell = JournalpostModellGenerator
                .lagJournalpostStrukturertDokument(xml == null ? "" : xml, fnr.value(), dokumenttypeId);
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

    private String tilSøknadXML(Søknad søknad, AktørId aktørId, DokumenttypeId dokumenttypeId) {
        var mapper = switch (dokumenttypeId) {
            case SØKNAD_FORELDREPENGER_FØDSEL, SØKNAD_FORELDREPENGER_ADOPSJON, FORELDREPENGER_ENDRING_SØKNAD -> new V3ForeldrepengerDomainMapper(oppslag);
            case SØKNAD_SVANGERSKAPSPENGER -> new V1SvangerskapspengerDomainMapper();
            case SØKNAD_ENGANGSSTØNAD_FØDSEL, SØKNAD_ENGANGSSTØNAD_ADOPSJON -> new V3EngangsstønadDomainMapper(oppslag);
            default -> throw new IllegalArgumentException("Ikke støttet dokumenttypeid: " + dokumenttypeId);
        };

        String xml;
        if (dokumenttypeId.equals(FORELDREPENGER_ENDRING_SØKNAD)) {
            xml = mapper.tilXML((Endringssøknad) søknad, aktørId, null);
        } else {
            xml = mapper.tilXML(søknad, aktørId, null);
        }
        return xml;
    }

    private String finnBehandlingstemaKode(DokumenttypeId dokumenttypeId) {
        try {
            return ControllerHelper.translateSøknadDokumenttypeToBehandlingstema(dokumenttypeId).getOffisiellKode();
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
     * Sender inn inntektsmelding og returnerer saksnummer
     */
    @Override
    @Step("Sender inn IM for fnr {fnr}")
    public long sendInnInntektsmelding(InntektsmeldingBuilder inntektsmelding, AktørId aktørId, Fødselsnummer fnr,
            Long gammeltSaksnummer) {
        String xml = inntektsmelding.createInntektesmeldingXML();
        debugSenderInnDokument("Inntektsmelding", xml);
        String behandlingstemaOffisiellKode = "ab0047";
        String dokumentKategori = Dokumentkategori.IKKE_TOLKBART_SKJEMA.getKode();
        String dokumentTypeIdOffisiellKode = DokumenttypeId.INNTEKTSMELDING.getKode();

        JournalpostModell journalpostModell = JournalpostModellGenerator.lagJournalpostStrukturertDokument(xml, fnr.value(),
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
                        .anyMatch(h -> HistorikkinnslagType.VEDLEGG_MOTTATT.equals(h.type()));
            }, 40, "Saken har ikke mottatt inntektsmeldingen.\nHar historikk: " + historikkRef.get());
        } else {
            Vent.til(() -> !fagsakKlient.søk("" + nyttSaksnummer).isEmpty(),
                    40, "Opprettet ikke fagsak for inntektsmelding");
        }

        return nyttSaksnummer;
    }

    @Override
    public long sendInnInntektsmelding(List<InntektsmeldingBuilder> inntektsmeldinger, AktørId aktørId, Fødselsnummer fnr,
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

    private int antallInntektsmeldingerMottatt(long saksnummer) {
        List<HistorikkInnslag> historikk = historikkKlient.hentHistorikk(saksnummer);
        return (int) historikk.stream()
                .filter(h -> HistorikkinnslagType.VEDLEGG_MOTTATT.equals(h.type()))
                .count();
    }

    @Override
    @Step("Sender inn klage på saksnummer {saksnummer}")
    public void sendInnKlage(AktørId aktørId, Fødselsnummer fnr, Long saksnummer) {
        String behandlingstemaOffisiellKode = "ab0047";
        String dokumentKategori = Dokumentkategori.KLAGE_ANKE.getKode();
        String dokumentTypeIdOffisiellKode = DokumenttypeId.KLAGE_DOKUMENT.getKode();

        JournalpostModell journalpostModell = JournalpostModellGenerator.lagJournalpostUstrukturertDokument(
                fnr.value(), DokumenttypeId.KLAGE_DOKUMENT);
        String journalpostId = journalpostKlient.journalfør(journalpostModell).journalpostId();

        long sakId = sendInnJournalpost(null, journalpostId, behandlingstemaOffisiellKode,
                dokumentTypeIdOffisiellKode, dokumentKategori, aktørId, saksnummer);
        journalpostModell.setSakId(String.valueOf(sakId));
    }

    /*
     * Sender inn journalpost og returnerer saksnummer
     */
    private Long sendInnJournalpost(String xml, String journalpostId, String behandlingstemaOffisiellKode,
            String dokumentTypeIdOffisiellKode, String dokumentKategori, AktørId aktørId, Long saksnummer) {
        return sendInnJournalpost(xml, LocalDate.now(), journalpostId, behandlingstemaOffisiellKode,
                dokumentTypeIdOffisiellKode, dokumentKategori, aktørId, saksnummer);
    }

    private Long sendInnJournalpost(String xml, LocalDate mottattDato, String journalpostId,
            String behandlingstemaOffisiellKode, String dokumentTypeIdOffisiellKode,
            String dokumentKategori, AktørId aktørId, Long saksnummer) {

        if ((saksnummer == null) || (saksnummer == 0L)) {
            OpprettSak journalpost = new OpprettSak(journalpostId, behandlingstemaOffisiellKode, aktørId.value());
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
}
