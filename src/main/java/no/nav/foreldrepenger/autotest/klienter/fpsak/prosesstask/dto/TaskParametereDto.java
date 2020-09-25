package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskParametereDto {
    protected String callId;
    protected String fagsakId;
    protected String behandlingId;
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
