package no.nav.foreldrepenger.autotest.klienter.fptilbake;

import java.net.URI;

import javax.ws.rs.client.ClientRequestFilter;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.util.rest.AbstractJerseyRestKlient;

public abstract class FptilbakeJerseyKlient extends AbstractJerseyRestKlient {

    protected URI base = BaseUriProvider.FPTILBAKE_BASE;

    public FptilbakeJerseyKlient(ClientRequestFilter filter) {
        super(filter);
    }
}
