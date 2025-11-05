package no.nav.foreldrepenger.generator.soknad.maler;


import java.util.List;

import no.nav.foreldrepenger.kontrakter.fpsoknad.ForeldrepengesøknadDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Målform;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.kontrakter.fpsoknad.SøknadDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.builder.EndringssøknadBuilder;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.UttaksplanDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.Uttaksplanperiode;

public class SøknadEndringMaler {

    private SøknadEndringMaler() {
        // Skal ikke instansieres
    }

    public static EndringssøknadBuilder lagEndringssøknad(SøknadDto søknadDto, Saksnummer saksnummer, UttaksplanDto uttaksplanDto) {
        var foreldrepengesøknadFS = (ForeldrepengesøknadDto) søknadDto;
        return new EndringssøknadBuilder(saksnummer)
                .medSpråkkode(Målform.NB)
                .medSøkerinfo(søknadDto.søkerinfo())
                .medRolle(foreldrepengesøknadFS.rolle())
                .medAnnenForelder(foreldrepengesøknadFS.annenForelder())
                .medBarn(søknadDto.barn())
                .medUttaksplan(uttaksplanDto);
    }

    public static EndringssøknadBuilder lagEndringssøknad(SøknadDto søknadDto, Saksnummer saksnummer, List<Uttaksplanperiode> uttaksplanDto) {
        return lagEndringssøknad(søknadDto, saksnummer, new UttaksplanDto(false, uttaksplanDto));
    }
}
