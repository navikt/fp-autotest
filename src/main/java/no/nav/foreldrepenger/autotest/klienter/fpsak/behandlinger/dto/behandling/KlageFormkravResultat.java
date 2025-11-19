package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import java.util.List;
import java.util.UUID;

public record KlageFormkravResultat(UUID påKlagdBehandlingUuid,
                                    String begrunnelse,
                                    boolean erKlagerPart,
                                    boolean erKlageKonkret,
                                    boolean erKlagefirstOverholdt,
                                    boolean erSignert,
                                    List<String> avvistÅrsaker) {

}
