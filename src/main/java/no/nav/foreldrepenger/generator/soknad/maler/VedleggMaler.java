package no.nav.foreldrepenger.generator.soknad.maler;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.common.domain.felles.DokumentType;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.VedleggDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.VedleggInnsendingType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.uttaksplan.UtsettelsesPeriodeDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.uttaksplan.UttaksPeriodeDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.uttaksplan.UttaksplanDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.uttaksplan.Uttaksplanperiode;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.svangerskapspenger.TilretteleggingbehovDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.ÅpenPeriodeDto;

public class VedleggMaler {

    private VedleggMaler() {
        // Skjuler konstruktør
    }

    public static VedleggDto dokumenterTermin(VedleggInnsendingType innsendingType) {
        var dokumenterer = new VedleggDto.Dokumenterer(VedleggDto.Dokumenterer.DokumentererType.BARN, null, null);
        return new VedleggDto(UUID.randomUUID(), DokumentType.I000141, innsendingType, null, dokumenterer);
    }

    public static VedleggDto dokumenterUttak(UttaksplanDto uttaksplan, MorsAktivitet morsAktivitet, VedleggInnsendingType innsendingType) {
        return dokumenterUttak(uttaksplan.uttaksperioder(), morsAktivitet, innsendingType);
    }

    public static VedleggDto dokumenterUttak(List<Uttaksplanperiode> uttaksplan, MorsAktivitet morsAktivitet, VedleggInnsendingType innsendingType) {
        var uttaksperiodeSomSkalDokumenteres = uttaksplan.stream()
                .filter(periode ->
                        periode instanceof UttaksPeriodeDto uttak && morsAktivitet.equals(uttak.morsAktivitetIPerioden()) ||
                        periode instanceof UtsettelsesPeriodeDto utsettelse && morsAktivitet.equals(utsettelse.morsAktivitetIPerioden()))
                .map(uttaksperiode -> new ÅpenPeriodeDto(uttaksperiode.fom(), uttaksperiode.tom()))
                .toList();
        if (uttaksperiodeSomSkalDokumenteres.isEmpty()) {
            throw new IllegalArgumentException("UTVIKLERFEIL: Uttaksplan har ingen perioder med morsAktivitet: " + morsAktivitet);
        }
        var dokumentTypeFraAktivitet = dokumentypeFraAktivitet(morsAktivitet);
        var dokumenterer = new VedleggDto.Dokumenterer(VedleggDto.Dokumenterer.DokumentererType.UTTAK, null, uttaksperiodeSomSkalDokumenteres);
        return new VedleggDto(UUID.randomUUID(), dokumentTypeFraAktivitet, innsendingType, null, dokumenterer);
    }


    public static VedleggDto dokumenterUttak(UttaksPeriodeDto uttaksperiode, VedleggInnsendingType innsendingType) {
        if (uttaksperiode.morsAktivitetIPerioden() == null) {
            throw new IllegalArgumentException("UTVIKLERFEIL: Uttaksperiode må ha noe å dokumentere. Morsk aktivitet er null.");
        }

        var dokumentTypeFraAktivitet = dokumentypeFraAktivitet(uttaksperiode.morsAktivitetIPerioden());
        var åpenPeriode = new ÅpenPeriodeDto(uttaksperiode.fom(), uttaksperiode.tom());
        var dokumenterer = new VedleggDto.Dokumenterer(VedleggDto.Dokumenterer.DokumentererType.UTTAK, null, List.of(åpenPeriode));
        return new VedleggDto(UUID.randomUUID(), dokumentTypeFraAktivitet, innsendingType, null, dokumenterer);
    }

    public static VedleggDto dokumenterTilrettelegging(TilretteleggingbehovDto tilretteleggingbehovDto, VedleggInnsendingType innsendingType) {
        var dokumenterer = new VedleggDto.Dokumenterer(VedleggDto.Dokumenterer.DokumentererType.TILRETTELEGGING, tilretteleggingbehovDto.arbeidsforhold(), null);
        return new VedleggDto(UUID.randomUUID(), DokumentType.I000109, innsendingType, null, dokumenterer);
    }

    private static DokumentType dokumentypeFraAktivitet(MorsAktivitet morsAktivitet) {
        return switch (morsAktivitet) {
            case ARBEID -> DokumentType.I000132;
            case UTDANNING -> DokumentType.I000038;
            case KVALPROG -> DokumentType.I000051;
            case INTROPROG -> DokumentType.I000112;
            case INNLAGT -> DokumentType.I000120;
            case ARBEID_OG_UTDANNING -> DokumentType.I000130;
            case TRENGER_HJELP, UFØRE, IKKE_OPPGITT -> throw new IllegalArgumentException("Ugyldig aktivitet: " + morsAktivitet);
        };
    }
}
