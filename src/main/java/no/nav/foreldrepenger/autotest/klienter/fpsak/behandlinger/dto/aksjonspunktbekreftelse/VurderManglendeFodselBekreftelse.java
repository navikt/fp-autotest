package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.UidentifisertBarn;

@BekreftelseKode(kode = "5027")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderManglendeFodselBekreftelse extends AksjonspunktBekreftelse {

    private Integer antallBarnFodt; // brukes ikke i ES?
    private LocalDate fodselsdato; // brukes ikke i ES?
    private boolean dokumentasjonForeligger;
    private boolean brukAntallBarnITps;
    private List<UidentifisertBarn> uidentifiserteBarn = new ArrayList<>();

    public VurderManglendeFodselBekreftelse() {
        super();
    }

    public Integer getAntallBarnFodt() {
        return antallBarnFodt;
    }

    public LocalDate getFodselsdato() {
        return fodselsdato;
    }

    public boolean isDokumentasjonForeligger() {
        return dokumentasjonForeligger;
    }

    public boolean isBrukAntallBarnITps() {
        return brukAntallBarnITps;
    }

    public List<UidentifisertBarn> getUidentifiserteBarn() {
        return uidentifiserteBarn;
    }

    public VurderManglendeFodselBekreftelse bekreftDokumentasjonForeligger(int antallBarn, LocalDate dato) {
        dokumentasjonForeligger = true;
        antallBarnFodt = antallBarn;
        for (int i = 0; i < antallBarn; i++) {
            uidentifiserteBarn.add(new UidentifisertBarn(dato, null));
        }
        fodselsdato = dato;
        return this;
    }

    public VurderManglendeFodselBekreftelse bekreftDokumentasjonIkkeForeligger() {
        uidentifiserteBarn.add(new UidentifisertBarn(null, null));
        dokumentasjonForeligger = false;
        antallBarnFodt = null;
        return this;
    }

    public VurderManglendeFodselBekreftelse bekreftBrukAntallBarnITps() {
        brukAntallBarnITps = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderManglendeFodselBekreftelse that = (VurderManglendeFodselBekreftelse) o;
        return dokumentasjonForeligger == that.dokumentasjonForeligger &&
                brukAntallBarnITps == that.brukAntallBarnITps &&
                Objects.equals(antallBarnFodt, that.antallBarnFodt) &&
                Objects.equals(fodselsdato, that.fodselsdato) &&
                Objects.equals(uidentifiserteBarn, that.uidentifiserteBarn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(antallBarnFodt, fodselsdato, dokumentasjonForeligger, brukAntallBarnITps, uidentifiserteBarn);
    }

    @Override
    public String toString() {
        return "VurderManglendeFodselBekreftelse{" +
                "antallBarnFodt=" + antallBarnFodt +
                ", fodselsdato=" + fodselsdato +
                ", dokumentasjonForeligger=" + dokumentasjonForeligger +
                ", brukAntallBarnITps=" + brukAntallBarnITps +
                ", uidentifiserteBarn=" + uidentifiserteBarn +
                "} " + super.toString();
    }
}
