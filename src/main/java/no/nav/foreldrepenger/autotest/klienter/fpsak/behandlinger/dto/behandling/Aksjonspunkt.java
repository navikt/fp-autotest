package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Aksjonspunkt {

    private static final Logger LOG = LoggerFactory.getLogger(Aksjonspunkt.class);

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
