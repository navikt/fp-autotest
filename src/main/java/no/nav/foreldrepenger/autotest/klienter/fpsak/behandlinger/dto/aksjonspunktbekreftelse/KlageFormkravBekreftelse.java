package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.UUID;

public abstract class KlageFormkravBekreftelse extends AksjonspunktBekreftelse {

    protected boolean erKlagerPart;
    protected boolean erFristOverholdt;
    protected boolean erKonkret;
    protected boolean erSignert;
    protected UUID vedtakBehandlingUuid;
    //TODO palfi fjern vedtak
    protected Long vedtak;

    public KlageFormkravBekreftelse() {
        super();
    }

    public KlageFormkravBekreftelse erKlagerPart(boolean verdi) {
        erKlagerPart = verdi;
        return this;
    }

    public KlageFormkravBekreftelse erFristOverholdt(boolean verdi) {
        erFristOverholdt = verdi;
        return this;
    }

    public KlageFormkravBekreftelse erKonkret(boolean verdi) {
        erKonkret = verdi;
        return this;
    }

    public KlageFormkravBekreftelse erSignert(boolean verdi) {
        erSignert = verdi;
        return this;
    }

    public KlageFormkravBekreftelse setPåklagdVedtak(UUID vedtakId, long behandlingId) {
        this.vedtakBehandlingUuid = vedtakId;
        this.vedtak = behandlingId;
        return this;
    }

    public KlageFormkravBekreftelse godkjennAlleFormkrav() {
        erKlagerPart(true);
        erFristOverholdt(true);
        erKonkret(true);
        erSignert(true);
        return this;
    }

    public KlageFormkravBekreftelse klageErIkkeKonkret() {
        erKlagerPart(true);
        erFristOverholdt(true);
        erKonkret(false);
        erSignert(true);
        return this;
    }

}
