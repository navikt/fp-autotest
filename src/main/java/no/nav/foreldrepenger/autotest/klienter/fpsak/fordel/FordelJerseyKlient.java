package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel;

import static jakarta.ws.rs.client.Entity.json;
import static no.nav.foreldrepenger.common.mapper.DefaultJsonMapper.MAPPER;

import jakarta.ws.rs.client.ClientRequestFilter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.FagsakInformasjon;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.JournalpostKnyttning;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.JournalpostMottak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.OpprettSak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.Saksnummer;
import no.nav.foreldrepenger.kontrakter.fordel.JournalpostMottakDto;

public class FordelJerseyKlient extends FpsakJerseyKlient {

    private static final String FORDEL_URL = "/fordel";
    private static final String JOURNALPOST_URL = FORDEL_URL + "/journalpost";

    private static final String FAGSAK_URL = FORDEL_URL + "/fagsak";
    private static final String FAGSAK_OPPRETT_URL = FAGSAK_URL + "/opprett";
    private static final String FAGSAK_INFORMASJON_URL = FAGSAK_URL + "/informasjon";
    private static final String FAGSAK_KNYTT_JOURNALPOST_URL = FAGSAK_URL + "/knyttJournalpost";

    public FordelJerseyKlient(ClientRequestFilter filter) {
        super(MAPPER, filter);
    }

    public void journalpost(JournalpostMottak journalpostMottak) {
        client.target(base)
                .path(JOURNALPOST_URL)
                .request()
                .post(json(journalpostMottak));
    }

    public void journalpost(JournalpostMottakDto journalpostMottak) {
        client.target(base)
                .path(JOURNALPOST_URL)
                .request()
                .post(json(journalpostMottak));
    }

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

    public void fagsakKnyttJournalpost(JournalpostKnyttning knyttJournalpost) {
        client.target(base)
                .path(FAGSAK_KNYTT_JOURNALPOST_URL)
                .request()
                .post(json(knyttJournalpost));
    }
}
