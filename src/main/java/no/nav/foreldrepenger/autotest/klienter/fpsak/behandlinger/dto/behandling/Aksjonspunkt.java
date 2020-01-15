package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

import java.lang.reflect.InvocationTargetException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Aksjonspunkt {

    @JsonIgnore
    private transient AksjonspunktBekreftelse bekreftelse;

    protected Kode definisjon;
    protected Kode status;
    protected String begrunnelse;
    protected Kode vilkarType;
    protected Kode kategori;
    protected Boolean toTrinnsBehandling;
    protected Boolean toTrinnsBehandlingGodkjent;
    protected Boolean kanLoses;

    public Kode getDefinisjon() {
        return definisjon;
    }

    public boolean erUbekreftet() {
        return !status.kode.equals("UTFO");
    }

    public boolean skalTilToTrinnsBehandling() {
        return toTrinnsBehandling;
    }
    public boolean getKanLoses() {return kanLoses;}

    public AksjonspunktBekreftelse getBekreftelse() {
        try {
            return AksjonspunktBekreftelse.fromAksjonspunkt(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public Kode getStatus() {
        return status;
    }

    public void setBekreftelse(AksjonspunktBekreftelse bekreftelse) {
        this.bekreftelse = bekreftelse;
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
