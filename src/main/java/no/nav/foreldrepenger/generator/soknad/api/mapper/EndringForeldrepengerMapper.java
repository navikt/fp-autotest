package no.nav.foreldrepenger.generator.soknad.api.mapper;

import static no.nav.foreldrepenger.generator.soknad.api.mapper.CommonMapper.tilRelasjonTilBarn;
import static no.nav.foreldrepenger.generator.soknad.api.mapper.CommonMapper.tilVedlegg;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Fordeling;
import no.nav.foreldrepenger.generator.soknad.api.dto.endringssøknad.EndringssøknadForeldrepengerDto;

final class EndringForeldrepengerMapper {

    private EndringForeldrepengerMapper() {
    }

    static Endringssøknad tilEndringForeldrepengesøknad(EndringssøknadForeldrepengerDto endringssøknadFP, LocalDate mottattDato) {
        return new Endringssøknad(
            mottattDato,
            ForeldrepengerMapper.tilSøker(endringssøknadFP.søker()),
            tilYtelse(endringssøknadFP),
            endringssøknadFP.tilleggsopplysninger(),
            tilVedlegg(endringssøknadFP.vedlegg()),
            endringssøknadFP.saksnummer());
    }

    private static Foreldrepenger tilYtelse(EndringssøknadForeldrepengerDto f) {
        return new Foreldrepenger(
            null,
            tilRelasjonTilBarn(f.barn(), f.situasjon()),
            null,
            null,
            null,
            tilFordeling(f),
            null
        );
    }

    private static Fordeling tilFordeling(EndringssøknadForeldrepengerDto f) {
        return new Fordeling(
            f.annenforelder().erInformertOmSøknaden(),
            ForeldrepengerMapper.tilLukketPeriodeMedVedlegg(f.uttaksplan()),
            f.ønskerJustertUttakVedFødsel()
        );
    }
}

