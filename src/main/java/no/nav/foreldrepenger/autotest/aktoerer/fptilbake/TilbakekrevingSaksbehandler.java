package no.nav.foreldrepenger.autotest.aktoerer.fptilbake;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus; //denne, --
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.ProsessTaskListItemDto;//-- og denne FPSAK import er OK. Ellers skal man generelt ikke blande fpsak og fptilbake
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.SokeFilterDto; //denne --
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.BehandlingerKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.*;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.FeilutbetalingPerioder;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.AksjonspunktBehandling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApFaktaFeilutbetaling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVerge;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVilkårsvurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.BehandledeAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.FattVedtakTilbakekreving;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ForeslåVedtak;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.OkonomiKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.BeregningResultat;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.BeregningResultatPerioder;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask.ProsesstaskKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask.dto.NewProsessTaskDto;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.vent.Vent;

public class TilbakekrevingSaksbehandler extends Aktoer {

    public List<Behandling> behandlingList;
    public Behandling valgtBehandling;
    public String saksnummer;

    private BehandlingerKlient behandlingerKlient;
    private OkonomiKlient okonomiKlient;
    private ProsesstaskKlient prosesstaskKlient;

    public TilbakekrevingSaksbehandler() {
        super();
        behandlingerKlient = new BehandlingerKlient(session);
        okonomiKlient = new OkonomiKlient(session);
        prosesstaskKlient = new ProsesstaskKlient(session);
    }

    // Behandlinger actions
    // Oppretter ny tilbakekreving tilsvarende Manuell Opprettelse via
    // behandlingsmenyen.
    public void opprettTilbakekreving(Long saksnummer, UUID uuid, String ytelseType) {
        behandlingerKlient.putTilbakekreving(new BehandlingOpprett(saksnummer, uuid, "BT-007", ytelseType));
    }

    public void opprettTilbakekrevingRevurdering(Long saksnummer, UUID uuid, int behandlingId, String ytelseType,
            RevurderingArsak behandlingArsakType) {
        behandlingerKlient.putTilbakekreving(new BehandlingOpprettRevurdering(saksnummer, valgtBehandling.id, uuid,
                "BT-009", ytelseType, behandlingArsakType));
    }

