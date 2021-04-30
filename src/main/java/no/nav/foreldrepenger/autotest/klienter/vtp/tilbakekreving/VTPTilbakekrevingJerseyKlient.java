package no.nav.foreldrepenger.autotest.klienter.vtp.tilbakekreving;

import static jakarta.ws.rs.client.Entity.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.vtp.VTPJerseyKlient;
import no.nav.foreldrepenger.vtp.kontrakter.TilbakekrevingKonsistensDto;

public class VTPTilbakekrevingJerseyKlient extends VTPJerseyKlient {

    private static final String TILBAKEKREVING_KONSISTENS = "/tilbakekreving/konsistens";
    private static final Logger LOG = LoggerFactory.getLogger(VTPTilbakekrevingJerseyKlient.class);

    public VTPTilbakekrevingJerseyKlient() {
        super();
    }

    @Step("Oppdaterer VTPs tilbakekrevingsmock med siste saksnummer og behandling")
    public void oppdaterTilbakekrevingKonsistens(Long saksnummer, int behandlingId) {
        LOG.info("Oppdaterer VTPs tilbakekrevingsmock med saksnummer {} og behandling {} for konsistens", saksnummer, behandlingId);

        client.target(base)
                .path(TILBAKEKREVING_KONSISTENS)
                .request()
                .post(json(new TilbakekrevingKonsistensDto(""+saksnummer, ""+behandlingId)));
    }
}
