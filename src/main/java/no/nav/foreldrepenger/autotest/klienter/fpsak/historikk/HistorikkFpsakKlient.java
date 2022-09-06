package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk;

import static no.nav.foreldrepenger.autotest.klienter.BaseUriProvider.FPSAK_BASE;

import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class HistorikkFpsakKlient implements HistorikkKlient {

    private final HistorikkKlientFelles historikkKlientFelles = new HistorikkKlientFelles(FPSAK_BASE);


    @Override
    public List<HistorikkInnslag> hentHistorikk(Saksnummer saksnummer) {
        return historikkKlientFelles.hentHistorikk(saksnummer);
    }
}
