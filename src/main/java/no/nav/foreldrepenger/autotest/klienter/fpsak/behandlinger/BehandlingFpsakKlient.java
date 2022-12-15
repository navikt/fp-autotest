package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.BaseUriProvider.FPSAK_BASE;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingKlientFelles.BEHANDLINGER_URL;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingKlientFelles.BEHANDLING_AKSJONSPUNKT_URL;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingKlientFelles.BEHANDLING_URL;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingKlientFelles.SAKSNUMMER_NAME;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingKlientFelles.UUID_NAME;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingHenlegg;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingNy;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.KlageVurderingResultatAksjonspunktMellomlagringDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.SettBehandlingPaVentDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftedeAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.OverstyrAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KlageInfo;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KontrollerAktiviteskravPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KontrollerFaktaData;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Soknad;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Vilkar;
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
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class BehandlingFpsakKlient implements BehandlingerKlient {

    private static final String API_NAME = "fpsak";
    private static final String BEHANDLINGER_ANNEN_PART_BEHANDLING_URL = BEHANDLINGER_URL + "/annen-part-behandling";

    private static final String BEHANDLINGER_STATUS_FPSAK_URL = BEHANDLING_URL + "/status";
    private static final String BEHANDLING_PERSON_MEDLEMSKAP = BEHANDLING_URL + "/person/medlemskap-v2";
    private static final String BEHANDLING_ENGANGSSTØNAD_URL = BEHANDLING_URL + "/beregningsresultat/engangsstonad";
    private static final String BEHANDLING_FORELDREPENGER_URL = BEHANDLING_URL + "/beregningsresultat/foreldrepenger";
    private static final String BEHANDLING_FERIEPENGER_URL = BEHANDLING_URL + "/feriepengegrunnlag";
    private static final String BEHANDLING_BEREGNINGSGRUNNALG_URL = BEHANDLING_URL + "/beregningsgrunnlag";
    private static final String BEHANDLING_VILKAAR_URL = BEHANDLING_URL + "/vilkar-v2";
    private static final String BEHANDLING_AKSJONSPUNKT_GET_FPSAK_URL = BEHANDLING_URL + "/aksjonspunkt-v2";
    private static final String BEHANDLING_AKSJONSPUNKT_OVERSTYR_URL = BEHANDLING_AKSJONSPUNKT_URL + "/overstyr";
    private static final String BEHANDLING_SOKNAD_URL = BEHANDLING_URL + "/soknad";
    private static final String BEHANDLING_OPPTJENING_URL = BEHANDLING_URL + "/opptjening";
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

    private final SaksbehandlerRolle saksbehandlerRolle;
    private final BehandlingKlientFelles behandlingerBasicKlient;

    public BehandlingFpsakKlient(SaksbehandlerRolle saksbehandlerRolle) {
        this.saksbehandlerRolle = saksbehandlerRolle;
        behandlingerBasicKlient = new BehandlingKlientFelles(saksbehandlerRolle, FPSAK_BASE, BEHANDLINGER_STATUS_FPSAK_URL, BEHANDLING_AKSJONSPUNKT_GET_FPSAK_URL,
                API_NAME);
    }

    @Override
    public Behandling getBehandling(UUID behandlingUuid) {
        return behandlingerBasicKlient.getBehandling(behandlingUuid);
    }

    @Override
    public Behandling hentBehandlingHvisTilgjenglig(UUID behandlingUuid) {
        return behandlingerBasicKlient.hentBehandlingHvisTilgjenglig(behandlingUuid);
    }

    @Override
    public List<Behandling> alle(Saksnummer saksnummer) {
        return behandlingerBasicKlient.alle(saksnummer);
    }

    @Override
    public void settPaVent(SettBehandlingPaVentDto behandling) {
        behandlingerBasicKlient.settPaVent(behandling);
    }

    @Override
    public void henlegg(BehandlingHenlegg behandling) {
        behandlingerBasicKlient.henlegg(behandling);
    }

    @Override
    public Behandling gjenoppta(BehandlingIdDto behandling) {
        return behandlingerBasicKlient.gjenoppta(behandling);
    }

    @Override
    public void postBehandlingAksjonspunkt(BekreftedeAksjonspunkter aksjonspunkter) {
        behandlingerBasicKlient.postBehandlingAksjonspunkt(aksjonspunkter);
    }

    @Override
    public List<Aksjonspunkt> hentAlleAksjonspunkter(UUID behandlingUuid) {
        return behandlingerBasicKlient.hentAlleAksjonspunkter(behandlingUuid);
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
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLINGER_URL)
                        .build())
                .PUT(HttpRequest.BodyPublishers.ofString(toJson(behandling)));
        return behandlingerBasicKlient.følgRedirectTilStatusOgReturnerBehandlingNårTilgjenglig(request);
    }


    public Behandling annenPartBehandling(Saksnummer saksnummer) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLINGER_ANNEN_PART_BEHANDLING_URL)
                        .queryParam(SAKSNUMMER_NAME, saksnummer.value())
                        .build())
                .GET();
        return send(request.build(), Behandling.class);
    }


    public Medlem behandlingMedlemskap(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_PERSON_MEDLEMSKAP)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Medlem.class);
    }


    public Beregningsgrunnlag behandlingBeregningsgrunnlag(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_BEREGNINGSGRUNNALG_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Beregningsgrunnlag.class);
    }


    public Beregningsresultat behandlingBeregningsresultatEngangsstønad(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_ENGANGSSTØNAD_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Beregningsresultat.class);
    }


    public BeregningsresultatMedUttaksplan behandlingBeregningsresultatForeldrepenger(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_FORELDREPENGER_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), BeregningsresultatMedUttaksplan.class);
    }

    public Feriepengegrunnlag behandlingFeriepengegrunnlag(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_FERIEPENGER_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Feriepengegrunnlag.class);
    }


    public List<Vilkar> behandlingVilkår(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_VILKAAR_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<Vilkar>>() {}))
                .orElse(List.of());
    }




    public void overstyr(OverstyrAksjonspunkter aksjonspunkter) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_AKSJONSPUNKT_OVERSTYR_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(aksjonspunkter)));
        send(request.build());
    }

    public Soknad behandlingSøknad(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_SOKNAD_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Soknad.class);
    }


    public Opptjening behandlingOpptjening(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_OPPTJENING_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Opptjening.class);
    }

    public KlageInfo klage(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_KLAGE_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), KlageInfo.class);
    }


    public void mellomlagre(KlageVurderingResultatAksjonspunktMellomlagringDto vurdering) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_KLAGE_MELLOMLAGRE_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(vurdering)));
        send(request.build());
    }

    /*
     * hent stønadskontoer for behandling
     */
    public Saldoer behandlingUttakStonadskontoer(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
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
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_UTTAK_STONADSKONTOER_GITT_UTTAKSPERIODER_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(uttaksperioderDto)));
        return send(request.build(), Saldoer.class);
    }

    /*
     * hent kontroller fakta for behandling
     */
    public KontrollerFaktaData behandlingKontrollerFaktaPerioder(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
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
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
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
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
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
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
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
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_ARBEID_INNTEKTSMELDING)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), ArbeidOgInntektsmeldingDto.class);
    }

    public void behandlingArbeidInntektsmeldingLagreArbfor(ManueltArbeidsforholdDto arbeidsforhold) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_ARBEID_INNTEKTSMELDING_OPPRETT_ARBEIDSFORHOLD)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(arbeidsforhold)));
        send(request.build());
    }

    public void behandlingArbeidInntektsmeldingNyVurdering(BehandlingIdDto behandlingIdDto) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_ARBEID_INNTEKTSMELDING_NY_VURDERING)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(behandlingIdDto)));
        send(request.build());
    }

    public void behandlingArbeidInntektsmeldingLagreValg(ManglendeOpplysningerVurderingDto manglendeOpplysningerVurderingDto) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(FPSAK_BASE)
                        .path(BEHANDLING_ARBEID_INNTEKTSMELDING_VURDERING)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(manglendeOpplysningerVurderingDto)));
        send(request.build());
    }


}
