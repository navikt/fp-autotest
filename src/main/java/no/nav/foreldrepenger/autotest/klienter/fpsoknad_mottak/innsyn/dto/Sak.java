package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto;

import java.util.Set;

interface Sak {

    Saksnummer saksnummer();

    Familiehendelse familiehendelse();

    Set<PersonDetaljer> barn();

    boolean gjelderAdopsjon();

    boolean sakAvsluttet();

}
