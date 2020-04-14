package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@BekreftelseKode(kode="5071")
public class FastsettUttaksperioderManueltBekreftelse extends AksjonspunktBekreftelse {

    protected List<UttakResultatPeriode> perioder = new ArrayList<>();

    public FastsettUttaksperioderManueltBekreftelse() {
        super();
    }

    @Override
    public void setFagsakOgBehandling(Fagsak fagsak, Behandling behandling) {
        super.setFagsakOgBehandling(fagsak, behandling);
        for (UttakResultatPeriode uttakPeriode : behandling.hentUttaksperioder()) {
            if (uttakPeriode.getManuellBehandlingÅrsak() != null && !uttakPeriode.getManuellBehandlingÅrsak().kode.equals("-")) {
                uttakPeriode.setBegrunnelse("Begrunnelse");
            }
            LeggTilUttakPeriode(uttakPeriode);
        }
    }

    public void godkjennAllePerioder() {
        for (UttakResultatPeriode uttakResultatPeriode : perioder) {
            godkjennPeriode(uttakResultatPeriode, 100);
            uttakResultatPeriode.setBegrunnelse("Begrunnelse autotest");
        }
    }
    public void godkjennAlleManuellePerioder(int utbetalingsgrad){
        godkjennAlleManuellePerioder(utbetalingsgrad, 100);
    }
    public void godkjennAlleManuellePerioder(int utbetalingsgrad, int trekkdager){
        for(UttakResultatPeriode uttakPeriode : perioder) {
            if(!uttakPeriode.getManuellBehandlingÅrsak().kode.equals("-")){
                godkjennPeriode(uttakPeriode, utbetalingsgrad, false, false, trekkdager);
            }
        }
    }
    public void godkjennAlleManuellePerioder(Kode periodeResultatÅrsak) {
        for(UttakResultatPeriode uttaksperiode : perioder) {
            if (!uttaksperiode.getManuellBehandlingÅrsak().kode.equals("-")) {
                godkjennPeriode(uttaksperiode, periodeResultatÅrsak);
            }
        }
    }
    public void godkjennAlleManuellePerioderMedHverSinÅrsak(List<Kode> periodeResultatÅrsak) {
        for (int i=0; i < perioder.size(); i++) {
            if (!perioder.get(i).getManuellBehandlingÅrsak().kode.equals("-")) {
                godkjennPeriode(perioder.get(i), periodeResultatÅrsak.get(i));
            }
        }
    }

