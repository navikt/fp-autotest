package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tilrettelegging {

    protected LocalDate termindato;
    protected LocalDate fødselsdato;
    protected List<SvpTilrettelegging> arbeidsforholdListe;

    public LocalDate getTermindato() {
        return this.termindato;
    }

    public LocalDate getFødselsdato() {
        return this.fødselsdato;
    }

    public List<SvpTilrettelegging> getArbeidsforholdList() {
        return this.arbeidsforholdListe;
    }

}
