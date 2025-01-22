package no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak;

import no.nav.foreldrepenger.autotest.klienter.fplos.FplosKlient;
import no.nav.foreldrepenger.autotest.klienter.fplos.LosSakslisteId;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;

public class Oppgavestyrer extends Saksbehandler {

    public Oppgavestyrer() {
        super(SaksbehandlerRolle.OPPGAVESTYRER);
    }

    public LosSakslisteId opprettSaksliste() {
        var builder = FplosKlient.SakslisteBuilder.nyListe();
        return builder.medSortering().medSorteringIntervall().build();
    }

}
