package no.nav.foreldrepenger.autotest.klienter.fptilbake;

import java.net.URI;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.util.rest.AbstractJerseyOpenAMRestKlient;

public abstract class FptilbakeJerseyKlient extends AbstractJerseyOpenAMRestKlient {

    protected URI base = BaseUriProvider.FPTILBAKE_BASE;

    public FptilbakeJerseyKlient() {
        super();
    }
}
