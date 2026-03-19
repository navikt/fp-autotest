package no.nav.foreldrepenger.generator.familie;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.vtp.kontrakter.person.PersonDto;

public class Mor extends Søker {
    Mor(Ident ident, Ident identAnnenpart, PersonDto personDto, Innsender innsenderType) {
        super(ident, identAnnenpart, personDto, innsenderType);
    }
}
