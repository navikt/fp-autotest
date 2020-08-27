package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FaktaOmBeregningAndelDto {

    private Long andelsnr;
    private BeregningsgrunnlagArbeidsforholdDto arbeidsforhold;
    private Kode inntektskategori;
    private Kode aktivitetStatus;
    private boolean lagtTilAvSaksbehandler;
    private boolean fastsattAvSaksbehandler;
    private List<Double> andelIArbeid;

    public Long getAndelsnr() {
        return andelsnr;
    }

    public BeregningsgrunnlagArbeidsforholdDto getArbeidsforhold() {
        return arbeidsforhold;
    }

    public Kode getInntektskategori() {
        return inntektskategori;
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

    public void setAndelsnr(Long andelsnr) {
        this.andelsnr = andelsnr;
    }

    public void setArbeidsforhold(BeregningsgrunnlagArbeidsforholdDto arbeidsforhold) {
        this.arbeidsforhold = arbeidsforhold;
    }

    public void setInntektskategori(Kode inntektskategori) {
        this.inntektskategori = inntektskategori;
    }

    public void setAktivitetStatus(Kode aktivitetStatus) {
        this.aktivitetStatus = aktivitetStatus;
    }

    public void setLagtTilAvSaksbehandler(boolean lagtTilAvSaksbehandler) {
        this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
    }

    public void setFastsattAvSaksbehandler(boolean fastsattAvSaksbehandler) {
        this.fastsattAvSaksbehandler = fastsattAvSaksbehandler;
    }

    public void setAndelIArbeid(List<Double> andelIArbeid) {
        this.andelIArbeid = andelIArbeid;
    }
}
