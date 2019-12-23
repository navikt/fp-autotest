package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode="5019")
public class AvklarLovligOppholdBekreftelse extends BekreftedePerioderMalDto{

    protected boolean erEosBorger;
    protected boolean lovligOppholdVurdering;

    public AvklarLovligOppholdBekreftelse() {
        super();
    }

    public void bekreftErEosBorger() {
        erEosBorger = true;
    }

    public void bekreftErIkkeEosBorger() {
        erEosBorger = true;
    }

    public void bekreftHarLovligOpphold() {
        lovligOppholdVurdering = true;
    }

    public void bekreftHarIkkeLovligOpphold() {
        lovligOppholdVurdering = false;
    }
}
