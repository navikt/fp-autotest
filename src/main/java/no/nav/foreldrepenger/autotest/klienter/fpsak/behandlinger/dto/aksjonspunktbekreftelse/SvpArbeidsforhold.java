package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import no.nav.vedtak.util.InputValideringRegex;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class SvpArbeidsforhold {

    private Long tilretteleggingId;

    private LocalDate tilretteleggingBehovFom;

    private List<SvpTilretteleggingDato> tilretteleggingDatoer = new ArrayList<>();
    private String arbeidsgiverNavn;
    private String arbeidsgiverIdent;
    private String opplysningerOmRisiko;
    private String opplysningerOmTilrettelegging;

    private Boolean kopiertFraTidligereBehandling;
    private LocalDateTime mottattTidspunkt;

    @Size(max = 4000)
    @Pattern(regexp = InputValideringRegex.FRITEKST)
    private String begrunnelse;


    public LocalDate getTilretteleggingBehovFom() {
        return tilretteleggingBehovFom;
    }

    public void setTilretteleggingBehovFom(LocalDate tilretteleggingBehovFom) {
        this.tilretteleggingBehovFom = tilretteleggingBehovFom;
    }

    public List<SvpTilretteleggingDato> getTilretteleggingDatoer() {
        return tilretteleggingDatoer;
    }

    public SvpArbeidsforhold setTilretteleggingDatoer(List<SvpTilretteleggingDato> tilretteleggingDatoer) {
        this.tilretteleggingDatoer = tilretteleggingDatoer;
        return this;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public void setArbeidsgiverNavn(String arbeidsgiverNavn) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
    }

    public String getArbeidsgiverIdent() {
        return arbeidsgiverIdent;
    }

    public void setArbeidsgiverIdent(String arbeidsgiverIdent) {
        this.arbeidsgiverIdent = arbeidsgiverIdent;
    }

    public Long getTilretteleggingId() {
        return tilretteleggingId;
    }

    public SvpArbeidsforhold setTilretteleggingId(Long tilretteleggingId) {
        this.tilretteleggingId = tilretteleggingId;
        return this;
    }

    public String getOpplysningerOmRisiko() {
        return opplysningerOmRisiko;
    }

    public SvpArbeidsforhold setOpplysningerOmRisiko(String opplysningerOmRisiko) {
        this.opplysningerOmRisiko = opplysningerOmRisiko;
        return this;
    }

    public String getOpplysningerOmTilrettelegging() {
        return opplysningerOmTilrettelegging;
    }

    public SvpArbeidsforhold setOpplysningerOmTilrettelegging(String opplysningerOmTilrettelegging) {
        this.opplysningerOmTilrettelegging = opplysningerOmTilrettelegging;
        return this;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public Boolean getKopiertFraTidligereBehandling() {
        return kopiertFraTidligereBehandling;
    }

    public void setKopiertFraTidligereBehandling(Boolean kopiertFraTidligereBehandling) {
        this.kopiertFraTidligereBehandling = kopiertFraTidligereBehandling;
    }

    public LocalDateTime getMottattTidspunkt() {
        return mottattTidspunkt;
    }

    public void setMottattTidspunkt(LocalDateTime mottattTidspunkt) {
        this.mottattTidspunkt = mottattTidspunkt;
    }

}
