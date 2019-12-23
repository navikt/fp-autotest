package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.*;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaStartdatoForForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTillegsopplysningerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaUttakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KontrollerFaktaPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.GraderingPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.OverføringÅrsak;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.GraderingBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.UttaksperiodeBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.ytelse.ForeldrepengerYtelseBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.*;
import no.nav.foreldrepenger.vtp.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper.*;

@Execution(ExecutionMode.CONCURRENT)
@Tag("utvikling")
@Tag("foreldrepenger")
public class Uttak extends ForeldrepengerTestBase {

    private static final Logger logger = LoggerFactory.getLogger(Uttak.class);

    // Testcaser
    @Tag("KOR2020")
    @Test
    public void testcase_mor_KOR1_refusjon_arbeidsforholdID() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("202");
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)));
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordelingMor).build();
        SøknadBuilder søknad = new SøknadBuilder(foreldrepenger, aktørIdMor, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), aktørIdMor, fnrMor, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        InntektsmeldingBuilder im = lagInntektsmeldingBuilder(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrMor, fødselsdato.minusWeeks(3),
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        im.medArbeidsforholdId(testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId());
        im.medRefusjonsBelopPerMnd(BigDecimal.valueOf(testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp()));
        fordel.sendInnInntektsmelding(im,aktørIdMor, fnrMor, saksnummer);

        InntektsmeldingBuilder im2 = lagInntektsmeldingBuilder(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrMor, fødselsdato.minusWeeks(3),
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr()
        );
        im2.medArbeidsforholdId(testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsforholdId());
        im2.medRefusjonsBelopPerMnd(BigDecimal.valueOf(testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp()));
        fordel.sendInnInntektsmelding(im2,aktørIdMor, fnrMor, saksnummer);

    }
    @Test
    public void testcase_mor_KOR_papirsøknad_gradering() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("202");

        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        var org1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var org2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr();
        var org3 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(2).getArbeidsgiverOrgnr();
        var arbeidsforholdID1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId();
        var arbeidsforholdID2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsforholdId();
        var arbeidsforholdID3 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(2).getArbeidsforholdId();
        var beløp1 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var beløp2 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(1).getBeløp();
        var beløp3 = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(2).getBeløp();
        var morStartDato = fødselsdato.minusWeeks(3);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnPapirsøknadForeldrepenger(testscenario, false);
        LocalDate fpStartMor = fødselsdato.minusWeeks(3);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        PapirSoknadForeldrepengerBekreftelse aksjonspunktBekreftelseMor =
                saksbehandler.aksjonspunktBekreftelse(PapirSoknadForeldrepengerBekreftelse.class);
        FordelingDto fordeling = new FordelingDto();
        PermisjonPeriodeDto før = new PermisjonPeriodeDto(
                STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartMor, fpStartMor.plusWeeks(3).minusDays(1));
        PermisjonPeriodeDto mk6 = new PermisjonPeriodeDto(
                STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusDays(41));
        PermisjonPeriodeDto mk6_mer = new PermisjonPeriodeDto(
                STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusDays(42), fødselsdato.plusDays(49));
        GraderingPeriodeDto gradering1 = new GraderingPeriodeDto(
                STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusDays(42), fødselsdato.plusDays(120),
                BigDecimal.valueOf(70),
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr(),
                true, false, false, "ARBEIDSTAKER"
        );
