package no.nav.foreldrepenger.autotest.klienter;

import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static no.nav.vedtak.klient.http.CommonHttpHeaders.HEADER_NAV_ALT_CALLID;
import static no.nav.vedtak.klient.http.CommonHttpHeaders.HEADER_NAV_CALLID;
import static no.nav.vedtak.klient.http.CommonHttpHeaders.HEADER_NAV_CONSUMER_ID;

import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.AzureTokenProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.maskinporten.AuthorizationDetails;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.vedtak.log.mdc.MDCOperations;

public final class HttpRequestProvider {

    private static final String OIDC_AUTH_HEADER_PREFIX = "Bearer ";

    private HttpRequestProvider() {
        // Statisk implementasjon
    }

    public static HttpRequest.Builder requestMedInnloggetBruker(Fødselsnummer søker) {
        var requestBuilder = requestMedBasicHeadere();
        return medBearerTokenOgConsumerId(requestBuilder, TokenProvider.tokenXToken(søker), getCallId());
    }

    public static HttpRequest.Builder requestMedInnloggetSaksbehandler(SaksbehandlerRolle saksbehandlerRolle, String clientId) {
        var requestBuilder = requestMedBasicHeadere();
        return medBearerTokenOgConsumerId(requestBuilder, AzureTokenProvider.azureOboToken(saksbehandlerRolle), clientId);
    }

    public static HttpRequest.Builder requestMedMaskinportenToken(String orgnr) {
        var requestBuilder = requestMedBasicHeadere();
        var authDetails = new AuthorizationDetails("maskinporten", new AuthorizationDetails.Consumer(orgnr, orgnr), List.of(orgnr), "NAVIDA_LPS");
        return medBearerTokenOgConsumerId(requestBuilder, TokenProvider.maskinportenToken("nav:inntektsmelding/foreldrepenger", List.of(authDetails)), getCallId());
    }

    public static HttpRequest.Builder requestMedBasicHeadere() {
        return HttpRequest.newBuilder()
                .header(ACCEPT, MediaType.APPLICATION_JSON)
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .timeout(Duration.ofSeconds(5));
    }

    private static HttpRequest.Builder medBearerTokenOgConsumerId(HttpRequest.Builder requestBuilder, String token, String consumerId) {
        return requestBuilder
                .header(AUTHORIZATION, OIDC_AUTH_HEADER_PREFIX + token)
                .header(HEADER_NAV_CONSUMER_ID, consumerId)
                .header(HEADER_NAV_CALLID, getCallId())
                .header(HEADER_NAV_ALT_CALLID, getCallId());
    }


    private static String getCallId() {
        return Optional.ofNullable(MDCOperations.getCallId()).orElseGet(MDCOperations::generateCallId);
    }

}
