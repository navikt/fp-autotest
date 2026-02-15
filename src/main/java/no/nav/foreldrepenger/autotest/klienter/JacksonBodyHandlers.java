package no.nav.foreldrepenger.autotest.klienter;

import java.util.Optional;

import no.nav.vedtak.mapper.json.DefaultJson3Mapper;
import tools.jackson.core.type.TypeReference;


public final class JacksonBodyHandlers {

    private JacksonBodyHandlers() {
        // Statisk implementasjon
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return Optional.ofNullable(json).map(j -> DefaultJson3Mapper.getJsonMapper().readValue(j, clazz)).orElse(null);
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        return DefaultJson3Mapper.getJsonMapper().readerFor(typeReference).readValue(json);
    }

    public static String toJson(Object obj) {
        return DefaultJson3Mapper.toJson(obj);
    }

}
