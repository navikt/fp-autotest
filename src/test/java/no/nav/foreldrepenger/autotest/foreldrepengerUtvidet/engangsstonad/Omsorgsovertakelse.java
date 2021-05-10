package no.nav.foreldrepenger.autotest.foreldrepengerUtvidet.engangsstonad;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType.FORELDREANSVARSVILKÅRET_2_LEDD;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType.OMSORGSVILKÅRET;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadOmsorgovertakelse;
import static no.nav.foreldrepenger.autotest.søknad.modell.felles.relasjontilbarn.OmsorgsOvertakelsesÅrsak.DØDSFALL_ANNEN_FORELDER;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvForeldreansvarAndreLedd;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvOmsorgsvilkoret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaOmsorgOgForeldreansvarBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.ManuellRegistreringEngangsstonadDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.AnnenForelderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.DekningsgradDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FamilieHendelseType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.RettigheterDto;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("fpsak")
@Tag("engangsstonad")
class Omsorgsovertakelse extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker Omsorgsovertakelse - godkjent")
    @Description("Mor søker Omsorgsovertakelse - godkjent happy case")
    void MorSøkerOmsorgsovertakelseGodkjent() {
        var familie = new Familie("55");
        var mor = familie.mor();
        var saksnummer = mor.søkPåPapir(DokumenttypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON);

        saksbehandler.hentFagsak(saksnummer);

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






        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1);
        var søknad = lagEngangstønadOmsorgovertakelse(BrukerRolle.MOR, omsorgsovertakelsedato, DØDSFALL_ANNEN_FORELDER);
//        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class)
                .setVilkårType(OMSORGSVILKÅRET);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        var vurderingAvOmsorgsvilkoret = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class)
                .bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(vurderingAvOmsorgsvilkoret);

        foreslåOgFatterVedtak(saksnummer);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker Omsorgsovertakelse 2.ledd – avvist")
    @Description("Mor søker Omsorgsovertakelse 2.ledd, men avvist fordi mor ikke er død")
    void morSøkerOmsorgsovertakelseAvvist() {
        var familie = new Familie("55");
        var mor = familie.mor();
        var saksnummer = mor.søkPåPapir(DokumenttypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON);

        saksbehandler.hentFagsak(saksnummer);
        var manuellRegistreringEngangsstonadDto = saksbehandler
                .hentAksjonspunktbekreftelse(ManuellRegistreringEngangsstonadDto.class)
                .tema(FamilieHendelseType.ADOPSJON)
                .søkersRolle("MOR")
                .rettighet(RettigheterDto.ANNEN_FORELDER_DOED)
                .omsorgovertakelse(LocalDate.now().plusMonths(1))
                .barnFødselsdatoer(LocalDate.now().minusMonths(6), 1)
                .annenpart(AnnenForelderDto.of(familie.far().fødselsnummer(), false));
        saksbehandler.bekreftAksjonspunkt(manuellRegistreringEngangsstonadDto);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class)
                .setVilkårType(OMSORGSVILKÅRET);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        var vurderingAvOmsorgsvilkoret = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class)
                .bekreftAvvist(Avslagsårsak.MOR_IKKE_DØD);
        saksbehandler.bekreftAksjonspunkt(vurderingAvOmsorgsvilkoret);

        foreslåOgFatterVedtak(saksnummer);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
    }


    @Test
    @DisplayName("Far søker Foreldreansvar 2. ledd - godkjent")
    @Description("Far søker Foreldreansvar 2. ledd - får godkjent aksjonspunkt og blir invilget")
    void farSøkerForeldreansvarGodkjent() {
        var familie = new Familie("61");
        var far = familie.far();
        var saksnummer = far.søkPåPapir(DokumenttypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON);

        saksbehandler.hentFagsak(saksnummer);
        var manuellRegistreringEngangsstonadDto = saksbehandler
                .hentAksjonspunktbekreftelse(ManuellRegistreringEngangsstonadDto.class)
                .tema(FamilieHendelseType.ADOPSJON)
                .søkersRolle("FAR")
                .rettighet(RettigheterDto.ANNEN_FORELDER_DOED)
                .omsorgovertakelse(LocalDate.now().plusMonths(1))
                .barnFødselsdatoer(LocalDate.now().minusMonths(6), 1)
                .annenpart(AnnenForelderDto.of(familie.mor().fødselsnummer(), false));
        saksbehandler.bekreftAksjonspunkt(manuellRegistreringEngangsstonadDto);

        var avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class)
                .setVilkårType(FORELDREANSVARSVILKÅRET_2_LEDD);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        var vurderingAvForeldreansvarAndreLedd = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvForeldreansvarAndreLedd.class)
                .bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(vurderingAvForeldreansvarAndreLedd);

        foreslåOgFatterVedtak(saksnummer);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Far søker Omsorgsovertakelse 3.ledd – godkjent")
    @Description("Far søker Omsorgsovertakelse 3.ledd – får godkjent aksjonspunkt og blir invilget")
    void farSøkerOmsorgsovertakelseGodkjent() {
        var familie = new Familie("61");
        var far = familie.far();
        var saksnummer = far.søkPåPapir(DokumenttypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON);

        saksbehandler.hentFagsak(saksnummer);
        var manuellRegistreringEngangsstonadDto = saksbehandler
                .hentAksjonspunktbekreftelse(ManuellRegistreringEngangsstonadDto.class)
                .tema(FamilieHendelseType.ADOPSJON)
                .søkersRolle("FAR")
                .rettighet(RettigheterDto.ANNEN_FORELDER_DOED)
                .omsorgovertakelse(LocalDate.now().plusMonths(1))
                .barnFødselsdatoer(LocalDate.now().minusMonths(6), 1)
                .annenpart(AnnenForelderDto.of(familie.mor().fødselsnummer(), false));
        saksbehandler.bekreftAksjonspunkt(manuellRegistreringEngangsstonadDto);

        var avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class)
                .setVilkårType(OMSORGSVILKÅRET);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        var vurderingAvOmsorgsvilkoret = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class)
                .bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(vurderingAvOmsorgsvilkoret);

        foreslåOgFatterVedtak(saksnummer);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }
}
