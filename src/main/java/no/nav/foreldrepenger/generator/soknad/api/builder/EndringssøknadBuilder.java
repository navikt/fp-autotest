package no.nav.foreldrepenger.generator.soknad.api.builder;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.generator.soknad.api.dto.BarnDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøkerDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.VedleggDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.endringssøknad.EndringssøknadDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.endringssøknad.EndringssøknadForeldrepengerDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.AnnenforelderDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.Situasjon;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.UttaksplanPeriodeDto;

public class EndringssøknadBuilder {
    private LocalDate mottattdato;
    private Situasjon situasjon;
    private Saksnummer saksnummer;
    private SøkerDto søker;
    private BarnDto barn;
    private AnnenforelderDto annenforelder;
    private String tilleggsopplysninger;
    private Boolean ønskerJustertUttakVedFødsel;
    private List<UttaksplanPeriodeDto> uttaksplan;
    private List<VedleggDto> vedlegg;

    public EndringssøknadBuilder(Saksnummer saksnummer) {
        this.saksnummer = saksnummer;
    }

    public EndringssøknadBuilder medMottattdato(LocalDate mottattdato) {
        this.mottattdato = mottattdato;
        return this;
    }

    public EndringssøknadBuilder medSøker(SøkerDto søker) {
        this.søker = søker;
        return this;
    }

    public EndringssøknadBuilder medBarn(BarnHelper barn) {
        this.situasjon = barn.situasjon();
        this.barn = barn.barn();
        return this;
    }

    public EndringssøknadBuilder medAnnenforelder(AnnenforelderDto annenforelder) {
        this.annenforelder = annenforelder;
        return this;
    }

    public EndringssøknadBuilder medTilleggsopplysninger(String tilleggsopplysninger) {
        this.tilleggsopplysninger = tilleggsopplysninger;
        return this;
    }

    public EndringssøknadBuilder medØnskerJustertUttakVedFødsel(Boolean ønskerJustertUttakVedFødsel) {
        this.ønskerJustertUttakVedFødsel = ønskerJustertUttakVedFødsel;
        return this;
    }

    public EndringssøknadBuilder medFordeling(List<UttaksplanPeriodeDto> uttaksplan) {
        this.uttaksplan = uttaksplan;
        return this;
    }

    public EndringssøknadBuilder medVedlegg(List<VedleggDto> vedlegg) {
        this.vedlegg = vedlegg;
        return this;
    }

    public EndringssøknadDto build() {
        if (mottattdato == null) mottattdato = LocalDate.now();
        return new EndringssøknadForeldrepengerDto(
                mottattdato,
                situasjon,
                saksnummer,
                søker,
                barn,
                annenforelder,
                tilleggsopplysninger,
                ønskerJustertUttakVedFødsel,
                uttaksplan,
                vedlegg
        );
    }
}
