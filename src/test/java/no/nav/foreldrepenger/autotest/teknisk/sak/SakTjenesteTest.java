package no.nav.foreldrepenger.autotest.teknisk.sak;

import static no.nav.foreldrepenger.autotest.klienter.BaseUriProvider.VTP_ROOT;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.ws.rs.core.HttpHeaders;
import no.nav.foreldrepenger.autotest.util.rest.JacksonObjectMapper;

@Tag("fpsak")
@Tag("foreldrepenger")
class SakTjenesteTest {

    @Test
    void testOmKrøllMedDependencies() throws Exception {
        var client = HttpClient.newHttpClient();
        var vtp = VTP_ROOT + "/rest/v1/sts/token/exchange";
        var samlTokenRequest = HttpRequest.newBuilder()
                .uri(URI.create(vtp))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        var samlResponse = JacksonObjectMapper.mapper.readValue(
                client.send(samlTokenRequest, HttpResponse.BodyHandlers.ofString()).body(), SAMLResponse.class);

        var authHeader = samlResponse.token_type() + " " + samlResponse.access_token();
        var path = Path.of(getClass().getClassLoader().getResource("sak-request.xml").toURI());
        var sakRequest = HttpRequest.newBuilder()
                .setHeader(HttpHeaders.AUTHORIZATION, authHeader)
                .uri(URI.create("http://localhost:8080/fpsak/tjenester/sak/opprettSak/v1?ping"))
                .POST(HttpRequest.BodyPublishers.ofFile(path))
                .build();

        var sakResponse = client.send(sakRequest, HttpResponse.BodyHandlers.ofString());
        //Beste hvis vi hadde klart å authentisere oss riktig og få en 200, men dette er en god nok løsning for å teste det vi ser etter
        assertThat(sakResponse.body()).contains("401");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static record SAMLResponse(String access_token, String token_type) { }
}
