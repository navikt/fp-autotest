package no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(exclude = "datoForAleneomsorg")
@EqualsAndHashCode(exclude = "datoForAleneomsorg")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Rettigheter {

    private final boolean harAnnenForelderRett;
    private final boolean harOmsorgForBarnetIPeriodene;
    private final boolean harAleneOmsorgForBarnet;
    private final LocalDate datoForAleneomsorg;

}
