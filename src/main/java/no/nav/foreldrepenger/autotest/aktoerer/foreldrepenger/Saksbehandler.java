package no.nav.foreldrepenger.autotest.aktoerer.foreldrepenger;

import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling.get;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugAksjonspunktbekreftelser;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.RisikovurderingJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.dto.RisikovurderingResponse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingerJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingHenlegg;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdPost;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingNy;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingPaVent;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.KlageVurderingResultatAksjonspunktMellomlagringDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftedeAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelseUtenTotrinn;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.OverstyrAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Vilkar;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeid.Arbeidsforhold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriodeAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.BehandlingMedUttaksperioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.KodeverkJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kodeverk;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.ProsesstaskJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.ProsessTaskListItemDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.SokeFilterDto;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.vent.Lazy;
import no.nav.foreldrepenger.autotest.util.vent.Vent;

public class Saksbehandler extends Aktoer {

    Logger LOG = LoggerFactory.getLogger(Saksbehandler.class);


    public Fagsak valgtFagsak;
    public Behandling valgtBehandling;
    public List<Behandling> behandlinger;
    public Kodeverk kodeverk;

    private Lazy<List<HistorikkInnslag>> historikkInnslag;
    private Lazy<Behandling> annenPartBehandling;

    private final FagsakJerseyKlient fagsakKlient;
    private final BehandlingerJerseyKlient behandlingerKlient;
    private final KodeverkJerseyKlient kodeverkKlient;
    private final HistorikkJerseyKlient historikkKlient;
    private final ProsesstaskJerseyKlient prosesstaskKlient;
    private final RisikovurderingJerseyKlient risikovurderingKlient;


    public Saksbehandler() {
        super();
        fagsakKlient = new FagsakJerseyKlient();
        behandlingerKlient = new BehandlingerJerseyKlient();
        kodeverkKlient = new KodeverkJerseyKlient();
        historikkKlient = new HistorikkJerseyKlient();
        prosesstaskKlient = new ProsesstaskJerseyKlient();
        risikovurderingKlient = new RisikovurderingJerseyKlient();
    }

    public Saksbehandler(Rolle rolle) {
        this();
        erLoggetInnMedRolle(rolle);
    }

    @Override
    public void erLoggetInnMedRolle(Rolle rolle) {
        super.erLoggetInnMedRolle(rolle);
        refreshKodeverk();
    }

    @Override
    public void erLoggetInnUtenRolle() {
        super.erLoggetInnUtenRolle();
        refreshKodeverk();
    }

    /*
     * Fagsak
     */
    public void hentFagsak(long saksnummer) {
        hentFagsak("" + saksnummer);
    }

    @Step("Hent fagsak {saksnummer}")
    public void hentFagsak(String saksnummer) {
        valgtFagsak = fagsakKlient.getFagsak(saksnummer);
        if (valgtFagsak == null) {
            throw new RuntimeException("Kan ikke velge fagsak. fagsak er null");
        }
        behandlinger = hentAlleBehandlingerForFagsak(valgtFagsak.saksnummer());
        velgSisteBehandling();
    }

    public void ventTilFagsakAvsluttet() {
        ventTilFagsakstatus("AVSLU");
    }

    public void ventTilFagsakLøpende() {
        ventTilFagsakstatus("LOP");
    }

    private void ventTilFagsakstatus(String status) {
        ventTilFagsakstatus(kodeverk.FagsakStatus.getKode(status));
    }

    private void ventTilFagsakstatus(Kode status) {
        if (harFagsakstatus(status)) {
            return;
        }
        Vent.til(() -> {
            refreshFagsak();
            return harFagsakstatus(status);
        }, 10, "Fagsak har ikke status " + status);
    }

    private boolean harFagsakstatus(Kode status) {
        return valgtFagsak.status().equals(status);
    }

    @Step("Refresh fagsak")
    private void refreshFagsak() {
        hentFagsak(valgtFagsak.saksnummer());
    }

    /*
     * Behandling
     */
    @Step("Henter behandlinger for saksnummer {saksnummer}")
    public List<Behandling> hentAlleBehandlingerForFagsak(long saksnummer) {
        return behandlingerKlient.alle(saksnummer);
    }

    public void velgSisteBehandling() {
        var behandling = hentAlleBehandlingerForFagsak(valgtFagsak.saksnummer()).stream()
                .max(Comparator.comparing(b -> b.opprettet))
                .orElseThrow();
        velgBehandling(behandling);
    }

