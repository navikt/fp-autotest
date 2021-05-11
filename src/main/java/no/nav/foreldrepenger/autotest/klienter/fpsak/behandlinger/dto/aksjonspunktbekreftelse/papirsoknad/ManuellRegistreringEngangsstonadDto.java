package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad;

import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder.REGISTRER_PAPIRSØKNAD_ENGANGSSTØNAD;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.FamilieHendelseType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.AnnenForelderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.RettigheterDto;


@BekreftelseKode(kode = REGISTRER_PAPIRSØKNAD_ENGANGSSTØNAD)
public class ManuellRegistreringEngangsstonadDto extends ManuellRegistreringDto {

    public ManuellRegistreringEngangsstonadDto() {
        super("ES");
    }

    public ManuellRegistreringEngangsstonadDto tema(FamilieHendelseType type) {
        setTema(type);
        return this;
    }

    public ManuellRegistreringEngangsstonadDto søkersRolle(String søker) {
        setSoker(søker);
        return this;
    }

    public ManuellRegistreringEngangsstonadDto rettighet(RettigheterDto rettigheter) {
        setRettigheter(rettigheter);
        return this;
    }

    public ManuellRegistreringEngangsstonadDto omsorgovertakelse(LocalDate omsorgsovertakelse) {
        var omsorgDto = getOmsorg();
        omsorgDto.setOmsorgsovertakelsesdato(omsorgsovertakelse);
        omsorgDto.setAnkomstdato(omsorgsovertakelse);
        setOmsorg(omsorgDto);
        return this;
    }


    public ManuellRegistreringEngangsstonadDto barnFødselsdatoer(LocalDate barnFødselsdato, int antallBarn) {
        var omsorgDto = getOmsorg();
        omsorgDto.setAntallBarn(antallBarn);
        setAntallBarn(antallBarn);
        setAntallBarnFraTerminbekreftelse(antallBarn);
        List<LocalDate> localDates = new ArrayList<>();
        for (int i=0; i < antallBarn; i++) {
            localDates.add(barnFødselsdato);
        }
        setFoedselsDato(localDates);
        omsorgDto.setFoedselsDato(localDates);
        return this;
    }

    public ManuellRegistreringEngangsstonadDto annenpart(AnnenForelderDto annenForelderDto) {
        setAnnenForelder(annenForelderDto);
        return this;
    }


}
