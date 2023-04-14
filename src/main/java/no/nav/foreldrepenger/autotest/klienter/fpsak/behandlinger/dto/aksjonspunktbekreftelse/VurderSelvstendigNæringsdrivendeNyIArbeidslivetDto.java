package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import jakarta.validation.constraints.NotNull;

public class VurderSelvstendigNæringsdrivendeNyIArbeidslivetDto {

    @NotNull
    private Boolean erNyIArbeidslivet;

    VurderSelvstendigNæringsdrivendeNyIArbeidslivetDto() {
        // For Jackson
    }

    public VurderSelvstendigNæringsdrivendeNyIArbeidslivetDto(Boolean erNyIArbeidslivet) {
        this.erNyIArbeidslivet = erNyIArbeidslivet;
    }

    public Boolean erNyIArbeidslivet() {
        return erNyIArbeidslivet;
    }

}
