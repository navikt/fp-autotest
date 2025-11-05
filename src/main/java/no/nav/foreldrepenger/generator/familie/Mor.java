package no.nav.foreldrepenger.generator.familie;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.InntektYtelseModell;

public class Mor extends Søker {
    Mor(Fødselsnummer ident, AktørId aktørId, AktørId aktørIdAnnenpart, InntektYtelseModell inntektYtelseModell, Innsender innsenderType) {
        super(ident, aktørId, aktørIdAnnenpart, inntektYtelseModell, innsenderType);
    }
}
