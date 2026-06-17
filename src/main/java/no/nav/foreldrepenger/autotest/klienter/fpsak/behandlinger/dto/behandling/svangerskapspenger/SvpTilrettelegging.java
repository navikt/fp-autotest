package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SvpTilrettelegging {

    protected List<SvpAvklartOppholdPeriodeDto> avklarteOppholdPerioder;
    protected Long tilretteleggingId;
    protected LocalDate tilretteleggingBehovFom;
    protected List<SvpTilretteleggingDatoDto> tilretteleggingDatoer = new ArrayList<>();
    protected String arbeidsgiverReferanse;
    protected UUID internArbeidsforholdReferanse;
    protected String eksternArbeidsforholdReferanse;
    protected Boolean skalBrukes;
    protected String begrunnelse;

    public List<SvpTilretteleggingDatoDto> getTilretteleggingDatoer() {
        return tilretteleggingDatoer;
    }

    public void setTilretteleggingDatoer(List<SvpTilretteleggingDatoDto> tilretteleggingDatoer) {
        this.tilretteleggingDatoer = tilretteleggingDatoer;
    }

    public LocalDate getTilretteleggingBehovFom() {
        return tilretteleggingBehovFom;
    }

    public void setTilretteleggingBehovFom(LocalDate tilretteleggingBehovFom) {
        this.tilretteleggingBehovFom = tilretteleggingBehovFom;
    }

    public Long getTilretteleggingId() {
        return tilretteleggingId;
    }

    public Boolean getSkalBrukes() {
        return skalBrukes;
    }

    public void setSkalBrukes(Boolean skalBrukes) {
        this.skalBrukes = skalBrukes;
    }

    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }

    public String getEksternArbeidsforholdReferanse() {
        return eksternArbeidsforholdReferanse;
    }

    public UUID getInternArbeidsforholdReferanse() {
        return internArbeidsforholdReferanse;
    }

    public List<SvpAvklartOppholdPeriodeDto> getAvklarteOppholdPerioder() {
        return avklarteOppholdPerioder;
    }

}
