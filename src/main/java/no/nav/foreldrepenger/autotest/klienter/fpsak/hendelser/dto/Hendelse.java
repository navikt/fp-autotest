package no.nav.foreldrepenger.autotest.klienter.fpsak.hendelser.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes({@JsonSubTypes.Type(
        value = FødselHendelse.class,
        name = "FØDSEL"
), @JsonSubTypes.Type(
        value = DødfødselHendelse.class,
        name = "DØDFØDSEL"
),@JsonSubTypes.Type(
        value = DødHendelse.class,
        name = "DØD"
)})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Hendelse {
    protected String id;
    public Hendelse() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract String getAvsenderSystem();

    public abstract String getHendelsetype();

    public abstract List<String> getAlleAktørId();

}
