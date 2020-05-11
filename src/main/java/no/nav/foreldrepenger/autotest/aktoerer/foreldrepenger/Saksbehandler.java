package no.nav.foreldrepenger.autotest.aktoerer.foreldrepenger;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingerKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingHenlegg;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdPost;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingNy;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingPaVent;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.KlageVurderingResultatAksjonspunktMellomlagringDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftedeAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelseUtenTotrinn;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.OverstyrAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Vilkar;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriodeAndel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.BehandlingMedUttaksperioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.brev.BrevKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.brev.dto.BestillBrev;
import no.nav.foreldrepenger.autotest.klienter.fpsak.dokument.DokumentKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.dokument.dto.DokumentListeEnhet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.hendelse.HendelseKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.hendelse.dto.FødselHendelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.KodeverkKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kodeverk;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.ProsesstaskKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.ProsessTaskListItemDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.SokeFilterDto;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.deferred.Deffered;
import no.nav.foreldrepenger.autotest.util.konfigurasjon.MiljoKonfigurasjon;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugAksjonspunktbekreftelser;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;

public class Saksbehandler extends Aktoer {

    public List<Fagsak> fagsaker;
    public Fagsak valgtFagsak;

    private Deffered<List<DokumentListeEnhet>> dokumenter;
    private Deffered<List<HistorikkInnslag>> historikkInnslag;
    private Deffered<Behandling> annenPartBehandling;

    public List<Behandling> behandlinger;
    public Behandling valgtBehandling;

    private FagsakKlient fagsakKlient;
    private BehandlingerKlient behandlingerKlient;
    private KodeverkKlient kodeverkKlient;
    private DokumentKlient dokumentKlient;
    private BrevKlient brevKlient;
    private HistorikkKlient historikkKlient;
    private ProsesstaskKlient prosesstaskKlient;
    private HendelseKlient hendelseKlient;

    public Kodeverk kodeverk;

    public boolean ikkeVentPåStatus = false; // TODO hack for økonomioppdrag

    public Saksbehandler() {
        super();
        fagsakKlient = new FagsakKlient(session);
        behandlingerKlient = new BehandlingerKlient(session);
        kodeverkKlient = new KodeverkKlient(session);
        dokumentKlient = new DokumentKlient(session);
        brevKlient = new BrevKlient(session);
        historikkKlient = new HistorikkKlient(session);
        prosesstaskKlient = new ProsesstaskKlient(session);
        hendelseKlient = new HendelseKlient(session);
    }

    public Saksbehandler(Rolle rolle) throws IOException {
        this();
        erLoggetInnMedRolle(rolle);
    }

    @Override
    public void erLoggetInnMedRolle(Rolle rolle) throws IOException {
        super.erLoggetInnMedRolle(rolle);
        hentKodeverk();
    }

    @Override
    public void erLoggetInnUtenRolle() throws IOException {
        super.erLoggetInnUtenRolle();
        hentKodeverk();
    }

    /*
     * Hent enkel fagsak
     */
    public void hentFagsak(String saksnummer) throws Exception {
        velgFagsak(fagsakKlient.getFagsak(saksnummer));
    }

    /*
     * Hent enkel fagsak
     */
    @Step("Hent fagsak {saksnummer}")
    public void hentFagsak(long saksnummer) throws Exception {
        hentFagsak("" + saksnummer);
    }

    /*
     * Søker etter fagsaker
     */
    @Step("Søker etter fagsak {søk}")
    public void søkEtterFagsak(String søk) throws Exception {
        fagsaker = fagsakKlient.søk(søk);
        if (fagsaker.size() == 1) {
            velgFagsak(fagsaker.get(0));
        }
    }

    /*
     * Refresh
     */
    @Step("Refresh behandling")
    public void refreshBehandling() throws Exception {
        velgBehandling(valgtBehandling);
    }

    @Step("Refresh fagsak")
    public void refreshFagsak() throws Exception {
        Behandling behandling = valgtBehandling;
        hentFagsak(valgtFagsak.saksnummer);
        if (valgtBehandling == null && behandling != null) {
            velgBehandling(behandling);
        }
    }

    /*
     * Henter en liste av behandlinger
     */
    @Step("Henter behandlinger for saksnummer {saksnummer}")
    public List<Behandling> hentAlleBehandlingerForFagsak(long saksnummer) throws Exception {
        return behandlingerKlient.alle(saksnummer);
    }

    /*
     * Velger fagsak
     */
    @Step("Velger fagsak")
    public void velgFagsak(Fagsak fagsak) throws Exception {
        if (fagsak == null) {
            throw new RuntimeException("Kan ikke velge fagsak. fagsak er null");
        }
        valgtFagsak = fagsak;

        behandlinger = behandlingerKlient.alle(fagsak.saksnummer);
        valgtBehandling = null;

        if (behandlinger.size() == 1) { // ellers må en velge explisit
            velgBehandling(behandlinger.get(0));
        }
    }

