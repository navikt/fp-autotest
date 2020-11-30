package no.nav.foreldrepenger.autotest.søknad.modell.felles.medlemskap;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({ "arbeidSiste12", "utenlandsopphold", "framtidigUtenlandsopphold" })
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Medlemsskap {

    private ArbeidsInformasjon arbeidSiste12;
    private List<Utenlandsopphold> utenlandsopphold;
    private List<Utenlandsopphold> framtidigUtenlandsopphold;

}
