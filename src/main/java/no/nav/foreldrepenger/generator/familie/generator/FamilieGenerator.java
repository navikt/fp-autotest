package no.nav.foreldrepenger.generator.familie.generator;

import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.bosattFra;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.norskStatsborgerskap;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.ugift;

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
import no.nav.foreldrepenger.vtp.kontrakter.person.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.InntektYtelseModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.Kjønn;
import no.nav.foreldrepenger.vtp.kontrakter.person.PersonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.PrivatArbeidsgiver;
import no.nav.foreldrepenger.vtp.kontrakter.person.Rolle;
import no.nav.foreldrepenger.vtp.kontrakter.person.SivilstandDto;
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
        var barn = PersonDto.builder()
                .rolle(Rolle.BARN)
                .kjønn(Kjønn.K)
                .adresser(foreldrene().stream().findFirst().map(PersonDto::adresser).orElse(List.of()))
                .personstatus(bosattFra(fødselsdato))
                .sivilstand(ugift())
                .statsborgerskap(norskStatsborgerskap())
                .fødselsdato(fødselsdato)
                .build();
        this.parter.add(barn);
        return this;
    }

    public FamilieGenerator relasjonForeldre(FamilierelasjonModellDto.Relasjon relasjon) {
        var foreldre = foreldrene();
        if (foreldre.size() != 2) {
            throw new IllegalStateException("Du må instansiere 2 foreldre!");
        }

        foreldre.get(0).familierelasjoner().add(new FamilierelasjonModellDto(relasjon, foreldre.get(1).uuid()));
        foreldre.get(1).familierelasjoner().add(new FamilierelasjonModellDto(relasjon, foreldre.get(0).uuid()));
        if (Objects.requireNonNull(relasjon) == FamilierelasjonModellDto.Relasjon.EKTE) {
            foreldre.forEach(f -> oppdaterSivilstand(f, SivilstandDto.Sivilstander.GIFT));
        } else if (relasjon == FamilierelasjonModellDto.Relasjon.SAMBOER) {
            foreldre.forEach(f -> oppdaterSivilstand(f, SivilstandDto.Sivilstander.SAMB));
        }

        return this;
    }

    private static void oppdaterSivilstand(PersonDto f, SivilstandDto.Sivilstander sivilstand) {
        f.sivilstand().removeIf(s -> SivilstandDto.Sivilstander.UGIF.equals(s.sivilstand()));
        f.sivilstand().add(new SivilstandDto(sivilstand, LocalDate.now().minusYears(4), null));
    }

    private void opprettFamilieRelasjonForFødteBarn() {
        var barnene = barnene();
        for (var barnet : barnene) {
            var foreldrene = foreldrene();
            for (var forelder : foreldrene) {
                var barnetsRelasjonTilForelder = switch (forelder.rolle()) {
                    case MOR -> FamilierelasjonModellDto.Relasjon.MOR;
                    case MEDMOR -> FamilierelasjonModellDto.Relasjon.MEDMOR;
                    case FAR, MEDFAR -> FamilierelasjonModellDto.Relasjon.FAR;
                    default -> throw new IllegalStateException("Unexpected value: " + forelder.rolle());
                };
                barnet.familierelasjoner().add(new FamilierelasjonModellDto(barnetsRelasjonTilForelder, forelder.uuid()));
                forelder.familierelasjoner().add(new FamilierelasjonModellDto(FamilierelasjonModellDto.Relasjon.BARN, barnet.uuid()));

            }
        }
    }

    private List<PersonDto> foreldrene() {
        return this.parter.stream().filter(p -> Set.of(Rolle.MOR, Rolle.FAR, Rolle.MEDMOR, Rolle.MEDFAR).contains(p.rolle())).toList();
    }

    private List<PersonDto> barnene() {
        return parter.stream().filter(personDto -> Rolle.BARN.equals(personDto.rolle())).toList();
    }

    private static PersonDto privatArbeidsgiver(PrivatArbeidsgiver p) {
        return PersonDto.builder()
                .uuid(p.uuid())
                .rolle(Rolle.PRIVATE_ARBEIDSGIVER)
                .fødselsdato(LocalDate.now().minusYears(40))
                .build();
    }

    public Familie build() {
        MDCOperations.putCallId();
        LOG.debug("Testcase: {}", LoggFormater.navnPåTestCaseSomKjører());
        guardMinstEnPart();
        guardForeldresammensetning();
        opprettFamilieRelasjonForFødteBarn();
        var privateArbeidsgiver = parter.stream()
                .map(PersonDto::inntektytelse)
                .filter(Objects::nonNull)
                .map(InntektYtelseModellDto::aareg)
                .filter(Objects::nonNull)
                .flatMap(p -> p.arbeidsforhold().stream())
                .map(ArbeidsforholdDto::arbeidsgiver)
                .filter(PrivatArbeidsgiver.class::isInstance)
                .map(PrivatArbeidsgiver.class::cast)
                .collect(Collectors.toSet());
        privateArbeidsgiver.forEach(p -> parter.add(privatArbeidsgiver(p)));
        var identer = TESTSCENARIO_JERSEY_KLIENT.opprettTestscenario(parter);
        return new Familie(parter, identer, saksbehandlerRolle);
    }

    private void guardForeldresammensetning() {
        if (parter.stream().noneMatch(p -> Set.of(Rolle.MOR, Rolle.FAR).contains(p.rolle()))) {
            throw new IllegalStateException("Familien må inneholde enten MOR eller FAR");
        }
        if (parter.stream().filter(p -> p.rolle().equals(Rolle.MOR)).count() > 1 ||
                parter.stream().filter(p -> p.rolle().equals(Rolle.FAR)).count() > 1) {
            throw new IllegalStateException("Annenpart må være enten MEDFAR eller MEDMOR");
        }
    }

    private void guardMinstEnPart() {
        if (parter.isEmpty()) {
            throw new IllegalStateException("Må være minst en part!");
        }
    }


}
