package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTillegsopplysningerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.GraderingBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.UttaksperiodeBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.ytelse.ForeldrepengerYtelseBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SoekersRelasjonErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SøknadErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenariodataDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Rettigheter;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.ObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper.*;

@Execution(ExecutionMode.CONCURRENT)
@Tag("utvikling")
@Tag("foreldrepenger")
public class Uttak extends ForeldrepengerTestBase {
    // Testcaser
    @Test
    public void testcase_farOgMor_farSøkerEtterMorMedMottatdatoFørMor() throws Exception {
        TestscenarioDto testscenario = opprettScenario("140");
        System.out.println("MOR: " +testscenario.getPersonopplysninger().getSøkerIdent());
        System.out.println("FAR: " +testscenario.getPersonopplysninger().getAnnenpartIdent());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();

        Fordeling fordleingMor = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10).minusDays(1)),
                oppholdsperiode(OPPHOLDSTYPE_FEDREKVOTE_ANNEN_FORELDER, fødselsdato.plusWeeks(10), fødselsdato.plusWeeks(12).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(12), fødselsdato.plusWeeks(14).minusDays(1)),
                new UttaksperiodeBuilder(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(14), fødselsdato.plusWeeks(16).minusDays(1))
                        .medSamtidigUttak(BigDecimal.valueOf(50))
                        .build()
        );
        Foreldrepenger foreldrepengerMor = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordleingMor).build();
        SøknadBuilder søknadMor = new SøknadBuilder(
                foreldrepengerMor, aktørIdMor, SøkersRolle.MOR);
        long saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                fnrMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);


        lagOgSendIm(testscenario.getScenariodata(), saksnummerMor, aktørIdMor, fnrMor,
                fødselsdato);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        long saksnummerFar = fordel.sendInnPapirsøknadForeldrepenger(testscenario, true);
        LocalDate fpStartFar = fødselsdato.plusWeeks(7);

        saksbehandler.hentFagsak(saksnummerFar);
        PapirSoknadForeldrepengerBekreftelse aksjonspunktBekreftelseFar = saksbehandler.aksjonspunktBekreftelse(PapirSoknadForeldrepengerBekreftelse.class);
        FordelingDto fordeling = new FordelingDto();
        PermisjonPeriodeDto fk = new PermisjonPeriodeDto(
                FordelingErketyper.STØNADSKONTOTYPE_FEDREKVOTE, fpStartFar, fpStartFar.plusWeeks(2));
        PermisjonPeriodeDto fk2 = new PermisjonPeriodeDto(
                STØNADSKONTOTYPE_FEDREKVOTE, fødselsdato.plusWeeks(10), fødselsdato.plusWeeks(12).minusDays(1));
        PermisjonPeriodeDto fk3 = new PermisjonPeriodeDto(
                STØNADSKONTOTYPE_FEDREKVOTE, fødselsdato.plusWeeks(14), fødselsdato.plusWeeks(16).minusDays(1));
        PermisjonPeriodeDto fk4 = new PermisjonPeriodeDto(
                FordelingErketyper.STØNADSKONTOTYPE_FEDREKVOTE, fødselsdato.plusWeeks(17), fødselsdato.plusWeeks(18));
        fordeling.permisjonsPerioder.add(fk);
        fordeling.permisjonsPerioder.add(fk2);
        fordeling.permisjonsPerioder.add(fk3);
        fordeling.permisjonsPerioder.add(fk4);

        aksjonspunktBekreftelseFar.morSøkerFødsel(fordeling, fødselsdato, fødselsdato);
        saksbehandler.bekreftAksjonspunktBekreftelse(aksjonspunktBekreftelseFar);
        lagOgSendIm(testscenario.getScenariodataAnnenpart(), saksnummerFar, aktørIdFar, fnrFar, fpStartFar);
    }
    @Test
    @DisplayName("Mor automatisk førstegangssøknad fødsel")
    @Description("Mor førstegangssøknad på fødsel")
    public void testcase_mor_fødsel() throws Exception {
        TestscenarioDto testscenario = opprettScenario("75");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(13).minusDays(1))
        );

        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato),
                fordeling).build();
        SøknadBuilder søknad = new SøknadBuilder(
                foreldrepenger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        lagOgSendIm(testscenario.getScenariodata(), saksnummerMor,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdatoMor
                );
        System.out.println("saksnummer Mor" +saksnummerMor);
    }

    @Test
    public void testcase_morOgFar_vurderingOmAnnenPartHarRett() throws Exception {
        TestscenarioDto testscenario = opprettScenario("140");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);

        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String orgNrMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();

        LocalDate fødsel = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartMor = fødsel.minusWeeks(4);

        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fpStartMor, fpStartMor.plusWeeks(1).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fødsel.minusWeeks(3), fødsel.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødsel, fødsel.plusWeeks(14).minusDays(1)),
                utsettelsesperiode(UTSETTELSETYPE_LOVBESTEMT_FERIE, fødsel.plusWeeks(14), fødsel.plusWeeks(18)),
                (new GraderingBuilder()
                        .medTidsperiode(fødsel.plusWeeks(18).plusDays(1), fødsel.plusWeeks(37))
                        .medStønadskontoType(STØNADSKONTOTYPE_FELLESPERIODE)
                        .medGraderingArbeidstaker(orgNrMor, 40)
                        .medSamtidigUttak(true, BigDecimal.valueOf(60))
                        .build()),
                (new UttaksperiodeBuilder(STØNADSKONTOTYPE_MØDREKVOTE, fødsel.plusDays(1).plusWeeks(37), fødsel.plusWeeks(38).plusDays(2)))
                        .medSamtidigUttak(BigDecimal.valueOf(60))
                        .build(),
                (new UttaksperiodeBuilder(STØNADSKONTOTYPE_FELLESPERIODE, fødsel.plusWeeks(42).plusDays(2), fødsel.plusDays(2).plusWeeks(46))
                        .medSamtidigUttak(BigDecimal.valueOf(60))
                        .build()));
        Foreldrepenger foreldrepengerMor = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødsel),
                fordelingMor)
                .medRettigheter(RettigheterErketyper.beggeForeldreRettIkkeAleneomsorg())
                .build();
        SøknadBuilder søknadMor = new SøknadBuilder(foreldrepengerMor, aktørIdMor, SøkersRolle.MOR);
        long saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                fnrMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        lagOgSendIm(testscenario.getScenariodata(), saksnummerMor, aktørIdMor, fnrMor, fpStartMor);

    }

    @Test
    @DisplayName("Mor får ES og far søker om FP med MS")
    @Description("Mor får innvilget ES og far søker om foreldrepenger, men far søker ikke tidlig nok")
    public void testcase_morES_farFP() throws Exception{
        TestscenarioDto testscenario = opprettScenario("140");
        System.out.println("MOR FNR: " +testscenario.getPersonopplysninger().getSøkerIdent());
        System.out.println("Far FNR: " +testscenario.getPersonopplysninger().getAnnenpartIdent());

        SøknadBuilder søknad = SøknadErketyper.engangstønadsøknadFødselErketype(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                1,
                testscenario.getPersonopplysninger().getFødselsdato());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD,
                null);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaTillegsopplysningerBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandling();

        Rettigheter rettigheter = new Rettigheter();
        rettigheter.setHarAleneomsorgForBarnet(false);
        rettigheter.setHarAnnenForelderRett(true);
        rettigheter.setHarOmsorgForBarnetIPeriodene(true);

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartFar = fødselsdato.plusWeeks(12);
        Fordeling fordelingFar = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER, fpStartFar, fpStartFar.plusWeeks(15)));
        Foreldrepenger foreldrepengerFar = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato),
                fordelingFar)
                .medAnnenForelder(testscenario.getPersonopplysninger().getSøkerAktørIdent())
                .medRettigheter(rettigheter)
                .build();
        SøknadBuilder søknadFar = new SøknadBuilder(
                foreldrepengerFar,
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                SøkersRolle.FAR);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(),
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        lagOgSendIm(
                testscenario.getScenariodataAnnenpart(),
                saksnummerFar,
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                fpStartFar);
    }
    @Test
    @DisplayName("Mor prematuruker")
    @Description("Mor prematuruker")
    public void testcase_mor_prematuruker_fødsel_8_uker_før_termin() throws Exception {
        TestscenarioDto testscenario = opprettScenario("75");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);
        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(13).minusDays(1)));
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.termin(1, LocalDate.now().plusWeeks(3)), fordeling)
                .build();
        SøknadBuilder søknad = new SøknadBuilder(
                foreldrepenger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
    }

    @Test
    @DisplayName("Mor automatisk førstegangssøknad termin")
    @Description("Mor førstegangssøknad på termin")
    public void testcase_mor_termin() throws Exception {
        TestscenarioDto testscenario = opprettScenario("74");
        LocalDate termindato = LocalDate.now().plusWeeks(4);
        LocalDate fpStartdatoMor = termindato.minusWeeks(3);

        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, termindato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato, termindato.plusWeeks(8).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusWeeks(8), termindato.plusWeeks(13).minusDays(1)));
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.termin(1, termindato), fordelingMor)
                .build();
        SøknadBuilder søknad = new SøknadBuilder(
                foreldrepenger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
    }
    @Test
    @DisplayName("Flytting pga fødsel")
    @Description ("Testcase hvor mor søker på termin men fødsel har skjedd. Ikke barn i TPS")
    public void testcase_mor_fødsel_flyttingAvPerioderGrunnetFødsel() throws Exception {
        TestscenarioDto testscenario = opprettScenario("74");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);

        LocalDate fødselsdato = LocalDate.now().minusWeeks(3);
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusDays(36)));
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordeling)
                .build();
        SøknadBuilder søknad = new SøknadBuilder(
                foreldrepenger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        List<InntektsmeldingBuilder> inntektsmedlinger = makeInntektsmeldingFromTestscenario(testscenario, fpStartdato);
        fordel.sendInnInntektsmeldinger(inntektsmedlinger, testscenario, saksnummerMor);

        //Automatisk behandling av Fakta om fødsel siden det ikke er registert barn i TPS
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, fødselsdato.minusMonths(1))
                .setBegrunnelse("omg lol haha ja bacon");
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderManglendeFodselBekreftelse.class);
    }
    @Test
    @DisplayName("Testcase for koblet sak med overlappende perioder, far mister dager")
    @Description("Mor og far søker etter fødsel med ett arbeidsforhold hver. 100% dekningsgrad.")
    public void testcase_morOgFar_etterFødsel_overlapp_misterDager() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");

        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        LocalDate familiehendelse = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = familiehendelse.minusWeeks(3);
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, familiehendelse.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, familiehendelse, familiehendelse.plusWeeks(6).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, familiehendelse.plusWeeks(6), familiehendelse.plusWeeks(22).minusDays(1)));
        Foreldrepenger foreldrepengerMor = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordeling)
                .build();
        SøknadBuilder søknadMor = new SøknadBuilder(
                foreldrepengerMor,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        List<InntektsmeldingBuilder> inntektsmedlinger = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmedlinger, testscenario, saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        String farFnr = testscenario.getPersonopplysninger().getAnnenpartIdent();
        LocalDate fpStartDatoFar = familiehendelse.plusWeeks(6);

        Fordeling fordelingFar = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, familiehendelse.plusWeeks(6), familiehendelse.plusWeeks(10).minusDays(1)));
        Foreldrepenger foreldrepengerFar = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordelingFar)
                .build();
        SøknadBuilder søknadFar = new SøknadBuilder(
                foreldrepengerFar,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR);
        long saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farFnr, fpStartDatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farFnr, saksnummerFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
    }

    @Test
    @DisplayName("Testcase for trekkdager i koblet sak")
    @Description("Far søker med overlapp og mister dager til mor, laget for TFP-390")
    public void testcase_morOgFar_kobletsak() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate termindato = fødselsdato.plusDays(1);
        LocalDate fpStartdatoMor = termindato.minusWeeks(3);

        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, termindato.minusDays(1)),
                new UttaksperiodeBuilder(
                        STØNADSKONTOTYPE_MØDREKVOTE, termindato, termindato.plusWeeks(15).minusDays(1))
                        .medSamtidigUttak(BigDecimal.valueOf(100))
                        .build(),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusWeeks(15), termindato.plusWeeks(24).plusDays(1)));
        Foreldrepenger foreldrepengerMor = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordelingMor)
                .build();
        SøknadBuilder søknadMor = new SøknadBuilder(
                foreldrepengerMor,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        List<InntektsmeldingBuilder> inntektsmedlinger = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmedlinger, testscenario, saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();

        String orgnrFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(6);

        Fordeling fordelingFar = generiskFordeling(
                new GraderingBuilder()
                        .medGraderingArbeidstaker(orgnrFar, 50)
                        .medTidsperiode(fpStartDatoFar, fpStartDatoFar.plusWeeks(9).minusDays(1))
                        .medStønadskontoType(STØNADSKONTOTYPE_FELLESPERIODE)
                        .medSamtidigUttak(true, BigDecimal.valueOf(50))
                        .build(),
                oppholdsperiode(OPPHOLDSTYPE_KVOTE_FELLESPERIODE_ANNEN_FORELDER, fpStartDatoFar.plusWeeks(9), fpStartDatoFar.plusWeeks(18).plusDays(2)),
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartDatoFar.plusWeeks(18).plusDays(3), fpStartDatoFar.plusWeeks(33).plusDays(2)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fpStartDatoFar.plusWeeks(33).plusDays(3), fpStartDatoFar.plusWeeks(35).plusDays(1)));

        Foreldrepenger foreldrepengerFar = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordelingFar)
                .build();
        SøknadBuilder søknadFar = new SøknadBuilder(
                foreldrepengerFar,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR);
        long saksnummerFar = fordel.sendInnSøknad(
                søknadFar.build(),
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(
                testscenario,
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                fpStartDatoFar,
                true);
        fordel.sendInnInntektsmeldinger(
                inntektsmeldingerFar,
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                saksnummerFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgFørstegangsbehandling();
    }


    private long lagOgSendIm(TestscenariodataDto testscenariodata, long saksnummer,
                             String aktørId, String fnr, LocalDate fpStartdato ) throws Exception{
        List<InntektsmeldingBuilder> inntektsmeldinger =  makeInntektsmeldingFromtestscenariodata(
                testscenariodata,
                fnr,
                fpStartdato);
        return fordel.sendInnInntektsmelding(
                inntektsmeldinger.get(0),
                aktørId,
                fnr,
                saksnummer);
    }
    @Step("Sender endringssøknad")
    private void sendEndringssøknad(TestscenarioDto testscenario, SøkersRolle søkersRolle, Fordeling fordeling, long saksnummer) throws Exception {
        SøknadBuilder søknad = SøknadErketyper.endringssøknadErketype(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                søkersRolle, fordeling, String.valueOf(saksnummer));
        fordel.sendInnSøknad(søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
    }
    @Step("Sender endringssøknad annenPart")
    private void sendEndringssøknadAnnenPart(TestscenarioDto testscenario, SøkersRolle søkersRolle, Fordeling fordeling, long saksnummer) throws Exception {
        SøknadBuilder søknad = SøknadErketyper.endringssøknadErketype(
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(), søkersRolle,
                fordeling, String.valueOf(saksnummer));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnSøknad(søknad.build(),
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
    }
    //TODO Flytte til dokumentgenerator for å lage fordelinger
    private static Fordeling generiskFordeling(LukketPeriodeMedVedlegg... perioder) {
        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);
        LukketPeriodeMedVedlegg[] var2 = perioder;
        int var3 = perioder.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            LukketPeriodeMedVedlegg uttaksperiode = var2[var4];
            fordeling.getPerioder().add(uttaksperiode);
        }

        return fordeling;
    }

}