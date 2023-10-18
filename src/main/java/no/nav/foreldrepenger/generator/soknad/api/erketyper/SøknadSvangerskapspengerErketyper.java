package no.nav.foreldrepenger.generator.soknad.api.erketyper;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.generator.soknad.api.builder.SvangerskapspengerBuilder;
import no.nav.foreldrepenger.generator.soknad.api.builder.SøkerBuilder;
import no.nav.foreldrepenger.generator.soknad.api.dto.svangerskapspenger.TilretteleggingDto;

public final class SøknadSvangerskapspengerErketyper {

    private SøknadSvangerskapspengerErketyper() {
    }

    public static SvangerskapspengerBuilder lagSvangerskapspengerSøknad(BrukerRolle brukerRolle,
                                                                        LocalDate termin,
                                                                        List<TilretteleggingDto> tilrettelegging) {
        return new SvangerskapspengerBuilder(tilrettelegging)
                .medSøker(new SøkerBuilder(brukerRolle).build())
                .medBarn(RelasjonTilBarnErketyper.termin(1, termin))
                .medMedlemsskap(MedlemsskapErketyper.medlemsskapNorge());
    }
}
