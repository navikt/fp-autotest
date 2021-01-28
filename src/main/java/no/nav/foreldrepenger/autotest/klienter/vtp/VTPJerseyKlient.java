package no.nav.foreldrepenger.autotest.klienter.vtp;

import java.net.URI;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.util.rest.AbstractJerseyRestKlient;

public abstract class VTPJerseyKlient extends AbstractJerseyRestKlient {

    protected URI base = BaseUriProvider.VTP_BASE;

    public VTPJerseyKlient() {
        super();
    }
}