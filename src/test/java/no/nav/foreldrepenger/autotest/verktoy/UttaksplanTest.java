package no.nav.foreldrepenger.autotest.verktoy;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;

@Tag("internal")
class UttaksplanTest {

    @Test
    @DisplayName("Periode som bare består av helgedager avvises")
    @Description("UttaksperioderMaler flytter grensene til virkedager, så en lørdag blir fom mandag og tom fredag før. Det skal gi feil, ikke en baklengs periode.")
    void periodeUtenVirkedagerAvvises() {
        var lørdag = LocalDate.of(2026, 11, 14);
        var perioder = List.of(Map.of("forelder", "mor", "konto", "MØDREKVOTE", "fom", lørdag.toString(), "tom", lørdag.toString()));

        assertThatThrownBy(() -> Uttaksplan.fra(perioder, null, lørdag, "mor"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ingen virkedager");
    }
}
