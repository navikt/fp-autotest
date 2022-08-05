package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.DokumentIDFraSøknad.dokumentTypeFraRelasjon;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugSenderInnDokument;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.JournalpostModellGenerator.lagJournalpostStrukturertDokument;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.JournalpostModellGenerator.lagJournalpostUstrukturertDokument;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.mockito.Mockito;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.FordelJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.SafJerseyKlient;
import no.nav.foreldrepenger.autotest.util.ControllerHelper;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.innsending.mappers.V1SvangerskapspengerDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3EngangsstønadDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.kontrakter.fordel.JournalpostKnyttningDto;
import no.nav.foreldrepenger.kontrakter.fordel.JournalpostMottakDto;
import no.nav.foreldrepenger.kontrakter.fordel.OpprettSakDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Dokumentkategori;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

public class Fordel extends DokumentVenter {

    private static int inkrementForEksternReferanse = 0;

    // TODO: Finn bedre plass for denne
    public Oppslag oppslag = Mockito.mock(Oppslag.class);

    /*
     * Klienter
     */
    FordelJerseyKlient fordelKlient;
    SafJerseyKlient safKlient;


    public Fordel(Rolle rolle) {
        super(rolle);
        fordelKlient = new FordelJerseyKlient(cookieRequestFilter);
        safKlient = new SafJerseyKlient();
    }

    /*
     * Sender inn søkand og returnerer saksinformasjon
     */
    @Override
    public Saksnummer sendInnSøknad(Endringssøknad søknad, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        return sendInnSøknad(søknad, aktørId, fnr, FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
    }

    public Saksnummer sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, DokumenttypeId dokumenttypeId) {
        return sendInnSøknad(søknad, aktørId, fnr, dokumenttypeId, null);
    }

    @Override
    @Step("Sender inn papirsøknad foreldrepenger")
    public Saksnummer sendInnPapirsøknadForeldrepenger(AktørId aktørId, Fødselsnummer fnr) {
        return sendInnSøknad(null, aktørId, fnr, SØKNAD_FORELDREPENGER_FØDSEL);
    }

    @Override
    @Step("Sender inn endringssøknad på papir")
    public Saksnummer sendInnPapirsøknadEEndringForeldrepenger(AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        return sendInnSøknad(null, aktørId, fnr, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
    }

    @Override
    @Step("Sender inn papirsøknad engangsstønad")
    public Saksnummer sendInnPapirsøknadEngangsstønad(AktørId aktørId, Fødselsnummer fnr) {
        return sendInnSøknad(null, aktørId, fnr, SØKNAD_ENGANGSSTØNAD_FØDSEL);
    }

    @Override
    public Saksnummer sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        var dokumenttypeId = dokumentTypeFraRelasjon(søknad);
        return sendInnSøknad(søknad, aktørId, fnr, dokumenttypeId, saksnummer);
    }

