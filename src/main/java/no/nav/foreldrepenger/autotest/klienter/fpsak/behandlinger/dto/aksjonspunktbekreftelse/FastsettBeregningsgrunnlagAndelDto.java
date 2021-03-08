package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.FordelBeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.RedigerbarAndelDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FastsettBeregningsgrunnlagAndelDto extends RedigerbarAndelDto {

    private FastsatteVerdierDto fastsatteVerdier;
    private final Inntektskategori forrigeInntektskategori;
    private final Integer forrigeRefusjonPrÅr;
    private final Integer forrigeArbeidsinntektPrÅr;

    public FastsettBeregningsgrunnlagAndelDto(FordelBeregningsgrunnlagAndelDto andelDto, BeregningsgrunnlagPrStatusOgAndelDto bgAndelDto) {
        super(andelDto.getAndelsnr(),
                andelDto.getArbeidsforhold() == null ? null : andelDto.getArbeidsforhold().getArbeidsgiverId(),
                andelDto.getArbeidsforhold() == null ? null : andelDto.getArbeidsforhold().getArbeidsforholdId(),
                false,
                andelDto.getKilde(),
                andelDto.getAktivitetStatus(),
                andelDto.getArbeidsforholdType(),
                andelDto.getLagtTilAvSaksbehandler(),
                bgAndelDto.getBeregningsperiodeFom(),
                bgAndelDto.getBeregningsperiodeTom());
            forrigeArbeidsinntektPrÅr = andelDto.getFordeltPrAar() == null ? null : andelDto.getFordeltPrAar().intValue();
            forrigeInntektskategori = andelDto.getInntektskategori();
            forrigeRefusjonPrÅr = andelDto.getRefusjonskravPrAar() == null ?
                    null : andelDto.getRefusjonskravPrAar().intValue();
    }

    public void setFastsatteVerdier(FastsatteVerdierDto fastsatteVerdier) {
        this.fastsatteVerdier = fastsatteVerdier;
    }
}
