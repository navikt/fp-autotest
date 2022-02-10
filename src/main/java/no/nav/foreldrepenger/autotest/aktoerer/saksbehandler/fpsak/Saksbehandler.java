package no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak;

import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling.get;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugAksjonspunktbekreftelser;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugBehandlingsstatus;
import static no.nav.vedtak.log.mdc.MDCOperations.MDC_CONSUMER_ID;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.MDC;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidInntektsmeldingAksjonspunktÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.ArbeidsforholdKomplettVurderingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.FagsakStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Kode;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Venteårsak;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.RisikovurderingJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingerJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingHenlegg;
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
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.ArbeidInntektsmeldingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Vilkar;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeid.Arbeidsforhold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding.ManglendeOpplysningerVurderingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding.ManueltArbeidsforholdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriodeAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.BehandlingMedUttaksperioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.ProsesstaskJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.ProsessTaskListItemDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.SokeFilterDto;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.vent.Lazy;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.kontrakter.risk.kodeverk.RisikoklasseType;
import no.nav.vedtak.log.mdc.MDCOperations;

public class Saksbehandler extends Aktoer {

    public Fagsak valgtFagsak;
    public Behandling valgtBehandling;
    public List<Behandling> behandlinger;

    private Lazy<List<HistorikkInnslag>> historikkInnslag;
    private Lazy<Behandling> annenPartBehandling;

    private final FagsakJerseyKlient fagsakKlient;
    private final BehandlingerJerseyKlient behandlingerKlient;
    private final HistorikkJerseyKlient historikkKlient;
    private final ProsesstaskJerseyKlient prosesstaskKlient;
    private final RisikovurderingJerseyKlient risikovurderingKlient;


    public Saksbehandler(Rolle rolle) {
        super(rolle);
        fagsakKlient = new FagsakJerseyKlient(cookieRequestFilter);
        behandlingerKlient = new BehandlingerJerseyKlient(cookieRequestFilter);
        historikkKlient = new HistorikkJerseyKlient(cookieRequestFilter);
        prosesstaskKlient = new ProsesstaskJerseyKlient(cookieRequestFilter);
        risikovurderingKlient = new RisikovurderingJerseyKlient(cookieRequestFilter);
    }

    /*
     * Fagsak
     */
    public void hentFagsak(long saksnummer) {
        hentFagsak("" + saksnummer);
    }

    private void hentFagsak(String saksnummer) {
        MDCOperations.putToMDC(MDC_CONSUMER_ID, MDC.get(saksnummer));
        valgtFagsak = fagsakKlient.getFagsak(saksnummer);
        if (valgtFagsak == null) {
            throw new RuntimeException("Finner ikke fagsak på saksnummer " + saksnummer);
        }
        behandlinger = hentAlleBehandlingerForFagsak(valgtFagsak.saksnummer());
        velgSisteBehandling();
    }

    public void ventTilFagsakAvsluttet() {
        ventTilFagsakstatus(FagsakStatus.AVSLUTTET);
    }

    public void ventTilFagsakLøpende() {
        ventTilFagsakstatus(FagsakStatus.LØPENDE);
    }

    private void ventTilFagsakstatus(FagsakStatus status) {
        if (harFagsakstatus(status)) {
            return;
        }
        Vent.til(() -> {
            refreshFagsak();
            return harFagsakstatus(status);
        }, 10, "Fagsak har ikke status [" + status + "]");
    }

    private boolean harFagsakstatus(FagsakStatus status) {
        return valgtFagsak.status().equals(status);
    }

    private void refreshFagsak() {
        hentFagsak(valgtFagsak.saksnummer().toString());
    }

    /*
     * Behandling
     */
    public List<Behandling> hentAlleBehandlingerForFagsak(long saksnummer) {
        return behandlingerKlient.alle(saksnummer);
    }

    public void velgSisteBehandling() {
        var behandling = hentAlleBehandlingerForFagsak(valgtFagsak.saksnummer()).stream()
                .max(Comparator.comparing(b -> b.opprettet))
                .orElseThrow(() -> new RuntimeException("Fant ingen behandlinger for saksnummer: " + valgtFagsak.saksnummer()));
        velgBehandling(behandling);
    }

