package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;

public record BehandlingÅrsak(BehandlingÅrsakType behandlingArsakType, boolean manueltOpprettet) {
}
