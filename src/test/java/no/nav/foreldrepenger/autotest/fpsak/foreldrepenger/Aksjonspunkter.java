package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType.OMSORGSVILKÅRET;
import static no.nav.foreldrepenger.generator.soknad.erketyper.FordelingErketyper.fordeling;
import static no.nav.foreldrepenger.generator.soknad.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.generator.soknad.erketyper.UttaksperioderErketyper.overføringsperiode;
import static no.nav.foreldrepenger.generator.soknad.erketyper.UttaksperioderErketyper.uttaksperiode;

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
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderVilkaarForSykdomBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaOmsorgOgForeldreansvarBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Overføringsårsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.generator.soknad.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.generator.soknad.erketyper.SøknadEngangsstønadErketyper;

@Tag("util")
class Aksjonspunkter extends FpsakTestBase {

    @Test
    @DisplayName("REGISTRER_PAPIRSØKNAD_FORELDREPENGER")
    void aksjonspunkt_FOEDSELSSOKNAD_FORELDREPENGER_5040() {
        var familie = new Familie("500", SEND_DOKUMENTER_UTEN_SELVBETJENING);
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
        var familie = new Familie("172", SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var adopsjon = new Adopsjon(
                1,
                fødselsdato,
                false,
                false,
                null,
                fødselsdato,
                List.of(fødselsdato)
        );
        var fordeling = fordeling(
                uttaksperiode(StønadskontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medRelasjonTilBarn(adopsjon)
                .medFordeling(fordeling.build())
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_ADOPSJONSDOKUMENTAJON);
    }

    @Test
    @DisplayName("MANUELL_VURDERING_AV_OMSORGSVILKÅRET")
    void aksjonspunkt_ADOPSJONSSOKNAD_ENGANGSSTONAD_5011() {
        var familie = new Familie("55", SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1L);
        var søknad = SøknadEngangsstønadErketyper.lagEngangstønadOmsorg(BrukerRolle.MOR, omsorgsovertakelsedato);
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
        var familie = new Familie("01", SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.gjenopptaBehandling();
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_OPPTJENINGSVILKÅRET);

    }

    @Test
    @DisplayName("5058 – VURDER_FAKTA_FOR_ATFL_SN")
    void aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER() {
        var familie = new Familie("501", SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = LocalDate.now().minusWeeks(3);
        var næringOpptjening = OpptjeningErketyper.egenNaeringOpptjening(mor.arbeidsforhold().arbeidsgiverIdentifikasjon().value(), true, 30_000, false);
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = fordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(StønadskontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10)),
                uttaksperiode(StønadskontoType.FEDREKVOTE, fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(30)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordeling.build())
                .medOpptjening(næringOpptjening)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
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

    @Test
    @DisplayName("VURDER_OM_VILKÅR_FOR_SYKDOM_OPPFYLT")
    void aksjonspunkt_FAR_FOEDSELSSOKNAD_FORELDREPENGER_5044() {
        var familie = new Familie("86", SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var far = familie.far();
        var termindato = LocalDate.now().plusWeeks(2);
        var fordeling = fordeling(
                overføringsperiode(Overføringsårsak.SYKDOM_ANNEN_FORELDER, StønadskontoType.MØDREKVOTE,
                        termindato, termindato.plusWeeks(10)),
                uttaksperiode(StønadskontoType.FEDREKVOTE, termindato.plusWeeks(20), termindato.plusWeeks(30)));
        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.FAR)
                .medFordeling(fordeling.build())
                .medAnnenForelder(lagNorskAnnenforeldre(familie.mor()));
        var saksnummer = far.søk(søknad.build());

        var arbeidsgiver = far.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, termindato);

        saksbehandler.hentFagsak(saksnummer);

        var avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class)
                .setUtstedtdato(termindato.minusWeeks(3));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        var vurderVilkaarForSykdomBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderVilkaarForSykdomBekreftelse.class)
                .setErMorForSykVedFodsel(true);
        saksbehandler.bekreftAksjonspunkt(vurderVilkaarForSykdomBekreftelse);

    }

    @DisplayName("AUTOMATISK_MARKERING_AV_UTENLANDSSAK_KODE")
    @Test
    void aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER_() {
        var familie = new Familie("75", SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(3);
        var fpStartdato = termindato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medOpptjening(OpptjeningErketyper.utenlandskArbeidsforhold(CountryCode.NO))
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTOMATISK_MARKERING_AV_UTENLANDSSAK_KODE);

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);
    }

    @DisplayName("SJEKK_MANGLENDE_FØDSEL")
    @Test
    void aksjonspunkt_MOR_FOEDSELSSOKNAD_FORELDREPENGER_5027() {
        var familie = new Familie("501", SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = LocalDate.now().minusWeeks(3);
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
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
        var familie = new Familie("522", SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        var vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(800_000, 1);
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        var vurderFaresignalerDto = saksbehandler.hentAksjonspunktbekreftelse(VurderFaresignalerDto.class);
//        vurderFaresignalerDto.setHarInnvirketBehandlingen(true);
//        vurderFaresignalerDto.setBegrunnelse("HELLO");
//        saksbehandler.bekreftAksjonspunkt(vurderFaresignalerDto);
    }
}
