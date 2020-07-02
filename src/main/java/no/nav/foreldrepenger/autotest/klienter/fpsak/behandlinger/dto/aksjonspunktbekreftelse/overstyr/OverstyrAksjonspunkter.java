package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OverstyrAksjonspunkter {

    private int behandlingId;
    private String saksnummer;
    private int behandlingVersjon;
    private List<AksjonspunktBekreftelse> overstyrteAksjonspunktDtoer;

    public OverstyrAksjonspunkter(Fagsak fagsak, Behandling behandling,
            List<AksjonspunktBekreftelse> aksjonspunktBekreftelser) {
        this(behandling.id, "" + fagsak.getSaksnummer(), behandling.versjon, aksjonspunktBekreftelser);
    }

    @JsonCreator
    public OverstyrAksjonspunkter(int behandlingId, String saksnummer, int behandlingVersjon,
            List<AksjonspunktBekreftelse> bekreftedeAksjonspunktDtoer) {
        super();
        this.behandlingId = behandlingId;
        this.saksnummer = saksnummer;
        this.behandlingVersjon = behandlingVersjon;
        this.overstyrteAksjonspunktDtoer = bekreftedeAksjonspunktDtoer;
    }

    public int getBehandlingId() {
        return behandlingId;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public int getBehandlingVersjon() {
        return behandlingVersjon;
    }

    public List<AksjonspunktBekreftelse> getOverstyrteAksjonspunktDtoer() {
        return overstyrteAksjonspunktDtoer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverstyrAksjonspunkter that = (OverstyrAksjonspunkter) o;
        return behandlingId == that.behandlingId &&
                behandlingVersjon == that.behandlingVersjon &&
                Objects.equals(saksnummer, that.saksnummer) &&
                Objects.equals(overstyrteAksjonspunktDtoer, that.overstyrteAksjonspunktDtoer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingId, saksnummer, behandlingVersjon, overstyrteAksjonspunktDtoer);
    }

    @Override
    public String toString() {
        return "OverstyrAksjonspunkter{" +
                "behandlingId=" + behandlingId +
                ", saksnummer='" + saksnummer + '\'' +
                ", behandlingVersjon=" + behandlingVersjon +
                ", overstyrteAksjonspunktDtoer=" + overstyrteAksjonspunktDtoer +
                '}';
    }
}
