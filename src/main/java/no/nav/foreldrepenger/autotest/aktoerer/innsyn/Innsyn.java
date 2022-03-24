package no.nav.foreldrepenger.autotest.aktoerer.innsyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.InnsynKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.tokenx.TokenXHenterKlient;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;

public class Innsyn {
    private static final Logger LOG = LoggerFactory.getLogger(Innsyn.class);
    private final InnsynKlient innsynKlient;
    private final TokenXHenterKlient tokenXHenterKlient;  // TODO: Bruk en felles klient ...
    private final Fødselsnummer fnr;

    public Innsyn(Fødselsnummer fnr) {
        innsynKlient = new InnsynKlient();
        tokenXHenterKlient = new TokenXHenterKlient();
        this.fnr = fnr;
    }

    public Saker hentSaker() {
        LOG.info("Henter saker på bruker {}", fnr.value());
        var token = tokenXHenterKlient.hentAccessTokenForBruker(fnr);
        return innsynKlient.hentSaker(token);
    }

}
