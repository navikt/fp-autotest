package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.common.domain.BrukerRolle.MOR;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.MØDREKVOTE;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("fpsak")
@Tag("foreldrepenger")
class Termin extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker med ett arbeidsforhold. Inntektmelding innsendt før søknad")
    @Description("Mor med ett arbeidsforhold sender inn inntektsmelding før søknad. " +
            "Forventer at vedtak bli fattet og det blir bare opprettet en behandling")
    void MorSøkerMedEttArbeidsforholdInntektsmeldingFørSøknad() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusYears(4),
                                        arbeidsavtale(LocalDate.now().minusYears(4), LocalDate.now().minusDays(60)).build(),
                                        arbeidsavtale(LocalDate.now().minusDays(59)).stillingsprosent(50).build()
                                )
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(3);
        var startDatoForeldrepenger = termindato.minusWeeks(3);
        var arbeidsgiver = mor.arbeidsgiver();
        var saksnummer = arbeidsgiver.sendInntektsmeldingerFP(null, startDatoForeldrepenger);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENT_PÅ_SØKNAD);
        var behandlinger = saksbehandler.hentAlleBehandlingerForFagsak(saksnummer);
        assertThat(behandlinger)
                .as("Antall behandlinger")
                .hasSize(1);

        var søknad = lagSøknadForeldrepengerTermin(termindato, MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        mor.søk(søknad.build(), saksnummer);

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.hentAlleBehandlingerForFagsak(saksnummer))
                .as("Antall behandlinger")
                .hasSize(1);
    }

    @Test
    @DisplayName("Mor søker sak behandlet før inntektsmelding mottatt")
    @Description("Mor søker og saken blir behandlet før inntektsmelding er mottat basert på data fra " +
            "inntektskomponenten, så mottas inntektsmeldingen")
    void MorSøkerMedEttArbeidsforholdInntektsmeldingPåGjennopptattSøknad() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusYears(4),
                                        arbeidsavtale(LocalDate.now().minusMonths(4), LocalDate.now().minusDays(60)).stillingsprosent(50).build(),
                                        arbeidsavtale(LocalDate.now().minusDays(59)).sisteLønnsendringsdato(LocalDate.now().minusMonths(1)).build()
                                )
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var termindato = LocalDate.now().minusWeeks(1);
        var startDatoForeldrepenger = termindato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTermin(termindato, MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandling er satt på vent (Behandling er ikke satt på vent etter uten inntektsmelding)")
                .isTrue();

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();
        if (saksbehandler.harAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE)) {
            saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE);
            saksbehandler.gjenopptaBehandling();
        }

        // Løs 5085, ikke vent på inntektsmeldinger
        saksbehandler.fortsettUteninntektsmeldinger();

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, startDatoForeldrepenger);
        Vent.til(() -> {
            saksbehandler.velgSisteBehandling();
            return saksbehandler.harAksjonspunkt(AksjonspunktKoder.FORESLÅ_VEDTAK_MANUELT);
            }, "Fikk ikke aksjonspunkt 5028 foreslå vedtak manuelt", "5028 aksjonspunkt");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        saksbehandler.velgSisteBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker termin med avvik i gradering")
    @Description("Mor med to arbeidsforhold søker termin. Søknad inneholder gradering. En periode som er forflyttet i" +
            "fht IM, en periode som har feil graderingsprosent i fht IM, en periode som har feil orgnr i fht IM og " +
            "en periode som er ok.")
    void morSøkerTerminEttArbeidsforhold_avvikIGradering() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(40, LocalDate.now().minusYears(4), 120_000)
                                .arbeidsforhold(60, LocalDate.now().minusYears(2), 300_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(6);
        var fpstartdato = termindato.minusWeeks(3);
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsgiveridentifikator1 = arbeidsforholdene.get(0).arbeidsgiverIdentifikasjon();
        var arbeidsgiveridentifikator2 = arbeidsforholdene.get(1).arbeidsgiverIdentifikasjon();
        var fordeling = fordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpstartdato, fpstartdato.plusWeeks(3).minusDays(1)),
                uttaksperiode(MØDREKVOTE, termindato, termindato.plusWeeks(6).minusDays(1)),
                graderingsperiodeArbeidstaker(MØDREKVOTE, termindato.plusWeeks(6),
                        termindato.plusWeeks(9).minusDays(1), arbeidsgiveridentifikator2, 40),
                uttaksperiode(MØDREKVOTE, termindato.plusWeeks(9), termindato.plusWeeks(12).minusDays(1)),
                graderingsperiodeArbeidstaker(MØDREKVOTE, termindato.plusWeeks(12),
                        termindato.plusWeeks(15).minusDays(1), arbeidsgiveridentifikator1, 10),
                graderingsperiodeArbeidstaker(FELLESPERIODE, termindato.plusWeeks(15),
                        termindato.plusWeeks(18).minusDays(1), arbeidsgiveridentifikator2, 20),
                graderingsperiodeArbeidstaker(FELLESPERIODE, termindato.plusWeeks(18),
                        termindato.plusWeeks(21).minusDays(1), arbeidsgiveridentifikator1, 30)
        );
        var søknad = lagSøknadForeldrepengerTermin(termindato, MOR)
                .medFordeling(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        mor.arbeidsgivere().sendDefaultInntektsmeldingerFP(saksnummer, fpstartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        var resultatPerioder = saksbehandler.valgtBehandling.getUttakResultatPerioder()
                .getPerioderSøker();
        assertThat(resultatPerioder)
                .as("Uttaksperioder (resultat)")
                .hasSize(7)
                .allMatch(p -> p.getPeriodeResultatType().equals(PeriodeResultatType.INNVILGET));
        assertThat(resultatPerioder.get(2).getGraderingInnvilget())
                .as("Gradering innvilget")
                .isTrue();
        assertThat(resultatPerioder.get(2).getGradertArbeidsprosent())
                .as("Graderingsprosent i periode")
                .isEqualByComparingTo(BigDecimal.valueOf(40));
        assertThat(resultatPerioder.get(4).getGraderingInnvilget())
                .as("Gradering innvilget")
                .isTrue();
        assertThat(resultatPerioder.get(4).getGradertArbeidsprosent())
                .as("Graderingsprosent i periode")
                .isEqualByComparingTo(BigDecimal.valueOf(10));
        assertThat(resultatPerioder.get(5).getGraderingInnvilget())
                .as("Gradering innvilget")
                .isTrue();
        assertThat(resultatPerioder.get(5).getGradertArbeidsprosent())
                .as("Graderingsprosent i periode")
                .isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(resultatPerioder.get(6).getGraderingInnvilget())
                .as("Gradering innvilget")
                .isTrue();
        assertThat(resultatPerioder.get(6).getGradertArbeidsprosent())
                .as("Graderingsprosent i periode")
                .isEqualByComparingTo(BigDecimal.valueOf(30));
    }

    @Test
    @DisplayName("Mor søker termin uten FPFF")
    @Description("Mor søker termin uten periode for foreldrepenger før fødsel. Skjæringstidspunkt skal være 3 uker før termindato.")
    void morSokerTerminUtenFPFFperiode() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusYears(4),
                                        arbeidsavtale(LocalDate.now().minusYears(4), LocalDate.now().minusDays(60)).build(),
                                        arbeidsavtale(LocalDate.now().minusDays(59)).stillingsprosent(50).build()
                                )
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(3);
        var startDatoForeldrepenger = termindato;
        var fordeling = fordeling(
                uttaksperiode(MØDREKVOTE, termindato, termindato.plusWeeks(15).minusDays(1))
        );
        var søknad = lagSøknadForeldrepengerTermin(termindato, MOR)
                .medFordeling(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, startDatoForeldrepenger);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        var resultatPerioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(resultatPerioder)
                .as("Antall uttaksperioder")
                .hasSize(2);
        assertThat(resultatPerioder.get(0).getPeriodeResultatType())
                .as("Perioderesultatstype for uttaksperiode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(resultatPerioder.get(0).getAktiviteter().get(0).getStønadskontoType())
                .as("Stønadskontotype for første uttaksperiode i første aktivitet")
                .isEqualTo(MØDREKVOTE);
        assertThat(resultatPerioder.get(1).getPeriodeResultatType())
                .as("Perioderesultatstype for uttaksperiode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(resultatPerioder.get(1).getAktiviteter().get(0).getStønadskontoType())
                .as("Stønadskontotype for uttaksperiode i aktivitet")
                .isEqualTo(MØDREKVOTE);
        var skjaeringstidspunkt = termindato.minusWeeks(3);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.skjæringstidspunkt().dato())
                .as("Skjæringstidspunkt")
                .isEqualTo(skjaeringstidspunkt);
    }
}
