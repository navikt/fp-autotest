package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding;

import java.time.LocalDate;
import java.util.List;

public record ArbeidOgInntektsmeldingDto(List<InntektsmeldingDto> inntektsmeldinger,
                                         List<ArbeidsforholdDto> arbeidsforhold,
                                         List<InntektDto> inntekter,
                                         LocalDate skjæringstidspunkt){}
