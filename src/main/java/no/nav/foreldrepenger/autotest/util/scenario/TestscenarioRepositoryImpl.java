package no.nav.foreldrepenger.autotest.util.scenario;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class TestscenarioRepositoryImpl {

    public static final String PERSONOPPLYSNING_JSON_FIL_NAVN = "personopplysning.json";
    public static final String INNTEKTYTELSE_SØKER_JSON_FIL_NAVN = "inntektytelse-søker.json";
    public static final String INNTEKTYTELSE_ANNENPART_JSON_FIL_NAVN = "inntektytelse-annenpart.json";
    public static final String ORGANISASJON_JSON_FIL_NAVN = "organisasjon.json";
    public static final String VARS_JSON_FIL_NAVN = "vars.json";

    private final Map<String, Object> scenarioObjects = new TreeMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final File rootDir = new File("target/classes/scenarios");

    public TestscenarioRepositoryImpl() {
    }

    public Collection<Object> hentAlleScenarioer() {
        return scenarioObjects.values();
    }

    public Object hentScenario(String scenarioId) {
        if (scenarioObjects.containsKey(scenarioId)) {
            return scenarioObjects.get(scenarioId);
        }
        return LesOgReturnerScenarioFraJsonfil(scenarioId);
    }


    private Object LesOgReturnerScenarioFraJsonfil(String scenarioId) {
        File scenarioFiles = hentScenarioFileneSomStarterMed(scenarioId);
        if (scenarioFiles == null) {
            System.out.println("Testscenario: [" + scenarioId + "] eksisterer ikke. ");
            // scenarioFiles.exists()
            // throw new FileNotFoundException("Fant ikke scenario med scenario nummer [" + scenarioId + "]");
            return null;
        }

        final ObjectNode root = mapper.createObjectNode();
        lesFilOgLeggTilIObjectNode(scenarioFiles, root, PERSONOPPLYSNING_JSON_FIL_NAVN, "personopplysninger");
        lesFilOgLeggTilIObjectNode(scenarioFiles, root, INNTEKTYTELSE_SØKER_JSON_FIL_NAVN, "inntektytelse-søker");
        lesFilOgLeggTilIObjectNode(scenarioFiles, root, INNTEKTYTELSE_ANNENPART_JSON_FIL_NAVN, "inntektytelse-annenpart");
        lesFilOgLeggTilIObjectNode(scenarioFiles, root, ORGANISASJON_JSON_FIL_NAVN, "organisasjon");
        lesFilOgLeggTilIObjectNode(scenarioFiles, root, VARS_JSON_FIL_NAVN, "vars");

        Object obj = mapper.convertValue(root, new TypeReference<>(){});
        scenarioObjects.put(scenarioId, obj);
        return obj;
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
