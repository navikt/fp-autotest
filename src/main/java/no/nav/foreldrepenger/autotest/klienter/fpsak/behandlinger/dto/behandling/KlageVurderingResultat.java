package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;
public record KlageVurderingResultat(String klageVurdering,
                                    String begrunnelse,
                                    String fritekstTilBrev,
                                    String klageMedholdArsak,
                                    String klageAvvistArsakNavn,
                                    String klageVurderingOmgjoer,
                                    String klageVurdertAv,
                                    Boolean godkjentAvMedunderskriver) {

}
