package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger;

import static jakarta.ws.rs.client.Entity.json;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingHenlegg;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdPost;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingNy;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingPaVent;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.KlageVurderingResultatAksjonspunktMellomlagringDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftedeAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.OverstyrAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Familiehendelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KlageInfo;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KontrollerAktiviteskravPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KontrollerFaktaData;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Soknad;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Vilkar;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeid.InntektArbeidYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding.ArbeidOgInntektsmeldingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding.ManglendeOpplysningerVurderingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding.ManueltArbeidsforholdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.Beregningsresultat;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatMedUttaksplan;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.Medlem;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.opptjening.Opptjening;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.Tilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.BehandlingMedUttaksperioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.autotest.util.rest.StatusRange;

public class BehandlingerJerseyKlient extends FpsakJerseyKlient {

    private static final String UUID = "uuid";
    private static final String SAKSNUMMER = "saksnummer";
    private static final String BEHANDLINGID = "behandlingId";

    private static final String BEHANDLINGER_URL = "/behandlinger";
    private static final String BEHANDLINGER_STATUS_URL = "/behandling/status";
    private static final String BEHANDLINGER_SETT_PA_VENT_URL = BEHANDLINGER_URL + "/sett-pa-vent";
    private static final String BEHANDLINGER_HENLEGG_URL = BEHANDLINGER_URL + "/henlegg";
    private static final String BEHANDLINGER_GJENOPPTA_URL = BEHANDLINGER_URL + "/gjenoppta";
    private static final String BEHANDLINGER_ALLE_URL = BEHANDLINGER_URL + "/alle";
    private static final String BEHANDLINGER_ANNEN_PART_BEHANDLING_URL = BEHANDLINGER_URL + "/annen-part-behandling";

    private static final String BEHANDLING_URL = "/behandling";
    private static final String BEHANDLING_PERSON_MEDLEMSKAP = BEHANDLING_URL + "/person/medlemskap-v2";
    private static final String BEHANDLING_ENGANGSSTØNAD_URL = BEHANDLING_URL + "/beregningsresultat/engangsstonad";
    private static final String BEHANDLING_FORELDREPENGER_URL = BEHANDLING_URL + "/beregningsresultat/foreldrepenger";
    private static final String BEHANDLING_BEREGNINGSGRUNNALG_URL = BEHANDLING_URL + "/beregningsgrunnlag";
    private static final String BEHANDLING_VILKAAR_URL = BEHANDLING_URL + "/vilkar-v2";
    private static final String BEHANDLING_AKSJONSPUNKT_URL = BEHANDLING_URL + "/aksjonspunkt";
    private static final String BEHANDLING_AKSJONSPUNKT_GET_URL = BEHANDLING_URL + "/aksjonspunkt-v2";
    private static final String BEHANDLING_AKSJONSPUNKT_OVERSTYR_URL = BEHANDLING_AKSJONSPUNKT_URL + "/overstyr";
    private static final String BEHANDLING_SOKNAD_URL = BEHANDLING_URL + "/soknad";
    private static final String BEHANDLING_FAMILIE_HENDELSE_URL = BEHANDLING_URL + "/familiehendelse";
    private static final String BEHANDLING_OPPTJENING_URL = BEHANDLING_URL + "/opptjening";
    private static final String BEHANDLING_INNTEKT_ARBEID_YTELSE_URL = BEHANDLING_URL + "/inntekt-arbeid-ytelse";
    private static final String BEHANDLING_KLAGE_URL = BEHANDLING_URL + "/klage-v2";
    private static final String BEHANDLING_KLAGE_MELLOMLAGRE_URL = BEHANDLING_URL + "/klage/mellomlagre-klage";

    private static final String BEHANDLING_UTTAK = BEHANDLING_URL + "/uttak";
    private static final String BEHANDLING_UTTAK_KONTROLLER_FAKTA_PERIODER_URL = BEHANDLING_UTTAK + "/kontroller-fakta-perioder";
    private static final String BEHANDLING_UTTAK_KONTROLLER_AKTIVITETSKRAV_URL = BEHANDLING_UTTAK + "/kontroller-aktivitetskrav";
    private static final String BEHANDLING_UTTAK_STONADSKONTOER_URL = BEHANDLING_UTTAK + "/stonadskontoer";
    private static final String BEHANDLING_UTTAK_STONADSKONTOER_GITT_UTTAKSPERIODER_URL = BEHANDLING_UTTAK + "/stonadskontoerGittUttaksperioder";
    private static final String BEHANDLING_UTTAK_RESULTAT_PERIODER_URL = BEHANDLING_UTTAK + "/resultat-perioder";

