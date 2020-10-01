package no.nav.foreldrepenger.autotest.klienter.fpsak.hendelser.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DødfødselHendelse extends Hendelse{

    protected String avsenderSystem = "tps";

    protected String hendelsetype = "DØDFØDSEL";

    protected String endringstype = "OPPRETTET";

    protected String NAME = "DØDFØDSEL";
    protected List<String> aktørId;
    @NotNull
    protected LocalDate dødfødselsdato;

    @Override
    public String getAvsenderSystem() {
        return avsenderSystem;
    }

    @Override
    public String getHendelsetype() {
        return hendelsetype;
    }
    public String getEndringstype() {
        return endringstype;
    }
    @Override
    public List<String> getAlleAktørId() {
        return aktørId;
    }

    public List<String> getAktørId() {
        return aktørId;
    }

    public void setAktørId(List<String> aktørId) {
        this.aktørId = aktørId;
    }

    public LocalDate getDødfødselsdato() {
        return dødfødselsdato;
    }

    public void setDødfødselsdato(LocalDate dødfødselsdato) {
        this.dødfødselsdato = dødfødselsdato;
    }
}
