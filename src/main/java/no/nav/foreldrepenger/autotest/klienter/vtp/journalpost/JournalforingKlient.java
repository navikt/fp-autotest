package no.nav.foreldrepenger.autotest.klienter.vtp.journalpost;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedBasicHeadere;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.util.Optional;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.dto.JournalpostIdDto;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.JournalpostModell;

public class JournalforingKlient {

    private static final String JOURNALFØRING_URL = "/journalforing";
    private static final String JOURNALFØR_JOURNALPOST = JOURNALFØRING_URL + "/journalfor";
    private static final String JOURNALFØR_FORELDREPENGER_SØKNAD_URL_FORMAT = JOURNALFØRING_URL + "/journalfor/fnr/{fnr}/dokumenttypeid/{dokumenttypeid}";
    private static final String KNYTT_SAK_TIL_JOURNALPOST = JOURNALFØRING_URL + "/knyttsaktiljournalpost/journalpostid/{journalpostid}/saksnummer/{saksnummer}";

    public JournalpostIdDto journalførR(JournalpostModell journalpostModell) {
        var request = requestMedBasicHeadere()
                .uri(fromUri(BaseUriProvider.VTP_BASE)
                        .path(JOURNALFØR_JOURNALPOST)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(journalpostModell)));
        return send(request.build(), JournalpostIdDto.class);
    }

    public JournalpostIdDto journalfør(JournalpostModell journalpostModell) {
        var request = requestMedBasicHeadere()
                .uri(fromUri(BaseUriProvider.VTP_BASE)
                        .path(JOURNALFØR_FORELDREPENGER_SØKNAD_URL_FORMAT)
                        .resolveTemplate("fnr", Optional.ofNullable(journalpostModell.getAvsenderFnr()).orElseThrow())
                        .resolveTemplate("dokumenttypeid", Optional.ofNullable(journalpostModell.getDokumentModellList().get(0).getDokumentType().getKode()).orElseThrow())
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(journalpostModell.getDokumentModellList().get(0).getInnhold())));
        return send(request.build(), JournalpostIdDto.class);
    }

    public JournalpostIdDto knyttSakTilJournalpost(String journalpostId, Saksnummer saksnummer) {
        var request = requestMedBasicHeadere()
                .uri(fromUri(BaseUriProvider.VTP_BASE)
                        .path(KNYTT_SAK_TIL_JOURNALPOST)
                        .resolveTemplate("journalpostid", Optional.ofNullable(journalpostId).orElseThrow())
                        .resolveTemplate("saksnummer", Optional.ofNullable(saksnummer).map(Saksnummer::value).orElseThrow())
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(""));
        return send(request.build(), JournalpostIdDto.class);
    }
}
