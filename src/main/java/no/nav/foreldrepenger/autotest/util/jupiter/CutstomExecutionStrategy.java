package no.nav.foreldrepenger.autotest.util.jupiter;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfiguration;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfigurationStrategy;

/**
 * Midlertidig workaround for https://github.com/SeleniumHQ/selenium/issues/10113
 * -> getMinimumRunnable settes til 0.
 */
class CutstomExecutionStrategy implements ParallelExecutionConfiguration, ParallelExecutionConfigurationStrategy {

    @Override
    public int getParallelism() {
        return 4;
    }

    @Override
    public int getMinimumRunnable() {
        return 0; // https://github.com/SeleniumHQ/selenium/issues/10113
    }

    @Override
    public int getMaxPoolSize() {
        return 4;
    }

    @Override
    public int getCorePoolSize() {
        return 4;
    }

    @Override
    public int getKeepAliveSeconds() {
        return 60;
    }

    @Override
    public ParallelExecutionConfiguration createConfiguration(final ConfigurationParameters configurationParameters) {
        return this;
    }
}
