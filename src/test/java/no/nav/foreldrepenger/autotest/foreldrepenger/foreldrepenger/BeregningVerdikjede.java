package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.graderingsperiodeSN;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.MedlemskapManuellVurderingType;
import no.nav.foreldrepenger.autotest.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AvklarAktiviteterBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FordelBeregningsgrunnlagBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerHarGyldigPeriodeBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarLopendeVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.inntektsmelding.xml.kodeliste._20180702.NaturalytelseKodeliste;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
public class BeregningVerdikjede extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold og tre bortfalte naturalytelser på forskjellige tidspunkt")
    public void morSøkerFødselMedEttArbeidsforhold() {
        TestscenarioDto testscenario = opprettTestscenario("500");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        int inntektPerMåned = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp();
        String fnr = testscenario.personopplysninger().søkerIdent();
        String orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr);

        // Legger til naturalytelser som opphører
        BortfaltnaturalytelseHelper førsteYtelse = lagBortfaltNaturalytelse(685, fpStartdato.plusDays(10));
        BortfaltnaturalytelseHelper andreYtelse = lagBortfaltNaturalytelse(998, fpStartdato.plusDays(40));
        BortfaltnaturalytelseHelper tredjeYtelse = lagBortfaltNaturalytelse(754, fpStartdato.plusDays(60));

        inntektsmeldingBuilder
                .medOpphoerAvNaturalytelseListe(førsteYtelse.beløpPrMnd, førsteYtelse.fom,
                        NaturalytelseKodeliste.ELEKTRONISK_KOMMUNIKASJON)
                .medOpphoerAvNaturalytelseListe(andreYtelse.beløpPrMnd, andreYtelse.fom,
                        NaturalytelseKodeliste.FRI_TRANSPORT)
                .medOpphoerAvNaturalytelseListe(tredjeYtelse.beløpPrMnd, tredjeYtelse.fom,
                        NaturalytelseKodeliste.KOST_DAGER);

        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        debugLoggBehandling(saksbehandler.valgtBehandling);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);
        verifiserLikhet(saksbehandler.getBehandlingsstatus(), "AVSLU");

        // Verifiser at beregning er gjort riktig
        List<LocalDate> startdatoer = Arrays.asList(fpStartdato, førsteYtelse.fom, andreYtelse.fom, tredjeYtelse.fom);
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserBGPerioder(startdatoer, beregningsgrunnlag);
        int inntektPrÅr = inntektPerMåned * 12;
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr, 0, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1),
                lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr, førsteYtelse.beløpPrÅr.doubleValue(), 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(2), lagBGAndel(orgNr, inntektPrÅr,
                inntektPrÅr, førsteYtelse.beløpPrÅr.add(andreYtelse.beløpPrÅr).doubleValue(), 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(3),
                lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr,
                        førsteYtelse.beløpPrÅr.add(andreYtelse.beløpPrÅr).add(tredjeYtelse.beløpPrÅr).doubleValue(),
                        0));
    }

    @Test
    @DisplayName("Mor søker fødsel med full AAP og et arbeidsforhold som tilkommer etter skjæringstidspunktet")
    public void morSøkerFødselMedFullAAPOgArbeidsforhold() {
        // LAG SØKNAD OG SEND INN INNTEKTSMELDING //
        TestscenarioDto testscenario = opprettTestscenario("166");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        int inntektPerMåned = 30_000;
        String fnr = testscenario.personopplysninger().søkerIdent();
        String orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        LocalDate fpStartdato = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .ansettelsesperiodeFom();
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato, orgNr)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektPerMåned));
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.hentFagsak(saksnummer);

        // FORDEL BEREGNINGSGRUNNLAG //
        BeregningsgrunnlagPrStatusOgAndelDto aapAndel = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0)
                .getBeregningsgrunnlagPrStatusOgAndel()
                .stream().filter(a -> a.getAndelsnr() == 1)
                .findFirst().orElseThrow();
        double totaltBg = aapAndel.getBeregnetPrAar();

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndelMedFordelt(aapAndel.getAktivitetStatus().kode, totaltBg, totaltBg, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1),
                lagBGAndelMedFordelt(aapAndel.getAktivitetStatus().kode, totaltBg, 0, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1),
                lagBGAndelMedFordelt(orgNr, 0, totaltBg, totaltBg, inntektPerMåned * 12));
    }

    @Test
    @DisplayName("Mor søker fødsel med full AAP og et arbeidsforhold som ikke skal benyttes.")
    public void morSøkerFødselMedFullAAPOgArbeidsforholdSomErAktivtPåStp() {
        // OPPSETT, INNTEKTSMELDING, SØKNAD //
        TestscenarioDto testscenario = opprettTestscenario("167");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        int inntektPerMåned = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp();
        String fnr = testscenario.personopplysninger().søkerIdent();
        String orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,
                orgNr)
                        .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektPerMåned));
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.hentFagsak(saksnummer);

        // AVKLAR AKTIVITETER //
        AvklarAktiviteterBekreftelse avklarAktiviteterBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarAktiviteterBekreftelse.class);
        avklarAktiviteterBekreftelse.setSkalBrukes(false, orgNr);
        saksbehandler.bekreftAksjonspunkt(avklarAktiviteterBekreftelse);

        // FORDEL BEREGNINGSGRUNNLAG //
        BeregningsgrunnlagPrStatusOgAndelDto aapAndel = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0)
                .getBeregningsgrunnlagPrStatusOgAndel()
                .stream().filter(a -> a.getAndelsnr() == 1)
                .findFirst().orElseThrow();
        double totaltBg = aapAndel.getBeregnetPrAar();

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndelMedFordelt(aapAndel.getAktivitetStatus().kode, (int) totaltBg, 0, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndelMedFordelt(orgNr, 0, (int) totaltBg, totaltBg, inntektPerMåned * 12));
    }

    @Test
    @DisplayName("Mor med kun ytelse på skjæringstidspunktet og dagpenger i opptjeningsperioden")
    @Description("Mor med kun ytelse på skjæringstidspunktet og dagpenger i opptjeningsperioden")
    public void kun_ytelse_med_vurdering_av_besteberegning() {
        TestscenarioDto testscenario = opprettTestscenario("172");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);

        // FAKTA OM FØDSEL: Avklar om søker har mottatt støtte
        AvklarLopendeVedtakBekreftelse avklarLopendeVedtakBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarLopendeVedtakBekreftelse.class);
        avklarLopendeVedtakBekreftelse.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(avklarLopendeVedtakBekreftelse);

        // FAKTA OM MEDLEMSKAP
        var avklarBrukerHarGyldigPeriodeBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class)
                .setVurdering(MedlemskapManuellVurderingType.MEDLEM, saksbehandler.valgtBehandling.getMedlem().getMedlemskapPerioder());
        saksbehandler.bekreftAksjonspunkt(avklarBrukerHarGyldigPeriodeBekreftelse);

        // FAKTA OM BEREGNING: Vurder besteberegning og fastsett månedsinntekt fra
        // ytelse
        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .leggTilAndelerYtelse(10000.0, Inntektskategori.ARBEIDSTAKER)
                .settSkalHaBesteberegningForKunYtelse(true)
                .setBegrunnelse("Endret av Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        // FORESLÅ VEDTAK //
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // FATTE VEDTAK //
        beslutter.hentFagsak(saksnummer);
        Aksjonspunkt apLopendeVedtak = beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_OM_SØKER_HAR_MOTTATT_STØTTE);
        Aksjonspunkt apFaktaOmBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(List.of(apLopendeVedtak, apFaktaOmBeregning));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        saksbehandler.ventTilAvsluttetBehandling();
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndelMedBesteberegning("BA", 120_000));
    }

    @Test
    @DisplayName("SN med gradering og Arbeidsforhold med refusjon over 6G")
    @Description("Mor er SN som søker gradering og har arbeidsgiver som søker refusjon over 6G")
    @Tag("beregning")
    public void SN_med_gradering_og_arbeidsforhold_som_søker_refusjon_over_6G() {
        // OPPSETT, INNTEKTSMELDING, SØKNAD //
        TestscenarioDto testscenario = opprettTestscenario("165");
        String fnr = testscenario.personopplysninger().søkerIdent();
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        List<Arbeidsforhold> arbeidsforhold = testscenario.scenariodataDto().arbeidsforholdModell()
                .arbeidsforhold();
        Arbeidsforhold tilkommet = arbeidsforhold.get(0);
        String orgNr = tilkommet.arbeidsgiverOrgnr();
        int inntektPerMåned = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp();
        BigDecimal refusjon = BigDecimal.valueOf(inntektPerMåned);
        Opptjening opptjening = OpptjeningErketyper.medEgenNaeringOpptjening(
                false, BigInteger.valueOf(30_000), false);

        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        LocalDate graderingFom = fødselsdato.plusWeeks(6);
        perioder.add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)));
        perioder.add(uttaksperiode(MØDREKVOTE, fødselsdato, graderingFom.minusDays(1)));
        perioder.add(graderingsperiodeSN(FELLESPERIODE, graderingFom, fødselsdato.plusWeeks(10), 50));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medSpesiellOpptjening(opptjening);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato, orgNr)
                .medRefusjonsBelopPerMnd(refusjon);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkinnslagType.VEDLEGG_MOTTATT);
        debugLoggBehandling(saksbehandler.valgtBehandling);

        // FORDEL BEREGNINGSGRUNNLAG //
        FordelBeregningsgrunnlagBekreftelse fordelBeregningsgrunnlagBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FordelBeregningsgrunnlagBekreftelse.class);
        fordelBeregningsgrunnlagBekreftelse
                .settFastsattBeløpOgInntektskategoriMedRefusjon(graderingFom, 500_000, 500_000,
                        new Kode("ARBEIDSTAKER"), 1)
                .settFastsattBeløpOgInntektskategori(graderingFom, 235_138, new Kode("SELVSTENDIG_NÆRINGSDRIVENDE"), 2);
        saksbehandler.bekreftAksjonspunkt(fordelBeregningsgrunnlagBekreftelse);

        // FORESLÅ VEDTAK //
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // FATTE VEDTAK //
        beslutter.hentFagsak(saksnummer);
        Aksjonspunkt apFordelBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.FORDEL_BEREGNINGSGRUNNLAG);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(List.of(apFordelBeregning));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        saksbehandler.ventTilAvsluttetBehandling();
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndel(orgNr, 720_000, 720_000, 0, 720_000));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndelForAktivitetStatus("SN", 37580.83, 37580.83));

        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1),
                lagBGAndelMedFordelt(orgNr, 720_000, 500_000, 500_000, 500_000));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1),
                lagBGAndelMedFordelt("SN", 37580.83, 235_138, 235_138));

        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(2),
                lagBGAndel(orgNr, 720_000, 720_000, 0, 720_000));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(2),
                lagBGAndelForAktivitetStatus("SN", 37580.83, 37580.83));
    }

    @Test
    @DisplayName("Mor med for sent refusjonskrav.")
    public void morFødselForSentRefusjonskrav() {
        // OPPSETT, INNTEKTSMELDING, SØKNAD //
        TestscenarioDto testscenario = opprettTestscenario("84");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(4);
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        int inntektPerMåned = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0)
                .beløp()/2;
        String fnr = testscenario.personopplysninger().søkerIdent();
        String orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,orgNr)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektPerMåned));
        InntektsmeldingBuilder inntektsmeldingBuilder2 = lagInntektsmelding(inntektPerMåned, fnr, fpStartdato,orgNr)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(29_000));
        fordel.sendInnInntektsmeldinger(List.of(inntektsmeldingBuilder, inntektsmeldingBuilder2), testscenario, saksnummer);
        saksbehandler.hentFagsak(saksnummer);

        // FAKTA OM FØDSEL
        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonForeligger(1, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        // FAKTA OM BEREGNING: Vurder gyldighet for refusjonskrav som har kommet for
        // sent
        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .leggTilRefusjonGyldighetVurdering(orgNr, true)
                .setBegrunnelse("Endret av Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        VurderBeregnetInntektsAvvikBekreftelse vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse.leggTilInntekt(360_000, 1);
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        VurderSoknadsfristForeldrepengerBekreftelse vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        // FORESLÅ VEDTAK //
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // FATTE VEDTAK //
        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(List.of(
                beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN),
                beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS),
                beslutter.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRIST_FORELDREPENGER),
                beslutter.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL)));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0),
                lagBGAndel(orgNr, 360_000, 360_000, 0, 348_000));
    }

    @Test
    @DisplayName("ATFL i samme org med lønnsendring")
    @Description("ATFL i samme org med lønnsendring")
    public void ATFL_samme_org_med_lønnendring_uten_inntektsmelding() {
        // OPPSETT, INNTEKTSMELDING, SØKNAD //
        TestscenarioDto testscenario = opprettTestscenario("163");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        Opptjening opptjening = OpptjeningErketyper.medFrilansOpptjening();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medSpesiellOpptjening(opptjening);

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        saksbehandler.hentFagsak(saksnummer);
        debugLoggBehandling(saksbehandler.valgtBehandling);

        // Gjenoppta autopunkter
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE);
        saksbehandler.gjenopptaBehandling();

        // FAKTA OM ARBEIDSFORHOLD
        AvklarArbeidsforholdBekreftelse avklarArbeidsforholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        avklarArbeidsforholdBekreftelse.bekreftArbeidsforholdErAktivt("910909088", true);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        // FAKTA OM BEREGNING
        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        verifiserLikhet(aksjonspunkt.erUbekreftet(), true);
    }

    @Test
    @DisplayName("Uten inntektsmelding, med lønnsendring")
    @Description("Uten inntektsmelding, med lønnsendring")
    public void vurder_mottar_ytelse_vurder_lonnsendring() {
        TestscenarioDto testscenario = opprettTestscenario("161");

        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        saksbehandler.hentFagsak(saksnummer);

        // Gjenoppta autopunkter
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE);
        saksbehandler.gjenopptaBehandling();

        AvklarArbeidsforholdBekreftelse avklarArbeidsforholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        avklarArbeidsforholdBekreftelse.bekreftArbeidsforholdErAktivt("910909088", true);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        var aksjonspunkt = saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        verifiserLikhet(aksjonspunkt.erUbekreftet(), true);
    }

    @Test
    @DisplayName("To arbeidsforhold samme org.")
    public void toArbeidsforholdSammeOrgEttStarterEtterStp() {
        // OPPSETT, INNTEKTSMELDING, SØKNAD //
        TestscenarioDto testscenario = opprettTestscenario("190");
        String fnr = testscenario.personopplysninger().søkerIdent();
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusDays(2);
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        int inntektPerMåned1 = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp();
        String orgNr1 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr();
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmelding(inntektPerMåned1, fnr, fpStartdato, orgNr1)
                .medArbeidsforholdId("ARB001-004")
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektPerMåned1));
        int inntektPerMåned2 = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(1).beløp();
        String orgNr2 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(3).arbeidsgiverOrgnr();
        InntektsmeldingBuilder inntektsmeldingBuilder2 = lagInntektsmelding(inntektPerMåned2, fnr, fpStartdato, orgNr2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektPerMåned2));
        fordel.sendInnInntektsmeldinger(List.of(inntektsmeldingBuilder, inntektsmeldingBuilder2) , testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);

    }

    // @Test for å kunne teste automatisk besteberegning
    public void skal_teste_automatisk_besteberegning() {
        TestscenarioDto testscenario = opprettTestscenario("173");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);

        // FORESLÅ VEDTAK //
        saksbehandler.ventTilAvsluttetBehandling();
    }


    private void verifiserAndelerIPeriode(BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode,
            BGAndelHelper BGAndelHelper) {
        assertThat(beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndel().stream()
                .noneMatch(a -> matchAndel(BGAndelHelper, a)))
                .isFalse();
        beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndel().forEach(andel -> {
            if (matchAndel(BGAndelHelper, andel)) {
                assertAndeler(andel, BGAndelHelper);
            }
        });
    }

    private boolean matchAndel(BGAndelHelper BGAndelHelper, BeregningsgrunnlagPrStatusOgAndelDto andel) {
        return andelTilhørerArbeidsgiverMedId(BGAndelHelper, andel)
                || andelTilhørerAktivitetMedStatus(BGAndelHelper, andel);
    }

    private boolean andelTilhørerAktivitetMedStatus(BGAndelHelper bgAndelHelper,
            BeregningsgrunnlagPrStatusOgAndelDto andel) {
        return andel.getAktivitetStatus().kode.equals(bgAndelHelper.aktivitetstatus);
    }

    private void assertAndeler(BeregningsgrunnlagPrStatusOgAndelDto andel, BGAndelHelper BGAndelHelper) {
        assertThat(andel.getBruttoPrAar()).isEqualTo(BGAndelHelper.bruttoPrÅr);
        assertThat(andel.getBeregnetPrAar()).isEqualTo(BGAndelHelper.beregnetPrÅr);
        assertThat(andel.getBortfaltNaturalytelse()).isEqualTo(BGAndelHelper.naturalytelseBortfaltPrÅr);
        assertThat(andel.getFordeltPrAar()).isEqualTo(BGAndelHelper.fordeltPrÅr);
        if (andel.getArbeidsforhold() != null) {
            assertThat(andel.getArbeidsforhold().getRefusjonPrAar()).isEqualTo(BGAndelHelper.refusjonPrÅr);
        }
    }

    private boolean andelTilhørerArbeidsgiverMedId(BGAndelHelper BGAndelHelper,
            BeregningsgrunnlagPrStatusOgAndelDto andel) {
        return (andel.getArbeidsforhold() != null)
                && Objects.equals(andel.getArbeidsforhold().getArbeidsgiverId(), BGAndelHelper.arbeidsgiverId);
    }

    private BGAndelHelper lagBGAndel(String orgNr, int beregnetPrÅr, int bruttoPrÅr, double bortfaltNaturalytelseBeløp,
            double refusjonskravPrÅr) {
        BGAndelHelper andel = new BGAndelHelper();
        andel.arbeidsgiverId = orgNr;
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = bruttoPrÅr;
        andel.naturalytelseBortfaltPrÅr = bortfaltNaturalytelseBeløp;
        andel.refusjonPrÅr = refusjonskravPrÅr;
        return andel;
    }

    private BGAndelHelper lagBGAndelForAktivitetStatus(String aktivitetStatus, double beregnetPrÅr, double bruttoPrÅr) {
        BGAndelHelper andel = new BGAndelHelper();
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = bruttoPrÅr;
        andel.aktivitetstatus = aktivitetStatus;
        return andel;
    }

    private BGAndelHelper lagBGAndelMedFordelt(String orgNr, double beregnetPrÅr, double bruttoPrÅr, double fordeltPrÅr,
            double refusjonPrÅr) {
        BGAndelHelper andel = new BGAndelHelper();
        andel.arbeidsgiverId = orgNr;
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = bruttoPrÅr;
        andel.fordeltPrÅr = fordeltPrÅr;
        andel.refusjonPrÅr = refusjonPrÅr;
        return andel;
    }

    private BGAndelHelper lagBGAndelMedFordelt(String aktivitetstatus, double beregnetPrÅr, double bruttoPrÅr,
            double fordeltPrÅr) {
        BGAndelHelper andel = new BGAndelHelper();
        andel.aktivitetstatus = aktivitetstatus;
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = bruttoPrÅr;
        andel.fordeltPrÅr = fordeltPrÅr;
        return andel;
    }

    private BGAndelHelper lagBGAndelMedBesteberegning(String aktivitetstatus, int beregnetPrÅr) {
        BGAndelHelper andel = new BGAndelHelper();
        andel.aktivitetstatus = aktivitetstatus;
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = beregnetPrÅr;
        return andel;
    }

    private BortfaltnaturalytelseHelper lagBortfaltNaturalytelse(double mndBeløp, LocalDate fom) {
        BortfaltnaturalytelseHelper nat = new BortfaltnaturalytelseHelper();
        nat.beløpPrMnd = BigDecimal.valueOf(mndBeløp);
        nat.beløpPrÅr = nat.beløpPrMnd.multiply(BigDecimal.valueOf(12));
        nat.fom = fom;
        return nat;

    }

    private void verifiserBGPerioder(List<LocalDate> startdatoer, Beregningsgrunnlag beregningsgrunnlag) {
        for (int i = 0; i < startdatoer.size(); i++) {
            assertThat(startdatoer.get(i))
                    .isEqualTo(beregningsgrunnlag.getBeregningsgrunnlagPeriode(i).getBeregningsgrunnlagPeriodeFom());
        }
    }

    private static class BGAndelHelper {
        public String aktivitetstatus;
        private double bruttoPrÅr;
        private double beregnetPrÅr;
        private double fordeltPrÅr;
        private double refusjonPrÅr;
        private double naturalytelseBortfaltPrÅr;
        private String arbeidsgiverId;
    }

    private static class BortfaltnaturalytelseHelper {
        private BigDecimal beløpPrMnd;
        private BigDecimal beløpPrÅr;
        private LocalDate fom;
    }

}
