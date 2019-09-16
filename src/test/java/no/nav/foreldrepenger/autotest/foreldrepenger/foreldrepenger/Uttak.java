package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper.*;
import static no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.erketyper.ForeldrepengeYtelseErketyper.standardAnnenForelder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import io.qameta.allure.Description;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.*;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.builders.GraderingBuilder;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.erketyper.*;
import no.nav.foreldrepenger.fpmock2.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.fpmock2.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.builders.UttaksperiodeBuilder;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.soeknad.ForeldrepengesoknadBuilder;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingBuilder;
import no.nav.foreldrepenger.fpmock2.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.fpmock2.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.ObjectFactory;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Utsettelsesperiode;

@Execution(ExecutionMode.CONCURRENT)
@Tag("utvikling")
@Tag("foreldrepenger")
public class Uttak extends ForeldrepengerTestBase {
    // Testcaser
    @Test
    @DisplayName("Mor automatisk førstegangssøknad fødsel")
    @Description("Mor førstegangssøknad på fødsel")
    public void testcase_mor_fødsel() throws Exception {
        TestscenarioDto testscenario = opprettScenario("75");
        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);
        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(13).minusDays(1)));
        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(morAktørId, fordelingMor, SoekersRelasjonErketyper.fødsel(1,fødselsdato) );
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
    }
    @Test
    @DisplayName("Mor fødselshendelse")
    @Description("Mor fødselshendelse")
    public void testcase_mor_fødselHendelse() throws Exception {
        TestscenarioDto testscenario = opprettScenario("75");
        Søker søker = new SøkerBuilder(testscenario, false)
                .medBarn()
                .medArbeidsforohld()
                .build();
        LocalDate fpStartdatoMor = søker.dato.minusWeeks(3);
        søker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL,
                fpStartdatoMor, søker.dato.minusDays(1)));
        søker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE,
                søker.dato, søker.dato.plusWeeks(8).minusDays(1)));
        søker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE,
                søker.dato.plusWeeks(8), søker.dato.plusWeeks(13).minusDays(1)));
        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(søker.aktørID,
                søker.getSisteFordeling(), SoekersRelasjonErketyper.fødsel(1,søker.dato) );
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        saksbehandler.sendFødselsHendelse(søker.aktørID, søker.dato);
    }
    @Test
    @DisplayName("Mor prematuruker")
    @Description("Mor prematuruker")
    public void testcase_mor_prematuruker_fødsel_8_uker_før_termin() throws Exception {
        TestscenarioDto testscenario = opprettScenario("75");
        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);
        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(13).minusDays(1)));
        Foedsel soekersRelasjonTilBarnet = SoekersRelasjonErketyper.fødselMedTermin(1, fødselsdato, fødselsdato.plusWeeks(8));
        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(morAktørId, fordelingMor, soekersRelasjonTilBarnet);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
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
        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate termindato = LocalDate.now().plusWeeks(4);
        LocalDate fpStartdatoMor = termindato.minusWeeks(3);
        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, termindato.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato, termindato.plusWeeks(8).minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, termindato.plusWeeks(8), termindato.plusWeeks(13).minusDays(1)));
        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(morAktørId, fordelingMor, SoekersRelasjonErketyper.søkerTermin(1,termindato) );
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
    }
    @Test
    @DisplayName("Mor automatisk førstegangssøknad termin")
    @Description("Mor førstegangssøknad på termin")
    public void testcase_mor_fødsel_utenBarnTPS() throws Exception {
        TestscenarioDto testscenario = opprettScenario("74");
        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusWeeks(4);
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);
        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(13).minusDays(1)));
        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(morAktørId, fordelingMor, SoekersRelasjonErketyper.fødsel(1,fødselsdato) );
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
    }
    @Test
    @DisplayName("Mor søker for sent")
    @Description ("Testcase hvor Søknadsfrist må vurderes for mor")
    public void testcase_mor_søkerForSent() throws Exception {
        TestscenarioDto testscenario = opprettScenario("74");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(5);
        LocalDate fpStartdato = fødselsdato;

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10).minusDays(1)));
        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.uttakMedFordeling(søkerAktørID, fordeling, SoekersRelasjonErketyper.fødsel(1, fødselsdato));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        List<InntektsmeldingBuilder> inntektsmedlinger = makeInntektsmeldingFromTestscenario(testscenario, fpStartdato);
        fordel.sendInnInntektsmeldinger(inntektsmedlinger, testscenario, saksnummer);

        //Automatisk behandling av Fakta om fødsel siden det ikke er registert barn i TPS
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, fødselsdato)
                .setBegrunnelse("omg lol haha ja bacon");
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderManglendeFodselBekreftelse.class);
    }
    @Test
    @DisplayName("Flytting pga fødsel")
    @Description ("Testcase hvor mor søker på termin men fødsel har skjedd. Ikke barn i TPS")
    public void testcase_mor_fødsel_flyttingAvPerioderGrunnetFødsel() throws Exception {
        TestscenarioDto testscenario = opprettScenario("74");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusWeeks(3);
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusDays(36)));
        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.uttakMedFordeling(søkerAktørID, fordeling, SoekersRelasjonErketyper.fødsel(1, fødselsdato));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        List<InntektsmeldingBuilder> inntektsmedlinger = makeInntektsmeldingFromTestscenario(testscenario, fpStartdato);
        fordel.sendInnInntektsmeldinger(inntektsmedlinger, testscenario, saksnummer);

        //Automatisk behandling av Fakta om fødsel siden det ikke er registert barn i TPS
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, fødselsdato.minusMonths(1))
                .setBegrunnelse("omg lol haha ja bacon");
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderManglendeFodselBekreftelse.class);
    }
    @Test
    @DisplayName("Testcase alenefar - søker med manglende periode")
    @Description("Alenefar søker med manglende perioder i starten")
    public void testcase_alenefar_fødsel_manglerPerioder() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(9);
        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER, fpStartDatoFar, fpStartDatoFar.plusWeeks(6).minusDays(1)));
        ForeldrepengesoknadBuilder søknadFar = foreldrepengeSøknadErketyper.uttakMedFordeling(farAktørId, new UkjentForelder(), fordeling, RettigheterErketyper.harAleneOmsorgOgEnerett(), SoekersRelasjonErketyper.søkerTermin(1, fødselsdato));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknadFar.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        List<InntektsmeldingBuilder> inntektsmedlinger = makeInntektsmeldingFromTestscenario(testscenario, fpStartDatoFar);
        fordel.sendInnInntektsmeldinger(inntektsmedlinger, testscenario, saksnummer);
    }
    @Test
    @DisplayName("Testcase for koblet sak")
    @Description("Mor og far søker etter fødsel med ett arbeidsforhold hver. 100% dekningsgrad.")
    public void testcase_morOgFar_etterFødsel_kantTilKant() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");

        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.fodselfunnetstedUttakKunMor(morAktørId, fødselsdato);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String farFnr = testscenario.getPersonopplysninger().getAnnenpartIdent();
        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(10).plusDays(1);

        ForeldrepengesoknadBuilder søknadFar = foreldrepengeSøknadErketyper.fodselfunnetstedFarMedMor(farAktørId, morAktørId,
                fødselsdato, LocalDate.now(), fordelingFarHappycaseKobletMedMorHappycase(fødselsdato));
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

        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();

        LocalDate familiehendelse = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = familiehendelse.minusWeeks(3);
        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordeling.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, familiehendelse.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, familiehendelse, familiehendelse.plusWeeks(6).minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, familiehendelse.plusWeeks(6), familiehendelse.plusWeeks(22).minusDays(1)));
        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(morAktørId, farAktørId, fordeling, SoekersRelasjonErketyper.fødsel(1, familiehendelse));

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
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

        ForeldrepengesoknadBuilder søknadFar = foreldrepengeSøknadErketyper.uttakMedFordeling(farAktørId, morAktørId, fordelingFar, SoekersRelasjonErketyper.fødsel(1, familiehendelse));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farFnr, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farFnr, fpStartDatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farFnr, saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
    }
    @Test
    @DisplayName("Testcase for koblet sak med AP i uttak")
    @Description("Mor og far søker etter fødsel med ett arbeidsforhold hver. 100% dekningsgrad. Med AP i uttak")
    public void testcase_morOgFar_etterFødsel_kantTilKant_APfar() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");

        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.fodselfunnetstedUttakKunMor(morAktørId, fødselsdato);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String farFnr = testscenario.getPersonopplysninger().getAnnenpartIdent();
        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(10).plusDays(1);

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartDatoFar, fpStartDatoFar.plusWeeks(6).minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fpStartDatoFar.plusWeeks(6), fpStartDatoFar.plusWeeks(10)));

        ForeldrepengesoknadBuilder søknadFar = foreldrepengeSøknadErketyper.fodselfunnetstedFarMedMor(farAktørId, morAktørId, fødselsdato, LocalDate.now(), fordeling);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);

        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farFnr, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farFnr, fpStartDatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farFnr, saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgFørstegangsbehandling();
        //saksbehandler.ventTilBehandlingsstatus("AVSLU");

    }
    @Test
    @DisplayName("Testcase for koblet sak hvor far stjeler dager")
    @Description("Mor og far søker etter fødsel med ett arbeidsforhold hver. Far stjeler dager.")
    public void testcase_morOgFar_etterFødsel_overlapp() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");

        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.fodselfunnetstedUttakKunMor(morAktørId, fødselsdato);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String farFnr = testscenario.getPersonopplysninger().getAnnenpartIdent();
        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(6).plusDays(1);

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartDatoFar, fpStartDatoFar.plusWeeks(1).minusDays(1)));

        ForeldrepengesoknadBuilder søknadFar = foreldrepengeSøknadErketyper.fodselfunnetstedFarMedMor(farAktørId, morAktørId, fødselsdato, LocalDate.now(), fordeling);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);

        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farFnr, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farFnr, fpStartDatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farFnr, saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgFørstegangsbehandling();
        //saksbehandler.ventTilBehandlingsstatus("AVSLU");
    }
    @Test
    @DisplayName("Testcase for trekkdager i koblet sak")
    @Description("Far søker med overlapp og mister dager til mor, laget for TFP-390")
    public void testcase_morOgFar_kobletsak() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");

        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate termindato = fødselsdato.plusDays(1);
        LocalDate fpStartdatoMor = termindato.minusWeeks(3);

        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, termindato.minusDays(1)));
        perioderMor.add(new UttaksperiodeBuilder()
                .medTidsperiode(termindato, termindato.plusWeeks(15).minusDays(1))
                .medStønadskontoType(STØNADSKONTOTYPE_MØDREKVOTE)
                .medSamtidigUttak(true, BigDecimal.valueOf(100))
                .build());
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusWeeks(15), termindato.plusWeeks(24).plusDays(1)));
        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(morAktørId, farAktørId, fordelingMor, SoekersRelasjonErketyper.søkerTermin(1,termindato) );

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();

        String farFnr = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String orgnrFar = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(6);

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(new GraderingBuilder()
                .medGraderingArbeidstaker(orgnrFar, 50)
                .medTidsperiode(fpStartDatoFar, fpStartDatoFar.plusWeeks(9).minusDays(1))
                .medStønadskontoType(STØNADSKONTOTYPE_FELLESPERIODE)
                .medSamtidigUttak(true, BigDecimal.valueOf(50))
                .build());
        perioder.add(FordelingErketyper.oppholdsperiode(OPPHOLDSTYPE_KVOTE_FELLESPERIODE_ANNEN_FORELDER, fpStartDatoFar.plusWeeks(9), fpStartDatoFar.plusWeeks(18).plusDays(2)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartDatoFar.plusWeeks(18).plusDays(3), fpStartDatoFar.plusWeeks(33).plusDays(2)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fpStartDatoFar.plusWeeks(33).plusDays(3), fpStartDatoFar.plusWeeks(35).plusDays(1)));

        ForeldrepengesoknadBuilder søknadFar = foreldrepengeSøknadErketyper.fodselfunnetstedFarMedMor(farAktørId, morAktørId, fødselsdato, LocalDate.now(), fordeling);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);

        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farFnr, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farFnr, fpStartDatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farFnr, saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgFørstegangsbehandling();
        //saksbehandler.ventTilBehandlingsstatus("AVSLU");
    }
    @Test
    @DisplayName("Testcase koblet sak med oppholdsperioder")
    @Description("Test for behandling med oppholdsperioder i koblet sak, far søker bare delvis det mor ønsker")
    public void testcase_morOgFar_kobletsak_medOppholdsperioder() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");
        Søker morSøker = new SøkerBuilder(testscenario, false)
                .medArbeidsforohld()
                .medBarn()
                .medAnnenForelder()
                .build();
        LocalDate fødselsdato = morSøker.dato;
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);
        morSøker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        morSøker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        morSøker.perioderRef.add(FordelingErketyper.oppholdsperiode(OPPHOLDSTYPE_FEDREKVOTE_ANNEN_FORELDER, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(15).minusDays(1)));
        morSøker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(30).minusDays(1)));
        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(morSøker.aktørID, morSøker.annenForelderAktørID, morSøker.getSisteFordeling(), SoekersRelasjonErketyper.fødsel(1,fødselsdato) );
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingerMor =  lagInntektsmeldingBuilder(morSøker.getInntektPerMåned(0), morSøker.ident, fpStartdatoMor, morSøker.getOrgNr(0), Optional.of(morSøker.getArbeidsforholdID(0)), Optional.empty(), Optional.empty());
        fordel.sendInnInntektsmeldinger(Arrays.asList(inntektsmeldingerMor), testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        Søker farSøker = new SøkerBuilder(testscenario, true)
                .medBarn()
                .medArbeidsforohld()
                .medAnnenForelder()
                .build();
        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(6);
        farSøker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartDatoFar, fødselsdato.plusWeeks(10).minusDays(1)));
        ForeldrepengesoknadBuilder søknadFar = foreldrepengeSøknadErketyper.fodselfunnetstedFarMedMor(farSøker.aktørID, farSøker.annenForelderAktørID, fødselsdato, LocalDate.now(), farSøker.getSisteFordeling());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farSøker.aktørID, farSøker.ident, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingerFar =  lagInntektsmeldingBuilder(farSøker.getInntektPerMåned(0), farSøker.ident, fpStartDatoFar, farSøker.getOrgNr(0), Optional.of(farSøker.getArbeidsforholdID(0)), Optional.empty(), Optional.empty());
        fordel.sendInnInntektsmeldinger(Arrays.asList(inntektsmeldingerFar), farSøker.aktørID, farSøker.ident, saksnummerFar);
    }
    @Test
    @DisplayName("Testcase endringssøknader i koblet sak")
    @Description("Test for rekkefølge på behandling ved at begge sender endringssøknader i koblet sak.")
    public void testcase_morOgFar_kobletsak_medSamtidigEndringssøknader() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");
        Søker morSøker = new SøkerBuilder(testscenario, false)
                .medArbeidsforohld()
                .medBarn()
                .medAnnenForelder()
                .build();

        LocalDate fødselsdato = morSøker.dato;
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);



        SoekersRelasjonTilBarnet relasjon = SoekersRelasjonErketyper.fødsel(1,fødselsdato);
        morSøker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        morSøker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6)));
        morSøker.perioderRef.add(FordelingErketyper.utsettelsesperiode(UTSETTELSETYPE_ARBEID, fødselsdato.plusWeeks(6).plusDays(1), fødselsdato.plusWeeks(15).minusDays(1)));
        morSøker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(30).minusDays(1)));
        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(morSøker.aktørID, morSøker.annenForelderAktørID, morSøker.getSisteFordeling(), relasjon);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingerMor =  lagInntektsmeldingBuilder(morSøker.getInntektPerMåned(0), morSøker.ident, fpStartdatoMor, morSøker.getOrgNr(0), Optional.of(morSøker.getArbeidsforholdID(0)), Optional.empty(), Optional.empty());
        fordel.sendInnInntektsmeldinger(Arrays.asList(inntektsmeldingerMor), testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        Søker farSøker = new SøkerBuilder(testscenario, true)
                .medArbeidsforohld()
                .medAnnenForelder()
                .build();
        LocalDate fpStartDatoFar = fødselsdato.plusWeeks(6);
        farSøker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartDatoFar, fødselsdato.plusWeeks(10).minusDays(1)));
        ForeldrepengesoknadBuilder søknadFar = foreldrepengeSøknadErketyper.fodselfunnetstedFarMedMor(farSøker.aktørID, farSøker.annenForelderAktørID, fødselsdato, LocalDate.now(), farSøker.getSisteFordeling());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farSøker.aktørID, farSøker.ident, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingerFar =  lagInntektsmeldingBuilder(farSøker.getInntektPerMåned(0), farSøker.ident, fpStartDatoFar, farSøker.getOrgNr(0), Optional.of(farSøker.getArbeidsforholdID(0)), Optional.empty(), Optional.empty());
        fordel.sendInnInntektsmeldinger(Arrays.asList(inntektsmeldingerFar), farSøker.aktørID, farSøker.ident, saksnummerFar);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilSakHarRevurdering();
        saksbehandler.velgRevurderingBehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        LocalDate endringssøknadMorStart = fødselsdato.plusWeeks(15);
        morSøker.nyFordeling();
        morSøker.perioderRef.add(utsettelsesperiode(UTSETTELSETYPE_LOVBESTEMT_FERIE, endringssøknadMorStart, endringssøknadMorStart.plusWeeks(2).minusDays(1)));
        morSøker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, endringssøknadMorStart.plusWeeks(2), endringssøknadMorStart.plusWeeks(6).minusDays(1)));
        morSøker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, endringssøknadMorStart.plusWeeks(6), endringssøknadMorStart.plusWeeks(6).plusDays(1)));
        ForeldrepengesoknadBuilder søknadEndringMor = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(morSøker.aktørID, morSøker.getSisteFordeling(), saksnummerMor.toString());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnSøknad(søknadEndringMor.build(), morSøker.aktørID, morSøker.ident, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummerMor);

        LocalDate endringssøknadFarStart = fødselsdato.plusWeeks(10);
        farSøker.nyFordeling();
        farSøker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, endringssøknadFarStart, endringssøknadFarStart.plusWeeks(5).minusDays(1)));
        ForeldrepengesoknadBuilder søknadEndringFar = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(farSøker.aktørID, farSøker.getSisteFordeling(), saksnummerMor.toString());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnSøknad(søknadEndringFar.build(), farSøker.aktørID, farSøker.ident, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummerFar);

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
    @Test
    @DisplayName("Testcase mor tom for dager")
    @Description("Mor går tom for dager, søker mer av kontoen senere også")
    public void testcase_mor_fødsel_tomForDager() throws Exception {

        TestscenarioDto testscenario = opprettScenario("50");

        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(23).minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusWeeks(23), fødselsdato.plusWeeks(33).minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(33), fødselsdato.plusWeeks(34).minusDays(1)));

        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.fodselfunnetstedUttakKunMor(morAktørId, fordeling, fødselsdato);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
    }
    @Test
    @DisplayName("Testcase mor - søker med manglende perioder")
    @Description("Mor søker med manglende perioder i starten")
    public void testcase_mor_fødsel_manglerPerioder() throws Exception {
        TestscenarioDto testscenario = opprettScenario("50");

        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(7)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusWeeks(10), fødselsdato.plusWeeks(11)));


        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.fodselfunnetstedUttakKunMor(morAktørId, fordeling, fødselsdato);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
    }
    @Test
    @DisplayName("Mor søker på termin men barn finnes, test for flytting av perioder")
    @Description("Mor sender førstegangssøknad på termin, kan brukes for å teste flytting")
    public void testcase_mor_fødsel_flyttingFødsel() throws Exception {
        //TODO FIKSE på periodene og legge til IM med utsettelse
        TestscenarioDto testscenario = opprettScenario("78");
        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate termindato = fødselsdato.plusWeeks(1);
        LocalDate fpStartdatoMor = termindato.minusWeeks(3);

        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, termindato.minusWeeks(3), termindato.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato, termindato.plusWeeks(2)));
        perioderMor.add(new UttaksperiodeBuilder()
                .medTidsperiode(termindato.plusWeeks(2).plusDays(1), termindato.plusWeeks(9))
                .medStønadskontoType(STØNADSKONTOTYPE_FELLESPERIODE)
                .medSamtidigUttak(true, BigDecimal.valueOf(100))
                .build());
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato.plusWeeks(9).plusDays(1), termindato.plusWeeks(23)));

        LocalDate utsettelseFOM = termindato.plusWeeks(23).plusDays(1);
        LocalDate utsettelseTOM = termindato.plusWeeks(27);
        Utsettelsesperiode utsettelse = utsettelsesperiode(UTSETTELSETYPE_ARBEID, utsettelseFOM, utsettelseTOM);
        perioderMor.add(utsettelse);
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato.plusWeeks(27).plusDays(1), termindato.plusWeeks(30).minusDays(1)));
        perioderMor.add(new UttaksperiodeBuilder()
                .medTidsperiode(termindato.plusWeeks(30),termindato.plusWeeks(50))
                .medStønadskontoType(STØNADSKONTOTYPE_FELLESPERIODE)
                .medFlerbarnsdager(true)
                .build());
        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(morAktørId, fordelingMor, SoekersRelasjonErketyper.søkerTermin( 2,termindato) );
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
    }
    @Test
    @DisplayName("Mor og fra søker om samtidig uttak med overlapp")
    @Description("Mor søker om samtidig uttak i førstegangssøknaden og far søker overlapp i denne perioden")
    public void testcase_morOgFar_samtidigUttak_overlappendePerioder() throws Exception {
        TestscenarioDto testscenario = opprettScenario("85");
        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String farFnr = testscenario.getPersonopplysninger().getAnnenpartIdent();

        LocalDate familiehendelse = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = familiehendelse.minusWeeks(3);
        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        LocalDate samtidigUttakFOM = familiehendelse.plusWeeks(6);
        LocalDate samtidigUttakTOM = familiehendelse.plusWeeks(14).minusDays(1);
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, familiehendelse.minusWeeks(3), familiehendelse.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, familiehendelse, familiehendelse.plusWeeks(6).minusDays(1)));
        perioderMor.add(new UttaksperiodeBuilder()
                .medTidsperiode(samtidigUttakFOM, samtidigUttakTOM)
                .medStønadskontoType(STØNADSKONTOTYPE_MØDREKVOTE)
                .medSamtidigUttak(true, BigDecimal.valueOf(50))
                .build());
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, familiehendelse.plusWeeks(14), familiehendelse.plusWeeks(20).minusDays(1)));
        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(morAktørId, farAktørId, fordelingMor, SoekersRelasjonErketyper.søkerTermin( 2,familiehendelse) );
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        LocalDate fpStartDatoFar = samtidigUttakFOM;
        Fordeling fordelingFar = new ObjectFactory().createFordeling();
        fordelingFar.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderFar = fordelingFar.getPerioder();
        perioderFar.add(new UttaksperiodeBuilder()
                .medTidsperiode(samtidigUttakFOM, samtidigUttakTOM.plusWeeks(3))
                .medStønadskontoType(STØNADSKONTOTYPE_FELLESPERIODE)
                .medSamtidigUttak(true, BigDecimal.valueOf(100))
                .medFlerbarnsdager(true)
                .build());
        perioderFar.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, familiehendelse.plusWeeks(20), familiehendelse.plusWeeks(21).minusDays(1)));
        ForeldrepengesoknadBuilder søknadFar = foreldrepengeSøknadErketyper.uttakMedFordeling(farAktørId, morAktørId, fordelingFar, SoekersRelasjonErketyper.fødsel( 2,familiehendelse));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farFnr, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farFnr, fpStartDatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farFnr, saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgFørstegangsbehandling();

    }
    @Test
    @DisplayName("Mor fødsel med Arena")
    @Description("Mor søker fødsel med arena")
    public void testcase_mor_fødsel_ARENA() throws Exception {
        TestscenarioDto testscenario = opprettScenario("46");

        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();

        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.fodselfunnetstedUttakKunMor(morAktørId, fødselsdato);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        // Må fortsette behandling fra behandlingsmeny for å få opp at FP er innvilget og behandlingen er avsluttet (refresh 1-2 ganger)

    }
    @Test
    @DisplayName("Testcase mor uten barn søker fødsel ")
    @Description("Mor har ikke barn registrert i TPS og søker på fødsel som skal ha skjedd 3 uker før.")
    public void testcase_mor_fødsel_ikkeBarn() throws Exception {
        TestscenarioDto testscenario = opprettScenario("55");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusWeeks(3);
        LocalDate fpStartdatoSøker = fødselsdato;

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fpStartdatoSøker, fpStartdatoSøker.plusWeeks(15).minusDays(1)));

        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.fodselfunnetstedUttakKunMor(søkerAktørIdent, fordeling, fødselsdato);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerSøker = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoSøker);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerSøker, testscenario, saksnummerMor);
    }
    @Test
    @DisplayName("Mor søker med ukjent annen foreldre")
    @Description("AnnenForelder, kjent eller ikke kjent")
    public void testcase_mor_farIkkeKjent() throws Exception {
        TestscenarioDto testscenario = opprettScenario("83");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String annenpartAktørIdent = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusWeeks(3);
        LocalDate fpStartdatoSøker = fødselsdato.minusWeeks(3);

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(false);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoSøker, fødselsdato.minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)));

        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.uttakMedFordeling(søkerAktørIdent,new UkjentForelder(),fordeling, RettigheterErketyper.beggeForeldreRettIkkeAleneomsorg(), SoekersRelasjonErketyper.fødsel( 1,fødselsdato));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerSøker = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoSøker);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerSøker, testscenario, saksnummerMor);

    }
    @Test
    @DisplayName("Mor søker om aleneomsorg")
    @Description("Mor søker fult uttak med foreldrepenger")
    public void testcase_mor_aleneomsorg() throws Exception {
        TestscenarioDto testscenario = opprettScenario("83");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String annenAktørIdent = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String søkerFnr = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String søkerIdent = søkerAktørIdent;
        LocalDate fødselsdato = LocalDate.now().plusWeeks(4);
        LocalDate fpStartdatoSøker = fødselsdato.minusWeeks(3);

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoSøker, fødselsdato.minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER, fødselsdato, fødselsdato.plusWeeks(46).minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusWeeks(46), fødselsdato.plusWeeks(50).minusDays(1)));

        Rettigheter rettigheter = new Rettigheter();
        rettigheter.setHarAleneomsorgForBarnet(true);
        rettigheter.setHarAnnenForelderRett(false);
        rettigheter.setHarOmsorgForBarnetIPeriodene(true);

        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.uttakMedFordeling(annenAktørIdent, new UkjentForelder(), fordeling, rettigheter, SoekersRelasjonErketyper.søkerTermin( 1,fødselsdato));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), søkerIdent, søkerFnr, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerSøker = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoSøker);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerSøker, søkerIdent, søkerFnr, saksnummer);
    }
    @Test
    @DisplayName("Far mor ikke rett")
    @Description("Far søker om foreldrepenger og mor har ikke rett til foreldrepenger")
    public void testcase_far_morIkkeRett() throws Exception {
        TestscenarioDto testscenario = opprettScenario("83");

        String søkerIdent = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String søkerFnr = testscenario.getPersonopplysninger().getAnnenpartIdent();
        LocalDate fødselsdato = LocalDate.now().minusWeeks(4);
        LocalDate fpStartdatoSøker = fødselsdato.plusWeeks(6);

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER, fpStartdatoSøker, fødselsdato.plusWeeks(46).minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusWeeks(46), fødselsdato.plusWeeks(50).minusDays(1)));

        Rettigheter rettigheter = new Rettigheter();
        rettigheter.setHarAleneomsorgForBarnet(true);
        rettigheter.setHarAnnenForelderRett(false);
        rettigheter.setHarOmsorgForBarnetIPeriodene(true);

        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.uttakMedFordeling(søkerIdent, new UkjentForelder(), fordeling, rettigheter, SoekersRelasjonErketyper.søkerTermin( 1,fødselsdato));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), søkerIdent, søkerFnr, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerSøker = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoSøker);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerSøker, søkerIdent, søkerFnr, saksnummer);
    }
    @Test
    @DisplayName("Mor fødsel med arbeidsforhold og frilans. AP i Fakta om uttak")
    @Description("Mor søker fødsel med ett arbeidsforhold og frilans. Vurder fakta om beregning. Avvik i beregning")
    public void testcase_mor_fødsel_ATFL_medAP() throws Exception {
        TestscenarioDto testscenario = opprettScenario("59");

        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();

        int inntektPerMåned = 20_000;
        int overstyrtInntekt = 500_000;
        int overstyrtFrilanserInntekt = 500_000;
        BigDecimal refusjon = BigDecimal.valueOf(overstyrtInntekt + overstyrtFrilanserInntekt);

        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.fodselfunnetstedUttakKunMorMedFrilans(søkerAktørIdent, fødselsdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(inntektPerMåned, fnr, fpStartdato,
                orgNr, Optional.empty(), Optional.of(refusjon), Optional.empty());

        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
    }
    @Test
    @DisplayName("Mor endringssøknad og ny IM")
    @Description("Mor sender førstegangssøknad på termin, automatsik vurdert og mor sender endringssøknad")
    public void testcase_mor_termin_endringssøknad_medNyIM() throws Exception {
        TestscenarioDto testscenario = opprettScenario("74");
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate familieHendelse = LocalDate.now().plusMonths(1);
        LocalDate fpStartdato = familieHendelse.minusWeeks(3);
        String orgnr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato, familieHendelse.minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, familieHendelse, familieHendelse.plusWeeks(8).minusDays(1)));
        perioder.add(FordelingErketyper.oppholdsperiode(OPPHOLDSTYPE_FEDREKVOTE_ANNEN_FORELDER, familieHendelse.plusWeeks(8), familieHendelse.plusWeeks(10).minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, familieHendelse.plusWeeks(10), familieHendelse.plusWeeks(14).minusDays(1)));
        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.uttakMedFordeling(søkerAktørIdent, fordeling, SoekersRelasjonErketyper.søkerTermin(1, familieHendelse));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario, fpStartdato);
        fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        LocalDate graderingFom = familieHendelse.plusWeeks(6);
        LocalDate graderingTom = familieHendelse.plusWeeks(14).minusDays(1);
        Fordeling fordelingEndring = new ObjectFactory().createFordeling();
        fordelingEndring.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderEndring = fordelingEndring.getPerioder();
        perioderEndring.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, familieHendelse.plusWeeks(14), familieHendelse.plusWeeks(16).minusDays(1)));
        perioderEndring.add(graderingsperiodeArbeidstaker(STØNADSKONTOTYPE_MØDREKVOTE, graderingFom, graderingTom, orgnr, 60));
        ForeldrepengesoknadBuilder søknadEndring = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(søkerAktørIdent, fordelingEndring, saksnummer.toString());
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        Long saksnummerE = fordel.sendInnSøknad(søknadEndring.build(), søkerAktørIdent, søkerIdent, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
        List<InntektsmeldingBuilder> inntektsmeldingEndret = makeInntektsmeldingFromTestscenario(testscenario, fpStartdato);
        for (InntektsmeldingBuilder im : inntektsmeldingEndret) {
            im.addGradertperiode(BigDecimal.valueOf(60), graderingFom, graderingTom);
        }
        fordel.sendInnInntektsmeldinger(inntektsmeldingEndret, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventTilSakHarRevurdering();
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        verifiser(saksbehandler.harRevurderingBehandling(), "Det er ikke opprettet revurdering.");
        saksbehandler.velgRevurderingBehandling();
        saksbehandler.refreshFagsak();
        saksbehandler.refreshBehandling();
    }
    @Test
    @DisplayName("Mor endringssøknad på åpen endringssøknad")
    @Description("Mor sender inn en endringssøknad på en åpen endringssøknad. Teste hvordan endringssøknader blir henlagt")
    public void testcase_mor_endringssøknad_åpenEndringssøknad() throws Exception {
        TestscenarioDto testscenario = opprettScenario("75");
        Søker søker = new SøkerBuilder(testscenario, false)
                .medArbeidsforohld()
                .medBarn()
                .build();
        LocalDate fødselsdato = søker.dato;
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        søker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)));
        søker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)));
        søker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(24).plusDays(2)));
        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.uttakMedFordeling(
                søker.aktørID,
                søker.getSisteFordeling(),
                SoekersRelasjonErketyper.søkerTermin(1, fødselsdato));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(
                søker.getInntektPerMåned(0),
                søker.ident,
                fpStartdato,
                søker.getOrgNr(0),
                Optional.of(søker.getArbeidsforholdID(0)),
                Optional.empty(),
                Optional.empty());
        fordel.sendInnInntektsmeldinger(Arrays.asList(inntektsmeldingBuilder), testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        LocalDate endringssøknadFOM = fødselsdato.plusWeeks(24).plusDays(3);
        LocalDate endringssøknadTOM = fødselsdato.plusWeeks(31).minusDays(1);
        søker.nyFordeling();
        søker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL,
                endringssøknadFOM.plusWeeks(1), endringssøknadTOM.plusWeeks(1)));
        ForeldrepengesoknadBuilder søknadEndring = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(
                søker.aktørID,
                søker.getSisteFordeling(),
                saksnummer.toString());
        fordel.sendInnSøknad(søknadEndring.build(),
                søker.aktørID,
                søker.ident,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD,
                saksnummer);

      søker.nyFordeling();
      søker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE,
              endringssøknadFOM, endringssøknadTOM));
      ForeldrepengesoknadBuilder søknadEndring3 = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(
              søker.aktørID,
              søker.getSisteFordeling(),
              saksnummer.toString());
      fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
      fordel.sendInnSøknad(søknadEndring3.build(),
              søker.aktørID,
              søker.ident,
              DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD,
              saksnummer);
    }
    @Test
    @DisplayName("Mor sender to førstegangssøknader")
    @Description("Test av henlegging på åpen førstegangssøknad.")
    public void testcase_mor_toFørstegangssøknader() throws Exception {
        TestscenarioDto testscenario = opprettScenario("75");
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate termindato = fødselsdato.plusDays(11);
        LocalDate fpStartdato = termindato.minusWeeks(3);
        String orgnr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();

        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato, termindato.minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato, termindato.plusWeeks(15).minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusWeeks(15), termindato.plusWeeks(24).plusDays(2)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE,termindato.plusWeeks(24).plusDays(3),termindato.plusWeeks(30)));

        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.uttakMedFordeling(søkerAktørIdent, fordeling, SoekersRelasjonErketyper.søkerTermin(1, termindato));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario, fpStartdato);
        fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario, saksnummer);

        Fordeling fordeling2 = new ObjectFactory().createFordeling();
        fordeling2.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder2 = fordeling.getPerioder();
        perioder2.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato, termindato.minusDays(1)));
        perioder2.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato, termindato.plusWeeks(15).minusDays(1)));
        perioder2.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusWeeks(15), termindato.plusWeeks(24).plusDays(2)));

        ForeldrepengesoknadBuilder søknad2 = foreldrepengeSøknadErketyper.uttakMedFordeling(søkerAktørIdent, fordeling2, SoekersRelasjonErketyper.søkerTermin(1, termindato));
        fordel.sendInnSøknad(søknad2.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, saksnummer);

    }
    @Test
    @DisplayName("Mor Termin endringssøknad med gradering førstegangssøknad")
    @Description("Mor sender inn førstegangssøknad med gradering og endringssøknad med vanlige perioder")
    public void testcase_mor_termin_endringssøknad_vedtaksperioerMedGradering() throws Exception {
        TestscenarioDto testscenario = opprettScenario("74");
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate familieHendelse = LocalDate.now().plusMonths(1);
        LocalDate fpStartdato = familieHendelse.minusWeeks(3);
        String orgnr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato, familieHendelse.minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, familieHendelse, familieHendelse.plusWeeks(15).minusDays(1)));
        perioder.add(graderingsperiodeArbeidstaker(STØNADSKONTOTYPE_FELLESPERIODE, familieHendelse.plusWeeks(15), familieHendelse.plusWeeks(31).minusDays(1), orgnr, 33 ));
        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.uttakMedFordeling(søkerAktørIdent, fordeling, SoekersRelasjonErketyper.søkerTermin(1, familieHendelse));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario, fpStartdato);
        inntektsmeldinger.get(0).addGradertperiode(BigDecimal.valueOf(33), familieHendelse.plusWeeks(15), familieHendelse.plusWeeks(31).minusDays(1));
        fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        verifiserLikhet(saksbehandler.valgtBehandling.status.kode, "AVSLU", "Behandlingsstatus");
        // Endringssøknad
        Fordeling fordelingEndring = new ObjectFactory().createFordeling();
        fordelingEndring.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderEndring = fordelingEndring.getPerioder();
        perioderEndring.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, familieHendelse.plusWeeks(31), familieHendelse.plusWeeks(41)));
        //TODO endre på fodselfunnetstedKunMorEndring til uttakMedFordeling
        ForeldrepengesoknadBuilder endretSøknad = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring( søkerAktørIdent, fordelingEndring, saksnummer.toString());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummerE = fordel.sendInnSøknad(endretSøknad.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventTilSakHarRevurdering();
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        verifiser(saksbehandler.harRevurderingBehandling(), "Det er ikke opprettet revurdering.");
        saksbehandler.velgRevurderingBehandling();
        saksbehandler.refreshFagsak();
        saksbehandler.refreshBehandling();
    }
    @Test
    @DisplayName("Koblet sak med oppholdsperioder og endringssøknad")
    @Description("Mor søker (med oppholdsperioder for far). Far søker (med oppholdsperioder for mor). Berørt sak på begge. " +
            "Endringssøknad fra mor for å sjekke endringsstartpunkt. Endringssøknad fra far for å sjekke endringsstartpunkt." +
            "Blir opprettet veldig mange revurderinger. En del gå automatisk gjennom og vrient å holde styr på rekkefølge.")
    public void testcase_kobletSak_endringssøknad() throws Exception {
        TestscenarioDto testscenario = opprettScenario("82");

        String morIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        String farIdent = testscenario.getPersonopplysninger().getAnnenpartIdent();
        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String farAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);
        LocalDate fpStartdatoFar = fødselsdato.plusWeeks(8);

        // Fordeling og søknad mor
        Fordeling fordelingMor = new ObjectFactory().createFordeling();
        fordelingMor.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMor = fordelingMor.getPerioder();
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1)));
        perioderMor.add(FordelingErketyper.oppholdsperiode(OPPHOLDSTYPE_FEDREKVOTE_ANNEN_FORELDER, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(10).minusDays(1)));
        perioderMor.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusWeeks(10), fødselsdato.plusWeeks(14).minusDays(1)));

        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.fodselfunnetstedMorMedFar(morAktørId, farAktørId, fødselsdato, LocalDate.now().minusWeeks(1), fordelingMor);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        // Fordeling og søknad far
        Fordeling fordelingFar = new ObjectFactory().createFordeling();
        fordelingFar.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderFar = fordelingFar.getPerioder();
        perioderFar.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fpStartdatoFar, fpStartdatoFar.plusWeeks(2).minusDays(1)));
        perioderFar.add(FordelingErketyper.oppholdsperiode(OPPHOLDSTYPE_MØDREKVOTE_ANNEN_FORELDER, fødselsdato.plusWeeks(10), fødselsdato.plusWeeks(14).minusDays(1)));
        perioderFar.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødselsdato.plusWeeks(14), fødselsdato.plusWeeks(20).minusDays(1)));

        ForeldrepengesoknadBuilder søknadFar = foreldrepengeSøknadErketyper.fodselfunnetstedFarMedMor(farAktørId, morAktørId, fødselsdato, LocalDate.now().minusWeeks(1), fordelingFar);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farIdent, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farIdent, fpStartdatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farIdent, saksnummerFar);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        // Endring - fordelig og søknad mor
        Fordeling fordelingMorEndring = new ObjectFactory().createFordeling();
        fordelingMorEndring.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderMorEndring = fordelingMorEndring.getPerioder();
        perioderMorEndring.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusWeeks(9), fødselsdato.plusWeeks(10).minusDays(1)));
        Long saksnummerMorL = saksnummerMor;
        String saksnummerMorS = saksnummerMorL.toString();
        ForeldrepengesoknadBuilder søknadMorEndring = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(morAktørId, fordelingMorEndring, saksnummerMorS);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        Long saksnummerMorE = fordel.sendInnSøknad(søknadMorEndring.build(), morAktørId, morIdent, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMorE);
        saksbehandler.ventTilSakHarRevurdering();
        saksbehandler.velgRevurderingBehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        // Endring - fordeling og søknad far
        Fordeling fordelingFarEndring = new ObjectFactory().createFordeling();
        fordelingFarEndring.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioderFarEndring = fordelingFarEndring.getPerioder();
        perioderFarEndring.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FEDREKVOTE, fødselsdato.plusWeeks(11), fødselsdato.plusWeeks(13).minusDays(1)));
        Long saksnummerFarL = saksnummerFar;
        String saksnummerFarS = saksnummerFarL.toString();
        ForeldrepengesoknadBuilder søknadFarEndring = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(farAktørId, fordelingFarEndring, saksnummerFarS);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        Long saksnummerFarE = fordel.sendInnSøknad(søknadFarEndring.build(), farAktørId, farIdent, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummerFar);

    }
    @Test
    @DisplayName("Mor fødsel utsettelse Ferie")
    @Description("Mor søker utsettekse pga Ferie")
    public void testcase_mor_fødsel_uttsettelse() throws Exception {
        TestscenarioDto testscenario = opprettScenario("74");
        String morAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(2);
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);
        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        perioder.add(utsettelsesperiode(UTSETTELSETYPE_LOVBESTEMT_FERIE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(30)));
        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(morAktørId, fordeling, SoekersRelasjonErketyper.fødsel(1,fødselsdato));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdatoMor);
        inntektsmeldingerMor.get(0).addUtsettelseperiode(UTSETTELSETYPE_LOVBESTEMT_FERIE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(30));
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummerMor);
    }
    @Test
    @DisplayName("Mor fødsel med ett arbeidsforhold, med gradering")
    @Description("Mor søker fødsel med ett arbeidsforhold, en periode med gradering")
    public void testcase_mor_fødsel_gradering_AT() throws Exception {
        TestscenarioDto testscenario = opprettScenario("74");
        Søker søker = new SøkerBuilder(testscenario, false)
                .medArbeidsforohld()
                .build();
        LocalDate termindato = LocalDate.now().plusWeeks(4);
        LocalDate fpStartdato = termindato.minusWeeks(2);

        søker.perioderRef.add(uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato , termindato.minusDays(1)));
        søker.perioderRef.add(uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato, termindato.plusWeeks(6).minusDays(1)));
        søker.perioderRef.add(graderingsperiodeArbeidstaker(STØNADSKONTOTYPE_MØDREKVOTE, termindato.plusWeeks(6), termindato.plusWeeks(12).minusDays(1), søker.getOrgNr(0),47));
        søker.perioderRef.add(graderingsperiodeArbeidstaker(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusWeeks(14), termindato.plusWeeks(18).minusDays(1), søker.getOrgNr(0), 47));

        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(søker.aktørID, søker.getSisteFordeling(), SoekersRelasjonErketyper.søkerTermin(1, termindato));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdato );
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        søker.nyFordeling();
        søker.perioderRef.add(uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusWeeks(6), termindato.plusWeeks(22).minusDays(1)));
        søker.perioderRef.add(uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato.plusWeeks(30), termindato.plusWeeks(35).minusDays(1)));
        søker.perioderRef.add(uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusWeeks(40), termindato.plusWeeks(45)));

        ForeldrepengesoknadBuilder søknadEndring = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(
                søker.aktørID,
                søker.getSisteFordeling(),
                saksnummer.toString());
        fordel.sendInnSøknad(søknadEndring.build(),
                søker.aktørID,
                søker.ident,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD,
                saksnummer);

    }
    @Test
    @DisplayName("Mor fødsel med ett arbeidsforhold, med gradering")
    @Description("Mor søker fødsel med ett arbeidsforhold, en periode med gradering")
    public void testcase_mor_fødsel_gradering_AT_medFlytting() throws Exception {
        TestscenarioDto testscenario = opprettScenario("74");
        Søker søker = new SøkerBuilder(testscenario, false)
                .medArbeidsforohld()
                .build();
        LocalDate fødselsdato = LocalDate.now().minusWeeks(4);
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        søker.perioderRef.add(uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato , fødselsdato.minusDays(1)));
        søker.perioderRef.add(uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        søker.perioderRef.add(graderingsperiodeArbeidstaker(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(12).minusDays(1), søker.getOrgNr(0),47));
        søker.perioderRef.add(uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(12), fødselsdato.plusWeeks(14).minusDays(1)));
        søker.perioderRef.add(graderingsperiodeArbeidstaker(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(14), fødselsdato.plusWeeks(18).minusDays(1), søker.getOrgNr(0), 47));

        ForeldrepengesoknadBuilder søknadMor = foreldrepengeSøknadErketyper.uttakMedFordeling(søker.aktørID, søker.getSisteFordeling(), SoekersRelasjonErketyper.fødsel(1, fødselsdato));
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknadMor.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpStartdato );
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, testscenario, saksnummer);

        //Automatisk behandling av Fakta om fødsel siden det ikke er registert barn i TPS
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, fødselsdato)
                .setBegrunnelse("omg lol haha ja bacon");
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderManglendeFodselBekreftelse.class);

        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);
        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        beslutter.velgSisteBehandling();
        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling();

        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus("AVSLU");

        søker.nyFordeling();
        søker.perioderRef.add(uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(22).minusDays(1)));
        søker.perioderRef.add(uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusWeeks(30), fødselsdato.plusWeeks(35).minusDays(1)));
        søker.perioderRef.add(uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(40), fødselsdato.plusWeeks(45)));

        ForeldrepengesoknadBuilder søknadEndring = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(
                søker.aktørID,
                søker.getSisteFordeling(),
                saksnummer.toString());
        fordel.sendInnSøknad(søknadEndring.build(),
                søker.aktørID,
                søker.ident,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD,
                saksnummer);

    }
    @Test
    @DisplayName("Mor fødsel SNFL gradering")
    @Description("Mor søker fødsel som selvstendig næringsdrivende med minst en periode som er gradering.")
    public void testcase_mor_fødsel_gradering_SNFL() throws Exception {
        TestscenarioDto testscenario = opprettScenario("48");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)));
        perioder.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        perioder.add(graderingsperiodeFL(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(12).minusDays(1), 47));
        perioder.add(graderingsperiodeSN(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato.plusWeeks(12),fødselsdato.plusWeeks(18).minusDays(1),  33));
        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.uttakMedFordelingOgOpptjening(søkerAktørIdent, fordeling, OpptjeningErketyper.medEgenNaeringOgFrilansOpptjening(), SoekersRelasjonErketyper.fødsel(1, fødselsdato));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class).godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderPerioderOpptjeningBekreftelse.class);
    }
    @Test
    @DisplayName("Mor søker med flere endingssøknader")
    @Description("Mor har åpen revurdering og sender endringssøknad på endringssøknad")
    public void testcase_mor_endringssøknadPåÅpenEndringssøknad() throws Exception {
        TestscenarioDto testscenario = opprettScenario("75");

        Søker søker = new SøkerBuilder(testscenario, false)
                .medArbeidsforohld()
                .medBarn()
                .build();

        LocalDate fødselsdato = søker.dato;
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        SoekersRelasjonTilBarnet relasjon = SoekersRelasjonErketyper.fødsel(1,fødselsdato);
        søker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)));
        søker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)));
        søker.perioderRef.add(FordelingErketyper.uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(31).minusDays(1)));
        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.uttakMedFordeling(søker.aktørID, søker.getSisteFordeling(), relasjon);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingerMor =  lagInntektsmeldingBuilder(søker.getInntektPerMåned(0), søker.ident, fpStartdato, søker.getOrgNr(0), Optional.of(søker.getArbeidsforholdID(0)), Optional.empty(), Optional.empty());
        fordel.sendInnInntektsmeldinger(Arrays.asList(inntektsmeldingerMor), testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.ikkeVentPåStatus = true;
        saksbehandler.hentFagsak(saksnummer);
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        InntektsmeldingBuilder inntektsmeldingerMor2 =  lagInntektsmeldingBuilder(søker.getInntektPerMåned(0), søker.ident, fpStartdato, søker.getOrgNr(0), Optional.of(søker.getArbeidsforholdID(0)), Optional.empty(), Optional.empty());
        fordel.sendInnInntektsmeldinger(Arrays.asList(inntektsmeldingerMor2), testscenario, saksnummer);
        saksbehandler.ventTilSakHarRevurdering();
        saksbehandler.velgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        søker.nyFordeling();
        søker.perioderRef.add(graderingsperiodeSN(
                STØNADSKONTOTYPE_FELLESPERIODE,
                fødselsdato.plusWeeks(20),
                fødselsdato.plusWeeks(25),
                40));
        ForeldrepengesoknadBuilder søknadEndring = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(søker.aktørID, søker.getSisteFordeling(), saksnummer.toString());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnSøknad(søknadEndring.build(), søker.aktørID, søker.ident, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
        søker.nyFordeling();
        søker.perioderRef.add(graderingsperiodeSN(
                STØNADSKONTOTYPE_MØDREKVOTE,
                fødselsdato.plusWeeks(10), fødselsdato.plusWeeks(20),
                 40));
        søker.perioderRef.add(utsettelsesperiode(UTSETTELSETYPE_ARBEID, fødselsdato.plusWeeks(20).plusDays(1), fødselsdato.plusWeeks(30)));
        ForeldrepengesoknadBuilder søknadEndringTO = foreldrepengeSøknadErketyper.fodselfunnetstedKunMorEndring(søker.aktørID, søker.getSisteFordeling(), saksnummer.toString());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        fordel.sendInnSøknad(søknadEndringTO.build(), søker.aktørID, søker.ident, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);
    }
    public class Søker{
        private boolean erAnnenSøker;
        public String aktørID;
        public String ident;
        public String annenForelderAktørID;
        public AnnenForelder annenForelder = new UkjentForelder();
        public LocalDate dato;
        public List<Arbeidsforhold> arbeidsforholdList;
        private List<Inntektsperiode> inntektsperiodeList;
        private List<Fordeling> fordelinger = new ArrayList<>();
        public List<LukketPeriodeMedVedlegg> perioderRef;

        public String getOrgNr(int arbeidsfoholdIndex){
            return arbeidsforholdList.get(arbeidsfoholdIndex).getArbeidsgiverOrgnr();
        }
        public String getArbeidsforholdID(int arbeidsfoholdIndex){
            return arbeidsforholdList.get(arbeidsfoholdIndex).getArbeidsforholdId();
        }
        public int getInntektPerMåned(int arbeidsfoholdIndex){
            return inntektsperiodeList.get(arbeidsfoholdIndex).getBeløp();
        }
        public void nyFordeling(){
            fordelinger.add(new ObjectFactory().createFordeling());
            fordelinger.get(fordelinger.size()-1).setAnnenForelderErInformert(true);
            perioderRef = fordelinger.get(fordelinger.size()-1).getPerioder();
        }
        public Fordeling getSisteFordeling(){
            return fordelinger.get(fordelinger.size()-1);
        }


    }
    public class SøkerBuilder{
        private Søker kladd;
        private TestscenarioDto testscenario;

        public SøkerBuilder(TestscenarioDto testscenario, boolean erAnnenSøker){
            this.testscenario = testscenario;
            kladd =  new Søker();
            kladd.erAnnenSøker = erAnnenSøker;
            if(kladd.erAnnenSøker){
                kladd.aktørID = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
                kladd.ident = testscenario.getPersonopplysninger().getAnnenpartIdent();
            }
            else {
                kladd.aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
                kladd.ident = testscenario.getPersonopplysninger().getSøkerIdent();
            }
            kladd.nyFordeling();
        }

        public SøkerBuilder medArbeidsforohld(){
            if(kladd.erAnnenSøker) {
                kladd.arbeidsforholdList = testscenario.getScenariodataAnnenpart().getArbeidsforholdModell().getArbeidsforhold();
                kladd.inntektsperiodeList = testscenario.getScenariodataAnnenpart().getInntektskomponentModell().getInntektsperioder();
            }
            else{
                kladd.arbeidsforholdList = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold();
                kladd.inntektsperiodeList = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder();
            }
            return this;
        }
        public SøkerBuilder medBarn(){
            if(testscenario.getPersonopplysninger().getFødselsdato() != null){
                kladd.dato = testscenario.getPersonopplysninger().getFødselsdato();
            }
            return this;
        }
        public SøkerBuilder medAnnenForelder(){
            if(kladd.erAnnenSøker){
                kladd.annenForelderAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
                kladd.annenForelder = standardAnnenForelder(kladd.annenForelderAktørID);
            }
            else {
                kladd.annenForelderAktørID = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
                kladd.annenForelder = standardAnnenForelder(kladd.annenForelderAktørID);
            }
            return this;
        }
        public Søker build(){
//            Objects.requireNonNull(kladd.perioderRef, "Perioder kan ikke være tom");
            return this.kladd;
        }
    }
}