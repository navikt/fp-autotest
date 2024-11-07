package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.time.LocalDate;
import java.util.Map;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

public class AvklarFaktaAdopsjonsdokumentasjonBekreftelse extends AksjonspunktBekreftelse {

    protected LocalDate omsorgsovertakelseDato;
    protected Map<Integer, LocalDate> fodselsdatoer;
    protected LocalDate barnetsAnkomstTilNorgeDato;

    public void setOmsorgsovertakelseDato(LocalDate omsorgsovertakelseDato) {
        this.omsorgsovertakelseDato = omsorgsovertakelseDato;
    }

    public void leggTilFødselsdato(LocalDate fødselsdato) {
        fodselsdatoer.put(fodselsdatoer.size(), fødselsdato);
    }

    public AvklarFaktaAdopsjonsdokumentasjonBekreftelse endreFødselsdato(Integer index, LocalDate fødselsdato) {
        fodselsdatoer.put(index, fødselsdato);
        return this;
    }

    public AvklarFaktaAdopsjonsdokumentasjonBekreftelse setBarnetsAnkomstTilNorgeDato(LocalDate dato) {
        barnetsAnkomstTilNorgeDato = dato;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5004";
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        if (behandling.getSoknad().getOmsorgsovertakelseDato() != null) {
            omsorgsovertakelseDato = behandling.getSoknad().getOmsorgsovertakelseDato();
            barnetsAnkomstTilNorgeDato = omsorgsovertakelseDato;
        }

        if (behandling.getSoknad().getAdopsjonFodelsedatoer() != null) {
            fodselsdatoer = behandling.getSoknad().getAdopsjonFodelsedatoer();
        }
    }
}
