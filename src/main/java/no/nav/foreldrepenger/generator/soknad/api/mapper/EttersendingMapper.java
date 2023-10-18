package no.nav.foreldrepenger.generator.soknad.api.mapper;

import static no.nav.foreldrepenger.generator.soknad.api.mapper.CommonMapper.tilVedlegg;

import no.nav.foreldrepenger.common.domain.felles.EttersendingsType;
import no.nav.foreldrepenger.generator.soknad.api.dto.ettersendelse.EttersendelseDto;

public final class EttersendingMapper {

    private EttersendingMapper() {
    }

    public static no.nav.foreldrepenger.common.domain.felles.Ettersending tilEttersending(EttersendelseDto ettersending) {
        return new no.nav.foreldrepenger.common.domain.felles.Ettersending(
            ettersending.saksnummer(),
            EttersendingsType.valueOf(ettersending.type().name()),
            tilVedlegg(ettersending.vedlegg()),
            ettersending.dialogId());
    }
}
