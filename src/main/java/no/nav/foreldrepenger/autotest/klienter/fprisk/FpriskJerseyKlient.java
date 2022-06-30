package no.nav.foreldrepenger.autotest.klienter.fprisk;

import static no.nav.foreldrepenger.common.mapper.DefaultJsonMapper.MAPPER;

import java.net.URI;

import jakarta.ws.rs.client.ClientRequestFilter;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.util.rest.AbstractJerseyRestKlient;

public abstract class FpriskJerseyKlient extends AbstractJerseyRestKlient {

    protected URI base = BaseUriProvider.FPRISK_BASE;

    protected FpriskJerseyKlient(ClientRequestFilter filter) {
        super(MAPPER, filter);
    }
}
