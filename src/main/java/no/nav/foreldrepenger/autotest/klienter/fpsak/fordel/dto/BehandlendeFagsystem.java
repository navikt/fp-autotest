package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BehandlendeFagsystem {

    private boolean behandlesIVedtaksløsningen;
    private boolean sjekkMotInfotrygd;
    private boolean manuellVurdering;
    private Saksnummer saksnummer;

    public BehandlendeFagsystem(boolean behandlesIVedtaksløsningen,
                                boolean sjekkMotInfotrygd,
                                boolean manuellVurdering,
                                Saksnummer saksnummer) {
        this.behandlesIVedtaksløsningen = behandlesIVedtaksløsningen;
        this.sjekkMotInfotrygd = sjekkMotInfotrygd;
        this.manuellVurdering = manuellVurdering;
        this.saksnummer = saksnummer;
    }

    public boolean isBehandlesIVedtaksløsningen() {
        return behandlesIVedtaksløsningen;
    }

    public boolean isSjekkMotInfotrygd() {
        return sjekkMotInfotrygd;
    }

    public boolean isManuellVurdering() {
        return manuellVurdering;
    }

    public Saksnummer getSaksnummer() {
        return saksnummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BehandlendeFagsystem that = (BehandlendeFagsystem) o;
        return behandlesIVedtaksløsningen == that.behandlesIVedtaksløsningen &&
                sjekkMotInfotrygd == that.sjekkMotInfotrygd &&
                manuellVurdering == that.manuellVurdering &&
                Objects.equals(saksnummer, that.saksnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlesIVedtaksløsningen, sjekkMotInfotrygd, manuellVurdering, saksnummer);
    }

    @Override
    public String toString() {
        return "BehandlendeFagsystem{" +
                "behandlesIVedtaksløsningen=" + behandlesIVedtaksløsningen +
                ", sjekkMotInfotrygd=" + sjekkMotInfotrygd +
                ", manuellVurdering=" + manuellVurdering +
                ", saksnummer=" + saksnummer +
                '}';
    }
}
