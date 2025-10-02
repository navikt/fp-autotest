package no.nav.foreldrepenger.generator.soknad.mapper;

import static no.nav.foreldrepenger.generator.soknad.mapper.CommonMapper.tilOppholdIUtlandet;
import static no.nav.foreldrepenger.generator.soknad.mapper.CommonMapper.tilRelasjonTilBarn;
import static no.nav.foreldrepenger.generator.soknad.mapper.CommonMapper.tilVedlegg;

import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.EngangsstønadDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.VedleggDto;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Søker;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.Ytelse;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;

public final class EngangsstønadMapper {

    private EngangsstønadMapper() {
    }

    public static Søknad tilEngangsstønad(EngangsstønadDto e, List<VedleggDto> påkrevdeVedlegg, LocalDateTime mottattDato) {
        return new Søknad(mottattDato.toLocalDate(), tilSøker(e.språkkode(), e.rolle()), tilYtelse(e, påkrevdeVedlegg), null, tilVedlegg(påkrevdeVedlegg));
    }

    private static Søker tilSøker(Målform språkkode, BrukerRolle rolle) {
        return new Søker(rolle == null ? BrukerRolle.MOR : rolle, språkkode);
    }

    private static Ytelse tilYtelse(EngangsstønadDto e, List<VedleggDto> vedlegg) {
        return new Engangsstønad(tilOppholdIUtlandet(e.utenlandsopphold()), tilRelasjonTilBarn(e.barn(), vedlegg));
    }


}
