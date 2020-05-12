package no.nav.foreldrepenger.autotest.util.http.rest;

import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import io.qameta.allure.Attachment;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;

public abstract class Rest {

    private static final String WRONG_STATUS_MESSAGE_FORMAT = "Request returned unexpected status code expected range %s got %s\n%s";
    protected HttpSession session;

    public Rest(HttpSession session) {
        this.session = session;
    }

    protected HttpResponse get(String url) {
        return get(url, HttpSession.createEmptyHeaders());
    }

    protected HttpResponse get(String url, Map<String, String> headers) {
        return session.get(url, headers);
    }

    protected HttpResponse post(String url, HttpEntity entity, Map<String, String> headers) {
        return session.post(url, entity, headers);
    }

    protected HttpResponse put(String url, HttpEntity entity) {
        return put(url, entity, HttpSession.createEmptyHeaders());
    }

    protected HttpResponse put(String url, HttpEntity entity, Map<String, String> headers) {
        return session.put(url, entity, headers);
    }

    @Attachment(value = "HttpResponse", type = "application/json")
    protected String hentResponseBody(HttpResponse response) {
        return HttpSession.readResponse(response);
    }

    protected void ValidateResponse(HttpResponse response, StatusRange expectedRange) {
        ValidateResponse(response, expectedRange, "");
    }

    protected void ValidateResponse(HttpResponse response, StatusRange expectedRange, String body) {
        int statuscode = response.getStatusLine().getStatusCode();

        if (!expectedRange.inRange(statuscode)) {
            if (body.equals("")) {
                body = hentResponseBody(response);
            }

            throw new RuntimeException(String.format(WRONG_STATUS_MESSAGE_FORMAT, expectedRange, statuscode, body));
        }
    }

    /*
     * URL ENCODING
     */
    public String UrlCompose(String url, Map<String, String> data) {
        return url + UrlEncodeQuery(data);
    }

    public String UrlEncodeQuery(Map<String, String> data) {
        return UrlEncodeQuery(data, "?");
    }

    public String UrlEncodeQuery(Map<String, String> data, String prefix) {
        StringBuilder query = new StringBuilder(prefix);
        for (Map.Entry<String, String> item : data.entrySet()) {
            if (item.getValue() != null && !item.getKey().isEmpty() && !item.getValue().isEmpty()) {
                String queryKey = UrlEncodeItem(item.getKey());
                String queryValue = UrlEncodeItem(item.getValue());
                query.append(String.format("%s=%s&", queryKey, queryValue));
            }
        }
        return query.substring(0, query.length() - 1);
    }

    public String UrlEncodeItem(String item) {
        try {
            return URLEncoder.encode(item, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Unable to encode '" + item + "': " + e.getMessage());
        }
    }

    public abstract String hentRestRotUrl();
}
