package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder.VURDER_ARBEIDSFORHOLD_INNTEKTSMELDING;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.inntektsmelding.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;

import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;

import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.generator.soknad.api.erketyper.AnnenforelderErketyper;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidInntektsmeldingAksjonspunktÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidsforholdKomplettVurderingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdVersjonDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettMaanedsinntektUtenInntektsmeldingAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.ArbeidInntektsmeldingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding.ManueltArbeidsforholdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Orgnummer;

@Tag("fpsak")
@Tag("foreldrepenger")
class ArbeidsforholdVarianter extends FpsakTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdVarianter.class);

    @Test
    @DisplayName("Mor søker fødsel, men har ikke arbeidsforhold i AAREG, sender inntektsmelding")
    @Description("Mor søker fødsel. Har ikke arbeidsforhold i AAREG, men det blir sendt inn en " +
            "innteksmelding. Saksbehandler legger til arbeidsforhold basert på motatt inntektsmelding")
    void utenArbeidsforholdMenMedInntektsmelding() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .inntektsperiode(TestOrganisasjoner.NAV, LocalDate.now().minusYears(2), LocalDate.now(), 360_000)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now())
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderErketyper.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var orgnummer = new Orgnummer("889640782");
        var inntektsmelding = lagInntektsmelding(mor.månedsinntekt(), mor.fødselsnummer(), fpStartdato, orgnummer);
        mor.sendIMBasertPåInntekskomponenten(inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);

        // LØSER AKSJONSPUNKT 5085 //
        opprettArbeidsforholdFraIM5085(orgnummer);

        // FORESLÅ VEDTAK //
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // FATTE VEDTAK //
        beslutter.hentFagsak(saksnummer);
        var ap = beslutter.hentAksjonspunkt(VURDER_ARBEIDSFORHOLD_INNTEKTSMELDING);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(Collections.singletonList(ap));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(beslutter.getBehandlingsstatus())
                .as("Behandlingsstatus")
                .isEqualTo(BehandlingStatus.AVSLUTTET);
    }

    @Test
    @DisplayName("Mor søker fødsel, men har ikke arbeidsforhold i AAREG. Legger til fiktivt arbeidsforhold.")
    @Description("Mor søker termin, men har ikke arbeidsforhold i AAREG. Saksbehandler legger til fiktivt arbeidsforhold")
    void morSøkerTerminUtenAktiviteterIAareg() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .inntektsperiode(TestOrganisasjoner.NAV_BERGEN, LocalDate.now().minusMonths(24), LocalDate.now().minusMonths(23), 150_000)
                                .arbeidsforhold(TestOrganisasjoner.NAV, "ARB001-001", 0, LocalDate.now().minusYears(4), LocalDate.now().minusYears(1), null)
                                .build())
                        .build())
                .forelder(far().build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now())
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var termindato = fødselsdato.plusDays(2);
        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderErketyper.norskMedRettighetNorge(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        if (!saksbehandler.harAksjonspunkt(AksjonspunktKoder.VURDER_OPPTJENINGSVILKÅRET)) {
            throw new IllegalStateException("Forventer å ha havnet i opptjeningsvilkåret her");
        }

        overstyrer.hentFagsak(saksnummer);
        opprettManueltArbeidsforhold5085();

        saksbehandler.velgSisteBehandling();
        // VURDER OPPTJENING: Godkjenn fiktivt arbeidsforhold i opptjening //
        var vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class)
                .godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // FAKTA OM BERGNING: Fastsett inntekt for fiktivt arbeidsforhold og vurder om
        // mottatt ytelse
        var fastsattInntekt = new FastsettMaanedsinntektUtenInntektsmeldingAndel(1L, 25_000);
        var ab = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilMaanedsinntektUtenInntektsmelding(List.of(fastsattInntekt))
                .leggTilMottarYtelse(List.of(new ArbeidstakerandelUtenIMMottarYtelse(1L, false)));
        saksbehandler.bekreftAksjonspunkt(ab);

        // AVVIK I BEREGNING //
        var aksjonspunktBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(300_000, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelse);

        // FORESLÅ VEDTAK //
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // FATTE VEDTAK //
        beslutter.hentFagsak(saksnummer);
        var apArbeid = beslutter.hentAksjonspunkt(VURDER_ARBEIDSFORHOLD_INNTEKTSMELDING);
        var apAvvikBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        var apFaktaOmBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        var apVurderOpptjening = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(
                List.of(apAvvikBeregning, apFaktaOmBeregning, apArbeid, apVurderOpptjening));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        beslutter.valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()
                .stream()
                .filter(p -> p.getDagsats() > 0)
                .forEach(p -> {
                    assertThat(p.getDagsats()).isEqualTo(1154);
                    assertThat(p.getAndeler()).hasSize(1);
                    assertThat(p.getAndeler().get(0).getRefusjon()).isZero();
                    assertThat(p.getAndeler().get(0).getTilSoker()).isEqualTo(1154);
        });

    }

    private void opprettArbeidsforholdFraIM5085(Orgnummer orgnummer) {
        var ab = saksbehandler
                .hentAksjonspunktbekreftelse(ArbeidInntektsmeldingBekreftelse.class);
        var arbeidOgInntektsmeldingDto = saksbehandler.valgtBehandling.getArbeidOgInntektsmeldingDto();
        var imMedAksjonspunkt = arbeidOgInntektsmeldingDto.inntektsmeldinger().stream()
                .filter(im -> im.arbeidsgiverIdent().equals(orgnummer.value()) && im.årsak().equals(ArbeidInntektsmeldingAksjonspunktÅrsak.INNTEKTSMELDING_UTEN_ARBEIDSFORHOLD))
                .findFirst()
                .orElseThrow();
        var dto = new ManueltArbeidsforholdDto(saksbehandler.valgtBehandling.uuid, "Dette er en begrunnelse", orgnummer.value(),
                imMedAksjonspunkt.internArbeidsforholdId(), null, LocalDate.now().minusYears(3), LocalDate.now().plusYears(2),
                100, ArbeidsforholdKomplettVurderingType.OPPRETT_BASERT_PÅ_INNTEKTSMELDING);
        saksbehandler.lagreOpprettetArbeidsforhold(dto);
        saksbehandler.bekreftAksjonspunkt(ab);
    }

    private void opprettManueltArbeidsforhold5085() {
        overstyrer.åpneForNyArbeidsforholdVurdering(new BehandlingIdVersjonDto(overstyrer.valgtBehandling));
        var ab = overstyrer.hentAksjonspunktbekreftelse(ArbeidInntektsmeldingBekreftelse.class);
        var dto = new ManueltArbeidsforholdDto(
                overstyrer.valgtBehandling.uuid,
                "Dette er en begrunnelse",
                "342352362",
                null,
                "Min bedrift",
                LocalDate.now().minusYears(3),
                LocalDate.now().plusYears(2),
                100, ArbeidsforholdKomplettVurderingType.MANUELT_OPPRETTET_AV_SAKSBEHANDLER);
        overstyrer.lagreOpprettetArbeidsforhold(dto);
        overstyrer.bekreftAksjonspunkt(ab);
    }

}
