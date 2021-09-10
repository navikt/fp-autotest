package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.Stønadskonto.FORELDREPENGER;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepenger;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.UttaksperioderErketyper.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmeldingPrivateArbeidsgiver;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.Stønadskonto;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.RelasjonTilBarnetErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.IkkeOppfyltÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakPeriodeVurderingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerAktivitetskravBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAleneomsorgBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaUttakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadEndringForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatMedUttaksplan;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.ObjectFactory;

@Tag("fpsak")
@Tag("foreldrepenger")
class Fodsel extends ForeldrepengerTestBase {

    private static final Logger logger = LoggerFactory.getLogger(Fodsel.class);

    @Test
    @DisplayName("Mor fødsel med arbeidsforhold og frilans. Vurderer opptjening og beregning. Finner avvik")
    @Description("Mor søker fødsel med ett arbeidsforhold og frilans. Vurder opptjening. Vurder fakta om beregning. Avvik i beregning")
    void morSøkerFødselMedEttArbeidsforholdOgFrilans_VurderOpptjening_VurderFaktaOmBeregning_AvvikIBeregning() {
        var testscenario = opprettTestscenario("59");

        var fnr = testscenario.personopplysninger().søkerIdent();
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr();

        var inntektPerMåned = 20_000;
        var overstyrtInntekt = 500_000;
        var overstyrtFrilanserInntekt = 500_000;
        var refusjon = BigDecimal.valueOf(overstyrtInntekt / 12);

        var opptjening = OpptjeningErketyper.medFrilansOpptjening();
        var søknad = lagSøknadForeldrepengerTermin(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medSpesiellOpptjening(opptjening);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr);
        inntektsmeldingBuilder.medRefusjonsBelopPerMnd(refusjon);

        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkinnslagType.VEDLEGG_MOTTATT);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        // Verifiser Beregningsgrunnlag
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus())
                .as("Antall aktivitetstatus")
                .isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0))
                .as("Aktivitetsstatus i beregnignsgrunnlag")
                .isEqualTo(AktivitetStatus.KOMBINERT_AT_FL);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto())
                .as("Antall beregningsgrunnlagsparioder")
                .isEqualTo(1);
        var andeler = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        assertThat(andeler.size())
                .as("Antall andeler")
                .isEqualTo(2);
        assertThat(andeler.get(0).getAktivitetStatus())
                .as("Aktivitetsstatus")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        assertThat(andeler.get(1).getAktivitetStatus())
                .as("Aktivitetsstatus")
                .isEqualTo(AktivitetStatus.FRILANSER);

        // Legg til og fjern ytelser for å se tilbakehopp og opprettelse av akjsonspunkter
        assertThat(saksbehandler.harAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS))
                .as("Har aksjonspunkt FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS")
                .isTrue();

        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntektFrilans(overstyrtFrilanserInntekt)
                .leggTilInntekt(overstyrtInntekt, 2)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(2, beslutter.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(beslutter.valgtBehandling.getBeregningResultatForeldrepenger(), true);

    }


    @Test
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold og avvik i beregning")
    void morSøkerFødselMedToArbeidsforhold_AvvikIBeregning() {
        var testscenario = opprettTestscenario("57");
        var fnr = testscenario.personopplysninger().søkerIdent();
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);

        var inntektPrMåned = 50_000;
        var overstyrtInntekt = inntektPrMåned * 12;
        var refusjon = BigDecimal.valueOf(inntektPrMåned);

        var arbeidsforhold_1 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold()
                .get(0);
        var arbeidsforhold_2 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold()
                .get(1);
        var arbeidsforholdId_1 = arbeidsforhold_1.arbeidsforholdId();
        var arbeidsforholdId_2 = arbeidsforhold_2.arbeidsforholdId();
        var orgNr_1 = arbeidsforhold_1.arbeidsgiverOrgnr();
        var orgNr_2 = arbeidsforhold_2.arbeidsgiverOrgnr();

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);

        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var inntektsmeldingBuilder_1 = lagInntektsmelding(inntektPrMåned, fnr, fpStartdato, orgNr_1)
                .medRefusjonsBelopPerMnd(refusjon)
                .medArbeidsforholdId(arbeidsforholdId_1);
        var inntektsmeldingBuilder_2 = lagInntektsmelding(inntektPrMåned, fnr, fpStartdato, orgNr_2)
                .medRefusjonsBelopPerMnd(refusjon)
                .medArbeidsforholdId(arbeidsforholdId_2);

        fordel.sendInnInntektsmeldinger(Arrays.asList(inntektsmeldingBuilder_1, inntektsmeldingBuilder_2), testscenario,
                saksnummer);


        saksbehandler.hentFagsak(saksnummer);
        debugLoggBehandling(saksbehandler.valgtBehandling);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(overstyrtInntekt, 1)
                .leggTilInntekt(overstyrtInntekt, 2)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(2, beslutter.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(beslutter.valgtBehandling.getBeregningResultatForeldrepenger(), true);

    }

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold og avvik i beregning")
    void morSøkerFødselMedEttArbeidsforhold_AvvikIBeregning() {
        var testscenario = opprettTestscenario("500");
        var orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var fnr = testscenario.personopplysninger().søkerIdent();
        var aktørID = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);

        var inntektPrMåned = 15_000;
        var refusjon = BigDecimal.valueOf(inntektPrMåned);
        var overstyrtInntekt = inntektPrMåned * 12;

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørID, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldingBuilder = lagInntektsmelding(inntektPrMåned, fnr, fpStartdato, orgNr)
                .medRefusjonsBelopPerMnd(refusjon);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(overstyrtInntekt, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(1, beslutter.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(beslutter.valgtBehandling.getBeregningResultatForeldrepenger(), true);
    }

    @Test
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold i samme organisasjon")
    void morSøkerFødselMedToArbeidsforholdISammeOrganisasjon() {
        var testscenario = opprettTestscenario("57");
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var fnr = testscenario.personopplysninger().søkerIdent();
        var arbeidsforhold1 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0);
        var arbeidsforhold2 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1);
        var orgNr1 = arbeidsforhold1.arbeidsgiverOrgnr();
        var orgNr2 = arbeidsforhold2.arbeidsgiverOrgnr();

        var inntekter = sorterteInntektsbeløp(testscenario);
        var inntektsmeldingBuilder1 = lagInntektsmelding(inntekter.get(0), fnr, fpStartdato, orgNr1)
                        .medArbeidsforholdId(arbeidsforhold1.arbeidsforholdId());
        var inntektsmeldingBuilder2 = lagInntektsmelding(inntekter.get(1), fnr, fpStartdato, orgNr2)
                        .medArbeidsforholdId(arbeidsforhold2.arbeidsforholdId());

        fordel.sendInnInntektsmeldinger(Arrays.asList(inntektsmeldingBuilder1, inntektsmeldingBuilder2), testscenario,
                saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        debugLoggBehandling(saksbehandler.valgtBehandling);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(2, saksbehandler.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger(), false);
    }

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold")
    void morSøkerFødselMedEttArbeidsforhold() {
        var testscenario = opprettTestscenario("500");
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldinger = lagInntektsmelding(
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
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(1, saksbehandler.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger(), false);
    }

    @Test
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold med inntekt over 6G")
    void morSøkerFødselMedToArbeidsforhold() {
        var testscenario = opprettTestscenario("56");

        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);

        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldingEn = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        var inntektsmeldingerTo = lagInntektsmelding(
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

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(2, saksbehandler.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger(), false);
    }

    @Test
    @DisplayName("Far søker fødsel med 1 arbeidsforhold")
    void farSøkerFødselMedEttArbeidsforhold() {
        var testscenario = opprettTestscenario("550");

        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var startDatoForeldrepenger = fødselsdato.plusWeeks(3);

        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        logger.info("Ident: " + søkerIdent);

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.FAR);

        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldinger = lagInntektsmelding(
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

        var avklarFaktaUttakBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class)
                .godkjennPeriode(startDatoForeldrepenger, startDatoForeldrepenger.plusWeeks(2),
                        UttakPeriodeVurderingType.PERIODE_KAN_IKKE_AVKLARES);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerAktivitetskravBekreftelse.class);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class)
                .innvilgPeriode(startDatoForeldrepenger,
                startDatoForeldrepenger.plusWeeks(2));
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        assertThat(saksbehandler.valgtBehandling.hentUttaksperioder().size())
                .as("Uttaksperioder")
                .isEqualTo(1);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.harHistorikkinnslagForBehandling(HistorikkinnslagType.VEDTAK_FATTET))
                .as("Har historikkinnslag VEDTAK_FATTET")
                .isTrue();
        beslutter.ventTilHistorikkinnslag(HistorikkinnslagType.BREV_BESTILT);

    }

    @Test
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold i samme organisasjon med 1 inntektsmelding")
    void morSøkerFødselMedToArbeidsforholdISammeOrganisasjonEnInntektsmelding() {

        var testscenario = opprettTestscenario("57");

        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var fnr = testscenario.personopplysninger().søkerIdent();
        var inntekter = sorterteInntektsbeløp(testscenario);
        var orgnr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var inntektsmelding = lagInntektsmelding(inntekter.get(0) + inntekter.get(1), fnr,
                fpStartdato, orgnr);

        fordel.sendInnInntektsmelding(inntektsmelding, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(1, saksbehandler.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger(), false);

    }

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold, Papirsøkand")
    void morSøkerFødselMedEttArbeidsforhold_papirsøknad() {
        var testscenario = opprettTestscenario("50");

        var saksnummer = fordel.sendInnPapirsøknadForeldrepenger(testscenario, false);
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);
        var inntektsmeldinger = lagInntektsmelding(
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

        var aksjonspunktBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(PapirSoknadForeldrepengerBekreftelse.class);
        var fordeling = new FordelingDto();
        var fpff = new PermisjonPeriodeDto(FORELDREPENGER_FØR_FØDSEL,
                startDatoForeldrepenger, fødselsdato.minusDays(1));
        var mødrekvote = new PermisjonPeriodeDto(MØDREKVOTE,
                fødselsdato, fødselsdato.plusWeeks(10));
        fordeling.permisjonsPerioder.add(fpff);
        fordeling.permisjonsPerioder.add(mødrekvote);
        aksjonspunktBekreftelse.morSøkerFødsel(fordeling, fødselsdato, fpff.periodeFom.minusWeeks(3));

        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelse);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // verifiserer uttak
        var perioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(perioder).hasSize(3);
        verifiserUttaksperiode(perioder.get(0), FORELDREPENGER_FØR_FØDSEL, 1);
        verifiserUttaksperiode(perioder.get(1), MØDREKVOTE, 1);
        verifiserUttaksperiode(perioder.get(2), MØDREKVOTE, 1);

        fordel.sendInnPapirsøknadEndringForeldrepenger(testscenario, saksnummer);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var aksjonspunktBekreftelseEndringssøknad = saksbehandler
                .hentAksjonspunktbekreftelse(PapirSoknadEndringForeldrepengerBekreftelse.class);
        var fordelingEndringssøknad = new FordelingDto();
        // Legger til fellesperiode på slutten
        var fellesperiode = new PermisjonPeriodeDto(FELLESPERIODE,
                fødselsdato.plusWeeks(10).plusDays(1), fødselsdato.plusWeeks(15));
        fordelingEndringssøknad.permisjonsPerioder.add(fellesperiode);
        aksjonspunktBekreftelseEndringssøknad.setFordeling(fordelingEndringssøknad);
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelseEndringssøknad);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);

        // verifiserer uttak
        var perioderEtterEndringssøknad = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(perioderEtterEndringssøknad).hasSize(4);
        verifiserUttaksperiode(perioderEtterEndringssøknad.get(0), FORELDREPENGER_FØR_FØDSEL, 1);
        verifiserUttaksperiode(perioderEtterEndringssøknad.get(1), MØDREKVOTE, 1);
        verifiserUttaksperiode(perioderEtterEndringssøknad.get(2), MØDREKVOTE, 1);
        verifiserUttaksperiode(perioderEtterEndringssøknad.get(3), FELLESPERIODE, 1);
    }

    @Test
    @Description("Mor søker fødsel med 2 arbeidsforhold med arbeidsforhold som ikke matcher på ID")
    @DisplayName("Mor søker fødsel med 2 arbeidsforhold med arbeidsforhold som ikke matcher på ID")
    void morSøkerFødselMed2ArbeidsforholdArbeidsforholdIdMatcherIkke() {
        var testscenario = opprettTestscenario("56");
        var fnr = testscenario.personopplysninger().søkerIdent();
        var orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var orgNr2 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1)
                .arbeidsgiverOrgnr();

        var inntektPerMåned = 20_000;

        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr,
                startDatoForeldrepenger,orgNr)
                .medArbeidsforholdId("1");
        var inntektsmeldingBuilder2 = lagInntektsmelding(inntektPerMåned, fnr,
                startDatoForeldrepenger,orgNr2)
                .medArbeidsforholdId("9");

        fordel.sendInnInntektsmeldinger(List.of(inntektsmeldingBuilder,inntektsmeldingBuilder2), testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandling satt på vent")
                .isTrue();
        debugLoggBehandling(saksbehandler.valgtBehandling);

    }

    @Test
    @Description("Mor søker fødsel med privatperson som arbeidsgiver")
    @DisplayName("Mor søker fødsel med privatperson som arbeidsgiver, avvik i beregning")
    void morSøkerFødselMedPrivatpersonSomArbeidsgiver() {

        var overstyrtInntekt = 250_000;

        // Lag privat arbeidsgiver
        var arbeidsgiverScenario = opprettTestscenario("59");
        var arbeidsgiverFnr = arbeidsgiverScenario.personopplysninger().søkerIdent();
        var arbeidsgiverAktørId = arbeidsgiverScenario.personopplysninger().søkerAktørIdent();

        // Lag testscenario
        var testscenario = opprettTestscenarioMedPrivatArbeidsgiver("152", arbeidsgiverAktørId, arbeidsgiverFnr);

        // Send inn søknad
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = LocalDate.now().minusDays(2);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        // Send inn inntektsmelding
        var inntektPerMaaned = 10_000;
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);
        var fnr = testscenario.personopplysninger().søkerIdent();
        var inntektsmeldingBuilder = lagInntektsmeldingPrivateArbeidsgiver(inntektPerMaaned, fnr,
                startDatoForeldrepenger, arbeidsgiverFnr);

        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);

        // Bekreft Opptjening
        var vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class)
                .godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // Verifiser Beregningsgrunnlag
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus())
                .as("Antall aktivitetsstatus i beregningsgrunnlag")
                .isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0))
                .as("Aktivitetstatus")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto())
                .as("Antall beregningsgrunnlagperioder i beregnignsgrunnlag")
                .isEqualTo(1);
        List<BeregningsgrunnlagPrStatusOgAndelDto> andeler = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        assertThat(andeler)
                .as("Andeler")
                .hasSize(1);
        assertThat(andeler.get(0).getAktivitetStatus())
                .as("Aktivitetsstatus i andel")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);

        // Bekreft inntekt i beregning
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(overstyrtInntekt, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        // Foreslå vedtak
        var foreslåVedtakBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(ForeslåVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(foreslåVedtakBekreftelse);

        // Totrinnskontroll
        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(1, beslutter.valgtBehandling.hentUttaksperioder());
        verifiserTilkjentYtelse(beslutter.valgtBehandling.getBeregningResultatForeldrepenger(), false);
    }

    @Test
    @Description("Mor søker fødsel med privatperson som arbeidsgiver med endring i refusjon")
    @DisplayName("Mor søker fødsel med privatperson som arbeidsgiver med endring i refusjon, avvik i beregning")
    void morSøkerFødselMedPrivatpersonSomArbeidsgiverMedEndringIRefusjon() {

        var overstyrtInntekt = 250_000;

        // Lag privat arbeidsgiver
        var arbeidsgiverScenario = opprettTestscenario("59");
        var arbeidsgiverFnr = arbeidsgiverScenario.personopplysninger().søkerIdent();
        var arbeidsgiverAktørId = arbeidsgiverScenario.personopplysninger().søkerAktørIdent();

        // Lag testscenario
        var testscenario = opprettTestscenarioMedPrivatArbeidsgiver("152", arbeidsgiverAktørId, arbeidsgiverFnr);

        // Send inn søknad
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = LocalDate.now().minusDays(2);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        // Send inn inntektsmelding
        var inntektPerMaaned = 10_000;
        var refusjon = 25_000;
        var endret_refusjon = 10_000;
        var endringsdato = fødselsdato.plusMonths(1);
        HashMap<LocalDate, BigDecimal> endringRefusjonMap = new HashMap<>();
        endringRefusjonMap.put(endringsdato, BigDecimal.valueOf(endret_refusjon));
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);
        var fnr = testscenario.personopplysninger().søkerIdent();
        var inntektsmeldingBuilder = lagInntektsmeldingPrivateArbeidsgiver(inntektPerMaaned, fnr, startDatoForeldrepenger,
                arbeidsgiverFnr)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(refusjon))
                .medEndringIRefusjonslist(endringRefusjonMap);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);

        // Bekreft Opptjening
        var vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class)
                .godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // Verifiser Beregningsgrunnlag
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus())
                .as("Antall aktivitetstatuser i beregnignsgrunnlag")
                .isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0))
                .as("Aktivitetsstatus i beregnignsgrunnlag")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto())
                .as("Antall beregningsgrunnlagperioder i beregningsgrunnlag")
                .isEqualTo(1);
        var andelerFørstePeriode = saksbehandler.valgtBehandling
                .getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        assertThat(andelerFørstePeriode)
                .as("Andeler i første periode")
                .hasSize(1);
        assertThat(andelerFørstePeriode.get(0).getAktivitetStatus())
                .as("Aktivitetsstatus i andel i første periode")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);

        // Bekreft inntekt i beregning
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(overstyrtInntekt, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.FORESLÅ_VEDTAK);

        // Verifiser Beregningsgrunnlag
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallAktivitetStatus())
                .as("Antall aktivitetsstatus i beregningsgrunnlag")
                .isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0))
                .as("Aktivitetstatus")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto())
                .as("Antall beregningsgrunnlagperioder i beregnignsgrunnlag")
                .isEqualTo(2);
        andelerFørstePeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0).getBeregningsgrunnlagPrStatusOgAndel();
        assertThat(andelerFørstePeriode)
                .as("Andeler")
                .hasSize(1);
        assertThat(andelerFørstePeriode.get(0).getAktivitetStatus())
                .as("Aktivitetsstatus i andel")
                .isEqualTo(AktivitetStatus.ARBEIDSTAKER);

        // Assert refusjon
        var resultatPerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger().getPerioder();
        assertThat(resultatPerioder.size()).isEqualTo(5);
        var forventetDagsats = BigDecimal.valueOf(overstyrtInntekt).divide(BigDecimal.valueOf(260),
                RoundingMode.HALF_EVEN);
        assertThat(resultatPerioder.get(0).getAndeler().get(0).getRefusjon()).isEqualTo(forventetDagsats.intValue());
        assertThat(resultatPerioder.get(1).getAndeler().get(0).getRefusjon()).isEqualTo(forventetDagsats.intValue());
        var forventetRefusjon = BigDecimal.valueOf(endret_refusjon * 12).divide(BigDecimal.valueOf(260),
                RoundingMode.HALF_EVEN);
        assertThat(resultatPerioder.get(2).getAndeler().get(0).getRefusjon()).isEqualTo(forventetRefusjon.intValue());
        assertThat(resultatPerioder.get(3).getAndeler().get(0).getRefusjon()).isEqualTo(forventetRefusjon.intValue());

        // Foreslå vedtak
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // Totrinnskontroll
        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        verifiserUttak(1, beslutter.valgtBehandling.hentUttaksperioder());
    }

    @Test
    @DisplayName("Far søker fødsel med aleneomsorg men er gift og bor med annenpart")
    void farSøkerFødselAleneomsorgMenErGiftOgBorMedAnnenpart() {

        var testscenario = opprettTestscenario("550");

        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.FAR)
                .medRettigheter(RettigheterErketyper.harAleneOmsorgOgEnerett())
                .medFordeling(FordelingErketyper.fordelingFarAleneomsorg(fødselsdato));

        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldinger = lagInntektsmelding(
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
        var bekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAleneomsorgBekreftelse.class)
                .bekreftBrukerHarIkkeAleneomsorg();
        saksbehandler.bekreftAksjonspunktbekreftelserer(bekreftelse);
        var avklarFaktaUttakPerioder = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class)
                .delvisGodkjennPeriode(fødselsdato, fødselsdato.plusWeeks(20), fødselsdato, fødselsdato.plusWeeks(20),
                        UttakPeriodeVurderingType.PERIODE_KAN_IKKE_AVKLARES);  // 20 uker fra erketype
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakPerioder);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerAktivitetskravBekreftelse.class);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class)
                .innvilgManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var fatterVedtakBekreftelse = beslutter
                .hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(fatterVedtakBekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // verifiserer uttak
        var uttaksperioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttaksperioder).hasSize(2);

        // uttak går til manuell behandling
        var foreldrepengerFørste6Ukene = uttaksperioder.get(0);
        assertThat(foreldrepengerFørste6Ukene.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);
        var foreldrepengerEtterUke6 = uttaksperioder.get(1);
        assertThat(foreldrepengerEtterUke6.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);

        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer())
                .as("Stonadskontoer i Saldo")
                .hasSize(4);
    }

    @Test
    @DisplayName("Mor søker fødsel har stillingsprosent 0")
    @Description("Mor søker fødsel har stillingsprosent 0 som fører til aksjonspunkt for opptjening")
    void morSøkerFødselStillingsprosent0() {
        var testscenario = opprettTestscenario("45");

        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldinger = lagInntektsmelding(
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
        var vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class)
                .godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter
                .hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker gradering. Med to arbeidsforhold. Uten avvikende inntektsmelding")
    @Description("Mor, med to arbeidsforhold, søker gradering. Samsvar med IM.")
    void morSøkerGraderingOgUtsettelseMedToArbeidsforhold_utenAvvikendeInntektsmeldinger() {

        var testscenario = opprettTestscenario("56");

        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();

        var fordeling = new ObjectFactory().createFordeling();
        fordeling.setAnnenForelderErInformert(true);
        var perioder = fordeling.getPerioder();
        var startdatoForeldrePenger = fødselsdato.minusWeeks(3);
        perioder.add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, startdatoForeldrePenger, fødselsdato.minusDays(1)));
        perioder.add(uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)));
        var gradetArbeidsgiver = "910909088";
        var graderingFom = fødselsdato.plusWeeks(10).plusDays(1);
        var graderingTom = fødselsdato.plusWeeks(12);
        var arbeidstidsprosent = BigDecimal.TEN;
        perioder.add(graderingsperiodeArbeidstaker(FELLESPERIODE, graderingFom, graderingTom, gradetArbeidsgiver,
                arbeidstidsprosent.intValue()));

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medFordeling(fordeling);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldingEn = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                startdatoForeldrePenger,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        var inntektsmeldingerTo = lagInntektsmelding(
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
        var uttaksperioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttaksperioder).hasSize(4);
        for (UttakResultatPeriode periode : uttaksperioder) {
            assertThat(periode.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
            assertThat(periode.getPeriodeResultatÅrsak().getKode()).isNotEqualTo(PeriodeResultatÅrsak.UKJENT.getKode());
            assertThat(periode.getAktiviteter()).hasSize(2);
            for (UttakResultatPeriodeAktivitet aktivitet : periode.getAktiviteter()) {
                assertThat(aktivitet.getArbeidsgiverReferanse()).isNotNull();
                assertThat(aktivitet.getUttakArbeidType().kode).isEqualTo("ORDINÆRT_ARBEID");
                var arbeidsforholdFraScenario = testscenario.scenariodataDto().arbeidsforholdModell()
                        .arbeidsforhold();
                assertThat(aktivitet.getArbeidsgiverReferanse()).isIn(
                        arbeidsforholdFraScenario.get(0).arbeidsgiverOrgnr(),
                        arbeidsforholdFraScenario.get(1).arbeidsgiverOrgnr());
            }
        }
        var fpff = uttaksperioder.get(0);
        assertThat(fpff.getAktiviteter().get(0).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(fpff.getAktiviteter().get(0).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(fpff.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER_FØR_FØDSEL);
        assertThat(fpff.getAktiviteter().get(1).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(fpff.getAktiviteter().get(1).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(fpff.getAktiviteter().get(1).getStønadskontoType()).isEqualTo(FORELDREPENGER_FØR_FØDSEL);
        var mødrekvoteFørste6Ukene = uttaksperioder.get(1);
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
        var mødrekvoteEtterUke6 = uttaksperioder.get(2);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(0).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(MØDREKVOTE);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(1).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(1).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mødrekvoteEtterUke6.getAktiviteter().get(1).getStønadskontoType()).isEqualTo(MØDREKVOTE);
        var gradering = uttaksperioder.get(3);
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

        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer())
                .as("Antall stønadskonter i saldo")
                .hasSize(4);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo())
                .as("Saldo igjen på FORELDREPENGER_FØR_FØDSEL")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FELLESPERIODE).getSaldo())
                .as("Saldo igjen på FELLESPERIODE")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(MØDREKVOTE).getSaldo())
                .as("Saldo igjen på MØDREKVOTE")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker fødsel med aleneomsorg")
    @Description("Mor søker fødsel aleneomsorg. Annen forelder ikke kjent.")
    void morSøkerFødselAleneomsorgKunEnHarRett() {

        var testscenario = opprettTestscenario("102");

        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medRettigheter(RettigheterErketyper.harAleneOmsorgOgEnerett())
                .medFordeling(FordelingErketyper.fordelingMorAleneomsorgHappyCase(fødselsdato));
        var startdatoForeldrePenger = fødselsdato.minusWeeks(3);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var inntektsmeldinger = lagInntektsmelding(
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
        var uttaksperioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttaksperioder).hasSize(4);

        var fpff = uttaksperioder.get(0);
        assertThat(fpff.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(fpff.getAktiviteter().get(0).getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(fpff.getAktiviteter().get(0).getTrekkdagerDesimaler()).isGreaterThan(BigDecimal.ZERO);
        assertThat(fpff.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER_FØR_FØDSEL);
        var foreldrepengerFørste6Ukene = uttaksperioder.get(1);
        assertThat(foreldrepengerFørste6Ukene.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(foreldrepengerFørste6Ukene.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);
        var foreldrepengerEtterUke6 = uttaksperioder.get(2);
        assertThat(foreldrepengerFørste6Ukene.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isEqualByComparingTo(BigDecimal.valueOf(200));
        assertThat(foreldrepengerEtterUke6.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);
        // Periode søkt mer enn 49 uker er avslått automatisk
        var periodeMerEnn49Uker = uttaksperioder.get(3);
        assertThat(periodeMerEnn49Uker.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.AVSLÅTT);
        assertThat(periodeMerEnn49Uker.getPeriodeResultatÅrsak()).isEqualTo(IkkeOppfyltÅrsak.IKKE_STØNADSDAGER_IGJEN);
        assertThat(periodeMerEnn49Uker.getAktiviteter().get(0).getUtbetalingsgrad())
                .isEqualByComparingTo(BigDecimal.valueOf(0));
        assertThat(periodeMerEnn49Uker.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(periodeMerEnn49Uker.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FORELDREPENGER);

        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer())
                .as("Antall stønadskonter i saldo")
                .hasSize(2);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo())
                .as("Saldo igjen på FORELDREPENGER_FØR_FØDSEL")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER).getSaldo())
                .as("Saldo igjen på FELLESPERIODE")
                .isNotNegative();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker fødsel for 2 barn med 1 barn registrert")
    @Description("Mor søker fødsel for 2 barn med 1 barn registrert. dette fører til aksjonspunkt for bekreftelse av antall barn")
    void morSøker2Barn1Registrert() {
        var testscenario = opprettTestscenario("50");

        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        var søknad = lagSøknadForeldrepenger(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(2, fødselsdato));
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var inntektsmeldinger = lagInntektsmelding(
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

        var fodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(2, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(fodselBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker uregistrert fødsel før det har gått 1 uke")
    @Description("Mor søker uregistrert fødsel før det har gått 1 uke - skal sette behandling på vent")
    void morSøkerUregistrertEtterFør2Uker() {
        var testscenario = opprettTestscenario("55");
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = LocalDate.now().minusDays(5);
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var inntektsmeldinger = lagInntektsmelding(
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

        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandling er satt på vent (Har ventet til 2. uke)")
                .isTrue();
        logger.debug("{}", saksbehandler.valgtBehandling.fristBehandlingPaaVent);
        logger.debug("{}", fødselsdato.plusWeeks(2));
        assertThat(saksbehandler.valgtBehandling.fristBehandlingPaaVent)
                .as("Frist behandling på vent")
                .isEqualTo(fødselsdato.plusWeeks(1).plusDays(1));
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
        assertThat(uttakResultatPeriode.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
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
        var perioder = beregningResultatForeldrepenger.getPerioder();
        assertThat(beregningResultatForeldrepenger.isSokerErMor()).isTrue();
        assertThat(perioder.size()).isPositive();
        for (var periode : perioder) {
            assertThat(periode.getDagsats()).isPositive();
            var andeler = periode.getAndeler();
            for (var andel : andeler) {
                var kode = andel.getAktivitetStatus();
                if (kode.equals(AktivitetStatus.ARBEIDSTAKER)) {
                    if (medFullRefusjon) {
                        assertThat(andel.getTilSoker()).isZero();
                        assertThat(andel.getRefusjon()).isPositive();
                    } else {
                        assertThat(andel.getTilSoker()).isPositive();
                        assertThat(andel.getRefusjon()).isZero();
                    }
                } else if (kode.equals(AktivitetStatus.FRILANSER) || kode.equals(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)) {
                    assertThat(andel.getTilSoker()).isPositive();
                    assertThat(andel.getRefusjon()).isZero();
                }
                assertThat(andel.getUttak().isGradering()).isFalse();
                assertThat(andel.getUtbetalingsgrad()).isEqualByComparingTo(BigDecimal.valueOf(100));
            }
        }
    }
}
