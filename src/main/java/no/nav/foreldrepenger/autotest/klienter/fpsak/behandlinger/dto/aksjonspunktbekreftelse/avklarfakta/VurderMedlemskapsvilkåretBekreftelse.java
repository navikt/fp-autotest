package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

@BekreftelseKode(kode = "5101")
public class VurderMedlemskapsvilkåretBekreftelse extends AksjonspunktBekreftelse {

    protected Avslagsårsak avslagskode;
    protected LocalDate medlemFom;
    protected LocalDate opphørTmo;

    public VurderMedlemskapsvilkåretBekreftelse() {

    }

    public VurderMedlemskapsvilkåretBekreftelse(Avslagsårsak avslagskode) {
        this.avslagskode = avslagskode;
    }
}
