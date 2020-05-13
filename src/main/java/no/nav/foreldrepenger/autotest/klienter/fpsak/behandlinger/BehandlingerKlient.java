package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpResponse;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingHenlegg;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdPost;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingNy;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingPaVent;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.KlageVurderingResultatAksjonspunktMellomlagringDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.Ytelsefordeling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftedeAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.OverstyrAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Familiehendelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.InnsynInfo;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KlageInfo;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KontrollerFaktaData;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Personopplysning;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Soknad;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Verge;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Vilkar;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeid.InntektArbeidYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.Beregningsresultat;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatMedUttaksplan;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.Medlem;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.opptjening.Opptjening;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.Tilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.BehandlingMedUttaksperioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.PeriodeGrense;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

public class BehandlingerKlient extends FpsakKlient {

    private static final String BEHANDLINGER_URL = "/behandlinger";

    private static final String BEHANDLINGER_STATUS_URL = "/behandling/status";
    private static final String BEHANDLINGER_SETT_PA_VENT_URL = BEHANDLINGER_URL + "/sett-pa-vent";
    private static final String BEHANDLINGER_ENDRE_PA_VENT_URL = BEHANDLINGER_URL + "/endre-pa-vent";
    private static final String BEHANDLINGER_HENLEGG_URL = BEHANDLINGER_URL + "/henlegg";
    private static final String BEHANDLINGER_GJENOPPTA_URL = BEHANDLINGER_URL + "/gjenoppta";
    private static final String BEHANDLINGER_BYTT_ENHET_URL = BEHANDLINGER_URL + "/bytt-enhet";
    private static final String BEHANDLINGER_ALLE_URL = BEHANDLINGER_URL + "/alle?saksnummer=%s";
    private static final String BEHANDLINGER_ANNEN_PART_BEHANDLING_URL = BEHANDLINGER_URL + "/annen-part-behandling?saksnummer=%s";

    private static final String BEHANDLING_URL = "/behandling";
    private static final String BEHANDLING_PERSONOPPLYSNINGER_URL = BEHANDLING_URL + "/person/personopplysninger";
    private static final String BEHANDLING_VERGE_URL = BEHANDLING_URL + "/person/verge";
    private static final String BEHANDLING_PERSON_MEDLEMSKAP = BEHANDLING_URL + "/person/medlemskap-v2";
    private static final String BEHANDLING_ENGANGSSTØNAD_URL = BEHANDLING_URL + "/beregningsresultat/engangsstonad";
    private static final String BEHANDLING_FORELDREPENGER_URL = BEHANDLING_URL + "/beregningsresultat/foreldrepenger";
    private static final String BEHANDLING_BEREGNINGSGRUNNALG_URL = BEHANDLING_URL + "/beregningsgrunnlag";
    private static final String BEHANDLING_VILKAAR_URL = BEHANDLING_URL + "/vilkar-v2";
    private static final String BEHANDLING_AKSJONSPUNKT_URL = BEHANDLING_URL + "/aksjonspunkt";
    private static final String BEHANDLING_AKSJONSPUNKT_GET_URL = "/behandling/aksjonspunkt-v2";
    private static final String BEHANDLING_AKSJONSPUNKT_OVERSTYR_URL = BEHANDLING_AKSJONSPUNKT_URL + "/overstyr";
    private static final String BEHANDLING_SOKNAD_URL = BEHANDLING_URL + "/soknad";
    private static final String BEHANDLING_FAMILIE_HENDELSE_URL = BEHANDLING_URL + "/familiehendelse";
    private static final String BEHANDLING_OPPTJENING_URL = BEHANDLING_URL + "/opptjening";
    private static final String BEHANDLING_INNTEKT_ARBEID_YTELSE_URL = BEHANDLING_URL + "/inntekt-arbeid-ytelse";
    private static final String BEHANDLING_INNSYN_URL = BEHANDLING_URL + "/innsyn";
    private static final String BEHANDLING_KLAGE_URL = BEHANDLING_URL + "/klage-v2";
    private static final String BEHANDLING_KLAGE_MELLOMLAGRE_URL = BEHANDLING_URL + "/klage/mellomlagre-klage";
    private static final String BEHANDLING_KLAGE_MELLOMLAGRE_GJENNÅPNE_URL = BEHANDLING_URL + "/klage/mellomlagre-gjennapne-klage";
    private static final String BEHANDLING_YTELSEFORDELING_URL = BEHANDLING_URL + "/ytelsefordeling";

