package no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak;

import java.util.List;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelseUtenTotrinn;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;

public class Beslutter extends Saksbehandler {

    public Beslutter() {
        super(Aktoer.Rolle.BESLUTTER);
    }

    public List<Aksjonspunkt> hentAksjonspunktSomSkalTilTotrinnsBehandling() {
        return valgtBehandling.getAksjonspunkter().stream()
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
