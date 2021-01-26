package no.nav.foreldrepenger.autotest.util.rest;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;

public class OpenAmRequestFilter implements ClientRequestFilter {

    private static final OpenAmRequestFilter instance = new OpenAmRequestFilter();

    private OpenAmRequestFilter() {
    }

    public static OpenAmRequestFilter getInstance() {
        return instance;
    }

    private Cookie cookie;

    @Override
    public void filter(ClientRequestContext ctx) throws IOException {
        ctx.getHeaders().add(HttpHeaders.COOKIE, cookie);
    }

    public void leggTilClientCookie(Cookie cookie) {
        this.cookie = cookie;
    }
}
