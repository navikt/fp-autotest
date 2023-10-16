package no.nav.foreldrepenger.generator.soknad.api.mapper;

import static no.nav.foreldrepenger.generator.soknad.api.mapper.EndringForeldrepengerMapper.tilEndringForeldrepengesøknad;
import static no.nav.foreldrepenger.generator.soknad.api.mapper.EngangsstønadMapper.tilEngangsstønad;
import static no.nav.foreldrepenger.generator.soknad.api.mapper.ForeldrepengerMapper.tilForeldrepengesøknad;
import static no.nav.foreldrepenger.generator.soknad.api.mapper.SvangerskapspengerMapper.tilSvangerskapspengesøknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøknadDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.endringssøknad.EndringssøknadDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.endringssøknad.EndringssøknadForeldrepengerDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.engangsstønad.EngangsstønadDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.ForeldrepengesøknadDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.svangerskapspenger.SvangerskapspengesøknadDto;

public final class SøknadMapper {

    private SøknadMapper() {
    }

    public static Søknad tilSøknad(SøknadDto søknad, LocalDate mottattDato) {
        if (søknad instanceof ForeldrepengesøknadDto f) {
            return tilForeldrepengesøknad(f, mottattDato);
        }
        if (søknad instanceof EngangsstønadDto e) {
            return tilEngangsstønad(e, mottattDato);
        }
        if (søknad instanceof SvangerskapspengesøknadDto s) {
            return tilSvangerskapspengesøknad(s, mottattDato);
        }
        throw new IllegalArgumentException("Ukjent søknad " + søknad.getClass().getSimpleName());
    }

    public static Endringssøknad tilEndringssøknad(EndringssøknadDto endringssøknad, LocalDate mottattDato) {
        if (endringssøknad instanceof EndringssøknadForeldrepengerDto f) {
            return tilEndringForeldrepengesøknad(f, mottattDato);
        }
        throw new IllegalArgumentException("Ukjent søknad " + endringssøknad.getClass().getSimpleName());
    }
}
