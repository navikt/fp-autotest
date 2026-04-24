package no.nav.foreldrepenger.autotest.klienter;

import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.maskinporten.AuthorizationDetails;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.maskinporten.MaskinportenKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.tokenx.TokenXVekslingKlient;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;

/**
 * Denne klassen skal tilby følgende token:
 *  - Loginservice token som er veksles inn til et TokenX token for en gitt bruker (fnr)
 *  - Maskinporten token for maskin-til-maskin autentisering
 */
public final class TokenProvider {

    private TokenProvider() {
        // Skal ikke instansieres
    }

    public static String tokenXToken(Fødselsnummer fnr) {
        return TokenXVekslingKlient.hentAccessTokenForBruker(fnr);
    }

    public static String maskinportenToken(String scope) {
        return MaskinportenKlient.hentAccessToken(scope);
    }

    public static String maskinportenToken(String scope, List<AuthorizationDetails> authorizationDetails) {
        return MaskinportenKlient.hentAccessToken(scope, authorizationDetails);
    }
}
