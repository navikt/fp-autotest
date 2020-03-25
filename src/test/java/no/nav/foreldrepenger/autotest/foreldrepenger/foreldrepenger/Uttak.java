package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OppholdÅrsak.*;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FEDREKVOTE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.oppholdsperiode;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.overføringsperiode;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.makeInntektsmeldingFromTestscenario;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadEndringErketyper.lagEndringssøknad;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerTermin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.OverføringÅrsak;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EndringssøknadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.perioder.GraderingBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.perioder.UttaksperiodeBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.erketyper.RelasjonTilBarnetErketyper;
import no.nav.foreldrepenger.autotest.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AvklarAktiviteterBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
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
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Rettigheter;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg;

@Execution(ExecutionMode.CONCURRENT)
@Tag("utvikling")
@Tag("foreldrepenger")
public class Uttak extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor automatisk førstegangssøknad fødsel")
    @Description("Mor førstegangssøknad på fødsel")
    public void testcase_mor_fødsel() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("75");
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1))
                );

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(
                fødselsdato,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(), SøkersRolle.MOR)
                .medFordeling(fordeling);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdatoMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummerMor);
    }
    @Test
    @DisplayName("Mor automatisk førstegangssøknad termin")
    @Description("Mor førstegangssøknad på termin")
    public void testcase_mor_termin() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("74");
        LocalDate termindato = LocalDate.now().plusWeeks(4);
        LocalDate fpStartdatoMor = termindato.minusWeeks(3);

        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, termindato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, termindato, termindato.plusWeeks(8).minusDays(1)),
                uttaksperiode(FEDREKVOTE, termindato.plusWeeks(8), termindato.plusWeeks(13).minusDays(1)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(termindato,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR)
                .medFordeling(fordelingMor);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdatoMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr())
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(10_000));
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummerMor);
    }
    @Test
    public void testcase_morOgFar_utenRelasjon() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("500");
        TestscenarioDto testscenario2 = opprettTestscenario("550");

        var termindato = LocalDate.now().plusWeeks(3);
        var startDatoForeldrepenger = termindato.minusWeeks(3);
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();

        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        var søkerInntekt = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder()
                .get(0).getBeløp();
        var søkerOrgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold()
                .get(0).getArbeidsgiverOrgnr();

        var annenPartAktørId = testscenario2.getPersonopplysninger().getSøkerAktørIdent();
        var annenPartIdent = testscenario2.getPersonopplysninger().getSøkerIdent();
        var annenPartInntekt = testscenario2.getScenariodata().getInntektskomponentModell().getInntektsperioder()
                .get(0).getBeløp();
        var annenPartOrgNr = testscenario2.getScenariodata().getArbeidsforholdModell().getArbeidsforhold()
                .get(0).getArbeidsgiverOrgnr();

        var søknad = lagSøknadForeldrepengerTermin(termindato, søkerAktørId, SøkersRolle.MOR)
                .medAnnenForelder(annenPartAktørId);
        var im = lagInntektsmelding(søkerInntekt, søkerIdent, startDatoForeldrepenger, søkerOrgNr);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), søkerAktørId, søkerIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        fordel.sendInnInntektsmelding(im, søkerAktørId, søkerIdent, saksnummer);

        var startFar = LocalDate.now().plusWeeks(4);
        var fordeling = generiskFordeling(
                uttaksperiode(FEDREKVOTE, startFar, LocalDate.now().plusWeeks(10))
        );
        var søknadFar = lagSøknadForeldrepengerTermin(termindato, annenPartAktørId, SøkersRolle.FAR)
                .medAnnenForelder(søkerAktørId)
                .medFordeling(fordeling);
        var saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), annenPartAktørId, annenPartIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        var imFar = lagInntektsmelding(annenPartInntekt, annenPartIdent, startFar, annenPartOrgNr);
        fordel.sendInnInntektsmelding(imFar, annenPartAktørId, annenPartIdent, saksnummerFar);

    }
    @Test
    public void testcase_mor_stortinget() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("418");
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        var søkerInntekt = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder()
                .get(0).getBeløp();
        var søkerOrgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold()
                .get(0).getArbeidsgiverOrgnr();

        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, startDatoForeldrepenger, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(20)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørId, SøkersRolle.MOR)
                .medFordeling(fordeling);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), søkerAktørId, søkerIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        var im = lagInntektsmelding(søkerInntekt, søkerIdent, startDatoForeldrepenger, søkerOrgNr);
        fordel.sendInnInntektsmelding(im, søkerAktørId, søkerIdent, saksnummer);
    }

    @Test
    public void testcase_far_fødsel() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("140");
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var søkerAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        var søkerIdent = testscenario.getPersonopplysninger().getAnnenpartIdent();
        var startDatoForeldrepenger = LocalDate.now().plusWeeks(3);
        var søkerInntekt = testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder()
                .get(0).getBeløp();
        var søkerOrgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold()
                .get(0).getArbeidsgiverOrgnr();

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(FEDREKVOTE, startDatoForeldrepenger, startDatoForeldrepenger.plusWeeks(10).minusDays(1)),
                uttaksperiode(FELLESPERIODE, startDatoForeldrepenger.plusWeeks(10), startDatoForeldrepenger.plusWeeks(20))
        );
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(
                fødselsdato, søkerAktørId, SøkersRolle.FAR)
                .medFordeling(fordeling);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                søkerAktørId, søkerIdent,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                søkerInntekt,
                søkerIdent,
                startDatoForeldrepenger,
                søkerOrgNr);
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                søkerAktørId,
                søkerIdent,
                saksnummer);
    }

    @DisplayName("Søknadfrist med revurdering")
    @Test
    public void testcase_mor_søknadfrist_endringssøknad() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("500");
        var søkterAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();

        var inntektBeløp = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummer = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();

        var familiehendelseDato = LocalDate.now().minusMonths(9);
        var fpStartdato = familiehendelseDato.minusWeeks(3);

        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, familiehendelseDato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, familiehendelseDato, familiehendelseDato.plusWeeks(15).minusDays(1)));
        var søknad = lagSøknadForeldrepengerTermin(familiehendelseDato, søkterAktørId, SøkersRolle.MOR);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        var saksnummerSøker = fordel.sendInnSøknad(søknad.build(), søkterAktørId, søkerFnr, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        var inntektsmelding = lagInntektsmelding(inntektBeløp, søkerFnr, fpStartdato, orgNummer);
        fordel.sendInnInntektsmelding(inntektsmelding, søkterAktørId, søkerFnr, saksnummerSøker);
    }
    @DisplayName("Far søker om periode rett etter fødsel med en revurdering behandling")
    @Test
    public void testcase_far_tidligsøktePerioder() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("550");
        var søkterAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();

        var inntektBeløp = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummer = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();

        var familiehendelseDato = testscenario.getPersonopplysninger().getFødselsdato();
        var fpStartdato = familiehendelseDato;

        var fordeling = generiskFordeling(
                uttaksperiode(MØDREKVOTE, familiehendelseDato, familiehendelseDato.plusWeeks(15).minusDays(1)));
        var søknad = lagSøknadForeldrepengerTermin(familiehendelseDato, søkterAktørId, SøkersRolle.FAR)
                .medFordeling(fordeling);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        var saksnummerSøker = fordel.sendInnSøknad(søknad.build(), søkterAktørId, søkerFnr, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        var inntektsmelding = lagInntektsmelding(inntektBeløp, søkerFnr, fpStartdato, orgNummer);
        fordel.sendInnInntektsmelding(inntektsmelding, søkterAktørId, søkerFnr, saksnummerSøker);
    }
    @Test
    public void testcase_mor_papirsøknad() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("75");

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnSøknad(null,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
    }
    @Tag("KOR2020")
    @Test
    public void testcase_mor_KOR1_refusjon_arbeidsforholdID() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("202");
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordelingMor);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), aktørIdMor, fnrMor, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        InntektsmeldingBuilder im = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrMor, fødselsdato.minusWeeks(3),
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        im.medArbeidsforholdId(testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId());
        im.medRefusjonsBelopPerMnd(BigDecimal.valueOf(testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp()));
        fordel.sendInnInntektsmelding(im,aktørIdMor, fnrMor, saksnummer);

        InntektsmeldingBuilder im2 = lagInntektsmelding(
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
                FORELDREPENGER_FØR_FØDSEL, fpStartMor, fpStartMor.plusWeeks(3).minusDays(1));
        PermisjonPeriodeDto mk6 = new PermisjonPeriodeDto(
                MØDREKVOTE, fødselsdato, fødselsdato.plusDays(41));
        PermisjonPeriodeDto mk6_mer = new PermisjonPeriodeDto(
                MØDREKVOTE, fødselsdato.plusDays(42), fødselsdato.plusDays(49));
        GraderingPeriodeDto gradering1 = new GraderingPeriodeDto(
                FELLESPERIODE, fødselsdato.plusDays(42), fødselsdato.plusDays(120),
                BigDecimal.valueOf(70),
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr(),
                true, false, false, "ARBEIDSTAKER"
        );
