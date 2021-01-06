package no.nav.foreldrepenger.autotest.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractHttpSession implements HttpSession {
    private final Logger log = LoggerFactory.getLogger("autotest.log");

    private final CloseableHttpClient redirectClient;
    private final CloseableHttpClient nonredirectClient;
    private CloseableHttpClient client;
    private final HttpClientContext context;

    public AbstractHttpSession() {
        this.context = HttpClientContext.create();
        setCookies(new BasicCookieStore());
        this.redirectClient = opprettKlient(true);
        this.nonredirectClient = opprettKlient(false);
        this.client = this.redirectClient;
    }

    protected abstract CloseableHttpClient opprettKlient(boolean doRedirect);

    @Override
    public HttpResponse execute(HttpUriRequest request, Map<String, String> headers) {
        applyHeaders(request, headers);
        try {
            return client.execute(request, context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HttpResponse get(String url) {
        return get(url, new HashMap<>());
    }

    @Override
    public HttpResponse get(String url, Map<String, String> headers) {
        var request = new HttpGet(url);
        var response = execute(request, headers);
        log.info("GET[{}]: [{}]", url, response.getStatusLine().getStatusCode());
        return response;
    }

    @Override
    public HttpResponse post(String url, HttpEntity entity, Map<String, String> headers) {
        var request = new HttpPost(url);
        request.setEntity(entity);

        var response = execute(request, headers);
        try {
            log.info("POST[{}]: [{}] med content\n\t[{}]\n\tHeaders: {}",
                    url,
                    response.getStatusLine().getStatusCode(),
                    new BufferedReader(new InputStreamReader(entity.getContent())).lines().parallel()
                            .collect(Collectors.joining("\n")),
                    new ObjectMapper().writeValueAsString(headers));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    @Override
    public HttpResponse put(String url, HttpEntity entity, Map<String, String> headers) {
        var request = new HttpPut(url);
        request.setEntity(entity);
        return execute(request, headers);
    }

    @Override
    public HttpResponse delete(String url, Map<String, String> headers) {
        var request = new HttpDelete(url);
        return execute(request, headers);
    }


    @Override
    public void setRedirect(boolean doRedirect) {
        client = doRedirect ? redirectClient : nonredirectClient;
    }

    @Override
    public void setUserCredentials(String username, String password) {
        var provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        context.setCredentialsProvider(provider);
    }

    @Override
    public CookieStore hentCookieStore() {
        return context.getCookieStore();
    }

    @Override
    public void setCookies(CookieStore cookieStore) {
        context.setCookieStore(cookieStore);
    }

    @Override
    public void leggTilCookie(String name, String value, String domain, String path) {
        var cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        leggTilCookie(cookie);
    }

    @Override
    public void leggTilCookie(Cookie cookie) {
        hentCookieStore().addCookie(cookie);
    }

    @Override
    public String getCurrentUrl() {
        return ((HttpUriRequest) context.getAttribute(HttpCoreContext.HTTP_REQUEST)).getURI().toString();
    }

    protected static ConnectionKeepAliveStrategy createKeepAliveStrategy(int seconds) {
        return (response, context1) -> {
            var it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                var he = it.nextElement();
                var param = he.getName();
                var value = he.getValue();
                if ((value != null) && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return seconds * 1000;
        };
    }

}
