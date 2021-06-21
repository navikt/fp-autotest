package no.nav.foreldrepenger.autotest.klienter.vtp.pdl;

import static jakarta.ws.rs.client.Entity.json;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.tilJsonOgPubliserIAllureRapport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.vtp.VTPJerseyKlient;
import no.nav.foreldrepenger.vtp.kontrakter.PersonhendelseDto;

public class PdlLeesahJerseyKlient extends VTPJerseyKlient {

    private static final String PDL_LEESAH = "/pdl/leesah";
    private static final Logger LOG = LoggerFactory.getLogger(PdlLeesahJerseyKlient.class);

    public PdlLeesahJerseyKlient() {
        super();
    }

    @Step("Sender inn {personhendelseDto.type}")
    public void opprettHendelse(PersonhendelseDto personhendelseDto) {
        tilJsonOgPubliserIAllureRapport(personhendelseDto);
        LOG.info("Legger til hendelse av type: {} i PDL", personhendelseDto.getType());
        client.target(base)
                .path(PDL_LEESAH)
                .request()
                .post(json(personhendelseDto));
    }
}
