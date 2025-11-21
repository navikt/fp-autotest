package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.AnnenForelderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.DekningsgradDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.EgenVirksomhetDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FrilansDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PapirSøknadForeldrepengerBekreftelse extends AksjonspunktBekreftelse {

    protected String tema = "FODSL";

    protected String søknadstype = "FP";

    protected String søker = "MOR";

    protected boolean erBarnetFødt = true;

    protected LocalDate termindato = null;

    protected boolean oppholdINorge = true;

    protected boolean harTidligereOppholdUtenlands = false;

    protected boolean harFremtidigeOppholdUtenlands = false;

    protected Integer antallBarn = 1;

    protected Integer antallBarnFraTerminbekreftelse = 1;

    protected LocalDate fødselsdato = LocalDate.now().minusDays(1);

    protected LocalDate mottattDato = LocalDate.now().minusDays(10);

    protected boolean ufullstendigSøknad;

    protected DekningsgradDto dekningsgrad = DekningsgradDto.HUNDRE;

    protected EgenVirksomhetDto egenVirksomhet = new EgenVirksomhetDto();

    protected FordelingDto tidsromPermisjon = new FordelingDto();

    protected FrilansDto frilans = new FrilansDto();

    protected AnnenForelderDto annenForelder = new AnnenForelderDto();

    protected boolean annenForelderInformert = true;

    public PapirSøknadForeldrepengerBekreftelse morSøkerFødsel(FordelingDto fordeling, LocalDate fødselsdato, LocalDate mottattDato) {
        this.tidsromPermisjon = fordeling;
        this.fødselsdato = fødselsdato;
        this.mottattDato = mottattDato;
        return this;
    }

    public PapirSøknadForeldrepengerBekreftelse morSøkerTermin(FordelingDto fordeling, LocalDate termindato, LocalDate mottattDato,
                                                               DekningsgradDto dekningsgrad) {
        this.tidsromPermisjon = fordeling;
        this.erBarnetFødt = false;
        this.termindato = termindato;
        this.fødselsdato = null;
        this.mottattDato = mottattDato;
        this.dekningsgrad = dekningsgrad;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5040";
    }
}
