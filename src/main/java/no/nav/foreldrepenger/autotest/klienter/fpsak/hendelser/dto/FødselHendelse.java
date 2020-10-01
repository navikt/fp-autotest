package no.nav.foreldrepenger.autotest.klienter.fpsak.hendelser.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FødselHendelse extends Hendelse {
    protected String avsenderSystem = "tps";

    protected String hendelsetype = "FØDSEL";

    protected String NAME = "FØDSEL";
    protected List<String> aktørIdForeldre;

    @NotNull
    protected LocalDate fødselsdato;

    @Override
    public String getAvsenderSystem() {
        return avsenderSystem;
    }

    @Override
    public String getHendelsetype() {
        return hendelsetype;
    }

    @Override
    public List<String> getAlleAktørId() {
        return aktørIdForeldre;
    }

    public List<String> getAktørIdForeldre() {
        return aktørIdForeldre;
    }

    public void setAktørIdForeldre(List<String> aktørIdForeldre) {
        this.aktørIdForeldre = aktørIdForeldre;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    public void setFødselsdato(LocalDate fødselsdato) {
        this.fødselsdato = fødselsdato;
    }
}
