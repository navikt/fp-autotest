package no.nav.foreldrepenger.generator.soknad.maler;

import static no.nav.foreldrepenger.generator.Landkoder.USA;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.soknad.kontrakt.UtenlandsoppholdsperiodeDto;


public final class UtenlandsoppholdMaler {

    private UtenlandsoppholdMaler() {
    }

    public static List<UtenlandsoppholdsperiodeDto> oppholdBareINorge() {
        return List.of();
    }

    public static List<UtenlandsoppholdsperiodeDto> oppholdIUtlandetForrige12mnd() {
        return List.of(new UtenlandsoppholdsperiodeDto(LocalDate.now().minusYears(2), LocalDate.now(), USA));
    }
}
