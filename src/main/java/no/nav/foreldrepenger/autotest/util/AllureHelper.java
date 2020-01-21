package no.nav.foreldrepenger.autotest.util;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.util.http.rest.JsonKlient;

public class AllureHelper {

    @Step("Henter aksjonspunkter: {aksjonspunkter}")
    public static void debugListUtAksjonspunkter(String aksjonspunkter){ }

    @Step("Informasjon om behandling:")
    public static void debugLoggBehandling(Behandling behandling) throws JsonProcessingException {
        String json = hentObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(behandling);
        skriverUtJson(json);
    }

    @Step("Informasjon om behandlinger: ")
    public static void debugLoggBehandlingsliste(List<Behandling> behandlinger) throws JsonProcessingException {
        for(Behandling behandling : behandlinger){
            debugLoggBehandling(behandling);
        }
    }

    @Step("Informasjon om behandlinger: {tekst} ")
    public static void debugLoggBehandlingsliste(String tekst, List<Behandling> behandlinger) throws JsonProcessingException {
        for(Behandling behandling : behandlinger){
            debugLoggBehandling(behandling);
        }
    }

    @Step("Informasjon om behandling ({tekst}):")
    public static void debugLoggBehandling(String tekst, Behandling behandling) throws JsonProcessingException {
        String json = hentObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(behandling);
        skriverUtJson(json);
    }

    @Step("Sender inn dokument {type} med innhold:")
    public static void debugSenderInnDokument(String type, String xml){
        skriverUtXmlRequest(xml);
    }

    @Step("Fritekstlogg {fritekst}")
    public static void debugFritekst(String fritekst){}

    public static void debugLoggHistorikkinnslag(List<HistorikkInnslag> historikkInnslagList) {
        StringBuilder sb = new StringBuilder();
        sb.append("Historikkinnslag\n");
        for(HistorikkInnslag historikkInnslag : historikkInnslagList){
            sb.append(String.format("\t{%s}",historikkInnslag.getTypeKode()));
        }
        loggHistorikkinnslag(sb.toString());
    }

    @Step("Informasjon om historikkinnslag: {historikkinnslag}")
    private static void loggHistorikkinnslag(String historikkinnslag){}

    @Step("Informasjon om aksjonspunkt:")
    public static void debugAksjonspunkt(Aksjonspunkt aksjonspunkt) {

    }

    @Step("{aksjonspunkt}")
    public static void debugAksjonspunktbekreftelse(AksjonspunktBekreftelse aksjonspunkt) throws JsonProcessingException {
        String json = hentObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(aksjonspunkt);
        skriverUtJson(json);
    }

    @Step("Informasjon om aksjonspunktbekreftelser:")
    public static void debugAksjonspunktbekreftelser(List<AksjonspunktBekreftelse> aksjonspunkter) throws JsonProcessingException {
        for(AksjonspunktBekreftelse aksjonspunkt : aksjonspunkter){
            debugAksjonspunktbekreftelse(aksjonspunkt);
        }
    }

    @Attachment(value = "XmlDokument", type = "application/xml")
    private static String skriverUtXmlRequest(String xml) {
        return xml;
    }

    @Attachment(value = "Json", type = "application/xml")
    private static String skriverUtJson(String json) {
        return json;
    }

    private static ObjectMapper hentObjectMapper() {
        return JsonKlient.getObjectMapper();
    }
}
