package no.nav.foreldrepenger.generator.soknad.maler;


import java.util.List;

import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.BarnHelper;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.EndringssøknadBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.SøknadDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.ForeldrepengesøknadDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.UttaksplanPeriodeDto;

public class SøknadEndringMaler {

    private SøknadEndringMaler() {
        // Skal ikke instansieres
    }

    public static EndringssøknadBuilder lagEndringssøknad(SøknadDto søknadDto, Saksnummer saksnummer, List<UttaksplanPeriodeDto> fordeling) {
        var foreldrepengesøknadFS = (ForeldrepengesøknadDto) søknadDto;
        return new EndringssøknadBuilder(saksnummer)
                .medSøker(søknadDto.søker())
                .medAnnenForelder(foreldrepengesøknadFS.annenForelder())
                .medBarn(new BarnHelper(søknadDto.barn(), søknadDto.situasjon()))
                .medFordeling(fordeling);
    }
}
