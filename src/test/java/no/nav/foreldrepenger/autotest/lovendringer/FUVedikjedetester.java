package no.nav.foreldrepenger.autotest.lovendringer;

import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.util.testscenario.modell.SøknadHendelse.FØDSEL;
import static no.nav.foreldrepenger.autotest.util.testscenario.modell.SøknadYtelse.FORELDREPENGER;
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
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerBosattBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAdopsjonsdokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAnnenForeldreHarRett;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaUttakBekreftelse;
import no.nav.foreldrepenger.autotest.søknad.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.SøknadHendelse;
import no.nav.foreldrepenger.vtp.kontrakter.FødselshendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

@Tag("lovendring")
// DATO FOR INNTREFFELSE ER 2019-01-01!
public class FUVedikjedetester extends ForeldrepengerTestBase {

    // TODO: Vi mangler støtte for følgende:
    //  * Søknad på nytt barn vil ikke fungere fordi opprettet_tid på fagsaken blir satt til dagens dato.
    //    Dette resultuerer da i at vurderFagsaken havner under "VurderFagsystem FP strukturert søknad nyere sak enn 10mnd for"
    //    som sender den videre til GOSYS.
    //      Gjelder test 1,3,
    //  * Søknad som er eldre enn 2019-01-01 vil falle direkte til GOSYS pga andre beregningsregler.
    //    Se på om det er nødvendig å kunne endre denne datoen slik at vi kna ha saker som er rundt 3 år gamle.
    //    Dette gjelder tester hvor far søker opp mot grensen på 3 år eller senere. Test 8?
    //  * Pleiepenger: Felles for disse er at vi må vurdere om det er nødvendig med å kunne migrere ytelser, slik at
    //    disse kan endres dynamisk i testen.







