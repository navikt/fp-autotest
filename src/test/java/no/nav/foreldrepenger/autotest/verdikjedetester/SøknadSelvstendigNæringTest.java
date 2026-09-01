package no.nav.foreldrepenger.autotest.verdikjedetester;

import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.OpptjeningMaler;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.soknad.kontrakt.opptjening.NæringDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.FamilierelasjonModellDto;

@Tag("verdikjede")
@Tag("foreldrepenger")
class SøknadSelvstendigNæringTest extends VerdikjedeTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(SøknadSelvstendigNæringTest.class);

    @Test
    @DisplayName("Sender foreldrepengesøknad med registrert og oppgitt selvstendig næring")
    @Description("Sender virksomheten fra Brreg-mocken i søkerinfo sammen med søkerens svar om egen næring.")
    void senderSøknadMedSelvstendigNæringPåNyttFormat() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().selvstendigNæringsdrivende(200_000).build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build();

        var mor = familie.mor();
        var oppgittNæring = OpptjeningMaler.registrertEgenNæring(
                "999999999",
                "VTP FISKE",
                NæringDto.Virksomhetstype.FISKE,
                mor.næringStartdato(),
                mor.næringsinntekt(),
                false);
        var søknad = lagSøknadForeldrepengerTerminFødsel(familie.barn().fødselsdato(), BrukerRolle.MOR)
                .medSelvstendigNæringsdrivendeInformasjon(oppgittNæring)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));

        var saksnummer = mor.søk(søknad);
        LOG.info("Søknad med selvstendig næring er sendt. Fødselsnummer: {}, saksnummer: {}",
                mor.fødselsnummer().value(), saksnummer.value());
    }

    @Test
    @DisplayName("Sender foreldrepengesøknad med manuelt oppgitt utenlandsk næring")
    @Description("Næringen finnes ikke i Enhetsregisteret, så navn, land og virksomhetstype er søkerens egne svar og skal vises i kvitteringen.")
    void senderSøknadMedUtenlandskNæringSomIkkeErForelagt() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().selvstendigNæringsdrivende(200_000).build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build();

        var mor = familie.mor();
        var oppgittNæring = OpptjeningMaler.utenlandskEgenNæring(
                "Utenlandsk Fiskeri AB",
                CountryCode.SE,
                NæringDto.Virksomhetstype.FISKE,
                mor.næringStartdato(),
                mor.næringsinntekt(),
                false);
        var søknad = lagSøknadForeldrepengerTerminFødsel(familie.barn().fødselsdato(), BrukerRolle.MOR)
                .medSelvstendigNæringsdrivendeInformasjon(oppgittNæring)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));

        var saksnummer = mor.søk(søknad);
        LOG.info("Søknad med utenlandsk næring er sendt. Fødselsnummer: {}, saksnummer: {}",
                mor.fødselsnummer().value(), saksnummer.value());
    }
}
