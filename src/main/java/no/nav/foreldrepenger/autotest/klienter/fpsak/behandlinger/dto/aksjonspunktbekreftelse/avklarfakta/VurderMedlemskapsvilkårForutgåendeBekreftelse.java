package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

import java.time.LocalDate;

@BekreftelseKode(kode = "5102")
public class VurderMedlemskapsvilkårForutgåendeBekreftelse extends AksjonspunktBekreftelse {

    protected Avslagsårsak avslagskode;
    protected LocalDate medlemFom;
    protected LocalDate opphørFom;

    public VurderMedlemskapsvilkårForutgåendeBekreftelse() {

    }

    public VurderMedlemskapsvilkårForutgåendeBekreftelse(Avslagsårsak avslagskode) {
        this.avslagskode = avslagskode;
    }
}
