package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding;

import java.util.UUID;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidsforholdKomplettVurderingType;

public class ManglendeOpplysningerVurderingDto {
    private UUID behandlingUuid;
    private ArbeidsforholdKomplettVurderingType vurdering;
    private String begrunnelse;
    private String arbeidsgiverIdent;
    private String internArbeidsforholdRef;

    public ManglendeOpplysningerVurderingDto(UUID behandlingUuid,
                                             ArbeidsforholdKomplettVurderingType vurdering,
                                             String begrunnelse,
                                             String arbeidsgiverIdent,
                                             String internArbeidsforholdRef) {
        this.behandlingUuid = behandlingUuid;
        this.vurdering = vurdering;
        this.begrunnelse = begrunnelse;
        this.arbeidsgiverIdent = arbeidsgiverIdent;
        this.internArbeidsforholdRef = internArbeidsforholdRef;
    }
}
