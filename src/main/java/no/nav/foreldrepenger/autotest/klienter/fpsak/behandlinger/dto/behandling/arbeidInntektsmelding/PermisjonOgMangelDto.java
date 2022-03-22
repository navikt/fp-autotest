package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AksjonspunktÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BekreftetPermisjonStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PermisjonsbeskrivelseType;

import java.time.LocalDate;

public record PermisjonOgMangelDto(LocalDate permisjonFom,
                                   LocalDate permisjonTom,
                                   PermisjonsbeskrivelseType type,
                                   AksjonspunktÅrsak årsak,
                                   BekreftetPermisjonStatus permisjonStatus){}

