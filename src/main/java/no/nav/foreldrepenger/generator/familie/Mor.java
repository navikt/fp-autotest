package no.nav.foreldrepenger.generator.familie;

import java.util.Map;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PersonDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.TilordnetIdentDto;

public class Mor extends Søker {
    Mor(Fødselsnummer ident, AktørId aktørId, AktørId aktørIdAnnenpart, PersonDto personDto, Map<UUID, TilordnetIdentDto> identer, Innsender innsenderType) {
        super(ident, aktørId, aktørIdAnnenpart, personDto, identer, innsenderType);
    }
}
