package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.erketyper.TilretteleggingsErketyper;

import java.util.List;

public class Tilrettelegging {
    protected List<TilretteleggingsErketyper> tilrettelegginger = null;
    public List<TilretteleggingsErketyper> getTilrettelegginger() { return tilrettelegginger; }
}
