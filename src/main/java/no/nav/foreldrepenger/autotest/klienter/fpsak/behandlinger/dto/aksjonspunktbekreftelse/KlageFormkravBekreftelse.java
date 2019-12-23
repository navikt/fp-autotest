package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

public abstract class KlageFormkravBekreftelse extends AksjonspunktBekreftelse {

    protected boolean erKlagerPart;
    protected boolean erFristOverholdt;
    protected boolean erKonkret;
    protected boolean erSignert;
    protected String vedtak; // påklagdBehandlingsId;

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

    public KlageFormkravBekreftelse setPåklagdVedtak(String vedtakId) {
        this.vedtak = vedtakId;
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
