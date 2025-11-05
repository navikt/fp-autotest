package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakresultatUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.util.localdate.Virkedager;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType;

public class FastsettUttaksperioderManueltBekreftelse extends AksjonspunktBekreftelse {

    protected List<UttakResultatPeriode> perioder = new ArrayList<>();

    @Override
    public String aksjonspunktKode() {
        return AksjonspunktKoder.FASTSETT_UTTAKPERIODER;
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        perioder = behandling.hentUttaksperioder();
        innvilgManuellePerioder();
    }

    public List<UttakResultatPeriode> getPerioder() {
        return perioder;
    }

    public FastsettUttaksperioderManueltBekreftelse innvilgManuellePerioder() {
        for (UttakResultatPeriode uttaksperiode : perioder) {
            if ((uttaksperiode.getManuellBehandlingÅrsak() != null)
                    && !uttaksperiode.getManuellBehandlingÅrsak().equals("-")) {
                innvilgPeriode(uttaksperiode);
            }
        }
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse avslåManuellePerioder() {
        for (UttakResultatPeriode uttaksperiode : perioder) {
            if ((uttaksperiode.getManuellBehandlingÅrsak() != null)
                    && !uttaksperiode.getManuellBehandlingÅrsak().equals("-")) {
                avslåPeriode(uttaksperiode);
            }
        }
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse avslåManuellePerioderMedPeriodeResultatÅrsak(PeriodeResultatÅrsak periodeResultatÅrsak) {
        for (UttakResultatPeriode uttakPeriode : perioder) {
            if (!uttakPeriode.getManuellBehandlingÅrsak().equals("-")) {
                avslåPeriode(uttakPeriode, periodeResultatÅrsak, false);
            }
        }
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse avslåPeriode(LocalDate fra, LocalDate til,
                                                                 PeriodeResultatÅrsak periodeResultatÅrsak) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        avslåPeriode(periode, periodeResultatÅrsak, false);
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse avslåPeriode(LocalDate fra, LocalDate til,
                                                                 PeriodeResultatÅrsak periodeResultatÅrsak,
                                                                 boolean trekkDager) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        avslåPeriode(periode, periodeResultatÅrsak, trekkDager);
        return this;
    }

    public List<UttakResultatPeriode> splitPeriode(LocalDate fom, LocalDate tom, LocalDate tomAvFørstePeriodeEtterSplitting) {
        UttakResultatPeriode periode1 = finnPeriode(fom, tom);

        periode1.setTom(tomAvFørstePeriodeEtterSplitting);
        for (UttakResultatPeriodeAktivitet aktivitet : periode1.getAktiviteter()) {
            int trekkdager = Virkedager.beregnAntallVirkedager(fom, tomAvFørstePeriodeEtterSplitting);
            aktivitet.setTrekkdagerDesimaler(BigDecimal.valueOf(trekkdager));
        }

        UttakResultatPeriode periode2 = deepCopy(periode1);
        periode2.setFom(tomAvFørstePeriodeEtterSplitting.plusDays(1));
        periode2.setTom(tom);
        for (UttakResultatPeriodeAktivitet aktivitet : periode2.getAktiviteter()) {
            int trekkdager = Virkedager.beregnAntallVirkedager(tomAvFørstePeriodeEtterSplitting.plusDays(1), tom);
            aktivitet.setTrekkdagerDesimaler(BigDecimal.valueOf(trekkdager));
        }

        int indeksAvPeriode = perioder.indexOf(periode1);
        perioder.add(indeksAvPeriode + 1, periode2);

        return List.of(periode1, periode2);
    }

    // PRIVATE METODER //

    public void innvilgPeriode(UttakResultatPeriode periode) {
        innvilgPeriode(periode, PeriodeResultatÅrsak.FELLESPERIODE_ELLER_FORELDREPENGER);
    }

    public void innvilgPeriode(UttakResultatPeriode periode, PeriodeResultatÅrsak periodeResultatÅrsak, KontoType stønadskonto) {
        periode.setPeriodeResultatType(PeriodeResultatType.INNVILGET);
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);
        periode.setBegrunnelse("Perioden er innvilget av Autotest.");
        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            aktivitet.setKontotype(stønadskonto);
            innvilgAktivitetForPeriode(periode, aktivitet);
        }
    }