    public void ventPåOgVelgFørstegangsbehandling() {
        ventPåOgVelgBehandling(BehandlingType.FØRSTEGANGSSØKNAD);
    }

    public void ventPåOgVelgÅpenFørstegangsbehandling() {
        ventPåOgVelgBehandling(BehandlingType.FØRSTEGANGSSØKNAD, true);
    }

    public void ventPåOgVelgKlageBehandling() {
        ventPåOgVelgBehandling(BehandlingType.KLAGE);
    }

    public void ventPåOgVelgRevurderingBehandling() {
        ventPåOgVelgBehandling(BehandlingType.REVURDERING);
    }

    public void ventPåOgVelgAnkeBehandling() {
        ventPåOgVelgBehandling(BehandlingType.ANKE);
    }

    public boolean harRevurderingBehandling() {
        return harBehandling(BehandlingType.REVURDERING);
    }

    public void ventPåOgVelgDokumentInnsynBehandling() {
        ventPåOgVelgBehandling(BehandlingType.INNSYN);
    }

    private void ventPåOgVelgBehandling(BehandlingType behandlingstype) {
        ventPåOgVelgBehandling(behandlingstype, false);
    }

    @Step("Venter på at fagsak får behandlingstype {behandlingstype.kode} ")
    private void ventPåOgVelgBehandling(BehandlingType behandlingstype, boolean åpenStatus) {
        ventTilSakHarBehandling(behandlingstype);
        var behandling = behandlinger.stream()
                .filter(b -> b.type.equals(behandlingstype))
                .filter(b -> !åpenStatus || !b.status.equals(BehandlingStatus.AVSLUTTET))
                .findFirst();
        behandling.ifPresent(this::velgBehandling);
    }


    private void ventTilSakHarBehandling(BehandlingType behandlingType) {
        if (harBehandling(behandlingType)) {
            return;
        }
        Vent.til(() -> harBehandling(behandlingType), 30, "Saken har ikke fått behandling av type: " + behandlingType);
    }

