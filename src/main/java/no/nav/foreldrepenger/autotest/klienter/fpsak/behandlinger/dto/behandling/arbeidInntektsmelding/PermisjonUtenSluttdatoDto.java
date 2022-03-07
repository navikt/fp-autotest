package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BekreftetPermisjonStatus;

import java.time.LocalDate;

public record PermisjonUtenSluttdatoDto(LocalDate permisjonFom,
                                        BekreftetPermisjonStatus permisjonStatus){}

