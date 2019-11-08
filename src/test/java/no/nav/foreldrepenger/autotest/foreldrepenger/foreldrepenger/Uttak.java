package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.*;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTillegsopplysningerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.OverføringÅrsak;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.GraderingBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.UttaksperiodeBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.ytelse.ForeldrepengerYtelseBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.*;
import no.nav.foreldrepenger.vtp.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenariodataDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Rettigheter;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper.*;

@Execution(ExecutionMode.CONCURRENT)
@Tag("utvikling")
@Tag("foreldrepenger")
public class Uttak extends ForeldrepengerTestBase {
    // Testcaser
    @Test
    public void testcase_farOgMor_farSøkerEtterMorMedMottatdatoFørMor() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("140");
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
                        .build());
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
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("75");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(13).minusDays(1)));

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
    public void testcase_morOgfar_endringsøknad_overføringperioderFørstePeriodeTilFørstegangssøknad() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("140");

        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();

        LocalDate fødselsDato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartMor = fødselsDato;
        LocalDate fpStartFar =  fødselsDato.plusWeeks(8);

        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsDato, fødselsDato.plusWeeks(8).minusDays(1)),
                oppholdsperiode(OPPHOLDSTYPE_FEDREKVOTE_ANNEN_FORELDER, fødselsDato.plusWeeks(8), fødselsDato.plusWeeks(10).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsDato.plusWeeks(10), fødselsDato.plusWeeks(15).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsDato.plusWeeks(15), fødselsDato.plusWeeks(20).minusDays(1)),
                oppholdsperiode(OPPHOLDSTYPE_FEDREKVOTE_ANNEN_FORELDER, fødselsDato.plusWeeks(20), fødselsDato.plusWeeks(24).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsDato.plusWeeks(24), fødselsDato.plusWeeks(28).minusDays(1)),
                oppholdsperiode(OPPHOLDSTYPE_FEDREKVOTE_ANNEN_FORELDER, fødselsDato.plusWeeks(28), fødselsDato.plusWeeks(32).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsDato.plusWeeks(32), fødselsDato.plusWeeks(36)));
        Foreldrepenger foreldrepengerMor = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsDato),
                fordelingMor)
                .build();
        SøknadBuilder søknadMor = new SøknadBuilder(
                foreldrepengerMor,
                aktørIdMor,
                SøkersRolle.MOR);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                fnrMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        List<InntektsmeldingBuilder> inntektsmeldinger =  makeInntektsmeldingFromtestscenariodata(
                testscenario.getScenariodata(),
                fnrMor,
                fpStartMor);
        fordel.sendInnInntektsmelding(
                inntektsmeldinger.get(0),
                aktørIdMor,
                fnrMor,
                saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();

        Fordeling fordelingFar = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødselsDato.plusWeeks(8), fødselsDato.plusWeeks(10).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødselsDato.plusWeeks(20), fødselsDato.plusWeeks(24).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødselsDato.plusWeeks(28), fødselsDato.plusWeeks(32).minusDays(1)));

        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsDato),
                fordelingFar)
                .build();
        SøknadBuilder søknadFar = new SøknadBuilder(
                foreldrepenger,
                aktørIdFar,
                SøkersRolle.FAR);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(),
                aktørIdFar,
                fnrFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        List<InntektsmeldingBuilder> inntektsmeldingerFar =  makeInntektsmeldingFromtestscenariodata(
                testscenario.getScenariodataAnnenpart(),
                fnrFar,
                fpStartFar);
        fordel.sendInnInntektsmelding(
                inntektsmeldingerFar.get(0),
                aktørIdFar,
                fnrFar,
                saksnummerFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();

        Fordeling endringFordeling = generiskFordeling(
                overføringsperiode(OverføringÅrsak.SYKDOM_ANNEN_FORELDER, STØNADSKONTOTYPE_MØDREKVOTE, fødselsDato.plusWeeks(7), fødselsDato.plusWeeks(8).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødselsDato.plusWeeks(8), fødselsDato.plusWeeks(10).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødselsDato.plusWeeks(20), fødselsDato.plusWeeks(24).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødselsDato.plusWeeks(28), fødselsDato.plusWeeks(32).minusDays(1)));
        SøknadBuilder søknadEndring = SøknadErketyper.endringssøknadErketype(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.FAR,
                endringFordeling,
                String.valueOf(saksnummerFar));
        fordel.sendInnSøknad(søknadEndring.build(),
                aktørIdFar,
                fnrFar,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummerFar);
    }
    @Test
    public void testcase_morOgFar_toArbeidsforhold() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("141");
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();

        LocalDate fødselsDato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartMor = fødselsDato;
        LocalDate fpStartFar =  fødselsDato.plusWeeks(12);

        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsDato, fpStartFar.minusDays(1)),
                oppholdsperiode(OPPHOLDSTYPE_FEDREKVOTE_ANNEN_FORELDER, fpStartFar, fpStartFar.plusWeeks(7).minusDays(1)),
                oppholdsperiode(OPPHOLDSTYPE_KVOTE_FELLESPERIODE_ANNEN_FORELDER, fpStartFar.plusWeeks(7), fpStartFar.plusWeeks(18).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fpStartFar.plusWeeks(18), fpStartFar.plusWeeks(23).minusDays(1)));
        Foreldrepenger foreldrepengerMor = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsDato), fordelingMor)
                .build();
        SøknadBuilder søknadMor = new SøknadBuilder(
                foreldrepengerMor,
                aktørIdMor,
                SøkersRolle.MOR);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                fnrMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        InntektsmeldingBuilder inntektsmeldingMorEn = lagInntektsmeldingBuilder(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrMor,
                fpStartMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr(),
                Optional.empty(), Optional.empty(), Optional.empty());
        InntektsmeldingBuilder inntektsmeldingMorTo = lagInntektsmeldingBuilder(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(1).getBeløp(),
                fnrMor,
                fpStartMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr(),
                Optional.empty(), Optional.empty(), Optional.empty());
        List<InntektsmeldingBuilder> inntektsmeldingBuilderListMor = new ArrayList<>();
        inntektsmeldingBuilderListMor.add(inntektsmeldingMorEn);
        inntektsmeldingBuilderListMor.add(inntektsmeldingMorTo);
        fordel.sendInnInntektsmeldinger(inntektsmeldingBuilderListMor, aktørIdMor, fnrMor, saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();

        Fordeling fordelingFar = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fpStartFar, fpStartFar.plusWeeks(6).minusDays(1)),
                graderingsperiodeArbeidstaker(STØNADSKONTOTYPE_FELLESPERIODE, fpStartFar.plusWeeks(6), fpStartFar.plusWeeks(18).minusDays(1),
                        testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr(), 50));
        Foreldrepenger foreldrepengerFar = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsDato),
                fordelingFar)
                .build();
        SøknadBuilder søknadFar = new SøknadBuilder(foreldrepengerFar, aktørIdFar, SøkersRolle.FAR);
        long saknsummerFar = fordel.sendInnSøknad(søknadFar.build(), aktørIdFar, fnrFar, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        InntektsmeldingBuilder inntektsmeldingFarEn = lagInntektsmeldingBuilder(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrFar,
                fpStartFar,
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr(),
                Optional.empty(), Optional.empty(), Optional.empty());
        InntektsmeldingBuilder inntektsmeldingFarTo = lagInntektsmeldingBuilder(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(1).getBeløp(),
                fnrFar,
                fpStartFar,
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr(),
                Optional.empty(), Optional.empty(), Optional.empty());
        List<InntektsmeldingBuilder> inntektsmeldingBuilderListFar = new ArrayList<>();
        inntektsmeldingBuilderListFar.add(inntektsmeldingFarEn);
        inntektsmeldingBuilderListFar.add(inntektsmeldingFarTo);
        fordel.sendInnInntektsmeldinger(inntektsmeldingBuilderListFar, aktørIdFar, fnrFar, saknsummerFar);

    }
    @Test
    public void testcase_mor_endringsSøknad_medGradering_FL_AAP() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("30");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStart = fødselsdato.minusWeeks(3);
        Opptjening opptjening = OpptjeningErketyper.medEgenNaeringOgFrilansOpptjening();
        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStart, fødselsdato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(35).minusDays(1)));
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato),
                fordeling)
                .medSpesiellOpptjening(opptjening)
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
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(
                5_000,
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStart,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr(),
                Optional.empty(), Optional.empty(), Optional.empty());
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING);
        saksbehandler.hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class)
                .godkjennOpptjening("FRILANS")
                .avvisOpptjening("NÆRING");
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderPerioderOpptjeningBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_AKTIVITETER);
        saksbehandler.hentAksjonspunktbekreftelse(AvklarAktiviteterBekreftelse.class)
            .godkjennOpptjeningsAktivitet("FRILANS")
            .avvisOpptjeningsAktivitet("NÆRING");
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarAktiviteterBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .behandleFrilansMottar(20_000);
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderFaktaOmBeregningBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntektFrilans(300_000)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummerMor);

        List<Aksjonspunkt> apSomSkalTilTotrinnskontroll = beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling();
        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(apSomSkalTilTotrinnskontroll);
        beslutter.fattVedtakOgVentTilAvsluttetBehandling();

        saksbehandler.ventTilAvsluttetBehandling();
        Fordeling fordelingEndring = generiskFordeling(
                FordelingErketyper.graderingsperiodeFL(
                        STØNADSKONTOTYPE_FELLESPERIODE,
                        fødselsdato.plusWeeks(24), fødselsdato.plusWeeks(28), 30),
                FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE,
                        fødselsdato.plusWeeks(28).plusDays(1), fødselsdato.plusWeeks(32).plusDays(1)));
        SøknadBuilder søknadEndring = SøknadErketyper.endringssøknadErketype(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                fordelingEndring,
                String.valueOf(saksnummerMor));
        fordel.sendInnSøknad(søknadEndring.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummerMor);

        saksbehandler.ventTilSakHarRevurdering();
        saksbehandler.velgRevurderingBehandling();

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_AKTIVITETER);
        saksbehandler.hentAksjonspunktbekreftelse(AvklarAktiviteterBekreftelse.class)
                .godkjennOpptjeningsAktivitet("FRILANS")
                .avvisOpptjeningsAktivitet("NÆRING");
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarAktiviteterBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .behandleFrilansMottar(20_000);
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderFaktaOmBeregningBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntektFrilans(300_000)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_UTTAKPERIODER);
        saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class)
                .delvisGodkjennAvslåAktivitetManuellePerioder("ANNET");
        saksbehandler.bekreftAksjonspunktBekreftelse(FastsettUttaksperioderManueltBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);

        beslutter.refreshFagsak();
        beslutter.velgRevurderingBehandling();
        List<Aksjonspunkt> apSomSkalTilTotrinnskontrollRevurdering = beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling();
        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(apSomSkalTilTotrinnskontrollRevurdering);
        beslutter.fattVedtakOgVentTilAvsluttetBehandling();
    }
    @Test
    public void testcase_morOgFar_vurderingOmAnnenPartHarRett() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("140");
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
                (new GraderingBuilder(STØNADSKONTOTYPE_FELLESPERIODE,fødsel.plusWeeks(18).plusDays(1), fødsel.plusWeeks(37))
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
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("140");

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
        rettigheter.setHarAnnenForelderRett(false);
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
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("75");
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
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("74");
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
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("74");
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
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("82");

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
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("82");

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
                new GraderingBuilder(STØNADSKONTOTYPE_FELLESPERIODE,
                        fpStartDatoFar, fpStartDatoFar.plusWeeks(9).minusDays(1))
                        .medGraderingArbeidstaker(orgnrFar, 50)
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
    @Test
    @DisplayName("Testcase koblet sak med oppholdsperioder")
    @Description("Test for behandling med oppholdsperioder i koblet sak, far søker bare delvis det mor ønsker")
    public void testcase_morOgFar_kobletsak_medOppholdsperioder() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("82");

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)),
                oppholdsperiode(OPPHOLDSTYPE_FEDREKVOTE_ANNEN_FORELDER, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(30).minusDays(1)));
        Foreldrepenger foreldrepengerMor = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordelingMor)
                .medAnnenForelder(testscenario.getPersonopplysninger().getAnnenPartAktørIdent())
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
        saksbehandler.ventTilAvsluttetBehandling();

        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(6);
        Fordeling fordelingFar = generiskFordeling(uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartDatoFar, fødselsdato.plusWeeks(10).minusDays(1)));
        Foreldrepenger foreldrepengerFar = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordelingFar)
                .build();
        SøknadBuilder søknadFar = new SøknadBuilder(
                foreldrepengerFar,
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                SøkersRolle.FAR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
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
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar,
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                saksnummerFar);
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

}
