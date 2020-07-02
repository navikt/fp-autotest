package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingPaVent extends BehandlingIdPost {

    private LocalDate frist;
    private Kode ventearsak;

    @JsonCreator
    public BehandlingPaVent(int behandlingId, int behandlingVersjon, LocalDate frist, Kode ventearsak) {
        super(behandlingId, behandlingVersjon);
        this.frist = frist;
        this.ventearsak = ventearsak;
    }

    public BehandlingPaVent(Behandling behandling, LocalDate frist, Kode ventearsak) {
        this(behandling.id, behandling.versjon, frist, ventearsak);
    }

    public LocalDate getFrist() {
        return frist;
    }

    public Kode getVentearsak() {
        return ventearsak;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BehandlingPaVent that = (BehandlingPaVent) o;
        return Objects.equals(frist, that.frist) &&
                Objects.equals(ventearsak, that.ventearsak);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), frist, ventearsak);
    }

    @Override
    public String toString() {
        return "BehandlingPaVent{" +
                "frist=" + frist +
                ", ventearsak=" + ventearsak +
                "} " + super.toString();
    }
}
