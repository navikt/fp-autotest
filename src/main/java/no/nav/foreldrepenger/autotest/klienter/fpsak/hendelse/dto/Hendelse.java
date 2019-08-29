package no.nav.foreldrepenger.autotest.klienter.fpsak.hendelse.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FødselHendelse.class, name = FødselHendelse.HENDELSE_TYPE),

})
public abstract class Hendelse {
    protected String id; // unik per hendelse

    public Hendelse() {
        this.id = UUID.randomUUID().toString();
    }
    public abstract String getAvsenderSystem();
    public abstract String getHendelsetype();
}