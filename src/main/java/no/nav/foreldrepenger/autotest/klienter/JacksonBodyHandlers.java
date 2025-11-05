package no.nav.foreldrepenger.autotest.klienter;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import no.nav.vedtak.exception.TekniskException;


public final class JacksonBodyHandlers {
    private static final ObjectMapper fellesObjectmapper = JsonMapper.builder()
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule())
            .addModule(new ParameterNamesModule(JsonCreator.Mode.DEFAULT))
            .defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.NON_ABSENT))
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            // Spring boot defaults
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS).build()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    private JacksonBodyHandlers() {
        // Statisk implementasjon
    }

    public static ObjectMapper getObjectmapper() {
        return fellesObjectmapper;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return fromJson(json, clazz, fellesObjectmapper);
    }

    public static <T> T fromJson(String json, Class<T> clazz, ObjectMapper mapper) {
        try {
            if (json.isEmpty()) {
                return null;
            }
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new TekniskException("FP-713328", "Fikk IO exception ved deserialisering av JSON", e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        return fromJson(json, fellesObjectmapper, typeReference);
    }

    public static <T> T fromJson(String json, ObjectMapper mapper, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new TekniskException("FP-713328", "Fikk IO exception ved deserialisering av JSON", e);
        }
    }

    public static String toJson(Object obj) {
        try {
            return fellesObjectmapper.writeValueAsString(obj);
        } catch (IOException var2) {
            throw new TekniskException("F-208314", "Kunne ikke serialisere objekt til JSON", var2);
        }
    }

}
