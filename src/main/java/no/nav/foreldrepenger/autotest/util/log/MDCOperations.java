package no.nav.foreldrepenger.autotest.util.log;

import java.util.Objects;
import java.util.Random;

import javax.xml.namespace.QName;

import org.slf4j.MDC;

public final class MDCOperations {
    public static final String HTTP_HEADER_CALL_ID = "Nav-Callid";
    public static final String HTTP_HEADER_CONSUMER_ID = "Nav-Consumer-Id";
    public static final String MDC_CALL_ID = "callId";
    public static final String MDC_USER_ID = "userId";
    public static final String MDC_CONSUMER_ID = "consumerId";
    public static final QName CALLID_QNAME = new QName("uri:no.nav.applikasjonsrammeverk", "callId");
    private static final Random RANDOM = new Random();

    private MDCOperations() {
    }

    public static void putCallId() {
        putCallId(generateCallId());
    }

    public static void putCallId(String callId) {
        Objects.requireNonNull(callId, "callId can't be null");
        MDC.put("callId", callId);
    }

    public static String getCallId() {
        return MDC.get("callId");
    }

    public static void removeCallId() {
        remove("callId");
    }

    public static void putConsumerId(String consumerId) {
        Objects.requireNonNull(consumerId, "consumerId can't be null");
        MDC.put("consumerId", consumerId);
    }

    public static String getConsumerId() {
        return MDC.get("consumerId");
    }

    public static void removeConsumerId() {
        remove("consumerId");
    }

    public static void putUserId(String userId) {
        Objects.requireNonNull(userId, "userId can't be null");
        MDC.put("userId", userId);
    }

    public static String getUserId() {
        return MDC.get("userId");
    }

    public static void removeUserId() {
        remove("userId");
    }

    public static String generateCallId() {
        int randomNr = RANDOM.nextInt(2147483647);
        long systemTime = System.currentTimeMillis();
        StringBuilder callId = (new StringBuilder("CallId_")).append(systemTime).append('_').append(randomNr);
        return callId.toString();
    }

    public static String getFromMDC(String key) {
        String value = MDC.get(key);
        return value;
    }

    public static void putToMDC(String key, String value) {
        MDC.put(key, value);
    }

    public static void remove(String key) {
        MDC.remove(key);
    }
}
