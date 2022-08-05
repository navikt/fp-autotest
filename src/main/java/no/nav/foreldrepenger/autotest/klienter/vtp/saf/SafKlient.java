package no.nav.foreldrepenger.autotest.klienter.vtp.saf;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.send;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.sendOgHentByteArray;

import java.net.http.HttpRequest;
import java.util.Map;
import java.util.Optional;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.graphql.GraphQLRequest;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.graphql.GraphQlResponse;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.modell.Journalpost;
import no.nav.foreldrepenger.autotest.util.ReadFileFromClassPathHelper;

public class SafKlient {

    private static final String SAF_URL = "/saf";
    private static final String GRAPHQL_ENDPOINT = SAF_URL + "/graphql";
    private static final String HENT_DOKUMENT = SAF_URL + "/rest/hentdokument/{journalpostId}/{dokumentId}/{variantFormat}";

    private static final String query = ReadFileFromClassPathHelper.hent("klienter/saf/journalpostQuery.graphql");

    public Journalpost hentJournalpost(String journalpostId) {
        var body = new GraphQLRequest(query, null, Map.of("journalpostId", journalpostId));
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.VTP_BASE)
                        .path(GRAPHQL_ENDPOINT)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(body)));
        var graphQlResponse = send(request.build(), GraphQlResponse.class);
        return graphQlResponse.data().journalpost();
    }

    public byte[] hentDokumenter(String journalpostId, String dokumentId, String variantFormat) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.VTP_BASE)
                        .path(HENT_DOKUMENT)
                        .resolveTemplate("journalpostId", Optional.ofNullable(journalpostId).orElse("null"))
                        .resolveTemplate("dokumentId", Optional.ofNullable(dokumentId).orElse("null"))
                        .resolveTemplate("variantFormat", Optional.ofNullable(variantFormat).orElse("null"))
                        .build())
                .GET();
        return sendOgHentByteArray(request.build());
    }

}
