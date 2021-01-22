package no.nav.foreldrepenger.autotest.klienter.fpsak;

import java.net.URI;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.util.rest.AbstractJerseyOpenAMRestKlient;

public abstract class FpsakJerseyKlient extends AbstractJerseyOpenAMRestKlient {

    protected URI base = BaseUriProvider.FPSAK_BASE;

    public FpsakJerseyKlient() {
        super();
    }
}
