package no.nav.foreldrepenger.autotest.util.rest;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;

/** Denne klassen lager en singleton instance per thread */
public class OpenAmRequestFilter implements ClientRequestFilter {

    private static final ThreadLocal<OpenAmRequestFilter> _threadLocal = ThreadLocal.withInitial(OpenAmRequestFilter::new);

    private OpenAmRequestFilter() {
    }

    public static OpenAmRequestFilter getInstance() {
        return _threadLocal.get();
    }

    private Cookie cookie;

    @Override
    public void filter(ClientRequestContext ctx) {
        ctx.getHeaders().add(HttpHeaders.COOKIE, cookie);
    }

    public void leggTilClientCookie(Cookie cookie) {
        this.cookie = cookie;
    }
}
