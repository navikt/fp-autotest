package no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.BehandlendeEnhet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kodeverk;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kodeverk.KodeListe;

public class KodeverkJerseyKlient extends FpsakJerseyKlient {

    private static final String KODEVERK_URL = "/kodeverk";
    private static final String KODEVERK_BEHANDLENDE_ENHETER_URL = KODEVERK_URL + "/behandlende-enheter";
    private static final String KODEVERK_HENLEGG_ÅRSAKER = KODEVERK_URL + "/henlegg/arsaker";
    private static final String KODEVERK_HENLEGG_ÅRSAKER_KLAGE = KODEVERK_HENLEGG_ÅRSAKER + "/klage";
    private static final String KODEVERK_HENLEGG_ÅRSAKER_INNSYN = KODEVERK_HENLEGG_ÅRSAKER + "/innsyn";

    public KodeverkJerseyKlient() {
        super();
    }

    @Step("Henter kodeverk for FPSAK")
    public Kodeverk getKodeverk() {
        return client.target(base)
                .path(KODEVERK_URL)
                .request()
                .get(Kodeverk.class);
    }

    public List<BehandlendeEnhet> behandlendeEnheter() {
        return client.target(base)
                .path(KODEVERK_BEHANDLENDE_ENHETER_URL)
                .request()
                .get(Response.class)
                .readEntity(new GenericType<>() {});
    }

    public KodeListe henleggArsaker() {
        return client.target(base)
                .path(KODEVERK_HENLEGG_ÅRSAKER)
                .request()
                .get(Response.class)
                .readEntity(new GenericType<>() {});
    }

    public KodeListe henleggArsakerKlage() {
        return client.target(base)
                .path(KODEVERK_HENLEGG_ÅRSAKER_KLAGE)
                .request()
                .get(Response.class)
                .readEntity(new GenericType<>() {});
    }

    public KodeListe henleggArsakerInnsyn() {
        return client.target(base)
                .path(KODEVERK_HENLEGG_ÅRSAKER_INNSYN)
                .request()
                .get(Response.class)
                .readEntity(new GenericType<>() {});
    }
}
