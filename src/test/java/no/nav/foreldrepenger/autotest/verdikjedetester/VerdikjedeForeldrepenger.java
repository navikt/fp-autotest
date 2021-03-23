package no.nav.foreldrepenger.autotest.verdikjedetester;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_HENDELSE_DØD_FORELDER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_HENDELSE_FØDSEL;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.OpptjeningErketyper.medEgenNaeringOpptjening;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerAdopsjon;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper.overføringsperiode;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.UtsettelsesÅrsak.ARBEID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.IkkeOppfyltÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.InnvilgetÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.KonsekvensForYtelsen;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AnkeVurderingResultatBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettBruttoBeregningsgrunnlagSNBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsetteUttakKontrollerOpplysningerOmDødDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravKa;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravNfp;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerAktivitetskravBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerManueltOpprettetRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerRealitetsbehandlingEllerKlage;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderRefusjonBeregningsgrunnlagBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedNegativSimulering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderVarigEndringEllerNyoppstartetSNBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAdopsjonsdokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAleneomsorgBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAnnenForeldreHarRett;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaUttakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrUttaksperioder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.VilkarTypeKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.DekningsgradDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.søknad.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.RelasjonTilBarnErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadForeldrepengerErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.Overføringsårsak;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.autotest.util.localdate.Virkedager;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Orgnummer;
import no.nav.foreldrepenger.vtp.kontrakter.DødfødselhendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.DødshendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.FødselshendelseDto;

@Tag("verdikjede")
class VerdikjedeForeldrepenger extends ForeldrepengerTestBase {

