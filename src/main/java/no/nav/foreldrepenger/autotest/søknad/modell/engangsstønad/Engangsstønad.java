package no.nav.foreldrepenger.autotest.søknad.modell.engangsstønad;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.nav.foreldrepenger.autotest.søknad.modell.Ytelse;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder.AnnenForelder;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.relasjontilbarn.RelasjonTilBarn;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonPropertyOrder({ "medlemsskap", "relasjonTilBarn", "annenForelder" })
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Engangsstønad extends Ytelse {

    @Valid
    private Medlemsskap medlemsskap;
    @Valid
    private AnnenForelder annenForelder;
    @Valid
    private RelasjonTilBarn relasjonTilBarn;

    @JsonCreator
    public Engangsstønad(@JsonProperty("medlemsskap") Medlemsskap medlemsskap,
            @JsonProperty("relasjonTilBarn") RelasjonTilBarn relasjonTilBarn) {
        this.medlemsskap = medlemsskap;
        this.relasjonTilBarn = relasjonTilBarn;
    }
}
