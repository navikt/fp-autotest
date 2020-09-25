package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskParametereDto {
    private String callId;
    private String fagsakId;
    private String behandlingId;
    private String aktoerId;
    @JsonProperty("batch.runner.name")
    private String batchrunnername;

    public TaskParametereDto(String batchrunnername){
        this.batchrunnername = batchrunnername;
    }

    @JsonCreator
    public TaskParametereDto(String callId, String fagsakId, String behandlingId, String aktoerId, String batchrunnername) {
        this.callId = callId;
        this.fagsakId = fagsakId;
        this.behandlingId = behandlingId;
        this.aktoerId = aktoerId;
        this.batchrunnername = batchrunnername;
    }

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

    public String getBatchrunnername() {
        return batchrunnername;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskParametereDto that = (TaskParametereDto) o;
        return Objects.equals(callId, that.callId) &&
                Objects.equals(fagsakId, that.fagsakId) &&
                Objects.equals(behandlingId, that.behandlingId) &&
                Objects.equals(aktoerId, that.aktoerId) &&
                Objects.equals(batchrunnername, that.batchrunnername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callId, fagsakId, behandlingId, aktoerId, batchrunnername);
    }

    @Override
    public String toString() {
        return "TaskParametereDto{" +
                "callId='" + callId + '\'' +
                ", fagsakId='" + fagsakId + '\'' +
                ", behandlingId='" + behandlingId + '\'' +
                ", aktoerId='" + aktoerId + '\'' +
                ", batchrunnername='" + batchrunnername + '\'' +
                '}';
    }
}
