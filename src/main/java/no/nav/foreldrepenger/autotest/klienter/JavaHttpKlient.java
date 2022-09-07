package no.nav.foreldrepenger.autotest.klienter;

import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.fromJson;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.vedtak.log.mdc.MDCOperations.HTTP_HEADER_ALT_CALL_ID;
import static no.nav.vedtak.log.mdc.MDCOperations.NAV_CALL_ID;
import static no.nav.vedtak.log.mdc.MDCOperations.NAV_CONSUMER_ID;
import static no.nav.vedtak.log.mdc.MDCOperations.generateCallId;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.core.MediaType;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;

public class JavaHttpKlient {
    private static final Logger LOG = LoggerFactory.getLogger(JavaHttpKlient.class);

    private static final ThreadLocal<JavaHttpKlient> instances;

    static {
        instances = ThreadLocal.withInitial(JavaHttpKlient::new);
    }

    private final HttpClient klient;
    private final CookieHandler cookieHandler;

    private JavaHttpKlient() {
        CookieHandler.setDefault(new CookieManager());
        this.cookieHandler = CookieHandler.getDefault();
        this.klient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .cookieHandler(cookieHandler)
                .build();
    }

    public HttpClient klient() {
        return klient;
    }

    public CookieManager cookieManager() {
        return (CookieManager) cookieHandler;
    }

    public static JavaHttpKlient getInstance() {
        return instances.get();
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
            return getInstance().klient().send(request, responseHandler);
        } catch (IOException e) {
            throw new TekniskException("F-432937", String.format("Kunne ikke sende request mot %s", request.uri().toString()), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TekniskException("F-432938", "InterruptedException ved henting av data.", e);
        }
    }


    private static <T, R> R handleResponse(HttpResponse<T> response, Function<HttpResponse<T>, R> responseFunction, Consumer<HttpResponse<T>> errorConsumer) {
        var statusCode = response.statusCode();
        var endpoint = response.uri();
        if (response.body() == null || statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            LOG.info("[HTTP {}] Ingen resultat fra {}", statusCode, endpoint);
            return null;
        }
        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED || statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
            throw new ManglerTilgangException("NO-AUTH", String.format("[HTTP %s] Mangler tilgang. Feilet mot %s", statusCode, endpoint));
        }
        if ((statusCode >= HttpURLConnection.HTTP_OK && statusCode < HttpURLConnection.HTTP_MULT_CHOICE)) {
            return responseFunction.apply(response);
        }
        if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new TekniskException("NOT-FOUND", String.format("[HTTP %s] Feilet mot %s.", statusCode, endpoint));
        }
        if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST && errorConsumer != null) {
            errorConsumer.accept(response);
        }
        throw new IntegrasjonException("REST-FEIL", String.format("[HTTP %s] Uventet respons fra %s, med melding: %s", statusCode, endpoint,
                toJson(response.body())));
    }

    private static Consumer<HttpResponse<String>> getDefualtErrorConsumer() {
        return httpResponse -> {
            throw new IntegrasjonException("FP-468820", String.format("[HTTP %s] Uventet respons fra %s, med melding: %s", httpResponse.statusCode(),
                    httpResponse.uri(), toJson(httpResponse.body())));
        };

    }

    public static HttpRequest.Builder getRequestBuilder() {
        var consumerID = MDC.get(NAV_CONSUMER_ID);
        var callid = consumerID != null ? consumerID : generateCallId();
        return HttpRequest.newBuilder()
                .header(ACCEPT, MediaType.APPLICATION_JSON)
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HTTP_HEADER_ALT_CALL_ID, callid)
                .header(NAV_CALL_ID, callid);
    }



}
