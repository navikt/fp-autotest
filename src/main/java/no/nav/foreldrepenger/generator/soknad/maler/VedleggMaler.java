package no.nav.foreldrepenger.generator.soknad.maler;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.felles.kodeverk.MorsAktivitet;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.UtsettelsesPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.UttaksPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.UttaksplanDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.Uttaksplanperiode;
import no.nav.foreldrepenger.kontrakter.fpsoknad.svangerskapspenger.TilretteleggingbehovDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.vedlegg.DokumentTypeId;
import no.nav.foreldrepenger.kontrakter.fpsoknad.vedlegg.Dokumenterer;
import no.nav.foreldrepenger.kontrakter.fpsoknad.vedlegg.InnsendingType;
import no.nav.foreldrepenger.kontrakter.fpsoknad.vedlegg.VedleggDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.vedlegg.ÅpenPeriodeDto;

public class VedleggMaler {

    private VedleggMaler() {
        // Skjuler konstruktør
    }

    public static VedleggDto dokumenterTermin(InnsendingType innsendingType) {
        var dokumenterer = new Dokumenterer(Dokumenterer.DokumentererType.BARN, null, null);
        return new VedleggDto(null, DokumentTypeId.I000141, innsendingType, null, dokumenterer);
    }

    public static VedleggDto dokumenterUttak(UttaksplanDto uttaksplan, MorsAktivitet morsAktivitet, InnsendingType innsendingType) {
        return dokumenterUttak(uttaksplan.uttaksperioder(), morsAktivitet, innsendingType);
    }

    public static VedleggDto dokumenterUttak(List<Uttaksplanperiode> uttaksplan, MorsAktivitet morsAktivitet, InnsendingType innsendingType) {
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
        var dokumenterer = new Dokumenterer(Dokumenterer.DokumentererType.UTTAK, null, uttaksperiodeSomSkalDokumenteres);
        return new VedleggDto(UUID.randomUUID(), dokumentTypeFraAktivitet, innsendingType, null, dokumenterer);
    }


    public static VedleggDto dokumenterUttak(UttaksPeriodeDto uttaksperiode, InnsendingType innsendingType) {
        if (uttaksperiode.morsAktivitetIPerioden() == null) {
            throw new IllegalArgumentException("UTVIKLERFEIL: Uttaksperiode må ha noe å dokumentere. Morsk aktivitet er null.");
        }

        var dokumentTypeFraAktivitet = dokumentypeFraAktivitet(uttaksperiode.morsAktivitetIPerioden());
        var åpenPeriode = new ÅpenPeriodeDto(uttaksperiode.fom(), uttaksperiode.tom());
        var dokumenterer = new Dokumenterer(Dokumenterer.DokumentererType.UTTAK, null, List.of(åpenPeriode));
        return new VedleggDto(UUID.randomUUID(), dokumentTypeFraAktivitet, innsendingType, null, dokumenterer);
    }

    public static VedleggDto dokumenterTilrettelegging(TilretteleggingbehovDto tilretteleggingbehovDto, InnsendingType innsendingType) {
        var dokumenterer = new Dokumenterer(Dokumenterer.DokumentererType.TILRETTELEGGING, tilretteleggingbehovDto.arbeidsforhold(), null);
        return new VedleggDto(UUID.randomUUID(), DokumentTypeId.I000109, innsendingType, null, dokumenterer);
    }

    private static DokumentTypeId dokumentypeFraAktivitet(MorsAktivitet morsAktivitet) {
        return switch (morsAktivitet) {
            case ARBEID -> DokumentTypeId.I000132;
            case UTDANNING -> DokumentTypeId.I000038;
            case KVALPROG -> DokumentTypeId.I000051;
            case INTROPROG -> DokumentTypeId.I000112;
            case INNLAGT -> DokumentTypeId.I000120;
            case ARBEID_OG_UTDANNING -> DokumentTypeId.I000130;
            case TRENGER_HJELP, UFØRE, IKKE_OPPGITT -> throw new IllegalArgumentException("Ugyldig aktivitet: " + morsAktivitet);
        };
    }
}
