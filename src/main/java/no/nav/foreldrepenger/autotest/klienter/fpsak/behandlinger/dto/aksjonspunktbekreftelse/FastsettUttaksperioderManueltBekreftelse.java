package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.autotest.util.localdate.Virkedager;

@BekreftelseKode(kode = "5071")
public class FastsettUttaksperioderManueltBekreftelse extends AksjonspunktBekreftelse {

    protected List<UttakResultatPeriode> perioder = new ArrayList<>();

    public FastsettUttaksperioderManueltBekreftelse() {
        super();
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        perioder = new ArrayList<>(behandling.hentUttaksperioder());
        innvilgManuellePerioder();
    }

    public List<UttakResultatPeriode> getPerioder() {
        return perioder;
    }

    public FastsettUttaksperioderManueltBekreftelse innvilgManuellePerioder() {
        for (UttakResultatPeriode uttaksperiode : perioder) {
            if ((uttaksperiode.getManuellBehandlingÅrsak() != null)
                    && !uttaksperiode.getManuellBehandlingÅrsak().kode.equals("-")) {
                innvilgPeriode(uttaksperiode);
            }
        }
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse innvilgManuellePerioder(Kode periodeResultatÅrsak) {
        for (UttakResultatPeriode uttaksperiode : perioder) {
            if ((uttaksperiode.getManuellBehandlingÅrsak() != null)
                    && !uttaksperiode.getManuellBehandlingÅrsak().kode.equals("-")) {
                innvilgPeriode(uttaksperiode, periodeResultatÅrsak);
            }
        }
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse avslåManuellePerioder() {
        for (UttakResultatPeriode uttaksperiode : perioder) {
            if ((uttaksperiode.getManuellBehandlingÅrsak() != null)
                    && !uttaksperiode.getManuellBehandlingÅrsak().kode.equals("-")) {
                avslåPeriode(uttaksperiode);
            }
        }
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse avslåManuellePerioderMedPeriodeResultatÅrsak(Kode periodeResultatÅrsak) {
        for (UttakResultatPeriode uttakPeriode : perioder) {
            if (!uttakPeriode.getManuellBehandlingÅrsak().kode.equals("-")) {
                avslåPeriode(uttakPeriode, periodeResultatÅrsak, false);
            }
        }
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse innvilgAktiviteterOgAvslåResten(LocalDate fra,
                                                                                    LocalDate til,
                                                                                    List<String> organisasjonsnummere) {
        UttakResultatPeriode uttakResultatPeriode = finnPeriode(fra, til);
        innvilgAktiviteterOgAvslåResten(uttakResultatPeriode, organisasjonsnummere);
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse innvilgPeriode(LocalDate fra, LocalDate til) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        innvilgPeriode(periode);
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse innvilgPeriode(LocalDate fra,
                                                                   LocalDate til,
                                                                   Kode periodeResultatÅrsak) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        innvilgPeriode(periode, periodeResultatÅrsak);
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse innvilgPeriode(LocalDate fra,
                                                                   LocalDate til,
                                                                   Kode periodeResultatÅrsak,
                                                                   Stønadskonto stønadskonto) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        innvilgPeriode(periode, periodeResultatÅrsak, stønadskonto);
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse innvilgPeriode(LocalDate fra,
                                                                   LocalDate til,
                                                                   Kode periodeResultatÅrsak,
                                                                   boolean flerbarnsdager,
                                                                   boolean samtidigUttak,
                                                                   int samtidigUttakProsent) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        periode.setSamtidigUttak(samtidigUttak);
        periode.setSamtidigUttaksprosent(BigDecimal.valueOf(samtidigUttakProsent));
        periode.setFlerbarnsdager(flerbarnsdager);
        innvilgPeriode(periode, periodeResultatÅrsak);
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse avslåPeriode(LocalDate fra, LocalDate til) {
        avslåPeriode(fra, til, Kode.lagBlankKode());
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse avslåPeriode(LocalDate fra,
                                                                 LocalDate til,
                                                                 Kode periodeResultatÅrsak) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        avslåPeriode(periode, periodeResultatÅrsak, false);
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse avslåPeriode(LocalDate fra,
                                                                 LocalDate til,
                                                                 Kode periodeResultatÅrsak,
                                                                 boolean trekkDager) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        avslåPeriode(periode, periodeResultatÅrsak, trekkDager);
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse splitPeriode(LocalDate fom,
                                                                 LocalDate tom,
                                                                 LocalDate sluttenAvFørstePeriode) {
        UttakResultatPeriode periode1 = finnPeriode(fom, tom);

        periode1.setTom(sluttenAvFørstePeriode);
        for (UttakResultatPeriodeAktivitet aktivitet : periode1.getAktiviteter()) {
            int trekkdager = Virkedager.beregnAntallVirkedager(fom, sluttenAvFørstePeriode);
            aktivitet.setTrekkdagerDesimaler(BigDecimal.valueOf(trekkdager));
        }

        UttakResultatPeriode periode2 = deepCopy(periode1);
        periode2.setFom(sluttenAvFørstePeriode.plusDays(1));
        periode2.setTom(tom);
        for (UttakResultatPeriodeAktivitet aktivitet : periode2.getAktiviteter()) {
            int trekkdager = Virkedager.beregnAntallVirkedager(sluttenAvFørstePeriode.plusDays(1), tom);
            aktivitet.setTrekkdagerDesimaler(BigDecimal.valueOf(trekkdager));
        }

        int indeksAvPeriode = perioder.indexOf(periode1);
        perioder.add(indeksAvPeriode + 1, periode2);

        return this;
    }

    // PRIVATE METODER //

    private void innvilgPeriode(UttakResultatPeriode periode) {
        innvilgPeriode(periode, new Kode("INNVILGET_AARSAK", "2001", "§14-6: Uttak er oppfylt"));
    }

    private void innvilgPeriode(UttakResultatPeriode periode, Kode periodeResultatÅrsak, Stønadskonto stønadskonto) {
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);
        periode.setBegrunnelse("Perioden er innvilget av Autotest.");
        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            aktivitet.setStønadskontoType(stønadskonto);
            innvilgAktivitetForPeriode(periode, aktivitet);
        }
    }

    private void innvilgPeriode(UttakResultatPeriode periode, Kode periodeResultatÅrsak) {
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
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
        if (samtidigUttak) {
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
        if (!UttakUtsettelseÅrsak.UDEFINERT.equals(periode.getUtsettelseType())) {
            aktivitet.setTrekkdagerDesimaler(BigDecimal.ZERO);
        }
    }

    private void avslåPeriode(UttakResultatPeriode periode) {
        avslåPeriode(periode, Kode.lagBlankKode(), false);
    }

    private void avslåPeriode(UttakResultatPeriode periode, Kode periodeResultatÅrsak, boolean trekkDager) {
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "AVSLÅTT", "Avslått"));
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
        // HACK for manglende aktivitet i periode (set aktivitet til å trekke fra
        // mødrekvoten)
        if ((aktivitet.getStønadskontoType() == null) || aktivitet.getStønadskontoType()
                .equals(Stønadskonto.INGEN_STØNADSKONTO)) {
            aktivitet.setStønadskontoType(Stønadskonto.MØDREKVOTE);
        }
    }

    private void innvilgAktiviteterOgAvslåResten(UttakResultatPeriode periode, List<String> organisasjonsnummere) {
        innvilgAktiviteterOgAvslåResten(periode, organisasjonsnummere,
                new Kode("INNVILGET_AARSAK", "2002", "§14-9: Innvilget fellesperiode/foreldrepenger"));
    }

    private void innvilgAktiviteterOgAvslåResten(UttakResultatPeriode periode,
                                                 List<String> organisasjonsnummere,
                                                 Kode periodeResultatÅrsak) {
        if ((organisasjonsnummere == null) || (organisasjonsnummere.size() == 0)) {
            throw new IllegalArgumentException("Bruk avslåPeriode() istedenfor!");
        }
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);
        periode.setBegrunnelse("Innvilger angitte aktiviteter og avslå resten");
        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            if (organisasjonsnummere.contains(aktivitet.getArbeidsgiverReferanse())) {
                innvilgAktivitetForPeriode(periode, aktivitet);
            } else {
                avslåAktivitet(aktivitet, periode, false);
            }

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

    private UttakResultatPeriode deepCopy(Object object) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream)) {
                outputStrm.writeObject(object);
            }
            try (ObjectInputStream objInputStream = new ObjectInputStream(
                    new ByteArrayInputStream(outputStream.toByteArray()))) {
                return (UttakResultatPeriode) objInputStream.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
