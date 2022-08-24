package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import java.lang.reflect.InvocationTargetException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.Fagsystem;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Aksjonspunkt {

    @JsonIgnore
    private AksjonspunktBekreftelse bekreftelse;

    protected String definisjon;
    protected String status;
    protected String begrunnelse;
    protected String vilkarType;
    protected String kategori;
    protected Boolean toTrinnsBehandling;
    protected Boolean toTrinnsBehandlingGodkjent;
    protected Boolean kanLoses;
    protected Boolean erAktivt;

    public String getDefinisjon() {
        return definisjon;
    }

    public boolean erUbekreftet() {
        return !status.equals("UTFO");
    }

    public boolean skalTilToTrinnsBehandling() {
        return toTrinnsBehandling;
    }

    public boolean getKanLoses() {
        return kanLoses;
    }

    public boolean getErAktivt() {
        return erAktivt;
    }

    public String getStatus() {
        return status;
    }

    public void setBekreftelse(AksjonspunktBekreftelse bekreftelse) {
        this.bekreftelse = bekreftelse;
    }

    public AksjonspunktBekreftelse getBekreftelse(Fagsystem fagsystem) {
        try {
            return AksjonspunktBekreftelse.fromAksjonspunkt(this, fagsystem);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }


    @Override
    public String toString() {
        return "Aksjonspunkt{" +
                "bekreftelse=" + bekreftelse +
                ", definisjon=" + definisjon +
                ", status=" + status +
                ", begrunnelse='" + begrunnelse + '\'' +
                ", vilkarType=" + vilkarType +
                ", kategori=" + kategori +
                ", toTrinnsBehandling=" + toTrinnsBehandling +
                ", toTrinnsBehandlingGodkjent=" + toTrinnsBehandlingGodkjent +
                '}';
    }

}
