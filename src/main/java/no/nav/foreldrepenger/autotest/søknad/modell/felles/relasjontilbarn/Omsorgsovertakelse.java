package no.nav.foreldrepenger.autotest.søknad.modell.felles.relasjontilbarn;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Omsorgsovertakelse extends RelasjonTilBarn {

    private final LocalDate omsorgsovertakelsesdato;
    private final OmsorgsOvertakelsesÅrsak årsak;
    private final List<LocalDate> fødselsdato;
    private String beskrivelse;

    @Builder
    @JsonCreator
    public Omsorgsovertakelse(int antallBarn,
            LocalDate omsorgsovertakelsesdato,
            OmsorgsOvertakelsesÅrsak årsak,
            List<LocalDate> fødselsdato, List<String> vedlegg) {
        super(antallBarn, vedlegg);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.årsak = årsak;
        this.fødselsdato = fødselsdato;
    }

    @Override
    public LocalDate relasjonsDato() {
        return omsorgsovertakelsesdato;
    }
}
