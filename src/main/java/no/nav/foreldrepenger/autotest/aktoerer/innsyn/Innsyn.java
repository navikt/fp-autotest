package no.nav.foreldrepenger.autotest.aktoerer.innsyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.InnsynKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto.Saker;
import no.nav.foreldrepenger.autotest.klienter.vtp.oauth2.AzureAdJerseyKlient;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;

public class Innsyn {
    private static final Logger LOG = LoggerFactory.getLogger(Innsyn.class);
    private final InnsynKlient innsynKlient;
    private final AzureAdJerseyKlient oauth2Klient;  // TODO: Bruk en felles klient ...
    private final Fødselsnummer fnr;

    public Innsyn(Fødselsnummer fnr) {
        innsynKlient = new InnsynKlient();
        oauth2Klient = new AzureAdJerseyKlient();
        this.fnr = fnr;
    }

    public Saker hentSaker() {
        LOG.info("Henter saker på bruker {}", fnr.value());
        var token = oauth2Klient.hentAccessTokenForBruker(fnr);
        return innsynKlient.hentSaker(token);
    }

}
