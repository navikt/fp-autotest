package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.BekreftedePerioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.MedlemPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

import java.util.ArrayList;
import java.util.List;

@BekreftelseKode(kode = "5020")
public class AvklarBrukerBosattBekreftelse extends BekreftedePerioderMalDto {
    public AvklarBrukerBosattBekreftelse() {
        super();
    }

    public void bekreftBrukerErBosatt() {

    }

    @Override
    public void setFagsakOgBehandling(Fagsak fagsak, Behandling behandling) {
        super.setFagsakOgBehandling(fagsak, behandling);
        List<MedlemPeriodeDto> perioder = behandling.getMedlem().getPerioder();
        List<BekreftedePerioderDto> bekreftedePerioderDtos = new ArrayList<>();
        for (MedlemPeriodeDto periodeDto : perioder) {
            BekreftedePerioderDto bekreftetPeriode = new BekreftedePerioderDto();
            bekreftetPeriode.setBosattVurdering(periodeDto.getBosattVurdering());
            bekreftedePerioderDtos.add(bekreftetPeriode);
        }
        setBekreftedePerioder(bekreftedePerioderDtos);
    }
}

