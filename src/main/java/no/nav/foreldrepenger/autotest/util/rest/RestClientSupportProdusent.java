package no.nav.foreldrepenger.autotest.util.rest;

import org.apache.http.Consts;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;

public class RestClientSupportProdusent {

    private RestClientSupportProdusent() {
    }

    /**
     * Sørger for å droppe og starte nye connections innimellom også om server ikke
     * sender keepalive header.
     */
    public static ConnectionKeepAliveStrategy createKeepAliveStrategy(int seconds) {
        return (response, context) -> {
            var it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                var he = it.nextElement();
                var param = he.getName();
                var value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000L;
                }
            }
            return seconds * 1000L;
        };
    }


    private static ConnectionConfig defaultConnectionConfig() {
        return ConnectionConfig.custom()
                .setCharset(Consts.UTF_8)
                .build();
    }

    public static RequestConfig defaultRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(30_000)
                .setSocketTimeout(30_000)
                .build();
    }

    public static PoolingHttpClientConnectionManager connectionManager() {
        var connManager = new PoolingHttpClientConnectionManager();
        connManager.setDefaultConnectionConfig(defaultConnectionConfig());
        connManager.setDefaultMaxPerRoute(40);
        connManager.setMaxTotal(80);
        return connManager;
    }

}
