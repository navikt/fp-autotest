package no.nav.foreldrepenger.autotest.util.http.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import no.nav.foreldrepenger.vtp.testmodell.identer.LokalIdentIndeks;
import no.nav.foreldrepenger.vtp.testmodell.util.VariabelContainer;

public class JsonKlient {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Sets serialization format of LocalDate to:
                                                                        // "yyyy-mm-dd";
//        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
//        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.PROTECTED_AND_PUBLIC);
        mapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);

        // må nullstille disse siden autotest deler klasser med intern scenario modell i  vtp.
        // Får ikke skrudd av injectableValues i jackson på annen måte
        InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(LokalIdentIndeks.class, null);
        injectableValues.addValue(VariabelContainer.class, null);
        mapper.setInjectableValues(injectableValues);
    }

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

}
