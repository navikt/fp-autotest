package no.nav.foreldrepenger.autotest.teknisk.token;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.AzureTokenProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;

public class HentTokenTest {
    private static final Logger LOG = LoggerFactory.getLogger(HentTokenTest.class);

    @Test
    void hentTokenForRolleSaksbehandler() {
        LOG.info(AzureTokenProvider.azureOboToken(SaksbehandlerRolle.SAKSBEHANDLER));
    }
}
