package no.nav.foreldrepenger.autotest.aktoerer.fpsoknad_mottak;

import java.io.UnsupportedEncodingException;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.MottakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.dto.Kvittering;
import no.nav.foreldrepenger.autotest.klienter.vtp.oauth2.Oauth2Klient;
import no.nav.foreldrepenger.autotest.søknad.modell.Søknad;

public class Selvbetjening extends Aktoer {

    private final MottakKlient mottakKlient;
    private final Oauth2Klient oauth2Klient;

    public Selvbetjening() {
        mottakKlient = new MottakKlient(session);
        oauth2Klient = new Oauth2Klient(session);
    }

    public Kvittering sendInnSøknad(String fnr, Søknad søknad) throws UnsupportedEncodingException {
        var token = oauth2Klient.hentTokenVtpForFnr(fnr);
        return mottakKlient.sendSøknad(token.getIdToken(), søknad);
    }


}
