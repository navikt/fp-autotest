package no.nav.foreldrepenger.generator.soknad.builder;

import java.util.List;

import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Fordeling;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;

public class FordelingBuilder {
    private Boolean erAnnenForelderInformert;
    private List<LukketPeriodeMedVedlegg> perioder;
    private Boolean ønskerJustertUttakVedFødsel;

    FordelingBuilder() {
    }

    public static FordelingBuilder builder() {
        return new FordelingBuilder();
    }

    public FordelingBuilder erAnnenForelderInformert(final Boolean erAnnenForelderInformert) {
        this.erAnnenForelderInformert = erAnnenForelderInformert;
        return this;
    }

    public FordelingBuilder perioder(final List<LukketPeriodeMedVedlegg> perioder) {
        this.perioder = perioder;
        return this;
    }

    public FordelingBuilder ønskerJustertUttakVedFødsel(final Boolean ønskerJustertUttakVedFødsel) {
        this.ønskerJustertUttakVedFødsel = ønskerJustertUttakVedFødsel;
        return this;
    }

    public Fordeling build() {
        return new Fordeling(this.erAnnenForelderInformert, this.perioder, this.ønskerJustertUttakVedFødsel);
    }
}
