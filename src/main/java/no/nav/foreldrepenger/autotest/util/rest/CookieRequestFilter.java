package no.nav.foreldrepenger.autotest.util.rest;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;

public class CookieRequestFilter implements ClientRequestFilter {

    private Cookie cookie;

    @Override
    public void filter(ClientRequestContext ctx) {
        ctx.getHeaders().add(HttpHeaders.COOKIE, cookie);
    }

    public void leggTilClientCookie(Cookie cookie) {
        this.cookie = cookie;
    }
}
