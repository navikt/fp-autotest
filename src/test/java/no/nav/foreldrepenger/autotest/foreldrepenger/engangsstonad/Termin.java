package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadTermin;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Venteårsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrFodselsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("engangsstonad")
public class Termin extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker termin - godkjent")
    @Description("Mor søker termin - godkjent happy case")
    public void morSøkerTerminGodkjent() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                LocalDate.now().plusWeeks(3));

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse
                .setAntallBarn(1)
                .setUtstedtdato(LocalDate.now().minusMonths(1))
                .setTermindato(LocalDate.now().plusMonths(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_TERMINBEKREFTELSE));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.getType(), BehandlingResultatType.INNVILGET, "Behandlingstatus");

    }

    @Test
    @DisplayName("Mor søker termin overstyrt vilkår")
    @Description("Mor søker termin overstyrt vilkår fødsel fra oppfylt til avvist")
    public void morSøkerTerminOvertyrt() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                LocalDate.now().plusWeeks(3));

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse
                .setAntallBarn(1)
                .setUtstedtdato(LocalDate.now().minusMonths(1))
                .setTermindato(LocalDate.now().plusMonths(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        overstyrer.hentFagsak(saksnummer);

        OverstyrFodselsvilkaaret overstyr = new OverstyrFodselsvilkaaret();
        overstyr.avvis(Avslagsårsak.SØKER_ER_FAR);
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        verifiserLikhet(overstyrer.valgtBehandling.behandlingsresultat.getType(), BehandlingResultatType.AVSLÅTT, "Behandlingstatus");
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.OVERSTYRING_AV_FØDSELSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.getType(), BehandlingResultatType.AVSLÅTT, "Behandlingstatus");
    }

    public void behandleTerminMorUtenTerminbekreftelse() {
        opprettTestscenario("55");
    }

    @Test
    @DisplayName("Far søker termin")
    @Description("Far søker termin avslått pga søker er far")
    public void farSøkerTermin() {
        TestscenarioDto testscenario = opprettTestscenario("61");
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.FAR,
                LocalDate.now().plusWeeks(3));

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse
                .setAntallBarn(1)
                .setUtstedtdato(LocalDate.now().minusMonths(1))
                .setTermindato(LocalDate.now().plusMonths(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.getType(), BehandlingResultatType.AVSLÅTT, "Behandlingstatus");
    }

    @Test
    @DisplayName("Setter behandling på vent og gjennoptar og henlegger")
    @Description("Setter behandling på vent og gjennoptar og henlegger")
    public void settBehandlingPåVentOgGjenopptaOgHenlegg() {
        // Opprett scenario og søknad
        TestscenarioDto testscenario = opprettTestscenario("55");
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                LocalDate.now().plusWeeks(3));

        // Send inn søknad
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.settBehandlingPåVent(LocalDate.now(), Venteårsak.AVV_DOK);
        verifiser(saksbehandler.valgtBehandling.erSattPåVent(), "Behandlingen er ikke satt på vent");

        saksbehandler.gjenopptaBehandling();
        verifiser(!saksbehandler.valgtBehandling.erSattPåVent(), "Behandlingen er satt på vent");

        saksbehandler.henleggBehandling(BehandlingResultatType.HENLAGT_SØKNAD_TRUKKET);
        verifiser(saksbehandler.valgtBehandling.erHenlagt(), "Behandlingen ble uventet ikke henlagt");
        verifiserLikhet(saksbehandler.getBehandlingsstatus(), BehandlingStatus.AVSLUTTET, "behandlingsstatus");
    }

    @Test
    @DisplayName("Mor søker termin 25 dager etter fødsel")
    @Description("Mor søker termin 25 dager etter fødsel - Får aksjonpunkt om manglende fødsel - godkjent")
    public void morSøkerTermin25DagerTilbakeITid() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                LocalDate.now().minusDays(26));

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(1));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.getType(), BehandlingResultatType.INNVILGET, "Behandlingstatus");

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }

}
