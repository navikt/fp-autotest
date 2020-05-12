package no.nav.foreldrepenger.autotest.aktoerer.fpoppdrag;

import java.io.IOException;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fpoppdrag.simulering.SimuleringKlient;
import no.nav.foreldrepenger.autotest.klienter.fpoppdrag.simulering.dto.BehandlingIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpoppdrag.simulering.dto.SimulerOppdragDto;
import no.nav.foreldrepenger.autotest.klienter.fpoppdrag.simulering.dto.SimuleringDto;

public class Saksbehandler extends Aktoer {

    SimuleringKlient simuleringKlient;

    public Saksbehandler() {
        simuleringKlient = new SimuleringKlient(session);
    }

    public SimuleringDto hentSimuleringResultat(BehandlingIdDto behandlingIdDto) {
        return simuleringKlient.hentSimuleringResultat(behandlingIdDto);
    }


    public void startSimulering(SimulerOppdragDto simulerOppdragDto) {
        simuleringKlient.startSimulering(simulerOppdragDto);
    }

    public void kansellerSimulering(BehandlingIdDto behandlingIdDto) {
        simuleringKlient.kansellerSimulering(behandlingIdDto);
    }
}
