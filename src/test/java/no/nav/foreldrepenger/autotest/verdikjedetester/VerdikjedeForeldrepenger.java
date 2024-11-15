package no.nav.foreldrepenger.autotest.verdikjedetester;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.REBEREGN_FERIEPENGER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_HENDELSE_DØD_FORELDER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_HENDELSE_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak.AKTIVITETSKRAVET_UTDANNING_IKKE_DOKUMENTERT;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak.IKKE_STØNADSDAGER_IGJEN;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder.VURDER_FEILUTBETALING_KODE;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType.MINSTERETT;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet.ARBEID;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet.IKKE_OPPGITT;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet.UTDANNING;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Overføringsårsak.SYKDOM_ANNEN_FORELDER;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.MØDREKVOTE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak.FRI;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEndringMaler.lagEndringssøknad;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerAdopsjon;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperiodeType.FLERBARNSDAGER;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperiodeType.SAMTIDIGUTTAK;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.overføringsperiode;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.utsettelsesperiode;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil.helgejustertTilMandag;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fptilbake.TilbakekrevingSaksbehandler;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.KonsekvensForYtelsen;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsetteUttakKontrollerOpplysningerOmDødDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KlageFormkravNfp;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerRealitetsbehandlingEllerKlage;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderRefusjonBeregningsgrunnlagBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedNegativSimulering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderVarigEndringEllerNyoppstartetSNBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageNfpBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAdopsjonsdokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAleneomsorgBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAnnenForeldreHarRett;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.KontrollerBesteberegningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderUttakDokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrUttaksperioder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.VilkarTypeKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.feriepenger.Feriepengeandel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.DekningsgradDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkTyper;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.common.innsyn.Dekningsgrad;
import no.nav.foreldrepenger.generator.familie.Familie;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler;
import no.nav.foreldrepenger.generator.soknad.maler.UttaksperiodeType;
import no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil;
import no.nav.foreldrepenger.kontrakter.risk.kodeverk.RisikoklasseType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.UttaksplanPeriodeDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.AnnenforelderBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.BarnBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.SøkerBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.OpptjeningMaler;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.GrunnlagDto;

;

