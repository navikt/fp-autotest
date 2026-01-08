package no.nav.foreldrepenger.autotest.fplos;

import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder.VURDER_ARBEIDSFORHOLD_INNTEKTSMELDING;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.graderingsperiodeSN;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidsforholdKomplettVurderingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdVersjonDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettMaanedsinntektUtenInntektsmeldingAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FordelBeregningsgrunnlagBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.ArbeidInntektsmeldingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding.ManueltArbeidsforholdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Prosent;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.ArbeidsforholdMaler;
import no.nav.foreldrepenger.generator.soknad.maler.OpptjeningMaler;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.soknad.kontrakt.builder.TilretteleggingBehovBuilder;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("fplos")
class Fplos extends VerdikjedeTestBase {

    @Test
    @DisplayName("Saksmarkering i fpsak gir oppgaveegenskap i LOS")
    @Description("Legger på saksmarkering som skal gi tilsvarende OppgaveEgenskap/AndreKriterier i LOS")
    void enkelSaksmarkering() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();

        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(6);
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforhold1 = arbeidsforholdene.getFirst().arbeidsgiverIdentifikasjon();

        var forsteTilrettelegging = new TilretteleggingBehovBuilder(ArbeidsforholdMaler.virksomhet(new Orgnummer(arbeidsforhold1)), termindato.minusMonths(3))
                .delvis(termindato.minusMonths(3), 50.0)
                .build();
        var søknad = lagSvangerskapspengerSøknad(termindato, List.of(forsteTilrettelegging));
        var saksnummerSVP = mor.søk(søknad);

