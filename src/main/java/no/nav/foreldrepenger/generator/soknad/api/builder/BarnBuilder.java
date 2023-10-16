package no.nav.foreldrepenger.generator.soknad.api.builder;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.generator.soknad.api.dto.BarnDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.MutableVedleggReferanseDto;

public class BarnBuilder {
    List<LocalDate> fødselsdatoer;
    int antallBarn;
    LocalDate termindato;
    LocalDate terminbekreftelseDato;
    LocalDate adopsjonsdato;
    LocalDate ankomstdato;
    boolean adopsjonAvEktefellesBarn;
    boolean søkerAdopsjonAlene;
    LocalDate foreldreansvarsdato;
    List<MutableVedleggReferanseDto> terminbekreftelse;
    List<MutableVedleggReferanseDto> adopsjonsvedtak;
    List<MutableVedleggReferanseDto> omsorgsovertakelse;
    List<MutableVedleggReferanseDto> dokumentasjonAvAleneomsorg;

    public BarnBuilder() {
    }

    public BarnBuilder medFødselsdatoer(List<LocalDate> fødselsdatoer) {
        this.fødselsdatoer = fødselsdatoer;
        return this;
    }

    public BarnBuilder medAntallBarn(int antallBarn) {
        this.antallBarn = antallBarn;
        return this;
    }

    public BarnBuilder medTermindato(LocalDate termindato) {
        this.termindato = termindato;
        return this;
    }

    public BarnBuilder medTerminbekreftelseDato(LocalDate terminbekreftelseDato) {
        this.terminbekreftelseDato = terminbekreftelseDato;
        return this;
    }

    public BarnBuilder medAdopsjonsdato(LocalDate adopsjonsdato) {
        this.adopsjonsdato = adopsjonsdato;
        return this;
    }

    public BarnBuilder medAnkomstdato(LocalDate ankomstdato) {
        this.ankomstdato = ankomstdato;
        return this;
    }

    public BarnBuilder medAdopsjonAvEktefellesBarn(boolean adopsjonAvEktefellesBarn) {
        this.adopsjonAvEktefellesBarn = adopsjonAvEktefellesBarn;
        return this;
    }

    public BarnBuilder medSøkerAdopsjonAlene(boolean søkerAdopsjonAlene) {
        this.søkerAdopsjonAlene = søkerAdopsjonAlene;
        return this;
    }

    public BarnBuilder medForeldreansvarsdato(LocalDate foreldreansvarsdato) {
        this.foreldreansvarsdato = foreldreansvarsdato;
        return this;
    }

    public BarnBuilder medTerminbekreftelse(List<MutableVedleggReferanseDto> terminbekreftelse) {
        this.terminbekreftelse = terminbekreftelse;
        return this;
    }

    public BarnBuilder medAdopsjonsvedtak(List<MutableVedleggReferanseDto> adopsjonsvedtak) {
        this.adopsjonsvedtak = adopsjonsvedtak;
        return this;
    }

    public BarnBuilder medOmsorgsovertakelse(List<MutableVedleggReferanseDto> omsorgsovertakelse) {
        this.omsorgsovertakelse = omsorgsovertakelse;
        return this;
    }

    public BarnBuilder medDokumentasjonAvAleneomsorg(List<MutableVedleggReferanseDto> dokumentasjonAvAleneomsorg) {
        this.dokumentasjonAvAleneomsorg = dokumentasjonAvAleneomsorg;
        return this;
    }

    public BarnDto build() {
        return new BarnDto(
                fødselsdatoer,
                antallBarn,
                termindato,
                terminbekreftelseDato,
                adopsjonsdato,
                ankomstdato,
                adopsjonAvEktefellesBarn,
                søkerAdopsjonAlene,
                foreldreansvarsdato,
                terminbekreftelse,
                adopsjonsvedtak,
                omsorgsovertakelse,
                dokumentasjonAvAleneomsorg);
    }
}
