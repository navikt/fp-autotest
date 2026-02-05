package no.nav.foreldrepenger.autotest.klienter;

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

import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

public final class JavaHttpKlient {
    private static final Logger LOG = LoggerFactory.getLogger(JavaHttpKlient.class);
    private static final int MAX_RETRY = 2;

    private static final HttpClient klient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private JavaHttpKlient() {
        // Statisk implementasjon
    }

    public static void send(HttpRequest request) {
        handleResponse(sendStringRequest(request), stringHttpResponse -> null,
                getDefaultErrorConsumer());
    }
    public static <T> T send(HttpRequest request, Class<T> clazz) {
        return handleResponse(sendStringRequest(request), httpResponse -> fromJson(httpResponse.body(), clazz),
                getDefaultErrorConsumer());
    }

    public static <T> T send(HttpRequest request, Class<T> clazz, JsonMapper mapper) {
        return handleResponse(sendStringRequest(request), httpResponse -> fromJson(httpResponse.body(), clazz, mapper),
                getDefaultErrorConsumer());
    }

    public static <T> T send(HttpRequest request, TypeReference<T> typeReference) {
        return handleResponse(sendStringRequest(request), httpResponse -> fromJson(httpResponse.body(), typeReference),
                getDefaultErrorConsumer());
    }

    public static <T> T send(HttpRequest request, JsonMapper mapper, TypeReference<T> typeReference) {
        return handleResponse(sendStringRequest(request), httpResponse -> fromJson(httpResponse.body(), mapper, typeReference),
                getDefaultErrorConsumer());
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
        int i = MAX_RETRY;
        while (i-- > 0) {
            try {
                return doSend(request, responseHandler);
            } catch (IntegrasjonException e) {
                var antallForsøk = MAX_RETRY - i;
                LOG.info("F-157390 IntegrasjonException ved kall {} til endepunkt {}. Prøver på nytt...", antallForsøk, request.uri());
                sleepWithBackoff(antallForsøk);
            }
        }
        return doSend(request, responseHandler);
    }


    private static <T> HttpResponse<T> doSend(HttpRequest request, HttpResponse.BodyHandler<T> responseHandler) {
        try {
            var response = klient.send(request, responseHandler);
            if (is5xxStatus(response)) {
                throw new IntegrasjonException("F-157390", "5xx feil mot " + request.uri());
            }
            return response;
        } catch (IOException e) {
            throw new IntegrasjonException("F-157391", "Uventet IO-exception mot endepunkt", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IntegrasjonException("F-157392", "InterruptedException ved kall mot endepunkt", e);
        }
    }

    private static void sleepWithBackoff(int antallForsøk) {
        int baseMillis = 500;
        int maxMillis = 1_500;
        int delay = Math.min(maxMillis, baseMillis * antallForsøk); // simple linear backoff
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IntegrasjonException("F-157393", "Thread ble avbrutt under venting mellom retries", e);
        }
    }

    private static <T> boolean is5xxStatus(HttpResponse<T> response) {
        return response.statusCode() >= 500 && response.statusCode() < 600;
    }

    private static <T, R> R handleResponse(HttpResponse<T> response, Function<HttpResponse<T>, R> responseFunction, Consumer<HttpResponse<T>> errorConsumer) {
        var statusCode = response.statusCode();
        var endpoint = response.uri();
        if (response.body() == null || statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            LOG.info("[HTTP {}] Ingen resultat fra {}", statusCode, endpoint);
            return null;
        }
        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED || statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
            throw new ManglerTilgangException("NO-AUTH", String.format("[HTTP %s] Mangler tilgang. Feilet mot %s. Med headers %s", statusCode, endpoint, response.headers()));
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

    private static Consumer<HttpResponse<String>> getDefaultErrorConsumer() {
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
