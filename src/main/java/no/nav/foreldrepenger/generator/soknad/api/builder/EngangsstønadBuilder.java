package no.nav.foreldrepenger.generator.soknad.api.builder;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.generator.soknad.api.dto.BarnDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøkerDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.UtenlandsoppholdDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.VedleggDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.engangsstønad.EngangsstønadDto;


public class EngangsstønadBuilder {
    private LocalDate mottattdato;
    private BarnDto barn;
    private UtenlandsoppholdDto informasjonOmUtenlandsopphold;
    private SøkerDto søker;
    private List<VedleggDto> vedlegg;


    public EngangsstønadBuilder() {
    }

    public EngangsstønadBuilder medMottattdato(LocalDate mottattdato) {
        this.mottattdato = mottattdato;
        return this;
    }

    public EngangsstønadBuilder medBarn(BarnHelper barn) {
        this.barn = barn.barn();
        return this;
    }

    public EngangsstønadBuilder medMedlemsskap(UtenlandsoppholdDto informasjonOmUtenlandsopphold) {
        this.informasjonOmUtenlandsopphold = informasjonOmUtenlandsopphold;
        return this;
    }

    public EngangsstønadBuilder medSøker(SøkerDto søker) {
        this.søker = søker;
        return this;
    }

    public EngangsstønadBuilder medVedlegg(List<VedleggDto> vedlegg) {
        this.vedlegg = vedlegg;
        return this;
    }

    public EngangsstønadDto build() {
        if (mottattdato == null) mottattdato = LocalDate.now();
        return new EngangsstønadDto(
                mottattdato,
                barn,
                informasjonOmUtenlandsopphold,
                søker,
                vedlegg
        );
    }
}
