package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.UUID;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.KlageVurderingResultat;

public record KlageVurderingResultatAksjonspunktMellomlagringDto(String kode,
                                                                String begrunnelse,
                                                                UUID behandlingUuid,
                                                                String fritekstTilBrev,
                                                                String klageMedholdArsak,
                                                                String klageVurdering,
                                                                String klageVurderingOmgjoer) {

    public KlageVurderingResultatAksjonspunktMellomlagringDto(UUID behandlingUuid, KlageVurderingResultat resultat, Aksjonspunkt aksjonspunkt) {
        this(aksjonspunkt.getDefinisjon(), resultat.begrunnelse(), behandlingUuid, resultat.fritekstTilBrev(),
                resultat.klageMedholdArsak(), resultat.klageVurdering(), resultat.klageVurderingOmgjoer());
    }

    public KlageVurderingResultatAksjonspunktMellomlagringDto(Behandling behandling, Aksjonspunkt aksjonspunkt) {
        this(behandling.uuid, behandling.getKlagevurdering().klageVurderingResultatNFP(), aksjonspunkt); // lage for hvert av dem?
    }
}
