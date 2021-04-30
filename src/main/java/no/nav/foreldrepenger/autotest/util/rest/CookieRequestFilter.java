package no.nav.foreldrepenger.autotest.util.rest;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;

public class CookieRequestFilter implements ClientRequestFilter {

    private final Cookie cookie;

    public CookieRequestFilter(Cookie cookie) {
        this.cookie = cookie;
    }

    @Override
    public void filter(ClientRequestContext ctx) {
        ctx.getHeaders().add(HttpHeaders.COOKIE, cookie);
    }

}
