package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

public abstract class VurderingAvKlageBekreftelse extends AksjonspunktBekreftelse {

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

    public VurderingAvKlageBekreftelse() {
        super();
    }

    // Omgjør vedtaket
    public VurderingAvKlageBekreftelse bekreftMedholdGunst(String årsak) {
        klageVurdering  = VURDERING_MEDHOLD;
        klageVurderingOmgjoer = OMGJØR_GUNST;
        klageMedholdArsak = årsak;
        return this;
    }

    public VurderingAvKlageBekreftelse bekreftMedholdDelvisGunst(String årsak) {
        klageVurdering  = VURDERING_MEDHOLD;
        klageVurderingOmgjoer = OMGJØR_DELVISGUNST;
        klageMedholdArsak = årsak;
        return this;
    }

    public VurderingAvKlageBekreftelse bekreftMedholdUGunst(String årsak) {
        klageVurdering  = VURDERING_MEDHOLD;
        klageVurderingOmgjoer = OMGJØR_UGUNST;
        klageMedholdArsak = årsak;
        return this;
    }

    // oppretthold vedtaket
    public VurderingAvKlageBekreftelse bekreftStadfestet() {
        klageVurdering  = VURDERING_STADFEST;
        return this;
    }

    public VurderingAvKlageBekreftelse fritekstBrev(String fritekst) {
        fritekstTilBrev = fritekst;
        return this;
    }

    @BekreftelseKode(kode="5035")
    public static class VurderingAvKlageNfpBekreftelse extends VurderingAvKlageBekreftelse {

        public VurderingAvKlageNfpBekreftelse() {
            super();
        }
    }

    @BekreftelseKode(kode="5036")
    public static class VurderingAvKlageNkBekreftelse extends VurderingAvKlageBekreftelse {

        private static final String VURDERING_OPPHEVE = "OPPHEVE_YTELSESVEDTAK";
        private static final String VURDERING_HJEMSENDE = "HJEMSENDE_UTEN_Å_OPPHEVE";

        public VurderingAvKlageNkBekreftelse() {
            super();
        }

        public VurderingAvKlageNkBekreftelse bekreftOpphevet(String årsak) {
            klageVurdering  = VURDERING_OPPHEVE;
            klageMedholdArsak = årsak;
            return this;
        }

        public VurderingAvKlageNkBekreftelse bekreftHjemsende() {
            klageVurdering = VURDERING_HJEMSENDE;
            return this;
        }


    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        this.vedtaksdatoPaklagdBehandling = LocalDate.now();
    }
}