//        GraderingPeriodeDto gradering2 = new GraderingPeriodeDto(
//                STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusDays(50), fødselsdato.plusDays(105),
//                BigDecimal.valueOf(70),
//                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr(),
//                true, false, false, "ARBEIDSTAKER"
//                );
        fordeling.permisjonsPerioder.add(før);
        fordeling.permisjonsPerioder.add(mk6);
        fordeling.graderingPeriode.add(gradering1);
        aksjonspunktBekreftelseMor.morSøkerFødsel(fordeling, fødselsdato, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelseMor);

        InntektsmeldingBuilder im = lagInntektsmeldingBuilder(
                beløp1, fnrMor, morStartDato, org1)
                .medArbeidsforholdId(testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId())
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(beløp1))
                .medRefusjonsOpphordato(LocalDate.now().minusDays(7))
                ;
        fordel.sendInnInntektsmelding(im,aktørIdMor, fnrMor, saksnummer);

        InntektsmeldingBuilder im2 = lagInntektsmeldingBuilder(
                beløp2, fnrMor, morStartDato, org2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(beløp2))
                .medArbeidsforholdId(arbeidsforholdID2)
                ;
        fordel.sendInnInntektsmelding(im2,aktørIdMor, fnrMor, saksnummer);
        InntektsmeldingBuilder im3 = lagInntektsmeldingBuilder(
                beløp3, fnrMor, morStartDato, org3)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(beløp3))
                .medArbeidsforholdId(arbeidsforholdID3)
                ;
        fordel.sendInnInntektsmelding(im2,aktørIdMor, fnrMor, saksnummer);
    }
    @Test
    public void testcase_mor_annenForelderHarRett() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("140");
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER, fødselsdato, fødselsdato.plusWeeks(15))
        );
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato),
                fordeling
        )
                .medRettigheter(RettigheterErketyper.harIkkeAleneomsorgOgAnnenpartIkkeRett())
                .build();
        SøknadBuilder søknadBuilder = new SøknadBuilder(foreldrepenger, aktørIdMor, SøkersRolle.MOR);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknadBuilder.build(), aktørIdMor, fnrMor, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder im = lagInntektsmeldingBuilder(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrMor, fødselsdato.minusWeeks(3),
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr()
        );
        fordel.sendInnInntektsmelding(im, aktørIdMor, fnrMor, saksnummer);

    }
    @Test
    public void testcase_far_annenForelderHarRett() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("140");
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(15))
        );
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato),
                fordeling
        )
                .medAnnenForelder(aktørIdMor)
                .medRettigheter(RettigheterErketyper.harIkkeAleneomsorgOgAnnenpartIkkeRett())
                .build();
        SøknadBuilder søknadBuilder = new SøknadBuilder(foreldrepenger, aktørIdFar, SøkersRolle.FAR);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknadBuilder.build(), aktørIdFar, fnrFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder im = lagInntektsmeldingBuilder(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrMor, fødselsdato.plusWeeks(6),
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr()
        );
        fordel.sendInnInntektsmelding(im, aktørIdFar, fnrFar, saksnummer);

    }
    @Test
    public void testcase_mor_overføring() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("140");
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                overføringsperiode(OverføringÅrsak.IKKE_RETT_ANNEN_FORELDER, STØNADSKONTOTYPE_FEDREKVOTE,fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(20).minusDays(1)),
                overføringsperiode(OverføringÅrsak.ALENEOMSORG, STØNADSKONTOTYPE_FEDREKVOTE, fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(22).minusDays(1))
        );
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordeling
        ).build();
        SøknadBuilder søknad = new SøknadBuilder(
                foreldrepenger, aktørIdMor, SøkersRolle.MOR
        );

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), aktørIdMor, fnrMor, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder im = lagInntektsmeldingBuilder(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrMor, fødselsdato,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr()
        );
        fordel.sendInnInntektsmelding(im, aktørIdMor, fnrMor, saksnummer);
    }
    @Test
    public void testcase_mor_papirsøknad() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("140");

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnPapirsøknadForeldrepenger(testscenario, false);
    }
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
        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromtestscenariodata(
                testscenario.getScenariodata(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fødselsdato);
        fordel.sendInnInntektsmelding(
                inntektsmeldinger.get(0),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummerMor);

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

        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelseFar);
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
        LocalDate fpStartFar = fødselsDato.plusWeeks(8);

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
        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromtestscenariodata(
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
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromtestscenariodata(
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
                aktørIdFar,
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
        LocalDate fpStartFar = fødselsDato.plusWeeks(12);

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
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        InntektsmeldingBuilder inntektsmeldingMorTo = lagInntektsmeldingBuilder(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(1).getBeløp(),
                fnrMor,
                fpStartMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr());
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
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        InntektsmeldingBuilder inntektsmeldingFarTo = lagInntektsmeldingBuilder(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(1).getBeløp(),
                fnrFar,
                fpStartFar,
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr());
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
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING);
        saksbehandler.hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class)
                .godkjennOpptjening("FRILANS")
                .avvisOpptjening("NÆRING");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderPerioderOpptjeningBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_AKTIVITETER);
        saksbehandler.hentAksjonspunktbekreftelse(AvklarAktiviteterBekreftelse.class)
                .godkjennOpptjeningsAktivitet("FRILANS")
                .avvisOpptjeningsAktivitet("NÆRING");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarAktiviteterBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .behandleFrilansMottar(20_000);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderFaktaOmBeregningBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntektFrilans(300_000)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderBeregnetInntektsAvvikBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummerMor);

        List<Aksjonspunkt> apSomSkalTilTotrinnskontroll = beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling();
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(apSomSkalTilTotrinnskontroll);
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

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
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarAktiviteterBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .behandleFrilansMottar(20_000);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderFaktaOmBeregningBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntektFrilans(300_000)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderBeregnetInntektsAvvikBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.refreshFagsak();
        beslutter.velgRevurderingBehandling();
        List<Aksjonspunkt> apSomSkalTilTotrinnskontrollRevurdering = beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling();
        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(apSomSkalTilTotrinnskontrollRevurdering);
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }

    @Test
    public void testcase_morOgFar_samtidiguttak() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("140");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);

        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String orgNrMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();

        LocalDate fødsel = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartMor = fødsel.minusWeeks(4);

        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fpStartMor, fpStartMor.plusWeeks(1).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fødsel.minusWeeks(3), fødsel.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødsel, fødsel.plusWeeks(14).minusDays(1)),
                utsettelsesperiode(UTSETTELSETYPE_LOVBESTEMT_FERIE, fødsel.plusWeeks(14), fødsel.plusWeeks(18)),
                (new GraderingBuilder(STØNADSKONTOTYPE_FELLESPERIODE, fødsel.plusWeeks(18).plusDays(1), fødsel.plusWeeks(37))
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
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmeldingBuilder(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrMor,
                fpStartMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                aktørIdMor,
                fnrMor,
                saksnummerMor);

    }
    @Tag("faktaOmUttak")
    @Test
    public void testcase_far_faktaOmUttak_søktForTidelig() throws Exception{
        TestscenarioDto testscenario = opprettTestscenario("140");
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String orgNrFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate fødsel = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartFar = fødsel.plusWeeks(2);

        Fordeling fordelingFar = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartFar, fpStartFar.plusWeeks(6).minusDays(1)),
                utsettelsesperiode(UTSETTELSETYPE_SYKDOMSKADE, fpStartFar.plusWeeks(6), fpStartFar.plusWeeks(10).minusDays(1)));
        Foreldrepenger foreldrepengerFar = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødsel), fordelingFar)
                .medAnnenForelder(aktørIdMor)
                .build();
        SøknadBuilder søknadBuilder = new SøknadBuilder(
                foreldrepengerFar,
                aktørIdFar, SøkersRolle.FAR);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(
                søknadBuilder.build(), aktørIdFar, fnrFar, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrFar, fpStartFar, orgNrFar);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, aktørIdFar, fnrFar, saksnummer);
    }
    @Tag("faktaOmUttak")
    @Test
    public void testcase_far_faktaOmUttak_overLappendePerioder() throws Exception{
        TestscenarioDto testscenario = opprettTestscenario("140");
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String orgNrFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate fødsel = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartFar = fødsel.plusWeeks(7);

        Fordeling fordelingFar = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartFar, fpStartFar.plusWeeks(6)),
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartFar.plusWeeks(6), fpStartFar.plusWeeks(10).minusDays(1)));
        Foreldrepenger foreldrepengerFar = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødsel), fordelingFar)
                .build();
        SøknadBuilder søknadBuilder = new SøknadBuilder(
                foreldrepengerFar,
                aktørIdFar, SøkersRolle.FAR);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(
                søknadBuilder.build(), aktørIdFar, fnrFar, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrFar, fpStartFar, orgNrFar);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, aktørIdFar, fnrFar, saksnummer);
    }
    @Tag("faktaOmUttak")
    @Test
    public void testcase_far_faktaOmUttak_uttakFørSTF() throws Exception{
        TestscenarioDto testscenario = opprettTestscenario("140");
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String orgNrFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate fødsel = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartFar = fødsel.plusWeeks(7);

        Fordeling fordelingFar = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartFar, fpStartFar.plusWeeks(6)));
        Foreldrepenger foreldrepengerFar = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødsel), fordelingFar)
                .build();
        SøknadBuilder søknadBuilder = new SøknadBuilder(
                foreldrepengerFar,
                aktørIdFar, SøkersRolle.FAR);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(
                søknadBuilder.build(), aktørIdFar, fnrFar, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrFar, fpStartFar.minusWeeks(1), orgNrFar);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, aktørIdFar, fnrFar, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaStartdatoForForeldrepengerBekreftelse.class)
                .setStartdatoFraSoknad(fpStartFar.minusWeeks(1))
                .setBegrunnelse("Endret startdato for fp.");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaStartdatoForForeldrepengerBekreftelse.class);
    }
    @Tag("faktaOmUttak")
    @Test
    public void testcase_far_faktaOmUttak_overføringAvPerioder() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("140");
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String orgNrFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate fødsel = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartFar = fødsel.plusWeeks(7);

        Fordeling fordeling = generiskFordeling(
                overføringsperiode(OverføringÅrsak.ALENEOMSORG, STØNADSKONTOTYPE_MØDREKVOTE, fpStartFar, fpStartFar.plusWeeks(10).minusDays(1)),
                overføringsperiode(OverføringÅrsak.IKKE_RETT_ANNEN_FORELDER, STØNADSKONTOTYPE_MØDREKVOTE, fpStartFar.plusWeeks(10), fpStartFar.plusWeeks(20).minusDays(1)),
                overføringsperiode(OverføringÅrsak.SYKDOM_ANNEN_FORELDER, STØNADSKONTOTYPE_MØDREKVOTE, fpStartFar.plusWeeks(20), fpStartFar.plusWeeks(30).minusDays(1)),
                overføringsperiode(OverføringÅrsak.INSTITUSJONSOPPHOLD_ANNEN_FORELDER, STØNADSKONTOTYPE_MØDREKVOTE, fpStartFar.plusWeeks(30), fpStartFar.plusWeeks(40).minusDays(1))
        );
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødsel),
                fordeling)
                .medAnnenForelder(aktørIdMor)
                .build();
        SøknadBuilder søknadBuilder = new SøknadBuilder(foreldrepenger, aktørIdFar, SøkersRolle.FAR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknadBuilder.build(), aktørIdFar, fnrFar, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrFar, fpStartFar, orgNrFar);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, aktørIdFar, fnrFar, saksnummer);
    }
    @Tag("faktaOmUttak")
    @Test
    public void testcase_morOgFar_faktaOmUttak_farLagerHullPåMor() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("140");
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        Integer beløpMor = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        Integer beløpFar = testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        String orgNrMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        String orgNrFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate fødsel = testscenario.getPersonopplysninger().getFødselsdato();

        Fordeling fordelingMor = generiskFordeling(
                utsettelsesperiode(UTSETTELSETYPE_SYKDOMSKADE, fødsel, fødsel.plusWeeks(14).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødsel.plusWeeks(14), fødsel.plusWeeks(20).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødsel.plusWeeks(20), fødsel.plusWeeks(24).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødsel.plusWeeks(24), fødsel.plusWeeks(28).minusDays(1)));
        Foreldrepenger foreldrepengerMor = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødsel), fordelingMor)
                .medAnnenForelder(aktørIdFar)
                .build();
        SøknadBuilder søknadMor = new SøknadBuilder(foreldrepengerMor, aktørIdMor, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), aktørIdMor, fnrMor, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder imMor= lagInntektsmeldingBuilder(beløpMor, fnrMor, fødsel, orgNrMor);
        fordel.sendInnInntektsmelding(imMor, aktørIdMor, fnrMor, saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);

        Kode godkjenningskode = saksbehandler.kodeverk.UttakPeriodeVurderingType.getKode("PERIODE_OK");
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_FAKTA_UTTAK);
        List<KontrollerFaktaPeriode> faktaUttakPerioderList = saksbehandler.valgtBehandling.getKontrollerFaktaPerioderManuell();
        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class)
                .godkjennPeriode(faktaUttakPerioderList.get(0), godkjenningskode, true);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummerMor);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_FAKTA_UTTAK));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        saksbehandler.ventTilAvsluttetBehandling();

        Fordeling fordelingFar = generiskFordeling(
                overføringsperiode(OverføringÅrsak.SYKDOM_ANNEN_FORELDER, STØNADSKONTOTYPE_MØDREKVOTE, fødsel, fødsel.plusWeeks(14).minusDays(1)),
                utsettelsesperiode(UTSETTELSETYPE_ARBEID, fødsel.plusWeeks(28), fødsel.plusWeeks(35).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødsel.plusWeeks(35), fødsel.plusWeeks(45))
        );
        Foreldrepenger foreldrepengerFar = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødsel),
                fordelingFar
        )
                .medAnnenForelder(aktørIdMor)
                .build();
        SøknadBuilder søknadFar = new SøknadBuilder(
                foreldrepengerFar, aktørIdFar, SøkersRolle.FAR
        );
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), aktørIdFar, fnrFar, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder imFar = lagInntektsmeldingBuilder(
                beløpFar, fnrFar, fødsel, orgNrFar);
        fordel.sendInnInntektsmelding(imFar, aktørIdFar, fnrFar, saksnummerFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_FAKTA_UTTAK);
        List<KontrollerFaktaPeriode> faktaUttakPerioderListFar = saksbehandler.valgtBehandling.getKontrollerFaktaPerioderManuell();
        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class)
                .godkjennPeriode(faktaUttakPerioderListFar.get(0), godkjenningskode, true);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class);

    }
    @Test
    @DisplayName("Mor får ES og far søker om FP med MS")
    @Description("Mor får innvilget ES og far søker om foreldrepenger, men far søker ikke tidlig nok")
    public void testcase_morES_farFP() throws Exception {
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
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaTillegsopplysningerBekreftelse.class);
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
                SoekersRelasjonErketyper.fødselMedTermin(1, fødselsdato, fødselsdato.plusWeeks(10)), fordeling)
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
                uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, termindato.plusWeeks(8), termindato.plusWeeks(13).minusDays(1)));
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
    @Description("Testcase hvor mor søker på termin men fødsel har skjedd. Ikke barn i TPS")
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
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderManglendeFodselBekreftelse.class);
    }
    @Test
    @DisplayName("Flytting pga fødsel")
    @Description("Testcase hvor mor søker på termin men fødsel har skjedd. Ikke barn i TPS")
    public void testcase_mor_flyttingAvPerioder_fødsel_TFP_2070() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("140");

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate termindato = fødselsdato.plusDays(13);
        LocalDate fpStartdato = termindato;

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato, termindato.plusDays(95)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusDays(96), termindato.plusDays(96+90)));
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.termin(1, termindato),
                fordeling)
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

        List<InntektsmeldingBuilder> inntektsmedlinger = makeInntektsmeldingFromTestscenario(testscenario, fpStartdato);
        fordel.sendInnInntektsmeldinger(inntektsmedlinger, testscenario, saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, fødselsdato)
                .setBegrunnelse("omg lol haha ja bacon");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderManglendeFodselBekreftelse.class);
    }
    @Test
    @DisplayName("Flytting pga fødsel")
    @Description("Testcase hvor mor søker på termin men fødsel har skjedd. Ikke barn i TPS")
    public void testcase_mor_flyttingAvPerioder_fødsel_endringssøknad() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("140");
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        Integer beløpMor = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        Integer beløpFar = testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        String orgNrMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        String orgNrFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate termindato = fødselsdato.plusDays(14);
        LocalDate fpStartdato = termindato.minusWeeks(3);

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato, termindato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato, termindato.plusWeeks(19).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusWeeks(19), termindato.plusWeeks(19+18).minusDays(1)));
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.termin(1, termindato),
                fordeling)
                .medAnnenForelder(aktørIdFar)
                .medDekningsgrad("80")
                .build();
        SøknadBuilder søknad = new SøknadBuilder(
                foreldrepenger,
                aktørIdMor,
                SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                aktørIdMor,
                fnrMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        List<InntektsmeldingBuilder> inntektsmedlinger = makeInntektsmeldingFromTestscenario(testscenario, fpStartdato);
        fordel.sendInnInntektsmeldinger(inntektsmedlinger, testscenario, saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, fødselsdato)
                .setBegrunnelse("omg lol haha ja bacon");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderManglendeFodselBekreftelse.class);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummerMor);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        saksbehandler.ventTilAvsluttetBehandling();

        Fordeling fordelingFar = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER, fødselsdato.plusWeeks(19+4), fødselsdato.plusWeeks(19+4+2).minusDays(1)));
        Foreldrepenger foreldrepengerFar = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordelingFar).build();
        SøknadBuilder søkerFar = new SøknadBuilder(
                foreldrepengerFar, aktørIdFar, SøkersRolle.FAR
        );



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

    @Deprecated
    private long lagOgSendIm(TestscenariodataDto testscenariodata, long saksnummer,
                             String aktørId, String fnr, LocalDate fpStartdato) throws Exception {
        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromtestscenariodata(
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
