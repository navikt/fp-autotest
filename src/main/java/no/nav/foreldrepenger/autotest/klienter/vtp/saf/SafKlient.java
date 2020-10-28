package no.nav.foreldrepenger.autotest.klienter.vtp.saf;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;

public class SafKlient extends VTPKlient {

    private static final String SAF_URL = "/saf";
    private static final String HENT_DOKUMENT = SAF_URL + "/rest/hentdokument/%s/%s/%s";

    public SafKlient(HttpSession session) {
        super(session);
    }

    public byte[] hentDokumenter(String journalpostid, String dokumentId, String variantFormat) {
        String url = hentRestRotUrl() + String.format(HENT_DOKUMENT, journalpostid, dokumentId, variantFormat);
        return getByteArray(url);
    }

}
