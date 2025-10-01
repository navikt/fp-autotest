package no.nav.foreldrepenger.generator.soknad.mapper;

import static no.nav.foreldrepenger.generator.soknad.mapper.CommonMapper.tilRelasjonTilBarn;
import static no.nav.foreldrepenger.generator.soknad.mapper.CommonMapper.tilVedlegg;
import static no.nav.foreldrepenger.generator.soknad.mapper.ForeldrepengerMapper.tilAnnenForelder;
import static no.nav.foreldrepenger.generator.soknad.mapper.ForeldrepengerMapper.tilRettigheter;
import static no.nav.foreldrepenger.generator.soknad.mapper.ForeldrepengerMapper.tilSøker;
import static no.nav.foreldrepenger.generator.soknad.mapper.ForeldrepengerMapper.tilUttaksplan;

import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.EndringssøknadForeldrepengerDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.VedleggDto;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;


public final class EndringForeldrepengerMapper {

    private EndringForeldrepengerMapper() {
    }

    public static Endringssøknad tilEndringForeldrepengesøknad(EndringssøknadForeldrepengerDto endringssøknadFP,
                                                               List<VedleggDto> påkrevdeVedlegg,
                                                               LocalDateTime mottattDato) {
        return new Endringssøknad(mottattDato.toLocalDate(),
            tilSøker(endringssøknadFP.rolle(), endringssøknadFP.språkkode()),
            tilYtelse(endringssøknadFP, påkrevdeVedlegg),
            null,
            tilVedlegg(påkrevdeVedlegg),
            endringssøknadFP.saksnummer());
    }

    private static Foreldrepenger tilYtelse(EndringssøknadForeldrepengerDto f, List<VedleggDto> vedlegg) {
        return new Foreldrepenger(tilAnnenForelder(f.annenForelder()),
            tilRelasjonTilBarn(f.barn(), vedlegg),
            tilRettigheter(f.annenForelder()),
            null,
            null,
            tilUttaksplan(f.uttaksplan(), f.annenForelder(), vedlegg),
            null);
    }
}

