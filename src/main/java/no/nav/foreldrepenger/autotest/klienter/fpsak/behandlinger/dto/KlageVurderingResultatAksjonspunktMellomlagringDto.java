package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KlageVurderingResultat;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class KlageVurderingResultatAksjonspunktMellomlagringDto {

    private String kode;
    private String begrunnelse;
    private int behandlingId;
    private String fritekstTilBrev;
    private Kode klageMedholdArsak;
    private Kode klageVurdering;
    private Kode klageVurderingOmgjoer;

    public KlageVurderingResultatAksjonspunktMellomlagringDto(String kode, String begrunnelse, int behandlingId,
                                                              String fritekstTilBrev, Kode klageMedholdArsak,
                                                              Kode klageVurdering, Kode klageVurderingOmgjoer) {
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
        this(behandling.id, behandling.getKlagevurdering().getKlageVurderingResultatNFP(), aksjonspunkt); // lage for hvert av dem?
    }
}
