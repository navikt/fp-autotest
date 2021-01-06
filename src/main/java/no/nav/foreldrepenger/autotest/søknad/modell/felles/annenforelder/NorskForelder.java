package no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;

@Data
@ToString(callSuper = true, exclude = "navn")
@EqualsAndHashCode(callSuper = true, exclude = { "navn" })
public final class NorskForelder extends AnnenForelder {

    private final Fødselsnummer fnr;
    private final String navn;

    @JsonCreator
    public NorskForelder(@JsonProperty("fnr") Fødselsnummer fnr, @JsonProperty("navn") String navn) {
        this.fnr = fnr;
        this.navn = navn;
    }

    @Override
    public boolean hasId() {
        return Optional.ofNullable(fnr)
                .map(Fødselsnummer::getFnr)
                .filter(Objects::nonNull)
                .isPresent();
    }
}
