package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingHenlegg;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.SettBehandlingPaVentDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftedeAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;

public interface BehandlingerKlient {
    Behandling getBehandling(UUID behandlingUuid);

    Behandling hentBehandlingHvisTilgjenglig(UUID behandlingUuid);

    List<Behandling> alle(Saksnummer saksnummer);

    void settPaVent(SettBehandlingPaVentDto behandling);

    void henlegg(BehandlingHenlegg behandling);

    Behandling gjenoppta(BehandlingIdDto behandling);

    List<Aksjonspunkt> hentAlleAksjonspunkter(UUID behandlingUuid);

    void postBehandlingAksjonspunkt(BekreftedeAksjonspunkter aksjonspunkter);

}
