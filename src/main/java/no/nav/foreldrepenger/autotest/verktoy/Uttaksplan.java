package no.nav.foreldrepenger.autotest.verktoy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import no.nav.foreldrepenger.generator.familie.Familie;
import no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.MorsAktivitet;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.OverføringÅrsak;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Rolle;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UtsettelseÅrsak;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UttakPeriodeDto;

/**
 * Felles uttaksplan fra JSON. Hver periode tilhører mor eller far; perioder for den andre forelderen
 * sendes som annen parts uttak i søknaden.
 */
record Uttaksplan(List<Periode> perioder) {

    record Periode(String forelder, LocalDate fom, LocalDate tom, UttakPeriodeDto dto) {
    }

    static Uttaksplan fra(Object json, Familie familie, LocalDate hendelsesdato, String søker) {
        if (!(json instanceof List<?> liste) || liste.isEmpty()) {
            throw new IllegalArgumentException("perioder må være en ikke-tom liste med periodeobjekter.");
        }
        var perioder = liste.stream()
                .map(p -> {
                    if (!(p instanceof Map<?, ?> m)) {
                        throw new IllegalArgumentException("Hver periode må være et JSON-objekt.");
                    }
                    return periode(m, familie, hendelsesdato, søker);
                })
                .sorted(Comparator.comparing(Periode::fom))
                .toList();
        return new Uttaksplan(perioder);
    }

    List<UttakPeriodeDto> sett(String søker) {
        return perioder.stream()
                .map(p -> p.forelder().equals(søker)
                        ? p.dto()
                        : new UttakPeriodeDto(p.dto().fom(), p.dto().tom(), null, p.dto().søker(), null))
                .toList();
    }

    Optional<LocalDate> førsteDag(String forelder) {
        return perioder.stream().filter(p -> p.forelder().equals(forelder)).map(p -> p.dto().fom()).findFirst();
    }

    private static Periode periode(Map<?, ?> m, Familie familie, LocalDate dato, String søker) {
        var forelder = tekst(m, "forelder").orElse(søker);
        if (!forelder.equals("mor") && !forelder.equals("far")) {
            throw new IllegalArgumentException("Ukjent forelder i periode: " + forelder);
        }
        var rolle = forelder.equals("mor") ? Rolle.MOR : Rolle.FAR_MEDMOR;
        var fom = dato(m, "fom").orElseGet(() -> dato.plusWeeks(uke(m, "fra_uke")));
        var tom = dato(m, "tom").orElseGet(() -> dato.plusWeeks(uke(m, "til_uke")).minusDays(1));
        if (tom.isBefore(fom)) {
            throw new IllegalArgumentException("Periode slutter før den starter: " + fom + " til " + tom);
        }
        var konto = tekst(m, "konto").map(k -> enumverdi(KontoType.class, k, "konto"));
        var morsAktivitet = tekst(m, "mors_aktivitet").map(a -> enumverdi(MorsAktivitet.class, a, "mors_aktivitet")).orElse(null);
        var utsettelse = tekst(m, "utsettelse").map(u -> enumverdi(UtsettelseÅrsak.class, u, "utsettelse"));
        var overføring = tekst(m, "overforing").map(o -> enumverdi(OverføringÅrsak.class, o, "overforing"));
        var samtidig = m.get("samtidig_uttak") instanceof Number n ? BigDecimal.valueOf(n.doubleValue()) : null;
        var gradering = m.get("gradering") instanceof Number n ? BigDecimal.valueOf(n.doubleValue()) : null;

        UttakPeriodeDto dto;
        if (utsettelse.isPresent()) {
            dto = UttaksperioderMaler.utsettelsesperiode(rolle, utsettelse.get(), fom, tom, morsAktivitet);
        } else {
            var kontoType = konto.orElseThrow(() -> new IllegalArgumentException(
                    "Periode " + fom + " mangler konto (eller utsettelse)."));
            if (overføring.isPresent()) {
                dto = UttaksperioderMaler.overføringsperiode(rolle, overføring.get(), kontoType, fom, tom);
            } else if (gradering != null) {
                var forelderen = forelder.equals("mor") ? familie.mor() : familie.far();
                var orgnr = forelderen.arbeidsforholdene().stream()
                        .map(a -> a.arbeidsgiverIdentifikasjon())
                        .filter(id -> id.length() == 9)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Gradering krever at " + forelder + " har arbeidsforhold."));
                dto = UttaksperioderMaler.graderingsperiodeArbeidstaker(rolle, kontoType, fom, tom, orgnr, gradering, morsAktivitet);
            } else {
                dto = UttaksperioderMaler.uttaksperiode(rolle, kontoType, fom, tom, samtidig, morsAktivitet);
            }
        }
        if (dto.tom().isBefore(dto.fom())) {
            throw new IllegalArgumentException("Perioden " + fom + " til " + tom + " har ingen virkedager.");
        }
        return new Periode(forelder, dto.fom(), dto.tom(), dto);
    }

    private static <E extends Enum<E>> E enumverdi(Class<E> type, String verdi, String felt) {
        var normalisert = verdi.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(type.getEnumConstants())
                .filter(e -> e.name().equals(normalisert))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ukjent " + felt + ": " + verdi + ". Gyldige: "
                        + String.join(", ", Arrays.stream(type.getEnumConstants()).map(Enum::name).toList())));
    }

    private static Optional<String> tekst(Map<?, ?> m, String navn) {
        var verdi = m.get(navn);
        return verdi == null || verdi.toString().isBlank() ? Optional.empty() : Optional.of(verdi.toString());
    }

    private static Optional<LocalDate> dato(Map<?, ?> m, String navn) {
        return tekst(m, navn).map(LocalDate::parse);
    }

    private static int uke(Map<?, ?> m, String navn) {
        if (m.get(navn) instanceof Number n) {
            return n.intValue();
        }
        throw new IllegalArgumentException("Periode mangler " + navn + " (eller " + (navn.equals("fra_uke") ? "fom" : "tom") + ").");
    }
}
