package no.nav.foreldrepenger.autotest.verktoy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Saksbehandler;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingFpsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;

final class SaksVerktoy {

    private static final String FRONTEND = "http://localhost:9000/fagsak/";
    private static final int STANDARD_TIMEOUT_SEKUNDER = 120;

    private final Katalog katalog;

    SaksVerktoy(Katalog katalog) {
        this.katalog = katalog;
    }

    String status(Map<String, Object> args) throws Exception {
        return beskriv(new Saksnummer(påkrevd(args, "saksnummer")));
    }

    String vent(Map<String, Object> args) throws Exception {
            var saksnummer = new Saksnummer(påkrevd(args, "saksnummer"));
            var kode = påkrevd(args, "kode");
            var timeout = args.get("timeout_sekunder") instanceof Number n ? n.intValue() : STANDARD_TIMEOUT_SEKUNDER;
            var funnet = ventPå(saksnummer, kode, timeout);
            return (funnet ? "OK: " + kode + " er åpent." : "TIMEOUT: " + kode + " kom ikke innen " + timeout + " s.")
                    + "\n\n" + beskriv(saksnummer);
    }

    String ventPåBehandling(Map<String, Object> args) throws Exception {
        var saksnummer = new Saksnummer(påkrevd(args, "saksnummer"));
        var timeout = args.get("timeout_sekunder") instanceof Number n ? n.intValue() : STANDARD_TIMEOUT_SEKUNDER;
        var slutt = System.currentTimeMillis() + timeout * 1000L;
        while (System.currentTimeMillis() < slutt) {
            var saksbehandler = new Saksbehandler();
            saksbehandler.hentFagsak(saksnummer);
            var behandling = saksbehandler.valgtBehandling;
            if (behandling.status == BehandlingStatus.AVSLUTTET) {
                return "OK: behandlingen er avsluttet.\n\n" + beskriv(saksnummer);
            }
            // 7xxx er ventepunkter som fpsak løser selv; de andre må saksbehandler løse.
            var manuelt = åpneAksjonspunkt(behandling.uuid).stream()
                    .map(Aksjonspunkt::getDefinisjon)
                    .filter(kode -> !kode.startsWith("7"))
                    .findFirst();
            if (manuelt.isPresent()) {
                return "STOPP: saken står på aksjonspunkt " + manuelt.get() + ", som må løses i fpsak-frontend.\n\n" + beskriv(saksnummer);
            }
            Thread.sleep(3000);
        }
        return "TIMEOUT: behandlingen er verken avsluttet eller stoppet på et aksjonspunkt etter " + timeout + " s.\n\n" + beskriv(saksnummer);
    }

    private boolean ventPå(Saksnummer saksnummer, String kode, int timeoutSekunder) throws InterruptedException {
        var slutt = System.currentTimeMillis() + timeoutSekunder * 1000L;
        while (System.currentTimeMillis() < slutt) {
            if (åpneAksjonspunkt(saksnummer).stream().anyMatch(ap -> ap.getDefinisjon().equals(kode))) {
                return true;
            }
            Thread.sleep(2000);
        }
        return false;
    }

    private List<Aksjonspunkt> åpneAksjonspunkt(Saksnummer saksnummer) {
        var saksbehandler = new Saksbehandler();
        saksbehandler.hentFagsak(saksnummer);
        return åpneAksjonspunkt(saksbehandler.valgtBehandling.uuid);
    }

    private static List<Aksjonspunkt> åpneAksjonspunkt(UUID behandlingUuid) {
        return new BehandlingFpsakKlient(SaksbehandlerRolle.SAKSBEHANDLER)
                .hentAlleAksjonspunkter(behandlingUuid)
                .stream()
                .filter(Aksjonspunkt::erUbekreftet)
                .toList();
    }

    private String beskriv(Saksnummer saksnummer) {
        var saksbehandler = new Saksbehandler();
        saksbehandler.hentFagsak(saksnummer);
        var behandling = saksbehandler.valgtBehandling;
        var linjer = new ArrayList<String>();
        linjer.add("Saksnummer: " + saksnummer.value());
        linjer.add("Behandling: " + behandling.type + " (" + behandling.uuid + ")");
        linjer.add("Status: " + behandling.status);
        if (behandling.behandlingPåVent) {
            linjer.add("På vent" + (behandling.venteArsakKode == null ? "" : ": " + behandling.venteArsakKode)
                    + ", frist " + behandling.fristBehandlingPåVent);
        }
        var åpne = åpneAksjonspunkt(behandling.uuid);
        if (åpne.isEmpty()) {
            linjer.add("Åpne aksjonspunkter: ingen");
        } else {
            linjer.add("Åpne aksjonspunkter:");
            åpne.forEach(ap -> linjer.add("- " + ap.getDefinisjon() + tittel(ap.getDefinisjon())
                    + (katalog.finn(ap.getDefinisjon()).isPresent() ? " [katalog/aksjonspunkt/" + ap.getDefinisjon() + ".md]" : "")));
        }
        linjer.add("Åpne i fpsak-frontend: " + FRONTEND + saksnummer.value() + "/");
        return String.join("\n", linjer);
    }

    private String tittel(String kode) {
        return katalog.finn(kode).map(o -> " " + o.tittel()).orElse("");
    }

    private static String påkrevd(Map<String, Object> args, String navn) {
        var verdi = args == null ? null : args.get(navn);
        if (verdi == null || verdi.toString().isBlank()) {
            throw new IllegalArgumentException("Mangler argument: " + navn);
        }
        return verdi.toString();
    }
}
