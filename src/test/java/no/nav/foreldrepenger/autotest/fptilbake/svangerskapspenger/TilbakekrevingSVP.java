package no.nav.foreldrepenger.autotest.fptilbake.svangerskapspenger;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.SøknadSvangerskapspengerErketype.lagSvangerskapspengerSøknad;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingSvangerskapspengerErketyper.lagSvangerskapspengerInntektsmelding;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FptilbakeTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.TilretteleggingsErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApFaktaFeilutbetaling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVilkårsvurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.FattVedtakTilbakekreving;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("tilbakekreving")
@Tag("fptilbake")
class TilbakekrevingSVP extends FptilbakeTestBase {

    private static final String ytelseType = "SVP";

    @Test
    @DisplayName("1. Oppretter en tilbakekreving manuelt etter Fpsak-førstegangsbehandling og revurdering")
    @Description("Vanligste scenario, enkel periode, treffer ikke foreldelse, full tilbakekreving.")
    void opprettTilbakekrevingManuelt() {
        var testscenario = opprettTestscenario("501");
        var søkerAktørId = testscenario.personopplysninger().søkerAktørIdent();
        var søkerFnr = testscenario.personopplysninger().søkerIdent();
        var orgNr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var tilrettelegginsprosent = 0;
        var termindato = LocalDate.now().plusMonths(3);
        var tilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now(),
                ArbeidsforholdErketyper.virksomhet(orgNr));
        var søknad = lagSvangerskapspengerSøknad(
                søkerAktørId,
                SøkersRolle.MOR,
                termindato,
                List.of(tilrettelegging));
        var saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                søkerAktørId,
                søkerFnr,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        var månedsinntekt = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0)
                .beløp();
        var orgNummer = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        var inntektsmedling = lagSvangerskapspengerInntektsmelding(
                søkerFnr,
                månedsinntekt,
                orgNummer);
        fordel.sendInnInntektsmelding(
                inntektsmedling,
                søkerAktørId,
                søkerFnr,
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaFødselOgTilrettelegging = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaFødselOgTilrettelegging);

        var bekreftSvangerskapspengervilkår = saksbehandler
                .hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class)
                .godkjenn()
                .setBegrunnelse("Godkjenner vilkår");
        saksbehandler.bekreftAksjonspunkt(bekreftSvangerskapspengervilkår);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);

        // Her mangler hele SVP revurderingen!

        tbksaksbehandler.opprettTilbakekreving(saksnummer, saksbehandler.valgtBehandling.uuid, ytelseType);
        tbksaksbehandler.hentSisteBehandling(saksnummer);
        tbksaksbehandler.ventTilBehandlingErPåVent();
        assertThat(tbksaksbehandler.valgtBehandling.venteArsakKode)
                .as("Venteårsak")
                .isEqualTo("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG");
        var kravgrunnlag = new Kravgrunnlag(saksnummer, testscenario.personopplysninger().søkerIdent(),
                saksbehandler.valgtBehandling.id, ytelseType, "NY");
        kravgrunnlag.leggTilGeneriskPeriode();
        tbksaksbehandler.sendNyttKravgrunnlag(kravgrunnlag, saksnummer, saksbehandler.valgtBehandling.id);
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

        tbkbeslutter.hentSisteBehandling(saksnummer);
        tbkbeslutter.ventTilBehandlingHarAktivtAksjonspunkt(5005);

        var fattVedtak = (FattVedtakTilbakekreving) tbkbeslutter.hentAksjonspunktbehandling(5005);
        fattVedtak.godkjennAksjonspunkt(5002);
        fattVedtak.godkjennAksjonspunkt(7003);
        fattVedtak.godkjennAksjonspunkt(5004);
        tbkbeslutter.behandleAksjonspunkt(fattVedtak);
        tbkbeslutter.ventTilAvsluttetBehandling();

        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getRenteBeløp())
                .as("Rente i beregningsresultat")
                .isZero();
        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getSkattBeløp())
                .as("Skattebeløp i beregningsresultat")
                .isEqualTo(412);
        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløp())
                .as("Tilbakekrevingsbeløp i beregningsresultat")
                .isEqualTo(1616);
        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløpEtterSkatt())
                .as("Tilbakekrevingsbeløp etter skatt i beregningsresultat")
                .isEqualTo(1204);
        assertThat(tbksaksbehandler.hentResultat(tbksaksbehandler.valgtBehandling.uuid).getTilbakekrevingBeløpUtenRenter())
                .as("Tilbakekrevingbeløp uten renter i beregningsresultat")
                .isEqualTo(1616);
    }
}
