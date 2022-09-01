package no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fptilbake;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType.REVURDERING_TILBAKEKREVING;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling.get;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftedeAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.BehandlingFptilbakeKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingIdBasicDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprettRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BrukerresponsDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.RevurderingArsak;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApFaktaFeilutbetaling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVerge;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ApVilkårsvurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.FattVedtakTilbakekreving;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.ForeslåVedtak;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.historikk.HistorikkFptilbakeKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.OkonomiKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.BeregningResultatPerioder;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask.ProsesstaskFptilbakeKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.tilbakekreving.VTPTilbakekrevingKlient;
import no.nav.foreldrepenger.autotest.util.vent.Lazy;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskOpprettInputDto;

// TODO: FIX
public class TilbakekrevingSaksbehandler {

    private final Logger LOG = LoggerFactory.getLogger(TilbakekrevingSaksbehandler.class);

    public List<Behandling> behandlingList;
    public Behandling valgtBehandling;
    public Saksnummer saksnummer;

    private Lazy<List<HistorikkInnslag>> historikkInnslag;

    private final Aktoer.Rolle rolle;
    private final BehandlingFptilbakeKlient behandlingerKlient;
    private final HistorikkFptilbakeKlient historikkKlient;
    private final OkonomiKlient okonomiKlient;
    private final ProsesstaskFptilbakeKlient prosesstaskKlient;
    private final VTPTilbakekrevingKlient vtpTilbakekrevingJerseyKlient;

    public TilbakekrevingSaksbehandler(Aktoer.Rolle rolle) {
        this.rolle = rolle;
        behandlingerKlient = new BehandlingFptilbakeKlient();
        historikkKlient = new HistorikkFptilbakeKlient();
        okonomiKlient = new OkonomiKlient();
        prosesstaskKlient = new ProsesstaskFptilbakeKlient();
        vtpTilbakekrevingJerseyKlient = new VTPTilbakekrevingKlient();
    }

    // Behandlinger actions
    // Oppretter ny tilbakekreving tilsvarende Manuell Opprettelse via
    // behandlingsmenyen.
    public void opprettTilbakekreving(Saksnummer saksnummer, UUID uuid, String ytelseType) {
        valgtBehandling = behandlingerKlient.opprettTilbakekrevingManuelt(new BehandlingOpprett(saksnummer, uuid, "BT-007", ytelseType));
    }

    public void opprettTilbakekrevingRevurdering(Saksnummer saksnummer, UUID uuid, int behandlingId, String ytelseType,
            RevurderingArsak behandlingArsakType) {
        valgtBehandling = behandlingerKlient.opprettTilbakekrevingManuelt(new BehandlingOpprettRevurdering(saksnummer, behandlingId, uuid,
                REVURDERING_TILBAKEKREVING.getKode(), ytelseType, behandlingArsakType));
    }

    // Brukerrespons action
    public void registrerBrukerrespons(boolean akseptertFaktagrunnlag){
        var brukerrespons = new BrukerresponsDto(valgtBehandling.id);
        brukerrespons.setAkseptertFaktagrunnlag(akseptertFaktagrunnlag);
        behandlingerKlient.registrerBrukerrespons(brukerrespons);
    }


    // Verge actions
    public void leggTilVerge() {
        valgtBehandling = behandlingerKlient.addVerge(new BehandlingIdBasicDto(valgtBehandling.id));
    }

    public void fjernVerge() {
        valgtBehandling = behandlingerKlient.removeVerge(new BehandlingIdBasicDto(valgtBehandling.id));
    }

    // Henter siste behandlingen fra fptilbake på gitt saksnummer.
    public void hentSisteBehandling(Saksnummer saksnummer) {
        this.saksnummer = saksnummer;
        ventPåOgVelgSisteBehandling(BehandlingType.TILBAKEKREVING);
    }

    public void hentSisteBehandling(Saksnummer saksnummer, BehandlingType behandlingstype) {
        Aktoer.loggInn(rolle); // TODO: Greit? eller gjøre det på en annen måte?
        this.saksnummer = saksnummer; // TODO.. fiks dette her
        ventPåOgVelgSisteBehandling(behandlingstype);
    }

