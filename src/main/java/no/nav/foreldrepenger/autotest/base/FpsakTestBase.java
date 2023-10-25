package no.nav.foreldrepenger.autotest.base;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;

import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Beslutter;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Klagebehandler;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Overstyrer;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Saksbehandler;
import no.nav.foreldrepenger.autotest.util.log.LoggFormater;

// TODO: Fiks opp i testbasene
public abstract class FpsakTestBase {

    /*
     * Aktører
     */
    protected Saksbehandler saksbehandler;
    protected Overstyrer overstyrer;
    protected Beslutter beslutter;
    protected Klagebehandler klagebehandler;


    @BeforeEach
    public void setUp() {
        saksbehandler = new Saksbehandler();
        overstyrer = new Overstyrer();
        beslutter = new Beslutter();
        klagebehandler = new Klagebehandler();
        LoggFormater.leggTilKjørendeTestCaseILogger();
    }

    // Hvis perioden som overføres er IKKE i samme måned som dagens dato ELLER
    // Hvis perioden som overføres er i samme måned som dagens dato OG dagens dato er ETTER utbetalingsdagen
    // (20. i alle måneder) så skal det resultere i negativ simulering.
    protected Boolean forventerNegativSimuleringForBehandling(LocalDate førsteAvslagsdag) {
        var iDag = LocalDate.now();
        if (førsteAvslagsdag.getMonthValue() > iDag.getMonthValue() || førsteAvslagsdag.getYear() > iDag.getYear()) {
            return false;
        }
        return førsteAvslagsdag.getMonth() != iDag.getMonth() || iDag.getDayOfMonth() >= 20;
    }
}
