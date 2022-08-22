package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger;

import static jakarta.ws.rs.core.HttpHeaders.LOCATION;
import static jakarta.ws.rs.core.Response.Status.ACCEPTED;
import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.BaseUriProvider.FPSAK_BASE;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.sendStringRequest;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus.Status.CANCELLED;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus.Status.HALTED;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingHenlegg;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdDto;
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
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.feriepenger.Feriepengegrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.Medlem;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.opptjening.Opptjening;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.Tilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.BehandlingMedUttaksperioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class BehandlingerKlient {
    private final Logger LOG = LoggerFactory.getLogger(BehandlingerKlient.class);

    private static final String UUID_NAME = "uuid";
    private static final String SAKSNUMMER_NAME = "saksnummer";

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
    private static final String BEHANDLING_FERIEPENGER_URL = BEHANDLING_URL + "/feriepengegrunnlag";
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

    public Behandling getBehandling(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Behandling.class);
    }

    public Behandling initHentBehandling(UUID behandlingsuuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLINGER_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(new BehandlingIdDto(behandlingsuuid))));
        return følgRedirectTilStatusOgReturnerBehandlingNårTilgjenglig(request);

    }

    private Behandling følgRedirectTilStatusOgReturnerBehandlingNårTilgjenglig(HttpRequest.Builder request) {
        var response = sendStringRequest(request.build());
        if (response.statusCode() == ACCEPTED.getStatusCode()) {
            var requestTilStatusEndepunkt = getRequestBuilder()
                    .uri(URI.create(hentRedirectUriFraLocationHeader(response)))
                    .GET();
            return Vent.på(() -> getBehandlingHvisTilgjenglig(requestTilStatusEndepunkt), 30,
                    "Behandling ikke tilgjenglig etter X sekund");
        }
        throw new RuntimeException("Uventet tilstand. Skal ikke være mulig!");
    }

    private static String hentRedirectUriFraLocationHeader(HttpResponse<String> response) {
        return response.headers().firstValue(LOCATION).get();  // TODO: Fiks
    }

    /**
     * Returnerer en redirect 303 til enten
     * 1) Behandling polle status (se håndtering hentBehandlingHvisTilgjenglig)
     * 2) Fagsak polle status
     *  - 200 hvis vi finner fagsaken og den er tilgjenglig
     *  - 403 hvis den ikke er tilgjenglig!
     *
     *  Denne metoden støtter bare tilfelle 1)
     *
     * @param behandling
     */
    public Behandling opprettBehandlingManuelt(BehandlingNy behandling) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLINGER_URL)
                        .build())
                .PUT(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(behandling)));
        return følgRedirectTilStatusOgReturnerBehandlingNårTilgjenglig(request);
    }

    // Sjekk om vi kan bruke en async klient her istedenfor.
    public Behandling hentBehandlingHvisTilgjenglig(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLINGER_STATUS_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return getBehandlingHvisTilgjenglig(request);
    }

    private Behandling getBehandlingHvisTilgjenglig(HttpRequest.Builder request) {
        var response = sendStringRequest(request.build());
        if (response.statusCode() == 303) {
            return followRedirectOgHentBehandling(URI.create(hentRedirectUriFraLocationHeader(response)));
        }

        var asyncPollingStatus = JacksonBodyHandlers.fromJson(response.body(), AsyncPollingStatus.class);
        if (asyncPollingStatus.getStatus().equals(HALTED) || asyncPollingStatus.getStatus().equals(CANCELLED)) {
            throw new IllegalStateException("Prosesstask i vrang tilstand: " + asyncPollingStatus.getMessage());
        }
        LOG.info("Behandlingen er ikke ferdig prosessert, men har status {}", asyncPollingStatus.getStatus());
        return null;
    }

    private Behandling followRedirectOgHentBehandling(URI redirectUri) {
        var redirect = getRequestBuilder()
                .uri(redirectUri)
                .GET();
        return send(redirect.build(), Behandling.class);
    }

    public void settPaVent(BehandlingPaVent behandling) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLINGER_SETT_PA_VENT_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(behandling)));
        send(request.build());
    }


    public void henlegg(BehandlingHenlegg behandling) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLINGER_HENLEGG_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(behandling)));
        send(request.build());
    }


    // TODO: Redirecter til Behandling. Bruk dette istedenfor vent på historikk.
    public Behandling gjenoppta(BehandlingIdDto behandling) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLINGER_GJENOPPTA_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(behandling)));
        return følgRedirectTilStatusOgReturnerBehandlingNårTilgjenglig(request);
    }


    public List<Behandling> alle(Saksnummer saksnummer) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLINGER_ALLE_URL)
                        .queryParam(SAKSNUMMER_NAME, saksnummer.value())
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<Behandling>>() {}))
                .orElse(List.of());
    }


    public Behandling annenPartBehandling(Saksnummer saksnummer) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLINGER_ANNEN_PART_BEHANDLING_URL)
                        .queryParam(SAKSNUMMER_NAME, saksnummer.value())
                        .build())
                .GET();
        return send(request.build(), Behandling.class);
    }


    public Medlem behandlingMedlemskap(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_PERSON_MEDLEMSKAP)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Medlem.class);
    }


    public Beregningsgrunnlag behandlingBeregningsgrunnlag(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_BEREGNINGSGRUNNALG_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Beregningsgrunnlag.class);
    }


    public Beregningsresultat behandlingBeregningsresultatEngangsstønad(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_ENGANGSSTØNAD_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Beregningsresultat.class);
    }


    public BeregningsresultatMedUttaksplan behandlingBeregningsresultatForeldrepenger(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_FORELDREPENGER_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), BeregningsresultatMedUttaksplan.class);
    }

    public Feriepengegrunnlag behandlingFeriepengegrunnlag(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_FERIEPENGER_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Feriepengegrunnlag.class);
    }


    public List<Vilkar> behandlingVilkår(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_VILKAAR_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<Vilkar>>() {}))
                .orElse(List.of());
    }


    public List<Aksjonspunkt> getBehandlingAksjonspunkt(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_AKSJONSPUNKT_GET_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<Aksjonspunkt>>() {}))
                .orElse(List.of());
    }


    public void postBehandlingAksjonspunkt(BekreftedeAksjonspunkter aksjonspunkter) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_AKSJONSPUNKT_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(aksjonspunkter)));
        send(request.build());
    }


    public void overstyr(OverstyrAksjonspunkter aksjonspunkter) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_AKSJONSPUNKT_OVERSTYR_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(aksjonspunkter)));
        send(request.build());
    }


    public Soknad behandlingSøknad(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_SOKNAD_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Soknad.class);
    }


    public Familiehendelse behandlingFamiliehendelse(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_FAMILIE_HENDELSE_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Familiehendelse.class);
    }


    public Opptjening behandlingOpptjening(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_OPPTJENING_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Opptjening.class);
    }


    public InntektArbeidYtelse behandlingInntektArbeidYtelse(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_INNTEKT_ARBEID_YTELSE_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), InntektArbeidYtelse.class);
    }


    public KlageInfo klage(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_KLAGE_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), KlageInfo.class);
    }


    public void mellomlagre(KlageVurderingResultatAksjonspunktMellomlagringDto vurdering) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_KLAGE_MELLOMLAGRE_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(vurdering)));
        send(request.build());
    }

    /*
     * hent stønadskontoer for behandling
     */
    public Saldoer behandlingUttakStonadskontoer(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_UTTAK_STONADSKONTOER_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Saldoer.class);
    }

    /*
     * hent stønadskontoer for behandling gitt uttaksperioder
     */
    public Saldoer behandlingUttakStonadskontoerGittUttaksperioder(BehandlingMedUttaksperioderDto uttaksperioderDto) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_UTTAK_STONADSKONTOER_GITT_UTTAKSPERIODER_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(uttaksperioderDto)));
        return send(request.build(), Saldoer.class);
    }

    /*
     * hent kontroller fakta for behandling
     */
    public KontrollerFaktaData behandlingKontrollerFaktaPerioder(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_UTTAK_KONTROLLER_FAKTA_PERIODER_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), KontrollerFaktaData.class);
    }

    /*
     * hent kontroller fakta for behandling
     */
    public List<KontrollerAktiviteskravPeriode> behandlingKontrollerAktivitetskrav(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_UTTAK_KONTROLLER_AKTIVITETSKRAV_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<KontrollerAktiviteskravPeriode>>() {}))
                .orElse(List.of());
    }

    /*
     * hent resultat perioder for behandling
     */
    public UttakResultatPerioder behandlingUttakResultatPerioder(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_UTTAK_RESULTAT_PERIODER_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), UttakResultatPerioder.class);
    }

    /*
     * hent tilrettelegging for behandling
     */
    public Tilrettelegging behandlingTilrettelegging(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_SVANGERSKAPSPENGER_TILRETTELEGGING_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Tilrettelegging.class);
    }

    /*
     * hent arbeid, inntekt og inntektsmeldinger
     */
    public ArbeidOgInntektsmeldingDto behandlingArbeidInntektsmelding(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_ARBEID_INNTEKTSMELDING)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), ArbeidOgInntektsmeldingDto.class);
    }

    public void behandlingArbeidInntektsmeldingLagreArbfor(ManueltArbeidsforholdDto arbeidsforhold) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_ARBEID_INNTEKTSMELDING_OPPRETT_ARBEIDSFORHOLD)
                        .build())
