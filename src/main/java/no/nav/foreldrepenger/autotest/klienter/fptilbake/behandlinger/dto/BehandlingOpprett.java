package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;

public class BehandlingOpprett {
    protected Saksnummer saksnummer;
    protected UUID eksternUuid;
    protected String behandlingType;
    protected String fagsakYtelseType;

    public BehandlingOpprett(Saksnummer saksnummer, UUID eksternUuid, String behandlingType, String fagsakYtelseType) {
        super();
        this.saksnummer = saksnummer;
        this.eksternUuid = eksternUuid;
        this.behandlingType = behandlingType;
        this.fagsakYtelseType = fagsakYtelseType;
    }
}