    public void avslåAlleManuellePerioder(){
        for(UttakResultatPeriode uttakPeriode : perioder) {
            if(!uttakPeriode.getManuellBehandlingÅrsak().kode.equals("-")){
                avvisPeriode(uttakPeriode, 0);
            }
        }
    }
    public void avslåAlleManuellePerioderMedPeriodeResultatÅrsak(Kode periodeResultatÅrsak){
        for(UttakResultatPeriode uttakPeriode : perioder) {
            if(!uttakPeriode.getManuellBehandlingÅrsak().kode.equals("-")){
                avvisPeriode(uttakPeriode, periodeResultatÅrsak,0);
            }
        }
    }
    public List<UttakResultatPeriode> getManuellePerioder() {
        return perioder.stream().
                filter(uttakResultatPeriode -> !uttakResultatPeriode.getManuellBehandlingÅrsak().kode.equals("-"))
                .collect(Collectors.toList());
    }
    public void delvisGodkjennAvslåAktivitetManuellePerioder(String aktivitetNavn) {
        delvisGodkjennAvslåForAktivitet(getManuellePerioder(), aktivitetNavn);
    }
    public void delvisGodkjennAvslåForAktivitet(List<UttakResultatPeriode> perioder, String aktivitetNavn) {
        for (UttakResultatPeriode periode : perioder) {
            for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
                if (aktivitet.getUttakArbeidType().kode.equals(aktivitetNavn)) {
                    aktivitet.setTrekkdagerDesimaler(BigDecimal.valueOf(0));
                    aktivitet.setUtbetalingsgrad(BigDecimal.valueOf(0));
                }
                else {
                    aktivitet.setUtbetalingsgrad(BigDecimal.valueOf(100));
                }
            }
            periode.setBegrunnelse("Begrunnelse");
            periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
            periode.setPeriodeResultatÅrsak(new Kode("INNVILGET_AARSAK", "2002"));
        }
    }

    public FastsettUttaksperioderManueltBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, int utbetalingsgrad) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        godkjennPeriode(periode, utbetalingsgrad);
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, Kode periodeResultatÅrsak, int utbetalingsgrad) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);
        periode.setBegrunnelse("Vurdering");
        godkjennPeriode(periode, utbetalingsgrad);
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, int utbetalingsgrad,
                                                                    Kode periodeResultatÅrsak, boolean flerbarnsdager,
                                                                    boolean samtidigUttak) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);
        periode.setSamtidigUttak(samtidigUttak);
        periode.setFlerbarnsdager(flerbarnsdager);
        periode.setBegrunnelse("Vurdering");
        godkjennPeriode(periode, utbetalingsgrad);
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, int utbetalingsgrad,
                                                                    Kode periodeResultatÅrsak, boolean flerbarnsdager,
                                                                    boolean samtidigUttak, int samtidigUttakProsent) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);
        periode.setSamtidigUttak(samtidigUttak);
        periode.setSamtidigUttaksprosent(BigDecimal.valueOf(samtidigUttakProsent));
        periode.setFlerbarnsdager(flerbarnsdager);
        periode.setBegrunnelse("Vurdering");
        godkjennPeriode(periode, utbetalingsgrad);
        return this;
    }

    public FastsettUttaksperioderManueltBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, int utbetalingsgrad, Stønadskonto stønadskonto) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        godkjennPeriode(periode, utbetalingsgrad, stønadskonto);
        return this;
    }

    public void godkjennPeriode(UttakResultatPeriode periode, int utbetalingsgrad) {
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        periode.setPeriodeResultatÅrsak(new Kode("INNVILGET_AARSAK", "2001", "§14-6: Uttak er oppfylt"));

        //Utsettelses perioder trenger ikke trekkdager. set dem til 0
        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            aktivitet.setUtbetalingsgrad(BigDecimal.valueOf(utbetalingsgrad));
            if(!UttakUtsettelseÅrsak.UDEFINERT.equals(periode.getUtsettelseType())) {
                aktivitet.setTrekkdagerDesimaler(BigDecimal.ZERO);
            }
        }
    }
    public void godkjennPeriode(UttakResultatPeriode periode, int utbetalingsgrad, boolean flerbarnsdager, boolean samtidigUttak ) {
        godkjennPeriode(periode, utbetalingsgrad, flerbarnsdager, samtidigUttak, 100);
    }
    public void godkjennPeriode(UttakResultatPeriode uttaksperiode, Kode periodeResultatÅrsak) {
        uttaksperiode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        uttaksperiode.setPeriodeResultatÅrsak(periodeResultatÅrsak);
        uttaksperiode.setSamtidigUttak(false);
        uttaksperiode.setFlerbarnsdager(false);

        //Trekkdager endres ikke på
        for (UttakResultatPeriodeAktivitet aktivitet : uttaksperiode.getAktiviteter()) {
            aktivitet.setUtbetalingsgrad(BigDecimal.valueOf((100 - aktivitet.getProsentArbeid().doubleValue())));
        }
    }

    public void godkjennPeriode(UttakResultatPeriode periode, int utbetalingsgrad, boolean flerbarnsdager,
                                boolean samtidigUttak, int trekkdager ) {
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        periode.setPeriodeResultatÅrsak(new Kode("INNVILGET_AARSAK", "2001", "§14-6: Uttak er oppfylt"));
        periode.setSamtidigUttak(samtidigUttak);
        periode.setFlerbarnsdager(flerbarnsdager);

        //Utsettelses perioder trenger ikke trekkdager. set dem til 0
        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            aktivitet.setUtbetalingsgrad(BigDecimal.valueOf(utbetalingsgrad));
            aktivitet.setTrekkdagerDesimaler(BigDecimal.valueOf(trekkdager));
            if(!UttakUtsettelseÅrsak.UDEFINERT.equals(periode.getUtsettelseType())) {
                aktivitet.setTrekkdagerDesimaler(BigDecimal.ZERO);
            }
        }
    }

    public FastsettUttaksperioderManueltBekreftelse godkjennPeriodeMedGradering(UttakResultatPeriode periode, Kode periodeResultatÅrsak) {
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);

        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            BigDecimal andelArbeid = aktivitet.getProsentArbeid();
            aktivitet.setUtbetalingsgrad(BigDecimal.valueOf(100).subtract(andelArbeid));
        }
        return this;
    }

    public void godkjennPeriode(UttakResultatPeriode periode, int utbetalingsgrad, Stønadskonto stønadskonto) {
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        periode.setPeriodeResultatÅrsak(new Kode("INNVILGET_AARSAK", "2001", "§14-6: Uttak er oppfylt"));
        periode.setStønadskonto(stønadskonto);

        //Utsettelses perioder trenger ikke trekkdager. set dem til 0
        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            aktivitet.setUtbetalingsgrad(BigDecimal.valueOf(utbetalingsgrad));
            if(!UttakUtsettelseÅrsak.UDEFINERT.equals(periode.getUtsettelseType())) {
                aktivitet.setTrekkdagerDesimaler(BigDecimal.ZERO);
            }
        }
    }


    public void avvisPeriode(LocalDate fra, LocalDate til, int utbetalingsgrad) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        avvisPeriode(periode, utbetalingsgrad);
    }

    public void avvisPeriode(UttakResultatPeriode periode, int utbetalingsgrad) {
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "AVSLÅTT", "Avslått"));
        periode.setPeriodeResultatÅrsak(Kode.lagBlankKode());

        //HACK for manglende aktivitet i periode (set aktivitet til å trekke fra mødrekvoten)
        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            aktivitet.setUtbetalingsgrad(BigDecimal.valueOf(utbetalingsgrad));
            aktivitet.setTrekkdagerDesimaler(BigDecimal.ZERO);
            if(aktivitet.getStønadskontoType() == null || aktivitet.getStønadskontoType().equals(Stønadskonto.INGEN_STØNADSKONTO)) {
                aktivitet.setStønadskontoType(Stønadskonto.MØDREKVOTE);
            }
        }
    }

    public void avvisPeriode(UttakResultatPeriode periode, Kode periodeResultatÅrsak, int utbetalingsgrad) {
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "AVSLÅTT", "Avslått"));
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);

        //HACK for manglende aktivitet i periode (set aktivitet til å trekke fra mødrekvoten)
        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            aktivitet.setUtbetalingsgrad(BigDecimal.valueOf(utbetalingsgrad));
            aktivitet.setTrekkdagerDesimaler(BigDecimal.ZERO);
        }
    }

    public void LeggTilUttakPeriode(UttakResultatPeriode uttakPeriode){
        perioder.add(uttakPeriode);
    }

    public UttakResultatPeriode finnPeriode(LocalDate fra, LocalDate til) {
        for (UttakResultatPeriode uttakPeriode : perioder) {
            if(uttakPeriode.getFom().equals(fra) && uttakPeriode.getTom().equals(til)) {
                return uttakPeriode;
            }
        }
        return null;
    }

    public void splitPeriode(LocalDate fom, LocalDate tom, LocalDate sluttenAvFørstePeriode) {
        UttakResultatPeriode periode1 = finnPeriode(fom, tom);
        BigDecimal arbeidsdagerIUken = BigDecimal.valueOf(5);
        BigDecimal diffTrekkdager = BigDecimal.valueOf(ChronoUnit.WEEKS.between(sluttenAvFørstePeriode, tom)).multiply(arbeidsdagerIUken);

        periode1.setTom(sluttenAvFørstePeriode);
        for (UttakResultatPeriodeAktivitet aktivitet : periode1.getAktiviteter()) {
            BigDecimal trekkdagerDesimalerOrdinær = aktivitet.getTrekkdagerDesimaler();
            aktivitet.setTrekkdagerDesimaler(trekkdagerDesimalerOrdinær.subtract(diffTrekkdager));
        }

        UttakResultatPeriode periode2 = deepCopy(periode1);
        periode2.setFom(sluttenAvFørstePeriode.plusDays(1));
        periode2.setTom(tom);
        for (UttakResultatPeriodeAktivitet aktivitet : periode2.getAktiviteter()) {
            aktivitet.setTrekkdagerDesimaler(diffTrekkdager);
        }

        int indeksAvPeriode = perioder.indexOf(periode1);
        perioder.add(indeksAvPeriode + 1, periode2);
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
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
