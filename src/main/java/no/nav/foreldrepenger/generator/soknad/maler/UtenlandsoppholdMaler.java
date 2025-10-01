package no.nav.foreldrepenger.generator.soknad.maler;

import java.time.LocalDate;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.UtenlandsoppholdsperiodeDto;


public final class UtenlandsoppholdMaler {

    private UtenlandsoppholdMaler() {
    }

    public static List<UtenlandsoppholdsperiodeDto> oppholdBareINorge() {
        return List.of();
    }

    public static List<UtenlandsoppholdsperiodeDto> oppholdIUtlandetForrige12mnd() {
        return List.of(new UtenlandsoppholdsperiodeDto(LocalDate.now().minusYears(2), LocalDate.now(), CountryCode.US));
    }
}
