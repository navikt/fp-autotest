package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.INGEN_STØNADSKONTO;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmeldingPrivateArbeidsgiver;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepenger;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.erketyper.RelasjonTilBarnetErketyper;
import no.nav.foreldrepenger.autotest.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerAktivitetskravBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAleneomsorgBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaUttakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadEndringForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatMedUttaksplan;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriodeAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.FaktaOmBeregningTilfelle;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.ObjectFactory;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("foreldrepenger")
public class Fodsel extends ForeldrepengerTestBase {

    private static final Logger logger = LoggerFactory.getLogger(Fodsel.class);

    @Test
    @DisplayName("Mor fødsel med arbeidsforhold og frilans. Vurderer opptjening og beregning. Finner avvik")
    @Description("Mor søker fødsel med ett arbeidsforhold og frilans. Vurder opptjening. Vurder fakta om beregning. Avvik i beregning")
    public void morSøkerFødselMedEttArbeidsforholdOgFrilans_VurderOpptjening_VurderFaktaOmBeregning_AvvikIBeregning() {
        TestscenarioDto testscenario = opprettTestscenario("59");

        String fnr = testscenario.personopplysninger().søkerIdent();
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr();

        int inntektPerMåned = 20_000;
        int overstyrtInntekt = 500_000;
        int overstyrtFrilanserInntekt = 500_000;
        BigDecimal refusjon = BigDecimal.valueOf(overstyrtInntekt / 12);

        Opptjening opptjening = OpptjeningErketyper.medFrilansOpptjening();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medSpesiellOpptjening(opptjening);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr);
        inntektsmeldingBuilder.medRefusjonsBelopPerMnd(refusjon);

        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDLEGG_MOTTATT);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        var vurderFaktaOmBeregningBekreftelse1 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse1
                .leggTilMottarYtelseFrilans(false)
                .setBegrunnelse("Endret av Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse1);

        // Verifiser Beregningsgrunnlag
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus(), 1);// ikke sikker
                                                                                                          // på denne
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0).kode, "AT_FL");
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto(), 1);
        List<BeregningsgrunnlagPrStatusOgAndelDto> andeler = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        verifiserLikhet(andeler.size(), 2);
        verifiserLikhet(andeler.get(0).getAktivitetStatus().kode, "AT");
        verifiserLikhet(andeler.get(1).getAktivitetStatus().kode, "FL");

        // Legg til og fjern ytelser for å se tilbakehopp og opprettelse av akjsonspunkter
        verifiser(saksbehandler.harAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS),
                "Har ikke aksjonspunkt for FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS");
        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .leggTilMottarYtelseFrilans(true)
                .leggTilMaanedsinntektFL(25800)
                .setBegrunnelse("Endret av Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        var vurderFaktaOmBeregningBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse2
                .fjernFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.FASTSETT_MAANEDSINNTEKT_FL.kode)
                .leggTilMottarYtelseFrilans(false)
                .setBegrunnelse("Endret av Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse2);

        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse
                .leggTilInntektFrilans(overstyrtFrilanserInntekt)
                .leggTilInntekt(overstyrtInntekt, 2)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiserUttak(2, beslutter.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(beslutter.valgtBehandling.getBeregningResultatForeldrepenger(), true);

    }


    @Test
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold og avvik i beregning")
    public void morSøkerFødselMedToArbeidsforhold_AvvikIBeregning() {
        TestscenarioDto testscenario = opprettTestscenario("57");
        String fnr = testscenario.personopplysninger().søkerIdent();
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        int inntektPrMåned = 50_000;
        int overstyrtInntekt = inntektPrMåned * 12;
        BigDecimal refusjon = BigDecimal.valueOf(inntektPrMåned);

        Arbeidsforhold arbeidsforhold_1 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold()
                .get(0);
        Arbeidsforhold arbeidsforhold_2 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold()
                .get(1);
        String arbeidsforholdId_1 = arbeidsforhold_1.arbeidsforholdId();
        String arbeidsforholdId_2 = arbeidsforhold_2.arbeidsforholdId();
        String orgNr_1 = arbeidsforhold_1.arbeidsgiverOrgnr();
        String orgNr_2 = arbeidsforhold_2.arbeidsgiverOrgnr();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        InntektsmeldingBuilder inntektsmeldingBuilder_1 = lagInntektsmelding(inntektPrMåned, fnr, fpStartdato, orgNr_1)
                .medRefusjonsBelopPerMnd(refusjon)
                .medArbeidsforholdId(arbeidsforholdId_1);
        InntektsmeldingBuilder inntektsmeldingBuilder_2 = lagInntektsmelding(inntektPrMåned, fnr, fpStartdato, orgNr_2)
                .medRefusjonsBelopPerMnd(refusjon)
                .medArbeidsforholdId(arbeidsforholdId_2);

        fordel.sendInnInntektsmeldinger(Arrays.asList(inntektsmeldingBuilder_1, inntektsmeldingBuilder_2), testscenario,
                saksnummer);


        saksbehandler.hentFagsak(saksnummer);
        debugLoggBehandling(saksbehandler.valgtBehandling);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse
                .leggTilInntekt(overstyrtInntekt, 1)
                .leggTilInntekt(overstyrtInntekt, 2)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiserUttak(2, beslutter.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(beslutter.valgtBehandling.getBeregningResultatForeldrepenger(), true);

    }

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold og avvik i beregning")
    public void morSøkerFødselMedEttArbeidsforhold_AvvikIBeregning() {
        TestscenarioDto testscenario = opprettTestscenario("500");
        String orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        String fnr = testscenario.personopplysninger().søkerIdent();
        String aktørID = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        int inntektPrMåned = 15_000;
        BigDecimal refusjon = BigDecimal.valueOf(inntektPrMåned);
        int overstyrtInntekt = inntektPrMåned * 12;

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørID, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPrMåned, fnr, fpStartdato, orgNr)
                .medRefusjonsBelopPerMnd(refusjon);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse
                .leggTilInntekt(overstyrtInntekt, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiserUttak(1, beslutter.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(beslutter.valgtBehandling.getBeregningResultatForeldrepenger(), true);
    }

    @Test
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold i samme organisasjon")
    public void morSøkerFødselMedToArbeidsforholdISammeOrganisasjon() {
        TestscenarioDto testscenario = opprettTestscenario("57");
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        String fnr = testscenario.personopplysninger().søkerIdent();
        Arbeidsforhold arbeidsforhold1 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0);
        Arbeidsforhold arbeidsforhold2 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1);
        String orgNr1 = arbeidsforhold1.arbeidsgiverOrgnr();
        String orgNr2 = arbeidsforhold2.arbeidsgiverOrgnr();

        List<Integer> inntekter = sorterteInntektsbeløp(testscenario);
        InntektsmeldingBuilder inntektsmeldingBuilder1 = lagInntektsmelding(inntekter.get(0), fnr, fpStartdato, orgNr1)
                        .medArbeidsforholdId(arbeidsforhold1.arbeidsforholdId());
        InntektsmeldingBuilder inntektsmeldingBuilder2 = lagInntektsmelding(inntekter.get(1), fnr, fpStartdato, orgNr2)
                        .medArbeidsforholdId(arbeidsforhold2.arbeidsforholdId());

        fordel.sendInnInntektsmeldinger(Arrays.asList(inntektsmeldingBuilder1, inntektsmeldingBuilder2), testscenario,
                saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        debugLoggBehandling(saksbehandler.valgtBehandling);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiserUttak(2, saksbehandler.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger(), false);
    }

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold")
    public void morSøkerFødselMedEttArbeidsforhold() {
        TestscenarioDto testscenario = opprettTestscenario("500");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                startDatoForeldrepenger,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        debugLoggBehandling(saksbehandler.valgtBehandling);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiserUttak(1, saksbehandler.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger(), false);
    }

    @Test
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold med inntekt over 6G")
    public void morSøkerFødselMedToArbeidsforhold() {
        TestscenarioDto testscenario = opprettTestscenario("56");

        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        InntektsmeldingBuilder inntektsmeldingEn = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        InntektsmeldingBuilder inntektsmeldingerTo = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(1).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmeldinger(
                List.of(inntektsmeldingEn, inntektsmeldingerTo),
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiserUttak(2, saksbehandler.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger(), false);
    }

    @Test
    @DisplayName("Far søker fødsel med 1 arbeidsforhold")
    public void farSøkerFødselMedEttArbeidsforhold() {
        TestscenarioDto testscenario = opprettTestscenario("550");

        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate startDatoForeldrepenger = fødselsdato.plusWeeks(3);

        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        String søkerIdent = testscenario.personopplysninger().søkerIdent();
        logger.info("Ident: " + søkerIdent);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.FAR);

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                startDatoForeldrepenger,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        AvklarFaktaUttakBekreftelse avklarFaktaUttakBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class);
        avklarFaktaUttakBekreftelse.godkjennPeriode(startDatoForeldrepenger, startDatoForeldrepenger.plusWeeks(2),
                saksbehandler.kodeverk.UttakPeriodeVurderingType.getKode("PERIODE_KAN_IKKE_AVKLARES"));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerAktivitetskravBekreftelse.class);

        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.innvilgPeriode(startDatoForeldrepenger,
                startDatoForeldrepenger.plusWeeks(2));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        List<UttakResultatPeriode> perioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        verifiserLikhet(perioder.size(), 1);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiser(beslutter.harHistorikkinnslagForBehandling(HistorikkInnslag.VEDTAK_FATTET),
                "behandling har ikke historikkinslag 'Vedtak fattet'");
        beslutter.ventTilHistorikkinnslag(HistorikkInnslag.BREV_BESTILT);

    }

    @Test
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold i samme organisasjon med 1 inntektsmelding")
    public void morSøkerFødselMedToArbeidsforholdISammeOrganisasjonEnInntektsmelding() {

        TestscenarioDto testscenario = opprettTestscenario("57");

        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        String fnr = testscenario.personopplysninger().søkerIdent();
        List<Integer> inntekter = sorterteInntektsbeløp(testscenario);
        String orgnr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(inntekter.get(0) + inntekter.get(1), fnr,
                fpStartdato, orgnr);

        fordel.sendInnInntektsmelding(inntektsmelding, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiserUttak(1, saksbehandler.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger(), false);

    }

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold, Papirsøkand")
    public void morSøkerFødselMedEttArbeidsforhold_papirsøknad() {
        TestscenarioDto testscenario = opprettTestscenario("50");

        long saksnummer = fordel.sendInnPapirsøknadForeldrepenger(testscenario, false);
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                startDatoForeldrepenger,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);
        saksbehandler.hentFagsak(saksnummer);

        PapirSoknadForeldrepengerBekreftelse aksjonspunktBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(PapirSoknadForeldrepengerBekreftelse.class);
        FordelingDto fordeling = new FordelingDto();
        PermisjonPeriodeDto fpff = new PermisjonPeriodeDto(FORELDREPENGER_FØR_FØDSEL,
                startDatoForeldrepenger, fødselsdato.minusDays(1));
        PermisjonPeriodeDto mødrekvote = new PermisjonPeriodeDto(MØDREKVOTE,
                fødselsdato, fødselsdato.plusWeeks(10));
        fordeling.permisjonsPerioder.add(fpff);
        fordeling.permisjonsPerioder.add(mødrekvote);
        aksjonspunktBekreftelse.morSøkerFødsel(fordeling, fødselsdato, fpff.periodeFom.minusWeeks(3));

        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelse);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        // verifiserer uttak
        List<UttakResultatPeriode> perioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(perioder).hasSize(3);
        verifiserUttaksperiode(perioder.get(0), FORELDREPENGER_FØR_FØDSEL, 1);
        verifiserUttaksperiode(perioder.get(1), MØDREKVOTE, 1);
        verifiserUttaksperiode(perioder.get(2), MØDREKVOTE, 1);

        fordel.sendInnPapirsøknadEndringForeldrepenger(testscenario, saksnummer);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var aksjonspunktBekreftelseEndringssøknad = saksbehandler
                .hentAksjonspunktbekreftelse(PapirSoknadEndringForeldrepengerBekreftelse.class);
        FordelingDto fordelingEndringssøknad = new FordelingDto();
        // Legger til fellesperiode på slutten
        PermisjonPeriodeDto fellesperiode = new PermisjonPeriodeDto(FELLESPERIODE,
                fødselsdato.plusWeeks(10).plusDays(1), fødselsdato.plusWeeks(15));
        fordelingEndringssøknad.permisjonsPerioder.add(fellesperiode);
        aksjonspunktBekreftelseEndringssøknad.setFordeling(fordelingEndringssøknad);
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelseEndringssøknad);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "FORELDREPENGER_ENDRET");

        // verifiserer uttak
        List<UttakResultatPeriode> perioderEtterEndringssøknad = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(perioderEtterEndringssøknad).hasSize(4);
        verifiserUttaksperiode(perioderEtterEndringssøknad.get(0), FORELDREPENGER_FØR_FØDSEL, 1);
        verifiserUttaksperiode(perioderEtterEndringssøknad.get(1), MØDREKVOTE, 1);
        verifiserUttaksperiode(perioderEtterEndringssøknad.get(2), MØDREKVOTE, 1);
        verifiserUttaksperiode(perioderEtterEndringssøknad.get(3), FELLESPERIODE, 1);
    }

    @Test
    @Description("Mor søker fødsel med 2 arbeidsforhold med arbeidsforhold som ikke matcher på ID")
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold med arbeidsforhold som ikke matcher på ID")
    public void morSøkerFødselMed2ArbeidsforholdArbeidsforholdIdMatcherIkke() {
        TestscenarioDto testscenario = opprettTestscenario("56");
        String fnr = testscenario.personopplysninger().søkerIdent();
        String orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        String orgNr2 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1)
                .arbeidsgiverOrgnr();

        int inntektPerMåned = 20_000;

        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr,
                startDatoForeldrepenger,orgNr)
                .medArbeidsforholdId("1");
        InntektsmeldingBuilder inntektsmeldingBuilder2 = lagInntektsmelding(inntektPerMåned, fnr,
                startDatoForeldrepenger,orgNr2)
                .medArbeidsforholdId("9");

        fordel.sendInnInntektsmeldinger(List.of(inntektsmeldingBuilder,inntektsmeldingBuilder2), testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        verifiser(saksbehandler.valgtBehandling.erSattPåVent(), "Behandling ikke satt på vent");
        debugLoggBehandling(saksbehandler.valgtBehandling);

    }

    @Test
    @Description("Mor søker fødsel med privatperson som arbeidsgiver")
    @DisplayName("Mor søker fødsel med privatperson som arbeidsgiver, avvik i beregning")
    public void morSøkerFødselMedPrivatpersonSomArbeidsgiver() {

        int overstyrtInntekt = 250_000;

        // Lag privat arbeidsgiver
        TestscenarioDto arbeidsgiverScenario = opprettTestscenario("59");
        String arbeidsgiverFnr = arbeidsgiverScenario.personopplysninger().søkerIdent();
        String arbeidsgiverAktørId = arbeidsgiverScenario.personopplysninger().søkerAktørIdent();

        // Lag testscenario
        TestscenarioDto testscenario = opprettTestscenarioMedPrivatArbeidsgiver("152", arbeidsgiverAktørId, arbeidsgiverFnr);

        // Send inn søknad
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusDays(2);
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        // Send inn inntektsmelding
        int inntektPerMaaned = 10_000;
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);
        String fnr = testscenario.personopplysninger().søkerIdent();
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingPrivateArbeidsgiver(inntektPerMaaned, fnr,
                startDatoForeldrepenger, arbeidsgiverFnr);

        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);

        // Bekreft Opptjening
        VurderPerioderOpptjeningBekreftelse vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class);
        vurderPerioderOpptjeningBekreftelse
                .godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // Verifiser Beregningsgrunnlag
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus(), 1);// ikke sikker
                                                                                                          // på denne
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0).kode, "AT");
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto(), 1);
        List<BeregningsgrunnlagPrStatusOgAndelDto> andeler = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        verifiserLikhet(andeler.size(), 1);
        verifiserLikhet(andeler.get(0).getAktivitetStatus().kode, "AT");

        // Bekreft inntekt i beregning
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse.leggTilInntekt(overstyrtInntekt, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        // Foreslå vedtak
        var foreslåVedtakBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(ForeslåVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(foreslåVedtakBekreftelse);

        // Totrinnskontroll
        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiserUttak(1, beslutter.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(beslutter.valgtBehandling.getBeregningResultatForeldrepenger(), false);
    }

    @Test
    @Description("Mor søker fødsel med privatperson som arbeidsgiver med endring i refusjon")
    @DisplayName("Mor søker fødsel med privatperson som arbeidsgiver med endring i refusjon, avvik i beregning")
    public void morSøkerFødselMedPrivatpersonSomArbeidsgiverMedEndringIRefusjon() {

        int overstyrtInntekt = 250_000;

        // Lag privat arbeidsgiver
        TestscenarioDto arbeidsgiverScenario = opprettTestscenario("59");
        String arbeidsgiverFnr = arbeidsgiverScenario.personopplysninger().søkerIdent();
        String arbeidsgiverAktørId = arbeidsgiverScenario.personopplysninger().søkerAktørIdent();

        // Lag testscenario
        TestscenarioDto testscenario = opprettTestscenarioMedPrivatArbeidsgiver("152", arbeidsgiverAktørId, arbeidsgiverFnr);

        // Send inn søknad
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusDays(2);
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        // Send inn inntektsmelding
        int inntektPerMaaned = 10_000;
        int refusjon = 25_000;
        int endret_refusjon = 10_000;
        LocalDate endringsdato = fødselsdato.plusMonths(1);
        HashMap<LocalDate, BigDecimal> endringRefusjonMap = new HashMap<>();
        endringRefusjonMap.put(endringsdato, BigDecimal.valueOf(endret_refusjon));
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);
        String fnr = testscenario.personopplysninger().søkerIdent();
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingPrivateArbeidsgiver(inntektPerMaaned, fnr, startDatoForeldrepenger,
                arbeidsgiverFnr)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(refusjon))
                .medEndringIRefusjonslist(endringRefusjonMap);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);

        // Bekreft Opptjening
        VurderPerioderOpptjeningBekreftelse vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class);
        vurderPerioderOpptjeningBekreftelse.godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // Verifiser Beregningsgrunnlag
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus(), 1);
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0).kode, "AT");
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto(), 1);
        List<BeregningsgrunnlagPrStatusOgAndelDto> andelerFørstePeriode = saksbehandler.valgtBehandling
                .getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        verifiserLikhet(andelerFørstePeriode.size(), 1);
        verifiserLikhet(andelerFørstePeriode.get(0).getAktivitetStatus().kode, "AT");

        // Bekreft inntekt i beregning
        VurderBeregnetInntektsAvvikBekreftelse vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse
                .leggTilInntekt(overstyrtInntekt, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.FORESLÅ_VEDTAK);

        // Verifiser Beregningsgrunnlag
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus(), 1);
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0).kode, "AT");
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto(), 2);
        andelerFørstePeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        verifiserLikhet(andelerFørstePeriode.size(), 1);
        verifiserLikhet(andelerFørstePeriode.get(0).getAktivitetStatus().kode, "AT");

        // Assert refusjon
        List<BeregningsresultatPeriode> resultatPerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger().getPerioder();
        assertThat(resultatPerioder.size()).isEqualTo(5);
        BigDecimal forventetDagsats = BigDecimal.valueOf(overstyrtInntekt).divide(BigDecimal.valueOf(260),
                RoundingMode.HALF_EVEN);
        assertThat(resultatPerioder.get(0).getAndeler().get(0).getRefusjon()).isEqualTo(forventetDagsats.intValue());
        assertThat(resultatPerioder.get(1).getAndeler().get(0).getRefusjon()).isEqualTo(forventetDagsats.intValue());
        BigDecimal forventetRefusjon = BigDecimal.valueOf(endret_refusjon * 12).divide(BigDecimal.valueOf(260),
                RoundingMode.HALF_EVEN);
        assertThat(resultatPerioder.get(2).getAndeler().get(0).getRefusjon()).isEqualTo(forventetRefusjon.intValue());
        assertThat(resultatPerioder.get(3).getAndeler().get(0).getRefusjon()).isEqualTo(forventetRefusjon.intValue());

        // Foreslå vedtak
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // Totrinnskontroll
        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        verifiserUttak(1, beslutter.valgtBehandling.hentUttaksperioder());
    }

    @Test
    @DisplayName("Far søker fødsel med aleneomsorg men er gift og bor med annenpart")
    public void farSøkerFødselAleneomsorgMenErGiftOgBorMedAnnenpart() {

        TestscenarioDto testscenario = opprettTestscenario("550");

        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.FAR)
                .medRettigheter(RettigheterErketyper.harAleneOmsorgOgEnerett())
                .medFordeling(FordelingErketyper.fordelingFarAleneomsorg(fødselsdato));

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fødselsdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        // fikk aleneomsorg aksjonspunkt siden far er gift og bor på sammested med
        // ektefelle
        // saksbehandler bekreftet at han har ikke aleneomsorg
        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaAleneomsorgBekreftelse bekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAleneomsorgBekreftelse.class);
        bekreftelse.bekreftBrukerHarIkkeAleneomsorg();
        saksbehandler.bekreftAksjonspunktbekreftelserer(bekreftelse);
        AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder avklarFaktaUttakPerioder = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class);
        // 20 uker fra erketype
        avklarFaktaUttakPerioder.delvisGodkjennPeriode(fødselsdato, fødselsdato.plusWeeks(20), fødselsdato,
                fødselsdato.plusWeeks(20),
                saksbehandler.hentKodeverk().UttakPeriodeVurderingType.getKode("PERIODE_KAN_IKKE_AVKLARES"));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakPerioder);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerAktivitetskravBekreftelse.class);

        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.innvilgManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse fatterVedtakBekreftelse =
                beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(fatterVedtakBekreftelse);
        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        // verifiserer uttak
        List<UttakResultatPeriode> uttaksperioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttaksperioder).hasSize(2);

        // uttak går til manuell behandling
        UttakResultatPeriode foreldrepengerFørste6Ukene = uttaksperioder.get(0);
        assertThat(foreldrepengerFørste6Ukene.getPeriodeResultatType().kode).isEqualTo("INNVILGET");
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);
        UttakResultatPeriode foreldrepengerEtterUke6 = uttaksperioder.get(1);
        assertThat(foreldrepengerEtterUke6.getPeriodeResultatType().kode).isEqualTo("INNVILGET");
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);

        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().size() == 4,
                "Antall stønadskontoer er feil.");
    }

    @Test
    @DisplayName("Mor søker fødsel har stillingsprosent 0")
    @Description("Mor søker fødsel har stillingsprosent 0 som fører til aksjonspunkt for opptjening")
    public void morSøkerFødselStillingsprosent0() {
        TestscenarioDto testscenario = opprettTestscenario("45");

        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        VurderPerioderOpptjeningBekreftelse vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class);
        vurderPerioderOpptjeningBekreftelse.godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
    }

    @Test
    @DisplayName("Mor søker gradering og utsettelse. Med to arbeidsforhold. Uten avvikende inntektsmelding")
    @Description("Mor, med to arbeidsforhold, søker gradering og utsettelse. Samsvar med IM.")
    public void morSøkerGraderingOgUtsettelseMedToArbeidsforhold_utenAvvikendeInntektsmeldinger() {

        TestscenarioDto testscenario = opprettTestscenario("56");

        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();

        Fordeling fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        LocalDate startdatoForeldrePenger = fødselsdato.minusWeeks(3);
        perioder.add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, startdatoForeldrePenger, fødselsdato.minusDays(1)));
        perioder.add(uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)));
        String gradetArbeidsgiver = "910909088";
        LocalDate graderingFom = fødselsdato.plusWeeks(10).plusDays(1);
        LocalDate graderingTom = fødselsdato.plusWeeks(12);
        BigDecimal arbeidstidsprosent = BigDecimal.TEN;
        perioder.add(graderingsperiodeArbeidstaker(FELLESPERIODE, graderingFom, graderingTom, gradetArbeidsgiver,
                arbeidstidsprosent.intValue()));
        LocalDate utsettelseFom = fødselsdato.plusWeeks(12).plusDays(1);
        LocalDate utsettelseTom = fødselsdato.plusWeeks(14);
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, utsettelseFom, utsettelseTom));

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medFordeling(fordeling);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        InntektsmeldingBuilder inntektsmeldingEn = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                startdatoForeldrePenger,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        InntektsmeldingBuilder inntektsmeldingerTo = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(1).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                startdatoForeldrePenger,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmeldinger(
                List.of(inntektsmeldingEn,inntektsmeldingerTo),
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        debugLoggBehandling(saksbehandler.valgtBehandling);
        // hackForÅKommeForbiØkonomi(saksnummer);

        // verifiserer uttak
        List<UttakResultatPeriode> uttaksperioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttaksperioder).hasSize(5);
        for (UttakResultatPeriode periode : uttaksperioder) {
            assertThat(periode.getPeriodeResultatType().kode).isEqualTo("INNVILGET");
            assertThat(periode.getPeriodeResultatÅrsak().kode).isNotEqualTo("-");
            assertThat(periode.getAktiviteter()).hasSize(2);
            for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
                assertThat(aktivitet.getArbeidsgiverReferanse()).isNotNull();
                assertThat(aktivitet.getUttakArbeidType().kode).isEqualTo("ORDINÆRT_ARBEID");
                List<Arbeidsforhold> arbeidsforholdFraScenario = testscenario.scenariodataDto().arbeidsforholdModell()
                        .arbeidsforhold();
                assertThat(aktivitet.getArbeidsgiverReferanse()).isIn(
                        arbeidsforholdFraScenario.get(0).arbeidsgiverOrgnr(),
                        arbeidsforholdFraScenario.get(1).arbeidsgiverOrgnr());
            }
        }
        UttakResultatPeriode fpff = uttaksperioder.get(0);
        assertThat(fpff.getAktiviteter().get(0).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(fpff.getAktiviteter().get(0).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(fpff.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER_FØR_FØDSEL);
        assertThat(fpff.getAktiviteter().get(1).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(fpff.getAktiviteter().get(1).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(fpff.getAktiviteter().get(1).getStønadskontoType()).isEqualTo(FORELDREPENGER_FØR_FØDSEL);
        UttakResultatPeriode mødrekvoteFørste6Ukene = uttaksperioder.get(1);
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(MØDREKVOTE);
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(1).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(1).getTrekkdagerDesimaler())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteFørste6Ukene.getAktiviteter().get(1).getStønadskontoType()).isEqualTo(MØDREKVOTE);
        UttakResultatPeriode mødrekvoteEtterUke6 = uttaksperioder.get(2);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(0).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(MØDREKVOTE);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(1).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(1).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(1).getStønadskontoType()).isEqualTo(MØDREKVOTE);
        UttakResultatPeriode gradering = uttaksperioder.get(3);
        assertThat(gradering.getGraderingAvslagÅrsak().kode).isEqualTo("-");
        assertThat(gradering.getGraderingInnvilget()).isTrue();
        assertThat(gradering.getGradertArbeidsprosent()).isEqualTo(arbeidstidsprosent);
        assertThat(gradering.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FELLESPERIODE);
        assertThat(gradering.getAktiviteter().get(1).getStønadskontoType()).isEqualTo(FELLESPERIODE);
        UttakResultatPeriodeAktivitet gradertAktivitet = finnAktivitetForArbeidsgiver(gradering, gradetArbeidsgiver);
        assertThat(gradertAktivitet.getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(gradertAktivitet.getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100).subtract(arbeidstidsprosent));
        assertThat(gradertAktivitet.getProsentArbeid()).isEqualTo(arbeidstidsprosent);
        UttakResultatPeriode utsettelse = uttaksperioder.get(4);
        assertThat(utsettelse.getUtsettelseType()).isEqualTo(UttakUtsettelseÅrsak.ARBEID);
        assertThat(utsettelse.getAktiviteter().get(0).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(utsettelse.getAktiviteter().get(0).getTrekkdagerDesimaler()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(utsettelse.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(INGEN_STØNADSKONTO);
        assertThat(utsettelse.getAktiviteter().get(1).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(utsettelse.getAktiviteter().get(1).getTrekkdagerDesimaler()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(utsettelse.getAktiviteter().get(1).getStønadskontoType()).isEqualTo(INGEN_STØNADSKONTO);

        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().size() == 4,
                "Feil i antall stønadskontoer, skal være 4.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL)
                .getSaldo() >= 0, "FPFF skal ikke være i minus.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FELLESPERIODE).getSaldo() >= 0,
                "Fellerperiode skal ikke være i minus.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(MØDREKVOTE).getSaldo() >= 0,
                "Mødrekvote skal ikke være i minus");

        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
    }

    @Test
    @DisplayName("Mor søker fødsel med aleneomsorg")
    @Description("Mor søker fødsel aleneomsorg. Annen forelder ikke kjent.")
    public void morSøkerFødselAleneomsorgKunEnHarRett() {

        TestscenarioDto testscenario = opprettTestscenario("102");

        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medRettigheter(RettigheterErketyper.harAleneOmsorgOgEnerett())
                .medFordeling(FordelingErketyper.fordelingMorAleneomsorgHappyCase(fødselsdato));
        LocalDate startdatoForeldrePenger = fødselsdato.minusWeeks(3);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                startdatoForeldrePenger,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        // saksbehandler.ventTilØkonomioppdragFerdigstilles();

        // verifiserer uttak
        List<UttakResultatPeriode> uttaksperioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttaksperioder).hasSize(4);

        UttakResultatPeriode fpff = uttaksperioder.get(0);
        assertThat(fpff.getPeriodeResultatType().kode).isEqualTo("INNVILGET");
        assertThat(fpff.getAktiviteter().get(0).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(fpff.getAktiviteter().get(0).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(fpff.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER_FØR_FØDSEL);
        UttakResultatPeriode foreldrepengerFørste6Ukene = uttaksperioder.get(1);
        assertThat(foreldrepengerFørste6Ukene.getPeriodeResultatType().kode).isEqualTo("INNVILGET");
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);
        UttakResultatPeriode foreldrepengerEtterUke6 = uttaksperioder.get(2);
        assertThat(foreldrepengerFørste6Ukene.getPeriodeResultatType().kode).isEqualTo("INNVILGET");
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isEqualByComparingTo(BigDecimal.valueOf(200));
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);
        // Periode søkt mer enn 49 uker er avslått automatisk
        UttakResultatPeriode periodeMerEnn49Uker = uttaksperioder.get(3);
        assertThat(periodeMerEnn49Uker.getPeriodeResultatType().kode).isEqualTo("AVSLÅTT");
        assertThat(periodeMerEnn49Uker.getPeriodeResultatÅrsak().kode).isEqualTo("4002");
        assertThat(periodeMerEnn49Uker.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(0));
        assertThat(periodeMerEnn49Uker.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(periodeMerEnn49Uker.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);

        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().size() == 2,
                "Feil antall stønadskontoer");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL)
                .getSaldo() >= 0, "FPFF skal ikke være minus.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER).getSaldo() >= 0,
                "Foreldrepenger skal ikke være i minus.");

        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
    }

    @Test
    @DisplayName("Mor søker fødsel for 2 barn med 1 barn registrert")
    @Description("Mor søker fødsel for 2 barn med 1 barn registrert. dette fører til aksjonspunkt for bekreftelse av antall barn")
    public void morSøker2Barn1Registrert() {
        TestscenarioDto testscenario = opprettTestscenario("50");

        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepenger(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(2, fødselsdato));
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                startDatoForeldrepenger,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        VurderManglendeFodselBekreftelse fodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(2, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(fodselBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
    }

    @Test
    @DisplayName("Mor søker uregistrert fødsel før det har gått 2 uker")
    @Description("Mor søker uregistrert fødsel før det har gått 2 uker - skal sette behandling på vent")
    public void morSøkerUregistrertEtterFør2Uker() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusDays(5);
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                startDatoForeldrepenger,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);
        saksbehandler.hentFagsak(saksnummer);

        verifiser(saksbehandler.valgtBehandling.erSattPåVent(),
                "Behandlingen er ikke satt på vent selv om behandlingen ikke har ventet til 2. uke");
        logger.debug("{}", saksbehandler.valgtBehandling.fristBehandlingPaaVent);
        logger.debug("{}", fødselsdato.plusWeeks(2));
        verifiser(saksbehandler.valgtBehandling.fristBehandlingPaaVent.equals(fødselsdato.plusWeeks(2)),
                "Behandlingen er satt på vent for lenge");
    }

    @Test
    @DisplayName("Utsettelse av forskjellige årsaker")
    @Description("Mor søker fødsel med mange utsettelseperioder. Hensikten er å sjekke at alle årsaker fungerer." +
            "Kun arbeid (og ferie) skal oppgis i IM. Verifiserer på 0 trekkdager for perioder med utsettelse. " +
            "Kun perioder som krever dokumentasjon skal bli manuelt behandlet i fakta om uttak. Ingen AP i uttak.")
    public void utsettelse_med_avvik() {
        TestscenarioDto testscenario = opprettTestscenario("50");

        String søkerAktørId = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødsel = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødsel.minusWeeks(3);

        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødsel.minusDays(1)));
        perioder.add(uttaksperiode(MØDREKVOTE, fødsel, fødsel.plusWeeks(6).minusDays(1)));
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.INSTITUSJON_BARN, fødsel.plusWeeks(6),
                fødsel.plusWeeks(9).minusDays(1)));
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.INSTITUSJON_SØKER, fødsel.plusWeeks(9),
                fødsel.plusWeeks(12).minusDays(1)));
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.SYKDOM, fødsel.plusWeeks(12),
                fødsel.plusWeeks(15).minusDays(1)));
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødsel.plusWeeks(15),
                fødsel.plusWeeks(16).minusDays(1)));
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.HV_OVELSE, fødsel.plusWeeks(16),
                fødsel.plusWeeks(17).minusDays(1)));
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.NAV_TILTAK, fødsel.plusWeeks(17),
                fødsel.plusWeeks(18).minusDays(1)));
        perioder.add(uttaksperiode(FELLESPERIODE, fødsel.plusWeeks(18),
                fødsel.plusWeeks(21).minusDays(1)));

        // sender inn søknad
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødsel, søkerAktørId, SøkersRolle.MOR)
                .medFordeling(fordeling);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        inntektsmeldinger.medUtsettelse(SøknadUtsettelseÅrsak.ARBEID.getKode(), fødsel.plusWeeks(15),
                fødsel.plusWeeks(18).minusDays(1));
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaUttakPerioder = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class);
        avklarFaktaUttakPerioder.godkjennPeriode(fødsel.plusWeeks(6), fødsel.plusWeeks(9).minusDays(1))
                .godkjennPeriode(fødsel.plusWeeks(9), fødsel.plusWeeks(12).minusDays(1))
                .godkjennPeriode(fødsel.plusWeeks(12), fødsel.plusWeeks(15).minusDays(1))
                .godkjennPeriode(fødsel.plusWeeks(16), fødsel.plusWeeks(17).minusDays(1))
                .godkjennPeriode(fødsel.plusWeeks(17), fødsel.plusWeeks(18).minusDays(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakPerioder);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiser(beslutter.valgtBehandling.hentUttaksperioder().size() == 9,
                "Feil antall uttaksperioder, skal være 9");
        verifiser(beslutter.valgtBehandling.hentUttaksperiode(2).getAktiviteter().get(0).getTrekkdagerDesimaler()
                .compareTo(BigDecimal.ZERO) == 0, "Feil i antall trekkdager, skal være 0");
        verifiser(beslutter.valgtBehandling.hentUttaksperiode(3).getAktiviteter().get(0).getTrekkdagerDesimaler()
                .compareTo(BigDecimal.ZERO) == 0, "Feil i antall trekkdager, skal være 0");
        verifiser(beslutter.valgtBehandling.hentUttaksperiode(4).getAktiviteter().get(0).getTrekkdagerDesimaler()
                .compareTo(BigDecimal.ZERO) == 0, "Feil i antall trekkdager, skal være 0");
        verifiser(beslutter.valgtBehandling.hentUttaksperiode(5).getAktiviteter().get(0).getTrekkdagerDesimaler()
                .compareTo(BigDecimal.ZERO) == 0, "Feil i antall trekkdager, skal være 0");
        verifiser(beslutter.valgtBehandling.hentUttaksperiode(6).getAktiviteter().get(0).getTrekkdagerDesimaler()
                .compareTo(BigDecimal.ZERO) == 0, "Feil i antall trekkdager, skal være 0");
        verifiser(beslutter.valgtBehandling.hentUttaksperiode(7).getAktiviteter().get(0).getTrekkdagerDesimaler()
                .compareTo(BigDecimal.ZERO) == 0, "Feil i antall trekkdager, skal være 0");

    }

    private UttakResultatPeriodeAktivitet finnAktivitetForArbeidsgiver(UttakResultatPeriode uttakResultatPeriode,
            String identifikator) {
        return uttakResultatPeriode.getAktiviteter().stream()
                .filter(a -> a.getArbeidsgiverReferanse().equals(identifikator)).findFirst().orElseThrow();
    }

    // TODO må ta inn fordeling som blir laget i søknad for å kunne verifisere
    // rikitg på hva som er i VL!
    // Denne verifisering kan bare brukes hvis fordeling = fordelingMorHappyCaseLong
    @Step("Verifiserer utttaksperioder")
    private void verifiserUttak(int antallAktiviteter, List<UttakResultatPeriode> perioder) {
        assertThat(perioder).hasSize(4);
        verifiserUttaksperiode(perioder.get(0), FORELDREPENGER_FØR_FØDSEL, antallAktiviteter);
        verifiserUttaksperiode(perioder.get(1), MØDREKVOTE, antallAktiviteter);
        verifiserUttaksperiode(perioder.get(2), MØDREKVOTE, antallAktiviteter);
        verifiserUttaksperiode(perioder.get(3), FELLESPERIODE, antallAktiviteter);
    }

    private void verifiserUttaksperiode(UttakResultatPeriode uttakResultatPeriode, Stønadskonto stønadskonto,
            int antallAktiviteter) {
        assertThat(uttakResultatPeriode.getPeriodeResultatType().kode).isEqualTo("INNVILGET");
        assertThat(uttakResultatPeriode.getUtsettelseType()).isEqualTo(UttakUtsettelseÅrsak.UDEFINERT);
        assertThat(uttakResultatPeriode.getAktiviteter()).hasSize(antallAktiviteter);
        for (UttakResultatPeriodeAktivitet aktivitet : uttakResultatPeriode.getAktiviteter()) {
            assertThat(aktivitet.getStønadskontoType()).isEqualTo(stønadskonto);
            assertThat(aktivitet.getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
            assertThat(aktivitet.getUtbetalingsgrad()).isGreaterThan(BigDecimal.ZERO);
        }
    }

    @Step("Verifiserer tilkjent ytelse")
    private void verifiserTilkjentYtelse(BeregningsresultatMedUttaksplan beregningResultatForeldrepenger,
            boolean medFullRefusjon) {
        List<BeregningsresultatPeriode> perioder = beregningResultatForeldrepenger.getPerioder();
        assertThat(beregningResultatForeldrepenger.isSokerErMor()).isTrue();
        assertThat(perioder.size()).isGreaterThan(0);
        for (var periode : perioder) {
            assertThat(periode.getDagsats()).isGreaterThan(0);
            List<BeregningsresultatPeriodeAndel> andeler = periode.getAndeler();
            for (var andel : andeler) {
                String kode = andel.getAktivitetStatus().kode;
                if (kode.equals("AT")) {
                    if (medFullRefusjon) {
                        assertThat(andel.getTilSoker()).isZero();
                        assertThat(andel.getRefusjon()).isGreaterThan(0);
                    } else {
                        assertThat(andel.getTilSoker()).isGreaterThan(0);
                        assertThat(andel.getRefusjon()).isZero();
                    }
                } else if (kode.equals("FL") || kode.equals("SN")) {
                    assertThat(andel.getTilSoker()).isGreaterThan(0);
                    assertThat(andel.getRefusjon()).isZero();
                }
                assertThat(andel.getUttak().isGradering()).isFalse();
                assertThat(andel.getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
            }
        }
    }
}
