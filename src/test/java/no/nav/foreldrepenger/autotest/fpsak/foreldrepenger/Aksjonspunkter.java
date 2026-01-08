package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType.ES_OMSORGSVILKÅRET;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaresignalerDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderOmsorgsovertakelseVilkårAksjonspunktDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.MerkOpptjeningUtlandDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.SjekkManglendeFødselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSøknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Prosent;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.OpptjeningMaler;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.soknad.kontrakt.barn.AdopsjonDto;
import no.nav.foreldrepenger.soknad.kontrakt.builder.BarnBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.GrunnlagDto;

@Tag("util")
class Aksjonspunkter extends VerdikjedeTestBase {

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
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var saksnummer = mor.søkPapirsøknadForeldrepenger();

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.REGISTRER_PAPIRSØKNAD_FORELDREPENGER);
        var aksjonspunktBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new PapirSøknadForeldrepengerBekreftelse());
        var fordeling = new FordelingDto();
        var fpff = new PermisjonPeriodeDto(KontoType.FORELDREPENGER_FØR_FØDSEL,
                fpStartdato, fødselsdato.minusDays(1));
        var mødrekvote = new PermisjonPeriodeDto(KontoType.MØDREKVOTE,
                fødselsdato, fødselsdato.plusWeeks(10));
        fordeling.permisjonsPerioder.add(fpff);
        fordeling.permisjonsPerioder.add(mødrekvote);
        aksjonspunktBekreftelse.morSøkerFødsel(fordeling, fødselsdato, fpff.periodeFom.minusWeeks(3));
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelse);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);
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
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var adopsjon = BarnBuilder.adopsjon(
                fødselsdato,
                false
        ).build();
        var fordeling = fordeling(
                uttaksperiode(KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10))
        );
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medBarn(adopsjon)
                .medUttaksplan(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_OMSORGSOVERTAKELSEVILKÅRET);
    }

    @Disabled
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
                .build();
        var mor = familie.mor();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1L);
        var søknad = SøknadEngangsstønadMaler.lagEngangstønadOmsorg(omsorgsovertakelsedato);
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);

        var vurderOmsorgsovertakelse = saksbehandler.hentAksjonspunktbekreftelse(new VurderOmsorgsovertakelseVilkårAksjonspunktDto())
                .oppfylt(ES_OMSORGSVILKÅRET, omsorgsovertakelsedato, false,
                        ((AdopsjonDto) søknad.build().barn()).fødselsdatoer().toArray(LocalDate[]::new));
        saksbehandler.bekreftAksjonspunkt(vurderOmsorgsovertakelse);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.FORESLÅ_VEDTAK);
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
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

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
                .build();

        var mor = familie.mor();
        var fødselsdato = LocalDate.now().minusWeeks(3);
        var næringOpptjening = OpptjeningMaler.egenNaeringOpptjening(
                mor.arbeidsforhold().arbeidsgiverIdentifikasjon(),
                true,
                30_000,
                false);
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = fordeling(
                uttaksperiode(KontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)),
                uttaksperiode(KontoType.FEDREKVOTE, fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(30)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medUttaksplan(fordeling)
                .medSelvstendigNæringsdrivendeInformasjon(næringOpptjening)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);
        var arbeidsgiver = mor.arbeidsgiver();
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medRefusjonBeløpPerMnd(Prosent.valueOf(100));
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);

        var sjekkManglendeFødsel = saksbehandler.hentAksjonspunktbekreftelse(new SjekkManglendeFødselBekreftelse())
                .bekreftBarnErFødt(1, LocalDate.now().minusMonths(1));
        saksbehandler.bekreftAksjonspunkt(sjekkManglendeFødsel);

        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
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
                .build();

        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(3);
        var fpStartdato = termindato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medAndreInntekterSiste10Mnd(List.of(OpptjeningMaler.utenlandskArbeidsforhold(CountryCode.DE)))
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTOMATISK_MARKERING_AV_UTENLANDSSAK_KODE);
        var sedAksjonspunkt = saksbehandler.hentAksjonspunktbekreftelse(new MerkOpptjeningUtlandDto())
                .setDokStatus(MerkOpptjeningUtlandDto.UtlandDokumentasjonStatus.DOKUMENTASJON_VIL_IKKE_BLI_INNHENTET)
                .setBegrunnelse("Mor har ikke opptjening i EØS!");
        saksbehandler.bekreftAksjonspunkt(sedAksjonspunkt);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
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
                .build();
        var mor = familie.mor();
        var fødselsdato = LocalDate.now().minusWeeks(3);
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL);
    }

    // Denne testen er avhengig av at fprisk kjører!
    @Disabled
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
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        var arbeidsgiver = mor.arbeidsgiver();
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderBeregnetInntektsAvvikBekreftelse())
                .leggTilInntekt(800_000, 1);
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.hentAksjonspunktbekreftelse(new VurderFaresignalerDto());
//        vurderFaresignalerDto.setHarInnvirketBehandlingen(true);
//        vurderFaresignalerDto.setBegrunnelse("HELLO");
//        saksbehandler.bekreftAksjonspunkt(vurderFaresignalerDto);
    }
}
