package no.nav.foreldrepenger.autotest.klienter.fplos;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJsonBodyPublisher;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.sendStringRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.common.domain.Saksnummer;

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

    public String hentLister() {
        var request = requestMedInnloggetSaksbehandler(SaksbehandlerRolle.OPPGAVESTYRER, API_NAME)
                .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                        .path("/avdelingsleder/sakslister")
                        .queryParam("avdelingEnhet", "4867")
                        .build())
                .GET();
        return Optional.ofNullable(sendStringRequest(request.build()))
                .orElseThrow(() -> new RuntimeException("Ussj")).body();
    }

    static class SakslisteBuilder {
        private final SakslisteId sakslisteId;

        private SakslisteBuilder() {
            var request = requestMedInnloggetSaksbehandler(SaksbehandlerRolle.OPPGAVESTYRER, API_NAME)
                    .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                            .path("/avdelingsleder/sakslister")
                            .build())
                    .POST(toJsonBodyPublisher(AvdelingEnhet.defaultEnhet));
            var sakslisteIdResponse = Optional.of(send(request.build(), new TypeReference<SakslisteId>() {}))
                    .map(SakslisteId::sakslisteId)
                    .orElseThrow();
            this.sakslisteId = new SakslisteId(sakslisteIdResponse);
        }

        public static SakslisteBuilder nyListe() {
            return new SakslisteBuilder();
        }

        public SakslisteBuilder medSortering() {
            var sorteringDto = new Sortering(
                    sakslisteId,
                    "OPPRBEH",
                    AvdelingEnhet.defaultEnhet
            );
            var sorteringReq = requestMedInnloggetSaksbehandler(SaksbehandlerRolle.OPPGAVESTYRER, API_NAME)
                    .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                            .path("/avdelingsleder/sakslister/sortering")
                            .build())
                    .POST(toJsonBodyPublisher(sorteringDto));
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
            var filterReq = requestMedInnloggetSaksbehandler(SaksbehandlerRolle.OPPGAVESTYRER, API_NAME)
                    .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                            .path("/avdelingsleder/sakslister/sortering-tidsintervall-dato")
                            .build())
                    .POST(toJsonBodyPublisher(fomFilter));
            send(filterReq.build());
           return this;
        }

        public SakslisteId build() {
            return sakslisteId;
        }
    }

    record SakslisteId(int sakslisteId) { }
    record AvdelingEnhet(String avdelingEnhet) {
        static final AvdelingEnhet defaultEnhet = new AvdelingEnhet("4867");
    }
    record SakslisteSorteringIntervallDato(SakslisteId sakslisteId,
                                           LocalDate fomDato,
                                           LocalDate tomDato,
                                           AvdelingEnhet avdelingEnhet) { }
    record Sortering(SakslisteId sakslisteId,
                     String sakslisteSorteringValg,
                     AvdelingEnhet avdelingEnhet) { }

}
