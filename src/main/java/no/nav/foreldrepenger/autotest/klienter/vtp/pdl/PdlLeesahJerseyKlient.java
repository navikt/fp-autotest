package no.nav.foreldrepenger.autotest.klienter.vtp.pdl;

import static javax.ws.rs.client.Entity.json;

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

    @Step("Legger til hendelse på PDL topic")
    public void opprettHendelse(PersonhendelseDto personhendelseDto) {
        LOG.info("Legger til hendelse av type: " + personhendelseDto.getType() + " i PDL ");
        client.target(base)
                .path(PDL_LEESAH)
                .request()
                .post(json(personhendelseDto));
    }
}
