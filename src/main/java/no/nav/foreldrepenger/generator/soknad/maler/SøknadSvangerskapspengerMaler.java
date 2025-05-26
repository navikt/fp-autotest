package no.nav.foreldrepenger.generator.soknad.maler;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.VedleggDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.VedleggInnsendingType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.svangerskapspenger.TilretteleggingbehovDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.builder.BarnBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.builder.SvangerskapspengerBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.maler.UtenlandsoppholdMaler;

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
