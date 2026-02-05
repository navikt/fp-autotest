package no.nav.foreldrepenger.autotest.klienter.fplos;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import tools.jackson.core.type.TypeReference;

public class FplosKlient {

    private static final String API_NAME = "fplos";

    private final SaksbehandlerRolle saksbehandlerRolle;

    public FplosKlient(SaksbehandlerRolle saksbehandlerRolle) {
        this.saksbehandlerRolle = saksbehandlerRolle;
    }

    public List<LosOppgave> hentOppgaverForFagsaker(Saksnummer... saksnummer) {
        var saksnummerListe = Arrays.stream(saksnummer).map(Saksnummer::value).collect(Collectors.joining(","));
        var req = requestMedInnloggetSaksbehandler(saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPLOS_BASE).path("/saksbehandler/oppgaver/oppgaver-for-fagsaker")
                        .queryParam("saksnummerListe", saksnummerListe)
                        .build())
                .GET();
        return Optional.of(send(req.build(), new TypeReference<List<LosOppgave>>() {}))
                .orElseThrow(() -> new RuntimeException("Feilet"));
    }

    public List<LosSakslisteId> hentLister() {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                        .path("/avdelingsleder/sakslister")
                        .queryParam("avdelingEnhet", AvdelingEnhet.defaultEnhet.avdelingEnhet)
                        .build())
                .GET();
        return send(request.build(), new TypeReference<>() {
        });
    }

    public void leggTilSaksbehandlerForListe(SaksbehandlerRolle saksbehandler, LosSakslisteId listeId) {
        var opprettSaksbehandlerRequest = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                        .path("/avdelingsleder/saksbehandlere")
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(new OpprettSaksbehandlerRequest(AvdelingEnhet.defaultEnhet.avdelingEnhet, saksbehandler.getKode()))))
                .build();
        send(opprettSaksbehandlerRequest);

        var leggTilRequest = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                        .path("/avdelingsleder/sakslister/saksbehandler")
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(new LeggTilSaksbehandlerForListeRequest(AvdelingEnhet.defaultEnhet.avdelingEnhet,
                        saksbehandler.getKode(), true, listeId.sakslisteId()))))
                .build();
        send(leggTilRequest);
    }

    private record OpprettSaksbehandlerRequest(String avdelingEnhet, String brukerIdent) {}

    private record LeggTilSaksbehandlerForListeRequest(String avdelingEnhet, String brukerIdent, boolean checked, int sakslisteId) {}

    public static class SakslisteBuilder {
        private final LosSakslisteId sakslisteId;
        private final SaksbehandlerRolle saksbehandlerRolle;

        private SakslisteBuilder(SaksbehandlerRolle saksbehandlerRolle) {
            this.saksbehandlerRolle = saksbehandlerRolle;
            var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle, API_NAME)
                    .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                            .path("/avdelingsleder/sakslister")
                            .build())
                    .POST(HttpRequest.BodyPublishers.ofString(toJson(AvdelingEnhet.defaultEnhet)));
            var sakslisteIdResponse = Optional.of(send(request.build(), new TypeReference<LosSakslisteId>() {}))
                    .map(LosSakslisteId::sakslisteId)
                    .orElseThrow();
            this.sakslisteId = new LosSakslisteId(sakslisteIdResponse);
        }

        public static SakslisteBuilder nyListe() {
            return new SakslisteBuilder(SaksbehandlerRolle.OPPGAVESTYRER);
        }

        public SakslisteBuilder medSortering() {
            var sorteringDto = new Sortering(
                    sakslisteId,
                    "OPPRBEH",
                    AvdelingEnhet.defaultEnhet
            );
            var sorteringReq = requestMedInnloggetSaksbehandler(saksbehandlerRolle, API_NAME)
                    .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                            .path("/avdelingsleder/sakslister/sortering")
                            .build())
                    .POST(HttpRequest.BodyPublishers.ofString(toJson(sorteringDto)));
            send(sorteringReq.build());
            return this;
        }

        public SakslisteBuilder medSorteringIntervall() {
            var fomFilter = new SakslisteSorteringIntervallDato(
                    sakslisteId,
                    LocalDate.now(),
                    null,
                    AvdelingEnhet.defaultEnhet
            );
            var filterReq = requestMedInnloggetSaksbehandler(saksbehandlerRolle, API_NAME)
                    .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                            .path("/avdelingsleder/sakslister/sortering-tidsintervall-dato")
                            .build())
                    .POST(HttpRequest.BodyPublishers.ofString(toJson(fomFilter)));
            send(filterReq.build());
           return this;
        }

        public LosSakslisteId build() {
            return sakslisteId;
        }
    }

    record AvdelingEnhet(String avdelingEnhet) {
        static final AvdelingEnhet defaultEnhet = new AvdelingEnhet("4867");
    }
    record SakslisteSorteringIntervallDato(LosSakslisteId sakslisteId,
                                           LocalDate fomDato,
                                           LocalDate tomDato,
                                           AvdelingEnhet avdelingEnhet) { }
    record Sortering(LosSakslisteId sakslisteId,
                     String sakslisteSorteringValg,
                     AvdelingEnhet avdelingEnhet) { }

}
