package no.nav.foreldrepenger.autotest.klienter.vtp.journalpost;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.util.Optional;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.vtp.VTPJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.dto.JournalpostIdDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.JournalpostModell;

public class JournalforingJerseyKlient extends VTPJerseyKlient {

    private static final String JOURNALFØRING_URL = "/journalforing";
    private static final String JOURNALFØR_JOURNALPOST = JOURNALFØRING_URL + "/journalfor";
    private static final String JOURNALFØR_FORELDREPENGER_SØKNAD_URL_FORMAT = JOURNALFØRING_URL + "/journalfor/fnr/{fnr}/dokumenttypeid/{dokumenttypeid}";
    private static final String KNYTT_SAK_TIL_JOURNALPOST = JOURNALFØRING_URL + "/knyttsaktiljournalpost/journalpostid/{journalpostid}/saksnummer/{saksnummer}";

    public JournalforingJerseyKlient() {
        super();
    }

    @Step("Journalfører sak i VTP")
    public JournalpostIdDto journalførR(JournalpostModell journalpostModell) {
        return client.target(base)
                .path(JOURNALFØR_JOURNALPOST)
                .request(APPLICATION_JSON_TYPE)
                .buildPost(json(journalpostModell))
                .invoke(JournalpostIdDto.class);
    }

    @Step("Journalfører sak i VTP")
    public JournalpostIdDto journalfør(JournalpostModell journalpostModell) {
        return client.target(base)
                .path(JOURNALFØR_FORELDREPENGER_SØKNAD_URL_FORMAT)
                .resolveTemplate("fnr", Optional.ofNullable(journalpostModell.getAvsenderFnr()).orElseThrow())
                .resolveTemplate("dokumenttypeid", Optional.ofNullable(journalpostModell.getDokumentModellList().get(0).getDokumentType().getKode()).orElseThrow())
                .request(APPLICATION_JSON_TYPE)
                .buildPost(json(journalpostModell.getDokumentModellList().get(0).getInnhold()))
                .invoke(JournalpostIdDto.class);
    }

    @Step("Knytter journalpost id {journalpostId} til sak {saksnummer} i VTP")
    public JournalpostIdDto knyttSakTilJournalpost(String journalpostId, String saksnummer) {
        return client.target(base)
                .path(KNYTT_SAK_TIL_JOURNALPOST)
                .resolveTemplate("journalpostid", Optional.ofNullable(journalpostId).orElseThrow())
                .resolveTemplate("saksnummer", Optional.ofNullable(saksnummer).orElseThrow())
                .request(APPLICATION_JSON_TYPE)
                .buildPost(json(""))
                .invoke(JournalpostIdDto.class);
    }
}