package no.nav.foreldrepenger.autotest.util;

import java.util.Collection;

public final class CollectionUtils {

    private CollectionUtils() {
        // skjul ctor
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }
}