    /*
     * velger behandling som valgt behandling
     */
    protected void velgBehandling(Kode behandlingstype) throws Exception {
        Behandling behandling = getBehandling(behandlingstype);
        if (null != behandling) {
            velgBehandling(behandling);
        } else {
            throw new RuntimeException("Valgt fagsak har ikke behandling av type: " + behandlingstype.kode);
        }
    }

    public void velgFørstegangsbehandling() throws Exception {
        velgBehandling(kodeverk.BehandlingType.getKode("BT-002"));
    }

    public void velgKlageBehandling() throws Exception {
        velgBehandling(kodeverk.BehandlingType.getKode("BT-003"));
    }

    public void velgRevurderingBehandling() throws Exception {
        velgBehandling(kodeverk.BehandlingType.getKode("BT-004"));
    }
    public void velgSisteBehandling() throws Exception {
        Behandling behandling = behandlinger.get(behandlinger.size()-1);
        this.valgtBehandling = behandling;
        if (null != behandling) {
            velgBehandling(behandling);
        } else {
            throw new RuntimeException("Valgt fagsak har ikke behandling");
        }
    }

    public void velgDokumentInnsynBehandling() throws Exception {
        velgBehandling(kodeverk.BehandlingType.getKode("BT-006"));
    }

    @Step("Velger behandling")
    public void velgBehandling(Behandling behandling) throws Exception {
        debugLoggBehandling(behandling);
        ventPåStatus(behandling);

        Deffered<Behandling> dBehandling = Deffered.deffered(() -> {
            return behandlingerKlient.getBehandling(behandling.uuid);
        });
        Deffered<List<HistorikkInnslag>> dHistorikkInnslag = Deffered.deffered(() -> {
            return historikkKlient.hentHistorikk(valgtFagsak.saksnummer);
        });
        Deffered<List<DokumentListeEnhet>> dDokumentListeEnhet = Deffered.deffered(() -> {
            return dokumentKlient.hentDokumentliste(valgtFagsak.saksnummer);
        });
        Deffered<Behandling> dAnnenPartBehandling = Deffered.deffered(() -> {
            return behandlingerKlient.annenPartBehandling(valgtFagsak.saksnummer);
        });

        valgtBehandling = dBehandling.get();
        populateBehandling(valgtBehandling);

        setDokumenter(dDokumentListeEnhet);
        setHistorikkInnslag(dHistorikkInnslag);
        setAnnenPartBehandling(dAnnenPartBehandling);
    }

