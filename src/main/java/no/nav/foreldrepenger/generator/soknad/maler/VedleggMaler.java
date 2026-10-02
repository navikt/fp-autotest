package no.nav.foreldrepenger.generator.soknad.maler;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.felles.kodeverk.MorsAktivitet;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UttakPeriodeDto;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.UttaksplanDto;
import no.nav.foreldrepenger.soknad.kontrakt.svangerskapspenger.TilretteleggingbehovDto;
import no.nav.foreldrepenger.soknad.kontrakt.vedlegg.DokumentTypeId;
import no.nav.foreldrepenger.soknad.kontrakt.vedlegg.Dokumenterer;
import no.nav.foreldrepenger.soknad.kontrakt.vedlegg.InnsendingType;
import no.nav.foreldrepenger.soknad.kontrakt.vedlegg.VedleggDto;
import no.nav.foreldrepenger.soknad.kontrakt.vedlegg.ÅpenPeriodeDto;

public class VedleggMaler {

    private VedleggMaler() {
        // Skjuler konstruktør
    }

    public static VedleggDto dokumenterTermin(InnsendingType innsendingType) {
        var dokumenterer = new Dokumenterer(Dokumenterer.DokumentererType.BARN, null, null);
        return new VedleggDto(null, DokumentTypeId.I000141, innsendingType, null, dokumenterer);
    }

    public static VedleggDto dokumenterMorsAktivitet(UttaksplanDto uttaksplan, MorsAktivitet morsAktivitet, InnsendingType innsendingType) {
        return dokumenterMorsAktivitet(uttaksplan.perioder(), morsAktivitet, innsendingType);
    }

    public static VedleggDto dokumenterMorsAktivitet(List<UttakPeriodeDto> uttaksplan, MorsAktivitet morsAktivitet, InnsendingType innsendingType) {
        var uttaksperiodeSomSkalDokumenteres = uttaksplan.stream()
                .filter(periode ->
                        periode.søker() != null && morsAktivitet.equals(periode.søker().morsAktivitet()))
                .map(periode -> new ÅpenPeriodeDto(periode.fom(), periode.tom()))
                .toList();
        if (uttaksperiodeSomSkalDokumenteres.isEmpty()) {
            throw new IllegalArgumentException("UTVIKLERFEIL: Uttaksplan har ingen perioder med morsAktivitet: " + morsAktivitet);
        }
        var dokumentTypeFraAktivitet = dokumentypeFraAktivitet(morsAktivitet);
        var dokumenterer = new Dokumenterer(Dokumenterer.DokumentererType.UTTAK, null, uttaksperiodeSomSkalDokumenteres);
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
