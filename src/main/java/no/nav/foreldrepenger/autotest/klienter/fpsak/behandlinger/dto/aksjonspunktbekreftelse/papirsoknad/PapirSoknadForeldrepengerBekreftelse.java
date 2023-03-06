package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.AnnenForelderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.DekningsgradDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.EgenVirksomhetDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FrilansDto;

@JsonIgnoreProperties(ignoreUnknown = true)
@BekreftelseKode(kode = "5040")
public class PapirSoknadForeldrepengerBekreftelse extends AksjonspunktBekreftelse {

    protected String tema = "FODSL";

    protected String soknadstype = "FP";

    protected String soker = "MOR";

    protected boolean erBarnetFodt = true;

    protected LocalDate termindato = null;

    protected boolean oppholdINorge = true;

    protected boolean harTidligereOppholdUtenlands = false;

    protected boolean harFremtidigeOppholdUtenlands = false;

    protected Integer antallBarn = 1;

    protected Integer antallBarnFraTerminbekreftelse = 1;

    protected LocalDate foedselsDato = LocalDate.now().minusDays(1);

    protected LocalDate mottattDato = LocalDate.now().minusDays(10);

    protected boolean ufullstendigSoeknad;

    protected DekningsgradDto dekningsgrad = DekningsgradDto.HUNDRE;

    protected EgenVirksomhetDto egenVirksomhet = new EgenVirksomhetDto();

    protected FordelingDto tidsromPermisjon = new FordelingDto();

    protected FrilansDto frilans = new FrilansDto();

    protected AnnenForelderDto annenForelder = new AnnenForelderDto();

    protected boolean annenForelderInformert = true;

    public PapirSoknadForeldrepengerBekreftelse morSøkerFødsel(FordelingDto fordeling, LocalDate fødselsdato, LocalDate mottattDato) {
        this.tidsromPermisjon = fordeling;
        this.foedselsDato = fødselsdato;
        this.mottattDato = mottattDato;
        return this;
    }

    public PapirSoknadForeldrepengerBekreftelse morSøkerTermin(FordelingDto fordeling, LocalDate termindato, LocalDate mottattDato,
            DekningsgradDto dekningsgrad) {
        this.tidsromPermisjon = fordeling;
        this.erBarnetFodt = false;
        this.termindato = termindato;
        this.foedselsDato = null;
        this.mottattDato = mottattDato;
        this.dekningsgrad = dekningsgrad;
        return this;
    }
}
