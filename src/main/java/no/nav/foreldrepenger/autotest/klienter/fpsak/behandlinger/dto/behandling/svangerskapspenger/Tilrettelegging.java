package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tilrettelegging {

    protected LocalDate termindato;
    protected LocalDate fødselsdato;
    protected List<Arbeidsforhold> arbeidsforholdListe;

    public LocalDate getTermindato() {
        return this.termindato;
    }

    public LocalDate getFødselsdato() {
        return this.fødselsdato;
    }

    public List<Arbeidsforhold> getArbeidsforholdList() {
        return this.arbeidsforholdListe;
    }

}
