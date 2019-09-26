package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.BekreftedePerioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
public abstract class BekreftedePerioderMalDto extends AksjonspunktBekreftelse {

    public BekreftedePerioderMalDto(Fagsak fagsak, Behandling behandling) {
        super(fagsak, behandling);
    }

    protected List<BekreftedePerioderDto> bekreftedePerioder = new ArrayList<>();

    public void setBekreftedePerioder(List<BekreftedePerioderDto> bekreftedePerioder){
        this.bekreftedePerioder = bekreftedePerioder;
    }

    public List<BekreftedePerioderDto> getBekreftedePerioder() {
        return bekreftedePerioder;
    }
}
