package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.BekreftetForelder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Medlem {
    protected List<BekreftedePerioderDto> medlemskapPerioder;


    public List<BekreftedePerioderDto> getMedlemskapPerioder() {
        return medlemskapPerioder;
    }

    public void setMedlemskapPerioder(List<BekreftedePerioderDto> medlemskapPerioder) {
        this.medlemskapPerioder = medlemskapPerioder;
    }
}
