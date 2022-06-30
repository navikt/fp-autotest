package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

import java.util.UUID;

public class BehandlingOpprettRevurdering {
    protected Long saksnummer;
    protected int behandlingId;
    protected UUID eksternUuid;
    protected String behandlingType;
    protected RevurderingArsak behandlingArsakType;
    protected String fagsakYtelseType;

    public BehandlingOpprettRevurdering(String saksnummer, int behandlingId, UUID eksternUuid, String behandlingType,
            String fagsakYtelseType, RevurderingArsak behandlingArsakType) {
        super();
        this.saksnummer = Long.valueOf(saksnummer);
        this.behandlingId = behandlingId;
        this.eksternUuid = eksternUuid;
        this.behandlingType = behandlingType;
        this.behandlingArsakType = behandlingArsakType;
        this.fagsakYtelseType = fagsakYtelseType;
    }
}