    @Step("Populerer behandling")
    private void populateBehandling(Behandling behandling) throws Exception {

        valgtBehandling.setAksjonspunkter(Deffered.deffered(() -> {
            return behandlingerKlient.getBehandlingAksjonspunkt(behandling.uuid);
        }));
        valgtBehandling.setVilkar(Deffered.deffered(() -> {
            return behandlingerKlient.behandlingVilkår(behandling.uuid);
        }));

        /*
         * KODE OFFISIELL_KODE BESKRIVELSE
         * BT-002 ae0034 Førstegangsbehandling
         * BT-003 ae0058 Klage
         * BT-004 ae0028 Revurdering
         * BT-005 ae0043 Tilbakebetaling endring
         * BT-006 ae0042 Dokumentinnsyn
         */

        if (behandling.type.kode.equalsIgnoreCase("BT-006") /* Dokumentinnsyn */) {

        } else if (behandling.type.kode.equalsIgnoreCase("BT-003" /* Klage */)) {
            valgtBehandling.setKlagevurdering(Deffered.deffered(() -> {
                return behandlingerKlient.klage(behandling.uuid);
            }));
        } else {
            // FIXME: Forespørslene her burde konsultere resultat for valgtbehandling for å sjekke om URLene er tilgjengelig før de kjører.
            // URLene kan endre seg, men koden i behandlingerKlient tar ikke hensyn til det p.t. I tillegg er det unødvendig å spørre på noe som ikke
            // finnes slik det skjer nå.

            valgtBehandling.setUttakResultatPerioder(Deffered.deffered(() -> {
                UttakResultatPerioder uttakResultatPerioder = behandlingerKlient.behandlingUttakResultatPerioder(behandling.uuid);
                if (!uttakResultatPerioder.getPerioderForSøker().isEmpty()) {
                    Deffered<Saldoer> dStonadskontoer = Deffered.defferedLazy(() -> {
                        return behandlingerKlient.behandlingUttakStonadskontoer(valgtBehandling.uuid);
                    });
                    valgtBehandling.setSaldoer(dStonadskontoer);
                }

                return uttakResultatPerioder;
            }));

            valgtBehandling.setBeregningsgrunnlag(Deffered.deffered(() -> {
                return behandlingerKlient.behandlingBeregningsgrunnlag(valgtBehandling.uuid);
            }));
            valgtBehandling.setInntektArbeidYtelse(Deffered.deffered(() -> {
                return behandlingerKlient.behandlingInntektArbeidYtelse(valgtBehandling.uuid);
            }));

            // de neste sjeldent i bruk. Kunne kanskje unngått å gjøre REST kall med mindre man trenger det?
            valgtBehandling.setBeregningResultatEngangsstonad(Deffered.defferedLazy(() -> {
                return behandlingerKlient.behandlingBeregningsresultatEngangsstønad(valgtBehandling.uuid);
            }));
            valgtBehandling.setBeregningResultatForeldrepenger(Deffered.defferedLazy(() -> {
                return behandlingerKlient.behandlingBeregningsresultatForeldrepenger(valgtBehandling.uuid);
            }));
            valgtBehandling.setPersonopplysning(Deffered.defferedLazy(() -> {
                return behandlingerKlient.behandlingPersonopplysninger(valgtBehandling.uuid);
            }));
            valgtBehandling.setSoknad(Deffered.defferedLazy(() -> {
                return behandlingerKlient.behandlingSøknad(valgtBehandling.uuid);
            }));
            valgtBehandling.setOpptjening(Deffered.defferedLazy(() -> {
                return behandlingerKlient.behandlingOpptjening(valgtBehandling.uuid);
            }));

            valgtBehandling.setKontrollerFaktaData(Deffered.defferedLazy(() -> {
                return behandlingerKlient.behandlingKontrollerFaktaPerioder(valgtBehandling.uuid);
            }));
            valgtBehandling.setMedlem(Deffered.defferedLazy(() -> {
                return behandlingerKlient.behandlingMedlemskap(valgtBehandling.uuid);
            }));

            valgtBehandling.setTilrettelegging(Deffered.defferedLazy(() -> {
                return behandlingerKlient.behandlingTilrettelegging(valgtBehandling.id);
            }));


//            valgtBehandling.setTilrettelegging(Deffered.defferedLazy(() -> {
////               return behandlingerKlient.behandlingT
//            }));

        }
    }

    private void ventPåStatus(Behandling behandling) throws Exception {
        if (!ikkeVentPåStatus) {
            Vent.til(() -> {
                return verifiserStatusForBehandling(behandling);
            }, 90, () -> {
                List<ProsessTaskListItemDto> prosessTasker = hentProsesstaskerForBehandling(behandling);
                String prosessTaskList = "";
                for (ProsessTaskListItemDto prosessTaskListItemDto : prosessTasker) {
                    prosessTaskList += prosessTaskListItemDto.getTaskType() + " - " + prosessTaskListItemDto.getStatus() + "\n";
                }
                return "Behandling status var ikke klar men har ikke feilet\n" + prosessTaskList;
            });
        }
    }

    private boolean verifiserStatusForBehandling(Behandling behandling) throws Exception {
        AsyncPollingStatus status = behandlingerKlient.statusAsObject(behandling.uuid, null);

        if (status == null || status.getStatusCode() == null) {
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
            AllureHelper.debugFritekst("Prosesstask feilet for behandling[" + behandling.id + "] i behandlingsverifisering: " + status.getMessage());
            throw new RuntimeException("Status for behandling " + behandling.id + " feilet: " + status.getMessage());
        }
    }

    public List<UttakResultatPeriode> hentAvslåtteUttaksperioder() {
        return valgtBehandling.hentUttaksperioder().stream()
                .filter(uttakResultatPeriode -> uttakResultatPeriode.getPeriodeResultatType().kode.equalsIgnoreCase("AVSLÅTT"))
                .collect(Collectors.toList());
    }

    public Saldoer hentSaldoerGittUttaksperioder(List<UttakResultatPeriode> uttakResultatPerioder) throws IOException {
        BehandlingMedUttaksperioderDto behandlingMedUttaksperioderDto = new BehandlingMedUttaksperioderDto();
        behandlingMedUttaksperioderDto.setPerioder(uttakResultatPerioder);
        BehandlingIdDto behandlingIdDto = new BehandlingIdDto((long)valgtBehandling.id);
        behandlingMedUttaksperioderDto.setBehandlingId(behandlingIdDto);

        return behandlingerKlient.behandlingUttakStonadskontoerGittUttaksperioder(behandlingMedUttaksperioderDto);
    }


