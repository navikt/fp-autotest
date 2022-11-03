package no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak;

import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelseUtenTotrinn;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.openam.SaksbehandlerRolle;

public class Beslutter extends Saksbehandler {

    public Beslutter() {
        super(SaksbehandlerRolle.BESLUTTER);
    }

    public List<Aksjonspunkt> hentAksjonspunktSomSkalTilTotrinnsBehandling() {
        return valgtBehandling.getAksjonspunkt().stream()
                .filter(Aksjonspunkt::skalTilToTrinnsBehandling)
                .toList();
    }

    public void fattVedtakOgVentTilAvsluttetBehandling(FatterVedtakBekreftelse bekreftelse) {
        bekreftAksjonspunkt(bekreftelse);
        ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
    }

    public void fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling() {
        bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelseUtenTotrinn.class);
        ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
    }
}
