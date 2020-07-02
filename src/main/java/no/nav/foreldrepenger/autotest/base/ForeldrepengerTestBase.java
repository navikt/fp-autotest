package no.nav.foreldrepenger.autotest.base;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;

public class ForeldrepengerTestBase extends FpsakTestBase {

    protected List<Integer> sorterteInntektsbeløp(TestscenarioDto testscenario) {
        return testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioderSplittMånedlig().stream()
                .map(Inntektsperiode::getBeløp)
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
}
