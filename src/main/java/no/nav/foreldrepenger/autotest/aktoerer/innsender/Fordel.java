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

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.FordelKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.EndringssøknadForeldrepengerDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.SøknadDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.VedleggDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.ettersendelse.YtelseType;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.autotest.util.ReadFileFromClassPathHelper;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.common.domain.felles.DokumentType;
import no.nav.foreldrepenger.common.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.common.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V1SvangerskapspengerDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3EngangsstønadDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.xml.InntektsmeldingXmlMapper;
import no.nav.foreldrepenger.generator.soknad.mapper.SøknadMapper;
import no.nav.foreldrepenger.kontrakter.fordel.JournalpostKnyttningDto;
import no.nav.foreldrepenger.kontrakter.fordel.JournalpostMottakDto;
import no.nav.foreldrepenger.kontrakter.fordel.OpprettSakDto;
import no.nav.foreldrepenger.vtp.kontrakter.PersonhendelseDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.DokumentModell;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.DokumentVariantInnhold;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Arkivfiltype;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.BehandlingsTema;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumentTilknyttetJournalpost;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Dokumentkategori;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Variantformat;

public class Fordel extends DokumentInnsendingHjelper {

    private static int inkrementForEksternReferanse = 0;

    private final FordelKlient fordelKlient;

    public Fordel(SaksbehandlerRolle saksbehandlerRolle) {
        super(saksbehandlerRolle);
        fordelKlient = new FordelKlient(saksbehandlerRolle);
    }

    /*
     * Sender inn søkand og returnerer saksinformasjon
     */
    @Override
    public Saksnummer sendInnSøknad(SøknadDto søknad, AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer) {
        return sendInnSøknad(SøknadMapper.tilSøknad(søknad, søknad.mottattdato()), aktørId, fnr, aktørIdAnnenpart, saksnummer);
    }

    @Override
    public Saksnummer sendInnSøknad(EndringssøknadForeldrepengerDto søknad, AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer) {
        return sendInnSøknad(SøknadMapper.tilSøknad(søknad, søknad.mottattdato()), aktørId, fnr, aktørIdAnnenpart, FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
    }

    @Override
    @Step("Sender inn papirsøknad foreldrepenger")
    public Saksnummer sendInnPapirsøknadForeldrepenger(AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart) {
        return sendInnSøknad(null, aktørId, fnr, aktørIdAnnenpart, SØKNAD_FORELDREPENGER_FØDSEL, null);
    }

    @Override
    @Step("Sender inn endringssøknad på papir")
    public Saksnummer sendInnPapirsøknadEEndringForeldrepenger(AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer) {
        return sendInnSøknad(null, aktørId, fnr, aktørIdAnnenpart, FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
    }

    @Override
    @Step("Sender inn papirsøknad engangsstønad")
    public Saksnummer sendInnPapirsøknadEngangsstønad(AktørId aktørId, Fødselsnummer fnr) {
        return sendInnSøknad(null, aktørId, fnr, null, SØKNAD_ENGANGSSTØNAD_FØDSEL, null);
    }

    private Saksnummer sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer) {
        var dokumenttypeId = dokumentTypeFraRelasjon(søknad);
        populerVedleggMedInnhold(søknad);
        return sendInnSøknad(søknad, aktørId, fnr, aktørIdAnnenpart, dokumenttypeId, saksnummer);
    }

    private static void populerVedleggMedInnhold(Søknad søknad) {
        søknad.getVedlegg().stream()
                .filter(v -> v.getMetadata().innsendingsType() == null || InnsendingsType.LASTET_OPP.equals(v.getMetadata().innsendingsType()))
                .forEach(v -> v.setInnhold(ReadFileFromClassPathHelper.readFileBytes("dummy.pdf")));
    }

    @Step("Sender inn søknad [{dokumenttypeId}]")
    public Saksnummer sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart,
                                    DokumenttypeId dokumenttypeId, Saksnummer saksnummer) {
        String xml = "";
        LocalDate mottattDato;
        if (null != søknad) {
            xml = tilSøknadXML(søknad, aktørId, aktørIdAnnenpart);
            mottattDato = søknad.getMottattdato();
        } else {
            mottattDato = LocalDate.now();
        }
        debugSenderInnDokument("Foreldrepengesøknad", xml);

        var journalpostModell = lagJournalpostStrukturertDokument(xml, fnr.value(), dokumenttypeId);
        if (søknad != null) {
            for (var vedlegg : søknad.getVedlegg()) {
                if (vedlegg.getInnsendingsType().equals(InnsendingsType.LASTET_OPP) && vedlegg.getInnhold() != null && vedlegg.getInnhold().length > 0) {
                    journalpostModell.getDokumentModellList().add(dokumentmodellFraVedlegg(vedlegg));
                }
            }
        }
        if (saksnummer != null) {
            journalpostModell.setSakId(saksnummer.value());
        }

        var skjæringsTidspunktForNyBehandling  = LocalDateTime.now().minusSeconds(1); // Legger inn slack på 1 sekund
        var antallEksistrendeFagsakerPåSøker = antallEksistrendeFagsakerPåSøker(fnr);
        var journalpostId = journalpostKlient.journalførR(journalpostModell).journalpostId();
        knyttJournalpostTilFagsak(xml, mottattDato, journalpostId, finnBehandlingstemaKode(dokumenttypeId).getOffisiellKode(), dokumenttypeId.getKode(), "SOK", aktørId, saksnummer);
        return ventTilFagsakOgBehandlingErOpprettet(fnr, skjæringsTidspunktForNyBehandling, antallEksistrendeFagsakerPåSøker);
    }

