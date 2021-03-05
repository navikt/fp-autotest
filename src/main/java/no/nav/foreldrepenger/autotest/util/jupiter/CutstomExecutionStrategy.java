package no.nav.foreldrepenger.autotest.util.jupiter;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfiguration;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfigurationStrategy;

/**
 * Fixed strategy vil ikke garantere antall threads opprette:
 * https://github.com/junit-team/junit5/issues/2273#issuecomment-623850954
 */
class CutstomExecutionStrategy implements ParallelExecutionConfiguration, ParallelExecutionConfigurationStrategy {
    @Override
    public int getParallelism() {
        return 4;
    }

    @Override
    public int getMinimumRunnable() {
        return 2;
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
        return 30;
    }

    @Override
    public ParallelExecutionConfiguration createConfiguration(final ConfigurationParameters configurationParameters) {
        return this;
    }
}
