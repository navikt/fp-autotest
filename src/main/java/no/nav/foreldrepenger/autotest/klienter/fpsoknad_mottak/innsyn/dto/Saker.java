package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto;

import java.util.Set;

// TODO: fpsoknad-mottak, foreldepengesoknad-api og autotest delere samme modell. Flytt denne til felles når ting blir stabilt/permanent
public record Saker(Set<FpSak> foreldrepenger,
                    Set<EsSak> engangsstønad,
                    Set<SvpSak> svangerskapspenger) {
}
