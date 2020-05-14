package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadTermin;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrFodselsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
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
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                LocalDate.now().plusWeeks(3));

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse
                .antallBarn(1)
                .utstedtdato(LocalDate.now().minusMonths(1))
                .setTermindato(LocalDate.now().plusMonths(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AVKLAR_TERMINBEKREFTELSE));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "INNVILGET", "Behandlingstatus");

    }

    @Disabled //Disabled til Kafka støtte for brev er i VTP
    @Test
    @DisplayName("Mor søker termin men mangler dokumentasjon")
    @Description("Mor søker termin men mangler dokumentasjon og sender melding om manglende brev")
    public void morSøkerTerminManglerDokumentasjon() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                LocalDate.now().plusWeeks(3));

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.sendBrev("INNHEN", "Søker", "Trenger utstedt dato");
        saksbehandler.settBehandlingPåVent(LocalDate.now().plusDays(2), "AVV_DOK");

        //Todo mock brev
        //verifiser(saksbehandler.harDokument(""), "Behandling har ikke dokument");

        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.BREV_BESTILT);
    }

    @Test
    @DisplayName("Mor søker termin overstyrt vilkår")
    @Description("Mor søker termin overstyrt vilkår fødsel fra oppfylt til avvist")
    public void morSøkerTerminOvertyrt() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                LocalDate.now().plusWeeks(3));

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse
                .antallBarn(1)
                .utstedtdato(LocalDate.now().minusMonths(1))
                .setTermindato(LocalDate.now().plusMonths(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        overstyrer.erLoggetInnMedRolle(Rolle.OVERSTYRER);
        overstyrer.hentFagsak(saksnummer);

        OverstyrFodselsvilkaaret overstyr = new OverstyrFodselsvilkaaret();
        overstyr.avvis(overstyrer.kodeverk.Avslagsårsak.get("FP_VK_1").getKode("1003" /* Søker er far */));
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        verifiserLikhet(overstyrer.valgtBehandling.behandlingsresultat.toString(), "AVSLÅTT", "Behandlingstatus");
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.OVERSTYRING_AV_FØDSELSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "AVSLÅTT", "Behandlingstatus");
    }

    public void behandleTerminMorUtenTerminbekreftelse() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
    }

    @Test
    @DisplayName("Far søker termin")
    @Description("Far søker termin avslått pga søker er far")
    public void farSøkerTermin() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("61");
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.FAR,
                LocalDate.now().plusWeeks(3));

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse.antallBarn(1)
                .utstedtdato(LocalDate.now().minusMonths(1))
                .setTermindato(LocalDate.now().plusMonths(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.toString(), "AVSLÅTT", "Behandlingstatus");
    }

    @Test
    @DisplayName("Setter behandling på vent og gjennoptar og henlegger")
    @Description("Setter behandling på vent og gjennoptar og henlegger")
    public void settBehandlingPåVentOgGjenopptaOgHenlegg() {
        //Opprett scenario og søknad
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                LocalDate.now().plusWeeks(3));

        //Send inn søknad
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.settBehandlingPåVent(LocalDate.now(), "AVV_DOK");
        verifiser(saksbehandler.valgtBehandling.erSattPåVent(), "Behandlingen er ikke satt på vent");

        saksbehandler.gjenopptaBehandling();
        verifiser(!saksbehandler.valgtBehandling.erSattPåVent(), "Behandlingen er satt på vent");

        saksbehandler.henleggBehandling(saksbehandler.kodeverk.BehandlingResultatType.getKode("HENLAGT_SØKNAD_TRUKKET"));
        verifiser(saksbehandler.valgtBehandling.erHenlagt(), "Behandlingen ble uventet ikke henlagt");
        verifiserLikhet(saksbehandler.getBehandlingsstatus(), "AVSLU", "behandlingsstatus");
    }

    @Test
    @DisplayName("Mor søker termin 25 dager etter fødsel")
    @Description("Mor søker termin 25 dager etter fødsel - Får aksjonpunkt om manglende fødsel - godkjent")
    public void morSøkerTermin25DagerTilbakeITid() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                LocalDate.now().minusDays(26));

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(1));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.toString(), "INNVILGET", "Behandlingstatus");

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }

}
