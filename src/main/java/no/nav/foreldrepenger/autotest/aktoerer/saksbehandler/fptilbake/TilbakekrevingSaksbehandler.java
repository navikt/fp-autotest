package no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fptilbake;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.BehandlingerJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingIdBasicDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprettRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BrukerresponsDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.RevurderingArsak;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.AksjonspunktBehandling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApFaktaFeilutbetaling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVerge;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVilkårsvurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.BehandledeAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.FattVedtakTilbakekreving;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ForeslåVedtak;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.OkonomiJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.BeregningResultatPerioder;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask.ProsesstaskJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.tilbakekreving.VTPTilbakekrevingJerseyKlient;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskOpprettInputDto;

public class TilbakekrevingSaksbehandler extends Aktoer {

    public List<Behandling> behandlingList;
    public Behandling valgtBehandling;
    public String saksnummer;

    private final BehandlingerJerseyKlient behandlingerKlient;
    private final OkonomiJerseyKlient okonomiKlient;
    private final ProsesstaskJerseyKlient prosesstaskKlient;
    private final VTPTilbakekrevingJerseyKlient vtpTilbakekrevingJerseyKlient;

    public TilbakekrevingSaksbehandler(Rolle rolle) {
        super(rolle);
        behandlingerKlient = new BehandlingerJerseyKlient(cookieRequestFilter);
        okonomiKlient = new OkonomiJerseyKlient(cookieRequestFilter);
        prosesstaskKlient = new ProsesstaskJerseyKlient(cookieRequestFilter);
        vtpTilbakekrevingJerseyKlient = new VTPTilbakekrevingJerseyKlient();
    }

    // Behandlinger actions
    // Oppretter ny tilbakekreving tilsvarende Manuell Opprettelse via
    // behandlingsmenyen.
    public void opprettTilbakekreving(String saksnummer, UUID uuid, String ytelseType) {
        behandlingerKlient.putTilbakekreving(new BehandlingOpprett(saksnummer, uuid, "BT-007", ytelseType));
    }

    public void opprettTilbakekrevingRevurdering(String saksnummer, UUID uuid, int behandlingId, String ytelseType,
            RevurderingArsak behandlingArsakType) {
        behandlingerKlient.putTilbakekreving(new BehandlingOpprettRevurdering(saksnummer, behandlingId, uuid,
                "BT-009", ytelseType, behandlingArsakType));
    }

    // Brukerrespons action
    public void registrerBrukerrespons(boolean akseptertFaktagrunnlag){
        var brukerrespons = new BrukerresponsDto(valgtBehandling.id);
        brukerrespons.setAkseptertFaktagrunnlag(akseptertFaktagrunnlag);
        behandlingerKlient.registrerBrukerrespons(brukerrespons);
    }


    // Verge actions
    public void leggTilVerge() {
        behandlingerKlient.addVerge(new BehandlingIdBasicDto(valgtBehandling.id));
        ventPåProsessering(valgtBehandling);
    }

    public void fjernVerge() {
        behandlingerKlient.removeVerge(new BehandlingIdBasicDto(valgtBehandling.id));
        ventPåProsessering(valgtBehandling);
        refreshBehandling();
    }

    // Henter siste behandlingen fra fptilbake på gitt saksnummer.
    public void hentSisteBehandling(String saksnummer) {
        this.saksnummer = String.valueOf(saksnummer);

        Vent.til(() -> !behandlingerKlient.hentAlleTbkBehandlinger(saksnummer).isEmpty(),
                30, "Behandling ble ikke opprettet");
        behandlingList = behandlingerKlient.hentAlleTbkBehandlinger(saksnummer);
        valgtBehandling = behandlingList.get(behandlingList.size() - 1);
    }

    public boolean harBehandlingsstatus(String status) {
        return Objects.equals(valgtBehandling.status, status);
    }

