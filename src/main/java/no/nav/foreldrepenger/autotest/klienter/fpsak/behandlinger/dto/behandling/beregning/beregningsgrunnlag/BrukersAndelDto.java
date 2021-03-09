package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BrukersAndelDto {

    protected int andelsnr;
    protected BeregningsgrunnlagArbeidsforholdDto arbeidsforhold;
    protected Inntektskategori inntektskategori;
    protected AktivitetStatus aktivitetStatus;
    protected boolean lagtTilAvSaksbehandler;
    protected boolean fastsattAvSaksbehandler;
    protected List<Double> andelIArbeid;
    protected Double fastsattBelopPrMnd;

    public int getAndelsnr() {
        return andelsnr;
    }

    public BeregningsgrunnlagArbeidsforholdDto getArbeidsforhold() {
        return arbeidsforhold;
    }

    public Inntektskategori getInntektskategori() {
        return inntektskategori;
    }

    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
    }

    public boolean isLagtTilAvSaksbehandler() {
        return lagtTilAvSaksbehandler;
    }

    public boolean isFastsattAvSaksbehandler() {
        return fastsattAvSaksbehandler;
    }

    public List<Double> getAndelIArbeid() {
        return andelIArbeid;
    }

    public Double getFastsattBelopPrMnd() {
        return fastsattBelopPrMnd;
    }
}