    // Brukerrespons action
    public void registrerBrukerrespons(boolean akseptertFaktagrunnlag){
        BrukerresponsDto brukerrespons = new BrukerresponsDto(valgtBehandling.id);
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
    public void hentSisteBehandling(Long saksnummer) {
        behandlingList = behandlingerKlient.hentAlleTbkBehandlinger(saksnummer);
        valgtBehandling = null;
        this.saksnummer = String.valueOf(saksnummer);

        if (behandlingList.isEmpty()) {
            throw new RuntimeException("Finnes ingen behandlinger på saksnummer");
        } else if (behandlingList.size() == 1) {
            valgtBehandling = behandlingList.get(0);
        } else {
            valgtBehandling = behandlingList.get(behandlingList.size() - 1);
        }
    }

    public boolean harBehandlingsstatus(String status) {
        return valgtBehandling.status.kode.equals(status);
    }

    // Generisk handling for å hente behandling på nytt
    private void refreshBehandling() {
        valgtBehandling = behandlingerKlient.hentTbkBehandling(valgtBehandling.uuid);
    }

    // Økonomi actions
    public void sendNyttKravgrunnlag(Long saksnummer, String ident, int fpsakBehandlingId, String ytelseType) {
        Kravgrunnlag kravgrunnlag = new Kravgrunnlag(saksnummer, ident, fpsakBehandlingId, ytelseType, "NY");
        sendNyttKravgrunnlag(kravgrunnlag);
    }

    public void sendNyttKravgrunnlag(Kravgrunnlag kravgrunnlag) {
        okonomiKlient.putGrunnlag(kravgrunnlag, valgtBehandling.id);
    }

    public void sendEndretKravgrunnlag(Long saksnummer, String ident, int fpsakBehandlingId, String ytelseType,
            int behandlingId) {
        Kravgrunnlag kravgrunnlag = new Kravgrunnlag(saksnummer, ident, fpsakBehandlingId, ytelseType, "ENDR");
        okonomiKlient.putGrunnlag(kravgrunnlag, behandlingId);
    }

    public BeregningResultatPerioder hentResultat(UUID uuid){
        BeregningResultatPerioder resultat = new BeregningResultatPerioder();
        for (BeregningResultatPerioder beregningResultatPeriode : okonomiKlient.hentResultat(uuid).getBeregningResultatPerioderList()) {
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
        for (AksjonspunktDto aksjonspunktDto : behandlingerKlient.hentAlleAksjonspunkter(valgtBehandling.uuid)) {
            if (aksjonspunktDto.definisjon.kode.equals(String.valueOf(kode))) {
                return aksjonspunktDto;
            }
        }
        return null;
    }

    public boolean harAktivtAksjonspunkt(int kode) {
        AksjonspunktDto aksjonspunktDto = hentAksjonspunkt(kode);
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
                ApFaktaFeilutbetaling apFaktaFeilutbetaling = new ApFaktaFeilutbetaling();
                for (FeilutbetalingPerioder perioder : behandlingerKlient.hentFeilutbetalingFakta(valgtBehandling.uuid)
                        .getPerioder()) {
                    apFaktaFeilutbetaling.addFaktaPeriode(perioder.fom, perioder.tom);
                }
                return apFaktaFeilutbetaling;
            case 5002:
                ApVilkårsvurdering apVilkårsvurdering = new ApVilkårsvurdering();
                for (FeilutbetalingPerioder perioder : behandlingerKlient.hentFeilutbetalingFakta(valgtBehandling.uuid)
                        .getPerioder()) {
                    apVilkårsvurdering.addVilkårPeriode(perioder.fom, perioder.tom);
                }
                return apVilkårsvurdering;
            case 5003:
//                ApForeldelse apForeldelse = new ApForeldelse();
            case 5004:
                return new ForeslåVedtak();
            case 5005:
                return new FattVedtakTilbakekreving();
            case 5030:
                return new ApVerge();
            default:
                throw new IllegalArgumentException(aksjonspunktkode + " er ikke et gyldig aksjonspunkt");
        }
    }

    // Metode for å sende inn og behandle et aksjonspunkt
    public void behandleAksjonspunkt(AksjonspunktBehandling aksjonspunktdata) {
        List<AksjonspunktBehandling> aksjonspunktdataer = new ArrayList<>();
        aksjonspunktdataer.add(aksjonspunktdata);
        BehandledeAksjonspunkter aksjonspunkter = new BehandledeAksjonspunkter(valgtBehandling, saksnummer,
                aksjonspunktdataer);
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
        Vent.til(() -> {
            return verifiserProsesseringFerdig(behandling);
        }, 90, () -> {
            List<ProsessTaskListItemDto> prosessTasker = hentProsesstaskerForBehandling(behandling);
            String prosessTaskList = "";
            for (ProsessTaskListItemDto prosessTaskListItemDto : prosessTasker) {
                prosessTaskList += prosessTaskListItemDto.getTaskType() + " - " + prosessTaskListItemDto.getStatus()
                        + "\n";
            }
            return "Behandling status var ikke klar men har ikke feilet\n" + prosessTaskList;
        });
    }

    private boolean verifiserProsesseringFerdig(Behandling behandling) {
        AsyncPollingStatus status = behandlingerKlient.hentStatus(behandling.id);

        if ((status == null) || (status.getStatusCode() == null)) {
            return true;
        } else if (status.getStatusCode() == 418) {
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

    private List<ProsessTaskListItemDto> hentProsesstaskerForBehandling(Behandling behandling) {
        SokeFilterDto filter = new SokeFilterDto();
        filter.setSisteKjoeretidspunktFraOgMed(LocalDateTime.now().minusMinutes(5));
        filter.setSisteKjoeretidspunktTilOgMed(LocalDateTime.now());
        List<ProsessTaskListItemDto> prosesstasker = prosesstaskKlient.list(filter);
        return prosesstasker.stream().filter(p -> p.getTaskParametre().getBehandlingId() == ("" + behandling.id))
                .collect(Collectors.toList());
    }

    //Batch trigger
    public void startAutomatiskBehandlingBatch(){
        prosesstaskKlient.create(new NewProsessTaskDto("batch.runner", "BFPT-003"));
    }
}
