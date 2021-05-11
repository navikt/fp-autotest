package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FamilieHendelseType {

    ADOPSJON("ADPSJN", "Adopsjon"),
    OMSORG("OMSRGO", "Omsorgoverdragelse"),
    FØDSEL("FODSL", "Fødsel"),
    TERMIN("TERM", "Termin"),
    UDEFINERT("-", "Ikke satt eller valgt kode"),
    ;


    private final String kode;
    private final String navn;

    FamilieHendelseType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @JsonCreator
    public static FamilieHendelseType fraKode(String kode) {
        return Arrays.stream(FamilieHendelseType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet FagsakStatus " + kode));
    }

    public String getKode() {
        return kode;
    }

    public String getNavn() {
        return navn;
    }
}
