package no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak;

import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder.AUTO_KØET_BEHANDLING;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling.get;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugAksjonspunktbekreftelser;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugBehandlingsstatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidInntektsmeldingAksjonspunktÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidsforholdKomplettVurderingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Venteårsak;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.RisikovurderingKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingFpsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingHenlegg;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdVersjonDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingNy;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingPaVent;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftedeAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.Fagsystem;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.ArbeidInntektsmeldingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Vilkar;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeid.Arbeidsforhold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding.ManglendeOpplysningerVurderingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding.ManueltArbeidsforholdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriodeAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.BehandlingMedUttaksperioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.FagsakStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkFpsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.ProsesstaskFpsakKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.SafKlient;
import no.nav.foreldrepenger.autotest.util.vent.Lazy;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.kontrakter.risk.kodeverk.RisikoklasseType;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;

public class Saksbehandler {

    private final Logger LOG = LoggerFactory.getLogger(Saksbehandler.class);
    private final Aktoer.Rolle rolle;

    public Fagsak valgtFagsak;
    public Behandling valgtBehandling;
    public List<Behandling> behandlinger;
    protected Lazy<List<HistorikkInnslag>> historikkInnslag;
    protected Lazy<Behandling> annenPartBehandling;

    protected final SafKlient safKlient;
    protected final FagsakKlient fagsakKlient;
    protected final BehandlingFpsakKlient behandlingerKlient;
    protected final HistorikkFpsakKlient historikkKlient;
    protected final ProsesstaskFpsakKlient prosesstaskKlient;
    protected final RisikovurderingKlient risikovurderingKlient;

    public Saksbehandler() {
        this(Aktoer.Rolle.SAKSBEHANDLER);
    }

    protected Saksbehandler(Aktoer.Rolle rolle) {
        this.rolle = rolle;
        safKlient = new SafKlient(); // TODO: Flytte denne?
        fagsakKlient = new FagsakKlient();
        behandlingerKlient = new BehandlingFpsakKlient();
        historikkKlient = new HistorikkFpsakKlient();
        prosesstaskKlient = new ProsesstaskFpsakKlient();
        risikovurderingKlient = new RisikovurderingKlient();
    }

    /*
     * Fagsak
     */
    public Fagsak hentFagsak(Saksnummer saksnummer) {
        Aktoer.loggInn(rolle);
        return hentFagsakOgSisteBehandling(saksnummer);
    }

    private Fagsak hentFagsakOgSisteBehandling(Saksnummer saksnummer) {
        refreshFagsak(saksnummer);
        velgSisteBehandling();
        return valgtFagsak;
    }

    private void refreshFagsak(Saksnummer saksnummer) {
        valgtFagsak = fagsakKlient.hentFagsak(saksnummer);
    }

    public void ventTilFagsakAvsluttet() {
        ventTilFagsakstatus(FagsakStatus.AVSLUTTET);
    }

    public void ventTilFagsakLøpende() {
        ventTilFagsakstatus(FagsakStatus.LØPENDE);
    }

    private void ventTilFagsakstatus(FagsakStatus... status) {
        if (harFagsakstatus(status)) {
            return;
        }
        // TODO: Timeouts burde være så lav som mulig i pipeline, men veldig høy lokalt pga forskjellig typer PCer.
        Vent.til(() -> {
            refreshFagsak(valgtFagsak.saksnummer());
            return harFagsakstatus(status);
        }, 10, "Fagsak har ikke status " + Set.of(status));
    }

    private boolean harFagsakstatus(FagsakStatus... status) {
        return Set.of(status).contains(valgtFagsak.status());
    }

    /*
     * Behandling
     */
    public List<Behandling> hentAlleBehandlingerForFagsak(Saksnummer saksnummer) {
        return behandlingerKlient.alle(saksnummer);
    }


    public void velgSisteBehandling() {
        behandlinger = hentAlleBehandlingerForFagsak(valgtFagsak.saksnummer());
        valgtBehandling = behandlinger.stream()
                .max(Comparator.comparing(Behandling::getOpprettet))
                .orElseThrow(() -> new RuntimeException("Fant ingen behandlinger for saksnummer: " + valgtFagsak.saksnummer()));
        refreshBehandling();
    }

