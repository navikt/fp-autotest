package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.FordelBeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FastsettBeregningsgrunnlagAndelDto extends RedigerbarAndel {

    protected FastsatteVerdierDto fastsatteVerdier;
    protected Kode forrigeInntektskategori;
    protected Integer forrigeRefusjonPrÅr;
    protected Integer forrigeArbeidsinntektPrÅr;

    public FastsettBeregningsgrunnlagAndelDto(String andel, int andelsnr, String arbeidsforholdId,
            String arbeidsgiverId, Boolean nyAndel,
            Boolean lagtTilAvSaksbehandler, Kode aktivitetStatus, LocalDate beregningsperiodeFom,
            LocalDate beregningsperiodeTom, Kode arbeidsforholdType) {
        super(andel, andelsnr, arbeidsgiverId, arbeidsforholdId, nyAndel, lagtTilAvSaksbehandler, aktivitetStatus,
                beregningsperiodeFom, beregningsperiodeTom, arbeidsforholdType);
    }

    public FastsettBeregningsgrunnlagAndelDto(FordelBeregningsgrunnlagAndelDto andelDto,
            BeregningsgrunnlagPrStatusOgAndelDto bgAndelDto) {
        super("", andelDto.getAndelsnr(),
                andelDto.getArbeidsforhold() == null ? null : andelDto.getArbeidsforhold().getArbeidsgiverId(),
                andelDto.getArbeidsforhold() == null ? null : andelDto.getArbeidsforhold().getArbeidsforholdId(),
                false, andelDto.isLagtTilAvSaksbehandler(),
                andelDto.getAktivitetStatus(),
                bgAndelDto.getBeregningsperiodeFom(),
                bgAndelDto.getBeregningsperiodeTom(),
                andelDto.getArbeidsforholdType());
        forrigeArbeidsinntektPrÅr = andelDto.getFordeltPrAar() == null ? null : andelDto.getFordeltPrAar().intValue();
        forrigeInntektskategori = andelDto.getInntektskategori();
        forrigeRefusjonPrÅr = andelDto.getRefusjonskravPrAar() == null ? null
                : andelDto.getRefusjonskravPrAar().intValue();

    }

    public void setFastsatteVerdier(FastsatteVerdierDto fastsatteVerdier) {
        this.fastsatteVerdier = fastsatteVerdier;
    }
}
