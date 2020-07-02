package no.nav.foreldrepenger.autotest.internal.konfigurasjon;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.base.TestBase;
import no.nav.foreldrepenger.autotest.util.konfigurasjon.MiljoKonfigurasjon;

@Execution(ExecutionMode.CONCURRENT)
@Tag("internal")
public class MiljoKonfigurasjonTest extends TestBase {

    @Test
    public void testHentMiljø() {
        verifiser(MiljoKonfigurasjon.hentMiljø() != null, "Kunne ikke hente miljø");
    }

    @Test
    public void testHentApiRoot() {
        verifiser(MiljoKonfigurasjon.getRouteApi() != null, "Kunne ikke hente Rest root url for miljøet");
    }
}
