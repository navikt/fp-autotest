package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static java.time.LocalDate.now;
import static java.util.Collections.singletonList;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmeldingBuilderMedGradering;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.perioder.GraderingBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsatteVerdier;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettMaanedsinntektUtenInntektsmeldingAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerManueltOpprettetRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.FaktaOmBeregningTilfelle;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagArbeidsforholdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg;

/**
 * Tester i denne klassen vil ikkje kjøres i felles pipeline med mindre dei har Tag "fpsak"
 */

@Execution(ExecutionMode.CONCURRENT)
@Tag("beregning")
@Tag("foreldrepenger")
public class Beregning extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor med ventelønn og vartpenger")
    @Description("Mor med ventelønn og vartpenger. Fører til aksjonspunkt i opptjening som godkjennes. " +
            "Aksjonspunkt i beregning for avklaring om ventelønn og vartpenger skal benyttes i beregning.")
    @Tag("beregning")
    public void mor_med_ventelønn_og_vartpenger() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("150");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();

        Opptjening opptjening = OpptjeningErketyper.medVentelonnVartpengerOpptjening();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medSpesiellOpptjening(opptjening);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        var vurderPerioderOpptjeningBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class)
                .godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        verifiserLikhet(saksbehandler.valgtBehandling.getAksjonspunkter().stream()
                .anyMatch(ap -> ap.erUbekreftet() && ap.getDefinisjon().kode.equals(AksjonspunktKoder.AVKLAR_AKTIVITETER)), true);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getAvklarAktiviteter().getAktiviteterTomDatoMapping().size()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getAvklarAktiviteter().getAktiviteterTomDatoMapping().get(0).getTom()).isEqualTo(fødselsdato.minusWeeks(3));
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getAvklarAktiviteter().getAktiviteterTomDatoMapping().get(0).getAktiviteter().size()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getAvklarAktiviteter().getAktiviteterTomDatoMapping().get(0).getAktiviteter().get(0).getArbeidsforholdType().kode).isEqualTo("VENTELØNN_VARTPENGER");
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getAvklarAktiviteter().getAktiviteterTomDatoMapping().get(0).getAktiviteter().get(0).getSkalBrukes()).isNull();
    }

    @Test
    @DisplayName("Mor med kortvarig arbeidsforhold")
    @Description("Mor med kortvarig arbeidsforhold")
    @Tag("beregning")
    public void vurder_tidsbegrenset_uten_inntektsmelding() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("151");

        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        int inntektPerMåned = 20_000;
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDLEGG_MOTTATT);

        debugLoggBehandling(saksbehandler.valgtBehandling);

        saksbehandler.gjenopptaBehandling();
        var avklarArbeidsforholdBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErRelevant("STATOIL", true);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);
        verifiserLikhet(saksbehandler.valgtBehandling.getAksjonspunkter().stream()
                .anyMatch(ap -> ap.erUbekreftet() &&
                        ap.getDefinisjon().kode.equals(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN)), true);
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning().getFaktaOmBeregningTilfeller()
                .contains(FaktaOmBeregningTilfelle.VURDER_TIDSBEGRENSET_ARBEIDSFORHOLD), true);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getKortvarigeArbeidsforhold().size()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getKortvarigeArbeidsforhold().get(0).getAndelsnr()).isEqualTo(2L);
        assertArbeidsforhold(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getKortvarigeArbeidsforhold().get(0).getArbeidsforhold(), "STATOIL", "892850372");

        assertMottarYtelse(2L, "STATOIL", "892850372");
    }

    @Test
    @DisplayName("Mor med kortvarig arbeidsforhold med inntektsmelding")
    @Description("Mor med kortvarig arbeidsforhold med inntektsmelding")
    @Tag("beregning")
    public void vurder_tidsbegrenset_med_inntektsmelding() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("151");

        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        String orgNr2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr();
        int inntektPerMåned = 20_000;
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr);
        InntektsmeldingBuilder inntektsmeldingBuilder2 = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr2);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder2, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDLEGG_MOTTATT);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        verifiserLikhet(aksjonspunkt.erUbekreftet(), true);
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning().getFaktaOmBeregningTilfeller()
                .contains(FaktaOmBeregningTilfelle.VURDER_TIDSBEGRENSET_ARBEIDSFORHOLD), true);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getKortvarigeArbeidsforhold().size()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getKortvarigeArbeidsforhold().get(0).getAndelsnr()).isEqualTo(2L);
        assertArbeidsforhold(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getKortvarigeArbeidsforhold().get(0).getArbeidsforhold(), "STATOIL", "892850372");
    }

    @Test
    @DisplayName("Tilkommer nytt arbeidsforhold med refusjonskrav på STP")
    @Description("Tilkommer nytt arbeidsforhold med refusjonskrav på STP")
    @Tag("beregning")
    public void morSøkerFødselMedToArbeidsforholdDerDetEneTilkommerPåSTP() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("161");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);


        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();

        InntektsmeldingBuilder im1 = lagInntektsmelding(20000, fnr, fpStartdato, "910909088")
                .medArbeidsforholdId("ARB001-001");
        InntektsmeldingBuilder im2 =   lagInntektsmelding(10000, fnr, fpStartdato, "973861778")
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(1000))
                .medArbeidsforholdId("ARB001-002")
                .medGradering(BigDecimal.valueOf(50), fpStartdato, fpStartdato.plusWeeks(3));

        List<InntektsmeldingBuilder> inntektsmeldinger = List.of(im1, im2);

        fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
    }

    @Test
    @DisplayName("To arbeidsforhold i samme organisasjon.")
    @Description("To arbeidsforhold i samme organisajon. Inntektsmelding med arbeidsforholdId. Setter det ene arbeidsforholdet til inaktivt.")
    @Tag("beregning")
    public void mor_søker_fødsel_med_to_arbeidsforhold_i_samme_organisasjon_inntektsmelding_for_en_med_id_velger_og_sette_det_andre_til_inaktivt() {

        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("57");

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        List<Integer> inntekter = sorterteInntektsbeløp(testscenario);
        String orgnr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(inntekter.get(0), fnr,
                fpStartdato, orgnr)
                .medArbeidsforholdId("ARB001-001");

        fordel.sendInnInntektsmelding(inntektsmelding, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.gjenopptaBehandling();

        var avklarArbeidsforholdBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErOverstyrt("BEDRIFT AS", now().minusYears(3), fpStartdato.minusDays(10));

        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
    }

    @Test
    @Disabled
    @DisplayName("Endret beregningsgrunnlag med kortvarig arbeidsforhold")
    @Description("Endret beregningsgrunnlag med kortvarig arbeidsforhold. Setter at arbeidsforhold er kortvarig i fakta om beregning." +
            "Aksjonspunkt for omfordeling av beregningsgrunnlag.")
    @Tag("beregning")
    public void endret_beregningsgrunnlag_med_kortvarig() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("151");

        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        String orgNr2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr();
        int inntektPerMåned = 20_000;
        BigDecimal refusjon = BigDecimal.valueOf(50_000);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr);
        InntektsmeldingBuilder inntektsmeldingBuilder2 = lagInntektsmeldingBuilderMedGradering(inntektPerMåned, fnr, fpStartdato,
                orgNr2,50, fpStartdato, fpStartdato.plusMonths(2));
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder2, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDLEGG_MOTTATT);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        // TODO: Her hjelper det å vente av en eller anne grunn.
        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        verifiserLikhet(aksjonspunkt.erUbekreftet(), true);
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning().getFaktaOmBeregningTilfeller()
                .contains(FaktaOmBeregningTilfelle.VURDER_TIDSBEGRENSET_ARBEIDSFORHOLD), true);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getKortvarigeArbeidsforhold().size()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getKortvarigeArbeidsforhold().get(0).getAndelsnr()).isEqualTo(2L);
        assertArbeidsforhold(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getKortvarigeArbeidsforhold().get(0).getArbeidsforhold(), "STATOIL", "892850372");


        var vurderFaktaOmBeregningBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.VURDER_TIDSBEGRENSET_ARBEIDSFORHOLD.kode)
                .leggTilVurderTidsbegrenset(true)
                .setBegrunnelse("Endret av Autotest");
        // Feiler her.
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.FORDEL_BEREGNINGSGRUNNLAG);

        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getEndringBeregningsgrunnlag().getEndredeArbeidsforhold().size()).isEqualTo(1);
        assertArbeidsforhold(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getEndringBeregningsgrunnlag().getEndredeArbeidsforhold().get(0), "STATOIL", "892850372");
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getEndringBeregningsgrunnlag().getEndredeArbeidsforhold().get(0).getPerioderMedGraderingEllerRefusjon().size()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getEndringBeregningsgrunnlag().getEndredeArbeidsforhold().get(0)
                .getPerioderMedGraderingEllerRefusjon().get(0).getFom()).isEqualTo(fpStartdato);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getEndringBeregningsgrunnlag().getEndredeArbeidsforhold().get(0)
                .getPerioderMedGraderingEllerRefusjon().get(0).getTom()).isEqualTo(fpStartdato.plusMonths(2));

        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto()).isEqualTo(1);

    }

    @Test
    @DisplayName("Vurder besteberegning: Mor med arbeidsforhold og dagpenger i opptjeningsperioden")
    @Description("Vurder besteberegning: Mor med arbeidsforhold og dagpenger i opptjeningsperioden.")
    public void vurder_besteberegning() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("156");

        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        int inntektPerMåned = 30_000;
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDLEGG_MOTTATT);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        verifiserLikhet(aksjonspunkt.erUbekreftet(), true);
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning().getFaktaOmBeregningTilfeller()
                .contains(FaktaOmBeregningTilfelle.VURDER_BESTEBEREGNING), true);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getSkalHaBesteberegning()).isNull();
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().size()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getAndelsnr()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getFastsattBelopPrMnd()).isNull();
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getAktivitetStatus().kode).isEqualTo("AT");
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getInntektskategori().kode).isEqualTo("ARBEIDSTAKER");
        assertArbeidsforhold(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getArbeidsforhold(), "BEDRIFT AS", "910909088");
    }


    @Test
    @DisplayName("Vurder besteberegning: Mor med arbeidsforhold og dagpenger i opptjeningsperioden. Uten inntektsmelding.")
    @Description("Vurder besteberegning: Mor med arbeidsforhold og dagpenger i opptjeningsperioden. Uten inntektsmelding")
    public void vurder_besteberegning_vurder_mottar_ytelse() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("156");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.gjenopptaBehandling();
        var avklarArbeidsforholdBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErRelevant("BEDRIFT AS", true);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);
        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        verifiserLikhet(aksjonspunkt.erUbekreftet(), true);
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning().getFaktaOmBeregningTilfeller()
                .contains(FaktaOmBeregningTilfelle.VURDER_BESTEBEREGNING), true);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getSkalHaBesteberegning()).isNull();
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().size()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getAndelsnr()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getFastsattBelopPrMnd()).isNull();
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getAktivitetStatus().kode).isEqualTo("AT");
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getInntektskategori().kode).isEqualTo("ARBEIDSTAKER");
        assertArbeidsforhold(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getArbeidsforhold(), "BEDRIFT AS", "910909088");
    }

    @Test
    @DisplayName("Vurder besteberegning: Mor med arbeidsforhold og dagpenger i opptjeningsperioden. Uten inntektsmelding, med lønnsendring")
    @Description("Vurder besteberegning: Mor med arbeidsforhold og dagpenger i opptjeningsperioden. Uten inntektsmelding, med lønnsendring")
    public void vurder_besteberegning_vurder_mottar_ytelse_vurder_lonnsendring() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("159");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.gjenopptaBehandling();
        var avklarArbeidsforholdBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErRelevant("BEDRIFT AS", true);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);
        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        verifiserLikhet(aksjonspunkt, true);
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning().getFaktaOmBeregningTilfeller()
                .contains(FaktaOmBeregningTilfelle.VURDER_BESTEBEREGNING), true);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getSkalHaBesteberegning()).isNull();
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().size()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getAndelsnr()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getFastsattBelopPrMnd()).isNull();
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getAktivitetStatus().kode).isEqualTo("AT");
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getInntektskategori().kode).isEqualTo("ARBEIDSTAKER");
        assertArbeidsforhold(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderBesteberegning().getAndeler().get(0).getArbeidsforhold(), "BEDRIFT AS", "910909088");
    }

    @Test
    @DisplayName("Uten inntektsmelding, med lønnsendring")
    @Description("Uten inntektsmelding, med lønnsendring")
    public void vurder_mottar_ytelse_vurder_lonnsendring() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("161");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.gjenopptaBehandling();
        var avklarArbeidsforholdBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErRelevant("BEDRIFT AS", true);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);
        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        verifiserLikhet(aksjonspunkt.erUbekreftet(), true);
    }

    @Test
    @DisplayName("ATFL i samme org med lønnsendring")
    @Description("ATFL i samme org med lønnsendring")
    public void ATFL_samme_org_med_lønnendring_uten_inntektsmelding() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("163");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        Opptjening opptjening = OpptjeningErketyper.medFrilansOpptjening();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medSpesiellOpptjening(opptjening);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.gjenopptaBehandling();
        var avklarArbeidsforholdBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErRelevant("BEDRIFT AS", true);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        verifiserLikhet(aksjonspunkt.erUbekreftet(), true);
}

    @Test
    @DisplayName("Vurder besteberegning: Mor med arbeidsforhold og dagpenger på skjæringstidspunktet")
    @Description("Vurder besteberegning: Mor med arbeidsforhold og dagpenger på skjæringstidspunktet.")
    public void vurder_besteberegning_dagpenger_på_skjæringstidspunktet() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("158");

        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        int inntektPerMåned = 30_000;
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDLEGG_MOTTATT);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        verifiserLikhet(aksjonspunkt.erUbekreftet(), true);
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning().getFaktaOmBeregningTilfeller()
                .contains(FaktaOmBeregningTilfelle.VURDER_BESTEBEREGNING), true);
    }


    @Test
    @Disabled
    @DisplayName("Mor med arbeidsforhold uten inntektsmelding som mottar ytelse")
    @Description("Mor med arbeidsforhold uten inntektsmelding som mottar ytelse. Produksjonshendelse som feilet i frontend.")
    @Tag("beregning")
    public void vurder_mottar_ytelse() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("155");

        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate termindato = now().minusMonths(1).plusWeeks(3);
        LocalDate startDatoForeldrepenger = termindato.minusWeeks(3);
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        int inntektPerMåned = 30_000;
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(termindato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE);
        saksbehandler.gjenopptaBehandling();

        var avklarArbeidsforholdBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErRelevant("BEDRIFT AS", true);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);
        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        verifiserLikhet(aksjonspunkt.erUbekreftet(), true);
        assertMottarYtelse(1L, "BEDRIFT AS", "910909088");

        var vurderFaktaOmBeregningBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilVurdertLønnsendring(false)
                .leggTilMottarYtelse(singletonList(new ArbeidstakerandelUtenIMMottarYtelse(1, true)))
                .leggTilMaanedsinntektUtenInntektsmelding(singletonList(new FastsettMaanedsinntektUtenInntektsmeldingAndel(1, 30000)));
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);

        saksbehandler.opprettBehandlingRevurdering("RE-HENDELSE-FØDSEL");

        saksbehandler.velgRevurderingBehandling();

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);

        assertMottarYtelse(1L, "BEDRIFT AS", "910909088");

        saksbehandler.settBehandlingPåVent(now().plusWeeks(1), "VENT_OPDT_INNTEKTSMELDING");

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_MANUELT_SATT_PÅ_VENT);

        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, startDatoForeldrepenger, orgNr)
                .medUtsettelse(SøknadUtsettelseÅrsak.ARBEID.getKode(), startDatoForeldrepenger, startDatoForeldrepenger.plusMonths(1));
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);

        saksbehandler.velgRevurderingBehandling();
        assertThat(saksbehandler.valgtBehandling.erSattPåVent()).isFalse();

        InntektsmeldingBuilder inntektsmeldingBuilder2 = lagInntektsmelding(inntektPerMåned, fnr, startDatoForeldrepenger,
                orgNr);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder2, testscenario, saksnummer);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerManueltOpprettetRevurdering.class);

        ForeslåVedtakManueltBekreftelse foreslåVedtakManueltBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(foreslåVedtakManueltBekreftelse);

        saksbehandler.velgRevurderingBehandling();
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()).isNull();
    }

    @Test
    @DisplayName("SN og Arbeidsforhold tilkommer etter stp")
    @Description("Mor er SN ny i arbeidslivet og har arbeidsforhold som tilkommer etter stp")
    @Tag("beregning")
    public void SN_ny_arbeidslivet_med_arbeidsforhold_tilkommer_etter_stp() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("164");

        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        List<Arbeidsforhold> arbeidsforhold = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold();
        Arbeidsforhold tilkommet = arbeidsforhold.get(0);
        String orgNr = tilkommet.getArbeidsgiverOrgnr();
        int inntektPerMåned = 30_000;
        BigDecimal refusjon = BigDecimal.valueOf(10_000);

        Opptjening opptjening = OpptjeningErketyper.medEgenNaeringOpptjening(
                true, BigInteger.valueOf(550000), false);
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medSpesiellOpptjening(opptjening);

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDLEGG_MOTTATT);
        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_FOR_SN_NY_I_ARBEIDSLIVET);

    }

    @Test
    @DisplayName("SN med gradering og Arbeidsforhold med refusjon over 6G")
    @Description("Mor er SN som søker gradering og har arbeidsgiver som søker refusjon over 6G")
    @Tag("beregning")
    public void SN_med_gradering_og_arbeidsforhold_som_søker_refusjon_over_6G() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("165");

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
        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)));
        perioder.add(uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        perioder.add(new GraderingBuilder(FELLESPERIODE.getKode(), fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(10))
                .medGraderingSN(50)
                .build());
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medSpesiellOpptjening(opptjening)
                .medFordeling(fordeling);

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr).medRefusjonsBelopPerMnd(refusjon);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDLEGG_MOTTATT);
        debugLoggBehandling(saksbehandler.valgtBehandling);
    }


    @Test
    @DisplayName("SN med gradering og Arbeidsforhold med refusjon under 6G og beregningsgrunnlag over 6G")
    @Description("Mor er SN som søker gradering og har arbeidsgiver som søker refusjon under 6G og har beregningsgrunnlag 6G.")
    @Tag("beregning")
    public void SN_med_gradering_og_arbeidsforhold_som_søker_refusjon_under_6G_med_bg_over_6G() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("165");

        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        List<Arbeidsforhold> arbeidsforhold = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold();
        Arbeidsforhold tilkommet = arbeidsforhold.get(0);
        String orgNr = tilkommet.getArbeidsgiverOrgnr();
        int inntektPerMåned = 60_000;
        BigDecimal refusjon = BigDecimal.valueOf(30_000);

        Opptjening opptjening = OpptjeningErketyper.medEgenNaeringOpptjening(
                false, BigInteger.valueOf(30_000), false);
        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();;
        perioder.add(uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        perioder.add(new GraderingBuilder(FELLESPERIODE.getKode(), fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(10))
                .medGraderingSN(50)
                .build());

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medSpesiellOpptjening(opptjening)
                .medFordeling(fordeling);

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr).medRefusjonsBelopPerMnd(refusjon);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDLEGG_MOTTATT);
        debugLoggBehandling(saksbehandler.valgtBehandling);
    }

    @Test
    @DisplayName("Arbeidsforhold tilkommer etter stp")
    @Description("Arbeidsforhold tilkommer etter stp")
    @Tag("beregning")
    public void arbeidsforhold_tilkommer_etter_stp() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("157");

        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        List<Arbeidsforhold> arbeidsforhold = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold();
        Arbeidsforhold tilkommet = arbeidsforhold.get(1);
        LocalDate tilkommetDato = tilkommet.getAnsettelsesperiodeFom();
        String orgNr = tilkommet.getArbeidsgiverOrgnr();
        String orgNr2 = arbeidsforhold.get(2).getArbeidsgiverOrgnr();
        int inntektPerMåned = 30_000;
        BigDecimal refusjon = BigDecimal.valueOf(10_000);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent,SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr);
        InntektsmeldingBuilder inntektsmeldingBuilder2 = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr2);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder2, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDLEGG_MOTTATT);

        debugLoggBehandling(saksbehandler.valgtBehandling);
        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        verifiserLikhet(aksjonspunkt.erUbekreftet(), true);


        assertEndretArbeidsforhold(tilkommetDato);

        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().antallBeregningsgrunnlagPeriodeDto()).isEqualTo(2);

        BeregningsgrunnlagPeriodeDto manuellPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(1);
        BeregningsgrunnlagPrStatusOgAndelDto tilkommetAndel = manuellPeriode.getBeregningsgrunnlagPrStatusOgAndel().stream()
                .filter(andel -> andel.getArbeidsforhold().getArbeidsgiverId().equals(orgNr)).findFirst().get();
        BeregningsgrunnlagPrStatusOgAndelDto eksisterendeAndel = manuellPeriode.getBeregningsgrunnlagPrStatusOgAndel().stream()
                .filter(andel -> andel.getArbeidsforhold().getArbeidsgiverId().equals(orgNr2)).findFirst().get();
        VurderFaktaOmBeregningBekreftelse bekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        int fastsattBeløp = 15_000;
        FastsatteVerdier fastsatteVerdier = new FastsatteVerdier(10_000, fastsattBeløp,
                saksbehandler.kodeverk.Inntektskategori.getKode("ARBEIDSTAKER"));
        FastsatteVerdier fastsatteVerdier2 = new FastsatteVerdier(0, fastsattBeløp,
                saksbehandler.kodeverk.Inntektskategori.getKode("ARBEIDSTAKER"));
        bekreftelse
