package no.nav.foreldrepenger.autotest.klienter.fptilbake.historikk;

import static no.nav.foreldrepenger.autotest.klienter.BaseUriProvider.FPTILBAKE_BASE;

import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkKlientFelles;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.openam.SaksbehandlerRolle;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class HistorikkFptilbakeKlient implements HistorikkKlient {

    private final HistorikkKlientFelles historikkKlientFelles;

    public HistorikkFptilbakeKlient(SaksbehandlerRolle saksbehandlerRolle) {
        historikkKlientFelles = new HistorikkKlientFelles(FPTILBAKE_BASE, saksbehandlerRolle, "fptilbake");
    }

    @Override
    public List<HistorikkInnslag> hentHistorikk(Saksnummer saksnummer) {
        return historikkKlientFelles.hentHistorikk(saksnummer);
    }
}
