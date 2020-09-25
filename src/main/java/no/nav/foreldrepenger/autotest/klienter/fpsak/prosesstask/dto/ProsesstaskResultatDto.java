package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProsesstaskResultatDto {

    private int prosessTaskId;
    private String prosessTaskStatus;
    private String nesteKjoeretidspunkt;

    public ProsesstaskResultatDto(int prosessTaskId, String prosessTaskStatus, String nesteKjoeretidspunkt) {
        this.prosessTaskId = prosessTaskId;
        this.prosessTaskStatus = prosessTaskStatus;
        this.nesteKjoeretidspunkt = nesteKjoeretidspunkt;
    }

    public int getProsessTaskId() {
        return prosessTaskId;
    }

    public String getProsessTaskStatus() {
        return prosessTaskStatus;
    }

    public String getNesteKjoeretidspunkt() {
        return nesteKjoeretidspunkt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProsesstaskResultatDto that = (ProsesstaskResultatDto) o;
        return prosessTaskId == that.prosessTaskId &&
                Objects.equals(prosessTaskStatus, that.prosessTaskStatus) &&
                Objects.equals(nesteKjoeretidspunkt, that.nesteKjoeretidspunkt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prosessTaskId, prosessTaskStatus, nesteKjoeretidspunkt);
    }

    @Override
    public String toString() {
        return "ProsesstaskResultatDto{" +
                "prosessTaskId=" + prosessTaskId +
                ", prosessTaskStatus='" + prosessTaskStatus + '\'' +
                ", nesteKjoeretidspunkt='" + nesteKjoeretidspunkt + '\'' +
                '}';
    }
}
