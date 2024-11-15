package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record HistorikkInnslag(UUID behandlingUuid,
                               HistorikkAktørDto aktør,
                               String skjermlenke,
                               String tittel,
                               LocalDateTime opprettetTidspunkt,
                               List<HistorikkInnslagDokumentLinkDto> dokumenter,
                               List<String> body) {

    public record HistorikkAktørDto(HistorikkAktør type, String ident) {}


    public boolean erSøknadMottatt() {
        return erAvTypen(HistorikkType.BEH_STARTET) && HistorikkAktør.SØKER.equals(aktør.type);
    }

    public boolean erAvTypen(HistorikkType... type) {
        return erAvTypen(null, type);
    }

    public boolean erAvTypen(HistorikkAktør aktør, HistorikkType... type) {
        if (aktør != null && !aktør.equals(this.aktør().type)) {
            return false;
        }

        return Arrays.stream(type).anyMatch(this::harTittelEllerSkjemlenkeTilsvarendeType);
    }

    private boolean harTittelEllerSkjemlenkeTilsvarendeType(HistorikkType t) {
        return t.tittel().equals(tittel()) || (t.skjermlenke() != null && t.skjermlenke().equals(skjermlenke()));
    }
}

