package no.nav.foreldrepenger.generator.familie.generator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType;
import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioKlient;
import no.nav.foreldrepenger.generator.familie.Familie;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.InntektYtelseModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PersonDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PrivatArbeidsgiver;
import no.nav.foreldrepenger.vtp.kontrakter.v2.Rolle;
import no.nav.foreldrepenger.vtp.kontrakter.v2.SivilstandDto;


public class FamilieGenerator {

    private static final TestscenarioKlient TESTSCENARIO_JERSEY_KLIENT = new TestscenarioKlient();

    private final List<PersonDto> parter = new ArrayList<>();

    public static FamilieGenerator ny() {
        return new FamilieGenerator();
    }

    public FamilieGenerator forelder(PersonDto person) {
        this.parter.add(person);
        return this;
    }


    public FamilieGenerator barn(LocalDate fødselsdato) {
        var barn = PersonDto.builder()
                .rolle(Rolle.BARN)
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

        foreldre.get(0).familierelasjoner().add(new FamilierelasjonModellDto(relasjon, foreldre.get(1).id()));
        foreldre.get(1).familierelasjoner().add(new FamilierelasjonModellDto(relasjon, foreldre.get(0).id()));
        if (Objects.requireNonNull(relasjon) == FamilierelasjonModellDto.Relasjon.EKTE) {
            foreldre.forEach(f -> f.sivilstand().add(new SivilstandDto(SivilstandDto.Sivilstander.GIFT, LocalDate.now().minusYears(4), null)));
        } else if (relasjon == FamilierelasjonModellDto.Relasjon.SAMBOER) {
            foreldre.forEach(f -> f.sivilstand().add(new SivilstandDto(SivilstandDto.Sivilstander.SAMB, null, null)));
        }

        return this;
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
                barnet.familierelasjoner().add(new FamilierelasjonModellDto(barnetsRelasjonTilForelder, forelder.id()));
                forelder.familierelasjoner().add(new FamilierelasjonModellDto(FamilierelasjonModellDto.Relasjon.BARN, barnet.id()));

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
                .rolle(Rolle.PRIVATE_ARBEIDSGIVER)
                .fødselsdato(LocalDate.now().minusYears(40))
                .id(p.uuid())
                .build();
    }

    public Familie build() {
        return build(InnsenderType.SEND_DOKUMENTER_MED_SELVBETJENING);
    }
    public Familie build(InnsenderType innsenderType) {
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
        var testscenarioDto = TESTSCENARIO_JERSEY_KLIENT.opprettTestscenario(parter);
        return new Familie(testscenarioDto, innsenderType);
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
