package no.nav.foreldrepenger.autotest.klienter.vtp.saf;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedBasicHeadere;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.sendOgHentByteArray;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;

public class SafKlient {

    private static final String SAF_URL = "/saf";
    private static final String HENT_DOKUMENT = SAF_URL + "/rest/hentdokument/{journalpostId}/{dokumentId}/{variantFormat}";


    public byte[] hentDokumenter(String journalpostId, String dokumentId, String variantFormat) {
        var request = requestMedBasicHeadere()
                .uri(fromUri(BaseUriProvider.VTP_API_BASE)
                        .path(HENT_DOKUMENT)
                        .resolveTemplate("journalpostId", String.valueOf(journalpostId))
                        .resolveTemplate("dokumentId", String.valueOf(dokumentId))
                        .resolveTemplate("variantFormat", String.valueOf(variantFormat))
                        .build())
                .GET();
        return sendOgHentByteArray(request.build());
    }

}
