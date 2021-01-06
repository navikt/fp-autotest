package no.nav.foreldrepenger.autotest.klienter.vtp.saf.graphql;

import java.util.List;

public record GraphQlError(String message,
                           List<ErrorLocation> locations,
                           List<String> path,
                           String exceptionType,
                           String exception) {
}