    @Test
    @DisplayName("1: Mor automatisk førstegangssøknad termin (med fødselshendelse), aleneomsorg og avvik i beregning.")
    @Description("Mor førstegangssøknad før fødsel på termin. Mor har aleneomsorg og enerett. Sender inn IM med over " +
                "25% avvik med delvis refusjon. Etter behandlingen er ferdigbehandlet mottas en fødselshendelse.")
    void testcase_mor_fødsel() {
        var familie = new Familie("501");
        var mor = familie.mor();
        var identSøker = familie.mor().fødselsnummer();
        var termindato = LocalDate.now().plusWeeks(1);
        var fpStartdato = termindato.minusWeeks(3);
        var fordeling = FordelingErketyper.generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, termindato.minusDays(1)),
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FORELDREPENGER, termindato, termindato.plusWeeks(15).minusDays(1)),
                UttaksperioderErketyper.utsettelsesperiode(UtsettelsesÅrsak.ARBEID, termindato.plusWeeks(15),
                        termindato.plusWeeks(20).minusDays(1)),
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FORELDREPENGER, termindato.plusWeeks(20), termindato.plusWeeks(36).minusDays(1)));
        var søknad = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medFordeling(fordeling)
                .medRettigheter(RettigheterErketyper.harAleneOmsorgOgEnerett())
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far().fødselsnummer()))
                .medMottatdato(termindato.minusWeeks(5));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        var månedsinntekt = mor.månedsinntekt();
        var avvikendeMånedsinntekt = månedsinntekt * 1.3;
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medBeregnetInntekt(BigDecimal.valueOf(avvikendeMånedsinntekt))
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(månedsinntekt * 0.6));
        arbeidsgiver.sendInntektsmeldinger(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(månedsinntekt * 12, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        var avklarFaktaAleneomsorgBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAleneomsorgBekreftelse.class)
                .bekreftBrukerHarAleneomsorg()
                .setBegrunnelse("Bekreftelse sendt fra Autotest.");
        saksbehandler.bekreftAksjonspunktbekreftelserer(avklarFaktaAleneomsorgBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo())
                .as("Saldo for stønadskontoen FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER).getSaldo())
                .as("Saldoen for stønadskontoen FORELDREPENGER")
                .isEqualTo(75);

        var beregnetDagsats = regnUtForventetDagsatsForPeriode(List.of(månedsinntekt), List.of(100), List.of(false));
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats())
                .as("Forventer at dagsatsen blir justert ut i fra årsinntekten og utbeatlinsggrad, og IKKE 6G fordi inntekten er under 6G!")
                .isEqualTo(beregnetDagsats.get(0));
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(60))
                .as("Forventer at 40% summen utbetales til søker og 60% av summen til arbeisdgiver pga 60% refusjon!")
                .isTrue();

        // Fødselshendelse
        var fødselshendelseDto = new FødselshendelseDto("OPPRETTET", null, identSøker.fnr(),
                null, null, termindato.minusWeeks(1));
        innsender.opprettHendelsePåKafka(fødselshendelseDto);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().get(0).getBehandlingArsakType())
                .as("Årsakskode til revuderingen")
                .isEqualTo(RE_HENDELSE_FØDSEL);

        var avklarFaktaAleneomsorgBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAleneomsorgBekreftelse.class)
                .bekreftBrukerHarAleneomsorg()
                .setBegrunnelse("Bekreftelse sendt fra Autotest.");
        saksbehandler.bekreftAksjonspunktbekreftelserer(avklarFaktaAleneomsorgBekreftelse2);

        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);

        // Verifiser riktig justering av kontoer og uttak.
        saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo())
                .as("Saldo for stønadskontoen FORELDREPENGER_FØR_FØDSEL")
                .isEqualTo(5);
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER).getSaldo())
                .as("Saldo for stønadskontoen FORELDREPENGER")
                .isEqualTo(70);
    }

    @Test
    @DisplayName("2: Mor selvstendig næringsdrivende, varig endring. Søker dør etter behandlingen er ferdigbehandlet.")
    @Description("Mor er selvstendig næringsdrivende og har ferdiglignet inntekt i mange år. Oppgir en næringsinntekt" +
            "som avviker med mer enn 25% fra de tre siste ferdiglignede årene. Søker dør etter behandlingen er " +
            "ferdigbehandlet.")
    void morSelvstendigNæringsdrivendeTest() {
        var familie = new Familie("510");
        var fødselsdato = familie.barn().fødselsdato();
        var mor = familie.mor();
        var identSøker = mor.fødselsnummer();
        var næringsinntekt = mor.næringsinntekt(2018);
        var avvikendeNæringsinntekt = næringsinntekt * 1.8; // >25% avvik
        var opptjening = OpptjeningErketyper.medEgenNaeringOpptjening(false, avvikendeNæringsinntekt, true);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far().fødselsnummer()))
                .medOpptjening(opptjening)
                .medMottatdato(fødselsdato.plusWeeks(2));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class)
                .godkjennAllOpptjening()
                .setBegrunnelse("Opptjening godkjent av Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // Verifiser at aksjonspunkt 5042 ikke blir oprettet uten varig endring
        var vurderVarigEndringEllerNyoppstartetSNBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderVarigEndringEllerNyoppstartetSNBekreftelse.class)
                .setErVarigEndretNaering(false)
                .setBegrunnelse("Ingen endring");
        saksbehandler.bekreftAksjonspunkt(vurderVarigEndringEllerNyoppstartetSNBekreftelse);
        assertThatThrownBy(() -> saksbehandler.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_SELVSTENDIG_NÆRINGSDRIVENDE))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Fant ikke aksjonspunkt med kode 5042");

        var vurderVarigEndringEllerNyoppstartetSNBekreftelse1 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderVarigEndringEllerNyoppstartetSNBekreftelse.class)
                .setErVarigEndretNaering(true)
                .setBegrunnelse("Vurder varig endring for selvstendig næringsdrivende begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderVarigEndringEllerNyoppstartetSNBekreftelse1);
        var fastsettBruttoBeregningsgrunnlagSNBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettBruttoBeregningsgrunnlagSNBekreftelse.class)
                .setBruttoBeregningsgrunnlag((int) avvikendeNæringsinntekt)
                .setBegrunnelse("Grunnlag begrunnelse");
        saksbehandler.bekreftAksjonspunkt(fastsettBruttoBeregningsgrunnlagSNBekreftelse);

        // verifiser skjæringstidspunkt i følge søknad
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getSkjaeringstidspunktBeregning())
                .as("Skæringstidspunkt beregning")
                .isEqualTo(fødselsdato.minusWeeks(3));

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var beregningAktivitetStatus = saksbehandler.hentUnikeBeregningAktivitetStatus();
        assertThat(beregningAktivitetStatus)
                .as("Forventer at søker får utbetaling med status SN!")
                .contains(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE);
        assertThat(beregningAktivitetStatus.size())
                .as("Beregning aktivitetsstatus")
                .isEqualTo(1);

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo())
                .as("Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo())
                .as("Saldoen for stønadskonton MØDREKVOTE")
                .isZero();
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo())
                .as("Saldoen for stønadskonton FELLESPERIODE")
                .isZero();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();

        var dødshendelseDto = new DødshendelseDto("OPPRETTET", null, identSøker.fnr(),
                LocalDate.now().minusDays(1));
        innsender.opprettHendelsePåKafka(dødshendelseDto);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().get(0).getBehandlingArsakType())
                .as("Behandlingsårsakstype")
                .isEqualTo(RE_HENDELSE_DØD_FORELDER);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class)
                .avslåManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(FastsetteUttakKontrollerOpplysningerOmDødDto.class);

        if (saksbehandler.harAksjonspunkt("5084")) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.
                    hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
            vurderTilbakekrevingVedNegativSimulering.setTilbakekrevingIgnorer();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        }

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true);

        assertThat(saksbehandler.hentAvslåtteUttaksperioder().size())
                .as("Avslåtte uttaksperioder")
                .isEqualTo(3);

        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats())
                .as("Dagsats tilkjent ytelse periode #2")
                .isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats())
                .as("Dagsats tilkjent ytelse periode #3")
                .isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(4).getDagsats())
                .as("Dagsats tilkjent ytelse periode #4")
                .isZero();
    }

    @Test
    @DisplayName("3: Mor, sykepenger, kun ytelse, papirsøknad")
    @Description("Mor søker fullt uttak, men søker mer enn det hun har rett til. Klager på førstegangsbehandlingen og " +
            "vedtaket stadfestes. Søker anker stadfestelsen og saksbehanlder oppretter en ankebehandling. Bruker får " +
            "omgjøring i anke")
    void morSykepengerKunYtelseTest() {
        var familie = new Familie("520");
        var mor = familie.mor();
        var saksnummer = mor.søkPåPapir();

        saksbehandler.hentFagsak(saksnummer);
        var termindato = LocalDate.now().plusWeeks(6);
        var fpStartdatoMor = termindato.minusWeeks(3);
        var fpMottatDato = termindato.minusWeeks(6);
        var fordelingDtoMor = new FordelingDto();
        var foreldrepengerFørFødsel = new PermisjonPeriodeDto(Stønadskonto.FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor,
                termindato.minusDays(1));
        var mødrekvote = new PermisjonPeriodeDto(Stønadskonto.MØDREKVOTE, termindato,
                termindato.plusWeeks(20).minusDays(1));
        fordelingDtoMor.permisjonsPerioder.add(foreldrepengerFørFødsel);
        fordelingDtoMor.permisjonsPerioder.add(mødrekvote);
        var papirSoknadForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(PapirSoknadForeldrepengerBekreftelse.class)
                .morSøkerTermin(fordelingDtoMor, termindato, fpMottatDato, DekningsgradDto.AATI);
        saksbehandler.bekreftAksjonspunkt(papirSoknadForeldrepengerBekreftelse);

        var avklarArbeidsforholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        var avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class)
                .setUtstedtdato(termindato.minusWeeks(10))
                .setBegrunnelse("Begrunnelse fra autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        assertThat(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"))
                .as("Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som " +
                        "er forut for permisjonen på skjæringstidspunktet!")
                .isTrue();

        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .leggTilAndelerYtelse(10000.0, Inntektskategori.ARBEIDSTAKER)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class)
                .avslåManuellePerioderMedPeriodeResultatÅrsak(IkkeOppfyltÅrsak.IKKE_STØNADSDAGER_IGJEN);
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();

        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();
        var klageFormkravNfp = klagebehandler
                .hentAksjonspunktbekreftelse(KlageFormkravNfp.class)
                .godkjennAlleFormkrav()
                .setBegrunnelse("Super duper klage!");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        var vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageBekreftelse.VurderingAvKlageNfpBekreftelse.class)
                .bekreftStadfestet()
                .fritekstBrev("Fritektst til brev fra klagebehandler.")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        var klageFormkravKa = klagebehandler
                .hentAksjonspunktbekreftelse(KlageFormkravKa.class)
                .godkjennAlleFormkrav()
                .setBegrunnelse("Super duper klage!");
        klagebehandler.bekreftAksjonspunkt(klageFormkravKa);

        var vurderingAvKlageNkBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageBekreftelse.VurderingAvKlageNkBekreftelse.class)
                .bekreftStadfestet()
                .fritekstBrev("Fritektst til brev fra klagebehandler.")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNkBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling();
        assertThat(klagebehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.KLAGE_YTELSESVEDTAK_STADFESTET);

        // ANKE
        mor.sendInnKlage();
        klagebehandler.opprettBehandling(BehandlingType.ANKE, null);
        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgAnkeBehandling();
        var ankeVurderingResultatBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(AnkeVurderingResultatBekreftelse.class)
                .omgjørTilGunst()
                .setBegrunnelse("Super duper anke!");
        klagebehandler.bekreftAksjonspunkt(ankeVurderingResultatBekreftelse);

        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgAnkeBehandling();
        var fatterVedtakBekreftelseAnke = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelseAnke.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelseAnke);

        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling();
        assertThat(klagebehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.ANKE_OMGJOER);
    }

    @Test
    @DisplayName("4: Far søker resten av fellesperioden og hele fedrekvoten med gradert uttak.")
    @Description("Mor har løpende fagsak med hele mødrekvoten og deler av fellesperioden. Far søker resten av fellesperioden" +
            "og hele fedrekvoten med gradert uttak. Far har to arbeidsforhold i samme virksomhet, samme org.nr, men ulik" +
            "arbeidsforholdsID. To inntekstmeldinger sendes inn med refusjon på begge.")
    void farSøkerForeldrepengerTest() {
        var familie = new Familie("560");

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(23);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(familie,
                fødselsdato, fpStartdatoMor, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus("IKKE_HOY");
        saksbehandler.ventTilAvsluttetBehandling();

        /*
         * FAR: Søker med to arbeidsforhold i samme virksomhet, orgn.nr, men med ulik
         * arbeidsforholdID. Søker utsettelse arbeid og deretter resten av felles
         * perioden og hele fedrekvoten med gradert uttak. Sender inn 2 IM med ulik
         * arbeidsforholdID og refusjon på begge.
         */
        var far = familie.far();
        var identFar = far.fødselsnummer();
        var orgNummerFar = far.arbeidsgiver().orgnummer();
        var fordelingFar = generiskFordeling(
                utsettelsesperiode(ARBEID, fpStartdatoFar, fpStartdatoFar.plusWeeks(3).minusDays(1)),
                graderingsperiodeArbeidstaker(FELLESPERIODE,
                        fpStartdatoFar.plusWeeks(3),
                        fpStartdatoFar.plusWeeks(19).minusDays(1),
                        orgNummerFar,
                        50),
                graderingsperiodeArbeidstaker(FEDREKVOTE,
                        fpStartdatoFar.plusWeeks(19),
                        fpStartdatoFar.plusWeeks(49).minusDays(1),
                        orgNummerFar,
                        50));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                        .medAnnenForelder(lagNorskAnnenforeldre(familie.mor().fødselsnummer()))
                        .medFordeling(fordelingFar);
        var saksnummerFar = far.søk(søknadFar.build());


        var arbeidsgiver = far.arbeidsgiver();
        var inntektsmeldingerFar = arbeidsgiver.lagInntektsmeldingerFP(fpStartdatoFar);
        inntektsmeldingerFar.get(0).medRefusjonsBelopPerMnd(100);
        inntektsmeldingerFar.get(1).medRefusjonsBelopPerMnd(100);
        arbeidsgiver.sendInntektsmeldinger(saksnummerFar, inntektsmeldingerFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerAktivitetskravBekreftelse.class);

        /*
         * Fellesperioden skal splittes slik at første periode på 8 uker blir avslått og
         * en andre perioden (av splitten) skal stjele dager fra fedrekvoten. Deretter
         * skal fedrekvoten reduseres med 8 uker. (trenger også en split).
         * Bruker overstyrer ettersom uttaket er automatisk innvilget
         */
        overstyrer.hentFagsak(saksnummerFar);
        overstyrer.velgSisteBehandling();
        var overstyringUttak = new OverstyrUttaksperioder();
        overstyringUttak.oppdaterMedDataFraBehandling(overstyrer.valgtFagsak, overstyrer.valgtBehandling);
        overstyringUttak.splitPeriode(
                fpStartdatoFar.plusWeeks(3),
                fpStartdatoFar.plusWeeks(19).minusDays(1),
                fpStartdatoFar.plusWeeks(11).minusDays(1));
        overstyringUttak.avslåPeriode(
                fpStartdatoFar.plusWeeks(3),
                fpStartdatoFar.plusWeeks(11).minusDays(1),
                IkkeOppfyltÅrsak.AKTIVITETSKRAVET_ARBEID_IKKE_OPPFYLT,
                true);
        overstyringUttak.innvilgPeriode(
                fpStartdatoFar.plusWeeks(11),
                fpStartdatoFar.plusWeeks(19).minusDays(1),
                InnvilgetÅrsak.GRADERING_KVOTE_ELLER_OVERFØRT_KVOTE,
                Stønadskonto.FEDREKVOTE);
        overstyringUttak.splitPeriode(
                fpStartdatoFar.plusWeeks(19),
                fpStartdatoFar.plusWeeks(49).minusDays(1),
                fpStartdatoFar.plusWeeks(41).minusDays(1));
        overstyringUttak.innvilgPeriode(
                fpStartdatoFar.plusWeeks(19),
                fpStartdatoFar.plusWeeks(41).minusDays(1),
                InnvilgetÅrsak.GRADERING_KVOTE_ELLER_OVERFØRT_KVOTE);
        overstyringUttak.avslåPeriode(
                fpStartdatoFar.plusWeeks(41),
                fpStartdatoFar.plusWeeks(49).minusDays(1),
                IkkeOppfyltÅrsak.IKKE_STØNADSDAGER_IGJEN);
        overstyringUttak.setBegrunnelse("Begrunnelse fra Autotest.");
        overstyrer.overstyr(overstyringUttak);

        saksbehandler.velgSisteBehandling();
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo())
                .as("saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo())
                .as("saldoen for stønadskonton MØDREKVOTE")
                .isZero();
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.FEDREKVOTE).getSaldo())
                .as("saldoen for stønadskonton FEDREKVOTE")
                .isZero();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(100))
                .as("Forventer at hele summen utbetales til arbeidsgiver, og derfor ingenting til søker!")
                .isTrue();
    }

    @Test
    @DisplayName("5: Far søker fellesperiode og fedrekvote som frilanser.")
    @Description("Mor søker hele mødrekvoten og deler av fellesperiode, happy case. Far søker etter føsdsel og søker" +
            "noe av fellesperioden og hele fedrekvoten. Opplyser at han er frilanser og har frilanserinntekt frem til" +
            "skjæringstidspunktet.")
    void farSøkerSomFrilanser() {
        var familie = new Familie("561");

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(18);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(familie,
                fødselsdato, fpStartdatoMor, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus("IKKE_HOY");
        saksbehandler.ventTilAvsluttetBehandling();

        /*
         * FAR: Søker som FL. Har frilansinntekt frem til, men ikke inklusiv,
         * skjæringstidspunktet. Søker noe av fellesperioden og deretter hele
         * fedrekvoten
         */
        var far = familie.far();
        var fordelingFar = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(4).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(4), fpStartdatoFar.plusWeeks(19).minusDays(1)));
        var frilansFom = far.FrilansAnnsettelsesFom();
        var opptjeningFar = OpptjeningErketyper.medFrilansOpptjening(frilansFom, fpStartdatoFar.minusDays(1));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.mor().fødselsnummer()))
                .medFordeling(fordelingFar)
                .medOpptjening(opptjeningFar);
        var saksnummerFar = far.søk(søknadFar.build());

        saksbehandler.hentFagsak(saksnummerFar);

        assertThat(saksbehandler.sjekkOmDetErOpptjeningFremTilSkjæringstidspunktet("FRILANS"))
                .as("Forventer at det er registert en opptjeningsaktivitet med aktivitettype FRILANSER som " +
                        "har frilansinntekt på skjæringstidspunktet!")
                .isTrue();

        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilMottarYtelseFrilans(false)
                .setBegrunnelse("Begrunnelse fra Autotest.");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerAktivitetskravBekreftelse.class);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();
    }

    @Test
    @DisplayName("6: Far søker foreldrepenger med AF som ikke er avsluttet og mor ikke har rett.")
    @Description("Far søker foreldrepenger med to aktive arbeidsforhold og ett gammelt arbeidsforhold som skulle vært " +
            "avsluttet, men er ikke det. Søker en graderingsperiode i en av virksomheten og gjennopptar full " +
            "deltidsstilling: I dette arbeidsforholdet vil AG ha full refusjon i hele perioden. I det andre vil AG " +
            "bare ha refusjon i to måneder.")
    void farSøkerMedToAktiveArbeidsforholdOgEtInaktivtTest() {
        var familie = new Familie("570");
        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();
        var arbeidsforholdene = far.arbeidsforholdene();
        var arbeidsforhold1 = arbeidsforholdene.get(0);
        var orgNummerFar1 = arbeidsforhold1.orgnummer();
        var stillingsprosent1 = arbeidsforhold1.stillingsprosent();
        var fpStartdatoFar = Virkedager.helgejustertTilMandag(fødselsdato.plusWeeks(6));
        var fordelingFar = generiskFordeling(
                graderingsperiodeArbeidstaker(StønadskontoType.FORELDREPENGER,
                        fpStartdatoFar,
                        fpStartdatoFar.plusWeeks(100).minusDays(1),
                        orgNummerFar1,
                        stillingsprosent1));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                        .medRettigheter(RettigheterErketyper.harIkkeAleneomsorgOgAnnenpartIkkeRett())
                        .medFordeling(fordelingFar)
                        .medAnnenForelder(lagNorskAnnenforeldre(familie.mor().fødselsnummer()));
        var saksnummerFar = far.søk(søknadFar.build());

        var arbeidsgivere = far.arbeidsgivere().getArbeidsgivere();
        var arbeidsgiver1 = arbeidsgivere.get(0);
        var inntektsmelding1 = arbeidsgiver1.lagInntektsmeldingFP(fpStartdatoFar)
                .medRefusjonsBelopPerMnd(100);
        arbeidsgiver1.sendInntektsmeldinger(saksnummerFar, inntektsmelding1);

        var arbeidsgiver2 = arbeidsgivere.get(1);
        var opphørsDatoForRefusjon = fpStartdatoFar.plusMonths(2).minusDays(1);
        var inntektsmelding2 = arbeidsgiver2.lagInntektsmeldingFP(fpStartdatoFar)
                .medRefusjonsBelopPerMnd(100)
                .medRefusjonsOpphordato(opphørsDatoForRefusjon);
        arbeidsgiver2.sendInntektsmeldinger(saksnummerFar, inntektsmelding2);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();

        var avklarArbeidsforholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErIkkeAktivt(
                        new Orgnummer("991779493"),
                        arbeidsforholdene.get(2).ansettelsesperiodeFom(),
                        LocalDate.now().minusYears(4).minusDays(1),
                        "Arbeidsforholdet skulle vært avsluttet");
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        var avklarFaktaAnnenForeldreHarRett = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAnnenForeldreHarRett.class)
                .setAnnenforelderHarRett(false)
                .setBegrunnelse("Bare far har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerAktivitetskravBekreftelse.class);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);

        /* VERIFISERINGER */
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(Stønadskonto.FORELDREPENGER).getSaldo())
                .as("Saldoen for stønadskonton FORELDREPENGER")
                .isZero();

        var beregningsresultatPerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger().getPerioder();
        assertThat(beregningsresultatPerioder.size())
                .as("Beregningsresultatperidoer")
                .isEqualTo(3);
        assertThat(beregningsresultatPerioder.get(0).getTom())
                .as("Forventer at lengden på første peridoe har tom dato som matcher tom dato angitt i IM#2")
                .isEqualTo(opphørsDatoForRefusjon);
        assertThat(beregningsresultatPerioder.get(1).getTom())
                .as("Forventer den andre periden har en varighet på 40 uker")
                .isEqualTo(fpStartdatoFar.plusWeeks(40).minusDays(1));


        var orgNummerFar2 = arbeidsforholdene.get(1).orgnummer();
        var månedsinntektFar1 = far.månedsinntekt(orgNummerFar1);
        var månedsinntektFar2 = far.månedsinntekt(orgNummerFar2);
        var andelerForAT1 = saksbehandler.hentBeregningsresultatPerioderMedAndelIArbeidsforhold(orgNummerFar1);
        var andelerForAT2 = saksbehandler.hentBeregningsresultatPerioderMedAndelIArbeidsforhold(orgNummerFar2);
        var forventetDagsatsForFørstePeriode = regnUtForventetDagsatsForPeriode(
                List.of(månedsinntektFar1, månedsinntektFar2),
                List.of(40, 100), List.of(true, true));
        assertThat(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().get(0).getDagsats())
                .as("Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel")
                .isEqualTo(sumOfList(forventetDagsatsForFørstePeriode));
        assertThat(andelerForAT1.get(0).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(forventetDagsatsForFørstePeriode.get(0));
        assertThat(andelerForAT2.get(0).getRefusjon())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til arbeidsgiver")
                .isEqualTo(forventetDagsatsForFørstePeriode.get(1));

        var forventetDagsatsForAndrePeriode = regnUtForventetDagsatsForPeriode(
                List.of(månedsinntektFar1, månedsinntektFar2),
                List.of(40, 100), List.of(true, false));
        assertThat(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().get(1).getDagsats())
                .as("Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel")
                .isEqualTo(sumOfList(forventetDagsatsForAndrePeriode));
        assertThat(andelerForAT1.get(1).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(forventetDagsatsForAndrePeriode.get(0));
        assertThat(andelerForAT2.get(1).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(forventetDagsatsForAndrePeriode.get(1));

        List<Integer> forventetDagsatsForTredjePeriode = regnUtForventetDagsatsForPeriode(
                List.of(månedsinntektFar1, månedsinntektFar2),
                List.of(40, 0), List.of(true, false));
        assertThat(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().get(2).getDagsats())
                .as("Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel")
                .isEqualTo(sumOfList(forventetDagsatsForTredjePeriode));
        assertThat(andelerForAT1.get(2).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(forventetDagsatsForTredjePeriode.get(0));
        assertThat(andelerForAT2.get(2).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(forventetDagsatsForTredjePeriode.get(1));
    }

    @Test
    @DisplayName("7: Far har AAP og søker overføring av gjennværende mødrekvoten fordi mor er syk.")
    @Description("Mor har løpende sak hvor hun har søkt om hele mødrekvoten og deler av fellesperioden. Mor blir syk 4" +
            "uker inn i mødrekvoten og far søker om overføring av resten. Far søker ikke overføring av fellesperioden." +
            "Far får innvilget mødrevkoten og mor sin sak blir berørt og automatisk revurdert.")
    void FarTestMorSyk() {
        var familie = new Familie("562");

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFarOrdinær = fødselsdato.plusWeeks(23);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(familie,
                fødselsdato, fpStartdatoMor, fpStartdatoFarOrdinær);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus("IKKE_HOY");
        saksbehandler.ventTilAvsluttetBehandling();

        /*
         * FAR: Søker overføring av mødrekvoten fordi mor er syk innenfor de 6 første
         * uker av mødrekvoten.
         */
        var far = familie.far();
        var fpStartdatoFarEndret = fødselsdato.plusWeeks(4);
        var fordelingFar = generiskFordeling(
                overføringsperiode(Overføringsårsak.SYKDOM_ANNEN_FORELDER, MØDREKVOTE, fpStartdatoFarEndret,
                        fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fpStartdatoFarOrdinær, fpStartdatoFarOrdinær.plusWeeks(15).minusDays(1)));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.mor().fødselsnummer()))
                .medMottatdato(fødselsdato.plusWeeks(6));
        var saksnummerFar = far.søk(søknadFar.build());

        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaUttakPerioder = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class);
        avklarFaktaUttakPerioder.godkjennPeriode(fpStartdatoFarEndret, fødselsdato.plusWeeks(15).minusDays(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakPerioder);

        var beregningAktivitetStatus = saksbehandler.hentUnikeBeregningAktivitetStatus();
        assertThat(beregningAktivitetStatus)
                .as("Forventer at beregningsstatusen er APP!")
                .contains(AktivitetStatus.ARBEIDSAVKLARINGSPENGER);
        assertThat(beregningAktivitetStatus.size())
                .as("Antall perioder med aktivitetsstatus lik AAP")
                .isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getRedusertPrAar())
                .as("Forventer at beregningsgrunnlaget baserer seg på en årsinntekt større enn 0. Søker har bare AAP.")
                .isPositive();

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        /* Mor: berørt sak */
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        // Løser AP 5084 negativ simulering! Oppretter tilbakekreving og sjekk at den er opprette. Ikke løs det.
        if (forventerNegativSimuleringForBehandling(fpStartdatoFarEndret)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler
                    .hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
            vurderTilbakekrevingVedNegativSimulering.setTilbakekrevingUtenVarsel();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        }


        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Foreldrepenger skal være endret pga annenpart har overlappende uttak!")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder().size())
                .as("Forventer at det er 2 avslåtte uttaksperioder")
                .isEqualTo(2);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(2).getPeriodeResultatÅrsak())
                .as("Perioden burde være avslått fordi annenpart har overlappende uttak!")
                .isInstanceOf(IkkeOppfyltÅrsak.class);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(3).getPeriodeResultatÅrsak())
                .as("Perioden burde være avslått fordi annenpart har overlappende uttak!")
                .isInstanceOf(IkkeOppfyltÅrsak.class);


        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats())
                .as("Siden perioden er avslått, forventes det 0 i dagsats")
                .isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats())
                .as("Siden perioden er avslått, forventes det 0 i dagsats")
                .isZero();

        if (forventerNegativSimuleringForBehandling(fpStartdatoFarEndret)) {
            tbksaksbehandler.hentSisteBehandling(saksnummerMor);
            tbksaksbehandler.ventTilBehandlingErPåVent();
            assertThat(tbksaksbehandler.valgtBehandling.venteArsakKode)
                    .as("Behandling har feil vent årsak")
                    .isEqualTo("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG");
        }
    }

    // Hvis perioden som overføres er IKKE i samme måned som dagens dato ELLER
    // Hvis perioden som overføres er i samme måned som dagens dato OG dagens dato er ETTER utbetalingsdagen
    // (20. i alle måneder) så skal det resultere i negativ simulering.
    private Boolean forventerNegativSimuleringForBehandling(LocalDate fpStartdatoFarEndret) {
        return fpStartdatoFarEndret.getMonth() != LocalDate.now().getMonth() ||
                (fpStartdatoFarEndret.getMonth() == LocalDate.now().getMonth() && LocalDate.now().getDayOfMonth() >= 20);
    }

    @Test
    @DisplayName("8: Mor har tvillinger og søker om hele utvidelsen.")
    @Description("Mor føder tvillinger og søker om hele mødrekvoten og fellesperioden, inkludert utvidelse. Far søker " +
            "samtidig uttak av fellesperioden fra da mor starter utvidelsen av fellesperioden. Søker deretter samtidig " +
            "av fedrekvoten, frem til mor er ferdig med fellesperioden, og deretter søker resten av fedrekvoten.")
    void MorSøkerFor2BarnHvorHunFårBerørtSakPgaFar() {
        var familie = new Familie("512");

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var mor = familie.mor();
        var identMor = mor.fødselsnummer();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(31);
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fpStartdatoFar.minusDays(1)),
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(17).minusDays(1), true, false));
        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordelingMor)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.fødsel(2, fødselsdato))
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far().fødselsnummer()))
                .medMottatdato(fpStartdatoMor.minusWeeks(3));
        var saksnummerMor = mor.søk(søknadMor.build());

        var arbeidsgiverMor = mor.arbeidsgiver();
        arbeidsgiverMor.sendDefaultInntektsmeldingerFP(saksnummerMor, fpStartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        var saldoerFørstgangsbehandling = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoerFørstgangsbehandling.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo())
                .as("Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoerFørstgangsbehandling.getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo())
                .as("Saldoen for stønadskonton MØDREKVOTE")
                .isZero();
        assertThat(saldoerFørstgangsbehandling.getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo())
                .as("Saldoen for stønadskonton FELLESPERIODE")
                .isZero();
        assertThat(saldoerFørstgangsbehandling.getStonadskontoer().get(Stønadskonto.FLERBARNSDAGER).getSaldo())
                .as("Saldoen for stønadskonton FLERBARNSDAGER")
                .isZero();

        /*
         * FAR: Søker samtidig uttak med flerbansdager. Søker deretter hele fedrekvoten,
         * også samtidig uttak.
         */
        var far = familie.far();
        var næringsinntekt = far.næringsinntekt(2018);
        var opptjeningFar = medEgenNaeringOpptjening(
                LocalDate.now().minusYears(4),
                fpStartdatoFar,
                false,
                næringsinntekt,
                false);
        var fordelingFar = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(4).minusDays(1),
                        true, true),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(4), fpStartdatoFar.plusWeeks(17).minusDays(1),
                        false,true),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(17), fpStartdatoFar.plusWeeks(19).minusDays(1)));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordelingFar)
                .medOpptjening(opptjeningFar)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.fødsel(2, fødselsdato))
                .medAnnenForelder(lagNorskAnnenforeldre(identMor));
        var saksnummerFar = far.søk(søknadFar.build());

        var arbeidsgiverFar = far.arbeidsgiver();
        arbeidsgiverFar.sendDefaultInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0))
                .as("Forventer at far får kombinert satus i beregning (da AT og SN)")
                .isEqualTo(AktivitetStatus.KOMBINERT_AT_SN);

        var beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPeriode(0);
        var dagsats = beregningsgrunnlagPeriode.getDagsats();
        var redusertPrAar = beregningsgrunnlagPeriode.getRedusertPrAar();
        var prosentfaktorAvDagsatsTilAF = beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndel().get(0)
                .getRedusertPrAar() / redusertPrAar;
        var dagsatsTilAF = (int) Math.round(dagsats * prosentfaktorAvDagsatsTilAF);

        var perioderMedAndelIArbeidsforhold = saksbehandler
                .hentBeregningsresultatPerioderMedAndelIArbeidsforhold(arbeidsgiverFar.orgnummer());
        assertThat(perioderMedAndelIArbeidsforhold.get(0).getTilSoker())
                .as("Forventer at dagsatsen for arbeidsforholdet blir beregnet først – rest går til søker for SN")
                .isEqualTo(dagsatsTilAF);
        assertThat(perioderMedAndelIArbeidsforhold.get(1).getTilSoker())
                .as("Forventer at dagsatsen for arbeidsforholdet blir beregnet først – rest går til søker for SN")
                .isEqualTo(dagsatsTilAF);
        assertThat(perioderMedAndelIArbeidsforhold.get(2).getTilSoker())
                .as("Forventer at dagsatsen for arbeidsforholdet blir beregnet først – rest går til søker for SN")
                .isEqualTo(dagsatsTilAF);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(arbeidsgiverFar.orgnummer(), 0))
                .as("Foventer at hele den utbetalte dagsatsen går til søker!")
                .isTrue();

        var perioderMedAndelISN = saksbehandler.hentBeregningsresultatPerioderMedAndelISN();
        assertThat(perioderMedAndelISN.get(0).getTilSoker())
                .as("Forventer at resten av dagsatsen går til søker for SN")
                .isEqualTo(dagsats - dagsatsTilAF);
        assertThat(perioderMedAndelISN.get(1).getTilSoker())
                .as("Forventer at resten av dagsatsen går til søker for SN")
                .isEqualTo(dagsats - dagsatsTilAF);
        assertThat(perioderMedAndelISN.get(2).getTilSoker())
                .as("Forventer at resten av dagsatsen går til søker for SN")
                .isEqualTo(dagsats - dagsatsTilAF);

        /* Mor: Berørt sak */
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.innvilgPeriode(
                fpStartdatoFar,
                fpStartdatoFar.plusWeeks(4).minusDays(1),
                InnvilgetÅrsak.FORELDREPENGER_REDUSERT_GRAD_PGA_SAMTIDIG_UTTAK,
                true,
                true,
                100);
        fastsettUttaksperioderManueltBekreftelse.splitPeriode(
                fpStartdatoFar.plusWeeks(4),
                fpStartdatoFar.plusWeeks(17).minusDays(1),
                fpStartdatoFar.plusWeeks(13).minusDays(1));
        fastsettUttaksperioderManueltBekreftelse.innvilgPeriode(
                fpStartdatoFar.plusWeeks(4),
                fpStartdatoFar.plusWeeks(13).minusDays(1),
                InnvilgetÅrsak.FORELDREPENGER_REDUSERT_GRAD_PGA_SAMTIDIG_UTTAK,
                true,
                true,
                100);
        fastsettUttaksperioderManueltBekreftelse.avslåPeriode(
                fpStartdatoFar.plusWeeks(13),
                fpStartdatoFar.plusWeeks(17).minusDays(1),
                IkkeOppfyltÅrsak.IKKE_STØNADSDAGER_IGJEN);
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerMor, true);

        var saldoerBerørtSak = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoerBerørtSak.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo())
                .as("Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoerBerørtSak.getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo())
                .as("Saldoen for stønadskonton MØDREKVOTE")
                .isZero();
        assertThat(saldoerBerørtSak.getStonadskontoer().get(Stønadskonto.FEDREKVOTE).getSaldo())
                .as("Saldoen for stønadskonton FEDREKVOTE")
                .isZero();
        assertThat(saldoerBerørtSak.getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo())
                .as("Saldoen for stønadskonton FELLESPERIODE")
                .isZero();
        assertThat(saldoerBerørtSak.getStonadskontoer().get(Stønadskonto.FLERBARNSDAGER).getSaldo())
                .as("Saldoen for stønadskonton FLERBARNSDAGER")
                .isZero();

        assertThat(saksbehandler.hentAvslåtteUttaksperioder().size())
                .as("Avslåtte uttaksperioder")
                .isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(6).getPeriodeResultatÅrsak())
                .as("Perioden burde være avslått fordi det er ingen stønadsdager igjen på stønadskontoen")
                .isInstanceOf(IkkeOppfyltÅrsak.class);
        assertThat(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().get(6).getDagsats())
                .as("Siden perioden er avslått, forventes det 0 i dagsats i tilkjent ytelse")
                .isZero();
    }

    @Test
    @DisplayName("9: Mor søker med dagpenger som grunnlag, besteberegnes automatisk")
    @Description("Mor søker med dagpenger som grunnlag. Kvalifiserer til automatisk besteberegning." +
            "Beregning etter etter §14-7, 3. ledd gir høyere inntekt enn beregning etter §14-7, 1. ledd")
    void MorSøkerMedDagpengerTest() {
        var familie = new Familie("521");
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(31).minusDays(1)));

        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far().fødselsnummer()));
        //TODO: Fjern denne etter debugging er ferdig.
        var saksnummerMor = mor.søk(søknadMor.build());

        saksbehandler.hentFagsak(saksnummerMor);
        assertThat(saksbehandler.sjekkOmDetErOpptjeningFremTilSkjæringstidspunktet("DAGPENGER"))
                .as("Forventer at det er registert en opptjeningsaktivitet med aktivitettype DAGPENGER med " +
                        "opptjening frem til skjæringstidspunktet for opptjening.")
                .isTrue();
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        assertThat(saksbehandler.harHistorikkinnslagForBehandling(HistorikkinnslagType.BREV_BESTILT))
                .as("Brev er bestillt i førstegangsbehandling")
                .isTrue();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();
    }

    @Test
    @DisplayName("10: Far søker adopsjon og får revurdert sak 4 måneder senere på grunn av IM med endring i refusjon.")
    @Description("Far søker adopsjon og får revurdert sak 4 måneder senere på grunn av IM med endring i refusjon. " +
            "Mens behandlingen er hos beslutter sender AG en ny korrigert IM. Behandlingen rulles tilbake. På den " +
            "siste IM som AG sender ber AG om full refusjon, men kommer for sent til å få alt. AG får refusjon for" +
            "den inneværende måneden og tre måneder tilbake i tid; tiden før dette skal gå til søker.")
    void FarSøkerAdopsjon() {
        var familie = new Familie("563");

        /* FAR */
        var far = familie.far();
        var omsorgsovertakelsedatoe = LocalDate.now().minusMonths(4);
        var fpStartdatoFar = omsorgsovertakelsedatoe;
        var fpSluttdatoFar = fpStartdatoFar.plusWeeks(46).minusDays(1);
        var fordelingFar = generiskFordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar, fpSluttdatoFar));
        var søknadFar = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, BrukerRolle.FAR, false)
                .medFordeling(fordelingFar)
                .medAnnenForelder(new UkjentForelder())
                .medMottatdato(fpStartdatoFar.minusWeeks(3));
        var saksnummerFar = far.søk(søknadFar.build());


        var arbeidsgiver = far.arbeidsgiver();
        arbeidsgiver.sendDefaultInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBegrunnelse("Adopsjon behandlet av Autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);

        var avklarFaktaAleneomsorgBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAleneomsorgBekreftelse.class)
                .bekreftBrukerHarAleneomsorg()
                .setBegrunnelse("Bekreftelse sendt fra Autotest.");
        saksbehandler.bekreftAksjonspunktbekreftelserer(avklarFaktaAleneomsorgBekreftelse);

        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        assertThat(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().size())
                .as("Forventer at det er to perioder i tilkjent ytelse. En for fedrekvote og en for fellesperioden")
                .isEqualTo(1);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();

        // AG sender inn en IM med endring i refusjon som skal føre til revurdering på far sin sak.
        var inntektsmeldingEndringFar = arbeidsgiver.lagInntektsmeldingFP(fpStartdatoFar)
                .medRefusjonsBelopPerMnd(50);
        arbeidsgiver.sendInntektsmeldinger(saksnummerFar, inntektsmeldingEndringFar);

        // Revurdering / Berørt sak til far
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse
                .leggTilRefusjonGyldighetVurdering(arbeidsgiver.orgnummer(), false)
                .setBegrunnelse("Refusjonskrav er sendt inn for sent og skal ikke tas med i beregning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        var vurderRefusjonBeregningsgrunnlagBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderRefusjonBeregningsgrunnlagBekreftelse.class);
        vurderRefusjonBeregningsgrunnlagBekreftelse
                .setFastsattRefusjonFomForAllePerioder(LocalDate.now().minusMonths(3))
                .setBegrunnelse("Fordi autotest sier det!");
        saksbehandler.bekreftAksjonspunkt(vurderRefusjonBeregningsgrunnlagBekreftelse);

        var vurderTilbakekrevingVedNegativSimulering = saksbehandler
                .hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
        vurderTilbakekrevingVedNegativSimulering.setTilbakekrevingUtenVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // AG sender inn ny korrigert IM med endring i refusjon mens behandlingen er hos beslutter. Behandlingen skal
        // rulles tilbake og behandles på nytt fra første AP i revurderingen.
        var inntektsmeldingEndringFar2 = arbeidsgiver.lagInntektsmeldingFP(fpStartdatoFar)
                .medRefusjonsBelopPerMnd(100);
        arbeidsgiver.sendInntektsmeldinger(saksnummerFar, inntektsmeldingEndringFar2);

        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.behandlinger.size())
                .as("Antall behandlinger")
                .isEqualTo(2);
        saksbehandler.ventTilHistorikkinnslag(HistorikkinnslagType.SPOLT_TILBAKE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var vurderFaktaOmBeregningBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse2
                .leggTilRefusjonGyldighetVurdering(arbeidsgiver.orgnummer(), false)
                .setBegrunnelse("Refusjonskrav er sendt inn for sent og skal ikke tas med i beregning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse2);

        var vurderRefusjonBeregningsgrunnlagBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderRefusjonBeregningsgrunnlagBekreftelse.class);
        vurderRefusjonBeregningsgrunnlagBekreftelse2
                .setFastsattRefusjonFomForAllePerioder(LocalDate.now().minusMonths(3))
                .setBegrunnelse("Fordi autotest sier det!");
        saksbehandler.bekreftAksjonspunkt(vurderRefusjonBeregningsgrunnlagBekreftelse2);

        var vurderTilbakekrevingVedNegativSimulering2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
        vurderTilbakekrevingVedNegativSimulering2.setTilbakekrevingUtenVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering2);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, true);

        var perioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        if (LocalDate.now().getDayOfMonth() == 1) {
            assertThat(perioder.size())
                    .as("Berørt behandlings tilkjent ytelse perioder")
                    .isEqualTo(2);
            assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(0), 0))
                    .as("Forventer at hele summen utbetales til søker i første periode, og derfor ingenting til arbeidsgiver!")
                    .isTrue();
            assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(1), 100))
                    .as("Forventer at hele summen utbetales til AG i andre periode, og derfor ingenting til søker!")
                    .isTrue();

        } else {
            assertThat(perioder.size())
                    .as("Berørt behandlings tilkjent ytelse perioder")
                    .isEqualTo(3);
            assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(0), 0))
                    .as("Forventer at hele summen utbetales til søker i første periode, og derfor ingenting til arbeidsgiver!")
                    .isTrue();
            assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(1), 0))
                    .as("Forventer at hele summen utbetales til søker i første periode, og derfor ingenting til arbeidsgiver!")
                    .isTrue();
            assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(2), 100))
                    .as("Forventer at hele summen utbetales til AG i tredje periode, og derfor ingenting til søker!")
                    .isTrue();
        }
    }

    @Test
    @DisplayName("11: Far søker adopsjon hvor han søker hele fedrekvoten og fellesperiode, og får berørt sak pga mor")
    @Description("Far søker adopsjon hvor han søker hele fedrekvoten og fellesperioden. Mor søker noe av mødrekvoten midt " +
            "i fars periode med fullt uttak. Deretter søker mor 9 uker av fellesperioden med samtidig uttak. Far får " +
            "berørt sak hvor han får avkortet fellesperidoen på slutten og redusert perioder hvor mor søker samtidig uttak")
    void FarSøkerAdopsjonOgMorMødrekvoteMidtIFarsOgDeretterSamtidigUttakAvFellesperidoe() {
        var familie = new Familie("563");

        /* FAR */
        var far = familie.far();
        var identFar = far.fødselsnummer();
        var identMor = familie.mor().fødselsnummer();
        var omsorgsovertakelsedatoe = LocalDate.now().minusWeeks(4);
        var fpStartdatoFar = omsorgsovertakelsedatoe;
        var fellesperiodeStartFar = fpStartdatoFar.plusWeeks(15);
        var fellesperiodeSluttFar = fellesperiodeStartFar.plusWeeks(16).minusDays(1);
        var fordelingFar = generiskFordeling(
                uttaksperiode(FEDREKVOTE, fpStartdatoFar, fellesperiodeStartFar.minusDays(1)),
                uttaksperiode(FELLESPERIODE, fellesperiodeStartFar, fellesperiodeSluttFar));
        var søknadFar = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, BrukerRolle.FAR, false)
                .medFordeling(fordelingFar)
                .medAnnenForelder(lagNorskAnnenforeldre(identMor));
        var saksnummerFar = far.søk(søknadFar.build());

        var arbeidsgiverFar = far.arbeidsgiver();
        arbeidsgiverFar.sendDefaultInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class)
                .setBegrunnelse("Adopsjon behandlet av Autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);

        var avklarFaktaAnnenForeldreHarRett = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAnnenForeldreHarRett.class)
                .setAnnenforelderHarRett(true)
                .setBegrunnelse("Både far og mor har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerAktivitetskravBekreftelse.class);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().size())
                .as("Beregningsresultatforeldrepenge perioder")
                .isEqualTo(2);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();

        /* MOR */
        var mor = familie.mor();
        var fpStartdatoMor = fpStartdatoFar.plusWeeks(7);
        var fellesperiodeStartMor = fpStartdatoMor.plusWeeks(4);
        var fellesperiodeSluttMor = fellesperiodeStartMor.plusWeeks(9).minusDays(1);
        var fordelingMor = generiskFordeling(
                uttaksperiode(MØDREKVOTE, fpStartdatoMor, fellesperiodeStartMor.minusDays(1), false, false),
                uttaksperiode(FELLESPERIODE, fellesperiodeStartMor, fellesperiodeSluttMor, false, true, 40));
        var søknadMor = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, BrukerRolle.MOR, false)
                .medFordeling(fordelingMor)
                .medAnnenForelder(lagNorskAnnenforeldre(identFar));
        var saksnummerMor = mor.søk(søknadMor.build());
        var arbeidsgiverMor = mor.arbeidsgiver();
        arbeidsgiverMor.sendDefaultInntektsmeldingerFP(saksnummerMor, fpStartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseMor = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        avklarFaktaAdopsjonsdokumentasjonBekreftelseMor.setBegrunnelse("Adopsjon behandlet av Autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseMor);

        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        /* FAR: Berørt behandling */
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var avslåttePerioder = saksbehandler.hentAvslåtteUttaksperioder();
        assertThat(avslåttePerioder.size())
                .as("Avslåtte uttaksperioder")
                .isEqualTo(1);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(1).getPeriodeResultatÅrsak())
                .as("Perioden burde være avslått fordi annenpart tar ut mødrekovte med 100% utbetalingsgrad samtidig")
                .isEqualTo(IkkeOppfyltÅrsak.DEN_ANDRE_PART_OVERLAPPENDE_UTTAK_IKKE_SØKT_INNVILGET_SAMTIDIG_UTTAK);

        var fastsettUttaksperioderManueltBekreftelseMor = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelseMor.innvilgPeriode(
                fellesperiodeStartMor,
                fellesperiodeStartFar.minusDays(1),
                InnvilgetÅrsak.FORELDREPENGER_REDUSERT_GRAD_PGA_SAMTIDIG_UTTAK,
                false,
                true,
                100);
        fastsettUttaksperioderManueltBekreftelseMor.innvilgPeriode(
                fellesperiodeStartFar,
                fellesperiodeSluttMor,
                InnvilgetÅrsak.FORELDREPENGER_REDUSERT_GRAD_PGA_SAMTIDIG_UTTAK,
                false,
                true,
                60);

        var fomSistePeriode = fastsettUttaksperioderManueltBekreftelseMor.getPerioder()
                .stream().sorted(Comparator.comparing(UttakResultatPeriode::getFom))
                .collect(Collectors.toList())
                .get(fastsettUttaksperioderManueltBekreftelseMor.getPerioder().size() - 1).getFom();
        fastsettUttaksperioderManueltBekreftelseMor.innvilgPeriode(
                fellesperiodeSluttMor.plusDays(1),
                fomSistePeriode.minusDays(1),
                InnvilgetÅrsak.FELLESPERIODE_ELLER_FORELDREPENGER);
        var saldoer = saksbehandler
                .hentSaldoerGittUttaksperioder(fastsettUttaksperioderManueltBekreftelseMor.getPerioder());
        var disponibleFellesdager = saldoer.getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo();
        var sisteDagMedFellesperiode = Virkedager.plusVirkedager(fomSistePeriode.plusDays(1), Math.abs(disponibleFellesdager));
        fastsettUttaksperioderManueltBekreftelseMor.splitPeriode(
                fomSistePeriode,
                fellesperiodeSluttFar, sisteDagMedFellesperiode);
        fastsettUttaksperioderManueltBekreftelseMor.innvilgPeriode(
                fomSistePeriode,
                sisteDagMedFellesperiode,
                InnvilgetÅrsak.FELLESPERIODE_ELLER_FORELDREPENGER);
        fastsettUttaksperioderManueltBekreftelseMor.avslåPeriode(
                sisteDagMedFellesperiode.plusDays(1),
                fellesperiodeSluttFar,
                IkkeOppfyltÅrsak.IKKE_STØNADSDAGER_IGJEN);
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelseMor);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, true);

        beslutter.ventTilFagsakLøpende();

        // verifisering i uttak
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo())
                .as("Saldoen for stønadskonton FELLESPERIODE")
                .isZero();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder().size())
                .as("Avslåtte uttaksperioder")
                .isEqualTo(2);

        // verifisering i tilkjent ytelse
        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(1).getDagsats())
                .as("Tilkjent ytelses periode 2")
                .isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats())
                .as("Forventer at dagsatsen blir redusert fra 100% til 60% for periode 3 i tilkjent ytelse")
                .isEqualTo((int) Math.round(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats() * 0.6));
        assertThat(tilkjentYtelsePerioder.getPerioder().get(6).getDagsats())
                .as("Tilkjent ytelses periode 7")
                .isZero();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();
    }

    @Test
    @DisplayName("12: Mor søker fødsel og mottar sykepenger uten inntektskilder, får avslag, klager og får medhold.")
    @Description("12: Mor søker fødsel og mottar sykepenger som er over 1/2 G. Har ingen inntektskilder. Saksbehandler" +
            "skriver inn sykepengebeløp som er under 1/2 G som vil før til at søker ikke har rett på foreldrepenger." +
            "Søker får avslag, klager og får medhold. Saksbehandler legger inn korrekt beløp som er over 1/2G og søker" +
            "får innvilget foreldrepenger.")
    void morSøkerFødselMottarForLite() {
        var familie = new Familie("70");
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far().fødselsnummer()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        var avklarArbeidsforholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        assertThat(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"))
                .as("Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som " +
                        "er forut for permisjonen på skjæringstidspunktet!")
                .isTrue();

        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilAndelerYtelse(4000.0, Inntektskategori.ARBEIDSTAKER);
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);

        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getRedusertPrAar())
                .as("Forventer at beregningsgrunnlaget baserer seg på et grunnlag som er mindre enn 1/2 G")
                .isLessThan(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getHalvG());
        assertThat(saksbehandler.vilkårStatus(VilkarTypeKoder.BEREGNINGSGRUNNLAGVILKÅR).kode)
                .as("Vilkårstatus for beregningsgrunnlag")
                .isEqualTo("IKKE_OPPFYLT");
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);

        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();

        var klageFormkravNfp = klagebehandler
                .hentAksjonspunktbekreftelse(KlageFormkravNfp.class)
                .godkjennAlleFormkrav()
                .setBegrunnelse("Super duper klage!");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        var vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(VurderingAvKlageBekreftelse.VurderingAvKlageNfpBekreftelse.class)
                .bekreftMedholdGunst("PROSESSUELL_FEIL")
                .fritekstBrev("Fritektst til brev fra klagebehandler.")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);


        klagebehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        // Saksbehandler oppretter ny revudering manuelt etter søker har fått medhold i klage.
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_KLAGE_MED_END_INNTEKT);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgSisteBehandling();

        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().get(0).getBehandlingArsakType())
                .as("Behandlingsårsakstype")
                .isEqualTo(BehandlingÅrsakType.RE_KLAGE_MED_END_INNTEKT);

        var avklarArbeidsforholdBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse2);

        assertThat(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"))
                .as("Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som " +
                        "er forut for permisjonen på skjæringstidspunktet!")
                .isTrue();

        var vurderFaktaOmBeregningBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilAndelerYtelse(10_000.0, Inntektskategori.ARBEIDSTAKER);
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse2);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerRealitetsbehandlingEllerKlage.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerManueltOpprettetRevurdering.class);
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true);


        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen())
                .as("Konsekvens for ytelse")
                .contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING, KonsekvensForYtelsen.ENDRING_I_UTTAK);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

    }


    @Test
    @DisplayName("13: Mor søker på termin og får innvilget, men etter termin mottas det en dødfødselshendelse")
    @Description("13: Mør søker på termin og blir automatisk behanldet (innvilget). En uke etter terminen mottas det" +
            "en dødfødselshendelse hvor mor får avslag etter det 6 uken av mødrekvoten.")
    void morSøkerTerminFårInnvilgetOgSåKommerDetEnDødfødselEtterTermin() {
        var familie = new Familie("55");
        var mor = familie.mor();
        var søkerIdent = mor.fødselsnummer();
        var termindato = LocalDate.now().minusWeeks(2);
        var fpStartdatoMor = termindato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far().fødselsnummer()))
                .medMottatdato(termindato.minusMonths(2));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendDefaultInntektsmeldingerFP(saksnummer, fpStartdatoMor);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo())
                .as("Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo())
                .as("Saldoen for stønadskonton MØDREKVOTE")
                .isZero();
        assertThat(saldoer.getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo())
                .as("Saldoen for stønadskonton FELLESPERIODE")
                .isZero();

        var differanseFødselTermin = 7;
        var dødfødselshendelseDto = new DødfødselhendelseDto("OPPRETTET", null, søkerIdent.fnr(),
                termindato.plusDays(differanseFødselTermin));
        innsender.opprettHendelsePåKafka(dødfødselshendelseDto);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().get(0).getBehandlingArsakType())
                .as("Behandlingsårsak revurdering")
                .isEqualTo(BehandlingÅrsakType.RE_HENDELSE_DØDFØDSEL);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse
                .avslåManuellePerioderMedPeriodeResultatÅrsak(IkkeOppfyltÅrsak.BARNET_ER_DØD);
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(FastsetteUttakKontrollerOpplysningerOmDødDto.class);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true);

        var saldoerRevurdering = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoerRevurdering.getStonadskontoer().get(Stønadskonto.FORELDREPENGER_FØR_FØDSEL).getSaldo())
                .as("Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoerRevurdering.getStonadskontoer().get(Stønadskonto.MØDREKVOTE).getSaldo())
                .as("Saldoen for stønadskonton MØDREKVOTE")
                .isEqualTo(45);
        assertThat(saldoerRevurdering.getStonadskontoer().get(Stønadskonto.FELLESPERIODE).getSaldo())
                .as("Saldoen for stønadskonton FELLESPERIODE")
                .isEqualTo(75);

        var uttakResultatPerioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        var uttaksperiode_0 = uttakResultatPerioder.get(0);
        var uttaksperiode_1 = uttakResultatPerioder.get(1);
        assertThat(uttaksperiode_0.getPeriodeResultatType())
                .as("Uttaksresultattype for første periode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttaksperiode_0.getPeriodeType().kode)
                .as("Forventer at første periode er FELLESPERIODE pga dødfødsel etter termin")
                .isEqualTo(FELLESPERIODE.name());
        assertThat(uttaksperiode_0.getFom())
                .as("Verifiserer at antall dager etter termin fylles med fellesperioden")
                .isEqualTo(uttaksperiode_1.getFom().minusDays(differanseFødselTermin));

        assertThat(uttaksperiode_1.getPeriodeResultatType())
                .as("Uttaksresultattype for andre periode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttaksperiode_1.getPeriodeType().kode)
                .as("Forventer at første periode er FORELDREPENGER_FØR_FØDSEL pga dødfødsel etter termin")
                .isEqualTo(FORELDREPENGER_FØR_FØDSEL.name());
        assertThat(uttaksperiode_1.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Verifiser at søker tar ut hele FORELDREPENGER_FØR_FØDSEL kvoten")
                .isEqualByComparingTo(BigDecimal.valueOf(3 * 5));

        var uttaksperiode_2 = uttakResultatPerioder.get(2);
        assertThat(uttaksperiode_2.getPeriodeResultatType())
                .as("Uttaksresultattype for tredje periode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttaksperiode_2.getPeriodeType().kode)
                .as("Forventer at første periode er MØDREKVTOEN pga dødfødsel etter termin")
                .isEqualTo(MØDREKVOTE.name());
        assertThat(uttaksperiode_2.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Forventer at det tas ut 6 uker av den gjenværende delen av stønadsperioden")
                .isEqualByComparingTo(BigDecimal.valueOf(6 * 5));

        assertThat(saksbehandler.hentAvslåtteUttaksperioder().size())
                .as("Avslåtte uttaksperioder pga dødfødsel")
                .isEqualTo(2);
        assertThat(uttakResultatPerioder.get(3).getPeriodeResultatÅrsak())
                .as("Perioden burde være avslått fordi det er mottatt dødfødselshendelse")
                .isInstanceOf(IkkeOppfyltÅrsak.class);
        assertThat(uttakResultatPerioder.get(4).getPeriodeResultatÅrsak())
                .as("Perioden burde være avslått fordi det er mottatt dødfødselshendelse")
                .isInstanceOf(IkkeOppfyltÅrsak.class);
    }


    private Long sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(Familie familie,
                                                                                        LocalDate fødselsdato,
                                                                                        LocalDate fpStartdatoMor,
                                                                                        LocalDate fpStartdatoFar) {
        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var mor = familie.mor();
        var identMor = mor.fødselsnummer();
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fpStartdatoFar.minusDays(1)));
        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far().fødselsnummer()))
                .medFordeling(fordelingMor)
                .medMottatdato(fpStartdatoMor.minusWeeks(4));
        var saksnummerMor = mor.søk(søknadMor.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendDefaultInntektsmeldingerFP(saksnummerMor, fpStartdatoMor);

        return saksnummerMor;
    }

    private NorskForelder lagNorskAnnenforeldre(Fødselsnummer indent) {
        return new NorskForelder(indent, "");
    }

}
