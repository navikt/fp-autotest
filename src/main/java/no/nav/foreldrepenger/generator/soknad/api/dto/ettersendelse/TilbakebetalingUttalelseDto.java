package no.nav.foreldrepenger.generator.soknad.api.dto.ettersendelse;

import no.nav.foreldrepenger.common.domain.Saksnummer;

public record TilbakebetalingUttalelseDto(YtelseType type,
                                          Saksnummer saksnummer,
                                          String dialogId,
                                          BrukerTekstDto brukerTekst) {
}
