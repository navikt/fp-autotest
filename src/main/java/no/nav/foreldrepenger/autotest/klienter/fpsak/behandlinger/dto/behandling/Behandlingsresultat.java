package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import java.util.List;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.KonsekvensForYtelsen;

public record Behandlingsresultat(Integer id,
                                 BehandlingResultatType type,
                                 Avslagsårsak avslagsarsak,
                                 String rettenTil,
                                 List<KonsekvensForYtelsen> konsekvenserForYtelsen,
                                 String avslagsarsakFritekst,
                                 String overskrift,
                                 String fritekstbrev,
                                 SkjæringstidspunktDto skjæringstidspunkt) {

}
