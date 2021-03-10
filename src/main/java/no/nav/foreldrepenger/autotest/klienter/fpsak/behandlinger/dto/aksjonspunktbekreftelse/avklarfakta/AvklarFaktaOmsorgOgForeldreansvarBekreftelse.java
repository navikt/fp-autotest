package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode = "5008")
public class AvklarFaktaOmsorgOgForeldreansvarBekreftelse extends AksjonspunktBekreftelse {

    protected LocalDate omsorgsovertakelseDato;
    protected String vilkarType;

    public AvklarFaktaOmsorgOgForeldreansvarBekreftelse() {
        super();
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        // Set omsorgsovertakelsedato fra søknad
        omsorgsovertakelseDato = behandling.getSoknad().getOmsorgsovertakelseDato();


    }

    public AvklarFaktaOmsorgOgForeldreansvarBekreftelse setVilkårType(OmsorgsovertakelseVilkårType vilkarType) {
        this.vilkarType = vilkarType.getKode();
        return this;
    }

}
