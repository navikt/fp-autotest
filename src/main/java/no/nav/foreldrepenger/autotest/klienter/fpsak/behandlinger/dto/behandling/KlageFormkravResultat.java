package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import java.util.List;

public record KlageFormkravResultat(List<String> avvistArsaker,
                                    Long paKlagdBehandlingId,
                                    String begrunnelse,
                                    boolean erKlagerPart,
                                    boolean erKlageKonkret,
                                    boolean erKlagefirstOverholdt,
                                    boolean erSignert) {

}
