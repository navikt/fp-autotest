package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

import java.util.UUID;

import no.nav.foreldrepenger.common.domain.Saksnummer;

public class BehandlingOpprettRevurdering {
    protected Saksnummer saksnummer;
    protected int behandlingId;
    protected UUID eksternUuid;
    protected String behandlingType;
    protected RevurderingArsak behandlingArsakType;
    protected String fagsakYtelseType;

    public BehandlingOpprettRevurdering(Saksnummer saksnummer, int behandlingId, UUID eksternUuid, String behandlingType,
                                        String fagsakYtelseType, RevurderingArsak behandlingArsakType) {
        super();
        this.saksnummer = saksnummer;
        this.behandlingId = behandlingId;
        this.eksternUuid = eksternUuid;
        this.behandlingType = behandlingType;
        this.behandlingArsakType = behandlingArsakType;
        this.fagsakYtelseType = fagsakYtelseType;
    }
}
