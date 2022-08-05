package no.nav.foreldrepenger.autotest.klienter;

import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class HttpRequestBodyPublishers {


    private HttpRequestBodyPublishers() {
    }

    public static HttpRequest.BodyPublisher buildFormDataFromMap(Map<String, String> data) {
        var builder = new StringBuilder();
        for (var entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
