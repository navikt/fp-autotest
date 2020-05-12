package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.BekreftedePerioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.MedlemPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode="5019")
public class AvklarLovligOppholdBekreftelse extends BekreftedePerioderMalDto{

    public AvklarLovligOppholdBekreftelse() {
        super();
    }

    public void bekreftBrukerHarLovligOpphold() {

    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        List<MedlemPeriodeDto> perioder = behandling.getMedlem().getPerioder();
        List<BekreftedePerioderDto> bekreftedePerioderDtos = new ArrayList<>();
        for (MedlemPeriodeDto ignored : perioder) {
            BekreftedePerioderDto bekreftetPeriode = new BekreftedePerioderDto();
            bekreftetPeriode.setVurderingsdato(LocalDate.now());
            bekreftetPeriode.setErEosBorger(false);
            bekreftetPeriode.setLovligOppholdVurdering(true);
            bekreftetPeriode.setBegrunnelse("Søker har lovlig opphold");
            bekreftedePerioderDtos.add(bekreftetPeriode);
        }
        setBekreftedePerioder(bekreftedePerioderDtos);
    }
}
