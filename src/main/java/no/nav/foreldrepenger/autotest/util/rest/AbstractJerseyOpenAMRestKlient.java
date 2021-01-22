package no.nav.foreldrepenger.autotest.util.rest;


public abstract class AbstractJerseyOpenAMRestKlient extends AbstractJerseyRestKlient {

    private static final OpenAmRequestFilter OPEN_AM_REQUEST_FILTER = OpenAmRequestFilter.getInstance();

    public AbstractJerseyOpenAMRestKlient() {
        super(OPEN_AM_REQUEST_FILTER);
    }
}
