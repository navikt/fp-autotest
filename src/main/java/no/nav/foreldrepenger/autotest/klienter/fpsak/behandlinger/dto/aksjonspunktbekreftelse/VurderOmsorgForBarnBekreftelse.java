package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@BekreftelseKode(kode = "5061")
public class VurderOmsorgForBarnBekreftelse extends AksjonspunktBekreftelse {

    protected Boolean omsorg;
    protected List<Periode> ikkeOmsorgPerioder = new ArrayList<>();

    public VurderOmsorgForBarnBekreftelse() {
        super();
    }

    public void bekreftBrukerHarOmsorg() {
        omsorg = true;
    }

    public void bekreftBrukerHarIkkeOmsorg(LocalDate fra, LocalDate til) {
        omsorg = false;
        ikkeOmsorgPerioder.add(new Periode(fra, til));
    }

    public static class Periode {
        LocalDate periodeFom;
        LocalDate periodeTom;

        public Periode(LocalDate periodeFom, LocalDate periodeTom) {
            this.periodeFom = periodeFom;
            this.periodeTom = periodeTom;
        }
    }
}
