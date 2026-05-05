package no.nav.foreldrepenger.generator.familie;

import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;

public record Ident(Fødselsnummer fødselsnummer, AktørId aktørId) {

    public static Ident fra(String verdi) {
        if (verdi == null || verdi.isBlank()) {
            throw new IllegalArgumentException("Ident kan ikke være null eller tom");
        }
        var fnr = switch (verdi.length()) {
            case 11 -> verdi;
            case 13 -> verdi.substring(2);
            default -> throw new IllegalArgumentException(
                    "Ident må være 11 siffer (fødselsnummer) eller 13 siffer (aktørId), var: " + verdi);
        };
        return new Ident(new Fødselsnummer(fnr), new AktørId("99" + fnr));
    }

    @Override
    public String toString() {
        return fødselsnummer.value();
    }
}