    public List<BeregningsresultatPeriodeAndel> hentBeregningsresultatPeriodeAndelerForArbeidsforhold(String organisasjonsnummer) {
        return Arrays.stream(valgtBehandling.getBeregningResultatForeldrepenger().getPerioder())
                .flatMap(beregningsresultatPeriode -> Arrays.stream(beregningsresultatPeriode.getAndeler()))
                .filter(andeler -> andeler.getArbeidsgiverOrgnr().equalsIgnoreCase(organisasjonsnummer))
                .sorted(Comparator.comparing(BeregningsresultatPeriodeAndel::getSisteUtbetalingsdato))
                .collect(Collectors.toList());


    }


    /* VERIFISERINGER */
    // TODO: Flytte dem en annen plass? Egen verifiserings-saksbehander?
    public boolean sjekkOmDetErFrilansinntektDagenFørSkjæringstidspuktet() {
        var skjaeringstidspunkt = valgtBehandling.behandlingsresultat.getSkjæringstidspunkt().getDato();
        for (var opptjening : valgtBehandling.getOpptjening().getOpptjeningAktivitetList()) {
            if ( opptjening.getAktivitetType().kode.equalsIgnoreCase("FRILANS") &&
                    opptjening.getOpptjeningTom().isEqual(skjaeringstidspunkt.minusDays(1)) ){
                return true;
            }
        }
        return false;
    }

    public boolean sjekkOmSykepengerLiggerTilGrunnForOpptjening() {
        var skjaeringstidspunkt = valgtBehandling.behandlingsresultat.getSkjæringstidspunkt().getDato();
        for (var opptjening : valgtBehandling.getOpptjening().getOpptjeningAktivitetList()) {
            if ( opptjening.getAktivitetType().kode.equalsIgnoreCase("SYKEPENGER") &&
                    opptjening.getOpptjeningTom().isBefore(skjaeringstidspunkt)){
                return true;
            }
        }
        return false;
    }

    public boolean verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(double prosentAvDagsatsTilArbeidsgiver) {
        for (var periode : valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()) {
            if ( !verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(periode, prosentAvDagsatsTilArbeidsgiver) ) {
                return false;
            }
        }
        return true;
    }

    public boolean verifiserUtbetaltDagsatsMedRefusjonGårTilRiktigPart(BeregningsresultatPeriode periode, double prosentAvDagsatsTilArbeidsgiver) {
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
        if ( utbetaltRefusjonForAndeler.stream().mapToInt(Integer::intValue).sum() != forventetUtbetaltDagsatsTilArbeidsgiver ) {
            return false;
        }
        if ( utbetaltTilSøkerForAndeler.stream().mapToInt(Integer::intValue).sum() != forventetUtbetaltDagsatsTilSøker ) {
            return false;
        }
        return true;
    }


    public boolean verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForAllePeriode(String internArbeidsforholdID,
                                                                                       double prosentAvDagsatsTilArbeidsgiver) {
        var prosentfaktor = prosentAvDagsatsTilArbeidsgiver / 100;
        for (var periode : valgtBehandling.getBeregningResultatForeldrepenger().getPerioder()) {
            if (verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForPeriode(periode, internArbeidsforholdID, prosentfaktor))
                return false;
        }
        return true;
    }

    public boolean verifiserUtbetaltDagsatsMedRefusjonGårTilArbeidsgiverForPeriode( BeregningsresultatPeriode periode,
                                                                                    String internArbeidsforholdID,
                                                                                    double prosentfaktor) {
        var dagsats = periode.getDagsats();
        var forventetUtbetaltDagsatsTilArbeidsgiver = Math.round(dagsats * prosentfaktor);
        List<Integer> utbetaltRefusjonForAndeler = new ArrayList<>();
        for (var andel : periode.getAndeler()) {
            if ( andel.getArbeidsforholdId() != null && andel.getArbeidsforholdId().equalsIgnoreCase(internArbeidsforholdID) ) {
                utbetaltRefusjonForAndeler.add(andel.getRefusjon());
            }
        }
        if ( utbetaltRefusjonForAndeler.stream().mapToInt(Integer::intValue).sum() != forventetUtbetaltDagsatsTilArbeidsgiver ) {
            return true;
        }
        return false;
    }

    /*
     * Henting av kodeverk
     */
    public void hentKodeverk() {
        try {
            kodeverk = kodeverkKlient.getKodeverk();
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke hente kodeverk: " + e.getMessage());
        }
    }

    public void hentSelftest() throws IOException {
        HttpResponse response = session.get(MiljoKonfigurasjon.getRouteMetrics());
        if (200 != response.getStatusLine().getStatusCode()) {
            throw new RuntimeException("Kunne ikke hente selftest. fikk httpstatus: " + response.getStatusLine().getStatusCode());
        }
    }

