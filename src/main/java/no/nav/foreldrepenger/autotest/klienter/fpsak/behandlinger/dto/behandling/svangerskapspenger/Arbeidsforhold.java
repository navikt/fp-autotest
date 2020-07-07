package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Arbeidsforhold {

    protected Integer tilretteleggingId;
    protected LocalDate tilretteleggingBehovFom;
    protected List<Tilretteleggingsdato> tilretteleggingDatoer = new ArrayList<>();
    protected String arbeidsgiverNavn;
    protected String arbeidsgiverIdent;
    protected String opplysningerOmRisiko;
    protected String opplysningerOmTilrettelegging;
    protected Boolean kopiertFraTidligereBehandling;
    protected LocalDateTime mottattTidspunkt;
    protected String internArbeidsforholdReferanse;
    protected String eksternArbeidsforholdReferanse;
    protected Boolean skalBrukes;
    protected String begrunnelse;

    public Boolean getSkalBrukes() {
        return skalBrukes;
    }

    public void setSkalBrukes(Boolean skalBrukes) {
        this.skalBrukes = skalBrukes;
    }

    public String getEksternArbeidsforholdReferanse() {
        return eksternArbeidsforholdReferanse;
    }

    public String getInternArbeidsforholdReferanse() {
        return internArbeidsforholdReferanse;
    }

}