    @Step("Sender inn søknad [{dokumenttypeId}]")
    public Saksnummer sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, DokumenttypeId dokumenttypeId, Saksnummer saksnummer) {
        String xml = "";
        LocalDate mottattDato;
        if (null != søknad) {
            xml = tilSøknadXML(søknad, aktørId, dokumenttypeId);
            mottattDato = søknad.getMottattdato();
        } else {
            mottattDato = LocalDate.now();
        }
        debugSenderInnDokument("Foreldrepengesøknad", xml);

        var journalpostModell = lagJournalpostStrukturertDokument(xml, fnr.value(), dokumenttypeId);
        if (saksnummer != null) {
            journalpostModell.setSakId(saksnummer.value());
        }

        var skjæringsTidspunktForNyBehandling  = LocalDateTime.now().minusSeconds(1); // Legger inn slack på 1 sekund
        var antallEksistrendeFagsakerPåSøker = antallEksistrendeFagsakerPåSøker(fnr);
        var journalpostId = journalpostKlient.journalfør(journalpostModell).journalpostId();
        knyttJournalpostTilFagsak(xml, mottattDato, journalpostId, finnBehandlingstemaKode(dokumenttypeId),
                dokumenttypeId.getKode(), "SOK", aktørId, saksnummer);
        return ventTilFagsakOgBehandlingErOpprettet(fnr, skjæringsTidspunktForNyBehandling, antallEksistrendeFagsakerPåSøker);
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

    /*
     * Sender inn inntektsmelding og returnerer saksnummer
     */
    @Override
    @Step("Sender inn IM for fnr {fnr}")
    public Saksnummer sendInnInntektsmelding(InntektsmeldingBuilder inntektsmelding, AktørId aktørId, Fødselsnummer fnr, Saksnummer gammeltSaksnummer) {
        return sendInnInntektsmelding(List.of(inntektsmelding), aktørId, fnr, gammeltSaksnummer);
    }

    @Override
    public Saksnummer sendInnInntektsmelding(List<InntektsmeldingBuilder> inntektsmeldinger, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        var antallEksisterendeInntekstmeldinger = antallInntektsmeldingerMottattPåSak(saksnummer);
        journalførKnyttIM(inntektsmeldinger, aktørId, fnr, saksnummer);
        return ventTilAlleInntekstmeldingeneErMottatt(fnr, saksnummer, inntektsmeldinger.size(), antallEksisterendeInntekstmeldinger);
    }

    private void journalførKnyttIM(List<InntektsmeldingBuilder> inntektsmeldinger, AktørId aktørId, Fødselsnummer fnr, Saksnummer eksisterendeSaksnummer) {
        LOG.info("Sender inn {} IM(er) for søker {} ...", inntektsmeldinger.size(), fnr.value());
        for (var inntektsmelding : inntektsmeldinger) {
            var xml = inntektsmelding.createInntektesmeldingXML();
            debugSenderInnDokument("Inntektsmelding", xml);
            var journalpostModell = lagJournalpostStrukturertDokument(xml, fnr.value(), DokumenttypeId.INNTEKTSMELDING);
            var journalpostId = journalpostKlient.journalfør(journalpostModell).journalpostId();

            var dokumentKategori = Dokumentkategori.IKKE_TOLKBART_SKJEMA.getKode();
            var dokumentTypeIdOffisiellKode = DokumenttypeId.INNTEKTSMELDING.getKode();
            knyttJournalpostTilFagsak(xml, journalpostId,  dokumentTypeIdOffisiellKode, dokumentKategori, aktørId, eksisterendeSaksnummer);
        }
    }

    @Override
    @Step("Sender inn klage på saksnummer {saksnummer}")
    public void sendInnKlage(AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        var dokumentKategori = Dokumentkategori.KLAGE_ANKE.getKode();
        var dokumentTypeIdOffisiellKode = DokumenttypeId.KLAGE_DOKUMENT.getKode();

        var journalpostModell = lagJournalpostUstrukturertDokument(fnr.value(), DokumenttypeId.KLAGE_DOKUMENT);
        var journalpostId = journalpostKlient.journalfør(journalpostModell).journalpostId();
        knyttJournalpostTilFagsak(null, journalpostId, dokumentTypeIdOffisiellKode, dokumentKategori, aktørId, saksnummer);
    }

    /*
     * Sender inn journalpost og returnerer saksnummer
     */
    private void knyttJournalpostTilFagsak(String xml, String journalpostId, String dokumentTypeIdOffisiellKode,
                                                 String dokumentKategori, AktørId aktørId, Saksnummer saksnummer) {
        knyttJournalpostTilFagsak(xml, LocalDate.now(), journalpostId, "ab0047", dokumentTypeIdOffisiellKode,
                dokumentKategori, aktørId, saksnummer);
    }

    private void knyttJournalpostTilFagsak(String xml, LocalDate mottattDato, String journalpostId,
                                                String behandlingstemaOffisiellKode, String dokumentTypeIdOffisiellKode,
                                                String dokumentKategori, AktørId aktørId, Saksnummer saksnummer) {

        if (saksnummer == null) {
            var opprettSak = new OpprettSakDto(journalpostId, behandlingstemaOffisiellKode, aktørId.value());
            saksnummer = fordelKlient.fagsakOpprett(opprettSak);
        }

        journalpostKlient.knyttSakTilJournalpost(journalpostId, saksnummer);
        fordelKlient.fagsakKnyttJournalpost(new JournalpostKnyttningDto(saksnummer.value(), journalpostId));

        var journalpostMottak = new JournalpostMottakDto(
                saksnummer.value(),
                journalpostId,
                behandlingstemaOffisiellKode,
                dokumentTypeIdOffisiellKode,
                mottattDato.atStartOfDay(),
                xml);
        journalpostMottak.setForsendelseId(UUID.randomUUID());
        journalpostMottak.setDokumentKategoriOffisiellKode(dokumentKategori);

        if (dokumentTypeIdOffisiellKode.equalsIgnoreCase(DokumenttypeId.INNTEKTSMELDING.getKode())) {
            journalpostMottak.setEksternReferanseId(lagUnikEksternReferanseId());
        }

        fordelKlient.journalpost(journalpostMottak);
    }

    private static String lagUnikEksternReferanseId() {
        inkrementForEksternReferanse++;
        return "AR" + String.format("%08d", inkrementForEksternReferanse);
    }

    /* SAF */
    public byte[] hentJournalførtDokument(String dokumentId, String variantFormat) {
        return safKlient.hentDokumenter(null, dokumentId, variantFormat);
    }
}
