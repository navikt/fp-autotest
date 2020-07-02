package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Referanse til en behandling. Enten {@link #behandlingId} eller
 * {@link #behandlingUuid} vil være satt.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BehandlingIdDto {

    private Long saksnummer;
    private Long behandlingId;
    private UUID behandlingUuid;

    public BehandlingIdDto() {
        behandlingId = null; // NOSONAR
    }

    public BehandlingIdDto(String id) {
        Objects.requireNonNull(id, "behandlingId");
        if (id.contains("-")) {
            this.behandlingUuid = UUID.fromString(id);
        } else {
            this.behandlingId = Long.valueOf(id);
        }
    }

    @JsonCreator
    public BehandlingIdDto(Long saksnummer, Long behandlingId, UUID behandlingUuid) {
        this.saksnummer = saksnummer;
        this.behandlingId = behandlingId;
        this.behandlingUuid = behandlingUuid;
    }

    public BehandlingIdDto(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    /**
     * Denne er kun intern nøkkel, bør ikke eksponeres ut men foreløpig støttes både
     * Long id og UUID id for behandling på grensesnittene.
     */
    public Long getBehandlingId() {
        return behandlingId;
    }

    public UUID getBehandlingUuid() {
        return behandlingUuid;
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BehandlingIdDto that = (BehandlingIdDto) o;
        return Objects.equals(saksnummer, that.saksnummer) &&
                Objects.equals(behandlingId, that.behandlingId) &&
                Objects.equals(behandlingUuid, that.behandlingUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saksnummer, behandlingId, behandlingUuid);
    }

    @Override
    public String toString() {
        return "BehandlingIdDto{" +
                "saksnummer=" + saksnummer +
                ", behandlingId=" + behandlingId +
                ", behandlingUuid=" + behandlingUuid +
                '}';
    }
}
