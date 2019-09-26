package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.BekreftedePerioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

import java.util.List;

@BekreftelseKode(kode="5020")
public class AvklarBrukerBosattBekreftelse extends BekreftedePerioderMalDto {
    public AvklarBrukerBosattBekreftelse(Fagsak fagsak, Behandling behandling) {
        super(fagsak, behandling);
    }

    public void bekreftBrukerErBosatt() {
        List<BekreftedePerioderDto> medlemskapPerioder = behandling.getMedlem().getMedlemskapPerioder();
        for (BekreftedePerioderDto bekreftedePerioderDto : medlemskapPerioder) {
            bekreftedePerioderDto.setBosattVurdering(true);

        }
    }


}

