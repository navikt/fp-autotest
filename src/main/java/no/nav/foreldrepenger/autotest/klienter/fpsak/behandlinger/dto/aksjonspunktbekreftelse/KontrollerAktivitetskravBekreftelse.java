package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KontrollerAktiviteskravPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KontrollerAktivitetskravAvklaring;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;

@BekreftelseKode(kode = "5099")
public class KontrollerAktivitetskravBekreftelse extends AksjonspunktBekreftelse {

    protected List<KontrollerAktiviteskravPeriode> avklartePerioder = new ArrayList<>();

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        avklartePerioder = behandling.getKontrollerAktiviteskrav();
        morErIAktivitetForAllePerioder();
    }

    public KontrollerAktivitetskravBekreftelse morErIAktivitetForAllePerioder() {
        for (KontrollerAktiviteskravPeriode p : avklartePerioder) {
            p.setAvklaring(KontrollerAktivitetskravAvklaring.I_AKTIVITET);
            p.setBegrunnelse("Autotest begrunnelse");
        }
        return this;
    }

    public KontrollerAktivitetskravBekreftelse periodeIkkeAktivitetIkkeDokumentert(LukketPeriodeMedVedlegg periode) {
        setAvklaringForPeriode(periode.getFom(), periode.getTom(), KontrollerAktivitetskravAvklaring.IKKE_I_AKTIVITET_IKKE_DOKUMENTERT);
        return this;
    }

    private KontrollerAktivitetskravBekreftelse setAvklaringForPeriode(LocalDate fom, LocalDate tom, KontrollerAktivitetskravAvklaring avklaring) {
        avklartePerioder.stream()
                .filter(p -> p.getFom().equals(fom))
                .filter(p -> p.getTom().equals(tom))
                .forEach(p -> p.setAvklaring(avklaring));
        return this;
    }
}
