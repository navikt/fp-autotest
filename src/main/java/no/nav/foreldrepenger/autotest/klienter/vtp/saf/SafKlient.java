package no.nav.foreldrepenger.autotest.klienter.vtp.saf;

import java.util.Map;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.graphql.GraphQLRequest;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.graphql.GraphQlResponse;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.modell.Journalpost;
import no.nav.foreldrepenger.autotest.util.ReadFileFromClassPathHelper;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

public class SafKlient extends VTPKlient {

    private static final String SAF_URL = "/saf";
    private static final String GRAPHQL_ENDPOINT = SAF_URL + "/graphql";
    private static final String HENT_DOKUMENT = SAF_URL + "/rest/hentdokument/%s/%s/%s";

    private static String query;

    public SafKlient(HttpSession session) {
        super(session);
        query = ReadFileFromClassPathHelper.hent("klienter/saf/journalpostQuery.graphql");
    }

    public Journalpost hentJournalpost(String journalpostId) {
        var url = hentRestRotUrl() + GRAPHQL_ENDPOINT;
        var request = GraphQLRequest.builder()
                .withQuery(query)
                .withVariables(Map.of("journalpostId", journalpostId))
                .build();
        var graphQlResponse = postOgHentJson(url, request, GraphQlResponse.class, StatusRange.STATUS_NO_SERVER_ERROR);
        return graphQlResponse.data().journalpost();
    }


    public byte[] hentDokumenter(String journalpostid, String dokumentId, String variantFormat) {
        var url = hentRestRotUrl() + String.format(HENT_DOKUMENT, journalpostid, dokumentId, variantFormat);
        return getOgHentByteArray(url);
    }

}