    private static final String BEHANDLING_UTTAK = BEHANDLING_URL + "/uttak";
    private static final String BEHANDLING_UTTAK_KONTROLLER_FAKTA_PERIODER_URL = BEHANDLING_UTTAK + "/kontroller-fakta-perioder";
    private static final String BEHANDLING_UTTAK_STONADSKONTOER_URL = BEHANDLING_UTTAK + "/stonadskontoer";
    private static final String BEHANDLING_UTTAK_STONADSKONTOER_GITT_UTTAKSPERIODER_URL = BEHANDLING_UTTAK + "/stonadskontoerGittUttaksperioder";
    private static final String BEHANDLING_UTTAK_RESULTAT_PERIODER_URL = BEHANDLING_UTTAK + "/resultat-perioder";
    private static final String BEHANDLING_UTTAK_PERIODE_GRENSE_URL = BEHANDLING_UTTAK + "/periode-grense";

    private static final String BEHANDLING_SVANGERSKAPSPENGER = BEHANDLING_URL + "/svangerskapspenger";
    private static final String BEHANDLING_SVANGERSKAPSPENGER_TILRETTELEGGING_URL = BEHANDLING_SVANGERSKAPSPENGER + "/tilrettelegging?behandlingId=%s";


    public BehandlingerKlient(HttpSession session) {
        super(session);
    }

    private String createBehandlingGetUrl(String path, UUID behandlingUuid) {
        UriBuilder builder = UriBuilder.fromPath(hentRestRotUrl() + path);
        builder.queryParam("uuid", behandlingUuid);

        return builder.toString();
    }

    private String createBehandlingGetUrl(String path, UUID behandlingUuid, Integer gruppe) {
        UriBuilder builder = UriBuilder.fromPath(hentRestRotUrl() + path);
        builder.queryParam("uuid", behandlingUuid);
        if (Optional.ofNullable(gruppe).orElse(0) != 0) {
            builder.queryParam("gruppe", gruppe);
        }
        return builder.toString();
    }

