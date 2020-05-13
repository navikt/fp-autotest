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
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
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
import no.nav.foreldrepenger.autotest.klienter.fpsak.brev.BrevKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.brev.dto.BestillBrev;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.KodeverkKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kodeverk;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.ProsesstaskKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.ProsessTaskListItemDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.SokeFilterDto;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.vent.Vent;

public class Saksbehandler extends Aktoer {

    public Fagsak valgtFagsak;

    private Supplier<List<HistorikkInnslag>> historikkInnslag;
    private Supplier<Behandling> annenPartBehandling;

    public List<Behandling> behandlinger;
    public Behandling valgtBehandling;

    private FagsakKlient fagsakKlient;
    private BehandlingerKlient behandlingerKlient;
    private KodeverkKlient kodeverkKlient;
    private BrevKlient brevKlient;
    private HistorikkKlient historikkKlient;
    private ProsesstaskKlient prosesstaskKlient;
    public Kodeverk kodeverk;

    public Saksbehandler() {
        super();
        fagsakKlient = new FagsakKlient(session);
        behandlingerKlient = new BehandlingerKlient(session);
        kodeverkKlient = new KodeverkKlient(session);
        brevKlient = new BrevKlient(session);
        historikkKlient = new HistorikkKlient(session);
        prosesstaskKlient = new ProsesstaskKlient(session);
    }

    public Saksbehandler(Rolle rolle) {
        this();
        erLoggetInnMedRolle(rolle);
    }

    @Override
    public void erLoggetInnMedRolle(Rolle rolle) {
        super.erLoggetInnMedRolle(rolle);
        hentKodeverk();
    }

    @Override
    public void erLoggetInnUtenRolle() {
        super.erLoggetInnUtenRolle();
        hentKodeverk();
    }

    /*
     * Hent enkel fagsak
     */
    public void hentFagsak(String saksnummer) {
        velgFagsak(fagsakKlient.getFagsak(saksnummer));
    }

    /*
     * Hent enkel fagsak
     */
    @Step("Hent fagsak {saksnummer}")
    public void hentFagsak(long saksnummer) {
        hentFagsak("" + saksnummer);
    }

    /*
     * Refresh
     */
    @Step("Refresh behandling")
    public void refreshBehandling() {
        velgBehandling(valgtBehandling);
    }