    /*
     * Setter behandling på vent
     */
    @Step("Setter behandling på vent")
    protected void settBehandlingPåVent(LocalDate frist, Kode årsak) throws Exception {
        behandlingerKlient.settPaVent(new BehandlingPaVent(valgtBehandling, frist, årsak));
        refreshBehandling();
    }

    public void settBehandlingPåVent(LocalDate frist, String årsak) throws Exception {
        settBehandlingPåVent(frist, kodeverk.Venteårsak.getKode(årsak));
    }

    @Step("Gjenopptar Behandling")
    public void gjenopptaBehandling() throws Exception {
        behandlingerKlient.gjenoppta(new BehandlingIdPost(valgtBehandling));
        refreshBehandling();
    }

    @Step("Henlegger behandling")
    public void henleggBehandling(Kode årsak) throws Exception {
        behandlingerKlient.henlegg(new BehandlingHenlegg(valgtBehandling.id, valgtBehandling.versjon, årsak.kode, "Henlagt"));
        refreshBehandling();
    }

    public <T extends AksjonspunktBekreftelse> T aksjonspunktBekreftelse(Class<T> type) throws JsonProcessingException {
        return hentAksjonspunktbekreftelse(type);
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
    @SuppressWarnings("unchecked")
    @Step("Henter aksjonspunktbekreftelse for {type}")
    public <T extends AksjonspunktBekreftelse> T hentAksjonspunktbekreftelse(Class<T> type) throws JsonProcessingException {
        for (Aksjonspunkt aksjonspunkt : valgtBehandling.getAksjonspunkter()) {
            if (type.isInstance(aksjonspunkt.getBekreftelse())) {
                AksjonspunktBekreftelse bekreftelse = aksjonspunkt.getBekreftelse();
                bekreftelse.setFagsakOgBehandling(valgtFagsak, valgtBehandling);
                return (T) bekreftelse;
            }
        }
        debugLoggBehandling("Behandling mangler aksjonspunkt: ", valgtBehandling);
        throw new RuntimeException(
                "Valgt behandling (" + valgtBehandling.id + " - " + valgtFagsak.saksnummer + ") har ikke aksjonspunktbekreftelse: " + type.getName());
    }

    /*
     * Henter aksjonspunkt av gitt kode
     */
    @Step("Henter aksjonspunkt {kode}")
    public Aksjonspunkt hentAksjonspunkt(String kode) {
        for (Aksjonspunkt aksjonspunkt : valgtBehandling.getAksjonspunkter()) {
            if (aksjonspunkt.getDefinisjon().kode.equals(kode)) {
                return aksjonspunkt;
            }
        }
        return null;
    }
    @Step("Henter aksjonspunkt {kode}")
    public Aksjonspunkt hentAksjonspunktSomKanLøses(String kode) {
        for (Aksjonspunkt aksjonspunkt : valgtBehandling.getAksjonspunkter()) {
            if (aksjonspunkt.getDefinisjon().kode.equals(kode) && aksjonspunkt.getKanLoses()) {
                return aksjonspunkt;
            }
        }
        return null;
    }
    @Step("Henter aksjonspunkt som skal til totrinns kontroll")
    public List<Aksjonspunkt> hentAksjonspunktSomSkalTilTotrinnsBehandling() {
        return valgtBehandling.getAksjonspunkter().stream()
                .filter(aksjonspunkt -> aksjonspunkt.skalTilToTrinnsBehandling())
                .collect(Collectors.toList());
    }

    /*
     * Sjekker om aksjonspunkt av gitt kode er på behandlingen
     */
    @Step("Sjekker om aksjonspunkt av gitt kode er på behandling")
    public boolean harAksjonspunkt(String kode) throws JsonProcessingException {
        debugLoggBehandling(valgtBehandling);
        return null != hentAksjonspunkt(kode);
    }

    public boolean harAksjonspunktSomKanLøses(String kode) throws JsonProcessingException {
        debugLoggBehandling(valgtBehandling);
        return null != hentAksjonspunktSomKanLøses(kode);
    }

    /*
     * Bekrefte aksjonspunkt bekreftelse
     */
    @Step("Henter og bekrefter aksjonspunkt for {type}")
    public <T extends AksjonspunktBekreftelse> void bekreftAksjonspunktMedDefaultVerdier(Class<T> type) throws Exception {
        bekreftAksjonspunkt(hentAksjonspunktbekreftelse(type));
    }


    public void bekreftAksjonspunkt(AksjonspunktBekreftelse bekreftelse) throws Exception {
        List<AksjonspunktBekreftelse> bekreftelser = new ArrayList<>();
        bekreftelser.add(bekreftelse);
        bekreftAksjonspunktbekreftelserer(bekreftelser);
    }

    public void bekreftAksjonspunktbekreftelserer(AksjonspunktBekreftelse... bekreftelser) throws Exception {
        bekreftAksjonspunktbekreftelserer(Arrays.asList(bekreftelser));
    }
    @Step("Bekrefter aksjonspunktbekreftelser")
    public void bekreftAksjonspunktbekreftelserer(List<AksjonspunktBekreftelse> bekreftelser) throws Exception {
        debugAksjonspunktbekreftelser(bekreftelser);
        BekreftedeAksjonspunkter aksjonspunkter = new BekreftedeAksjonspunkter(valgtFagsak, valgtBehandling, bekreftelser);
        behandlingerKlient.postBehandlingAksjonspunkt(aksjonspunkter);
        refreshBehandling();
    }

    /*
     * Oversyring
     */
    public void overstyr(AksjonspunktBekreftelse bekreftelse) throws Exception {
        List<AksjonspunktBekreftelse> bekreftelser = new ArrayList<>();
        bekreftelser.add(bekreftelse);
        overstyr(bekreftelser);
    }

    @Step("Overstyrer aksjonspunkt")
    public void overstyr(List<AksjonspunktBekreftelse> bekreftelser) throws Exception {
        OverstyrAksjonspunkter aksjonspunkter = new OverstyrAksjonspunkter(valgtFagsak, valgtBehandling, bekreftelser);
        behandlingerKlient.overstyr(aksjonspunkter);
        refreshBehandling();
    }

    /*
     * Opretter behandling på nåværende fagsak
     */
    public void opprettBehandling(Kode behandlingstype, Kode årsak) throws Exception {
        opprettBehandling(behandlingstype, årsak, valgtFagsak);
        hentFagsak(valgtFagsak.saksnummer);
    }

    public void opprettBehandlingRevurdering(String årsak) throws Exception {
        opprettBehandling(kodeverk.BehandlingType.getKode("BT-004"), kodeverk.BehandlingÅrsakType.getKode(årsak));
    }

    public void oprettBehandlingInnsyn(Kode årsak) throws Exception {
        opprettBehandling(kodeverk.BehandlingType.getKode("BT-006"), årsak);
    }

    /*
     * Brev
     */
    @Step("Sender breev med malkode {brevmalKode} til mottaker {mottaker}")
    public void sendBrev(String brevmalKode, String mottaker, String fritekst) throws IOException {
        brevKlient.bestill(new BestillBrev(valgtBehandling.id,
                mottaker,
                brevmalKode,
                fritekst));
    }

    /*
     * Dokumenter
     */
    @Step("Venter på dokument {dokument}")
    public void ventTilDokument(String dokument) throws Exception {
        if (harDokument(dokument)) {
            return;
        }
        Vent.til(() -> {
            refreshBehandling();
            return harDokument(dokument);
        }, 30, () -> "Behandling har ikke dokument: " + dokument + "\n\tDokumenter:" + getDokumenter());
    }

    public boolean harDokument(String dokument) {
        return getDokument(dokument) != null;
    }

    private DokumentListeEnhet getDokument(String dokument) {
        throw new RuntimeException("getDokument ikke implementert");
    }

    /*
     * Historikkinnslag
     */
    @Step("Venter på historikkinnslag {type}")
    public void ventTilHistorikkinnslag(HistorikkInnslag.Type type) throws Exception {
        if (harHistorikkinnslag(type)) {
            return;
        }
        Vent.til(() -> {
            refreshBehandling();
            return harHistorikkinnslag(type);
        }, 30, () -> "Saken  hadde ikke historikkinslag " + type + "\nHistorikkInnslag:" + String.join("\t\n", String.valueOf(getHistorikkInnslag())));
    }

    /*
     * Historikkinnslag
     */
    @Step("Venter sekunder antall sekunder på historikkinnslag {type}")
    public void ventTilAntallHistorikkinnslag(HistorikkInnslag.Type type, Integer sekunder, Integer antallHistorikkInnslag) throws Exception {
        Vent.til(() -> {
            velgBehandling(valgtBehandling);
            return harAntallHistorikkinnslag(type) == antallHistorikkInnslag;
        }, sekunder, () -> "Saken  hadde ikke historikkinslag " + type + " \nHistorikkInnslag:" + String.join("\t\n", String.valueOf(getHistorikkInnslag())));
    }

    public boolean harHistorikkinnslag(HistorikkInnslag.Type type) {
        return getHistorikkInnslag(type) != null;
    }

    public boolean harHistorikkinnslagForBehandling(HistorikkInnslag.Type type, int behandlingsId) {
        for (HistorikkInnslag innslag : getHistorikkInnslag()) {
            if (innslag.getTypeKode().contains(type.getKode()) && innslag.getBehandlingsid() == behandlingsId) {
                return true;
            }
        }
        return false;
    }


    private HistorikkInnslag getHistorikkInnslag(HistorikkInnslag.Type type) {
        for (HistorikkInnslag innslag : getHistorikkInnslag()) {
            if (innslag.getTypeKode().contains(type.getKode())) {
                return innslag;
            }
        }
        return null;
    }

    /*
     * Fagsakstatus
     */

    public String getFagsakstatus() {
        return valgtFagsak.hentStatus().kode;
    }

    public boolean harFagsakstatus(Kode status) {
        return valgtFagsak.hentStatus().equals(status);
    }

    protected void ventTilFagsakstatus(Kode status) throws Exception {
        if (harFagsakstatus(status)) {
            return;
        }
        Vent.til(() -> {
            refreshFagsak();
            return harFagsakstatus(status);
        }, 10, "Fagsak har ikke status " + status);
    }

    protected void ventTilFagsakstatus(String status) throws Exception {
        ventTilFagsakstatus(kodeverk.FagsakStatus.getKode(status));
    }

    public int harAntallHistorikkinnslag(HistorikkInnslag.Type type) {
        int antall = 0;
        for (HistorikkInnslag innslag : getHistorikkInnslag()) {
            if (innslag.getTypeKode().equals(type.getKode())) {
                antall++;
            }
        }
        return antall;
    }

    /*
     * Aksjonspunkt
     */
    @Step("Venter på aksjonspunkt {kode}")
    public void ventTilAksjonspunkt(String kode) throws Exception {
        if (harAksjonspunkt(kode)) {
            refreshBehandling();
            return;
        }
        Vent.til(() -> {
            refreshBehandling();
            return harAksjonspunkt(kode);
        }, 30, () -> "Saken  hadde ikke aksjonspunkt " + kode + (valgtBehandling == null ? "" : "\n\tAksjonspunkter:" + valgtBehandling.getAksjonspunkter()));
    }

    @Step("Venter på aksjonspunkt {kode} hvis det kan løses")
    public void ventTilAksjonspunktSomKanLøses(String kode) throws Exception {
        if (harAksjonspunktSomKanLøses(kode)) {
            refreshBehandling();
            return;
        }
        Vent.til(() -> {
            refreshBehandling();
            return harAksjonspunktSomKanLøses(kode);
        }, 30, () -> "Saken  hadde ikke aksjonspunkt " + kode + (valgtBehandling == null ? "" : "\n\tAksjonspunkter:" + valgtBehandling.getAksjonspunkter()));
    }

    private Vilkar hentVilkår(Kode vilkårKode) {
        for (Vilkar vilkår : valgtBehandling.getVilkar()) {
            if (vilkår.getVilkarType().equals(vilkårKode)) {
                return vilkår;
            }
        }
        throw new IllegalStateException(String.format("Fant ikke vilkår %s for behandling %s", vilkårKode.toString(), valgtBehandling.id));
    }

    public Vilkar hentVilkår(String vilkårKode) {
        return hentVilkår(new Kode("VILKAR_TYPE", vilkårKode));
    }

    public Kode vilkårStatus(String vilkårKode) {
        return hentVilkår(vilkårKode).getVilkarStatus();
    }

    /*
     * Behandlingsstatus
     */
    @Step("Venter på behandlingsstatus {status}")
    public void ventTilBehandlingsstatus(String status) throws Exception {
        if (harBehandlingsstatus(status)) {
            return;
        }
        Vent.til(() -> {
            refreshBehandling();
            return harBehandlingsstatus(status);
        }, 60, "Behandlingsstatus var ikke " + status + " men var " + getBehandlingsstatus() + " i sak: " + valgtFagsak.saksnummer);
    }

    public boolean harBehandlingsstatus(String status) {
        return getBehandlingsstatus().equals(status);
    }

    public String getBehandlingsstatus() {
        return valgtBehandling.status.kode;
    }

    public void ventTilSakHarFørstegangsbehandling() throws Exception {
        ventTilSakHarBehandling(kodeverk.BehandlingType.getKode("BT-002"));
    }

    public void ventTilSakHarRevurdering() throws Exception {
        ventTilSakHarBehandling(kodeverk.BehandlingType.getKode("BT-004"));
    }

    public void ventTilSakHarKlage() throws Exception {
        ventTilSakHarBehandling(kodeverk.BehandlingType.getKode("BT-003"));
    }

    @Step("Venter på at fagsak får behandlingstype: {behandlingType}")
    protected void ventTilSakHarBehandling(Kode behandlingType) throws Exception {
        if (harBehandling(behandlingType)) {
            return;
        }
        Vent.til(() -> {
            refreshFagsak();
            return harBehandling(behandlingType);
        }, 30, "Saken har ikke fått behandling av type: " + behandlingType);
    }

    @Step("Venter på at fagsak får x antall behandlinger")
    public void ventTilSakHarXAntallBehandlinger(int antallBehandlinger) throws Exception {
        if (behandlinger.size() >= antallBehandlinger) {
            return;
        }
        Vent.til(() -> {
            refreshFagsak();
            return behandlinger.size() >= antallBehandlinger;
        }, 30, "Saken har ikke riktig anntal behandlinger" );
    }
    protected boolean harBehandling(Kode behandlingType) {
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

    @Step("Fatter vedtak og venter til sak er avsluttet")
    public void fattVedtakOgVentTilAvsluttetBehandling(FatterVedtakBekreftelse bekreftelse) throws Exception {
        bekreftAksjonspunkt(bekreftelse);
        ventTilAvsluttetBehandling();
    }

    @Step("Fatter vedtak uten totrinnsbehandling og venter til sak er avsluttet")
    public void fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling() throws Exception {
        bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelseUtenTotrinn.class);
        ventTilAvsluttetBehandling();
    }

    @Step("Venter til saken er avsluttet")
    public void ventTilAvsluttetBehandling() throws Exception {
        ventTilBehandlingsstatus("AVSLU");
    }

    /*
     * Private
     */

    /*
     * Opretter behandling på gitt fagsak
     */
    @Step("Oppretter behandling på gitt fagsak")
    private void opprettBehandling(Kode behandlingstype, Kode årsak, Fagsak fagsak) throws Exception {
        behandlingerKlient.putBehandlinger(new BehandlingNy(fagsak.saksnummer, behandlingstype.kode, årsak == null ? null : årsak.kode));
        velgFagsak(valgtFagsak); // Henter fagsaken på ny
    }

    @Step("Henter prosesstasker for behandling")
    private List<ProsessTaskListItemDto> hentProsesstaskerForBehandling(Behandling behandling) throws IOException {
        SokeFilterDto filter = new SokeFilterDto();
        filter.setSisteKjoeretidspunktFraOgMed(LocalDateTime.now().minusMinutes(5));
        filter.setSisteKjoeretidspunktTilOgMed(LocalDateTime.now());
        List<ProsessTaskListItemDto> prosesstasker = prosesstaskKlient.list(filter);
        return prosesstasker.stream().filter(p -> p.getTaskParametre().getBehandlingId() == "" + behandling.id).collect(Collectors.toList());
    }

    public boolean sakErKobletTilAnnenpart() {
        return getAnnenPartBehandling() != null;
    }

    public void mellomlagreKlage() throws Exception {
        behandlingerKlient.mellomlagre(
                new KlageVurderingResultatAksjonspunktMellomlagringDto(valgtBehandling, hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP)));
        refreshBehandling();
    }

