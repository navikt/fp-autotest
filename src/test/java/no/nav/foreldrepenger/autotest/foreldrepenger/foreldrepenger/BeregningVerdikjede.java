package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.*;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerHarGyldigPeriodeBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarLopendeVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.GraderingBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.ytelse.ForeldrepengerYtelseBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SoekersRelasjonErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SøknadErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.inntektsmelding.xml.kodeliste._20180702.NaturalytelseKodeliste;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper.STØNADSKONTOTYPE_FELLESPERIODE;
import static no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper.STØNADSKONTOTYPE_MØDREKVOTE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
public class BeregningVerdikjede extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold og tre bortfalte naturalytelser på forskjellige tidspunkt")
    public void morSøkerFødselMedEttArbeidsforhold() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("49");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR,
                1, fødselsdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        int inntektPerMåned = 30_000;
        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();

        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();

        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(inntektPerMåned, fnr, fpStartdato,
                orgNr);


        // Legger til naturalytelser som opphører
        BortfaltnaturalytelseHelper førsteYtelse = lagBortfaltNaturalytelse(685, fpStartdato.plusDays(10));
        BortfaltnaturalytelseHelper andreYtelse = lagBortfaltNaturalytelse(998, fpStartdato.plusDays(40));
        BortfaltnaturalytelseHelper tredjeYtelse = lagBortfaltNaturalytelse(754, fpStartdato.plusDays(60));

        inntektsmeldingBuilder
                .medOpphoerAvNaturalytelseListe(førsteYtelse.beløpPrMnd, førsteYtelse.fom, NaturalytelseKodeliste.ELEKTRONISK_KOMMUNIKASJON)
                .medOpphoerAvNaturalytelseListe(andreYtelse.beløpPrMnd, andreYtelse.fom, NaturalytelseKodeliste.FRI_TRANSPORT)
                .medOpphoerAvNaturalytelseListe(tredjeYtelse.beløpPrMnd, tredjeYtelse.fom, NaturalytelseKodeliste.KOST_DAGER);

        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        debugLoggBehandling(saksbehandler.valgtBehandling);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiserLikhet(saksbehandler.getBehandlingsstatus(), "AVSLU");

        // Verifiser at beregning er gjort riktig
        List<LocalDate> startdatoer = Arrays.asList(fpStartdato, førsteYtelse.fom, andreYtelse.fom, tredjeYtelse.fom);
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserBGPerioder(startdatoer, beregningsgrunnlag);
        int inntektPrÅr = inntektPerMåned * 12;
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr, 0, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1), lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr, førsteYtelse.beløpPrÅr.doubleValue(), 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(2), lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr, førsteYtelse.beløpPrÅr.add(andreYtelse.beløpPrÅr).doubleValue(), 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(3), lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr, førsteYtelse.beløpPrÅr.add(andreYtelse.beløpPrÅr).add(tredjeYtelse.beløpPrÅr).doubleValue(), 0));
    }

    @Test
    @DisplayName("Mor søker fødsel med full AAP og et arbeidsforhold som tilkommer etter skjæringstidspunktet")
    public void morSøkerFødselMedFullAAPOgArbeidsforhold() throws Exception {
        // LAG SØKNAD OG SEND INN INNTEKTSMELDING //
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("166");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        int inntektPerMåned = 30_000;
        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate fpStartdato = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getAnsettelsesperiodeFom();
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(inntektPerMåned, fnr, fpStartdato, orgNr)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektPerMåned));
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        // FORDEL BEREGNINGSGRUNNLAG //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FORDEL_BEREGNINGSGRUNNLAG);
        BeregningsgrunnlagPrStatusOgAndelDto aapAndel = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0)
                .getBeregningsgrunnlagPrStatusOgAndel()
                .stream().filter(a -> a.getAndelsnr() == 1)
                .findFirst().get();
        double totaltBg = aapAndel.getBeregnetPrAar();
        saksbehandler.hentAksjonspunktbekreftelse(FordelBeregningsgrunnlagBekreftelse.class)
                .settFastsattBeløpOgInntektskategori(fpStartdato, 0, new Kode("ARBEIDSAVKLARINGSPENGER"), 1)
                .settFastsattBeløpOgInntektskategori(fpStartdato, (int) totaltBg, new Kode("ARBEIDSAVKLARINGSPENGER"), 2);
        saksbehandler.bekreftAksjonspunktBekreftelse(FordelBeregningsgrunnlagBekreftelse.class);

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndelMedFordelt(aapAndel.getAktivitetStatus().kode, totaltBg, totaltBg, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1), lagBGAndelMedFordelt(aapAndel.getAktivitetStatus().kode, totaltBg, 0, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1), lagBGAndelMedFordelt(orgNr, 0, totaltBg, totaltBg, inntektPerMåned*12));
    }

    @Test
    @DisplayName("Mor søker fødsel med full AAP og et arbeidsforhold som ikke skal benyttes.")
    public void morSøkerFødselMedFullAAPOgArbeidsforholdSomErAktivtPåStp() throws Exception {
        // OPPSETT, INNTEKTSMELDING, SØKNAD //
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("167");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        int inntektPerMåned = 30_000;
        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(inntektPerMåned, fnr, fpStartdato,
                orgNr)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektPerMåned));
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        // AVKLAR AKTIVITETER //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.AVKLAR_AKTIVITETER);
        saksbehandler.hentAksjonspunktbekreftelse(AvklarAktiviteterBekreftelse.class)
                .setSkalBrukes(false, orgNr);
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarAktiviteterBekreftelse.class);

        // FORDEL BEREGNINGSGRUNNLAG //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FORDEL_BEREGNINGSGRUNNLAG);
        BeregningsgrunnlagPrStatusOgAndelDto aapAndel = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0)
                .getBeregningsgrunnlagPrStatusOgAndel()
                .stream().filter(a -> a.getAndelsnr() == 1)
                .findFirst().get();
        double totaltBg = aapAndel.getBeregnetPrAar();
        saksbehandler.hentAksjonspunktbekreftelse(FordelBeregningsgrunnlagBekreftelse.class)
        .settFastsattBeløpOgInntektskategori(fpStartdato, 0, new Kode("ARBEIDSAVKLARINGSPENGER"), 1)
        .settFastsattBeløpOgInntektskategori(fpStartdato, (int) totaltBg, new Kode("ARBEIDSAVKLARINGSPENGER"), 2);
        saksbehandler.bekreftAksjonspunktBekreftelse(FordelBeregningsgrunnlagBekreftelse.class);

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndelMedFordelt(aapAndel.getAktivitetStatus().kode, (int) totaltBg, 0, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndelMedFordelt(orgNr, 0, (int) totaltBg, totaltBg, inntektPerMåned*12));
    }

    @Test
    @Disabled
    @DisplayName("Mor søker fødsel med skjæringstidspunkt på mandag. Arbeidsforhold avslutter fredag og nytt starter på lørdag.")
    public void morSøkerFødselMedStpPåMandagMedTilkommetArbeidIHelga() throws Exception {
        // OPPSETT, INNTEKTSMELDING, SØKNAD //
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("169");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        int inntektPerMåned = 30_000;
        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        String orgNr2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr();

        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(inntektPerMåned, fnr, fpStartdato,
                orgNr);
        InntektsmeldingBuilder inntektsmeldingBuilder2 = lagInntektsmeldingBuilder(inntektPerMåned, fnr, fpStartdato,
                orgNr2).medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektPerMåned));
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder2, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        // FORDEL BEREGNINGSGRUNNLAG //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FORDEL_BEREGNINGSGRUNNLAG);
        BeregningsgrunnlagPrStatusOgAndelDto andel1 = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0)
                .getBeregningsgrunnlagPrStatusOgAndel()
                .stream().filter(a -> a.getAndelsnr() == 1)
                .findFirst().get();
        double totaltBg = andel1.getBeregnetPrAar();
        saksbehandler.hentAksjonspunktbekreftelse(FordelBeregningsgrunnlagBekreftelse.class)
                .settFastsattBeløpOgInntektskategori(fpStartdato, 0, new Kode("ARBEIDSTAKER"), 1)
                .settFastsattBeløpOgInntektskategori(fpStartdato, (int) totaltBg, new Kode("ARBEIDSTAKER"), 2);
        saksbehandler.bekreftAksjonspunktBekreftelse(FordelBeregningsgrunnlagBekreftelse.class);

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndelMedFordelt(orgNr, (int) totaltBg, (int) totaltBg, 0, 0));

        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1), lagBGAndelMedFordelt(orgNr, (int) totaltBg, 0, 0, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1), lagBGAndelMedFordelt(orgNr2, 0, (int) totaltBg, totaltBg, totaltBg));
    }

    @Test
    @DisplayName("Mor søker fødsel med skjæringstidspunkt på mandag. Ett arbeidsforhold avslutter fredag og ett løper videre over skjæringstidspunktet.")
    public void morSøkerFødselMedStpPåMandagMedAvsluttetArbeidsforholdPåFredagOgEttLøpende() throws Exception {
        // OPPSETT, INNTEKTSMELDING, SØKNAD //
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("170");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        int inntektPerMåned = 30_000;
        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        String orgNr2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr();
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(inntektPerMåned, fnr, fpStartdato,
                orgNr);
        InntektsmeldingBuilder inntektsmeldingBuilder2 = lagInntektsmeldingBuilder(inntektPerMåned, fnr, fpStartdato,
                orgNr2).medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektPerMåned));
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder2, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        saksbehandler.ventTilAvsluttetBehandling();
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndel(orgNr, inntektPerMåned*12, inntektPerMåned*12, 0, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndel(orgNr2, inntektPerMåned*12, inntektPerMåned*12,  0, inntektPerMåned*12));
    }

    @Test
    @DisplayName("Mor med kun ytelse på skjæringstidspunktet og dagpenger i opptjeningsperioden")
    @Description("Mor med kun ytelse på skjæringstidspunktet og dagpenger i opptjeningsperioden")
    public void kun_ytelse_med_vurdering_av_besteberegning() throws Exception {
        TestscenarioDto testscenario = opprettTestscenario("172");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        // FAKTA OM FØDSEL: Avklar om søker har mottatt støtte
        saksbehandler.hentAksjonspunktbekreftelse(AvklarLopendeVedtakBekreftelse.class).bekreftGodkjent();
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarLopendeVedtakBekreftelse.class);

        // FAKTA OM MEDLEMSKAP
        saksbehandler.hentAksjonspunktbekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class)
                .setVurdering(hentKodeverk().MedlemskapManuellVurderingType.getKode("MEDLEM"));
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class);

        // FAKTA OM BEREGNING: Vurder besteberegning og fastsett månedsinntekt fra ytelse
        saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilFaktaOmBeregningTilfeller("FASTSETT_BG_KUN_YTELSE")
                .leggTilFaktaOmBeregningTilfeller("VURDER_BESTEBEREGNING")
                .leggTilAndelerYtelse(10000.0, new Kode("INNTEKTSKATEGORI", "ARBEIDSTAKER", ""))
                .settSkalHaBesteberegningForKunYtelse(true);
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderFaktaOmBeregningBekreftelse.class);


        // FORESLÅ VEDTAK //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FORESLÅ_VEDTAK);
        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);

        // FATTE VEDTAK //
        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        Aksjonspunkt apLopendeVedtak = beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_OM_SØKER_HAR_MOTTATT_STØTTE);
        Aksjonspunkt apMedlemskap = beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_GYLDIG_MEDLEMSKAPSPERIODE);
        Aksjonspunkt apFaktaOmBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(List.of(apLopendeVedtak, apMedlemskap, apFaktaOmBeregning));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling();

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        saksbehandler.ventTilAvsluttetBehandling();
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndelMedBesteberegning("BA", 120_000));
    }


    @Test
    @DisplayName("SN med gradering og Arbeidsforhold med refusjon over 6G")
    @Description("Mor er SN som søker gradering og har arbeidsgiver som søker refusjon over 6G")
    @Tag("beregning")
    public void SN_med_gradering_og_arbeidsforhold_som_søker_refusjon_over_6G() throws Exception {
        // OPPSETT, INNTEKTSMELDING, SØKNAD //
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
        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();;
        LocalDate graderingFom = fødselsdato.plusWeeks(6);
        perioder.add(uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, graderingFom.minusDays(1)));
        perioder.add(new GraderingBuilder(STØNADSKONTOTYPE_FELLESPERIODE, graderingFom, fødselsdato.plusWeeks(10))
                .medGraderingSN(50)
                .build());
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato), fordeling)
                .medSpesiellOpptjening(opptjening)
                .build();
        SøknadBuilder søknad = new SøknadBuilder(foreldrepenger, søkerAktørIdent, SøkersRolle.MOR);

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(inntektPerMåned, fnr, fpStartdato,
                orgNr).medRefusjonsBelopPerMnd(refusjon);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDLEGG_MOTTATT);
        debugLoggBehandling(saksbehandler.valgtBehandling);

        // FORDEL BEREGNINGSGRUNNLAG //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FORDEL_BEREGNINGSGRUNNLAG);
        saksbehandler.hentAksjonspunktbekreftelse(FordelBeregningsgrunnlagBekreftelse.class)
                .settFastsattBeløpOgInntektskategoriMedRefusjon(graderingFom, 500_000, 500_000, new Kode("ARBEIDSTAKER"), 1)
                .settFastsattBeløpOgInntektskategori(graderingFom, 263_488, new Kode("SELVSTENDIG_NÆRINGSDRIVENDE"), 2);
        saksbehandler.bekreftAksjonspunktBekreftelse(FordelBeregningsgrunnlagBekreftelse.class);

        // FORESLÅ VEDTAK //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FORESLÅ_VEDTAK);
        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);

        // FATTE VEDTAK //
        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        Aksjonspunkt apFordelBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.FORDEL_BEREGNINGSGRUNNLAG);
        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(List.of(apFordelBeregning));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling();

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        saksbehandler.ventTilAvsluttetBehandling();
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndel(orgNr, 720_000, 720_000, 0, 720_000));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndelForAktivitetStatus("SN", 43_287.95, 43_287.95));

        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1), lagBGAndelMedFordelt(orgNr, 720_000, 500_000, 500_000, 500_000));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1), lagBGAndelMedFordelt("SN", 43_287.95, 263_488, 263_488));

        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(2), lagBGAndel(orgNr, 720_000, 720_000, 0, 720_000));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(2), lagBGAndelForAktivitetStatus("SN", 43_287.95, 43_287.95));
    }


    @Test
    @DisplayName("Mor med for sent refusjonskrav.")
    public void morFødselForSentRefusjonskrav() throws Exception {
        // OPPSETT, INNTEKTSMELDING, SØKNAD //
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("84");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(4);
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        int inntektPerMåned = 30_000;
        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(inntektPerMåned, fnr, fpStartdato,
                orgNr).medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektPerMåned));
        InntektsmeldingBuilder inntektsmeldingBuilder2 = lagInntektsmeldingBuilder(inntektPerMåned, fnr, fpStartdato,
                orgNr).medRefusjonsBelopPerMnd(BigDecimal.valueOf(29_000));
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, saksnummer);
        fordel.sendInnInntektsmelding(inntektsmeldingBuilder2, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        // FAKTA OM FØDSEL
        saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, fødselsdato);
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderManglendeFodselBekreftelse.class);

        // FAKTA OM BEREGNING: Vurder gyldighet for refusjonskrav som har kommet for sent
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilFaktaOmBeregningTilfeller("VURDER_REFUSJONSKRAV_SOM_HAR_KOMMET_FOR_SENT")
                .leggTilRefusjonGyldighetVurdering(orgNr, true);
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderFaktaOmBeregningBekreftelse.class);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(360_000, 1L);
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);


        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRIST_FORELDREPENGER);
        saksbehandler.hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class)
                .bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);

        // FORESLÅ VEDTAK //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FORESLÅ_VEDTAK);
        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);

        // FATTE VEDTAK //
        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(List.of(
                        beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN),
                        beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS),
                        beslutter.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRIST_FORELDREPENGER),
                        beslutter.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL)
                ));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling();

        // ASSERT FASTSATT BEREGNINGSGRUNNLAG //
        saksbehandler.ventTilAvsluttetBehandling();
        Beregningsgrunnlag beregningsgrunnlag = saksbehandler.valgtBehandling.getBeregningsgrunnlag();
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndel(orgNr, 360_000, 360_000, 0, 348_000));
    }




    private void verifiserAndelerIPeriode(BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode, BGAndelHelper BGAndelHelper) {
        if (beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndel().stream().noneMatch(a -> matchAndel(BGAndelHelper, a))) {
            throw new AssertionError("Finnes ingen andeler med detaljer " + BGAndelHelper.aktivitetstatus + " orgnr: " + BGAndelHelper.arbeidsgiverId);
        }
        beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndel().forEach(andel -> {
            if (matchAndel(BGAndelHelper, andel)) {
                assertAndeler(andel, BGAndelHelper);
            }
        });
    }

    private boolean matchAndel(BGAndelHelper BGAndelHelper, BeregningsgrunnlagPrStatusOgAndelDto andel) {
        return andelTilhørerArbeidsgiverMedId(BGAndelHelper, andel) || andelTilhørerAktivitetMedStatus(BGAndelHelper, andel);
    }

    private boolean andelTilhørerAktivitetMedStatus(BGAndelHelper bgAndelHelper, BeregningsgrunnlagPrStatusOgAndelDto andel) {
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

    private boolean andelTilhørerArbeidsgiverMedId(BGAndelHelper BGAndelHelper, BeregningsgrunnlagPrStatusOgAndelDto andel) {
        return andel.getArbeidsforhold() != null && Objects.equals(andel.getArbeidsforhold().getArbeidsgiverId(), BGAndelHelper.arbeidsgiverId);
    }


    private BGAndelHelper lagBGAndel(String orgNr, int beregnetPrÅr, int bruttoPrÅr, double bortfaltNaturalytelseBeløp, double refusjonskravPrÅr) {
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


    private BGAndelHelper lagBGAndelMedFordelt(String orgNr, double beregnetPrÅr, double bruttoPrÅr, double fordeltPrÅr, double refusjonPrÅr) {
        BGAndelHelper andel = new BGAndelHelper();
        andel.arbeidsgiverId = orgNr;
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = bruttoPrÅr;
        andel.fordeltPrÅr = fordeltPrÅr;
        andel.refusjonPrÅr = refusjonPrÅr;
        return andel;
    }

    private BGAndelHelper lagBGAndelMedFordelt(String aktivitetstatus, double beregnetPrÅr, double bruttoPrÅr, double fordeltPrÅr) {
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
        andel.besteberegningPrÅr = beregnetPrÅr;
        return andel;
    }


    private BortfaltnaturalytelseHelper lagBortfaltNaturalytelse(double mndBeløp, LocalDate fom) {
        BortfaltnaturalytelseHelper nat = new BortfaltnaturalytelseHelper();
        nat.beløpPrMnd = BigDecimal.valueOf(mndBeløp);
        nat.beløpPrÅr = nat.beløpPrMnd.multiply(BigDecimal.valueOf(12));
        nat.fom = fom;
        nat.type = NaturalytelseKodeliste.ELEKTRONISK_KOMMUNIKASJON;
        return nat;

    }

    private void verifiserBGPerioder(List<LocalDate> startdatoer, Beregningsgrunnlag beregningsgrunnlag) {
        for (int i=0; i < startdatoer.size(); i++) {
            assertThat(startdatoer.get(i)).isEqualTo(beregningsgrunnlag.getBeregningsgrunnlagPeriode(i).getBeregningsgrunnlagPeriodeFom());
        }
    }

    private class BGAndelHelper {
        public String aktivitetstatus;
        private double besteberegningPrÅr;
        private double bruttoPrÅr;
        private double beregnetPrÅr;
        private double fordeltPrÅr;
        private double refusjonPrÅr;
        private double naturalytelseBortfaltPrÅr;
        private String arbeidsgiverId;
    }

    private class BortfaltnaturalytelseHelper {
        private BigDecimal beløpPrMnd;
        private BigDecimal beløpPrÅr;
        private LocalDate fom;
        private NaturalytelseKodeliste type;
    }

}
