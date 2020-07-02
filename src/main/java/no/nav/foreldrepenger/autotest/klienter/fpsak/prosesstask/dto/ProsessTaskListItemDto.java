package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProsessTaskListItemDto {

    private int id;
    private String taskType;
    private String nesteKjøringEtter;
    private String gruppe;
    private String sekvens;
    private String status;
    private String sistKjørt;
    private String sisteFeilKode;
    private TaskParametereDto taskParametre;

    public ProsessTaskListItemDto(int id, String taskType, String nesteKjøringEtter, String gruppe,
                                  String sekvens,String status, String sistKjørt, String sisteFeilKode,
                                  TaskParametereDto taskParametre) {
        this.id = id;
        this.taskType = taskType;
        this.nesteKjøringEtter = nesteKjøringEtter;
        this.gruppe = gruppe;
        this.sekvens = sekvens;
        this.status = status;
        this.sistKjørt = sistKjørt;
        this.sisteFeilKode = sisteFeilKode;
        this.taskParametre = taskParametre;
    }

    public int getId() {
        return id;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getNesteKjøringEtter() {
        return nesteKjøringEtter;
    }

    public String getGruppe() {
        return gruppe;
    }

    public String getSekvens() {
        return sekvens;
    }

    public String getStatus() {
        return status;
    }

    public String getSistKjørt() {
        return sistKjørt;
    }

    public String getSisteFeilKode() {
        return sisteFeilKode;
    }

    public TaskParametereDto getTaskParametre() {
        return taskParametre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProsessTaskListItemDto that = (ProsessTaskListItemDto) o;
        return id == that.id &&
                Objects.equals(taskType, that.taskType) &&
                Objects.equals(nesteKjøringEtter, that.nesteKjøringEtter) &&
                Objects.equals(gruppe, that.gruppe) &&
                Objects.equals(sekvens, that.sekvens) &&
                Objects.equals(status, that.status) &&
                Objects.equals(sistKjørt, that.sistKjørt) &&
                Objects.equals(sisteFeilKode, that.sisteFeilKode) &&
                Objects.equals(taskParametre, that.taskParametre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskType, nesteKjøringEtter, gruppe, sekvens, status, sistKjørt, sisteFeilKode, taskParametre);
    }

    @Override
    public String toString() {
        return "ProsessTaskListItemDto{" +
                "id=" + id +
                ", taskType='" + taskType + '\'' +
                ", nesteKjøringEtter='" + nesteKjøringEtter + '\'' +
                ", gruppe='" + gruppe + '\'' +
                ", sekvens='" + sekvens + '\'' +
                ", status='" + status + '\'' +
                ", sistKjørt='" + sistKjørt + '\'' +
                ", sisteFeilKode='" + sisteFeilKode + '\'' +
                ", taskParametre=" + taskParametre +
                '}';
    }
}
