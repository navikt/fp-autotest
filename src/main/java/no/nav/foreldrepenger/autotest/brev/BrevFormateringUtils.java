package no.nav.foreldrepenger.autotest.brev;

import no.nav.foreldrepenger.autotest.base.BrevTestBase;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ofPattern;

public class BrevFormateringUtils {

    private BrevFormateringUtils() {
        // Static utility class
    }

    public static String formaterFnr(String fnr) {
        return fnr.substring(0, 6) + " " + fnr.substring(6);
    }

    public static String formaterKroner(int beløp) {
        var symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' '); // Explicitly setting a normal space (U+0020)
        return new DecimalFormat("#,###", symbols).format(beløp);
    }

    public static String formaterDato(LocalDate dato) {
        if (dato == null) {
            return null;
        }
        return dato.format(ofPattern("d. MMMM yyyy", Locale.forLanguageTag("NO")));
    }

    public static LocalDate førsteArbeidsdagEtter(LocalDate dato) {
        if (DayOfWeek.SATURDAY.equals(dato.getDayOfWeek())) {
            return dato.plusDays(2);
        } else if (DayOfWeek.SUNDAY.equals(dato.getDayOfWeek())) {
            return dato.plusDays(1);
        } else {
            return dato;
        }
    }

    public static String ytelseNavn(BrevTestBase.TypeYtelse typeYtelse) {
        return switch (typeYtelse) {
            case FP -> "foreldrepenger";
            case SVP -> "svangerskapspenger";
        };
    }
}
