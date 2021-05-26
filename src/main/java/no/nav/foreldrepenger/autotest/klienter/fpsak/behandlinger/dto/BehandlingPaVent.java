package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Venteårsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingPaVent extends BehandlingIdPost {

    protected LocalDate frist;
    protected Venteårsak ventearsak;

    public BehandlingPaVent(UUID behandlingUuid, int behandlingVersjon, LocalDate frist, Venteårsak ventearsak) {
        super(behandlingUuid, behandlingVersjon);
        this.frist = frist;
        this.ventearsak = ventearsak;
    }

    public BehandlingPaVent(Behandling behandling, LocalDate frist, Venteårsak ventearsak) {
        this(behandling.uuid, behandling.versjon, frist, ventearsak);
    }
}
