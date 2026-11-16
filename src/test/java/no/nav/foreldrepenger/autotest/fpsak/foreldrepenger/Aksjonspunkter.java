package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.generator.Landkoder.DEU;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEndringMaler.lagEndringssøknad;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil.helgejustertTilMandag;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FptilbakeTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.MerkOpptjeningUtlandDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.SjekkManglendeFødselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSøknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApFaktaFeilutbetaling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApForeldelse;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVilkårsvurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.FattVedtakTilbakekreving;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Prosent;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.OpptjeningMaler;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.soknad.kontrakt.builder.BarnBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Rolle;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.FamilierelasjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.PermisjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.Permisjonstype;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.YtelseDto;

@Tag("util")
@Tag("foreldrepenger")
class Aksjonspunkter extends FptilbakeTestBase {

    @Test
    @DisplayName("5040 – REGISTRER_PAPIRSØKNAD_FORELDREPENGER")
    void aksjonspunkt_REGISTRER_PAPIRSØKNAD_FORELDREPENGER_5040() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
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
    @DisplayName("5018 – VURDER_OMSORGSOVERTAKELSEVILKÅRET")
    void aksjonspunkt_VURDER_OMSORGSOVERTAKELSEVILKÅRET_5018() {
        var fødselsdatoBarn = LocalDate.now().minusDays(2);
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .ytelse(YtelseDto.YtelseType.DAGPENGER, LocalDate.now().minusYears(1), LocalDate.now().minusWeeks(12), 1000, 100)
                        .ytelse(YtelseDto.YtelseType.SYKEPENGER, LocalDate.now().minusMonths(9), LocalDate.now(), 462, 100)
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(fødselsdatoBarn)
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var adopsjon = BarnBuilder.adopsjon(
                fødselsdato,
                false
        ).build();
        var fordeling = fordeling(
                uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10))
        );
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medBarn(adopsjon)
                .medPerioder(fordeling.perioder())
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_OMSORGSOVERTAKELSEVILKÅRET);
    }

    @Test
    @DisplayName("5089 – VURDER_OPPTJENINGSVILKÅRET")
    void aksjonspunkt_VURDER_OPPTJENINGSVILKÅRET_5089() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny()
                                .arbeidsforhold(LocalDate.now().minusMonths(2))
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
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
    void aksjonspunkt_VURDER_FAKTA_FOR_ATFL_SN_5058() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
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
                uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)),
                uttaksperiode(Rolle.MOR, KontoType.FEDREKVOTE, fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(30)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medPerioder(fordeling.perioder())
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

    @DisplayName("5068 – AUTOMATISK_MARKERING_AV_UTENLANDSSAK")
    @Test
    void aksjonspunkt_AUTOMATISK_MARKERING_AV_UTENLANDSSAK_5068() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .build();

        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(3);
        var fpStartdato = termindato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medAndreInntekterSiste10Mnd(List.of(OpptjeningMaler.utenlandskArbeidsforhold(DEU)))
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

    @DisplayName("5027 – SJEKK_MANGLENDE_FØDSEL")
    @Test
    void aksjonspunkt_SJEKK_MANGLENDE_FØDSEL_5027() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
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

    @Test
    @DisplayName("5041 – VURDER_PERMISJON_UTEN_SLUTTDATO")
    void aksjonspunkt_VURDER_PERMISJON_UTEN_SLUTTDATO_5041() {
        var fødselsdato = LocalDate.now().minusWeeks(3);
        var fpStartdato = fødselsdato.minusWeeks(3);
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny().arbeidsforhold(LocalDate.now().minusYears(3), 480_000,
                                List.of(new PermisjonDto(100, fpStartdato.minusMonths(2), null,
                                        Permisjonstype.VELFERDSPERMISJON)))
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(fødselsdato)
                .build();
        var mor = familie.mor();
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        ventPåInntektsmeldingForespørsel(saksnummer);
        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_PERMISJON_UTEN_SLUTTDATO);
    }

    @Test
    @DisplayName("5049 – FASTSETT_BEREGNINGSGRUNNLAG_FOR_SN_NY_I_ARBEIDSLIVET")
    void aksjonspunkt_FASTSETT_BEREGNINGSGRUNNLAG_FOR_SN_NY_I_ARBEIDSLIVET_5049() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
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
                uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)),
                uttaksperiode(Rolle.MOR, KontoType.FEDREKVOTE, fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(30)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medPerioder(fordeling.perioder())
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

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_FOR_SN_NY_I_ARBEIDSLIVET);
    }

    @Test
    @DisplayName("5047 – FASTSETT_BEREGNINGSGRUNNLAG_TIDSBEGRENSET_ARBEIDSFORHOLD")
    void aksjonspunkt_FASTSETT_BEREGNINGSGRUNNLAG_TIDSBEGRENSET_ARBEIDSFORHOLD_5047() {
        var fødselsdato = LocalDate.now().minusWeeks(3);
        var fpStartdato = fødselsdato.minusWeeks(3);
        var kortvarigFom = fpStartdato.minusMonths(3);
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny()
                                // Tidligere arbeidsforhold, avsluttet før det kortvarige, så opptjeningsvilkåret er oppfylt
                                .arbeidsforhold(TestOrganisasjoner.NAV_OSLO, 100, LocalDate.now().minusYears(3), kortvarigFom.minusDays(1), 360_000)
                                // Kortvarig arbeidsforhold (under 6 mnd) som slutter etter skjæringstidspunktet
                                .arbeidsforhold(TestOrganisasjoner.NAV_BERGEN, 100, kortvarigFom, fpStartdato.plusMonths(2), 360_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(fødselsdato)
                .build();
        var mor = familie.mor();
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);
        var arbeidsgiver = mor.arbeidsgivere().arbeidgiver(new Orgnummer(TestOrganisasjoner.NAV_BERGEN.orgnummer().value()));
        var inntektsmelding = arbeidsgiver.lagInntektsmeldingFP(fpStartdato)
                .medBeregnetInntekt(60_000)
                .leggTilEndretInntektÅrsak(Inntektsmelding.Endringsårsaker.Endringsårsak.BONUS);
        ventPåInntektsmeldingForespørsel(saksnummer);
        arbeidsgiver.sendInntektsmelding(saksnummer, inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);
        var vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderFaktaOmBeregningBekreftelse())
                .leggTilVurderTidsbegrenset(true);
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_TIDSBEGRENSET_ARBEIDSFORHOLD);
    }

    @Test
    @DisplayName("1b. Oppretter en tilbakekreving manuelt med en feilutbetalingsperiode som er foreldet")
    @Description("To perioder, en eldre enn foreldelsesfristen på 30 måneder som utleder aksjonspunkt 5003 "
            + "Vurder foreldelse, og en fersk periode som går videre til vilkårsvurdering.")
    void opprettTilbakekrevingManueltMedForeldelse() {
        var ytelseType = "FP";
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntekt(InntektGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad);

        ventPåInntektsmeldingForespørsel(saksnummer);
        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmelding(saksnummer, arbeidsgiver.lagInntektsmeldingFP(fpStartdato));
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        AllureHelper.debugFritekst("Ferdig med førstegangsbehandling");

        var fordeling = fordeling(
                uttaksperiode(Rolle.MOR, KontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1)),
                uttaksperiode(Rolle.MOR, KontoType.FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(10).minusDays(1))
        );
        var endringsdato = helgejustertTilMandag(fødselsdato.plusWeeks(8));
        var søknadE = lagEndringssøknad(søknad.build(), saksnummer, endringsdato, fordeling);
        mor.søk(søknadE);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        tbksaksbehandler.opprettTilbakekreving(saksnummer, saksbehandler.valgtBehandling.uuid, ytelseType);
        tbksaksbehandler.hentSisteBehandling(saksnummer);
        tbksaksbehandler.ventTilBehandlingErPåVent();
        assertThat(tbksaksbehandler.valgtBehandling.venteArsakKode)
                .as("Venteårsak")
                .isEqualTo("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG");

        var kravgrunnlag = new Kravgrunnlag(saksnummer, mor.fødselsnummer().value(),
                saksbehandler.valgtBehandling.id, ytelseType, "NY");
        kravgrunnlag.leggTilForeldetPeriode();
        kravgrunnlag.leggTilGeneriskPeriode();
        tbksaksbehandler.sendNyttKravgrunnlag(kravgrunnlag, saksnummer, saksbehandler.valgtBehandling.id);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(7003);

        var vurderFakta = (ApFaktaFeilutbetaling) tbksaksbehandler.hentAksjonspunktbehandling(7003);
        vurderFakta.addGeneriskVurdering(ytelseType);
        tbksaksbehandler.behandleAksjonspunkt(vurderFakta);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5003);

        var vurderForeldelse = (ApForeldelse) tbksaksbehandler.hentAksjonspunktbehandling(5003);
        vurderForeldelse.addGeneriskVurdering();
        tbksaksbehandler.behandleAksjonspunkt(vurderForeldelse);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5002);

        var vurderVilkår = (ApVilkårsvurdering) tbksaksbehandler.hentAksjonspunktbehandling(5002);
        vurderVilkår.addGeneriskVurdering();
        tbksaksbehandler.behandleAksjonspunkt(vurderVilkår);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5004);

        tbksaksbehandler.behandleAksjonspunkt(tbksaksbehandler.hentAksjonspunktbehandling(5004));
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5005);

        tbkbeslutter.hentSisteBehandling(saksnummer);
        tbkbeslutter.ventTilBehandlingHarAktivtAksjonspunkt(5005);

        var fattVedtak = (FattVedtakTilbakekreving) tbkbeslutter.hentAksjonspunktbehandling(5005);
        fattVedtak.godkjennAksjonspunkt(7003);
        fattVedtak.godkjennAksjonspunkt(5003);
        fattVedtak.godkjennAksjonspunkt(5002);
        fattVedtak.godkjennAksjonspunkt(5004);
        tbkbeslutter.behandleAksjonspunkt(fattVedtak);
        tbkbeslutter.ventTilAvsluttetBehandling();

        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløp())
                .as("Tilbakekrevingsbeløp i beregningsresultat, kun perioden som ikke er foreldet")
                .isEqualTo(1616);
    }
}
