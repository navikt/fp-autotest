package no.nav.foreldrepenger.autotest.klienter;

import static java.lang.Thread.sleep;
import static java.nio.charset.StandardCharsets.UTF_8;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.fromJson;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;

public final class JavaHttpKlient {
    private static final Logger LOG = LoggerFactory.getLogger(JavaHttpKlient.class);
    private static final int MAX_RETRY = 3;

    private static final HttpClient klient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    private JavaHttpKlient() {
        // Statisk implementasjon
    }

    public static void send(HttpRequest request) {
        handleResponse(sendStringRequest(request), stringHttpResponse -> null,
                getDefualtErrorConsumer());
    }

    public static <T> T send(HttpRequest request, Class<T> clazz) {
        return handleResponse(sendStringRequest(request), httpResponse -> fromJson(httpResponse.body(), clazz),
                getDefualtErrorConsumer());
    }

    public static <T> T send(HttpRequest request, Class<T> clazz, ObjectMapper mapper) {
        return handleResponse(sendStringRequest(request), httpResponse -> fromJson(httpResponse.body(), clazz, mapper),
                getDefualtErrorConsumer());
    }

    public static <T> T send(HttpRequest request, TypeReference<T> typeReference) {
        return handleResponse(sendStringRequest(request), httpResponse -> fromJson(httpResponse.body(), typeReference),
                getDefualtErrorConsumer());
    }

    public static byte[] sendOgHentByteArray(HttpRequest request) {
        return handleResponse(sendByteArrayRequest(request), HttpResponse::body,
                null); // TODO
    }

    public static HttpResponse<String> sendStringRequest(HttpRequest request) {
        return send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
    }

    private static HttpResponse<byte[]> sendByteArrayRequest(HttpRequest request) {
        return send(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    private static <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseHandler) {
        try {
            var response = klient.send(request, responseHandler);
            var antallForsøk = 1;
            while (retryOn5xxFailures(response, antallForsøk)) {
                LOG.warn("5xx feil mot {} for {}. gang. Prøver på nytt.", request.uri(), antallForsøk);
                int ventSekunder = Math.min(2000, 1000 * antallForsøk++);
                sleep(ventSekunder);
                response = klient.send(request, responseHandler);
            }
            return response;
        } catch (IOException e) {
            throw new TekniskException("F-432937", String.format("Kunne ikke sende request %s", request), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TekniskException("F-432938", "InterruptedException ved henting av data.", e);
        }
    }

    private static <T> boolean retryOn5xxFailures(HttpResponse<T> response, int antallForsøk) {
        return antallForsøk > MAX_RETRY || 500 <= response.statusCode() && response.statusCode() <= 599;
    }

    private static <T, R> R handleResponse(HttpResponse<T> response, Function<HttpResponse<T>, R> responseFunction, Consumer<HttpResponse<T>> errorConsumer) {
        var statusCode = response.statusCode();
        var endpoint = response.uri();
        if (response.body() == null || statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            LOG.info("[HTTP {}] Ingen resultat fra {}", statusCode, endpoint);
            return null;
        }
        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED || statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
            logRequest(response);
            throw new ManglerTilgangException("NO-AUTH", String.format("[HTTP %s] Mangler tilgang. Feilet mot %s", statusCode, endpoint));
        }
        if ((statusCode >= HttpURLConnection.HTTP_OK && statusCode < HttpURLConnection.HTTP_MULT_CHOICE)) {
            return responseFunction.apply(response);
        }
        if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new TekniskException("NOT-FOUND", String.format("[HTTP %s] Feilet mot %s.", statusCode, endpoint));
        }
        if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST && errorConsumer != null) {
            logRequest(response);
            errorConsumer.accept(response);
        }
        logRequest(response);
        throw new IntegrasjonException("REST-FEIL", String.format("[HTTP %s] Uventet respons fra %s, med melding: %s", statusCode, endpoint,
                toJson(response.body())));
    }

    private static Consumer<HttpResponse<String>> getDefualtErrorConsumer() {
        return httpResponse -> {
            throw new IntegrasjonException("FP-468820", String.format("[HTTP %s] Uventet respons fra %s, med melding: %s", httpResponse.statusCode(),
                    httpResponse.uri(), toJson(httpResponse.body())));
        };
    }


    private static <T> void logRequest(HttpResponse<T> response) {
        var request = response.request();
        if (response.body() != null && response.body() instanceof String body) {
            LOG.warn("REST-FEIL: Prøvde å sende request {} med headere {} og body {}", request, request.headers(), body);
        } else {
            logRequest(request);
        }
    }

    private static void logRequest(HttpRequest request) {
        LOG.warn("REST-FEIL: Prøvde å sende request {}, med headere {} og uten body", request, request.headers());
    }
}
