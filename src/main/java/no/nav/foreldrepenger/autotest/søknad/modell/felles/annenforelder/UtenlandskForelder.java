package no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class UtenlandskForelder extends AnnenForelder {

    private final String id;
    private final CountryCode land;
    private final String navn;

    @Override
    public boolean hasId() {
        return id != null;
    }
}