    public void ventPåOgVelgFørstegangsbehandling() {
        ventPåOgVelgSisteBehandling(BehandlingType.FØRSTEGANGSSØKNAD);
    }

    public void ventPåOgVelgFørstegangsbehandling(Integer antallBehandlingerSomMatcherType) {
        ventPåOgVelgSisteBehandling(BehandlingType.FØRSTEGANGSSØKNAD, null, null, antallBehandlingerSomMatcherType);
    }

    public void ventPåOgVelgÅpenFørstegangsbehandling() {
        ventPåOgVelgSisteBehandling(BehandlingType.FØRSTEGANGSSØKNAD, null, true);
    }

    public void ventPåOgVelgKlageBehandling() {
        ventPåOgVelgSisteBehandling(BehandlingType.KLAGE);
    }

    public void ventPåOgVelgRevurderingBehandling() {
        ventPåOgVelgSisteBehandling(BehandlingType.REVURDERING);
    }

    public void ventPåOgVelgRevurderingBehandling(BehandlingÅrsakType behandlingÅrsakType) {
        ventPåOgVelgSisteBehandling(BehandlingType.REVURDERING, behandlingÅrsakType, null, null);
    }

    public void ventPåOgVelgRevurderingBehandling(BehandlingÅrsakType behandlingÅrsakType, Integer antallBehandlingerSomMatcherType) {
        ventPåOgVelgSisteBehandling(BehandlingType.REVURDERING, behandlingÅrsakType, null, antallBehandlingerSomMatcherType);
    }

    public void ventPåOgVelgAnkeBehandling() {
        ventPåOgVelgSisteBehandling(BehandlingType.ANKE);
    }

    public boolean harRevurderingBehandling() {
        return harBehandling(BehandlingType.REVURDERING, null, null, null);
    }

    public void ventPåOgVelgDokumentInnsynBehandling() {
        ventPåOgVelgSisteBehandling(BehandlingType.INNSYN);
    }

    public void ventPåOgVelgSisteBehandling(BehandlingType behandlingstype) {
        ventPåOgVelgSisteBehandling(behandlingstype, null);
    }
    public void ventPåOgVelgSisteBehandling(BehandlingType behandlingstype, BehandlingÅrsakType behandlingÅrsakType) {
        ventPåOgVelgSisteBehandling(behandlingstype, behandlingÅrsakType, null, null);
    }

    private void ventPåOgVelgSisteBehandling(BehandlingType behandlingstype, BehandlingÅrsakType behandlingÅrsakType, Boolean åpenStatus) {
        ventPåOgVelgSisteBehandling(behandlingstype, behandlingÅrsakType, åpenStatus, null);
    }


