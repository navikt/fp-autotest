package no.nav.foreldrepenger.autotest.aktoerer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.openam.OpenamJerseyKlient;
import no.nav.foreldrepenger.autotest.util.rest.CookieRequestFilter;

public class Aktoer {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected final CookieRequestFilter cookieRequestFilter;
    private final OpenamJerseyKlient openamJerseyKlient;

    public Aktoer(Rolle rolle) {
        openamJerseyKlient = new OpenamJerseyKlient();
        var cookie = openamJerseyKlient.logInnMedRolle(rolle.getKode());
        cookieRequestFilter = new CookieRequestFilter(cookie);
    }

    public enum Rolle {
        SAKSBEHANDLER("saksbeh"),
        SAKSBEHANDLER_KODE_6("saksbeh6"),
        SAKSBEHANDLER_KODE_7("saksbeh7"),
        BESLUTTER("beslut"),
        OVERSTYRER("oversty"),
        KLAGEBEHANDLER("klageb"),
        VEILEDER("veil");

        final String kode;

        Rolle(String kode) {
            this.kode = kode;
        }

        public String getKode() {
            return kode;
        }
    }
}
