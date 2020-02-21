package no.nav.foreldrepenger.autotest.foreldrepenger;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAleneomsorgBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.*;

public class VerdikjedeForeldrepenger extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor automatisk førstegangssøknad fødsel")
    @Description("Mor førstegangssøknad på fødsel")
    public void testcase_mor_fødsel() throws Exception {
        var testscenario = opprettTestscenario("500");
        var fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var fpStartdatoMor = fødselsdato.minusWeeks(3);

        var fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                utsettelsesperiode(UTSETTELSETYPE_ARBEID, fødselsdato.plusWeeks(15), fødselsdato.plusWeeks(20).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER, fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(36).minusDays(1)));

        var søknad = lagSøknadForeldrepengerFødsel(
                fødselsdato, søkerAktørId, SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medRettigheter(RettigheterErketyper.harAleneOmsorgOgEnerett());

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        var inntektBeløp = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        var orgNummer = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        var søkerFnr = testscenario.getPersonopplysninger().getSøkerIdent();

        var inntektsmeldinger = lagInntektsmelding(
                inntektBeløp+15_000,
                søkerFnr,
                fpStartdatoMor,
                orgNummer)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(inntektBeløp/2));
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummerMor);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS);
        var vurderBeregnetInntektsAvvikBekreftelse =
                saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse
                .leggTilInntekt(inntektBeløp*12, 1L)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.MANUELL_KONTROLL_AV_OM_BRUKER_HAR_ALENEOMSORG);
        var bekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaAleneomsorgBekreftelse.class);
        bekreftelse.bekreftBrukerHarAleneomsorg();
        saksbehandler.bekreftAksjonspunktbekreftelserer(bekreftelse);


    }
}
