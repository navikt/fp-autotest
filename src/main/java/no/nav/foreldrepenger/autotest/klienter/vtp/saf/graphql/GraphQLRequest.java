package no.nav.foreldrepenger.autotest.klienter.vtp.saf.graphql;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GraphQLRequest(@JsonProperty("query") String query,
                             @JsonProperty("operationName") String operationName,
                             @JsonProperty("variables") Map<String, Object> variables) {
}
