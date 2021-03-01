package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettMaanedsinntektUtenInntektsmeldingAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderPerioderOpptjeningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("foreldrepenger")
public class ArbeidsforholdVarianter extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor søker fødsel, men har ikke arbeidsforhold i AAREG, sender inntektsmelding")
    @Description("Mor søker fødsel, men har ikke arbeidsforhold i AAREG. Saksbehandler legger til arbeidsforhold " +
            "basert på inntektsmelding")
    public void utenArbeidsforholdMenMedInntektsmelding() {
        TestscenarioDto testscenario = opprettTestscenario("171");

        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        String fnr = testscenario.personopplysninger().søkerIdent();
        List<Integer> inntekter = sorterteInntektsbeløp(testscenario);

        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(inntekter.get(0), fnr, fpStartdato, "910909088")
                .medArbeidsforholdId("ARB001-001");

        fordel.sendInnInntektsmelding(inntektsmelding, testscenario, saksnummer);
        saksbehandler.hentFagsak(saksnummer);

        // LØSER AKSJONSPUNKT 5080 //
        var ab = saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErBasertPåInntektsmelding("910909088", LocalDate.now().minusYears(3),
                        LocalDate.now().plusYears(2), BigDecimal.valueOf(100));
        saksbehandler.bekreftAksjonspunkt(ab);

        // FORESLÅ VEDTAK //
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // FATTE VEDTAK //
        beslutter.hentFagsak(saksnummer);
        Aksjonspunkt ap = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(Collections.singletonList(ap));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);
        verifiserLikhet(beslutter.getBehandlingsstatus(), "AVSLU");
    }

    @Test
    @DisplayName("Mor søker termin uten aktiviteter i aareg. Legger til fiktivt arbeidsforhold.")
    @Description("Mor søker termin, men har ikke arbeidsforhold i AAREG. Saksbehandler legger til fiktivt arbeidsforhold")
    public void morSøkerTerminUtenAktiviteterIAareg() {
        // SØKNAD //
        TestscenarioDto testscenario = opprettTestscenario("168");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        String annenPartAktørid = testscenario.personopplysninger().annenpartAktørIdent();
        LocalDate fødselsdato = LocalDate.now().plusDays(2);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medAnnenForelder(annenPartAktørid);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.gjenopptaBehandling();

        // VURDER ARBEIDSFORHOLD: Legg til fikivt arbeidsforhold //
        var apBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .leggTilArbeidsforhold("Ambassade", LocalDate.now().minusYears(2), LocalDate.now().plusYears(1), 100);
        saksbehandler.bekreftAksjonspunkt(apBekreftelse);

        // VURDER OPPTJENING: Godkjenn fiktivt arbeidsforhold i opptjening //
        VurderPerioderOpptjeningBekreftelse vurderPerioderOpptjeningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class);
        vurderPerioderOpptjeningBekreftelse.godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // FAKTA OM BERGNING: Fastsett inntekt for fiktivt arbeidsforhold og vurder om
        // mottatt ytelse
        FastsettMaanedsinntektUtenInntektsmeldingAndel fastsattInntekt = new FastsettMaanedsinntektUtenInntektsmeldingAndel(
                1L, 25_000);
        var ab = saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
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
        Aksjonspunkt apAvvikBeregning = beslutter
                .hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        Aksjonspunkt apFaktaOmBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        Aksjonspunkt apVurderArbeidsforhold = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD);
        Aksjonspunkt apVurderOpptjening = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(
                List.of(apAvvikBeregning, apFaktaOmBeregning, apVurderArbeidsforhold, apVurderOpptjening));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }
}
