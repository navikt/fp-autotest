package no.nav.foreldrepenger.generator.soknad.maler;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.common.domain.felles.DokumentType;
import no.nav.foreldrepenger.common.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.VedleggDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.svangerskapspenger.tilretteleggingbehov.TilretteleggingbehovDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.builder.BarnBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.builder.SvangerskapspengerBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.maler.UtenlandsoppholdMaler;

public final class SøknadSvangerskapspengerMaler {

    private SøknadSvangerskapspengerMaler() {
    }

    public static SvangerskapspengerBuilder lagSvangerskapspengerSøknad(LocalDate termin, List<TilretteleggingbehovDto> tilrettelegging) {
        var vedlegg = tilrettelegging.stream()
                .map(SøknadSvangerskapspengerMaler::lagVedleggFor)
                .toList();
        return new SvangerskapspengerBuilder(tilrettelegging)
                .medBarn(BarnBuilder.termin(1, termin).build())
                .medUtenlandsopphold(UtenlandsoppholdMaler.oppholdBareINorge())
                .medVedlegg(vedlegg);
    }

    private static VedleggDto lagVedleggFor(TilretteleggingbehovDto tilretteleggingbehov) {
        return new VedleggDto(UUID.randomUUID(), DokumentType.I000109, InnsendingsType.SEND_SENERE, null,
                new VedleggDto.Dokumenterer(VedleggDto.Dokumenterer.Type.TILRETTELEGGING, tilretteleggingbehov.arbeidsforhold(), null)
        );
    }
}
