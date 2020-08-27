package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.FordelBeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettBeregningsgrunnlagAndelDto extends RedigerbarAndel {

    private FastsatteVerdierDto fastsatteVerdier;
    private Kode forrigeInntektskategori;
    private Integer forrigeRefusjonPrÅr;
    private Integer forrigeArbeidsinntektPrÅr;

    public FastsettBeregningsgrunnlagAndelDto(FordelBeregningsgrunnlagAndelDto andelDto,
                                              BeregningsgrunnlagPrStatusOgAndelDto bgAndelDto) {
        this(andelDto.getAndelsnr(),
                andelDto.getArbeidsforhold() == null ? null : andelDto.getArbeidsforhold().getArbeidsgiverId(),
                andelDto.getArbeidsforhold() == null ? null : andelDto.getArbeidsforhold().getArbeidsforholdId(),
                false, andelDto.getAktivitetStatus(), andelDto.getArbeidsforholdType(),
                andelDto.isLagtTilAvSaksbehandler(), bgAndelDto.getBeregningsperiodeFom(),
                bgAndelDto.getBeregningsperiodeTom(), null, andelDto.getInntektskategori(),
                andelDto.getRefusjonskravPrAar() == null ? null: andelDto.getRefusjonskravPrAar().intValue(),
                andelDto.getFordeltPrAar() == null ? null : andelDto.getFordeltPrAar().intValue());
    }

    @JsonCreator
    public FastsettBeregningsgrunnlagAndelDto(Long andelsnr, String arbeidsgiverId, String arbeidsforholdId,
                                              Boolean nyAndel, Kode aktivitetStatus, Kode arbeidsforholdType,
                                              Boolean lagtTilAvSaksbehandler, LocalDate beregningsperiodeFom,
                                              LocalDate beregningsperiodeTom, FastsatteVerdierDto fastsatteVerdier,
                                              Kode forrigeInntektskategori, Integer forrigeRefusjonPrÅr,
                                              Integer forrigeArbeidsinntektPrÅr) {
        super(andelsnr, arbeidsgiverId, arbeidsforholdId, nyAndel, aktivitetStatus, arbeidsforholdType,
                lagtTilAvSaksbehandler, beregningsperiodeFom, beregningsperiodeTom);
        this.fastsatteVerdier = fastsatteVerdier;
        this.forrigeInntektskategori = forrigeInntektskategori;
        this.forrigeRefusjonPrÅr = forrigeRefusjonPrÅr;
        this.forrigeArbeidsinntektPrÅr = forrigeArbeidsinntektPrÅr;
    }

    public FastsatteVerdierDto getFastsatteVerdier() {
        return fastsatteVerdier;
    }

    public Kode getForrigeInntektskategori() {
        return forrigeInntektskategori;
    }

    public Integer getForrigeRefusjonPrÅr() {
        return forrigeRefusjonPrÅr;
    }

    public Integer getForrigeArbeidsinntektPrÅr() {
        return forrigeArbeidsinntektPrÅr;
    }

    public void setFastsatteVerdier(FastsatteVerdierDto fastsatteVerdier) {
        this.fastsatteVerdier = fastsatteVerdier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsettBeregningsgrunnlagAndelDto that = (FastsettBeregningsgrunnlagAndelDto) o;
        return Objects.equals(fastsatteVerdier, that.fastsatteVerdier) &&
                Objects.equals(forrigeInntektskategori, that.forrigeInntektskategori) &&
                Objects.equals(forrigeRefusjonPrÅr, that.forrigeRefusjonPrÅr) &&
                Objects.equals(forrigeArbeidsinntektPrÅr, that.forrigeArbeidsinntektPrÅr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fastsatteVerdier, forrigeInntektskategori, forrigeRefusjonPrÅr, forrigeArbeidsinntektPrÅr);
    }

    @Override
    public String toString() {
        return "FastsettBeregningsgrunnlagAndelDto{" +
                "fastsatteVerdier=" + fastsatteVerdier +
                ", forrigeInntektskategori=" + forrigeInntektskategori +
                ", forrigeRefusjonPrÅr=" + forrigeRefusjonPrÅr +
                ", forrigeArbeidsinntektPrÅr=" + forrigeArbeidsinntektPrÅr +
                "} " + super.toString();
    }
}
