package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AndelKilde;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FaktaOmBeregningAndelDto {

    private Long andelsnr;
    private BeregningsgrunnlagArbeidsforholdDto arbeidsforhold;
    private Inntektskategori inntektskategori;
    private AktivitetStatus aktivitetStatus;
    private AndelKilde kilde;
    private final Boolean lagtTilAvSaksbehandler = false;
    private final Boolean fastsattAvSaksbehandler = false;
    private final List<BigDecimal> andelIArbeid = new ArrayList();

    public Long getAndelsnr() {
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

    public AndelKilde getKilde() {
        return kilde;
    }

    public Boolean getLagtTilAvSaksbehandler() {
        return lagtTilAvSaksbehandler;
    }

    public Boolean getFastsattAvSaksbehandler() {
        return fastsattAvSaksbehandler;
    }

    public List<BigDecimal> getAndelIArbeid() {
        return andelIArbeid;
    }
}
