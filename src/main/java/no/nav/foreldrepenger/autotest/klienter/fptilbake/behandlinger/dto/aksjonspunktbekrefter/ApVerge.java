package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.Fagsystem;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

@AksjonspunktKode(kode = "5030", fagsystem = Fagsystem.FPTILBAKE)
public class ApVerge extends AksjonspunktBehandling {

    protected String begrunnelse;
    protected String fnr;
    protected LocalDate gyldigFom;
    protected LocalDate gyldigTom;
    protected String navn;
    protected String vergeType;

    public ApVerge() {
        this.kode = "5030";
        this.begrunnelse = "Dette er en begrunnelse dannet av Autotest!";
    }

    public void setVerge(TestscenarioDto person) {
        this.fnr = person.getPersonopplysninger().getSøkerIdent();
        this.gyldigFom = LocalDate.now().withDayOfMonth(1).minusMonths(3);
        this.gyldigTom = LocalDate.now().withDayOfMonth(1).plusMonths(6);
        this.navn = "VERGE PERSON";
        this.vergeType = "VOKSEN";
    }
}
