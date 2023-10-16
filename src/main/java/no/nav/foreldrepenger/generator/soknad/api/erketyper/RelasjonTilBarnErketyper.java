package no.nav.foreldrepenger.generator.soknad.api.erketyper;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.generator.soknad.api.builder.BarnBuilder;
import no.nav.foreldrepenger.generator.soknad.api.builder.BarnHelper;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.Situasjon;

public final class RelasjonTilBarnErketyper {



    private RelasjonTilBarnErketyper() {
    }

    public static BarnHelper fødsel(int antallBarn, LocalDate fødselsdato) {
        return new BarnHelper(new BarnBuilder()
                .medFødselsdatoer(List.of(fødselsdato))
                .medAntallBarn(antallBarn)
                .medTermindato(fødselsdato)
                .build(),
                Situasjon.FØDSEL);
    }

    public static BarnHelper fødselMedTermin(int antallBarn, LocalDate fødselsdato, LocalDate termindato) {
        return new BarnHelper(new BarnBuilder()
                .medFødselsdatoer(List.of(fødselsdato))
                .medAntallBarn(antallBarn)
                .medTermindato(termindato)
                .build(),
                Situasjon.FØDSEL);
    }

    public static BarnHelper termin(int antallBarn, LocalDate termindato) {
        return new BarnHelper(new BarnBuilder()
                .medAntallBarn(antallBarn)
                .medTermindato(termindato)
                .medTerminbekreftelseDato(termindato.minusMonths(1))
                .build(),
                Situasjon.FØDSEL);
    }

    public static BarnHelper adopsjon(LocalDate omsorgsovertakelsesdato, Boolean ektefellesBarn) {
        return new BarnHelper(new BarnBuilder()
                .medFødselsdatoer(List.of(LocalDate.now().minusYears(10)))
                .medAntallBarn(1)
                .medAdopsjonsdato(omsorgsovertakelsesdato)
                .medAnkomstdato(omsorgsovertakelsesdato)
                .medAdopsjonAvEktefellesBarn(ektefellesBarn)
                .build(),
                Situasjon.ADOPSJON);
    }

    public static BarnHelper omsorgsovertakelse(LocalDate omsorgsovertakelsedato) {
        return new BarnHelper(new BarnBuilder()
                .medFødselsdatoer(List.of(LocalDate.now().minusMonths(6)))
                .medAntallBarn(1)
                .medAnkomstdato(omsorgsovertakelsedato)
                .medForeldreansvarsdato(omsorgsovertakelsedato)
                .build(),
                Situasjon.OMSORGSOVERTAKELSE);
    }

}