//        GraderingPeriodeDto gradering2 = new GraderingPeriodeDto(
//                Stønadskonto.MØDREKVOTE, fødselsdato.plusDays(50), fødselsdato.plusDays(105),
//                BigDecimal.valueOf(70),
//                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr(),
//                true, false, false, "ARBEIDSTAKER"
//                );
        fordeling.permisjonsPerioder.add(før);
        fordeling.permisjonsPerioder.add(mk6);
        fordeling.graderingPeriode.add(gradering1);
        aksjonspunktBekreftelseMor.morSøkerFødsel(fordeling, fødselsdato, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelseMor);

        InntektsmeldingBuilder im = lagInntektsmelding(
                beløp1, fnrMor, morStartDato, org1)
                .medArbeidsforholdId(testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsforholdId())
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(beløp1))
                .medRefusjonsOpphordato(LocalDate.now().minusDays(7))
                ;
        fordel.sendInnInntektsmelding(im,aktørIdMor, fnrMor, saksnummer);

        InntektsmeldingBuilder im2 = lagInntektsmelding(
                beløp2, fnrMor, morStartDato, org2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(beløp2))
                .medArbeidsforholdId(arbeidsforholdID2)
                ;
        fordel.sendInnInntektsmelding(im2,aktørIdMor, fnrMor, saksnummer);
        InntektsmeldingBuilder im3 = lagInntektsmelding(
                beløp3, fnrMor, morStartDato, org3)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(beløp3))
                .medArbeidsforholdId(arbeidsforholdID3)
                ;
        fordel.sendInnInntektsmelding(im2,aktørIdMor, fnrMor, saksnummer);
    }
    @Test
    public void testcase_mor_flere_arbeidsforhold_endring_i_fordeling() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("165");
        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        List<Arbeidsforhold> arbeidsforhold = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold();
        Arbeidsforhold tilkommet = arbeidsforhold.get(0);
        String orgNr = tilkommet.getArbeidsgiverOrgnr();
        int inntektPerMåned = 60_000;
        BigDecimal refusjon = BigDecimal.valueOf(60_000);

        Opptjening opptjening = OpptjeningErketyper.medEgenNaeringOpptjening(
                false, BigInteger.valueOf(30_000), false);
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medSpesiellOpptjening(opptjening);

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        InntektsmeldingBuilder inntektsmeldingBuilder2 = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr).medRefusjonsBelopPerMnd(refusjon);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder2, testscenario, saksnummer);

        Fordeling fordelingEndring = new Fordeling();
        fordelingEndring.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordelingEndring.getPerioder();
        ;
        LocalDate graderingFom = fødselsdato.plusWeeks(6);
        perioder.add(uttaksperiode(MØDREKVOTE, fødselsdato, graderingFom.minusDays(1)));
        perioder.add(new GraderingBuilder(FELLESPERIODE.getKode(), graderingFom, fødselsdato.plusWeeks(10))
                .medGraderingSN(50)
                .build());
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
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(FORELDREPENGER, fødselsdato, fødselsdato.plusWeeks(15)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordeling);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), aktørIdMor, fnrMor, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder im = lagInntektsmelding(
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
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(15))
        );
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordeling)
                .medRettigheter(RettigheterErketyper.harIkkeAleneomsorgOgAnnenpartIkkeRett());

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), aktørIdFar, fnrFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder im = lagInntektsmelding(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrFar, fødselsdato.plusWeeks(6),
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr()
        );
        fordel.sendInnInntektsmelding(im, aktørIdFar, fnrFar, saksnummer);

    }
    @Test
    public void testcase_mor_overføring() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("140");
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                overføringsperiode(OverføringÅrsak.IKKE_RETT_ANNEN_FORELDER, FEDREKVOTE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(20).minusDays(1)),
                overføringsperiode(OverføringÅrsak.ALENEOMSORG, FEDREKVOTE, fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(22).minusDays(1))
        );
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordeling);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), aktørIdMor, fnrMor, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder im = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrMor, fødselsdato,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr()
        );
        fordel.sendInnInntektsmelding(im, aktørIdMor, fnrMor, saksnummer);
    }
    @Test
    public void testcase_farOgMor_farSøkerEtterMor_medMottatdatoFørMor() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("140");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        String aktørIdMor = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String aktørIdFar = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        String fnrFar = testscenario.getPersonopplysninger().getAnnenpartIdent();

        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10).minusDays(1)),
                oppholdsperiode(FEDREKVOTE_ANNEN_FORELDER, fødselsdato.plusWeeks(10), fødselsdato.plusWeeks(12).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(12), fødselsdato.plusWeeks(14).minusDays(1)),
                new UttaksperiodeBuilder(FELLESPERIODE.getKode(), fødselsdato.plusWeeks(14), fødselsdato.plusWeeks(16).minusDays(1))
                        .medSamtidigUttak(BigDecimal.valueOf(50))
                        .build());
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordelingMor);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                aktørIdMor,
                fnrMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrMor,
                fødselsdato,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                aktørIdMor, fnrMor,
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
                FEDREKVOTE, fpStartFar, fpStartFar.plusWeeks(2));
        PermisjonPeriodeDto fk2 = new PermisjonPeriodeDto(
                FEDREKVOTE, fødselsdato.plusWeeks(10), fødselsdato.plusWeeks(12).minusDays(1));
        PermisjonPeriodeDto fk3 = new PermisjonPeriodeDto(
                FEDREKVOTE, fødselsdato.plusWeeks(14), fødselsdato.plusWeeks(16).minusDays(1));
        PermisjonPeriodeDto fk4 = new PermisjonPeriodeDto(
                FEDREKVOTE, fødselsdato.plusWeeks(17), fødselsdato.plusWeeks(18));
        fordeling.permisjonsPerioder.add(fk);
        fordeling.permisjonsPerioder.add(fk2);
        fordeling.permisjonsPerioder.add(fk3);
        fordeling.permisjonsPerioder.add(fk4);
        aksjonspunktBekreftelseFar.morSøkerFødsel(fordeling, fødselsdato, fødselsdato);

        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelseFar);
        InntektsmeldingBuilder inntektsmeldingerFar = lagInntektsmelding(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrFar,
                fpStartFar,
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldingerFar,
                aktørIdFar,
                fnrFar,
                saksnummerFar);
    }

      @Test
    public void testcase_far_søker_ikke_om_uttak_7_uker_etter_fødsel() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("60");
        var aktørIdSøker = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        var fnrSøker = testscenario.getPersonopplysninger().getAnnenpartIdent();
        var inntekt = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNrSøker = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var søkersRolle = SøkersRolle.FAR;


        LocalDate fødselsDato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate startDato = fødselsDato.plusMonths(10);

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER, startDato, startDato.plusMonths(4)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsDato,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(), SøkersRolle.MOR)
                .medFordeling(fordeling);
        Long saksnummer =
                fordel.sendInnSøknad(
                søknad.build(), aktørIdSøker, fnrSøker, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        InntektsmeldingBuilder im = lagInntektsmelding(inntekt, fnrSøker, startDato, orgNrSøker);
        fordel.sendInnInntektsmelding(im, aktørIdSøker, fnrSøker, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_UTTAKPERIODER);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.avslåAlleManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        List<Aksjonspunkt> apSomSkalTilTotrinnskontroll = beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling();
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(apSomSkalTilTotrinnskontroll);
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        saksbehandler.opprettBehandlingRevurdering("RE-FRDLING");

        Fordeling endringsFordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER, startDato, startDato.plusWeeks(2).plusDays(1)));

        EndringssøknadBuilder søknadEndring = lagEndringssøknad(
                aktørIdSøker, søkersRolle, endringsFordeling, saksnummer.toString());
        fordel.sendInnSøknad(
                        søknadEndring.build(), aktørIdSøker, fnrSøker,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        InntektsmeldingBuilder im2 = lagInntektsmelding(inntekt, fnrSøker, startDato, orgNrSøker);
        fordel.sendInnInntektsmelding(im2, aktørIdSøker, fnrSøker, saksnummer);
    }
    @Test
    public void testcase_mor_søker_med_endringssøknad() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("140");
        var aktørIdSøker = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var fnrSøker = testscenario.getPersonopplysninger().getSøkerIdent();
        var inntekt = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNrSøker = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        var søkersRolle = SøkersRolle.MOR;


        Fordeling fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdSøker, SøkersRolle.MOR)
                .medFordeling(fordeling);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder im = lagInntektsmelding(
                inntekt, fnrSøker, fpStartdato, orgNrSøker);
        fordel.sendInnInntektsmelding(im, aktørIdSøker, fnrSøker, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        Fordeling fordelingEndring = generiskFordeling(
                uttaksperiode(FELLESPERIODE,
                        fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(10).minusDays(1)),
                overføringsperiode(OverføringÅrsak.IKKE_RETT_ANNEN_FORELDER, FEDREKVOTE,
                        fødselsdato.plusWeeks(10).plusDays(1), fødselsdato.plusWeeks(15)));

        EndringssøknadBuilder søknadE = lagEndringssøknad(aktørIdSøker, søkersRolle, fordelingEndring, saksnummer.toString());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummerE = fordel.sendInnSøknad(søknadE.build(), aktørIdSøker, fnrSøker, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventTilSakHarRevurdering();
    }
    @Test
    public void testcase_far_søker_med_endringssøknad() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("140");
        var aktørIdSøker = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        var fnrSøker = testscenario.getPersonopplysninger().getAnnenpartIdent();
        var inntekt = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNrSøker = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.plusWeeks(7);
        var søkersRolle = SøkersRolle.FAR;

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(FEDREKVOTE, fpStartdato, fpStartdato.plusWeeks(6)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdSøker, søkersRolle)
                .medFordeling(fordeling);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), aktørIdSøker, fnrSøker, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder im = lagInntektsmelding(
                inntekt, fnrSøker, fpStartdato, orgNrSøker);
        fordel.sendInnInntektsmelding(im, aktørIdSøker, fnrSøker, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        Fordeling fordelingEndring = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(10).minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato.plusWeeks(11), fødselsdato.plusWeeks(12).minusDays(1)));
        EndringssøknadBuilder søknadE = lagEndringssøknad(aktørIdSøker, søkersRolle, fordelingEndring, saksnummer.toString());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummerE = fordel.sendInnSøknad(søknadE.build(), aktørIdSøker, fnrSøker, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventTilSakHarRevurdering();
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
                uttaksperiode(MØDREKVOTE, fødselsDato, fødselsDato.plusWeeks(8).minusDays(1)),
                oppholdsperiode(FEDREKVOTE_ANNEN_FORELDER, fødselsDato.plusWeeks(8), fødselsDato.plusWeeks(10).minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsDato.plusWeeks(10), fødselsDato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsDato.plusWeeks(15), fødselsDato.plusWeeks(20).minusDays(1)),
                oppholdsperiode(FEDREKVOTE_ANNEN_FORELDER, fødselsDato.plusWeeks(20), fødselsDato.plusWeeks(24).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsDato.plusWeeks(24), fødselsDato.plusWeeks(28).minusDays(1)),
                oppholdsperiode(FEDREKVOTE_ANNEN_FORELDER, fødselsDato.plusWeeks(28), fødselsDato.plusWeeks(32).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsDato.plusWeeks(32), fødselsDato.plusWeeks(36)));

        ForeldrepengerBuilder søknadMor = lagSøknadForeldrepengerFødsel(fødselsDato, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordelingMor);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                fnrMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrMor,
                fpStartMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                aktørIdMor,
                fnrMor,
                saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();

        Fordeling fordelingFar = generiskFordeling(
                uttaksperiode(FEDREKVOTE, fødselsDato.plusWeeks(8), fødselsDato.plusWeeks(10).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fødselsDato.plusWeeks(20), fødselsDato.plusWeeks(24).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fødselsDato.plusWeeks(28), fødselsDato.plusWeeks(32).minusDays(1)));
        ForeldrepengerBuilder søknadFar = lagSøknadForeldrepengerFødsel(fødselsDato, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordelingFar);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(),
                aktørIdFar,
                fnrFar,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        InntektsmeldingBuilder inntektsmeldingerFar = lagInntektsmelding(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrFar,
                fpStartFar,
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldingerFar,
                aktørIdFar,
                fnrFar,
                saksnummerFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();

        Fordeling endringFordeling = generiskFordeling(
//                overføringsperiode(OverføringÅrsak.SYKDOM_ANNEN_FORELDER, Stønadskonto.MØDREKVOTE, fødselsDato.plusWeeks(7), fødselsDato.plusWeeks(8).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fødselsDato.plusWeeks(8), fødselsDato.plusWeeks(10).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fødselsDato.plusWeeks(20), fødselsDato.plusWeeks(24).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fødselsDato.plusWeeks(28), fødselsDato.plusWeeks(32).minusDays(1)));
        EndringssøknadBuilder søknadEndring = lagEndringssøknad(
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
                uttaksperiode(MØDREKVOTE, fødselsDato, fpStartFar.minusDays(1)),
                oppholdsperiode(FEDREKVOTE_ANNEN_FORELDER, fpStartFar, fpStartFar.plusWeeks(7).minusDays(1)),
                oppholdsperiode(FELLESPERIODE_ANNEN_FORELDER, fpStartFar.plusWeeks(7), fpStartFar.plusWeeks(18).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fpStartFar.plusWeeks(18), fpStartFar.plusWeeks(23).minusDays(1)));
        ForeldrepengerBuilder søknadMor = lagSøknadForeldrepengerFødsel(fødselsDato, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordelingMor);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor, fnrMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        InntektsmeldingBuilder inntektsmeldingMorEn = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrMor,
                fpStartMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        InntektsmeldingBuilder inntektsmeldingMorTo = lagInntektsmelding(
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
                uttaksperiode(FELLESPERIODE, fpStartFar, fpStartFar.plusWeeks(6).minusDays(1)),
                graderingsperiodeArbeidstaker(FELLESPERIODE, fpStartFar.plusWeeks(6), fpStartFar.plusWeeks(18).minusDays(1),
                        testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr(), 50));
        ForeldrepengerBuilder søknadFar = lagSøknadForeldrepengerFødsel(fødselsDato, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordelingFar);
        long saknsummerFar = fordel.sendInnSøknad(søknadFar.build(), aktørIdFar, fnrFar, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        InntektsmeldingBuilder inntektsmeldingFarEn = lagInntektsmelding(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                fnrFar,
                fpStartFar,
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        InntektsmeldingBuilder inntektsmeldingFarTo = lagInntektsmelding(
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
    public void testcase_mor_gradering() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("141");
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();

        LocalDate fødselsDato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate foreldrepengerStartDato = fødselsDato.minusWeeks(3);

        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, foreldrepengerStartDato, fødselsDato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsDato, fødselsDato.plusWeeks(10).minusDays(1)),
                graderingsperiodeArbeidstaker(
                        MØDREKVOTE, fødselsDato.plusWeeks(10), fødselsDato.plusWeeks(17).minusDays(1),
                        "342352362", 33),
                utsettelsesperiode(SøknadUtsettelseÅrsak.INSTITUSJON_BARN, fødselsDato.plusWeeks(17), fødselsDato.plusWeeks(20))
        );
        ForeldrepengerBuilder søknadMor = lagSøknadForeldrepengerFødsel(fødselsDato, søkerAktørIdent, SøkersRolle.MOR)
                .medFordeling(fordelingMor);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                søkerAktørIdent, søkerIdent,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        List<InntektsmeldingBuilder> im = makeInntektsmeldingFromTestscenario(testscenario, foreldrepengerStartDato);
        fordel.sendInnInntektsmeldinger(im, testscenario, saksnummerMor);

    }

    @Test
    public void testcase_mor_endringsSøknad_medGradering_FL_AAP() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("30");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStart = fødselsdato.minusWeeks(3);
        Opptjening opptjening = OpptjeningErketyper.medEgenNaeringOgFrilansOpptjening();
        Fordeling fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStart, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(35).minusDays(1)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(
                fødselsdato,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medSpesiellOpptjening(opptjening);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(
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
                        FELLESPERIODE,
                        fødselsdato.plusWeeks(24), fødselsdato.plusWeeks(28), 30),
                FordelingErketyper.uttaksperiode(FELLESPERIODE,
                        fødselsdato.plusWeeks(28).plusDays(1), fødselsdato.plusWeeks(32).plusDays(1)));
        EndringssøknadBuilder søknadEndring = lagEndringssøknad(
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
                uttaksperiode(FELLESPERIODE, fpStartMor, fpStartMor.plusWeeks(1).minusDays(1)),
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødsel.minusWeeks(3), fødsel.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødsel, fødsel.plusWeeks(14).minusDays(1)),
                utsettelsesperiode(SøknadUtsettelseÅrsak.FERIE, fødsel.plusWeeks(14), fødsel.plusWeeks(18)),
                (new GraderingBuilder(FELLESPERIODE.getKode(), fødsel.plusWeeks(18).plusDays(1), fødsel.plusWeeks(37))
                        .medGraderingArbeidstaker(orgNrMor, 40)
                        .medSamtidigUttak(true, BigDecimal.valueOf(60))
                        .build()),
                (new UttaksperiodeBuilder(MØDREKVOTE.getKode(), fødsel.plusDays(1).plusWeeks(37), fødsel.plusWeeks(38).plusDays(2)))
                        .medSamtidigUttak(BigDecimal.valueOf(60))
                        .build(),
                (new UttaksperiodeBuilder(FELLESPERIODE.getKode(), fødsel.plusWeeks(42).plusDays(2), fødsel.plusDays(2).plusWeeks(46))
                        .medSamtidigUttak(BigDecimal.valueOf(60))
                        .build()));
        ForeldrepengerBuilder søknadMor = lagSøknadForeldrepengerFødsel(fødsel, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordelingMor);
        long saksnummerMor = fordel.sendInnSøknad(
                søknadMor.build(),
                aktørIdMor,
                fnrMor,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
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
                uttaksperiode(FEDREKVOTE, fpStartFar, fpStartFar.plusWeeks(6).minusDays(1)),
                utsettelsesperiode(SøknadUtsettelseÅrsak.SYKDOM, fpStartFar.plusWeeks(6), fpStartFar.plusWeeks(10).minusDays(1)));

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødsel, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(aktørIdMor);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(), aktørIdFar, fnrFar, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(
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
                uttaksperiode(FEDREKVOTE, fpStartFar, fpStartFar.plusWeeks(6)),
                uttaksperiode(FEDREKVOTE, fpStartFar.plusWeeks(6), fpStartFar.plusWeeks(10).minusDays(1)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødsel, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordelingFar);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(), aktørIdFar, fnrFar, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(
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
                uttaksperiode(FEDREKVOTE, fpStartFar, fpStartFar.plusWeeks(6)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødsel, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordelingFar);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(), aktørIdFar, fnrFar, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(
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
                overføringsperiode(OverføringÅrsak.ALENEOMSORG, MØDREKVOTE, fpStartFar, fpStartFar.plusWeeks(10).minusDays(1)),
                overføringsperiode(OverføringÅrsak.IKKE_RETT_ANNEN_FORELDER, MØDREKVOTE, fpStartFar.plusWeeks(10), fpStartFar.plusWeeks(20).minusDays(1)),
                overføringsperiode(OverføringÅrsak.SYKDOM_ANNEN_FORELDER, MØDREKVOTE, fpStartFar.plusWeeks(20), fpStartFar.plusWeeks(30).minusDays(1)),
                overføringsperiode(OverføringÅrsak.INSTITUSJONSOPPHOLD_ANNEN_FORELDER, MØDREKVOTE, fpStartFar.plusWeeks(30), fpStartFar.plusWeeks(40).minusDays(1))
        );
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødsel, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordeling)
                .medAnnenForelder(aktørIdMor);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), aktørIdFar, fnrFar, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(
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
                utsettelsesperiode(SøknadUtsettelseÅrsak.SYKDOM, fødsel, fødsel.plusWeeks(14).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødsel.plusWeeks(14), fødsel.plusWeeks(20).minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødsel.plusWeeks(20), fødsel.plusWeeks(24).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødsel.plusWeeks(24), fødsel.plusWeeks(28).minusDays(1)));
        ForeldrepengerBuilder søknadMor = lagSøknadForeldrepengerFødsel(fødsel, aktørIdMor, SøkersRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(aktørIdFar);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), aktørIdMor, fnrMor, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);
        InntektsmeldingBuilder imMor= lagInntektsmelding(beløpMor, fnrMor, fødsel, orgNrMor);
        fordel.sendInnInntektsmelding(imMor, aktørIdMor, fnrMor, saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);

        Kode godkjenningskode = saksbehandler.kodeverk.UttakPeriodeVurderingType.getKode("PERIODE_OK");
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_FAKTA_UTTAK);
        List<KontrollerFaktaPeriode> faktaUttakPerioderList = saksbehandler.valgtBehandling.getKontrollerFaktaPerioderManuell();
        AvklarFaktaUttakBekreftelse avklarFaktaUttakBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class)
                .godkjennPeriode(faktaUttakPerioderList.get(0), godkjenningskode, true);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummerMor);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_FAKTA_UTTAK));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        saksbehandler.ventTilAvsluttetBehandling();

        Fordeling fordelingFar = generiskFordeling(
                overføringsperiode(OverføringÅrsak.SYKDOM_ANNEN_FORELDER, MØDREKVOTE, fødsel, fødsel.plusWeeks(14).minusDays(1)),
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødsel.plusWeeks(28), fødsel.plusWeeks(35).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fødsel.plusWeeks(35), fødsel.plusWeeks(45)));
        ForeldrepengerBuilder søknadFar = lagSøknadForeldrepengerFødsel(fødsel, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(aktørIdMor);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), aktørIdFar, fnrFar, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, null);

        InntektsmeldingBuilder imFar = lagInntektsmelding(
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

        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
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
                uttaksperiode(FORELDREPENGER, fpStartFar, fpStartFar.plusWeeks(15)));
        ForeldrepengerBuilder søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(), SøkersRolle.FAR);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(),
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                fpStartFar,
                testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getAnnenPartAktørIdent(),
                testscenario.getPersonopplysninger().getAnnenpartIdent(),
                saksnummerMor);

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
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(13).minusDays(1)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(), SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødselMedTermin(1, fødselsdato, fødselsdato.plusWeeks(10)));
           long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdatoMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummerMor);
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
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusDays(36)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(), SøkersRolle.MOR)
                .medFordeling(fordeling);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummerMor);

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
                uttaksperiode(MØDREKVOTE, termindato, termindato.plusDays(95)),
                uttaksperiode(FELLESPERIODE, termindato.plusDays(96), termindato.plusDays(96+90)));

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(fødselsdato,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(), SøkersRolle.MOR)
                .medFordeling(fordeling);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, fødselsdato)
                .setBegrunnelse("omg lol haha ja bacon");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderManglendeFodselBekreftelse.class);
    }



}
