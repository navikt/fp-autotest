package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Arbeidsforhold {

    protected Long tilretteleggingId;

    protected LocalDate tilretteleggingBehovFom;

    protected List<Tilretteleggingsdato> tilretteleggingDatoer = new ArrayList<>();
    protected String arbeidsgiverNavn;
    protected String arbeidsgiverIdent;
    protected String opplysningerOmRisiko;
    protected String opplysningerOmTilrettelegging;

    protected Boolean kopiertFraTidligereBehandling;
    protected LocalDateTime mottattTidspunkt;

    protected String begrunnelse;


}
