package no.nav.foreldrepenger.autotest.klienter.fpsak;

import java.net.URI;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.client.ClientRequestFilter;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.util.rest.AbstractJerseyRestKlient;

public abstract class FpsakJerseyKlient extends AbstractJerseyRestKlient {

    protected URI base = BaseUriProvider.FPSAK_BASE;

    protected FpsakJerseyKlient(ObjectMapper mapper, ClientRequestFilter filter) {
        super(mapper, filter);
    }

    protected FpsakJerseyKlient(ClientRequestFilter filter) {
        super(filter);
    }
}
