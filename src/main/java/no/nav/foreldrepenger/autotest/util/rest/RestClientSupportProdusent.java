package no.nav.foreldrepenger.autotest.util.rest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

public class RestClientSupportProdusent {

    /**
     * Sørger for å droppe og starte nye connections innimellom også om server ikke
     * sender keepalive header.
     */
    public static ConnectionKeepAliveStrategy createKeepAliveStrategy(int seconds) {
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
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
                .build();
    }

    public static List<Header> defaultHeaders() {
        return List.of(new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
    }

    public static PoolingHttpClientConnectionManager connectionManager() {
        ConnectionConfig defaultConnectionConfig = defaultConnectionConfig();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(55, TimeUnit.MINUTES);
        connManager.setMaxTotal(100);
        connManager.setDefaultConnectionConfig(defaultConnectionConfig);
        connManager.setValidateAfterInactivity(100);
        return connManager;
    }

}
