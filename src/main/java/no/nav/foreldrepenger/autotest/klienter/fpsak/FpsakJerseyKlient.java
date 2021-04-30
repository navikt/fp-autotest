package no.nav.foreldrepenger.autotest.klienter.fpsak;

import java.net.URI;

import jakarta.ws.rs.client.ClientRequestFilter;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.util.rest.AbstractJerseyRestKlient;

public abstract class FpsakJerseyKlient extends AbstractJerseyRestKlient {

    protected URI base = BaseUriProvider.FPSAK_BASE;

    public FpsakJerseyKlient(ClientRequestFilter filter) {
        super(filter);
    }
}