//                .header("Cookie", "ID_token=\"eyJraWQiOiIxIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwOi8vdnRwOjgwNjAvcmVzdC9pc3NvL29hdXRoMiIsImV4cCI6MTY2MTA0MDQwNywianRpIjoiUVMxZmplRUhpZkZQNkNjQktZa08tQSIsImlhdCI6MTY2MTAxODgwNywic3ViIjoib3ZlcnN0eSIsImF1ZCI6Ik9JREMiLCJhY3IiOiJMZXZlbDQiLCJhenAiOiJPSURDIn0.LNfQSyKrD7eoI4GslF746WF51VQnDA-5wtLPooaUezvkWjZ_ETz9dj063aOX07VxQW1f_BnLvGsU3qV73bLIod06zM8bW9kiQrsLQq_An_9_fgxIxMw95Q7O1OsBOZ73bnPdNbFEeJ4Ro-bpnzyHm8xSRnHuvZkdoQugVhGy544EmRQOqJQ56mkF5ESAXtb_DApgrH4Q1E84R1-q9tTFfs_7MclRk8Is-M4dtTYluYF3lwOC3NnXIs4w1Yot4ZQqnQi1oUnLxDHhGFpl6usChgNMqdC5IoGXNIivPXOA6Sl8yG-fGKeKDYiHOEIqambSkG2vo_3FgUBwH1cqfpvXkw\";$Path=\"/\";$Domain=\"127.0.0.1\"")
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(arbeidsforhold)));
        send(request.build());
    }

    public void behandlingArbeidInntektsmeldingNyVurdering(BehandlingIdDto behandlingIdDto) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_ARBEID_INNTEKTSMELDING_NY_VURDERING)
                        .build())
