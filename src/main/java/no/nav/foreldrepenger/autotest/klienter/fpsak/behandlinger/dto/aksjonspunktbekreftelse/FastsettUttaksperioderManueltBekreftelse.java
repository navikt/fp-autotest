package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.autotest.util.localdate.Virkedager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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

    public List<UttakResultatPeriode> getPerioder() {
        return perioder;
    }

    public void godkjennAllePerioder() {
        for (UttakResultatPeriode uttakResultatPeriode : perioder) {
            godkjennPeriode(uttakResultatPeriode);
            uttakResultatPeriode.setBegrunnelse("Begrunnelse autotest");
        }
    }
    public void godkjennAlleManuellePerioder(){
        for(UttakResultatPeriode uttakPeriode : perioder) {
            if(!uttakPeriode.getManuellBehandlingÅrsak().kode.equals("-")){
                godkjennPeriode(uttakPeriode);
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
                avvisPeriode(uttakPeriode);
            }
        }
    }
    public void avslåAlleManuellePerioderMedPeriodeResultatÅrsak(Kode periodeResultatÅrsak){
        for(UttakResultatPeriode uttakPeriode : perioder) {
            if(!uttakPeriode.getManuellBehandlingÅrsak().kode.equals("-")){
                avvisPeriode(uttakPeriode, periodeResultatÅrsak);
            }
        }
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

    public FastsettUttaksperioderManueltBekreftelse godkjennPeriode(LocalDate fra, LocalDate til) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        godkjennPeriode(periode);
        return this;
    }
    public FastsettUttaksperioderManueltBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, Kode periodeResultatÅrsak) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);
        periode.setBegrunnelse("Vurdering");
        godkjennPeriode(periode);
        return this;
    }
    public FastsettUttaksperioderManueltBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, Kode periodeResultatÅrsak,
                                                                    boolean flerbarnsdager, boolean samtidigUttak,
                                                                    int samtidigUttakProsent) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);
        periode.setSamtidigUttak(samtidigUttak);
        periode.setSamtidigUttaksprosent(BigDecimal.valueOf(samtidigUttakProsent));
        periode.setFlerbarnsdager(flerbarnsdager);
        periode.setBegrunnelse("Vurdering");
        godkjennPeriode(periode);
        return this;
    }
    public FastsettUttaksperioderManueltBekreftelse avvisPeriode(LocalDate fra, LocalDate til) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        avvisPeriode(periode);
        return this;
    }
    public FastsettUttaksperioderManueltBekreftelse avvisPeriode(LocalDate fra, LocalDate til, Kode periodeResultatÅrsak) {
        UttakResultatPeriode periode = finnPeriode(fra, til);
        avvisPeriode(periode, periodeResultatÅrsak);
        return this;
    }

    public void splitPeriode(LocalDate fom, LocalDate tom, LocalDate sluttenAvFørstePeriode) {
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
    }

    private void godkjennPeriode(UttakResultatPeriode periode) {
        godkjennPeriode(periode, new Kode("INNVILGET_AARSAK", "2001", "§14-6: Uttak er oppfylt"));
    }
    private void godkjennPeriode(UttakResultatPeriode periode, Kode periodeResultatÅrsak) {
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "INNVILGET", "Innvilget"));
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);
        BigDecimal ordinæreTrekkdagerVedFulltUttak = BigDecimal.valueOf(
                Virkedager.beregnAntallVirkedager(periode.getFom(), periode.getTom()));

        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            if ( periode.getSamtidigUttak() ) {
                if ( periode.getSamtidigUttaksprosent() == null ){
                    throw new NullPointerException("Samtidig uttaksprosent er ikke satt!");
                }
                if ( aktivitet.getProsentArbeid().doubleValue() > periode.getSamtidigUttaksprosent().doubleValue()) {
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
            if ( !UttakUtsettelseÅrsak.UDEFINERT.equals(periode.getUtsettelseType()) ) {
                aktivitet.setTrekkdagerDesimaler(BigDecimal.ZERO);
            }
        }
    }
    private void avvisPeriode(UttakResultatPeriode periode) {
        avvisPeriode(periode, Kode.lagBlankKode());
    }
    private void avvisPeriode(UttakResultatPeriode periode, Kode periodeResultatÅrsak) {
        periode.setPeriodeResultatType(new Kode("PERIODE_RESULTAT_TYPE", "AVSLÅTT", "Avslått"));
        periode.setPeriodeResultatÅrsak(periodeResultatÅrsak);

        //HACK for manglende aktivitet i periode (set aktivitet til å trekke fra mødrekvoten)
        for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
            aktivitet.setUtbetalingsgrad(BigDecimal.ZERO);
            aktivitet.setTrekkdagerDesimaler(BigDecimal.ZERO);
            if(aktivitet.getStønadskontoType() == null || aktivitet.getStønadskontoType().equals(Stønadskonto.INGEN_STØNADSKONTO)) {
                aktivitet.setStønadskontoType(Stønadskonto.MØDREKVOTE);
            }
        }
    }

    private void LeggTilUttakPeriode(UttakResultatPeriode uttakPeriode){
        perioder.add(uttakPeriode);
    }
    private UttakResultatPeriode finnPeriode(LocalDate fra, LocalDate til) {
        for (UttakResultatPeriode uttakPeriode : perioder) {
            if(uttakPeriode.getFom().equals(fra) && uttakPeriode.getTom().equals(til)) {
                return uttakPeriode;
            }
        }
        return null;
    }
    private List<UttakResultatPeriode> getManuellePerioder() {
        return perioder.stream().
                filter(uttakResultatPeriode -> !uttakResultatPeriode.getManuellBehandlingÅrsak().kode.equals("-"))
                .collect(Collectors.toList());
    }
    private void setTrekkdager(UttakResultatPeriodeAktivitet aktivitet, BigDecimal ordinæreTrekkdagerVedFulltUttak, BigDecimal utbetalignsgrad) {
        BigDecimal utbetalingsprosentfaktor = utbetalignsgrad.divide(BigDecimal.valueOf(100));
        aktivitet.setTrekkdagerDesimaler(ordinæreTrekkdagerVedFulltUttak
                .multiply(utbetalingsprosentfaktor)
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
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
