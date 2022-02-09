package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidInntektsmeldingAksjonspunktÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidsforholdKomplettVurderingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdPost;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettMaanedsinntektUtenInntektsmeldingAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.ArbeidInntektsmeldingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding.ManueltArbeidsforholdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.autotest.util.toggle.ArbeidInnteksmeldingToggle;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Orgnummer;

@Tag("fpsak")
@Tag("foreldrepenger")
class ArbeidsforholdVarianter extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker fødsel, men har ikke arbeidsforhold i AAREG, sender inntektsmelding")
    @Description("Mor søker fødsel. Har ikke arbeidsforhold i AAREG, men det blir sendt inn en " +
            "innteksmelding. Saksbehandler legger til arbeidsforhold basert på motatt inntektsmelding")
    void utenArbeidsforholdMenMedInntektsmelding() {
        var familie = new Familie("171", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var orgnummer = Orgnummer.valueOf("910909088");
        var inntektsmelding = lagInntektsmelding(mor.månedsinntekt(), mor.fødselsnummer(), fpStartdato, orgnummer);
        mor.sendIMBasertPåInntekskomponenten(inntektsmelding);

        saksbehandler.hentFagsak(saksnummer);

        // LØSER AKSJONSPUNKT 5080 ELLER 5085 AVHENGIG AV TOGGLE //
        if (ArbeidInnteksmeldingToggle.erTogglePå()) {
            opprettArbeidsforholdFraIM5085(orgnummer);
        } else {
            var ab = saksbehandler
                    .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                    .bekreftArbeidsforholdErBasertPåInntektsmelding(new Orgnummer("910909088"), LocalDate.now().minusYears(3),
                            LocalDate.now().plusYears(2), BigDecimal.valueOf(100));
            saksbehandler.bekreftAksjonspunkt(ab);
        }

        // FORESLÅ VEDTAK //
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // FATTE VEDTAK //
        beslutter.hentFagsak(saksnummer);
        var ap = ArbeidInnteksmeldingToggle.erTogglePå()
                ? beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD_INNTEKTSMELDING)
                : beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD);
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
        var familie = new Familie("168", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var termindato = fødselsdato.plusDays(2);
        var søknad = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.gjenopptaBehandling();

        if (ArbeidInnteksmeldingToggle.erTogglePå()) {
            if (!saksbehandler.harAksjonspunkt("5089")) {
                throw new IllegalStateException();
            }
            var dto = new BehandlingIdPost(saksbehandler.valgtBehandling.uuid, saksbehandler.valgtBehandling.versjon);
            overstyrer.hentFagsak(saksnummer);
            overstyrer.åpneForNyArbeidsforholdVurdering(dto);
            opprettManueltArbeidsforhold5085();
            saksbehandler.velgSisteBehandling();
        } else {
            // VURDER ARBEIDSFORHOLD: Legg til fikivt arbeidsforhold //
            var apBekreftelse = saksbehandler
                    .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                    .leggTilArbeidsforhold("Ambassade", LocalDate.now().minusYears(2), LocalDate.now().plusYears(1), 100);
            saksbehandler.bekreftAksjonspunkt(apBekreftelse);
        }


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
        var apAvvikBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        var apFaktaOmBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        var apVurderArbeidsforhold = ArbeidInnteksmeldingToggle.erTogglePå()
                ? beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD_INNTEKTSMELDING)
                : beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD);
        var apVurderOpptjening = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(
                List.of(apAvvikBeregning, apFaktaOmBeregning, apVurderArbeidsforhold, apVurderOpptjening));
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
                    assertThat(p.getAndeler().get(0).getRefusjon()).isEqualTo(0);
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
        overstyrer.velgSisteBehandling();
        var ab = overstyrer
                .hentAksjonspunktbekreftelse(ArbeidInntektsmeldingBekreftelse.class);
        var dto = new ManueltArbeidsforholdDto(overstyrer.valgtBehandling.uuid, "Dette er en begrunnelse", "342352362", null, "Min bedrift", LocalDate.now().minusYears(3), LocalDate.now().plusYears(2),
                100, ArbeidsforholdKomplettVurderingType.MANUELT_OPPRETTET_AV_SAKSBEHANDLER);
        overstyrer.lagreOpprettetArbeidsforhold(dto);
        overstyrer.bekreftAksjonspunkt(ab);
    }

}