    private static final String BEHANDLING_SVANGERSKAPSPENGER = BEHANDLING_URL + "/svangerskapspenger";
    private static final String BEHANDLING_SVANGERSKAPSPENGER_TILRETTELEGGING_URL = BEHANDLING_SVANGERSKAPSPENGER + "/tilrettelegging-v2";

    private static final String BEHANDLING_ARBEID_INNTEKTSMELDING = BEHANDLING_URL + "/arbeid-inntektsmelding";
    private static final String BEHANDLING_ARBEID_INNTEKTSMELDING_VURDERING = BEHANDLING_ARBEID_INNTEKTSMELDING + "/lagre-vurdering";
    private static final String BEHANDLING_ARBEID_INNTEKTSMELDING_OPPRETT_ARBEIDSFORHOLD = BEHANDLING_ARBEID_INNTEKTSMELDING + "/lagre-arbeidsforhold";
    private static final String BEHANDLING_ARBEID_INNTEKTSMELDING_NY_VURDERING = BEHANDLING_ARBEID_INNTEKTSMELDING + "/apne-for-ny-vurdering";

    public BehandlingerJerseyKlient(ClientRequestFilter filter) {
        super(filter);
    }

    public Behandling getBehandling(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Behandling.class);
    }

    public void putBehandlinger(BehandlingNy behandling) {
        client.target(base)
                .path(BEHANDLINGER_URL)
                .request(APPLICATION_JSON_TYPE)
                .put(json(behandling));
    }

    public AsyncPollingStatus statusAsObject(UUID behandlingUuid) {
        var response = client.target(base)
                .path(BEHANDLINGER_STATUS_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Response.class);
        if (StatusRange.STATUS_REDIRECT.inRange(response.getStatus())) {
            return null;
        } else {
            return response.readEntity(AsyncPollingStatus.class);
        }
    }


    public void settPaVent(BehandlingPaVent behandling) {
        client.target(base)
                .path(BEHANDLINGER_SETT_PA_VENT_URL)
                .request(APPLICATION_JSON_TYPE)
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .post(json(behandling));
    }


    public void henlegg(BehandlingHenlegg behandling) {
        client.target(base)
                .path(BEHANDLINGER_HENLEGG_URL)
                .request(APPLICATION_JSON_TYPE)
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .post(json(behandling));
    }


    public void gjenoppta(BehandlingIdPost behandling) {
        client.target(base)
                .path(BEHANDLINGER_GJENOPPTA_URL)
                .request(APPLICATION_JSON_TYPE)
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .post(json(behandling));
    }


    public List<Behandling> alle(long saksnummer) {
        return Optional.ofNullable(client.target(base)
                .path(BEHANDLINGER_ALLE_URL)
                .queryParam(SAKSNUMMER, saksnummer)
                .request()
                .get(Response.class)
                .readEntity(new GenericType<List<Behandling>>() {}))
                .orElse(List.of());
    }


    public Behandling annenPartBehandling(long saksnummer) {
        return client.target(base)
                .path(BEHANDLINGER_ANNEN_PART_BEHANDLING_URL)
                .queryParam(SAKSNUMMER, saksnummer)
                .request(APPLICATION_JSON_TYPE)
                .get(Behandling.class);
    }


    public Medlem behandlingMedlemskap(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_PERSON_MEDLEMSKAP)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Medlem.class);
    }


    public Beregningsgrunnlag behandlingBeregningsgrunnlag(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_BEREGNINGSGRUNNALG_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Beregningsgrunnlag.class);
    }


    public Beregningsresultat behandlingBeregningsresultatEngangsstønad(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_ENGANGSSTØNAD_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Beregningsresultat.class);
    }


    public BeregningsresultatMedUttaksplan behandlingBeregningsresultatForeldrepenger(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_FORELDREPENGER_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(BeregningsresultatMedUttaksplan.class);
    }


    public List<Vilkar> behandlingVilkår(UUID behandlingUuid) {
        return Optional.ofNullable(client.target(base)
                .path(BEHANDLING_VILKAAR_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Response.class)
                .readEntity(new GenericType<List<Vilkar>>() {}))
                .orElse(List.of());
    }


    public List<Aksjonspunkt> getBehandlingAksjonspunkt(UUID behandlingUuid) {
        return Optional.ofNullable(client.target(base)
                .path(BEHANDLING_AKSJONSPUNKT_GET_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Response.class)
                .readEntity(new GenericType<List<Aksjonspunkt>>() {}))
                .orElse(List.of());
    }


    public void postBehandlingAksjonspunkt(BekreftedeAksjonspunkter aksjonspunkter) {
        client.target(base)
                .path(BEHANDLING_AKSJONSPUNKT_URL)
                .request()
                .post(json(aksjonspunkter));
    }


    public void overstyr(OverstyrAksjonspunkter aksjonsunkter) {
        client.target(base)
                .path(BEHANDLING_AKSJONSPUNKT_OVERSTYR_URL)
                .request(APPLICATION_JSON_TYPE)
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .post(json(aksjonsunkter));
    }


    public Soknad behandlingSøknad(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_SOKNAD_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Soknad.class);
    }


    public Familiehendelse behandlingFamiliehendelse(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_FAMILIE_HENDELSE_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Familiehendelse.class);
    }


    public Opptjening behandlingOpptjening(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_OPPTJENING_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Opptjening.class);
    }


    public InntektArbeidYtelse behandlingInntektArbeidYtelse(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_INNTEKT_ARBEID_YTELSE_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(InntektArbeidYtelse.class);
    }


    public KlageInfo klage(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_KLAGE_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(KlageInfo.class);
    }


    public void mellomlagre(KlageVurderingResultatAksjonspunktMellomlagringDto vurdering) {
        client.target(base)
                .path(BEHANDLING_KLAGE_MELLOMLAGRE_URL)
                .request(APPLICATION_JSON_TYPE)
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .post(json(vurdering));
    }

    /*
     * hent stønadskontoer for behandling
     */
    public Saldoer behandlingUttakStonadskontoer(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_UTTAK_STONADSKONTOER_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Saldoer.class);
    }

    /*
     * hent stønadskontoer for behandling gitt uttaksperioder
     */
    public Saldoer behandlingUttakStonadskontoerGittUttaksperioder(BehandlingMedUttaksperioderDto uttaksperioderDto) {
        return client.target(base)
                .path(BEHANDLING_UTTAK_STONADSKONTOER_GITT_UTTAKSPERIODER_URL)
                .request(APPLICATION_JSON_TYPE)
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .post(json(uttaksperioderDto), Saldoer.class);
    }

    /*
     * hent kontroller fakta for behandling
     */
    public KontrollerFaktaData behandlingKontrollerFaktaPerioder(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_UTTAK_KONTROLLER_FAKTA_PERIODER_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(KontrollerFaktaData.class);
    }

    /*
     * hent kontroller fakta for behandling
     */
    public List<KontrollerAktiviteskravPeriode> behandlingKontrollerAktivitetskrav(UUID behandlingUuid) {
        return Optional.ofNullable(client.target(base)
                .path(BEHANDLING_UTTAK_KONTROLLER_AKTIVITETSKRAV_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Response.class)
                .readEntity(new GenericType<List<KontrollerAktiviteskravPeriode>>() {}))
                .orElse(List.of());
    }

    /*
     * hent resultat perioder for behandling
     */
    public UttakResultatPerioder behandlingUttakResultatPerioder(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_UTTAK_RESULTAT_PERIODER_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(UttakResultatPerioder.class);
    }

    /*
     * hent tilrettelegging for behandling
     */
    public Tilrettelegging behandlingTilrettelegging(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_SVANGERSKAPSPENGER_TILRETTELEGGING_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Tilrettelegging.class);
    }

    /*
     * hent arbeid, inntekt og inntektsmeldinger
     */
    public ArbeidOgInntektsmeldingDto behandlingArbeidInntektsmelding(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLING_ARBEID_INNTEKTSMELDING)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(ArbeidOgInntektsmeldingDto.class);
    }

    public void behandlingArbeidInntektsmeldingLagreArbfor(ManueltArbeidsforholdDto arbeidsforhold) {
        client.target(base)
                .path(BEHANDLING_ARBEID_INNTEKTSMELDING_OPPRETT_ARBEIDSFORHOLD)
                .request(APPLICATION_JSON_TYPE)
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .post(json(arbeidsforhold));
    }

    public void behandlingArbeidInntektsmeldingNyVurdering(BehandlingIdPost behandlingIdDto) {
        client.target(base)
                .path(BEHANDLING_ARBEID_INNTEKTSMELDING_NY_VURDERING)
                .request(APPLICATION_JSON_TYPE)
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .post(json(behandlingIdDto));
    }

    public void behandlingArbeidInntektsmeldingLagreValg(ManglendeOpplysningerVurderingDto manglendeOpplysningerVurderingDto) {
        client.target(base)
                .path(BEHANDLING_ARBEID_INNTEKTSMELDING_VURDERING)
                .request(APPLICATION_JSON_TYPE)
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .post(json(manglendeOpplysningerVurderingDto));
    }
}
