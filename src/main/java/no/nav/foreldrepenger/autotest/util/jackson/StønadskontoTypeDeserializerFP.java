package no.nav.foreldrepenger.autotest.util.jackson;

import static no.nav.foreldrepenger.common.domain.serialization.JacksonUtils.fromString;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;

public class StønadskontoTypeDeserializerFP extends StdDeserializer<StønadskontoType> {
    public StønadskontoTypeDeserializerFP() {
        this(null);
    }

    public StønadskontoTypeDeserializerFP(Class<StønadskontoType> t) {
        super(t);
    }

    // TODO gjør dette på en bedre måte ...
    public StønadskontoType deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        JsonNode rootNode = p.getCodec().readTree(p);
        StønadskontoType stønadskontoType;
        if (rootNode instanceof TextNode t) {
            stønadskontoType = StønadskontoType.valueSafelyOf(t.asText());
        } else if (rootNode instanceof ObjectNode o) {
            stønadskontoType = StønadskontoType.valueSafelyOf(fromString(o));
        } else {
            throw new IllegalArgumentException("Ukjent node type [" + rootNode.getClass().getSimpleName() + "]");
        }
        if (stønadskontoType == null) {
            return StønadskontoType.IKKE_SATT;
        }
        return stønadskontoType;
    }
}
