package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
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
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SoekersRelasjonErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SøknadErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenariodataDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.ObjectFactory;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Uttaksperiode;
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


        lagOgSendImFraTestscenariodata(testscenario.getScenariodata(), saksnummerMor, aktørIdMor, fnrMor,
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
        lagOgSendImFraTestscenariodata(testscenario.getScenariodataAnnenpart(), saksnummerFar, aktørIdFar, fnrFar, fpStartFar);
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

        lagOgSendImFraTestscenariodata(testscenario.getScenariodata(), saksnummerMor,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdatoMor
                );
        System.out.println("saksnummer Mor" +saksnummerMor);
    }

    @Test
    @DisplayName("Mor prematuruker")
    @Description("Mor prematuruker")
    public void testcase_mor_prematuruker_fødsel_8_uker_før_termin() throws Exception {
        TestscenarioDto testscenario = opprettScenario("75");
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(13).minusDays(1)));

        long saksnummerMor = sendForeldrepengersøknadTerminOgIm(testscenario, SøkersRolle.MOR, fordeling, fpStartdatoMor);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
    }

    @Test
    @DisplayName("Mor automatisk førstegangssøknad termin")
    @Description("Mor førstegangssøknad på termin")
    public void testcase_mor_termin() throws Exception {
        TestscenarioDto testscenario = opprettScenario("74");
        LocalDate termindato = LocalDate.now().plusWeeks(4);
        LocalDate fpStartdatoMor = termindato.minusWeeks(3);
        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, termindato.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato, termindato.plusWeeks(8).minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, termindato.plusWeeks(8), termindato.plusWeeks(13).minusDays(1)));
        long saksnummerMor = sendForeldrepengersøknadTerminOgIm(testscenario, SøkersRolle.MOR, fordelingMor, fpStartdatoMor);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
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
        LocalDate fødselsdato = LocalDate.now().minusWeeks(3);
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusDays(36)));
        long saksnummerMor = sendForeldrepengersøknadFødselOgIm(testscenario, SøkersRolle.MOR, fordeling, fpStartdato);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
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
    @DisplayName("Testcase for koblet sak")
    @Description("Mor og far søker etter fødsel med ett arbeidsforhold hver. 100% dekningsgrad.")
    public void testcase_morOgFar_etterFødsel_kantTilKant() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");

        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        SøknadBuilder søknadMor = SøknadErketyper.foreldrepengesøknadFødselErketype(morAktørId, SøkersRolle.MOR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        //SØKNAD FAR
        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String farFnr = testscenario.getPersonopplysninger().getAnnenpartIdent();
        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(10).plusDays(1);

        SøknadBuilder søknadFar = SøknadErketyper.foreldrepengesøknadFødselErketype(farAktørId, SøkersRolle.FAR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farFnr, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farFnr, fpStartDatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farFnr, saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
    }
    @Test
    @DisplayName("Testcase for koblet sak med overlappende perioder, far mister dager")
    @Description("Mor og far søker etter fødsel med ett arbeidsforhold hver. 100% dekningsgrad.")
    public void testcase_morOgFar_etterFødsel_overlapp_misterDager() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");

        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();

        LocalDate familiehendelse = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = familiehendelse.minusWeeks(3);
        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordeling.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, familiehendelse.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, familiehendelse, familiehendelse.plusWeeks(6).minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, familiehendelse.plusWeeks(6), familiehendelse.plusWeeks(22).minusDays(1)));

        long saksnummerMor = sendForeldrepengersøknadFødselOgIm(testscenario, SøkersRolle.MOR, fordeling, fpStartdatoMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        String farFnr = testscenario.getPersonopplysninger().getAnnenpartIdent();
        LocalDate fpStartDatoFar = familiehendelse.plusWeeks(6);
        Fordeling fordelingFar = new ObjectFactory().createFordeling();
        fordelingFar.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderFar = fordelingFar.getPerioder();
        perioderFar.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, familiehendelse.plusWeeks(6), familiehendelse.plusWeeks(10).minusDays(1)));

        long saksnummerFar = sendForeldrepengersøknadFødselAnnenPart(testscenario, SøkersRolle.FAR, fordelingFar, fpStartDatoFar);

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

        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, termindato.minusDays(1)));
        perioderMor.add(new UttaksperiodeBuilder(
                STØNADSKONTOTYPE_MØDREKVOTE,
                termindato,
                termindato.plusWeeks(15).minusDays(1))
                .medSamtidigUttak(BigDecimal.valueOf(100))
                .build());
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusWeeks(15), termindato.plusWeeks(24).plusDays(1)));

        long saksnummerMor = sendForeldrepengersøknadFødselOgIm(testscenario, SøkersRolle.MOR, fordelingMor, fpStartdatoMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();

        String orgnrFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(6);

        Fordeling fordelingFar = new ObjectFactory().createFordeling();
        fordelingFar.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordelingFar.getPerioder();
        perioder.add(new GraderingBuilder()
                .medGraderingArbeidstaker(orgnrFar, 50)
                .medTidsperiode(fpStartDatoFar, fpStartDatoFar.plusWeeks(9).minusDays(1))
                .medStønadskontoType(STØNADSKONTOTYPE_FELLESPERIODE)
                .medSamtidigUttak(true, BigDecimal.valueOf(50))
                .build());
        perioder.add(FordelingErketyper.oppholdsperiode(OPPHOLDSTYPE_KVOTE_FELLESPERIODE_ANNEN_FORELDER, fpStartDatoFar.plusWeeks(9), fpStartDatoFar.plusWeeks(18).plusDays(2)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartDatoFar.plusWeeks(18).plusDays(3), fpStartDatoFar.plusWeeks(33).plusDays(2)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fpStartDatoFar.plusWeeks(33).plusDays(3), fpStartDatoFar.plusWeeks(35).plusDays(1)));

        long saksnummerFar = sendForeldrepengersøknadFødselAnnenPart(testscenario, SøkersRolle.FAR, fordelingFar, fpStartDatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgFørstegangsbehandling();
    }
    @Test
    @DisplayName("Testcase koblet sak med oppholdsperioder")
    @Description("Test for behandling med oppholdsperioder i koblet sak, far søker bare delvis det mor ønsker")
    public void testcase_morOgFar_kobletsak_medOppholdsperioder() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        perioderMor.add(FordelingErketyper.oppholdsperiode(OPPHOLDSTYPE_FEDREKVOTE_ANNEN_FORELDER, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(15).minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(30).minusDays(1)));

        long saksnummerMor = sendForeldrepengersøknadFødselOgIm(testscenario, SøkersRolle.MOR, fordelingMor, fpStartdatoMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();

        Fordeling fordelingFar = new ObjectFactory().createFordeling();
        fordelingFar.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderFar = fordelingFar.getPerioder();
        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(6);
        perioderFar.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartDatoFar, fødselsdato.plusWeeks(10).minusDays(1)));

        sendForeldrepengersøknadFødselAnnenPart(testscenario, SøkersRolle.FAR, fordelingFar, fpStartDatoFar);

    }
    @Test
    @DisplayName("Testcase endringssøknader i koblet sak")
    @Description("Test for rekkefølge på behandling ved at begge sender endringssøknader i koblet sak.")
    public void testcase_morOgFar_kobletsak_medSamtidigEndringssøknader() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);


        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6)));
        perioderMor.add(FordelingErketyper.utsettelsesperiode(UTSETTELSETYPE_ARBEID, fødselsdato.plusWeeks(6).plusDays(1), fødselsdato.plusWeeks(15).minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(30).minusDays(1)));

        long saksnummerMor = sendForeldrepengersøknadFødselOgIm(testscenario, SøkersRolle.MOR, fordelingMor, fpStartdatoMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(6);
        Fordeling fordelingFar = new ObjectFactory().createFordeling();
        fordelingFar.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderFar = fordelingFar.getPerioder();
        perioderFar.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartDatoFar, fødselsdato.plusWeeks(10).minusDays(1)));

        long saksnummerFar = sendForeldrepengersøknadFødselAnnenPart(testscenario, SøkersRolle.FAR, fordelingFar, fpStartDatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilSakHarRevurdering();
        saksbehandler.velgRevurderingBehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        LocalDate endringssøknadMorStart = fødselsdato.plusWeeks(15);
        Fordeling fordelingMorEndring = new ObjectFactory().createFordeling();
        fordelingFar.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMorEndring = fordelingMorEndring.getPerioder();
        perioderMorEndring.add(utsettelsesperiode(UTSETTELSETYPE_LOVBESTEMT_FERIE, endringssøknadMorStart, endringssøknadMorStart.plusWeeks(2).minusDays(1)));
        perioderMorEndring.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, endringssøknadMorStart.plusWeeks(2), endringssøknadMorStart.plusWeeks(6).minusDays(1)));
        perioderMorEndring.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, endringssøknadMorStart.plusWeeks(6), endringssøknadMorStart.plusWeeks(6).plusDays(1)));

        sendEndringssøknad(testscenario,SøkersRolle.MOR, fordelingMorEndring, saksnummerMor);

        LocalDate endringssøknadFarStart = fødselsdato.plusWeeks(10);
        Fordeling fordelingFarEndring = new ObjectFactory().createFordeling();
        fordelingFar.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderFarEndring = fordelingFarEndring.getPerioder();
        perioderFarEndring.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, endringssøknadFarStart, endringssøknadFarStart.plusWeeks(5).minusDays(1)));

        sendEndringssøknadAnnenPart(testscenario, SøkersRolle.FAR, fordelingFarEndring, saksnummerFar);

        saksbehandler.ventTilSakHarXAntallBehandlinger(3);
        saksbehandler.velgSisteBehandling();
        saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class)
                .godkjennAlleManuellePerioder(100);
        saksbehandler.bekreftAksjonspunktBekreftelse(FastsettUttaksperioderManueltBekreftelse.class);

        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);
        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummerMor);
        beslutter.velgSisteBehandling();
        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_UTTAKPERIODER));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling();
    }
    //TODO lage test med gradering og utsettelse

    //TODO Flytte testen til morOgFar og gjøre den ferdig
    @Test
    @DisplayName("Mor søker med flere endingssøknader")
    @Description("Mor har åpen revurdering og sender endringssøknad på endringssøknad")
    public void testcase_mor_endringssøknadPåÅpenEndringssøknad() throws Exception {
        TestscenarioDto testscenario = opprettScenario("75");
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(31).minusDays(1)));

        long saksnummer = sendForeldrepengersøknadFødselOgIm(testscenario, SøkersRolle.MOR, fordelingMor, fpStartdato);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.ikkeVentPåStatus = true;
        saksbehandler.hentFagsak(saksnummer);
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();

