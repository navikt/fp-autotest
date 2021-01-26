package no.nav.foreldrepenger.autotest.klienter.fprisk;

import java.net.URI;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.util.rest.AbstractJerseyOpenAMRestKlient;

public abstract class FpriskJerseyKlient extends AbstractJerseyOpenAMRestKlient {

    protected URI base = BaseUriProvider.FPRISK_BASE;

    public FpriskJerseyKlient() {
        super();
    }
}
