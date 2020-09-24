package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskParametereDto {
    @JsonIgnore
    protected String callId;
    @JsonIgnore
    protected String fagsakId;
    @JsonIgnore
    protected String behandlingId;
    @JsonIgnore
    protected String aktoerId;
    @JsonProperty("batch.runner.name")
    protected String batchrunnername;

    public String getCallId() {
        return callId;
    }

    public String getFagsakId() {
        return fagsakId;
    }

    public String getBehandlingId() {
        return behandlingId;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    public TaskParametereDto(String batchrunnername){
        this.batchrunnername = batchrunnername;
    }
}
