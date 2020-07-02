package no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Kodeverk {
    public KodeListe ArbeidType;
    public KodeListe OpptjeningAktivitetType;
    public Map<String, KodeListe> Avslagsårsak;
    public KodeListe BehandlingType;
    public KodeListe BehandlingÅrsakType;
    public KodeListe BehandlingResultatType;
    public KodeListe FagsakStatus;
    public KodeListe FagsakYtelseType;
    public KodeListe FagsakÅrsakType;
    public KodeListe ForeldreType;
    public KodeListe InnsynResultatType;
    public KodeListe InnvilgetÅrsak;
    public KodeListe KlageAvvistÅrsak;
    public KodeListe KlageMedholdÅrsak;
    public KodeListe Landkoder;
    public KodeListe MedlemskapManuellVurderingType;
    public KodeListe MorsAktivitet;
    public KodeListe NæringsvirksomhetType;
    public KodeListe OmsorgsovertakelseVilkårType;
    public KodeListe OverføringÅrsak;
    public KodeListe PersonstatusType;
    public KodeListe RelatertYtelseTilstand;
    public KodeListe RelatertYtelseType;
    public KodeListe SøknadtypeTillegg;
    public KodeListe TypeFiske;
    public KodeListe UtsettelseÅrsak;
    public KodeListe UttakPeriodeType;
    public KodeListe Venteårsak;
    public KodeListe VergeType;
    public KodeListe UttakPeriodeVurderingType;
    public KodeListe IkkeOppfyltÅrsak;
    public KodeListe SkatteOgAvgiftsregelType;
    public KodeListe Inntektskategori;
    public KodeListe VurderÅrsak;
    public KodeListe StønadskontoType;
    public KodeListe OppholdÅrsak;
    public KodeListe UttakUtsettelseType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kodeverk kodeverk = (Kodeverk) o;
        return Objects.equals(ArbeidType, kodeverk.ArbeidType) &&
                Objects.equals(OpptjeningAktivitetType, kodeverk.OpptjeningAktivitetType) &&
                Objects.equals(Avslagsårsak, kodeverk.Avslagsårsak) &&
                Objects.equals(BehandlingType, kodeverk.BehandlingType) &&
                Objects.equals(BehandlingÅrsakType, kodeverk.BehandlingÅrsakType) &&
                Objects.equals(BehandlingResultatType, kodeverk.BehandlingResultatType) &&
                Objects.equals(FagsakStatus, kodeverk.FagsakStatus) &&
                Objects.equals(FagsakYtelseType, kodeverk.FagsakYtelseType) &&
                Objects.equals(FagsakÅrsakType, kodeverk.FagsakÅrsakType) &&
                Objects.equals(ForeldreType, kodeverk.ForeldreType) &&
                Objects.equals(InnsynResultatType, kodeverk.InnsynResultatType) &&
                Objects.equals(InnvilgetÅrsak, kodeverk.InnvilgetÅrsak) &&
                Objects.equals(KlageAvvistÅrsak, kodeverk.KlageAvvistÅrsak) &&
                Objects.equals(KlageMedholdÅrsak, kodeverk.KlageMedholdÅrsak) &&
                Objects.equals(Landkoder, kodeverk.Landkoder) &&
                Objects.equals(MedlemskapManuellVurderingType, kodeverk.MedlemskapManuellVurderingType) &&
                Objects.equals(MorsAktivitet, kodeverk.MorsAktivitet) &&
                Objects.equals(NæringsvirksomhetType, kodeverk.NæringsvirksomhetType) &&
                Objects.equals(OmsorgsovertakelseVilkårType, kodeverk.OmsorgsovertakelseVilkårType) &&
                Objects.equals(OverføringÅrsak, kodeverk.OverføringÅrsak) &&
                Objects.equals(PersonstatusType, kodeverk.PersonstatusType) &&
                Objects.equals(RelatertYtelseTilstand, kodeverk.RelatertYtelseTilstand) &&
                Objects.equals(RelatertYtelseType, kodeverk.RelatertYtelseType) &&
                Objects.equals(SøknadtypeTillegg, kodeverk.SøknadtypeTillegg) &&
                Objects.equals(TypeFiske, kodeverk.TypeFiske) &&
                Objects.equals(UtsettelseÅrsak, kodeverk.UtsettelseÅrsak) &&
                Objects.equals(UttakPeriodeType, kodeverk.UttakPeriodeType) &&
                Objects.equals(Venteårsak, kodeverk.Venteårsak) &&
                Objects.equals(VergeType, kodeverk.VergeType) &&
                Objects.equals(UttakPeriodeVurderingType, kodeverk.UttakPeriodeVurderingType) &&
                Objects.equals(IkkeOppfyltÅrsak, kodeverk.IkkeOppfyltÅrsak) &&
                Objects.equals(SkatteOgAvgiftsregelType, kodeverk.SkatteOgAvgiftsregelType) &&
                Objects.equals(Inntektskategori, kodeverk.Inntektskategori) &&
                Objects.equals(VurderÅrsak, kodeverk.VurderÅrsak);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ArbeidType, OpptjeningAktivitetType, Avslagsårsak, BehandlingType, BehandlingÅrsakType, BehandlingResultatType, FagsakStatus, FagsakYtelseType, FagsakÅrsakType, ForeldreType, InnsynResultatType, InnvilgetÅrsak, KlageAvvistÅrsak, KlageMedholdÅrsak, Landkoder, MedlemskapManuellVurderingType, MorsAktivitet, NæringsvirksomhetType, OmsorgsovertakelseVilkårType, OverføringÅrsak, PersonstatusType, RelatertYtelseTilstand, RelatertYtelseType, SøknadtypeTillegg, TypeFiske, UtsettelseÅrsak, UttakPeriodeType, Venteårsak, VergeType, UttakPeriodeVurderingType, IkkeOppfyltÅrsak, SkatteOgAvgiftsregelType, Inntektskategori, VurderÅrsak);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KodeListe extends ArrayList<Kode> {

        public Kode getKode(String kodeverdi) {
            for (Kode kode : this) {
                if (kode.kode.equals(kodeverdi)) {
                    return kode;
                }
            }
            throw new IllegalArgumentException("Ukjent kode: " + kodeverdi);
        }
    }
}
