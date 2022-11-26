package no.nav.foreldrepenger.autotest.klienter;

import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.openam.SaksbehandlerRolle;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.tokenx.TokenXVekslingKlient;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;

/**
 * Denne klassen skal tilby følgende token:
 *  - OpenAM Token
 *  - Loginservice token som er veksles inn til et TokenX token for en gitt bruker (fnr)
 */
public final class TokenProvider {

    private TokenProvider() {
        // Skal ikke instansieres
    }

    public static String openAMToken(SaksbehandlerRolle saksbehandlerRolle) {
        return no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.TokenProvider.azureAdToken(saksbehandlerRolle, "fpsak-localhost");
    }

    public static String tokenXToken(Fødselsnummer fnr) {
        return TokenXVekslingKlient.hentAccessTokenForBruker(fnr);
    }


}
