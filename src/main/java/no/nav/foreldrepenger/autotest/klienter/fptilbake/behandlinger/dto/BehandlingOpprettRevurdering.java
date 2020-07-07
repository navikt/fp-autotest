package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

import java.util.UUID;

public class BehandlingOpprettRevurdering {
    protected Long saksnummer;
    protected int behandlingId;
    protected UUID eksternUuid;
    protected String behandlingType;
    protected RevurderingArsak behandlingArsakType;
    protected BehandlingType fagsakYtelseType = new BehandlingType();

    public BehandlingOpprettRevurdering(Long saksnummer, int behandlingId, UUID eksternUuid, String behandlingType,
            String fagsakYtelseType, RevurderingArsak behandlingArsakType) {
        super();
        this.saksnummer = saksnummer;
        this.behandlingId = behandlingId;
        this.eksternUuid = eksternUuid;
        this.behandlingType = behandlingType;
        this.behandlingArsakType = behandlingArsakType;
        this.fagsakYtelseType.kode = fagsakYtelseType;
    }
}
