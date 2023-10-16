package no.nav.foreldrepenger.generator.soknad.api.erketyper;


import java.util.List;

import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.generator.soknad.api.builder.BarnHelper;
import no.nav.foreldrepenger.generator.soknad.api.builder.EndringssøknadBuilder;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøknadDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.ForeldrepengesøknadDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.UttaksplanPeriodeDto;

public class SøknadEndringErketyper {

    private SøknadEndringErketyper() {
        // Skal ikke instansieres
    }

    public static EndringssøknadBuilder lagEndringssøknad(SøknadDto søknadDto, Saksnummer saksnummer, List<UttaksplanPeriodeDto> fordeling) {
        var foreldrepengesøknadFS = (ForeldrepengesøknadDto) søknadDto;
        return new EndringssøknadBuilder(saksnummer)
                .medSøker(søknadDto.søker())
                .medAnnenforelder(foreldrepengesøknadFS.annenForelder())
                .medBarn(new BarnHelper(søknadDto.barn(), søknadDto.situasjon()))
                .medFordeling(fordeling);
    }
}
