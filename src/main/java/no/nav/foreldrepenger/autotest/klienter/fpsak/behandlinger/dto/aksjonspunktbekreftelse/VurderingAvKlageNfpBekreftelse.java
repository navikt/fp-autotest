package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

public class VurderingAvKlageNfpBekreftelse extends AksjonspunktBekreftelse {

    private static final String VURDERING_STADFEST = "STADFESTE_YTELSESVEDTAK";
    private static final String VURDERING_MEDHOLD = "MEDHOLD_I_KLAGE";

    private static final String OMGJØR_GUNST = "GUNST_MEDHOLD_I_KLAGE";
    private static final String OMGJØR_DELVISGUNST = "DELVIS_MEDHOLD_I_KLAGE";
    private static final String OMGJØR_UGUNST = "UGUNST_MEDHOLD_I_KLAGE";

    protected String klageVurdering;
    protected String klageMedholdÅrsak;
    protected String klageVurderingOmgjør;
    protected String fritekstTilBrev;

    // Omgjør vedtaket
    public VurderingAvKlageNfpBekreftelse bekreftMedholdGunst(String årsak) {
        klageVurdering = VURDERING_MEDHOLD;
        klageVurderingOmgjør = OMGJØR_GUNST;
        klageMedholdÅrsak = årsak;
        return this;
    }

    public VurderingAvKlageNfpBekreftelse bekreftMedholdDelvisGunst(String årsak) {
        klageVurdering = VURDERING_MEDHOLD;
        klageVurderingOmgjør = OMGJØR_DELVISGUNST;
        klageMedholdÅrsak = årsak;
        return this;
    }

    public VurderingAvKlageNfpBekreftelse bekreftMedholdUGunst(String årsak) {
        klageVurdering = VURDERING_MEDHOLD;
        klageVurderingOmgjør = OMGJØR_UGUNST;
        klageMedholdÅrsak = årsak;
        return this;
    }

    // oppretthold vedtaket
    public VurderingAvKlageNfpBekreftelse bekreftStadfestet() {
        klageVurdering = VURDERING_STADFEST;
        return this;
    }

    public VurderingAvKlageNfpBekreftelse fritekstBrev(String fritekst) {
        fritekstTilBrev = fritekst;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5035";
    }
}
