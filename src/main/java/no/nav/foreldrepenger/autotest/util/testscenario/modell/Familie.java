package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioJerseyKlient;
import no.nav.foreldrepenger.autotest.util.testscenario.TestscenarioHenter;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.personopplysning.BrukerModell;

public class Familie {

    private static final TestscenarioHenter TESTSCENARIO_HENTER = new TestscenarioHenter();
    private static final TestscenarioJerseyKlient TESTSCENARIO_JERSEY_KLIENT = new TestscenarioJerseyKlient();

    private final TestscenarioDto scenario;

    public Familie(String scenarioId) {
        this.scenario = opprettTestscenario(scenarioId);
    }

    public Mor mor() {
        if (scenario.personopplysninger().søkerKjønn().equals(BrukerModell.Kjønn.K)) {
            return new Mor(scenario.personopplysninger().søkerIdent(), scenario.scenariodataDto());
        } else if (scenario.personopplysninger().annenpartKjønn().equals(BrukerModell.Kjønn.K)) {
            return new Mor(scenario.personopplysninger().annenpartIdent(), scenario.scenariodataAnnenpartDto());
        } else {
            throw new IllegalStateException("Hverken søker eller annenpart er kvinne. Bruk metoden far()!");
        }
    }

    public Mor medmor() {
        if (scenario.personopplysninger().søkerKjønn().equals(BrukerModell.Kjønn.K) &&
                scenario.personopplysninger().annenpartKjønn().equals(BrukerModell.Kjønn.K)) {
            return new Mor(scenario.personopplysninger().annenpartIdent(), scenario.scenariodataAnnenpartDto());
        } else {
            throw new IllegalStateException("Medmor eksistere ikke for scenarioid: Enten er søker eller annenpart far");
        }
    }

    public Far far() {
        if (scenario.personopplysninger().søkerKjønn().equals(BrukerModell.Kjønn.M)) {
            return new Far(scenario.personopplysninger().søkerIdent(), scenario.scenariodataDto());
        } else if (scenario.personopplysninger().annenpartKjønn().equals(BrukerModell.Kjønn.M)) {
            return new Far(scenario.personopplysninger().annenpartIdent(), scenario.scenariodataAnnenpartDto());
        } else {
            throw new IllegalStateException("Hverken søker eller annenpart er mann: Bruk mor() eller medmor()");
        }
    }

    public Barn barn() {
        if (scenario.personopplysninger().barnIdenter().isEmpty()) {
            throw new IllegalStateException("Barn er enda ikke født for familie");
        }
        return new Barn(scenario.personopplysninger().fødselsdato());
    }


    @Step("Oppretter testscenario {id} fra Json fil lokalisert i Autotest")
    private TestscenarioDto opprettTestscenario(String id) {
        var testscenarioObject = TESTSCENARIO_HENTER.hentScenario(id);
        return TESTSCENARIO_JERSEY_KLIENT.opprettTestscenario(id, testscenarioObject);
    }
}
