package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AvklarAktiviteterBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FordelBeregningsgrunnlagBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SøknadErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.inntektsmelding.xml.kodeliste._20180702.NaturalytelseKodeliste;
import no.seres.xsd.nav.inntektsmelding_m._20181211.NaturalytelseDetaljer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
public class BeregningVerdikjede extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor søker fødsel med 1 arbeidsforhold og tre bortfalte naturalytelser på forskjellige tidspunkt")
    public void morSøkerFødselMedEttArbeidsforhold() throws Exception {
        TestscenarioDto testscenario = opprettScenario("49");

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
                orgNr, Optional.empty(), Optional.empty(), Optional.empty());


        // Legger til naturalytelser som opphører
        BortfaltnaturalytelseHelper førsteYtelse = lagBortfaltNaturalytelse(685, fpStartdato.plusDays(10));
        BortfaltnaturalytelseHelper andreYtelse = lagBortfaltNaturalytelse(998, fpStartdato.plusDays(40));
        BortfaltnaturalytelseHelper tredjeYtelse = lagBortfaltNaturalytelse(754, fpStartdato.plusDays(60));

        List<NaturalytelseDetaljer> opphørNaturalytelseListe = Arrays.asList(
                InntektsmeldingBuilder.createNaturalytelseDetaljer(
                        førsteYtelse.beløpPrMnd, førsteYtelse.fom, NaturalytelseKodeliste.ELEKTRONISK_KOMMUNIKASJON),
                InntektsmeldingBuilder.createNaturalytelseDetaljer(
                        andreYtelse.beløpPrMnd, andreYtelse.fom, NaturalytelseKodeliste.FRI_TRANSPORT),
                InntektsmeldingBuilder.createNaturalytelseDetaljer(
                        tredjeYtelse.beløpPrMnd, tredjeYtelse.fom, NaturalytelseKodeliste.KOST_DAGER));
        inntektsmeldingBuilder.getOpphoerAvNaturalytelsesList().getOpphoerAvNaturalytelse().addAll(opphørNaturalytelseListe);

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
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1), lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr, førsteYtelse.beløpPrÅr.doubleValue()));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(2), lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr, førsteYtelse.beløpPrÅr.add(andreYtelse.beløpPrÅr).doubleValue()));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(3), lagBGAndel(orgNr, inntektPrÅr, inntektPrÅr, førsteYtelse.beløpPrÅr.add(andreYtelse.beløpPrÅr).add(tredjeYtelse.beløpPrÅr).doubleValue()));
    }

    @Test
    @DisplayName("Mor søker fødsel med full AAP og et arbeidsforhold som tilkommer etter skjæringstidspunktet")
    public void morSøkerFødselMedFullAAPOgArbeidsforhold() throws Exception {
        // LAG SØKNAD OG SEND INN INNTEKTSMELDING //
        TestscenarioDto testscenario = opprettScenario("166");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        int inntektPerMåned = 30_000;
        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        LocalDate fpStartdato = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getAnsettelsesperiodeFom();
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(inntektPerMåned, fnr, fpStartdato,
                orgNr, Optional.empty(), Optional.of(BigDecimal.valueOf(inntektPerMåned)), Optional.empty());
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
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(0), lagBGAndelMedFordelt(aapAndel.getAktivitetStatus().kode, (int) totaltBg, (int) totaltBg, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1), lagBGAndelMedFordelt(aapAndel.getAktivitetStatus().kode, (int) totaltBg, 0, 0));
        verifiserAndelerIPeriode(beregningsgrunnlag.getBeregningsgrunnlagPeriode(1), lagBGAndelMedFordelt(orgNr, 0, (int) totaltBg, totaltBg, inntektPerMåned*12));
    }

    @Test
    @DisplayName("Mor søker fødsel med full AAP og et arbeidsforhold som ikke skal benyttes.")
    public void morSøkerFødselMedFullAAPOgArbeidsforholdSomErAktivtPåStp() throws Exception {
        // OPPSETT, INNTEKTSMELDING, SØKNAD //
        TestscenarioDto testscenario = opprettScenario("167");
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
                orgNr, Optional.empty(), Optional.of(BigDecimal.valueOf(inntektPerMåned)), Optional.empty());
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

    private void verifiserAndelerIPeriode(BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode, BGAndelHelper BGAndelHelper) {
        if (beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndel().stream().noneMatch(a -> matchAndel(BGAndelHelper, a))) {
            throw new AssertionError("Finnes ingen andeler med detaljer " + BGAndelHelper.toString());
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


    private BGAndelHelper lagBGAndel(String orgNr, int beregnetPrÅr, int bruttoPrÅr, double bortfaltNaturalytelseBeløp) {
        BGAndelHelper andel = new BGAndelHelper();
        andel.arbeidsgiverId = orgNr;
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = bruttoPrÅr;
        andel.naturalytelseBortfaltPrÅr = bortfaltNaturalytelseBeløp;
        return andel;    }

    private BGAndelHelper lagBGAndelMedFordelt(String orgNr, int beregnetPrÅr, int bruttoPrÅr, double fordeltPrÅr, double refusjonPrÅr) {
        BGAndelHelper andel = new BGAndelHelper();
        andel.arbeidsgiverId = orgNr;
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = bruttoPrÅr;
        andel.fordeltPrÅr = fordeltPrÅr;
        andel.refusjonPrÅr = refusjonPrÅr;
        return andel;
    }

    private BGAndelHelper lagBGAndelMedFordelt(String aktivitetstatus, int beregnetPrÅr, int bruttoPrÅr, double fordeltPrÅr) {
        BGAndelHelper andel = new BGAndelHelper();
        andel.aktivitetstatus = aktivitetstatus;
        andel.beregnetPrÅr = beregnetPrÅr;
        andel.bruttoPrÅr = bruttoPrÅr;
        andel.fordeltPrÅr = fordeltPrÅr;
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
