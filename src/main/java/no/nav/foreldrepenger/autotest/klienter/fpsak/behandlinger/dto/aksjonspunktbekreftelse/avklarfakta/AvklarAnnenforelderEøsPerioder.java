package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;

public class AvklarAnnenforelderEøsPerioder extends AksjonspunktBekreftelse {

    @Valid
    @NotNull
    @Size(max = 200)
    private List<@Valid @NotNull EøsUttakPeriodeDto> perioder;

    public List<EøsUttakPeriodeDto> getPerioder() {
        return perioder;
    }

    public record EøsUttakPeriodeDto(@NotNull LocalDate fom,
                                     @NotNull LocalDate tom,
                                     @NotNull @Min(0) @Max(1000) @Digits(integer = 3, fraction = 1) BigDecimal trekkdager,
                                     @NotNull StønadskontoType trekkonto) {
    }

    public AvklarAnnenforelderEøsPerioder setPerioder(List<EøsUttakPeriodeDto> perioder) {
        this.perioder = perioder;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5103";
    }
}
