package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

public class VurderMedlemskapsvilkårForutgåendeBekreftelse extends AksjonspunktBekreftelse {

    protected Avslagsårsak avslagskode;
    protected LocalDate medlemFom;
    protected LocalDate opphørFom;

    public VurderMedlemskapsvilkårForutgåendeBekreftelse() {
    }

    public VurderMedlemskapsvilkårForutgåendeBekreftelse(Avslagsårsak avslagskode) {
        this.avslagskode = avslagskode;
    }

    @Override
    public String aksjonspunktKode() {
        return "5102";
    }
}
