package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProsessTaskStatusDto {

    private String prosessTaskStatusName;

    public ProsessTaskStatusDto(@JsonProperty("prosessTaskStatusName") String prosessTaskStatusName) {
        this.prosessTaskStatusName = prosessTaskStatusName;
    }

    public String getProsessTaskStatusName() {
        return prosessTaskStatusName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProsessTaskStatusDto that = (ProsessTaskStatusDto) o;
        return Objects.equals(prosessTaskStatusName, that.prosessTaskStatusName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prosessTaskStatusName);
    }

    @Override
    public String toString() {
        return "ProsessTaskStatusDto{" +
                "prosessTaskStatusName='" + prosessTaskStatusName + '\'' +
                '}';
    }
}