    public void ventPåOgVelgFørstegangsbehandling() {
        ventPåOgVelgBehandling(kodeverk.BehandlingType.getKode("BT-002"));
    }

    public void ventPåOgVelgKlageBehandling() {
        ventPåOgVelgBehandling(kodeverk.BehandlingType.getKode("BT-003"));
    }

    public void ventPåOgVelgRevurderingBehandling() {
        ventPåOgVelgBehandling(kodeverk.BehandlingType.getKode("BT-004"));
    }

    public boolean harRevurderingBehandling() {
        return harBehandling(kodeverk.BehandlingType.getKode("BT-004"));
    }

    public void ventPåOgVelgDokumentInnsynBehandling() {
        ventPåOgVelgBehandling(kodeverk.BehandlingType.getKode("BT-006"));
    }

    private void ventPåOgVelgBehandling(Kode behandlingstype) {
        ventTilSakHarBehandling(behandlingstype);
        velgBehandling(getBehandling(behandlingstype));
    }

    @Step("Venter på at fagsak får behandlingstype: {behandlingType}")
    private void ventTilSakHarBehandling(Kode behandlingType) {
        if (harBehandling(behandlingType)) {
            return;
        }
        Vent.til(() -> harBehandling(behandlingType), 30, "Saken har ikke fått behandling av type: " + behandlingType);
    }

    private boolean harBehandling(Kode behandlingType) {
        refreshFagsak();
        for (Behandling behandling : behandlinger) {
            if (behandling.type.kode.equals(behandlingType.kode)) {
                return true;
            }
        }
        return false;
    }

    private Behandling getBehandling(Kode behandlingstype) {
        for (Behandling behandling : behandlinger) {
            if (behandling.type.kode.equals(behandlingstype.kode)) {
                return behandling;
            }
        }
        return null;
    }

    /*
     * Behandlingsstatus
     */
    @Step("Venter på behandlingsstatus {status}")
    public void ventTilBehandlingsstatus(String status) {
        if (harBehandlingsstatus(status)) {
            return;
        }
        Vent.til(() -> {
            refreshBehandling();
            return harBehandlingsstatus(status);
        }, 60, "Behandlingsstatus var ikke " + status + " men var " + getBehandlingsstatus() + " i sak: "
                + valgtFagsak.saksnummer());
    }

    @Step("Refresh behandling")
    private void refreshBehandling() {
        velgBehandling(valgtBehandling);
    }

    public boolean harBehandlingsstatus(String status) {
        return getBehandlingsstatus().equals(status);
    }

    public String getBehandlingsstatus() {
        return valgtBehandling.status.kode;
    }

    @Step("Velger behandling")
    private void velgBehandling(Behandling behandling) {
        debugLoggBehandling(behandling);

        // Sjekker om behandlingen prosesserer. Siden vi vil vente på at den er ferdig for å få den siste
        // behandling.versjon. Og å hindre at tester henter data fra behandlingen som kan endre seg ettersom
        // behandlingen ikke har stoppet opp
        ventPåProsessering(behandling);

        valgtBehandling = behandlingerKlient.getBehandling(behandling.uuid);
        populateBehandling(valgtBehandling);

        this.historikkInnslag = new Lazy<>(() -> historikkKlient.hentHistorikk(valgtFagsak.saksnummer()));
        this.annenPartBehandling = new Lazy<>(() -> behandlingerKlient.annenPartBehandling(valgtFagsak.saksnummer()));
    }

    private void ventPåProsessering(Behandling behandling) {
        Vent.til(() -> verifiserProsesseringFerdig(behandling), 90, () -> {
            var prosessTasker = hentProsesstaskerForBehandling(behandling);
            var prosessTaskList = new StringBuilder();
            for (ProsessTaskListItemDto prosessTaskListItemDto : prosessTasker) {
                prosessTaskList.append(prosessTaskListItemDto.taskType())
                        .append(" - ")
                        .append(prosessTaskListItemDto.status())
                        .append("\n");
            }
            return "Behandling status var ikke klar men har ikke feilet\n" + prosessTaskList.toString();
        });
    }