    private boolean harBehandling(BehandlingType behandlingType) {
        refreshFagsak();
        for (Behandling behandling : behandlinger) {
            if (behandling.type.equals(behandlingType)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Behandlingsstatus
     */
    public void ventTilBehandlingsstatus(BehandlingStatus status) {
        debugBehandlingsstatus(status, valgtBehandling.uuid);
        if (harBehandlingsstatus(status)) {
            return;
        }
        Vent.til(() -> {
            refreshBehandling();
            return harBehandlingsstatus(status);
        }, 60, "Behandlingsstatus var ikke " + status + " men var " + getBehandlingsstatus() + " i sak: "
                + valgtFagsak.saksnummer());
    }

    private void refreshBehandling() {
        velgBehandling(valgtBehandling);
    }

    public boolean harBehandlingsstatus(BehandlingStatus status) {
        return getBehandlingsstatus().equals(status);
    }

    public BehandlingStatus getBehandlingsstatus() {
        return valgtBehandling.status;
    }


    private void velgBehandling(Behandling behandling) {
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
            return "Behandling status var ikke klar men har ikke feilet\n" + prosessTaskList;
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
                return false;
            }
        } else if (status.isPending()) {
            return false;
        } else {
            throw new RuntimeException("Status for behandling " + behandling.uuid + " feilet: " + status.getMessage());
        }
    }

    private void populateBehandling(Behandling behandling) {
        behandling.setAksjonspunkter(new Lazy<>(() -> behandlingerKlient.getBehandlingAksjonspunkt(behandling.uuid)));
        behandling.setVilkar(new Lazy<>(() -> behandlingerKlient.behandlingVilkår(behandling.uuid)));

        if (behandling.type.equals(BehandlingType.INNSYN)) {
            // Gjør ingenting
        } else if (List.of(BehandlingType.KLAGE, BehandlingType.ANKE).contains(behandling.type)) {
            behandling.setKlagevurdering(new Lazy<>(() -> behandlingerKlient.klage(behandling.uuid)));
        } else {
            behandling.setBeregningsgrunnlag(new Lazy<>(() -> behandlingerKlient.behandlingBeregningsgrunnlag(behandling.uuid)));
            behandling.setBeregningResultatEngangsstonad(new Lazy<>(() -> behandlingerKlient.behandlingBeregningsresultatEngangsstønad(behandling.uuid)));
            behandling.setBeregningResultatForeldrepenger(new Lazy<>(() -> behandlingerKlient.behandlingBeregningsresultatForeldrepenger(behandling.uuid)));
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
        refreshBehandling();
    }

    @Step("Gjenopptar Behandling")
    public void gjenopptaBehandling() {
        behandlingerKlient.gjenoppta(new BehandlingIdPost(valgtBehandling));
        refreshBehandling();
    }

    @Step("Henlegger behandling")
    public void henleggBehandling(BehandlingResultatType årsak) {
        behandlingerKlient.henlegg(new BehandlingHenlegg(valgtBehandling.uuid, valgtBehandling.versjon, årsak, "Henlagt"));
        refreshBehandling();
    }

    /*
     * Opretter behandling på nåværende fagsak
     */
    public void opprettBehandling(BehandlingType behandlingstype, BehandlingÅrsakType årsak) {
        opprettBehandling(behandlingstype, årsak, valgtFagsak);
        hentFagsak(valgtFagsak.saksnummer().toString());
    }

    @Step("Oppretter {behandlingstype} på fagsak med saksnummer: {fagsak.saksnummer}")
    private void opprettBehandling(BehandlingType behandlingstype, BehandlingÅrsakType årsak, Fagsak fagsak) {
        behandlingerKlient.putBehandlinger(new BehandlingNy(fagsak.saksnummer(), behandlingstype, årsak));
    }

    public void opprettBehandlingRevurdering(BehandlingÅrsakType årsak) {
        opprettBehandling(BehandlingType.REVURDERING, årsak);
    }

    public void oprettBehandlingInnsyn(BehandlingÅrsakType årsak) {
        opprettBehandling(BehandlingType.INNSYN, årsak);
    }

    /*
     * Henting av uttaksperidoer
     */
    public List<UttakResultatPeriode> hentAvslåtteUttaksperioder() {
        return valgtBehandling.hentUttaksperioder().stream()
                .filter(p -> p.getPeriodeResultatType().equals(PeriodeResultatType.AVSLÅTT))
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

    /*
     * Henter aksjonspunkt bekreftelse av gitt klasse
     */

    public <T extends AksjonspunktBekreftelse> T hentAksjonspunktbekreftelse(Class<T> type) {
        var aksjonspunktKode = type.getDeclaredAnnotation(BekreftelseKode.class).kode();
        return hentAksjonspunktbekreftelse(aksjonspunktKode);
    }

    public <T extends AksjonspunktBekreftelse> T hentAksjonspunktbekreftelse(String kode) {
        var aksjonspunkt = hentAksjonspunkt(kode);
        var bekreftelse = aksjonspunkt.getBekreftelse();
        bekreftelse.oppdaterMedDataFraBehandling(valgtFagsak, valgtBehandling);
        return (T) bekreftelse;
    }

    /*
     * Henter aksjonspunkt av gitt kode
     */
    public Aksjonspunkt hentAksjonspunkt(String kode) {
        return valgtBehandling.getAksjonspunkter().stream()
                .filter(ap -> ap.getDefinisjon().kode.equals(kode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Fant ikke aksjonspunkt med kode " + kode + "." +
                        "\nAksjonspunkt på behanlding: " + valgtBehandling.getAksjonspunkter().toString()));
    }

    public List<Aksjonspunkt> hentAksjonspunktSomSkalTilTotrinnsBehandling() {
        return valgtBehandling.getAksjonspunkter().stream()
                .filter(Aksjonspunkt::skalTilToTrinnsBehandling)
                .toList();
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
    }

    public void bekreftAksjonspunktbekreftelserer(AksjonspunktBekreftelse... bekreftelser) {
        bekreftAksjonspunktbekreftelserer(Arrays.asList(bekreftelser));
    }

    public void bekreftAksjonspunktbekreftelserer(List<AksjonspunktBekreftelse> bekreftelser) {
        debugAksjonspunktbekreftelser(bekreftelser, valgtBehandling.uuid);
        BekreftedeAksjonspunkter aksjonspunkter = new BekreftedeAksjonspunkter(valgtBehandling, bekreftelser);
        behandlingerKlient.postBehandlingAksjonspunkt(aksjonspunkter);
        refreshBehandling();
        verifsierAP(bekreftelser);
    }

    private void verifsierAP(List<AksjonspunktBekreftelse> bekreftelser) {
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
                .filter(aksjonspunkt -> aksjonspunkt.getDefinisjon().kode.equalsIgnoreCase(bekreftelse.kode()))
                .findFirst()
                .orElseThrow(); // Vil ikke inntreffe ettersom hentAksjonspunkt() vil alltid bli kalt først.
        if (!ap.getStatus().kode.equalsIgnoreCase("UTFO")) {
            throw new RuntimeException("AP bekreftelse er sendt inn programatisk for AP [" + bekreftelse.kode() +
                    "] uten at det løste AP. Forventet status på AP er UTFO, men er [" + ap.getStatus().kode + "]");
        }
    }

    private void verifiserAtAPErOpprettetPåNytt(FatterVedtakBekreftelse bekreftelse) {
        var avvisteAksjonspunktkoder = bekreftelse.avvisteAksjonspunkt();
        for (var kode : avvisteAksjonspunktkoder) {
            var AP = hentAksjonspunkt(kode);
            if (!AP.getStatus().kode.equalsIgnoreCase("OPPR")) {
                throw new RuntimeException("AP [" + bekreftelse.kode() + "] skal være avvist av beslutter og " +
                        "opprettet nytt, men har status [" + AP.getStatus().kode + "]");
            }
        }
    }

    public void fattVedtakOgVentTilAvsluttetBehandling(FatterVedtakBekreftelse bekreftelse) {
        bekreftAksjonspunkt(bekreftelse);
        ventTilAvsluttetBehandling();
    }

    public void fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling() {
        bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelseUtenTotrinn.class);
        ventTilAvsluttetBehandling();
    }

    public void ventTilAvsluttetBehandling() {
        ventTilBehandlingsstatus(BehandlingStatus.AVSLUTTET);
    }


    /*
     * Oversyring
     */
    public void overstyr(AksjonspunktBekreftelse bekreftelse) {
        List<AksjonspunktBekreftelse> bekreftelser = new ArrayList<>();
        bekreftelser.add(bekreftelse);
        overstyr(bekreftelser);
    }

    @Step("Overstyrer aksjonspunkter")
    public void overstyr(List<AksjonspunktBekreftelse> bekreftelser) {
        OverstyrAksjonspunkter aksjonspunkter = new OverstyrAksjonspunkter(valgtFagsak, valgtBehandling, bekreftelser);
        behandlingerKlient.overstyr(aksjonspunkter);
        refreshBehandling();
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
        ventTilHistorikkinnslag(type);
        return hentHistorikkinnslagPåBehandling().stream()
                .filter(innslag -> type.equals(innslag.type()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Finner ikke historikkinnslag av typen " + type.getKode()));
    }

    public void ventTilHistorikkinnslag(HistorikkinnslagType type) {
        Vent.til(() -> harHistorikkinnslagPåBehandling(type),
                45, () -> "Saken  hadde ikke historikkinslag " + type + "\nHistorikkInnslag:"
                        + String.join("\t\n", String.valueOf(hentHistorikkinnslagPåBehandling())));
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
        throw new IllegalStateException("Fant ikke vilkår " + vilkårKode.toString() + "for behandling " + valgtBehandling.uuid);
    }

    private Vilkar hentVilkår(String vilkårKode) {
        return hentVilkår(new Kode("VILKAR_TYPE", vilkårKode));
    }

    public Kode vilkårStatus(String vilkårKode) {
        return hentVilkår(vilkårKode).getVilkarStatus();
    }

    private List<ProsessTaskListItemDto> hentProsesstaskerForBehandling(Behandling behandling) {
        var filter = new SokeFilterDto(List.of(), LocalDateTime.now().minusMinutes(5), LocalDateTime.now());
        var prosesstasker = prosesstaskKlient.list(filter);
        return prosesstasker.stream()
                .filter(p -> Objects.equals(behandling.uuid.toString(), p.taskParametre().behandlingId()))
                .toList();
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

    public void åpneForNyArbeidsforholdVurdering(BehandlingIdPost behandlingIdDto) {
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
}
