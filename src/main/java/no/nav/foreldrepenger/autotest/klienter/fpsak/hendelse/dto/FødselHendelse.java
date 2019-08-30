package no.nav.foreldrepenger.autotest.klienter.fpsak.hendelse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class FødselHendelse extends Hendelse {
    public static final String HENDELSE_TYPE = "FØDSEL";
    public static final String AVSENDER = "tps";

    @NotNull
    @Size(min = 1)
    protected List<String> aktørIdForeldre;

    @NotNull
    protected LocalDate fødselsdato;

    public FødselHendelse(String aktørIdForeldre, LocalDate fødselsdato){
        super();
        this.aktørIdForeldre = List.of(aktørIdForeldre);
        this.fødselsdato = fødselsdato;
    }
    @Override
    public String getHendelsetype() {
        return HENDELSE_TYPE;
    }
    @Override
    public String getAvsenderSystem() {
        return AVSENDER;
    }
}
