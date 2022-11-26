package no.nav.foreldrepenger.autotest.klienter;

import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static no.nav.vedtak.log.mdc.MDCOperations.HTTP_HEADER_ALT_CALL_ID;
import static no.nav.vedtak.log.mdc.MDCOperations.NAV_CALL_ID;
import static no.nav.vedtak.log.mdc.MDCOperations.NAV_CONSUMER_ID;

import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Optional;

import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.AzureTokenProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.openam.SaksbehandlerRolle;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.vedtak.log.mdc.MDCOperations;

public final class HttpRequestProvider {

    private static final String OIDC_AUTH_HEADER_PREFIX = "Bearer ";

    private HttpRequestProvider() {
        // Statisk implementasjon
    }

    public static HttpRequest.Builder requestMedInnloggetBruker(Fødselsnummer søker) {
        var requestBuilder = requestMedBasicHeadere();
        return medBearerTokenOgConsumerId(requestBuilder, TokenProvider.tokenXToken(søker));
    }

    public static HttpRequest.Builder requestMedInnloggetSaksbehandler(SaksbehandlerRolle saksbehandlerRolle, String clientId) {
        var requestBuilder = requestMedBasicHeadere();
        return medBearerTokenOgConsumerId(requestBuilder, AzureTokenProvider.azureOboToken(saksbehandlerRolle, clientId));
    }

    public static HttpRequest.Builder requestMedBasicHeadere() {
        return HttpRequest.newBuilder()
                .header(ACCEPT, MediaType.APPLICATION_JSON)
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .timeout(Duration.ofSeconds(10));
    }

    private static HttpRequest.Builder medBearerTokenOgConsumerId(HttpRequest.Builder requestBuilder, String token) {
        return requestBuilder
                .header(AUTHORIZATION, OIDC_AUTH_HEADER_PREFIX + token)
                .header(NAV_CONSUMER_ID, getConsumerId())
                .header(NAV_CALL_ID, getCallId())
                .header(HTTP_HEADER_ALT_CALL_ID, getCallId());
    }


    private static String getConsumerId() {
        return Optional.ofNullable(MDCOperations.getConsumerId())
                .orElseGet(MDCOperations::generateCallId);
    }

    private static String getCallId() {
        return getConsumerId();
    }

}
