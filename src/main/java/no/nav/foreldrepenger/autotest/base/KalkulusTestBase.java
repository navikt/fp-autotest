package no.nav.foreldrepenger.autotest.base;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;

import io.qameta.allure.Step;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.BeregnRequestDto;
import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.detaljert.BeregningsgrunnlagGrunnlagDto;
import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.gui.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.autotest.klienter.kalkulus.KalkulusKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.generator.kalkulus.TestscenarioRepositoryImpl;

public class KalkulusTestBase {

    protected static TestscenarioRepositoryImpl testscenarioRepositoryImpl;
    protected KalkulusKlient saksbehandler = new KalkulusKlient(SaksbehandlerRolle.SAKSBEHANDLER);
    protected KalkulusKlient overstyrer = new KalkulusKlient(SaksbehandlerRolle.OVERSTYRER);

    @BeforeAll
    protected static void setUpAll() {
        testscenarioRepositoryImpl = new TestscenarioRepositoryImpl();
    }

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i ftkalkulus-verdikjede")
    protected BeregnRequestDto opprettTestscenario(String id) throws IOException {
        return testscenarioRepositoryImpl.hentScenario(id, null);
    }

    @Step("Oppretter testscenario {mappeId} fra Json fil lokalisert i ftkalkulus-verdikjede, prefix var {inputPrefix}")
    protected BeregnRequestDto opprettTestscenario(String mappeId, String inputPrefix) throws IOException {
        return testscenarioRepositoryImpl.hentScenario(mappeId, inputPrefix);
    }

    @Step("Oppretter testscenario {mappeId} fra Json fil lokalisert i ftkalkulus-verdikjede, prefix var {inputPrefix}")
    protected BeregnRequestDto opprettTestscenario(String mappeId, String inputPrefix, BeregnRequestDto originalRequest) throws IOException {
        return testscenarioRepositoryImpl.hentScenario(mappeId, inputPrefix, originalRequest);
    }


    @Step("Validerer resultat for test")
    protected BeregningsgrunnlagGrunnlagDto hentForventetResultat(TestInfo testInfo) throws IOException {
        String testName = getTestName(testInfo);
        return testscenarioRepositoryImpl.hentForventetResultat(testName);
    }


    @Step("Validerer gui resultat for kofakber for test")
    protected BeregningsgrunnlagDto hentForventetGUIKofakber(TestInfo testInfo) throws IOException {
        String testName = getTestName(testInfo);
        return testscenarioRepositoryImpl.hentForventetGUIResultatKofakber(testName);
    }

    @Step("Validerer gui resultat for foreslå")
    protected BeregningsgrunnlagDto hentForventetGUIForeslå(TestInfo testInfo) throws IOException {
        String testName = getTestName(testInfo);
        return testscenarioRepositoryImpl.hentForventetGUIResultatForeslå(testName);
    }

    @Step("Validerer gui resultat for fordel")
    protected BeregningsgrunnlagDto hentForventetGUIFordel(TestInfo testInfo) throws IOException {
        String testName = getTestName(testInfo);
        return testscenarioRepositoryImpl.hentForventetGUIResultatFordel(testName);
    }

    private String getTestName(TestInfo testInfo) {
        return testInfo.getTestMethod().orElseThrow(() -> new IllegalArgumentException("Forventer testmetode")).getName();
    }
}
