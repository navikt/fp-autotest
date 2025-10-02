package no.nav.foreldrepenger.generator.soknad.mapper;

import static no.nav.foreldrepenger.generator.soknad.mapper.CommonMapper.tilVedlegg;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.ettersendelse.EttersendelseDto;
import no.nav.foreldrepenger.common.domain.felles.EttersendingsType;

public final class EttersendingMapper {

    private EttersendingMapper() {
    }

    public static no.nav.foreldrepenger.common.domain.felles.Ettersending tilEttersending(EttersendelseDto ettersending) {
        return new no.nav.foreldrepenger.common.domain.felles.Ettersending(ettersending.saksnummer(),
                EttersendingsType.valueOf(ettersending.type().name()), tilVedlegg(ettersending.vedlegg()), null);
    }
}
