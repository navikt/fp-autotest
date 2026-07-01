package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder.VURDER_FEILUTBETALING_KODE;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.MØDREKVOTE;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.Paragrafer;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.brev.BrevAssertionBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedNegativSimulering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.DokumentTag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.vtp.kontrakter.hendelser.YtelsevedtakDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.GrunnlagDto;

@Tag("fpsak")
@Tag("foreldrepenger")
class Ytelser extends VerdikjedeTestBase {

    @Test
    @DisplayName("Mor søker fødsel og mottar sykepenger")
    @Description("Mor søker fødsel og mottar sykepenger - opptjening automatisk oppfylt")
    void morSøkerFødselMottarSykepenger() {
        var fødselsdatoBarn = LocalDate.now().minusDays(2);
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .infotrygd(GrunnlagDto.Ytelse.SP, LocalDate.now().minusMonths(9), LocalDate.now(), GrunnlagDto.Status.LØPENDE, fødselsdatoBarn)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(fødselsdatoBarn)
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);

        assertThat(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"))
                .as("Forventer at SYKEPENGER ligger til grunn for opptjening")
                .isTrue();

        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
                .leggTilAndelerYtelse(10000.0, Inntektskategori.ARBEIDSTAKER);
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(fatterVedtakBekreftelse);

        saksbehandler.ventTilFagsakLøpende();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker")
                .isTrue();
    }


    @Test
    @DisplayName("Mor søker fødsel og mottar sykepenger og inntekter")
    @Description("Mor søker fødsel og mottar sykepenger og inntekter - opptjening automatisk godkjent")
    void morSøkerFødselMottarSykepengerOgInntekter() {
        var fødselsdatoBarn = LocalDate.now().minusDays(2);
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .infotrygd(GrunnlagDto.Ytelse.SP, LocalDate.now().minusMonths(9), LocalDate.now(), GrunnlagDto.Status.LØPENDE, fødselsdatoBarn)
                                .arbeidsforhold(LocalDate.now().minusYears(4), 30_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(fødselsdatoBarn)
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, startDatoForeldrepenger);

        saksbehandler.hentFagsak(saksnummer);

        assertThat(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"))
                .as("Forventer at SYKEPENGER ligger til grunn for opptjening")
                .isTrue();

        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderBeregnetInntektsAvvikBekreftelse())
                .leggTilInntekt((100_000), 1);
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        saksbehandler.ventTilFagsakLøpende();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor innvilges foreldrepenger på termin, deretter fødselshendelse, deretter pleiepenger med innleggelse")
    @Description("Mor innvilges foreldrepenger på termin, deretter fødselshendelse, deretter pleiepenger med innleggelse")
    void morFårFratrekkIForeldrepengerNårHunInnvilgesPleiepengerVedPrematurFødsel() {
        var termindato = LocalDate.now().plusWeeks(10).with(DayOfWeek.MONDAY);
        var fpStartdato = termindato.minusWeeks(8);

        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();
        var mor = familie.mor();

        // Steg 1: Søknad og inntektsmelding — innvilg foreldrepenger
        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medUttaksplan(List.of(
                        uttaksperiode(FELLESPERIODE, termindato.minusWeeks(12), termindato.minusWeeks(12)), //Starter med en tidlig fellesperiode for å hindre "for tidlig søkt"
                        uttaksperiode(FORELDREPENGER_FØR_FØDSEL, termindato.minusWeeks(3), termindato.minusDays(1)),
                        uttaksperiode(MØDREKVOTE, termindato, termindato.plusWeeks(15).minusDays(1)),
                        uttaksperiode(FELLESPERIODE, termindato.plusWeeks(15), termindato.plusWeeks(16).minusDays(4))))
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        ventPåInntektsmeldingForespørsel(saksnummer);
        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Foreldrepenger skal innvilges")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var fødselsdato = termindato.minusWeeks(11);
        //Steg 2: Prematur fødsel
        familie.sendInnFødselshendelse(fødselsdato);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        // Steg 3: Pleiepenger innvilges nesten helt fram til termindato
        var pleiepengerFom = fødselsdato;
        var pleiepengerTom = termindato.minusWeeks(2);
        familie.nyttVedtakOmYtelse(YtelsevedtakDto.YtelseType.PLEIEPENGER_SYKT_BARN, pleiepengerFom, pleiepengerTom,
                BigDecimal.valueOf(100));

        // Steg 4: Vent på at revurdering opprettes automatisk og avsluttes
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling(BehandlingÅrsakType.RE_VEDTAK_PLEIEPENGER);
        if (saksbehandler.harAksjonspunkt(VURDER_FEILUTBETALING_KODE)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(
                    new VurderTilbakekrevingVedNegativSimulering()).avventSamordningIngenTilbakekreving();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
            saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new ForeslåVedtakManueltBekreftelse());
        }
        saksbehandler.ventTilAvsluttetBehandling();

        // Steg 5: Verifiser at overlappende periode har fratrekk for pleiepenger
        var uttaksperioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        var overlappendePeriode = uttaksperioder.stream()
                .filter(p -> !p.getTom().isBefore(pleiepengerFom) && !p.getFom().isAfter(pleiepengerTom))
                .findFirst();

        assertThat(overlappendePeriode)
                .as("Skal finnes en uttaksperiode som overlapper pleiepengerperioden")
                .isPresent();
        assertThat(overlappendePeriode.get().getPeriodeResultatÅrsak())
                .as("Overlappende periode skal ha årsak FRATREKK_PLEIEPENGER")
                .isEqualTo(PeriodeResultatÅrsak.FRATREKK_PLEIEPENGER);
        assertThat(overlappendePeriode.get().getAktiviteter().getFirst().getTrekkdagerDesimaler().intValue())
                .as("Overlappende periode skal ha trekkdager")
                .isGreaterThan(0);
        assertThat(overlappendePeriode.get().getAktiviteter().getFirst().getKontoType())
                .as("Overlappende periode skal ha fellesperiode")
                .isEqualTo(FELLESPERIODE);

        var brevAssertionsBuilder = BrevAssertionBuilder.ny()
                .medFratrekkPgaPleiepenger()
                .medParagraf(Paragrafer.P_14_10);
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }
}
