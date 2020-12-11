package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.Fagsystem;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

@AksjonspunktKode(kode = "5030", fagsystem = Fagsystem.FPTILBAKE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApVerge extends AksjonspunktBehandling {

    protected String begrunnelse;
    protected String fnr;
    protected LocalDate gyldigFom;
    protected LocalDate gyldigTom;
    protected String navn;
    protected String organisasjonsnummer;
    protected String vergeType;

    public ApVerge() {
        this.kode = "5030";
        this.begrunnelse = "Dette er en begrunnelse dannet av Autotest!";
        this.gyldigFom = LocalDate.now().withDayOfMonth(1).minusMonths(6);
        this.gyldigTom = LocalDate.now().withDayOfMonth(1).plusMonths(6);
    }

    public void setVerge(TestscenarioDto person) {
        this.fnr = person.personopplysninger().søkerIdent();
        this.navn = "VERGE PERSON";
        this.vergeType = "VOKSEN";
    }
    public void setVerge(String orgnummer){
        this.navn = "Sleip Advokat";
        this.organisasjonsnummer = orgnummer;
        this.vergeType = "ADVOKAT";
    }
}
