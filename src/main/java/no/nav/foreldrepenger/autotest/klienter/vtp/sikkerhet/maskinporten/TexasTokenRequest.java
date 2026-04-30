package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.maskinporten;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TexasTokenRequest(
        String identity_provider,
        String target,
        List<AuthorizationDetails> authorization_details
) {
}
