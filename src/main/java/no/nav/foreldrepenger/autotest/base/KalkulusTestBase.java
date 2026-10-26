package no.nav.foreldrepenger.autotest.base;

import java.io.IOException;
import java.lang.reflect.Method;

import no.nav.folketrygdloven.kalkulus.response.beregningsgrunnlag.gui.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.EnkelBeregnRequestDto;

import no.nav.foreldrepenger.kalkulus.kontrakt.response.beregningsgrunnlag.detaljert.BeregningsgrunnlagGrunnlagDto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;

import io.qameta.allure.Allure;
import no.nav.foreldrepenger.autotest.klienter.fpkalkulus.KalkulusKlient;
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

    protected EnkelBeregnRequestDto opprettTestscenario(TestInfo testInfo) throws IOException {
        return Allure.step("Oppretter testscenario " + testInfo.getTestMethod().map(Method::getName).orElse("") + " fra Json fil lokalisert i ftkalkulus-verdikjede", () -> testscenarioRepositoryImpl.hentScenario(testInfo, null));
    }

    protected EnkelBeregnRequestDto opprettTestscenario(TestInfo testInfo, String inputPrefix) throws IOException {
        return Allure.step("Oppretter testscenario " + testInfo.getTestMethod().map(Method::getName).orElse("") + " fra Json fil lokalisert i ftkalkulus-verdikjede, prefix var " + inputPrefix, () -> testscenarioRepositoryImpl.hentScenario(testInfo, inputPrefix));
    }

    protected EnkelBeregnRequestDto opprettTestscenario(TestInfo testInfo, String inputPrefix, EnkelBeregnRequestDto originalRequest) throws IOException {
        return Allure.step("Oppretter testscenario " + testInfo.getTestMethod().map(Method::getName).orElse("") + " fra Json fil lokalisert i ftkalkulus-verdikjede, prefix var " + inputPrefix, () -> testscenarioRepositoryImpl.hentScenario(testInfo, inputPrefix, originalRequest));
    }

    protected BeregningsgrunnlagGrunnlagDto hentForventetResultat(TestInfo testInfo) throws IOException {
        return Allure.step("Validerer resultat for test", () -> testscenarioRepositoryImpl.hentForventetResultat(testInfo));
    }


    protected BeregningsgrunnlagDto hentForventetGUIKofakber(TestInfo testInfo) throws IOException {
        return Allure.step("Validerer gui resultat for kofakber for test", () -> testscenarioRepositoryImpl.hentForventetGUIResultatKofakber(testInfo));
    }

    protected BeregningsgrunnlagDto hentForventetGUIForeslå(TestInfo testInfo) throws IOException {
        return Allure.step("Validerer gui resultat for foreslå", () -> testscenarioRepositoryImpl.hentForventetGUIResultatForeslå(testInfo));
    }

    protected BeregningsgrunnlagDto hentForventetGUIFordel(TestInfo testInfo) throws IOException {
        return Allure.step("Validerer gui resultat for fordel", () -> testscenarioRepositoryImpl.hentForventetGUIResultatFordel(testInfo));
    }


}
