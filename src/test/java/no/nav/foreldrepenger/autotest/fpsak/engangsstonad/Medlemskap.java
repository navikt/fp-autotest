package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.utenlandskAdresse;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadFødsel;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.i18n.CountryCode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderMedlemskapsvilkåretBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrMedlemskapsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArenaSakerDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.GeografiskTilknytningDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.MedlemskapDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PersonstatusDto;

@Tag("fpsak")
@Tag("engangsstonad")
class Medlemskap extends FpsakTestBase {

    private static final Logger logger = LoggerFactory.getLogger(Medlemskap.class);

    @Test
    @DisplayName("Mor søker fødsel er utvandret")
    @Description("Mor søker fødsel og er utvandret. Skal føre til aksjonspunkt angående medlemskap - avslått")
    void morSøkerFødselErUtvandret() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .personstatus(List.of(new PersonstatusDto(PersonstatusDto.Personstatuser.UTVA, LocalDate.now().minusYears(30), null)))
                        .medlemskap(List.of(new MedlemskapDto(LocalDate.now().minusYears(1), LocalDate.now().plusYears(3), CountryCode.DE, MedlemskapDto.DekningsType.IHT_AVTALE)))
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arena(ArenaSakerDto.YtelseTema.AAP, LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2), 10_000)
                                .arbeidsforhold(LocalDate.now().minusYears(4), LocalDate.now().minusMonths(4))
                                .arbeidsforhold(LocalDate.now().minusMonths(4))
                                .arbeidMedOpptjeningOver6G().build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        overstyrer.hentFagsak(saksnummer);

        OverstyrMedlemskapsvilkaaret overstyr = new OverstyrMedlemskapsvilkaaret();
        overstyr.avvis(Avslagsårsak.SØKER_ER_IKKE_MEDLEM);
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        assertThat(overstyrer.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.OVERSTYRING_AV_MEDLEMSKAPSVILKÅRET));
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.type())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
    }

    @Test
    @DisplayName("Mor søker med personstatus uregistrert")
    @Description("Mor søker med personstatus uregistrert, får askjonspunkt så hennlegges")
    void morSøkerFødselUregistrert() {
        var familie = new FamilieGenerator()
                .forelder(mor()
                        .geografiskTilknytning(new GeografiskTilknytningDto(CountryCode.GB, GeografiskTilknytningDto.GeografiskTilknytningType.LAND))
                        .personstatus(List.of(new PersonstatusDto(PersonstatusDto.Personstatuser.UREG, LocalDate.now().minusYears(30), null)))
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunkt(new VurderMedlemskapsvilkåretBekreftelse(Avslagsårsak.SØKER_ER_IKKE_BOSATT));
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        saksbehandler.hentFagsak(saksnummer);

        assertThat(saksbehandler.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak (Avklart som ikke bosatt skal gi avslag med VM 1025)")
                .isEqualTo(Avslagsårsak.SØKER_ER_IKKE_BOSATT);
    }

    @Test
    @DisplayName("Mor søker med utenlandsk adresse og ingen registert inntekt")
    @Description("Mor søker med utelandsk adresse og ingen registret inntekt")
    void morSøkerFødselUtenlandsadresse() {
        var familie = new FamilieGenerator()
                .forelder(mor()
                        .adresser(utenlandskAdresse(CountryCode.DE))
                        .medlemskap(List.of(new MedlemskapDto(LocalDate.now().minusYears(1), LocalDate.now().plusYears(3), CountryCode.DE, MedlemskapDto.DekningsType.FULL)))
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusMonths(1))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(fødselsdato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderMedlemskapsvilkåretBekreftelse.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }
}
