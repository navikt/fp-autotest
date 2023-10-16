package no.nav.foreldrepenger.generator.soknad.api.erketyper;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.generator.soknad.api.dto.UtenlandsoppholdDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.UtenlandsoppholdPeriodeDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.ÅpenPeriodeDto;

public final class MedlemsskapErketyper {

    private MedlemsskapErketyper() {
    }

    public static UtenlandsoppholdDto medlemsskapNorge() {
        return new UtenlandsoppholdDto(emptyList(), emptyList());
    }

    public static UtenlandsoppholdDto medlemskapUtlandetForrige12mnd() {
        return new UtenlandsoppholdDto(List.of(utenlandsopphold(LocalDate.now().minusYears(2), LocalDate.now())), emptyList());
    }

    private static UtenlandsoppholdPeriodeDto utenlandsopphold(LocalDate fom, LocalDate tom) {
        return new UtenlandsoppholdPeriodeDto(CountryCode.US.getAlpha3(), new ÅpenPeriodeDto(fom, tom));
    }
}
