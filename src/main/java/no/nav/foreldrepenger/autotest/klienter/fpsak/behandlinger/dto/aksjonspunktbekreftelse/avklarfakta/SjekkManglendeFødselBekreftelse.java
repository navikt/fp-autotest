package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SjekkManglendeFødselBekreftelse extends AksjonspunktBekreftelse {

    protected Boolean erBarnFødt;
    protected List<BekreftetBarnDto> barn = new ArrayList<>();

    public SjekkManglendeFødselBekreftelse() {
        super();
    }

    public SjekkManglendeFødselBekreftelse bekreftBarnErFødt(int antallBarn, LocalDate dato) {
        erBarnFødt = true;
        for (int i = 0; i < antallBarn; i++) {
            barn.add(new BekreftetBarnDto(dato, null));
        }
        return this;
    }

    public SjekkManglendeFødselBekreftelse bekreftBarnErIkkeFødt() {
        erBarnFødt = false;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5027";
    }

    public record BekreftetBarnDto(LocalDate fødselsdato,
                                   LocalDate dødsdato) {
    }
}
