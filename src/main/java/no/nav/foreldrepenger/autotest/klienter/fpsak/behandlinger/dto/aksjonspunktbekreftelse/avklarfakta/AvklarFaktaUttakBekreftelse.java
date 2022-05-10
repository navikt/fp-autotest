package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakPeriodeVurderingType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KontrollerFaktaPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakDokumentasjon;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

public abstract class AvklarFaktaUttakBekreftelse extends AksjonspunktBekreftelse {

    protected List<BekreftetUttakPeriode> bekreftedePerioder = new ArrayList<>();
    protected List<BekreftetUttakPeriode> slettedePerioder = new ArrayList<>();

    protected AvklarFaktaUttakBekreftelse() {
        super();
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        for (KontrollerFaktaPeriode periode : behandling.getKontrollerFaktaData().getPerioder()) {
            var bekreftetUttakPeriode = new BekreftetUttakPeriode(periode.getFom(),
                    periode.getTom(),
                    periode.getArbeidstidsprosent(),
                    periode.getBegrunnelse(),
                    periode);

            periode.setBekreftet(true);

            bekreftedePerioder.add(bekreftetUttakPeriode);
        }
    }

    public AvklarFaktaUttakBekreftelse godkjennPeriode(LocalDate fra, LocalDate til) {
        return godkjennPeriode(fra, til, UttakPeriodeVurderingType.PERIODE_OK, true);
    }

    public AvklarFaktaUttakBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, UttakPeriodeVurderingType godkjenningskode) {
        return godkjennPeriode(fra, til, godkjenningskode, false);
    }

    public AvklarFaktaUttakBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, boolean dokumenterPeriode) {
        return godkjennPeriode(fra, til, UttakPeriodeVurderingType.PERIODE_OK, dokumenterPeriode);
    }

    public AvklarFaktaUttakBekreftelse godkjennPeriode(KontrollerFaktaPeriode faktaPeriode) {
        godkjennPeriode(faktaPeriode.getFom(), faktaPeriode.getTom(), UttakPeriodeVurderingType.PERIODE_OK, true);
        return this;
    }

    public AvklarFaktaUttakBekreftelse kanIkkeAvgjørePeriode(LocalDate fra, LocalDate til) {
        var periode = finnUttaksperiode(fra, til);
        periode.bekreftetPeriode.setResultat(UttakPeriodeVurderingType.PERIODE_KAN_IKKE_AVKLARES);
        return this;
    }

    public AvklarFaktaUttakBekreftelse delvisGodkjennPeriode(LocalDate fra, LocalDate til, LocalDate godkjentFra,
            LocalDate godkjentTil, UttakPeriodeVurderingType godkjenningskode) {
        delvisGodkjennPeriode(fra, til, godkjentFra, godkjentTil, godkjenningskode, false);
        return this;
    }

    public AvklarFaktaUttakBekreftelse delvisGodkjennPeriode(LocalDate fra, LocalDate til, LocalDate godkjentFra,
            LocalDate godkjentTil, UttakPeriodeVurderingType godkjenningskode, boolean dokumenterPeriode) {
        var periode = finnUttaksperiode(fra, til);
        godkjennPeriode(periode, godkjenningskode, dokumenterPeriode);
        periode.bekreftetPeriode.setFom(godkjentFra);
        periode.bekreftetPeriode.setTom(godkjentTil);
        return this;
    }

    private AvklarFaktaUttakBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, UttakPeriodeVurderingType godkjenningskode,
            boolean dokumenterPeriode) {
        var periode = finnUttaksperiode(fra, til);
        godkjennPeriode(periode, godkjenningskode, dokumenterPeriode);
        return this;
    }

    private void godkjennPeriode(BekreftetUttakPeriode periode, UttakPeriodeVurderingType godkjenningskode, boolean dokumenterPeriode) {
        periode.bekreftetPeriode.setBekreftet(true);
        periode.bekreftetPeriode.setResultat(godkjenningskode);
        periode.bekreftetPeriode.setBegrunnelse("Godkjent av autotest");
        if (dokumenterPeriode) {
            periode.bekreftetPeriode.getDokumentertePerioder().add(new UttakDokumentasjon(periode.orginalFom, periode.orginalTom));
        }
    }

    private BekreftetUttakPeriode finnUttaksperiode(LocalDate fra, LocalDate til) {
        for (BekreftetUttakPeriode periode : bekreftedePerioder) {
            if (periode.orginalFom.equals(fra) && periode.orginalTom.equals(til)) {
                return periode;
            }
        }
        throw new IllegalStateException("Fant ikke uttaksperiode fra som går fra " + fra + " til " + til);
    }

    public static class BekreftetUttakPeriode {

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

    @BekreftelseKode(kode = "5070")
    public static class AvklarFaktaUttakPerioder extends AvklarFaktaUttakBekreftelse {

        public AvklarFaktaUttakPerioder() {
            super();
        }

        public AvklarFaktaUttakPerioder sykdomErDokumentertForPeriode() {
            bekreftedePerioder.stream()
                    .map(periode -> periode.bekreftetPeriode)
                    .filter(kontrollerFaktaPeriode -> kontrollerFaktaPeriode.getResultat().equals(UttakPeriodeVurderingType.PERIODE_IKKE_VURDERT))
                    .forEach(this::godkjennPeriode);
            return this;
        }

    }

    @BekreftelseKode(kode = "5081")
    public static class AvklarFaktaUttakFørsteUttakDato extends AvklarFaktaUttakBekreftelse {

        public AvklarFaktaUttakFørsteUttakDato() {
            super();
        }
    }

}
