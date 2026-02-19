package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.InnsynResultatType;

public class VurderingAvInnsynBekreftelse extends AksjonspunktBekreftelse {

    public LocalDate mottattDato;
    public LocalDate fristDato;
    public List<Object> innsynDokumenter = new ArrayList<>();
    public String innsynResultatType;
    public Boolean sattPåVent;

    public VurderingAvInnsynBekreftelse setMottattDato(LocalDate mottattDato) {
        this.mottattDato = mottattDato;
        this.fristDato = mottattDato.plusDays(4);
        return this;
    }

    public VurderingAvInnsynBekreftelse setInnsynResultatType(InnsynResultatType innsynResultatType) {
        this.innsynResultatType = innsynResultatType.getKode();
        return this;
    }

    public VurderingAvInnsynBekreftelse skalSetteSakPåVent(boolean settPåVent) {
        this.sattPåVent = settPåVent;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5037";
    }
}
