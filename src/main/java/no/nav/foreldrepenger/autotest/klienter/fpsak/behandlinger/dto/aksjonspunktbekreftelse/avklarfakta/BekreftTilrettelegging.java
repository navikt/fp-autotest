package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import jakarta.validation.Valid;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.AvklartOpphold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.Tilretteleggingsdato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BekreftTilrettelegging {

    private Long tilretteleggingId;
    private LocalDate tilretteleggingBehovFom;
    private List<@Valid Tilretteleggingsdato> tilretteleggingDatoer = new ArrayList<>();
    private String arbeidsgiverReferanse;
    private UUID internArbeidsforholdReferanse;
    private String eksternArbeidsforholdReferanse;
    private boolean skalBrukes = true;
    private boolean kanTilrettelegges = true;
    private BigDecimal stillingsprosentStartTilrettelegging;
    private List<@Valid AvklartOpphold> avklarteOppholdPerioder = new ArrayList<>();

    private String begrunnelse;

    public BekreftTilrettelegging() {
    }

    public BekreftTilrettelegging(Long tilretteleggingId,
                                  LocalDate tilretteleggingBehovFom,
                                  List<Tilretteleggingsdato> tilretteleggingDatoer,
                                  String arbeidsgiverReferanse,
                                  UUID internArbeidsforholdReferanse,
                                  String eksternArbeidsforholdReferanse,
                                  boolean skalBrukes,
                                  boolean kanTilrettelegges,
                                  BigDecimal stillingsprosentStartTilrettelegging,
                                  List<AvklartOpphold> avklarteOppholdPerioder,
                                  String begrunnelse) {
        this.tilretteleggingId = tilretteleggingId;
        this.tilretteleggingBehovFom = tilretteleggingBehovFom;
        this.tilretteleggingDatoer = tilretteleggingDatoer != null ? tilretteleggingDatoer : new ArrayList<>();
        this.arbeidsgiverReferanse = arbeidsgiverReferanse;
        this.internArbeidsforholdReferanse = internArbeidsforholdReferanse;
        this.eksternArbeidsforholdReferanse = eksternArbeidsforholdReferanse;
        this.skalBrukes = skalBrukes;
        this.kanTilrettelegges = kanTilrettelegges;
        this.stillingsprosentStartTilrettelegging = stillingsprosentStartTilrettelegging;
        this.avklarteOppholdPerioder = avklarteOppholdPerioder != null ? avklarteOppholdPerioder : new ArrayList<>();
        this.begrunnelse = begrunnelse;
    }



    public LocalDate getTilretteleggingBehovFom() {
        return tilretteleggingBehovFom;
    }

    public void setTilretteleggingBehovFom(LocalDate tilretteleggingBehovFom) {
        this.tilretteleggingBehovFom = tilretteleggingBehovFom;
    }

    public List<Tilretteleggingsdato> getTilretteleggingDatoer() {
        return tilretteleggingDatoer;
    }

    public void setTilretteleggingDatoer(List<Tilretteleggingsdato> tilretteleggingDatoer) {
        this.tilretteleggingDatoer = tilretteleggingDatoer;
    }

    public Long getTilretteleggingId() {
        return tilretteleggingId;
    }

    public void setTilretteleggingId(Long tilretteleggingId) {
        this.tilretteleggingId = tilretteleggingId;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public UUID getInternArbeidsforholdReferanse() {
        return internArbeidsforholdReferanse;
    }

    public void setInternArbeidsforholdReferanse(UUID internArbeidsforholdRef) {
        this.internArbeidsforholdReferanse = internArbeidsforholdRef;
    }

    public boolean getSkalBrukes() {
        return skalBrukes;
    }

    public void setSkalBrukes(boolean skalBrukes) {
        this.skalBrukes = skalBrukes;
    }

    public List<AvklartOpphold> getAvklarteOppholdPerioder() {
        return avklarteOppholdPerioder;
    }

    public void setAvklarteOppholdPerioder(List<AvklartOpphold> avklarteOppholdPerioder) {
        this.avklarteOppholdPerioder = avklarteOppholdPerioder;
    }

    public boolean isKanTilrettelegges() {
        return kanTilrettelegges;
    }

    public void setKanTilrettelegges(boolean kanTilrettelegges) {
        this.kanTilrettelegges = kanTilrettelegges;
    }

    public String getEksternArbeidsforholdReferanse() {
        return eksternArbeidsforholdReferanse;
    }

    public void setEksternArbeidsforholdReferanse(String eksternArbeidsforholdReferanse) {
        this.eksternArbeidsforholdReferanse = eksternArbeidsforholdReferanse;
    }


    public void leggTilAvklarteOppholdPerioder(List<AvklartOpphold> avklarteOppholdPerioder) {
        avklarteOppholdPerioder.forEach(oppholdPeriode -> this.avklarteOppholdPerioder.add(oppholdPeriode));
    }

    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }

    public void setArbeidsgiverReferanse(String arbeidsgiverReferanse) {
        this.arbeidsgiverReferanse = arbeidsgiverReferanse;
    }

    public BigDecimal getStillingsprosentStartTilrettelegging() {
        return stillingsprosentStartTilrettelegging;
    }

    public void setStillingsprosentStartTilrettelegging(BigDecimal stillingsprosentStartTilrettelegging) {
        this.stillingsprosentStartTilrettelegging = stillingsprosentStartTilrettelegging;
    }
}
