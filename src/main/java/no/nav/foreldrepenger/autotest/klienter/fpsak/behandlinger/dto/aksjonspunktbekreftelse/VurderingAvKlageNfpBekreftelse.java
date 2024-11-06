package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

public class VurderingAvKlageNfpBekreftelse extends AksjonspunktBekreftelse {

    private static final String VURDERING_STADFEST = "STADFESTE_YTELSESVEDTAK";
    private static final String VURDERING_MEDHOLD = "MEDHOLD_I_KLAGE";

    private static final String OMGJØR_GUNST = "GUNST_MEDHOLD_I_KLAGE";
    private static final String OMGJØR_DELVISGUNST = "DELVIS_MEDHOLD_I_KLAGE";
    private static final String OMGJØR_UGUNST = "UGUNST_MEDHOLD_I_KLAGE";

    protected String klageVurdering;
    protected String klageMedholdArsak;
    protected String klageVurderingOmgjoer;
    protected String fritekstTilBrev;
    protected LocalDate vedtaksdatoPaklagdBehandling;

    // Omgjør vedtaket
    public VurderingAvKlageNfpBekreftelse bekreftMedholdGunst(String årsak) {
        klageVurdering = VURDERING_MEDHOLD;
        klageVurderingOmgjoer = OMGJØR_GUNST;
        klageMedholdArsak = årsak;
        return this;
    }

    public VurderingAvKlageNfpBekreftelse bekreftMedholdDelvisGunst(String årsak) {
        klageVurdering = VURDERING_MEDHOLD;
        klageVurderingOmgjoer = OMGJØR_DELVISGUNST;
        klageMedholdArsak = årsak;
        return this;
    }

    public VurderingAvKlageNfpBekreftelse bekreftMedholdUGunst(String årsak) {
        klageVurdering = VURDERING_MEDHOLD;
        klageVurderingOmgjoer = OMGJØR_UGUNST;
        klageMedholdArsak = årsak;
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

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        this.vedtaksdatoPaklagdBehandling = LocalDate.now();
    }
}
