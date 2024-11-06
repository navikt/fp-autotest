package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadTermin;
import static no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsavtaleDto.arbeidsavtale;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Venteårsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrFodselsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

@Tag("fpsak")
@Tag("engangsstonad")
class Termin extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker termin - godkjent")
    @Description("Mor søker termin - godkjent happy case")
    void morSøkerTerminGodkjent() {
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
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = lagEngangstønadTermin(termindato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaTerminBekreftelse());
        avklarFaktaTerminBekreftelse
                .setAntallBarn(1)
                .setUtstedtdato(LocalDate.now().minusMonths(1))
                .setTermindato(LocalDate.now().plusMonths(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_TERMINBEKREFTELSE));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

    }

    @Test
    @DisplayName("Mor søker termin overstyrt vilkår")
    @Description("Mor søker termin overstyrt vilkår fødsel fra oppfylt til avvist")
    void morSøkerTerminOvertyrt() {
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
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = lagEngangstønadTermin(termindato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaTerminBekreftelse());
        avklarFaktaTerminBekreftelse
                .setAntallBarn(1)
                .setUtstedtdato(LocalDate.now().minusMonths(1))
                .setTermindato(LocalDate.now().plusMonths(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        overstyrer.hentFagsak(saksnummer);

        OverstyrFodselsvilkaaret overstyr = new OverstyrFodselsvilkaaret();
        overstyr.avvis(Avslagsårsak.SØKER_ER_FAR);
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        assertThat(overstyrer.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        overstyrer.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.OVERSTYRING_AV_FØDSELSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
    }


    @Test
    @DisplayName("Far søker termin")
    @Description("Far søker termin avslått pga søker er far")
    void farSøkerTermin() {
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
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = lagEngangstønadTermin(termindato);
        var saksnummer = far.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new AvklarFaktaTerminBekreftelse());
        avklarFaktaTerminBekreftelse
                .setAntallBarn(1)
                .setUtstedtdato(LocalDate.now().minusMonths(1))
                .setTermindato(LocalDate.now().plusMonths(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
    }

    @Test
    @DisplayName("Setter behandling på vent og gjennoptar og henlegger")
    @Description("Setter behandling på vent og gjennoptar og henlegger")
    void settBehandlingPåVentOgGjenopptaOgHenlegg() {
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
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = lagEngangstønadTermin(termindato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.settBehandlingPåVent(LocalDate.now(), Venteårsak.AVV_DOK);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandlingen satt på vent")
                .isTrue();

        saksbehandler.gjenopptaBehandling();
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandlingen satt på vent")
                .isFalse();

        saksbehandler.henleggBehandling(BehandlingResultatType.HENLAGT_SØKNAD_TRUKKET);
        assertThat(saksbehandler.valgtBehandling.erHenlagt())
                .as("Behandlingen ble uventet ikke henlagt")
                .isTrue();
        assertThat(saksbehandler.getBehandlingsstatus())
                .as("Behandlingsstatus")
                .isEqualTo(BehandlingStatus.AVSLUTTET);
    }

    @Test
    @DisplayName("Mor søker termin 25 dager etter fødsel")
    @Description("Mor søker termin 25 dager etter fødsel - Får aksjonpunkt om manglende fødsel - godkjent")
    void morSøkerTermin25DagerTilbakeITid() {
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
        var termindato = LocalDate.now().minusDays(26);
        var søknad = lagEngangstønadTermin(termindato)
                .medMottattdato(termindato.plusDays(25));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(new VurderManglendeFodselBekreftelse());
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(1));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }

}
