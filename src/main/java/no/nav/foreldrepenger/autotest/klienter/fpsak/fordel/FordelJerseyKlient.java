package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel;

import static javax.ws.rs.client.Entity.json;

import javax.ws.rs.client.ClientRequestFilter;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.BehandlendeFagsystem;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.FagsakInformasjon;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.JournalpostKnyttning;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.JournalpostMottak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.OpprettSak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.Saksnummer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.VurderFagsystem;

public class FordelJerseyKlient extends FpsakJerseyKlient {

    private static final String FORDEL_URL = "/fordel";
    private static final String VURDER_FAGSYSTEM_URL = FORDEL_URL + "/vurderFagsystem";
    private static final String JOURNALPOST_URL = FORDEL_URL + "/journalpost";

    private static final String FAGSAK_URL = FORDEL_URL + "/fagsak";
    private static final String FAGSAK_OPPRETT_URL = FAGSAK_URL + "/opprett";
    private static final String FAGSAK_INFORMASJON_URL = FAGSAK_URL + "/informasjon";
    private static final String FAGSAK_KNYTT_JOURNALPOST_URL = FAGSAK_URL + "/knyttJournalpost";

    public FordelJerseyKlient(ClientRequestFilter filter) {
        super(filter);
    }

    public BehandlendeFagsystem vurderFagsystem(VurderFagsystem vurderFagsystem) {
        return client.target(base)
                .path(VURDER_FAGSYSTEM_URL)
                .request()
                .post(json(vurderFagsystem), BehandlendeFagsystem.class);
    }

    @Step("Sender journalpost")
    public void journalpost(JournalpostMottak journalpostMottak) {
        client.target(base)
                .path(JOURNALPOST_URL)
                .request()
                .post(json(journalpostMottak));
    }

    @Step("Oppretter fagsak")
    public Saksnummer fagsakOpprett(OpprettSak journalpost) {
        return client.target(base)
                .path(FAGSAK_OPPRETT_URL)
                .request()
                .post(json(journalpost), Saksnummer.class);
    }

    public FagsakInformasjon fagsakInformasjon(Saksnummer id) {
        return client.target(base)
                .path(FAGSAK_INFORMASJON_URL)
                .request()
                .post(json(id), FagsakInformasjon.class);
    }

    @Step("Knytter fagsak til journalpost")
    public void fagsakKnyttJournalpost(JournalpostKnyttning knyttJournalpost) {
        client.target(base)
                .path(FAGSAK_KNYTT_JOURNALPOST_URL)
                .request()
                .post(json(knyttJournalpost));
    }
}
