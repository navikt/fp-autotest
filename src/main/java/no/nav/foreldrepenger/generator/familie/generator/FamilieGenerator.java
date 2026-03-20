package no.nav.foreldrepenger.generator.familie.generator;

import static no.nav.foreldrepenger.generator.familie.generator.PersonopplysningMaler.bosattFra;
import static no.nav.foreldrepenger.generator.familie.generator.PersonopplysningMaler.norskStatsborgerskap;
import static no.nav.foreldrepenger.generator.familie.generator.PersonopplysningMaler.ugift;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioKlient;
import no.nav.foreldrepenger.autotest.util.log.LoggFormater;
import no.nav.foreldrepenger.generator.familie.Familie;
import no.nav.foreldrepenger.vtp.kontrakter.person.PersonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.arbeidsforhold.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.arbeidsforhold.PrivatArbeidsgiverDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.FamilierelasjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.Kjønn;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.PersonopplysningerDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.Rolle;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.SivilstandDto;
import no.nav.vedtak.log.mdc.MDCOperations;


public class FamilieGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(FamilieGenerator.class);

    private static final TestscenarioKlient TESTSCENARIO_JERSEY_KLIENT = new TestscenarioKlient();

    private final List<PersonDto> parter = new ArrayList<>();
    private SaksbehandlerRolle saksbehandlerRolle;

    public FamilieGenerator() {
        saksbehandlerRolle = SaksbehandlerRolle.SAKSBEHANDLER;
    }

    public FamilieGenerator(SaksbehandlerRolle saksbehandlerRolle) {
        this.saksbehandlerRolle = saksbehandlerRolle;
    }

    public static FamilieGenerator ny() {
        return new FamilieGenerator(SaksbehandlerRolle.SAKSBEHANDLER);
    }

    public static FamilieGenerator ny(SaksbehandlerRolle saksbehandlerRolle) {
        return new FamilieGenerator(saksbehandlerRolle);
    }

    public FamilieGenerator forelder(PersonDto person) {
        this.parter.add(person);
        return this;
    }


    public FamilieGenerator barn(LocalDate fødselsdato) {
        var personopplysninger = PersonopplysningerDto.builder()
                .medRolle(Rolle.BARN)
                .medKjønn(Kjønn.K)
                .medAdresser(foreldrene().stream().findFirst().map(p -> p.personopplysninger().adresser()).orElse(null))
                .medPersonstatus(bosattFra(fødselsdato))
                .medSivilstand(ugift())
                .medStatsborgerskap(norskStatsborgerskap())
                .medFødselsdato(fødselsdato)
                .build();
        var barn = PersonDto.builder()
                .medPersonopplysninger(personopplysninger)
                .build();
        this.parter.add(barn);
        return this;
    }

    public FamilieGenerator relasjonForeldre(FamilierelasjonDto.Relasjon relasjon) {
        var foreldre = foreldrene();
        if (foreldre.size() != 2) {
            throw new IllegalStateException("Du må instansiere 2 foreldre!");
        }

        foreldre.get(0).personopplysninger().familierelasjoner().add(new FamilierelasjonDto(relasjon, foreldre.get(1).personopplysninger().fnr()));
        foreldre.get(1).personopplysninger().familierelasjoner().add(new FamilierelasjonDto(relasjon, foreldre.get(0).personopplysninger().fnr()));
        if (Objects.requireNonNull(relasjon) == FamilierelasjonDto.Relasjon.EKTE) {
            foreldre.forEach(f -> oppdaterSivilstand(f, SivilstandDto.Type.GIFT));
        } else if (relasjon == FamilierelasjonDto.Relasjon.SAMBOER) {
            foreldre.forEach(f -> oppdaterSivilstand(f, SivilstandDto.Type.GIFT));
        }

        return this;
    }

    private static void oppdaterSivilstand(PersonDto f, SivilstandDto.Type sivilstand) {
        f.personopplysninger().sivilstand().removeIf(s -> SivilstandDto.Type.UGIFT.equals(s.sivilstand()));
        f.personopplysninger().sivilstand().add(new SivilstandDto(sivilstand, LocalDate.now().minusYears(4), null));
    }

    private void opprettFamilieRelasjonForFødteBarn() {
        var barnene = barnene();
        for (var barnet : barnene) {
            var foreldrene = foreldrene();
            for (var forelder : foreldrene) {
                var barnetsRelasjonTilForelder = switch (forelder.personopplysninger().rolle()) {
                    case MOR -> FamilierelasjonDto.Relasjon.MOR;
                    case MEDMOR -> FamilierelasjonDto.Relasjon.MEDMOR;
                    case FAR, MEDFAR -> FamilierelasjonDto.Relasjon.FAR;
                    default -> throw new IllegalStateException("Unexpected value: " + forelder.personopplysninger().rolle());
                };
                barnet.personopplysninger().familierelasjoner().add(new FamilierelasjonDto(barnetsRelasjonTilForelder, forelder.personopplysninger().fnr()));
                forelder.personopplysninger().familierelasjoner().add(new FamilierelasjonDto(FamilierelasjonDto.Relasjon.BARN, barnet.personopplysninger().fnr()));

            }
        }
    }

    private List<PersonDto> foreldrene() {
        return this.parter.stream().filter(p -> Set.of(Rolle.MOR, Rolle.FAR, Rolle.MEDMOR, Rolle.MEDFAR).contains(p.personopplysninger().rolle())).toList();
    }

    private List<PersonDto> barnene() {
        return parter.stream().filter(personDto -> Rolle.BARN.equals(personDto.personopplysninger().rolle())).toList();
    }

    private static PersonDto privatArbeidsgiver(PrivatArbeidsgiverDto p) {
        var personopplysninger = PersonopplysningerDto.builder()
                .medFnr(p.fnr())
                .medRolle(Rolle.PRIVATE_ARBEIDSGIVER)
                .medFødselsdato(LocalDate.now().minusYears(40))
                .build();
        return PersonDto.builder()
                .medPersonopplysninger(personopplysninger)
                .build();
    }

    public Familie build() {
        MDCOperations.putCallId();
        LOG.debug("Testcase: {}", LoggFormater.navnPåTestCaseSomKjører());
        guardMinstEnPart();
        guardForeldresammensetning();
        opprettFamilieRelasjonForFødteBarn();
        var privateArbeidsgiver = parter.stream()
                .flatMap(p -> p.arbeidsforhold().stream())
                .map(ArbeidsforholdDto::arbeidsgiver)
                .filter(PrivatArbeidsgiverDto.class::isInstance)
                .map(PrivatArbeidsgiverDto.class::cast)
                .collect(Collectors.toSet());
        privateArbeidsgiver.forEach(p -> parter.add(privatArbeidsgiver(p)));
        TESTSCENARIO_JERSEY_KLIENT.opprettTestscenario(parter);
        return new Familie(parter, saksbehandlerRolle);
    }

    private void guardForeldresammensetning() {
        if (parter.stream().noneMatch(p -> Set.of(Rolle.MOR, Rolle.FAR).contains(p.personopplysninger().rolle()))) {
            throw new IllegalStateException("Familien må inneholde enten MOR eller FAR");
        }
        if (parter.stream().filter(p -> p.personopplysninger().rolle().equals(Rolle.MOR)).count() > 1 ||
                parter.stream().filter(p -> p.personopplysninger().rolle().equals(Rolle.FAR)).count() > 1) {
            throw new IllegalStateException("Annenpart må være enten MEDFAR eller MEDMOR");
        }
    }

    private void guardMinstEnPart() {
        if (parter.isEmpty()) {
            throw new IllegalStateException("Må være minst en part!");
        }
    }


}
