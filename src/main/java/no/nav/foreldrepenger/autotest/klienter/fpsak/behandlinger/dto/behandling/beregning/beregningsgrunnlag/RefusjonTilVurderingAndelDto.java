package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefusjonTilVurderingAndelDto {
    private AktivitetStatus aktivitetStatus;
    private Arbeidsgiver arbeidsgiver;
    private String internArbeidsforholdRef;

    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
    }

    public String getInternArbeidsforholdRef() {
        return internArbeidsforholdRef;
    }

    public Arbeidsgiver getArbeidsgiver() {
        return arbeidsgiver;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Arbeidsgiver {
        private String arbeidsgiverOrgnr;
        public String getArbeidsgiverOrgnr() {
            return arbeidsgiverOrgnr;
        }
    }
}
