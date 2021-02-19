package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode = "5073")
public class KontrollerRealitetsbehandlingEllerKlage extends AksjonspunktBekreftelse {

    protected List<UttakResultatPeriode> perioder = new ArrayList<>();

    public KontrollerRealitetsbehandlingEllerKlage() {
        super();
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        perioder = behandling.hentUttaksperioder();
    }

}
