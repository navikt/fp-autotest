package no.nav.foreldrepenger.autotest.klienter;

import java.util.Optional;

import no.nav.vedtak.mapper.json.DefaultJsonMapper;
import tools.jackson.core.type.TypeReference;


public final class JacksonBodyHandlers {

    private JacksonBodyHandlers() {
        // Statisk implementasjon
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return Optional.ofNullable(json).map(j -> DefaultJsonMapper.getJsonMapper().readValue(j, clazz)).orElse(null);
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        return DefaultJsonMapper.getJsonMapper().readerFor(typeReference).readValue(json);
    }

    public static String toJson(Object obj) {
        return DefaultJsonMapper.toJson(obj);
    }

}
