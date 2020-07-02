package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BekreftedeAksjonspunkter {

    private int behandlingId;
    private String saksnummer;
    private int behandlingVersjon;
    private List<AksjonspunktBekreftelse> bekreftedeAksjonspunktDtoer;

    public BekreftedeAksjonspunkter(Fagsak fagsak, Behandling behandling,
            List<AksjonspunktBekreftelse> aksjonspunktBekreftelser) {
        this(behandling.id, "" + fagsak.getSaksnummer(), behandling.versjon, aksjonspunktBekreftelser);
    }

    @JsonCreator
    public BekreftedeAksjonspunkter(int behandlingId, String saksnummer, int behandlingVersjon,
            List<AksjonspunktBekreftelse> bekreftedeAksjonspunktDtoer) {
        super();
        this.behandlingId = behandlingId;
        this.saksnummer = saksnummer;
        this.behandlingVersjon = behandlingVersjon;
        this.bekreftedeAksjonspunktDtoer = bekreftedeAksjonspunktDtoer;
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

    public List<AksjonspunktBekreftelse> getBekreftedeAksjonspunktDtoer() {
        return bekreftedeAksjonspunktDtoer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BekreftedeAksjonspunkter that = (BekreftedeAksjonspunkter) o;
        return behandlingId == that.behandlingId &&
                behandlingVersjon == that.behandlingVersjon &&
                Objects.equals(saksnummer, that.saksnummer) &&
                Objects.equals(bekreftedeAksjonspunktDtoer, that.bekreftedeAksjonspunktDtoer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingId, saksnummer, behandlingVersjon, bekreftedeAksjonspunktDtoer);
    }

    @Override
    public String toString() {
        return "BekreftedeAksjonspunkter{" +
                "behandlingId=" + behandlingId +
                ", saksnummer='" + saksnummer + '\'' +
                ", behandlingVersjon=" + behandlingVersjon +
                ", bekreftedeAksjonspunktDtoer=" + bekreftedeAksjonspunktDtoer +
                '}';
    }
}
