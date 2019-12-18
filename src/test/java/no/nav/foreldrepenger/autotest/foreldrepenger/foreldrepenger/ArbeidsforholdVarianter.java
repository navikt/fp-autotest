package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.*;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.FaktaOmBeregningTilfelle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.ytelse.ForeldrepengerYtelseBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SoekersRelasjonErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SøknadErketyper.foreldrepengesøknadFødselErketype;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("foreldrepenger")
public class ArbeidsforholdVarianter extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor søker fødsel, men har ikke arbeidsforhold i AAREG, sender inntektsmelding")
    @Description("Mor søker fødsel, men har ikke arbeidsforhold i AAREG. Saksbehandler legger til arbeidsforhold basert på inntektsmelding")
    public void utenArbeidsforholdMenMedInntektsmelding() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("171");

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();

        SøknadBuilder søknad = foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        List<Integer> inntekter = sorterteInntektsbeløp(testscenario);

        InntektsmeldingBuilder inntektsmelding = lagInntektsmeldingBuilder(inntekter.get(0), fnr,
                fpStartdato, "910909088")
                .medArbeidsforholdId("ARB001-001");

        fordel.sendInnInntektsmelding(inntektsmelding, testscenario, saksnummer);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        // LØSER AKSJONSPUNKT 5080 //
        saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErBasertPåInntektsmelding("BEDRIFT AS", LocalDate.now().minusYears(3), LocalDate.now().plusYears(2), BigDecimal.valueOf(100));
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarArbeidsforholdBekreftelse.class);

        // FORESLÅ VEDTAK //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FORESLÅ_VEDTAK);
        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        // FATTE VEDTAK //
        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        Aksjonspunkt ap = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(Collections.singletonList(ap));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiserLikhet(beslutter.getBehandlingsstatus(), "AVSLU");
    }

    @Test
    @DisplayName("Mor søker termin uten aktiviteter i aareg. Legger til fiktivt arbeidsforhold.")
    @Description("Mor søker termin, men har ikke arbeidsforhold i AAREG. Saksbehandler legger til fiktivt arbeidsforhold")
    public void morSøkerTerminUtenAktiviteterIAareg() throws Exception {
        // SØKNAD //
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("168");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String annenPartAktørid = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        LocalDate fødselsdato = LocalDate.now().plusDays(2);
        Fordeling fordeling = FordelingErketyper.fordelingMorHappyCase(fødselsdato);
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.termin(1, fødselsdato), fordeling)
                .medAnnenForelder(annenPartAktørid)
                .build();
        SøknadBuilder søknad = new SøknadBuilder(foreldrepenger, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.gjenopptaBehandling();

        // VURDER ARBEIDSFORHOLD: Legg til fikivt arbeidsforhold //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD);
        saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .leggTilArbeidsforhold("Ambassade", LocalDate.now().minusYears(2), LocalDate.now().plusYears(1), 100);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarArbeidsforholdBekreftelse.class);

        // VURDER OPPTJENING: Godkjenn fiktivt arbeidsforhold i opptjening //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING);
        VurderPerioderOpptjeningBekreftelse vurderPerioderOpptjeningBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderPerioderOpptjeningBekreftelse.class);
        vurderPerioderOpptjeningBekreftelse.godkjennAllOpptjening();
        saksbehandler.bekreftAksjonspunkt(vurderPerioderOpptjeningBekreftelse);

        // FAKTA OM BERGNING: Fastsett inntekt for fiktivt arbeidsforhold og vurder om mottatt ytelse
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        FastsettMaanedsinntektUtenInntektsmeldingAndel fastsattInntekt = new FastsettMaanedsinntektUtenInntektsmeldingAndel(1l, 25_000);
        saksbehandler.hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class)
                .leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.FASTSETT_MAANEDSLONN_ARBEIDSTAKER_UTEN_INNTEKTSMELDING.kode)
                .leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.VURDER_MOTTAR_YTELSE.kode)
                .leggTilMaanedsinntektUtenInntektsmelding(List.of(fastsattInntekt))
                .leggTilMottarYtelse(List.of(new ArbeidstakerandelUtenIMMottarYtelse(1L, false)));
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderFaktaOmBeregningBekreftelse.class);

        // AVVIK I BEREGNING //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(300_000, 1L)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderBeregnetInntektsAvvikBekreftelse.class);

        // FORESLÅ VEDTAK //
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FORESLÅ_VEDTAK);
        saksbehandler.hentAksjonspunktbekreftelse(ForesloVedtakBekreftelse.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        // FATTE VEDTAK //
        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        Aksjonspunkt apAvvikBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        Aksjonspunkt apFaktaOmBeregning = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);
        Aksjonspunkt apVurderArbeidsforhold = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD);
        Aksjonspunkt apVurderOpptjening = beslutter.hentAksjonspunkt(AksjonspunktKoder.VURDER_PERIODER_MED_OPPTJENING);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(List.of(apAvvikBeregning, apFaktaOmBeregning, apVurderArbeidsforhold, apVurderOpptjening));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }
}
