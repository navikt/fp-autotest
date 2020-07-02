package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
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
        klageVurdering = VURDERING_MEDHOLD;
        klageVurderingOmgjoer = OMGJØR_GUNST;
        klageMedholdArsak = årsak;
        return this;
    }

    public VurderingAvKlageBekreftelse bekreftMedholdDelvisGunst(String årsak) {
        klageVurdering = VURDERING_MEDHOLD;
        klageVurderingOmgjoer = OMGJØR_DELVISGUNST;
        klageMedholdArsak = årsak;
        return this;
    }

    public VurderingAvKlageBekreftelse bekreftMedholdUGunst(String årsak) {
        klageVurdering = VURDERING_MEDHOLD;
        klageVurderingOmgjoer = OMGJØR_UGUNST;
        klageMedholdArsak = årsak;
        return this;
    }

    // oppretthold vedtaket
    public VurderingAvKlageBekreftelse bekreftStadfestet() {
        klageVurdering = VURDERING_STADFEST;
        return this;
    }

    public VurderingAvKlageBekreftelse fritekstBrev(String fritekst) {
        fritekstTilBrev = fritekst;
        return this;
    }

    @BekreftelseKode(kode = "5035")
    @JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class VurderingAvKlageNfpBekreftelse extends VurderingAvKlageBekreftelse {

        public VurderingAvKlageNfpBekreftelse() {
            super();
        }
    }

    @BekreftelseKode(kode = "5036")
    @JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class VurderingAvKlageNkBekreftelse extends VurderingAvKlageBekreftelse {

        private static final String VURDERING_OPPHEVE = "OPPHEVE_YTELSESVEDTAK";
        private static final String VURDERING_HJEMSENDE = "HJEMSENDE_UTEN_Å_OPPHEVE";

        public VurderingAvKlageNkBekreftelse() {
            super();
        }

        public VurderingAvKlageNkBekreftelse bekreftOpphevet(String årsak) {
            klageVurdering = VURDERING_OPPHEVE;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderingAvKlageBekreftelse that = (VurderingAvKlageBekreftelse) o;
        return Objects.equals(klageVurdering, that.klageVurdering) &&
                Objects.equals(klageMedholdArsak, that.klageMedholdArsak) &&
                Objects.equals(klageVurderingOmgjoer, that.klageVurderingOmgjoer) &&
                Objects.equals(fritekstTilBrev, that.fritekstTilBrev) &&
                Objects.equals(vedtaksdatoPaklagdBehandling, that.vedtaksdatoPaklagdBehandling);
    }

    @Override
    public int hashCode() {
        return Objects.hash(klageVurdering, klageMedholdArsak, klageVurderingOmgjoer, fritekstTilBrev,
                vedtaksdatoPaklagdBehandling);
    }

    @Override
    public String toString() {
        return "VurderingAvKlageBekreftelse{" +
                "klageVurdering='" + klageVurdering + '\'' +
                ", klageMedholdArsak='" + klageMedholdArsak + '\'' +
                ", klageVurderingOmgjoer='" + klageVurderingOmgjoer + '\'' +
                ", fritekstTilBrev='" + fritekstTilBrev + '\'' +
                ", vedtaksdatoPaklagdBehandling=" + vedtaksdatoPaklagdBehandling +
                "} " + super.toString();
    }
}
