package no.nav.foreldrepenger.autotest.klienter.vtp.journalpost;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.dto.JournalpostIdDto;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.JournalpostModell;

public class JournalforingKlient extends VTPKlient{


    private static final String JOURNALFØRING_URL = "/journalforing";
    private static final String JOURNALFØR_FORELDREPENGER_SØKNAD_URL_FORMAT = JOURNALFØRING_URL + "/foreldrepengesoknadxml/fnr/%s/dokumenttypeid/%s";
    private static final String KNYTT_SAK_TIL_JOURNALPOST = JOURNALFØRING_URL + "/knyttsaktiljournalpost/journalpostid/%s/saksnummer/%s";

    public JournalforingKlient(HttpSession session) {
        super(session);
    }

    @Step("Journalfører sak i VTP")
    public JournalpostIdDto journalfør(JournalpostModell journalpostModell) {
        String url = hentRestRotUrl() + String.format(JOURNALFØR_FORELDREPENGER_SØKNAD_URL_FORMAT, journalpostModell.getAvsenderFnr(), journalpostModell.getDokumentModellList().get(0).getDokumentType().getKode());
        return postOgHentJson(url, journalpostModell.getDokumentModellList().get(0).getInnhold(), JournalpostIdDto.class, StatusRange.STATUS_SUCCESS);
    }

    @Step("Knytter journalpost id {journalpostId} til sak {saksnummer} i VTP")
    public JournalpostIdDto knyttSakTilJournalpost(String journalpostId, String saksnummer){
        String url = hentRestRotUrl() + String.format(KNYTT_SAK_TIL_JOURNALPOST, journalpostId, saksnummer);
        return postOgHentJson(url, null, JournalpostIdDto.class, StatusRange.STATUS_SUCCESS);
    }

}