//                .header("Cookie", "ID_token=\"eyJraWQiOiIxIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwOi8vdnRwOjgwNjAvcmVzdC9pc3NvL29hdXRoMiIsImV4cCI6MTY2MTA0MDQwNywianRpIjoiUVMxZmplRUhpZkZQNkNjQktZa08tQSIsImlhdCI6MTY2MTAxODgwNywic3ViIjoib3ZlcnN0eSIsImF1ZCI6Ik9JREMiLCJhY3IiOiJMZXZlbDQiLCJhenAiOiJPSURDIn0.LNfQSyKrD7eoI4GslF746WF51VQnDA-5wtLPooaUezvkWjZ_ETz9dj063aOX07VxQW1f_BnLvGsU3qV73bLIod06zM8bW9kiQrsLQq_An_9_fgxIxMw95Q7O1OsBOZ73bnPdNbFEeJ4Ro-bpnzyHm8xSRnHuvZkdoQugVhGy544EmRQOqJQ56mkF5ESAXtb_DApgrH4Q1E84R1-q9tTFfs_7MclRk8Is-M4dtTYluYF3lwOC3NnXIs4w1Yot4ZQqnQi1oUnLxDHhGFpl6usChgNMqdC5IoGXNIivPXOA6Sl8yG-fGKeKDYiHOEIqambSkG2vo_3FgUBwH1cqfpvXkw\";$Path=\"/\";$Domain=\"127.0.0.1\"")
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(behandlingIdDto)));
        var response = sendStringRequest(request.build());
//        LOG.info("Body i request {}", JacksonBodyHandlers.toJson(behandlingIdDto));
//        var response = sendStringRequest(request);

//        LOG.info("Responese {}", response);
//        LOG.info("Responese {} {}", response.body(), response.headers());
    }

    public void behandlingArbeidInntektsmeldingLagreValg(ManglendeOpplysningerVurderingDto manglendeOpplysningerVurderingDto) {
        var request = getRequestBuilder()
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_ARBEID_INNTEKTSMELDING_VURDERING)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(manglendeOpplysningerVurderingDto)));
        send(request.build());
    }
}