//                .leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.FASTSETT_ENDRET_BEREGNINGSGRUNNLAG.kode)
                .leggTilAndelerEndretBg(manuellPeriode, tilkommetAndel, fastsatteVerdier)
                .leggTilAndelerEndretBg(manuellPeriode, eksisterendeAndel, fastsatteVerdier2);
        saksbehandler.bekreftAksjonspunkt(bekreftelse);

        assertEndretArbeidsforhold(tilkommetDato);

        BeregningsgrunnlagPeriodeDto behandletPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(1);
        tilkommetAndel = behandletPeriode.getBeregningsgrunnlagPrStatusOgAndel().stream()
                .filter(andel -> andel.getArbeidsforhold().getArbeidsgiverId().equals(orgNr)).findFirst().get();
        eksisterendeAndel = behandletPeriode.getBeregningsgrunnlagPrStatusOgAndel().stream()
                .filter(andel -> andel.getArbeidsforhold().getArbeidsgiverId().equals(orgNr2)).findFirst().get();
        assertThat(tilkommetAndel.getBeregnetPrAar()).isEqualTo((double)12*fastsattBeløp);
        assertThat(eksisterendeAndel.getBeregnetPrAar()).isEqualTo((double)12*fastsattBeløp);
    }

    private void assertEndretArbeidsforhold(LocalDate tilkommetDato) {
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning().getFaktaOmBeregningTilfeller()
                .contains(FaktaOmBeregningTilfelle.FASTSETT_ENDRET_BEREGNINGSGRUNNLAG), true);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getEndringBeregningsgrunnlag().getEndredeArbeidsforhold().size()).isEqualTo(1);
        assertArbeidsforhold(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getEndringBeregningsgrunnlag().getEndredeArbeidsforhold().get(0), "BEDRIFT AS", "910909088");
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getEndringBeregningsgrunnlag().getEndredeArbeidsforhold().get(0).getPerioderMedGraderingEllerRefusjon().size()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getEndringBeregningsgrunnlag().getEndredeArbeidsforhold().get(0)
                .getPerioderMedGraderingEllerRefusjon().get(0).getFom()).isEqualTo(tilkommetDato);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getEndringBeregningsgrunnlag().getEndredeArbeidsforhold().get(0)
                .getPerioderMedGraderingEllerRefusjon().get(0).getTom()).isNull();
    }


    private void assertMottarYtelse(long l, String s, String s2) {
        verifiserLikhet(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning().getFaktaOmBeregningTilfeller()
                .contains(FaktaOmBeregningTilfelle.VURDER_MOTTAR_YTELSE), true);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderMottarYtelse().isErFrilans()).isEqualTo(false);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderMottarYtelse().getArbeidstakerAndelerUtenIM().size()).isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderMottarYtelse().getArbeidstakerAndelerUtenIM().get(0)
                .getAndelsnr()).isEqualTo(l);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getVurderMottarYtelse().getArbeidstakerAndelerUtenIM().get(0)
                .getInntektPrMnd()).isEqualTo(30000);
        assertArbeidsforhold(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                        .getVurderMottarYtelse().getArbeidstakerAndelerUtenIM().get(0).getArbeidsforhold(),
                s, s2);
    }


    private void assertArbeidsforhold(BeregningsgrunnlagArbeidsforholdDto arbeidsforhold, String navn, String arbeidsgiverId) {
        assertThat(arbeidsforhold.getArbeidsgiverNavn()).isEqualTo(navn);
        assertThat(arbeidsforhold.getArbeidsgiverId()).isEqualTo(arbeidsgiverId);
    }

}
