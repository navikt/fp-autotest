package no.nav.foreldrepenger.autotest.klienter;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.http.HttpClient;

public class JavaHttpKlient {

    private static final ThreadLocal<JavaHttpKlient> instances;

    static {
        instances = ThreadLocal.withInitial(JavaHttpKlient::new);
    }

    private final HttpClient klient;
    private final CookieHandler cookieHandler;

    private JavaHttpKlient() {
        CookieHandler.setDefault(new CookieManager());
        this.cookieHandler = CookieManager.getDefault();
        this.klient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .cookieHandler(cookieHandler)
                .build();
    }


    public static JavaHttpKlient getInstance() {
        return instances.get();
    }

    public HttpClient klient() {
        return klient;
    }

    public CookieManager cookieManager() {
        return (CookieManager) cookieHandler;
    }
}
