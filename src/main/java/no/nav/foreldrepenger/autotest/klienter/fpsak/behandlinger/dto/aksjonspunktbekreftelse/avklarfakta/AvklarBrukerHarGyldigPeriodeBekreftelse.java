package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.BekreftedePerioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode="5021")
public class AvklarBrukerHarGyldigPeriodeBekreftelse extends BekreftedePerioderMalDto{

    public AvklarBrukerHarGyldigPeriodeBekreftelse(Fagsak fagsak, Behandling behandling) {
        super(fagsak, behandling);
    }

    public void setVurdering(Kode medlem) {
        for (BekreftedePerioderDto bekreftedePerioderDto :behandling.getMedlem().getMedlemskapPerioder()) {
            bekreftedePerioderDto.setMedlemskapManuellVurderingType(medlem);
            bekreftedePerioder.add(bekreftedePerioderDto);
        }

    }
}
