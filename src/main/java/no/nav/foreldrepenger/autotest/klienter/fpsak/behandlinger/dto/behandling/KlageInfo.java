package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

public record KlageInfo(KlageVurderingResultat klageVurderingResultatNFP,
                       KlageVurderingResultat klageVurderingResultatNK,
                       KlageFormkravResultat klageFormkravResultatNFP,
                       KlageFormkravResultat klageFormkravResultatKA) {
}
