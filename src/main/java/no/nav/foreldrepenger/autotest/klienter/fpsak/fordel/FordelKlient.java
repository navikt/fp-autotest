package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.BehandlendeFagsystem;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.FagsakInformasjon;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.JournalpostKnyttning;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.JournalpostMottak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.OpprettSak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.Saksnummer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.VurderFagsystem;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

public class FordelKlient extends FpsakKlient{

    private static final String FORDEL_URL = "/fordel";
    private static final String VURDER_FAGSYSTEM_URL = FORDEL_URL + "/vurderFagsystem";
    private static final String JOURNALPOST_URL = FORDEL_URL + "/journalpost";

    private static final String FAGSAK_URL = FORDEL_URL + "/fagsak";
    private static final String FAGSAK_OPPRETT_URL = FAGSAK_URL + "/opprett";
    private static final String FAGSAK_INFORMASJON_URL = FAGSAK_URL + "/informasjon";
    private static final String FAGSAK_KNYTT_JOURNALPOST_URL = FAGSAK_URL + "/knyttJournalpost";


    public FordelKlient(HttpSession session) {
        super(session);
    }


    public BehandlendeFagsystem vurderFagsystem(VurderFagsystem vurderFagsystem) {
        String url = hentRestRotUrl() + VURDER_FAGSYSTEM_URL;
        return postOgHentJson(url, vurderFagsystem, BehandlendeFagsystem.class, StatusRange.STATUS_SUCCESS);
    }

    @Step("Sender journalpost")
    public void journalpost(JournalpostMottak journalpostMottak) {
        String url = hentRestRotUrl() + JOURNALPOST_URL;
        postOgVerifiser(url, journalpostMottak, StatusRange.STATUS_SUCCESS);
    }
    @Step("Oppretter fagsak")
    public Saksnummer fagsakOpprett(OpprettSak journalpost) {
        String url = hentRestRotUrl() + FAGSAK_OPPRETT_URL;
        return postOgHentJson(url, journalpost, Saksnummer.class, StatusRange.STATUS_SUCCESS);
    }

    public FagsakInformasjon fagsakInformasjon(Saksnummer id) {
        String url = hentRestRotUrl() + FAGSAK_INFORMASJON_URL;
        return postOgHentJson(url, id, FagsakInformasjon.class, StatusRange.STATUS_SUCCESS);
    }

    @Step("Knytter fagsak til journalpost")
    public void fagsakKnyttJournalpost(JournalpostKnyttning knyttJournalpost) {
        String url = hentRestRotUrl() + FAGSAK_KNYTT_JOURNALPOST_URL;
        postOgVerifiser(url, knyttJournalpost, StatusRange.STATUS_SUCCESS);
    }
}
