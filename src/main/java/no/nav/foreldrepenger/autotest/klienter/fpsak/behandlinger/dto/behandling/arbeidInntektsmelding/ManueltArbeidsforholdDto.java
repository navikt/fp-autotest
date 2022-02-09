package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidsforholdKomplettVurderingType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ManueltArbeidsforholdDto {
    private UUID behandlingUuid;
    private String begrunnelse;
    private String arbeidsgiverIdent;
    private String internArbeidsforholdRef;
    private String arbeidsgiverNavn;
    private LocalDate fom;
    private LocalDate tom;
    private Integer stillingsprosent;
    private ArbeidsforholdKomplettVurderingType vurdering;

    public ManueltArbeidsforholdDto(UUID behandlingUuid,
                                    String begrunnelse,
                                    String arbeidsgiverIdent,
                                    String internArbeidsforholdRef,
                                    String arbeidsgiverNavn,
                                    LocalDate fom,
                                    LocalDate tom,
                                    Integer stillingsprosent,
                                    ArbeidsforholdKomplettVurderingType vurdering) {
        this.behandlingUuid = behandlingUuid;
        this.begrunnelse = begrunnelse;
        this.arbeidsgiverIdent = arbeidsgiverIdent;
        this.internArbeidsforholdRef = internArbeidsforholdRef;
        this.arbeidsgiverNavn = arbeidsgiverNavn;
        this.fom = fom;
        this.tom = tom;
        this.stillingsprosent = stillingsprosent;
        this.vurdering = vurdering;
    }
}
