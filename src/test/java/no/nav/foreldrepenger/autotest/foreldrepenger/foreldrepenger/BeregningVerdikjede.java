package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.soeknad.ForeldrepengesoknadBuilder;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingBuilder;
import no.nav.foreldrepenger.fpmock2.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.fpmock2.testmodell.dokument.modell.koder.DokumenttypeId;

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

        ForeldrepengesoknadBuilder søknad = foreldrepengeSøknadErketyper.fodselfunnetstedUttakKunMor(søkerAktørIdent, fødselsdato);
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

    private void verifiserAndelerIPeriode(BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode, BGAndelHelper BGAndelHelper) {
        beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndel().stream().forEach(andel -> {
            if (andelTilhørerArbeidsgiverMedId(BGAndelHelper, andel)) {
                assertAndeler(andel, BGAndelHelper);
            }
        });
    }

    private void assertAndeler(BeregningsgrunnlagPrStatusOgAndelDto andel, BGAndelHelper BGAndelHelper) {
        assertThat(andel.getBruttoPrAar()).isEqualTo(BGAndelHelper.bruttoPrÅr);
        assertThat(andel.getBeregnetPrAar()).isEqualTo(BGAndelHelper.beregnetPrÅr);
        assertThat(andel.getBortfaltNaturalytelse()).isEqualTo(BGAndelHelper.naturalytelseBortfaltPrÅr);
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
        private double bruttoPrÅr;
        private double beregnetPrÅr;
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