    /**
     * For å sikre at vi venter på riktig behandling og behandlingen er ferdig prossessert for konsistens venter vi på følgende:
     *  1) Vi venter til det er opprettet en behandling ved forventet type, årsak, status og antall
     *  2) Hvis vi venter på en REVURDERING, vent til behandlingen er tatt av kø (AP 7011).
     *      - Hvis vi ikke gjør dette så kan punkt 3) returnere behandlingen mens den er på kø, uten at prosseseringen er ferdig
     *  3) Venter til enten behandling avsluttet eller det har oppstått et aksjonspunkt
     */
    @Step("Venter på at fagsak får behandlingstype {behandlingstype.kode} ")
    private void ventPåOgVelgSisteBehandling(BehandlingType behandlingstype, BehandlingÅrsakType behandlingÅrsakType,
                                             Boolean åpenStatus, Integer antallBehandlingerSomMatcherType) {
        LOG.info("Venter til fagsak {} har behandling av type {} {} ...", valgtFagsak.saksnummer().value(), behandlingstype,
                Optional.ofNullable(behandlingÅrsakType).map(Objects::toString).orElse(""));
        Objects.requireNonNull(valgtFagsak);

        // 1) Vi venter til det er opprettet en behandling ved forventet type, årsak, status og antall
        var behandling = Vent.på(() -> {
            var matchedeBehandlinger = hentAlleBehandlingerAvTypen(behandlingstype, behandlingÅrsakType, åpenStatus);
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

        // 2) Hvis vi venter på en REVURDERING og behandling er køet, men ikke gjennopptatt venter vi til AP 7011 er utført.
        if (BehandlingType.REVURDERING.equals(behandlingstype) && erBehandlingKøetOgIkkeGjenopptatt(behandling.uuid)) {
            Vent.til(() -> behandlingerKlient.hentAlleAksjonspunkter(behandling.uuid).stream()
                            .filter(aksjonspunkt -> aksjonspunkt.getDefinisjon().equals(AUTO_KØET_BEHANDLING))
                            .findFirst()
                            .orElseThrow()
                            .getStatus()
                            .equals("UTFO")
                    , 30, "Køet behandling er ikke gjenopptatt av fpsak!");
        }

        // 3) Venter til enten behandling avsluttet eller det har oppstått et aksjonspunkt
        venterPåFerdigProssesseringOgOppdaterBehandling(behandling);
        LOG.info("Behandling opprettet og oppdatert!");
    }

    private boolean erBehandlingKøetOgIkkeGjenopptatt(UUID behandlingsuuid) {
        return behandlingerKlient.hentAlleAksjonspunkter(behandlingsuuid).stream()
                .filter(aksjonspunkt -> aksjonspunkt.getDefinisjon().equals(AUTO_KØET_BEHANDLING))
                .anyMatch(Aksjonspunkt::erUbekreftet);
    }

    private Set<Behandling> hentAlleBehandlingerAvTypen(BehandlingType behandlingstype, BehandlingÅrsakType behandlingÅrsakType, Boolean åpenStatus) {
        return hentAlleBehandlingerForFagsak(valgtFagsak.saksnummer()).stream()
                .filter(b -> b.type.equals(behandlingstype))
                .filter(b -> behandlingÅrsakType == null || b.behandlingÅrsaker.stream()
                        .map(BehandlingÅrsak::getBehandlingArsakType)
                        .anyMatch(årsak -> årsak.equals(behandlingÅrsakType)))
                .filter(b -> åpenStatus == null || !b.status.equals(BehandlingStatus.AVSLUTTET))
                .collect(Collectors.toSet());
    }


    private boolean harBehandling(BehandlingType behandlingstype, BehandlingÅrsakType behandlingÅrsakType,
                                  Boolean åpenStatus, Integer antallBehandlingerSomMatcherType) {
        var matchedeBehandlinger = hentAlleBehandlingerAvTypen(behandlingstype, behandlingÅrsakType, åpenStatus);
        if (matchedeBehandlinger == null || matchedeBehandlinger.isEmpty()) {
            return false;
        }
        return antallBehandlingerSomMatcherType == null || antallBehandlingerSomMatcherType == matchedeBehandlinger.size();
    }

    /*
     * Behandlingsstatus
     */
    public void ventTilBehandlingsstatus(BehandlingStatus forventetStatus) {
        debugBehandlingsstatus(forventetStatus, valgtBehandling.uuid);
        venterPåFerdigProssesseringOgOppdaterBehandling(valgtBehandling.uuid);
        var behandlingsstatus = getBehandlingsstatus();
        if (forventetStatus.equals(behandlingsstatus)) {
            return;
        }
        throw new IllegalStateException(String.format("Behandlingsstatus for behandling %s på fagsak %s var ikke %s, men var %s",
                valgtBehandling.uuid, valgtFagsak.saksnummer(), forventetStatus, behandlingsstatus));
    }

    protected void refreshBehandling() {
        venterPåFerdigProssesseringOgOppdaterBehandling(valgtBehandling);
    }

    public boolean harBehandlingsstatus(BehandlingStatus status) {
        return getBehandlingsstatus().equals(status);
    }

    public BehandlingStatus getBehandlingsstatus() {
        return valgtBehandling.status;
    }

    private void venterPåFerdigProssesseringOgOppdaterBehandling(Behandling behandling) {
        venterPåFerdigProssesseringOgOppdaterBehandling(behandling.uuid);
    }

    private void venterPåFerdigProssesseringOgOppdaterBehandling(UUID behandlingsuuid) {
        // Sjekker om behandlingen prosesserer. Siden vi vil vente på at den er ferdig for å få den siste
        // behandling.versjon. Og å hindre at tester henter data fra behandlingen som kan endre seg ettersom
        // behandlingen ikke har stoppet opp

        valgtBehandling = ventTilBehandlingErFerdigProsessertOgReturner(behandlingsuuid);

        // TODO: fiks dette!
        oppdaterLazyFelterForBehandling();

        LOG.info("Behandling {} er prossessert og valgt!", valgtBehandling.uuid);
    }

    private void oppdaterLazyFelterForBehandling() {
        populateBehandling(valgtBehandling);
        this.historikkInnslag = new Lazy<>(() -> historikkKlient.hentHistorikk(valgtFagsak.saksnummer()));
        this.annenPartBehandling = new Lazy<>(() -> behandlingerKlient.annenPartBehandling(valgtFagsak.saksnummer()));
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
        return prosesstaskKlient.alleProsesstaskPåBehandling().stream()
                .filter(p -> Objects.equals(behandlingsuuid.toString(), p.getTaskParametre().getProperty("behandlingId")))
                .toList();
    }

    private void populateBehandling(Behandling behandling) {
        behandling.setAksjonspunkter(new Lazy<>(() -> behandlingerKlient.hentAlleAksjonspunkter(behandling.uuid)));
        behandling.setVilkar(new Lazy<>(() -> behandlingerKlient.behandlingVilkår(behandling.uuid)));

        if (behandling.type.equals(BehandlingType.INNSYN)) {
            // Gjør ingenting
        } else if (List.of(BehandlingType.KLAGE, BehandlingType.ANKE).contains(behandling.type)) {
            behandling.setKlagevurdering(new Lazy<>(() -> behandlingerKlient.klage(behandling.uuid)));
        } else {
            behandling.setBeregningsgrunnlag(new Lazy<>(() -> behandlingerKlient.behandlingBeregningsgrunnlag(behandling.uuid)));
            behandling.setBeregningResultatEngangsstonad(new Lazy<>(() -> behandlingerKlient.behandlingBeregningsresultatEngangsstønad(behandling.uuid)));
            behandling.setBeregningResultatForeldrepenger(new Lazy<>(() -> behandlingerKlient.behandlingBeregningsresultatForeldrepenger(behandling.uuid)));
            behandling.setFeriepengegrunnlag(new Lazy<>(() -> behandlingerKlient.behandlingFeriepengegrunnlag(behandling.uuid)));
            behandling.setInntektArbeidYtelse(new Lazy<>(() -> behandlingerKlient.behandlingInntektArbeidYtelse(behandling.uuid)));
            behandling.setKontrollerAktivitetskrav(new Lazy<>(() -> behandlingerKlient.behandlingKontrollerAktivitetskrav(behandling.uuid)));
            behandling.setKontrollerFaktaData(new Lazy<>(() -> behandlingerKlient.behandlingKontrollerFaktaPerioder(behandling.uuid)));
            behandling.setMedlem(new Lazy<>(() -> behandlingerKlient.behandlingMedlemskap(behandling.uuid)));
            behandling.setOpptjening(new Lazy<>(() -> behandlingerKlient.behandlingOpptjening(behandling.uuid)));
            behandling.setSaldoer(new Lazy<>(() -> behandlingerKlient.behandlingUttakStonadskontoer(behandling.uuid)));
            behandling.setSoknad(new Lazy<>(() -> behandlingerKlient.behandlingSøknad(behandling.uuid)));
            behandling.setTilrettelegging(new Lazy<>(() -> behandlingerKlient.behandlingTilrettelegging(behandling.uuid)));
            behandling.setArbeidInntektsmelding(new Lazy<>(() -> behandlingerKlient.behandlingArbeidInntektsmelding(behandling.uuid)));
            behandling.setUttakResultatPerioder(new Lazy<>(() -> behandlingerKlient.behandlingUttakResultatPerioder(behandling.uuid)));
        }
    }

    /*
     * Henlegg behandling eller sett på vent
     */
    @Step("Setter behandling på vent")
    public void settBehandlingPåVent(LocalDate frist, Venteårsak årsak) {
        behandlingerKlient.settPaVent(new BehandlingPaVent(valgtBehandling, frist, årsak));
        ventTilHistorikkinnslag(HistorikkinnslagType.BEH_VENT);
    }

    @Step("Gjenopptar Behandling")
    public void gjenopptaBehandling() {
        valgtBehandling = behandlingerKlient.gjenoppta(new BehandlingIdVersjonDto(valgtBehandling));
        ventTilHistorikkinnslag(HistorikkinnslagType.BEH_GJEN);
    }

    @Step("Henlegger behandling")
    public void henleggBehandling(BehandlingResultatType årsak) {
        behandlingerKlient.henlegg(new BehandlingHenlegg(valgtBehandling.uuid, valgtBehandling.versjon, årsak, "Henlagt"));
        ventTilHistorikkinnslag(HistorikkinnslagType.AVBRUTT_BEH);
    }

    /*
     * Opretter behandling på nåværende fagsak
     */

    public void opprettBehandlingRevurdering(BehandlingÅrsakType årsak) {
        opprettBehandling(BehandlingType.REVURDERING, årsak);
    }

    public void oprettBehandlingInnsyn(BehandlingÅrsakType årsak) {
        opprettBehandling(BehandlingType.INNSYN, årsak);
    }


    public void opprettBehandling(BehandlingType behandlingstype, BehandlingÅrsakType årsak) {
        opprettBehandling(behandlingstype, årsak, valgtFagsak);
    }

    @Step("Oppretter {behandlingstype} på fagsak med saksnummer: {fagsak.saksnummer}")
    private void opprettBehandling(BehandlingType behandlingstype, BehandlingÅrsakType årsak, Fagsak fagsak) {
        if (behandlingstype.equals(BehandlingType.FØRSTEGANGSSØKNAD)) {
            throw new UnsupportedOperationException("Ikke implementert enda. Gi beskjed om det er ønskelig.");
        }

        LOG.info("Oppretter behandling {} {} på fagsak {} ...", behandlingstype, årsak, fagsak.saksnummer().value());
        valgtBehandling = behandlingerKlient.opprettBehandlingManuelt(new BehandlingNy(fagsak.saksnummer(), behandlingstype, årsak));
        oppdaterLazyFelterForBehandling();
        LOG.info("Behandling {}|{} opprettet", behandlingstype, årsak);
    }

    /*
     * Henting av uttaksperidoer
     */
    public List<UttakResultatPeriode> hentInnvilgedeUttaksperioder() {
        return valgtBehandling.hentUttaksperioder().stream()
                .filter(p -> PeriodeResultatType.INNVILGET.equals(p.getPeriodeResultatType()))
                .toList();
    }

    public List<UttakResultatPeriode> hentAvslåtteUttaksperioder() {
        return valgtBehandling.hentUttaksperioder().stream()
                .filter(p -> PeriodeResultatType.AVSLÅTT.equals(p.getPeriodeResultatType()))
                .toList();
    }


    /*
     * Henting av Saldo
     */
    public Saldoer hentSaldoerGittUttaksperioder(List<UttakResultatPeriode> uttakResultatPerioder) {
        BehandlingMedUttaksperioderDto behandlingMedUttaksperioderDto = new BehandlingMedUttaksperioderDto();
        behandlingMedUttaksperioderDto.setPerioder(uttakResultatPerioder);
        behandlingMedUttaksperioderDto.setBehandlingUuid(valgtBehandling.uuid);

        return behandlingerKlient.behandlingUttakStonadskontoerGittUttaksperioder(behandlingMedUttaksperioderDto);
    }

    /*
     * Henting av Beregningsresultat
     */
    public List<BeregningsresultatPeriodeAndel> hentBeregningsresultatPerioderMedAndelIArbeidsforhold(
            ArbeidsgiverIdentifikator arbeidsgiverIdentifikator) {
        return valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().stream()
                .flatMap(beregningsresultatPeriode -> beregningsresultatPeriode.getAndeler().stream())
                .filter(andeler -> arbeidsgiverIdentifikator.value().equalsIgnoreCase(andeler.getArbeidsgiverReferanse()))
                .sorted(Comparator.comparing(BeregningsresultatPeriodeAndel::getSisteUtbetalingsdato))
                .toList();
    }

    public List<BeregningsresultatPeriodeAndel> hentBeregningsresultatPerioderMedAndelISN() {
        return valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().stream()
                .flatMap(beregningsresultatPeriode -> beregningsresultatPeriode.getAndeler().stream())
                .filter(a -> a.getAktivitetStatus().equals(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .toList();
    }

    public Set<AktivitetStatus> hentUnikeBeregningAktivitetStatus() {
        return valgtBehandling.getBeregningResultatForeldrepenger().getPerioder().stream()
                .flatMap(p -> p.getAndeler().stream())
                .map(BeregningsresultatPeriodeAndel::getAktivitetStatus)
                .collect(Collectors.toSet());
    }

    /**
     * AKSJONSPUNKT
     */

    public <T extends AksjonspunktBekreftelse> T hentAksjonspunktbekreftelse(Class<T> type) {
        var aksjonspunktKode = type.getDeclaredAnnotation(BekreftelseKode.class).kode();
        LOG.info("Henter aksjonspunktbekreftelse for {} ({})", aksjonspunktKode, type.getSimpleName());
        return hentAksjonspunktbekreftelse(aksjonspunktKode);
    }

    private <T extends AksjonspunktBekreftelse> T hentAksjonspunktbekreftelse(String kode) {
        var aksjonspunkt = hentAksjonspunkt(kode);
        var bekreftelse = aksjonspunkt.getBekreftelse(Fagsystem.FPSAK);
        bekreftelse.oppdaterMedDataFraBehandling(valgtFagsak, valgtBehandling);
        return (T) bekreftelse;
    }

    /*
     * Henter aksjonspunkt av gitt kode
     */
    public Aksjonspunkt hentAksjonspunkt(String kode) {
        return valgtBehandling.getAksjonspunkter().stream()
                .filter(ap -> ap.getDefinisjon().equals(kode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Fant ikke aksjonspunkt med kode " + kode + "." +
                        "\nAksjonspunkt på behandling: " + valgtBehandling.getAksjonspunkter().toString()));
    }



    /*
     * Sjekker om aksjonspunkt av gitt kode er på behandlingen
     */
    public boolean harAksjonspunkt(String kode) {
        try {
            return hentAksjonspunkt(kode) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Bekrefte aksjonspunkt bekreftelse
     */
    public <T extends AksjonspunktBekreftelse> void bekreftAksjonspunktMedDefaultVerdier(Class<T> type) {
        bekreftAksjonspunkt(hentAksjonspunktbekreftelse(type));
    }

    public void bekreftAksjonspunkt(AksjonspunktBekreftelse bekreftelse) {
        bekreftAksjonspunktbekreftelserer(List.of(bekreftelse));
        LOG.info("Aksjonspunktbekreftelse for {} er sendt inn og AP er løst", bekreftelse.kode());
    }

    public void bekreftAksjonspunktbekreftelserer(List<AksjonspunktBekreftelse> bekreftelser) {
        debugAksjonspunktbekreftelser(bekreftelser, valgtBehandling.uuid);
        var aksjonspunkter = new BekreftedeAksjonspunkter(valgtBehandling.uuid, valgtBehandling.versjon, bekreftelser);
        behandlingerKlient.postBehandlingAksjonspunkt(aksjonspunkter);
        refreshBehandling();
        verifsierAPErHåndtert(bekreftelser);
    }

    private void verifsierAPErHåndtert(List<AksjonspunktBekreftelse> bekreftelser) {
        for (var bekreftelse : bekreftelser) {
            if (bekreftelse instanceof FatterVedtakBekreftelse f && f.harAvvisteAksjonspunkt()) {
                verifiserAtAPErOpprettetPåNytt(f);
            } else {
                verifsierAtAPErFerdigbehandlet(bekreftelse);
            }
        }
    }

    private void verifsierAtAPErFerdigbehandlet(AksjonspunktBekreftelse bekreftelse) {
        var ap = valgtBehandling.getAksjonspunkter().stream()
                .filter(aksjonspunkt -> aksjonspunkt.getDefinisjon().equalsIgnoreCase(bekreftelse.kode()))
                .findFirst()
                .orElseThrow(); // Vil ikke inntreffe ettersom hentAksjonspunkt() vil alltid bli kalt først.
        if (!ap.getStatus().equalsIgnoreCase("UTFO")) {
            throw new RuntimeException("AP bekreftelse er sendt inn programatisk for AP [" + bekreftelse.kode() +
                    "] uten at det løste AP. Forventet status på AP er UTFO, men er [" + ap.getStatus() + "]");
        }
    }

    private void verifiserAtAPErOpprettetPåNytt(FatterVedtakBekreftelse bekreftelse) {
        var avvisteAksjonspunktkoder = bekreftelse.avvisteAksjonspunkt();
        for (var kode : avvisteAksjonspunktkoder) {
            var AP = hentAksjonspunkt(kode);
            if (!AP.getStatus().equalsIgnoreCase("OPPR")) {
                throw new RuntimeException("AP [" + bekreftelse.kode() + "] skal være avvist av beslutter og " +
                        "opprettet nytt, men har status [" + AP.getStatus() + "]");
            }
        }
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

    public void ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet() {
        ventTilAvsluttetBehandling();
        ventTilFagsakstatus(FagsakStatus.LØPENDE, FagsakStatus.AVSLUTTET);
    }


    /*
     * Historikkinnslag
     */
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

    public boolean harHistorikkinnslagPåBehandling(HistorikkinnslagType type) {
        return harHistorikkinnslagPåBehandling(type, valgtBehandling.uuid);
    }

    public boolean harHistorikkinnslagPåBehandling(HistorikkinnslagType type, UUID behandlingsId) {
        if (List.of(HistorikkinnslagType.VEDLEGG_MOTTATT, HistorikkinnslagType.REVURD_OPPR).contains(type)) {
            behandlingsId = null;
        }
        return hentHistorikkinnslagPåBehandling(behandlingsId).stream()
                .anyMatch(innslag -> type.equals(innslag.type()));
    }

    public HistorikkInnslag hentHistorikkinnslagAvType(HistorikkinnslagType type) {
        return hentHistorikkinnslagAvType(type, valgtBehandling.uuid);
    }

    public HistorikkInnslag hentHistorikkinnslagAvType(HistorikkinnslagType type, UUID behandlingsId) {
        venterPåFerdigProssesseringOgOppdaterBehandling(behandlingsId);
        if (List.of(HistorikkinnslagType.VEDLEGG_MOTTATT, HistorikkinnslagType.REVURD_OPPR).contains(type)) {
            behandlingsId = null;
        }
        return hentHistorikkinnslagPåBehandling(behandlingsId).stream()
                .filter(innslag -> type.equals(innslag.type()))
                .findFirst()
                .orElse(null);
    }

    public void ventTilHistorikkinnslag(HistorikkinnslagType type) {
        Vent.til(() -> harHistorikkinnslagPåBehandling(type),
                45, () -> "Saken  hadde ikke historikkinslag " + type + "\nHistorikkInnslag:"
                        + String.join("\t\n", String.valueOf(hentHistorikkinnslagPåBehandling())));
    }

    /*
     * Vilkar
     */
    private Vilkar hentVilkår(String vilkårKode) {
        for (Vilkar vilkår : valgtBehandling.getVilkar()) {
            if (vilkår.getVilkarType().equals(vilkårKode)) {
                return vilkår;
            }
        }
        throw new IllegalStateException("Fant ikke vilkår " + vilkårKode + "for behandling " + valgtBehandling.uuid);
    }

    public String vilkårStatus(String vilkårKode) {
        return hentVilkår(vilkårKode).getVilkarStatus();
    }

    public boolean sakErKobletTilAnnenpart() {
        return getAnnenPartBehandling() != null;
    }

    private Behandling getAnnenPartBehandling() {
        return get(annenPartBehandling);
    }


    /*
     * Risikovurderingsklient
     */
    public void ventTilRisikoKlassefiseringsstatus(RisikoklasseType forventetRisikoklasse) {
        Vent.til(() -> {
            var response = risikovurderingKlient.getRisikovurdering(valgtBehandling.uuid);
            return response.risikoklasse().equals(forventetRisikoklasse);
        }, 45, "Har ikke riktig risikoklassifiseringsstatus");
    }

    /* VERIFISERINGER */
    public boolean sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(BeregningsresultatPeriode beregningsresultatPeriode,
                                                                    AktivitetStatus aktivitetskode) {
        return beregningsresultatPeriode.getAndeler().stream()
                .anyMatch(p -> p.getAktivitetStatus().equals(aktivitetskode));
    }

    public boolean sjekkOmDetErOpptjeningFremTilSkjæringstidspunktet(String aktivitet) {
        var skjaeringstidspunkt = valgtBehandling.behandlingsresultat.getSkjæringstidspunkt().getDato();
        for (var opptjening : valgtBehandling.getOpptjening().getOpptjeningAktivitetList()) {
            if (opptjening.getAktivitetType().equalsIgnoreCase(aktivitet) &&
                    !opptjening.getOpptjeningTom().isBefore(skjaeringstidspunkt.minusDays(1))) {
                return true;
            }
        }
        return false;
    }

    public boolean sjekkOmYtelseLiggerTilGrunnForOpptjening(String ytelse) {
        for (var opptjening : valgtBehandling.getOpptjening().getOpptjeningAktivitetList()) {
            if (opptjening.getAktivitetType().equalsIgnoreCase(ytelse)) {
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

    private String hentInternArbeidsforholdId(ArbeidsgiverIdentifikator arbeidsgiverIdentifikator) {
        return valgtBehandling.getInntektArbeidYtelse().getArbeidsforhold().stream()
                .filter(arbeidsforhold -> arbeidsgiverIdentifikator.equals(arbeidsforhold.getArbeidsgiverReferanse()))
                .map(Arbeidsforhold::getArbeidsforholdId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Fant ingen interne arbeidforhold med orgnummer " + arbeidsgiverIdentifikator));
    }

    public boolean verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(ArbeidsgiverIdentifikator arbeidsgiverIdentifikator,
                                                                                       double prosentAvDagsatsTilArbeidsgiver) {
        var prosentfaktor = prosentAvDagsatsTilArbeidsgiver / 100;
        var internArbeidsforholdID = hentInternArbeidsforholdId(arbeidsgiverIdentifikator);
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

    public Behandling førstegangsbehandling() {
        return behandlinger.stream().filter(b -> b.type.equals(BehandlingType.FØRSTEGANGSSØKNAD)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Finner ikke førstegangsbehandling"));
    }

    public void lagreArbeidsforholdValg(ManglendeOpplysningerVurderingDto manglendeOpplysningerVurderingDto) {
        behandlingerKlient.behandlingArbeidInntektsmeldingLagreValg(manglendeOpplysningerVurderingDto);
        refreshBehandling();
    }

    public void lagreOpprettetArbeidsforhold(ManueltArbeidsforholdDto manueltArbeidsforholdDto) {
        behandlingerKlient.behandlingArbeidInntektsmeldingLagreArbfor(manueltArbeidsforholdDto);
        refreshBehandling();
    }

    public void åpneForNyArbeidsforholdVurdering(BehandlingIdDto behandlingIdDto) {
        behandlingerKlient.behandlingArbeidInntektsmeldingNyVurdering(behandlingIdDto);
        refreshBehandling();
    }

    public void fortsettUteninntektsmeldinger() {
        var arbforSomManglerIM = valgtBehandling.getArbeidOgInntektsmeldingDto()
                .arbeidsforhold()
                .stream()
                .filter(a -> a.årsak().equals(ArbeidInntektsmeldingAksjonspunktÅrsak.MANGLENDE_INNTEKTSMELDING))
                .toList();
        var dtoer = arbforSomManglerIM.stream()
                .map(arbfor -> new ManglendeOpplysningerVurderingDto(valgtBehandling.uuid,
                        ArbeidsforholdKomplettVurderingType.FORTSETT_UTEN_INNTEKTSMELDING, "Dette er en begrunnelse",
                        arbfor.arbeidsgiverIdent(), arbfor.internArbeidsforholdId()))
                .toList();
        dtoer.forEach(this::lagreArbeidsforholdValg);
        var ab = hentAksjonspunktbekreftelse(ArbeidInntektsmeldingBekreftelse.class);
        bekreftAksjonspunkt(ab);
    }

    /* SAF */
    public byte[] hentJournalførtDokument(String dokumentId, String variantFormat) {
        return safKlient.hentDokumenter(null, dokumentId, variantFormat);
    }
}
