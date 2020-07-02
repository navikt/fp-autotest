package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeid;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Arbeidsforhold {

    protected String id = null;
    protected String navn = null;
    protected String arbeidsgiverIdentifikator = null;
    protected String arbeidsforholdId = null;
    protected LocalDate fomDato = null;
    protected LocalDate tomDato = null;
    protected ArbeidsforholdKilde kilde = null;
    protected LocalDate mottattDatoInntektsmelding = null;
    protected String begrunnelse = null;
    protected BigDecimal stillingsprosent = null;
    protected Boolean brukArbeidsforholdet = null;
    protected Boolean fortsettBehandlingUtenInntektsmelding = null;
    protected Boolean erNyttArbeidsforhold = null;
    protected Boolean erEndret = null;
    protected Boolean erSlettet = null;
    protected String erstatterArbeidsforholdId = null;
    protected Boolean harErstattetEttEllerFlere = null;
    protected Boolean ikkeRegistrertIAaRegister = null;
    protected Boolean tilVurdering = null;
    protected Boolean vurderOmSkalErstattes = null;
    protected LocalDate overstyrtTom = null;
    protected Boolean lagtTilAvSaksbehandler;
    protected Boolean basertPaInntektsmelding;

    public Arbeidsforhold() {
        // jaxb
    }

    public Arbeidsforhold(String navn, LocalDate fomDato, LocalDate tomDato, BigDecimal stillingsprosent,
            Boolean lagtTilAvSaksbehandler) {
        this.navn = navn;
        this.fomDato = fomDato;
        this.tomDato = tomDato;
        this.stillingsprosent = stillingsprosent;
        this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
    }

    public Boolean getBrukArbeidsforholdet() {
        return brukArbeidsforholdet;
    }

    public void setBrukArbeidsforholdet(Boolean brukArbeidsforholdet) {
        this.brukArbeidsforholdet = brukArbeidsforholdet;
    }

    public Boolean getFortsettBehandlingUtenInntektsmelding() {
        return fortsettBehandlingUtenInntektsmelding;
    }

    public void setFortsettBehandlingUtenInntektsmelding(Boolean fortsettBehandlingUtenInntektsmelding) {
        this.fortsettBehandlingUtenInntektsmelding = fortsettBehandlingUtenInntektsmelding;
    }

    public String getNavn() {
        return navn;
    }

    public LocalDate getFomDato() {
        return fomDato;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getArbeidsgiverIdentifikator() {
        return arbeidsgiverIdentifikator;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public void setOverstyrtTom(LocalDate overstyrtTom) {
        this.overstyrtTom = overstyrtTom;
    }

    public void setFomDato(LocalDate fomDato) {
        this.fomDato = fomDato;
    }

    public LocalDate getTomDato() {
        return tomDato;
    }

    public void setTomDato(LocalDate tomDato) {
        this.tomDato = tomDato;
    }

    public BigDecimal getStillingsprosent() {
        return stillingsprosent;
    }

    public void setStillingsprosent(BigDecimal stillingsprosent) {
        this.stillingsprosent = stillingsprosent;
    }

    public Boolean getBasertPaInntektsmelding() {
        return basertPaInntektsmelding;
    }

    public void setBasertPaInntektsmelding(Boolean basertPaInntektsmelding) {
        this.basertPaInntektsmelding = basertPaInntektsmelding;
    }
}
