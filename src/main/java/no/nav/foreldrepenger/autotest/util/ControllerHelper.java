package no.nav.foreldrepenger.autotest.util;

import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.BehandlingsTema;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

public class ControllerHelper {

    public static BehandlingsTema translateSøknadDokumenttypeToBehandlingstema(DokumenttypeId dokumenttypeId) {

        if (dokumenttypeId == DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL) {
            return BehandlingsTema.FORELDREPENGER_FØDSEL;
        } else if (dokumenttypeId == DokumenttypeId.SØKNAD_FORELDREPENGER_ADOPSJON) {
            return BehandlingsTema.FORELDREPENGER_ADOPSJON;
        } else if (dokumenttypeId == DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL) {
            return BehandlingsTema.ENGANGSSTØNAD_FØDSEL;
        } else if (dokumenttypeId == DokumenttypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON) {
            return BehandlingsTema.ENGANGSSTØNAD_ADOPSJON;
        } else if (dokumenttypeId == DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD) {
            return BehandlingsTema.FORELDREPENGER;
        } else if (dokumenttypeId == DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER){
            return BehandlingsTema.SVANGERSKAPSPENGER;
        } else {
            throw new RuntimeException("Kunne ikke matche på dokumenttype.");
        }
    }
}
