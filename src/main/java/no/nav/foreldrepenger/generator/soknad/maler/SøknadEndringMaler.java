package no.nav.foreldrepenger.generator.soknad.maler;


import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.ForeldrepengesøknadDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.SøknadDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.foreldrepenger.uttaksplan.UttaksplanDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.foreldrepenger.uttaksplan.Uttaksplanperiode;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.generator.soknad.builder.EndringssøknadBuilder;

public class SøknadEndringMaler {

    private SøknadEndringMaler() {
        // Skal ikke instansieres
    }

    public static EndringssøknadBuilder lagEndringssøknad(SøknadDto søknadDto, Saksnummer saksnummer, UttaksplanDto uttaksplanDto) {
        var foreldrepengesøknadFS = (ForeldrepengesøknadDto) søknadDto;
        return new EndringssøknadBuilder(saksnummer)
                .medSpråkkode(Målform.NB)
                .medSøkerinfo(søknadDto.søkerinfo())
                .medRolle(søknadDto.rolle())
                .medAnnenForelder(foreldrepengesøknadFS.annenForelder())
                .medBarn(søknadDto.barn())
                .medUttaksplan(uttaksplanDto);
    }

    public static EndringssøknadBuilder lagEndringssøknad(SøknadDto søknadDto, Saksnummer saksnummer, List<Uttaksplanperiode> uttaksplanDto) {
        return lagEndringssøknad(søknadDto, saksnummer, new UttaksplanDto(false, uttaksplanDto));
    }
}
