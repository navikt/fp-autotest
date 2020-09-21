package no.nav.foreldrepenger.autotest.klienter.vtp.pdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;
import no.nav.foreldrepenger.vtp.kontrakter.PersonhendelseDto;

public class PdlLeesahKlient extends VTPKlient {

    private static final String PDL_LEESAH = "/pdl/leesah";
    private static final Logger LOG = LoggerFactory.getLogger(PdlLeesahKlient.class);

    public PdlLeesahKlient(HttpSession session) {
        super(session);
    }

    @Step("Legger til hendelse på PDL topic")
    public void opprettHendelse(PersonhendelseDto personhendelseDto) {
        String url = hentRestRotUrl() + PDL_LEESAH;
        LOG.info("Legger til hendelse av type: " + personhendelseDto.getType() + " i PDL ");
        postOgHentJson(url, personhendelseDto, Object.class, StatusRange.STATUS_SUCCESS);
    }
}
