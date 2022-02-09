package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding;

import java.util.List;

public record InntektDto(String arbeidsgiverIdent, List<InntektspostDto> inntekter) {}