    @Step("Refresh fagsak")
    public void refreshFagsak() {
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
    public List<Behandling> hentAlleBehandlingerForFagsak(long saksnummer) {
        return behandlingerKlient.alle(saksnummer);
    }

    /*
     * Velger fagsak
     */
    @Step("Velger fagsak")
    public void velgFagsak(Fagsak fagsak) {
        if (fagsak == null) {
            throw new RuntimeException("Kan ikke velge fagsak. fagsak er null");
        }
        valgtFagsak = fagsak;

        behandlinger = hentAlleBehandlingerForFagsak(fagsak.saksnummer);
        velgSisteBehandling();
    }

    /*
     * velger behandling som valgt behandling
     */
    protected void velgBehandling(Kode behandlingstype) {
        Behandling behandling = getBehandling(behandlingstype);
        if (null != behandling) {
            velgBehandling(behandling);
        } else {
            throw new RuntimeException("Valgt fagsak har ikke behandling av type: " + behandlingstype.kode);
        }
    }

    public void velgFørstegangsbehandling() {
        velgBehandling(kodeverk.BehandlingType.getKode("BT-002"));
    }

    public void velgKlageBehandling() {
        velgBehandling(kodeverk.BehandlingType.getKode("BT-003"));
    }

    public void velgRevurderingBehandling() {
        velgBehandling(kodeverk.BehandlingType.getKode("BT-004"));
    }
    public void velgSisteBehandling() {
        var behandling = hentAlleBehandlingerForFagsak(valgtFagsak.saksnummer).stream()
                .max(Comparator.comparing(b -> b.opprettet))
                .orElseThrow();
        velgBehandling(behandling);
    }

    public void velgDokumentInnsynBehandling() {
        velgBehandling(kodeverk.BehandlingType.getKode("BT-006"));
    }

    @Step("Velger behandling")
    public void velgBehandling(Behandling behandling) {
        debugLoggBehandling(behandling);
        ventPåStatus(behandling);

        valgtBehandling = behandlingerKlient.getBehandling(behandling.uuid);
        populateBehandling(valgtBehandling);

        this.historikkInnslag = () -> historikkKlient.hentHistorikk(valgtFagsak.saksnummer);
        this.annenPartBehandling = () -> behandlingerKlient.annenPartBehandling(valgtFagsak.saksnummer);
    }

    @Step("Populerer behandling")
    private void populateBehandling(Behandling behandling) {

        behandling.setAksjonspunkter(() -> behandlingerKlient.getBehandlingAksjonspunkt(behandling.uuid));
        behandling.setVilkar(() -> behandlingerKlient.behandlingVilkår(behandling.uuid));

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
            behandling.setKlagevurdering(() -> behandlingerKlient.klage(behandling.uuid));
        } else {
            // FIXME: Forespørslene her burde konsultere resultat for valgtbehandling for å sjekke om URLene er tilgjengelig før de kjører.
            // URLene kan endre seg, men koden i behandlingerKlient tar ikke hensyn til det p.t. I tillegg er det unødvendig å spørre på noe som ikke
            // finnes slik det skjer nå.

            behandling.setUttakResultatPerioder(() -> behandlingerKlient.behandlingUttakResultatPerioder(behandling.uuid));
            behandling.setSaldoer(() -> behandlingerKlient.behandlingUttakStonadskontoer(behandling.uuid));

            behandling.setBeregningsgrunnlag(() -> behandlingerKlient.behandlingBeregningsgrunnlag(behandling.uuid));
            behandling.setInntektArbeidYtelse(() -> behandlingerKlient.behandlingInntektArbeidYtelse(behandling.uuid));

            behandling.setBeregningResultatEngangsstonad(() -> behandlingerKlient.behandlingBeregningsresultatEngangsstønad(behandling.uuid));
            behandling.setBeregningResultatForeldrepenger(() -> behandlingerKlient.behandlingBeregningsresultatForeldrepenger(behandling.uuid));
            behandling.setPersonopplysning(() -> behandlingerKlient.behandlingPersonopplysninger(behandling.uuid));
            behandling.setSoknad(() -> behandlingerKlient.behandlingSøknad(behandling.uuid));
            behandling.setOpptjening(() -> behandlingerKlient.behandlingOpptjening(behandling.uuid));

            behandling.setKontrollerFaktaData(() -> behandlingerKlient.behandlingKontrollerFaktaPerioder(behandling.uuid));
            behandling.setMedlem(() -> behandlingerKlient.behandlingMedlemskap(behandling.uuid));

            behandling.setTilrettelegging(() -> behandlingerKlient.behandlingTilrettelegging(behandling.id));
        }
    }

