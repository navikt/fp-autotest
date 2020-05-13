package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;
import java.util.UUID;

/**
 * Referanse til en behandling.
 * Enten {@link #behandlingId} eller {@link #behandlingUuid} vil være satt.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingIdDto {
    protected Long saksnummer;
    protected Long behandlingId;
    protected UUID behandlingUuid;

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

    public BehandlingIdDto(Long saksnummer, Long behandlingId, UUID behandlingUuid) {
        this.saksnummer = saksnummer;
        this.behandlingId = behandlingId;
        this.behandlingUuid = behandlingUuid;
    }

    public BehandlingIdDto(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    /**
     * Denne er kun intern nøkkel, bør ikke eksponeres ut men foreløpig støttes både Long id og UUID id for behandling på grensesnittene.
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

}