    public void innvilgPeriode(UttakResultatPeriode periode, PeriodeResultatÅrsak periodeResultatÅrsak) {
        periode.setPeriodeResultatType(PeriodeResultatType.INNVILGET);
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);
        periode.setBegrunnelse("Perioden er innvilget av Autotest.");
        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            innvilgAktivitetForPeriode(periode, aktivitet);
        }
    }

    private void innvilgAktivitetForPeriode(UttakResultatPeriode periode, UttakResultatPeriodeAktivitet aktivitet) {
        Boolean samtidigUttak = periode.getSamtidigUttak();
        BigDecimal ordinæreTrekkdagerVedFulltUttak = BigDecimal.valueOf(
                Virkedager.beregnAntallVirkedager(periode.getFom(), periode.getTom()));
        if (Boolean.TRUE.equals(samtidigUttak)) {
            if (periode.getSamtidigUttaksprosent() == null) {
                throw new NullPointerException("Samtidig uttaksprosent er ikke satt!");
            }
            if (aktivitet.getProsentArbeid().doubleValue() > periode.getSamtidigUttaksprosent().doubleValue()) {
                BigDecimal andelArbeid = aktivitet.getProsentArbeid();
                aktivitet.setUtbetalingsgrad(BigDecimal.valueOf(100).subtract(andelArbeid));
                setTrekkdager(aktivitet, ordinæreTrekkdagerVedFulltUttak, aktivitet.getUtbetalingsgrad());
            } else {
                BigDecimal samtidigUttaksprosent = periode.getSamtidigUttaksprosent();
                aktivitet.setUtbetalingsgrad(samtidigUttaksprosent);
                setTrekkdager(aktivitet, ordinæreTrekkdagerVedFulltUttak, aktivitet.getUtbetalingsgrad());
            }
        } else {
            BigDecimal andelArbeid = aktivitet.getProsentArbeid();
            aktivitet.setUtbetalingsgrad(BigDecimal.valueOf(100).subtract(andelArbeid));
            setTrekkdager(aktivitet, ordinæreTrekkdagerVedFulltUttak, aktivitet.getUtbetalingsgrad());
        }
        // Utsettelses perioder trenger ikke trekkdager. set dem til 0
        if (!UttakresultatUtsettelseÅrsak.UDEFINERT.equals(periode.getUtsettelseType())) {
            aktivitet.setTrekkdagerDesimaler(BigDecimal.ZERO);
        }
    }

    public void avslåPeriode(UttakResultatPeriode periode) {
        avslåPeriode(periode, PeriodeResultatÅrsak.IKKE_STØNADSDAGER_IGJEN, false);
    }

    public void avslåPeriode(UttakResultatPeriode periode, PeriodeResultatÅrsak periodeResultatÅrsak, boolean trekkDager) {
        periode.setPeriodeResultatType(PeriodeResultatType.AVSLÅTT);
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);
        periode.setBegrunnelse("Perioden er avslått av Autotest.");
        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            avslåAktivitet(aktivitet, periode, trekkDager);
        }
    }

    private void avslåAktivitet(UttakResultatPeriodeAktivitet aktivitet,
                                UttakResultatPeriode periode,
                                boolean trekkDager) {
        if (trekkDager) {
            var trekkdagerDesimaler = BigDecimal.valueOf(
                    Virkedager.beregnAntallVirkedager(periode.getFom(), periode.getTom()));
            aktivitet.setTrekkdagerDesimaler(trekkdagerDesimaler);
        } else {
            aktivitet.setTrekkdagerDesimaler(BigDecimal.ZERO);
        }
        aktivitet.setUtbetalingsgrad(BigDecimal.ZERO);
        // HACK for manglende aktivitet i periode (set aktivitet til å trekke fra mødrekvoten)
        if (aktivitet.getKontoType() == null) {
            aktivitet.setKontotype(KontoType.MØDREKVOTE);
        }
    }

    private UttakResultatPeriode finnPeriode(LocalDate fra, LocalDate til) {
        for (UttakResultatPeriode uttakPeriode : perioder) {
            if (uttakPeriode.getFom().equals(fra) && uttakPeriode.getTom().equals(til)) {
                return uttakPeriode;
            }
        }
        throw new IllegalArgumentException("Fant ikke periode for gitt FOM og TOM dato! " + fra + " - " + til);
    }

    private void setTrekkdager(UttakResultatPeriodeAktivitet aktivitet,
                               BigDecimal ordinæreTrekkdagerVedFulltUttak,
                               BigDecimal utbetalignsgrad) {
        aktivitet.setTrekkdagerDesimaler(
                ordinæreTrekkdagerVedFulltUttak.multiply(utbetalignsgrad)
                        .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
                        .setScale(1, RoundingMode.HALF_UP));

    }

    private UttakResultatPeriode deepCopy(Object object){
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream)) {
                outputStrm.writeObject(object);
            }
            try (ObjectInputStream objInputStream = new ObjectInputStream(
                    new ByteArrayInputStream(outputStream.toByteArray()))) {
                return (UttakResultatPeriode) objInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Klarte ikke å lage en kopi av perioden", e);
        }
    }
}
