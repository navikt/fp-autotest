package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto;

import java.util.Set;

public record EsSak(Saksnummer saksnummer,
             Familiehendelse familiehendelse,
             EsVedtak gjeldendeVedtak,
             EsÅpenBehandling åpenBehandling,
             Set<PersonDetaljer> barn,
             boolean sakAvsluttet,
             boolean gjelderAdopsjon) implements Sak {
}
