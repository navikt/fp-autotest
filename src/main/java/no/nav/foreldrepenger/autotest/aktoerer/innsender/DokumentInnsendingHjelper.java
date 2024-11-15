package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import static no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkAktør.ARBEIDSGIVER;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingFpsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkFpsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkTyper;
import no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.JournalforingKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;

abstract class DokumentInnsendingHjelper implements Innsender {

    protected final FagsakKlient fagsakKlient;
    protected final BehandlingFpsakKlient behandlingerKlient;
    protected final HistorikkFpsakKlient historikkKlient;
    protected final JournalforingKlient journalpostKlient;

    protected DokumentInnsendingHjelper() {
        this(SaksbehandlerRolle.SAKSBEHANDLER);
    }

    protected DokumentInnsendingHjelper(SaksbehandlerRolle saksbehandlerRolle) {
            fagsakKlient = new FagsakKlient(saksbehandlerRolle);
            behandlingerKlient = new BehandlingFpsakKlient(saksbehandlerRolle);
            historikkKlient = new HistorikkFpsakKlient(saksbehandlerRolle);
            journalpostKlient = new JournalforingKlient();
    }

    /**
     * Hvis inntektsmeldingen er sendt inn før søknad, venter vi på oppretting av fagsak.
     * Hvis inntektsmeldingen er sendt inn mens det foreligger en sak, venter vi på at saken oppdateres med VEDLEGG_MOTTATT
     */
    protected Saksnummer ventTilAlleInntekstmeldingeneErMottatt(Fødselsnummer fnr, Saksnummer saksnummer, Integer antallNyeInntektsmeldinger, Integer antallGamleInntekstmeldinger) {
        if (saksnummer != null) {
            final var saksnummerTemp = saksnummer;
            var forventetAntallInnteksmeldinger = antallGamleInntekstmeldinger + antallNyeInntektsmeldinger;
            var failReason = String.format(
                    "Forventet at det ble mottatt %d ny(e) inntektsmelding(er), men det ble " + "bare mottatt %d på saksnummer %s",
                    antallNyeInntektsmeldinger, (antallInntektsmeldingerMottattPåSak(saksnummerTemp) - antallGamleInntekstmeldinger),
                    saksnummer.value());
            Vent.på(() -> antallInntektsmeldingerMottattPåSak(saksnummerTemp) == forventetAntallInnteksmeldinger, () -> failReason,
                    35);
        } else {
            saksnummer = ventTilFagsakErOpprettetPåFnr(fnr);
        }
        LOG.info("Inntektsmeldingene er sendt inn og mottatt!");
        return saksnummer;
    }

    protected Integer antallInntektsmeldingerMottattPåSak(Saksnummer saksnummer) {
        if (saksnummer == null) {
            return 0;
        }
        return (int) historikkKlient.hentHistorikk(saksnummer).stream()
                .filter(h -> h.erAvTypen(ARBEIDSGIVER, HistorikkTyper.VEDLEGG_MOTTATT))
                .count();
    }

    /**
     * CASE 1: Før innsending var det ingen fagsaker på personen. Trenger bare vente på at fagsak og behandling opprettes
     * CASE 2: Det eksistrer allerede en eller flere fagsaker. Venter til det er opprettet historikkinnslag på en eksistrende eller en ny fagsak.
     */
    protected Saksnummer ventTilFagsakOgBehandlingErOpprettet(Fødselsnummer fnr, LocalDateTime skjæringsTidspunktForNyBehandling,
                                                              int antallEksistrendeFagsakerPåSøker) {
        LOG.info("Venter på at det opprettes fagsak og/eller ny(e) behandling(er) på eksisterende fagsak...");

        // CASE 1: Det eksistere ingen fagsaker på søker. Venter til det er opprettet og en behandling er tilgjenglig
        if (antallEksistrendeFagsakerPåSøker == 0) {
            return ventTilFagsakErOpprettetPåFnr(fnr);
        }

        // CASE 2: Det finnes allerede fagsak(er) på søker – venter til oppdatering på en av disse, eller oppretting av ny fagsak
        var skjæringstidpunktRundetNed = skjæringsTidspunktForNyBehandling.truncatedTo(ChronoUnit.SECONDS); // Fpsak sender ikke med nanosekunder, men runder ned til nærmeste sekund. Vi gjør det samme!
        return Vent.på(() -> {
            for (var fagsak : fagsakKlient.søk(fnr)) {
                var saksnummer = fagsak.saksnummer();
                for (var h : historikkinnslagEtterSkjæringstidspunkt(saksnummer, skjæringstidpunktRundetNed)) {
                    if (h.erSøknadMottatt()) {
                        return saksnummer;
                    }
                }
            }
            return null;
        }, "Det er hverken opprettet en ny fagsak eller oppdatert en eksistrende fagsak etter innsending. "
                + "Noe har gått galt ved innsending av søknad/inntektsmelding på søker med fnr " + fnr.value());
    }

    private Saksnummer ventTilFagsakErOpprettetPåFnr(Fødselsnummer fnr) {
        LOG.debug("Det finnes ingen fagsaker på gitt fødselsnummer. Venter til det er opprettet fagsak med behandling.");
        return Vent.på(() -> {
            var fagsaker = fagsakKlient.søk(fnr);

            // Det er ikke opprettet noen fagsaker på søker enda
            if (fagsaker == null || fagsaker.isEmpty()) {
                return null;
            }

            // Antall fagsaker har økt, og innsending er dermed prosessert
            var tempSaksnummer = fagsaker.stream()
                    .max(Comparator.comparingInt(f -> Integer.parseInt(f.saksnummer().value())))
                    .map(Fagsak::saksnummer)
                    .orElseThrow();

            // Venter til det er blitt opprettet en behandling på den nye fagsaken
            if (!behandlingerKlient.alle(tempSaksnummer).isEmpty()) {
                return tempSaksnummer;
            }
            return null;
        }, "Ingen fagsak opprettet på sak");
    }

    private List<HistorikkInnslag> historikkinnslagEtterSkjæringstidspunkt(Saksnummer saksnummer, LocalDateTime skjæringstidpunktRundetNed) {
        return historikkKlient.hentHistorikk(saksnummer).stream()
                .filter(h -> h.opprettetTidspunkt().isEqual(skjæringstidpunktRundetNed) ||  h.opprettetTidspunkt().isAfter(skjæringstidpunktRundetNed))
                .toList();
    }



    protected int antallEksistrendeFagsakerPåSøker(Fødselsnummer fnr) {
        return fagsakKlient.søk(fnr).size();
    }

}
