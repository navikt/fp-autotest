package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.AnnenForelderDto;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@BekreftelseKode(kode="5012")
public class PapirSoknadEngangstonadBekreftelse extends AksjonspunktBekreftelse {
    protected String tema = "FODSL";
    protected String soknadstype = "ES";
    protected LocalDate mottattDato = LocalDate.now().minusDays(10);

    protected boolean harFremtidigOppholdUtlands = false;
    protected boolean harTidligereOppholdUtenlands = false;
    protected boolean oppholdINorge = true;

    protected String soker = "MOR";
    protected boolean erBarnetFodt = true;
    protected Integer antallBarn = 1;
    protected List<LocalDate> foedselsDato =  Collections.singletonList(LocalDate.now().minusWeeks(1));
    protected AnnenForelderDto annenForelder = new AnnenForelderDto();


    public PapirSoknadEngangstonadBekreftelse setSøker (SøkersRolle søker) {
        this.soker = søker.name();
        return this;
    }
    public PapirSoknadEngangstonadBekreftelse setFoedselsDato(LocalDate foedselsDato) {
        this.foedselsDato = Collections.singletonList(foedselsDato);
        return this;
    }

}
