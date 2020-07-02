package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.BekreftedePerioderDto;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
public abstract class BekreftedePerioderMalDto extends AksjonspunktBekreftelse {

    public BekreftedePerioderMalDto() {
        super();
    }

    protected List<BekreftedePerioderDto> bekreftedePerioder = new ArrayList<>();

    public void setBekreftedePerioder(List<BekreftedePerioderDto> bekreftedePerioder) {
        this.bekreftedePerioder = bekreftedePerioder;
    }

    public List<BekreftedePerioderDto> getBekreftedePerioder() {
        return bekreftedePerioder;
    }
}
