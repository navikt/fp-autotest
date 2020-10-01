package no.nav.foreldrepenger.autotest.fptilbake.svangerskapspenger;

import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingSvangerskapspengerErketyper.lagSvangerskapspengerInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadSvangerskapspengerErketype.lagSvangerskapspengerSøknad;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.FptilbakeTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.autotest.erketyper.TilretteleggingsErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApFaktaFeilutbetaling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVilkårsvurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.FattVedtakTilbakekreving;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("tilbakekreving")
@Tag("fptilbake")
public class TilbakekrevingSVP extends FptilbakeTestBase {

    private static final String ytelseType = "SVP";

    @Test
    @DisplayName("1. Oppretter en tilbakekreving manuelt etter Fpsak-førstegangsbehandling og revurdering")
    @Description("Vanligste scenario, enkel periode, treffer ikke foreldelse, full tilbakekreving.")
    public void opprettTilbakekrevingManuelt() {
        var testscenario = opprettTestscenario("501");
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();
        var orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
        var tilrettelegginsprosent = 0;
        LocalDate termindato = LocalDate.now().plusMonths(3);
        var tilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(orgNr));
        var søknad = lagSvangerskapspengerSøknad(
                søkerAktørId,
                SøkersRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        var saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                søkerAktørId,
                søkerFnr,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        var månedsinntekt = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0)
                .getBeløp();
        var orgNummer = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();
        var inntektsmedling = lagSvangerskapspengerInntektsmelding(
                søkerFnr,
                månedsinntekt,
                orgNummer);
        fordel.sendInnInntektsmelding(
                inntektsmedling,
                søkerAktørId,
                søkerFnr,
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaFødselOgTilrettelegging avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        avklarFaktaFødselOgTilrettelegging.setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        BekreftSvangerskapspengervilkår bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class);
        bekreftSvangerskapspengervilkår
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);

        // Her mangler hele SVP revurderingen!

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

        verifiser(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getRenteBeløp() == 0,"Forventet rentebeløp er 0, rente i beregningsresultat er noe annet");
        verifiser(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getSkattBeløp() == 412, "Forventet skattbeløp er 412, skatt i beregningsresultat er noe annet");
        verifiser(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløp() == 1616, "Forventet tilbakekrevingsbeløp er 412, tilbakekrevingsbeløp i beregningsresultat er noe annet");
        verifiser(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløpEtterSkatt() == 1204, "Forventet tilbakekrevingsbeløp etter skatt er 412, tilbakekrevingsbeløp etter skatt i beregningsresultat er noe annet");
        verifiser(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløpUtenRenter() == 1616, "Forventet tilbakekrevingsbeløp uten renter er 412, tilbakekrevingsbeløp uten renter i beregningsresultat er noe annet");
    }
}
