package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderFagsystem {
    private String journalpostId;
    private boolean strukturertSøknad;
    private String aktørId;
    private String behandlingstemaOffisiellKode;
    private List<String> adopsjonsBarnFodselsdatoer;
    private String barnTermindato;
    private String barnFodselsdato;
    private String omsorgsovertakelsedato;
    private String årsakInnsendingInntektsmelding;
    private String saksnummer;
    private String annenPart;

    public VurderFagsystem(String journalpostId, boolean strukturertSøknad, String aktørId,
                           String behandlingstemaOffisiellKode,  List<String> adopsjonsBarnFodselsdatoer,
                           String barnTermindato, String barnFodselsdato, String omsorgsovertakelsedato,
                           String årsakInnsendingInntektsmelding, String saksnummer, String annenPart) {
        this.journalpostId = journalpostId;
        this.strukturertSøknad = strukturertSøknad;
        this.aktørId = aktørId;
        this.behandlingstemaOffisiellKode = behandlingstemaOffisiellKode;
        this.adopsjonsBarnFodselsdatoer = adopsjonsBarnFodselsdatoer;
        this.barnTermindato = barnTermindato;
        this.barnFodselsdato = barnFodselsdato;
        this.omsorgsovertakelsedato = omsorgsovertakelsedato;
        this.årsakInnsendingInntektsmelding = årsakInnsendingInntektsmelding;
        this.saksnummer = saksnummer;
        this.annenPart = annenPart;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public boolean isStrukturertSøknad() {
        return strukturertSøknad;
    }

    public String getAktørId() {
        return aktørId;
    }

    public String getBehandlingstemaOffisiellKode() {
        return behandlingstemaOffisiellKode;
    }

    public List<String> getAdopsjonsBarnFodselsdatoer() {
        return adopsjonsBarnFodselsdatoer;
    }

    public String getBarnTermindato() {
        return barnTermindato;
    }

    public String getBarnFodselsdato() {
        return barnFodselsdato;
    }

    public String getOmsorgsovertakelsedato() {
        return omsorgsovertakelsedato;
    }

    public String getÅrsakInnsendingInntektsmelding() {
        return årsakInnsendingInntektsmelding;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getAnnenPart() {
        return annenPart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderFagsystem that = (VurderFagsystem) o;
        return strukturertSøknad == that.strukturertSøknad &&
                Objects.equals(journalpostId, that.journalpostId) &&
                Objects.equals(aktørId, that.aktørId) &&
                Objects.equals(behandlingstemaOffisiellKode, that.behandlingstemaOffisiellKode) &&
                Objects.equals(adopsjonsBarnFodselsdatoer, that.adopsjonsBarnFodselsdatoer) &&
                Objects.equals(barnTermindato, that.barnTermindato) &&
                Objects.equals(barnFodselsdato, that.barnFodselsdato) &&
                Objects.equals(omsorgsovertakelsedato, that.omsorgsovertakelsedato) &&
                Objects.equals(årsakInnsendingInntektsmelding, that.årsakInnsendingInntektsmelding) &&
                Objects.equals(saksnummer, that.saksnummer) &&
                Objects.equals(annenPart, that.annenPart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(journalpostId, strukturertSøknad, aktørId, behandlingstemaOffisiellKode, adopsjonsBarnFodselsdatoer, barnTermindato, barnFodselsdato, omsorgsovertakelsedato, årsakInnsendingInntektsmelding, saksnummer, annenPart);
    }

    @Override
    public String toString() {
        return "VurderFagsystem{" +
                "journalpostId='" + journalpostId + '\'' +
                ", strukturertSøknad=" + strukturertSøknad +
                ", aktørId='" + aktørId + '\'' +
                ", behandlingstemaOffisiellKode='" + behandlingstemaOffisiellKode + '\'' +
                ", adopsjonsBarnFodselsdatoer=" + adopsjonsBarnFodselsdatoer +
                ", barnTermindato='" + barnTermindato + '\'' +
                ", barnFodselsdato='" + barnFodselsdato + '\'' +
                ", omsorgsovertakelsedato='" + omsorgsovertakelsedato + '\'' +
                ", årsakInnsendingInntektsmelding='" + årsakInnsendingInntektsmelding + '\'' +
                ", saksnummer='" + saksnummer + '\'' +
                ", annenPart='" + annenPart + '\'' +
                '}';
    }
}
