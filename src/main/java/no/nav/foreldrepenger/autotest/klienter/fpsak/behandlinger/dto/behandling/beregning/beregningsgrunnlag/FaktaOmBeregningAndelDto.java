package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FaktaOmBeregningAndelDto {

    protected int andelsnr;
    protected BeregningsgrunnlagArbeidsforholdDto arbeidsforhold;
    protected Kode inntektskategori;
    protected Kode aktivitetStatus;
    protected boolean lagtTilAvSaksbehandler;
    protected boolean fastsattAvSaksbehandler;
    protected Kode kilde;
    protected List<Double> andelIArbeid;

    public int getAndelsnr() {
        return andelsnr;
    }

    public BeregningsgrunnlagArbeidsforholdDto getArbeidsforhold() {
        return arbeidsforhold;
    }

    public Kode getInntektskategori() {
        return inntektskategori;
    }

    public Kode getKilde() {
        return kilde;
    }

    public Kode getAktivitetStatus() {
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
}
