package no.nav.foreldrepenger.autotest.util;

import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.Behandlingstema;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

public class ControllerHelper {

    public static Behandlingstema translateSøknadDokumenttypeToBehandlingstema(DokumenttypeId dokumenttypeId) {

        if (dokumenttypeId == DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL) {
            return Behandlingstema.FORELDREPENGER_FOEDSEL;
        } else if (dokumenttypeId == DokumenttypeId.SØKNAD_FORELDREPENGER_ADOPSJON) {
            return Behandlingstema.FORELDREPENGER_ADOPSJON;
        } else if (dokumenttypeId == DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL) {
            return Behandlingstema.ENGANGSSTONAD_FOEDSEL;
        } else if (dokumenttypeId == DokumenttypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON) {
            return Behandlingstema.ENGANGSSTONAD_ADOPSJON;
        } else if (dokumenttypeId == DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD) {
            return Behandlingstema.FORELDREPENGER;
        } else if (dokumenttypeId == DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER){
            return Behandlingstema.SVANGERSKAPSPENGER;
        } else {
            throw new RuntimeException("Kunne ikke matche på dokumenttype.");
        }
    }
}
