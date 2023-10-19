package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType.OMSORGSVILKÅRET;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaresignalerDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaOmsorgOgForeldreansvarBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.BarnBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.SøkerBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.OpptjeningMaler;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.GrunnlagDto;

@Tag("util")
class Aksjonspunkter extends FpsakTestBase {

    @Test
    @DisplayName("REGISTRER_PAPIRSØKNAD_FORELDREPENGER")
    void aksjonspunkt_FOEDSELSSOKNAD_FORELDREPENGER_5040() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(8))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var saksnummer = mor.søkPapirsøknadForeldrepenger();

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.REGISTRER_PAPIRSØKNAD_FORELDREPENGER);
    }

    @Test
    @DisplayName("AVKLAR_ADOPSJONSDOKUMENTAJON")
    void aksjonspunkt_ADOPSJONSSOKNAD_FORELDREPENGER_5004() {
        var fødselsdatoBarn = LocalDate.now().minusDays(2);
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arena(ArenaSakerDto.YtelseTema.DAG, LocalDate.now().minusYears(1), LocalDate.now().minusWeeks(12), 10_000)
                                .infotrygd(GrunnlagDto.Ytelse.SP, LocalDate.now().minusMonths(9), LocalDate.now(),
                                        GrunnlagDto.Status.AVSLUTTET, fødselsdatoBarn)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(fødselsdatoBarn)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var adopsjon = BarnBuilder.adopsjon(
                fødselsdato,
                false
        ).build();
        var fordeling = fordeling(
                uttaksperiode(StønadskontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10))
        );
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medBarn(adopsjon)
                .medFordeling(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_ADOPSJONSDOKUMENTAJON);
    }

    @Test
    @DisplayName("MANUELL_VURDERING_AV_OMSORGSVILKÅRET")
    void aksjonspunkt_ADOPSJONSSOKNAD_ENGANGSSTONAD_5011() {
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
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1L);
        var søknad = SøknadEngangsstønadMaler.lagEngangstønadOmsorg(BrukerRolle.MOR, omsorgsovertakelsedato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        var avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class)
                .setVilkårType(OMSORGSVILKÅRET);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET);
    }

    @Test
    @DisplayName("VURDER_OPPTJENINGSVILKÅRET")
    void aksjonspunkt_FOEDSELSSOKNAD_FORELDREPENGER_5089() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusMonths(2))
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(15))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.gjenopptaBehandling();
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_OPPTJENINGSVILKÅRET);

    }

    @Test
    @DisplayName("5058 – VURDER_FAKTA_FOR_ATFL_SN")
    void aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var fødselsdato = LocalDate.now().minusWeeks(3);
        var næringOpptjening = OpptjeningMaler.egenNaeringOpptjening(mor.arbeidsforhold().arbeidsgiverIdentifikasjon().value(), true, 30_000, false);
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = fordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(StønadskontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)),
                uttaksperiode(StønadskontoType.FEDREKVOTE, fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(30)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordeling)
                .medSøker(new SøkerBuilder(BrukerRolle.MOR).medSelvstendigNæringsdrivendeInformasjon(List.of(næringOpptjening)).build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());
        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medRefusjonsBelopPerMnd(ProsentAndel.valueOf(100));
        arbeidsgiver.sendInntektsmeldinger(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);
        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(1));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilNyIArbeidslivet(true);
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

    }

    @DisplayName("AUTOMATISK_MARKERING_AV_UTENLANDSSAK_KODE")
    @Test
    void aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER_() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(3);
        var fpStartdato = termindato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medSøker(new SøkerBuilder(BrukerRolle.MOR).medAndreInntekterSiste10Mnd(List.of(OpptjeningMaler.utenlandskArbeidsforhold(CountryCode.NO))).build())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTOMATISK_MARKERING_AV_UTENLANDSSAK_KODE);

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);
    }

    @DisplayName("SJEKK_MANGLENDE_FØDSEL")
    @Test
    void aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER_5027() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = LocalDate.now().minusWeeks(3);
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL);
    }

    // Denne testen er avhengig av at fprisk kjører!
    @Test
    @DisplayName("5095 – VURDER_FARESIGNALER_KODE")
    void aksjonspunkt_VURDER_FARESIGNALER_KODE_5095() {
        var fødselsdatoBarn = LocalDate.now().minusMonths(1);
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidsforhold(TestOrganisasjoner.NYLIG_OPPSTATET, LocalDate.now())
                                .infotrygd(GrunnlagDto.Ytelse.SP, LocalDate.now().minusMonths(9), LocalDate.now(), GrunnlagDto.Status.LØPENDE, fødselsdatoBarn)
                                .arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(fødselsdatoBarn)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(800_000, 1);
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.hentAksjonspunktbekreftelse(VurderFaresignalerDto.class);
//        vurderFaresignalerDto.setHarInnvirketBehandlingen(true);
//        vurderFaresignalerDto.setBegrunnelse("HELLO");
//        saksbehandler.bekreftAksjonspunkt(vurderFaresignalerDto);
    }
}
