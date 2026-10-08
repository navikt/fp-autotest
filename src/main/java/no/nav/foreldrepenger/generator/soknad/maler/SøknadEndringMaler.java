package no.nav.foreldrepenger.generator.soknad.maler;


import java.util.List;

import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.soknad.kontrakt.EndringssøknadForeldrepengerDto;
import no.nav.foreldrepenger.soknad.kontrakt.ForeldrepengesøknadDto;
import no.nav.foreldrepenger.soknad.kontrakt.Målform;
import no.nav.foreldrepenger.soknad.kontrakt.SøkerDto;
import no.nav.foreldrepenger.soknad.kontrakt.SøknadDto;
import no.nav.foreldrepenger.soknad.kontrakt.barn.BarnDto;
import no.nav.foreldrepenger.soknad.kontrakt.builder.EndringssøknadBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UttakPeriodeDto;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.UttaksplanDto;

public class SøknadEndringMaler {

    private SøknadEndringMaler() {
        // Skal ikke instansieres
    }

    /**
     * Endringssøknad med {@code uttaksplanDto} som hele den nye planen. Barn, rolle og annen forelder hentes fra
     * {@code forrigeSøknad}, som kan være en førstegangssøknad eller en endringssøknad.
     */
    public static EndringssøknadBuilder lagEndringssøknad(SøknadDto forrigeSøknad,
                                                          Saksnummer saksnummer,
                                                          UttaksplanDto uttaksplanDto) {
        var builder = switch (forrigeSøknad) {
            case ForeldrepengesøknadDto f ->
                    ny(saksnummer, f.søkerinfo(), f.barn()).medRolle(f.rolle()).medAnnenForelder(f.annenForelder());
            case EndringssøknadForeldrepengerDto e ->
                    ny(saksnummer, e.søkerinfo(), e.barn()).medRolle(e.rolle()).medAnnenForelder(e.annenForelder());
            default ->
                    throw new IllegalArgumentException("Kan ikke lage endringssøknad fra " + forrigeSøknad.getClass().getSimpleName());
        };
        return builder.medUttaksplan(uttaksplanDto);
    }

    public static EndringssøknadBuilder lagEndringssøknad(SøknadDto forrigeSøknad,
                                                          Saksnummer saksnummer,
                                                          List<UttakPeriodeDto> perioder) {
        return lagEndringssøknad(forrigeSøknad, saksnummer, UttakMaler.fordeling(perioder));
    }

    private static EndringssøknadBuilder ny(Saksnummer saksnummer, SøkerDto søkerinfo, BarnDto barn) {
        return new EndringssøknadBuilder(saksnummer)
                .medSpråkkode(Målform.NB).medSøkerinfo(søkerinfo).medBarn(barn);
    }

}
