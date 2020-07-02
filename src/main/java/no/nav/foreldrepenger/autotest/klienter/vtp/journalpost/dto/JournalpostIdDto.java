package no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JournalpostIdDto {
    protected String journalpostId;

    public JournalpostIdDto() {

    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public void setJournalpostId(String journalpostId) {
        this.journalpostId = journalpostId;
    }

}
