package no.nav.foreldrepenger.autotest.base;

import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EndringssøknadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.erketyper.MedlemskapErketyper;
import no.nav.foreldrepenger.autotest.erketyper.RelasjonTilBarnetErketyper;
import no.nav.foreldrepenger.autotest.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.UkjentForelder;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Oppholdsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Uttaksperiode;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ForeldrepengerTestBase extends FpsakTestBase {


    protected ForeldrepengerBuilder lagSøknadForeldrepenger(LocalDate familiehendelse, String søkerAktørId, SøkersRolle søkersRolle) {
        return new ForeldrepengerBuilder(søkerAktørId, søkersRolle)
                .medFordeling(FordelingErketyper.fordelingHappyCase(familiehendelse, søkersRolle))
                .medDekningsgrad("100")
                .medMedlemskap(MedlemskapErketyper.medlemskapNorge())
                .medRettigheter(RettigheterErketyper.beggeForeldreRettIkkeAleneomsorg())
                .medAnnenForelder(new UkjentForelder());
    }
    protected ForeldrepengerBuilder lagSøknadForeldrepengerTermin(LocalDate termindato, String søkerAktørId, SøkersRolle søkersRolle) {

        return lagSøknadForeldrepenger(termindato, søkerAktørId, søkersRolle)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.termin(1, termindato));

    }
    protected ForeldrepengerBuilder lagSøknadForeldrepengerFødsel(LocalDate fødselsdato, String søkerAktørId, SøkersRolle søkersRolle) {

        return lagSøknadForeldrepenger(fødselsdato, søkerAktørId, søkersRolle)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(1, fødselsdato));

    }

    public static EndringssøknadBuilder lagEndringssøknad(String aktoerId, SøkersRolle søkersRolle, Fordeling fordeling, String saksnummer) {
        return new EndringssøknadBuilder(aktoerId, søkersRolle)
                .medFordeling(fordeling)
                .medSaksnummer(saksnummer);
    }

    protected List<Integer> sorterteInntektsbeløp(TestscenarioDto testscenario) {
        return testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioderSplittMånedlig().stream()
                .map(Inntektsperiode::getBeløp)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    protected Fordeling fordeling(Uttaksperiode... perioder) {
        return FordelingErketyper.generiskFordeling(perioder);
    }

    protected Uttaksperiode uttaksperiode(String stønadskontotype, LocalDate fom, LocalDate tom) {
        return FordelingErketyper.uttaksperiode(stønadskontotype, fom, tom);
    }

    protected Oppholdsperiode oppholdsperiode(String stonadskontotype, LocalDate fom, LocalDate tom) {
        return FordelingErketyper.oppholdsperiode(stonadskontotype, fom, tom);
    }

}
