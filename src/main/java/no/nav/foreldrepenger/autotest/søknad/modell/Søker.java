package no.nav.foreldrepenger.autotest.søknad.modell;

import static no.nav.foreldrepenger.autotest.søknad.modell.felles.SpråkKode.defaultSpråk;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.SpråkKode;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Søker {
    private final BrukerRolle søknadsRolle;
    private final SpråkKode språkkode;

    public Søker(@JsonProperty("søknadsRolle") BrukerRolle søknadsRolle) {
        this(søknadsRolle, defaultSpråk());
    }

    @JsonCreator
    public Søker(@JsonProperty("søknadsRolle") BrukerRolle søknadsRolle,
            @JsonProperty("språkkode") SpråkKode språkkode) {
        this.søknadsRolle = søknadsRolle;
        this.språkkode = Optional.ofNullable(språkkode).orElse(defaultSpråk());
    }

}
