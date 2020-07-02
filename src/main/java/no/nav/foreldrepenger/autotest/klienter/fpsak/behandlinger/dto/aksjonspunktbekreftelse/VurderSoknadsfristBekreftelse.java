package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode = "5007")
public class VurderSoknadsfristBekreftelse extends AksjonspunktBekreftelse {

    protected boolean erVilkarOk;
    protected LocalDate mottattDato;
    protected LocalDate omsorgsovertakelseDato;

    public VurderSoknadsfristBekreftelse() {
        super();
    }

    public VurderSoknadsfristBekreftelse bekreftVilkårErOk() {
        erVilkarOk = true;
        return this;
    }

    public VurderSoknadsfristBekreftelse bekreftVilkårErIkkeOk() {
        erVilkarOk = false;
        return this;
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        omsorgsovertakelseDato = behandling.getSoknad().getOmsorgsovertakelseDato();
        mottattDato = behandling.getSoknad().getMottattDato();
    }
}
