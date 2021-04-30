package no.nav.foreldrepenger.autotest.klienter.vtp.kafka;

import static jakarta.ws.rs.client.Entity.json;

import java.util.Optional;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPJerseyKlient;

public class KafkaJerseyKlient extends VTPJerseyKlient {

    private static final String KAFKA_URL = "/kafka";
    private static final String KAFKA_SEND_URL = KAFKA_URL + "/send/{topic}";

    public KafkaJerseyKlient() {
        super();
    }

    public void putMessageOnKafkaTopic(String topicName, Object messageObject) {
        client.target(base)
                .path(KAFKA_SEND_URL)
                .resolveTemplate("topic", Optional.ofNullable(topicName).orElseThrow(() -> new RuntimeException("Topic er null!")))
                .request()
                .post(json(messageObject), String.class);
    }
}