//        søker.nyFordeling();
//        søker.perioderRef.add(graderingsperiodeSN(
//                STØNADSKONTOTYPE_FELLESPERIODE,
//                fødselsdato.plusWeeks(20),
//                fødselsdato.plusWeeks(25),
//                40));
//        ForeldrepengesoknadBuilder søknadEndring = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(søker.aktørID, søker.getSisteFordeling(), saksnummer.toString());
//        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
//        fordel.sendInnSøknad(søknadEndring.build(), søker.aktørID, søker.ident, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
//
//        søker.nyFordeling();
//        søker.perioderRef.add(graderingsperiodeSN(
//                STØNADSKONTOTYPE_MØDREKVOTE,
//                fødselsdato.plusWeeks(10), fødselsdato.plusWeeks(20),
//                 40));
//        søker.perioderRef.add(utsettelsesperiode(UTSETTELSETYPE_ARBEID, fødselsdato.plusWeeks(20).plusDays(1), fødselsdato.plusWeeks(30)));
//        ForeldrepengesoknadBuilder søknadEndringTO = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(søker.aktørID, søker.getSisteFordeling(), saksnummer.toString());
//        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
//        fordel.sendInnSøknad(søknadEndringTO.build(), søker.aktørID, søker.ident, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
    }

    @Step("Lager og sender inn foreldrepengesøknad Termin")
    private long sendForeldrepengersøknadTerminOgIm(TestscenarioDto testscenario, SøkersRolle søkersRolle ,
                                                    Fordeling fordeling, LocalDate fpStartdato) throws Exception {
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.termin(1, LocalDate.now().plusWeeks(3)), fordeling)
                .build();
        SøknadBuilder søknad = new SøknadBuilder(foreldrepenger, søkerAktørIdent, søkersRolle);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        return lagOgSendImFraTestscenariodata(testscenario.getScenariodata(), saksnummer,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato);
    }
    @Step("Lager og sender inn foreldrepengesøknad Fødsel")
    private long sendForeldrepengersøknadFødselOgIm(TestscenarioDto testscenario, SøkersRolle søkersRolle ,
                                                    Fordeling fordeling, LocalDate fpStartdato) throws Exception {
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordeling)
                .medAnnenForelder(testscenario.getPersonopplysninger().getAnnenPartAktørIdent())
                .build();
        SøknadBuilder søknad = new SøknadBuilder(foreldrepenger, søkerAktørIdent, søkersRolle);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        return lagOgSendImFraTestscenariodata(testscenario.getScenariodata(), saksnummer,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato);
    }
    @Step("Lager og sender inn foreldrepengesøknad Fødsel for annePart")
    private long sendForeldrepengersøknadFødselAnnenPart(TestscenarioDto testscenario, SøkersRolle søkersRolle,
                                                         Fordeling fordeling, LocalDate fpStartdato) throws Exception {
        String søkerAktørIdent = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordeling)
                .medAnnenForelder(testscenario.getPersonopplysninger().getSøkerAktørIdent())
                .build();
        SøknadBuilder søknad = new SøknadBuilder(foreldrepenger, søkerAktørIdent, søkersRolle);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        return lagOgSendImFraTestscenariodata(testscenario.getScenariodataAnnenpart(), saksnummer,
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                fpStartdato);
    }
    private long lagOgSendImFraTestscenariodata(TestscenariodataDto testscenariodata, long saksnummer,
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

    //TODO flytte til FpsakTestBase når ferdig.
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