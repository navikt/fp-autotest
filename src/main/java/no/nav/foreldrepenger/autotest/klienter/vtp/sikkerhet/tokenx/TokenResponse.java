package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.tokenx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TokenResponse(String access_token,
                            String id_token,
                            String issued_token_type,
                            String token_type,
                            int expires_in) {
}
