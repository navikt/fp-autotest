package no.nav.foreldrepenger.autotest.klienter.fprisk;

import java.net.URI;

import javax.ws.rs.client.ClientRequestFilter;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.util.rest.AbstractJerseyRestKlient;

public abstract class FpriskJerseyKlient extends AbstractJerseyRestKlient {

    protected URI base = BaseUriProvider.FPRISK_BASE;

    public FpriskJerseyKlient(ClientRequestFilter filter) {
        super(filter);
    }
}
