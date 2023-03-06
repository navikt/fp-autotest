package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.InntektspostType;

public record InntektspostDto (BigDecimal beløp, LocalDate fom, LocalDate tom, InntektspostType type){}
