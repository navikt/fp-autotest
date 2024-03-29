package no.nav.foreldrepenger.autotest.klienter.vtp.saf.modell;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Journalpost(String journalpostId,
        String journalposttype,
        String journalstatus,
        LocalDateTime datoOpprettet,
        String tittel,
        String kanal,
        String tema,
        String behandlingstema,
        String journalfoerendeEnhet,
        String eksternReferanseId,
        Bruker bruker,
        AvsenderMottaker avsenderMottaker,
        Sak sak,
        List<DokumentInfo> dokumenter) {

    public boolean harArkivsaksnummer() {
        if (sak == null) {
            return false;
        }
        return (sak.arkivsaksnummer() != null) && !sak.arkivsaksnummer().trim().isEmpty();
    }
}