    public void ventPåOgVelgSisteBehandling(BehandlingType behandlingstype) {
        ventPåOgVelgSisteBehandling(behandlingstype, null, null);
    }

    private void ventPåOgVelgSisteBehandling(BehandlingType behandlingstype, Integer antallBehandlingerSomMatcherType) {
        ventPåOgVelgSisteBehandling(behandlingstype, null, antallBehandlingerSomMatcherType);
    }

    @Step("Venter på at fagsak får behandlingstype {behandlingstype.kode} ")
    private void ventPåOgVelgSisteBehandling(BehandlingType behandlingstype, BehandlingÅrsakType behandlingÅrsakType,
                                             Integer antallBehandlingerSomMatcherType) {
        // 1) Vi venter til det er opprettet en behandling ved forventet type, årsak, status og antall
        var behandling = Vent.på(() -> {
            var matchedeBehandlinger = hentAlleBehandlingerAvTypen(behandlingstype, behandlingÅrsakType);
            if (matchedeBehandlinger == null || matchedeBehandlinger.isEmpty()) {
                return null;
            }

            if (antallBehandlingerSomMatcherType == null || antallBehandlingerSomMatcherType == matchedeBehandlinger.size()) {
                return matchedeBehandlinger.stream()
                        .max(Comparator.comparing(b -> b.opprettet))
                        .orElseThrow();
            }
            return null; // Vi har matchede behandlinger, men ikke av forventet antall!
        }, 30, "Saken har ikke fått behandling av type: " + behandlingstype);

        // 3) Venter til enten behandling avsluttet eller det har oppstått et aksjonspunkt
        venterPåFerdigProssesseringOgOppdaterBehandling(behandling.uuid);
        LOG.info("Behandling opprettet og oppdatert!");
    }

    private Set<Behandling> hentAlleBehandlingerAvTypen(BehandlingType behandlingstype, BehandlingÅrsakType behandlingÅrsakType) {
        return behandlingerKlient.alle(saksnummer).stream()
                .filter(b -> b.type.equals(behandlingstype))
                .collect(Collectors.toSet());
    }

    public boolean harBehandlingsstatus(String status) {
        return Objects.equals(valgtBehandling.status, status);
    }

    // Generisk handling for å hente behandling på nytt
    private void refreshBehandling() {
        venterPåFerdigProssesseringOgOppdaterBehandling(valgtBehandling.uuid);
    }