    private boolean verifiserProsesseringFerdig(Behandling behandling) {
        var status = behandlingerKlient.statusAsObject(behandling.uuid);

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

    @Step("Populerer behandling")
    private void populateBehandling(Behandling behandling) {

        behandling.setAksjonspunkter(new Lazy<>(() -> behandlingerKlient.getBehandlingAksjonspunkt(behandling.uuid)));
        behandling.setVilkar(new Lazy<>(() -> behandlingerKlient.behandlingVilkår(behandling.uuid)));

        /*
         * KODE OFFISIELL_KODE BESKRIVELSE BT-002 ae0034 Førstegangsbehandling BT-003
         * ae0058 Klage BT-004 ae0028 Revurdering BT-005 ae0043 Tilbakebetaling endring
         * BT-006 ae0042 Dokumentinnsyn
         */

        if (behandling.type.kode.equalsIgnoreCase("BT-006") /* Dokumentinnsyn */) {
            // Gjør ingenting
        } else if (behandling.type.kode.equalsIgnoreCase("BT-003" /* Klage */)) {
            behandling.setKlagevurdering(new Lazy<>(() -> behandlingerKlient.klage(behandling.uuid)));
        } else {
            // FIXME: Forespørslene her burde konsultere resultat for valgtbehandling for å
            // sjekke om URLene er tilgjengelig før de kjører.
            // URLene kan endre seg, men koden i behandlingerKlient tar ikke hensyn til det
            // p.t. I tillegg er det unødvendig å spørre på noe som ikke
            // finnes slik det skjer nå.

            behandling.setUttakResultatPerioder(
                    new Lazy<>(() -> behandlingerKlient.behandlingUttakResultatPerioder(behandling.uuid)));
            behandling.setSaldoer(new Lazy<>(() -> behandlingerKlient.behandlingUttakStonadskontoer(behandling.uuid)));

            behandling.setBeregningsgrunnlag(
                    new Lazy<>(() -> behandlingerKlient.behandlingBeregningsgrunnlag(behandling.uuid)));
            behandling.setInntektArbeidYtelse(
                    new Lazy<>(() -> behandlingerKlient.behandlingInntektArbeidYtelse(behandling.uuid)));

            behandling.setBeregningResultatEngangsstonad(
                    new Lazy<>(() -> behandlingerKlient.behandlingBeregningsresultatEngangsstønad(behandling.uuid)));
            behandling.setBeregningResultatForeldrepenger(
                    new Lazy<>(() -> behandlingerKlient.behandlingBeregningsresultatForeldrepenger(behandling.uuid)));
            behandling.setPersonopplysning(
                    new Lazy<>(() -> behandlingerKlient.behandlingPersonopplysninger(behandling.uuid)));
            behandling.setSoknad(new Lazy<>(() -> behandlingerKlient.behandlingSøknad(behandling.uuid)));
            behandling.setOpptjening(new Lazy<>(() -> behandlingerKlient.behandlingOpptjening(behandling.uuid)));

            behandling.setKontrollerFaktaData(
                    new Lazy<>(() -> behandlingerKlient.behandlingKontrollerFaktaPerioder(behandling.uuid)));
            behandling.setKontrollerAktivitetskrav(
                    new Lazy<>(() -> behandlingerKlient.behandlingKontrollerAktivitetskrav(behandling.uuid)));
            behandling.setMedlem(new Lazy<>(() -> behandlingerKlient.behandlingMedlemskap(behandling.uuid)));

            behandling
                    .setTilrettelegging(new Lazy<>(() -> behandlingerKlient.behandlingTilrettelegging(behandling.id)));
        }
    }

    /*
     * Henlegg behandling eller sett på vent
     */
    @Step("Setter behandling på vent")
    protected void settBehandlingPåVent(LocalDate frist, Kode årsak) {
        behandlingerKlient.settPaVent(new BehandlingPaVent(valgtBehandling, frist, årsak));
        refreshBehandling();
    }

    public void settBehandlingPåVent(LocalDate frist, String årsak) {
        settBehandlingPåVent(frist, kodeverk.Venteårsak.getKode(årsak));
    }

    @Step("Gjenopptar Behandling")
    public void gjenopptaBehandling() {
        behandlingerKlient.gjenoppta(new BehandlingIdPost(valgtBehandling));
        refreshBehandling();
    }

    @Step("Henlegger behandling")
    public void henleggBehandling(Kode årsak) {
        behandlingerKlient
                .henlegg(new BehandlingHenlegg(valgtBehandling.id, valgtBehandling.versjon, årsak.kode, "Henlagt"));
        refreshBehandling();
    }

    /*
     * Opretter behandling på nåværende fagsak
     */
    @Step("Oppretter behandling på gitt fagsak")
    public void opprettBehandling(Kode behandlingstype, Kode årsak) {
        opprettBehandling(behandlingstype, årsak, valgtFagsak);
        hentFagsak(valgtFagsak.saksnummer());
    }

    private void opprettBehandling(Kode behandlingstype, Kode årsak, Fagsak fagsak) {
        behandlingerKlient.putBehandlinger(
                new BehandlingNy(fagsak.saksnummer(), behandlingstype.kode, årsak == null ? null : årsak.kode));
    }

    public void opprettBehandlingRevurdering(String årsak) {
        opprettBehandling(kodeverk.BehandlingType.getKode("BT-004"), kodeverk.BehandlingÅrsakType.getKode(årsak));
    }

    public void oprettBehandlingInnsyn(Kode årsak) {
        opprettBehandling(kodeverk.BehandlingType.getKode("BT-006"), årsak);
    }

    /*
     * Henting av uttaksperidoer
     */
    public List<UttakResultatPeriode> hentAvslåtteUttaksperioder() {
        return valgtBehandling.hentUttaksperioder().stream()
                .filter(uttakResultatPeriode -> uttakResultatPeriode.getPeriodeResultatType().kode
                        .equalsIgnoreCase("AVSLÅTT"))
                .collect(Collectors.toList());
    }

    /*
     * Henting av Saldo
     */
    public Saldoer hentSaldoerGittUttaksperioder(List<UttakResultatPeriode> uttakResultatPerioder) {
        BehandlingMedUttaksperioderDto behandlingMedUttaksperioderDto = new BehandlingMedUttaksperioderDto();
        behandlingMedUttaksperioderDto.setPerioder(uttakResultatPerioder);
        BehandlingIdDto behandlingIdDto = new BehandlingIdDto((long) valgtBehandling.id);
        behandlingMedUttaksperioderDto.setBehandlingId(behandlingIdDto);

        return behandlingerKlient.behandlingUttakStonadskontoerGittUttaksperioder(behandlingMedUttaksperioderDto);
    }

    /*
     * Henting av Beregningsresultat
     */
    public List<BeregningsresultatPeriodeAndel> hentBeregningsresultatPerioderMedAndelIArbeidsforhold(
            String organisasjonsnummer) {
        return valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().stream()
                .flatMap(beregningsresultatPeriode -> beregningsresultatPeriode.getAndeler().stream())
                .filter(andeler -> organisasjonsnummer.equalsIgnoreCase(andeler.getArbeidsgiverReferanse()))
                .sorted(Comparator.comparing(BeregningsresultatPeriodeAndel::getSisteUtbetalingsdato))
                .collect(Collectors.toList());
    }

    public List<BeregningsresultatPeriodeAndel> hentBeregningsresultatPerioderMedAndelISN() {
        return valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().stream()
                .flatMap(beregningsresultatPeriode -> beregningsresultatPeriode.getAndeler().stream())
                .filter(andeler -> andeler.getAktivitetStatus().kode.equalsIgnoreCase("SN"))
                .collect(Collectors.toList());
    }

    /*
     * Henting av kodeverk
     */
    public Kodeverk hentKodeverk() {
        if (kodeverk == null) {
            refreshKodeverk();
        }
        return kodeverk;
    }

    public void refreshKodeverk() {
        try {
            kodeverk = kodeverkKlient.getKodeverk();
        } catch (Exception e) {
            LOG.info(e.toString());
            LOG.info(e.getStackTrace().toString());
            e.printStackTrace();
            throw new RuntimeException("Kunne ikke hente kodeverk: " + e.getMessage());
        }
    }


    @Step("Henter ut unike aktivitetstatuser i beregning")
    public Set<Kode> hentUnikeBeregningAktivitetStatus() {
        Set<Kode> aktivitetStatus = new HashSet<>();
        for (var beregningsresultatPerioder : valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()) {
            for (var andel : beregningsresultatPerioder.getAndeler()) {
                aktivitetStatus.add(andel.getAktivitetStatus());
            }
        }
        return aktivitetStatus;
    }

    /*
     * Henter aksjonspunkt bekreftelse av gitt klasse
     */
    @Step("Henter aksjonspunktbekreftelse for {type}")
    public <T extends AksjonspunktBekreftelse> T hentAksjonspunktbekreftelse(Class<T> type) {
        var aksjonspunktKode = type.getDeclaredAnnotation(BekreftelseKode.class).kode();
        var aksjonspunkt = hentAksjonspunkt(aksjonspunktKode);
        var bekreftelse = aksjonspunkt.getBekreftelse();
        bekreftelse.oppdaterMedDataFraBehandling(valgtFagsak, valgtBehandling);
        return (T) bekreftelse;
    }

    /*
     * Henter aksjonspunkt av gitt kode
     */
    @Step("Henter aksjonspunkt {kode}")
    public Aksjonspunkt hentAksjonspunkt(String kode) {
        return valgtBehandling.getAksjonspunkter().stream()
                .filter(ap -> ap.getDefinisjon().kode.equals(kode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Fant ikke aksjonspunkt med kode " + kode));
    }

    @Step("Henter aksjonspunkt som skal til totrinns kontroll")
    public List<Aksjonspunkt> hentAksjonspunktSomSkalTilTotrinnsBehandling() {
        return valgtBehandling.getAksjonspunkter().stream()
                .filter(Aksjonspunkt::skalTilToTrinnsBehandling)
                .collect(Collectors.toList());
    }

    /*
     * Sjekker om aksjonspunkt av gitt kode er på behandlingen
     */
    @Step("Sjekker om aksjonspunkt av gitt kode er på behandling")
    public boolean harAksjonspunkt(String kode) {
        debugLoggBehandling(valgtBehandling);
        try {
            return hentAksjonspunkt(kode) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Bekrefte aksjonspunkt bekreftelse
     */
    @Step("Henter og bekrefter aksjonspunkt for {type}")
    public <T extends AksjonspunktBekreftelse> void bekreftAksjonspunktMedDefaultVerdier(Class<T> type) {
        bekreftAksjonspunkt(hentAksjonspunktbekreftelse(type));
    }

    public void bekreftAksjonspunkt(AksjonspunktBekreftelse bekreftelse) {
        List<AksjonspunktBekreftelse> bekreftelser = new ArrayList<>();
        bekreftelser.add(bekreftelse);
        bekreftAksjonspunktbekreftelserer(bekreftelser);
    }

    public void bekreftAksjonspunktbekreftelserer(AksjonspunktBekreftelse... bekreftelser) {
        bekreftAksjonspunktbekreftelserer(Arrays.asList(bekreftelser));
    }

    @Step("Bekrefter aksjonspunktbekreftelser")
    public void bekreftAksjonspunktbekreftelserer(List<AksjonspunktBekreftelse> bekreftelser) {
        debugAksjonspunktbekreftelser(bekreftelser);
        BekreftedeAksjonspunkter aksjonspunkter = new BekreftedeAksjonspunkter(valgtFagsak, valgtBehandling, bekreftelser);
        behandlingerKlient.postBehandlingAksjonspunkt(aksjonspunkter);
        refreshBehandling();
    }

    @Step("Fatter vedtak og venter til sak er avsluttet")
    public void fattVedtakOgVentTilAvsluttetBehandling(FatterVedtakBekreftelse bekreftelse) {
        bekreftAksjonspunkt(bekreftelse);
        ventTilAvsluttetBehandling();
    }

    @Step("Fatter vedtak uten totrinnsbehandling og venter til sak er avsluttet")
    public void fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling() {
        bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelseUtenTotrinn.class);
        ventTilAvsluttetBehandling();
    }

    @Step("Venter til saken er avsluttet")
    public void ventTilAvsluttetBehandling() {
        ventTilBehandlingsstatus("AVSLU");
    }


    /*
     * Oversyring
     */
    public void overstyr(AksjonspunktBekreftelse bekreftelse) {
        List<AksjonspunktBekreftelse> bekreftelser = new ArrayList<>();
        bekreftelser.add(bekreftelse);
        overstyr(bekreftelser);
    }

    @Step("Overstyrer aksjonspunkt")
    public void overstyr(List<AksjonspunktBekreftelse> bekreftelser) {
        OverstyrAksjonspunkter aksjonspunkter = new OverstyrAksjonspunkter(valgtFagsak, valgtBehandling, bekreftelser);
        behandlingerKlient.overstyr(aksjonspunkter);
        refreshBehandling();
    }

    /*
     * Historikkinnslag
     */
    @Step("Venter på historikkinnslag {type}")
    public void ventTilHistorikkinnslag(HistorikkInnslag.Type type) {
        Vent.til(() -> harHistorikkinnslagForBehandling(type, valgtBehandling.id),
                60, () -> "Saken  hadde ikke historikkinslag " + type + "\nHistorikkInnslag:"
                        + String.join("\t\n", String.valueOf(getHistorikkInnslag())));

    }

    public boolean harHistorikkinnslagForBehandling(HistorikkInnslag.Type type) {
        return harHistorikkinnslagForBehandling(type, valgtBehandling.id);
    }

    public boolean harHistorikkinnslagForBehandling(HistorikkInnslag.Type type, int behandlingsId) {
        if (type == HistorikkInnslag.VEDLEGG_MOTTATT) {
            behandlingsId = 0;
        }
        for (HistorikkInnslag innslag : getHistorikkInnslag()) {
            if (innslag.getTypeKode().contains(type.getKode()) && (innslag.behandlingId() == behandlingsId)) {
                return true;
            }
        }
        return false;
    }

    public List<HistorikkInnslag> getHistorikkInnslag() {
        refreshBehandling();
        return get(historikkInnslag);
    }

    public HistorikkInnslag hentHistorikkinnslagAvType(HistorikkInnslag.Type type) {
        ventTilHistorikkinnslag(type);
        return historikkInnslag.get().stream()
                .filter(h -> h.type().kode.equalsIgnoreCase(type.getKode()))
                .findFirst()
                .orElseThrow();
    }

    public String hentDokumentIdFraHistorikkinnslag(HistorikkInnslag.Type type) {
        HistorikkInnslag historikkInnslag = hentHistorikkinnslagAvType(type);
        return historikkInnslag.dokumentLinks().get(0).dokumentId();
    }


    /*
     * Vilkar
     */
    private Vilkar hentVilkår(Kode vilkårKode) {
        for (Vilkar vilkår : valgtBehandling.getVilkar()) {
            if (vilkår.getVilkarType().equals(vilkårKode)) {
                return vilkår;
            }
        }
        throw new IllegalStateException(
                String.format("Fant ikke vilkår %s for behandling %s", vilkårKode.toString(), valgtBehandling.id));
    }

    private Vilkar hentVilkår(String vilkårKode) {
        return hentVilkår(new Kode("VILKAR_TYPE", vilkårKode));
    }

    public Kode vilkårStatus(String vilkårKode) {
        return hentVilkår(vilkårKode).getVilkarStatus();
    }

    @Step("Henter prosesstasker for behandling")
    private List<ProsessTaskListItemDto> hentProsesstaskerForBehandling(Behandling behandling) {
        SokeFilterDto filter = new SokeFilterDto(List.of(), LocalDateTime.now().minusMinutes(5), LocalDateTime.now());
        List<ProsessTaskListItemDto> prosesstasker = prosesstaskKlient.list(filter);
        return prosesstasker.stream().filter(p -> p.taskParametre().behandlingId().equalsIgnoreCase("" + behandling.id))
                .collect(Collectors.toList());
    }

    public boolean sakErKobletTilAnnenpart() {
        return getAnnenPartBehandling() != null;
    }

    private Behandling getAnnenPartBehandling() {
        return get(annenPartBehandling);
    }

    public void mellomlagreKlage() {
        behandlingerKlient.mellomlagre(
                new KlageVurderingResultatAksjonspunktMellomlagringDto(valgtBehandling,
                        hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP)));
        refreshBehandling();
    }


    /*
     * Risikovurderingsklient
     */
    @Step("Venter til risikovurdering har status: {status}")
    public void ventTilRisikoKlassefiseringsstatus(String status) {
        Vent.til(() -> {
            RisikovurderingResponse response = getRisikovurdering(valgtBehandling.uuid.toString());
            return harRisikoKlassefiseringsstatus(status, response);
        }, 30, "Feilet. Fikk ikke riktig status");
    }

    private RisikovurderingResponse getRisikovurdering(String uuid) {
        return risikovurderingKlient.getRisikovurdering(uuid);
    }
    private boolean harRisikoKlassefiseringsstatus(String status, RisikovurderingResponse responseDto) {
        return responseDto.risikoklasse().equalsIgnoreCase(status);
    }


    /* VERIFISERINGER */
    public boolean sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(BeregningsresultatPeriode beregningsresultatPeriode,
                                                                    String aktivitetskode) {
        return beregningsresultatPeriode.getAndeler().stream()
                .anyMatch(beregningsresultatPeriodeAndel -> beregningsresultatPeriodeAndel.getAktivitetStatus().kode
                        .equalsIgnoreCase(aktivitetskode));
    }

    public boolean sjekkOmDetErOpptjeningFremTilSkjæringstidspunktet(String aktivitet) {
        var skjaeringstidspunkt = valgtBehandling.behandlingsresultat.getSkjæringstidspunkt().getDato();
        for (var opptjening : valgtBehandling.getOpptjening().getOpptjeningAktivitetList()) {
            if (opptjening.getAktivitetType().kode.equalsIgnoreCase(aktivitet) &&
                    !opptjening.getOpptjeningTom().isBefore(skjaeringstidspunkt.minusDays(1))) {
                return true;
            }
        }
        return false;
    }

    public boolean sjekkOmYtelseLiggerTilGrunnForOpptjening(String ytelse) {
        for (var opptjening : valgtBehandling.getOpptjening().getOpptjeningAktivitetList()) {
            if (opptjening.getAktivitetType().kode.equalsIgnoreCase(ytelse)) {
                return true;
            }
        }
        return false;
    }

    public boolean verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(
            double prosentAvDagsatsTilArbeidsgiver) {
        for (var periode : valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()) {
            if (!verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(periode, prosentAvDagsatsTilArbeidsgiver)) {
                return false;
            }
        }
        return true;
    }

    public boolean verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(BeregningsresultatPeriode periode,
                                                                       double prosentAvDagsatsTilArbeidsgiver) {
        var prosentfaktor = prosentAvDagsatsTilArbeidsgiver / 100;
        var dagsats = periode.getDagsats();
        var forventetUtbetaltDagsatsTilArbeidsgiver = Math.round(dagsats * prosentfaktor);
        var forventetUtbetaltDagsatsTilSøker = Math.round(dagsats * (1 - prosentfaktor));
        List<Integer> utbetaltTilSøkerForAndeler = new ArrayList<>();
        List<Integer> utbetaltRefusjonForAndeler = new ArrayList<>();
        for (var andel : periode.getAndeler()) {
            utbetaltTilSøkerForAndeler.add(andel.getTilSoker());
            utbetaltRefusjonForAndeler.add(andel.getRefusjon());
        }
        if (utbetaltRefusjonForAndeler.stream().mapToInt(Integer::intValue)
                .sum() != forventetUtbetaltDagsatsTilArbeidsgiver) {
            return false;
        }
        return utbetaltTilSøkerForAndeler.stream().mapToInt(Integer::intValue).sum() == forventetUtbetaltDagsatsTilSøker;
    }

    private String hentInternArbeidsforholdId(String orgnummer) {
        return valgtBehandling.getInntektArbeidYtelse().getArbeidsforhold().stream()
                .filter(arbeidsforhold -> orgnummer.equalsIgnoreCase(arbeidsforhold.getArbeidsgiverReferanse()))
                .map(Arbeidsforhold::getArbeidsforholdId)
                .findFirst()
                .orElseThrow();
    }

    public boolean verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(String orgnummer,
                                                                                       double prosentAvDagsatsTilArbeidsgiver) {
        var prosentfaktor = prosentAvDagsatsTilArbeidsgiver / 100;
        var internArbeidsforholdID = hentInternArbeidsforholdId(orgnummer);
        for (var periode : valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()) {
            if (verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForPeriode(periode, internArbeidsforholdID,
                    prosentfaktor)) {
                return false;
            }
        }
        return true;
    }

    public boolean verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForPeriode(BeregningsresultatPeriode periode,
                                                                                   String internArbeidsforholdID,
                                                                                   double prosentfaktor) {
        var dagsats = periode.getDagsats();
        var forventetUtbetaltDagsatsTilArbeidsgiver = Math.round(dagsats * prosentfaktor);
        List<Integer> utbetaltRefusjonForAndeler = new ArrayList<>();
        for (var andel : periode.getAndeler()) {
            if ((andel.getArbeidsforholdId() != null) && andel.getArbeidsforholdId().equalsIgnoreCase(internArbeidsforholdID)) {
                utbetaltRefusjonForAndeler.add(andel.getRefusjon());
            }
        }
        return utbetaltRefusjonForAndeler.stream().mapToInt(Integer::intValue)
                .sum() != forventetUtbetaltDagsatsTilArbeidsgiver;
    }
}
