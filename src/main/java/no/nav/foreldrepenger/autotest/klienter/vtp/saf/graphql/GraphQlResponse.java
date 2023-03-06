package no.nav.foreldrepenger.autotest.klienter.vtp.saf.graphql;

import java.util.List;

public record GraphQlResponse(GrapQlData data, List<GraphQlError> errors) {

}
