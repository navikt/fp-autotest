package no.nav.foreldrepenger.generator.soknad.maler;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.common.domain.felles.DokumentType;
import no.nav.foreldrepenger.common.innsyn.MorsAktivitet;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.UttaksplanPeriodeDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.ÅpenPeriodeDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.VedleggDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.VedleggInnsendingType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.svangerskapspenger.TilretteleggingbehovDto;

public class VedleggMaler {

    private VedleggMaler() {
        // Skjuler konstruktør
    }

    public static VedleggDto dokumenterTermin(VedleggInnsendingType innsendingType) {
        var dokumenterer = new VedleggDto.Dokumenterer(VedleggDto.Dokumenterer.Type.BARN, null, null);
        return new VedleggDto(UUID.randomUUID(), DokumentType.I000141, innsendingType, null, dokumenterer);
    }

    public static VedleggDto dokumenterUttak(List<UttaksplanPeriodeDto> uttaksplan, MorsAktivitet morsAktivitet, VedleggInnsendingType innsendingType) {
        var uttaksperiodeSomSkalDokumenteres = uttaksplan.stream()
                .filter(uttaksperiode -> morsAktivitet.name().equals(uttaksperiode.morsAktivitetIPerioden()))
                .map(uttaksperiode -> uttaksperiode.tidsperiode())
                .toList();
        if (uttaksperiodeSomSkalDokumenteres.isEmpty()) {
            throw new IllegalArgumentException("UTVIKLERFEIL: Uttaksplan har ingen perioder med morsAktivitet: " + morsAktivitet);
        }
        var dokumentTypeFraAktivitet = dokumentypeFraAktivitet(morsAktivitet);
        var dokumenterer = new VedleggDto.Dokumenterer(VedleggDto.Dokumenterer.Type.UTTAK, null, uttaksperiodeSomSkalDokumenteres);
        return new VedleggDto(UUID.randomUUID(), dokumentTypeFraAktivitet, innsendingType, null, dokumenterer);
    }


    public static VedleggDto dokumenterUttak(UttaksplanPeriodeDto uttaksperiode, VedleggInnsendingType innsendingType) {
        if (uttaksperiode.morsAktivitetIPerioden() == null) {
            throw new IllegalArgumentException("UTVIKLERFEIL: Uttaksperiode må ha noe å dokumentere. Morsk aktivitet er null.");
        }

        var dokumentTypeFraAktivitet = dokumentypeFraAktivitet(MorsAktivitet.valueOf(uttaksperiode.morsAktivitetIPerioden()));
        var åpenPeriode = new ÅpenPeriodeDto(uttaksperiode.tidsperiode().fom(), uttaksperiode.tidsperiode().tom());
        var dokumenterer = new VedleggDto.Dokumenterer(VedleggDto.Dokumenterer.Type.UTTAK, null, List.of(åpenPeriode));
        return new VedleggDto(UUID.randomUUID(), dokumentTypeFraAktivitet, innsendingType, null, dokumenterer);
    }

    public static VedleggDto dokumenterTilrettelegging(TilretteleggingbehovDto tilretteleggingbehovDto, VedleggInnsendingType innsendingType) {
        var dokumenterer = new VedleggDto.Dokumenterer(VedleggDto.Dokumenterer.Type.TILRETTELEGGING, tilretteleggingbehovDto.arbeidsforhold(), null);
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
