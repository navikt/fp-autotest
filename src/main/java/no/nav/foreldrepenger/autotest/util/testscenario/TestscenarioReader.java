package no.nav.foreldrepenger.autotest.util.testscenario;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class TestscenarioReader {

    public static final String PERSONOPPLYSNING_JSON_FIL_NAVN = "personopplysning.json";
    public static final String INNTEKTYTELSE_SØKER_JSON_FIL_NAVN = "inntektytelse-søker.json";
    public static final String INNTEKTYTELSE_ANNENPART_JSON_FIL_NAVN = "inntektytelse-annenpart.json";
    public static final String ORGANISASJON_JSON_FIL_NAVN = "organisasjon.json";
    public static final String VARS_JSON_FIL_NAVN = "vars.json";

    private final ObjectMapper mapper;
    private final File rootDir;

    public TestscenarioReader() {
        mapper = new ObjectMapper();
        rootDir = new File("target/classes/scenarios");
    }

    public Object LesOgReturnerScenarioFraJsonfil(String scenarioNummer) {
        File scenarioFiles = hentScenarioFileneSomStarterMed(scenarioNummer);
        if (scenarioFiles == null) {
            System.out.println("Testscenario: [" + scenarioNummer + "] eksisterer ikke. ");
            return null;
        }

        final ObjectNode root = mapper.createObjectNode();
        lesFilOgLeggTilIObjectNode(scenarioFiles, root, PERSONOPPLYSNING_JSON_FIL_NAVN, "personopplysninger");
        lesFilOgLeggTilIObjectNode(scenarioFiles, root, INNTEKTYTELSE_SØKER_JSON_FIL_NAVN, "inntektytelse-søker");
        lesFilOgLeggTilIObjectNode(scenarioFiles, root, INNTEKTYTELSE_ANNENPART_JSON_FIL_NAVN, "inntektytelse-annenpart");
        lesFilOgLeggTilIObjectNode(scenarioFiles, root, ORGANISASJON_JSON_FIL_NAVN, "organisasjon");
        lesFilOgLeggTilIObjectNode(scenarioFiles, root, VARS_JSON_FIL_NAVN, "vars");

        return mapper.convertValue(root, new TypeReference<>(){});
    }


    private void lesFilOgLeggTilIObjectNode(File scenarioFiles, ObjectNode root, String jsonFilNavn, String navnPåNøkkel){
        try {
            File fil = hentFilSomMatcherStreng(scenarioFiles, jsonFilNavn);
            if (fil != null) {
                JsonNode verdiAvNøkkel = mapper.readValue(fil, JsonNode.class);
                root.set(navnPåNøkkel, verdiAvNøkkel);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Kunne ikke lese " + jsonFilNavn + "for scenario", e);
        }

    }

    private File hentFilSomMatcherStreng(File scenarioFiles, String FilNavnPåJsonFil) {
        File[] files = scenarioFiles.listFiles((dir, name) -> name.equalsIgnoreCase(FilNavnPåJsonFil));
        if (files.length > 0) {
            return files[0];
        }
        return null;
    }

    private File hentScenarioFileneSomStarterMed(String scenarioNummer) {
        File[] filesFiltered = rootDir.listFiles((dir, name) -> name.startsWith(scenarioNummer));
        if (filesFiltered.length > 0) {
            return filesFiltered[0];
        }
        return null;
    }
}
