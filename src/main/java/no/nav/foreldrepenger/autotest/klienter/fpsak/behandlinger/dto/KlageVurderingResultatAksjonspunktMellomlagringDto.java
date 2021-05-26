package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Kode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KlageVurderingResultat;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class KlageVurderingResultatAksjonspunktMellomlagringDto {

    private final String kode;
    private final String begrunnelse;
    private final UUID behandlingUuid;
    private final String fritekstTilBrev;
    private final Kode klageMedholdArsak;
    private final Kode klageVurdering;
    private final Kode klageVurderingOmgjoer;

    public KlageVurderingResultatAksjonspunktMellomlagringDto(String kode, String begrunnelse, UUID behandlingUuid,
                                                              String fritekstTilBrev, Kode klageMedholdArsak,
                                                              Kode klageVurdering, Kode klageVurderingOmgjoer) {
        this.kode = kode;
        this.begrunnelse = begrunnelse;
        this.behandlingUuid = behandlingUuid;
        this.fritekstTilBrev = fritekstTilBrev;
        this.klageMedholdArsak = klageMedholdArsak;
        this.klageVurdering = klageVurdering;
        this.klageVurderingOmgjoer = klageVurderingOmgjoer;
    }

    public KlageVurderingResultatAksjonspunktMellomlagringDto(UUID behandlingUuid, KlageVurderingResultat resultat,
                                                              Aksjonspunkt aksjonspunkt) {
        this(aksjonspunkt.getDefinisjon().kode, resultat.getBegrunnelse(), behandlingUuid, resultat.getFritekstTilBrev(),
                resultat.getKlageMedholdArsak(), resultat.getKlageVurdering(), resultat.getKlageVurderingOmgjoer());
    }

    public KlageVurderingResultatAksjonspunktMellomlagringDto(Behandling behandling, Aksjonspunkt aksjonspunkt) {
        this(behandling.uuid, behandling.getKlagevurdering().getKlageVurderingResultatNFP(), aksjonspunkt); // lage for hvert av dem?
    }
}