        var arbeidsgivere = mor.arbeidsgivere();
        ventPåInntektsmeldingForespørsel(saksnummerSVP);
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummerSVP);

        saksbehandler.hentFagsak(saksnummerSVP);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler.hentAksjonspunktbekreftelse(new AvklarFaktaFødselOgTilrettelegging());
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        saksbehandler.endreSaksmarkering(saksnummerSVP, Set.of("SAMMENSATT_KONTROLL"));

        saksbehandler.ventTilHistorikkinnslag(HistorikkType.BYTT_ENHET);
        var bekreftSvangerskapspengervilkår = saksbehandler.hentAksjonspunktbekreftelse(new BekreftSvangerskapspengervilkår());
        bekreftSvangerskapspengervilkår.godkjenn().setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        // Skal komme oppgave for foreslå vedtak
        var oppgaver = saksbehandler.hentLosOppgaver(saksnummerSVP);
        assertThat(oppgaver).first().matches(o ->
                o.andreKriterier().contains("SAMMENSATT_KONTROLL"), "har egenskap SAMMENSATT_KONTROLL");

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
    }

    @Test
    @DisplayName("SN får saksmarkering Næring i LOS")
    @Description("Sjekker at oppgave er markert næring og at når markering fjernes så er den ikke tilstede i oppgaven")
    @Tag("beregning")
    void SN_med_gradering_og_arbeidsforhold_som_søker_refusjon_over_6G() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntektytelse(
                        InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().selvstendigNæringsdrivende(1_000_000).build()).build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusDays(2))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var opptjening = OpptjeningMaler.egenNaeringOpptjening(mor.arbeidsforhold().arbeidsgiverIdentifikasjon(),
                mor.næringStartdato(), LocalDate.now(), false, 30_000, false);
        var fordeling = fordeling(
                uttaksperiode(KontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)),
                graderingsperiodeSN(KontoType.FELLESPERIODE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(10).minusDays(1),
                        50));
        var søknad = lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medSelvstendigNæringsdrivendeInformasjon(opptjening)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato).medRefusjonBeløpPerMnd(Prosent.valueOf(100));
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.VEDLEGG_MOTTATT);
        debugLoggBehandling(saksbehandler.valgtBehandling);

        // FORDEL BEREGNINGSGRUNNLAG //
        var graderingsperiode = fordeling.uttaksperioder().get(2);
        var fordelBeregningsgrunnlagBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(new FordelBeregningsgrunnlagBekreftelse())
                .settFastsattBeløpOgInntektskategoriMedRefusjon(graderingsperiode.fom(), 500_000, 500_000,
                        Inntektskategori.ARBEIDSTAKER, 1)
                .settFastsattBeløpOgInntektskategori(graderingsperiode.fom(), 235_138,
                        Inntektskategori.SELVSTENDIG_NÆRINGSDRIVENDE, 2)
                .settFastsattBeløpOgInntektskategoriMedRefusjon(graderingsperiode.tom().plusDays(1), 720_000, 720_000,
                        Inntektskategori.ARBEIDSTAKER, 1)
                .settFastsattBeløpOgInntektskategori(graderingsperiode.tom().plusDays(1), 0,
                        Inntektskategori.SELVSTENDIG_NÆRINGSDRIVENDE, 2);
        var oppgaverFør = saksbehandler.hentLosOppgaver(saksnummer);
        assertThat(oppgaverFør).first()
                .matches(o -> o.andreKriterier().contains("NÆRING"), "har egenskap NÆRING");
        saksbehandler.endreSaksmarkering(saksnummer, Set.of());
        saksbehandler.bekreftAksjonspunkt(fordelBeregningsgrunnlagBekreftelse);

        // Skal komme oppgave for foreslå vedtak
        var oppgaver = saksbehandler.hentLosOppgaver(saksnummer);
        assertThat(oppgaver).first()
                .matches(o -> o.andreKriterier().isEmpty(), "har ingen egenskaper");

    }

    @Test
    @DisplayName("Fødsel og fiktivt arbeidsforhold gir bare beslutter-egenskap i LOS.")
    @Description("Mor søker termin uten arbeidsforhold i AAREG. Fiktivt arbeidsforhold. Sjekker oppgavekriterie til_beslutter")
    void morSøkerTerminUtenAktiviteterIAareg() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .inntektsperiode(TestOrganisasjoner.NAV_BERGEN, LocalDate.now().minusMonths(24), LocalDate.now().minusMonths(23), 150_000)
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", 0, LocalDate.now().minusYears(4), LocalDate.now().minusYears(1), null)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now())
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var termindato = fødselsdato.plusDays(2);
        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        if (!saksbehandler.harAksjonspunkt(AksjonspunktKoder.VURDER_OPPTJENINGSVILKÅRET)) {
            throw new IllegalStateException("Forventer å ha havnet i opptjeningsvilkåret her");
        }

        overstyrer.hentFagsak(saksnummer);
        opprettManueltArbeidsforhold5085();

        saksbehandler.velgSisteBehandling();
        // VURDER OPPTJENING: Godkjenn fiktivt arbeidsforhold i opptjening //
        var vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderPerioderOpptjeningBekreftelse())
                .godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // FAKTA OM BERGNING: Fastsett inntekt for fiktivt arbeidsforhold og vurder om
        // mottatt ytelse
        var fastsattInntekt = new FastsettMaanedsinntektUtenInntektsmeldingAndel(1L, 25_000);
        var ab = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
                .leggTilMaanedsinntektUtenInntektsmelding(List.of(fastsattInntekt))
                .leggTilMottarYtelse(List.of(new ArbeidstakerandelUtenIMMottarYtelse(1L, false)));
        saksbehandler.bekreftAksjonspunkt(ab);

        // AVVIK I BEREGNING //
        var aksjonspunktBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderBeregnetInntektsAvvikBekreftelse())
                .leggTilInntekt(300_000, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelse);

        // FORESLÅ VEDTAK //
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        // FATTE VEDTAK //
        beslutter.hentFagsak(saksnummer);
        // Skal komme oppgave for foreslå vedtak
        var oppgaver = beslutter.hentLosOppgaver(saksnummer);
        assertThat(oppgaver).first()
                .matches(o -> o.andreKriterier().contains("TIL_BESLUTTER"), "har egenskape TIL_BESLUTTER");
        var apArbeid = beslutter.hentAksjonspunkt(VURDER_ARBEIDSFORHOLD_INNTEKTSMELDING);
        var apAvvikBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        var apFaktaOmBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        var apVurderOpptjening = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(
                List.of(apAvvikBeregning, apFaktaOmBeregning, apArbeid, apVurderOpptjening));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

    }

    private void opprettManueltArbeidsforhold5085() {
        overstyrer.åpneForNyArbeidsforholdVurdering(new BehandlingIdVersjonDto(overstyrer.valgtBehandling));
        var ab = overstyrer.hentAksjonspunktbekreftelse(new ArbeidInntektsmeldingBekreftelse());
        var dto = new ManueltArbeidsforholdDto(
                overstyrer.valgtBehandling.uuid,
                "Dette er en begrunnelse",
                "342352362",
                null,
                "Min bedrift",
                LocalDate.now().minusYears(3),
                LocalDate.now().plusYears(2),
                100, ArbeidsforholdKomplettVurderingType.MANUELT_OPPRETTET_AV_SAKSBEHANDLER,
                (long) overstyrer.valgtBehandling.versjon);
        overstyrer.lagreOpprettetArbeidsforhold(dto);
        overstyrer.bekreftAksjonspunkt(ab);
    }

    @Test
    @DisplayName("Enkel sjekk på at opprettelse av saksliste ikke gir feil")
    @Description("Lager en enkel saksliste med enkle kriterier LOS")
    void opprettSaksliste() {
        var sakslisteId = oppgavestyrer.opprettSaksliste();
        assertThat(sakslisteId).isNotNull();
    }

}
