package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType.FORELDREANSVARSVILKÅRET_2_LEDD;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType.OMSORGSVILKÅRET;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadOmsorg;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;

import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;

import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvForeldreansvarAndreLedd;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvOmsorgsvilkoret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaOmsorgOgForeldreansvarBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadEngangstonadBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.common.domain.BrukerRolle;

@Tag("fpsak")
@Tag("engangsstonad")
@Disabled
class Omsorgsovertakelse extends FpsakTestBase {

    // TODO PAPIRSØKNAD!!!

    @Test
    @DisplayName("Mor søker Omsorgsovertakelse - godkjent")
    @Description("Mor søker Omsorgsovertakelse - godkjent happy case")
    void MorSøkerOmsorgsovertakelseGodkjent() {
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

        var medmor = familie.medmor();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1L);
        var saksnummer = medmor.søkPapirsøknadForeldrepenger();

        saksbehandler.hentFagsak(saksnummer);
        var papirsøknadAP = saksbehandler.hentAksjonspunktbekreftelse(PapirSoknadEngangstonadBekreftelse.class);


        var søknad = lagEngangstønadOmsorg(BrukerRolle.MOR, omsorgsovertakelsedato);
//        var saksnummer = medmor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse
                .setVilkårType(OMSORGSVILKÅRET);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        VurderingAvOmsorgsvilkoret vurderingAvOmsorgsvilkoret = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class);
        vurderingAvOmsorgsvilkoret.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(vurderingAvOmsorgsvilkoret);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(
                saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker Omsorgsovertakelse - avvist")
    @Description("Mor søker Omsorgsovertakelse - avvist fordi mor ikke er død")
    void morSøkerOmsorgsovertakelseAvvist() {
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
        var søknad = lagEngangstønadOmsorg(BrukerRolle.MOR, omsorgsovertakelsedato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse
                .setVilkårType(OMSORGSVILKÅRET);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        VurderingAvOmsorgsvilkoret vurderingAvOmsorgsvilkoret = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class);
        vurderingAvOmsorgsvilkoret
                .bekreftAvvist(Avslagsårsak.MOR_IKKE_DØD);
        saksbehandler.bekreftAksjonspunkt(vurderingAvOmsorgsvilkoret);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(
                saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
    }

    @Test
    @DisplayName("Far søker Omsorgsovertakelse - godkjent")
    @Description("Far søker Omsorgsovertakelse - får godkjent aksjonspunkt og blir invilget")
    void farSøkerOmsorgsovertakelseGodkjent() {
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arena(ArenaSakerDto.YtelseTema.AAP, LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2), 10_000)
                                .build())
                        .build())
                .forelder(mor().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var far = familie.far();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1L);
        var søknad = lagEngangstønadOmsorg(BrukerRolle.FAR, omsorgsovertakelsedato);
        var saksnummer = far.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse
                .setVilkårType(OMSORGSVILKÅRET);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        VurderingAvOmsorgsvilkoret vurderingAvOmsorgsvilkoret = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class);
        vurderingAvOmsorgsvilkoret.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(vurderingAvOmsorgsvilkoret);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(
                saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Far søker Foreldreansvar 2. ledd - godkjent")
    @Description("Far søker Foreldreansvar 2. ledd - får godkjent aksjonspunkt og blir invilget")
    void farSøkerForeldreansvarGodkjent() {
        var familie = FamilieGenerator.ny()
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arena(ArenaSakerDto.YtelseTema.AAP, LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2), 10_000)
                                .build())
                        .build())
                .forelder(mor().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var far = familie.far();
        var omsorgsovertakelsedato = LocalDate.now().plusMonths(1L);
        var søknad = lagEngangstønadOmsorg(BrukerRolle.FAR, omsorgsovertakelsedato);
        var saksnummer = far.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse
                .setVilkårType(FORELDREANSVARSVILKÅRET_2_LEDD);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        var vurderingAvForeldreansvarAndreLedd = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvForeldreansvarAndreLedd.class);
        vurderingAvForeldreansvarAndreLedd.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(vurderingAvForeldreansvarAndreLedd);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(
                saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_FORELDREANSVARSVILKÅRET_2_LEDD));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }
}
