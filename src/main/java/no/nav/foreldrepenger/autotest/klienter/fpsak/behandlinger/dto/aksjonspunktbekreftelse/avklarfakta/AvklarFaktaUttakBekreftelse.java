package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KontrollerFaktaPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakDokumentasjon;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;


public abstract class AvklarFaktaUttakBekreftelse extends AksjonspunktBekreftelse {

    protected List<BekreftetUttakPeriode> bekreftedePerioder = new ArrayList<>();
    protected List<BekreftetUttakPeriode> slettedePerioder = new ArrayList<>();

    public AvklarFaktaUttakBekreftelse() {
        super();
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        for (KontrollerFaktaPeriode periode : behandling.getKontrollerFaktaData().getPerioder()) {
            BekreftetUttakPeriode bekreftetUttakPeriode = new BekreftetUttakPeriode(periode.getFom(),
                    periode.getTom(),
                    periode.getArbeidstidsprosent(),
                    periode.getBegrunnelse(),
                    periode);

            periode.setBekreftet(true);

            bekreftedePerioder.add(bekreftetUttakPeriode);
        }
    }

    public AvklarFaktaUttakBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, Kode godkjenningskode) {
        return godkjennPeriode(fra, til, godkjenningskode, false);
    }

    public AvklarFaktaUttakBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, Kode godkjenningskode, boolean dokumenterPeriode) {
        BekreftetUttakPeriode periode = finnUttaksperiode(fra, til);

        periode.bekreftetPeriode.setBekreftet(true);
        periode.bekreftetPeriode.setResultat(godkjenningskode);
        periode.bekreftetPeriode.setBegrunnelse("Godkjent av autotest");
        if (dokumenterPeriode) {
            periode.bekreftetPeriode.getDokumentertePerioder().add(new UttakDokumentasjon(fra, til));
        }
        return this;
    }
    public AvklarFaktaUttakBekreftelse godkjennPeriode(KontrollerFaktaPeriode faktaPeriode, Kode godkjenningskode, boolean dokumenterPeriode) {
        godkjennPeriode(faktaPeriode.getFom(), faktaPeriode.getTom(), godkjenningskode, true);
        return this;
    }

    public AvklarFaktaUttakBekreftelse delvisGodkjennPeriode(LocalDate fra, LocalDate til, LocalDate godkjentFra, LocalDate godkjentTil, Kode godkjenningskode) {
        delvisGodkjennPeriode(fra, til, godkjentFra, godkjentTil, godkjenningskode, false);
        return this;
    }

    public AvklarFaktaUttakBekreftelse delvisGodkjennPeriode(LocalDate fra, LocalDate til, LocalDate godkjentFra, LocalDate godkjentTil, Kode godkjenningskode, boolean dokumenterPeriode) {
        BekreftetUttakPeriode periode = finnUttaksperiode(fra, til);

        periode.bekreftetPeriode.setBekreftet(true);
        periode.bekreftetPeriode.setResultat(godkjenningskode);
        periode.bekreftetPeriode.setBegrunnelse("Godkjent av autotest");

        periode.bekreftetPeriode.setFom(godkjentFra);
        periode.bekreftetPeriode.setTom(godkjentTil);
        if (dokumenterPeriode) {
            periode.bekreftetPeriode.getDokumentertePerioder().add(new UttakDokumentasjon(godkjentFra, godkjentTil));
        }
        return this;
    }

    private BekreftetUttakPeriode finnUttaksperiode(LocalDate fra, LocalDate til) {
        for (BekreftetUttakPeriode periode : bekreftedePerioder) {
            if(periode.orginalFom.equals(fra) && periode.orginalTom.equals(til)) {
                return periode;
            }
        }
        return null;
    }

    public class BekreftetUttakPeriode{

        protected LocalDate orginalFom;
        protected LocalDate orginalTom;
        protected BigDecimal originalArbeidstidsprosent;
        protected String originalBegrunnelse;
        protected KontrollerFaktaPeriode bekreftetPeriode;


        public BekreftetUttakPeriode(LocalDate orginalFom, LocalDate orginalTom, BigDecimal originalArbeidstidsprosent,
                String originalBegrunnelse, KontrollerFaktaPeriode bekreftetPeriode) {
            super();
            this.orginalFom = orginalFom;
            this.orginalTom = orginalTom;
            this.originalArbeidstidsprosent = originalArbeidstidsprosent;
            this.originalBegrunnelse = originalBegrunnelse;
            this.bekreftetPeriode = bekreftetPeriode;
        }
    }

    @BekreftelseKode(kode="5070")
    public static class AvklarFaktaUttakPerioder extends AvklarFaktaUttakBekreftelse {

        public AvklarFaktaUttakPerioder() {
            super();
        }
    }

    @BekreftelseKode(kode="5081")
    public static class AvklarFaktaUttakFørsteUttakDato extends AvklarFaktaUttakBekreftelse {

        public AvklarFaktaUttakFørsteUttakDato() {
            super();
        }
    }

}
