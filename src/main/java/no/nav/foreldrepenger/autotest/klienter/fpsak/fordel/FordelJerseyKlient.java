package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel;

import static jakarta.ws.rs.client.Entity.json;
import static no.nav.foreldrepenger.common.mapper.DefaultJsonMapper.MAPPER;

import jakarta.ws.rs.client.ClientRequestFilter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakJerseyKlient;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.kontrakter.fordel.JournalpostKnyttningDto;
import no.nav.foreldrepenger.kontrakter.fordel.JournalpostMottakDto;
import no.nav.foreldrepenger.kontrakter.fordel.OpprettSakDto;

public class FordelJerseyKlient extends FpsakJerseyKlient {

    private static final String FORDEL_URL = "/fordel";
    private static final String JOURNALPOST_URL = FORDEL_URL + "/journalpost";

    private static final String FAGSAK_URL = FORDEL_URL + "/fagsak";
    private static final String FAGSAK_OPPRETT_URL = FAGSAK_URL + "/opprett";
    private static final String FAGSAK_KNYTT_JOURNALPOST_URL = FAGSAK_URL + "/knyttJournalpost";

    public FordelJerseyKlient(ClientRequestFilter filter) {
        super(MAPPER, filter);
    }

    public void journalpost(JournalpostMottakDto journalpostMottak) {
        client.target(base)
                .path(JOURNALPOST_URL)
                .request()
                .post(json(journalpostMottak));
    }

    public Saksnummer fagsakOpprett(OpprettSakDto opprettSakDto) {
        return client.target(base)
                .path(FAGSAK_OPPRETT_URL)
                .request()
                .post(json(opprettSakDto), Saksnummer.class);
    }

    public void fagsakKnyttJournalpost(JournalpostKnyttningDto knyttJournalpost) {
        client.target(base)
                .path(FAGSAK_KNYTT_JOURNALPOST_URL)
                .request()
                .post(json(knyttJournalpost));
    }
}
