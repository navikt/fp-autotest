package no.nav.foreldrepenger.autotest.util;

import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;

import java.util.List;
import java.util.UUID;

import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;

public final class AllureHelper {

    private AllureHelper() {
    }

    public static void debugLoggBehandling(Behandling behandling) {
        Allure.step("Informasjon om behandling:", () -> debugJson(toJson(behandling)));
    }

    public static void debugLoggBehandlingsliste(List<Behandling> behandlinger) {
        Allure.step("Informasjon om behandlinger: ", () -> behandlinger.forEach(AllureHelper::debugLoggBehandling));
    }

    public static void debugBehandlingsstatus(BehandlingStatus status, UUID behandlingsUUID) {
        Allure.step("[" + behandlingsUUID + "] Venter på behandlingsstatus " + status);
    }

    public static void debugSenderInnDokument(String type, String xml) {
        Allure.step("Sender inn dokument " + type + " med innhold:", () -> skriverUtXmlRequest(xml));
    }

    public static void debugFritekst(String fritekst) {
        Allure.step("Fritekstlogg " + fritekst);
    }

    public static void debugAksjonspunkt(Aksjonspunkt aksjonspunkt) {
        Allure.step("Informasjon om aksjonspunkt:", () -> debugJson(toJson(aksjonspunkt)));
    }

    public static void debugAksjonspunktbekreftelse(AksjonspunktBekreftelse aksjonspunkt, UUID id) {
        Allure.step("[" + id + "]: Behandler aksjonspunkt " + aksjonspunkt.aksjonspunktKode(), () -> debugJson(toJson(aksjonspunkt)));
    }

    public static void debugAksjonspunktbekreftelser(List<AksjonspunktBekreftelse> aksjonspunkter, UUID id) {
        aksjonspunkter.forEach(aksjonspunkt -> debugAksjonspunktbekreftelse(aksjonspunkt, id));
    }

    public static void tilJsonOgPubliserIAllureRapport(Object testscenarioDto) {
        debugJson(toJson(testscenarioDto));
    }

    public static void debugJson(String json) {
        Allure.attachment("Json", "application/json", json, AttachmentOptions.withFileExtension(".json"));
    }

    private static void skriverUtXmlRequest(String xml) {
        Allure.attachment("XmlDokument", "application/xml", xml, AttachmentOptions.withFileExtension(".xml"));
    }

}