    @Test
    @DisplayName("1: Far, fødsel, starter med uttak etterfult av opphold. Ny fødselshendelse på nytt barn fører til opphør.")
    @Description("Far, fødsel, starter med uttak etterfulgt av opphold midt i far sin periode." +
                "Det mottas en ny fødselshendelse på et nytt barn." +
                "Mor søker for nytt barn og tidligere fagsak skal opphøres.")
    void farOppholdMidtIsinPeriodeNyttBarnFødesOgGammelFagsakOpphøres() {
        var familie = new Familie(600);
        var fødselsdatoBarn1 = familie.barn().fødselsdato();
        var mor = familie.mor();
        var søknadMor = mor.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn1)
                .medMottatdato(fødselsdatoBarn1.plusMonths(2));
        var saksnummerMor = mor.søk(søknadMor.build());
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor, fødselsdatoBarn1.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        var avklarBrukerBosattBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarBrukerBosattBekreftelse.class)
                .bekreftBosettelse();
        saksbehandler.bekreftAksjonspunkt(avklarBrukerBosattBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerMor, false);
        // TODO: Legg til verifiseringer

        var far = familie.far();
        var sisteUttaksdatoMor = hentSisteUttaksdatoFraForeldrepengeSøknad(søknadMor);

        var førsteUttaksdatoeFar = sisteUttaksdatoMor.plusDays(1);
        var fordeling = FordelingErketyper.generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, førsteUttaksdatoeFar, førsteUttaksdatoeFar.plusWeeks(10).minusDays(1)),
                // Opphold i 3 mnd
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, førsteUttaksdatoeFar.plusMonths(4).plusWeeks(10), førsteUttaksdatoeFar.plusMonths(4).plusWeeks(15).minusDays(1)));
        var søknadFar = far.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn1)
                .medFordeling(fordeling)
                .medMottatdato(førsteUttaksdatoeFar.plusWeeks(2));
        var saksnummerFar =far.søk(søknadFar.build());
        far.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerFar, sisteUttaksdatoMor);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();

        // Fødselshendelse
        var fødselsdatoBarn2 = fødselsdatoBarn1.plusMonths(13);
        var fødselshendelseDto = new FødselshendelseDto("OPPRETTET", null, mor.fødselsnummer().toString(),
                far.fødselsnummer().toString(), null, fødselsdatoBarn2);
        innsender.opprettHendelsePåKafka(fødselshendelseDto);

        // TODO: Ny søknad sendes til GOSYS??
        var søknadMor2 = mor.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn2)
                .medMottatdato(fødselsdatoBarn2);
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
        var søknadMor = mor.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn1)
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
        var søknadFar = far.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn1)
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
        var søknadMor = mor.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn1)
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
        var søknadMor2 = mor.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn2)
                .medMottatdato(fødselsdatoBarn2.plusWeeks(2));
        var saksnummerMor2 = mor.søk(søknadMor2.build());
    }


    @Test
    @DisplayName("4: Mor, adopsjon, syk innenfor de første 6 ukene")
    @Description("Mor, adopsjon, syk innenfor de første 6 ukene. Sjekk at mor ikke må søke utsettelse for sykdom.")
    void morAdopsjonSykInnenforDeFørste6Ukene() {
        var familie = new Familie(501);
        var overtagelsesdato = LocalDate.now().minusWeeks(4);
        var mor = familie.mor();
        var fordeling = generiskFordeling(
                uttaksperiode(MØDREKVOTE, overtagelsesdato, overtagelsesdato.plusWeeks(1).minusDays(1)),
                // Opphold på grunn av sykdom (Tilsvarende som å ikke søke om utsettelse).
                uttaksperiode(MØDREKVOTE, overtagelsesdato.plusWeeks(4), overtagelsesdato.plusWeeks(18).minusDays(1)),
                uttaksperiode(FELLESPERIODE, overtagelsesdato.plusWeeks(18), overtagelsesdato.plusWeeks(34).minusDays(1)));
        var søknadMor = mor.lagSøknad(FORELDREPENGER, SøknadHendelse.ADOPSJON, overtagelsesdato)
                .medFordeling(fordeling)
                .medMottatdato(overtagelsesdato.minusWeeks(2));
        var saksnummerMor = mor.søk(søknadMor.build());
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor, overtagelsesdato.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBegrunnelse("Adopsjon behandlet av Autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);

        saksbehandler.ventTilAvsluttetBehandling();
        // TODO: Legg til verifiseringer
    }


    @Test
    @DisplayName("5: Far, adopsjon, starter med opphold")
    @Description("Far søker for adopsjon og starter med opphold, etterfulgt av fult uttak av fedrekvoten.")
    void farAdopsjonStarterMedOpphold() {
        var familie = new Familie(563);
        var overtagelsesdato = LocalDate.now().minusWeeks(4);
        var far = familie.far();
        var fordeling = generiskFordeling(
                // Opphold i 1 måneder etter overtagelsesdato
                uttaksperiode(FEDREKVOTE, overtagelsesdato.plusMonths(1), overtagelsesdato.plusMonths(1).plusWeeks(15).minusDays(1)));
        var søknadFar = far.lagSøknad(FORELDREPENGER, SøknadHendelse.ADOPSJON, overtagelsesdato)
                .medFordeling(fordeling)
                .medMottatdato(overtagelsesdato.plusWeeks(2));
        var saksnummerMor = far.søk(søknadFar.build());
        far.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor, overtagelsesdato.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBegrunnelse("Adopsjon behandlet av Autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);

        var avklarFaktaAnnenForeldreHarRett = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAnnenForeldreHarRett.class)
                .setAnnenforelderHarRett(true)
                .setBegrunnelse("Begge har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        saksbehandler.ventTilAvsluttetBehandling();
        // TODO: Legg til verifiseringer
    }


    @Test
    @DisplayName("6: Mor syk fra uke 5 til 10. Far har opphold mellom mor sin periode og sin egen.")
    @Description("Mor søker fødsel og er syk innenfor de 6 første ukene og etter: Fra uke 5 til 10." +
                "Far starter med opphold mellom mor og sin periode.")
    void MorSykdomUke5Til10FarStarterMedOpphold() {
        var familie = new Familie(601);
        var fødselsdatoBarn1 = familie.barn().fødselsdato();
        var mor = familie.mor();
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdatoBarn1.minusWeeks(3), fødselsdatoBarn1.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdatoBarn1, fødselsdatoBarn1.plusWeeks(5).minusDays(1)),
                utsettelsesperiode(UtsettelsesÅrsak.SYKDOM, fødselsdatoBarn1.plusWeeks(5), fødselsdatoBarn1.plusWeeks(6).minusDays(1)),
                // Opphold på grunn av sykdom, men utenfor de 6 første ukene
                uttaksperiode(MØDREKVOTE, fødselsdatoBarn1.plusWeeks(10), fødselsdatoBarn1.plusWeeks(20).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdatoBarn1.plusWeeks(20), fødselsdatoBarn1.plusWeeks(36).minusDays(1)));
        var søknadMor = mor.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn1)
                .medFordeling(fordelingMor)
                .medMottatdato(fødselsdatoBarn1.plusMonths(2));
        var saksnummerMor = mor.søk(søknadMor.build());
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor, fødselsdatoBarn1.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        var avklarFaktaUttakPerioder = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class)
                .godkjennPeriode(fødselsdatoBarn1.plusWeeks(5), fødselsdatoBarn1.plusWeeks(6).minusDays(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakPerioder);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerMor, false);
        // TODO: Legg til verifiseringer

        var far = familie.far();
        var sisteUttaksdatoMor = hentSisteUttaksdatoFraForeldrepengeSøknad(søknadMor);
        var førsteUttaksdatoeFar = sisteUttaksdatoMor.plusDays(1);
        var fordeling = FordelingErketyper.generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, førsteUttaksdatoeFar, førsteUttaksdatoeFar.plusWeeks(10).minusDays(1)),
                // Opphold i 3 mnd
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, førsteUttaksdatoeFar.plusMonths(2).plusWeeks(10), førsteUttaksdatoeFar.plusMonths(2).plusWeeks(15).minusDays(1)));
        var søknadFar = far.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn1)
                .medFordeling(fordeling)
                .medMottatdato(førsteUttaksdatoeFar.plusWeeks(2));
        var saksnummerFar = far.søk(søknadFar.build());
        far.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerFar, sisteUttaksdatoMor);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();
    }

    @Test
    @DisplayName("7: Mor, termin før regelverksendring med søk omutsettelse, fødsel etter regelverksendring. Far søke fødsel etter 3 år. ")
    @Description("Mor søker termin før regelverkendringen. Her søker hun utsettelse en gang etter de 6 første ukene." +
                "Barnet blir født en gang etter regelverksendringen og utsettelse blir til opphold.")
    void MorTerminGammelRegelverkMenFødesPåNytt() {
        var familie = new Familie(501);
        // DATO_FOR_NYE_UTTAKSREGLER = 2019-01-01
        var termindato = LocalDate.of(2019,5, 20);
        var mor = familie.mor();
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, termindato.minusWeeks(3), termindato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, termindato, termindato.plusWeeks(6).minusDays(1)),
                utsettelsesperiode(UtsettelsesÅrsak.SYKDOM, termindato.plusWeeks(6), termindato.plusWeeks(10).minusDays(1)),
                uttaksperiode(MØDREKVOTE, termindato.plusWeeks(10), termindato.plusWeeks(20).minusDays(1)),
                uttaksperiode(FELLESPERIODE, termindato.plusWeeks(20), termindato.plusWeeks(36).minusDays(1)));
        var søknadMor = mor.lagSøknad(FORELDREPENGER, SøknadHendelse.TERMIN, termindato)
                .medFordeling(fordelingMor)
                .medMottatdato(termindato.plusMonths(2));
        var saksnummerMor = mor.søk(søknadMor.build());
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor, termindato.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.harAksjonspunkt(SJEKK_MANGLENDE_FØDSEL);

        // TODO: Legg til verifiseringer
        // DATO_FOR_NYE_UTTAKSREGLER = 2019-06-01
        var fødselsdatoBarn = termindato.plusWeeks(2);
        var fødselshendelseDto = new FødselshendelseDto("OPPRETTET", null, mor.fødselsnummer().toString(),
               null, null, fødselsdatoBarn);
        innsender.opprettHendelsePåKafka(fødselshendelseDto);

        // TODO: Fødselshendelse registerers ikke? Er det for langt tilbake i tid kanskje?
    }

    @Test
    @DisplayName("8:Far søker foreldrepenger rett før barnet fyller 3 år. Delvis avlsag.")
    @Description("Mor happy case. Far søker foreldrepenger rett før barnet fyller 3 år. Får innvilget det som er" +
                "innenfor 3 år, men avslag på resten.")
    void FarSøkerRettFørBarnets3Årsdag() {
        var familie = new Familie(602);
        var fødselsdatoBarn1 = familie.barn().fødselsdato();
        var mor = familie.mor();
        var søknadMor = mor.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn1)
                .medMottatdato(fødselsdatoBarn1.plusMonths(2));
        var saksnummerMor = mor.søk(søknadMor.build());
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor, fødselsdatoBarn1.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        // TODO: Finn ut hvorfor vi trenger denne her for eldre saker?
        var avklarBrukerBosattBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarBrukerBosattBekreftelse.class)
                .bekreftBosettelse();
        saksbehandler.bekreftAksjonspunkt(avklarBrukerBosattBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerMor, false);


        // TODO: For at dette skal fungere så må fødselsdatoen settes før 2019, og dermed må
        //  Endringsdato for beregningregler FTL som styrer ruting fpsak/Infotrygd også justeres til tidligere.
        var far = familie.far();
        var farStartdato = fødselsdatoBarn1.plusYears(2).plusWeeks(42);
        var fordelingFar = generiskFordeling(
                uttaksperiode(FEDREKVOTE, farStartdato, farStartdato.plusWeeks(15).minusDays(1)));
        var søknad = far.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn1)
                .medFordeling(fordelingFar)
                .medMottatdato(farStartdato.minusWeeks(2));
        var saksnummerFar = far.søk(søknad.build());
        far.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerFar, farStartdato);

        saksbehandler.hentFagsak(saksnummerFar);
        // TODO: Finn ut hvorfor vi trenger denne her for eldre saker?
        var avklarBrukerBosattBekreftelseFar = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarBrukerBosattBekreftelse.class)
                .bekreftBosettelse();
        saksbehandler.bekreftAksjonspunkt(avklarBrukerBosattBekreftelseFar);
    }


    @Test
    @DisplayName("9: Prematur fødsel med pleiepenger")
    @Description("Permatur")
    void MorPrematurFødselMedPleiepenger() {
        // TODO: Trenger å utvide modellen i VTP til å tillate å legge til ytelse.
    }

    @Test
    @DisplayName("10: Pleiepenger – 3 uker sykehus, 6 uker hjemme med sykdom")
    @Description("Permatur")
    void MorFødselMedPleiepenger() {
        var familie = new Familie(603);
        var fødselsdatoBarn1 = familie.barn().fødselsdato();
        var mor = familie.mor();
        var søknadMor = mor.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn1)
                .medMottatdato(fødselsdatoBarn1.plusMonths(2));
        var saksnummerMor = mor.søk(søknadMor.build());
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor, fødselsdatoBarn1.minusWeeks(3));
        // TODO: Finn ut hvordan mor sin søknad skal se ut
        // TODO: Finn ut hvordan TREX skal se ut når det mottas støtte for innlagt sykt barn etterfult av sykt barn hjemme.
    }


    @Test
    @DisplayName("11: Mor søker foreldrepenger på nytt barn mens både mor og far har opphold")
    @Description("Mor har opphold midt i sin periode. Far har også opphold midt i sin periode." +
                "Far har opphold samtidig som mor i en periode, hvor et nytt barn blir født." +
                "Uttak etter oppholdene skal opphøres for begge parter.")
    void NyttBarnMensBeggeHarOpphold() {
        var familie = new Familie(601);
        var fødselsdatoBarn1 = familie.barn().fødselsdato();
        var mor = familie.mor();
        var fordelingMor = FordelingErketyper.generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdatoBarn1.minusWeeks(3), fødselsdatoBarn1.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdatoBarn1, fødselsdatoBarn1.plusWeeks(6).minusDays(1)),
                // OPPHOLD I 30 UKER
                uttaksperiode(MØDREKVOTE, fødselsdatoBarn1.plusWeeks(36), fødselsdatoBarn1.plusWeeks(45).minusDays(1)),
                // OPPHOLD I 4 UKER (Far har også opphold i denne perioden)
                uttaksperiode(FELLESPERIODE, fødselsdatoBarn1.plusWeeks(50), fødselsdatoBarn1.plusWeeks(65).minusDays(1)));
        var søknadMor = mor.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn1)
                .medFordeling(fordelingMor)
                .medMottatdato(fødselsdatoBarn1.plusMonths(2));
        var saksnummerMor = mor.søk(søknadMor.build());
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor, fødselsdatoBarn1.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();
        // TODO: Legg til verifiseringer

        var far = familie.far();
        var fordelingFar = FordelingErketyper.generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, fødselsdatoBarn1.plusWeeks(26), fødselsdatoBarn1.plusWeeks(36).minusDays(1)),
                // Opphold i 39 uker
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, fødselsdatoBarn1.plusWeeks(65), fødselsdatoBarn1.plusWeeks(70).minusDays(1)));
        var søknadFar = far.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn1)
                .medFordeling(fordelingFar)
                .medMottatdato(fødselsdatoBarn1.plusWeeks(26));
        var saksnummerFar =far.søk(søknadFar.build());
        far.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerFar, fødselsdatoBarn1.plusWeeks(26));

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();
        // TODO: Legg til verifiseringer

        // Fødselshendelse
        var fødselsdatoBarn2 = fødselsdatoBarn1.plusWeeks(45);
        var fødselshendelseDto = new FødselshendelseDto("OPPRETTET", null, mor.fødselsnummer().toString(),
                far.fødselsnummer().toString(), null, fødselsdatoBarn2);
        innsender.opprettHendelsePåKafka(fødselshendelseDto);

        // TODO: Ny søknad sendes til GOSYS??
        var søknadMor2 = mor.lagSøknad(FORELDREPENGER, FØDSEL, fødselsdatoBarn2)
                .medMottatdato(fødselsdatoBarn2);
        var saksnummerMor2 = mor.søk(søknadMor2.build());

        List<TestscenarioDto> testscenarioDtos = hentTestscenario();
        mor.arbeidsgiver().sendDefaultInntektsmeldingerFP(saksnummerMor2, fødselsdatoBarn2.minusWeeks(3));
        // TODO: Her opprettes det ikke ny fagsak automatisk? What is wrong?
        // TODO: Legg til verifiseringer
    }





    private LocalDate hentSisteUttaksdatoFraForeldrepengeSøknad(no.nav.foreldrepenger.autotest.søknad.builder.ForeldrepengerBuilder søknadMor) {
        return ((Foreldrepenger) søknadMor.build().getYtelse()).getFordeling().getPerioder().stream()
                .max(Comparator.comparing(LukketPeriodeMedVedlegg::getTom))
                .map(LukketPeriodeMedVedlegg::getTom)
                .orElseThrow();
    }

}
