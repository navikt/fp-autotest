package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.util.ArrayList;
import java.util.List;

public class FattVedtakDetaljerDto {

    public String aksjonspunktKode;
    protected boolean godkjent;
    protected List<ReturnerVedtakÅrsaker> arsaker = new ArrayList<>();

    public FattVedtakDetaljerDto(int kode, boolean godkjent) {
        this.aksjonspunktKode = String.valueOf(kode);
        this.godkjent = godkjent;
    }
}
