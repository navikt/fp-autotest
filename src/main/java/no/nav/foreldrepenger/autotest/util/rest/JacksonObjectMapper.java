package no.nav.foreldrepenger.autotest.util.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import no.nav.foreldrepenger.vtp.testmodell.identer.LokalIdentIndeks;
import no.nav.foreldrepenger.vtp.testmodell.util.VariabelContainer;

public class JacksonObjectMapper {

    public static final ObjectMapper mapper = getObjectMapper();

    private JacksonObjectMapper() {
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.DEFAULT));

        mapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // må nullstille disse siden autotest deler klasser med intern scenario modell i  vtp.
        // Får ikke skrudd av injectableValues i jackson på annen måte
        InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(LokalIdentIndeks.class, null);
        injectableValues.addValue(VariabelContainer.class, null);
        mapper.setInjectableValues(injectableValues);
        return mapper;
    }
}
