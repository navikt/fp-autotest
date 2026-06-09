package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;

public class KontrollerAapKombinertAtflBekreftelse extends AksjonspunktBekreftelse {

    @Override
    public String aksjonspunktKode() {
        return AksjonspunktKoder.MANUELL_KONTROLL_AAP_KOMBINERT_ATFL_KODE;
    }
}
