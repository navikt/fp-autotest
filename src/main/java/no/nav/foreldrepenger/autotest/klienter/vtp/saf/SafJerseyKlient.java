package no.nav.foreldrepenger.autotest.klienter.vtp.saf;

import static javax.ws.rs.client.Entity.json;

import java.util.Map;
import java.util.Optional;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.graphql.GraphQLRequest;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.graphql.GraphQlResponse;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.modell.Journalpost;
import no.nav.foreldrepenger.autotest.util.ReadFileFromClassPathHelper;

public class SafJerseyKlient extends VTPJerseyKlient {

    private static final String SAF_URL = "/saf";
    private static final String GRAPHQL_ENDPOINT = SAF_URL + "/graphql";
    private static final String HENT_DOKUMENT = SAF_URL + "/rest/hentdokument/{journalpostId}/{dokumentId}/{variantFormat}";

    private static String query;

    public SafJerseyKlient() {
        super();
        query = ReadFileFromClassPathHelper.hent("klienter/saf/journalpostQuery.graphql");
    }

    public Journalpost hentJournalpost(String journalpostId) {
        var request = GraphQLRequest.builder()
                .withQuery(query)
                .withVariables(Map.of("journalpostId", journalpostId))
                .build();
        var graphQlResponse = client.target(base)
                .path(GRAPHQL_ENDPOINT)
                .request()
                .post(json(request), GraphQlResponse.class);
        return graphQlResponse.data().journalpost();
    }


    public byte[] hentDokumenter(String journalpostId, String dokumentId, String variantFormat) {
        return client.target(base)
                .path(HENT_DOKUMENT)
                .resolveTemplate("journalpostId", Optional.ofNullable(journalpostId).orElse("null"))
                .resolveTemplate("dokumentId", Optional.ofNullable(dokumentId).orElse("null"))
                .resolveTemplate("variantFormat", Optional.ofNullable(variantFormat).orElse("null"))
                .request()
                .get(byte[].class);
    }

}
