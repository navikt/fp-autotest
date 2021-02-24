package no.nav.foreldrepenger.autotest.aktoerer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.klienter.vtp.openam.OpenamJerseyKlient;
import no.nav.foreldrepenger.autotest.util.rest.CookieRequestFilter;

public class Aktoer {

    protected static final Logger LOG = LoggerFactory.getLogger(Aktoer.class);

    public final CookieRequestFilter cookieRequestFilter;
    public final OpenamJerseyKlient openamJerseyKlient;

    public Aktoer() {
        openamJerseyKlient = new OpenamJerseyKlient();
        cookieRequestFilter = new CookieRequestFilter();
    }

    public Aktoer(Rolle rolle) {
        this();
        openamJerseyKlient.logInnMedRolle(rolle.getKode(), cookieRequestFilter);
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