    public void sendNyttKravgrunnlag(Kravgrunnlag kravgrunnlag, Saksnummer saksnummer, int fpsakBehandlingId) {
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
    private Aksjonspunkt hentAksjonspunkt(int kode) {
        for (var aksjonspunktDto : behandlingerKlient.hentAlleAksjonspunkter(valgtBehandling.uuid)) {
            if (Objects.equals(aksjonspunktDto.getDefinisjon(), String.valueOf(kode))) {
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
        return (aksjonspunktDto.getKanLoses() && aksjonspunktDto.getErAktivt());
    }

    // TODO: Gjør det samme som i fpsak saksbehandler
    public AksjonspunktBekreftelse hentAksjonspunktbehandling(int aksjonspunktkode) {
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
    public void behandleAksjonspunkt(AksjonspunktBekreftelse aksjonspunktBekreftelse) {
        var aksjonspunkter = new BekreftedeAksjonspunkter(valgtBehandling.uuid, valgtBehandling.versjon, List.of(aksjonspunktBekreftelse));
        behandlingerKlient.postBehandlingAksjonspunkt(aksjonspunkter);
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
            venterPåFerdigProssesseringOgOppdaterBehandling(valgtBehandling.uuid);
            return harAktivtAksjonspunkt(aksjonspunktKode);
        }, 60, "Aksjonspunkt" + aksjonspunktKode + "ble aldri oppnådd");
    }

    public void ventTilBehandlingsstatus(BehandlingStatus forventetStatus) {
        venterPåFerdigProssesseringOgOppdaterBehandling(valgtBehandling.uuid);
        var behandlingsstatus = valgtBehandling.status;
        if (forventetStatus.equals(behandlingsstatus)) {
            return;
        }
        throw new IllegalStateException(String.format("Behandlingsstatus for behandling %s var ikke %s, men var %s",
                valgtBehandling.uuid, forventetStatus, behandlingsstatus));
    }

    public List<HistorikkInnslag> hentHistorikkinnslagPåFagsak() {
        refreshBehandling();
        return get(historikkInnslag);
    }

    public List<HistorikkInnslag> hentHistorikkinnslagPåBehandling() {
        return hentHistorikkinnslagPåBehandling(valgtBehandling.uuid);
    }

    public List<HistorikkInnslag> hentHistorikkinnslagPåBehandling(UUID uuid) {
        return hentHistorikkinnslagPåFagsak().stream()
                .filter(innslag -> Objects.equals(uuid, innslag.behandlingUuid()))
                .toList();
    }

    public void ventTilAvsluttetBehandling() {
        LOG.info("Venter til behandling er avsluttet ...");

        /**
         * Hvis vi har en BEH_VENT på behandlingen OG saken IKKE er GJENOPPRETTET da er vi enten i
         * 1) En feiltilstand og behandlingen kan ikke avsluttes
         * 2) Behandlingen er ikke tatt av vent enda og vi venter på at behandlingen GJENOPPRETTET
         *    Venter da til den er gjenopprettet, for så og vente på potensiell prosessering.
         */
        if (hentHistorikkinnslagPåBehandling().stream().anyMatch(h -> h.type().equals(HistorikkinnslagType.BEH_VENT))) {
            Vent.til(() -> hentHistorikkinnslagPåBehandling().stream().anyMatch(h -> h.type().equals(HistorikkinnslagType.BEH_GJEN))
                    ,10, "Behandlingen er på vent og er ikke blitt gjenopptatt!");
        }

        ventTilBehandlingsstatus(BehandlingStatus.AVSLUTTET);
        LOG.info("Alle manuelle aksjonspunkt er løst og behandlingen har status AVSLUTTET");
    }

    private void venterPåFerdigProssesseringOgOppdaterBehandling(UUID behandlingsuuid) {
        valgtBehandling = ventTilBehandlingErFerdigProsessertOgReturner(behandlingsuuid);
        this.historikkInnslag = new Lazy<>(() -> historikkKlient.hentHistorikk(this.saksnummer));
    }

    /**
     * Status endepunktet i fpsak fungerer med unntak når saken er satt på vent. Hvis vi sjekke status før behandlingen er
     * gjenopptatt, vil den bare returnere behandlingen før prosesseringene er ferdig. Må legge inn noe spesiallokikk for håndtering av dette.
     */
    private Behandling ventTilBehandlingErFerdigProsessertOgReturner(UUID behandlinguuid) {
        return Vent.på(() -> behandlingerKlient.hentBehandlingHvisTilgjenglig(behandlinguuid), 90, () -> {
            var prosessTasker = hentProsesstaskerForBehandling(behandlinguuid);
            var prosessTaskList = new StringBuilder();
            for (ProsessTaskDataDto prosessTaskListItemDto : prosessTasker) {
                prosessTaskList.append(prosessTaskListItemDto.getTaskType())
                        .append(" - ")
                        .append(prosessTaskListItemDto.getStatus())
                        .append("\n");
            }
            return "Behandling status var ikke klar men har ikke feilet\n" + prosessTaskList;
        });
    }

    private List<ProsessTaskDataDto> hentProsesstaskerForBehandling(UUID behandlingsuuid) {
        return prosesstaskKlient.prosesstaskMedKlarEllerVentStatus().stream()
                .filter(p -> Objects.equals(behandlingsuuid.toString(), p.getTaskParametre().getProperty("behandlingId")))
                .toList();
    }

    //Batch trigger
    public void startAutomatiskBehandlingBatchOgVentTilAutoPunktErKjørt(int autopunkt){
        var prosessTaskOpprettInputDto = new ProsessTaskOpprettInputDto();
        prosessTaskOpprettInputDto.setTaskType("batch.automatisk.saksbehandling");
        prosesstaskKlient.create(prosessTaskOpprettInputDto);
        Vent.til(() -> hentAksjonspunkt(autopunkt) == null, 10, "Kravgrunnlag skal være sendt og plukket opp av batch!");
    }
}