    private String tilSøknadXML(Søknad søknad, AktørId aktørId, AktørId aktørIdAnnenpart) {
        DomainMapper mapper;
        var ytelse = søknad.getYtelse();
        if (ytelse instanceof Foreldrepenger) {
            mapper = new V3ForeldrepengerDomainMapper(fnr -> aktørIdAnnenpart);
        } else if (ytelse instanceof Svangerskapspenger) {
            mapper = new V1SvangerskapspengerDomainMapper();
        } else if (ytelse instanceof Engangsstønad) {
            mapper = new V3EngangsstønadDomainMapper(null);
        } else {
            throw new IllegalArgumentException("Søknad har ytelse er hverken fp, svp eller es... noe er feil!s");
        }

        if (søknad instanceof Endringssøknad endringssøknad) {
            return mapper.tilXML(endringssøknad, aktørId, null);
        }
        return mapper.tilXML(søknad, aktørId, null);
    }

    private static BehandlingsTema finnBehandlingstemaKode(DokumenttypeId dokumenttypeId) {
        if (dokumenttypeId == DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL) {
            return BehandlingsTema.FORELDREPENGER_FØDSEL;
        }
        if (dokumenttypeId == DokumenttypeId.SØKNAD_FORELDREPENGER_ADOPSJON) {
            return BehandlingsTema.FORELDREPENGER_ADOPSJON;
        }
        if (dokumenttypeId == DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL) {
            return BehandlingsTema.ENGANGSSTØNAD_FØDSEL;
        }
        if (dokumenttypeId == DokumenttypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON) {
            return BehandlingsTema.ENGANGSSTØNAD_ADOPSJON;
        }
        if (dokumenttypeId == DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD) {
            return BehandlingsTema.FORELDREPENGER;
        }
        if (dokumenttypeId == DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER){
            return BehandlingsTema.SVANGERSKAPSPENGER;
        }
        throw new RuntimeException("Kunne ikke matche på dokumenttype.");
    }

    /*
     * Sender inn inntektsmelding og returnerer saksnummer
     */
    @Override
    @Step("Sender inn IM for fnr {fnr}")
    public Saksnummer sendInnInntektsmelding(Inntektsmelding inntektsmelding, AktørId aktørId, Fødselsnummer fnr, Saksnummer gammeltSaksnummer) {
        return sendInnInntektsmelding(List.of(inntektsmelding), aktørId, fnr, gammeltSaksnummer);
    }

    @Override
    public Saksnummer sendInnInntektsmelding(List<Inntektsmelding> inntektsmeldinger, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer) {
        var antallEksisterendeInntekstmeldinger = antallInntektsmeldingerMottattPåSak(saksnummer);
        journalførKnyttIM(inntektsmeldinger, aktørId, fnr, saksnummer);
        return ventTilAlleInntekstmeldingeneErMottatt(fnr, saksnummer, inntektsmeldinger.size(), antallEksisterendeInntekstmeldinger);
    }

    private void journalførKnyttIM(List<Inntektsmelding> inntektsmeldinger, AktørId aktørId, Fødselsnummer fnr, Saksnummer eksisterendeSaksnummer) {
        LOG.info("Sender inn {} IM(er) for søker {} ...", inntektsmeldinger.size(), fnr.value());
        for (var inntektsmelding : inntektsmeldinger) {
            var xml = InntektsmeldingXmlMapper.opprettInntektsmeldingXML(inntektsmelding);
            debugSenderInnDokument("Inntektsmelding", xml);
            var journalpostModell = lagJournalpostStrukturertDokument(xml, fnr.value(), DokumenttypeId.INNTEKTSMELDING);
            var journalpostId = journalpostKlient.journalfør(journalpostModell).journalpostId();

            var dokumentKategori = Dokumentkategori.IKKE_TOLKBART_SKJEMA.getKode();
            var dokumentTypeIdOffisiellKode = DokumenttypeId.INNTEKTSMELDING.getKode();
            knyttJournalpostTilFagsak(xml, journalpostId,  dokumentTypeIdOffisiellKode, dokumentKategori, aktørId, eksisterendeSaksnummer);
        }
    }

