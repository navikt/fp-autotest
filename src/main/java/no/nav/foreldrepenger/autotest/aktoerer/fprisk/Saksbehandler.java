package no.nav.foreldrepenger.autotest.aktoerer.fprisk;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.RisikovurderingKlient;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.dto.RisikovurderingResponse;
import no.nav.foreldrepenger.autotest.klienter.vtp.kafka.KafkaKlient;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Saksbehandler extends Aktoer {
    Logger LOG = LoggerFactory.getLogger(Saksbehandler.class);

    private KafkaKlient kafkaKlient;
    private RisikovurderingKlient risikovurderingKlient;

    public Saksbehandler(){
        kafkaKlient = new KafkaKlient(session);
        risikovurderingKlient = new RisikovurderingKlient(session);
    }

    public String getKafkaTopics() throws IOException {
        return kafkaKlient.getKafkaTopics();
    }

    public String sendMessageToKafkaTopic(String topic, Object messageObject) throws IOException {
        return kafkaKlient.putMessageOnKafkaTopic(topic, messageObject);
}

    public RisikovurderingResponse getRisikovurdering(String konsumentId) throws IOException {
        return risikovurderingKlient.getRisikovurdering(konsumentId);
}


    /*
     * Behandlingsstatus
     */
    @Step("Venter til risikovurdering har status: {status}")
    public void ventTilRisikoKlassefiseringsstatus(String konsumentId, String status) throws Exception {
        Vent.til(() -> {
            RisikovurderingResponse response = getRisikovurdering(konsumentId);
            return harRisikoKlassefiseringsstatus(status, response);
        }, 60, "Feilet. Fikk ikke riktig status");
    }

    public boolean harRisikoKlassefiseringsstatus(String status, RisikovurderingResponse responseDto) {
         LOG.info("Har status: {} ", responseDto.getRisikoklasse());
         return responseDto.getRisikoklasse().equalsIgnoreCase(status);
    }
}
