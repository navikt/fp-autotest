package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import static no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkAktør.ARBEIDSGIVER;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkAktør.SØKER;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType.BEH_STARTET;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType.VEDLEGG_MOTTATT;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingerJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.JournalforingJerseyKlient;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public abstract class DokumentVenter extends Aktoer implements Innsender {

    protected final BehandlingerJerseyKlient behandlingerKlient;
    protected final FagsakJerseyKlient fagsakKlient;
    protected final HistorikkJerseyKlient historikkKlient;

    protected final JournalforingJerseyKlient journalpostKlient;

    protected DokumentVenter(Rolle rolle) {
        super(rolle);
        fagsakKlient = new FagsakJerseyKlient(cookieRequestFilter);
        behandlingerKlient = new BehandlingerJerseyKlient(cookieRequestFilter);
        historikkKlient = new HistorikkJerseyKlient(cookieRequestFilter);
        journalpostKlient = new JournalforingJerseyKlient();
    }

    /**
     * Hvis inntektsmeldingen er sendt inn før søknad, venter vi på oppretting av fagsak.
     * Hvis inntektsmeldingen er sendt inn mens det foreligger en sak, venter vi på at saken oppdateres med VEDLEGG_MOTTATT
     */
    protected Saksnummer ventTilAlleInntekstmeldingeneErMottatt(Fødselsnummer fnr, Saksnummer saksnummer, Integer antallNyeInntektsmeldinger, Integer antallGamleInntekstmeldinger) {
        if (saksnummer != null) {
            final var saksnummerTemp = saksnummer;
            var forventetAntallInnteksmeldinger = antallGamleInntekstmeldinger + antallNyeInntektsmeldinger;
            Vent.til(() -> antallInntektsmeldingerMottattPåSak(saksnummerTemp) == forventetAntallInnteksmeldinger,
                    60, String.format("Forventet at det ble mottatt %d ny(e) inntektsmelding(er), men det ble "
                            + "bare mottatt %d på saksnummer %s", antallNyeInntektsmeldinger,
                            (antallInntektsmeldingerMottattPåSak(saksnummerTemp) - antallGamleInntekstmeldinger),
                            saksnummer.value()));
        } else {
            saksnummer = ventTilFagsakOgBehandlingErOpprettet(fnr);
        }
        LOG.info("Inntektsmeldingene er sendt inn og mottatt!");
        return saksnummer;
    }

    protected Integer antallInntektsmeldingerMottattPåSak(Saksnummer saksnummer) {
        if (saksnummer == null) {
            return 0;
        }
        return (int) historikkKlient.hentHistorikk(saksnummer).stream()
                .filter(h -> VEDLEGG_MOTTATT.equals(h.type()))
                .filter(h -> ARBEIDSGIVER.equals(h.aktoer()))
                .count();
    }

    private Saksnummer ventTilFagsakOgBehandlingErOpprettet(Fødselsnummer fnr) {
        return ventTilFagsakOgBehandlingErOpprettet(fnr, null, 0);
    }

    /**
     * CASE 1: Før innsending var det ingen fagsaker på personen. Trenger bare vente på at fagsak og behandling opprettes
     * CASE 2: Det eksistrer allerede en eller flere fagsaker. Venter til det er opprettet historikkinnslag på en eksistrende eller en ny fagsak.
     *         - Innsending av søknad venter på BEH_STARTET
     *         - Innsending av IM venter på VEDLEGG_MOTTATT
     */
    protected Saksnummer ventTilFagsakOgBehandlingErOpprettet(Fødselsnummer fnr, LocalDateTime skjæringsTidspunktForNyBehandling,
                                                              int antallEksistrendeFagsakerPåSøker) {
        LOG.info("Venter på at det opprettes fagsak og/eller ny(e) behandling(er) på eksisterende fagsak...");
        var saksnummer = new AtomicReference<Saksnummer>(); // TODO: Ikke bruk AtomicReference.

        // CASE 1
        if (antallEksistrendeFagsakerPåSøker == 0) {
            Vent.til(() -> {
                var fagsaker = fagsakKlient.søk(fnr);

                // Det er ikke opprettet noen fagsaker på søker enda
                if (fagsaker == null || fagsaker.isEmpty()) {
                    return false;
                }

                // Antall fagsaker har økt, og innsending er dermed prosessert
                var tempSaksnummer = fagsaker.stream()
                        .max(Comparator.comparingInt(f -> Integer.parseInt(f.saksnummer().value())))
                        .map(Fagsak::saksnummer)
                        .orElseThrow();

                // Venter til det er blitt opprettet en behandling på den nye fagsaken
                if (!behandlingerKlient.alle(tempSaksnummer).isEmpty()){
                    saksnummer.set(tempSaksnummer);
                    return true;
                }

                return false;
            }, 30, "Ingen fagsak opprettet på sak");
            return saksnummer.get();
        }

        // CASE 2
        var skjæringstidpunktRundetNed = skjæringsTidspunktForNyBehandling.truncatedTo(ChronoUnit.SECONDS); // Fpsak sender ikke med nanosekunder, men runder ned til nærmeste sekund. Vi gjør det samme!
        Vent.til(() -> {
            for (var fagsak : fagsakKlient.søk(fnr)) {
                if (historikkinnslagEtterSTP(skjæringstidpunktRundetNed, fagsak).anyMatch(h -> erSøknadMottatt(h) || erInntektsmeldingMottatt(h))) {
                    saksnummer.set(fagsak.saksnummer());
                    return true;
                }
            }
            return false;
        }, 30, "Det er hverken opprettet en ny fagsak eller oppdatert en eksistrende fagsak etter innsending. "
                + "Noe har gått galt ved innsending av søknad/inntektsmelding på søker med fnr " + fnr.value());
        return saksnummer.get();
    }

    private Stream<HistorikkInnslag> historikkinnslagEtterSTP(LocalDateTime skjæringstidpunktRundetNed, Fagsak fagsak) {
        return historikkKlient.hentHistorikk(fagsak.saksnummer()).stream()
                .filter(h -> h.opprettetTidspunkt().isEqual(skjæringstidpunktRundetNed) ||
                            h.opprettetTidspunkt().isAfter(skjæringstidpunktRundetNed));
    }

    private boolean erSøknadMottatt(HistorikkInnslag h) {
        return BEH_STARTET.equals(h.type()) && SØKER.equals(h.aktoer());
    }

    private boolean erInntektsmeldingMottatt(HistorikkInnslag h) {
        return VEDLEGG_MOTTATT.equals(h.type()) && ARBEIDSGIVER.equals(h.aktoer());
    }

    protected int antallEksistrendeFagsakerPåSøker(Fødselsnummer fnr) {
        return fagsakKlient.søk(fnr).size();
    }

}
