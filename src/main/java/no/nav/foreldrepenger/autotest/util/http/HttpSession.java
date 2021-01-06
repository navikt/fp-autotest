package no.nav.foreldrepenger.autotest.util.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.autotest.util.http.rest.JacksonObjectMapper;

public interface HttpSession {

    ObjectMapper mapper = JacksonObjectMapper.getObjectMapper();

    static Map<String, String> createEmptyHeaders() {
        return new HashMap<>();
    }

    static String readResponse(HttpResponse response) {
        try {
            var entity = response.getEntity();
            if (entity == null) {
                return "";
            }
            var content = EntityUtils.toString(entity, "UTF-8");
            if (content.isEmpty()) {
                return "";
            }
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(content, Object.class));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    static byte[] readResponseByteArray(HttpResponse response) {
        try {
            var entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            return EntityUtils.toByteArray(entity);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    default void applyHeaders(HttpUriRequest request, Map<String, String> headers) {
        for (String headerKey : headers.keySet()) {
            request.addHeader(headerKey, headers.get(headerKey));
        }

        // Hack for missing cookies in header (Client refuses to set cookies from one domain to another)
        var cookies = new StringBuilder();
        var cookieStore = hentCookieStore();
        var cookiesList = cookieStore.getCookies();

        for (Cookie cookie : cookiesList) {
            cookies.append(String.format("%s=%s; ", cookie.getName(), cookie.getValue()));
        }
        request.addHeader("Cookie", cookies.toString());
    }


    HttpResponse execute(HttpUriRequest request, Map<String, String> headers);

    HttpResponse get(String url);

    HttpResponse get(String url, Map<String, String> headers);

    HttpResponse post(String url, HttpEntity entity, Map<String, String> headers);

    HttpResponse put(String url, HttpEntity entity, Map<String, String> headers);

    HttpResponse delete(String url, Map<String, String> headers);

    void setRedirect(boolean doRedirect);

    void setUserCredentials(String username, String password);

    CookieStore hentCookieStore();

    void setCookies(CookieStore cookieStore);

    void leggTilCookie(String name, String value, String domain, String path);

    void leggTilCookie(Cookie cookie);

    String getCurrentUrl();
}
