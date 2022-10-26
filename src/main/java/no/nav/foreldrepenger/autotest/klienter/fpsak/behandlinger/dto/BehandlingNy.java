package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public record BehandlingNy(@NotNull Saksnummer saksnummer,
                           @NotNull BehandlingType behandlingType,
                           BehandlingÅrsakType behandlingArsakType,
                           boolean nyBehandlingEtterKlage) {
}
