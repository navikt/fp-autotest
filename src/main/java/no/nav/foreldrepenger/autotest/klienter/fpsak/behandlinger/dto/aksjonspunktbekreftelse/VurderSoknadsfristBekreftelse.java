package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode = "5007")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderSoknadsfristBekreftelse extends AksjonspunktBekreftelse {

    private boolean erVilkarOk;
    private LocalDate mottattDato;
    private LocalDate omsorgsovertakelseDato;

    public VurderSoknadsfristBekreftelse() {
        super();
    }

    public boolean isErVilkarOk() {
        return erVilkarOk;
    }

    public void setErVilkarOk(boolean erVilkarOk) {
        this.erVilkarOk = erVilkarOk;
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public void setMottattDato(LocalDate mottattDato) {
        this.mottattDato = mottattDato;
    }

    public LocalDate getOmsorgsovertakelseDato() {
        return omsorgsovertakelseDato;
    }

    public void setOmsorgsovertakelseDato(LocalDate omsorgsovertakelseDato) {
        this.omsorgsovertakelseDato = omsorgsovertakelseDato;
    }

    public VurderSoknadsfristBekreftelse bekreftVilkårErOk() {
        erVilkarOk = true;
        return this;
    }

    public VurderSoknadsfristBekreftelse bekreftVilkårErIkkeOk() {
        erVilkarOk = false;
        return this;
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        omsorgsovertakelseDato = behandling.getSoknad().getOmsorgsovertakelseDato();
        mottattDato = behandling.getSoknad().getMottattDato();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderSoknadsfristBekreftelse that = (VurderSoknadsfristBekreftelse) o;
        return erVilkarOk == that.erVilkarOk &&
                Objects.equals(mottattDato, that.mottattDato) &&
                Objects.equals(omsorgsovertakelseDato, that.omsorgsovertakelseDato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(erVilkarOk, mottattDato, omsorgsovertakelseDato);
    }

    @Override
    public String toString() {
        return "VurderSoknadsfristBekreftelse{" +
                "erVilkarOk=" + erVilkarOk +
                ", mottattDato=" + mottattDato +
                ", omsorgsovertakelseDato=" + omsorgsovertakelseDato +
                "} " + super.toString();
    }
}
