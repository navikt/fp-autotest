package no.nav.foreldrepenger.autotest.fptilbake.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadEndringErketyper.lagEndringssøknad;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.uttaksperiode;

import java.math.BigInteger;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.FptilbakeTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EndringssøknadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerRevuderingsbehandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedNegativSimulering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApFaktaFeilutbetaling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVerge;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVilkårsvurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.FattVedtakTilbakekreving;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;

@Tag("tilbakekreving")
@Tag("fptilbake")
public class TilbakekrevingFP extends FptilbakeTestBase {

    private static final Logger logger = LoggerFactory.getLogger(TilbakekrevingFP.class);
    private static final String ytelseType = "FP";

    @Test
    @DisplayName("1. Oppretter en tilbakekreving manuelt etter Fpsak-førstegangsbehandling og revurdering")
    @Description("Enkel periode, treffer ikke foreldelse, full tilbakekreving.")
    public void opprettTilbakekrevingManuelt() {

        TestscenarioDto testscenario = opprettTestscenario("50");
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String søkerIdent = testscenario.getPersonopplysninger().getSøkerIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        lagOgSendInntektsmelding(testscenario, fpStartdato, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        AllureHelper.debugFritekst("Ferdig med førstegangsbehandling");

        Fordeling fordeling = generiskFordeling(uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(10).minusDays(1)));
        EndringssøknadBuilder søknadE = lagEndringssøknad(søkerAktørIdent, SøkersRolle.MOR, fordeling, saksnummer);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        fordel.sendInnSøknad(søknadE.build(), søkerAktørIdent, søkerIdent, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        tbksaksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        tbksaksbehandler.opprettTilbakekreving(saksnummer, saksbehandler.valgtBehandling.uuid, ytelseType);
        tbksaksbehandler.hentSisteBehandling(saksnummer);
        tbksaksbehandler.ventTilBehandlingErPåVent();
        verifiser(tbksaksbehandler.valgtBehandling.venteArsakKode.equals("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG"),
                "Behandling har feil vent årsak.");
        Kravgrunnlag kravgrunnlag = new Kravgrunnlag(saksnummer, testscenario.getPersonopplysninger().getSøkerIdent(),
                saksbehandler.valgtBehandling.id, ytelseType, "NY");
        kravgrunnlag.leggTilGeneriskPeriode();
        tbksaksbehandler.sendNyttKravgrunnlag(kravgrunnlag);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(7003);

        var vurderFakta = (ApFaktaFeilutbetaling) tbksaksbehandler.hentAksjonspunktbehandling(7003);
        vurderFakta.addGeneriskVurdering(ytelseType);
        tbksaksbehandler.behandleAksjonspunkt(vurderFakta);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5002);

        var vurderVilkår = (ApVilkårsvurdering) tbksaksbehandler.hentAksjonspunktbehandling(5002);
        vurderVilkår.addGeneriskVurdering();
        tbksaksbehandler.behandleAksjonspunkt(vurderVilkår);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5004);

