package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProsesstaskDto {

    private int prosessTaskId;
    private String naaVaaerendeStatus;

    public ProsesstaskDto(int prosessTaskId, String naaVaaerendeStatus) {
        super();
        this.prosessTaskId = prosessTaskId;
        this.naaVaaerendeStatus = naaVaaerendeStatus;
    }

    public int getProsessTaskId() {
        return prosessTaskId;
    }

    public String getNaaVaaerendeStatus() {
        return naaVaaerendeStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProsesstaskDto that = (ProsesstaskDto) o;
        return prosessTaskId == that.prosessTaskId &&
                Objects.equals(naaVaaerendeStatus, that.naaVaaerendeStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prosessTaskId, naaVaaerendeStatus);
    }

    @Override
    public String toString() {
        return "ProsesstaskDto{" +
                "prosessTaskId=" + prosessTaskId +
                ", naaVaaerendeStatus='" + naaVaaerendeStatus + '\'' +
                '}';
    }
}
