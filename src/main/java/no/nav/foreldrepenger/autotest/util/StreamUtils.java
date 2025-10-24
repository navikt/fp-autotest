package no.nav.foreldrepenger.autotest.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StreamUtils {

    private StreamUtils() {
        // Ikke instansiere
    }

    public static <T> Stream<T> safeStream(List<T> list) {
        return ((List) Optional.ofNullable(list).orElseGet(List::of)).stream();
    }

    public static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors)
    {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .toList();

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }
}
