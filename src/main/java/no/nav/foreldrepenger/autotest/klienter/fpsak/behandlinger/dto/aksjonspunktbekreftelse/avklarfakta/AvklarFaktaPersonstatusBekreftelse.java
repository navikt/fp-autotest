package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

@BekreftelseKode(kode="5022")
public class AvklarFaktaPersonstatusBekreftelse extends AksjonspunktBekreftelse {

    protected String erEosBorger;
    protected String oppholdsrettVurdering;
    protected Boolean fortsettBehandling;

    public AvklarFaktaPersonstatusBekreftelse() {
        super();
    }

    public AvklarFaktaPersonstatusBekreftelse bekreftErEøsBorger() {
        erEosBorger =  "true";
        return this;
    }

    public AvklarFaktaPersonstatusBekreftelse bekreftErIkkeEøsBorger() {
        erEosBorger =  "false";
        return this;
    }

    public AvklarFaktaPersonstatusBekreftelse bekreftHarOppholdsrett() {
        erEosBorger =  "true";
        return this;
    }

    public AvklarFaktaPersonstatusBekreftelse bekreftHarIkkeOppholdsrett() {
        erEosBorger =  "false";
        return this;
    }

    public AvklarFaktaPersonstatusBekreftelse bekreftHenleggBehandling() {
        fortsettBehandling = false;
        return this;
    }
}
