package no.nav.foreldrepenger.autotest.klienter.fpsak.hendelse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdPost;
import no.nav.foreldrepenger.autotest.klienter.fpsak.hendelse.dto.FødselHendelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.hendelse.dto.Hendelse;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

import java.io.IOException;

public class HendelseKlient extends FpsakKlient {

    private static String HENDELSE_URL = "/hendelser";
    private static String SEND_HENDELSE_URL = HENDELSE_URL + "/hendelse";


    public HendelseKlient(HttpSession session) {super(session);}

    public void sendHendelse(FødselHendelse hendelse) throws IOException {
        String url = hentRestRotUrl() + SEND_HENDELSE_URL;
        postOgVerifiser(url,new HendelseWrapper(hendelse), StatusRange.STATUS_200);
    }

    public static class HendelseWrapper {
        protected Hendelse hendelse;

        public HendelseWrapper(Hendelse hendelse){
            this.hendelse = hendelse;
        }

    }
}
