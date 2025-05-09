package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.UidentifisertBarn;

public class VurderManglendeFodselBekreftelse extends AksjonspunktBekreftelse {

    protected Integer antallBarnFodt; // brukes ikke i ES?
    protected LocalDate fodselsdato; // brukes ikke i ES?
    protected boolean dokumentasjonForeligger;
    protected boolean brukAntallBarnITps;
    protected List<UidentifisertBarn> uidentifiserteBarn = new ArrayList<>();

    public VurderManglendeFodselBekreftelse() {
        super();
    }

    public VurderManglendeFodselBekreftelse bekreftDokumentasjonForeligger(int antallBarn, LocalDate dato) {
        dokumentasjonForeligger = true;
        antallBarnFodt = antallBarn;
        for (int i = 0; i < antallBarn; i++) {
            uidentifiserteBarn.add(new UidentifisertBarn(dato, null));
        }
        fodselsdato = dato;
        return this;
    }

    public VurderManglendeFodselBekreftelse bekreftDokumentasjonIkkeForeligger() {
        uidentifiserteBarn.add(new UidentifisertBarn(null, null));
        dokumentasjonForeligger = false;
        antallBarnFodt = null;
        return this;
    }

    public void bekreftBrukAntallBarnITps() {
        brukAntallBarnITps = true;
    }

    @Override
    public String aksjonspunktKode() {
        return "5027";
    }
}
