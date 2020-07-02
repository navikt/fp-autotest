package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Medlem {
    protected List<BekreftedePerioderDto> medlemskapPerioder;
    protected List<MedlemPeriodeDto> perioder;

    public List<MedlemPeriodeDto> getPerioder() {
        return perioder;
    }

    public void setPerioder(List<MedlemPeriodeDto> perioder) {
        this.perioder = perioder;
    }

    public List<BekreftedePerioderDto> getMedlemskapPerioder() {
        return medlemskapPerioder;
    }

    public void setMedlemskapPerioder(List<BekreftedePerioderDto> medlemskapPerioder) {
        this.medlemskapPerioder = medlemskapPerioder;
    }
}
