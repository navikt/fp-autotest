package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;

public class BehandlingOpprettRevurdering extends BehandlingOpprett{
    protected int behandlingId;
    protected RevurderingArsak behandlingArsakType;

    public BehandlingOpprettRevurdering(Saksnummer saksnummer, int behandlingId, UUID eksternUuid, String behandlingType,
                                        String fagsakYtelseType, RevurderingArsak behandlingArsakType) {
        super(saksnummer, eksternUuid, behandlingType, fagsakYtelseType);
        this.behandlingId = behandlingId;
        this.behandlingArsakType = behandlingArsakType;
    }
}
