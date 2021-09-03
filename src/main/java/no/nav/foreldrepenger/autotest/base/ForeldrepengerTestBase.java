package no.nav.foreldrepenger.autotest.base;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;

public class ForeldrepengerTestBase extends FpsakTestBase {

    protected List<Integer> sorterteInntektsbeløp(TestscenarioDto testscenario) {
        return testscenario.scenariodataDto().inntektskomponentModell().getInntektsperioderSplittMånedlig().stream()
                .map(Inntektsperiode::beløp)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    /* Verifisering av PDF */
    protected boolean is_pdf(byte[] data) {
        if (data != null && data.length > 4 &&
                data[0] == 0x25 && // %
                data[1] == 0x50 && // P
                data[2] == 0x44 && // D
                data[3] == 0x46 && // F
                data[4] == 0x2D) { // -

            // version 1.3 file terminator
            if (data[5] == 0x31 && data[6] == 0x2E && data[7] == 0x33 &&
                    data[data.length - 7] == 0x25 && // %
                    data[data.length - 6] == 0x25 && // %
                    data[data.length - 5] == 0x45 && // E
                    data[data.length - 4] == 0x4F && // O
                    data[data.length - 3] == 0x46 && // F
                    data[data.length - 2] == 0x20 && // SPACE
                    data[data.length - 1] == 0x0A) { // EOL
                return true;
            }

            // version 1.3 file terminator
            // EOL
            return data[5] == 0x31 && data[6] == 0x2E && data[7] == 0x34 &&
                    data[data.length - 6] == 0x25 && // %
                    data[data.length - 5] == 0x25 && // %
                    data[data.length - 4] == 0x45 && // E
                    data[data.length - 3] == 0x4F && // O
                    data[data.length - 2] == 0x46 && // F
                    data[data.length - 1] == 0x0A;
        }
        return false;
    }
}
