package no.nav.foreldrepenger.autotest.base;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.sigrun.Inntektsår;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.sigrun.SigrunModell;

public class ForeldrepengerTestBase extends FpsakTestBase {

    protected Double hentNæringsinntektFraSigrun(TestscenarioDto testscenarioDto, Integer beregnFraOgMedÅr, Boolean annenPart) {
        Integer beregFraÅr = beregnFraOgMedÅr;

        SigrunModell sigrunModell;
        if (annenPart) {
            sigrunModell = testscenarioDto.scenariodataAnnenpartDto().sigrunModell();
        } else {
            sigrunModell = testscenarioDto.scenariodataDto().sigrunModell();
        }

        double gjennomsnittDeTreSisteÅrene = sigrunModell.inntektsår().stream()
                .sorted(Comparator.comparing(Inntektsår::år).reversed())
                .filter(inntektsår -> Integer.parseInt(inntektsår.år()) <= beregFraÅr)
                .flatMap(inntektsår -> inntektsår.oppføring().stream())
                .mapToDouble(oppføring -> Double.parseDouble(oppføring.verdi()))
                .limit(3)
                .sum();

        return gjennomsnittDeTreSisteÅrene/3;
    }

    protected List<Integer> sorterteInntektsbeløp(TestscenarioDto testscenario) {
        return testscenario.scenariodataDto().inntektskomponentModell().getInntektsperioderSplittMånedlig().stream()
                .map(Inntektsperiode::beløp)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    protected List<Integer> regnUtForventetDagsatsForPeriode(List<Integer> månedsinntekter,
            List<Integer> utbetalingsgraderForPeriode,
            List<Boolean> refusjonerForPeriode) {
        List<Integer> dagsatser = Arrays.asList(new Integer[månedsinntekter.size()]);
        double årsinntekt = sumOfList(månedsinntekter).doubleValue() * 12;
        if (refusjonerForPeriode.contains(true)) {
            double sumAvÅrsinntektTilRefusjon = 0;
            double sumAvÅrsinntektDirekteTilSøker = 0;
            for (int i = 0; i < månedsinntekter.size(); i++) {
                if (refusjonerForPeriode.get(i)) {
                    sumAvÅrsinntektTilRefusjon += månedsinntekter.get(i).doubleValue() * 12;
                } else {
                    sumAvÅrsinntektDirekteTilSøker += månedsinntekter.get(i).doubleValue() * 12;
                }
            }

            double redusertSumAvÅrsinntektTilRefusjon = justerÅrsinntekt(sumAvÅrsinntektTilRefusjon);
            for (int i = 0; i < månedsinntekter.size(); i++) {
                if (refusjonerForPeriode.get(i)) {
                    double fordeling = (månedsinntekter.get(i).doubleValue() * 12) / sumAvÅrsinntektTilRefusjon;
                    dagsatser.set(i, (int) Math.round(((redusertSumAvÅrsinntektTilRefusjon / 260) * fordeling
                            * utbetalingsgraderForPeriode.get(i).doubleValue()) / 100));
                }
            }

            double resterendeÅrsinntekt = justerÅrsinntekt(årsinntekt);
            resterendeÅrsinntekt -= redusertSumAvÅrsinntektTilRefusjon;
            for (int i = 0; i < månedsinntekter.size(); i++) {
                if (dagsatser.get(i) == null) {
                    double fordeling = (månedsinntekter.get(i).doubleValue() * 12) / sumAvÅrsinntektDirekteTilSøker;
                    dagsatser.set(i, (int) Math.round(((resterendeÅrsinntekt / 260) * fordeling
                            * utbetalingsgraderForPeriode.get(i).doubleValue()) / 100));
                }
            }
        } else {
            double redusertÅrsinntekt = justerÅrsinntekt(årsinntekt);
            for (int i = 0; i < månedsinntekter.size(); i++) {
                if (dagsatser.get(i) == null) {
                    double fordeling = (månedsinntekter.get(i).doubleValue() * 12) / årsinntekt;
                    dagsatser.set(i, (int) Math.round(
                            ((redusertÅrsinntekt / 260) * fordeling * utbetalingsgraderForPeriode.get(i).doubleValue())
                                    / 100));
                }
            }
        }

        return dagsatser;
    }

    private Double justerÅrsinntekt(Double opprinneligÅrsinntekt) {
        double seksG = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getHalvG() * 2 * 6;
        if (opprinneligÅrsinntekt > seksG) {
            return seksG;
        }
        return opprinneligÅrsinntekt;
    }

    protected Integer sumOfList(List<Integer> list) {
        int sum = 0;
        for (int i : list) {
            sum += i;
        }
        return sum;
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
