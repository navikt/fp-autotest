package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KontrollerAktiviteskravPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KontrollerAktivitetskravAvklaring;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode = "5099")
public class KontrollerAktivitetskravBekreftelse extends AksjonspunktBekreftelse {

    protected List<KontrollerAktiviteskravPeriode> avklartePerioder = new ArrayList<>();

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        avklartePerioder = behandling.getKontrollerAktiviteskrav();
        iAktivitet();
    }

    public KontrollerAktivitetskravBekreftelse iAktivitet() {
        for (KontrollerAktiviteskravPeriode p : avklartePerioder) {
            p.setAvklaring(KontrollerAktivitetskravAvklaring.I_AKTIVITET);
            p.setBegrunnelse("Autotest begrunnelse");
        }
        return this;
    }
}
