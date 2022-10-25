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
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;

public abstract class AvklarFaktaUttakBekreftelse extends AksjonspunktBekreftelse {

    protected List<BekreftetUttakPeriode> bekreftedePerioder = new ArrayList<>();

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

    public AvklarFaktaUttakBekreftelse godkjennPeriode(LocalDate fom, LocalDate tom) {
        return godkjennPeriode(fom, tom, UttakPeriodeVurderingType.PERIODE_OK, true);
    }
    public AvklarFaktaUttakBekreftelse godkjennPeriode(LukketPeriodeMedVedlegg periode) {
        return godkjennPeriode(periode.getFom(), periode.getTom(), UttakPeriodeVurderingType.PERIODE_OK, true);
    }

    public AvklarFaktaUttakBekreftelse kanIkkeAvgjørePeriode(LukketPeriodeMedVedlegg periode) {
        var bekreftetUttakPeriode = finnUttaksperiode(periode.getFom(), periode.getTom());
        bekreftetUttakPeriode.bekreftetPeriode.setResultat(UttakPeriodeVurderingType.PERIODE_KAN_IKKE_AVKLARES);
        return this;
    }

    private AvklarFaktaUttakBekreftelse godkjennPeriode(LocalDate fra, LocalDate til, UttakPeriodeVurderingType godkjenningskode, boolean dokumenterPeriode) {
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

    public record BekreftetUttakPeriode(LocalDate orginalFom, LocalDate orginalTom,
                                        BigDecimal originalArbeidstidsprosent,
                                        String originalBegrunnelse,
                                        KontrollerFaktaPeriode bekreftetPeriode) {
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
                    .forEach(periode -> godkjennPeriode(periode.getFom(), periode.getTom()));
            return this;
        }

    }
}
