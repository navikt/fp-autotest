package no.nav.foreldrepenger.autotest.util.vent;

import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonValue;

public class Lazy<V> {

    private final Supplier<V> supplier;
    @JsonValue
    private V result;

    public Lazy(Supplier<V> supplier) {
        this.supplier = supplier;
    }

    public V get() {
        if (result == null) {
            result = supplier.get();
        }
        return result;
    }
}
