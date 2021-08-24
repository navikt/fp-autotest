package no.nav.foreldrepenger.autotest.util.rest;

import static no.nav.vedtak.log.mdc.MDCOperations.MDC_CONSUMER_ID;

import java.io.IOException;

import org.slf4j.MDC;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

@Priority(999998)
class CallIdRequestFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext ctx) throws IOException {
        ctx.getHeaders().add("nav-call-id", MDC.get(MDC_CONSUMER_ID));
        ctx.getHeaders().add("Nav-CallId", MDC.get(MDC_CONSUMER_ID));
    }
}
