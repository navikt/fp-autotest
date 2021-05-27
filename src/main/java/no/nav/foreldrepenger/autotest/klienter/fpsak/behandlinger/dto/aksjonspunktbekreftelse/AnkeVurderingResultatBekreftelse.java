package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.UUID;

@BekreftelseKode(kode = "5093")
public class AnkeVurderingResultatBekreftelse extends AksjonspunktBekreftelse{

    private AnkeVurdering ankeVurdering;
    private boolean erSubsidiartRealitetsbehandles;
    private String fritekstTilBrev;
    private AnkeOmgjørÅrsak ankeOmgjoerArsak;
    private AnkeVurderingOmgjør ankeVurderingOmgjoer;
    private boolean erGodkjentAvMedunderskriver;
    private UUID vedtakBehandlingUuid;
    private final boolean erAnkerIkkePart = false;
    private final boolean erFristIkkeOverholdt = false;
    private final boolean erIkkeKonkret = false;
    private final boolean erIkkeSignert = false;

    public AnkeVurderingResultatBekreftelse() {
        super();
    }

    public AnkeVurderingResultatBekreftelse omgjørTilGunst(UUID vedtakBehandlingUuid) {
        ankeOmgjoerArsak = AnkeOmgjørÅrsak.ULIK_VURDERING;
        ankeVurdering = AnkeVurdering.ANKE_OMGJOER;
        ankeVurderingOmgjoer = AnkeVurderingOmgjør.ANKE_TIL_GUNST;
        this.vedtakBehandlingUuid = vedtakBehandlingUuid;
        return this;
    }

    private enum AnkeVurdering {
        ANKE_STADFESTE_YTELSESVEDTAK,
        ANKE_HJEMSEND_UTEN_OPPHEV,
        ANKE_OPPHEVE_OG_HJEMSENDE,
        ANKE_OMGJOER,
        ANKE_AVVIS
    }

    private enum AnkeOmgjørÅrsak {
        NYE_OPPLYSNINGER,
        ULIK_REGELVERKSTOLKNING,
        ULIK_VURDERING,
        PROSESSUELL_FEIL,
        UDEFINERT
    }

    private enum AnkeVurderingOmgjør {
        ANKE_TIL_GUNST,
        ANKE_DELVIS_OMGJOERING_TIL_GUNST,
        ANKE_TIL_UGUNST,
        UDEFINERT
    }
}
