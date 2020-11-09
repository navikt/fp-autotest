package no.nav.foreldrepenger.autotest.søknad.modell.felles.opptjening;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Opptjening {

    @Valid
    private List<UtenlandskArbeidsforhold> utenlandskArbeidsforhold;
    @Valid
    private List<EgenNæring> egenNæring;
    @Valid
    private List<AnnenOpptjening> annenOpptjening;
    @Valid
    private Frilans frilans;

    @Builder
    @JsonCreator
    public Opptjening(@JsonProperty("arbeidsforhold") List<UtenlandskArbeidsforhold> utenlandskArbeidsforhold,
                      @JsonProperty("egenNæring") List<EgenNæring> egenNæring,
                      @JsonProperty("annenOpptjening") List<AnnenOpptjening> annenOpptjening,
                      @JsonProperty("frilans") Frilans frilans) {
        this.utenlandskArbeidsforhold = Optional.ofNullable(utenlandskArbeidsforhold).orElse(emptyList());
        this.egenNæring = Optional.ofNullable(egenNæring).orElse(emptyList());
        this.annenOpptjening = Optional.ofNullable(annenOpptjening).orElse(emptyList());
        this.frilans = frilans;
    }
}
