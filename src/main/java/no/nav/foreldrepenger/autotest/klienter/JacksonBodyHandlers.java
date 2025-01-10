package no.nav.foreldrepenger.autotest.klienter;

import static no.nav.foreldrepenger.common.mapper.DefaultJsonMapper.MAPPER;

import java.io.IOException;
import java.net.http.HttpRequest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.vedtak.exception.TekniskException;


public final class JacksonBodyHandlers {
    private static final ObjectMapper fellesObjectmapper = MAPPER
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
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new TekniskException("FP-713328", "Fikk IO exception ved deserialisering av JSON", e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return fellesObjectmapper.readValue(json, typeReference);
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

    public static HttpRequest.BodyPublisher toJsonBodyPublisher(Object obj) {
        try {
            var json = fellesObjectmapper.writeValueAsString(obj);
            return HttpRequest.BodyPublishers.ofString(json);
        } catch (IOException e) {
            throw new TekniskException("F-208314", "Kunne ikke serialisere objekt til JSON", e);
        }
    }
}
