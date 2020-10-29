package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class HistorikkInnslagDokumentLinkDto {

    private String tag;
    private URI url;
    private String journalpostId;
    private String dokumentId;
    private boolean utgått;

    public HistorikkInnslagDokumentLinkDto(String tag, URI url, String journalpostId, String dokumentId, boolean utgått) {
        this.tag = tag;
        this.url = url;
        this.journalpostId = journalpostId;
        this.dokumentId = dokumentId;
        this.utgått = utgått;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public void setJournalpostId(String journalpostId) {
        this.journalpostId = journalpostId;
    }

    public String getDokumentId() {
        return dokumentId;
    }

    public void setDokumentId(String dokumentId) {
        this.dokumentId = dokumentId;
    }

    public boolean isUtgått() {
        return utgått;
    }

    public void setUtgått(boolean utgått) {
        this.utgått = utgått;
    }
}
