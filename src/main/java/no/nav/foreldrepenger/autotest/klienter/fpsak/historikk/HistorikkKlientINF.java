package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk;

import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public interface HistorikkKlientINF {
    List<HistorikkInnslag> hentHistorikk(Saksnummer saksnummer);
}
