package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidInntektsmeldingAksjonspunktÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidsforholdKomplettVurderingType;

public record ArbeidsforholdDto(String arbeidsgiverIdent,
                                String internArbeidsforholdId,
                                String eksternArbeidsforholdId,
                                LocalDate fom,
                                LocalDate tom,
                                BigDecimal stillingsprosent,
                                ArbeidInntektsmeldingAksjonspunktÅrsak årsak,
                                ArbeidsforholdKomplettVurderingType saksbehandlersVurdering,
                                PermisjonUtenSluttdatoDto permisjonUtenSluttdatoDto,
                                String begrunnelse){}