        tbksaksbehandler.behandleAksjonspunkt(tbksaksbehandler.hentAksjonspunktbehandling(5004));
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5005);

        tbkbeslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        tbkbeslutter.hentSisteBehandling(saksnummer);
        tbkbeslutter.ventTilBehandlingHarAktivtAksjonspunkt(5005);

        var fattVedtak = (FattVedtakTilbakekreving) tbkbeslutter.hentAksjonspunktbehandling(5005);
        fattVedtak.godkjennAksjonspunkt(5002);
        fattVedtak.godkjennAksjonspunkt(7003);
        fattVedtak.godkjennAksjonspunkt(5004);
        tbkbeslutter.behandleAksjonspunkt(fattVedtak);
        tbkbeslutter.ventTilAvsluttetBehandling();
    }

    @Test
    @DisplayName("2. Oppretter en tilbakekreving automatisk etter negativ simulering på fpsak revurdering")
    @Description("Vanligste scenario, enkel periode, treffer ikke foreldelse, full tilbakekreving men med registrert advokat som verge/fullmektig")
    public void opprettTilbakekrevingAutomatisk() {
        TestscenarioDto testscenario = opprettTestscenario("142");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR).medMottattDato(fpStartdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        lagOgSendInntektsmelding(testscenario, fpStartdato, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        AllureHelper.debugFritekst("Ferdig med førstegangsbehandling");

        lagOgSendInntektsmelding(testscenario, fpStartdato, saksnummer, true);

        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var vurderBeregnetInntektsAvvikBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse
                .leggTilInntekt(testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp() * 6, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.harAksjonspunkt("5084");
        var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
        vurderTilbakekrevingVedNegativSimulering.setTilbakekrevingMedVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        var kontrollerRevuderingsbehandling = saksbehandler.hentAksjonspunktbekreftelse(KontrollerRevuderingsbehandling.class);
        saksbehandler.bekreftAksjonspunkt(kontrollerRevuderingsbehandling);
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true);

        tbksaksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        tbksaksbehandler.hentSisteBehandling(saksnummer);
        tbksaksbehandler.ventTilBehandlingErPåVent();
        verifiser(tbksaksbehandler.valgtBehandling.venteArsakKode.equals("VENT_PÅ_BRUKERTILBAKEMELDING"), "Behandling har feil vent årsak.");

        Kravgrunnlag kravgrunnlag = new Kravgrunnlag(saksnummer, testscenario.getPersonopplysninger().getSøkerIdent(),
                saksbehandler.valgtBehandling.id, ytelseType, "NY");
        kravgrunnlag.leggTilGeneriskPeriode();
        tbksaksbehandler.sendNyttKravgrunnlag(kravgrunnlag);

        tbksaksbehandler.registrerBrukerrespons(true);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(7003);

        tbksaksbehandler.leggTilVerge();
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5030);
        var vergeFakta = (ApVerge) tbksaksbehandler.hentAksjonspunktbehandling(5030);
        vergeFakta.setVerge("973861778");
        tbksaksbehandler.behandleAksjonspunkt(vergeFakta);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(7003);

        var vurderFakta = (ApFaktaFeilutbetaling) tbksaksbehandler.hentAksjonspunktbehandling(7003);
        vurderFakta.addGeneriskVurdering(ytelseType);
        tbksaksbehandler.behandleAksjonspunkt(vurderFakta);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5002);

        var vurderVilkår = (ApVilkårsvurdering) tbksaksbehandler.hentAksjonspunktbehandling(5002);
        vurderVilkår.addGeneriskVurdering();
        tbksaksbehandler.behandleAksjonspunkt(vurderVilkår);
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5004);

        tbksaksbehandler.behandleAksjonspunkt(tbksaksbehandler.hentAksjonspunktbehandling(5004));
        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(5005);

        tbkbeslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        tbkbeslutter.hentSisteBehandling(saksnummer);
        tbkbeslutter.ventTilBehandlingHarAktivtAksjonspunkt(5005);

        var fattVedtak = (FattVedtakTilbakekreving) tbkbeslutter.hentAksjonspunktbehandling(5005);
        fattVedtak.godkjennAksjonspunkt(5002);
        fattVedtak.godkjennAksjonspunkt(7003);
        fattVedtak.godkjennAksjonspunkt(5004);
        tbkbeslutter.behandleAksjonspunkt(fattVedtak);
        tbkbeslutter.ventTilAvsluttetBehandling();

        verifiser(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getRenteBeløp() == 0,"Forventet rentebeløp er 0, rente i beregningsresultat er noe annet");
        verifiser(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getSkattBeløp() == 412, "Forventet skattbeløp er 412, skatt i beregningsresultat er noe annet");
        verifiser(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløp() == 1616, "Forventet tilbakekrevingsbeløp er 412, tilbakekrevingsbeløp i beregningsresultat er noe annet");
        verifiser(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløpEtterSkatt() == 1204, "Forventet tilbakekrevingsbeløp etter skatt er 412, tilbakekrevingsbeløp etter skatt i beregningsresultat er noe annet");
        verifiser(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløpUtenRenter() == 1616, "Forventet tilbakekrevingsbeløp uten renter er 412, tilbakekrevingsbeløp uten renter i beregningsresultat er noe annet");
    }

    @Test
    @DisplayName("3. Oppretter og behandler en tilbakekreving helt-automatisk")
    @Description("Heltautomatisert scenario. Beløp under et halvt rettsgebyr og blir plukket av auto-batch")
    public void opprettOgBehandleTilbakekrevingAutomatisk() {
        TestscenarioDto testscenario = opprettTestscenario("142");

        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR).medMottattDato(fpStartdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        lagOgSendInntektsmelding(testscenario, fpStartdato, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        AllureHelper.debugFritekst("Ferdig med førstegangsbehandling");

        lagOgSendInntektsmelding(testscenario, fpStartdato, saksnummer, true);

        saksbehandler.ventPåOgVelgRevurderingBehandling();
        var vurderBeregnetInntektsAvvikBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse
                .leggTilInntekt(testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp() * 6, 1)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.harAksjonspunkt("5084");
        var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class);
        vurderTilbakekrevingVedNegativSimulering.setTilbakekrevingUtenVarsel();
        saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
        saksbehandler.harAksjonspunkt("5055");
        var kontrollerRevuderingsbehandling = saksbehandler.hentAksjonspunktbekreftelse(KontrollerRevuderingsbehandling.class);
        saksbehandler.bekreftAksjonspunkt(kontrollerRevuderingsbehandling);
        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, true);

        tbksaksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        tbksaksbehandler.hentSisteBehandling(saksnummer);
        tbksaksbehandler.ventTilBehandlingErPåVent();
        verifiser(tbksaksbehandler.valgtBehandling.venteArsakKode.equals("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG"), "Behandling har feil vent årsak.");

        Kravgrunnlag kravgrunnlag = new Kravgrunnlag(saksnummer, testscenario.getPersonopplysninger().getSøkerIdent(),
                saksbehandler.valgtBehandling.id, ytelseType, "NY");
        kravgrunnlag.leggTilPeriodeMedSmåBeløp();
        tbksaksbehandler.sendNyttKravgrunnlag(kravgrunnlag);

        tbksaksbehandler.ventTilBehandlingHarAktivtAksjonspunkt(7003);
        tbksaksbehandler.startAutomatiskBehandlingBatch();
        tbksaksbehandler.ventTilAvsluttetBehandling();
        verifiser(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløp() == 0, "Forventet tilbakekrevingsbeløp er 0, tilbakekrevingsbeløp i beregningsresultatet er noe annet");
    }

    private void lagOgSendInntektsmelding(TestscenarioDto testscenario, LocalDate fpStartdato, Long saksnummer){
        lagOgSendInntektsmelding(testscenario, fpStartdato, saksnummer, false);
    }
    private void lagOgSendInntektsmelding(TestscenarioDto testscenario, LocalDate fpStartdato, Long saksnummer, Boolean redusert) {
        Integer belop = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        if (redusert){
            belop = BigInteger.valueOf(belop).divide(BigInteger.valueOf(2)).intValue();
        }
        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(
                belop,
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdato,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                        .getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmelding,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);
    }
}
