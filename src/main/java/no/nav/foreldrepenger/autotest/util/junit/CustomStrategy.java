package no.nav.foreldrepenger.autotest.util.junit;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfiguration;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfigurationStrategy;

/** EXPERIMENTAL: Denne strategien brukes fordi det ser ut til å være noe bugg med "fixed"
 *  strategy i junit parallel execution */
class CustomStrategy implements ParallelExecutionConfiguration, ParallelExecutionConfigurationStrategy {
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
        return 2;
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
