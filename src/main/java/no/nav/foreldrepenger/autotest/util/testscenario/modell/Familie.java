package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.autotest.aktoerer.innsender.SøknadMottak;
import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioKlient;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.personopplysning.BrukerModell;

public class Familie {

    private static final TestscenarioKlient TESTSCENARIO_JERSEY_KLIENT = new TestscenarioKlient();

    private final TestscenarioDto scenario;
    private final Innsender innsender;

    private Mor mor;
    private Far far;
    private Mor medmor;

    public Familie(String scenarioId) {
        this(scenarioId, new SøknadMottak());
    }

    public Familie(String scenarioId, Innsender innsender) {
        this(scenarioId, false, innsender);
    }

    public Familie(String scenarioId, boolean privatArbeidsgiver, Innsender innsender) {
        this.scenario = opprettTestscenario(scenarioId, privatArbeidsgiver);
        this.innsender = innsender;
    }

    public Mor mor() {
        if (mor == null) {
            if (scenario.personopplysninger().søkerKjønn().equals(BrukerModell.Kjønn.K)) {
                mor = new Mor(
                        new Fødselsnummer(scenario.personopplysninger().søkerIdent()),
                        new AktørId(scenario.personopplysninger().søkerAktørIdent()),
                        scenario.scenariodataDto(),
                        innsender);
                return mor;
            } else if (scenario.personopplysninger().annenpartKjønn().equals(BrukerModell.Kjønn.K)) {
                mor = new Mor(
                        new Fødselsnummer(scenario.personopplysninger().annenpartIdent()),
                        new AktørId(scenario.personopplysninger().annenpartAktørIdent()),
                        scenario.scenariodataAnnenpartDto(),
                        innsender);
                return mor;
            } else {
                throw new IllegalStateException("Hverken søker eller annenpart er kvinne. Bruk metoden far()!");
            }
        } else {
            return mor;
        }

    }

    public Mor medmor() {
        if (medmor == null) {
            if (scenario.personopplysninger().søkerKjønn().equals(BrukerModell.Kjønn.K) &&
                    scenario.personopplysninger().annenpartKjønn().equals(BrukerModell.Kjønn.K)) {
                medmor = new Mor(
                        new Fødselsnummer(scenario.personopplysninger().annenpartIdent()),
                        new AktørId(scenario.personopplysninger().annenpartAktørIdent()),
                        scenario.scenariodataAnnenpartDto(),
                        innsender);
                return medmor;
            } else {
                throw new IllegalStateException("Medmor eksistere ikke for scenarioid: Enten er søker eller annenpart far");
            }
        } else {
            return medmor;
        }
    }

    public Far far() {
        if (far == null) {
            if (scenario.personopplysninger().søkerKjønn().equals(BrukerModell.Kjønn.M)) {
                far = new Far(
                        new Fødselsnummer(scenario.personopplysninger().søkerIdent()),
                        new AktørId(scenario.personopplysninger().søkerAktørIdent()),
                        scenario.scenariodataDto(),
                        innsender);
                return far;
            } else if (scenario.personopplysninger().annenpartKjønn().equals(BrukerModell.Kjønn.M)) {
                far = new Far(
                        new Fødselsnummer(scenario.personopplysninger().annenpartIdent()),
                        new AktørId(scenario.personopplysninger().annenpartAktørIdent()),
                        scenario.scenariodataAnnenpartDto(),
                        innsender);
                return far;
            } else {
                throw new IllegalStateException("Hverken søker eller annenpart er mann: Bruk mor() eller medmor()");
            }
        } else {
            return far;
        }
    }

    public Barn barn() {
        if (scenario.personopplysninger().barnIdenter().isEmpty()) {
            throw new IllegalStateException("Barn er enda ikke født for familie");
        }
        return new Barn(scenario.personopplysninger().fødselsdato());
    }

    private TestscenarioDto opprettTestscenario(String id, boolean privatArbeidsgiver) {
        if (privatArbeidsgiver) {
            return opprettScenarioMedPrivatArbeidsgiver(id);
        }
        return TESTSCENARIO_JERSEY_KLIENT.opprettTestscenario(id);
    }

    private TestscenarioDto opprettScenarioMedPrivatArbeidsgiver(String id) {
        var arbeidsgiverScenario = TESTSCENARIO_JERSEY_KLIENT.opprettTestscenario("59"); // TODO: ok med Hardkodet?
        var fnrArbeidsgiver = arbeidsgiverScenario.personopplysninger().søkerIdent();
        var arbeidsgiverAktørId = arbeidsgiverScenario.personopplysninger().søkerAktørIdent();
        return TESTSCENARIO_JERSEY_KLIENT.opprettTestscenarioMedAktorId(id, arbeidsgiverAktørId, fnrArbeidsgiver);
    }
}
