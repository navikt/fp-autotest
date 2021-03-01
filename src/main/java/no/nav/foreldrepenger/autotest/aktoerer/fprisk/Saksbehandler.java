package no.nav.foreldrepenger.autotest.aktoerer.fprisk;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.RisikovurderingJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.dto.RisikovurderingResponse;
import no.nav.foreldrepenger.autotest.klienter.vtp.kafka.KafkaJerseyKlient;
import no.nav.foreldrepenger.autotest.util.vent.Vent;

public class Saksbehandler extends Aktoer {

    private final KafkaJerseyKlient kafkaKlient;
    private final RisikovurderingJerseyKlient risikovurderingKlient;

    public Saksbehandler(Rolle rolle) {
        super(rolle);
        kafkaKlient = new KafkaJerseyKlient();
        risikovurderingKlient = new RisikovurderingJerseyKlient(cookieRequestFilter);
    }

    public void sendMessageToKafkaTopic(String topic, Object messageObject) {
        kafkaKlient.putMessageOnKafkaTopic(topic, messageObject);
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
        LOG.info("Har status: {} ", responseDto.risikoklasse());
        return responseDto.risikoklasse().equalsIgnoreCase(status);
    }
}
