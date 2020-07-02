package no.nav.foreldrepenger.autotest.klienter.vtp.kafka;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

public class KafkaKlient extends VTPKlient {

    private static final String KAFKA_URL = "/kafka";
    private static final String KAFKA_GET_TOPICS_URL = KAFKA_URL + "/topics";
    private static final String KAFKA_SEND_URL = KAFKA_URL + "/send/%s";

    public KafkaKlient(HttpSession session) {
        super(session);
    }

    public String getKafkaTopics() {
        String url = hentRestRotUrl() + KAFKA_GET_TOPICS_URL;
        List<Topic> topics = getOgHentJson(url,
                hentObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, Topic.class),
                StatusRange.STATUS_SUCCESS);
        String result = "";
        for (Topic topic : topics) {
            result += String.format("%s : %s\n", topic.getName(), topic.getInternal());
        }
        return result;
    }

    public String putMessageOnKafkaTopic(String topicName, Object messageObject) {
        String url = hentRestRotUrl() + String.format(KAFKA_SEND_URL, topicName);
        return postOgVerifiser(url, messageObject, StatusRange.STATUS_SUCCESS);
    }

    public static class Topic {
        protected String name;
        protected String internal;

        public String getName() {
            return name;
        }

        public String getInternal() {
            return internal;
        }
    }

}
