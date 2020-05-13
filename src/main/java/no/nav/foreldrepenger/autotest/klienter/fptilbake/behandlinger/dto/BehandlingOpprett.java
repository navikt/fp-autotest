package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

import java.util.UUID;

public class BehandlingOpprett {
    protected Long saksnummer;
    protected UUID eksternUuid;
    protected String behandlingType;
    protected String fagsakYtelseType;

    public BehandlingOpprett(Long saksnummer, UUID eksternUuid, String behandlingType, String fagsakYtelseType) {
        super();
        this.saksnummer = saksnummer;
        this.eksternUuid = eksternUuid;
        this.behandlingType = behandlingType;
        this.fagsakYtelseType = fagsakYtelseType;
    }
}