    /*
     * Hent Behandling data
     */
    @Step("Henter behandlingsdata")
    public Behandling getBehandling(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_URL, behandlingUuid);
        return getOgHentJson(url, Behandling.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Opprett ny behandling
     */
    @Step("Oppretter ny behandling")
    public void putBehandlinger(BehandlingNy behandling) {
        String url = hentRestRotUrl() + BEHANDLINGER_URL;
        putJson(url, behandling, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent status for behandling
     */
    @Step("Henter status for behandling")
    public AsyncPollingStatus statusAsObject(UUID behandlingUuid, Integer gruppe) {
        HttpResponse response = status(behandlingUuid, gruppe);
        if (StatusRange.STATUS_REDIRECT.inRange(response.getStatusLine().getStatusCode())) {
            return null;
        } else {
            return fromJson(hentResponseBody(response), AsyncPollingStatus.class);
        }
    }

    private HttpResponse status(UUID behandlingUuid, Integer gruppe) {
        try {
            session.setRedirect(false);
            String url = createBehandlingGetUrl(BEHANDLINGER_STATUS_URL, behandlingUuid, gruppe);
            return getJson(url);
        } finally {
            session.setRedirect(true);
        }
    }

    /*
     * Set behandling på vent
     */
    @Step("Setter behandling på vent")
    public void settPaVent(BehandlingPaVent behandling) {
        String url = hentRestRotUrl() + BEHANDLINGER_SETT_PA_VENT_URL;
        postOgVerifiser(url, behandling, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Endre behandling på vent
     */
    public void endrePaVent(BehandlingPaVent behandling) {
        String url = hentRestRotUrl() + BEHANDLINGER_ENDRE_PA_VENT_URL;
        postOgVerifiser(url, behandling, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Henlegg behandling
     */
    @Step("Henlegger behandling")
    public void henlegg(BehandlingHenlegg behandling) {
        String url = hentRestRotUrl() + BEHANDLINGER_HENLEGG_URL;
        postOgVerifiser(url, behandling, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Gjenoppta behandling på vent
     */
    @Step("Gjenopptar behandling")
    public void gjenoppta(BehandlingIdPost behandling) {
        String url = hentRestRotUrl() + BEHANDLINGER_GJENOPPTA_URL;
        postOgVerifiser(url, behandling, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent alle behandlinger
     */
    @Step("Henter alle behandlinger")
    public List<Behandling> alle(long saksnummer) {
        String url = hentRestRotUrl() + String.format(BEHANDLINGER_ALLE_URL, saksnummer);
        return getOgHentJson(url, hentObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, Behandling.class), StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent behandling for annen part
     */
    @Step("Henter behandling for annenpart {saksnummer}")
    public Behandling annenPartBehandling(long saksnummer) {
        String url = hentRestRotUrl() + String.format(BEHANDLINGER_ANNEN_PART_BEHANDLING_URL, saksnummer);
        return getOgHentJson(url, Behandling.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent personopplysninger for behandling
     */
    @Step("Henter personopplysninger for behandling")
    public Personopplysning behandlingPersonopplysninger(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_PERSONOPPLYSNINGER_URL, behandlingUuid);
        return getOgHentJson(url, Personopplysning.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent verge for behandling
     */
    @Step("Henter verge for behandling")
    public Verge behandlingVerge(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_VERGE_URL, behandlingUuid);
        return getOgHentJson(url, Verge.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent medlemskap for behandling
     */
    @Step("Henter medlemskap for behandling")
    public Medlem behandlingMedlemskap(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_PERSON_MEDLEMSKAP, behandlingUuid);
        return getOgHentJson(url, Medlem.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent beregningsgrunnlag for behandling
     */
    @Step("Henter beregningsgrunnlag for behandling")
    public Beregningsgrunnlag behandlingBeregningsgrunnlag(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_BEREGNINGSGRUNNALG_URL, behandlingUuid);
        return getOgHentJson(url, Beregningsgrunnlag.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent beregningsresultat engangstønad for behandling
     */
    @Step("Henter beregningsresultat engangstønad for behandling")
    public Beregningsresultat behandlingBeregningsresultatEngangsstønad(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_ENGANGSSTØNAD_URL, behandlingUuid);
        return getOgHentJson(url, Beregningsresultat.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent beregningsresultat foreldrepenger for behandling
     */
    @Step("Henter beregningsresultat foreldrepenger for behandling")
    public BeregningsresultatMedUttaksplan behandlingBeregningsresultatForeldrepenger(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_FORELDREPENGER_URL, behandlingUuid);
        return getOgHentJson(url, BeregningsresultatMedUttaksplan.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent vilkår for behandling
     */
    @Step("Henter ut vilkår for behanlding {behandlingUuid}")
    public List<Vilkar> behandlingVilkår(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_VILKAAR_URL, behandlingUuid);
        return getOgHentJson(url, hentObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, Vilkar.class), StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent aksjonspunkter for behandling
     */
    @Step("Henter aksjonspunkter for behandling {behandlingUuid}")
    public List<Aksjonspunkt> getBehandlingAksjonspunkt(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_AKSJONSPUNKT_GET_URL, behandlingUuid);
        return getOgHentJson(url, hentObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, Aksjonspunkt.class), StatusRange.STATUS_SUCCESS);
    }

    @Step("Sender inn aksjonspunktbekreftelse.")
    public void postBehandlingAksjonspunkt(BekreftedeAksjonspunkter aksjonsunkter) {
        String url = hentRestRotUrl() + BEHANDLING_AKSJONSPUNKT_URL;
        postOgVerifiser(url, aksjonsunkter, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Overstyring
     */
    @Step("Overstyrer aksjonspunkter")
    public void overstyr(OverstyrAksjonspunkter aksjonsunkter) {
        String url = hentRestRotUrl() + BEHANDLING_AKSJONSPUNKT_OVERSTYR_URL;
        postOgVerifiser(url, aksjonsunkter, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent søknad for behandling
     */
    @Step("Henter søknad for behandling")
    public Soknad behandlingSøknad(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_SOKNAD_URL, behandlingUuid);
        return getOgHentJson(url, Soknad.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * hent familiehendelse for behandling
     */
    @Step("Henter familiehendelse for behandling")
    public Familiehendelse behandlingFamiliehendelse(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_FAMILIE_HENDELSE_URL, behandlingUuid);
        return getOgHentJson(url, Familiehendelse.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent opptjening for behandling
     */
    @Step("Henter opptjening for behandling")
    public Opptjening behandlingOpptjening(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_OPPTJENING_URL, behandlingUuid);
        return getOgHentJson(url, Opptjening.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * hent inntekt arbeid ytelse for behandling
     */
    @Step("Henter inntekt arbeidsytelse for behandling")
    public InntektArbeidYtelse behandlingInntektArbeidYtelse(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_INNTEKT_ARBEID_YTELSE_URL, behandlingUuid);
        return getOgHentJson(url, InntektArbeidYtelse.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent insynsinfo for behandling
     */
    @Step("Henter innsyn for behandling")
    public InnsynInfo innsyn(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_INNSYN_URL, behandlingUuid);
        return getOgHentJson(url, InnsynInfo.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent klageinfo for behandling
     */
    @Step("Henter klageinfo for behandling")
    public KlageInfo klage(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_KLAGE_URL, behandlingUuid);
        return getOgHentJson(url, KlageInfo.class, StatusRange.STATUS_SUCCESS);
    }

    @Step("Mellomlagrer klage")
    public void mellomlagre(KlageVurderingResultatAksjonspunktMellomlagringDto vurdering) {
        String url = hentRestRotUrl() + BEHANDLING_KLAGE_MELLOMLAGRE_URL;
        postOgVerifiser(url, vurdering, StatusRange.STATUS_SUCCESS);
    }

    @Step("Mellomlagrer og gjennåpner klage")
    public void mellomlagreGjennapne(KlageVurderingResultatAksjonspunktMellomlagringDto vurdering) {
        String url = hentRestRotUrl() + BEHANDLING_KLAGE_MELLOMLAGRE_GJENNÅPNE_URL;
        postOgVerifiser(url, vurdering, StatusRange.STATUS_SUCCESS);
    }

    /*
     * Hent info om ytelsesfordeling
     */
    @Step("Henter informasjon om ytelsesfordeling")
    public Ytelsefordeling ytelsefordeling(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_YTELSEFORDELING_URL, behandlingUuid);
        return getOgHentJson(url, Ytelsefordeling.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * hent stønadskontoer for behandling
     */
    @Step("Henter stønadskontoer for behandling")
    public Saldoer behandlingUttakStonadskontoer(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_UTTAK_STONADSKONTOER_URL, behandlingUuid);
        return getOgHentJson(url, Saldoer.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * hent stønadskontoer for behandling gitt uttaksperioder
     */
    @Step("Henter stønadskontoer for behandling gitt uttaksperioder")
    public Saldoer behandlingUttakStonadskontoerGittUttaksperioder(BehandlingMedUttaksperioderDto uttaksperioderDto) {
        String url = hentRestRotUrl() + BEHANDLING_UTTAK_STONADSKONTOER_GITT_UTTAKSPERIODER_URL;
        return postOgHentJson(url, uttaksperioderDto, Saldoer.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * hent kontroller fakta for behandling
     */
    @Step("Kontrollerer fakta for behandling")
    public KontrollerFaktaData behandlingKontrollerFaktaPerioder(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_UTTAK_KONTROLLER_FAKTA_PERIODER_URL, behandlingUuid);
        return getOgHentJson(url, KontrollerFaktaData.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * hent resultat perioder for behandling
     */
    @Step("Henter resultatperioder for behandling")
    public UttakResultatPerioder behandlingUttakResultatPerioder(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_UTTAK_RESULTAT_PERIODER_URL, behandlingUuid);
        return getOgHentJson(url, UttakResultatPerioder.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * hent periode grense for behandling
     */
    @Step("Henter periode grense for behandling")
    public PeriodeGrense behandlingUttakPeriodeGrense(UUID behandlingUuid) {
        String url = createBehandlingGetUrl(BEHANDLING_UTTAK_PERIODE_GRENSE_URL, behandlingUuid);
        return getOgHentJson(url, PeriodeGrense.class, StatusRange.STATUS_SUCCESS);
    }

    /*
     * hent tilrettelegging for behandling
     */
    @Step("Henter tilrettelegging for behandling")
    public Tilrettelegging behandlingTilrettelegging(Integer behandlingId) {
        String url = hentRestRotUrl() + String.format(BEHANDLING_SVANGERSKAPSPENGER_TILRETTELEGGING_URL, behandlingId);
        return getOgHentJson(url,Tilrettelegging.class,StatusRange.STATUS_SUCCESS);
    }

}
