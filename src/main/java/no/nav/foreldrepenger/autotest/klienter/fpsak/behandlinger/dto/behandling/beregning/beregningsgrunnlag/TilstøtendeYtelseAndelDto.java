package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TilstøtendeYtelseAndelDto {

    protected int andelsnr;
    protected BeregningsgrunnlagArbeidsforholdDto arbeidsforhold;
    protected Inntektskategori inntektskategori;
    protected AktivitetStatus aktivitetStatus;
    protected boolean lagtTilAvSaksbehandler;
    protected boolean fastsattAvSaksbehandler;
    protected List<Double> andelIArbeid;
    protected double fordelingForrigeYtelse;
    protected double refusjonskrav;
    protected double fastsattPrAar;
}