    @Override
    public void sendInnHendelse(PersonhendelseDto personhendelseDto) {
        throw new UnsupportedOperationException("Fpabonnent kjører ikke i fpsak context. Innsending av fødelsehendelse vil da ikke bli plukket opp.");
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

    @Override
    public void ettersendVedlegg(Fødselsnummer fnr, Saksnummer saksnummer, YtelseType ytelseType, VedleggDto vedlegg) {
        throw new IllegalStateException("Ikke implementert! Bruk innsending via selvbetjening!");
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
        return "AR" + String.format("%08d", ++inkrementForEksternReferanse);
    }

    private static DokumentModell dokumentmodellFraVedlegg(Vedlegg vedlegg) {
        DokumentModell dokumentModell = new DokumentModell();
        dokumentModell.setTittel(vedlegg.getDokumentType().getTittel());
        dokumentModell.setDokumentType(tilDokumenttypeId(vedlegg.getDokumentType()));
        dokumentModell.setDokumentTilknyttetJournalpost(DokumentTilknyttetJournalpost.VEDLEGG);
        dokumentModell.getDokumentVariantInnholdListe().add(new DokumentVariantInnhold(
                Arkivfiltype.PDF, Variantformat.ARKIV, vedlegg.getInnhold()
        ));
        dokumentModell.getDokumentVariantInnholdListe().add(new DokumentVariantInnhold(
                Arkivfiltype.PDF, Variantformat.ORIGINAL, vedlegg.getInnhold()
        ));
        return dokumentModell;
    }

    // DokumenttypeId.navn her i VTP må matche navn i fpsak sin DokumentTypeId tittel, ellers blir det vedleggtypen av typen ANNET
    private static DokumenttypeId tilDokumenttypeId(DokumentType dokumentType) {
        return switch (dokumentType) {
            case I000007 -> DokumenttypeId.INNTEKTSOPPLYSNING_SELVSTENDIG;
            case I000026 -> DokumenttypeId.INNTEKTSOPPLYSNINGER;
            case I000023 -> DokumenttypeId.LEGEERKLÆRING;
            case I000032 -> DokumenttypeId.RESULTATREGNSKAP;
            case I000037 -> DokumenttypeId.DOK_INNLEGGELSE;
            case I000038 -> DokumenttypeId.DOK_MORS_UTDANNING_ARBEID_SYKDOM;
            case I000039 -> DokumenttypeId.DOK_MILITÆR_SIVIL_TJENESTE;
            case I000041 -> DokumenttypeId.DOKUMENTASJON_AV_TERMIN_ELLER_FØDSEL;
            case I000042 -> DokumenttypeId.DOKUMENTASJON_AV_OMSORGSOVERTAKELSE;
            case I000044 -> DokumenttypeId.DOK_ETTERLØNN;
            case I000045 -> DokumenttypeId.BESKRIVELSE_FUNKSJONSNEDSETTELSE;
            case I000061 -> DokumenttypeId.BEKREFTELSE_FRA_STUDIESTED;
            case I000062 -> DokumenttypeId.BEKREFTELSE_VENTET_FØDSELSDATO;
            case I000063 -> DokumenttypeId.FØDSELSATTEST;
            case I000065 -> DokumenttypeId.BEKREFTELSE_FRA_ARBEIDSGIVER;
            case I000109 -> DokumenttypeId.SKJEMA_TILRETTELEGGING_OMPLASSERING;
            case I000110 -> DokumenttypeId.DOKUMENTASJON_ALENEOMSORG;
            case I000111 -> DokumenttypeId.BEGRUNNELSE_SØKNAD_ETTERSKUDD;
            case I000112 -> DokumenttypeId.DOKUMENTASJON_INTRODUKSJONSPROGRAM;
            case I000116 -> DokumenttypeId.DOKUMENTASJON_FORSVARSTJENESTE;
            case I000117 -> DokumenttypeId.DOKUMENTASJON_NAVTILTAK;
            case I000118 -> DokumenttypeId.SEN_SØKNAD;
            case I000120 -> DokumenttypeId.MOR_INNLAGT;
            case I000121 -> DokumenttypeId.MOR_SYK;
            case I000122 -> DokumenttypeId.FAR_INNLAGT;
            case I000123 -> DokumenttypeId.FAR_SYK;
            case I000124 -> DokumenttypeId.BARN_INNLAGT;
            case I000130 -> DokumenttypeId.MOR_ARBEID_STUDIE;
            case I000131 -> DokumenttypeId.MOR_STUDIE;
            case I000132 -> DokumenttypeId.MOR_ARBEID;
            case I000133 -> DokumenttypeId.MOR_KVALPROG;
            case I000140 -> DokumenttypeId.SKATTEMELDING;
            case I000141 -> DokumenttypeId.TERMINBEKREFTELSE;
            case I000143 -> DokumenttypeId.OPPHOLD;
            case I000144 -> DokumenttypeId.REISE;
            case I000145 -> DokumenttypeId.OPPFØLGING;
            case I000146 -> DokumenttypeId.DOKUMENTASJON_INNTEKT;
            default -> throw new IllegalArgumentException("LEGG INN KODE HVIS DU SKAL SENDE INN NY: " + dokumentType);
        };
    }
}
