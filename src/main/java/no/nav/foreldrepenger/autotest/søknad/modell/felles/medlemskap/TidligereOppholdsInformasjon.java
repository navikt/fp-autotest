package no.nav.foreldrepenger.autotest.søknad.modell.felles.medlemskap;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@ToString(exclude = "arbeidSiste12")
@EqualsAndHashCode(exclude = "arbeidSiste12")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TidligereOppholdsInformasjon {

    private final ArbeidsInformasjon arbeidSiste12;

    @Valid
    private final List<Utenlandsopphold> utenlandsOpphold;

    @JsonCreator
    public TidligereOppholdsInformasjon(
            @JsonProperty("arbeidSiste12") ArbeidsInformasjon arbeidSiste12,
            @JsonProperty("utenlandsOpphold") List<Utenlandsopphold> utenlandsOpphold) {
        this.arbeidSiste12 = arbeidSiste12;
        this.utenlandsOpphold = Optional.ofNullable(utenlandsOpphold).orElse(emptyList());
    }

    @JsonIgnore
    public boolean isBoddINorge() {
        return utenlandsOpphold.isEmpty();
    }
}
