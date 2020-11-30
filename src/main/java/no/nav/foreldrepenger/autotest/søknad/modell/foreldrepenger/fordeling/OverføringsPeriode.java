package no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OverføringsPeriode extends LukketPeriodeMedVedlegg {

    private final Overføringsårsak årsak;
    private final StønadskontoType uttaksperiodeType;

    @Builder
    public OverføringsPeriode(LocalDate fom, LocalDate tom, @NotNull Overføringsårsak årsak,
            @NotNull StønadskontoType uttaksperiodeType, List<String> vedlegg) {
        super(fom, tom, vedlegg);
        this.årsak = årsak;
        this.uttaksperiodeType = uttaksperiodeType;
    }
}
