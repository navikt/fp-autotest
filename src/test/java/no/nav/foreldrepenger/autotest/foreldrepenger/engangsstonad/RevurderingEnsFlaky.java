package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadAdopsjon;

import java.time.LocalDate;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VarselOmRevurderingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderEktefellesBarnBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAdopsjonsdokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("flaky")
@Tag("engangsstonad")
public class RevurderingEnsFlaky extends ForeldrepengerTestBase {
    @Test
    public void manueltOpprettetRevurderingIkkeSendVarsel() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        EngangstønadBuilder søknad = lagEngangstønadAdopsjon(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR, false);

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON);

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaAdopsjonsdokumentasjonBekreftelse bekreftelse1 = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        bekreftelse1.setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        VurderEktefellesBarnBekreftelse bekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class);
        bekreftelse2.bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunktbekreftelserer(bekreftelse1, bekreftelse2);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(
                beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_OM_ADOPSJON_GJELDER_EKTEFELLES_BARN));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "INNVILGET", "Behandlingsresultat");

        saksbehandler.ventTilAvsluttetBehandling();

        saksbehandler.opprettBehandlingRevurdering("RE-FEFAKTA");
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var varselOmRevurderingBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VarselOmRevurderingBekreftelse.class)
                .bekreftIkkeSendVarsel();
        saksbehandler.bekreftAksjonspunkt(varselOmRevurderingBekreftelse);
        bekreftelse1 = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaAdopsjonsdokumentasjonBekreftelse.class);
        bekreftelse1.setBarnetsAnkomstTilNorgeDato(LocalDate.now());
        bekreftelse2 = saksbehandler.hentAksjonspunktbekreftelse(VurderEktefellesBarnBekreftelse.class);
        bekreftelse2.bekreftBarnErIkkeEktefellesBarn();
        saksbehandler.bekreftAksjonspunktbekreftelserer(bekreftelse1, bekreftelse2);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgRevurderingBehandling();
        FatterVedtakBekreftelse bekreftelse3 = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse3.godkjennAksjonspunkt(
                beslutter.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_OM_ADOPSJON_GJELDER_EKTEFELLES_BARN));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        beslutter.ventTilAvsluttetBehandling();
        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "INNVILGET", "Behandlingsresultat");
    }
}