    private void ventPåStatus(Behandling behandling) {
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

    private boolean verifiserStatusForBehandling(Behandling behandling) {
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

    public Saldoer hentSaldoerGittUttaksperioder(List<UttakResultatPeriode> uttakResultatPerioder) {
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

    public Optional<BeregningsresultatPeriodeAndel> hentBeregningsresultatPeriodeAndelerForSN() {
        return Arrays.stream(valgtBehandling.getBeregningResultatForeldrepenger().getPerioder())
                .flatMap(beregningsresultatPeriode -> Arrays.stream(beregningsresultatPeriode.getAndeler()))
                .filter(andeler -> andeler.getAktivitetStatus().kode.equalsIgnoreCase("SN"))
                .findAny();
    }

    /* VERIFISERINGER */
    // TODO: Flytte dem en annen plass? Egen verifiserings-saksbehander?
    public boolean sjekkOmPeriodeITilkjentYtelseInneholderAktivitet(BeregningsresultatPeriode beregningsresultatPeriode,
                                                                    String aktivitetskode) {
        return Arrays.stream(beregningsresultatPeriode.getAndeler())
                .anyMatch(beregningsresultatPeriodeAndel ->
                        beregningsresultatPeriodeAndel.getAktivitetStatus().kode.equalsIgnoreCase(aktivitetskode));
    }

    public boolean sjekkOmDetErFrilansinntektDagenFørSkjæringstidspuktet() {
        var skjaeringstidspunkt = valgtBehandling.behandlingsresultat.getSkjæringstidspunkt().getDato();
        for (var opptjening : valgtBehandling.getOpptjening().getOpptjeningAktivitetList()) {
            if ( opptjening.getAktivitetType().kode.equalsIgnoreCase("FRILANS") &&
                    opptjening.getOpptjeningTom().isEqual(skjaeringstidspunkt.minusDays(1)) ) {
                return true;
            }
        }
        return false;
    }

    public boolean sjekkOmSykepengerLiggerTilGrunnForOpptjening() {
        for (var opptjening : valgtBehandling.getOpptjening().getOpptjeningAktivitetList()) {
            if ( opptjening.getAktivitetType().kode.equalsIgnoreCase("SYKEPENGER") ) {
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

    /*
     * Setter behandling på vent
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
        behandlingerKlient.henlegg(new BehandlingHenlegg(valgtBehandling.id, valgtBehandling.versjon, årsak.kode, "Henlagt"));
        refreshBehandling();
    }

    public <T extends AksjonspunktBekreftelse> T aksjonspunktBekreftelse(Class<T> type) {
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
        return Vent.tilRetur(() -> hentAksjonspunktDirekte(kode), 30, "Finner ikke aksjonspunkt " + kode);
    }

    private Optional<Aksjonspunkt> hentAksjonspunktDirekte(String kode) {
        for (Aksjonspunkt aksjonspunkt : valgtBehandling.getAksjonspunkter()) {
            if (aksjonspunkt.getDefinisjon().kode.equals(kode)) {
                return Optional.of(aksjonspunkt);
            }
        }
        return Optional.empty();
    }

    @Step("Henter aksjonspunkt {kode}")
    public Aksjonspunkt hentAksjonspunktSomKanLøses(String kode) {
        var aksjonspunkt = hentAksjonspunkt(kode);
        if (aksjonspunkt.getKanLoses()) {
            return aksjonspunkt;
        }
        return null;
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
        return hentAksjonspunkt(kode) != null;
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
        refreshBehandling();
        debugAksjonspunktbekreftelser(bekreftelser);
        BekreftedeAksjonspunkter aksjonspunkter = new BekreftedeAksjonspunkter(valgtFagsak, valgtBehandling, bekreftelser);
        behandlingerKlient.postBehandlingAksjonspunkt(aksjonspunkter);
        refreshBehandling();
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
     * Opretter behandling på nåværende fagsak
     */
    public void opprettBehandling(Kode behandlingstype, Kode årsak) {
        opprettBehandling(behandlingstype, årsak, valgtFagsak);
        hentFagsak(valgtFagsak.saksnummer);
    }

    public void opprettBehandlingRevurdering(String årsak) {
        opprettBehandling(kodeverk.BehandlingType.getKode("BT-004"), kodeverk.BehandlingÅrsakType.getKode(årsak));
    }

    public void oprettBehandlingInnsyn(Kode årsak) {
        opprettBehandling(kodeverk.BehandlingType.getKode("BT-006"), årsak);
    }

    /*
     * Brev
     */
    @Step("Sender breev med malkode {brevmalKode} til mottaker {mottaker}")
    public void sendBrev(String brevmalKode, String mottaker, String fritekst) {
        brevKlient.bestill(new BestillBrev(valgtBehandling.id,
                mottaker,
                brevmalKode,
                fritekst));
    }

    /*
     * Historikkinnslag
     */
    @Step("Venter på historikkinnslag {type}")
    public void ventTilHistorikkinnslag(HistorikkInnslag.Type type) {
        Vent.til(() -> harHistorikkinnslag(type),
                30, () -> "Saken  hadde ikke historikkinslag " + type + "\nHistorikkInnslag:" + String.join("\t\n", String.valueOf(getHistorikkInnslag())));
    }

    /*
     * Historikkinnslag
     */
    @Step("Venter sekunder antall sekunder på historikkinnslag {type}")
    public void ventTilAntallHistorikkinnslag(HistorikkInnslag.Type type, Integer sekunder, Integer antallHistorikkInnslag) {
        Vent.til(() -> harAntallHistorikkinnslag(type) == antallHistorikkInnslag, sekunder,
                () -> "Saken  hadde ikke historikkinslag " + type + " \nHistorikkInnslag:" + String.join("\t\n", String.valueOf(getHistorikkInnslag())));
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

    public boolean harFagsakstatus(Kode status) {
        return valgtFagsak.hentStatus().equals(status);
    }

    protected void ventTilFagsakstatus(Kode status) {
        if (harFagsakstatus(status)) {
            return;
        }
        Vent.til(() -> {
            refreshFagsak();
            return harFagsakstatus(status);
        }, 10, "Fagsak har ikke status " + status);
    }

    protected void ventTilFagsakstatus(String status) {
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

    private Vilkar hentVilkår(Kode vilkårKode) {
        for (Vilkar vilkår : valgtBehandling.getVilkar()) {
            if (vilkår.getVilkarType().equals(vilkårKode)) {
                return vilkår;
            }
        }
        throw new IllegalStateException(String.format("Fant ikke vilkår %s for behandling %s", vilkårKode.toString(), valgtBehandling.id));
    }

    private Vilkar hentVilkår(String vilkårKode) {
        return hentVilkår(new Kode("VILKAR_TYPE", vilkårKode));
    }

    public Kode vilkårStatus(String vilkårKode) {
        return hentVilkår(vilkårKode).getVilkarStatus();
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
        }, 60, "Behandlingsstatus var ikke " + status + " men var " + getBehandlingsstatus() + " i sak: " + valgtFagsak.saksnummer);
    }

    public boolean harBehandlingsstatus(String status) {
        return getBehandlingsstatus().equals(status);
    }

    public String getBehandlingsstatus() {
        return valgtBehandling.status.kode;
    }

    public void ventTilSakHarRevurdering() {
        ventTilSakHarBehandling(kodeverk.BehandlingType.getKode("BT-004"));
    }

    public void ventTilSakHarKlage() {
        ventTilSakHarBehandling(kodeverk.BehandlingType.getKode("BT-003"));
    }

    @Step("Venter på at fagsak får behandlingstype: {behandlingType}")
    protected void ventTilSakHarBehandling(Kode behandlingType) {
        if (harBehandling(behandlingType)) {
            return;
        }
        Vent.til(() -> {
            refreshFagsak();
            return harBehandling(behandlingType);
        }, 30, "Saken har ikke fått behandling av type: " + behandlingType);
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
    public void fattVedtakOgVentTilAvsluttetBehandling(FatterVedtakBekreftelse bekreftelse) {
        bekreftAksjonspunkt(bekreftelse);
        ventTilAvsluttetBehandling();
    }

    @Step("Fatter vedtak uten totrinnsbehandling og venter til sak er avsluttet")
    public void fattVedtakUtenTotrinnOgVentTilAvsluttetBehandling() {
        bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelseUtenTotrinn.class);
        ventTilAvsluttetBehandling();
    }

    @Step("Venter til saken er avsluttet")
    public void ventTilAvsluttetBehandling() {
        ventTilBehandlingsstatus("AVSLU");
    }

    /*
     * Private
     */

    /*
     * Opretter behandling på gitt fagsak
     */
    @Step("Oppretter behandling på gitt fagsak")
    private void opprettBehandling(Kode behandlingstype, Kode årsak, Fagsak fagsak) {
        behandlingerKlient.putBehandlinger(new BehandlingNy(fagsak.saksnummer, behandlingstype.kode, årsak == null ? null : årsak.kode));
        velgFagsak(valgtFagsak); // Henter fagsaken på ny
    }

    @Step("Henter prosesstasker for behandling")
    private List<ProsessTaskListItemDto> hentProsesstaskerForBehandling(Behandling behandling) {
        SokeFilterDto filter = new SokeFilterDto();
        filter.setSisteKjoeretidspunktFraOgMed(LocalDateTime.now().minusMinutes(5));
        filter.setSisteKjoeretidspunktTilOgMed(LocalDateTime.now());
        List<ProsessTaskListItemDto> prosesstasker = prosesstaskKlient.list(filter);
        return prosesstasker.stream().filter(p -> p.getTaskParametre().getBehandlingId() == "" + behandling.id).collect(Collectors.toList());
    }

    public boolean sakErKobletTilAnnenpart() {
        return getAnnenPartBehandling() != null;
    }

    public void mellomlagreOgGjennåpneKlage() {
        behandlingerKlient.mellomlagreGjennapne(
                new KlageVurderingResultatAksjonspunktMellomlagringDto(valgtBehandling, hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP)));
        refreshBehandling();
    }

    public boolean harRevurderingBehandling() {
        return harBehandling(kodeverk.BehandlingType.getKode("BT-004"));
    }

    public void ventTilFagsakAvsluttet() {
        ventTilFagsakstatus("AVSLU");
    }

    public void ventTilFagsakLøpende() {
        ventTilFagsakstatus("LOP");
    }

    public List<HistorikkInnslag> getHistorikkInnslag() {
        return get(historikkInnslag);
    }

    public Behandling getAnnenPartBehandling() {
        return get(annenPartBehandling);
    }
}
