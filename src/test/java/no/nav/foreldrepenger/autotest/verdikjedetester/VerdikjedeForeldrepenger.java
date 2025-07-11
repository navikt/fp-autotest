package no.nav.foreldrepenger.autotest.verdikjedetester;

import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_10;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_11;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_12;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_13;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_14;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_15;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_16;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_7;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_9;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_21_3;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_8_30;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_8_35;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_8_38;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_8_41;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_8_49;
import static no.nav.foreldrepenger.autotest.brev.BrevFormateringUtils.formaterDato;
import static no.nav.foreldrepenger.autotest.brev.BrevFormateringUtils.formaterKroner;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.REBEREGN_FERIEPENGER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_HENDELSE_DØD_FORELDER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_HENDELSE_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak.AKTIVITETSKRAVET_ARBEID_IKKE_OPPFYLT;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak.AKTIVITETSKRAVET_UTDANNING_IKKE_DOKUMENTERT;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak.IKKE_STØNADSDAGER_IGJEN;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderUttakDokumentasjonBekreftelse.DokumentasjonVurderingBehov;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder.VURDER_FEILUTBETALING_KODE;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType.MINSTERETT;
import static no.nav.foreldrepenger.common.domain.BrukerRolle.FAR;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet.ARBEID;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet.ARBEID_OG_UTDANNING;
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
import static no.nav.foreldrepenger.generator.soknad.maler.VedleggMaler.dokumenterTermin;
import static no.nav.foreldrepenger.generator.soknad.maler.VedleggMaler.dokumenterUttak;
import static no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil.helgejustertTilMandag;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fptilbake.TilbakekrevingSaksbehandler;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.brev.BrevAssertionBuilder;
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
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurdereAnnenYtelseFørVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvKlageNfpBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAdopsjonsdokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAleneomsorgBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAnnenForeldreHarRett;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.KontrollerBesteberegningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.SjekkManglendeFødselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.SjekkTerminbekreftelseBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderUttakDokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.VilkarTypeKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.feriepenger.Feriepengeandel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.DekningsgradDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.DokumentTag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.felles.DokumentType;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.common.innsyn.DekningsgradSak;
import no.nav.foreldrepenger.generator.familie.Familie;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Prosent;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler;
import no.nav.foreldrepenger.generator.soknad.maler.UttaksperiodeType;
import no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil;
import no.nav.foreldrepenger.kontrakter.risk.kodeverk.RisikoklasseType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.VedleggDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.VedleggInnsendingType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.ettersendelse.YtelseType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.uttaksplan.KontoType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.uttaksplan.UttaksPeriodeDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.uttaksplan.UttaksplanDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.ÅpenPeriodeDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.AnnenforelderBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.BarnBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.OpptjeningMaler;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.GrunnlagDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PermisjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.Permisjonstype;


@Tag("verdikjede")
class VerdikjedeForeldrepenger extends VerdikjedeTestBase {

