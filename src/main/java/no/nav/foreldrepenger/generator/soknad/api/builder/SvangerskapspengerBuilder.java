package no.nav.foreldrepenger.generator.soknad.api.builder;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.generator.soknad.api.dto.BarnDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøkerDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.UtenlandsoppholdDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.VedleggDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.svangerskapspenger.SvangerskapspengesøknadDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.svangerskapspenger.TilretteleggingDto;

public class SvangerskapspengerBuilder {
    private LocalDate mottattdato;
    private List<TilretteleggingDto> tilrettelegging;
    private BarnDto barn;
    private UtenlandsoppholdDto informasjonOmUtenlandsopphold;
    private SøkerDto søker;
    private List<VedleggDto> vedlegg;

    public SvangerskapspengerBuilder(List<TilretteleggingDto> tilrettelegging) {
        this.tilrettelegging = tilrettelegging;
    }

    public SvangerskapspengerBuilder medMottattdato(LocalDate mottattdato) {
        this.mottattdato = mottattdato;
        return this;
    }

    public SvangerskapspengerBuilder medTilrettelegging(List<TilretteleggingDto> tilrettelegging) {
        this.tilrettelegging = tilrettelegging;
        return this;
    }

    public SvangerskapspengerBuilder medBarn(BarnHelper barn) {
        this.barn = barn.barn();
        return this;
    }


    public SvangerskapspengerBuilder medMedlemsskap(UtenlandsoppholdDto informasjonOmUtenlandsopphold) {
        this.informasjonOmUtenlandsopphold = informasjonOmUtenlandsopphold;
        return this;
    }

    public SvangerskapspengerBuilder medSøker(SøkerDto søker) {
        this.søker = søker;
        return this;
    }

    public SvangerskapspengerBuilder medVedlegg(List<VedleggDto> vedlegg) {
        this.vedlegg = vedlegg;
        return this;
    }

    public SvangerskapspengesøknadDto build() {
        if (mottattdato == null) mottattdato = LocalDate.now();
        return new SvangerskapspengesøknadDto(
                mottattdato,
                tilrettelegging,
                barn,
                informasjonOmUtenlandsopphold,
                søker,
                vedlegg
        );
    }
}
