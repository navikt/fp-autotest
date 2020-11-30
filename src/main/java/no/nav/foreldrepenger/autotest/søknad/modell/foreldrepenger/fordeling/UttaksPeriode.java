package no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.ProsentAndel;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = GradertUttaksPeriode.class, name = "gradert")
})
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UttaksPeriode extends LukketPeriodeMedVedlegg {
    private StønadskontoType uttaksperiodeType;
    private boolean ønskerSamtidigUttak;
    private MorsAktivitet morsAktivitetsType;
    private boolean ønskerFlerbarnsdager;
    private ProsentAndel samtidigUttakProsent;

    @Builder
    @JsonCreator
    public UttaksPeriode(@JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom,
            @JsonProperty("uttaksperiodeType") @NotNull StønadskontoType uttaksperiodeType,
            @JsonProperty("ønskerSamtidigUttak") boolean ønskerSamtidigUttak,
            @JsonProperty("morsAktivitetsType") MorsAktivitet morsAktivitetsType,
            @JsonProperty("ønskerFlerbarnsdager") boolean ønskerFlerbarnsdager,
            @JsonProperty("samtidigUttakProsent") ProsentAndel samtidigUttakProsent,
            @JsonProperty("vedlegg") List<String> vedlegg) {
        super(fom, tom, vedlegg);
        this.uttaksperiodeType = uttaksperiodeType;
        this.ønskerSamtidigUttak = ønskerSamtidigUttak;
        this.morsAktivitetsType = morsAktivitetsType;
        this.ønskerFlerbarnsdager = ønskerFlerbarnsdager;
        this.samtidigUttakProsent = samtidigUttakProsent;
    }
}
