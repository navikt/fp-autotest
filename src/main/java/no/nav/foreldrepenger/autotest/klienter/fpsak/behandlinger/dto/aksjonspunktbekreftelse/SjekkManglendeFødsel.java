package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SjekkManglendeFødsel extends AksjonspunktBekreftelse {

    protected Boolean erBarnFødt;
    protected List<BekreftetBarnDto> barn = new ArrayList<>();

    public SjekkManglendeFødsel() {
        super();
    }

    public SjekkManglendeFødsel bekreftBarnErFødt(int antallBarn, LocalDate dato) {
        erBarnFødt = true;
        for (int i = 0; i < antallBarn; i++) {
            barn.add(new BekreftetBarnDto(dato, null));
        }
        return this;
    }

    public SjekkManglendeFødsel bekreftBarnErIkkeFødt() {
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
