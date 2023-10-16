package no.nav.foreldrepenger.generator.soknad.api.mapper;

import static no.nav.foreldrepenger.generator.soknad.api.mapper.CommonMapper.tilMedlemskap;
import static no.nav.foreldrepenger.generator.soknad.api.mapper.CommonMapper.tilRelasjonTilBarn;
import static no.nav.foreldrepenger.generator.soknad.api.mapper.CommonMapper.tilVedlegg;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Søker;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.Ytelse;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøkerDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.engangsstønad.EngangsstønadDto;

final class EngangsstønadMapper {

    private EngangsstønadMapper() {
    }

    static no.nav.foreldrepenger.common.domain.Søknad tilEngangsstønad(EngangsstønadDto e, LocalDate mottattDato) {
        return new Søknad(
            mottattDato,
            tilSøker(e.søker()),
            tilYtelse(e),
            null,
            tilVedlegg(e.vedlegg())
        );
    }

    private static Søker tilSøker(SøkerDto søker) {
        return new Søker(BrukerRolle.MOR, søker.språkkode()); // TODO: Frontend sender ikke ned søker her. Kan også være Far/Medmor!
    }

    private static Ytelse tilYtelse(EngangsstønadDto e) {
        return new Engangsstønad(
            tilMedlemskap(e),
            tilRelasjonTilBarn(e.barn(), e.situasjon())
        );
    }
}