@Tag("verdikjede")
class VerdikjedeForeldrepenger extends VerdikjedeTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(VerdikjedeForeldrepenger.class);

    @Test
    @DisplayName("1: Mor automatisk førstegangssøknad termin (med fødselshendelse), aleneomsorg og avvik i beregning.")
    @Description("Mor førstegangssøknad før fødsel på termin. Mor har aleneomsorg og enerett. Sender inn IM med over " +
                "25% avvik med delvis refusjon. Etter behandlingen er ferdigbehandlet mottas en fødselshendelse.")
    void testcase_mor_fødsel() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();

        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(1);
        var fpStartdato = termindato.minusWeeks(3);
        var fordeling = List.of(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, termindato.minusDays(1)),
                uttaksperiode(StønadskontoType.FORELDREPENGER, termindato, termindato.plusWeeks(15).minusDays(1)),
                uttaksperiode(StønadskontoType.FORELDREPENGER, termindato.plusWeeks(20), termindato.plusWeeks(36).minusDays(1)));
        var søknad = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medFordeling(fordeling)
                .medSøker(new SøkerBuilder(BrukerRolle.MOR).medErAleneOmOmsorg(true).build())
                .medAnnenForelder(AnnenforelderMaler.norskIkkeRett(familie.far()))
                .medMottattdato(termindato.minusWeeks(5));
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
                .hentAksjonspunktbekreftelse(new VurderBeregnetInntektsAvvikBekreftelse())
                .leggTilInntekt(månedsinntekt * 12, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        var avklarFaktaAleneomsorgBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAleneomsorgBekreftelse())
                .bekreftBrukerHarAleneomsorg()
                .setBegrunnelse("Bekreftelse sendt fra Autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAleneomsorgBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldo for stønadskontoen FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER).saldo())
                .as("Saldoen for stønadskontoen FORELDREPENGER")
                .isEqualTo(75);

        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats())
                .as("Forventer at dagsatsen blir justert ut i fra årsinntekten og utbeatlinsggrad, og IKKE 6G fordi inntekten er under 6G!")
                .isEqualTo(1846);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(60))
                .as("Forventer at 40% summen utbetales til søker og 60% av summen til arbeisdgiver pga 60% refusjon!")
                .isTrue();

        // Fødselshendelse
        familie.sendInnFødselshendelse(termindato.minusWeeks(1));

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().get(0).behandlingArsakType())
                .as("Årsakskode til revuderingen")
                .isEqualTo(RE_HENDELSE_FØDSEL);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);

        // Verifiser riktig justering av kontoer og uttak.
        saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldo for stønadskontoen FORELDREPENGER_FØR_FØDSEL")
                .isEqualTo(5);
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER).saldo())
                .as("Saldo for stønadskontoen FORELDREPENGER")
                .isEqualTo(70);

        var feriepenger = saksbehandler.valgtBehandling.getFeriepengegrunnlag();
        assertThat(feriepenger).isNotNull();
        var feriepengerTilArbeidsgiver = oppsummerFeriepengerForArbeidsgiver(feriepenger.andeler(), arbeidsgiver.arbeidsgiverIdentifikator().value(), false);
        var feriepengerTilSøker = oppsummerFeriepengerForArbeidsgiver(feriepenger.andeler(), arbeidsgiver.arbeidsgiverIdentifikator().value(), true);
        assertFeriepenger(feriepengerTilSøker + feriepengerTilArbeidsgiver, 11297);
    }

    @Test
    @DisplayName("2: Mor selvstendig næringsdrivende, varig endring. Søker dør etter behandlingen er ferdigbehandlet.")
    @Description("Mor er selvstendig næringsdrivende og har ferdiglignet inntekt i mange år. Oppgir en næringsinntekt" +
            "som avviker med mer enn 25% fra de tre siste ferdiglignede årene. Søker dør etter behandlingen er " +
            "ferdigbehandlet. NB: Må legge til ferdiglignet inntekt for inneværende år -1 etter 1/7")
    void morSelvstendigNæringsdrivendeTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .selvstendigNæringsdrivende(200_000)
                                .build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build();

        var fødselsdato = familie.barn().fødselsdato();
        var mor = familie.mor();
        var næringsinntekt = mor.næringsinntekt();
        // Merk: Avviket er G-sensitivt og kan bli påvirket av g-regulering
        var avvikendeNæringsinntekt = næringsinntekt * 1.9; // >25% avvik
        // Legger inn orgnummer fra 510/organisasjon ettersom det ikke finnes arbeidsforhold for organisasjonen
        var orgnummer = familie.far().arbeidsforhold().arbeidsgiverIdentifikasjon().value(); // TODO: Må legge inn gyldig orgnummer. Instansiere AF via far. Legg til støtte for å nstansiere arbeidsforold som ikke er knyttetr til bruker.
        var opptjening = OpptjeningMaler.egenNaeringOpptjening(
                orgnummer,
                mor.næringStartdato(),
                LocalDate.now(),
                false,
                avvikendeNæringsinntekt,
                true);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medSøker(new SøkerBuilder(BrukerRolle.MOR)
                        .medSelvstendigNæringsdrivendeInformasjon(List.of(opptjening))
                        .build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fødselsdato.plusWeeks(2));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var vurderVarigEndringEllerNyoppstartetSNBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderVarigEndringEllerNyoppstartetSNBekreftelse())
                .setErVarigEndretNaering(false)
                .setBegrunnelse("Ingen endring");
        saksbehandler.bekreftAksjonspunkt(vurderVarigEndringEllerNyoppstartetSNBekreftelse);
        var vurderVarigEndringEllerNyoppstartetSNBekreftelse1 = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderVarigEndringEllerNyoppstartetSNBekreftelse())
                .setErVarigEndretNaering(true)
                .setBruttoBeregningsgrunnlag((int)avvikendeNæringsinntekt)
                .setBegrunnelse("Vurder varig endring for selvstendig næringsdrivende begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderVarigEndringEllerNyoppstartetSNBekreftelse1);

        // verifiser skjæringstidspunkt i følge søknad
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getSkjaeringstidspunktBeregning())
                .as("Skæringstidspunkt beregning")
                .isEqualTo(fødselsdato.minusWeeks(3));

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false, false);
        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        assertThat(saksbehandler.hentUnikeBeregningAktivitetStatus())
                .as("Forventer at søker får utbetaling med status SN og bare det!")
                .contains(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)
                .hasSize(1);

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo())
                .as("Saldoen for stønadskonton MØDREKVOTE")
                .isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo())
                .as("Saldoen for stønadskonton FELLESPERIODE")
                .isZero();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();

        var dødsdato = LocalDate.now().minusDays(1);
        familie.sendInnDødshendelse(mor.fødselsnummer(), dødsdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().get(0).behandlingArsakType())
                .as("Behandlingsårsakstype")
                .isEqualTo(RE_HENDELSE_DØD_FORELDER);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new FastsettUttaksperioderManueltBekreftelse())
                .avslåManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new FastsetteUttakKontrollerOpplysningerOmDødDto());

        if (saksbehandler.harAksjonspunkt(VURDER_FEILUTBETALING_KODE)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(new VurderTilbakekrevingVedNegativSimulering())
                    .avventSamordningIngenTilbakekreving();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        }
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true, false);

        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .hasSize(3);

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

        var feriepenger = saksbehandler.valgtBehandling.getFeriepengegrunnlag();
        assertThat(feriepenger).isNull();
    }

    @Test
    @DisplayName("3: Mor, sykepenger, kun ytelse, papirsøknad")
    @Description("Mor søker fullt uttak, men søker mer enn det hun har rett til. Klager på førstegangsbehandlingen og " +
            "vedtaket stadfestes. Søker anker stadfestelsen og saksbehanlder oppretter en ankebehandling. Bruker får " +
            "omgjøring i anke")
    void morSykepengerKunYtelseTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .infotrygd(GrunnlagDto.Ytelse.SP, LocalDate.now().minusMonths(9), LocalDate.now(),
                                        GrunnlagDto.Status.LØPENDE, LocalDate.now().minusDays(2))
                                .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusYears(1), 480_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();

        var mor = familie.mor();
        var saksnummer = mor.søkPapirsøknadForeldrepenger();

        saksbehandler.hentFagsak(saksnummer);
        var termindato = LocalDate.now().plusWeeks(6);
        var fpStartdatoMor = termindato.minusWeeks(3);
        var fpMottatDato = termindato.minusWeeks(6);
        var fordelingDtoMor = new FordelingDto();
        var foreldrepengerFørFødsel = new PermisjonPeriodeDto(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor,
                termindato.minusDays(1));
        var mødrekvote = new PermisjonPeriodeDto(MØDREKVOTE, termindato,
                termindato.plusWeeks(20).minusDays(1));
        fordelingDtoMor.permisjonsPerioder.add(foreldrepengerFørFødsel);
        fordelingDtoMor.permisjonsPerioder.add(mødrekvote);
        var papirSoknadForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new PapirSoknadForeldrepengerBekreftelse())
                .morSøkerTermin(fordelingDtoMor, termindato, fpMottatDato, DekningsgradDto.AATI);
        saksbehandler.bekreftAksjonspunkt(papirSoknadForeldrepengerBekreftelse);

        var avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaTerminBekreftelse())
                .setUtstedtdato(termindato.minusWeeks(10))
                .setBegrunnelse("Begrunnelse fra autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        assertThat(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"))
                .as("Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som " +
                        "er forut for permisjonen på skjæringstidspunktet!")
                .isTrue();

        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse());
        vurderFaktaOmBeregningBekreftelse
                .leggTilAndelerYtelse(10000.0, Inntektskategori.ARBEIDSTAKER)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new FastsettUttaksperioderManueltBekreftelse())
                .avslåManuellePerioderMedPeriodeResultatÅrsak(IKKE_STØNADSDAGER_IGJEN);
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false, false);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();
    }

    @Test
    @DisplayName("4: Far søker resten av fellesperioden og hele fedrekvoten med gradert uttak og opphold mellom disse.")
    @Description("Mor har løpende fagsak med hele mødrekvoten og deler av fellesperioden. Far søker resten av fellesperioden" +
            "og hele fedrekvoten med gradert uttak. Far starter med opphold og har også opphold mellom uttak av" +
            "fellesperioden og fedrekvoten. Far har to arbeidsforhold i samme virksomhet, samme org.nr, men ulik" +
            "arbeidsforholdsID. To inntekstmeldinger sendes inn med refusjon på begge.")
    void farSøkerForeldrepengerTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", 50, LocalDate.now().minusYears(2), 720_000)
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-002", 50, LocalDate.now().minusYears(4), null)
                                .build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(25))
                .build();

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpSluttdatoMor = fødselsdato.plusWeeks(23);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(familie,
                fødselsdato, fpStartdatoMor, fpSluttdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(RisikoklasseType.IKKE_HØY);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        /*
         * FAR: Søker med to arbeidsforhold i samme virksomhet, orgn.nr, men med ulik
         * arbeidsforholdID. Starter med opphold og starter uttak med resten av fellesperiode etter oppholdet.
         * Far har også opphold mellom uttak av fellesperioden og fedrekvoten. Sender inn 2 IM med ulik
         * arbeidsforholdID og refusjon på begge.
         */
        var far = familie.far();
        var fpStartdatoFar = fpSluttdatoMor.plusWeeks(3);
        var orgNummerFar = far.arbeidsgiver().arbeidsgiverIdentifikator();
        var graderingsperiodeFørste = graderingsperiodeArbeidstaker(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(16).minusDays(1), orgNummerFar, 50);
        var graderingsperiodeSiste = graderingsperiodeArbeidstaker(FEDREKVOTE, fpStartdatoFar.plusWeeks(25), fpStartdatoFar.plusWeeks(55).minusDays(1), orgNummerFar, 50);
        var fordelingFar = List.of(
                graderingsperiodeFørste,
                // Opphold på 9 uker
                graderingsperiodeSiste);
        var søknadFar = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medFordeling(fordelingFar);
        var saksnummerFar = far.søk(søknadFar.build());


        var arbeidsgiver = far.arbeidsgiver();
        var inntektsmeldingerFar = arbeidsgiver.lagInntektsmeldingerFP(fpStartdatoFar);
        inntektsmeldingerFar.get(0).medRefusjonsBelopPerMnd(ProsentAndel.valueOf(100));
        inntektsmeldingerFar.get(1).medRefusjonsBelopPerMnd(ProsentAndel.valueOf(100));
        arbeidsgiver.sendInntektsmeldinger(saksnummerFar, inntektsmeldingerFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new VurderUttakDokumentasjonBekreftelse());

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
        var oppslittedePerioderFørstePeriode = overstyringUttak.splitPeriode(
                graderingsperiodeFørste.tidsperiode().fom(),
                graderingsperiodeFørste.tidsperiode().tom(),
                fpStartdatoFar.plusWeeks(8).minusDays(1));
        overstyringUttak.avslåPeriode(
                oppslittedePerioderFørstePeriode.get(0),
                PeriodeResultatÅrsak.AKTIVITETSKRAVET_ARBEID_IKKE_OPPFYLT,
                true);
        overstyringUttak.innvilgPeriode(
                oppslittedePerioderFørstePeriode.get(1),
                PeriodeResultatÅrsak.GRADERING_KVOTE_ELLER_OVERFØRT_KVOTE,
                StønadskontoType.FEDREKVOTE);
        var oppslittedePerioderAndrePeriode = overstyringUttak.splitPeriode(
                graderingsperiodeSiste.tidsperiode().fom(),
                graderingsperiodeSiste.tidsperiode().tom(),
                fpStartdatoFar.plusWeeks(47).minusDays(1));
        overstyringUttak.innvilgPeriode(
                oppslittedePerioderAndrePeriode.get(0),
                PeriodeResultatÅrsak.GRADERING_KVOTE_ELLER_OVERFØRT_KVOTE);
        overstyringUttak.avslåPeriode(
                oppslittedePerioderAndrePeriode.get(1),
                IKKE_STØNADSDAGER_IGJEN,
                false);
        overstyringUttak.setBegrunnelse("Begrunnelse fra Autotest.");
        overstyrer.overstyr(overstyringUttak);

        saksbehandler.velgSisteBehandling();
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo())
                .as("saldoen for stønadskonton MØDREKVOTE")
                .isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FEDREKVOTE).saldo())
                .as("saldoen for stønadskonton FEDREKVOTE")
                .isZero();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(100))
                .as("Forventer at hele summen utbetales til arbeidsgiver, og derfor ingenting til søker!")
                .isTrue();
    }

    @Test
    @DisplayName("5: Far søker fellesperiode og fedrekvote som frilanser. Tar ut 2 uker ifm fødsel.")
    @Description("Mor søker hele mødrekvoten og deler av fellesperiode, happy case. Far søker etter føsdsel og søker" +
            "noe av fellesperioden og hele fedrekvoten; 2 av disse tas ut ifm fødsel. Opplyser at han er frilanser og har frilanserinntekt frem til" +
            "skjæringstidspunktet.")
    void farSøkerSomFrilanserOgTarUt2UkerIfmFødsel() {
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .frilans(LocalDate.now().minusYears(2), 540_000)
                                .build())
                        .build())
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(4))
                .build();

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFellesperiodeFar = fødselsdato.plusWeeks(18);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(familie,
                fødselsdato, fpStartdatoMor, fpStartdatoFellesperiodeFar);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(RisikoklasseType.IKKE_HØY);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        /*
         * FAR: Søker som FL. Har frilansinntekt frem til, men ikke inklusiv,
         * skjæringstidspunktet. Søker noe av fellesperioden og deretter hele
         * fedrekvoten
         */
        var far = familie.far();
        var fordelingFar = List.of(
                uttaksperiode(FEDREKVOTE, fødselsdato, fødselsdato.plusWeeks(2).minusDays(1), SAMTIDIGUTTAK),
                uttaksperiode(FELLESPERIODE, fpStartdatoFellesperiodeFar, fpStartdatoFellesperiodeFar.plusWeeks(4).minusDays(1), ARBEID),
                uttaksperiode(FEDREKVOTE, fpStartdatoFellesperiodeFar.plusWeeks(4), fpStartdatoFellesperiodeFar.plusWeeks(17).minusDays(1)));
        var opptjeningFar = OpptjeningMaler.frilansOpptjening();
        var søknadFar = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medSøker(new SøkerBuilder(BrukerRolle.FAR).medFrilansInformasjon(opptjeningFar).build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medFordeling(fordelingFar)
                .medMottattdato(fødselsdato.minusWeeks(1));
        var saksnummerFar = far.søk(søknadFar.build());

        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.sjekkOmDetErOpptjeningFremTilSkjæringstidspunktet("FRILANS"))
                .as("Forventer at det er registert en opptjeningsaktivitet med aktivitettype FRILANSER som har frilansinntekt på skjæringstidspunktet!")
                .isTrue();
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new VurderUttakDokumentasjonBekreftelse());
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false, false);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();

        saksbehandler.hentFagsak(saksnummerMor);
        assertThat(saksbehandler.harRevurderingBehandling())
                .as("Mor skal ikke få berørt behandling pga samtidig uttak ifm fødsel")
                .isFalse();
    }

    @Test
    @DisplayName("6: Bare Far har rett (BFHR) søker foreldrepenger med AF som ikke er avsluttet. Utsettelse i midten. Gradert uttak ifm fødsel.")
    @Description("Far søker foreldrepenger med to aktive arbeidsforhold og ett gammelt arbeidsforhold som skulle vært " +
            "avsluttet men er ikke det. Far søker gradering i ett av disse AFene med utsettelsesperiode i midten." +
            "I dette arbeidsforholdet gjennopptar han full deltidsstilling og AG vil har full refusjon i hele perioden." +
            "I det andre arbeidsforholdet vil AG bare ha refusjon i to måneder. Søker også gradert uttak ifm fødsel." +
            "Far sender dermed inn endringssøknad og gir fra seg alle periodene.")
    void farSøkerMedToAktiveArbeidsforholdOgEtInaktivtTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NAV, 60, LocalDate.now().minusYears(2), 360_000)
                                .arbeidsforhold(TestOrganisasjoner.NAV_BERGEN, 40, LocalDate.now().minusYears(4), 240_000)
                                .arbeidsforholdUtenInntekt(TestOrganisasjoner.NAV_STORD, LocalDate.now().minusYears(8))
                                .build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(3))
                .build();

        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();
        var arbeidsforhold1 = far.arbeidsforhold(TestOrganisasjoner.NAV.orgnummer().value());
        var orgNummerFar1 = arbeidsforhold1.arbeidsgiverIdentifikasjon();
        var stillingsprosent1 = arbeidsforhold1.stillingsprosent();
        var førsteGradertUttaksPeriodeEtterUke6 =
                graderingsperiodeArbeidstaker(StønadskontoType.FORELDREPENGER,
                        fødselsdato.plusWeeks(6),
                        fødselsdato.plusWeeks(56).minusDays(1),
                        orgNummerFar1,
                        stillingsprosent1);
        var fpStartdatoEtterUke6Far = førsteGradertUttaksPeriodeEtterUke6.tidsperiode().fom();
        var fordelingFar = List.of(
                graderingsperiodeArbeidstaker(StønadskontoType.FORELDREPENGER,
                        fødselsdato.minusWeeks(2),
                        fødselsdato.plusWeeks(2).minusDays(1),
                        orgNummerFar1,
                        50),
                førsteGradertUttaksPeriodeEtterUke6,
                utsettelsesperiode(UtsettelsesÅrsak.FRI,
                        førsteGradertUttaksPeriodeEtterUke6.tidsperiode().tom().plusDays(1),
                        førsteGradertUttaksPeriodeEtterUke6.tidsperiode().tom().plusWeeks(4),
                        UTDANNING),
                graderingsperiodeArbeidstaker(StønadskontoType.FORELDREPENGER,
                        førsteGradertUttaksPeriodeEtterUke6.tidsperiode().tom().plusWeeks(4).plusDays(1),
                        førsteGradertUttaksPeriodeEtterUke6.tidsperiode().tom().plusWeeks(14),
                        orgNummerFar1,
                        stillingsprosent1)
        );
        var fpStartdatoIfmFødselFar = fødselsdato.minusWeeks(2);
        var søknadFar = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(AnnenforelderMaler.norskIkkeRett(familie.mor()))
                .medMottattdato(fødselsdato.minusWeeks(2))
                .build();
        var saksnummerFar = far.søk(søknadFar);

        var arbeidsgiver1 = far.arbeidsgiver(TestOrganisasjoner.NAV.orgnummer().value());
        var inntektsmelding1 = arbeidsgiver1.lagInntektsmeldingFP(fpStartdatoIfmFødselFar)
                .medRefusjonsBelopPerMnd(ProsentAndel.valueOf(100));
        arbeidsgiver1.sendInntektsmeldinger(saksnummerFar, inntektsmelding1);

        var arbeidsgiver2 = far.arbeidsgiver(TestOrganisasjoner.NAV_BERGEN.orgnummer().value());
        var orgNummerFar2 = arbeidsgiver2.arbeidsgiverIdentifikator();
        var opphørsDatoForRefusjon = fpStartdatoEtterUke6Far.plusMonths(2).minusDays(1);
        var inntektsmelding2 = arbeidsgiver2.lagInntektsmeldingFP(fpStartdatoIfmFødselFar)
                .medRefusjonsBelopPerMnd(ProsentAndel.valueOf(100))
                .medRefusjonsOpphordato(opphørsDatoForRefusjon);
        arbeidsgiver2.sendInntektsmeldinger(saksnummerFar, inntektsmelding2);

        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaAnnenForeldreHarRett = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAnnenForeldreHarRett())
                .setAnnenforelderHarRett(false)
                .setBegrunnelse("Bare far har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new VurderUttakDokumentasjonBekreftelse());

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false, false);

        /* VERIFISERINGER */
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // SALDO
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER).saldo())
                .as("Saldoen for stønadskonton FORELDREPENGER")
                .isEqualTo(14 * 5);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(SaldoVisningStønadskontoType.MINSTERETT).saldo())
                .as("Saldoen for stønadskonton MINSTRETT")
                .isEqualTo(10 * 5);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(SaldoVisningStønadskontoType.MINSTERETT).maxDager())
                .as("Maxdager for stønadskonton MINSTRETT")
                .isEqualTo(10 * 5);

        // UTTAK
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .isEmpty();
        // Utsettelse skal være innvilget, riktig årsak og skal ikke trekke dager
        var uttakResultatPeriode = saksbehandler.valgtBehandling.hentUttaksperiode(4);
        assertThat(uttakResultatPeriode.getPeriodeResultatÅrsak())
                .as("Perioderesultatårsak")
                .isEqualTo(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_BFR_AKT_KRAV_OPPFYLT);
        assertThat(uttakResultatPeriode.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Trekkdager")
                .isZero();
        assertThat(uttakResultatPeriode.getAktiviteter().get(1).getTrekkdagerDesimaler())
                .as("Trekkdager")
                .isZero();

        // TILKJENT YTELSE
        var beregningsresultatPerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        assertThat(beregningsresultatPerioder)
                .as("Beregningsresultatperidoer")
                .hasSize(7);

        var andelerForAT1 = saksbehandler.hentBeregningsresultatPerioderMedAndelIArbeidsforhold(orgNummerFar1);
        var andelerForAT2 = saksbehandler.hentBeregningsresultatPerioderMedAndelIArbeidsforhold(orgNummerFar2);
        // IFM fødsel
        assertThat(beregningsresultatPerioder.get(0).getDagsats())
                .as("Dagsatsen for perioden")
                .isEqualTo(1616);
        assertThat(andelerForAT1.get(0).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(462);
        assertThat(andelerForAT2.get(0).getRefusjon())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til arbeidsgiver")
                .isEqualTo(923);

        // Første uttaksperiode etter uke 6
        assertThat(beregningsresultatPerioder.get(2).getDagsats())
                .as("Dagsatsen for perioden")
                .isEqualTo(1477);
        assertThat(andelerForAT1.get(2).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(554);
        assertThat(andelerForAT2.get(2).getRefusjon())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til arbeidsgiver")
                .isEqualTo(923);

        // Endring i refusjon, flyttes til søker.
        assertThat(beregningsresultatPerioder.get(3).getDagsats())
                .as("Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel")
                .isEqualTo(1477);
        assertThat(andelerForAT1.get(3).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(554);
        assertThat(andelerForAT2.get(3).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(923);

        // Opphører IM med refusjon
        assertThat(beregningsresultatPerioder.get(4).getDagsats())
                .as("Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel")
                .isEqualTo(554);
        assertThat(andelerForAT1.get(4).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(554);
        assertThat(andelerForAT2.get(4).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isZero();

        assertThat(beregningsresultatPerioder.get(5).getDagsats())
                .as("Forventer at dagsatsen for utsettelsen er null")
                .isZero();

        assertThat(beregningsresultatPerioder.get(6).getDagsats())
                .as("Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel")
                .isEqualTo(554);
        assertThat(andelerForAT1.get(6).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(554);
        assertThat(andelerForAT2.get(6).getTilSoker())
                .as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isZero();


        // Endringssøknad: Far bestemmer seg for å gi fra seg alle periodene
        var fordelingGiFraSegAlt = List.of(
                utsettelsesperiode(UtsettelsesÅrsak.FRI, fpStartdatoIfmFødselFar, fpStartdatoIfmFødselFar.plusDays(1))
        );
        var endringssøknadBuilder = lagEndringssøknad(søknadFar, saksnummerFar, fordelingGiFraSegAlt);
        far.søk(endringssøknadBuilder.build());

        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var vurderTilbakekrevingVedNegativSimulering = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderTilbakekrevingVedNegativSimulering());
        vurderTilbakekrevingVedNegativSimulering.tilbakekrevingUtenVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_SENERE);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperioder())
                .as("Uttaksperioder for valgt behandling")
                .isEmpty();
        assertThat(saksbehandler.hentHistorikkinnslagPåBehandling())
                .anyMatch(innslag -> innslag.erAvTypen(HistorikkTyper.BREV_BESTILT));
        // TODO: Legg til støtte får å hente ut maltype på brevet som er produsert. Skal være FORELDREPENGER_ANNULLERT
    }

    @Test
    @DisplayName("7: Far har AAP og søker overføring av gjennværende mødrekvoten fordi mor er syk.")
    @Description("Mor har løpende sak hvor hun har søkt om hele mødrekvoten og deler av fellesperioden. Mor blir syk 4" +
            "uker inn i mødrekvoten og far søker om overføring av resten. Far søker ikke overføring av fellesperioden." +
            "Far får innvilget mødrevkoten og mor sin sak blir berørt og automatisk revurdert.")
    void FarTestMorSyk() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arena(ArenaSakerDto.YtelseTema.AAP, LocalDate.now().minusMonths(12), LocalDate.now().plusMonths(2), 10_000)
                                .build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(6))
                .build();


        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFarOrdinær = fødselsdato.plusWeeks(23);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(familie,
                fødselsdato, fpStartdatoMor, fpStartdatoFarOrdinær);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(RisikoklasseType.IKKE_HØY);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        /*
         * FAR: Søker overføring av mødrekvoten fordi mor er syk innenfor de 6 første
         * uker av mødrekvoten.
         */
        var far = familie.far();
        var fpStartdatoFarEndret = fødselsdato.plusWeeks(4);
        var overføringsperiodeEndring = overføringsperiode(SYKDOM_ANNEN_FORELDER, MØDREKVOTE, fpStartdatoFarEndret, fødselsdato.plusWeeks(15).minusDays(1));
        var uttaksperiodeEndring = uttaksperiode(FEDREKVOTE, fpStartdatoFarOrdinær, fpStartdatoFarOrdinær.plusWeeks(15).minusDays(1));
        var fordelingFar = List.of(
                overføringsperiodeEndring,
                uttaksperiodeEndring
        );
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medMottattdato(fødselsdato.plusWeeks(6));
        var saksnummerFar = far.søk(søknadFar.build());

        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaUttakPerioder = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse())
                .godkjenn(overføringsperiodeEndring.tidsperiode());
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakPerioder);

        var beregningAktivitetStatus = saksbehandler.hentUnikeBeregningAktivitetStatus();
        assertThat(beregningAktivitetStatus)
                .as("Forventer at beregningsstatusen er APP!")
                .containsOnly(AktivitetStatus.ARBEIDSAVKLARINGSPENGER);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getRedusertPrAar())
                .as("Forventer at beregningsgrunnlaget baserer seg på en årsinntekt større enn 0. Søker har bare AAP.")
                .isPositive();

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        /* Mor: berørt sak */
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        // Løser AP 5084 negativ simulering! Oppretter tilbakekreving og sjekk at den er opprette. Ikke løs det.
        if (saksbehandler.harAksjonspunkt(VURDER_FEILUTBETALING_KODE)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler
                    .hentAksjonspunktbekreftelse(new VurderTilbakekrevingVedNegativSimulering());
            vurderTilbakekrevingVedNegativSimulering.tilbakekrevingUtenVarsel();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
            saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        }


        saksbehandler.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Foreldrepenger skal være endret pga annenpart har overlappende uttak!")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Forventer at det er 2 avslåtte uttaksperioder")
                .hasSize(2);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(2).getPeriodeResultatÅrsak().isAvslåttÅrsak())
                .as("Perioden burde være avslått fordi annenpart har overlappende uttak!")
                .isTrue();
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(3).getPeriodeResultatÅrsak().isAvslåttÅrsak())
                .as("Perioden burde være avslått fordi annenpart har overlappende uttak!")
                .isTrue();


        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats())
                .as("Siden perioden er avslått, forventes det 0 i dagsats")
                .isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats())
                .as("Siden perioden er avslått, forventes det 0 i dagsats")
                .isZero();

        if (saksbehandler.harAksjonspunkt(VURDER_FEILUTBETALING_KODE)) {
            var tbksaksbehandler = new TilbakekrevingSaksbehandler(SaksbehandlerRolle.SAKSBEHANDLER);
            tbksaksbehandler.hentSisteBehandling(saksnummerMor);
            tbksaksbehandler.ventTilBehandlingErPåVent();
            assertThat(tbksaksbehandler.valgtBehandling.venteArsakKode)
                    .as("Behandling har feil vent årsak")
                    .isEqualTo("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG");
        }
    }

    @Test
    @DisplayName("8: Mor har tvillinger og søker om hele utvidelsen.")
    @Description("Mor føder tvillinger og søker om hele mødrekvoten og fellesperioden, inkludert utvidelse. Far søker " +
            "samtidig uttak av fellesperioden fra da mor starter utvidelsen av fellesperioden. Søker deretter samtidig " +
            "av fedrekvoten, frem til mor er ferdig med fellesperioden, og deretter søker resten av fedrekvoten.")
    void MorSøkerFor2BarnHvorHunFårBerørtSakPgaFar() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidMedOpptjeningUnder6G()
                                .selvstendigNæringsdrivende(1_000_000)
                                .build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(28))
                .barn(LocalDate.now().minusWeeks(28))
                .build();

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(31);
        var fordelingMor = List.of(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fpStartdatoFar.minusDays(1)),
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(17).minusDays(1), FLERBARNSDAGER)
        );
        var søknadMor = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordelingMor)
                .medBarn(BarnBuilder.fødsel(2, fødselsdato).build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fpStartdatoMor.minusWeeks(3));
        var saksnummerMor = mor.søk(søknadMor.build());

        var arbeidsgiverMor = mor.arbeidsgiver();
        arbeidsgiverMor.sendInntektsmeldingerFP(saksnummerMor, fpStartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        var saldoerFørstgangsbehandling = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoerFørstgangsbehandling.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoerFørstgangsbehandling.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo())
                .as("Saldoen for stønadskonton MØDREKVOTE")
                .isZero();
        assertThat(saldoerFørstgangsbehandling.stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo())
                .as("Saldoen for stønadskonton FELLESPERIODE")
                .isZero();

        var feriepenger = saksbehandler.valgtBehandling.getFeriepengegrunnlag();
        assertThat(feriepenger).isNotNull();
        var feriepengerTilArbeidsgiver = oppsummerFeriepengerForArbeidsgiver(feriepenger.andeler(), arbeidsgiverMor.arbeidsgiverIdentifikator().value(), false);
        var feriepengerTilSøker = oppsummerFeriepengerForArbeidsgiver(feriepenger.andeler(), arbeidsgiverMor.arbeidsgiverIdentifikator().value(), true);
        assertFeriepenger(feriepengerTilSøker + feriepengerTilArbeidsgiver, 11297);


        /*
         * FAR: Søker samtidig uttak med flerbansdager. Søker deretter hele fedrekvoten,
         * også samtidig uttak.
         */
        var far = familie.far();
        var næringsinntekt = far.næringsinntekt();
        var opptjeningFar = OpptjeningMaler.egenNaeringOpptjening(
                far.arbeidsforhold().arbeidsgiverIdentifikasjon().value(),
                far.næringStartdato(),
                VirkedagUtil.helgejustertTilMandag(fpStartdatoFar),
                false,
                næringsinntekt,
                false);
        var fordelingFar = List.of(
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(4).minusDays(1), FLERBARNSDAGER, SAMTIDIGUTTAK),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(4), fpStartdatoFar.plusWeeks(17).minusDays(1), SAMTIDIGUTTAK),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(17), fpStartdatoFar.plusWeeks(19).minusDays(1))
        );
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordelingFar)
                .medSøker(new SøkerBuilder(BrukerRolle.FAR).medSelvstendigNæringsdrivendeInformasjon(List.of(opptjeningFar)).build())
                .medBarn(BarnBuilder.fødsel(2, fødselsdato).build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()));
        var saksnummerFar = far.søk(søknadFar.build());

        var arbeidsgiverFar = far.arbeidsgiver();
        arbeidsgiverFar.sendInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
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
                .hentBeregningsresultatPerioderMedAndelIArbeidsforhold(arbeidsgiverFar.arbeidsgiverIdentifikator());
        assertThat(perioderMedAndelIArbeidsforhold.get(0).getTilSoker())
                .as("Forventer at dagsatsen for arbeidsforholdet blir beregnet først – rest går til søker for SN")
                .isEqualTo(dagsatsTilAF);
        assertThat(perioderMedAndelIArbeidsforhold.get(1).getTilSoker())
                .as("Forventer at dagsatsen for arbeidsforholdet blir beregnet først – rest går til søker for SN")
                .isEqualTo(dagsatsTilAF);
        assertThat(perioderMedAndelIArbeidsforhold.get(2).getTilSoker())
                .as("Forventer at dagsatsen for arbeidsforholdet blir beregnet først – rest går til søker for SN")
                .isEqualTo(dagsatsTilAF);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(arbeidsgiverFar.arbeidsgiverIdentifikator(), 0))
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
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var saldoerBerørtSak = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoerBerørtSak.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoerBerørtSak.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo())
                .as("Saldoen for stønadskonton MØDREKVOTE")
                .isZero();
        assertThat(saldoerBerørtSak.stonadskontoer().get(SaldoVisningStønadskontoType.FEDREKVOTE).saldo())
                .as("Saldoen for stønadskonton FEDREKVOTE")
                .isZero();
        assertThat(saldoerBerørtSak.stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo())
                .as("Saldoen for stønadskonton FELLESPERIODE")
                .isZero();

        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .hasSize(1);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(6).getPeriodeResultatÅrsak().isAvslåttÅrsak())
                .as("Perioden burde være avslått fordi det er ingen stønadsdager igjen på stønadskontoen")
                .isTrue();
        assertThat(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().get(6).getDagsats())
                .as("Siden perioden er avslått, forventes det 0 i dagsats i tilkjent ytelse")
                .isZero();
    }

    @Test
    @DisplayName("9: Mor søker med dagpenger som grunnlag, besteberegnes automatisk")
    @Description("Mor søker med dagpenger som grunnlag. Kvalifiserer til automatisk besteberegning." +
            "Beregning etter etter §14-7, 3. ledd gir høyere inntekt enn beregning etter §14-7, 1. ledd")
    void MorSøkerMedDagpengerTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusMonths(10), LocalDate.now().minusMonths(5).minusDays(1))
                                .arena(ArenaSakerDto.YtelseTema.DAG, LocalDate.now().minusMonths(5), LocalDate.now().minusWeeks(5), 21_667)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fordelingMor = List.of(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(31).minusDays(1)));

        var søknadMor = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummerMor = mor.søk(søknadMor.build());

        saksbehandler.hentFagsak(saksnummerMor);
        assertThat(saksbehandler.sjekkOmDetErOpptjeningFremTilSkjæringstidspunktet("DAGPENGER"))
                .as("Forventer at det er registert en opptjeningsaktivitet med aktivitettype DAGPENGER med " +
                        "opptjening frem til skjæringstidspunktet for opptjening.")
                .isTrue();

        var bekreftKorrektBesteberegninging = saksbehandler
                .hentAksjonspunktbekreftelse(new KontrollerBesteberegningBekreftelse())
                .godkjenn()
                .setBegrunnelse("Besteberegning godkjent av autotest.");
        saksbehandler.bekreftAksjonspunkt(bekreftKorrektBesteberegninging);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        assertThat(saksbehandler.harHistorikkinnslagPåBehandling(HistorikkTyper.BREV_BESTILT))
                .as("Brev er bestillt i førstegangsbehandling")
                .isTrue();
        assertThat(saksbehandler.hentHistorikkinnslagPåBehandling())
                .as("Historikkinnslag")
                .anyMatch(h -> h.erAvTypen(HistorikkTyper.BREV_BESTILT));
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();
    }

    @Test
    @DisplayName("10: Far, aleneomsorg, søker adopsjon og får revurdert sak 4 måneder senere på grunn av IM med endring i refusjon.")
    @Description("Far, aleneomsorg, søker adopsjon og får revurdert sak 4 måneder senere på grunn av IM med endring i refusjon. " +
            "Mens behandlingen er hos beslutter sender AG en ny korrigert IM. Behandlingen rulles tilbake. På den " +
            "siste IM som AG sender ber AG om full refusjon, men kommer for sent til å få alt. AG får refusjon for" +
            "den inneværende måneden og tre måneder tilbake i tid; tiden før dette skal gå til søker.")
    void FarSøkerAdopsjonAleneomsorgOgRevurderingPgaEndringIRefusjonFraAG() {
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(mor().build())
                .barn(LocalDate.now().minusYears(10))
                .build();

        /* FAR */
        var far = familie.far();
        var omsorgsovertakelsedatoe = LocalDate.now().minusMonths(4).minusWeeks(1);
        var fpStartdatoFar = omsorgsovertakelsedatoe;
        var fordelingFar = List.of(
                uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar, fpStartdatoFar.plusWeeks(46).minusDays(1))
        );
        var søknadFar = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, BrukerRolle.FAR, false)
                .medFordeling(fordelingFar)
                .medAnnenForelder(AnnenforelderBuilder.ukjentForelder())
                .medMottattdato(fpStartdatoFar.minusWeeks(3));
        var saksnummerFar = far.søk(søknadFar.build());


        var arbeidsgiver = far.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAdopsjonsdokumentasjonBekreftelse())
                .setBegrunnelse("Adopsjon behandlet av Autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);
        var avklarFaktaAleneomsorgBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAleneomsorgBekreftelse())
                .bekreftBrukerHarAleneomsorg();
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAleneomsorgBekreftelse);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Forventer søkt periode blir innvilget")
                .isEmpty();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();

        // AG sender inn en IM med endring i refusjon som skal føre til revurdering på far sin sak.
        var inntektsmeldingEndringFar = arbeidsgiver.lagInntektsmeldingFP(fpStartdatoFar)
                .medRefusjonsBelopPerMnd(ProsentAndel.valueOf(50));
        arbeidsgiver.sendInntektsmeldinger(saksnummerFar, inntektsmeldingEndringFar);

        // Revurdering / Berørt sak til far
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
                .leggTilRefusjonGyldighetVurdering(arbeidsgiver.arbeidsgiverIdentifikator(), false)
                .setBegrunnelse("Refusjonskrav er sendt inn for sent og skal ikke tas med i beregning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        var vurderRefusjonBeregningsgrunnlagBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderRefusjonBeregningsgrunnlagBekreftelse());
        vurderRefusjonBeregningsgrunnlagBekreftelse
                .setFastsattRefusjonFomForAllePerioder(LocalDate.now().minusMonths(3))
                .setBegrunnelse("Fordi autotest sier det!");
        saksbehandler.bekreftAksjonspunkt(vurderRefusjonBeregningsgrunnlagBekreftelse);

        var vurderTilbakekrevingVedNegativSimulering = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderTilbakekrevingVedNegativSimulering());
        vurderTilbakekrevingVedNegativSimulering.tilbakekrevingUtenVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        // AG sender inn ny korrigert IM med endring i refusjon mens behandlingen er hos beslutter. Behandlingen skal
        // rulles tilbake og behandles på nytt fra første AP i revurderingen.
        var inntektsmeldingEndringFar2 = arbeidsgiver.lagInntektsmeldingFP(fpStartdatoFar)
                .medRefusjonsBelopPerMnd(ProsentAndel.valueOf(100));
        arbeidsgiver.sendInntektsmeldinger(saksnummerFar, inntektsmeldingEndringFar2);

        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.behandlinger)
                .as("Antall behandlinger")
                .hasSize(2);
        saksbehandler.ventTilHistorikkinnslag(HistorikkTyper.SPOLT_TILBAKE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var vurderFaktaOmBeregningBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse());
        vurderFaktaOmBeregningBekreftelse2
                .leggTilRefusjonGyldighetVurdering(arbeidsgiver.arbeidsgiverIdentifikator(), false)
                .setBegrunnelse("Refusjonskrav er sendt inn for sent og skal ikke tas med i beregning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse2);

        var vurderRefusjonBeregningsgrunnlagBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderRefusjonBeregningsgrunnlagBekreftelse());
        vurderRefusjonBeregningsgrunnlagBekreftelse2
                .setFastsattRefusjonFomForAllePerioder(LocalDate.now().minusMonths(3))
                .setBegrunnelse("Fordi autotest sier det!");
        saksbehandler.bekreftAksjonspunkt(vurderRefusjonBeregningsgrunnlagBekreftelse2);

        var vurderTilbakekrevingVedNegativSimulering2 = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderTilbakekrevingVedNegativSimulering());
        vurderTilbakekrevingVedNegativSimulering2.tilbakekrevingUtenVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering2);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, true, true);

        var perioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        if (LocalDate.now().getDayOfMonth() == 1) {
            assertThat(perioder)
                    .as("Berørt behandlings tilkjent ytelse perioder")
                    .hasSize(2);
            assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(0), 0))
                    .as("Forventer at hele summen utbetales til søker i første periode, og derfor ingenting til arbeidsgiver!")
                    .isTrue();
            assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(1), 100))
                    .as("Forventer at hele summen utbetales til AG i andre periode, og derfor ingenting til søker!")
                    .isTrue();

        } else {
            assertThat(perioder)
                    .as("Berørt behandlings tilkjent ytelse perioder")
                    .hasSize(3);
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
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .barn(LocalDate.now().minusYears(10))
                .build();

        /* FAR */
        var far = familie.far();
        var mor = familie.mor();
        var omsorgsovertakelsedatoe = LocalDate.now().minusWeeks(4);
        var fpStartdatoFar = omsorgsovertakelsedatoe;
        var fellesperiodeStartFar = fpStartdatoFar.plusWeeks(15);
        var fellesperiodeSluttFar = fellesperiodeStartFar.plusWeeks(16).minusDays(1);
        var fordelingFar = fordeling(
                uttaksperiode(FEDREKVOTE, fpStartdatoFar, fellesperiodeStartFar.minusDays(1)),
                uttaksperiode(FELLESPERIODE, fellesperiodeStartFar, fellesperiodeSluttFar));
        var søknadFar = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, BrukerRolle.FAR, false)
                .medFordeling(fordelingFar)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(mor));
        var saksnummerFar = far.søk(søknadFar.build());

        var arbeidsgiverFar = far.arbeidsgiver();
        arbeidsgiverFar.sendInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAdopsjonsdokumentasjonBekreftelse())
                .setBegrunnelse("Adopsjon behandlet av Autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);

        var avklarFaktaAnnenForeldreHarRett = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAnnenForeldreHarRett())
                .setAnnenforelderHarRett(true)
                .setBegrunnelse("Både far og mor har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new VurderUttakDokumentasjonBekreftelse());

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Forventer alle fars sine peridoder er innvilget")
                .isEmpty();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();

        /* MOR */
        var fpStartdatoMor = fpStartdatoFar.plusWeeks(7);
        var fellesperiodeStartMor = fpStartdatoMor.plusWeeks(4);
        var fellesperiodeSluttMor = fellesperiodeStartMor.plusWeeks(9).minusDays(1);
        var fordelingMor = fordeling(
                uttaksperiode(MØDREKVOTE, fpStartdatoMor, fellesperiodeStartMor.minusDays(1)),
                uttaksperiode(FELLESPERIODE, fellesperiodeStartMor, fellesperiodeSluttMor, 40, UttaksperiodeType.SAMTIDIGUTTAK)
        );
        var søknadMor = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, BrukerRolle.MOR, false)
                .medFordeling(fordelingMor)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(far));
        var saksnummerMor = mor.søk(søknadMor.build());
        var arbeidsgiverMor = mor.arbeidsgiver();
        arbeidsgiverMor.sendInntektsmeldingerFP(saksnummerMor, fpStartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseMor = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAdopsjonsdokumentasjonBekreftelse());
        avklarFaktaAdopsjonsdokumentasjonBekreftelseMor.setBegrunnelse("Adopsjon behandlet av Autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseMor);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Forventer alle mors sine peridoder er innvilget")
                .isEmpty();

        /* FAR: Berørt behandling */
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        // UTTAK
        var mødrekvoten = fordelingMor.stream()
                .filter(uttaksPeriode -> uttaksPeriode.konto().equals(UttaksplanPeriodeDto.KontoType.MØDREKVOTE))
                .findFirst()
                .orElseThrow();
        var avslåtteSamtidigUttak = saksbehandler.hentAvslåtteUttaksperioder().get(0);
        assertThat(avslåtteSamtidigUttak.getFom()).isEqualTo(mødrekvoten.tidsperiode().fom());
        assertThat(avslåtteSamtidigUttak.getTom()).isEqualTo(mødrekvoten.tidsperiode().tom());
        assertThat(avslåtteSamtidigUttak.getAktiviteter().get(0).getTrekkdagerDesimaler()).isZero();
        assertThat(avslåtteSamtidigUttak.getPeriodeResultatÅrsak())
                .as("Perioden burde være avslått fordi annenpart tar ut mødrekovte med 100% utbetalingsgrad samtidig")
                .isEqualTo(PeriodeResultatÅrsak.DEN_ANDRE_PART_OVERLAPPENDE_UTTAK_IKKE_SØKT_INNVILGET_SAMTIDIG_UTTAK);

        /*
         * Mødrekvoten kan ikke tas ut samtidig som fedrekvoten. Denne blir automatisk avslått.
         * Her overlapper mor sin fellesperiode både med far sin fedrekvote og fellesperiode (100% uttak).
         * Siden foreldrenes uttak samtidig uttak overstiger 100% samlet så må disse vurderes manuelt.
         * Begge disse innvilges.
         *  Fellesperiode + fedrekvote -> 100% uttak på far (samlet 140% samlet) innvilges
         *  Fellesperiode + fellesperiode -> 60% uttak på far (samlet 100% samlet).
         *
         * Siste periode delvis innvilges og avslås pga manglende dager igjen på felleskonto
         */
        var fastsettUttaksperioderManueltBekreftelseMor = saksbehandler
                .hentAksjonspunktbekreftelse(new FastsettUttaksperioderManueltBekreftelse());

        // Siste periode skal slippes og delvis innvilges med resterende saldo
        var fastsatteUttaksperioder = fastsettUttaksperioderManueltBekreftelseMor.getPerioder();
        var sistePeriode = fastsatteUttaksperioder.get(fastsatteUttaksperioder.size() - 1);
        fastsettUttaksperioderManueltBekreftelseMor.avslåPeriode(sistePeriode.getFom(), sistePeriode.getTom(), IKKE_STØNADSDAGER_IGJEN, false);
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelseMor);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, true, false);

        beslutter.ventTilFagsakLøpende();

        // verifisering i uttak
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo())
                .as("Saldoen for stønadskonton FELLESPERIODE")
                .isZero();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .hasSize(2);

        // verifisering i tilkjent ytelse
        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(1).getDagsats())
                .as("Tilkjent ytelses periode 2")
                .isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats())
                .as("Forventer at dagsatsen blir redusert fra 100% til 60% for periode 3 i tilkjent ytelse")
                .isEqualTo((int) Math.round(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats() * 0.6));
        assertThat(tilkjentYtelsePerioder.getPerioder().get(5).getDagsats())
                .as("Tilkjent ytelses periode 6")
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
        var fødselsdatoBarn = LocalDate.now().minusDays(2);
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .infotrygd(GrunnlagDto.Ytelse.SP, LocalDate.now().minusMonths(9), LocalDate.now().plusDays(1), GrunnlagDto.Status.LØPENDE, fødselsdatoBarn)
                                .build())
                        .build())
                .forelder(far().build())
                .barn(fødselsdatoBarn)
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        assertThat(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"))
                .as("Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som " +
                        "er forut for permisjonen på skjæringstidspunktet!")
                .isTrue();

        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
                .leggTilAndelerYtelse(4000.0, Inntektskategori.ARBEIDSTAKER);
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false, false);

        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getRedusertPrAar())
                .as("Forventer at beregningsgrunnlaget baserer seg på et grunnlag som er mindre enn 1/2 G")
                .isLessThan(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getHalvG());
        assertThat(saksbehandler.vilkårStatus(VilkarTypeKoder.BEREGNINGSGRUNNLAGVILKÅR))
                .as("Vilkårstatus for beregningsgrunnlag")
                .isEqualTo("IKKE_OPPFYLT");
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);

        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();

        var førstegangsbehandling = klagebehandler.førstegangsbehandling();
        var klageFormkravNfp = klagebehandler
                .hentAksjonspunktbekreftelse(new KlageFormkravNfp())
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("Super duper klage!");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        var vurderingAvKlageNfpBekreftelse = klagebehandler
                .hentAksjonspunktbekreftelse(new VurderingAvKlageNfpBekreftelse())
                .bekreftMedholdGunst("PROSESSUELL_FEIL")
                .fritekstBrev("Fritektst til brev fra klagebehandler.")
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);


        klagebehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());
        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        // Saksbehandler oppretter ny revudering manuelt etter søker har fått medhold i klage.
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_KLAGE_MED_END_INNTEKT);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgSisteBehandling();

        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().get(0).behandlingArsakType())
                .as("Behandlingsårsakstype")
                .isEqualTo(BehandlingÅrsakType.RE_KLAGE_MED_END_INNTEKT);

        assertThat(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"))
                .as("Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som " +
                        "er forut for permisjonen på skjæringstidspunktet!")
                .isTrue();

        var vurderFaktaOmBeregningBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
                .leggTilAndelerYtelse(10_000.0, Inntektskategori.ARBEIDSTAKER);
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse2);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new KontrollerRealitetsbehandlingEllerKlage());
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true, false);


        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.konsekvenserForYtelsen())
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
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusYears(4),
                                        ArbeidsavtaleDto.arbeidsavtale(LocalDate.now().minusYears(4), LocalDate.now().minusDays(60)).build(),
                                        ArbeidsavtaleDto.arbeidsavtale(LocalDate.now().minusDays(59)).stillingsprosent(50).build()
                                )
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();

        var mor = familie.mor();
        var termindato = LocalDate.now().minusWeeks(2);
        var fpStartdatoMor = termindato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(termindato.minusMonths(2));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdatoMor);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo())
                .as("Saldoen for stønadskonton MØDREKVOTE")
                .isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo())
                .as("Saldoen for stønadskonton FELLESPERIODE")
                .isZero();

        var differanseFødselTermin = 7;
        familie.sendInnDødfødselhendelse(termindato.plusDays(differanseFødselTermin));

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().get(0).behandlingArsakType())
                .as("Behandlingsårsak revurdering")
                .isEqualTo(BehandlingÅrsakType.RE_HENDELSE_DØDFØDSEL);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new FastsettUttaksperioderManueltBekreftelse());
        fastsettUttaksperioderManueltBekreftelse
                .avslåManuellePerioderMedPeriodeResultatÅrsak(PeriodeResultatÅrsak.BARNET_ER_DØD);
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new FastsetteUttakKontrollerOpplysningerOmDødDto());

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true, false);

        var saldoerRevurdering = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoerRevurdering.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoerRevurdering.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo())
                .as("Saldoen for stønadskonton MØDREKVOTE")
                .isEqualTo(45);
        assertThat(saldoerRevurdering.stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo())
                .as("Saldoen for stønadskonton FELLESPERIODE")
                .isEqualTo(75);

        var uttakResultatPerioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        var uttaksperiode_0 = uttakResultatPerioder.get(0);
        var uttaksperiode_1 = uttakResultatPerioder.get(1);
        assertThat(uttaksperiode_0.getPeriodeResultatType())
                .as("Uttaksresultattype for første periode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttaksperiode_0.getPeriodeType())
                .as("Forventer at første periode er FELLESPERIODE pga dødfødsel etter termin")
                .isEqualTo(FELLESPERIODE.name());
        assertThat(uttaksperiode_0.getFom())
                .as("Verifiserer at antall dager etter termin fylles med fellesperioden")
                .isEqualTo(uttaksperiode_1.getFom().minusDays(differanseFødselTermin));

        assertThat(uttaksperiode_1.getPeriodeResultatType())
                .as("Uttaksresultattype for andre periode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttaksperiode_1.getPeriodeType())
                .as("Forventer at første periode er FORELDREPENGER_FØR_FØDSEL pga dødfødsel etter termin")
                .isEqualTo(FORELDREPENGER_FØR_FØDSEL.name());
        assertThat(uttaksperiode_1.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Verifiser at søker tar ut hele FORELDREPENGER_FØR_FØDSEL kvoten")
                .isEqualByComparingTo(BigDecimal.valueOf(3 * 5));

        var uttaksperiode_2 = uttakResultatPerioder.get(2);
        assertThat(uttaksperiode_2.getPeriodeResultatType())
                .as("Uttaksresultattype for tredje periode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttaksperiode_2.getPeriodeType())
                .as("Forventer at første periode er MØDREKVTOEN pga dødfødsel etter termin")
                .isEqualTo(MØDREKVOTE.name());
        assertThat(uttaksperiode_2.getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Forventer at det tas ut 6 uker av den gjenværende delen av stønadsperioden")
                .isEqualByComparingTo(BigDecimal.valueOf(6 * 5));

        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder pga dødfødsel")
                .hasSize(2);
        assertThat(uttakResultatPerioder.get(3).getPeriodeResultatÅrsak().isAvslåttÅrsak())
                .as("Perioden burde være avslått fordi det er mottatt dødfødselshendelse")
                .isTrue();
        assertThat(uttakResultatPerioder.get(4).getPeriodeResultatÅrsak().isAvslåttÅrsak())
                .as("Perioden burde være avslått fordi det er mottatt dødfødselshendelse")
                .isTrue();
    }

    @Test
    @DisplayName("14: Mor, fødsel, sykdom uke 5 til 10, må søke om utsettelse fra uke 5-6")
    @Description("Mor søker fødsel hvor hun oppgir annenpart. Mor er syk fra uke 5 til 10. Hun må søke utsettelse fra " +
            "uke 5 til 6 ettersom det er innenfor de første 6 ukene etter fødsel. Ukene etter trenger hun ikke søke om" +
            "utsettelse og blir automatisk innvilget uten trekk.")
    void mor_fødsel_sykdom_innefor_første_6_ukene_utsettelse() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(8))
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var utsettelsesperiodeMidtIMødrekvoten = utsettelsesperiode(UtsettelsesÅrsak.SYKDOM,
                fødselsdato.plusWeeks(5), fødselsdato.plusWeeks(6).minusDays(1));
        var uttaksperiodeEtterUtsettelseOgOpphold = uttaksperiode(MØDREKVOTE, fødselsdato.plusWeeks(10), fødselsdato.plusWeeks(20).minusDays(1));
        var fordeling = fordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(5).minusDays(1)),
                utsettelsesperiodeMidtIMødrekvoten,
                // Opphold uke 6 til 10
                uttaksperiodeEtterUtsettelseOgOpphold,
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(36).minusDays(1))
        );
        var fpStartdato = fordeling.get(0).tidsperiode().fom();
        var søknad = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fødselsdato.minusWeeks(3))
                .build();
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaUttak = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse())
                .godkjennSykdom();
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttak);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false, false);

        // UTTAK
        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldo for stønadskontoen FORELDREPENGER_FØR_FØDSEL")
                .isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo())
                .as("Saldo for stønadskontoen MØDREKVOTE")
                .isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo())
                .as("Saldo for stønadskontoen FELLESPERIODE")
                .isZero();

        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .isEmpty();
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(2).getPeriodeResultatÅrsak())
                .as("Perioderesultatårsak")
                .isEqualTo(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_SEKS_UKER_FRI_SYKDOM);

        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getFom())
                .as("Utsettelsesperiode fom")
                .isEqualTo(utsettelsesperiodeMidtIMødrekvoten.tidsperiode().fom());
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getTom())
                .as("Utsettelsesperiode tom")
                .isEqualTo(utsettelsesperiodeMidtIMødrekvoten.tidsperiode().tom());
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats())
                .as("Utsettelsesperiode dagsats")
                .isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(3).getFom())
                .as("Periode etter fri utsettelse fom")
                .isEqualTo(uttaksperiodeEtterUtsettelseOgOpphold.tidsperiode().fom());
    }

    @Test
    @DisplayName("15: Mor, adopsjon, sykdom uke 3 til 8, trenger ikke søke utsettelse for uke 3 til 6")
    @Description("Mor søker adopsjon hvor hun oppgir annenpart. Mor er syk innenfor de første 6 ukene og etter. Sykdom" +
            "fra uke 3 til 8. Ikke noe krav til å søke om utsettlse og saken blir automatisk behandlet og innvilget.")
    void mor_adopsjon_sykdom_uke_3_til_8_automatisk_invilget() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(15))
                .build();

        var mor = familie.mor();
        var omsorgsovertagelsesdato = LocalDate.now().minusMonths(2);
        var fordeling = fordeling(
                uttaksperiode(MØDREKVOTE, omsorgsovertagelsesdato, omsorgsovertagelsesdato.plusWeeks(3).minusDays(1)),
                // Opphold uke 3 til 10
                uttaksperiode(MØDREKVOTE, omsorgsovertagelsesdato.plusWeeks(10), omsorgsovertagelsesdato.plusWeeks(22).minusDays(1))
        );

        var søknad = SøknadForeldrepengerMaler.lagSøknadForeldrepengerAdopsjon(omsorgsovertagelsesdato, BrukerRolle.MOR, false)
                .medFordeling(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(omsorgsovertagelsesdato.minusWeeks(3));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, omsorgsovertagelsesdato);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAdopsjonsdokumentasjonBekreftelse())
                .setBegrunnelse("Adopsjon behandlet av Autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo())
                .as("Saldo for stønadskontoen MØDREKVOTE")
                .isZero();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .isEmpty();

        var feriepenger = saksbehandler.valgtBehandling.getFeriepengegrunnlag();
        assertThat(feriepenger).isNotNull();
        var feriepengerTilArbeidsgiver = oppsummerFeriepengerForArbeidsgiver(feriepenger.andeler(), arbeidsgiver.arbeidsgiverIdentifikator().value(), false);
        var feriepengerTilSøker = oppsummerFeriepengerForArbeidsgiver(feriepenger.andeler(), arbeidsgiver.arbeidsgiverIdentifikator().value(), true);
        assertFeriepenger(feriepengerTilSøker + feriepengerTilArbeidsgiver, 11297);
    }

    @Test
    @DisplayName("16: Mor engangstønad. Bare far har rett (BFHR). Utsettelse uten at mor er i aktivitet. Trekker alt utenom minstretten.")
    @Description("Mor har en ferdig behanldet engagnstønad liggende. Far søker derette foreldrepenger hvor han oppgir at bare han har rett. "
            + "Far søker først 2 uker foreldrepenger ifm fødselen og deretter 40 uker utsettelse hvor mors aktivit ikke er dokumentert. "
            + "Utsettelsen avslås og trekker dager løpende, men skal ikke trekke noe av minsteretten. Etter utsettelsen så tar far ut en "
            + "periode med foreldrepenger med aktivitetskrav hvor mor er i aktivitet, etterfulgt av en periode hvor han bruker sine resterende "
            + "6 uker med foreldrepenger uten aktivitetskrav. Første uttaksperiode etter utsettelsen innvilges delvis med disse 6 "
            + "gjenværende stønadsukene uten aktivitetskrav. Resten av periode og neste uttaks periode avslås pga av manglede stønadsdager igjen")
    void farBhfrTest() {
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4))
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-002", LocalDate.now().minusMonths(4))
                                .build())
                        .build())
                .forelder(mor().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build();

        var fødselsdato = familie.barn().fødselsdato();

        /* Mor's engangsstønad*/
        var mor = familie.mor();
        var saksnummerMor = mor.søk(lagEngangstønadFødsel(fødselsdato).build());
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        /* Far's søknad */
        var far = familie.far();
        var fpStartdatoFar = fødselsdato;
        var uttaksperiodeFørste = uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar, fpStartdatoFar.plusWeeks(2).minusDays(1), IKKE_OPPGITT);
        var utsettelsesperiode = utsettelsesperiode(UtsettelsesÅrsak.FRI,  fpStartdatoFar.plusWeeks(6), fpStartdatoFar.plusWeeks(46).minusDays(1), UTDANNING);
        var uttaksperiodeEtterUtsettelse1= uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar.plusWeeks(46), fpStartdatoFar.plusWeeks(56).minusDays(1), ARBEID);
        var uttaksperiodeEtterUtsettelse2= uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar.plusWeeks(56), fpStartdatoFar.plusWeeks(62).minusDays(1));
        var fordeling = fordeling(uttaksperiodeFørste, utsettelsesperiode, uttaksperiodeEtterUtsettelse1, uttaksperiodeEtterUtsettelse2);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskIkkeRett(familie.mor()))
                .medMottattdato(fødselsdato.minusWeeks(1));
        var saksnummerFar = far.søk(søknad.build());
        var arbeidsgiver = far.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        /*
        * Skal ikke få AP 5086 hvor saksbehandler må avklare om anneforelder har rett, ettersom mor allerede mottar engangsstønad
        * Mors aktivitet er ikke dokumentert for utsettelsesperioden og første uttaksperiode etter utsettelsen.
        * */
        saksbehandler.hentFagsak(saksnummerFar);
        var vurderUttakDokBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse())
                .ikkeDokumentert(utsettelsesperiode.tidsperiode())
                .ikkeDokumentert(uttaksperiodeEtterUtsettelse1.tidsperiode())
                .setBegrunnelse("Mor er ikke i aktivitet for siste uttaksperiode!");
        saksbehandler.bekreftAksjonspunkt(vurderUttakDokBekreftelse);
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummerFar, false, false);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // SALDO
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(FORELDREPENGER).saldo())
                .as("Saldoen for stønadskonton FORELDREPENGER")
                .isZero();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(MINSTERETT).saldo())
                .as("Saldoen for stønadskonton MINSTERETT")
                .isZero();

        // UTTAKSPLAN
        var avslåtteUttaksperioder = saksbehandler.hentAvslåtteUttaksperioder();
        assertThat(avslåtteUttaksperioder)
                .as("Forventer at det er 4 avslåtte uttaksperioder")
                .hasSize(4);

        var avslåttUtsettelseperiodeFørsteDel = avslåtteUttaksperioder.get(0);
        assertThat(avslåttUtsettelseperiodeFørsteDel.getFom()).isEqualTo(utsettelsesperiode.tidsperiode().fom());
        assertThat(avslåttUtsettelseperiodeFørsteDel.getTom()).isCloseTo(utsettelsesperiode.tidsperiode().fom().plusWeeks(30), within(2, ChronoUnit.DAYS)); // splitt tar ikke hensyn til helger
        assertThat(avslåttUtsettelseperiodeFørsteDel.getAktiviteter().get(0).getTrekkdagerDesimaler())  // Trekker opp til minstretten
                .as("Trekkdager")
                .isEqualByComparingTo(BigDecimal.valueOf(30 * 5));
        assertThat(avslåttUtsettelseperiodeFørsteDel.getPeriodeResultatÅrsak())
                .as("Perioderesultatårsak")
                .isEqualTo(AKTIVITETSKRAVET_UTDANNING_IKKE_DOKUMENTERT);

        var avslåttUtsettelseperiodeAndreDel = avslåtteUttaksperioder.get(1);
        assertThat(avslåttUtsettelseperiodeAndreDel.getFom()).isCloseTo(utsettelsesperiode.tidsperiode().fom().plusWeeks(30), within(2, ChronoUnit.DAYS)); // splitt tar ikke hensyn til helger
        assertThat(avslåttUtsettelseperiodeAndreDel.getTom()).isEqualTo(utsettelsesperiode.tidsperiode().tom());
        assertThat(avslåttUtsettelseperiodeAndreDel.getAktiviteter().get(0).getTrekkdagerDesimaler()) // avslag på siste rest av utsettelsen, men trekker ikke av minsteretten!
                .as("Trekkdager")
                .isZero();
        assertThat(avslåttUtsettelseperiodeAndreDel.getPeriodeResultatÅrsak())
                .as("Perioderesultatårsak")
                .isEqualTo(IKKE_STØNADSDAGER_IGJEN);

        var avslåtttUttaksperiode1 = avslåtteUttaksperioder.get(2);
        assertThat(avslåtttUttaksperiode1.getFom()).isEqualTo(uttaksperiodeEtterUtsettelse1.tidsperiode().fom().plusWeeks(8));
        assertThat(avslåtttUttaksperiode1.getTom()).isEqualTo(uttaksperiodeEtterUtsettelse1.tidsperiode().tom());
        assertThat(avslåtttUttaksperiode1.getPeriodeResultatÅrsak())
                .as("Perioderesultatårsak")
                .isEqualTo(IKKE_STØNADSDAGER_IGJEN);

        var avslåtttUttaksperiode2 = avslåtteUttaksperioder.get(3);
        assertThat(avslåtttUttaksperiode2.getFom()).isEqualTo(uttaksperiodeEtterUtsettelse2.tidsperiode().fom());
        assertThat(avslåtttUttaksperiode2.getTom()).isEqualTo(uttaksperiodeEtterUtsettelse2.tidsperiode().tom());
        assertThat(avslåtttUttaksperiode2.getPeriodeResultatÅrsak())
                .as("Perioderesultatårsak")
                .isEqualTo(IKKE_STØNADSDAGER_IGJEN);
    }

    @Test
    @DisplayName("17: Mor happy case - verifiser innsyn har korrekt data")
    @Description("Verifiserer at innsyn har korrekt data og sammenligner med vedtaket med det saksbehandlerene ser")
    void mor_innsyn_verifsere() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(8))
                .build();

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpSluttdatoMor = fødselsdato.plusWeeks(23);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(familie,
                fødselsdato, fpStartdatoMor, fpSluttdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(RisikoklasseType.IKKE_HØY);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Mor går på min side for innsyn på foreldrepengesaken sin. Verifisere innhold
        var mor = familie.mor();
        var fpSak = mor.innsyn().hentFpSakUtenÅpenBehandling(saksnummerMor);
        assertThat(fpSak.annenPart()).isNotNull();
        assertThat(fpSak.dekningsgrad()).isEqualTo(Dekningsgrad.HUNDRE);
        assertThat(fpSak.barn()).hasSize(1);

        // Sammenlign uttak fra fpfrontend og innsyn for bruker
        var uttakResultatPerioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        var vedtaksperioderInnsyn = fpSak.gjeldendeVedtak().perioder();
        assertThat(vedtaksperioderInnsyn)
                .hasSize(3);

        // Verifisere at alle perioder er innvilget i både uttak og vedtaket i innsyn
        uttakResultatPerioder.forEach(periode -> assertThat(periode.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET));
        vedtaksperioderInnsyn.forEach(periode -> assertThat(periode.resultat().innvilget()).isTrue());
    }

    @Test
    @DisplayName("Koblet sak. Far utsetter oppstart rundt fødsel, søker termin og med fødselshendelse")
    @Description("Far søker og får innvilget før termin. Fødselshendelse med fødsel etter termin. Far utsetter oppstart for å matche"
            + "fødselsdato")
    void farUtsetterOppstartRundtFødselSøkerTermin() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build()).build())
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build()).build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();

        var termindato = LocalDate.now().minusWeeks(2).minusDays(2);

        var mor = familie.mor();
        var søknadMor = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummerMor = mor.søk(søknadMor.build());

        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummerMor, termindato.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var far = familie.far();
        var fomFedrekvote = termindato.minusDays(4);
        var søknadFar = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin(termindato, BrukerRolle.FAR)
                .medFordeling(fordeling(uttaksperiode(FEDREKVOTE, fomFedrekvote, fomFedrekvote.plusWeeks(1).plusDays(3), SAMTIDIGUTTAK)))
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medMottattdato(termindato.minusWeeks(1))
                .build();
        var saksnummerFar = far.søk(søknadFar);

        far.arbeidsgiver().sendInntektsmeldingerFP(saksnummerFar, termindato.minusWeeks(1));

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Forventer omfordeling av feriepenger
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling(REBEREGN_FERIEPENGER);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Fødselshendelse
        var fødselsdato = termindato.plusWeeks(1);
        familie.sendInnFødselshendelse(fødselsdato);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling(BehandlingÅrsakType.RE_HENDELSE_FØDSEL);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var endringsdato = termindato.minusWeeks(1);
        var endringssøknad = lagEndringssøknad(søknadFar, saksnummerFar,
                fordeling(
                        utsettelsesperiode(FRI, endringsdato, fødselsdato.minusDays(1)),
                        uttaksperiode(FEDREKVOTE, fødselsdato, fødselsdato.plusWeeks(2).minusDays(1), SAMTIDIGUTTAK)
                ));
        far.søk(endringssøknad.build());

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling(RE_ENDRING_FRA_BRUKER);
        if (saksbehandler.harAksjonspunkt(VURDER_FEILUTBETALING_KODE)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(new VurderTilbakekrevingVedNegativSimulering())
                    .avventSamordningIngenTilbakekreving();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
            saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        }
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var uttak = saksbehandler.valgtBehandling.hentUttaksperioder();
        for (var periode : uttak) {
            assertThat(periode.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
            assertThat(periode.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FEDREKVOTE);
            assertThat(periode.getAktiviteter().get(0).getUtbetalingsgrad()).isEqualTo(BigDecimal.valueOf(100));
        }
        var trekkdager = uttak.stream().mapToInt(p -> p.getAktiviteter().get(0).getTrekkdagerDesimaler().intValue()).sum();
        assertThat(trekkdager).isEqualTo(10);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().stream().map(BehandlingÅrsak::behandlingArsakType))
                .doesNotContain(BehandlingÅrsakType.BERØRT_BEHANDLING);
    }

    @Test
    @DisplayName("Far får justert uttaket rundt termin etter fødselshendelse")
    @Description("Far søker og får innvilget på termin. Fødselen kommer og uttaket justeres")
    void farFårJustertUttakVedFødselshendelse() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build()).build())
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build()).build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();
        var termindato = LocalDate.now().minusWeeks(2).plusDays(2);

        var mor = familie.mor();
        var saksnummerMor = mor.søk(lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .build());
        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummerMor, termindato.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();

        var far = familie.far();

        var farsPeriodeRundtFødsel = uttaksperiode(FEDREKVOTE, termindato, termindato.plusWeeks(2).minusDays(1), SAMTIDIGUTTAK);
        var søknadFar = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin(termindato, BrukerRolle.FAR)
                .medFordeling(List.of(farsPeriodeRundtFødsel))
                .medØnskerJustertUttakVedFødsel(true)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medMottattdato(termindato.minusWeeks(1));
        var saksnummerFar = far.søk(søknadFar.build());

        far.arbeidsgiver().sendInntektsmeldingerFP(saksnummerFar, termindato);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Forventer omfordeling av feriepenger før innsenidng av fødselshendelse
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling(REBEREGN_FERIEPENGER);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Fødselshendelse
        var fødselsdato = termindato.plusWeeks(1);
        familie.sendInnFødselshendelse(fødselsdato);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling(BehandlingÅrsakType.RE_HENDELSE_FØDSEL);

        if (saksbehandler.harAksjonspunkt(VURDER_FEILUTBETALING_KODE)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(new VurderTilbakekrevingVedNegativSimulering())
                    .avventSamordningIngenTilbakekreving();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
            saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
            saksbehandler.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();
        } else {
            saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        }

        var uttak = saksbehandler.valgtBehandling.hentUttaksperioder();
        for (var periode : uttak) {
            assertThat(periode.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
            assertThat(periode.getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FEDREKVOTE);
            assertThat(periode.getAktiviteter().get(0).getUtbetalingsgrad()).isEqualTo(BigDecimal.valueOf(100));
        }
        var trekkdager = uttak.stream().mapToInt(p -> p.getAktiviteter().get(0).getTrekkdagerDesimaler().intValue()).sum();
        assertThat(trekkdager).isEqualTo(10);
        assertThat(uttak.get(0).getFom()).isEqualTo(helgejustertTilMandag(fødselsdato));
    }

    private Saksnummer sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(Familie familie,
                                                                                              LocalDate fødselsdato,
                                                                                              LocalDate fpStartdatoMor,
                                                                                              LocalDate fpStartdatoFar) {
        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var mor = familie.mor();
        var fordelingMor = fordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fpStartdatoFar.minusDays(1))
        );
        var søknadMor = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medFordeling(fordelingMor)
                .medMottattdato(fpStartdatoMor.minusWeeks(4));
        var saksnummerMor = mor.søk(søknadMor.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerMor, fpStartdatoMor);

        return saksnummerMor;
    }

    private void assertFeriepenger(int faktiskBeløp, int forventetBeløp) {
        // Grunnet dynamiske perioder / oppslitting av perioder er det vanskelig å få valideringen 100% rett.
        // Legger derfor inn en liten buffer for å unngå knekte tester
        assertThat(faktiskBeløp).isGreaterThan(forventetBeløp - 10);
        assertThat(faktiskBeløp).isLessThan(forventetBeløp + 10);
    }

    private int oppsummerFeriepengerForArbeidsgiver(List<Feriepengeandel> andeler,
                                                    String arbeidsgiverIdentifikator,
                                                    boolean brukerErMottaker) {
        return andeler.stream()
                .filter(andel -> andel.arbeidsgiverId().equals(arbeidsgiverIdentifikator))
                .filter(andel -> andel.erBrukerMottaker() == brukerErMottaker)
                .mapToInt(andel -> andel.årsbeløp().intValue())
                .sum();
    }

}
