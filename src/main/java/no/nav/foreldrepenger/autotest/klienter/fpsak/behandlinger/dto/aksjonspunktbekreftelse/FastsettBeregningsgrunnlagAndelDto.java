package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.FordelBeregningsgrunnlagAndelDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FastsettBeregningsgrunnlagAndelDto extends RedigerbarAndel {

    protected FastsatteVerdierDto fastsatteVerdier;

    public FastsettBeregningsgrunnlagAndelDto(String andel, int andelsnr, String arbeidsforholdId, Boolean nyAndel, Boolean lagtTilAvSaksbehandler) {
        super(andel, andelsnr, arbeidsforholdId, nyAndel, lagtTilAvSaksbehandler);
    }

    public FastsettBeregningsgrunnlagAndelDto(FordelBeregningsgrunnlagAndelDto andelDto) {
        super("", andelDto.getAndelsnr(), andelDto.getArbeidsforhold() == null ? null : andelDto.getArbeidsforhold().getArbeidsforholdId(), false, andelDto.isLagtTilAvSaksbehandler());
    }

    public void setFastsatteVerdier(FastsatteVerdierDto fastsatteVerdier) {
        this.fastsatteVerdier = fastsatteVerdier;
    }
}
