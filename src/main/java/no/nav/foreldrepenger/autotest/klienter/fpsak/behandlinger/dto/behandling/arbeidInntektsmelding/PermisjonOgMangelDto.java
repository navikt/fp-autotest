package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AksjonspunktÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BekreftetPermisjonStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PermisjonsbeskrivelseType;

public record PermisjonOgMangelDto(LocalDate permisjonFom,
                                   LocalDate permisjonTom,
                                   PermisjonsbeskrivelseType type,
                                   AksjonspunktÅrsak årsak,
                                   BekreftetPermisjonStatus permisjonStatus){}

