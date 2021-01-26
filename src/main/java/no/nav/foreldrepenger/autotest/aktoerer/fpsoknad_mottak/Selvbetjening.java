package no.nav.foreldrepenger.autotest.aktoerer.fpsoknad_mottak;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.MottakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.dto.Kvittering;
import no.nav.foreldrepenger.autotest.klienter.vtp.oauth2.Oauth2JerseyKlient;
import no.nav.foreldrepenger.autotest.søknad.modell.Søknad;

public class Selvbetjening extends Aktoer {

    private final MottakJerseyKlient mottakKlient;
    private final Oauth2JerseyKlient oauth2Klient;

    public Selvbetjening() {
        mottakKlient = new MottakJerseyKlient();
        oauth2Klient = new Oauth2JerseyKlient();
    }

    public Kvittering sendInnSøknad(String fnr, Søknad søknad) {
        var token = oauth2Klient.hentAccessTokenForBruker(fnr);
        return mottakKlient.sendSøknad(token, søknad);
    }


}
