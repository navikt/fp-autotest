package no.nav.foreldrepenger.autotest.klienter.fpsak.hendelser.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DødHendelse extends Hendelse {
    protected String avsenderSystem = "tps";

    protected String hendelsetype = "DØD";

    protected String endringstype = "OPPRETTET";

    protected String NAME = "DØD";

    protected List<String> aktørId;
    @NotNull
    protected LocalDate dødsdato;

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

    public LocalDate getDødsdato() {
        return dødsdato;
    }

    public void setDødsdato(LocalDate dødsdato) {
        this.dødsdato = dødsdato;
    }
}
