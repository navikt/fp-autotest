package no.nav.foreldrepenger.autotest.util;

import org.opentest4j.TestAbortedException;

import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

/**
 * Stopper en test når saksbehandler skal løse et gitt aksjonspunkt, slik at saken blir stående i det lokale miljøet
 * for manuell testing. Slås på med {@code -Dstopp.ved=<aksjonspunktkode>}, for eksempel {@code -Dstopp.ved=5038}.
 * Testen blir markert som avbrutt (aborted), ikke feilet.
 */
public final class StoppVedAksjonspunkt {

    private static final String FRONTEND = "http://localhost:9000/fagsak/";

    private StoppVedAksjonspunkt() {
    }

    public static void sjekk(String aksjonspunktKode, Fagsak fagsak) {
        var stoppVed = System.getProperty("stopp.ved");
        if (stoppVed == null || !stoppVed.equals(aksjonspunktKode)) {
            return;
        }
        var saksnummer = fagsak == null ? "ukjent" : fagsak.saksnummer().value();
        var melding = "STOPPET ved aksjonspunkt " + aksjonspunktKode
                + "\nSaksnummer: " + saksnummer
                + "\nfpsak-frontend: " + FRONTEND + saksnummer + "/";
        System.out.println(melding);
        throw new TestAbortedException(melding);
    }
}
