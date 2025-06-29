package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak.DEN_ANDRE_PART_SYK_SKADET_IKKE_OPPFYLT;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet.ARBEID;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet.INNLAGT;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet.TRENGER_HJELP;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet.UFØRE;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.utsettelsesperiode;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAnnenForeldreHarRett;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderUttakDokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("fpsak")
@Tag("foreldrepenger")
class RegresjonPreWLB extends FpsakTestBase {


    @Test
    @DisplayName("Bare far har rett. Mor Ufør. Perioder uten at mor er i aktivitet skal trekke fra foreldrepenger uten aktivitetskrav.")
    @Description("Bare far har rett (BFHR) og mor mottak uføretrygd. Far har 15 uker uten aktivitetskrav som kan tas ut utover en "
            + "periode på 46 uker. Far søker uttak og utsettelserList uten at aktivitetskravet er oppfylt. Her vil uttaket bli innvilget"
            + "fra de 15 ukene uten aktivitetskrav, mens utsettelsen trekker dager. Far prøver å ta ut foreldrepenger uten aktivitetskrav"
            + "etter uke 46 og for avslag pga manglende stønadsdager igjen på konto.")
    void BFHRMorUføreTrekkerDagerFortløpendeNårVilkårIkkeErOppfylt() {
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NAV_STORD, "ARB001-001", LocalDate.of(2021, 07, 1))
                                .arbeidsforhold(TestOrganisasjoner.NAV_STORD, "ARB001-002", LocalDate.of(2017, 11, 1), LocalDate.of(2021, 7,1))
                                .build())
                        .build())
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .harUføretrygd()
                                .build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.of(2021, 10, 15))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdatoFar = fødselsdato.plusWeeks(6);

        /*
        * Far søker 5 uker med aktivitetskrav, hvor saksbehandler finner ut at mor ikke er i aktivtet. Her brukes 5 uker uten aktivitetskrav.
        * Far søker etter dette 5 uker etter en avslått periode med uttsettelse hvor det samme skjer.
        * Far søker 5 uker fra uke 38 til 43.
        * Han skal få innvilge totalt 12 av 15 uker med foreldrepenger uten aktivitetskrav, men får avslag på de siste 3 fordi
        *   disse dagene må tas ut innen uke 46 (+ eventuelle innvilgede utsettelserList).
        * */
        var uttaksperiode1 = uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar, fpStartdatoFar.plusWeeks(5).minusDays(1), ARBEID);
        var utsettelsesperiode1 = utsettelsesperiode(UtsettelsesÅrsak.FRI, fpStartdatoFar.plusWeeks(5), fpStartdatoFar.plusWeeks(10).minusDays(1), TRENGER_HJELP);
        var uttaksperiode2 = uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar.plusWeeks(10), fpStartdatoFar.plusWeeks(15).minusDays(1), ARBEID);
        var utsettelsesperiode2 = utsettelsesperiode(UtsettelsesÅrsak.FRI, fpStartdatoFar.plusWeeks(15), fpStartdatoFar.plusWeeks(38).minusDays(1), TRENGER_HJELP);
        var uttaksperiode3 = uttaksperiode(StønadskontoType.FORELDREPENGER, fpStartdatoFar.plusWeeks(38), fpStartdatoFar.plusWeeks(43).minusDays(1), UFØRE);
        var fordeling = fordeling(
                uttaksperiode1,
                utsettelsesperiode1,
                uttaksperiode2,
                utsettelsesperiode2,
                uttaksperiode3
        );
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.annenpartIkkeRettOgMorHarUføretrygd(familie.mor()))
                .medMottattdato(fødselsdato);
        var saksnummer = far.søk(søknad.build());

        var arbeidsgiver = far.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdatoFar);

        // Må bekrefte at mor er ufør inntil det kommer mock + modell
        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAnnenForeldreHarRett = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAnnenForeldreHarRett())
                .setAnnenforelderHarRett(false)
                .setBegrunnelse("Mor har ikke rett og er uføretrygded i følge pesys!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        var kontrollerAktivitetskravBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse())
                .ikkeDokumentert(uttaksperiode1)
                .ikkeDokumentert(utsettelsesperiode1)
                .ikkeDokumentert(uttaksperiode2)
                .ikkeDokumentert(utsettelsesperiode2)
                .setBegrunnelse("Mor er ikke i aktivtet i perioden som det søkes om, med unntak av siste periode som søkes uten aktivitetskrav");
        saksbehandler.bekreftAksjonspunkt(kontrollerAktivitetskravBekreftelse);

        // 2trinn pga bekreftet at mor er ufør
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());
        beslutter.hentFagsak(saksnummer);
        var bekreftelseFørstegangsbehandling = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse())
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelseFørstegangsbehandling);

        // Verifiseringer førstegangsbehandling
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var stonadskontoer = saksbehandler.valgtBehandling.getSaldoer().stonadskontoer();
        assertThat(stonadskontoer.get(Saldoer.SaldoVisningStønadskontoType.FORELDREPENGER).saldo())
                .as("Saldoen for stønadskonton FORELDREPENGER")
                .isZero();
        assertThat(stonadskontoer.get(Saldoer.SaldoVisningStønadskontoType.UTEN_AKTIVITETSKRAV).saldo())
                .as("Saldoen for stønadskonton MINSTERETT")
                .isZero();


        var innvilgedeUttaksperioder = saksbehandler.hentInnvilgedeUttaksperioder();
        assertThat(innvilgedeUttaksperioder)
                .as("Forventer at det er 3 innvilgete uttaksperioder")
                .hasSize(3);
        innvilgedeUttaksperioder.forEach(uttakResultatPeriode -> assertThat(innvilgedeUttaksperioder.get(0).getPeriodeResultatÅrsak())
                .as("Innvilget periode fra perioder uten aktivitetskrav")
                .isEqualTo(PeriodeResultatÅrsak.FORELDREPENGER_KUN_FAR_HAR_RETT_MOR_UFØR));

        var avslåtteUttaksperioder = saksbehandler.hentAvslåtteUttaksperioder();
        assertThat(avslåtteUttaksperioder)
                .as("Forventer at det er 3 avslåtte uttaksperioder")
                .hasSize(3);
        assertThat(avslåtteUttaksperioder.get(0).getPeriodeResultatÅrsak())
                .as("Første avslåtte periode er avslått fordi det ikke er søkt")
                .isEqualTo(PeriodeResultatÅrsak.AKTIVITETSKRAVET_SYKDOM_ELLER_SKADE_IKKE_DOKUMENTERT);
        assertThat(avslåtteUttaksperioder.get(0).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(avslåtteUttaksperioder.get(1).getPeriodeResultatÅrsak())
                .as("Første avslåtte periode er avslått fordi det ikke er søkt")
                .isEqualTo(PeriodeResultatÅrsak.AKTIVITETSKRAVET_SYKDOM_ELLER_SKADE_IKKE_DOKUMENTERT);
        assertThat(avslåtteUttaksperioder.get(1).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(avslåtteUttaksperioder.get(2).getPeriodeResultatÅrsak())
                .as("Første avslåtte periode er avslått fordi det ikke er søkt")
                .isEqualTo(PeriodeResultatÅrsak.IKKE_STØNADSDAGER_IGJEN);
        assertThat(avslåtteUttaksperioder.get(2).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Forventer at første uke avslått MSP")
                .isZero();
    }

    @Test
    @DisplayName("Far søker rundt fødsel, saksbehandler finner ut at mor ikke er syk eller innlagt, vil føre til avslag i perioden")
    @Description("Far søker rundt fødsel hvor han oppgir at mor er trenger hjelp. Saksbehanlder velger at sykdom ikke er dokumentert"
            + "og avslå perioden ifm fødel. Resten skal innvilges automatisk.")
    void farSøkerImfFødselMenMorErIkkeSykEllerInnlagt() {
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NAV_STORD, "ARB001-001", LocalDate.of(2021, 07, 1))
                                .arbeidsforhold(TestOrganisasjoner.NAV_STORD, "ARB001-002", LocalDate.of(2017, 11, 1), LocalDate.of(2021, 7,1))
                                .build())
                        .build())
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .harUføretrygd()
                                .build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.of(2021, 10, 15))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();
        var uttaksperiodeIfmFødsel = uttaksperiode(StønadskontoType.FEDREKVOTE, fødselsdato, fødselsdato.plusWeeks(1).minusDays(1), INNLAGT);
        var fordeling = fordeling(
                uttaksperiodeIfmFødsel,
                uttaksperiode(StønadskontoType.FEDREKVOTE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(20).minusDays(1)),
                uttaksperiode(StønadskontoType.FELLESPERIODE, fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(26).minusDays(1), ARBEID)
        );
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medMottattdato(fødselsdato);
        var saksnummer = far.søk(søknad.build());
        var arbeidsgiver = far.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fødselsdato);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaAnnenForeldreHarRett = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaAnnenForeldreHarRett())
                .setAnnenforelderHarRett(true)
                .setBegrunnelse("Mor gar rett!");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaAnnenForeldreHarRett);

        var avklarFaktaUttakPerioder = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderUttakDokumentasjonBekreftelse())
                .ikkeGodkjenn(uttaksperiodeIfmFødsel)
                .godkjennMorsAktivitet(VurderUttakDokumentasjonBekreftelse.DokumentasjonVurderingBehov.Behov.Årsak.AKTIVITETSKRAV_ARBEID);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakPerioder);

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new FastsettUttaksperioderManueltBekreftelse())
                .avslåPeriode(uttaksperiodeIfmFødsel.fom(), uttaksperiodeIfmFødsel.tom(), DEN_ANDRE_PART_SYK_SKADET_IKKE_OPPFYLT);
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());
        beslutter.hentFagsak(saksnummer);
        var bekreftelseFørstegangsbehandling = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse())
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelseFørstegangsbehandling);

        // Verifiseringer førstegangsbehandling
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .hasSize(1);
    }

}
