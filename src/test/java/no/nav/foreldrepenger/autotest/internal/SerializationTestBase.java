package no.nav.foreldrepenger.autotest.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.mapper.json.DefaultJson3Mapper;


public class SerializationTestBase {

    protected static final Logger LOG = LoggerFactory.getLogger(SerializationTestBase.class);

    protected static void test(Object obj) {
        test(obj, true);
    }

    protected static void test(Object obj, boolean log) {
        try {
            if (log) {
                LOG.info("{}", obj);
            }
            var serialized = DefaultJson3Mapper.toJson(obj);
            if (log) {
                LOG.info("Serialized as {}", serialized);
            }
            var deserialized = DefaultJson3Mapper.fromJson(serialized, obj.getClass());
            if (log) {
                LOG.info("{}", deserialized);
            }
            assertEquals(obj, deserialized);
        } catch (Exception e) {
            LOG.error("Oops", e);
            fail(obj.getClass().getSimpleName() + " failed");
        }
    }
}