    // Generisk handling for å hente behandling på nytt
    private void refreshBehandling() {
        valgtBehandling = behandlingerKlient.hentTbkBehandling(valgtBehandling.uuid);
    }

    public void sendNyttKravgrunnlag(Kravgrunnlag kravgrunnlag, String saksnummer, int fpsakBehandlingId) {
        vtpTilbakekrevingJerseyKlient.oppdaterTilbakekrevingKonsistens(saksnummer, fpsakBehandlingId);
        okonomiKlient.putGrunnlag(kravgrunnlag, valgtBehandling.id);
    }

    public BeregningResultatPerioder hentResultat(UUID uuid){
        var resultat = new BeregningResultatPerioder();
        for (var beregningResultatPeriode : okonomiKlient.hentResultat(uuid).getBeregningResultatPerioderList()) {
            resultat.addRenteBeløp(beregningResultatPeriode.getRenteBeløp());
            resultat.addSkattBeløp(beregningResultatPeriode.getSkattBeløp());
            resultat.addTilbakekrevingBeløp(beregningResultatPeriode.getTilbakekrevingBeløp());
            resultat.addTilbakekrevingBeløpEtterSkatt(beregningResultatPeriode.getTilbakekrevingBeløpEtterSkatt());
            resultat.addTilbakekrevingBeløpUtenRenter(beregningResultatPeriode.getTilbakekrevingBeløpUtenRenter());
        }
        return resultat;
    }

    // Aksjonspunkt actions
    // Henter aksjonspunkt for en gitt kode, brukes ikke direkte i test men av
    // metoder for å verifisere at aksjonspunktet finnes.
    private AksjonspunktDto hentAksjonspunkt(int kode) {
        for (var aksjonspunktDto : behandlingerKlient.hentAlleAksjonspunkter(valgtBehandling.uuid)) {
            if (Objects.equals(aksjonspunktDto.definisjon, String.valueOf(kode))) {
                return aksjonspunktDto;
            }
        }
        return null;
    }

    public boolean harAktivtAksjonspunkt(int kode) {
        var aksjonspunktDto = hentAksjonspunkt(kode);
        if (aksjonspunktDto == null) {
            return false;
        }
        return (aksjonspunktDto.kanLoses && aksjonspunktDto.erAktivt);
    }

    public AksjonspunktBehandling hentAksjonspunktbehandling(int aksjonspunktkode) {
        if (!harAktivtAksjonspunkt(aksjonspunktkode)) {
            throw new IllegalStateException("Behandlingen har ikke nådd aksjonspunkt " + aksjonspunktkode);
        }
        switch (aksjonspunktkode) {
            case 7003:
                var apFaktaFeilutbetaling = new ApFaktaFeilutbetaling();
                for (var perioder : behandlingerKlient.hentFeilutbetalingFakta(valgtBehandling.uuid)
                        .getPerioder()) {
                    apFaktaFeilutbetaling.addFaktaPeriode(perioder.fom, perioder.tom);
                }
                return apFaktaFeilutbetaling;
            case 5002:
                var apVilkårsvurdering = new ApVilkårsvurdering();
                for (var perioder : behandlingerKlient.hentFeilutbetalingFakta(valgtBehandling.uuid)
                        .getPerioder()) {
                    apVilkårsvurdering.addVilkårPeriode(perioder.fom, perioder.tom);
                }
                return apVilkårsvurdering;
            case 5004:
                return new ForeslåVedtak();
            case 5005:
                return new FattVedtakTilbakekreving();
            case 5030:
                return new ApVerge();
            default:
                throw new IllegalArgumentException(aksjonspunktkode + " er ikke et gyldig aksjonspunkt eller ikke støttet!");
        }
    }

