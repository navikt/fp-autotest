package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidInntektsmeldingAksjonspunktÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidsforholdKomplettVurderingType;

public record InntektsmeldingDto(BigDecimal inntektPrMnd,
                                 BigDecimal refusjonPrMnd,
                                 String arbeidsgiverIdent,
                                 String eksternArbeidsforholdId,
                                 String internArbeidsforholdId,
                                 String kontaktpersonNavn,
                                 String kontaktpersonNummer,
                                 String journalpostId,
                                 String dokumentId,
                                 LocalDate mottattDato,
                                 LocalDateTime innsendingstidspunkt,
                                 ArbeidInntektsmeldingAksjonspunktÅrsak årsak,
                                 String begrunnelse,
                                 ArbeidsforholdKomplettVurderingType saksbehandlersVurdering){}
