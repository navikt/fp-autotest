package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk;

import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Saksnummer;

public interface HistorikkKlient {
    List<HistorikkInnslag> hentHistorikk(Saksnummer saksnummer);
}
