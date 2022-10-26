package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Soknad {
    // Generelt
    protected LocalDate mottattDato;
    protected String begrunnelseForSenInnsending;
    protected int antallBarn;

    // Fødsel
    protected LocalDate utstedtdato;
    protected LocalDate termindato;
    protected Map<Integer, LocalDate> fodselsdatoer;

    // adopsjon
    protected String farSokerType;
    protected LocalDate omsorgsovertakelseDato;
    protected Map<Integer, LocalDate> adopsjonFodelsedatoer;

    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public LocalDate getOmsorgsovertakelseDato() {
        return omsorgsovertakelseDato;
    }

    public LocalDate getUtstedtdato() {
        return utstedtdato;
    }

    public LocalDate getTermindato() {
        return termindato;
    }

    public int getAntallBarn() {
        return antallBarn;
    }
    public Map<Integer, LocalDate> getAdopsjonFodelsedatoer() {
        return adopsjonFodelsedatoer;
    }


    public Map<Integer, LocalDate> getFodselsdatoer() {
        return fodselsdatoer;
    }

}
