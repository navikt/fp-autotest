package no.nav.foreldrepenger.generator.soknad.builder;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.SøkerDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.SøknadDto;

public interface SøknadBuilder<S> {
    SøknadBuilder<S> medSøkerinfo(SøkerDto søkerinfo);
    SøknadDto build();
}
