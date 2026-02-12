package no.nav.foreldrepenger.autotest.klienter.fplos.dto;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;

public record SakslisteLagreDto(@NotNull String avdelingEnhet,
                                @NotNull @Digits(integer = 18, fraction = 0) Long sakslisteId,
                                @NotNull String navn,
                                @NotNull @Valid SorteringDto sortering,
                                @Size(max = 20) Set<@NotNull BehandlingType> behandlingTyper,
                                @Size(max = 20) Set<@NotNull FagsakYtelseType> fagsakYtelseTyper,
                                @Valid @NotNull AndreKriterieDto andreKriterie) {



    public record SorteringDto(@NotNull KøSortering sorteringType,
                               @Valid @NotNull Periodefilter periodefilter,
                               @Min(-500) @Max(10_000_000) Long fra,
                               @Min(-500) @Max(10_000_000) Long til,
                               LocalDate fomDato,
                               LocalDate tomDato) {
    }

    public record AndreKriterieDto(@Size(max = 50) Set<@NotNull AndreKriterierType> inkluder, @Size(max = 50) Set<@NotNull AndreKriterierType> ekskluder) {
    }
}
