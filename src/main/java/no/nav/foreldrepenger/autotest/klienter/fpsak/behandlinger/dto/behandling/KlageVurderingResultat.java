package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import jakarta.validation.constraints.NotNull;

public record KlageVurderingResultat(@NotNull String klageVurdertAv,
                                     String klageVurdering,
                                     String begrunnelse,
                                     String klageMedholdÅrsak,
                                     String klageVurderingOmgjør,
                                     String klageHjemmel,
                                     String fritekstTilBrev) {

}
