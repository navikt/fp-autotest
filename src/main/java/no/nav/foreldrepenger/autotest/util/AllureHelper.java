package no.nav.foreldrepenger.autotest.util;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.util.rest.JacksonObjectMapper;

public final class AllureHelper {

    private AllureHelper() {
    }

    @Step("Informasjon om behandling:")
    public static void debugLoggBehandling(Behandling behandling) {
        debugJson(toJson(behandling));
    }

    @Step("Informasjon om behandlinger: ")
    public static void debugLoggBehandlingsliste(List<Behandling> behandlinger) {
        for (Behandling behandling : behandlinger) {
            debugLoggBehandling(behandling);
        }
    }

    @Step("[{behandlingsUUID}] Venter på behandlingsstatus {status}")
    public static void debugBehandlingsstatus(BehandlingStatus status, UUID behandlingsUUID) {
    }

    @Step("Sender inn dokument {type} med innhold:")
    public static void debugSenderInnDokument(String type, String xml) {
        skriverUtXmlRequest(xml);
    }

    @Step("Fritekstlogg {fritekst}")
    public static void debugFritekst(String fritekst) {
    }

    public static void debugLoggHistorikkinnslag(List<HistorikkInnslag> historikkInnslagList) {
        StringBuilder sb = new StringBuilder();
        sb.append("Historikkinnslag\n");
        for (HistorikkInnslag historikkInnslag : historikkInnslagList) {
            sb.append(String.format("\t{%s}", historikkInnslag.type()));
        }
        loggHistorikkinnslag(sb.toString());
    }

    @Step("Informasjon om historikkinnslag: {historikkinnslag}")
    private static void loggHistorikkinnslag(String historikkinnslag) {
    }

    @Step("Informasjon om aksjonspunkt:")
    public static void debugAksjonspunkt(Aksjonspunkt aksjonspunkt) {
        debugJson(toJson(aksjonspunkt));
    }

    @Step("[{id}]: Behandler aksjonspunkt {aksjonspunkt.kode}")
    public static void debugAksjonspunktbekreftelse(AksjonspunktBekreftelse aksjonspunkt, UUID id) {
        debugJson(toJson(aksjonspunkt));
    }

    public static void debugAksjonspunktbekreftelser(List<AksjonspunktBekreftelse> aksjonspunkter, UUID id) {
        for (AksjonspunktBekreftelse aksjonspunkt : aksjonspunkter) {
            debugAksjonspunktbekreftelse(aksjonspunkt, id);
        }
    }

    public static void tilJsonOgPubliserIAllureRapport(Object testscenarioDto) {
        debugJson(toJson(testscenarioDto));
    }

    @Attachment(value = "Json", type = "application/json")
    public static String debugJson(String json) {
        return json;
    }

    @Attachment(value = "XmlDokument", type = "application/xml")
    private static String skriverUtXmlRequest(String xml) {
        return xml;
    }

    private static ObjectMapper hentObjectMapper() {
        return JacksonObjectMapper.getObjectMapper();
    }

    private static String toJson(Object object) {
        try {
            return hentObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
