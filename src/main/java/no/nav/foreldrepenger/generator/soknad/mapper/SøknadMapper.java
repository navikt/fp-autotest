package no.nav.foreldrepenger.generator.soknad.mapper;

import static no.nav.foreldrepenger.generator.soknad.mapper.EndringForeldrepengerMapper.tilEndringForeldrepengesøknad;
import static no.nav.foreldrepenger.generator.soknad.mapper.EngangsstønadMapper.tilEngangsstønad;
import static no.nav.foreldrepenger.generator.soknad.mapper.ForeldrepengerMapper.tilForeldrepengesøknad;
import static no.nav.foreldrepenger.generator.soknad.mapper.SvangerskapspengerMapper.tilSvangerskapspengesøknad;

import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.EndringssøknadForeldrepengerDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.EngangsstønadDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.ForeldrepengesøknadDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.SvangerskapspengesøknadDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.SøknadDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.VedleggDto;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;

public final class SøknadMapper {

    private SøknadMapper() {
    }

    public static Søknad tilSøknad(SøknadDto innsending, LocalDateTime mottattDato) {
        var påkrevdeVedlegg = innsending.vedlegg();
        if (innsending instanceof SøknadDto søknadV2) {
            return tilSøknad(søknadV2, påkrevdeVedlegg, mottattDato);
        } else if (innsending instanceof EndringssøknadForeldrepengerDto endrringsøknad) {
            return tilEndringssøknad(endrringsøknad, påkrevdeVedlegg, mottattDato);
        }
        throw new IllegalArgumentException("Utviklerfeil: Ukjent søknad " + innsending.getClass().getSimpleName());
    }

    private static Søknad tilSøknad(SøknadDto søknad, List<VedleggDto> påkrevdeVedlegg, LocalDateTime mottattDato) {
        if (søknad instanceof EngangsstønadDto e) {
            return tilEngangsstønad(e, påkrevdeVedlegg, mottattDato);
        }
        if (søknad instanceof ForeldrepengesøknadDto f) {
            return tilForeldrepengesøknad(f, påkrevdeVedlegg, mottattDato);
        }
        if (søknad instanceof SvangerskapspengesøknadDto s) {
            return tilSvangerskapspengesøknad(s, påkrevdeVedlegg, mottattDato);
        }
        throw new IllegalArgumentException("Ukjent søknad " + søknad.getClass().getSimpleName());
    }

    private static Endringssøknad tilEndringssøknad(EndringssøknadForeldrepengerDto endringssøknad, List<VedleggDto> påkrevdeVedlegg, LocalDateTime mottattDato) {
        if (endringssøknad instanceof EndringssøknadForeldrepengerDto f) {
            return tilEndringForeldrepengesøknad(f, påkrevdeVedlegg, mottattDato);
        }
        throw new IllegalArgumentException("Ukjent søknad " + endringssøknad.getClass().getSimpleName());
    }
}
