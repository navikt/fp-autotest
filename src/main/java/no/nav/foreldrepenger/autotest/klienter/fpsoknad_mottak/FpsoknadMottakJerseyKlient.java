package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak;

import java.net.URI;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.util.rest.AbstractJerseyRestKlient;

public abstract class FpsoknadMottakJerseyKlient extends AbstractJerseyRestKlient {

    public static final String OIDC_AUTH_HEADER_PREFIX = "Bearer ";

    protected URI base = BaseUriProvider.FPSOKNAD_MOTTAK_BASE;

    protected FpsoknadMottakJerseyKlient() {
        super();
    }
}
