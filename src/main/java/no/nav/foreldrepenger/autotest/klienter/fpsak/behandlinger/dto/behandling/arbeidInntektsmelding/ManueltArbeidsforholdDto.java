package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding;

import java.time.LocalDate;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidsforholdKomplettVurderingType;

public record ManueltArbeidsforholdDto(UUID behandlingUuid,
                                       String begrunnelse,
                                       String arbeidsgiverIdent,
                                       String internArbeidsforholdRef,
                                       String arbeidsgiverNavn,
                                       LocalDate fom,
                                       LocalDate tom,
                                       Integer stillingsprosent,
                                       ArbeidsforholdKomplettVurderingType vurdering) {}
