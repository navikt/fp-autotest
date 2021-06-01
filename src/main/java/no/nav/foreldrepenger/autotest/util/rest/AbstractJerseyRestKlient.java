package no.nav.foreldrepenger.autotest.util.rest;

import static no.nav.foreldrepenger.autotest.util.rest.JacksonObjectMapper.mapper;
import static no.nav.foreldrepenger.autotest.util.rest.RestClientSupportProdusent.connectionManager;
import static no.nav.foreldrepenger.autotest.util.rest.RestClientSupportProdusent.createKeepAliveStrategy;
import static no.nav.foreldrepenger.autotest.util.rest.RestClientSupportProdusent.defaultRequestConfig;
import static org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS;

import java.util.Optional;
import java.util.Set;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.apache.connector.ApacheHttpClientBuilderConfigurator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestFilter;


public abstract class AbstractJerseyRestKlient {

    protected final Client client;

    protected AbstractJerseyRestKlient() {
        this(mapper, Set.of());
    }
    protected AbstractJerseyRestKlient(ObjectMapper mapper) {
        this(mapper, Set.of());
    }
    protected AbstractJerseyRestKlient(ClientRequestFilter... filters) {
        this(mapper, filters);
    }
    protected AbstractJerseyRestKlient(ObjectMapper mapper, ClientRequestFilter... filters) {
        this(mapper, Set.of(filters));
    }

    private AbstractJerseyRestKlient(ObjectMapper mapper, Set<? extends ClientRequestFilter> filters) {
        var cfg = new ClientConfig();
        cfg.register(jacksonProvider(mapper));
        cfg.connectorProvider(new ApacheConnectorProvider());
        cfg.register((ApacheHttpClientBuilderConfigurator) b ->
                b.setKeepAliveStrategy(createKeepAliveStrategy(30))
                .setDefaultRequestConfig(defaultRequestConfig())
                .setRetryHandler(new HttpRequestRetryHandler())
                .setConnectionManager(connectionManager()));
        cfg.register(new HeaderLoggingFilter());
        cfg.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE);
        filters.forEach(cfg::register);
        client = ClientBuilder.newClient(cfg);
    }

    private static JacksonJaxbJsonProvider jacksonProvider(ObjectMapper mapper) {
        return Optional.ofNullable(mapper)
                .map(m -> new JacksonJaxbJsonProvider(m, DEFAULT_ANNOTATIONS))
                .orElse(new JacksonJaxbJsonProvider());
    }
}
