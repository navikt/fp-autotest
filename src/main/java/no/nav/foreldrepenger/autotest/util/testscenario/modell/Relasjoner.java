package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.util.List;

public record Relasjoner(List<Relasjon> relasjoner) {

    public Relasjon annenforeldre() {
        return relasjoner.stream()
                .filter(relasjon -> relasjon.relatertPersonsRolle().equals(RelasjonType.EKTEFELLE))
                .findFirst()
                .orElseThrow();
    }

    public Relasjon barn() {
        return relasjoner.stream()
                .filter(relasjon -> relasjon.relatertPersonsRolle().equals(RelasjonType.BARN))
                .findFirst()
                .orElseThrow();
    }
}
