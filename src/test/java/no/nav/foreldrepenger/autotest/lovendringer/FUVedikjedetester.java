package no.nav.foreldrepenger.autotest.lovendringer;

import static no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.MØDREKVOTE;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.søknad.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.SøknadHendelse;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.SøknadYtelse;
import no.nav.foreldrepenger.vtp.kontrakter.FødselshendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

@Tag("lovendring")
public class FUVedikjedetester extends ForeldrepengerTestBase {

    @Test
    @DisplayName("1: Far, fødsel, starter med uttak etterfult av opphold. Ny fødselshendelse på nytt barn fører til opphør.")
    @Description("Far, fødsel, starter med uttak etterfulgt av opphold midt i far sin periode." +
                "Det mottas en ny fødselshendelse på et nytt barn." +
                "Mor søker for nytt barn og tidligere fagsak skal opphøres.")
    void farOppholdMidtIsinPeriodeNyttBarnFødesOgGammelFagsakOpphøres() {
        var familie = new Familie(600);
        var fødselsdatoBarn1 = familie.barn().fødselsdato();
        var mor = familie.mor();
        var søknadMor = mor.lagSøknad(SøknadYtelse.FORELDREPENGER, SøknadHendelse.FØDSEL, fødselsdatoBarn1)
                .medMottatdato(fødselsdatoBarn1.plusMonths(2));
        var saksnummerMor = mor.søk(søknadMor.build());
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor, fødselsdatoBarn1.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();
        // TODO: Legg til verifiseringer

        var far = familie.far();
        var sisteUttaksdatoMor = hentSisteUttaksdatoFraForeldrepengeSøknad(søknadMor);

        var førsteUttaksdatoeFar = sisteUttaksdatoMor.plusDays(1);
        var fordeling = FordelingErketyper.generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, førsteUttaksdatoeFar, førsteUttaksdatoeFar.plusWeeks(10).minusDays(1)),
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, førsteUttaksdatoeFar.plusWeeks(15), førsteUttaksdatoeFar.plusWeeks(20).minusDays(1)));
        var søknadFar = far.lagSøknad(SøknadYtelse.FORELDREPENGER, SøknadHendelse.FØDSEL, fødselsdatoBarn1)
                .medFordeling(fordeling);
        var saksnummerFar =far.søk(søknadFar.build());
        far.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerFar, sisteUttaksdatoMor);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();

        // Fødselshendelse
        var fødselsdatoBarn2 = fødselsdatoBarn1.plusMonths(10);
        var fødselshendelseDto = new FødselshendelseDto("OPPRETTET", null, mor.fødselsnummer().toString(),
                far.fødselsnummer().toString(), null, fødselsdatoBarn2);
        innsender.opprettHendelsePåKafka(fødselshendelseDto);

        // TODO: Ny søknad sendes til GOSYS??
        var søknadMor2 = mor.lagSøknad(SøknadYtelse.FORELDREPENGER, SøknadHendelse.FØDSEL, fødselsdatoBarn2)
                .medMottatdato(fødselsdatoBarn2.plusWeeks(2));
        var saksnummerMor2 = mor.søk(søknadMor2.build());

        List<TestscenarioDto> testscenarioDtos = hentTestscenario();
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor2, fødselsdatoBarn2.minusWeeks(3));
        // TODO: Her opprettes det ikke ny fagsak automatisk? What is wrong?



    }

    @Test
    @DisplayName("2: Far, fødsel, starter med opphold")
    @Description("Far, fødsel, starter med opphold. Dvs. opphold mellom mor sin siste uttaksdato og far sin første.")
    void farStarterMedOpphold() {
        var familie = new Familie(600);
        var fødselsdatoBarn1 = familie.barn().fødselsdato();
        var mor = familie.mor();
        var søknadMor = mor.lagSøknad(SøknadYtelse.FORELDREPENGER, SøknadHendelse.FØDSEL, fødselsdatoBarn1)
                .medMottatdato(fødselsdatoBarn1.plusMonths(2));
        var saksnummerMor = mor.søk(søknadMor.build());
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor, fødselsdatoBarn1.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();
        // TODO: Legg til verifiseringer

        var far = familie.far();
        var sisteUttaksdatoMor = hentSisteUttaksdatoFraForeldrepengeSøknad(søknadMor);
        var førsteUttaksdatoeFar = sisteUttaksdatoMor.plusMonths(3);
        var fordeling = FordelingErketyper.generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, førsteUttaksdatoeFar, førsteUttaksdatoeFar.plusWeeks(15).minusDays(1)));
        var søknadFar = far.lagSøknad(SøknadYtelse.FORELDREPENGER, SøknadHendelse.FØDSEL, fødselsdatoBarn1)
                .medFordeling(fordeling);
        var saksnummerFar =far.søk(søknadFar.build());
        far.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerFar, sisteUttaksdatoMor);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();

        // TODO: Verifiser at oppholdsperioden ikke fører til fedrekvoten.
        //  Orginalt regelverk: Ikke innvilget uttak pga ikke sammenhengene perioder for oppholde. Mister dermed store del av fedrekvoten etter.
        //  Nytt regelverk: Opphold OK, mister ikke noe av fedrekvoten.


    }

    @Test
    @DisplayName("3: Mor, fødsel med opphold , fødsel, starter med opphold")
    @Description("Mor søker fødsel med opphold midt i sin periode. Mens fagsaken løper for dette barnet så mottas det en" +
                "fødselshendelse på nytt barn. Tidligere fagsak skal opphøres ved ny søknad for nytt barn. ")
    void morOppholdMidtIperiodeNyttBarnMensForrigeFagsakLøper() {
        var familie = new Familie(600);
        var fødselsdatoBarn1 = familie.barn().fødselsdato();
        var mor = familie.mor();
        var fordeling = FordelingErketyper.generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdatoBarn1.minusWeeks(3), fødselsdatoBarn1.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdatoBarn1, fødselsdatoBarn1.plusWeeks(6).minusDays(1)),
                // OPPHOLD I 20 UKER
                uttaksperiode(MØDREKVOTE, fødselsdatoBarn1.plusWeeks(26), fødselsdatoBarn1.plusWeeks(35).minusDays(1)),
                // OPPHOLD I 4 UKER
                uttaksperiode(FELLESPERIODE, fødselsdatoBarn1.plusWeeks(39), fødselsdatoBarn1.plusWeeks(50).minusDays(1)));
        var søknadMor = mor.lagSøknad(SøknadYtelse.FORELDREPENGER, SøknadHendelse.FØDSEL, fødselsdatoBarn1)
                .medFordeling(fordeling)
                .medMottatdato(fødselsdatoBarn1.plusMonths(2));
        var saksnummerMor = mor.søk(søknadMor.build());
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor, fødselsdatoBarn1.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();

        // TODO: Legg til verifiseringer
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Opphold skal ikke avslås pga ikke sammenhengende perioder")
                .isEmpty();
        var uttakResultatPerioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttakResultatPerioder)
                .as("Forventer samme antall perioder som i fordelingen i søknadden")
                .hasSize(4);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo())
                .as("Verifsier at mødrekvoten er brukt opp og ikke i minus!")
                .isZero();


        // Fødselshendelse på nytt barn
        var fødselsdatoBarn2 = fødselsdatoBarn1.plusMonths(10);
        var fødselshendelseDto = new FødselshendelseDto("OPPRETTET", null, mor.fødselsnummer().toString(),
                null, null, fødselsdatoBarn2);
        innsender.opprettHendelsePåKafka(fødselshendelseDto);


        // TODO: Ny søknad sendes til GOSYS??
        var søknadMor2 = mor.lagSøknad(SøknadYtelse.FORELDREPENGER, SøknadHendelse.FØDSEL, fødselsdatoBarn2)
                .medMottatdato(fødselsdatoBarn2.plusWeeks(2));
        var saksnummerMor2 = mor.søk(søknadMor2.build());
    }


    @Test
    @DisplayName("4: Mor, adopsjon, syk innenfor de første 6 ukene")
    @Description("Mor, adopsjon, syk innenfor de første 6 ukene. Sjekk at mor ikke må søke utsettelse for sykdom.")
    void morAdopsjonSykInnenforDeFørste6Ukene() {
        var familie = new Familie(500);
        var fødselsdatoBarn1 = familie.barn().fødselsdato();
        var mor = familie.mor();
        var søknadMor = mor.lagSøknad(SøknadYtelse.FORELDREPENGER, SøknadHendelse.FØDSEL, fødselsdatoBarn1)
                .medMottatdato(fødselsdatoBarn1.plusMonths(2));
        var saksnummerMor = mor.søk(søknadMor.build());
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor, fødselsdatoBarn1.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();
        // TODO: Legg til verifiseringer
    }





    private LocalDate hentSisteUttaksdatoFraForeldrepengeSøknad(no.nav.foreldrepenger.autotest.søknad.builder.ForeldrepengerBuilder søknadMor) {
        return ((Foreldrepenger) søknadMor.build().getYtelse()).getFordeling().getPerioder().stream()
                .max(Comparator.comparing(LukketPeriodeMedVedlegg::getTom))
                .map(LukketPeriodeMedVedlegg::getTom)
                .orElseThrow();
    }

}
