package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId.SØKNAD_FORELDREPENGER_ADOPSJON;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Omsorgsovertakelse;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.common.error.UnexpectedInputException;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

class DokumentIDFraSøknad {

    private DokumentIDFraSøknad () {

    }

    public static DokumenttypeId dokumentTypeFraRelasjon(Søknad søknad) {
        var ytelse = søknad.getYtelse();
        if (ytelse instanceof Foreldrepenger) {
            return dokumentTypeFraRelasjonForForeldrepenger(søknad);
        } else if (ytelse instanceof Engangsstønad) {
            return dokumentTypeFraRelasjonForEngangsstønad(søknad);
        } else if (ytelse instanceof Svangerskapspenger){
            return SØKNAD_SVANGERSKAPSPENGER;
        }
        throw new UnsupportedOperationException("DokumenttypeID er ikke støttet for søknad");
    }

    private static DokumenttypeId dokumentTypeFraRelasjonForEngangsstønad(Søknad søknad) {
        var relasjon = ((Engangsstønad) søknad.getYtelse()).getRelasjonTilBarn();
        if (relasjon instanceof Fødsel || relasjon instanceof FremtidigFødsel) {
            return SØKNAD_ENGANGSSTØNAD_FØDSEL;
        }
        if (relasjon instanceof Omsorgsovertakelse || relasjon instanceof Adopsjon) {
            return SØKNAD_ENGANGSSTØNAD_ADOPSJON;
        }
        throw new UnexpectedInputException("Ukjent relasjon %s", relasjon.getClass().getSimpleName());
    }

    private static DokumenttypeId dokumentTypeFraRelasjonForForeldrepenger(Søknad søknad) {
        var relasjon = ((Foreldrepenger) søknad.getYtelse()).getRelasjonTilBarn();
        if (relasjon instanceof Fødsel || relasjon instanceof FremtidigFødsel) {
            return SØKNAD_FORELDREPENGER_FØDSEL;
        }

        if (relasjon instanceof Adopsjon || relasjon instanceof Omsorgsovertakelse) {
            return SØKNAD_FORELDREPENGER_ADOPSJON;
        }
        throw new UnexpectedInputException("Ukjent relasjon %s", relasjon.getClass().getSimpleName());
    }
}
