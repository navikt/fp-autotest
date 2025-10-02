package no.nav.foreldrepenger.generator.soknad.maler;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.VedleggDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.VedleggInnsendingType;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.svangerskapspenger.TilretteleggingbehovDto;
import no.nav.foreldrepenger.generator.soknad.builder.BarnBuilder;
import no.nav.foreldrepenger.generator.soknad.builder.SvangerskapspengerBuilder;

public final class SøknadSvangerskapspengerMaler {

    private SøknadSvangerskapspengerMaler() {
    }

    public static SvangerskapspengerBuilder lagSvangerskapspengerSøknad(LocalDate termin, List<TilretteleggingbehovDto> tilrettelegging) {
        var vedlegg = tilrettelegging.stream()
                .map(SøknadSvangerskapspengerMaler::påkrevdVedleggFor)
                .toList();
        return new SvangerskapspengerBuilder(tilrettelegging)
                .medBarn(BarnBuilder.termin(1, termin).build())
                .medUtenlandsopphold(UtenlandsoppholdMaler.oppholdBareINorge())
                .medVedlegg(vedlegg);
    }

    private static VedleggDto påkrevdVedleggFor(TilretteleggingbehovDto tilretteleggingbehov) {
        return VedleggMaler.dokumenterTilrettelegging(tilretteleggingbehov, VedleggInnsendingType.LASTET_OPP);
    }
}
