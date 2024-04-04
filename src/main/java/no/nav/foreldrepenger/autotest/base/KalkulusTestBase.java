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

    @Step("Oppretter testscenario {testInfo.testMethod} fra Json fil lokalisert i ftkalkulus-verdikjede")
    protected BeregnRequestDto opprettTestscenario(TestInfo testInfo) throws IOException {
        return testscenarioRepositoryImpl.hentScenario(testInfo, null);
    }

    @Step("Oppretter testscenario {testInfo.testMethod} fra Json fil lokalisert i ftkalkulus-verdikjede, prefix var {inputPrefix}")
    protected BeregnRequestDto opprettTestscenario(TestInfo testInfo, String inputPrefix) throws IOException {
        return testscenarioRepositoryImpl.hentScenario(testInfo, inputPrefix);
    }

    @Step("Oppretter testscenario {testInfo.testMethod} fra Json fil lokalisert i ftkalkulus-verdikjede, prefix var {inputPrefix}")
    protected BeregnRequestDto opprettTestscenario(TestInfo testInfo, String inputPrefix, BeregnRequestDto originalRequest) throws IOException {
        return testscenarioRepositoryImpl.hentScenario(testInfo, inputPrefix, originalRequest);
    }

    @Step("Validerer resultat for test")
    protected BeregningsgrunnlagGrunnlagDto hentForventetResultat(TestInfo testInfo) throws IOException {
        return testscenarioRepositoryImpl.hentForventetResultat(testInfo);
    }


    @Step("Validerer gui resultat for kofakber for test")
    protected BeregningsgrunnlagDto hentForventetGUIKofakber(TestInfo testInfo) throws IOException {
        return testscenarioRepositoryImpl.hentForventetGUIResultatKofakber(testInfo);
    }

    @Step("Validerer gui resultat for foreslå")
    protected BeregningsgrunnlagDto hentForventetGUIForeslå(TestInfo testInfo) throws IOException {
        return testscenarioRepositoryImpl.hentForventetGUIResultatForeslå(testInfo);
    }

    @Step("Validerer gui resultat for fordel")
    protected BeregningsgrunnlagDto hentForventetGUIFordel(TestInfo testInfo) throws IOException {
        return testscenarioRepositoryImpl.hentForventetGUIResultatFordel(testInfo);
    }


}