    public void mellomlagreOgGjennåpneKlage() throws Exception {
        behandlingerKlient.mellomlagreGjennapne(
                new KlageVurderingResultatAksjonspunktMellomlagringDto(valgtBehandling, hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP)));
        refreshBehandling();
    }

    public boolean harRevurderingBehandling() {
        return harBehandling(kodeverk.BehandlingType.getKode("BT-004"));
    }

    public void ventTilFagsakAvsluttet() throws Exception {
        ventTilFagsakstatus("AVSLU");
    }

    public void ventTilFagsakLøpende() throws Exception {
        ventTilFagsakstatus("LOP");
    }

    public List<DokumentListeEnhet> getDokumenter() {
        return get(dokumenter);
    }

    private void setDokumenter(Deffered<List<DokumentListeEnhet>> dDokumentListeEnhet) {
        this.dokumenter = dDokumentListeEnhet;
    }

    public List<HistorikkInnslag> getHistorikkInnslag() {
        return get(historikkInnslag);
    }

    private void setHistorikkInnslag(Deffered<List<HistorikkInnslag>> dHistorikkInnslag) {
        this.historikkInnslag = dHistorikkInnslag;
    }

    public Behandling getAnnenPartBehandling() {
        return get(annenPartBehandling);
    }

    private void setAnnenPartBehandling(Deffered<Behandling> dAnnenPartBehandling) {
        this.annenPartBehandling = dAnnenPartBehandling;
    }

    private static <V> V get(Deffered<V> deferred) {
        try {
            return deferred == null ? null : deferred.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Step("Sender fødselshendelse")
    public void sendFødselsHendelse(String aktørIdForeldre, LocalDate fødselsdato) throws Exception{
        FødselHendelse fødselHendelse= new FødselHendelse(aktørIdForeldre, fødselsdato);
        hendelseKlient.sendHendelse(fødselHendelse);
    }
}
