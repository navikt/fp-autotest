package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.Fagsystem;
import no.nav.foreldrepenger.generator.familie.Familie;

@BekreftelseKode(kode = "5030", fagsystem = Fagsystem.FPTILBAKE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApVerge extends AksjonspunktBekreftelse {

    protected String fnr;
    protected LocalDate gyldigFom;
    protected LocalDate gyldigTom;
    protected String navn;
    protected String organisasjonsnummer;
    protected String vergeType;

    public ApVerge() {
        setBegrunnelse("Dette er en begrunnelse dannet av Autotest!");
        this.gyldigFom = LocalDate.now().withDayOfMonth(1).minusMonths(6);
        this.gyldigTom = LocalDate.now().withDayOfMonth(1).plusMonths(6);
    }

    public void setVerge(Familie familie) {
        this.fnr = familie.mor().fødselsnummer().value();
        this.navn = "VERGE PERSON";
        this.vergeType = "VOKSEN";
    }
    public void setVerge(String orgnummer){
        this.navn = "Sleip Advokat";
        this.organisasjonsnummer = orgnummer;
        this.vergeType = "ADVOKAT";
    }
}
