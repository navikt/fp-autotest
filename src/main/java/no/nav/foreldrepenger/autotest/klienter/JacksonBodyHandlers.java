package no.nav.foreldrepenger.autotest.klienter;

import java.util.Optional;

import no.nav.vedtak.mapper.json.DefaultJson3Mapper;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;


public final class JacksonBodyHandlers {

    private JacksonBodyHandlers() {
        // Statisk implementasjon
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return fromJson(json, clazz, DefaultJson3Mapper.getJsonMapper());
    }

    public static <T> T fromJson(String json, Class<T> clazz, JsonMapper mapper) {
        return Optional.ofNullable(json).map(j -> mapper.readValue(j, clazz)).orElse(null);
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        return fromJson(json, DefaultJson3Mapper.getJsonMapper(), typeReference);
    }

    public static <T> T fromJson(String json, JsonMapper mapper, TypeReference<T> typeReference) {
        return mapper.readerFor(typeReference).readValue(json);
    }

    public static String toJson(Object obj) {
        return DefaultJson3Mapper.toJson(obj);
    }

}
