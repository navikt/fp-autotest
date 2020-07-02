package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode = "5037")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderingAvInnsynBekreftelse extends AksjonspunktBekreftelse {

    private LocalDate mottattDato;
    private LocalDate fristDato;
    private List<Object> innsynDokumenter = new ArrayList<>();
    private String innsynResultatType;
    private Boolean sattPaVent;

    public VurderingAvInnsynBekreftelse() {
        super();
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public LocalDate getFristDato() {
        return fristDato;
    }

    public List<Object> getInnsynDokumenter() {
        return innsynDokumenter;
    }

    public String getInnsynResultatType() {
        return innsynResultatType;
    }

    public Boolean getSattPaVent() {
        return sattPaVent;
    }

    public VurderingAvInnsynBekreftelse setMottattDato(LocalDate mottattDato) {
        this.mottattDato = mottattDato;
        this.fristDato = mottattDato.plusDays(4);
        return this;
    }

    public VurderingAvInnsynBekreftelse setInnsynDokumenter(List<Object> innsynDokumenter) {
        this.innsynDokumenter = innsynDokumenter;
        return this;
    }

    public VurderingAvInnsynBekreftelse setInnsynResultatType(Kode innsynResultatType) {
        this.innsynResultatType = innsynResultatType.kode;
        return this;
    }

    public VurderingAvInnsynBekreftelse skalSetteSakPåVent(boolean settPåVent) {
        this.sattPaVent = settPåVent;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderingAvInnsynBekreftelse that = (VurderingAvInnsynBekreftelse) o;
        return Objects.equals(mottattDato, that.mottattDato) &&
                Objects.equals(fristDato, that.fristDato) &&
                Objects.equals(innsynDokumenter, that.innsynDokumenter) &&
                Objects.equals(innsynResultatType, that.innsynResultatType) &&
                Objects.equals(sattPaVent, that.sattPaVent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mottattDato, fristDato, innsynDokumenter, innsynResultatType, sattPaVent);
    }

    @Override
    public String toString() {
        return "VurderingAvInnsynBekreftelse{" +
                "mottattDato=" + mottattDato +
                ", fristDato=" + fristDato +
                ", innsynDokumenter=" + innsynDokumenter +
                ", innsynResultatType='" + innsynResultatType + '\'' +
                ", sattPaVent=" + sattPaVent +
                "} " + super.toString();
    }
}
