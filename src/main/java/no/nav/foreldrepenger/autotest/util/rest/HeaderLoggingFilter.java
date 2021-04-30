package no.nav.foreldrepenger.autotest.util.rest;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;

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
