package no.nav.foreldrepenger.autotest.klienter.vtp.testscenario;

import no.nav.foreldrepenger.vtp.testmodell.identer.LokalIdentIndeks;
import no.nav.foreldrepenger.vtp.testmodell.personopplysning.AdresseIndeks;
import no.nav.foreldrepenger.vtp.testmodell.util.VariabelContainer;
import no.nav.vedtak.mapper.json.DefaultJson3Mapper;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.json.JsonMapper;

public class TestscenarioJsonMapper {

    public static final JsonMapper DEFAULT_MAPPER_VTP = getJsonMapper();

    private TestscenarioJsonMapper() {
    }

    private static JsonMapper getJsonMapper() {
        // Trengs pga privat arbeidsgiver i inntektskomponentmodell.
        InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(LokalIdentIndeks.class, null);
        injectableValues.addValue(VariabelContainer.class, null);
        injectableValues.addValue(AdresseIndeks.class, null);
        return DefaultJson3Mapper.getJsonMapper().rebuild()
                .injectableValues(injectableValues)
                .build();
    }
}
