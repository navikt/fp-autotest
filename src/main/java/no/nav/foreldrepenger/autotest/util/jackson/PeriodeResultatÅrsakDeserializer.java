package no.nav.foreldrepenger.autotest.util.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.IkkeOppfyltÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.InnvilgetÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;

public class PeriodeResultatÅrsakDeserializer extends StdDeserializer<PeriodeResultatÅrsak> {

    public PeriodeResultatÅrsakDeserializer() {
        super(PeriodeResultatÅrsak.class);
    }

    @Override
    public PeriodeResultatÅrsak deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = mapper.readTree(jp);

        if (root.has("kodeverk") && root.has("kode")) {
            var kode = root.get("kode").asText();
            var kodeverk = root.get("kodeverk").asText();
            if (kodeverk.equalsIgnoreCase(InnvilgetÅrsak.KODEVERK)) {
                return InnvilgetÅrsak.fraKode(kode);
            } else {
                return IkkeOppfyltÅrsak.fraKode(kode);
            }
        } else {
            return PeriodeResultatÅrsak.UKJENT;
        }
    }
}