    @Test
    @DisplayName("1: Mor automatisk førstegangssøknad termin (med fødselshendelse), aleneomsorg og avvik i beregning.")
    @Description("Mor førstegangssøknad før fødsel på termin. Mor har aleneomsorg og enerett. Sender inn IM med over "
            + "25% avvik med delvis refusjon. Etter behandlingen er ferdigbehandlet mottas en fødselshendelse.")
    void testcase_mor_fødsel() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();

        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(1);
        var fpStartdato = termindato.minusWeeks(3);
        var fordeling = List.of(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, termindato.minusDays(1)),
                uttaksperiode(StønadskontoType.FORELDREPENGER, termindato, termindato.plusWeeks(15).minusDays(1)),
                uttaksperiode(StønadskontoType.FORELDREPENGER, termindato.plusWeeks(20), termindato.plusWeeks(36).minusDays(1)));
        var søknad = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskIkkeRettAleneomsorg(familie.far()))
                .medVedlegg(List.of(dokumenterTermin(VedleggInnsendingType.SEND_SENERE)))
                .medMottattdato(termindato.minusWeeks(5));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        var månedsinntekt = mor.månedsinntekt();
        var avvikendeMånedsinntekt = månedsinntekt * 1.3;
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medBeregnetInntekt(BigDecimal.valueOf(avvikendeMånedsinntekt))
                .medRefusjonBeløpPerMnd(BigDecimal.valueOf(månedsinntekt * 0.6));

        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);
        var brevAssertionsBuilder = BrevAssertionBuilder.ny()
                .medOverskriftOmViHarBedtOmOpplysningerFraArbeidsgiverenDin()
                .medTekstOmAtViHarBedtArbeidsgiverenOmInntektsmelding()
                .medTekstOmDuKanSeBortFreDenneOmArbeidsgiverenHarSendt();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.ETTERLYS_INNTEKTSMELDING,
                HistorikkType.BREV_SENDT);

        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(
                new VurderBeregnetInntektsAvvikBekreftelse()).leggTilInntekt(månedsinntekt * 12, 1).setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        var avklarFaktaAleneomsorgBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaAleneomsorgBekreftelse())
                .bekreftBrukerHarAleneomsorg()
                .setBegrunnelse("Bekreftelse sendt fra Autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAleneomsorgBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, false, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo()).as(
                "Saldo for stønadskontoen FORELDREPENGER_FØR_FØDSEL").isZero();

        var forventetRestPåKonto = 75;
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER).saldo()).as(
                "Saldoen for stønadskontoen FORELDREPENGER").isEqualTo(forventetRestPåKonto);

        var forventetDagsats = 1846;
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getDagsats()).as(
                        "Forventer at dagsatsen blir justert ut i fra årsinntekten og utbeatlinsggrad, og IKKE 6G fordi inntekten er under 6G!")
                .isEqualTo(forventetDagsats);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(60)).as(
                "Forventer at 40% summen utbetales til søker og 60% av summen til arbeisdgiver pga 60% refusjon!").isTrue();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(mor.fødselsnummer(), saksnummer).medTekstOmAleneomsorg()
                .medTekstOmXDagerIgjenAvPeriodenMed(forventetRestPåKonto)
                .medTekstOmDuFårXKronerPerDagFørSkatt(forventetDagsats)
                .medTekstOmOpplysningerFraEnArbeidsgiver()
                .medKapittelDetteHarViInnvilget()
                .medParagrafer(P_8_30, P_14_10, P_14_15);
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        // Fødselshendelse
        familie.sendInnFødselshendelse(termindato.minusWeeks(1));

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().getFirst().behandlingArsakType()).as(
                "Årsakskode til revuderingen").isEqualTo(RE_HENDELSE_FØDSEL);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);

        // Verifiser riktig justering av kontoer og uttak.
        saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo()).as(
                "Saldo for stønadskontoen FORELDREPENGER_FØR_FØDSEL").isEqualTo(5);
        var forventetRestForeldrepenger = 70;
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER).saldo()).as(
                "Saldo for stønadskontoen FORELDREPENGER").isEqualTo(forventetRestForeldrepenger);

        var feriepenger = saksbehandler.valgtBehandling.getFeriepengegrunnlag();
        assertThat(feriepenger).isNotNull();
        var feriepengerTilArbeidsgiver = oppsummerFeriepengerForArbeidsgiver(feriepenger.andeler(),
                arbeidsgiver.arbeidsgiverIdentifikator().value(), false);
        var feriepengerTilSøker = oppsummerFeriepengerForArbeidsgiver(feriepenger.andeler(),
                arbeidsgiver.arbeidsgiverIdentifikator().value(), true);
        assertFeriepenger(feriepengerTilSøker + feriepengerTilArbeidsgiver, 11297);

        brevAssertionsBuilder = foreldrepengerInnvilgetEndringAssertionsBuilder(mor.fødselsnummer(), saksnummer)
                .medTekstOmXDagerIgjenAvPeriodenMed(forventetRestForeldrepenger)
                .medTekstOmForeldrepengerUtgjørDetSammeSomTidligere();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("2: Mor selvstendig næringsdrivende, varig endring. Søker dør etter behandlingen er ferdigbehandlet.")
    @Description("Mor er selvstendig næringsdrivende og har ferdiglignet inntekt i mange år. Oppgir en næringsinntekt"
            + "som avviker med mer enn 25% fra de tre siste ferdiglignede årene. Søker dør etter behandlingen er "
            + "ferdigbehandlet. NB: Må legge til ferdiglignet inntekt for inneværende år -1 etter 1/7")
    void morSelvstendigNæringsdrivendeTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().selvstendigNæringsdrivende(200_000).build()).build())
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build()).build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build();

        var fødselsdato = familie.barn().fødselsdato();
        var mor = familie.mor();
        var næringsinntekt = mor.næringsinntekt();
        // Merk: Avviket er G-sensitivt og kan bli påvirket av g-regulering
        var avvikendeNæringsinntekt = næringsinntekt * 1.9; // >25% avvik
        // Legger inn orgnummer fra 510/organisasjon ettersom det ikke finnes arbeidsforhold for organisasjonen
        var orgnummer = familie.far()
                .arbeidsforhold()
                .arbeidsgiverIdentifikasjon()
                .value(); // TODO: Må legge inn gyldig orgnummer. Instansiere AF via far. Legg til støtte for å nstansiere arbeidsforold som ikke er knyttetr til bruker.
        var opptjening = OpptjeningMaler.egenNaeringOpptjening(orgnummer, mor.næringStartdato(), LocalDate.now(), false,
                avvikendeNæringsinntekt, true);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medSelvstendigNæringsdrivendeInformasjon(opptjening)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fødselsdato.plusWeeks(2));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var vurderVarigEndringEllerNyoppstartetSNBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(
                new VurderVarigEndringEllerNyoppstartetSNBekreftelse()).setErVarigEndretNaering(false).setBegrunnelse("Ingen endring");
        saksbehandler.bekreftAksjonspunkt(vurderVarigEndringEllerNyoppstartetSNBekreftelse);
        var vurderVarigEndringEllerNyoppstartetSNBekreftelse1 = saksbehandler.hentAksjonspunktbekreftelse(
                        new VurderVarigEndringEllerNyoppstartetSNBekreftelse())
                .setErVarigEndretNaering(true)
                .setBruttoBeregningsgrunnlag((int) avvikendeNæringsinntekt)
                .setBegrunnelse("Vurder varig endring for selvstendig næringsdrivende begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderVarigEndringEllerNyoppstartetSNBekreftelse1);

        // verifiser skjæringstidspunkt i følge søknad
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getSkjaeringstidspunktBeregning()).as(
                "Skæringstidspunkt beregning").isEqualTo(fødselsdato.minusWeeks(3));

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, false, false);
        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        assertThat(saksbehandler.hentUnikeBeregningAktivitetStatus()).as(
                        "Forventer at søker får utbetaling med status SN og bare det!")
                .contains(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)
                .hasSize(1);

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo()).as(
                "Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL").isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo()).as(
                "Saldoen for stønadskonton MØDREKVOTE").isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo()).as(
                "Saldoen for stønadskonton FELLESPERIODE").isZero();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0)).as(
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!").isTrue();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(mor.fødselsnummer(), saksnummer)
                .medTekstOmDuFårXKronerPerDagFørSkatt((int) Math.ceil(avvikendeNæringsinntekt / 260))
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medEgenndefinertAssertion(
                        "Vi har beregnet foreldrepengene dine ut fra en årsinntekt på %s kroner. Dette er gjennomsnittet av inntekten du hadde i ".formatted(
                                formaterKroner((int) avvikendeNæringsinntekt)))
                .medParagraf(P_8_35);
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        var dødsdato = LocalDate.now().minusDays(1);
        familie.sendInnDødshendelse(mor.fødselsnummer(), dødsdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().getFirst().behandlingArsakType()).as("Behandlingsårsakstype")
                .isEqualTo(RE_HENDELSE_DØD_FORELDER);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(
                new FastsettUttaksperioderManueltBekreftelse()).avslåManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new FastsetteUttakKontrollerOpplysningerOmDødDto());

        if (saksbehandler.harAksjonspunkt(VURDER_FEILUTBETALING_KODE)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(
                    new VurderTilbakekrevingVedNegativSimulering()).avventSamordningIngenTilbakekreving();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        }
        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, true, false);

        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Avslåtte uttaksperioder").hasSize(3);

        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats()).as("Dagsats tilkjent ytelse periode #2").isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats()).as("Dagsats tilkjent ytelse periode #3").isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(4).getDagsats()).as("Dagsats tilkjent ytelse periode #4").isZero();

        var feriepenger = saksbehandler.valgtBehandling.getFeriepengegrunnlag();
        assertThat(feriepenger).isNull();

        brevAssertionsBuilder = foreldrepengerInnvilgetEndringAssertionsBuilder(mor.fødselsnummer(), saksnummer)
                .medTekstOmForeldrepengerUtgjørDetSammeSomTidligere()
                .medKapittelDetteHarViAvslått();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("3: Mor, sykepenger, kun ytelse, papirsøknad")
    @Description("Mor søker fullt uttak, men søker mer enn det hun har rett til. Klager på førstegangsbehandlingen og "
            + "vedtaket stadfestes. Søker anker stadfestelsen og saksbehanlder oppretter en ankebehandling. Bruker får "
            + "omgjøring i anke")
    void morSykepengerKunYtelseTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny()
                        .infotrygd(GrunnlagDto.Ytelse.SP, LocalDate.now().minusMonths(9), LocalDate.now(), GrunnlagDto.Status.LØPENDE,
                                LocalDate.now().minusDays(2))
                        .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusYears(1), 480_000)
                        .build()).build())
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
        var foreldrepengerFørFødsel = new PermisjonPeriodeDto(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, termindato.minusDays(1));
        var mødrekvote = new PermisjonPeriodeDto(MØDREKVOTE, termindato, termindato.plusWeeks(20).minusDays(1));
        fordelingDtoMor.permisjonsPerioder.add(foreldrepengerFørFødsel);
        fordelingDtoMor.permisjonsPerioder.add(mødrekvote);
        var papirSoknadForeldrepengerBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(
                        new PapirSoknadForeldrepengerBekreftelse())
                .morSøkerTermin(fordelingDtoMor, termindato, fpMottatDato, DekningsgradDto.AATI);
        saksbehandler.bekreftAksjonspunkt(papirSoknadForeldrepengerBekreftelse);

        var sjekkTerminbekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new SjekkTerminbekreftelseBekreftelse())
                .setUtstedtdato(termindato.minusWeeks(10))
                .setBegrunnelse("Begrunnelse fra autotest");
        saksbehandler.bekreftAksjonspunkt(sjekkTerminbekreftelse);

        assertThat(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER")).as(
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som "
                        + "er forut for permisjonen på skjæringstidspunktet!").isTrue();

        var vurderFaktaOmBeregningBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse());
        var beløpYtelse = 10_000;
        vurderFaktaOmBeregningBekreftelse.leggTilAndelerYtelse(beløpYtelse, Inntektskategori.ARBEIDSTAKER)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(
                new FastsettUttaksperioderManueltBekreftelse()).avslåManuellePerioderMedPeriodeResultatÅrsak(IKKE_STØNADSDAGER_IGJEN);
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, false, false);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0)).as(
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!").isTrue();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = foreldrepengerInnvilget80ProsentAssertionsBuilder(mor.fødselsnummer(), saksnummer)
                .medTekstOmDuFårXKronerPerDagFørSkatt((int) Math.ceil((beløpYtelse * 12 / 260) * 0.8))
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medEgenndefinertAssertion(
                        "Vi har beregnet foreldrepengene dine ut fra en inntekt på %s kroner i måneden før skatt. Dette er gjennomsnittet av inntekten du har fått fra Nav de siste tre månedene.".formatted(
                                formaterKroner(beløpYtelse)))
                .medParagrafer(P_14_9, P_14_10, P_14_12);

        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("4: Far søker resten av fellesperioden og hele fedrekvoten med gradert uttak og opphold mellom disse.")
    @Description("Mor har løpende fagsak med hele mødrekvoten og deler av fellesperioden. Far søker resten av fellesperioden"
            + "og hele fedrekvoten med gradert uttak. Far starter med opphold og har også opphold mellom uttak av"
            + "fellesperioden og fedrekvoten. Far har to arbeidsforhold i samme virksomhet, samme org.nr, men ulik"
            + "arbeidsforholdsID. To inntekstmeldinger sendes inn med refusjon på begge. Far søker med aktivitetskrav arbeid."
            + "Mor har permisjon som trigger deling av fellesperioden og aksjonspunkt om uttaksdokumentasjon")
    void farSøkerForeldrepengerTest() {
        var fødselsdato = LocalDate.now().minusWeeks(25);
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpSluttdatoMor = fødselsdato.plusWeeks(23);
        var fpStartdatoFar = fpSluttdatoMor.plusWeeks(3);
        var morsPermisjonsTom = fpStartdatoFar.plusWeeks(8).minusDays(1);
        var årslønnMor = 480_000;
        var årslønnFar = 720_000;
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(3), årslønnMor,
                                List.of(new PermisjonDto(100, fpStartdatoMor, morsPermisjonsTom,
                                        Permisjonstype.PERMISJON_MED_FORELDREPENGER)))
                        .build()).build())
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny()
                        .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", 50, LocalDate.now().minusYears(2), årslønnFar)
                        .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-002", 50, LocalDate.now().minusYears(4), null)
                        .build()).build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(fødselsdato)
                .build();

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(familie, fødselsdato,
                fpStartdatoMor, fpSluttdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(RisikoklasseType.IKKE_HØY);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var beregningResultatFpMor = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        var dagsatsMor = beregningResultatFpMor.getPerioder()
                .stream()
                .filter(tilkjentYtelsePeriode -> tilkjentYtelsePeriode.getDagsats() > 0)
                .min(Comparator.comparing(BeregningsresultatPeriode::getFom))
                .map(BeregningsresultatPeriode::getDagsats)
                .orElse(0);


        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(familie.mor().fødselsnummer(), saksnummerMor)
                .medTekstOmDuFårXKronerPerDagFørSkatt(dagsatsMor)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medParagraf(P_8_30)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        /*
         * FAR: Søker med to arbeidsforhold i samme virksomhet, orgn.nr, men med ulik
         * arbeidsforholdID. Starter med opphold og starter uttak med resten av fellesperiode etter oppholdet.
         * Far har også opphold mellom uttak av fellesperioden og fedrekvoten. Sender inn 2 IM med ulik
         * arbeidsforholdID og refusjon på begge. Far søker med aktivitetskrav arbeid.
         */
        var far = familie.far();
        var orgNummerFar = far.arbeidsgiver().arbeidsgiverIdentifikator();
        var gradertFellesperiode = graderingsperiodeArbeidstaker(FELLESPERIODE, fpStartdatoFar,
                fpStartdatoFar.plusWeeks(16).minusDays(1), orgNummerFar, 50, ARBEID);
        var gradertFedrekvote = graderingsperiodeArbeidstaker(FEDREKVOTE, fpStartdatoFar.plusWeeks(25),
                fpStartdatoFar.plusWeeks(55).minusDays(1), orgNummerFar, 50);
        var fordelingFar = List.of(gradertFellesperiode,
                // Opphold på 9 uker
                gradertFedrekvote);
        var søknadFar = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor())).medUttaksplan(fordelingFar)
                .medVedlegg(List.of(dokumenterUttak(fordelingFar, MorsAktivitet.ARBEID, VedleggInnsendingType.LASTET_OPP)));
        var saksnummerFar = far.søk(søknadFar.build());


        var arbeidsgiver = far.arbeidsgiver();
        var inntektsmeldingFar = arbeidsgiver.lagInntektsmeldingerFP(fpStartdatoFar, true).getFirst();
        inntektsmeldingFar.medRefusjonBeløpPerMnd(Prosent.valueOf(100));

        ventPåInntektsmeldingForespørsel(saksnummerFar);
        arbeidsgiver.sendInntektsmelding(saksnummerFar, inntektsmeldingFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.bekreftAksjonspunkt(saksbehandler.hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse())
                .ikkeGodkjenn(new ÅpenPeriodeDto(gradertFellesperiode.fom(), morsPermisjonsTom)));

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummerFar, false, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo()).as(
                "saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL").isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo()).as(
                "saldoen for stønadskonton MØDREKVOTE").isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FEDREKVOTE).saldo()).as(
                "saldoen for stønadskonton FEDREKVOTE").isZero();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(100)).as(
                "Forventer at hele summen utbetales til arbeidsgiver, og derfor ingenting til søker!").isTrue();

        var beregningResultatFar = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        var dagsatsFar = beregningResultatFar.getPerioder()
                .stream()
                .filter(tilkjentYtelsePeriode -> tilkjentYtelsePeriode.getDagsats() > 0)
                .min(Comparator.comparing(BeregningsresultatPeriode::getFom))
                .map(BeregningsresultatPeriode::getDagsats)
                .orElse(0);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(far.fødselsnummer(), saksnummerFar).medTekstOmDuFårXKronerPerDagFørSkatt(dagsatsFar)
                .medKapittelDetteHarViInnvilget()
                .medParagrafer(P_14_9, P_14_12, P_14_13, P_14_16)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medParagraf(P_8_30);
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("5: Far søker fellesperiode og fedrekvote som frilanser. Tar ut 2 uker ifm fødsel.")
    @Description("Mor søker hele mødrekvoten og deler av fellesperiode, happy case. Far søker etter fødsel og søker"
            + "noe av fellesperioden og hele fedrekvoten; 2 av disse tas ut ifm fødsel. Opplyser at han er frilanser og har frilanserinntekt frem til"
            + "skjæringstidspunktet. Mor har tidligere hatt permisjon under sitt uttak, men er tilbake i jobb. Far får ikke AP om uttaksdokumentasjon")
    void farSøkerSomFrilanserOgTarUt2UkerIfmFødsel() {
        var fødselsdato = LocalDate.now().minusMonths(4);
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFellesperiodeFar = fødselsdato.plusWeeks(18);
        var årslønnFar = 540_000;
        var årslønnMor = 900_000;

        var familie = FamilieGenerator.ny()
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny().frilans(LocalDate.now().minusYears(2), årslønnFar).build())
                        .build())
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(3), årslønnMor,
                                List.of(new PermisjonDto(100, fpStartdatoMor, fødselsdato.plusMonths(3),
                                        Permisjonstype.PERMISJON_MED_FORELDREPENGER)))
                        .build()).build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(fødselsdato)
                .build();


        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(familie, fødselsdato,
                fpStartdatoMor, fpStartdatoFellesperiodeFar);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(RisikoklasseType.IKKE_HØY);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(familie.mor().fødselsnummer(), saksnummerMor)
                .medTekstOmDuFårXKronerPerDagFørSkatt(SEKS_G_2024 / 260)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medTekstOmInntektOverSeksGBeløp(SEKS_G_2024)
                .medParagraf(P_8_30)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

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
                .medFrilansInformasjon(opptjeningFar)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medUttaksplan(fordelingFar)
                .medMottattdato(fødselsdato.minusWeeks(1))
                .medVedlegg(List.of(dokumenterUttak(fordelingFar, MorsAktivitet.ARBEID, VedleggInnsendingType.AUTOMATISK)));
        var saksnummerFar = far.søk(søknadFar.build());

        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.sjekkOmDetErOpptjeningFremTilSkjæringstidspunktet("FRILANS")).as(
                        "Forventer at det er registert en opptjeningsaktivitet med aktivitettype FRILANSER som har frilansinntekt på skjæringstidspunktet!")
                .isTrue();

        saksbehandler.ventTilAvsluttetBehandling();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0)).as(
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!").isTrue();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(far.fødselsnummer(), saksnummerFar)
                .medTekstOmDuFårXKronerPerDagFørSkatt((int) Math.ceil((double) årslønnFar / 260))
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medKapittelDetteHarViInnvilget()
                .medParagrafer(P_14_9, P_14_12, P_14_13)
                .medEgenndefinertAssertion(
                        "Inntekten din som frilanser er %s kroner i måneden. Oppdragsgiveren eller oppdragsgiverne dine har gitt oss disse opplysningene.".formatted(
                                formaterKroner(årslønnFar / 12)))
                .medEgenndefinertAssertion(
                        "Dette er gjennomsnittet av inntekten din fra de siste tre månedene. Hvis du nettopp har begynt å arbeide som frilanser, har vi brukt inntektene etter at du startet.")
                .medParagraf(P_8_38);
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        saksbehandler.hentFagsak(saksnummerMor);
        assertThat(saksbehandler.harRevurderingBehandling()).as("Mor skal ikke få berørt behandling pga samtidig uttak ifm fødsel")
                .isFalse();
    }

    @Test
    @DisplayName("6: Bare Far har rett (BFHR) søker foreldrepenger med AF som ikke er avsluttet. Utsettelse i midten. Gradert uttak ifm fødsel.")
    @Description("Far søker foreldrepenger med to aktive arbeidsforhold og ett gammelt arbeidsforhold som skulle vært "
            + "avsluttet men er ikke det. Far søker gradering i ett av disse AFene med utsettelsesperiode i midten."
            + "I dette arbeidsforholdet gjennopptar han full deltidsstilling og AG vil har full refusjon i hele perioden."
            + "I det andre arbeidsforholdet vil AG bare ha refusjon i to måneder. Søker også gradert uttak ifm fødsel."
            + "Far sender deretter inn endringssøknad med utsatt oppstart. "
            + "Mor dokumentasjon automatisk godkjent med unntak av arbeid og utdanning som må vurders.")
    void farSøkerMedToAktiveArbeidsforholdOgEtInaktivtTest() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build()).build())
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny()
                        .arbeidsforhold(TestOrganisasjoner.NAV, 60, LocalDate.now().minusYears(2), 360_000)
                        .arbeidsforhold(TestOrganisasjoner.NAV_BERGEN, 40, LocalDate.now().minusYears(4), 240_000)
                        .arbeidsforholdUtenInntekt(TestOrganisasjoner.NAV_STORD, LocalDate.now().minusYears(8))
                        .build()).build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(3))
                .build();

        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();
        var arbeidsforhold1 = far.arbeidsforhold(TestOrganisasjoner.NAV.orgnummer().value());
        var orgNummerFar1 = arbeidsforhold1.arbeidsgiverIdentifikasjon();
        var stillingsprosent1 = arbeidsforhold1.stillingsprosent();
        var førsteGradertUttaksPeriodeEtterUke6 = graderingsperiodeArbeidstaker(StønadskontoType.FORELDREPENGER,
                fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(56).minusDays(1), orgNummerFar1, stillingsprosent1, ARBEID);
        var fpStartdatoEtterUke6Far = førsteGradertUttaksPeriodeEtterUke6.fom();
        var fordelingFar = List.of(
                graderingsperiodeArbeidstaker(StønadskontoType.FORELDREPENGER, fødselsdato.minusWeeks(2),
                        fødselsdato.plusWeeks(2).minusDays(1), orgNummerFar1, 50, ARBEID),
                førsteGradertUttaksPeriodeEtterUke6,
                utsettelsesperiode(UtsettelsesÅrsak.FRI, førsteGradertUttaksPeriodeEtterUke6.tom().plusDays(1),
                        førsteGradertUttaksPeriodeEtterUke6.tom().plusWeeks(2), ARBEID),
                utsettelsesperiode(UtsettelsesÅrsak.FRI, førsteGradertUttaksPeriodeEtterUke6.tom().plusWeeks(2).plusDays(1),
                        førsteGradertUttaksPeriodeEtterUke6.tom().plusWeeks(4), ARBEID_OG_UTDANNING),
                graderingsperiodeArbeidstaker(StønadskontoType.FORELDREPENGER,
                        førsteGradertUttaksPeriodeEtterUke6.tom().plusWeeks(4).plusDays(1),
                        førsteGradertUttaksPeriodeEtterUke6.tom().plusWeeks(14), orgNummerFar1, stillingsprosent1, ARBEID));
        var fpStartdatoIfmFødselFar = fødselsdato.minusWeeks(2);
        var søknadFar = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medUttaksplan(fordelingFar)
                .medAnnenForelder(AnnenforelderMaler.norskIkkeRett(familie.mor()))
                .medMottattdato(fødselsdato.minusWeeks(2))
                .medVedlegg(List.of(
                        dokumenterUttak(fordelingFar, MorsAktivitet.ARBEID, VedleggInnsendingType.AUTOMATISK),
                        dokumenterUttak(fordelingFar, MorsAktivitet.ARBEID_OG_UTDANNING, VedleggInnsendingType.LASTET_OPP)
                ))
                .build();
        var saksnummerFar = far.søk(søknadFar);

        var arbeidsgiver1 = far.arbeidsgiver(TestOrganisasjoner.NAV.orgnummer().value());
        var inntektsmelding1 = arbeidsgiver1.lagInntektsmeldingFP(fpStartdatoIfmFødselFar)
                .medRefusjonBeløpPerMnd(Prosent.valueOf(100));

        ventPåInntektsmeldingForespørsel(saksnummerFar);
        arbeidsgiver1.sendInntektsmelding(saksnummerFar, inntektsmelding1);

        var arbeidsgiver2 = far.arbeidsgiver(TestOrganisasjoner.NAV_BERGEN.orgnummer().value());
        var orgNummerFar2 = arbeidsgiver2.arbeidsgiverIdentifikator();
        var opphørsDatoForRefusjon = fpStartdatoEtterUke6Far.plusMonths(2).minusDays(1);
        var inntektsmelding2 = arbeidsgiver2.lagInntektsmeldingFP(fpStartdatoIfmFødselFar)
                .medRefusjonBeløpPerMnd(Prosent.valueOf(100))
                .medRefusjonsOpphordato(opphørsDatoForRefusjon);
        arbeidsgiver2.sendInntektsmelding(saksnummerFar, inntektsmelding2);

        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaAnnenForeldreHarRett = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaAnnenForeldreHarRett())
                .setAnnenforelderHarRett(false)
                .setBegrunnelse("Bare far har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        var vurderUttakDokumentasjonBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse());
        var vurderingsbehov = vurderUttakDokumentasjonBekreftelse.getVurderingBehov()
                .stream()
                .sorted(Comparator.comparing(DokumentasjonVurderingBehov::tom))
                .toList();
        assertThat(vurderingsbehov.get(0).vurdering()).isEqualTo(DokumentasjonVurderingBehov.Vurdering.GODKJENT_AUTOMATISK);
        assertThat(vurderingsbehov.get(1).vurdering()).isEqualTo(DokumentasjonVurderingBehov.Vurdering.GODKJENT_AUTOMATISK);
        assertThat(vurderingsbehov.get(2).vurdering()).isEqualTo(DokumentasjonVurderingBehov.Vurdering.GODKJENT_AUTOMATISK); // Godkjent utsettelse ARBEID
        assertThat(vurderingsbehov.get(3).vurdering()).isNull(); // Utsettelse ARBEID_OG_UTDANNING
        assertThat(vurderingsbehov.get(4).vurdering()).isEqualTo(DokumentasjonVurderingBehov.Vurdering.GODKJENT_AUTOMATISK);
        vurderUttakDokumentasjonBekreftelse.godkjenn(fordelingFar.get(3));
        saksbehandler.bekreftAksjonspunkt(vurderUttakDokumentasjonBekreftelse.godkjenn());

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummerFar, false, false);

        /* VERIFISERINGER */
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // SALDO
        assertThat(saksbehandler.valgtBehandling.getSaldoer()
                .stonadskontoer()
                .get(SaldoVisningStønadskontoType.FORELDREPENGER)
                .saldo()).as("Saldoen for stønadskonton FORELDREPENGER").isEqualTo(14 * 5);
        assertThat(
                saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(SaldoVisningStønadskontoType.MINSTERETT).saldo()).as(
                "Saldoen for stønadskonton MINSTRETT").isEqualTo(10 * 5);
        assertThat(saksbehandler.valgtBehandling.getSaldoer()
                .stonadskontoer()
                .get(SaldoVisningStønadskontoType.MINSTERETT)
                .maxDager()).as("Maxdager for stønadskonton MINSTRETT").isEqualTo(10 * 5);

        // UTTAK
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Avslåtte uttaksperioder").isEmpty();
        // Utsettelse skal være innvilget, riktig årsak og skal ikke trekke dager
        var uttakResultatPeriode = saksbehandler.valgtBehandling.hentUttaksperiode(4);
        assertThat(uttakResultatPeriode.getPeriodeResultatÅrsak()).as("Perioderesultatårsak")
                .isEqualTo(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_BFR_AKT_KRAV_OPPFYLT);
        assertThat(uttakResultatPeriode.getAktiviteter().getFirst().getTrekkdagerDesimaler()).as("Trekkdager").isZero();
        assertThat(uttakResultatPeriode.getAktiviteter().get(1).getTrekkdagerDesimaler()).as("Trekkdager").isZero();

        // TILKJENT YTELSE
        var beregningsresultatPerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder();
        assertThat(beregningsresultatPerioder).as("Beregningsresultatperidoer").hasSize(8);

        var andelerForAT1 = saksbehandler.hentBeregningsresultatPerioderMedAndelIArbeidsforhold(orgNummerFar1);
        var andelerForAT2 = saksbehandler.hentBeregningsresultatPerioderMedAndelIArbeidsforhold(orgNummerFar2);
        // IFM fødsel
        assertThat(beregningsresultatPerioder.getFirst().getDagsats()).as("Dagsatsen for perioden").isEqualTo(1616);
        assertThat(andelerForAT1.getFirst().getTilSoker()).as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(462);
        assertThat(andelerForAT2.getFirst().getRefusjon()).as(
                "Forventer at dagsatsen matchen den kalkulerte og alt går til arbeidsgiver").isEqualTo(923);

        // Første uttaksperiode etter uke 6
        assertThat(beregningsresultatPerioder.get(2).getDagsats()).as("Dagsatsen for perioden").isEqualTo(1477);
        assertThat(andelerForAT1.get(2).getTilSoker()).as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(554);
        assertThat(andelerForAT2.get(2).getRefusjon()).as("Forventer at dagsatsen matchen den kalkulerte og alt går til arbeidsgiver")
                .isEqualTo(923);

        // Endring i refusjon, flyttes til søker.
        assertThat(beregningsresultatPerioder.get(3).getDagsats()).as(
                "Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel").isEqualTo(1477);
        assertThat(andelerForAT1.get(3).getTilSoker()).as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(554);
        assertThat(andelerForAT2.get(3).getTilSoker()).as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(923);

        // Opphører IM med refusjon
        assertThat(beregningsresultatPerioder.get(4).getDagsats()).as(
                "Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel").isEqualTo(554);
        assertThat(andelerForAT1.get(4).getTilSoker()).as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(554);
        assertThat(andelerForAT2.get(4).getTilSoker()).as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isZero();

        assertThat(beregningsresultatPerioder.get(5).getDagsats()).as("Forventer at dagsatsen for utsettelsen er null").isZero();
        assertThat(beregningsresultatPerioder.get(6).getDagsats()).as("Forventer at dagsatsen for utsettelsen er null").isZero();

        assertThat(beregningsresultatPerioder.get(7).getDagsats()).as(
                "Forventer at dagsatsen for perioden matcher summen av den kalkulerte dagsatsen for hver andel").isEqualTo(554);
        assertThat(andelerForAT1.get(7).getTilSoker()).as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isEqualTo(554);
        assertThat(andelerForAT2.get(7).getTilSoker()).as("Forventer at dagsatsen matchen den kalkulerte og alt går til søker")
                .isZero();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(far.fødselsnummer(), saksnummerFar)
                .medParagrafer(P_14_13, P_14_14, P_14_16, P_8_30)
                .medEgenndefinertAssertion(
                        "I periodene du kombinerer arbeid og foreldrepenger, får du utbetalt fulle foreldrepenger der du ikke jobber.")
                .medKapittelDuHarFlereAgbeidsgivere()
                .medTekstOmOpplysningerFraFlereArbeidsgivere()
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medTekstOmDenAndreForelderenIkkeHarRettDerforFårDuAlt();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        // Endringssøknad: Far bestemmer seg for utsatt oppstart
        var fordelingGiFraSegAlt = fordeling(
                utsettelsesperiode(UtsettelsesÅrsak.FRI, fpStartdatoIfmFødselFar, fpStartdatoIfmFødselFar.plusDays(1)),
                uttaksperiode(StønadskontoType.FORELDREPENGER, fødselsdato.plusWeeks(30), fødselsdato.plusWeeks(56).minusDays(1), ARBEID));
        var endringssøknadBuilder = lagEndringssøknad(søknadFar, saksnummerFar, fordelingGiFraSegAlt);
        far.søk(endringssøknadBuilder.build());

        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(
                new VurderTilbakekrevingVedNegativSimulering());
        vurderTilbakekrevingVedNegativSimulering.tilbakekrevingUtenVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_SENERE);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperioder()).as("Uttaksperioder for valgt behandling").isEmpty();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        hentBrevOgSjekkAtInnholdetErRiktig(foreldrepengerAnnuleringAssertionsBuilder(familie.far().fødselsnummer(), saksnummerFar),
                DokumentTag.FORELDREPENGER_ANNULERING, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("7: Far har AAP og søker overføring av gjennværende mødrekvoten fordi mor er syk.")
    @Description("Mor har løpende sak hvor hun har søkt om hele mødrekvoten og deler av fellesperioden. Mor blir syk 4"
            + "uker inn i mødrekvoten og far søker om overføring av resten. Far søker ikke overføring av fellesperioden."
            + "Far får innvilget mødrevkoten og mor sin sak blir berørt og automatisk revurdert.")
    void FarTestMorSyk() {
        var ytelseFar = 260_000;
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build()).build())
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny()
                        .arena(ArenaSakerDto.YtelseTema.AAP, LocalDate.now().minusMonths(12), LocalDate.now().plusMonths(2), ytelseFar)
                        .build()).build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(6))
                .build();

        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFarOrdinær = fødselsdato.plusWeeks(23);
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(familie, fødselsdato,
                fpStartdatoMor, fpStartdatoFarOrdinær);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(RisikoklasseType.IKKE_HØY);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);

        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(familie.mor().fødselsnummer(), saksnummerMor).medTekstOmDuFårXKronerPerDagFørSkatt(DAGSATS_VED_6_G_2025)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medTekstOmInntektOverSeksGBeløp(SEKS_G_2025)
                .medParagraf(P_8_30)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        /*
         * FAR: Søker overføring av mødrekvoten fordi mor er syk innenfor de 6 første
         * uker av mødrekvoten.
         */
        var far = familie.far();
        var fpStartdatoFarEndret = fødselsdato.plusWeeks(4);
        var overføringsperiodeEndring = overføringsperiode(SYKDOM_ANNEN_FORELDER, MØDREKVOTE, fpStartdatoFarEndret,
                fødselsdato.plusWeeks(15).minusDays(1));
        var uttaksperiodeEndring = uttaksperiode(FEDREKVOTE, fpStartdatoFarOrdinær, fpStartdatoFarOrdinær.plusWeeks(15).minusDays(1));
        var fordelingFar = List.of(overføringsperiodeEndring, uttaksperiodeEndring);
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR).medUttaksplan(fordelingFar)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medMottattdato(fødselsdato.plusWeeks(6));
        var saksnummerFar = far.søk(søknadFar.build());

        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaUttakPerioder = saksbehandler.hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse())
                .godkjenn(overføringsperiodeEndring);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakPerioder);

        var beregningAktivitetStatus = saksbehandler.hentUnikeBeregningAktivitetStatus();
        assertThat(beregningAktivitetStatus).as("Forventer at beregningsstatusen er APP!")
                .containsOnly(AktivitetStatus.ARBEIDSAVKLARINGSPENGER);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getRedusertPrAar()).as(
                "Forventer at beregningsgrunnlaget baserer seg på en årsinntekt større enn 0. Søker har bare AAP.").isPositive();

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummerFar, false, false);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var dagsats = ytelseFar / 260;
        brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(far.fødselsnummer(), saksnummerFar).medTekstOmDuFårXKronerPerDagFørSkatt(dagsats)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medKapittelDetteHarViInnvilget()
                .medParagraf(P_14_12)
                .medEgenndefinertAssertion(
                        "Du fikk %s kroner per dag før skatt i arbeidsavklaringspenger. Vi har brukt dette beløpet i beregningen av foreldrepengene dine.".formatted(
                                formaterKroner(dagsats)));
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        /* Mor: berørt sak */
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        // Løser AP 5084 negativ simulering! Oppretter tilbakekreving og sjekk at den er opprette. Ikke løs det.
        if (saksbehandler.harAksjonspunkt(VURDER_FEILUTBETALING_KODE)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(
                    new VurderTilbakekrevingVedNegativSimulering());
            vurderTilbakekrevingVedNegativSimulering.tilbakekrevingUtenVarsel();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
            saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        }

        saksbehandler.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as(
                        "Foreldrepenger skal være endret pga annenpart har overlappende uttak!")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Forventer at det er 2 avslåtte uttaksperioder").hasSize(2);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(2).getPeriodeResultatÅrsak().isAvslåttÅrsak()).as(
                "Perioden burde være avslått fordi annenpart har overlappende uttak!").isTrue();
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(3).getPeriodeResultatÅrsak().isAvslåttÅrsak()).as(
                "Perioden burde være avslått fordi annenpart har overlappende uttak!").isTrue();


        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats()).as("Siden perioden er avslått, forventes det 0 i dagsats")
                .isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats()).as("Siden perioden er avslått, forventes det 0 i dagsats")
                .isZero();

        if (saksbehandler.harAksjonspunkt(VURDER_FEILUTBETALING_KODE)) {
            var tbksaksbehandler = new TilbakekrevingSaksbehandler(SaksbehandlerRolle.SAKSBEHANDLER);
            tbksaksbehandler.hentSisteBehandling(saksnummerMor);
            tbksaksbehandler.ventTilBehandlingErPåVent();
            assertThat(tbksaksbehandler.valgtBehandling.venteArsakKode).as("Behandling har feil vent årsak")
                    .isEqualTo("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG");
        }

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilgetEndringAssertionsBuilder(familie.mor().fødselsnummer(), saksnummerMor)
                .medTekstOmForeldrepengerUtgjørDetSammeSomTidligere()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("8: Mor har tvillinger og søker om hele utvidelsen.")
    @Description("Mor føder tvillinger og søker om hele mødrekvoten og fellesperioden, inkludert utvidelse. Far søker "
            + "samtidig uttak av fellesperioden fra da mor starter utvidelsen av fellesperioden. Søker deretter samtidig "
            + "av fedrekvoten, frem til mor er ferdig med fellesperioden, og deretter søker resten av fedrekvoten.")
    void MorSøkerFor2BarnHvorHunFårBerørtSakPgaFar() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .forelder(far().inntektytelse(
                                InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().selvstendigNæringsdrivende(1_000_000).build())
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
        var fordelingMor = List.of(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fpStartdatoFar.minusDays(1)),
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(17).minusDays(1), FLERBARNSDAGER));
        var søknadMor = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR).medUttaksplan(fordelingMor)
                .medBarn(BarnBuilder.fødsel(2, fødselsdato).build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fpStartdatoMor.minusWeeks(3));
        var saksnummerMor = mor.søk(søknadMor.build());

        var arbeidsgiverMor = mor.arbeidsgiver();

        ventPåInntektsmeldingForespørsel(saksnummerMor);
        arbeidsgiverMor.sendInntektsmeldingerFP(saksnummerMor, fpStartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        var saldoerFørstgangsbehandling = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(
                saldoerFørstgangsbehandling.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo()).as(
                "Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL").isZero();
        assertThat(saldoerFørstgangsbehandling.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo()).as(
                "Saldoen for stønadskonton MØDREKVOTE").isZero();
        assertThat(saldoerFørstgangsbehandling.stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo()).as(
                "Saldoen for stønadskonton FELLESPERIODE").isZero();

        var feriepenger = saksbehandler.valgtBehandling.getFeriepengegrunnlag();
        assertThat(feriepenger).isNotNull();
        var feriepengerTilArbeidsgiver = oppsummerFeriepengerForArbeidsgiver(feriepenger.andeler(),
                arbeidsgiverMor.arbeidsgiverIdentifikator().value(), false);
        var feriepengerTilSøker = oppsummerFeriepengerForArbeidsgiver(feriepenger.andeler(),
                arbeidsgiverMor.arbeidsgiverIdentifikator().value(), true);
        assertFeriepenger(feriepengerTilSøker + feriepengerTilArbeidsgiver, 11297);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var morDagsats = 480_000 / 260;
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(mor.fødselsnummer(), saksnummerMor)
                .medTekstOmDuFårXKronerPerDagFørSkatt(morDagsats)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medParagraf(P_8_30)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        /*
         * FAR: Søker samtidig uttak med flerbansdager. Søker deretter hele fedrekvoten,
         * også samtidig uttak.
         */
        var far = familie.far();
        var næringsinntekt = far.næringsinntekt();
        var opptjeningFar = OpptjeningMaler.egenNaeringOpptjening(far.arbeidsforhold().arbeidsgiverIdentifikasjon().value(),
                far.næringStartdato(), VirkedagUtil.helgejustertTilMandag(fpStartdatoFar), false, næringsinntekt, false);
        var fordelingFar = List.of(
                uttaksperiode(FELLESPERIODE, fpStartdatoFar, fpStartdatoFar.plusWeeks(4).minusDays(1), FLERBARNSDAGER, SAMTIDIGUTTAK),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(4), fpStartdatoFar.plusWeeks(17).minusDays(1), SAMTIDIGUTTAK),
                uttaksperiode(FEDREKVOTE, fpStartdatoFar.plusWeeks(17), fpStartdatoFar.plusWeeks(19).minusDays(1)));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR).medUttaksplan(fordelingFar)
                .medSelvstendigNæringsdrivendeInformasjon(opptjeningFar)
                .medBarn(BarnBuilder.fødsel(2, fødselsdato).build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()));
        var saksnummerFar = far.søk(søknadFar.build());

        var arbeidsgiverFar = far.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummerFar);
        arbeidsgiverFar.sendInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getAktivitetStatus(0)).as(
                "Forventer at far får kombinert satus i beregning (da AT og SN)").isEqualTo(AktivitetStatus.KOMBINERT_AT_SN);

        var beregningsgrunnlagPeriode = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0);
        var dagsats = beregningsgrunnlagPeriode.getDagsats();
        var redusertPrAar = beregningsgrunnlagPeriode.getRedusertPrAar();
        var prosentfaktorAvDagsatsTilAF =
                beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndel().getFirst().getRedusertPrAar() / redusertPrAar;
        var dagsatsTilAF = (int) Math.round(dagsats * prosentfaktorAvDagsatsTilAF);

        var perioderMedAndelIArbeidsforhold = saksbehandler.hentBeregningsresultatPerioderMedAndelIArbeidsforhold(
                arbeidsgiverFar.arbeidsgiverIdentifikator());
        assertThat(perioderMedAndelIArbeidsforhold.getFirst().getTilSoker()).as(
                "Forventer at dagsatsen for arbeidsforholdet blir beregnet først – rest går til søker for SN").isEqualTo(dagsatsTilAF);
        assertThat(perioderMedAndelIArbeidsforhold.get(1).getTilSoker()).as(
                "Forventer at dagsatsen for arbeidsforholdet blir beregnet først – rest går til søker for SN").isEqualTo(dagsatsTilAF);
        assertThat(perioderMedAndelIArbeidsforhold.get(2).getTilSoker()).as(
                "Forventer at dagsatsen for arbeidsforholdet blir beregnet først – rest går til søker for SN").isEqualTo(dagsatsTilAF);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(
                arbeidsgiverFar.arbeidsgiverIdentifikator(), 0)).as("Foventer at hele den utbetalte dagsatsen går til søker!")
                .isTrue();

        var perioderMedAndelISN = saksbehandler.hentBeregningsresultatPerioderMedAndelISN();
        assertThat(perioderMedAndelISN.getFirst().getTilSoker()).as("Forventer at resten av dagsatsen går til søker for SN")
                .isEqualTo(dagsats - dagsatsTilAF);
        assertThat(perioderMedAndelISN.get(1).getTilSoker()).as("Forventer at resten av dagsatsen går til søker for SN")
                .isEqualTo(dagsats - dagsatsTilAF);
        assertThat(perioderMedAndelISN.get(2).getTilSoker()).as("Forventer at resten av dagsatsen går til søker for SN")
                .isEqualTo(dagsats - dagsatsTilAF);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(far.fødselsnummer(), saksnummerFar)
                .medTekstOmDuFårXKronerPerDagFørSkatt(DAGSATS_VED_6_G_2025)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagraf(P_14_9)
                .medParagraf(P_14_12)
                .medParagraf(P_8_41)
                // OBS: Beløpet som sendes til formidling avrundes ikke riktig. Beregning sier 415 115.99 men formidling sier 415 115 istedenfor 415 116. Matcher ikke med avrunding gjort i skjermbilder
                .medEgenndefinertAssertion(
                        "Næringsinntekten din er fastsatt til 415 115 kroner i året. Når vi beregner foreldrepenger ut fra "
                                + "næringsinntekten din, bruker vi gjennomsnittet av de siste tre årene vi har fått oppgitt av "
                                + "Skatteetaten. Hvis du nettopp har begynt å arbeide, bruker vi inntekten vi har fått opplyst for det "
                                + "siste året. Dette gjennomsnittet kan også inneholde arbeidsinntekten din. De er trukket fra i "
                                + "beregningen av næringsinntekten.")
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET,
                HistorikkType.BREV_SENDT);

        /* Mor: Berørt sak */
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var saldoerBerørtSak = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoerBerørtSak.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo()).as(
                "Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL").isZero();
        assertThat(saldoerBerørtSak.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo()).as(
                "Saldoen for stønadskonton MØDREKVOTE").isZero();
        assertThat(saldoerBerørtSak.stonadskontoer().get(SaldoVisningStønadskontoType.FEDREKVOTE).saldo()).as(
                "Saldoen for stønadskonton FEDREKVOTE").isZero();
        assertThat(saldoerBerørtSak.stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo()).as(
                "Saldoen for stønadskonton FELLESPERIODE").isZero();

        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Avslåtte uttaksperioder").hasSize(1);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(6).getPeriodeResultatÅrsak().isAvslåttÅrsak()).as(
                "Perioden burde være avslått fordi det er ingen stønadsdager igjen på stønadskontoen").isTrue();
        assertThat(saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().get(6).getDagsats()).as(
                "Siden perioden er avslått, forventes det 0 i dagsats i tilkjent ytelse").isZero();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilgetEndringAssertionsBuilder(mor.fødselsnummer(), saksnummerMor)
                .medTekstOmForeldrepengerUtgjørDetSammeSomTidligere()
                .medKapittelDetteHarViAvslått()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("9: Mor søker med dagpenger som grunnlag, besteberegnes automatisk")
    @Description("Mor søker med dagpenger som grunnlag. Kvalifiserer til automatisk besteberegning."
            + "Beregning etter etter §14-7, 3. ledd gir høyere inntekt enn beregning etter §14-7, 1. ledd")
    void MorSøkerMedDagpengerTest() {
        var ytelseMor = 260_000;
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusMonths(10), LocalDate.now().minusMonths(5).minusDays(1))
                        .arena(ArenaSakerDto.YtelseTema.DAG, LocalDate.now().minusMonths(5), LocalDate.now().minusWeeks(5), ytelseMor)
                        .build()).build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(2))
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);
        var fordelingMor = List.of(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(31).minusDays(1)));

        var søknadMor = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR).medUttaksplan(fordelingMor)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummerMor = mor.søk(søknadMor.build());

        saksbehandler.hentFagsak(saksnummerMor);
        assertThat(saksbehandler.sjekkOmDetErOpptjeningFremTilSkjæringstidspunktet("DAGPENGER")).as(
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype DAGPENGER med "
                        + "opptjening frem til skjæringstidspunktet for opptjening.").isTrue();

        var bekreftKorrektBesteberegninging = saksbehandler.hentAksjonspunktbekreftelse(new KontrollerBesteberegningBekreftelse())
                .godkjenn()
                .setBegrunnelse("Besteberegning godkjent av autotest.");
        saksbehandler.bekreftAksjonspunkt(bekreftKorrektBesteberegninging);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0)).as(
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!").isTrue();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(mor.fødselsnummer(), saksnummerMor)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medEgenndefinertAssertion(
                        "Når du har mottatt dagpenger i forkant av foreldrepenger kan du få beregnet foreldrepenger ut i "
                                + "fra de 6 beste av de 10 siste månedene med inntekt, eller etter ordinære beregningsregler "
                                + "avhengig av hva som gir deg best resultat.")
                .medParagraf(P_8_49)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("10: Far, aleneomsorg, søker adopsjon og får revurdert sak 4 måneder senere på grunn av IM med endring i refusjon.")
    @Description("Far, aleneomsorg, søker adopsjon og får revurdert sak 4 måneder senere på grunn av IM med endring i refusjon. "
            + "Mens behandlingen er hos beslutter sender AG en ny korrigert IM. Behandlingen rulles tilbake. På den "
            + "siste IM som AG sender ber AG om full refusjon, men kommer for sent til å få alt. AG får refusjon for"
            + "den inneværende måneden og tre måneder tilbake i tid; tiden før dette skal gå til søker.")
    void FarSøkerAdopsjonAleneomsorgOgRevurderingPgaEndringIRefusjonFraAG() {
        var familie = FamilieGenerator.ny()
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build()).build())
                .forelder(mor().build())
                .barn(LocalDate.now().minusYears(10))
                .build();

        /* FAR */
        var far = familie.far();
        var omsorgsovertakelsedatoe = LocalDate.now().minusMonths(4).minusWeeks(1);
        var fpStartdatoFar = omsorgsovertakelsedatoe;
        var fordelingFar = List.of(
                uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar, fpStartdatoFar.plusWeeks(46).minusDays(1)));
        var søknadFar = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, BrukerRolle.FAR, false).medUttaksplan(fordelingFar)
                .medAnnenForelder(AnnenforelderBuilder.ukjentForelder())
                .medMottattdato(fpStartdatoFar.minusWeeks(3));
        var saksnummerFar = far.søk(søknadFar.build());
        var arbeidsgiver = far.arbeidsgiver();

        ventPåInntektsmeldingForespørsel(saksnummerFar);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler.hentAksjonspunktbekreftelse(
                new AvklarFaktaAdopsjonsdokumentasjonBekreftelse()).setBegrunnelse("Adopsjon behandlet av Autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);
        var avklarFaktaAleneomsorgBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaAleneomsorgBekreftelse())
                .bekreftBrukerHarAleneomsorg();
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAleneomsorgBekreftelse);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Forventer søkt periode blir innvilget").isEmpty();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0)).as(
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!").isTrue();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var dagsats = SEKS_G_2024 / 260;
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(far.fødselsnummer(), saksnummerFar)
                .medTekstOmDuFårXKronerPerDagFørSkatt(dagsats)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medTekstOmAleneomsorg()
                .medParagraf(P_14_15)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medTekstOmInntektOverSeksGBeløp(SEKS_G_2024)
                .medParagraf(P_8_30)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        // AG sender inn en IM med endring i refusjon som skal føre til revurdering på far sin sak.
        var inntektsmeldingEndringFar = arbeidsgiver.lagInntektsmeldingFP(fpStartdatoFar).medRefusjonBeløpPerMnd(Prosent.valueOf(50));

        ventPåInntektsmeldingForespørsel(saksnummerFar);
        arbeidsgiver.sendInntektsmelding(saksnummerFar, inntektsmeldingEndringFar);

        // Revurdering / Berørt sak til far
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var vurderFaktaOmBeregningBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
                .leggTilRefusjonGyldighetVurdering(arbeidsgiver.arbeidsgiverIdentifikator(), false)
                .setBegrunnelse("Refusjonskrav er sendt inn for sent og skal ikke tas med i beregning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        var vurderRefusjonBeregningsgrunnlagBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(
                new VurderRefusjonBeregningsgrunnlagBekreftelse());
        vurderRefusjonBeregningsgrunnlagBekreftelse.setFastsattRefusjonFomForAllePerioder(LocalDate.now().minusMonths(3))
                .setBegrunnelse("Fordi autotest sier det!");
        saksbehandler.bekreftAksjonspunkt(vurderRefusjonBeregningsgrunnlagBekreftelse);

        var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(
                new VurderTilbakekrevingVedNegativSimulering());
        vurderTilbakekrevingVedNegativSimulering.tilbakekrevingUtenVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        // AG sender inn ny korrigert IM med endring i refusjon mens behandlingen er hos beslutter. Behandlingen skal
        // rulles tilbake og behandles på nytt fra første AP i revurderingen.
        var inntektsmeldingEndringFar2 = arbeidsgiver.lagInntektsmeldingFP(fpStartdatoFar)
                .medRefusjonBeløpPerMnd(Prosent.valueOf(100));

        arbeidsgiver.sendInntektsmelding(saksnummerFar, inntektsmeldingEndringFar2);

        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.behandlinger).as("Antall behandlinger").hasSize(2);
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.SPOLT_TILBAKE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var vurderFaktaOmBeregningBekreftelse2 = saksbehandler.hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse());
        vurderFaktaOmBeregningBekreftelse2.leggTilRefusjonGyldighetVurdering(arbeidsgiver.arbeidsgiverIdentifikator(), false)
                .setBegrunnelse("Refusjonskrav er sendt inn for sent og skal ikke tas med i beregning!");
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse2);

        var vurderRefusjonBeregningsgrunnlagBekreftelse2 = saksbehandler.hentAksjonspunktbekreftelse(
                new VurderRefusjonBeregningsgrunnlagBekreftelse());
        vurderRefusjonBeregningsgrunnlagBekreftelse2.setFastsattRefusjonFomForAllePerioder(LocalDate.now().minusMonths(3))
                .setBegrunnelse("Fordi autotest sier det!");
        saksbehandler.bekreftAksjonspunkt(vurderRefusjonBeregningsgrunnlagBekreftelse2);

        var vurderTilbakekrevingVedNegativSimulering2 = saksbehandler.hentAksjonspunktbekreftelse(
                new VurderTilbakekrevingVedNegativSimulering());
        vurderTilbakekrevingVedNegativSimulering2.tilbakekrevingUtenVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering2);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummerFar, true, true, false);

        var perioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder();
        if (LocalDate.now().getDayOfMonth() == 1) {
            assertThat(perioder).as("Berørt behandlings tilkjent ytelse perioder").hasSize(2);
            assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.getFirst(), 0)).as(
                    "Forventer at hele summen utbetales til søker i første periode, og derfor ingenting til arbeidsgiver!").isTrue();
            assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(1), 100)).as(
                    "Forventer at hele summen utbetales til AG i andre periode, og derfor ingenting til søker!").isTrue();

        } else {
            assertThat(perioder).as("Berørt behandlings tilkjent ytelse perioder").hasSize(3);
            assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.getFirst(), 0)).as(
                    "Forventer at hele summen utbetales til søker i første periode, og derfor ingenting til arbeidsgiver!").isTrue();
            assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(1), 0)).as(
                    "Forventer at hele summen utbetales til søker i første periode, og derfor ingenting til arbeidsgiver!").isTrue();
            assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(perioder.get(2), 100)).as(
                    "Forventer at hele summen utbetales til AG i tredje periode, og derfor ingenting til søker!").isTrue();
        }
    }

    @Test
    @DisplayName("11: Far søker adopsjon hvor han søker hele fedrekvoten og fellesperiode, og får berørt sak pga mor")
    @Description("Far søker adopsjon hvor han søker hele fedrekvoten og fellesperioden. Mor søker noe av mødrekvoten midt "
            + "i fars periode med fullt uttak. Deretter søker mor 9 uker av fellesperioden med samtidig uttak. Far får "
            + "berørt sak hvor han får avkortet fellesperidoen på slutten og redusert perioder hvor mor søker samtidig uttak")
    void FarSøkerAdopsjonOgMorMødrekvoteMidtIFarsOgDeretterSamtidigUttakAvFellesperidoe() {
        var familie = FamilieGenerator.ny()
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build()).build())
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
                uttaksperiode(FELLESPERIODE, fellesperiodeStartFar, fellesperiodeSluttFar, ARBEID));
        var søknadFar = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, BrukerRolle.FAR, false).medUttaksplan(fordelingFar)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(mor))
                .medVedlegg(List.of(dokumenterUttak(fordelingFar, MorsAktivitet.ARBEID, VedleggInnsendingType.AUTOMATISK)));
        var saksnummerFar = far.søk(søknadFar.build());

        var arbeidsgiverFar = far.arbeidsgiver();

        ventPåInntektsmeldingForespørsel(saksnummerFar);
        arbeidsgiverFar.sendInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler.hentAksjonspunktbekreftelse(
                new AvklarFaktaAdopsjonsdokumentasjonBekreftelse()).setBegrunnelse("Adopsjon behandlet av Autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);

        var avklarFaktaAnnenForeldreHarRett = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaAnnenForeldreHarRett())
                .setAnnenforelderHarRett(true)
                .setBegrunnelse("Både far og mor har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse()); // Ikke totrinn

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Forventer alle fars sine peridoder er innvilget").isEmpty();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0)).as(
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!").isTrue();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(far.fødselsnummer(), saksnummerFar)
                .medTekstOmDuFårXKronerPerDagFørSkatt(DAGSATS_VED_6_G_2025)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medTekstOmDenAndreForelderenSomHarRettOgså()
                .medParagrafer(P_14_9, P_14_12, P_14_13)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medTekstOmInntektOverSeksGBeløp(SEKS_G_2025)
                .medParagraf(P_8_30);
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        /* MOR */
        var fpStartdatoMor = fpStartdatoFar.plusWeeks(7);
        var fellesperiodeStartMor = fpStartdatoMor.plusWeeks(4);
        var fellesperiodeSluttMor = fellesperiodeStartMor.plusWeeks(9).minusDays(1);
        var fordelingMor = fordeling(uttaksperiode(MØDREKVOTE, fpStartdatoMor, fellesperiodeStartMor.minusDays(1)),
                uttaksperiode(FELLESPERIODE, fellesperiodeStartMor, fellesperiodeSluttMor, 40, UttaksperiodeType.SAMTIDIGUTTAK));
        var søknadMor = lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoe, BrukerRolle.MOR, false).medUttaksplan(fordelingMor)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(far));
        var saksnummerMor = mor.søk(søknadMor.build());
        var arbeidsgiverMor = mor.arbeidsgiver();

        ventPåInntektsmeldingForespørsel(saksnummerMor);
        arbeidsgiverMor.sendInntektsmeldingerFP(saksnummerMor, fpStartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseMor = saksbehandler.hentAksjonspunktbekreftelse(
                new AvklarFaktaAdopsjonsdokumentasjonBekreftelse());
        avklarFaktaAdopsjonsdokumentasjonBekreftelseMor.setBegrunnelse("Adopsjon behandlet av Autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseMor);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Forventer alle mors sine peridoder er innvilget").isEmpty();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var dagsatsMor = 480_000 / 260;
        brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(mor.fødselsnummer(), saksnummerMor)
                .medTekstOmDuFårXKronerPerDagFørSkatt(dagsatsMor)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagrafer(P_14_9, P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medParagraf(P_8_30)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        /* FAR: Berørt behandling */
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        // UTTAK
        var mødrekvoten = fordelingMor.uttaksperioder().stream()
                .filter(uttaksperiode -> uttaksperiode instanceof UttaksPeriodeDto uttak
                && uttak.konto().equals(KontoType.MØDREKVOTE))
                .findFirst()
                .orElseThrow();
        var avslåtteSamtidigUttak = saksbehandler.hentAvslåtteUttaksperioder().getFirst();
        assertThat(avslåtteSamtidigUttak.getFom()).isEqualTo(mødrekvoten.fom());
        assertThat(avslåtteSamtidigUttak.getTom()).isEqualTo(mødrekvoten.tom());
        assertThat(avslåtteSamtidigUttak.getAktiviteter().getFirst().getTrekkdagerDesimaler()).isZero();
        assertThat(avslåtteSamtidigUttak.getPeriodeResultatÅrsak()).as(
                        "Perioden burde være avslått fordi annenpart tar ut mødrekovte med 100% utbetalingsgrad samtidig")
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
        var fastsettUttaksperioderManueltBekreftelseMor = saksbehandler.hentAksjonspunktbekreftelse(
                new FastsettUttaksperioderManueltBekreftelse());

        // Siste periode skal slippes og delvis innvilges med resterende saldo
        var fastsatteUttaksperioder = fastsettUttaksperioderManueltBekreftelseMor.getPerioder();
        var sistePeriode = fastsatteUttaksperioder.getLast();
        fastsettUttaksperioderManueltBekreftelseMor.avslåPeriode(sistePeriode.getFom(), sistePeriode.getTom(), IKKE_STØNADSDAGER_IGJEN,
                false);
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelseMor);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummerFar, true, false);

        beslutter.ventTilFagsakLøpende();

        // verifisering i uttak
        assertThat(saksbehandler.valgtBehandling.getSaldoer()
                .stonadskontoer()
                .get(SaldoVisningStønadskontoType.FELLESPERIODE)
                .saldo()).as("Saldoen for stønadskonton FELLESPERIODE").isZero();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Avslåtte uttaksperioder").hasSize(2);

        // verifisering i tilkjent ytelse
        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(1).getDagsats()).as("Tilkjent ytelses periode 2").isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats()).as(
                        "Forventer at dagsatsen blir redusert fra 100% til 60% for periode 3 i tilkjent ytelse")
                .isEqualTo((int) Math.round(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats() * 0.6));
        assertThat(tilkjentYtelsePerioder.getPerioder().get(5).getDagsats()).as("Tilkjent ytelses periode 6").isZero();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0)).as(
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!").isTrue();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilgetEndringAssertionsBuilder(far.fødselsnummer(), saksnummerFar)
                .medTekstOmForeldrepengerUtgjørDetSammeSomTidligere()
                .medKapittelDetteHarViAvslått()
                .medParagrafer(P_14_9, P_14_10, P_14_12, P_14_13);
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("12: Mor søker fødsel og mottar sykepenger uten inntektskilder, får avslag, klager og får medhold.")
    @Description("12: Mor søker fødsel og mottar sykepenger som er over 1/2 G. Har ingen inntektskilder. Saksbehandler"
            + "skriver inn sykepengebeløp som er under 1/2 G som vil før til at søker ikke har rett på foreldrepenger."
            + "Søker får avslag, klager og får medhold. Saksbehandler legger inn korrekt beløp som er over 1/2G og søker"
            + "får innvilget foreldrepenger.")
    void morSøkerFødselMottarForLite() {
        var fødselsdatoBarn = LocalDate.now().minusDays(2);
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny()
                        .infotrygd(GrunnlagDto.Ytelse.SP, LocalDate.now().minusMonths(9), LocalDate.now().plusDays(1),
                                GrunnlagDto.Status.LØPENDE, fødselsdatoBarn)
                        .build()).build())
                .forelder(far().build())
                .barn(fødselsdatoBarn)
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR).medAnnenForelder(
                AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        assertThat(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER")).as(
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som "
                        + "er forut for permisjonen på skjæringstidspunktet!").isTrue();

        var vurderFaktaOmBeregningBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
                .leggTilAndelerYtelse(4000.0, Inntektskategori.ARBEIDSTAKER);
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, false, false);

        assertThat(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPeriode(0).getRedusertPrAar()).as(
                        "Forventer at beregningsgrunnlaget baserer seg på et grunnlag som er mindre enn 1/2 G")
                .isLessThan(saksbehandler.valgtBehandling.getBeregningsgrunnlag().getHalvG());
        assertThat(saksbehandler.vilkårStatus(VilkarTypeKoder.BEREGNINGSGRUNNLAGVILKÅR)).as("Vilkårstatus for beregningsgrunnlag")
                .isEqualTo("IKKE_OPPFYLT");
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = foreldrepengerAvslagAssertionsBuilder(familie.mor().fødselsnummer(), saksnummer).medEgenndefinertAssertion(
                        "Du har ikke rett til foreldrepenger, fordi inntekten din er lavere enn %s kroner i året før skatt.".formatted(
                                formaterKroner(G_2025 / 2)));
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_AVSLAG, HistorikkType.BREV_SENDT);

        mor.sendInnKlage();
        klagebehandler.hentFagsak(saksnummer);
        klagebehandler.ventPåOgVelgKlageBehandling();

        var førstegangsbehandling = klagebehandler.førstegangsbehandling();
        var klageFormkravNfp = klagebehandler.hentAksjonspunktbekreftelse(new KlageFormkravNfp())
                .godkjennAlleFormkrav()
                .setPåklagdVedtak(førstegangsbehandling.uuid)
                .setBegrunnelse("Super duper klage!");
        klagebehandler.bekreftAksjonspunkt(klageFormkravNfp);

        var fritekst = "Fritektst til brev fra klagebehandler (vises i brevet).";
        var vurderingAvKlageNfpBekreftelse = klagebehandler.hentAksjonspunktbekreftelse(new VurderingAvKlageNfpBekreftelse())
                .bekreftMedholdGunst("PROSESSUELL_FEIL")
                .fritekstBrev(fritekst)
                .setBegrunnelse("Fordi");
        klagebehandler.bekreftAksjonspunkt(vurderingAvKlageNfpBekreftelse);

        klagebehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());
        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgKlageBehandling();
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        klagebehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = BrevAssertionBuilder.ny()
                .medEgenndefinertAssertion("Nav har omgjort vedtaket ditt om foreldrepenger")
                .medEgenndefinertAssertion(
                        "Etter at du klaget har vi vurdert saken din på nytt. Vi har kommet fram til at vedtaket ditt må gjøres om.")
                .medEgenndefinertAssertion("Dette har vi lagt vekt på i vurderingen vår")
                .medEgenndefinertAssertion(fritekst)
                .medKapittelDuMåMeldeOmEndringer()
                .medEgenndefinertAssertion(
                        "Dersom det skjer endringer som kan ha betydning for stønaden du får utbetalt, må du straks melde fra til Nav.");
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.KLAGE_OMGJØRIN, HistorikkType.BREV_SENDT);

        // Saksbehandler oppretter ny revudering manuelt etter søker har fått medhold i klage.
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_KLAGE_MED_END_INNTEKT);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgSisteBehandling();

        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().getFirst().behandlingArsakType()).as("Behandlingsårsakstype")
                .isEqualTo(BehandlingÅrsakType.RE_KLAGE_MED_END_INNTEKT);

        assertThat(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER")).as(
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som "
                        + "er forut for permisjonen på skjæringstidspunktet!").isTrue();

        var vurderFaktaOmBeregningBekreftelse2 = saksbehandler.hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
                .leggTilAndelerYtelse(10_000.0, Inntektskategori.ARBEIDSTAKER);
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse2);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new KontrollerRealitetsbehandlingEllerKlage());
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new VurdereAnnenYtelseFørVedtakBekreftelse());
        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, true, false, false);

        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.konsekvenserForYtelsen()).as("Konsekvens for ytelse")
                .contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING, KonsekvensForYtelsen.ENDRING_I_UTTAK);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }


    @Test
    @DisplayName("13: Mor søker på termin og får innvilget, men etter termin mottas det en dødfødselshendelse")
    @Description("13: Mør søker på termin og blir automatisk behanldet (innvilget). En uke etter terminen mottas det"
            + "en dødfødselshendelse hvor mor får avslag etter det 6 uken av mødrekvoten.")
    void morSøkerTerminFårInnvilgetOgSåKommerDetEnDødfødselEtterTermin() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4),
                                arbeidsavtale(LocalDate.now().minusYears(4), LocalDate.now().minusDays(60)).build(),
                                arbeidsavtale(LocalDate.now().minusDays(59)).stillingsprosent(50).build())
                        .build()).build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();

        var mor = familie.mor();
        var termindato = LocalDate.now().minusWeeks(2);
        var fpStartdatoMor = termindato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR).medAnnenForelder(
                AnnenforelderMaler.norskMedRettighetNorge(familie.far())).medMottattdato(termindato.minusMonths(2));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();

        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdatoMor);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = BrevAssertionBuilder.ny()
                .medOverskriftOmViHarBedtOmOpplysningerFraArbeidsgiverenDin()
                .medTekstOmAtViHarBedtArbeidsgiverenOmInntektsmelding()
                .medTekstOmDuKanSeBortFreDenneOmArbeidsgiverenHarSendt()
                .medEgenndefinertAssertion(
                        "Vi kan ikke behandle søknaden din om foreldrepenger før vi har mottatt inntektsmelding fra arbeidsgiveren din.");
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.ETTERLYS_INNTEKTSMELDING, HistorikkType.BREV_SENDT);

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo()).as(
                "Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL").isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo()).as(
                "Saldoen for stønadskonton MØDREKVOTE").isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo()).as(
                "Saldoen for stønadskonton FELLESPERIODE").isZero();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var dagsatsMor = (double) 600_000 / 260;
        brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(mor.fødselsnummer(), saksnummer).medTekstOmDuFårXKronerPerDagFørSkatt(
                        (int) Math.ceil(dagsatsMor))
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medParagraf(P_8_30)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        var differanseFødselTermin = 7;
        var dødsdato = termindato.plusDays(differanseFødselTermin);
        familie.sendInnDødfødselhendelse(dødsdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().getFirst().behandlingArsakType()).as(
                "Behandlingsårsak revurdering").isEqualTo(BehandlingÅrsakType.RE_HENDELSE_DØDFØDSEL);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(
                new FastsettUttaksperioderManueltBekreftelse());
        fastsettUttaksperioderManueltBekreftelse.avslåManuellePerioderMedPeriodeResultatÅrsak(PeriodeResultatÅrsak.BARNET_ER_DØD);
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(new FastsetteUttakKontrollerOpplysningerOmDødDto());

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, true, false);

        var saldoerRevurdering = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoerRevurdering.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo()).as(
                "Saldoen for stønadskonton FORELDREPENGER_FØR_FØDSEL").isZero();
        assertThat(saldoerRevurdering.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo()).as(
                "Saldoen for stønadskonton MØDREKVOTE").isEqualTo(45);
        assertThat(saldoerRevurdering.stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo()).as(
                "Saldoen for stønadskonton FELLESPERIODE").isEqualTo(75);

        var uttakResultatPerioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        var uttaksperiode_0 = uttakResultatPerioder.getFirst();
        var uttaksperiode_1 = uttakResultatPerioder.get(1);
        assertThat(uttaksperiode_0.getPeriodeResultatType()).as("Uttaksresultattype for første periode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttaksperiode_0.getPeriodeType()).as("Forventer at første periode er FELLESPERIODE pga dødfødsel etter termin")
                .isEqualTo(FELLESPERIODE.name());
        assertThat(uttaksperiode_0.getFom()).as("Verifiserer at antall dager etter termin fylles med fellesperioden")
                .isEqualTo(uttaksperiode_1.getFom().minusDays(differanseFødselTermin));

        assertThat(uttaksperiode_1.getPeriodeResultatType()).as("Uttaksresultattype for andre periode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttaksperiode_1.getPeriodeType()).as(
                        "Forventer at første periode er FORELDREPENGER_FØR_FØDSEL pga dødfødsel etter termin")
                .isEqualTo(FORELDREPENGER_FØR_FØDSEL.name());
        assertThat(uttaksperiode_1.getAktiviteter().getFirst().getTrekkdagerDesimaler()).as(
                "Verifiser at søker tar ut hele FORELDREPENGER_FØR_FØDSEL kvoten").isEqualByComparingTo(BigDecimal.valueOf(3 * 5));

        var uttaksperiode_2 = uttakResultatPerioder.get(2);
        assertThat(uttaksperiode_2.getPeriodeResultatType()).as("Uttaksresultattype for tredje periode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttaksperiode_2.getPeriodeType()).as("Forventer at første periode er MØDREKVTOEN pga dødfødsel etter termin")
                .isEqualTo(MØDREKVOTE.name());
        assertThat(uttaksperiode_2.getAktiviteter().getFirst().getTrekkdagerDesimaler()).as(
                        "Forventer at det tas ut 6 uker av den gjenværende delen av stønadsperioden")
                .isEqualByComparingTo(BigDecimal.valueOf(6 * 5));

        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Avslåtte uttaksperioder pga dødfødsel").hasSize(2);
        assertThat(uttakResultatPerioder.get(3).getPeriodeResultatÅrsak().isAvslåttÅrsak()).as(
                "Perioden burde være avslått fordi det er mottatt dødfødselshendelse").isTrue();
        assertThat(uttakResultatPerioder.get(4).getPeriodeResultatÅrsak().isAvslåttÅrsak()).as(
                "Perioden burde være avslått fordi det er mottatt dødfødselshendelse").isTrue();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = BrevAssertionBuilder.ny()
                .medOverskriftOmInnvilgetEndringAvForeldrepenger()
                .medEgenndefinertAssertion(
                        "Vi har fått opplyst at barnet ditt døde %s. Den siste dagen din med foreldrepenger er derfor %s.".formatted(
                                formaterDato(dødsdato), formaterDato(dødsdato.plusWeeks(6).minusDays(1))))
                .medEgenndefinertAssertion("Vedtaket er gjort etter folketrygdloven § 14-9 og forvaltningsloven § 35.");
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_OPPHØR, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("14: Mor, fødsel, sykdom uke 5 til 10, må søke om utsettelse fra uke 5-6")
    @Description("Mor søker fødsel hvor hun oppgir annenpart. Mor er syk fra uke 5 til 10. Hun må søke utsettelse fra "
            + "uke 5 til 6 ettersom det er innenfor de første 6 ukene etter fødsel. Ukene etter trenger hun ikke søke om"
            + "utsettelse og blir automatisk innvilget uten trekk.")
    void mor_fødsel_sykdom_innefor_første_6_ukene_utsettelse() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(8))
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var utsettelsesperiodeMidtIMødrekvoten = utsettelsesperiode(UtsettelsesÅrsak.SYKDOM, fødselsdato.plusWeeks(5),
                fødselsdato.plusWeeks(6).minusDays(1));
        var uttaksperiodeEtterUtsettelseOgOpphold = uttaksperiode(MØDREKVOTE, fødselsdato.plusWeeks(10),
                fødselsdato.plusWeeks(20).minusDays(1));
        var fordeling = fordeling(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(5).minusDays(1)), utsettelsesperiodeMidtIMødrekvoten,
                // Opphold uke 6 til 10
                uttaksperiodeEtterUtsettelseOgOpphold,
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(36).minusDays(1)));
        var fpStartdato = fordeling.uttaksperioder().getFirst().fom();
        var søknad = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fødselsdato.minusWeeks(3))
                .build();
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();

        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaUttak = saksbehandler.hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse()).godkjennSykdom();
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttak);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, false, false);

        // UTTAK
        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL).saldo()).as(
                "Saldo for stønadskontoen FORELDREPENGER_FØR_FØDSEL").isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo()).as(
                "Saldo for stønadskontoen MØDREKVOTE").isZero();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.FELLESPERIODE).saldo()).as(
                "Saldo for stønadskontoen FELLESPERIODE").isZero();

        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Avslåtte uttaksperioder").isEmpty();
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(2).getPeriodeResultatÅrsak()).as("Perioderesultatårsak")
                .isEqualTo(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_SEKS_UKER_FRI_SYKDOM);

        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getFom()).as("Utsettelsesperiode fom")
                .isEqualTo(utsettelsesperiodeMidtIMødrekvoten.fom());
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getTom()).as("Utsettelsesperiode tom")
                .isEqualTo(utsettelsesperiodeMidtIMødrekvoten.tom());
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats()).as("Utsettelsesperiode dagsats").isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(3).getFom()).as("Periode etter fri utsettelse fom")
                .isEqualTo(uttaksperiodeEtterUtsettelseOgOpphold.fom());

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var dagsatsMor = 480_000 / 260;
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(mor.fødselsnummer(), saksnummer)
                .medTekstOmDuFårXKronerPerDagFørSkatt(dagsatsMor)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medEgenndefinertAssertion(" fordi du er helt avhengig av hjelp til å ta deg av barnet.")
                .medParagrafer(P_14_9, P_14_10, P_14_11, P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medParagraf(P_8_30);
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("15: Mor, adopsjon, sykdom uke 3 til 8, trenger ikke søke utsettelse for uke 3 til 6")
    @Description("Mor søker adopsjon hvor hun oppgir annenpart. Mor er syk innenfor de første 6 ukene og etter. Sykdom"
            + "fra uke 3 til 8. Ikke noe krav til å søke om utsettlse og saken blir automatisk behandlet og innvilget.")
    void mor_adopsjon_sykdom_uke_3_til_8_automatisk_invilget() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(15))
                .build();

        var mor = familie.mor();
        var omsorgsovertagelsesdato = LocalDate.now().minusMonths(2);
        var fordeling = fordeling(
                uttaksperiode(MØDREKVOTE, omsorgsovertagelsesdato, omsorgsovertagelsesdato.plusWeeks(3).minusDays(1)),
                // Opphold uke 3 til 10
                uttaksperiode(MØDREKVOTE, omsorgsovertagelsesdato.plusWeeks(10), omsorgsovertagelsesdato.plusWeeks(22).minusDays(1)));

        var søknad = SøknadForeldrepengerMaler.lagSøknadForeldrepengerAdopsjon(omsorgsovertagelsesdato, BrukerRolle.MOR, false)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(omsorgsovertagelsesdato.minusWeeks(3));
        var saksnummer = mor.søk(søknad.build());
        var arbeidsgiver = mor.arbeidsgiver();

        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, omsorgsovertagelsesdato);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAdopsjonsdokumentasjonBekreftelseFar = saksbehandler.hentAksjonspunktbekreftelse(
                new AvklarFaktaAdopsjonsdokumentasjonBekreftelse()).setBegrunnelse("Adopsjon behandlet av Autotest");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAdopsjonsdokumentasjonBekreftelseFar);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var saldoer = saksbehandler.valgtBehandling.getSaldoer();
        assertThat(saldoer.stonadskontoer().get(SaldoVisningStønadskontoType.MØDREKVOTE).saldo()).as(
                "Saldo for stønadskontoen MØDREKVOTE").isZero();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).as("Avslåtte uttaksperioder").isEmpty();

        var feriepenger = saksbehandler.valgtBehandling.getFeriepengegrunnlag();
        assertThat(feriepenger).isNotNull();
        var feriepengerTilArbeidsgiver = oppsummerFeriepengerForArbeidsgiver(feriepenger.andeler(),
                arbeidsgiver.arbeidsgiverIdentifikator().value(), false);
        var feriepengerTilSøker = oppsummerFeriepengerForArbeidsgiver(feriepenger.andeler(),
                arbeidsgiver.arbeidsgiverIdentifikator().value(), true);
        assertFeriepenger(feriepengerTilSøker + feriepengerTilArbeidsgiver, 11297);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var dagsatsMor = 480_000 / 260;
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(mor.fødselsnummer(), saksnummer)
                .medTekstOmDuFårXKronerPerDagFørSkatt(dagsatsMor)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagraf(P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medParagraf(P_8_30);
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("16: Mor engangstønad. Bare far har rett (BFHR). Utsettelse uten at mor er i aktivitet. Trekker alt utenom minstretten.")
    @Description(
            "Mor har en ferdig behanldet engagnstønad liggende. Far søker derette foreldrepenger hvor han oppgir at bare han har rett. " +
            "Far søker først 2 uker foreldrepenger ifm fødselen og deretter 40 uker utsettelse hvor mors aktivit ikke er dokumentert. " +
            "Utsettelsen avslås og trekker dager løpende, men skal ikke trekke noe av minsteretten. Etter utsettelsen så tar far ut en " +
            "periode med foreldrepenger med aktivitetskrav hvor mor er i aktivitet, etterfulgt av en periode hvor han bruker sine resterende " +
            "6 uker med foreldrepenger uten aktivitetskrav. Første uttaksperiode etter utsettelsen innvilges delvis med disse 6 " +
            "gjenværende stønadsukene uten aktivitetskrav. Resten av periode og neste uttaks periode avslås pga av manglede stønadsdager igjen")
    void farBhfrTest() {
        var familie = FamilieGenerator.ny()
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny()
                        .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", LocalDate.now().minusYears(4),
                                LocalDate.now().minusMonths(4))
                        .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-002", LocalDate.now().minusMonths(4))
                        .build()).build())
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

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = engangsstønadInnvilgetAssertionsBuilder(familie.mor().fødselsnummer(), saksnummerMor)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.ENGANGSSTØNAD_INNVILGET,
                HistorikkType.BREV_SENDT);

        /* Far's søknad */
        var far = familie.far();
        var fpStartdatoFar = fødselsdato;
        var uttaksperiodeFørste = uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar,
                fpStartdatoFar.plusWeeks(2).minusDays(1), IKKE_OPPGITT);
        var utsettelsesperiode = utsettelsesperiode(UtsettelsesÅrsak.FRI, fpStartdatoFar.plusWeeks(6),
                fpStartdatoFar.plusWeeks(46).minusDays(1), UTDANNING);
        var uttaksperiodeEtterUtsettelse1 = uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar.plusWeeks(46),
                fpStartdatoFar.plusWeeks(56).minusDays(1), ARBEID);
        var uttaksperiodeEtterUtsettelse2 = uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar.plusWeeks(56),
                fpStartdatoFar.plusWeeks(62).minusDays(1));
        var fordeling = fordeling(uttaksperiodeFørste, utsettelsesperiode, uttaksperiodeEtterUtsettelse1,
                uttaksperiodeEtterUtsettelse2);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR).medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskIkkeRett(familie.mor()))
                .medMottattdato(fødselsdato.minusWeeks(1))
                .medVedlegg(List.of(
                        dokumenterUttak(fordeling, MorsAktivitet.ARBEID, VedleggInnsendingType.SEND_SENERE),
                        dokumenterUttak(fordeling, MorsAktivitet.UTDANNING, VedleggInnsendingType.SEND_SENERE)
                ));
        var saksnummerFar = far.søk(søknad.build());
        var arbeidsgiver = far.arbeidsgiver();

        ventPåInntektsmeldingForespørsel(saksnummerFar);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        brevAssertionsBuilder = BrevAssertionBuilder.ny()
                .medOverskriftOmViHarBedtOmOpplysningerFraArbeidsgiverenDin()
                .medTekstOmAtViHarBedtArbeidsgiverenOmInntektsmelding()
                .medTekstOmDuKanSeBortFreDenneOmArbeidsgiverenHarSendt();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.ETTERLYS_INNTEKTSMELDING, HistorikkType.BREV_SENDT);

        /*
         * Skal ikke få AP 5086 hvor saksbehandler må avklare om anneforelder har rett, ettersom mor allerede mottar engangsstønad
         * Mors aktivitet er ikke dokumentert for utsettelsesperioden og første uttaksperiode etter utsettelsen.
         * */
        var vurderUttakDokBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse())
                .ikkeDokumentert(utsettelsesperiode)
                .ikkeDokumentert(uttaksperiodeEtterUtsettelse1)
                .godkjenn(uttaksperiodeEtterUtsettelse2)
                .setBegrunnelse("Mor er ikke i aktivitet!");
        saksbehandler.bekreftAksjonspunkt(vurderUttakDokBekreftelse);
        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummerFar, false, false);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // SALDO
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(FORELDREPENGER).saldo()).as(
                "Saldoen for stønadskonton FORELDREPENGER").isZero();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(MINSTERETT).saldo()).as(
                "Saldoen for stønadskonton MINSTERETT").isZero();

        // UTTAKSPLAN
        var avslåtteUttaksperioder = saksbehandler.hentAvslåtteUttaksperioder();
        assertThat(avslåtteUttaksperioder).as("Forventer at det er 4 avslåtte uttaksperioder").hasSize(4);

        var avslåttUtsettelseperiodeFørsteDel = avslåtteUttaksperioder.getFirst();
        assertThat(avslåttUtsettelseperiodeFørsteDel.getFom()).isEqualTo(utsettelsesperiode.fom());
        assertThat(avslåttUtsettelseperiodeFørsteDel.getTom()).isCloseTo(utsettelsesperiode.fom().plusWeeks(30),
                within(2, ChronoUnit.DAYS)); // splitt tar ikke hensyn til helger
        assertThat(
                avslåttUtsettelseperiodeFørsteDel.getAktiviteter().getFirst().getTrekkdagerDesimaler())  // Trekker opp til minstretten
                .as("Trekkdager").isEqualByComparingTo(BigDecimal.valueOf(30 * 5));
        assertThat(avslåttUtsettelseperiodeFørsteDel.getPeriodeResultatÅrsak()).as("Perioderesultatårsak")
                .isEqualTo(AKTIVITETSKRAVET_UTDANNING_IKKE_DOKUMENTERT);

        var avslåttUtsettelseperiodeAndreDel = avslåtteUttaksperioder.get(1);
        assertThat(avslåttUtsettelseperiodeAndreDel.getFom()).isCloseTo(utsettelsesperiode.fom().plusWeeks(30),
                within(2, ChronoUnit.DAYS)); // splitt tar ikke hensyn til helger
        assertThat(avslåttUtsettelseperiodeAndreDel.getTom()).isEqualTo(utsettelsesperiode.tom());
        assertThat(avslåttUtsettelseperiodeAndreDel.getAktiviteter()
                .getFirst()
                .getTrekkdagerDesimaler()) // avslag på siste rest av utsettelsen, men trekker ikke av minsteretten!
                .as("Trekkdager").isZero();
        assertThat(avslåttUtsettelseperiodeAndreDel.getPeriodeResultatÅrsak()).as("Perioderesultatårsak")
                .isEqualTo(IKKE_STØNADSDAGER_IGJEN);

        var avslåtttUttaksperiode1 = avslåtteUttaksperioder.get(2);
        assertThat(avslåtttUttaksperiode1.getFom()).isEqualTo(uttaksperiodeEtterUtsettelse1.fom().plusWeeks(8));
        assertThat(avslåtttUttaksperiode1.getTom()).isEqualTo(uttaksperiodeEtterUtsettelse1.tom());
        assertThat(avslåtttUttaksperiode1.getPeriodeResultatÅrsak()).as("Perioderesultatårsak").isEqualTo(IKKE_STØNADSDAGER_IGJEN);

        var avslåtttUttaksperiode2 = avslåtteUttaksperioder.get(3);
        assertThat(avslåtttUttaksperiode2.getFom()).isEqualTo(uttaksperiodeEtterUtsettelse2.fom());
        assertThat(avslåtttUttaksperiode2.getTom()).isEqualTo(uttaksperiodeEtterUtsettelse2.tom());
        assertThat(avslåtttUttaksperiode2.getPeriodeResultatÅrsak()).as("Perioderesultatårsak").isEqualTo(IKKE_STØNADSDAGER_IGJEN);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var dagsatsFar = (double) 600_000 / 260;
        brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(far.fødselsnummer(), saksnummerFar)
                .medTekstOmDuFårXKronerPerDagFørSkatt((int) Math.ceil(dagsatsFar))
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagrafer(P_14_9, P_14_13, P_14_14, P_21_3)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medParagraf(P_8_30);
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
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
        var saksnummerMor = sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(familie, fødselsdato,
                fpStartdatoMor, fpSluttdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(RisikoklasseType.IKKE_HØY);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var dagsatsMor = 480_000 / 260;
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(familie.mor().fødselsnummer(), saksnummerMor)
                .medTekstOmDuFårXKronerPerDagFørSkatt(dagsatsMor)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medParagraf(P_8_30)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        // Mor går på min side for innsyn på foreldrepengesaken sin. Verifisere innhold
        var mor = familie.mor();
        var fpSak = mor.innsyn().hentFpSakUtenÅpenBehandling(saksnummerMor);
        assertThat(fpSak.annenPart()).isNotNull();
        assertThat(fpSak.dekningsgrad()).isEqualTo(DekningsgradSak.HUNDRE);
        assertThat(fpSak.barn()).hasSize(1);

        // Sammenlign uttak fra fpfrontend og innsyn for bruker
        var uttakResultatPerioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        var vedtaksperioderInnsyn = fpSak.gjeldendeVedtak().perioder();
        assertThat(vedtaksperioderInnsyn).hasSize(3);

        // Verifisere at alle perioder er innvilget i både uttak og vedtaket i innsyn
        uttakResultatPerioder.forEach(
                periode -> assertThat(periode.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET));
        vedtaksperioderInnsyn.forEach(periode -> assertThat(periode.resultat().innvilget()).isTrue());
    }

    @Test
    @DisplayName("18: Koblet sak. Far utsetter oppstart rundt fødsel, søker termin og med fødselshendelse")
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

        ventPåInntektsmeldingForespørsel(saksnummerMor);
        var startdatoForeldrepengerMor = termindato.minusWeeks(3);
        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummerMor, startdatoForeldrepengerMor);

        saksbehandler.hentFagsak(saksnummerMor);
        validerInnsendtInntektsmeldingForeldrepenger(mor.fødselsnummer(), startdatoForeldrepengerMor, mor.månedsinntekt(), false);

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var dagsatsMor = Math.min(DAGSATS_VED_6_G_2025, (mor.månedsinntekt() * 12) / 260);
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(mor.fødselsnummer(), saksnummerMor).medTekstOmDuFårXKronerPerDagFørSkatt(dagsatsMor)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medTekstOmInntektOverSeksGBeløp(SEKS_G_2025)
                .medParagraf(P_8_30)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        var far = familie.far();
        var fomFedrekvote = termindato.minusDays(4);
        var startdatoForeldrepengerFar = termindato.minusWeeks(1);
        var søknadFar = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin(termindato, BrukerRolle.FAR)
                .medUttaksplan(
                        fordeling(uttaksperiode(FEDREKVOTE, fomFedrekvote, fomFedrekvote.plusWeeks(1).plusDays(3), SAMTIDIGUTTAK)))
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medMottattdato(startdatoForeldrepengerFar)
                .build();
        var saksnummerFar = far.søk(søknadFar);

        ventPåInntektsmeldingForespørsel(saksnummerFar);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = BrevAssertionBuilder.ny()
                .medOverskriftOmViHarBedtOmOpplysningerFraArbeidsgiverenDin()
                .medTekstOmAtViHarBedtArbeidsgiverenOmInntektsmelding()
                .medTekstOmDuKanSeBortFreDenneOmArbeidsgiverenHarSendt();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.ETTERLYS_INNTEKTSMELDING, HistorikkType.BREV_SENDT);

        far.arbeidsgiver().sendInntektsmeldingerFP(saksnummerFar, startdatoForeldrepengerFar);

        validerInnsendtInntektsmeldingForeldrepenger(far.fødselsnummer(), fomFedrekvote, far.månedsinntekt(), false);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(far.fødselsnummer(), saksnummerFar).medTekstOmDuFårXKronerPerDagFørSkatt(DAGSATS_VED_6_G_2025)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagraf(P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medTekstOmInntektOverSeksGBeløp(SEKS_G_2025)
                .medParagraf(P_8_30)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

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

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilgetEndringAssertionsBuilder(mor.fødselsnummer(), saksnummerMor)
                .medTekstOmForeldrepengerUtgjørDetSammeSomTidligere()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        var endringssøknad = lagEndringssøknad(søknadFar, saksnummerFar,
                fordeling(utsettelsesperiode(FRI, startdatoForeldrepengerFar, fødselsdato.minusDays(1)),
                        uttaksperiode(FEDREKVOTE, fødselsdato, fødselsdato.plusWeeks(2).minusDays(1), SAMTIDIGUTTAK)));
        far.søk(endringssøknad.build());

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling(RE_ENDRING_FRA_BRUKER);
        if (saksbehandler.harAksjonspunkt(VURDER_FEILUTBETALING_KODE)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(
                    new VurderTilbakekrevingVedNegativSimulering()).avventSamordningIngenTilbakekreving();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
            saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        }
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var uttak = saksbehandler.valgtBehandling.hentUttaksperioder();
        for (var periode : uttak) {
            assertThat(periode.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
            assertThat(periode.getAktiviteter().getFirst().getStønadskontoType()).isEqualTo(FEDREKVOTE);
            assertThat(periode.getAktiviteter().getFirst().getUtbetalingsgrad()).isEqualTo(BigDecimal.valueOf(100));
        }
        var trekkdager = uttak.stream().mapToInt(p -> p.getAktiviteter().getFirst().getTrekkdagerDesimaler().intValue()).sum();
        assertThat(trekkdager).isEqualTo(10);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilgetEndringAssertionsBuilder(far.fødselsnummer(), saksnummerFar)
                .medTekstOmForeldrepengerUtgjørDetSammeSomTidligere()
                .medParagraf(P_14_12)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
    }

    @Test
    @DisplayName("19: Far får justert uttaket rundt termin etter fødselshendelse")
    @Description("Far søker og får innvilget på termin. Fødselen kommer og uttaket justeres")
    void farFårJustertUttakVedFødselshendelse() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build()).build())
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build()).build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();
        var termindato = LocalDate.now().minusWeeks(2).plusDays(2);

        var mor = familie.mor();
        var saksnummerMor = mor.søk(lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR).medAnnenForelder(
                AnnenforelderMaler.norskMedRettighetNorge(familie.far())).build());

        ventPåInntektsmeldingForespørsel(saksnummerMor);
        var startdatoForeldrepengerMor = termindato.minusWeeks(3);
        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummerMor, startdatoForeldrepengerMor);

        saksbehandler.hentFagsak(saksnummerMor);
        validerInnsendtInntektsmeldingForeldrepenger(mor.fødselsnummer(), startdatoForeldrepengerMor, mor.månedsinntekt(), false);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(mor.fødselsnummer(), saksnummerMor).medTekstOmDuFårXKronerPerDagFørSkatt(DAGSATS_VED_6_G_2025)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medTekstOmInntektOverSeksGBeløp(SEKS_G_2025)
                .medParagraf(P_8_30)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        var far = familie.far();

        var farsPeriodeRundtFødsel = uttaksperiode(FEDREKVOTE, termindato, termindato.plusWeeks(2).minusDays(1), SAMTIDIGUTTAK);
        var søknadFar = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin(termindato, BrukerRolle.FAR)
                .medUttaksplan(new UttaksplanDto(true, List.of(farsPeriodeRundtFødsel)))
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medMottattdato(termindato.minusWeeks(1));
        var saksnummerFar = far.søk(søknadFar.build());

        ventPåInntektsmeldingForespørsel(saksnummerFar);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = BrevAssertionBuilder.ny()
                .medOverskriftOmViHarBedtOmOpplysningerFraArbeidsgiverenDin()
                .medTekstOmAtViHarBedtArbeidsgiverenOmInntektsmelding()
                .medTekstOmDuKanSeBortFreDenneOmArbeidsgiverenHarSendt();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.ETTERLYS_INNTEKTSMELDING, HistorikkType.BREV_SENDT);

        far.arbeidsgiver().sendInntektsmeldingerFP(saksnummerFar, termindato);
        validerInnsendtInntektsmeldingForeldrepenger(far.fødselsnummer(), termindato, far.månedsinntekt(), false);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(far.fødselsnummer(), saksnummerFar).medTekstOmDuFårXKronerPerDagFørSkatt(DAGSATS_VED_6_G_2025)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagraf(P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medTekstOmInntektOverSeksGBeløp(SEKS_G_2025)
                .medParagraf(P_8_30)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

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
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(
                    new VurderTilbakekrevingVedNegativSimulering()).avventSamordningIngenTilbakekreving();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
            saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
            saksbehandler.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();
        } else {
            saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        }

        var uttak = saksbehandler.valgtBehandling.hentUttaksperioder();
        for (var periode : uttak) {
            assertThat(periode.getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
            assertThat(periode.getAktiviteter().getFirst().getStønadskontoType()).isEqualTo(FEDREKVOTE);
            assertThat(periode.getAktiviteter().getFirst().getUtbetalingsgrad()).isEqualTo(BigDecimal.valueOf(100));
        }
        var trekkdager = uttak.stream().mapToInt(p -> p.getAktiviteter().getFirst().getTrekkdagerDesimaler().intValue()).sum();
        assertThat(trekkdager).isEqualTo(10);
        assertThat(uttak.getFirst().getFom()).isEqualTo(helgejustertTilMandag(fødselsdato));

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilgetEndringAssertionsBuilder(far.fødselsnummer(), saksnummerFar)
                .medTekstOmForeldrepengerUtgjørDetSammeSomTidligere()
                .medParagraf(P_14_12)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        brevAssertionsBuilder = foreldrepengerInnvilgetEndringAssertionsBuilder(mor.fødselsnummer(), saksnummerMor)
                .medTekstOmForeldrepengerUtgjørDetSammeSomTidligere()
                .medParagrafer(P_14_9, P_14_10, P_14_12)
                .medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("20: Far søker termin hvor han velger SEND_SENERE på terminbekreftelse. Havner på vent pga kompletthet. Far ettersender og behandlingen forsetter")
    @Description("Far søker og får innvilget på termin. Fødselen kommer og uttaket justeres")
    void farSettesPåVentPåManglendeVedleggOgEttersenderVedleggSomFørerTilKomplettbehandlingOgAtDenTasAvVent() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();
        var termindato = LocalDate.now().minusWeeks(6);
        var far = familie.far();
        var søknad = lagSøknadForeldrepengerTermin(termindato, FAR)
                .medUttaksplan(fordeling(uttaksperiode(FEDREKVOTE, termindato.plusWeeks(9).plusDays(2), termindato.plusWeeks(15).plusDays(1))))
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medVedlegg(List.of(dokumenterTermin(VedleggInnsendingType.SEND_SENERE)))
                .build();

        var saksnummer = far.søk(søknad);

        var arbeidsgiver = far.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, termindato.plusWeeks(3));

        validerInnsendtInntektsmeldingForeldrepenger(far.fødselsnummer(), termindato.plusWeeks(9).plusDays(2), far.månedsinntekt(),
                false);

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.harAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD)).isTrue();

        // Ettersend vedlegg som mangler
        far.ettersendVedlegg(far.fødselsnummer(), YtelseType.FORELDREPENGER, DokumentType.I000141,
                new VedleggDto.Dokumenterer(VedleggDto.Dokumenterer.DokumentererType.BARN, null, null));

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.erAksjonspunktUtført(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD)).isTrue();

        var sjekkManglendeFødsel = saksbehandler.hentAksjonspunktbekreftelse(new SjekkManglendeFødselBekreftelse()).bekreftBarnErIkkeFødt();
        saksbehandler.bekreftAksjonspunkt(sjekkManglendeFødsel);

        var avklarFaktaAnnenForeldreHarRett = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaAnnenForeldreHarRett())
                .setAnnenforelderHarRett(true)
                .setBegrunnelse("Både far og mor har rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, false, false);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var dagsatsFar = Math.min(SEKS_G_2025, far.månedsinntekt() * 12) / 260;
        var brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(far.fødselsnummer(), saksnummer).medTekstOmDuFårXKronerPerDagFørSkatt(dagsatsFar)
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medTekstOmDenAndreForelderenSomHarRettOgså()
                .medEgenndefinertAssertion("Det er 45 dager igjen av kvoten din, og 80 dager som begge kan ta ut.")
                .medTekstOmDageneMåVæreTattUtFørTreÅrEllerNyttBarn()
                .medParagraf(P_14_12)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medParagraf(P_8_30);
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    @Test
    @DisplayName("21: Mor engangstønad. Bare far har rett (BFHR). Far skal få 10 uker minsterett og 20 uker mor/arbeid.")
    @Description(" Mor har engangstønad men begynner i en 75% stilling 26 uker etter fødsel. "
            + "Far søker 10 uker Minsterett (fom uke 6 etter fødsel) + 20 uker mor/arbeid. "
            + "Far skal få aksjonspunkt rundt uttaksdokumentasjon. Perioden fra F+16 til F+26 skal ha behov for avklaring.")
    void farBfhrMinsterettOgUttakTest() {
        var fødselsdato = LocalDate.now().minusMonths(1);
        var familie = FamilieGenerator.ny()
                .forelder(far().inntektytelse(InntektYtelseGenerator.ny()
                        .arbeidsforhold(LocalDate.now().minusYears(4),
                                arbeidsavtale(LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4).minusDays(1)).build(),
                                arbeidsavtale(LocalDate.now().minusMonths(4)).build())
                        .build()).build())
                .forelder(mor().inntektytelse(InntektYtelseGenerator.ny()
                        .arbeidsforhold(fødselsdato.plusWeeks(26),
                                arbeidsavtale(fødselsdato.plusWeeks(26)).stillingsprosent(80).build())
                        .build()).build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(fødselsdato)
                .build();

        /* Mor's engangsstønad*/
        var mor = familie.mor();
        var saksnummerMor = mor.søk(lagEngangstønadFødsel(fødselsdato).build());
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var brevAssertionsBuilder = engangsstønadInnvilgetAssertionsBuilder(familie.mor().fødselsnummer(),
                saksnummerMor).medTekstOmAutomatiskVedtakUtenUndferskrift();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.ENGANGSSTØNAD_INNVILGET, HistorikkType.BREV_SENDT);

        /* Far's søknad */
        var far = familie.far();
        var fpStartdatoFar = fødselsdato.plusWeeks(6);
        var uttaksperiodeFørste = uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar,
                fpStartdatoFar.plusWeeks(10).minusDays(1), IKKE_OPPGITT);
        var uttaksperiodeAndre = uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar.plusWeeks(10),
                fpStartdatoFar.plusWeeks(30).minusDays(1), ARBEID);
        var fordeling = fordeling(uttaksperiodeFørste, uttaksperiodeAndre);
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR).medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskIkkeRett(familie.mor()))
                .medMottattdato(fødselsdato.minusWeeks(1))
                .medVedlegg(List.of(dokumenterUttak(fordeling, MorsAktivitet.ARBEID, VedleggInnsendingType.AUTOMATISK)));
        var saksnummerFar = far.søk(søknad.build());
        var arbeidsgiver = far.arbeidsgiver();

        ventPåInntektsmeldingForespørsel(saksnummerFar);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        brevAssertionsBuilder = BrevAssertionBuilder.ny()
                .medOverskriftOmViHarBedtOmOpplysningerFraArbeidsgiverenDin()
                .medTekstOmAtViHarBedtArbeidsgiverenOmInntektsmelding()
                .medTekstOmDuKanSeBortFreDenneOmArbeidsgiverenHarSendt();
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.ETTERLYS_INNTEKTSMELDING, HistorikkType.BREV_SENDT);

        var vurderUttakDokumentasjonBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse());
        var vurderingsbehov = vurderUttakDokumentasjonBekreftelse.getVurderingBehov()
                .stream()
                .sorted(Comparator.comparing(DokumentasjonVurderingBehov::tom))
                .toList();
        assertThat(vurderingsbehov.getFirst().vurdering()).isNull(); // Uttaket før mor begynner i arbeid 26 uker etter fødsel
        assertThat(vurderingsbehov.getLast().vurdering()).isEqualTo(DokumentasjonVurderingBehov.Vurdering.GODKJENT_AUTOMATISK); // Mor i arbeid med 80% stilling
        var førsteVurderingBehov = vurderingsbehov.getFirst();
        vurderUttakDokumentasjonBekreftelse
                .ikkeGodkjenn(new ÅpenPeriodeDto(førsteVurderingBehov.fom(), førsteVurderingBehov.tom()))
                .setBegrunnelse("Mor er ikke i aktivitet første periode!");
        saksbehandler.bekreftAksjonspunkt(vurderUttakDokumentasjonBekreftelse);
        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummerFar, false, false);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // UTTAKSPLAN
        var avslåtteUttaksperioder = saksbehandler.hentAvslåtteUttaksperioder();
        assertThat(avslåtteUttaksperioder).as("Forventer at det er 1 avslåtte uttaksperioder").hasSize(1);
        assertThat(avslåtteUttaksperioder.getFirst().getPeriodeResultatÅrsak()).as("Perioderesultatårsak")
                .isEqualTo(AKTIVITETSKRAVET_ARBEID_IKKE_OPPFYLT);

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        var dagsatsFar = (double) 600_000 / 260;
        brevAssertionsBuilder = foreldrepengerInnvilget100ProsentAssertionsBuilder(far.fødselsnummer(), saksnummerFar)
                .medTekstOmDuFårXKronerPerDagFørSkatt((int) Math.ceil(dagsatsFar))
                .medTekstOmForeldrepengerUtbetaltForAlleDagerMenVarierer()
                .medParagrafer(P_14_13, P_14_14)
                .medTekstOmGjennomsnittInntektFraTreSisteMåndene()
                .medParagrafer(P_14_7, P_8_30);
        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.FORELDREPENGER_INNVILGET, HistorikkType.BREV_SENDT);
    }

    private Saksnummer sendInnSøknadOgIMAnnenpartMorMødrekvoteOgDelerAvFellesperiodeHappyCase(Familie familie,
                                                                                              LocalDate fødselsdato,
                                                                                              LocalDate fpStartdatoMor,
                                                                                              LocalDate fpStartdatoFar) {
        /* MOR: løpende fagsak med hele mødrekvoten og deler av fellesperioden */
        var mor = familie.mor();
        var fordelingMor = fordeling(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15), fpStartdatoFar.minusDays(1)));
        var søknadMor = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR).medAnnenForelder(
                        AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medUttaksplan(fordelingMor)
                .medMottattdato(fpStartdatoMor.minusWeeks(4));
        var saksnummerMor = mor.søk(søknadMor.build());

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummerMor);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerMor, fpStartdatoMor);

        validerInnsendtInntektsmeldingForeldrepenger(familie.mor().fødselsnummer(), fpStartdatoMor, mor.månedsinntekt(), false);

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
