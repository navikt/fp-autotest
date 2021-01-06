package no.nav.foreldrepenger.autotest.aktoerer.inntektsmelding;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.JournalforingKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.kafka.KafkaKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.SafKlient;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
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

public class Inntektsmelding extends Aktoer {

    FagsakKlient fagsakKlient;
    HistorikkKlient historikkKlient;
    JournalforingKlient journalpostKlient;
    KafkaKlient kafkaKlient;
    SafKlient safKlient;

    public Inntektsmelding() {
        fagsakKlient = new FagsakKlient(session);
        historikkKlient = new HistorikkKlient(session);
        journalpostKlient = new JournalforingKlient(session);
        kafkaKlient = new KafkaKlient(session);
        safKlient = new SafKlient(session);
    }

    public void sendInnInnteksmeldingFpfordel(InntektsmeldingBuilder inntektsmelding, String fnr) {
        sendInnInnteksmeldingFpfordel(List.of(inntektsmelding), fnr, null);
    }

    public void sendInnInnteksmeldingFpfordel(List<InntektsmeldingBuilder> inntektsmelding, String fnr) {
        sendInnInnteksmeldingFpfordel(inntektsmelding, fnr, null);
    }

    public void sendInnInnteksmeldingFpfordel(InntektsmeldingBuilder inntektsmelding, String fnr, Long saksnummer) {
        sendInnInnteksmeldingFpfordel(List.of(inntektsmelding), fnr, saksnummer);
    }

    public void sendInnInnteksmeldingFpfordel(List<InntektsmeldingBuilder> inntektsmeldinger, String fnr, Long saksnummer) {
        erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        var antallGamleInntekstmeldinger = hentAntallHistorikkInnslagAvTypenVedleggMottatt(saksnummer);
        journalførInnteksmeldinger(inntektsmeldinger, fnr);
        ventTilInntekstmeldingErMottatt(fnr, saksnummer, inntektsmeldinger.size(), antallGamleInntekstmeldinger);
    }

    private void journalførInnteksmeldinger(List<InntektsmeldingBuilder> inntektsmeldinger, String fnr) {
        for (InntektsmeldingBuilder inntektsmelding : inntektsmeldinger) {
            var xml = inntektsmelding.createInntektesmeldingXML();
            var journalpostModell = lagJournalpostInntektsmelding(xml, fnr, DokumenttypeId.INNTEKTSMELDING);
            journalpostKlient.journalførR(journalpostModell);
        }
    }


    private JournalpostModell lagJournalpostInntektsmelding(String innhold, String fnr, DokumenttypeId dokumenttypeId) {
        JournalpostModell journalpostModell = new JournalpostModell();
        journalpostModell.setTittel("Inntektsmelding");
        journalpostModell.setJournalStatus(Journalstatus.MIDLERTIDIG_JOURNALFØRT);
        journalpostModell.setMottattDato(LocalDateTime.now());
        journalpostModell.setMottakskanal("ALTINN");
        journalpostModell.setArkivtema(Arkivtema.FOR);
        journalpostModell.setAvsenderFnr(fnr);
        journalpostModell.setSakId("");
        journalpostModell.setBruker(new JournalpostBruker(fnr, BrukerType.FNR));
        journalpostModell.setJournalposttype(Journalposttyper.INNGAAENDE_DOKUMENT);

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
        journalpostModell.getDokumentModellList().add(dokumentModell);

        return journalpostModell;
    }

    private Integer hentAntallHistorikkInnslagAvTypenVedleggMottatt(Long saksnummer) {
        if (saksnummer == null) {
            return 0;
        }
        return (int) historikkKlient.hentHistorikk(saksnummer).stream()
                .filter(h -> HistorikkInnslag.VEDLEGG_MOTTATT.getKode().equalsIgnoreCase(h.getTypeKode()))
                .count();
    }

    private void ventTilInntekstmeldingErMottatt(String fnr, Long saksnummer,
                                                 Integer antallNyeInntektsmeldinger,
                                                 Integer antallGamleInntekstmeldinger) {
        if (saksnummer != null) {
            var forventetAntallNyeInnteksmeldinger = antallGamleInntekstmeldinger + antallNyeInntektsmeldinger;
            AtomicReference<Integer> antallIM = new AtomicReference<>(antallGamleInntekstmeldinger);
            Vent.til(() -> {
                antallIM.set(hentAntallHistorikkInnslagAvTypenVedleggMottatt(saksnummer));
                return antallIM.get() == forventetAntallNyeInnteksmeldinger;
            }, 40, String.format("Forventet at det ble mottatt %d ny(e) innteksmeldinge(r), men det ble mottatt %d!",
                    antallNyeInntektsmeldinger, antallIM.get() - antallGamleInntekstmeldinger));
        } else {
            Vent.til(() -> fagsakKlient.søk("" + fnr).size() > 0,
                    40, "Opprettet ikke fagsak for inntektsmelding");
        }
    }
}
