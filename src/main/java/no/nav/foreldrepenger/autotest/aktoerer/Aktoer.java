package no.nav.foreldrepenger.autotest.aktoerer;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.openam.OpenamKlient;

public class Aktoer {

    private static final Logger LOG = LoggerFactory.getLogger(Aktoer.class);

    private static final OpenamKlient openamKlientTest = new OpenamKlient();

    // TODO: Finn en bedre måte å gjøre dette (?)
    public static void loggInn(Rolle rolle) {
        LOG.info("Logger inn med rolle {}", rolle);
        var cookie = openamKlientTest.logInnMedRolle(rolle.getKode());
        JavaHttpKlient.getInstance().cookieManager().getCookieStore()
                .add(URI.create(cookie.getPath()), cookie);
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