    // Metode for å sende inn og behandle et aksjonspunkt
    public void behandleAksjonspunkt(AksjonspunktBehandling aksjonspunktdata) {
        List<AksjonspunktBehandling> aksjonspunktdataer = new ArrayList<>();
        aksjonspunktdataer.add(aksjonspunktdata);
        var aksjonspunkter = new BehandledeAksjonspunkter(valgtBehandling, saksnummer, aksjonspunktdataer);
        behandlingerKlient.postAksjonspunkt(aksjonspunkter);
        refreshBehandling();
    }

    // Vent actions
    public void ventTilBehandlingErPåVent() {
        if (valgtBehandling.behandlingPaaVent) {
            return;
        }
        Vent.til(() -> {
            refreshBehandling();
            return valgtBehandling.behandlingPaaVent;
        }, 60, "Behandling kom aldri på vent");
    }

    public void ventTilBehandlingHarAktivtAksjonspunkt(int aksjonspunktKode) {
        if (harAktivtAksjonspunkt(aksjonspunktKode)) {
            refreshBehandling();
            return;
        }
        Vent.til(() -> {
            ventPåProsessering(valgtBehandling);
            refreshBehandling();
            return harAktivtAksjonspunkt(aksjonspunktKode);
        }, 60, "Aksjonspunkt" + aksjonspunktKode + "ble aldri oppnådd");
    }

    public void ventTilBehandlingsstatus(String status) {
        if (harBehandlingsstatus(status)) {
            return;
        }
        Vent.til(() -> {
            ventPåProsessering(valgtBehandling);
            refreshBehandling();
            return harBehandlingsstatus(status);
        }, 30, "Saken har ikke fått behanldingsstatus " + status);
    }

    public void ventTilAvsluttetBehandling() {
        ventTilBehandlingsstatus("AVSLU");
    }

    private void ventPåProsessering(Behandling behandling) {
        Vent.til(() -> verifiserProsesseringFerdig(behandling), 90, () -> {
            var prosessTaskList = new StringBuilder();
            for (var prosessTaskListItemDto : hentProsesstaskerForBehandling(behandling)) {
                prosessTaskList
                        .append(prosessTaskListItemDto.getTaskType())
                        .append(" - ")
                        .append(prosessTaskListItemDto.getStatus()).append("\n");
            }
            return "Behandling status var ikke klar men har ikke feilet\n" + prosessTaskList;
        });
    }

    private boolean verifiserProsesseringFerdig(Behandling behandling) {
        var status = behandlingerKlient.hentStatus(behandling.id);

        if ((status == null) || (status.getStatus() == null)) {
            return true;
        } else if (status.getStatus().getHttpStatus() == 418) {
            if (status.getStatus() != AsyncPollingStatus.Status.DELAYED) {
                AllureHelper.debugFritekst("Prosesstask feilet i behandlingsverifisering: " + status.getMessage());
                throw new IllegalStateException("Prosesstask i vrang tilstand: " + status.getMessage());
            } else {
                AllureHelper.debugFritekst("Prossesstask DELAYED: " + status.getMessage());
                return false;
            }
        } else if (status.isPending()) {
            return false;
        } else {
            AllureHelper.debugFritekst("Prosesstask feilet for behandling[" + behandling.id
                    + "] i behandlingsverifisering: " + status.getMessage());
            throw new RuntimeException("Status for behandling " + behandling.id + " feilet: " + status.getMessage());
        }
    }

    private List<ProsessTaskDataDto> hentProsesstaskerForBehandling(Behandling behandling) {
        return prosesstaskKlient.prosesstaskMedKlarEllerVentStatus().stream()
                .filter(p -> Objects.equals("" + behandling.id, p.getTaskParametre().getProperty("behandlingId")))
                .toList();
    }

    //Batch trigger
    public void startAutomatiskBehandlingBatch(){
        var prosessTaskOpprettInputDto = new ProsessTaskOpprettInputDto();
        prosessTaskOpprettInputDto.setTaskType("batch.automatisk.saksbehandling");
        prosesstaskKlient.create(prosessTaskOpprettInputDto);
    }
}
