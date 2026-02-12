package no.nav.foreldrepenger.autotest.klienter.fplos;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.fplos.dto.LosOppgave;
import no.nav.foreldrepenger.autotest.klienter.fplos.dto.SakslisteIdDto;
import no.nav.foreldrepenger.autotest.klienter.fplos.dto.SakslisteLagreDto;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;

public class FplosKlient {
    public static final String DEFAULT_AVDELING = "4867";

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

    public List<SakslisteIdDto> hentLister() {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                        .path("/avdelingsleder/sakslister")
                        .queryParam("avdelingEnhet", DEFAULT_AVDELING)
                        .build())
                .GET();
        return send(request.build(), new TypeReference<>() {
        });
    }

    public void leggTilSaksbehandlerForListe(SaksbehandlerRolle saksbehandler, SakslisteIdDto listeId) {
        var opprettSaksbehandlerRequest = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                        .path("/avdelingsleder/saksbehandlere")
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(new OpprettSaksbehandlerRequest(DEFAULT_AVDELING, saksbehandler.getKode()))))
                .build();
        send(opprettSaksbehandlerRequest);

        var leggTilRequest = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                        .path("/avdelingsleder/sakslister/saksbehandler")
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(
                        new LeggTilSaksbehandlerForListeRequest(DEFAULT_AVDELING, saksbehandler.getKode(), true, listeId.sakslisteId()))))
                .build();
        send(leggTilRequest);
    }

    public SakslisteIdDto opprettNySaksliste(String defaultAvdeling) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                        .path("/avdelingsleder/sakslister")
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(defaultAvdeling)));
        return send(request.build(), SakslisteIdDto.class);
    }

    public void endreEksistrendeSaksliste(SakslisteLagreDto nySaksliste) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPLOS_BASE)
                        .path("/avdelingsleder/sakslister/endre")
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(nySaksliste)));
        send(request.build(), SakslisteIdDto.class);
    }

    private record OpprettSaksbehandlerRequest(String avdelingEnhet, String brukerIdent) {}

    private record LeggTilSaksbehandlerForListeRequest(String avdelingEnhet, String brukerIdent, boolean checked, Long sakslisteId) {}


}
