package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stonadskontoer {
    private final Stønadskonto stonadskontoType;
    private final int maxDager;
    private final int saldo;

    public Stonadskontoer(Stønadskonto stonadskontoType, int maxDager, int saldo) {
        this.stonadskontoType = stonadskontoType;
        this.maxDager = maxDager;
        this.saldo = saldo;
    }

    public Stønadskonto getStonadskontoType() {
        return this.stonadskontoType;
    }

    public int getMaxDager() {
        return this.maxDager;
    }

    public int getSaldo() {
        return this.saldo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stonadskontoType, maxDager, saldo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Stonadskontoer other = (Stonadskontoer) obj;
        return Objects.equals(this.saldo, other.saldo)
                && Objects.equals(this.stonadskontoType, other.stonadskontoType)
                && Objects.equals(this.maxDager, other.maxDager);

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [stonadskontotype=" + stonadskontoType + ", maxDager=" + maxDager
                + ", saldo=" + saldo + "]";
    }

}
