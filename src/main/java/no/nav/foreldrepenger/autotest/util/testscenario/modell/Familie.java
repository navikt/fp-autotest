package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioJerseyKlient;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.personopplysning.BrukerModell;

public class Familie {

    private static final TestscenarioJerseyKlient TESTSCENARIO_JERSEY_KLIENT = new TestscenarioJerseyKlient();

    private final TestscenarioDto scenario;

    public Familie(String scenarioId) {
        this.scenario = opprettTestscenario(scenarioId);
    }
    public Familie(Integer scenarioId) {
        this.scenario = opprettTestscenario(scenarioId.toString());
    }

    public Mor mor() {
        if (scenario.personopplysninger().søkerKjønn().equals(BrukerModell.Kjønn.K)) {
            return new Mor(new Fødselsnummer(scenario.personopplysninger().søkerIdent()),
                    BrukerRolle.MOR,
                    hentRelasjonerFor(new Fødselsnummer(scenario.personopplysninger().søkerIdent())),
                    scenario.scenariodataDto());
        } else if (scenario.personopplysninger().annenpartKjønn().equals(BrukerModell.Kjønn.K)) {
            return new Mor(new Fødselsnummer(scenario.personopplysninger().annenpartIdent()),
                    BrukerRolle.MOR,
                    hentRelasjonerFor(new Fødselsnummer(scenario.personopplysninger().annenpartIdent())),
                    scenario.scenariodataAnnenpartDto());
        } else {
            throw new IllegalStateException("Hverken søker eller annenpart er kvinne. Bruk metoden far()!");
        }
    }

    public Mor medmor() {
        if (scenario.personopplysninger().søkerKjønn().equals(BrukerModell.Kjønn.K) &&
                scenario.personopplysninger().annenpartKjønn().equals(BrukerModell.Kjønn.K)) {
            return new Mor(new Fødselsnummer(scenario.personopplysninger().annenpartIdent()),
                    BrukerRolle.MEDMOR,
                    hentRelasjonerFor(new Fødselsnummer(scenario.personopplysninger().annenpartIdent())),
                    scenario.scenariodataAnnenpartDto());
        } else {
            throw new IllegalStateException("Medmor eksistere ikke for scenarioid: Enten er søker eller annenpart far");
        }
    }

    public Far far() {
        if (scenario.personopplysninger().søkerKjønn().equals(BrukerModell.Kjønn.M)) {
            return new Far(new Fødselsnummer(scenario.personopplysninger().søkerIdent()),
                    BrukerRolle.FAR,
                    hentRelasjonerFor(new Fødselsnummer(scenario.personopplysninger().søkerIdent())),
                    scenario.scenariodataDto());
        } else if (scenario.personopplysninger().annenpartKjønn().equals(BrukerModell.Kjønn.M)) {
            return new Far(new Fødselsnummer(scenario.personopplysninger().annenpartIdent()),
                    BrukerRolle.FAR,
                    hentRelasjonerFor(new Fødselsnummer(scenario.personopplysninger().annenpartIdent())),
                    scenario.scenariodataAnnenpartDto());
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

    private TestscenarioDto opprettTestscenario(String id) {
        return TESTSCENARIO_JERSEY_KLIENT.opprettTestscenario(id);
    }

    private Relasjoner hentRelasjonerFor(Fødselsnummer fnr) {
        List<Relasjon> relasjoner = new ArrayList<>();
        var personopplysninger = scenario.personopplysninger();
        if (personopplysninger.søkerIdent().equalsIgnoreCase(fnr.toString())) {
            relasjoner.add(new Relasjon(new Fødselsnummer(personopplysninger.annenpartIdent()), RelasjonType.EKTEFELLE, null));
        } else {
            relasjoner.add(new Relasjon(new Fødselsnummer(personopplysninger.søkerIdent()), RelasjonType.EKTEFELLE, null));
        }
        if (personopplysninger.barnIdenter() != null && !personopplysninger.barnIdenter().isEmpty()) {
            for(String barnident : personopplysninger.barnIdenter()) {
                relasjoner.add(new Relasjon(new Fødselsnummer(barnident), RelasjonType.BARN, personopplysninger.fødselsdato()));
            }
        }
        return new Relasjoner(relasjoner);
    }

}
