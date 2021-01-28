package no.nav.foreldrepenger.autotest.aktoerer.fpsoknad_mottak;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.MottakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.oauth2.Oauth2JerseyKlient;
import no.nav.foreldrepenger.autotest.søknad.modell.Søknad;

public class Selvbetjening extends Aktoer {

    private static final Logger LOG = LoggerFactory.getLogger(Selvbetjening.class);

    private final MottakJerseyKlient mottakKlient;
    private final Oauth2JerseyKlient oauth2Klient;

    public Selvbetjening() {
        mottakKlient = new MottakJerseyKlient();
        oauth2Klient = new Oauth2JerseyKlient();
    }

    public Long sendInnSøknad(String fnr, Søknad søknad) {
        var token = oauth2Klient.hentAccessTokenForBruker(fnr);
        var kvittering = mottakKlient.sendSøknad(token, søknad);
        assertTrue(kvittering.erVellykket(), "Innsending av søknad til fpsoknad-mottak feilet!");
        LOG.info("Sendt inn søknad til mottak og sak er opprettet på saksnummer: {}", kvittering.getSaksNr());
        return Long.valueOf(kvittering.getSaksNr());
    }


}
