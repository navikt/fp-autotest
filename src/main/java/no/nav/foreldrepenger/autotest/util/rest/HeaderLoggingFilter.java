package no.nav.foreldrepenger.autotest.util.rest;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Priority(999999)
class HeaderLoggingFilter implements ClientRequestFilter, ClientResponseFilter {

    private static final Logger LOG = LoggerFactory.getLogger(HeaderLoggingFilter.class);


    @Override
    public void filter(ClientRequestContext ctx) throws IOException {
        LOG.trace("Request til URI {}", ctx.getUri());
        ctx.getHeaders().forEach((key, value) -> LOG.trace("{} -> {}", key, value));
    }

    @Override
    public void filter(ClientRequestContext ctx, ClientResponseContext responseContext) throws IOException {
        LOG.trace("Respons fra URI {}", ctx.getUri());
        ctx.getHeaders().forEach((key, value) -> LOG.trace("{} -> {}", key, value));
    }
}
