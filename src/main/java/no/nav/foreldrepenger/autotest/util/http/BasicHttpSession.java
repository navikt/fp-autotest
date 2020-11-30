package no.nav.foreldrepenger.autotest.util.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;

public class BasicHttpSession extends AbstractHttpSession {
    private static final ThreadLocal<BasicHttpSession> sessions = ThreadLocal.withInitial(BasicHttpSession::new);

    private static final CloseableHttpClient redirectClient = createKlient(true);
    private static final CloseableHttpClient nonRedirectClient = createKlient(false);

    public static BasicHttpSession session() {
        return sessions.get();
    }

    private BasicHttpSession() {
    }

    @Override
    protected CloseableHttpClient opprettKlient(boolean doRedirect) {
        return doRedirect ? redirectClient : nonRedirectClient;
    }

    private static CloseableHttpClient createKlient(boolean doRedirect) {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setMaxConnPerRoute(40);
        builder.setMaxConnTotal(200);
        builder.setRetryHandler(new StandardHttpRequestRetryHandler());

        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        int connectTimeoutMillis = 30_000;
        requestBuilder = requestBuilder.setConnectTimeout(connectTimeoutMillis);
        requestBuilder = requestBuilder.setSocketTimeout(connectTimeoutMillis);

        builder.setDefaultRequestConfig(requestBuilder.build());
        builder.setKeepAliveStrategy(createKeepAliveStrategy(30));

        if (doRedirect) {
            builder.setRedirectStrategy(new LaxRedirectStrategy());
        } else {
            builder.disableRedirectHandling();
        }
        return builder.build();
    }

}
