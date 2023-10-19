package no.nav.foreldrepenger.generator.soknad.maler;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.svangerskapspenger.TilretteleggingDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.BarnBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.SvangerskapspengerBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.SøkerBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.MedlemsskapMaler;

public final class SøknadSvangerskapspengerMaler {

    private SøknadSvangerskapspengerMaler() {
    }

    public static SvangerskapspengerBuilder lagSvangerskapspengerSøknad(BrukerRolle brukerRolle,
                                                                        LocalDate termin,
                                                                        List<TilretteleggingDto> tilrettelegging) {
        return new SvangerskapspengerBuilder(tilrettelegging)
                .medSøker(new SøkerBuilder(brukerRolle).build())
                .medBarn(BarnBuilder.termin(1, termin).build())
                .medMedlemsskap(MedlemsskapMaler.medlemsskapNorge());
    }
}
