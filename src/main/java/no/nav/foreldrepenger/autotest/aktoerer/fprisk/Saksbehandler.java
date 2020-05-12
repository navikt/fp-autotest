package no.nav.foreldrepenger.autotest.aktoerer.fprisk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.RisikovurderingKlient;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.dto.RisikovurderingResponse;
import no.nav.foreldrepenger.autotest.klienter.vtp.kafka.KafkaKlient;
import no.nav.foreldrepenger.autotest.util.vent.Vent;

public class Saksbehandler extends Aktoer {
    Logger LOG = LoggerFactory.getLogger(Saksbehandler.class);

    private KafkaKlient kafkaKlient;
    private RisikovurderingKlient risikovurderingKlient;

    public Saksbehandler(){
        kafkaKlient = new KafkaKlient(session);
        risikovurderingKlient = new RisikovurderingKlient(session);
    }

    public String getKafkaTopics() {
        return kafkaKlient.getKafkaTopics();
    }

    public String sendMessageToKafkaTopic(String topic, Object messageObject) {
        return kafkaKlient.putMessageOnKafkaTopic(topic, messageObject);
}

    public RisikovurderingResponse getRisikovurdering(String konsumentId) {
        return risikovurderingKlient.getRisikovurdering(konsumentId);
}

    /*
     * Behandlingsstatus
     */
    @Step("Venter til risikovurdering har status: {status}")
    public void ventTilRisikoKlassefiseringsstatus(String konsumentId, String status) {
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
