package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
