package no.nav.foreldrepenger.autotest.søknad.modell.felles;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ValgfrittVedlegg extends Vedlegg {

    @JsonCreator
    public ValgfrittVedlegg(@JsonProperty("metadata") VedleggMetaData metadata,
            @JsonProperty("vedlegg") byte[] vedlegg) {
        super(metadata, vedlegg);
    }

}
