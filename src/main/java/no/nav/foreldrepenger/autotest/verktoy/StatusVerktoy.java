package no.nav.foreldrepenger.autotest.verktoy;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;

public final class StatusVerktoy {

    private static final Duration TIDSAVBRUDD = Duration.ofSeconds(2);
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(TIDSAVBRUDD)
            .build();

    private StatusVerktoy() {
    }

    static String kjor(Map<String, Object> args) throws InterruptedException {
        var vent = args != null && args.get("vent_sekunder") instanceof Number n ? n.intValue() : 0;
        var slutt = System.currentTimeMillis() + vent * 1000L;
        var resultat = sjekkAlle();
        while (resultat.contains("| FEIL |") && System.currentTimeMillis() < slutt) {
            Thread.sleep(5000);
            resultat = sjekkAlle();
        }
        return resultat;
    }

    private static String sjekkAlle() {
        var resultat = new ArrayList<String>();
        resultat.add(sjekk("fpsak", helsesjekk(BaseUriProvider.FPSAK_BASE)));
        resultat.add(sjekk("VTP", URI.create(BaseUriProvider.VTP_ROOT + "/rest/isReady")));
        resultat.add(sjekk("fpabakus", URI.create("http://localhost:8015/fpabakus/internal/health/isReady")));
        resultat.add(sjekk("fpkalkulus", helsesjekk(BaseUriProvider.KALKULUS_BASE)));
        return String.join("\n", resultat);
    }

    private static URI helsesjekk(URI apiBase) {
        var sti = apiBase.getPath();
        var kontekst = sti.substring(0, sti.indexOf('/', 1));
        return URI.create(apiBase.getScheme() + "://" + apiBase.getAuthority()
                + kontekst + "/internal/health/isReady");
    }

    private static String sjekk(String namn, URI uri) {
        try {
            var request = HttpRequest.newBuilder(uri)
                    .timeout(TIDSAVBRUDD)
                    .GET()
                    .build();
            var response = HTTP.send(request, HttpResponse.BodyHandlers.discarding());
            var ok = response.statusCode() >= 200 && response.statusCode() < 300;
            return namn + " | " + uri + " | " + (ok ? "OK" : "FEIL")
                    + " | HTTP " + response.statusCode();
        } catch (Exception e) {
            return namn + " | " + uri + " | FEIL | " + feilmelding(e);
        }
    }

    private static String feilmelding(Exception e) {
        var melding = e.getMessage();
        if (melding == null || melding.isBlank()) {
            return e.getClass().getSimpleName();
        }
        return e.getClass().getSimpleName() + ": " + melding;
    }
}
