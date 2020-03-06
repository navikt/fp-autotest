package no.nav.foreldrepenger.autotest.fptilbake.svangerskapspenger;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.fptilbake.FptilbakeTestBaseSvangerskapspenger;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.SvangerskapspengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.autotest.erketyper.TilretteleggingsErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApFaktaFeilutbetaling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVilkårsvurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.FattVedtakTilbakekreving;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Tilrettelegging;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Tag("tilbakekreving")
@Tag("fptilbake")
public class Tilbakekreving extends FptilbakeTestBaseSvangerskapspenger {

    private static final Logger logger = LoggerFactory.getLogger(Tilbakekreving.class);
    private static final String ytelseType = "SVP";

    @Test
    @DisplayName("Oppretter en tilbakekreving manuelt etter Fpsak-førstegangsbehandling og revurdering")
    @Description("Vanligste scenario, enkel periode, treffer ikke foreldelse, full tilbakekreving.")
    public void opprettTilbakekrevingManuelt() throws Exception{
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("56");

        String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        List<Inntektsperiode> inntektsperioder = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder();
        List<Arbeidsforhold> arbeidsforhold = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold();
        String orgnr1 = arbeidsforhold.get(0).getArbeidsgiverOrgnr();
        String orgnr2 = arbeidsforhold.get(1).getArbeidsgiverOrgnr();

        Tilrettelegging forsteTilrettelegging = TilretteleggingsErketyper.helTilrettelegging(
                LocalDate.now().minusWeeks(1),
                LocalDate.now().plusWeeks(2),
                ArbeidsforholdErketyper.virksomhet(orgnr1));
        Tilrettelegging andreTilrettelegging2 = TilretteleggingsErketyper.helTilrettelegging(
                LocalDate.now(),
                LocalDate.now().plusWeeks(3),
                ArbeidsforholdErketyper.virksomhet(orgnr2));

        SvangerskapspengerBuilder søknad = lagSvangerskapspengerSøknad(morAktoerId, SøkersRolle.MOR, LocalDate.now().plusWeeks(4),
                List.of(forsteTilrettelegging, andreTilrettelegging2));

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        InntektsmeldingBuilder inntektsmelding1 = lagSvangerskapspengerInntektsmelding(fnrMor, inntektsperioder.get(0).getBeløp(), orgnr1);
        InntektsmeldingBuilder inntektsmelding2 = lagSvangerskapspengerInntektsmelding(fnrMor, inntektsperioder.get(1).getBeløp(), orgnr2);
        fordel.sendInnInntektsmeldinger(List.of(inntektsmelding1, inntektsmelding2), testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.ikkeVentPåStatus = true;
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);
        saksbehandler.ventTilAksjonspunkt("5092");
        saksbehandler.hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);
        saksbehandler.ventTilAvsluttetBehandling();

        // Her mangler hele SVP revurderingen!

        tbksaksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        tbksaksbehandler.opprettTilbakekreving(saksnummer, saksbehandler.valgtBehandling.uuid, ytelseType);
        tbksaksbehandler.hentSisteBehandling(saksnummer);
        tbksaksbehandler.ventTilBehandlingErPåVent();
        verifiser(tbksaksbehandler.valgtBehandling.venteArsakKode.equals("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG"), "Behandling har feil vent årsak.");
        Kravgrunnlag kravgrunnlag = new Kravgrunnlag(saksnummer, testscenario.getPersonopplysninger().getSøkerIdent(), saksbehandler.valgtBehandling.id, ytelseType, "NY");
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

        tbkbeslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        tbkbeslutter.hentSisteBehandling(saksnummer);
        tbkbeslutter.ventTilBehandlingHarAktivtAksjonspunkt(5005);

        var fattVedtak = (FattVedtakTilbakekreving) tbkbeslutter.hentAksjonspunktbehandling(5005);
        fattVedtak.godkjennAksjonspunkt(5002);
        fattVedtak.godkjennAksjonspunkt(7003);
        tbkbeslutter.behandleAksjonspunkt(fattVedtak);
        tbkbeslutter.ventTilAvsluttetBehandling();
    }
}
