package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding;

import java.util.UUID;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidsforholdKomplettVurderingType;

public record ManglendeOpplysningerVurderingDto(UUID behandlingUuid,
                                                ArbeidsforholdKomplettVurderingType vurdering,
                                                String begrunnelse,
                                                String arbeidsgiverIdent,
                                                String internArbeidsforholdRef,
                                                Long behandlingVersjon){}
