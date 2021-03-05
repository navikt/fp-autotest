package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadAdopsjon;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Venteårsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VarselOmRevurderingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderEktefellesBarnBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaAdopsjonsdokumentasjonBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("fpsak")
@Tag("engangsstonad")
public class Revurdering extends FpsakTestBase {

    @Test
    @DisplayName("Manuelt opprettet revurdering")
    @Description("Manuelt opprettet revurdering etter avsluttet behandling med utsendt varsel")
    public void manueltOpprettetRevurderingSendVarsel() {
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

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.getType(), BehandlingResultatType.INNVILGET, "Behandlingsresultat");

        saksbehandler.ventTilAvsluttetBehandling();

        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_FEIL_ELLER_ENDRET_FAKTA);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        VarselOmRevurderingBekreftelse varselOmRevurderingBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VarselOmRevurderingBekreftelse.class);
        varselOmRevurderingBekreftelse.bekreftSendVarsel(Venteårsak.UTV_FRIST, "Send brev");
        saksbehandler.bekreftAksjonspunkt(varselOmRevurderingBekreftelse);

        saksbehandler.harHistorikkinnslagForBehandling(HistorikkinnslagType.REVURD_OPPR);
        saksbehandler.harHistorikkinnslagForBehandling(HistorikkinnslagType.BREV_BESTILT);
        saksbehandler.harHistorikkinnslagForBehandling(HistorikkinnslagType.BEH_VENT);

        verifiser(saksbehandler.valgtBehandling.erSattPåVent(),
                "Behandlingen er ikke satt på vent etter varsel for revurdering");
    }
}
