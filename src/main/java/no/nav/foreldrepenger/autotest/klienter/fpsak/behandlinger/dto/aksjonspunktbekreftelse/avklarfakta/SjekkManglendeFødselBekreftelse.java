package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SjekkManglendeFødselBekreftelse extends AksjonspunktBekreftelse {

    protected List<BekreftetBarnDto> barn = new ArrayList<>();
    protected LocalDate termindato;

    public SjekkManglendeFødselBekreftelse() {
        super();
    }

    public SjekkManglendeFødselBekreftelse bekreftBarnErFødt(int antallBarn, LocalDate fødselsdato) {
        return bekreftBarnErFødt(antallBarn, fødselsdato, fødselsdato);
    }

    public SjekkManglendeFødselBekreftelse bekreftBarnErFødt(int antallBarn, LocalDate fødselsdato, LocalDate termindato) {
        for (int i = 0; i < antallBarn; i++) {
            barn.add(new BekreftetBarnDto(fødselsdato, null));
        }
        this.termindato = termindato;
        return this;
    }

    public SjekkManglendeFødselBekreftelse bekreftBarnErIkkeFødt(LocalDate termindato) {
        this.termindato = termindato;
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
