package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KlageVurderingResultat;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class KlageVurderingResultatAksjonspunktMellomlagringDto {

    private String kode;
    private String begrunnelse;
    private int behandlingId;
    private String fritekstTilBrev;
    private String klageMedholdArsak;
    private String klageVurdering;
    private String klageVurderingOmgjoer;

    @JsonCreator
    public KlageVurderingResultatAksjonspunktMellomlagringDto(String kode, String begrunnelse, int behandlingId,
            String fritekstTilBrev, String klageMedholdArsak, String klageVurdering, String klageVurderingOmgjoer) {
        super();
        this.kode = kode;
        this.begrunnelse = begrunnelse;
        this.behandlingId = behandlingId;
        this.fritekstTilBrev = fritekstTilBrev;
        this.klageMedholdArsak = klageMedholdArsak;
        this.klageVurdering = klageVurdering;
        this.klageVurderingOmgjoer = klageVurderingOmgjoer;
    }

    public KlageVurderingResultatAksjonspunktMellomlagringDto(int behandlingId, KlageVurderingResultat resultat,
            Aksjonspunkt aksjonspunkt) {
        this(aksjonspunkt.getDefinisjon().kode, resultat.getBegrunnelse(), behandlingId, resultat.getFritekstTilBrev(),
                resultat.getKlageMedholdArsak(), resultat.getKlageVurdering(), resultat.getKlageVurderingOmgjoer());
    }

    public KlageVurderingResultatAksjonspunktMellomlagringDto(Behandling behandling, Aksjonspunkt aksjonspunkt) {
        this(behandling.id, behandling.getKlagevurdering().getKlageVurderingResultatNFP(), aksjonspunkt); // lage for
                                                                                                          // hvert av
                                                                                                          // dem?
    }

    public String getKode() {
        return kode;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public int getBehandlingId() {
        return behandlingId;
    }

    public String getFritekstTilBrev() {
        return fritekstTilBrev;
    }

    public String getKlageMedholdArsak() {
        return klageMedholdArsak;
    }

    public String getKlageVurdering() {
        return klageVurdering;
    }

    public String getKlageVurderingOmgjoer() {
        return klageVurderingOmgjoer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KlageVurderingResultatAksjonspunktMellomlagringDto that = (KlageVurderingResultatAksjonspunktMellomlagringDto) o;
        return behandlingId == that.behandlingId &&
                Objects.equals(kode, that.kode) &&
                Objects.equals(begrunnelse, that.begrunnelse) &&
                Objects.equals(fritekstTilBrev, that.fritekstTilBrev) &&
                Objects.equals(klageMedholdArsak, that.klageMedholdArsak) &&
                Objects.equals(klageVurdering, that.klageVurdering) &&
                Objects.equals(klageVurderingOmgjoer, that.klageVurderingOmgjoer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kode, begrunnelse, behandlingId, fritekstTilBrev, klageMedholdArsak, klageVurdering, klageVurderingOmgjoer);
    }

    @Override
    public String toString() {
        return "KlageVurderingResultatAksjonspunktMellomlagringDto{" +
                "kode='" + kode + '\'' +
                ", begrunnelse='" + begrunnelse + '\'' +
                ", behandlingId=" + behandlingId +
                ", fritekstTilBrev='" + fritekstTilBrev + '\'' +
                ", klageMedholdArsak='" + klageMedholdArsak + '\'' +
                ", klageVurdering='" + klageVurdering + '\'' +
                ", klageVurderingOmgjoer='" + klageVurderingOmgjoer + '\'' +
                '}';
    }
}
