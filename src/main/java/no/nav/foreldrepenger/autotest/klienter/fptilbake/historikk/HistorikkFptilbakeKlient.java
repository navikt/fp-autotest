package no.nav.foreldrepenger.autotest.klienter.fptilbake.historikk;

import static no.nav.foreldrepenger.autotest.klienter.BaseUriProvider.FPTILBAKE_BASE;

import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkKlientFelles;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkKlientINF;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class HistorikkFptilbakeKlient implements HistorikkKlientINF {

    private final HistorikkKlientFelles historikkKlientFelles = new HistorikkKlientFelles(FPTILBAKE_BASE);


    @Override
    public List<HistorikkInnslag> hentHistorikk(Saksnummer saksnummer) {
        return historikkKlientFelles.hentHistorikk(saksnummer);
    }
}
