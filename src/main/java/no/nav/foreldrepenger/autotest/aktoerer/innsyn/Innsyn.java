package no.nav.foreldrepenger.autotest.aktoerer.innsyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.InnsynKlient;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;

public class Innsyn {
    private static final Logger LOG = LoggerFactory.getLogger(Innsyn.class);
    private final InnsynKlient innsynKlient;
    private final Fødselsnummer fnr;

    public Innsyn(Fødselsnummer fnr) {
        innsynKlient = new InnsynKlient();
        this.fnr = fnr;
    }

    public Saker hentSaker() {
        LOG.info("Henter saker på bruker {}", fnr.value());
        return innsynKlient.hentSaker(fnr);
    }

}
