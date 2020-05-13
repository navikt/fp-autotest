package no.nav.foreldrepenger.autotest.util.vent;

import java.util.function.Supplier;

public class Lazy<V> {

    private final Supplier<V> supplier;
    private V result;

    public Lazy(Supplier<V> supplier) {
        this.supplier = supplier;
    }

    public synchronized V get() {
        if (result == null) {
            result = supplier.get();
        }
        return result;
    }
}
