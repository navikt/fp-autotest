package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.medmor;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadFødsel;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.neovisionaries.i18n.CountryCode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaVergeBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.SjekkManglendeFødselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderMedlemskapsvilkårForutgåendeBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrFodselsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.Beregningsresultat;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.soknad.kontrakt.builder.BarnBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.MedlemskapDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PersonstatusDto;

@Tag("fpsak")
@Tag("engangsstonad")
class Fodsel extends VerdikjedeTestBase {

    @Test
    @DisplayName("Mor søker fødsel - godkjent")
    @Description("Mor søker fødsel - godkjent happy case")
    void morSøkerFødselGodkjent() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker fødsel - avvist")
    @Description("Mor søker fødsel - avvist fordi dokumentasjon mangler og barn er ikke registrert i freg")
    void morSøkerFødselAvvist() {
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
        var fødselsdato = LocalDate.now().minusDays(30L);
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);

        var sjekkManglendeFødsel = saksbehandler.hentAksjonspunktbekreftelse(new SjekkManglendeFødselBekreftelse())
                .bekreftBarnErIkkeFødt(fødselsdato);
        saksbehandler.bekreftAksjonspunkt(sjekkManglendeFødsel);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        assertThat(saksbehandler.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsarsak (Forventer at behandlingen er avslått fordi fødselsdato er ikke oppgitt eller registrert!)")
                .isEqualTo(Avslagsårsak.FØDSELSDATO_IKKE_OPPGITT_ELLER_REGISTRERT);
    }

    @Test
    @DisplayName("Far søker registrert fødsel")
    @Description("Far søker registrert fødsel og blir avvist fordi far søker")
    void farSøkerFødselRegistrert() {
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

        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = far.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        assertThat(saksbehandler.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak (Forventer at behandlingen er avslått fordi søker er far!)")
                .isEqualTo(Avslagsårsak.SØKER_ER_FAR);
    }

    @Test
    @DisplayName("Mor søker fødsel overstyrt vilkår")
    @Description("Mor søker fødsel overstyrt vilkår adopsjon fra godkjent til avslått")
    void morSøkerFødselOverstyrt() {
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
        var fødselsdato = LocalDate.now().minusDays(30L);
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        var sjekkManglendeFødsel = saksbehandler.hentAksjonspunktbekreftelse(new SjekkManglendeFødselBekreftelse())
                .bekreftBarnErFødt(1, LocalDate.now().minusMonths(1));
        saksbehandler.bekreftAksjonspunkt(sjekkManglendeFødsel);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        overstyrer.hentFagsak(saksnummer);

        OverstyrFodselsvilkaaret overstyr = new OverstyrFodselsvilkaaret();
        overstyr.avvis(Avslagsårsak.SØKER_ER_FAR);
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        assertThat(overstyrer.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        assertThat(overstyrer.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsarsak")
                .isEqualTo(Avslagsårsak.SØKER_ER_FAR);
        overstyrer.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }

    @Test
    @DisplayName("Mor søker fødsel med flere barn")
    @Description("Mor søker fødsel med flere barn - happy case flere barn")
    void morSøkerFødselFlereBarn() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .personstatus(List.of(new PersonstatusDto(PersonstatusDto.Personstatuser.UTVA, LocalDate.now().minusYears(30), null)))
                        .medlemskap(List.of(new MedlemskapDto(LocalDate.now().minusYears(1), LocalDate.now().plusYears(3), CountryCode.DE, MedlemskapDto.DekningsType.IHT_AVTALE)))
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .barn(LocalDate.now().minusMonths(1))
                .build();
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(fødselsdato)
                .medBarn(BarnBuilder.fødsel(2, fødselsdato).build());
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunkt(new VurderMedlemskapsvilkårForutgåendeBekreftelse());

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        Beregningsresultat beregningResultatEngangsstonad = beslutter.valgtBehandling.getBeregningResultatEngangsstonad();
        assertThat(beregningResultatEngangsstonad.getBeregnetTilkjentYtelse())
                .as("Beregnet tilkjent ytelse")
                .isPositive();
        assertThat(beregningResultatEngangsstonad.getAntallBarn())
                .as("Antall barn")
                .isEqualTo(2);
    }

    @Test
    @DisplayName("Mor søker fødsel med verge")
    @Description("Mor søker fødsel med verge - skal få aksjonspunkt om registrering av verge når man er under 18")
    void morSøkerFødselMedVerge() {
        var familie = FamilieGenerator.ny()
                .forelder(mor(LocalDate.now().minusYears(17))
                        .personstatus(List.of(new PersonstatusDto(PersonstatusDto.Personstatuser.UTVA, LocalDate.now().minusYears(30), null)))
                        .medlemskap(List.of(new MedlemskapDto(LocalDate.now().minusYears(1), LocalDate.now().plusYears(3), CountryCode.DE, MedlemskapDto.DekningsType.IHT_AVTALE)))
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arena(ArenaSakerDto.YtelseTema.AAP, LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2), 100_00)
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4))
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-002", LocalDate.now().minusMonths(4))
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build();

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);

        AvklarFaktaVergeBekreftelse avklarFaktaVergeBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaVergeBekreftelse());
        avklarFaktaVergeBekreftelse.bekreftSøkerErKontaktperson()
                .bekreftSøkerErIkkeUnderTvungenForvaltning()
                .setVerge(familie.far().fødselsnummer());
        saksbehandler.bekreftAksjonspunkt(avklarFaktaVergeBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new VurderMedlemskapsvilkårForutgåendeBekreftelse());

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker uregistrert fødsel mindre enn 14 dager etter fødsel")
    @Description("Mor søker uregistrert fødsel mindre enn 14 dager etter fødsel. Behandlingen skal bli satt på vent")
    void morSøkerUregistrertFødselMindreEnn14DagerEtter() {
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
        var fødselsdato = LocalDate.now().minusWeeks(1);
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);

        assertThat(saksbehandler.valgtBehandling.erSattPåVent()).as("behandling er ikke satt på vent").isTrue();
    }

    @Test
    @DisplayName("Medmor søker fødsel")
    @Description("Medmor søker fødsel - søkand blir avslått fordi søker er medmor")
    void medmorSøkerFødsel() {
        var familie = FamilieGenerator.ny()
                .forelder(mor().build())
                .forelder(medmor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusDays(30))
                .build();

        var medmor = familie.medmor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = medmor.søk(søknad);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        assertThat(saksbehandler.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak")
                .isEqualTo(Avslagsårsak.SØKER_ER_MEDMOR);
    }

}
